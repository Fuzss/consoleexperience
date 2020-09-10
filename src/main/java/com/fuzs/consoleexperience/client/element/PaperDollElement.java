package com.fuzs.consoleexperience.client.element;

import com.fuzs.consoleexperience.ConsoleExperience;
import com.fuzs.consoleexperience.client.gui.PaperDollRenderer;
import com.fuzs.consoleexperience.client.gui.PositionPreset;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PaperDollElement extends GameplayElement implements IHasDisplayTime {

    private static final Set<DisplayAction> DEFAULT_DOLL_CONDITIONS = ImmutableSet.of(DisplayAction.SPRINTING, DisplayAction.SWIMMING, DisplayAction.CRAWLING, DisplayAction.CROUCHING, DisplayAction.FLYING, DisplayAction.GLIDING);
    private final PaperDollRenderer dollRenderer = new PaperDollRenderer();

    private int scale;
    private int xOffset;
    private int yOffset;
    private int displayTime;
    public PositionPreset position;
    private boolean potionShift;
    private boolean firstPerson;
    public PaperDollRenderer.HeadMovement headMovement;
    private Set<DisplayAction> dollConditions;

    private int remainingDisplayTicks;
    private int remainingRidingTicks;
    private float prevRotationYaw;

    @Override
    public void setup() {

        this.addListener(this::onClientTick);
        this.addListener(this::onRenderGameOverlayPre);
    }

    @Override
    public boolean getDefaultState() {

        return true;
    }

    @Override
    public String getDisplayName() {

        return "Paper Doll";
    }

    @Override
    public String getDescription() {

        return "Show a small player model while the player is performing certain actions such as sprinting, swimming, crouching, flying and gliding.";
    }

    @Override
    public void setupConfig(ForgeConfigSpec.Builder builder) {

        registerClientEntry(builder.comment("Scale of paper doll. Works in tandem with \"GUI Scale\" option in \"Video Settings\".").defineInRange("Scale", 4, 1, 24), v -> this.scale = v);
        registerClientEntry(builder.comment("Offset on x-axis from original doll position.").defineInRange("X-Offset", 0, Integer.MIN_VALUE, Integer.MAX_VALUE), v -> this.xOffset = v);
        registerClientEntry(builder.comment("Offset on y-axis from original doll position.").defineInRange("Y-Offset", 0, Integer.MIN_VALUE, Integer.MAX_VALUE), v -> this.yOffset = v);
        registerClientEntry(builder.comment("Amount of ticks the paper doll will be kept on screen after its display conditions are no longer met. Set to 0 to always display the doll.").defineInRange("Display Time", 12, 0, Integer.MAX_VALUE), v -> this.displayTime = v);
        registerClientEntry(builder.comment("Define a screen corner to display the paper doll in.").defineEnum("Screen Corner", PositionPreset.TOP_LEFT), v -> this.position = v);
        registerClientEntry(builder.comment("Shift paper doll downwards when it would otherwise overlap with potion icons. Only applicable when \"Screen Corner\" is set to \"TOP_RIGHT\".").define("Potion Shift", true), v -> this.potionShift = v);
        registerClientEntry(builder.comment("Only show paper doll when in first person mode.").define("First Person", true), v -> this.firstPerson = v);
        registerClientEntry(builder.comment("Set axis the player head can move on.").defineEnum("Head Movement", PaperDollRenderer.HeadMovement.YAW), v -> this.headMovement = v);
        registerClientEntry(builder.comment("Display paper doll while performing these actions.", "Allowed Values: " + Arrays.stream(DisplayAction.values()).map(Enum::name).collect(Collectors.joining(", "))).define("Display Actions", DEFAULT_DOLL_CONDITIONS.stream().map(Enum::name).collect(Collectors.toList())), v -> {

            try {

                this.dollConditions = v.stream().map(DisplayAction::valueOf).collect(Collectors.toSet());
            } catch (IllegalArgumentException e) {

                ConsoleExperience.LOGGER.error(e);
                this.dollConditions = DEFAULT_DOLL_CONDITIONS;
            }
        });
    }

    @Override
    public boolean isVisible() {

        return this.remainingDisplayTicks > 0 || this.displayTime == 0;
    }

    private void onClientTick(final TickEvent.ClientTickEvent evt) {

        ClientPlayerEntity player = this.mc.player;
        if (evt.phase != TickEvent.Phase.END || player == null || this.mc.isGamePaused()) {

            return;
        }

        // update display ticks
        if (this.dollConditions.stream().anyMatch(condition -> condition.isActive(player, this.remainingRidingTicks))) {

            this.remainingDisplayTicks = this.displayTime;
        } else if (this.remainingDisplayTicks > 0) {

            this.remainingDisplayTicks--;
        }

        // reset rotation when no longer shown
        if (!this.isVisible()) {

            this.prevRotationYaw = 0;
        }

        // don't show paper doll in sneaking position after unmounting a vehicle / mount
        if (player.isPassenger()) {

            this.remainingRidingTicks = Math.max(0, this.displayTime - 2);
        } else if (this.remainingRidingTicks > 0) {

            this.remainingRidingTicks--;
        }
    }

    private void onRenderGameOverlayPre(final RenderGameOverlayEvent.Pre evt) {

        if (evt.getType() != RenderGameOverlayEvent.ElementType.ALL) {

            return;
        }

        this.mc.getProfiler().startSection("paperDoll");
        ClientPlayerEntity player = this.mc.player;
        assert player != null && this.mc.playerController != null;
        boolean isVisible = !player.isInvisible() && !this.mc.playerController.isSpectatorMode();
        boolean firstPerson = this.mc.gameSettings.func_243230_g().func_243192_a() || !this.firstPerson;
        if (isVisible && firstPerson && !((IHasDisplayTime) GameplayElements.HIDE_HUD).isVisible() && this.isVisible()) {

            int scale = this.scale * 5;
            PositionPreset position = this.position;
            int x = position.getX(0, evt.getWindow().getScaledWidth(), (int) (scale * 1.5F) + this.xOffset);
            // can't use PositionPreset#getY as the orientation point isn't in the top left corner of the image
            int y = position.isBottom() ? evt.getWindow().getScaledHeight() - scale - this.yOffset : (int) (scale * 2.5F) + this.yOffset;
            y -= scale - this.updateOffset(player, evt.getPartialTicks()) * scale;
            if (this.potionShift) {

                y += position.getPotionShift(player.getActivePotionEffects());
            }

            this.prevRotationYaw = this.dollRenderer.drawEntityOnScreen(x, y, scale, player, evt.getPartialTicks(), this.prevRotationYaw);
        }

        this.mc.getProfiler().endSection();
    }

    private float updateOffset(ClientPlayerEntity player, float partialTicks) {

        // crouching check after elytra since you can do both at the same time
        float height = player.getSize(Pose.STANDING).height;
        if (player.getTicksElytraFlying() > 0) {

            float ticksElytraFlying = player.getTicksElytraFlying() + partialTicks;
            float flyingAnimation = MathHelper.clamp(ticksElytraFlying * 0.09F, 0.0F, 1.0F);
            float flyingHeight = player.getSize(Pose.FALL_FLYING).height / height;
            return MathHelper.lerp(flyingAnimation, 1.0F, flyingHeight);
        } else if (player.getSwimAnimation(partialTicks) > 0) {

            float swimmingAnimation = player.isActualySwimming() ? 1.0F : player.getSwimAnimation(partialTicks);
            float swimmingHeight = player.getSize(Pose.SWIMMING).height / height;
            return MathHelper.lerp(swimmingAnimation, 1.0F, swimmingHeight);
        } else if (player.isSpinAttacking()) {

            return player.getSize(Pose.SPIN_ATTACK).height / height;
        } else if (player.isCrouching()) {

            return player.getSize(Pose.CROUCHING).height / height;
        } else if (player.isSleeping()) {

            return player.getSize(Pose.SLEEPING).height / height;
        } else if (player.deathTime > 0) {

            float dyingAnimation = ((float) player.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
            dyingAnimation = Math.min(1.0F, MathHelper.sqrt(dyingAnimation));
            float dyingHeight = player.getSize(Pose.DYING).height / height;
            return MathHelper.lerp(dyingAnimation, 1.0F, dyingHeight);
        } else {

            return 1.0F;
        }
    }

    private enum DisplayAction {

        SPRINTING(ClientPlayerEntity::func_230269_aK_),
        SWIMMING(player -> player.getSwimAnimation(1.0F) > 0 && player.isInWater()),
        CRAWLING(player -> player.getSwimAnimation(1.0F) > 0 && !player.isInWater()),
        CROUCHING(ClientPlayerEntity::isCrouching),
        FLYING(player -> player.abilities.isFlying),
        GLIDING(LivingEntity::isElytraFlying),
        RIDING(Entity::isPassenger),
        SPIN_ATTACKING(LivingEntity::isSpinAttacking),
        USING(ClientPlayerEntity::isHandActive);

        final Predicate<ClientPlayerEntity> action;

        DisplayAction(Predicate<ClientPlayerEntity> action) {

            this.action = action;
        }

        boolean isActive(ClientPlayerEntity player, int remainingRidingTicks) {

            return (this != CROUCHING || remainingRidingTicks == 0) && this.action.test(player);
        }
    }

}

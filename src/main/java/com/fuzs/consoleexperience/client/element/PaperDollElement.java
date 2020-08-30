package com.fuzs.consoleexperience.client.element;

import com.fuzs.consoleexperience.client.gui.PositionPreset;
import com.fuzs.consoleexperience.client.gui.PaperDollRenderer;
import com.google.common.collect.Sets;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;

import java.util.Set;
import java.util.function.Predicate;

public class PaperDollElement extends GameplayElement {
    
    private ForgeConfigSpec.EnumValue<PositionPreset> position;
    private ForgeConfigSpec.IntValue scale;
    private ForgeConfigSpec.IntValue xOffset;
    private ForgeConfigSpec.IntValue yOffset;
    private ForgeConfigSpec.IntValue displayTime;
    private ForgeConfigSpec.BooleanValue potionShift;
    private ForgeConfigSpec.BooleanValue burning;
    private ForgeConfigSpec.BooleanValue firstPerson;

    private static final Set<PaperDollCondition> DOLL_CONDITIONS = Sets.newHashSet();
    private final PaperDollRenderer dollRenderer = new PaperDollRenderer();

    private int remainingDisplayTicks;
    private int remainingRidingTicks;
    private float prevRotationYaw;
    private float lastSwimAnimation = 1.0F;

    @Override
    public void setupElement() {

        this.addListener(this::onClientTick);
        this.addListener(this::onRenderGameOverlayPre);
        this.addListener(this::onRenderBlockOverlay);
        this.dollRenderer.setPositionPreset(this.position);
    }

    @Override
    protected boolean getDefaultState() {

        return true;
    }

    @Override
    protected String getDisplayName() {

        return "Paper Doll";
    }

    @Override
    protected String getDescription() {

        return "Show a small player model in a configurable corner of the screen while the player is performing certain actions like sprinting, sneaking, or flying.";
    }

    @Override
    public void setupConfig(ForgeConfigSpec.Builder builder) {

        this.scale = builder.comment("Scale of paper doll. Works in tandem with \"GUI Scale\" option in \"Video Settings\".").defineInRange("Scale", 4, 1, 24);
        this.xOffset = builder.comment("Offset on x-axis from original doll position.").defineInRange("X-Offset", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.yOffset = builder.comment("Offset on y-axis from original doll position.").defineInRange("Y-Offset", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.displayTime = builder.comment("Amount of ticks the paper doll will be kept on screen after its display conditions are no longer met. Set to 0 to always display the doll.").defineInRange("Display Time", 12, 0, Integer.MAX_VALUE);
        this.position = builder.comment("Define a screen corner to display the paper doll in.").defineEnum("Screen Corner", PositionPreset.TOP_LEFT);
        this.potionShift = builder.comment("Shift the paper doll downwards when it would otherwise overlap with the potion icons. Only applicable when the \"Screen Corner\" is set to \"TOP_RIGHT\".").define("Potion Shift", true);
        this.firstPerson = builder.comment("Only show the paper doll when in first person mode.").define("First Person Only", true);
        
        this.setupConditions(builder);
        this.dollRenderer.setupConfig(builder);
    }
    
    private void setupConditions(ForgeConfigSpec.Builder builder) {
        
        builder.push("conditions");
        String s = "Display paper doll while ";
        registerCondition(builder.comment(s + "sprinting.").define("Sprinting", true), player -> player.isSprinting() && !player.isSwimming());
        registerCondition(builder.comment(s + "swimming.").define("Swimming", true), PlayerEntity::isSwimming);
        registerCondition(builder.comment(s + "crawling in a tight space.").define("Crawling", true), player -> player.getPose() == Pose.SWIMMING && !player.isSwimming());
        registerCondition(builder.comment(s + "crouching.").define("Crouching", true), player -> this.remainingRidingTicks == 0 && player.movementInput.sneaking);
        registerCondition(builder.comment(s + "using creative mode flight.").define("Flying", true), player -> player.abilities.isFlying);
        registerCondition(builder.comment(s + "gliding using an elytra.").define("Elytra Flying", true), LivingEntity::isElytraFlying);
        registerCondition(builder.comment(s + "riding a mount.").define("Riding", false), Entity::isPassenger);
        registerCondition(builder.comment(s + "spin attacking using a riptide enchanted trident.").define("Spin Attacking", false), LivingEntity::isSpinAttacking);
        registerCondition(builder.comment(s + "moving.").define("Moving", false), player -> !player.movementInput.getMoveVector().equals(Vector2f.ZERO));
        registerCondition(builder.comment(s + "jumping.").define("Jumping", false), player -> player.movementInput.jump);
        registerCondition(builder.comment(s + "interacting with the world.").define("Interacting", false), player -> player.isSwingInProgress);
        registerCondition(builder.comment(s + "using an item like food or a bow.").define("Using", false), ClientPlayerEntity::isHandActive);
        registerCondition(builder.comment("Display paper doll when being hurt.").define("Hurt", false), player -> player.hurtTime > 0);
        this.burning = registerCondition(builder.comment("Disable flame overlay on the hud when on fire and only display burning paper doll instead.").define("Burning", false), Entity::isBurning);
        builder.pop();
    }

    @Override
    public boolean isActive() {

        return this.remainingDisplayTicks > 0;
    }

    private void onClientTick(final TickEvent.ClientTickEvent evt) {

        ClientPlayerEntity player = this.mc.player;
        if (evt.phase != TickEvent.Phase.END || player == null || this.mc.isGamePaused()) {

            return;
        }

        assert player.movementInput != null;
        // update display ticks
        if (this.displayTime.get() == 0 || DOLL_CONDITIONS.stream().anyMatch(condition -> condition.isActive(player))) {

            this.remainingDisplayTicks = this.displayTime.get() == 0 ? 1 : this.displayTime.get();
        } else if (this.remainingDisplayTicks > 0) {

            this.remainingDisplayTicks--;
        } else {

            this.prevRotationYaw = 0;
        }

        // don't show paper doll in sneaking position after unmounting a vehicle / mount
        if (player.isPassenger()) {

            this.remainingRidingTicks = 10;
        } else if (this.remainingRidingTicks > 0) {

            this.remainingRidingTicks--;
        }

    }

    private void onRenderBlockOverlay(final RenderBlockOverlayEvent evt) {

        // hide flame overlay and only show on paper doll
        if (this.burning.get() && evt.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE) {

            evt.setCanceled(true);
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
        boolean firstPerson = this.mc.gameSettings.func_243230_g().func_243192_a() || !this.firstPerson.get();
        if (isVisible && firstPerson && !GameplayElements.HIDE_HUD.isActive() && this.remainingDisplayTicks > 0) {

            int scale = this.scale.get() * 5;
            PositionPreset position = this.position.get();
            int x = position.getX(0, evt.getWindow().getScaledWidth(), (int) (scale * 1.5F) + this.xOffset.get());
            // can't use PositionPreset#getY as the orientation point isn't in the top left corner of the image
            int yOffset = this.yOffset.get();
            int y = position.isBottom() ? evt.getWindow().getScaledHeight() - scale - yOffset : (int) (scale * 2.5F) + yOffset;
            y -= scale - this.updateOffset(player, evt.getPartialTicks()) * scale;
            if (this.potionShift.get()) {

                y += position.getPotionShift(player.getActivePotionEffects());
            }

            this.prevRotationYaw = this.dollRenderer.drawEntityOnScreen(x, y, scale, player, evt.getPartialTicks(), this.prevRotationYaw);
        }

        this.mc.getProfiler().endSection();
    }

    private float updateOffset(ClientPlayerEntity player, float partialTicks) {

        float standingHeight = player.getSize(Pose.STANDING).height;
        float relativeHeight = player.getHeight() / standingHeight;
        if (player.isCrouching()) {

            if (player.isCrouching()) {

                return player.getSize(Pose.CROUCHING).height / standingHeight;
            }
        } else if (player.getPose() == Pose.FALL_FLYING) {

            if (player.getTicksElytraFlying() > 0) {

                float ticksElytraFlying = (float) player.getTicksElytraFlying() + partialTicks;
                float f = 1.0F - MathHelper.clamp(ticksElytraFlying * ticksElytraFlying / 100.0F, 0.0F, 1.0F);
                float flyingHeight = player.getSize(Pose.FALL_FLYING).height / standingHeight;
                return flyingHeight + (1.0F - flyingHeight) * f;
            }
        } else if (player.isActualySwimming()) {

            if (player.getSwimAnimation(partialTicks) > 0) {

                float swimmingHeight = player.getSize(Pose.SWIMMING).height / standingHeight;
                float swimAnimation = player.getSwimAnimation(partialTicks);
                if (this.lastSwimAnimation > swimAnimation) {

                    swimmingHeight += (1.0F - swimmingHeight) * (1.0F - swimAnimation);
                }

                this.lastSwimAnimation = swimAnimation;
                return swimmingHeight;
            }
        } else if (relativeHeight < 1.0F) {

            return relativeHeight <= 0.0F ? 1.0F : relativeHeight;
        }

        return 1.0F;
    }
    
    private static ForgeConfigSpec.BooleanValue registerCondition(ForgeConfigSpec.BooleanValue active, Predicate<ClientPlayerEntity> action) {
        
        DOLL_CONDITIONS.add(new PaperDollCondition(active, action));
        return active;
    }

    private static class PaperDollCondition {

        final ForgeConfigSpec.BooleanValue active;
        final Predicate<ClientPlayerEntity> action;

        PaperDollCondition(ForgeConfigSpec.BooleanValue active, Predicate<ClientPlayerEntity> action) {

            this.active = active;
            this.action = action;
        }

        boolean isActive(ClientPlayerEntity player) {

            return this.active.get() && this.action.test(player);
        }
    }

}

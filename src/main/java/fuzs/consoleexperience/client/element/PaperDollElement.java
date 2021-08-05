package fuzs.consoleexperience.client.element;

import fuzs.consoleexperience.ConsoleExperience;
import fuzs.consoleexperience.client.gui.PaperDollRenderer;
import fuzs.consoleexperience.client.gui.PositionPreset;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameType;
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
    private boolean firstPersonOnly;
    public PaperDollRenderer.HeadMovement headMovement;
    private Set<DisplayAction> dollConditions;

    private int remainingDisplayTicks;
    private int remainingRidingTicks;

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

        registerClientEntry(builder.comment("Scale of paper doll. Also influenced by \"GUI Scale\" option in \"Video Settings\".").defineInRange("Scale", 4, 1, 24), v -> this.scale = v);
        registerClientEntry(builder.comment("Offset on x-axis from original doll position.").defineInRange("X-Offset", 0, Integer.MIN_VALUE, Integer.MAX_VALUE), v -> this.xOffset = v);
        registerClientEntry(builder.comment("Offset on y-axis from original doll position.").defineInRange("Y-Offset", 0, Integer.MIN_VALUE, Integer.MAX_VALUE), v -> this.yOffset = v);
        registerClientEntry(builder.comment("Amount of ticks the paper doll will be kept on screen after its display conditions are no longer met. Set to 0 to always display the doll.").defineInRange("Display Time", 12, 0, Integer.MAX_VALUE), v -> this.displayTime = v);
        registerClientEntry(builder.comment("Define a screen corner to display the paper doll in.").defineEnum("Screen Corner", PositionPreset.TOP_LEFT), v -> this.position = v);
        registerClientEntry(builder.comment("Shift paper doll downwards when it would otherwise overlap with potion icons. Only applicable when \"Screen Corner\" is set to \"TOP_RIGHT\".").define("Potion Shift", true), v -> this.potionShift = v);
        registerClientEntry(builder.comment("Only show paper doll when in first-person mode.").define("First Person Only", true), v -> this.firstPersonOnly = v);
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
        if (evt.phase != TickEvent.Phase.END || player == null || this.mc.isPaused()) {

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

            this.dollRenderer.reset();
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

        this.mc.getProfiler().push("paperDoll");
        ClientPlayerEntity player = this.mc.player;
        boolean playerVisible = !player.isInvisible() && this.mc.gameMode.getPlayerMode() != GameType.SPECTATOR;
        boolean firstPerson = this.mc.options.getCameraType().isFirstPerson() || !this.firstPersonOnly;
        if (playerVisible && firstPerson && !((IHasDisplayTime) GameplayElements.HIDE_HUD).isVisible() && this.isVisible()) {

            int scale = this.scale * 5;
            int posX = this.position.getX(0, evt.getWindow().getGuiScaledWidth(), (int) (scale * 1.5F) + this.xOffset);
            // can't use PositionPreset#getY as the orientation point isn't in the top left corner of the image
            int posY = this.position.isBottom() ? evt.getWindow().getGuiScaledHeight() - scale - this.yOffset : (int) (scale * 2.5F) + this.yOffset;
            posY -= scale - this.updateOffset(player, evt.getPartialTicks()) * scale;
            if (this.potionShift) {

                posY += this.position.getPotionShift(player.getActiveEffects());
            }

            this.dollRenderer.drawEntityOnScreen(posX, posY, scale, player, evt.getPartialTicks());
        }

        this.mc.getProfiler().pop();
    }

    private float updateOffset(ClientPlayerEntity player, float partialTicks) {

        // crouching check after elytra since you can do both at the same time
        float height = player.getDimensions(Pose.STANDING).height;
        if (player.getFallFlyingTicks() > 0) {

            float ticksElytraFlying = player.getFallFlyingTicks() + partialTicks;
            float flyingAnimation = MathHelper.clamp(ticksElytraFlying * 0.09F, 0.0F, 1.0F);
            float flyingHeight = player.getDimensions(Pose.FALL_FLYING).height / height;
            return MathHelper.lerp(flyingAnimation, 1.0F, flyingHeight);
        } else if (player.getSwimAmount(partialTicks) > 0) {

            float swimmingAnimation = player.isVisuallySwimming() ? 1.0F : player.getSwimAmount(partialTicks);
            float swimmingHeight = player.getDimensions(Pose.SWIMMING).height / height;
            return MathHelper.lerp(swimmingAnimation, 1.0F, swimmingHeight);
        } else if (player.isAutoSpinAttack()) {

            return player.getDimensions(Pose.SPIN_ATTACK).height / height;
        } else if (player.isCrouching()) {

            return player.getDimensions(Pose.CROUCHING).height / height;
        } else if (player.isSleeping()) {

            return player.getDimensions(Pose.SLEEPING).height / height;
        } else if (player.deathTime > 0) {

            float dyingAnimation = ((float) player.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
            dyingAnimation = Math.min(1.0F, MathHelper.sqrt(dyingAnimation));
            float dyingHeight = player.getDimensions(Pose.DYING).height / height;
            return MathHelper.lerp(dyingAnimation, 1.0F, dyingHeight);
        } else {

            return 1.0F;
        }
    }

    private enum DisplayAction {

        SPRINTING(ClientPlayerEntity::canSpawnSprintParticle),
        SWIMMING(player -> player.getSwimAmount(1.0F) > 0 && player.isInWater()),
        CRAWLING(player -> player.getSwimAmount(1.0F) > 0 && !player.isInWater()),
        CROUCHING(ClientPlayerEntity::isCrouching),
        FLYING(player -> player.abilities.flying),
        GLIDING(LivingEntity::isFallFlying),
        RIDING(Entity::isPassenger),
        SPIN_ATTACKING(LivingEntity::isAutoSpinAttack),
        USING(ClientPlayerEntity::isUsingItem);

        final Predicate<ClientPlayerEntity> action;

        DisplayAction(Predicate<ClientPlayerEntity> action) {

            this.action = action;
        }

        boolean isActive(ClientPlayerEntity player, int remainingRidingTicks) {

            return (this != CROUCHING || remainingRidingTicks == 0) && this.action.test(player);
        }
    }

}

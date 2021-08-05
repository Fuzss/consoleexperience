package fuzs.consoleexperience.client.element;

import fuzs.consoleexperience.mixin.client.accessor.FirstPersonRendererAccessor;
import fuzs.puzzleslib.config.option.OptionsBuilder;
import fuzs.puzzleslib.element.AbstractElement;
import fuzs.puzzleslib.element.side.IClientElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.TickEvent;

public class PlayerAnimationsElement extends AbstractElement implements IClientElement {

    private final Minecraft mc = Minecraft.getInstance();
    private float elytraRotation;
    private float prevElytraRotation;
    private PointOfView oldPointOfView;

    public boolean elytraTilt;
    public float tiltAmount;
    public float tiltSpeed;
    public boolean thirdPersonGliding;
    public boolean supermanGliding;
    public boolean fallingAsleep;
    public int fallingSpeed;
    public boolean handIdleAnimation;
    public int idleAnimationSpeed;

    @Override
    public void constructClient() {

        this.addListener(this::onClientTick);
        this.addListener(this::onCameraSetup);
        this.addListener(this::onRenderHand);
    }

    @Override
    public String[] getDescription() {

        return new String[]{"A collection of small animations for the player."};
    }

    @Override
    public void setupClientConfig(OptionsBuilder builder) {
        
        builder.push("elytra_gliding");
        builder.define("Elytra Camera Tilt", true).comment("Tilt camera depending on elytra flight angle.").sync(v -> this.elytraTilt = v);
        builder.define("Tilt Amount", 0.5F).range(0.1F, 1.0F).comment("Multiplier for camera tilt amount when gliding.").sync(v -> this.tiltAmount = v);
        builder.define("Tilt Speed", 0.4F).range(0.1F, 1.0F).comment("Multiplier for camera tilt speed when gliding.").sync(v -> this.tiltSpeed = v);
        builder.define("Third-Person Gliding", true).comment("Auto-switch to third-person mode while elytra gliding.").sync(v -> this.thirdPersonGliding = v);
        builder.define("Superman Pose", true).comment("Superman pose when crouching and elytra gliding at the same time.").sync(v -> this.supermanGliding = v);
        builder.pop();
        builder.push("falling_asleep");
        builder.define("Falling Asleep", true).comment("Fall into bed slowly and smoothly.").sync(v -> this.fallingAsleep = v);
        builder.define("Falling Speed", 10).range(1, 20).comment("Speed of falling into bed animations.").sync(v -> this.fallingSpeed = v);
        builder.pop();
        builder.push("hand_idle_animation");
        builder.define("Hand Idle Animation", false).comment("Subtle hand swing animation in first-person mode while standing still.").sync(v -> this.handIdleAnimation = v);
        builder.define("Animation Speed", 10).range(1, 20).comment("Animations speed of idle hands.").sync(v -> this.idleAnimationSpeed = v);
        builder.pop();
    }

    private void onClientTick(final TickEvent.ClientTickEvent evt) {

        // player null check actually matters here as this also runs outside of a world
        PlayerEntity player = this.mc.player;
        if (evt.phase != TickEvent.Phase.END || player == null) {

            return;
        }

        if (this.thirdPersonGliding) {

            if (player.isFallFlying() && player.getFallFlyingTicks() > 4) {

                if (this.oldPointOfView == null) {

                    this.oldPointOfView = this.mc.options.getCameraType();
                    this.mc.options.setCameraType(PointOfView.THIRD_PERSON_BACK);
                }
            } else if (this.oldPointOfView != null) {

                if (this.mc.options.getCameraType() == PointOfView.THIRD_PERSON_BACK) {

                    this.mc.options.setCameraType(this.oldPointOfView);
                }

                this.oldPointOfView = null;
            }
        }

        if (this.elytraTilt && player.isFallFlying()) {

            // code from PlayerRenderer#applyRotations which is used there for rotating player model while flying
            Vector3d vector3d = player.getViewVector(1.0F);
            Vector3d vector3d1 = player.getDeltaMovement();
            double d0 = Entity.getHorizontalDistanceSqr(vector3d1);
            double d1 = Entity.getHorizontalDistanceSqr(vector3d);
            if (d0 > 0.0 && d1 > 0.0) {

                double d2 = (vector3d1.x * vector3d.x + vector3d1.z * vector3d.z) / Math.sqrt(d0 * d1);
                double d3 = vector3d1.x * vector3d.z - vector3d1.z * vector3d.x;
                // fix Math#acos returning NaN when d2 > 1.0
                float rotationDelta = (float) (Math.signum(d3) * Math.acos(Math.min(d2, 1.0)));
                rotationDelta = rotationDelta / (float) (Math.PI) * 180.0F * 0.4F * this.tiltAmount;
                this.prevElytraRotation = this.elytraRotation;
                this.elytraRotation += (rotationDelta - this.elytraRotation) * this.tiltSpeed;
            }

            return;
        }

        this.prevElytraRotation = this.elytraRotation = 0.0F;
    }

    private void onCameraSetup(final EntityViewRenderEvent.CameraSetup evt) {

        // don't mess with this when we don't have to
        if (this.elytraTilt && (this.prevElytraRotation != 0.0F || this.elytraRotation != 0.0F)) {

            evt.setRoll((float) MathHelper.lerp(evt.getRenderPartialTicks(), this.prevElytraRotation, this.elytraRotation));
        }

        if (this.fallingAsleep && this.mc.player.isSleeping()) {

            evt.setPitch(Math.min(0.0F, (float) Math.pow(this.mc.player.getSleepTimer() + evt.getRenderPartialTicks(), 2) * 0.0008F * this.fallingSpeed - 45.0F));
        }

    }

    private void onRenderHand(final RenderHandEvent evt) {

        if (!this.handIdleAnimation || evt.getItemStack().isEmpty()) {

            return;
        }

        PlayerEntity player = this.mc.player;
        boolean isPrimary = evt.getHand() == Hand.MAIN_HAND;
        HandSide handside = isPrimary ? player.getMainArm() : player.getMainArm().getOpposite();
        float ageInTicks = this.mc.player.tickCount + evt.getPartialTicks();
        float speedAmplifier = 0.0005F * this.idleAnimationSpeed;
        float angleX = MathHelper.sin(ageInTicks * 0.067F) * speedAmplifier;
        float angleY = MathHelper.cos(ageInTicks * 0.09F) * speedAmplifier;
        float angleXOpposite = MathHelper.sin((ageInTicks - (int) (Math.PI / 0.022)) * 0.067F) * speedAmplifier;
        float angleYOpposite = MathHelper.cos((ageInTicks + (int) (Math.PI / 0.03)) * 0.09F) * speedAmplifier;

        FirstPersonRendererAccessor itemInHandRenderer = (FirstPersonRendererAccessor) this.mc.getItemInHandRenderer();
        if (handside == HandSide.LEFT) {

            if (!isPrimary && !itemInHandRenderer.getMainHandItem().isEmpty()) {

                // reset right main hand translation and add off-hand one
                evt.getMatrixStack().translate(-angleX, -angleY, 0.0F);
            }

            // offset 1/3 of a period so both hand sides aren't identical
            evt.getMatrixStack().translate(-(angleXOpposite), angleYOpposite, 0.0F);
        } else {

            if (!isPrimary && !itemInHandRenderer.getMainHandItem().isEmpty()) {

                // reset right main hand translation and add off-hand one
                evt.getMatrixStack().translate(angleXOpposite, -angleYOpposite, 0.0F);
            }

            evt.getMatrixStack().translate(angleX, angleY, 0.0F);
        }
    }

}

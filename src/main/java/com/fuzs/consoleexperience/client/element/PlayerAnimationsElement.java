package com.fuzs.consoleexperience.client.element;

import com.fuzs.consoleexperience.mixin.client.accessor.FirstPersonRendererAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;

public class PlayerAnimationsElement extends GameplayElement {

    private float elytraRotation;
    private float prevElytraRotation;

    private boolean elytrTilt;
    private double tiltAmount;
    private double tiltSpeed;
    private boolean supermanGliding;
    private boolean handIdleAnimation;
    private boolean fallingAsleep;

    @Override
    public void setup() {

        this.addListener(this::onClientTick);
        this.addListener(this::onCameraSetup);
        this.addListener(this::onRenderHand);
    }

    @Override
    public boolean getDefaultState() {

        return true;
    }

    @Override
    public String getDisplayName() {

        return "Player Animations";
    }

    @Override
    public String getDescription() {

        return "A collection of small animations for the player.";
    }

    @Override
    public void setupConfig(ForgeConfigSpec.Builder builder) {

        builder.push("elytra_tilt");
        registerClientEntry(builder.comment("Tilt camera depending on elytra flight angle.").define("Elytra Camera Tilt", true), v -> this.elytrTilt = v);
        registerClientEntry(builder.comment("Multiplier for camera tilt amount when gliding.").defineInRange("Tilt Amount", 0.5, 0.1, 1.0), v -> this.tiltAmount = v);
        registerClientEntry(builder.comment("Multiplier for camera tilt speed when gliding.").defineInRange("Tilt Speed", 0.4, 0.1, 1.0), v -> this.tiltSpeed = v);
        builder.pop();

        registerClientEntry(builder.comment("Superman pose when crouching and gliding at the same time.").define("Superman Gliding", true), v -> this.supermanGliding = v);
        registerClientEntry(builder.comment("Fall into bed slowly and smoothly.").define("Falling Asleep", true), v -> this.fallingAsleep = v);
        registerClientEntry(builder.comment("Subtle hand swing animation in first-person mode while standing still.").define("Hand Idle Animation", false), v -> this.handIdleAnimation = v);
    }

    public boolean getSupermanGliding() {

        return this.isEnabled() && this.supermanGliding;
    }

    private void onClientTick(final TickEvent.ClientTickEvent evt) {

        // player null check actually matters here as this also runs outside of a world
        if (!this.elytrTilt || evt.phase != TickEvent.Phase.END || this.mc.player == null) {

            return;
        }

        PlayerEntity player = this.mc.player;
        if (player.isElytraFlying()) {

            // code from PlayerRenderer#applyRotations which is used there for rotating player model while flying
            Vector3d vector3d = player.getLook(1.0F);
            Vector3d vector3d1 = player.getMotion();
            double d0 = Entity.horizontalMag(vector3d1);
            double d1 = Entity.horizontalMag(vector3d);
            if (d0 > 0.0 && d1 > 0.0) {

                double d2 = (vector3d1.x * vector3d.x + vector3d1.z * vector3d.z) / Math.sqrt(d0 * d1);
                double d3 = vector3d1.x * vector3d.z - vector3d1.z * vector3d.x;
                // fix Math#acos returning NaN when d2 > 1.0
                float rotationDelta = (float) (Math.signum(d3) * Math.acos(Math.min(d2, 1.0)));
                rotationDelta = rotationDelta / (float) (Math.PI) * 180.0F * 0.4F * (float) this.tiltAmount;
                this.prevElytraRotation = this.elytraRotation;
                this.elytraRotation += (rotationDelta - this.elytraRotation) * (float) this.tiltSpeed;
            }

            return;
        }

        this.prevElytraRotation = this.elytraRotation = 0.0F;
    }

    private void onCameraSetup(final EntityViewRenderEvent.CameraSetup evt) {

        // don't mess with this when we don't have to
        if (this.elytrTilt && (this.prevElytraRotation != 0.0F || this.elytraRotation != 0.0F)) {

            evt.setRoll((float) MathHelper.lerp(evt.getRenderPartialTicks(), this.prevElytraRotation, this.elytraRotation));
        }

        if (this.fallingAsleep && this.mc.player.isSleeping()) {

            evt.setPitch(Math.min(0.0F, (float) Math.pow(this.mc.player.getSleepTimer() + evt.getRenderPartialTicks(), 2) * 0.008F - 45.0F));
        }

    }

    private void onRenderHand(final RenderHandEvent evt) {

        if (!this.handIdleAnimation || evt.getItemStack().isEmpty()) {

            return;
        }

        PlayerEntity player = this.mc.player;
        boolean isPrimary = evt.getHand() == Hand.MAIN_HAND;
        HandSide handside = isPrimary ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
        float ageInTicks = this.mc.player.ticksExisted + evt.getPartialTicks();
        if (handside == HandSide.LEFT) {

            if (!isPrimary && !((FirstPersonRendererAccessor) this.mc.getFirstPersonRenderer()).getItemStackMainHand().isEmpty()) {

                // reset right main hand translation and add off-hand one
                evt.getMatrixStack().translate(-this.getAngleX(ageInTicks), -this.getAngleY(ageInTicks), 0.0F);
            }

            // offset 1/3 of a period so both hand sides aren't identical
            evt.getMatrixStack().translate(this.getOppositeAngleX(ageInTicks), this.getOppositeAngleY(ageInTicks), 0.0F);
        } else {

            if (!isPrimary && !((FirstPersonRendererAccessor) this.mc.getFirstPersonRenderer()).getItemStackMainHand().isEmpty()) {

                // reset right main hand translation and add off-hand one
                evt.getMatrixStack().translate(-this.getOppositeAngleX(ageInTicks), -this.getOppositeAngleY(ageInTicks), 0.0F);
            }

            evt.getMatrixStack().translate(this.getAngleX(ageInTicks), this.getAngleY(ageInTicks), 0.0F);
        }
    }

    private float getAngleX(float ageInTicks) {

        return MathHelper.sin(ageInTicks * 0.067F) * 0.005F;
    }

    private float getAngleY(float ageInTicks) {

        return MathHelper.cos(ageInTicks * 0.09F) * 0.005F;
    }

    private float getOppositeAngleX(float ageInTicks) {

        return -this.getAngleX(ageInTicks - (int) (Math.PI / 0.022));
    }

    private float getOppositeAngleY(float ageInTicks) {

        return this.getAngleY(ageInTicks + (int) (Math.PI / 0.03));
    }

}

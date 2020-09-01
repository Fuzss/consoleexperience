package com.fuzs.consoleexperience.client.element;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.ForgeConfigSpec;

public class ElytraTiltElement extends GameplayElement {

    private double tiltMultiplier;

    private float prevElytraRotation;

    @Override
    public void setup() {

        this.addListener(this::onCameraSetup);
    }

    @Override
    protected boolean getDefaultState() {

        return false;
    }

    @Override
    protected String getDisplayName() {

        return "Elytra Camera Tilt";
    }

    @Override
    protected String getDescription() {

        return "Tilt the camera according to elytra flight angle.";
    }

    @Override
    public void setupConfig(ForgeConfigSpec.Builder builder) {

        registerClientEntry(builder.comment("Multiplier for the camera tilt when gliding.").defineInRange("Tilt Multiplier", 0.5, 0.1, 1.0), v -> this.tiltMultiplier = v);
    }

    private void onCameraSetup(final EntityViewRenderEvent.CameraSetup evt) {

        ClientPlayerEntity player = this.mc.player;
        assert player != null;
        if (player.isElytraFlying()) {

            // code from PlayerRenderer#applyRotations which is used there for rotating the player model
            float partialTicks = (float) evt.getRenderPartialTicks();
            Vector3d vector3d = player.getLook(partialTicks);
            Vector3d vector3d1 = player.getMotion();
            double d0 = Entity.horizontalMag(vector3d1);
            double d1 = Entity.horizontalMag(vector3d);
            if (d0 > 0.0D && d1 > 0.0D) {

                double d2 = (vector3d1.x * vector3d.x + vector3d1.z * vector3d.z) / Math.sqrt(d0 * d1);
                double d3 = vector3d1.x * vector3d.z - vector3d1.z * vector3d.x;
                // fix Math#acos returning NaN when d2 > 1.0
                float rotationDelta = (float) (Math.signum(d3) * Math.acos(Math.min(d2, 1.0)) / Math.PI) - this.prevElytraRotation;
                partialTicks *= (0.04F + Math.abs(rotationDelta) * 0.05F) / this.tiltMultiplier;
                this.prevElytraRotation += MathHelper.clamp(rotationDelta, -partialTicks, partialTicks);
                evt.setRoll(this.prevElytraRotation * (float) (25.0 / this.tiltMultiplier));
            }
        }
    }

}

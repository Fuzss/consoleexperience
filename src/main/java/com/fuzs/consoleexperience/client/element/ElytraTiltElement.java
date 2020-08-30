package com.fuzs.consoleexperience.client.element;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.ForgeConfigSpec;

public class ElytraTiltElement extends GameplayElement {

    private ForgeConfigSpec.DoubleValue tiltMultiplier;

    @Override
    public void setupElement() {

        this.addListener(this::onCameraSetup);
    }

    @Override
    protected boolean getDefaultState() {

        return true;
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

        this.tiltMultiplier = builder.comment("Multiplier for the camera tilt when gliding.").defineInRange("Tilt Multiplier", 0.5, 0.1, 1.0);
    }

    private void onCameraSetup(final EntityViewRenderEvent.CameraSetup evt) {

        ClientPlayerEntity player = this.mc.player;
        assert player != null;
        if (player.isElytraFlying()) {

            // code from PlayerRenderer#applyRotations which is used there for rotating the player model
            Vector3d motion = player.getMotion();
            double d0 = motion.getX() * motion.getX() + motion.getZ() * motion.getZ();
            Vector3d look = player.getLook((float) evt.getRenderPartialTicks());
            double d1 = look.getX() * look.getX() + look.getZ() * look.getZ();

            if (d0 > 0.0 && d1 > 0.0) {

                double d2 = (motion.getX() * look.getX() + motion.getZ() * look.getZ()) / (Math.sqrt(d0) * Math.sqrt(d1));
                double d3 = motion.getX() * look.getZ() - motion.getZ() * look.getX();
                // fixed Math#acos returning NaN when d2 > 1.0
                double d4 = Math.signum(d3) * Math.acos(Math.min(d2, 1.0)) * 180.0 / (Math.PI * (1.0F / this.tiltMultiplier.get()));
                evt.setRoll((evt.getRoll() * 3.0F + (float) d4 * 2.0F) / 5.0F);
            }
        }
    }

}

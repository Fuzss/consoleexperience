package com.fuzs.consoleexperience.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ElytraTiltHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onCameraSetup(EntityViewRenderEvent.CameraSetup evt) {

        ClientPlayerEntity player = Minecraft.getInstance().player;

        if (ConfigBuildHandler.MISCELLANEOUS_CONFIG.elytraTilt.get() && player != null && player.isElytraFlying()) {

            // code from PlayerRenderer#applyRotations which is used there for rotating the player model
            Vector3d motion = player.getMotion();
            double d0 = motion.getX() * motion.getX() + motion.getZ() * motion.getZ();
            Vector3d look = player.getLook((float) evt.getRenderPartialTicks());
            double d1 = look.getX() * look.getX() + look.getZ() * look.getZ();

            if (d0 > 0.0 && d1 > 0.0) {
                double d2 = (motion.getX() * look.getX() + motion.getZ() * look.getZ()) / (Math.sqrt(d0) * Math.sqrt(d1));
                double d3 = motion.getX() * look.getZ() - motion.getZ() * look.getX();
                // fixed Math#acos returning NaN when d2 > 1.0
                double d = Math.signum(d3) * Math.acos(Math.min(d2, 1.0)) * 180.0 / (Math.PI * (1.0F / ConfigBuildHandler.MISCELLANEOUS_CONFIG.elytraMultiplier.get()));
                evt.setRoll((evt.getRoll() * 3.0F + (float) d * 2.0F) / 5.0F);
            }

        }

    }

}

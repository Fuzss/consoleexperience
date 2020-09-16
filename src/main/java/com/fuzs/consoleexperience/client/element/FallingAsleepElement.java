package com.fuzs.consoleexperience.client.element;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.client.event.EntityViewRenderEvent;

public class FallingAsleepElement extends GameplayElement {

    @Override
    public void setup() {

        this.addListener(this::onCameraSetup);
    }

    @Override
    public boolean getDefaultState() {

        return true;
    }

    @Override
    public String getDisplayName() {

        return "Falling Asleep";
    }

    @Override
    public String getDescription() {

        return "Fall into bed slowly and smoothly.";
    }

    private void onCameraSetup(final EntityViewRenderEvent.CameraSetup evt) {

        ClientPlayerEntity player = this.mc.player;
        assert player != null;
        if (player.isSleeping()) {

            evt.setPitch(Math.min(0.0F, (float) Math.pow(player.getSleepTimer() + evt.getRenderPartialTicks(), 2) * 0.008F - 45.0F));
        }
    }

}

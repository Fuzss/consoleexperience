package com.fuzs.consoleexperience.client.util;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.GuiScreenEvent;

public class BackgroundState {

    private int state;

    public boolean isActive() {

        return this.state > 0;
    }

    public void tick() {

        this.state--;
    }

    private void start() {

        this.state = 2;
    }

    public void onBackgroundDrawn(final GuiScreenEvent.BackgroundDrawnEvent evt) {

        if (Minecraft.getInstance().world != null) {

            this.start();
        }
    }

}
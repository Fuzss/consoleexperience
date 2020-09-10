package com.fuzs.consoleexperience.client.util;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class BackgroundState {

    private int capacity;
    private int state;

    public BackgroundState(int capacity) {

        this.capacity = capacity;
    }

    public boolean isActive() {

        return this.state > 0;
    }

    private void tick() {

        if (this.isActive()) {

            this.state--;
        }
    }

    private void start() {

        this.state = this.capacity;
    }

    public void setCapacity(int capacity) {

        this.capacity = capacity;
    }

    public void onBackgroundDrawn(final GuiScreenEvent.BackgroundDrawnEvent evt) {

        if (Minecraft.getInstance().world != null) {

            this.start();
        }
    }

    public void onRenderGameOverlayPost(final RenderGameOverlayEvent.Post evt) {

        if (evt.getType() == RenderGameOverlayEvent.ElementType.ALL) {

            this.tick();
        }
    }

}
package com.fuzs.consoleexperience.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.SleepInMultiplayerScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class BackgroundState {

    private int capacity;
    private int state;
    private boolean keepChat;

    public BackgroundState(int capacity) {

        this.capacity = capacity;
    }

    public boolean isActive() {

        return this.state > 0;
    }

    public boolean isChatHidden() {

        return !this.keepChat;
    }

    private void tick() {

        if (this.isActive()) {

            this.state--;
        }
    }

    private void start(boolean keepChat) {

        this.state = this.capacity;
        this.keepChat = keepChat;
    }

    public void setCapacity(int capacity) {

        this.capacity = capacity;
    }

    public void onBackgroundDrawn(final GuiScreenEvent.BackgroundDrawnEvent evt) {

        if (Minecraft.getInstance().world != null) {

            this.start(false);
        }
    }

    public void onRenderGameOverlayPost(final RenderGameOverlayEvent.Post evt) {

        // also hide while laying in bed
        if (Minecraft.getInstance().currentScreen instanceof SleepInMultiplayerScreen) {

            this.start(true);
        }

        if (evt.getType() == RenderGameOverlayEvent.ElementType.ALL) {

            this.tick();
        }
    }

}
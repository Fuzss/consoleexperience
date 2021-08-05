package fuzs.consoleexperience.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.SleepInMultiplayerScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.TickEvent;

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

    @SuppressWarnings("unused")
    public void onBackgroundDrawn(final GuiScreenEvent.BackgroundDrawnEvent evt) {

        if (Minecraft.getInstance().level != null) {

            this.start(false);
        }
    }

    public void onRenderTick(final TickEvent.RenderTickEvent evt) {

        // also hide while laying in bed, only for hide hud element though
        if (evt.phase == TickEvent.Phase.END && Minecraft.getInstance().screen instanceof SleepInMultiplayerScreen) {

            this.start(true);
        }
    }

    public void onClientTick(final TickEvent.ClientTickEvent evt) {

        if (evt.phase == TickEvent.Phase.END) {

            this.tick();
        }
    }

}
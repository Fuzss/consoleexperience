package com.fuzs.consoleexperience.client.element;

import com.fuzs.consoleexperience.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.function.Consumer;

public abstract class GameplayElement {

    protected final Minecraft mc = Minecraft.getInstance();

    private boolean enabled;

    public final void init() {

        if (this.isEnabled()) {

            this.setupElement();
        }
    }

    protected abstract void setupElement();

    public final void setupGeneralConfig(ForgeConfigSpec.Builder builder) {

        ConfigManager.registerClientEntry(builder.comment(this.getDescription()).define(this.getDisplayName(), this.getDefaultState()), v -> this.enabled = v);
    }

    public void setupConfig(ForgeConfigSpec.Builder builder) {

    }

    protected abstract boolean getDefaultState();

    protected abstract String getDisplayName();

    protected abstract String getDescription();

    public final boolean isEnabled() {

        return this.enabled;
    }

    public boolean isActive() {

        return false;
    }

    protected final <T extends Event> void addListener(Consumer<T> consumer) {

        MinecraftForge.EVENT_BUS.addListener(consumer);
    }

    protected final <T extends Event> void addListener(EventPriority priority, Consumer<T> consumer) {

        MinecraftForge.EVENT_BUS.addListener(priority, consumer);
    }

    protected final <T extends Event> void addListener(EventPriority priority, boolean receiveCancelled, Consumer<T> consumer) {

        MinecraftForge.EVENT_BUS.addListener(priority, receiveCancelled, consumer);
    }

}

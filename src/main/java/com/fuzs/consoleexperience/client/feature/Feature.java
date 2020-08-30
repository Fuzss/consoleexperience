package com.fuzs.consoleexperience.client.feature;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.function.Consumer;

public abstract class Feature {

    protected final Minecraft mc = Minecraft.getInstance();

    private ForgeConfigSpec.BooleanValue enabled;

    public final void init() {

        if (this.isEnabled()) {

            this.setupFeature();
        }
    }

    protected abstract void setupFeature();

    public final void setupGeneralConfig(ForgeConfigSpec.Builder builder) {

        this.enabled = builder.comment(this.getDescription()).define(this.getDisplayName(), this.getDefaultState());
    }

    public void setupConfig(ForgeConfigSpec.Builder builder) {

    }

    protected abstract boolean getDefaultState();

    protected abstract String getDisplayName();

    protected abstract String getDescription();

    public final boolean isEnabled() {

        return this.enabled.get();
    }

    public boolean isActive() {

        return false;
    }

    protected final <T extends Event> void addListener(Consumer<T> consumer) {

        MinecraftForge.EVENT_BUS.addListener(consumer);
    }

    protected final <T extends Event> void addListener(EventPriority priority, Consumer<T> consumer) {

        MinecraftForge.EVENT_BUS.addListener(consumer);
    }

    protected final <T extends Event> void addListener(EventPriority priority, boolean receiveCancelled, Consumer<T> consumer) {

        MinecraftForge.EVENT_BUS.addListener(consumer);
    }

}

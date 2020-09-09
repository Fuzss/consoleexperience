package com.fuzs.consoleexperience.client.element;

import com.fuzs.consoleexperience.config.ConfigManager;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.config.ModConfig;

import java.util.List;
import java.util.function.Consumer;

public abstract class GameplayElement implements IConfigurableElement {

    protected final Minecraft mc = Minecraft.getInstance();

    private final List<EventStorage<? extends Event>> events = Lists.newArrayList();
    private boolean enabled;

    protected abstract void setup();

    public final void setup(ForgeConfigSpec.Builder builder) {

        this.setup();
        registerClientEntry(builder.comment(this.getDescription()).define(this.getDisplayName(), this.getDefaultState()), this::setEnabled);
    }

    public final void load() {

        this.reload(true);
        this.init();
    }

    protected void init() {

    }

    private void reload(boolean isInit) {

        if (this.isEnabled()) {

            this.events.forEach(EventStorage::register);
        } else if (!isInit) {

            this.events.forEach(EventStorage::unregister);
        }
    }

    @Override
    public boolean isEnabled() {

        return this.enabled;
    }

    private void setEnabled(boolean enabled) {

        if (enabled != this.enabled) {

            this.enabled = enabled;
            this.reload(false);
        }
    }

    protected static <S extends ForgeConfigSpec.ConfigValue<T>, T> void registerCommonEntry(S entry, Consumer<T> action) {

        ConfigManager.registerEntry(ModConfig.Type.COMMON, entry, action);
    }

    protected static <S extends ForgeConfigSpec.ConfigValue<T>, T> void registerClientEntry(S entry, Consumer<T> action) {

        ConfigManager.registerEntry(ModConfig.Type.CLIENT, entry, action);
    }

    protected static <S extends ForgeConfigSpec.ConfigValue<T>, T> void registerServerEntry(S entry, Consumer<T> action) {

        ConfigManager.registerEntry(ModConfig.Type.SERVER, entry, action);
    }

    protected final <T extends Event> void addListener(Consumer<T> consumer) {

        this.addListener(EventPriority.NORMAL, consumer);
    }

    protected final <T extends Event> void addListener(EventPriority priority, Consumer<T> consumer) {

        this.addListener(priority, false, consumer);
    }

    protected final <T extends Event> void addListener(EventPriority priority, boolean receiveCancelled, Consumer<T> consumer) {

        this.events.add(new EventStorage<>(consumer, priority, receiveCancelled));
    }

    private static class EventStorage<T extends Event> {

        private final Consumer<T> event;
        private final EventPriority priority;
        private final boolean receiveCancelled;
        private boolean active;

        EventStorage(Consumer<T> consumer, EventPriority priority, boolean receiveCancelled) {

            this.event = consumer;
            this.priority = priority;
            this.receiveCancelled = receiveCancelled;
        }

        void register() {

            if (this.isActive(true)) {

                MinecraftForge.EVENT_BUS.addListener(this.priority, this.receiveCancelled, this.event);
            }
        }

        void unregister() {

            if (this.isActive(false)) {

                MinecraftForge.EVENT_BUS.unregister(this.event);
            }
        }

        private boolean isActive(boolean state) {

            if (this.active != state) {

                this.active = state;
                return true;
            }

            return false;
        }
    }

}

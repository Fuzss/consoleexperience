package com.fuzs.consoleexperience.mixin.client.accessor;

import net.minecraft.client.gui.screen.WorldLoadProgressScreen;
import net.minecraft.world.chunk.listener.TrackingChunkStatusListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldLoadProgressScreen.class)
public interface WorldLoadProgressScreenAccessor {

    @Accessor
    TrackingChunkStatusListener getTracker();

}

package com.fuzs.consoleexperience.mixin;

import net.minecraft.client.gui.screen.WorldLoadProgressScreen;
import net.minecraft.world.chunk.listener.TrackingChunkStatusListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldLoadProgressScreen.class)
public interface WorldLoadProgressScreenAccessorMixin {

    @Accessor
    TrackingChunkStatusListener getTracker();

}

package com.fuzs.consoleexperience.mixin;

import net.minecraft.client.gui.screen.WorkingScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nullable;

@Mixin(WorkingScreen.class)
public interface WorkingScreenAccessorMixin {

    @Nullable
    @Accessor
    String getStage();

    @Accessor
    int getProgress();

    @Accessor
    boolean getDoneWorking();

}

package com.fuzs.consoleexperience.mixin;

import net.minecraft.client.gui.screen.WorkingScreen;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nullable;

@Mixin(WorkingScreen.class)
public interface WorkingScreenAccessorMixin {

    @Nullable
    @Accessor("field_238648_a_")
    ITextComponent getWorkingTitle();

    @Nullable
    @Accessor
    ITextComponent getStage();

    @Accessor
    int getProgress();

    @Accessor
    boolean getDoneWorking();

}

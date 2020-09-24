package com.fuzs.consoleexperience.mixin;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ContainerScreen.class)
public interface ContainerScreenAccessorMixin {

    @Accessor
    int getGuiTop();

    @Accessor
    void setGuiTop(int guiTop);

}

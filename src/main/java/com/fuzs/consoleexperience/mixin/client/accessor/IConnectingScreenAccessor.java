package com.fuzs.consoleexperience.mixin.client.accessor;

import net.minecraft.client.gui.screen.ConnectingScreen;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ConnectingScreen.class)
public interface IConnectingScreenAccessor {

    @Accessor("field_209515_s")
    ITextComponent getConnectingProgress();

}

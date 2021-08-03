package com.fuzs.consoleexperience.mixin.client.accessor;

import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DisconnectedScreen.class)
public interface DisconnectedScreenAccessor {

    @Accessor
    ITextComponent getMessage();

    @Accessor
    Screen getNextScreen();

}

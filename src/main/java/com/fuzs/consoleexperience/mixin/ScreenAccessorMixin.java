package com.fuzs.consoleexperience.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Screen.class)
public interface ScreenAccessorMixin {

    @Invoker
    <T extends Widget> T callAddButton(T button);

}

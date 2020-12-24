package com.fuzs.consoleexperience.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(Screen.class)
public interface ScreenAccessorMixin {

    @Accessor
    List<Widget> getButtons();

    @Accessor
    void setTitle(ITextComponent title);

}

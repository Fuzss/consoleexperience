package com.fuzs.consoleexperience.mixin;

import com.fuzs.consoleexperience.client.element.GameplayElements;
import com.fuzs.consoleexperience.client.element.HoveringHotbarElement;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.NewChatGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@SuppressWarnings("unused")
@Mixin(NewChatGui.class)
public abstract class NewChatGuiMixin extends AbstractGui {

    @ModifyVariable(method = "func_238494_b_(DD)Lnet/minecraft/util/text/Style;", ordinal = 1, at = @At("HEAD"))
    private double func_238494_b_(double mouseY) {

        // move chat tooltips together with hovering hotbar
        return mouseY + ((HoveringHotbarElement) GameplayElements.HOVERING_HOTBAR).getTooltipOffset();
    }

}

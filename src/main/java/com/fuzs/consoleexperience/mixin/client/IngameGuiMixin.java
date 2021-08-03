package com.fuzs.consoleexperience.mixin.client;

import com.fuzs.consoleexperience.client.element.GameplayElements;
import com.fuzs.consoleexperience.client.element.HoveringHotbarElement;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IngameGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@SuppressWarnings("unused")
@Mixin(IngameGui.class)
public abstract class IngameGuiMixin extends AbstractGui {

    // renderExperience
    @ModifyConstant(method = "func_238454_b_", constant = @Constant(intValue = 4), slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=expLevel")))
    public int getXpLevelOffset(int oldValue) {

        // same y pos as health and hunger
        return ((HoveringHotbarElement) GameplayElements.HOVERING_HOTBAR).moveXpLevel() ? 7 : oldValue;
    }

}

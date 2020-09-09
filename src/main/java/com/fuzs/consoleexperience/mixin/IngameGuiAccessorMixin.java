package com.fuzs.consoleexperience.mixin;

import net.minecraft.client.gui.IngameGui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(IngameGui.class)
public interface IngameGuiAccessorMixin {

    @Accessor
    ITextComponent getOverlayMessage();

    @Accessor
    void setOverlayMessageTime(int overlayMessageTime);

    @Accessor
    int getOverlayMessageTime();

    @Accessor
    boolean getAnimateOverlayMessageColor();

    @Accessor
    void setRemainingHighlightTicks(int remainingHighlightTicks);

    @Accessor
    void setHighlightingItemStack(ItemStack highlightingItemStack);

}

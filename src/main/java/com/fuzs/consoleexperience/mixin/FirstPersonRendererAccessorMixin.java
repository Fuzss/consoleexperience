package com.fuzs.consoleexperience.mixin;

import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FirstPersonRenderer.class)
public interface FirstPersonRendererAccessorMixin {

    @Accessor
    ItemStack getItemStackMainHand();

}

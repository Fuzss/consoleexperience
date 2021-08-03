package com.fuzs.consoleexperience.mixin.client.accessor;

import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FirstPersonRenderer.class)
public interface FirstPersonRendererAccessor {

    @Accessor
    ItemStack getItemStackMainHand();

}

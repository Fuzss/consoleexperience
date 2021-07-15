package com.fuzs.consoleexperience.mixin.client.accessor;

import net.minecraft.client.gui.DisplayEffectsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DisplayEffectsScreen.class)
public interface IDisplayEffectsScreenAccessor {

    @Accessor
    void setHasActivePotionEffects(boolean hasActivePotionEffects);

}

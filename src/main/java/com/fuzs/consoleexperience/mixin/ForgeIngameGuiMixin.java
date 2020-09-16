package com.fuzs.consoleexperience.mixin;

import com.fuzs.consoleexperience.client.element.GameplayElements;
import com.fuzs.consoleexperience.client.element.ScreenAnimationsElement;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IngameGui;
import net.minecraftforge.client.gui.ForgeIngameGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(ForgeIngameGui.class)
public abstract class ForgeIngameGuiMixin extends IngameGui {

    public ForgeIngameGuiMixin(Minecraft mc) {

        super(mc);
    }

    @Inject(method = "renderHUDText(IILcom/mojang/blaze3d/matrix/MatrixStack;)V", at = @At(value = "CONSTANT", args = "intValue=2", ordinal = 2), remap = false)
    protected void renderHUDText(int width, int height, MatrixStack matrixstack, CallbackInfo ci) {

        // animate right side of debug screen, other side is handles by the Forge event
        matrixstack.translate(2.0F * ((ScreenAnimationsElement) GameplayElements.SCREEN_ANIMATIONS).getAnimationTranslation(), 0.0F, 0.0F);
    }

}

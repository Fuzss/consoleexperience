package com.fuzs.consoleexperience.mixin;

import com.fuzs.consoleexperience.client.element.GameplayElements;
import com.fuzs.consoleexperience.client.element.ScreenAnimationsElement;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.IScreen;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@SuppressWarnings("unused")
@Mixin(Screen.class)
public abstract class ScreenMixin extends FocusableGui implements IScreen, IRenderable {

    @ModifyArgs(method = "renderBackground(Lcom/mojang/blaze3d/matrix/MatrixStack;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;fillGradient(Lcom/mojang/blaze3d/matrix/MatrixStack;IIIIII)V"))
    public void renderBackground(Args args) {

        args.set(5, this.setAlpha(args.get(5)));
        args.set(6, this.setAlpha(args.get(6)));
        ((ScreenAnimationsElement) GameplayElements.SCREEN_ANIMATIONS).isMoved();
    }

    private int setAlpha(int color) {

        int alpha = color >> 24 & 255;
        alpha = (int) (alpha * ((ScreenAnimationsElement) GameplayElements.SCREEN_ANIMATIONS).getAnimationProgress()) << 24;
        return (color & 16777215) + alpha;
    }

}

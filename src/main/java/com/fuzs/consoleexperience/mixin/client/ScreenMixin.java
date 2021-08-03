package com.fuzs.consoleexperience.mixin.client;

import com.fuzs.consoleexperience.client.element.GameplayElements;
import com.fuzs.consoleexperience.client.element.HideHudElement;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.IScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("unused")
@Mixin(Screen.class)
public abstract class ScreenMixin extends FocusableGui implements IScreen, IRenderable {

    @Redirect(method = "renderBackground(Lcom/mojang/blaze3d/matrix/MatrixStack;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;fillGradient(Lcom/mojang/blaze3d/matrix/MatrixStack;IIIIII)V"))
    protected void fillBackgroundGradient(Screen screen, MatrixStack matrixStack, int x1, int y1, int x2, int y2, int colorFrom, int colorTo) {

        if (!(screen instanceof ContainerScreen) || !((HideHudElement) GameplayElements.HIDE_HUD).hideBackground()) {

            this.fillGradient(matrixStack, x1, y1, x2, y2, colorFrom, colorTo);
        }
    }

}

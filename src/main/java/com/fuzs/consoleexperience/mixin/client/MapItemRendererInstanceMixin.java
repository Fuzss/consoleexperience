package com.fuzs.consoleexperience.mixin.client;

import com.fuzs.consoleexperience.client.element.PositionDisplayElement;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(targets = "net.minecraft.client.gui.MapItemRenderer$Instance")
public abstract class MapItemRendererInstanceMixin implements AutoCloseable {

    // renderMap
    @Inject(method = "func_228089_a_(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ZI)V", at = @At("TAIL"))
    private void func_228089_a_(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, boolean hideIcons, int combinedLightIn, CallbackInfo callbackInfo) {

        Minecraft mc = Minecraft.getInstance();
        FontRenderer fontrenderer = mc.fontRenderer;
        ITextComponent itextcomponent = PositionDisplayElement.getCoordinateComponent(mc.player, "map.coordinates", 0);
        matrixStack.push();
        matrixStack.translate(0.0F, 0.0F, -0.125F);
        fontrenderer.func_243247_a(itextcomponent, 0.0F, 0.0F, 0, false, matrixStack.getLast().getMatrix(), renderTypeBuffer, false, 0, combinedLightIn);
        matrixStack.pop();
    }

}

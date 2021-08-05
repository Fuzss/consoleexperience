package fuzs.consoleexperience.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import fuzs.consoleexperience.ConsoleExperience;
import fuzs.consoleexperience.client.element.PositionDisplayElement;
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

    @Inject(method = "draw(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ZI)V", at = @At("TAIL"))
    private void draw(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, boolean hideIcons, int combinedLightIn, CallbackInfo callbackInfo) {

        PositionDisplayElement element = ((PositionDisplayElement) ConsoleExperience.POSITION_DISPLAY);
        if (element.isEnabled() && element.mapDisplay) {

            Minecraft mc = Minecraft.getInstance();
            FontRenderer fontrenderer = mc.font;
            ITextComponent textComponent = PositionDisplayElement.getCoordinateComponent(mc.player, element.mapTextFormat, element.mapDecimalPlaces);
            float scale = element.mapScale / 8.0F;
            int textWidth = (int) (mc.font.width(textComponent) * scale);
            int textHeight = (int) (mc.font.lineHeight * scale);
            int posX = element.mapPosition.getX(textWidth, 128, element.mapXOffset);
            int posY = element.mapPosition.getY(textHeight, 128, element.mapYOffset);
            int fontColor = element.mapBackground ? -1 : 0;
            int backgroundOpacity = element.mapBackground ? Integer.MIN_VALUE : 0;

            matrixStack.pushPose();
            matrixStack.translate(posX, posY, -0.025F);
            matrixStack.scale(scale, scale, 1.0F);
            matrixStack.translate(0.0F, 0.0F, -0.1F);
            fontrenderer.drawInBatch(textComponent, 0.0F, 0.0F, fontColor, false, matrixStack.last().pose(), renderTypeBuffer, false, backgroundOpacity, combinedLightIn);
            matrixStack.popPose();
        }
    }

}

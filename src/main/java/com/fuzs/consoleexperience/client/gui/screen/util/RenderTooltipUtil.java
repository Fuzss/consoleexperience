package com.fuzs.consoleexperience.client.gui.screen.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;

@SuppressWarnings("deprecation")
public class RenderTooltipUtil {

    public static final int[] TOOLTIP_COLORS = new int[]{-851356582, -986896};

    public static void drawTooltip(MatrixStack matrixstack, int posX, int posY, int width, int height, IBidiRenderer messageRenderer) {

        drawTooltipBackground(matrixstack, posX - width / 2, posY, width, height);
        messageRenderer.func_241863_a(matrixstack, posX, posY + 2);
    }

    private static void drawTooltipBackground(MatrixStack matrixstack, int posX, int posY, int width, int height) {

        final int backgroundColor = TOOLTIP_COLORS[0];
        final int frameColor = TOOLTIP_COLORS[1];
        matrixstack.push();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        Matrix4f matrix4f = matrixstack.getLast().getMatrix();
        // big middle part
        fillGradient(matrix4f, bufferbuilder, posX - 3, posY - 3, posX + width + 3, posY + height + 3, 400, backgroundColor, backgroundColor);
        // top background strip
        fillGradient(matrix4f, bufferbuilder, posX - 3, posY - 4, posX + width + 3, posY - 3, 400, frameColor, frameColor);
        // bottom background strip
        fillGradient(matrix4f, bufferbuilder, posX - 3, posY + height + 3, posX + width + 3, posY + height + 4, 400, frameColor, frameColor);
        // left background strip
        fillGradient(matrix4f, bufferbuilder, posX - 4, posY - 3, posX - 3, posY + height + 3, 400, frameColor, frameColor);
        // right background strip
        fillGradient(matrix4f, bufferbuilder, posX + width + 3, posY - 3, posX + width + 4, posY + height + 3, 400, frameColor, frameColor);
        // top left dot
        fillGradient(matrix4f, bufferbuilder, posX - 3, posY - 3, posX - 3 + 1, posY - 3 + 1, 400, frameColor, frameColor);
        // top right dot
        fillGradient(matrix4f, bufferbuilder, posX + width + 2, posY - 3, posX + width + 2 + 1, posY - 3 + 1, 400, frameColor, frameColor);
        // bottom right dot
        fillGradient(matrix4f, bufferbuilder, posX + width + 2, posY + height + 2, posX + width + 2 + 1, posY + height + 2 + 1, 400, frameColor, frameColor);
        // bottom left dot
        fillGradient(matrix4f, bufferbuilder, posX - 3, posY + height + 2, posX - 3 + 1, posY + height + 2 + 1, 400, frameColor, frameColor);
        RenderSystem.enableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        bufferbuilder.finishDrawing();
        WorldVertexBufferUploader.draw(bufferbuilder);
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
        matrixstack.translate(0.0D, 0.0D, 400.0D);
        irendertypebuffer$impl.finish();
        matrixstack.pop();
    }

    private static void fillGradient(Matrix4f matrix, BufferBuilder builder, int x1, int y1, int x2, int y2, int z, int colorA, int colorB) {

        float f = (float)(colorA >> 24 & 255) / 255.0F;
        float f1 = (float)(colorA >> 16 & 255) / 255.0F;
        float f2 = (float)(colorA >> 8 & 255) / 255.0F;
        float f3 = (float)(colorA & 255) / 255.0F;
        float f4 = (float)(colorB >> 24 & 255) / 255.0F;
        float f5 = (float)(colorB >> 16 & 255) / 255.0F;
        float f6 = (float)(colorB >> 8 & 255) / 255.0F;
        float f7 = (float)(colorB & 255) / 255.0F;
        builder.pos(matrix, (float)x2, (float)y1, (float)z).color(f1, f2, f3, f).endVertex();
        builder.pos(matrix, (float)x1, (float)y1, (float)z).color(f1, f2, f3, f).endVertex();
        builder.pos(matrix, (float)x1, (float)y2, (float)z).color(f5, f6, f7, f4).endVertex();
        builder.pos(matrix, (float)x2, (float)y2, (float)z).color(f5, f6, f7, f4).endVertex();
    }

}

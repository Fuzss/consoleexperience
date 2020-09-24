package com.fuzs.consoleexperience.client.gui.screen.util;

import com.fuzs.consoleexperience.mixin.MainMenuScreenAccessorMixin;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

import java.util.function.BiConsumer;

@SuppressWarnings({"deprecation", "unused"})
public class FancyScreenUtil {

    private static final ResourceLocation PANORAMA_OVERLAY_TEXTURES = MainMenuScreenAccessorMixin.getPanoramaOverlayTextures();
    private static final ResourceLocation MINECRAFT_TITLE_TEXTURES = MainMenuScreenAccessorMixin.getMinecraftTitleTextures();
    private static final ResourceLocation MINECRAFT_TITLE_EDITION = MainMenuScreenAccessorMixin.getMinecraftTitleEdition();

    public static void renderMenuElements(Minecraft minecraft, MatrixStack matrixStack, int width, int height, float partialTicks) {

        if (partialTicks >= 1.0F) {

            return;
        }

        renderMenuOverlay(minecraft, matrixStack, width, height, partialTicks);
        renderTitleElements(minecraft, matrixStack, width, height, partialTicks);
    }

    private static void renderMenuOverlay(Minecraft minecraft, MatrixStack matrixStack, int width, int height, float partialTicks) {

        minecraft.getTextureManager().bindTexture(PANORAMA_OVERLAY_TEXTURES);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        AbstractGui.blit(matrixStack, 0, 0, width, height, 0.0F, 0.0F, 16, 128, 16, 128);
    }

    private static void renderTitleElements(Minecraft minecraft, MatrixStack matrixStack, int width, int height, float partialTicks) {

        int j = width / 2 - 137;
        int l = MathHelper.ceil(1.0F * 255.0F) << 24;
        if ((l & -67108864) != 0) {

            minecraft.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURES);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            blitBlackOutline(j, 30, (p_238657_2_, p_238657_3_) -> {

                AbstractGui.blit(matrixStack, p_238657_2_, p_238657_3_, 0, 0, 155, 44, 256, 256);
                AbstractGui.blit(matrixStack, p_238657_2_ + 155, p_238657_3_, 0, 45, 155, 44, 256, 256);
            });

            minecraft.getTextureManager().bindTexture(MINECRAFT_TITLE_EDITION);
            AbstractGui.blit(matrixStack, j + 88, 67, 0.0F, 0.0F, 98, 14, 128, 16);
        }
    }

    public static void renderLoadingBar(MatrixStack matrixstack, FontRenderer fontrenderer, ITextComponent textComponent, int width, int height, float progress) {

        renderLoadingBar(matrixstack, fontrenderer,  textComponent, width / 2, height / 2 + 30, 240, 8, progress);
    }

    public static void renderLoadingBar(MatrixStack matrixstack, FontRenderer fontrenderer, ITextComponent textComponent, int posX, int posY, int width, int height, float progress) {

        AbstractGui.fill(matrixstack, posX - width / 2 - 1, posY - height / 2 - 1, posX + width / 2 + 1, posY + height / 2 + 1, -8684675);
        AbstractGui.fill(matrixstack, posX - width / 2, posY - height / 2, posX - width / 2 + (int) (MathHelper.clamp(progress, 0.0F, 1.0F) * width), posY + height / 2, -15728895);
        AbstractGui.drawString(matrixstack, fontrenderer, textComponent, posX - width / 2 + 2, posY - height / 2 - fontrenderer.FONT_HEIGHT - 2, 16777215);
    }

    public static void drawCenteredString(MatrixStack matrixStack, FontRenderer fontrenderer, ITextComponent itextcomponent, int width, int height) {

        drawCenteredString(matrixStack, fontrenderer, itextcomponent, width / 2, 110, 16777215, 2.0F);
    }

    public static void drawCenteredString(MatrixStack matrixStack, FontRenderer fontrenderer, ITextComponent itextcomponent, int width, int height, int color, float scale) {

        matrixStack.scale(scale, scale, 0.0F);
        AbstractGui.drawCenteredString(matrixStack, fontrenderer, itextcomponent, (int) (width / scale), (int) (height / scale), color);
        matrixStack.scale(1.0F / scale, 1.0F / scale, 0.0F);
    }

    public static RenderSkybox getPanorama() {

        return new RenderSkybox(MainMenuScreen.PANORAMA_RESOURCES);
    }

    private static void blitBlackOutline(int width, int height, BiConsumer<Integer, Integer> boxXYConsumer) {

        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        boxXYConsumer.accept(width + 1, height);
        boxXYConsumer.accept(width - 1, height);
        boxXYConsumer.accept(width, height + 1);
        boxXYConsumer.accept(width, height - 1);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        boxXYConsumer.accept(width, height);
    }

}

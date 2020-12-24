package com.fuzs.consoleexperience.client.gui.screen.util;

import com.fuzs.consoleexperience.config.JSONConfigUtil;
import com.fuzs.consoleexperience.mixin.MainMenuScreenAccessorMixin;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.io.FileReader;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

@SuppressWarnings({"IntegerDivisionInFloatingPointContext", "SameParameterValue"})
public class FancyScreenUtil {

    private static final ResourceLocation PANORAMA_OVERLAY_TEXTURES = MainMenuScreenAccessorMixin.getPanoramaOverlayTextures();
    private static final ResourceLocation MINECRAFT_TITLE_TEXTURES = MainMenuScreenAccessorMixin.getMinecraftTitleTextures();
    private static final ResourceLocation MINECRAFT_TITLE_EDITION = MainMenuScreenAccessorMixin.getMinecraftTitleEdition();

    private static final List<ITextComponent> TIPS_LIST = Lists.newArrayList();
    public static final RenderSkybox MENU_PANORAMA = new RenderSkybox(MainMenuScreen.PANORAMA_RESOURCES) {

        private final Minecraft mc = Minecraft.getInstance();
        private float time;

        @Override
        public void render(float deltaT, float alpha) {

            this.time += 0.34F;
            MainMenuScreen.PANORAMA_RESOURCES.render(this.mc, MathHelper.sin(this.time * 0.001F) * 5.0F + 25.0F, -this.time * 0.1F, alpha);
        }
    };

    private static final int TIP_UPDATE_INTERVAL = 6000;
    private static long lastTipUpdate = TIP_UPDATE_INTERVAL;
    private static List<String> activeTip = Lists.newArrayList();

    public static void renderMenuElements(Minecraft minecraft, int width, int height) {

        renderMenuOverlay(minecraft, width, height);
        renderTitleElements(minecraft, width, height);
    }

    private static void renderMenuOverlay(Minecraft minecraft, int width, int height) {

        minecraft.getTextureManager().bindTexture(PANORAMA_OVERLAY_TEXTURES);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        AbstractGui.blit(0, 0, width, height, 0.0F, 0.0F, 16, 128, 16, 128);
    }

    private static void renderTitleElements(Minecraft minecraft, int width, int height) {

        int j = width / 2 - 137;
        int l = MathHelper.ceil(255.0F) << 24;
        if ((l & -67108864) != 0) {

            minecraft.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURES);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            blitBlackOutline(j, 30, (p_238657_2_, p_238657_3_) -> {

                AbstractGui.blit(p_238657_2_, p_238657_3_, 0, 0, 155, 44, 256, 256);
                AbstractGui.blit(p_238657_2_ + 155, p_238657_3_, 0, 45, 155, 44, 256, 256);
            });

            minecraft.getTextureManager().bindTexture(MINECRAFT_TITLE_EDITION);
            AbstractGui.blit(j + 88, 67, 0.0F, 0.0F, 98, 14, 128, 16);
        }
    }

    public static void renderLoadingBar(FontRenderer fontrenderer, @Nullable ITextComponent itextcomponent, int width, int height, int progress) {

        progress = (int) ((MathHelper.clamp(progress, 0, 100) / 100.0F) * 240);
        if (itextcomponent == null) {

            itextcomponent = new StringTextComponent("");
        }

        renderLoadingBar(fontrenderer,  itextcomponent, width / 2, height / 2 + 36, 240, 8, progress);
    }

    private static void renderLoadingBar(FontRenderer fontrenderer, ITextComponent itextcomponent, int posX, int posY, int width, int height, int progress) {

        AbstractGui.fill(posX - width / 2 - 1, posY - height / 2 - 1, posX + width / 2 + 1, posY + height / 2 + 1, -8684675);
        AbstractGui.fill(posX - width / 2, posY - height / 2, posX - width / 2 + progress, posY + height / 2, -15728895);
        fontrenderer.drawStringWithShadow(itextcomponent.getFormattedText(), posX - width / 2 + 2, posY - height / 2 - fontrenderer.FONT_HEIGHT - 2, 16777215);
    }

    public static void drawCenteredString(FontRenderer fontrenderer, @Nullable ITextComponent itextcomponent, int width, int height) {

        if (itextcomponent == null) {

            itextcomponent = new StringTextComponent("");
        }

        drawCenteredString(fontrenderer, itextcomponent, width / 2, 110, 16777215, 2.0F);
    }

    private static void drawCenteredString(FontRenderer fontrenderer, ITextComponent itextcomponent, int width, int height, int color, float scale) {

        RenderSystem.scalef(scale, scale, 0.0F);
        String component = itextcomponent.getFormattedText();
        fontrenderer.drawStringWithShadow(component, (int) (width / scale) - fontrenderer.getStringWidth(component) / 2, (int) (height / scale), color);
        RenderSystem.scalef(1.0F / scale, 1.0F / scale, 0.0F);
    }

    public static void renderPanorama() {

        MENU_PANORAMA.render(0.34F, 1.0F);
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

    public static void drawTooltip(Minecraft minecraft, int posX, int posY, int width, int height) {

        RenderTooltipUtil.drawTooltip(minecraft.fontRenderer, posX, posY, width, height, getActiveTip());
    }

    private static List<String> getActiveTip() {

        if (Util.milliTime() - TIP_UPDATE_INTERVAL > lastTipUpdate) {

            lastTipUpdate = Util.milliTime();
            activeTip = Minecraft.getInstance().fontRenderer.listFormattedStringToWidth(!TIPS_LIST.isEmpty() ?
                    TIPS_LIST.get((int) (TIPS_LIST.size() * Math.random())).getFormattedText() : new StringTextComponent("missingno").getFormattedText(), 270);
        }

        return activeTip;
    }

    public static void deserialize(FileReader reader) {

        TIPS_LIST.clear();
        Stream.of(JSONConfigUtil.GSON.fromJson(reader, String[].class))
                .forEach(tip -> TIPS_LIST.add(new TranslationTextComponent(tip)));
    }

}

package fuzs.consoleexperience.client.gui.screen.util;

import fuzs.consoleexperience.config.JSONConfigUtil;
import fuzs.consoleexperience.mixin.client.accessor.MainMenuScreenAccessor;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.io.FileReader;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

@SuppressWarnings({"deprecation", "unused", "SameParameterValue"})
public class FancyScreenUtil {

    private static final ResourceLocation PANORAMA_OVERLAY_TEXTURES = MainMenuScreenAccessor.getPanoramaOverlayTextures();
    private static final ResourceLocation MINECRAFT_TITLE_TEXTURES = MainMenuScreenAccessor.getMinecraftTitleTextures();
    private static final ResourceLocation MINECRAFT_TITLE_EDITION = MainMenuScreenAccessor.getMinecraftTitleEdition();

    private static final List<IFormattableTextComponent> TIPS_LIST = Lists.newArrayList();
    public static final RenderSkybox MENU_PANORAMA = new RenderSkybox(MainMenuScreen.CUBE_MAP) {

        private final Minecraft mc = Minecraft.getInstance();
        private float time;

        @Override
        public void render(float deltaT, float alpha) {

            this.time += 0.34F;
            MainMenuScreen.CUBE_MAP.render(this.mc, MathHelper.sin(this.time * 0.001F) * 5.0F + 25.0F, -this.time * 0.1F, alpha);
        }
    };

    private static final int TIP_UPDATE_INTERVAL = 6000;
    private static long lastTipUpdate = TIP_UPDATE_INTERVAL;
    private static IBidiRenderer activeTip = IBidiRenderer.EMPTY;

    public static void renderMenuElements(Minecraft minecraft, MatrixStack matrixStack, int width, int height) {

        renderMenuOverlay(minecraft, matrixStack, width, height);
        renderTitleElements(minecraft, matrixStack, width, height);
    }

    private static void renderMenuOverlay(Minecraft minecraft, MatrixStack matrixStack, int width, int height) {

        minecraft.getTextureManager().bind(PANORAMA_OVERLAY_TEXTURES);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        AbstractGui.blit(matrixStack, 0, 0, width, height, 0.0F, 0.0F, 16, 128, 16, 128);
    }

    private static void renderTitleElements(Minecraft minecraft, MatrixStack matrixStack, int width, int height) {

        int j = width / 2 - 137;
        int l = MathHelper.ceil(255.0F) << 24;
        if ((l & -67108864) != 0) {

            minecraft.getTextureManager().bind(MINECRAFT_TITLE_TEXTURES);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            blitBlackOutline(j, 30, (p_238657_2_, p_238657_3_) -> {

                AbstractGui.blit(matrixStack, p_238657_2_, p_238657_3_, 0, 0, 155, 44, 256, 256);
                AbstractGui.blit(matrixStack, p_238657_2_ + 155, p_238657_3_, 0, 45, 155, 44, 256, 256);
            });

            minecraft.getTextureManager().bind(MINECRAFT_TITLE_EDITION);
            AbstractGui.blit(matrixStack, j + 88, 67, 0.0F, 0.0F, 98, 14, 128, 16);
        }
    }

    public static void renderLoadingBar(MatrixStack matrixstack, FontRenderer fontrenderer, @Nullable ITextComponent itextcomponent, int width, int height, int progress) {

        progress = (int) ((MathHelper.clamp(progress, 0, 100) / 100.0F) * 240);
        if (itextcomponent == null) {

            itextcomponent = StringTextComponent.EMPTY;
        }

        renderLoadingBar(matrixstack, fontrenderer,  itextcomponent, width / 2, height / 2 + 36, 240, 8, progress);
    }

    private static void renderLoadingBar(MatrixStack matrixstack, FontRenderer fontrenderer, ITextComponent itextcomponent, int posX, int posY, int width, int height, int progress) {

        AbstractGui.fill(matrixstack, posX - width / 2 - 1, posY - height / 2 - 1, posX + width / 2 + 1, posY + height / 2 + 1, -8684675);
        AbstractGui.fill(matrixstack, posX - width / 2, posY - height / 2, posX - width / 2 + progress, posY + height / 2, -15728895);
        AbstractGui.drawString(matrixstack, fontrenderer, itextcomponent, posX - width / 2 + 2, posY - height / 2 - fontrenderer.lineHeight - 2, 16777215);
    }

    public static void drawCenteredString(MatrixStack matrixStack, FontRenderer fontrenderer, @Nullable ITextComponent itextcomponent, int width, int height) {

        if (itextcomponent == null) {

            itextcomponent = StringTextComponent.EMPTY;
        }

        drawCenteredString(matrixStack, fontrenderer, itextcomponent, width / 2, 110, 16777215, 2.0F);
    }

    private static void drawCenteredString(MatrixStack matrixStack, FontRenderer fontrenderer, ITextComponent itextcomponent, int width, int height, int color, float scale) {

        matrixStack.scale(scale, scale, 0.0F);
        AbstractGui.drawCenteredString(matrixStack, fontrenderer, itextcomponent, (int) (width / scale), (int) (height / scale), color);
        matrixStack.scale(1.0F / scale, 1.0F / scale, 0.0F);
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

    public static void drawTooltip(MatrixStack matrixstack, int posX, int posY, int width, int height) {

        RenderTooltipUtil.drawTooltip(matrixstack, posX, posY, width, height, getActiveTip());
    }

    private static IBidiRenderer getActiveTip() {

        if (Util.getMillis() - TIP_UPDATE_INTERVAL > lastTipUpdate) {

            lastTipUpdate = Util.getMillis();
            activeTip = IBidiRenderer.create(Minecraft.getInstance().font, !TIPS_LIST.isEmpty() ?
                    TIPS_LIST.get((int) (TIPS_LIST.size() * Math.random())) : new StringTextComponent("missingno"), 270);
        }

        return activeTip;
    }

    public static void deserialize(FileReader reader) {

        TIPS_LIST.clear();
        Stream.of(JSONConfigUtil.GSON.fromJson(reader, String[].class))
                .forEach(tip -> TIPS_LIST.add(new TranslationTextComponent(tip)));
    }

}

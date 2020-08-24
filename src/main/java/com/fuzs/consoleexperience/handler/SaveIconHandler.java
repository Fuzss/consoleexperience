package com.fuzs.consoleexperience.handler;

import com.fuzs.consoleexperience.ConsoleExperience;
import com.fuzs.consoleexperience.helper.PaperDollHelper;
import com.fuzs.consoleexperience.util.PositionPreset;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@SuppressWarnings("FieldCanBeLocal")
public class SaveIconHandler {

    private final Minecraft mc = Minecraft.getInstance();
    private static final ResourceLocation SAVE_ICONS = new ResourceLocation(ConsoleExperience.MODID,"textures/gui/auto_save.png");
    private final int width = 18;
    private final int height = 30;
    private int remainingDisplayTicks;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onSaveWorld(WorldEvent.Save evt) {

        if (ConfigBuildHandler.GENERAL_CONFIG.saveIcon.get()) {
            this.remainingDisplayTicks = ConfigBuildHandler.SAVE_ICON_CONFIG.displayTime.get();
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent evt) {

        if (evt.phase != TickEvent.Phase.END) {
            return;
        }

        if (this.remainingDisplayTicks > 0) {
            this.remainingDisplayTicks--;
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderGameOverlayPre(RenderGameOverlayEvent.Pre evt) {

        if (HideHudHandler.background == 0 && evt.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            MainWindow window = evt.getWindow();
            this.drawIcon(evt.getMatrixStack(), window.getScaledWidth(), window.getScaledHeight(), true);
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onBackgroundDrawn(GuiScreenEvent.BackgroundDrawnEvent evt) {

        if (this.mc.world != null) {
            MainWindow window = this.mc.getMainWindow();
            this.drawIcon(evt.getMatrixStack(), window.getScaledWidth(), window.getScaledHeight(), false);
        }

    }

    @SuppressWarnings("deprecation")
    private void drawIcon(MatrixStack matrixStack, int windowWidth, int windowHeight, boolean shift) {

        if ((this.remainingDisplayTicks > 0 || ConfigBuildHandler.SAVE_ICON_CONFIG.displayTime.get() == 0) && this.mc.player != null) {

            PositionPreset position = ConfigBuildHandler.SAVE_ICON_CONFIG.position.get();
            int k = position.getX(this.width, windowWidth, ConfigBuildHandler.SAVE_ICON_CONFIG.xOffset.get());
            int l = position.getY(this.height, windowHeight, ConfigBuildHandler.SAVE_ICON_CONFIG.yOffset.get());

            if (shift && ConfigBuildHandler.SAVE_ICON_CONFIG.potionShift.get() && position.shouldShift()) {
                l += PaperDollHelper.getPotionShift(this.mc.player.getActivePotionEffects());
            }

            this.mc.getTextureManager().bindTexture(SAVE_ICONS);
            RenderSystem.pushMatrix();
            RenderSystem.enableBlend();
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

            if (ConfigBuildHandler.SAVE_ICON_CONFIG.rotatingModel.get()) {

                int textureX = (int) ((this.remainingDisplayTicks % 12) * 0.5F) * 36;
                int textureY = 30 + ((int) ((this.remainingDisplayTicks % 48) * 0.5F) / 6) * 36;
                float f = 0.5F;
                RenderSystem.scalef(f, f, 1.0F);
                AbstractGui.blit(matrixStack, (int) (k / f), (int) ((l + 14) / f), textureX, textureY, 36, 36, 256, 256);
                RenderSystem.scalef(1.0F / f, 1.0F / f, 1.0F);

            } else {

                AbstractGui.blit(matrixStack, k, l, position.isMirrored() ? 162 : 144, 0, this.width, this.height, 256, 256);

            }

            if (ConfigBuildHandler.SAVE_ICON_CONFIG.showArrow.get()) {

                int x = (int) ((this.remainingDisplayTicks % 16) * 0.5F) * this.width;
                AbstractGui.blit(matrixStack, k, l, x, 0, this.width, this.height, 256, 256);

            }

            RenderSystem.disableBlend();
            RenderSystem.popMatrix();

        }

    }

}

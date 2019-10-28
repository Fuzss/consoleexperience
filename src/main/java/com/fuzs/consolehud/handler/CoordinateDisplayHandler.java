package com.fuzs.consolehud.handler;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CoordinateDisplayHandler {

    private final Minecraft mc = Minecraft.getInstance();

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void renderGameOverlayPre(RenderGameOverlayEvent.Text evt) {

        if (!ConfigHandler.GENERAL_CONFIG.coordinateDisplay.get() || this.mc.gameSettings.showDebugInfo) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        ITextComponent component;
        double d = Math.pow(10, ConfigHandler.COORDINATE_DISPLAY_CONFIG.decimalPlaces.get());
        double posX = Math.round(this.mc.player.posX * d) / d;
        double posY = Math.round(this.mc.player.getBoundingBox().minY * d) / d;
        double posZ = Math.round(this.mc.player.posZ * d) / d;

        if (ConfigHandler.COORDINATE_DISPLAY_CONFIG.decimalPlaces.get() == 0) {
            // no empty decimal place added like this
            component = new TranslationTextComponent("screen.coordinates", (int) posX, (int) posY, (int) posZ);
        } else {
            component = new TranslationTextComponent("screen.coordinates", posX, posY, posZ);
        }

        int f = (int) ((this.mc.gameSettings.chatOpacity * 0.9f + 0.1f) * 255.0f);
        int width = this.mc.fontRenderer.getStringWidth(component.getString());
        int x = ConfigHandler.COORDINATE_DISPLAY_CONFIG.xOffset.get();
        int y = ConfigHandler.COORDINATE_DISPLAY_CONFIG.yOffset.get();

        if (ConfigHandler.COORDINATE_DISPLAY_CONFIG.background.get()) {
            AbstractGui.fill(x, y, x + width + 3, y + 7 + 4, f / 2 << 24);
        }

        this.mc.fontRenderer.drawStringWithShadow(component.getFormattedText(), x + 2, y + 2, 16777215 + (f << 24));

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

    }

}

package com.fuzs.consolehud.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CoordinateDisplayHandler {

    private final Minecraft mc = Minecraft.getInstance();

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void renderGameOverlayPre(RenderGameOverlayEvent.Pre evt) {

        if (!ConfigHandler.GENERAL_CONFIG.coordinateDisplay.get() || evt.getType() != RenderGameOverlayEvent.ElementType.ALL || this.mc.gameSettings.showDebugInfo) {
            return;
        }

        ITextComponent component;
        double d = Math.pow(10, ConfigHandler.COORDINATE_DISPLAY_CONFIG.decimalPlaces.get());
        double posX = Math.round(this.mc.player.posX * d) / d;
        double posY = Math.round(this.mc.player.getBoundingBox().minY * d) / d;
        double posZ = Math.round(this.mc.player.posZ * d) / d;

        if (ConfigHandler.COORDINATE_DISPLAY_CONFIG.decimalPlaces.get() == 0) {

            component = new TextComponentTranslation("screen.coordinates", (int) posX, (int) posY, (int) posZ);

        } else {

            component = new TextComponentTranslation("screen.coordinates", posX, posY, posZ);

        }

        int f = (int) ((this.mc.gameSettings.chatOpacity * 0.9f + 0.1f) * 255.0f);
        int width = this.mc.fontRenderer.getStringWidth(component.getString());
        int x = ConfigHandler.COORDINATE_DISPLAY_CONFIG.xOffset.get();
        int y = ConfigHandler.COORDINATE_DISPLAY_CONFIG.yOffset.get();
        int i = ConfigHandler.COORDINATE_DISPLAY_CONFIG.backgroundBorder.get();

        if (ConfigHandler.COORDINATE_DISPLAY_CONFIG.background.get()) {
            Gui.drawRect(x, y, x + width + i * 2, y + 7 + i * 2, f / 2 << 24);
        }

        this.mc.fontRenderer.drawStringWithShadow(component.getFormattedText(), x + i, y + i, 16777215 + (f << 24));

    }

}

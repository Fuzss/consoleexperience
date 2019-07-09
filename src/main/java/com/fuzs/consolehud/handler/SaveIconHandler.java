package com.fuzs.consolehud.handler;

import com.fuzs.consolehud.ConsoleHud;
import com.fuzs.consolehud.helper.PaperDollHelper;
import com.fuzs.consolehud.util.EnumPositionPreset;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class SaveIconHandler extends GuiIngame {

    private static final ResourceLocation SAVE_ICONS = new ResourceLocation(ConsoleHud.MODID,"textures/gui/auto_save.png");
    private final int width = 18;
    private final int height = 30;
    private int remainingDisplayTicks;

    public SaveIconHandler() {
        super(Minecraft.getMinecraft());
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void saveWorld(WorldEvent.Save evt) {

        if (ConfigHandler.saveIcon) {
            this.remainingDisplayTicks = ConfigHandler.saveIconConfig.displayTime;
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent evt) {

        if (evt.phase != TickEvent.Phase.END) {
            return;
        }

        if (this.remainingDisplayTicks > 0) {
            this.remainingDisplayTicks--;
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void renderGameOverlayPre(RenderGameOverlayEvent.Pre evt) {

        if (evt.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        if (this.remainingDisplayTicks > 0) {

            EnumPositionPreset position = ConfigHandler.saveIconConfig.position;

            this.mc.getTextureManager().bindTexture(SAVE_ICONS);
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();

            int k = position.getX(this.width, evt.getResolution().getScaledWidth(), ConfigHandler.saveIconConfig.xOffset);
            int l = position.getY(this.height, evt.getResolution().getScaledHeight(), ConfigHandler.saveIconConfig.yOffset);

            if (ConfigHandler.saveIconConfig.potionShift && position.shouldShift()) {
                l += PaperDollHelper.getPotionShift(this.mc.player.getActivePotionEffects());
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            if (ConfigHandler.saveIconConfig.rotatingModel) {

                int textureX = (int) ((this.remainingDisplayTicks % 12) * 0.5F) * 36;
                int textureY = 30 + ((int) ((this.remainingDisplayTicks % 48) * 0.5F) / 6) * 36;
                GlStateManager.scale(0.5F, 0.5F, 0.5F);
                this.drawTexturedModalRect(k * 2, (l + 14) * 2, textureX, textureY, 36, 36);
                GlStateManager.scale(2.0F, 2.0F, 2.0F);

            } else {

                this.drawTexturedModalRect(k, l, position.isMirrored() ? 162 : 144, 0, this.width, this.height);

            }

            if (ConfigHandler.saveIconConfig.showArrow) {

                int x = (int) ((this.remainingDisplayTicks % 16) * 0.5F) * this.width;
                this.drawTexturedModalRect(k, l, x, 0, this.width, this.height);

            }

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();

        }

    }

}

package com.fuzs.consolehud.handler;

import com.fuzs.consolehud.ConsoleHud;
import com.fuzs.consolehud.helper.PaperDollHelper;
import com.fuzs.consolehud.util.EnumPositionPreset;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@SuppressWarnings("unused")
public class SaveIconHandler extends IngameGui {

    private static final ResourceLocation SAVE_ICONS = new ResourceLocation(ConsoleHud.MODID,"textures/gui/auto_save.png");
    private final int width = 18;
    private final int height = 30;
    private int remainingDisplayTicks;

    public SaveIconHandler() {
        super(Minecraft.getInstance());
    }

    @SubscribeEvent
    public void saveWorld(WorldEvent.Save evt) {

        if (ConfigHandler.GENERAL_CONFIG.saveIcon.get()) {
            this.remainingDisplayTicks = ConfigHandler.SAVE_ICON_CONFIG.displayTime.get();
        }

    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent evt) {

        if (evt.phase != TickEvent.Phase.END) {
            return;
        }

        if (this.remainingDisplayTicks > 0) {
            this.remainingDisplayTicks--;
        }

    }

    @SubscribeEvent
    public void renderGameOverlayPre(RenderGameOverlayEvent.Pre evt) {

        if (evt.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        if (this.remainingDisplayTicks > 0) {

            EnumPositionPreset position = ConfigHandler.SAVE_ICON_CONFIG.position.get();

            this.mc.getTextureManager().bindTexture(SAVE_ICONS);
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();

            int k = position.getX(this.width, evt.getWindow().getScaledWidth(), ConfigHandler.SAVE_ICON_CONFIG.xOffset.get());
            int l = position.getY(this.height, evt.getWindow().getScaledHeight(), ConfigHandler.SAVE_ICON_CONFIG.yOffset.get());

            if (ConfigHandler.SAVE_ICON_CONFIG.potionShift.get() && position.shouldShift()) {
                l += PaperDollHelper.getPotionShift(this.mc.player.getActivePotionEffects());
            }

            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

            if (ConfigHandler.SAVE_ICON_CONFIG.rotatingModel.get()) {

                int textureX = (int) ((this.remainingDisplayTicks % 12) * 0.5F) * 36;
                int textureY = 30 + ((int) ((this.remainingDisplayTicks % 48) * 0.5F) / 6) * 36;
                GlStateManager.scalef(0.5F, 0.5F, 0.5F);
                this.blit(k * 2, (l + 14) * 2, textureX, textureY, 36, 36);
                GlStateManager.scalef(2.0F, 2.0F, 2.0F);

            } else {

                this.blit(k, l, position.isMirrored() ? 162 : 144, 0, this.width, this.height);

            }

            if (ConfigHandler.SAVE_ICON_CONFIG.showArrow.get()) {

                int x = (int) ((this.remainingDisplayTicks % 16) * 0.5F) * this.width;
                this.blit(k, l, x, 0, this.width, this.height);

            }

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();

        }

    }

}

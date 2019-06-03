package com.fuzs.consolehud.handler;

import com.fuzs.consolehud.ConsoleHud;
import com.fuzs.consolehud.util.EnumPositionPreset;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Collection;

@SuppressWarnings("unused")
public class SaveIconHandler extends GuiIngame {

    private static final ResourceLocation SAVE_ICONS = new ResourceLocation(ConsoleHud.MODID,"textures/gui/auto_save.png");
    private final int width = 18;
    private final int height = 30;
    private int remainingDisplayTicks;

    public SaveIconHandler() {
        super(Minecraft.getMinecraft());
    }

    @SubscribeEvent
    public void saveWorld(WorldEvent.Save evt) {

        if (ConfigHandler.saveIcon) {
            this.remainingDisplayTicks = ConfigHandler.saveIconConfig.displayTime;
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

            EnumPositionPreset position = ConfigHandler.saveIconConfig.position;

            this.mc.getTextureManager().bindTexture(SAVE_ICONS);
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();

            int k = position.getX(this.width, evt.getResolution().getScaledWidth(), ConfigHandler.saveIconConfig.xOffset);
            int l = position.getY(this.height, evt.getResolution().getScaledHeight(), ConfigHandler.saveIconConfig.yOffset);

            if (ConfigHandler.saveIconConfig.potionShift && position.shouldShift()) {
                l += this.getPotionShift();
            }

            int textureX = (int) ((this.remainingDisplayTicks % 16) * 0.5F) * 18;
            int textureY = 0;

            if (position.isMirrored()) {
                textureY += 30;
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(k, l, textureX, textureY, this.width, this.height);

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();

        }

    }

    private int getPotionShift() {

        Collection<PotionEffect> collection = this.mc.player.getActivePotionEffects();
        int shift = 0;
        boolean renderInHUD = collection.stream().anyMatch(it -> it.getPotion().shouldRenderHUD(it));
        boolean doesShowParticles = collection.stream().anyMatch(PotionEffect::doesShowParticles);

        if (!collection.isEmpty() && renderInHUD && doesShowParticles) {
            shift += collection.stream().anyMatch(it -> !it.getPotion().isBeneficial()) ? 50 : 25;
        }

        return shift;

    }

}

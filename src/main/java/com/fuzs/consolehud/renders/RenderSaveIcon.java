package com.fuzs.consolehud.renders;

import com.fuzs.consolehud.ConsoleHud;
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

public class RenderSaveIcon extends GuiIngame {

    private static final ResourceLocation SAVE_ICONS = new ResourceLocation(ConsoleHud.MODID,"textures/gui/auto_save.png");
    private int remainingDisplayTicks;

    public RenderSaveIcon(Minecraft mc) {
        super(mc);
    }

    @SubscribeEvent
    public void saveWorld(WorldEvent.Save evt) {
        this.remainingDisplayTicks = 40;
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent evt) {
        if (evt.phase == TickEvent.Phase.START && this.remainingDisplayTicks > 0) {
            this.remainingDisplayTicks--;
        }
    }

    @SubscribeEvent
    public void renderGameOverlayPre(RenderGameOverlayEvent.Pre evt) {

        if (evt.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        if (this.remainingDisplayTicks > 0) {

            this.mc.getTextureManager().bindTexture(SAVE_ICONS);
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            int k = evt.getResolution().getScaledWidth() - 35;
            int l = 15;

            Collection<PotionEffect> collection = this.mc.player.getActivePotionEffects();
            if (!collection.isEmpty()) {
                l += 50;
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(k, l, (int) ((this.remainingDisplayTicks % 16) * 0.5F) * 18, 0, 18, 30);

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();

        }

    }

}

package com.fuzs.consolehud.renders;

import com.fuzs.consolehud.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.List;

public class RenderHoveringHotbar {

    private Minecraft mc;
    private List<RenderGameOverlayEvent.ElementType> elements;

    public RenderHoveringHotbar(Minecraft mc) {
        this.mc = mc;
        this.elements = Arrays.asList(
                RenderGameOverlayEvent.ElementType.ARMOR,
                RenderGameOverlayEvent.ElementType.HEALTH,
                RenderGameOverlayEvent.ElementType.FOOD,
                RenderGameOverlayEvent.ElementType.AIR,
                RenderGameOverlayEvent.ElementType.HOTBAR,
                RenderGameOverlayEvent.ElementType.EXPERIENCE,
                RenderGameOverlayEvent.ElementType.HEALTHMOUNT,
                RenderGameOverlayEvent.ElementType.JUMPBAR
        );
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderGameOverlayText2(RenderGameOverlayEvent.Pre event) {

        if (ConfigHandler.hoveringHotbar) {
            if (this.elements.contains(event.getType())) {
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.translate((float) ConfigHandler.hoveringHotbarConfig.xOffset, (float) -ConfigHandler.hoveringHotbarConfig.yOffset, 0.0F);
            } else if (this.mc.gameSettings.showDebugInfo && event.getType() == RenderGameOverlayEvent.ElementType.DEBUG) {
                GlStateManager.translate((float) -ConfigHandler.hoveringHotbarConfig.xOffset, (float) ConfigHandler.hoveringHotbarConfig.yOffset, 0.0F);
            }
        }

    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderGameOverlayTextPost(RenderGameOverlayEvent.Text event) {
        if (ConfigHandler.hoveringHotbar && !this.mc.gameSettings.showDebugInfo) {
            GlStateManager.translate((float) -ConfigHandler.hoveringHotbarConfig.xOffset, (float) ConfigHandler.hoveringHotbarConfig.yOffset, 0.0F);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void renderGameOverlayText2Post(RenderGameOverlayEvent.Post event) {

        if (ConfigHandler.hoveringHotbar && event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE && this.elements.contains(event.getType())) {
            GlStateManager.translate((float) -ConfigHandler.hoveringHotbarConfig.xOffset, (float) ConfigHandler.hoveringHotbarConfig.yOffset, 0.0F);
        }

    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderGameOverlayText3Post(RenderGameOverlayEvent.Post event) {

        if (ConfigHandler.hoveringHotbar && ConfigHandler.hoveringHotbarConfig.modCompat && event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translate((float) ConfigHandler.hoveringHotbarConfig.xOffset, (float) -ConfigHandler.hoveringHotbarConfig.yOffset, 0.0F);
        }

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void renderGameOverlayText4Post(RenderGameOverlayEvent.Post event) {

        if (ConfigHandler.hoveringHotbar && ConfigHandler.hoveringHotbarConfig.modCompat && event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            GlStateManager.translate((float) -ConfigHandler.hoveringHotbarConfig.xOffset, (float) ConfigHandler.hoveringHotbarConfig.yOffset, 0.0F);
        }

    }
}

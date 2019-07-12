package com.fuzs.consolehud.handler;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.List;

public class HoveringHotbarHandler {

    // list of gui elements to be moved
    private List<RenderGameOverlayEvent.ElementType> elements = Arrays.asList(
            RenderGameOverlayEvent.ElementType.ARMOR,
            RenderGameOverlayEvent.ElementType.HEALTH,
            RenderGameOverlayEvent.ElementType.FOOD,
            RenderGameOverlayEvent.ElementType.AIR,
            RenderGameOverlayEvent.ElementType.HOTBAR,
            RenderGameOverlayEvent.ElementType.EXPERIENCE,
            RenderGameOverlayEvent.ElementType.HEALTHMOUNT,
            RenderGameOverlayEvent.ElementType.JUMPBAR
    );

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderGameOverlayPre(RenderGameOverlayEvent.Pre evt) {

        if (!ConfigHandler.hoveringHotbar) {
            return;
        }

        if (this.elements.contains(evt.getType())) {
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translate((float) ConfigHandler.hoveringHotbarConfig.xOffset, (float) -ConfigHandler.hoveringHotbarConfig.yOffset, 0.0F);
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void renderGameOverlayPost(RenderGameOverlayEvent.Post evt) {

        if (!ConfigHandler.hoveringHotbar) {
            return;
        }

        if (this.elements.contains(evt.getType())) {
            GlStateManager.translate((float) -ConfigHandler.hoveringHotbarConfig.xOffset, (float) ConfigHandler.hoveringHotbarConfig.yOffset, 0.0F);
        }

        if (ConfigHandler.hoveringHotbarConfig.modCompat && evt.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            GlStateManager.translate((float) -ConfigHandler.hoveringHotbarConfig.xOffset, (float) ConfigHandler.hoveringHotbarConfig.yOffset, 0.0F);
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderGameOverlayPostAll(RenderGameOverlayEvent.Post evt) {

        if (!ConfigHandler.hoveringHotbar) {
            return;
        }

        if (ConfigHandler.hoveringHotbarConfig.modCompat && evt.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translate((float) ConfigHandler.hoveringHotbarConfig.xOffset, (float) -ConfigHandler.hoveringHotbarConfig.yOffset, 0.0F);
        }

    }
}

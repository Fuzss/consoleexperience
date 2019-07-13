package com.fuzs.consolehud.handler;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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

        if (!ConfigHandler.GENERAL_CONFIG.hoveringHotbar.get()) {
            return;
        }

        if (this.elements.contains(evt.getType())) {
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float) ConfigHandler.HOVERING_HOTBAR_CONFIG.xOffset.get(), (float) -ConfigHandler.HOVERING_HOTBAR_CONFIG.yOffset.get(), 0.0F);
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void renderGameOverlayPost(RenderGameOverlayEvent.Post evt) {

        if (!ConfigHandler.GENERAL_CONFIG.hoveringHotbar.get()) {
            return;
        }

        if (this.elements.contains(evt.getType())) {
            GlStateManager.translatef((float) -ConfigHandler.HOVERING_HOTBAR_CONFIG.xOffset.get(), (float) ConfigHandler.HOVERING_HOTBAR_CONFIG.yOffset.get(), 0.0F);
        }

        if (ConfigHandler.HOVERING_HOTBAR_CONFIG.modCompat.get() && evt.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            GlStateManager.translatef((float) -ConfigHandler.HOVERING_HOTBAR_CONFIG.xOffset.get(), (float) ConfigHandler.HOVERING_HOTBAR_CONFIG.yOffset.get(), 0.0F);
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderGameOverlayPostAll(RenderGameOverlayEvent.Post evt) {

        if (!ConfigHandler.GENERAL_CONFIG.hoveringHotbar.get()) {
            return;
        }

        if (ConfigHandler.HOVERING_HOTBAR_CONFIG.modCompat.get() && evt.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float) ConfigHandler.HOVERING_HOTBAR_CONFIG.xOffset.get(), (float) -ConfigHandler.HOVERING_HOTBAR_CONFIG.yOffset.get(), 0.0F);
        }

    }
}

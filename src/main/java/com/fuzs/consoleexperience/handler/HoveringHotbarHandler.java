package com.fuzs.consoleexperience.handler;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Arrays;
import java.util.List;

public class HoveringHotbarHandler {

    // list of gui elements to be moved, idea is to basically wrap around them and whatever other mods would be doing
    private final List<RenderGameOverlayEvent.ElementType> elements = Arrays.asList(
            ElementType.ARMOR, ElementType.HEALTH, ElementType.FOOD, ElementType.AIR, ElementType.HOTBAR,
            ElementType.EXPERIENCE, ElementType.HEALTHMOUNT, ElementType.JUMPBAR
    );

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void renderGameOverlayPre(RenderGameOverlayEvent.Pre evt) {

        if (!ConfigBuildHandler.GENERAL_CONFIG.hoveringHotbar.get()) {
            return;
        }

        if (this.elements.contains(evt.getType())) {
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float) ConfigBuildHandler.HOVERING_HOTBAR_CONFIG.xOffset.get(), (float) -ConfigBuildHandler.HOVERING_HOTBAR_CONFIG.yOffset.get(), 0.0F);
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.LOW)
    public void renderGameOverlayPost(RenderGameOverlayEvent.Post evt) {

        if (!ConfigBuildHandler.GENERAL_CONFIG.hoveringHotbar.get()) {
            return;
        }

        if (this.elements.contains(evt.getType())) {
            GlStateManager.translatef((float) -ConfigBuildHandler.HOVERING_HOTBAR_CONFIG.xOffset.get(), (float) ConfigBuildHandler.HOVERING_HOTBAR_CONFIG.yOffset.get(), 0.0F);
        }

        if (ConfigBuildHandler.HOVERING_HOTBAR_CONFIG.modCompat.get() && evt.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            GlStateManager.translatef((float) -ConfigBuildHandler.HOVERING_HOTBAR_CONFIG.xOffset.get(), (float) ConfigBuildHandler.HOVERING_HOTBAR_CONFIG.yOffset.get(), 0.0F);
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void renderGameOverlayPostAll(RenderGameOverlayEvent.Post evt) {

        if (!ConfigBuildHandler.GENERAL_CONFIG.hoveringHotbar.get()) {
            return;
        }

        if (ConfigBuildHandler.HOVERING_HOTBAR_CONFIG.modCompat.get() && evt.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float) ConfigBuildHandler.HOVERING_HOTBAR_CONFIG.xOffset.get(), (float) -ConfigBuildHandler.HOVERING_HOTBAR_CONFIG.yOffset.get(), 0.0F);
        }

    }
}

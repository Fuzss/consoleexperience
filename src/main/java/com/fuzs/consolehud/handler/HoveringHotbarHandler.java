package com.fuzs.consolehud.handler;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class HoveringHotbarHandler {

    private final Minecraft mc = Minecraft.getInstance();
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

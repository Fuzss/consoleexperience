package com.fuzs.consoleexperience.handler;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class HoveringHotbarHandler {

    private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widgets.png");

    private final Minecraft mc = Minecraft.getInstance();
    // list of gui elements to be moved, idea is to basically wrap around them and whatever other mods would be doing
    private final List<RenderGameOverlayEvent.ElementType> elements = Lists.newArrayList(
            ElementType.ARMOR, ElementType.HEALTH, ElementType.FOOD, ElementType.AIR, ElementType.HOTBAR,
            ElementType.EXPERIENCE, ElementType.HEALTHMOUNT, ElementType.JUMPBAR
    );

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onRenderGameOverlayPre(RenderGameOverlayEvent.Pre evt) {

        if (!ConfigBuildHandler.GENERAL_CONFIG.hoveringHotbar.get()) {
            return;
        }

        if (this.elements.contains(evt.getType())) {
            GlStateManager.translatef((float) ConfigBuildHandler.HOVERING_HOTBAR_CONFIG.xOffset.get(), (float) -ConfigBuildHandler.HOVERING_HOTBAR_CONFIG.yOffset.get(), 0.0F);
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onRenderGameOverlayPost(RenderGameOverlayEvent.Pre evt) {

        if (!ConfigBuildHandler.GENERAL_CONFIG.hoveringHotbar.get()) {
            return;
        }

        if (evt.isCanceled() && this.elements.contains(evt.getType())) {
            GlStateManager.translatef((float) -ConfigBuildHandler.HOVERING_HOTBAR_CONFIG.xOffset.get(), (float) ConfigBuildHandler.HOVERING_HOTBAR_CONFIG.yOffset.get(), 0.0F);
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderGameOverlayPost(RenderGameOverlayEvent.Post evt) {

        if (!ConfigBuildHandler.GENERAL_CONFIG.hoveringHotbar.get()) {
            return;
        }

        if (this.elements.contains(evt.getType())) {
            GlStateManager.translatef((float) -ConfigBuildHandler.HOVERING_HOTBAR_CONFIG.xOffset.get(), (float) ConfigBuildHandler.HOVERING_HOTBAR_CONFIG.yOffset.get(), 0.0F);
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderGameOverlayPostHotbar(RenderGameOverlayEvent.Post evt) {

        if (!ConfigBuildHandler.GENERAL_CONFIG.hoveringHotbar.get()) {
            return;
        }

        if (evt.getType() == ElementType.HOTBAR) {

            int width = evt.getWindow().getScaledWidth();
            int height = evt.getWindow().getScaledHeight();
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(WIDGETS);
            GlStateManager.enableBlend();
            AbstractGui.blit(width / 2 - 91 - 1 + this.mc.player.inventory.currentItem * 20, height - 1, 0, 44, 24, 2, 256, 256);
            GlStateManager.disableBlend();

        }

    }

}

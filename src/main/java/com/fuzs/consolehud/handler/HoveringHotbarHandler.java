package com.fuzs.consolehud.handler;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class HoveringHotbarHandler extends GuiIngame {

    private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widgets.png");

    // list of gui elements to be moved, idea is to basically wrap around them and whatever other mods would be doing
    private final List<RenderGameOverlayEvent.ElementType> elements = Lists.newArrayList(
            ElementType.ARMOR, ElementType.HEALTH, ElementType.FOOD, ElementType.AIR, ElementType.HOTBAR,
            ElementType.EXPERIENCE, ElementType.HEALTHMOUNT, ElementType.JUMPBAR
    );

    public HoveringHotbarHandler() {

        super(Minecraft.getMinecraft());

    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onRenderGameOverlayPre(RenderGameOverlayEvent.Pre evt) {

        if (!ConfigHandler.hoveringHotbar) {
            return;
        }

        if (this.elements.contains(evt.getType())) {
            GlStateManager.translate((float) ConfigHandler.hoveringHotbarConfig.xOffset, (float) -ConfigHandler.hoveringHotbarConfig.yOffset, 0.0F);
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onRenderGameOverlayPost(RenderGameOverlayEvent.Pre evt) {

        if (!ConfigHandler.hoveringHotbar) {
            return;
        }

        if (evt.isCanceled() && this.elements.contains(evt.getType())) {
            GlStateManager.translate((float) -ConfigHandler.hoveringHotbarConfig.xOffset, (float) ConfigHandler.hoveringHotbarConfig.yOffset, 0.0F);
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderGameOverlayPost(RenderGameOverlayEvent.Post evt) {

        if (!ConfigHandler.hoveringHotbar) {
            return;
        }

        if (this.elements.contains(evt.getType())) {
            GlStateManager.translate((float) -ConfigHandler.hoveringHotbarConfig.xOffset, (float) ConfigHandler.hoveringHotbarConfig.yOffset, 0.0F);
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderGameOverlayPostHotbar(RenderGameOverlayEvent.Post evt) {

        if (!ConfigHandler.hoveringHotbar || this.mc.player == null) {
            return;
        }

        if (evt.getType() == ElementType.HOTBAR) {

            int width = evt.getResolution().getScaledWidth();
            int height = evt.getResolution().getScaledHeight();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(WIDGETS);
            GlStateManager.enableBlend();
            this.drawTexturedModalRect(width / 2 - 91 - 1 + this.mc.player.inventory.currentItem * 20, height - 1, 0, 44, 24, 2);
            GlStateManager.disableBlend();

        }

    }

}
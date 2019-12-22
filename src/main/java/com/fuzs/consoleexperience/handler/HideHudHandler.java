package com.fuzs.consoleexperience.handler;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class HideHudHandler {

    private final Minecraft mc = Minecraft.getInstance();
    // list of hud elements allowed to be hidden
    private final List<RenderGameOverlayEvent.ElementType> elements = Lists.newArrayList(
            ElementType.CROSSHAIRS, ElementType.BOSSHEALTH, ElementType.BOSSINFO, ElementType.ARMOR, ElementType.HEALTH,
            ElementType.FOOD, ElementType.AIR, ElementType.HOTBAR, ElementType.EXPERIENCE, ElementType.TEXT,
            ElementType.HEALTHMOUNT, ElementType.JUMPBAR, ElementType.CHAT, ElementType.PLAYER_LIST, ElementType.DEBUG,
            ElementType.POTION_ICONS, ElementType.SUBTITLES, ElementType.FPS_GRAPH
    );

    public static int background;
    public static boolean active;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onBackgroundDrawn(GuiScreenEvent.BackgroundDrawnEvent evt) {

        if (this.mc.world != null) {
            background = 2;
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderGameOverlayPre(RenderGameOverlayEvent.Pre evt) {

        active = ConfigBuildHandler.MISCELLANEOUS_CONFIG.hideHudInGui.get() && background > 0;
        if (active) {
            if (evt.getType() == ElementType.ALL) {
                background--;
            } else if (this.elements.contains(evt.getType())) {
                evt.setCanceled(true);
            }
        }

    }

}

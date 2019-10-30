package com.fuzs.consoleexperience.handler;

import com.fuzs.consoleexperience.helper.ReflectionHelper;
import com.fuzs.consoleexperience.helper.ShulkerBoxTooltipHelper;
import com.fuzs.consoleexperience.util.CloseButton;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class ClientEventHandler {

    private final Minecraft mc = Minecraft.getInstance();
    // list of hud elements allowed to be hidden
    private final List<RenderGameOverlayEvent.ElementType> elements = Arrays.asList(
            ElementType.CROSSHAIRS, ElementType.BOSSHEALTH, ElementType.BOSSINFO, ElementType.ARMOR, ElementType.HEALTH,
            ElementType.FOOD, ElementType.AIR, ElementType.HOTBAR, ElementType.EXPERIENCE, ElementType.HEALTHMOUNT,
            ElementType.JUMPBAR, ElementType.PLAYER_LIST, ElementType.DEBUG, ElementType.POTION_ICONS,
            ElementType.SUBTITLES, ElementType.FPS_GRAPH
    );

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.LOW)
    public void makeTooltip(ItemTooltipEvent evt) {

        if (ConfigBuildHandler.MISCELLANEOUS_CONFIG.sumShulkerBox.get() && Block.getBlockFromItem(evt.getItemStack().getItem()) instanceof ShulkerBoxBlock) {

            List<ITextComponent> tooltip = evt.getToolTip();
            List<ITextComponent> contents = Lists.newArrayList();

            evt.getItemStack().getItem().addInformation(evt.getItemStack(), evt.getEntityPlayer() == null ? null : evt.getEntityPlayer().world, contents, evt.getFlags());

            if (!tooltip.isEmpty() && !contents.isEmpty()) {

                int i = tooltip.indexOf(contents.get(0));

                if (i != -1 && tooltip.removeAll(contents)) {

                    List<ITextComponent> list = Lists.newArrayList();
                    ShulkerBoxTooltipHelper.getLootTableTooltip(list, evt.getItemStack());
                    ShulkerBoxTooltipHelper.getContentsTooltip(list, evt.getItemStack(), new Style().setColor(TextFormatting.GRAY), 6);
                    tooltip.addAll(i, list);

                }

            }

        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderGameOverlayPre(RenderGameOverlayEvent.Pre evt) {

        boolean flag = ConfigBuildHandler.MISCELLANEOUS_CONFIG.hideHudInGui.get() && this.mc.currentScreen instanceof ContainerScreen;
        if (flag && this.elements.contains(evt.getType())) {
            evt.setCanceled(true);
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderGameOverlayText(RenderGameOverlayEvent.Text evt) {

        if (ConfigBuildHandler.MISCELLANEOUS_CONFIG.hideHudInGui.get() && this.mc.currentScreen instanceof ContainerScreen) {
            evt.setCanceled(true);
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderGameOverlayChat(RenderGameOverlayEvent.Chat evt) {

        if (ConfigBuildHandler.MISCELLANEOUS_CONFIG.hideHudInGui.get() && this.mc.currentScreen instanceof ContainerScreen) {
            evt.setCanceled(true);
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void initGui(GuiScreenEvent.InitGuiEvent.Post evt) {

        if (!ConfigBuildHandler.MISCELLANEOUS_CONFIG.closeButton.get() || !(evt.getGui() instanceof ContainerScreen)) {
            return;
        }

        ContainerScreen screen = (ContainerScreen) evt.getGui();

        if (screen.getXSize() != 176 || screen.getYSize() != 166) {
            return;
        }

        int x = ConfigBuildHandler.MISCELLANEOUS_CONFIG.closeButtonXOffset.get();
        int y = ConfigBuildHandler.MISCELLANEOUS_CONFIG.closeButtonYOffset.get();
        CloseButton button = new CloseButton(screen.getGuiLeft() + screen.getXSize() - x - 15,
                screen.getGuiTop() + y, p_213076_1_ -> this.mc.player.closeScreen());

        try {

            Method method = ReflectionHelper.getAddButton();
            if (method != null) {
                method.invoke(evt.getGui(), button);
            }

        } catch (IllegalAccessException | InvocationTargetException e) {

            e.printStackTrace();

        }

    }

}

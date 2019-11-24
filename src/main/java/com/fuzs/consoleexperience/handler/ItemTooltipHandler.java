package com.fuzs.consoleexperience.handler;

import com.fuzs.consoleexperience.helper.ItemTooltipHelper;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class ItemTooltipHandler {

    private final ItemTooltipHelper itemHelper = new ItemTooltipHelper();

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onMakeTooltip(ItemTooltipEvent evt) {

        if (ConfigBuildHandler.MISCELLANEOUS_CONFIG.sumShulkerBox.get() && Block.getBlockFromItem(evt.getItemStack().getItem()) instanceof ShulkerBoxBlock) {

            List<ITextComponent> tooltip = evt.getToolTip();
            List<ITextComponent> contents = Lists.newArrayList();

            evt.getItemStack().getItem().addInformation(evt.getItemStack(), evt.getEntityPlayer() == null ? null : evt.getEntityPlayer().world, contents, evt.getFlags());

            if (!tooltip.isEmpty() && !contents.isEmpty()) {

                int i = tooltip.indexOf(contents.get(0));

                if (i != -1 && tooltip.removeAll(contents)) {

                    List<ITextComponent> list = Lists.newArrayList();
                    this.itemHelper.getLootTableTooltip(list, evt.getItemStack());
                    this.itemHelper.getContentsTooltip(list, evt.getItemStack(), new Style().setColor(TextFormatting.GRAY), ConfigBuildHandler.MISCELLANEOUS_CONFIG.shulkerBoxRows.get());
                    tooltip.addAll(i, list);

                }

            }

        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderTooltipColor(RenderTooltipEvent.Color evt) {

        if (ConfigBuildHandler.MISCELLANEOUS_CONFIG.tintedTooltip.get()) {

            evt.setBorderStart(0xEBFFFFFF);
            evt.setBorderEnd(0xE6FFFFFF);
            evt.setBackground(0xAA09202A);

        }

    }

}

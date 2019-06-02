package com.fuzs.consolehud.helper;

import com.fuzs.consolehud.handler.ConfigHandler;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class TooltipHelper extends TooltipElementsHelper {

    public List<String> createTooltip(ItemStack stack, boolean simple) {

        this.itemstack = stack;
        List<String> tooltip = Lists.newArrayList();

        if (simple) {

            this.getName(tooltip, new Style().setColor(TextFormatting.WHITE), true, ITooltipFlag.TooltipFlags.NORMAL);

        } else {

            this.getName(tooltip, new Style().setColor(TextFormatting.WHITE), false, ITooltipFlag.TooltipFlags.NORMAL);
            this.getInformation(tooltip, new Style().setColor(TextFormatting.GRAY), ITooltipFlag.TooltipFlags.NORMAL);

            if (stack.getItem() instanceof ItemShulkerBox && tooltip.size() == ConfigHandler.heldItemTooltipsConfig.rows) {
                return tooltip;
            }

            this.getEnchantments(tooltip, new Style().setColor(TextFormatting.GRAY));
            this.getColorTag(tooltip, new Style().setColor(TextFormatting.GRAY), ITooltipFlag.TooltipFlags.NORMAL);
            this.getLoreTag(tooltip, new Style().setItalic(true).setColor(TextFormatting.DARK_PURPLE));
            //this.getUnbreakable(tooltip, new Style().setColor(TextFormatting.BLUE));
            //this.getAdventureStats(tooltip, new Style().setColor(TextFormatting.GRAY));

            if (ConfigHandler.heldItemTooltipsConfig.appearanceConfig.durabilityShow) {
                this.getDurability(tooltip, new Style().setColor(TextFormatting.GRAY));
            }

            //this.getNameID(tooltip, new Style().setColor(TextFormatting.GRAY));
            //this.getNBTAmount(tooltip, new Style().setColor(TextFormatting.GRAY));

            if (ConfigHandler.heldItemTooltipsConfig.appearanceConfig.moddedTooltips) {
                this.getForgeInformation(tooltip, ITooltipFlag.TooltipFlags.NORMAL);
            }

            if (tooltip.size() > ConfigHandler.heldItemTooltipsConfig.rows) {
                this.fitList(tooltip);
            }

        }

        return tooltip;

    }

    private void fitList(List<String> list) {

        int i = list.size() - ConfigHandler.heldItemTooltipsConfig.rows + 1;
        list.subList(ConfigHandler.heldItemTooltipsConfig.rows + (ConfigHandler.heldItemTooltipsConfig.appearanceConfig.lastLineShow ? -1 : 0), list.size()).clear();
        if (ConfigHandler.heldItemTooltipsConfig.appearanceConfig.lastLineShow) {
            this.getLastLine(list, new Style().setItalic(true).setColor(TextFormatting.GRAY), i);
        }

    }

    public static void getAdventureBlockInfo(List<String> list, Style style, NBTTagList nbttaglist) {

        for (int k1 = 0; k1 < nbttaglist.tagCount(); ++k1)
        {
            Block block1 = Block.getBlockFromName(nbttaglist.getStringTagAt(k1));

            if (block1 != null)
            {
                list.add(new TextComponentString(block1.getLocalizedName()).setStyle(style).getFormattedText());
            }
            else
            {
                list.add(new TextComponentString("missingno").setStyle(style).getFormattedText());
            }
        }

    }

}

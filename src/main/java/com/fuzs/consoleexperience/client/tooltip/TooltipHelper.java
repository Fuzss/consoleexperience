package com.fuzs.consoleexperience.client.tooltip;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class TooltipHelper extends TooltipElementsHelper {

    private final Minecraft mc = Minecraft.getInstance();

    public List<ITextComponent> createTooltip(ItemStack stack, boolean simple, int rows) {

        this.itemstack = stack;
        List<ITextComponent> tooltip = Lists.newArrayList();

        this.getName(tooltip, TextFormatting.WHITE, ITooltipFlag.TooltipFlags.NORMAL);

        if (simple) {
            return tooltip;
        }

        this.getInformation(tooltip, TextFormatting.GRAY, ITooltipFlag.TooltipFlags.ADVANCED, this.mc.world, rows);
        if (Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock && tooltip.size() == rows) {

            return tooltip;
        }

        this.getEnchantments(tooltip, TextFormatting.GRAY);
        this.getColorTag(tooltip, TextFormatting.GRAY, ITooltipFlag.TooltipFlags.ADVANCED);
        this.getLoreTag(tooltip, TextFormatting.DARK_PURPLE);
        this.getUnbreakable(tooltip, TextFormatting.BLUE);
        this.getDurability(tooltip, TextFormatting.GRAY, false);
        this.getNameID(tooltip, TextFormatting.GRAY);
        this.getNBTAmount(tooltip, TextFormatting.GRAY);
        this.getForgeInformation(tooltip, ITooltipFlag.TooltipFlags.NORMAL);

        this.applyLastLine(tooltip, rows);

        return tooltip;

    }

    private void applyLastLine(List<ITextComponent> tooltip, int rows) {

        boolean showDurability = true, forceDurability = true, showLastLine = true;
        boolean flag = showDurability && forceDurability && this.itemstack.isDamaged();
        int i = 0, j = 0; // i counts the lines to be added afterwards, j is for counting how many lines to remove

        if (flag) {
            i++;
        }

        if (tooltip.size() + i > rows) {

            if (showLastLine) {
                i++;
            }

            j = tooltip.size() - rows + i;

            if (j == tooltip.size()) {
                i--; // prevent item name from being removed
                j = this.itemstack.isDamaged() ? 0 : j; // prioritise durability over last line
            }

            tooltip.subList(rows - i, tooltip.size()).clear();

        }

        if (flag) {
            this.getDurability(tooltip, TextFormatting.GRAY, true);
        }

        if (j > 0 && showLastLine) {
            this.getLastLine(tooltip, TextFormatting.GRAY, j);
        }

    }

}

package com.fuzs.consoleexperience.client.tooltip;

import com.fuzs.consoleexperience.client.util.ConfigBuildHandler;
import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TooltipHelper extends TooltipElementsHelper {

    private final Minecraft mc = Minecraft.getInstance();

    public List<ITextComponent> createTooltip(ItemStack stack, boolean simple) {

        this.itemstack = stack;
        List<ITextComponent> tooltip = Lists.newArrayList();

        this.getName(tooltip, Style.EMPTY.setFormatting(TextFormatting.WHITE), ITooltipFlag.TooltipFlags.NORMAL);

        if (simple) {
            return tooltip;
        }

        this.getInformation(tooltip, ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.textColor.get().getChatColor(), ITooltipFlag.TooltipFlags.ADVANCED, this.mc.player.world);

        if (Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock && tooltip.size() == ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.rows.get()) {
            return tooltip;
        }

        this.getEnchantments(tooltip, Style.EMPTY.setFormatting(ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.textColor.get().getChatColor()));
        this.getColorTag(tooltip, Style.EMPTY.setFormatting(ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.textColor.get().getChatColor()), ITooltipFlag.TooltipFlags.ADVANCED);
        this.getLoreTag(tooltip, Style.EMPTY.setItalic(true).setFormatting(TextFormatting.DARK_PURPLE));
        //this.getUnbreakable(tooltip, Style.EMPTY.setFormatting(TextFormatting.BLUE));
        this.getDurability(tooltip, Style.EMPTY.setFormatting(ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.textColor.get().getChatColor()), false);
        //this.getNameID(tooltip, Style.EMPTY.setFormatting(ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.textColor.getChatColor()));
        //this.getNBTAmount(tooltip, Style.EMPTY.setFormatting(ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.textColor.getChatColor()));
        this.getForgeInformation(tooltip, ITooltipFlag.TooltipFlags.NORMAL);

        this.applyLastLine(tooltip);

        return tooltip;

    }

    private void applyLastLine(List<ITextComponent> tooltip) {

        boolean flag = ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.showDurability.get() && ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.forceDurability.get() && this.itemstack.isDamaged();
        int i = 0, j = 0; // i counts the lines to be added afterwards, j is for counting how many lines to remove

        if (flag) {
            i++;
        }

        if (tooltip.size() + i > ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.rows.get()) {

            if (ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.showLastLine.get()) {
                i++;
            }

            j = tooltip.size() - ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.rows.get() + i;

            if (j == tooltip.size()) {
                i--; // prevent item name from being removed
                j = this.itemstack.isDamaged() ? 0 : j; // prioritise durability over last line
            }

            tooltip.subList(ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.rows.get() - i, tooltip.size()).clear();

        }

        if (flag) {
            this.getDurability(tooltip, Style.EMPTY.setFormatting(ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.textColor.get().getChatColor()), true);
        }

        if (j > 0 && ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.showLastLine.get()) {
            this.getLastLine(tooltip, Style.EMPTY.setItalic(true).setFormatting(ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.textColor.get().getChatColor()), j);
        }

    }

}

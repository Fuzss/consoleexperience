package com.fuzs.consolehud.helper;

import com.fuzs.consolehud.handler.ConfigHandler;
import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TooltipHelper extends TooltipElementsHelper {

    private final Minecraft mc;

    public TooltipHelper(Minecraft mc) {
        this.mc = mc;
    }

    public List<ITextComponent> createTooltip(ItemStack stack, boolean simple) {

        this.itemstack = stack;
        List<ITextComponent> tooltip = Lists.newArrayList();

        this.getName(tooltip, new Style().setColor(TextFormatting.WHITE), ITooltipFlag.TooltipFlags.NORMAL);

        if (simple) {
            return tooltip;
        }

        this.getInformation(tooltip, new Style().setColor(ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.textColor.get().getChatColor()), ITooltipFlag.TooltipFlags.ADVANCED, this.mc.player.world);

        if (Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock && tooltip.size() == ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.rows.get()) {
            return tooltip;
        }

        this.getEnchantments(tooltip, new Style().setColor(ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.textColor.get().getChatColor()));
        this.getColorTag(tooltip, new Style().setColor(ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.textColor.get().getChatColor()), ITooltipFlag.TooltipFlags.ADVANCED);
        this.getLoreTag(tooltip, new Style().setItalic(true).setColor(TextFormatting.DARK_PURPLE));
        //this.getUnbreakable(tooltip, new Style().setColor(TextFormatting.BLUE));
        //this.getAdventureStats(tooltip, new Style().setColor(ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.textColor.getChatColor()));
        this.getDurability(tooltip, new Style().setColor(ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.textColor.get().getChatColor()), false);
        //this.getNameID(tooltip, new Style().setColor(ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.textColor.getChatColor()));
        //this.getNBTAmount(tooltip, new Style().setColor(ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.textColor.getChatColor()));
        this.getForgeInformation(tooltip, ITooltipFlag.TooltipFlags.NORMAL);

        this.applyLastLine(tooltip);

        return tooltip;

    }

    private void applyLastLine(List<ITextComponent> tooltip) {

        boolean flag = ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.showDurability.get() && ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.forceDurability.get() && this.itemstack.isDamaged();
        int i = 0, j = 0; // i counts the lines to be added afterwards, j is for counting how many lines to remove

        if (flag) {
            i++;
        }

        if (tooltip.size() + i > ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.rows.get()) {

            if (ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.showLastLine.get()) {
                i++;
            }

            j = tooltip.size() - ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.rows.get() + i;

            if (j == tooltip.size()) {
                i--; // prevent item name from being removed
                j = this.itemstack.isDamaged() ? 0 : j; // prioritise durability over last line
            }

            tooltip.subList(ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.rows.get() - i, tooltip.size()).clear();

        }

        if (flag) {
            this.getDurability(tooltip, new Style().setColor(ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.textColor.get().getChatColor()), true);
        }

        if (j > 0 && ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.showLastLine.get()) {
            this.getLastLine(tooltip, new Style().setItalic(true).setColor(ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.textColor.get().getChatColor()), j);
        }

    }

    @SuppressWarnings({"WeakerAccess", "ConstantConditions"})
    protected static void getAdventureBlockInfo(List<ITextComponent> list, Style style, ListNBT nbttaglist) {

        for (int i = 0; i < nbttaglist.size(); i++) {

            try {

                BlockStateParser blockstateparser = new BlockStateParser(new StringReader(nbttaglist.getString(i)), true).parse(true);
                BlockState blockstate = blockstateparser.getState();
                ResourceLocation resourcelocation = blockstateparser.getTag();
                boolean flag = blockstate != null;
                boolean flag1 = resourcelocation != null;

                if (flag || flag1) {

                    if (flag) {
                        list.addAll(Lists.newArrayList(blockstate.getBlock().getNameTextComponent().setStyle(style)));
                    }

                    Tag<Block> tag = BlockTags.getCollection().get(resourcelocation);
                    if (tag != null) {
                        Collection<Block> collection = tag.getAllElements();
                        if (!collection.isEmpty()) {
                            list.addAll(collection.stream().map(Block::getNameTextComponent).map(it -> it.setStyle(style)).collect(Collectors.toList()));
                        }
                    }

                }

            } catch (CommandSyntaxException ignored) {

            }

        }

    }

}

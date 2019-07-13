package com.fuzs.consolehud.helper;

import com.fuzs.consolehud.handler.ConfigHandler;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class TooltipHelper extends TooltipElementsHelper {

    private final Minecraft mc;

    public TooltipHelper(Minecraft mc) {
        this.mc = mc;
    }

    public List<String> createTooltip(ItemStack stack, boolean simple) {

        this.itemstack = stack;
        List<String> tooltip = Lists.newArrayList();

        this.getName(tooltip, new Style().setColor(TextFormatting.WHITE), false);

        if (simple) {
            return tooltip;
        }

        this.getInformation(tooltip, new Style().setColor(ConfigHandler.heldItemTooltipsConfig.appearanceConfig.textColor.getChatColor()), true, this.mc.player);

        if (stack.getItem() instanceof ItemShulkerBox && tooltip.size() == ConfigHandler.heldItemTooltipsConfig.rows) {
            return tooltip;
        }

        this.getEnchantments(tooltip, new Style().setColor(ConfigHandler.heldItemTooltipsConfig.appearanceConfig.textColor.getChatColor()));
        this.getColorTag(tooltip, new Style().setColor(ConfigHandler.heldItemTooltipsConfig.appearanceConfig.textColor.getChatColor()), true);
        this.getLoreTag(tooltip, new Style().setItalic(true).setColor(TextFormatting.DARK_PURPLE));
        //this.getUnbreakable(tooltip, new Style().setColor(TextFormatting.BLUE));
        //this.getAdventureStats(tooltip, new Style().setColor(ConfigHandler.heldItemTooltipsConfig.textColor.getChatColor()));
        this.getDurability(tooltip, new Style().setColor(ConfigHandler.heldItemTooltipsConfig.appearanceConfig.textColor.getChatColor()), false);
        //this.getNameID(tooltip, new Style().setColor(ConfigHandler.heldItemTooltipsConfig.textColor.getChatColor()));
        //this.getNBTAmount(tooltip, new Style().setColor(ConfigHandler.heldItemTooltipsConfig.textColor.getChatColor()));
        this.getForgeInformation(tooltip, false);

        this.applyLastLine(tooltip);

        return tooltip;

    }

    private void applyLastLine(List<String> tooltip) {

        boolean flag = ConfigHandler.heldItemTooltipsConfig.appearanceConfig.showDurability && ConfigHandler.heldItemTooltipsConfig.appearanceConfig.forceDurability && this.itemstack.isItemDamaged();
        int i = 0, j = 0; // i counts the lines to be added afterwards, j is for counting how many lines to remove

        if (flag) {
            i++;
        }

        if (tooltip.size() + i > ConfigHandler.heldItemTooltipsConfig.rows) {

            if (ConfigHandler.heldItemTooltipsConfig.appearanceConfig.showLastLine) {
                i++;
            }

            j = tooltip.size() - ConfigHandler.heldItemTooltipsConfig.rows + i;

            if (j == tooltip.size()) {
                i--; // prevent item name from being removed
                j = this.itemstack.isItemDamaged() ? 0 : j; // prioritise durability over last line
            }

            tooltip.subList(ConfigHandler.heldItemTooltipsConfig.rows - i, tooltip.size()).clear();

        }

        if (flag) {
            this.getDurability(tooltip, new Style().setColor(ConfigHandler.heldItemTooltipsConfig.appearanceConfig.textColor.getChatColor()), true);
        }

        if (j > 0 && ConfigHandler.heldItemTooltipsConfig.appearanceConfig.showLastLine ) {
            this.getLastLine(tooltip, new Style().setItalic(true).setColor(ConfigHandler.heldItemTooltipsConfig.appearanceConfig.textColor.getChatColor()), j);
        }

    }

    @SuppressWarnings("WeakerAccess")
    protected static void getAdventureBlockInfo(List<String> list, Style style, NBTTagList nbttaglist) {

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

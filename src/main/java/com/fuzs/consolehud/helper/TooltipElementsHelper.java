package com.fuzs.consolehud.helper;

import com.fuzs.consolehud.ConsoleHud;
import com.fuzs.consolehud.handler.ConfigHandler;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This is basically ItemStack#getTooltip split into separate functions to be modular (and completely customisable in the future)
 */
public class TooltipElementsHelper {

    protected ItemStack itemstack = ItemStack.EMPTY;

    protected void getName(List<String> list, Style style, ITooltipFlag.TooltipFlags tooltipflag) {

        String s = new TextComponentString(this.itemstack.getDisplayName()).setStyle((new Style()).setItalic(this.itemstack.hasDisplayName()).setColor(this.itemstack.getItem().getForgeRarity(this.itemstack).getColor())).getFormattedText();
        String s2 = "";

        if (tooltipflag.isAdvanced()) {
            String s1 = "";

            if (!s.isEmpty())
            {
                s2 = s2 + " (";
                s1 = ")";
            }

            int i = Item.getIdFromItem(this.itemstack.getItem());

            if (this.itemstack.getHasSubtypes())
            {
                s2 = s2 + String.format("#%04d/%d%s", i, this.itemstack.getItemDamage(), s1);
            }
            else
            {
                s2 = s2 + String.format("#%04d%s", i, s1);
            }
        } else if (!this.itemstack.hasDisplayName() && this.itemstack.getItem() == Items.FILLED_MAP) {
            s2 = s2 + " #" + this.itemstack.getItemDamage();
        }

        list.add(s + new TextComponentString(s2).setStyle(style).getFormattedText());

    }

    protected void getInformation(List<String> list, Style style, ITooltipFlag.TooltipFlags tooltipflag) {

        List<String> information = Lists.newArrayList();

        if (this.itemstack.getItem() instanceof ItemShulkerBox) {
            TooltipShulkerBoxHelper.getContentsTooltip(information, this.itemstack, style, ConfigHandler.heldItemTooltipsConfig.rows - 1);
        } else {
            this.itemstack.getItem().addInformation(this.itemstack, null, information, tooltipflag);
            information = information.stream().map(it -> new TextComponentString(it).setStyle(style).getFormattedText()).collect(Collectors.toList());
            information.removeIf(Strings::isNullOrEmpty); // remove empty lines from a list of strings
        }

        list.addAll(information);

    }

    protected void getEnchantments(List<String> list, Style style) {

        if (this.itemstack.hasTagCompound()) {

            NBTTagList nbttaglist = this.itemstack.getEnchantmentTagList();

            for (int j = 0; j < nbttaglist.tagCount(); ++j) {
                NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(j);
                int k = nbttagcompound.getShort("id");
                int l = nbttagcompound.getShort("lvl");
                Enchantment enchantment = Enchantment.getEnchantmentByID(k);

                if (enchantment != null) {
                    list.add(new TextComponentString(enchantment.getTranslatedName(l)).setStyle(style).getFormattedText());
                }
            }

        }

    }

    protected void getColorTag(List<String> list, Style style, ITooltipFlag.TooltipFlags tooltipflag) {

        if (this.itemstack.hasTagCompound())
        {
            if (this.itemstack.getTagCompound().hasKey("display", 10))
            {
                NBTTagCompound nbttagcompound = this.itemstack.getTagCompound().getCompoundTag("display");

                if (nbttagcompound.hasKey("color", 3))
                {
                    if (tooltipflag.isAdvanced())
                    {
                        list.add(new TextComponentTranslation("item.color", String.format("#%06X", nbttagcompound.getInteger("color"))).setStyle(style).getFormattedText());
                    }
                    else
                    {
                        list.add(new TextComponentTranslation("item.dyed").setStyle(style.setItalic(true)).getFormattedText());
                    }
                }
            }
        }

    }

    protected void getLoreTag(List<String> list, Style style) {

        if (this.itemstack.hasTagCompound())
        {
            if (this.itemstack.getTagCompound().hasKey("display", 10))
            {
                NBTTagCompound nbttagcompound = this.itemstack.getTagCompound().getCompoundTag("display");

                if (nbttagcompound.getTagId("Lore") == 9)
                {
                    NBTTagList nbttaglist = nbttagcompound.getTagList("Lore", 8);

                    if (!nbttaglist.hasNoTags())
                    {
                        for (int l1 = 0; l1 < nbttaglist.tagCount(); ++l1)
                        {
                            list.add(new TextComponentString(nbttaglist.getStringTagAt(l1)).setStyle(style).getFormattedText());
                        }
                    }
                }
            }
        }

    }

    protected void getUnbreakable(List<String> list, Style style) {

        if (this.itemstack.hasTagCompound() && this.itemstack.getTagCompound().getBoolean("Unbreakable")) {
            list.add(new TextComponentTranslation("item.unbreakable").setStyle(style).getFormattedText());
        }

    }

    protected void getAdventureStats(List<String> list, Style style) {

        if (this.itemstack.hasTagCompound()) {

            if (this.itemstack.getTagCompound().hasKey("CanDestroy", 9)) {

                NBTTagList nbttaglist1 = this.itemstack.getTagCompound().getTagList("CanDestroy", 8);

                if (!nbttaglist1.hasNoTags()) {

                    list.add(new TextComponentTranslation("item.canBreak").setStyle(style).getFormattedText());

                    TooltipHelper.getAdventureBlockInfo(list, style, nbttaglist1);
                }
            }

            if (this.itemstack.getTagCompound().hasKey("CanPlaceOn", 9)) {

                NBTTagList nbttaglist2 = this.itemstack.getTagCompound().getTagList("CanPlaceOn", 8);

                if (!nbttaglist2.hasNoTags()) {

                    list.add(new TextComponentTranslation("item.canPlace").setStyle(style).getFormattedText());

                    TooltipHelper.getAdventureBlockInfo(list, style, nbttaglist2);
                }
            }

        }

    }

    protected void getDurability(List<String> list, Style style, boolean force) {

        if ((!ConfigHandler.heldItemTooltipsConfig.appearanceConfig.showDurability || ConfigHandler.heldItemTooltipsConfig.appearanceConfig.forceDurability) && !force || !this.itemstack.isItemDamaged()) {
            return;
        }

        if (!ConfigHandler.heldItemTooltipsConfig.appearanceConfig.durabilityFormat.isEmpty()) {

            try {
                list.add(new TextComponentString(String.format(ConfigHandler.heldItemTooltipsConfig.appearanceConfig.durabilityFormat, this.itemstack.getMaxDamage() -
                        this.itemstack.getItemDamage(), this.itemstack.getMaxDamage())).setStyle(style).getFormattedText());
            } catch (IllegalFormatException e) {
                ConsoleHud.LOGGER.error("Caught exception while parsing string format. Go to config file > helditemtooltips > appearance > Durability Format to fix this.");
            }

        } else  {

            list.add(new TextComponentTranslation("item.durability", this.itemstack.getMaxDamage() -
                    this.itemstack.getItemDamage(), this.itemstack.getMaxDamage()).setStyle(style).getFormattedText());

        }

    }

    protected void getNameID(List<String> list, Style style) {

        ResourceLocation resource = Item.REGISTRY.getNameForObject(this.itemstack.getItem());
        if (resource != null) {
            list.add(new TextComponentString(resource.toString()).setStyle(style).getFormattedText());
        }

    }

    protected void getNBTAmount(List<String> list, Style style) {

        if (this.itemstack.hasTagCompound()) {
            list.add(new TextComponentTranslation("item.nbt_tags", this.itemstack.getTagCompound().getKeySet().size()).setStyle(style).getFormattedText());
        }

    }

    protected void getForgeInformation(List<String> list, ITooltipFlag.TooltipFlags tooltipflag) {

        if (ConfigHandler.heldItemTooltipsConfig.appearanceConfig.moddedTooltips) {
            net.minecraftforge.event.ForgeEventFactory.onItemTooltip(this.itemstack, null, list, tooltipflag);
        }

    }

    protected void getLastLine(List<String> list, Style style, int i) {

        if (!ConfigHandler.heldItemTooltipsConfig.appearanceConfig.lastLineFormat.isEmpty()) {

            try {
                list.add(new TextComponentString(String.format(ConfigHandler.heldItemTooltipsConfig.appearanceConfig.lastLineFormat, i)).setStyle(style).getFormattedText());
            } catch (IllegalFormatException e) {
                ConsoleHud.LOGGER.error("Caught exception while parsing string format. Go to config file > helditemtooltips > appearance > Last Line Format to fix this.");
            }

        } else  {

            list.add(new TextComponentTranslation("container.shulkerBox.more", i).setStyle(style).getFormattedText());

        }

    }

}

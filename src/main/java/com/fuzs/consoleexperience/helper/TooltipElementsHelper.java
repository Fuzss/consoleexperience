package com.fuzs.consoleexperience.helper;

import com.fuzs.consoleexperience.handler.ConfigBuildHandler;
import com.google.common.base.Strings;
import com.google.gson.JsonParseException;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * This is basically ItemStack#getTooltip split into separate functions to be modular (and completely customisable in the future)
 */
@SuppressWarnings({"WeakerAccess", "ConstantConditions", "SameParameterValue", "unused"})
public class TooltipElementsHelper {

    private final ItemTooltipHelper itemHelper = new ItemTooltipHelper();
    protected ItemStack itemstack = ItemStack.EMPTY;

    protected void getName(List<ITextComponent> list, Style style, ITooltipFlag.TooltipFlags tooltipflag) {

        list.add(new StringTextComponent("").append(this.itemstack.getDisplayName()).setStyle(Style.EMPTY.setItalic(this.itemstack.hasDisplayName()).setFormatting(this.itemstack.getRarity().color)));

//        ITextComponent component = new StringTextComponent("").appendSibling(this.itemstack.getDisplayName()).setStyle(new Style().setItalic(this.itemstack.hasDisplayName()).setColor(this.itemstack.getRarity().color));
//
//        if (!this.itemstack.hasDisplayName() && this.itemstack.getItem() == Items.FILLED_MAP) {
//            component.appendSibling(new StringTextComponent(" #" + FilledMapItem.getMapId(this.itemstack)).setStyle(style));
//        }
//
//        list.add(component);

    }

    protected void getInformation(List<ITextComponent> list, Style style, ITooltipFlag.TooltipFlags tooltipflag, World world) {

        // create list with single element that'll be removed later anyways as some mods apparently expect the list to not be empty
        List<ITextComponent> information = new ArrayList<>(Collections.singletonList(new StringTextComponent("")));

        if (Block.getBlockFromItem(this.itemstack.getItem()) instanceof ShulkerBoxBlock) {

            this.itemHelper.getContentsTooltip(information, this.itemstack, style, ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.rows.get() - 1);

        } else {

                this.itemstack.getItem().addInformation(this.itemstack, world, information, tooltipflag);
        }

        // remove empty lines from a list of strings
        information = information.stream().filter(it -> !Strings.isNullOrEmpty(it.getString())).collect(Collectors.toList());
        list.addAll(information);

    }

    protected void getEnchantments(List<ITextComponent> list, Style style) {

        if (this.itemstack.hasTag()) {

            ListNBT nbttaglist = this.itemstack.getEnchantmentTagList();

            for(int j = 0; j < nbttaglist.size(); ++j) {

                CompoundNBT nbttagcompound = nbttaglist.getCompound(j);
                Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.tryCreate(nbttagcompound.getString("id")));

                if (enchantment != null) {
                    list.add(enchantment.getDisplayName(nbttagcompound.getInt("lvl")));
                }

            }

        }

    }

    protected void getColorTag(List<ITextComponent> list, Style style, ITooltipFlag.TooltipFlags tooltipflag) {

        if (this.itemstack.hasTag()) {
            if (this.itemstack.getTag().contains("display", 10)) {
                CompoundNBT nbttagcompound = this.itemstack.getTag().getCompound("display");

                if (nbttagcompound.contains("color", 3)) {
                    if (tooltipflag.isAdvanced()) {
                        list.add(new TranslationTextComponent("item.color", String.format(Locale.ROOT, "#%06X", nbttagcompound.getInt("color"))).setStyle(style));
                    }
                    else {
                        list.add(new TranslationTextComponent("item.dyed").setStyle(style.setItalic(true)));
                    }
                }
            }
        }

    }

    protected void getLoreTag(List<ITextComponent> list, Style style) {

        if (this.itemstack.hasTag()) {
            if (this.itemstack.getTag().contains("display", 10)) {
                CompoundNBT nbttagcompound = this.itemstack.getTag().getCompound("display");

                if (nbttagcompound.getTagId("Lore") == 9) {
                    ListNBT nbttaglist = nbttagcompound.getList("Lore", 8);

                    for(int j = 0; j < nbttaglist.size(); ++j) {

                        String s = nbttaglist.getString(j);

                        try {
                            IFormattableTextComponent iformattabletextcomponent1 = ITextComponent.Serializer.func_240643_a_(s);
                            if (iformattabletextcomponent1 != null) {
                                list.add(TextComponentUtils.func_240648_a_(iformattabletextcomponent1, style));
                            }
                        } catch (JsonParseException var19) {
                            nbttagcompound.remove("Lore");
                        }
                    }
                }
            }
        }

    }

    protected void getUnbreakable(List<ITextComponent> list, Style style) {

        if (this.itemstack.hasTag() && this.itemstack.getTag().getBoolean("Unbreakable")) {
            list.add(new TranslationTextComponent("item.unbreakable").setStyle(style));
        }

    }

    protected void getAdventureStats(List<ITextComponent> list, Style style) {

        if (this.itemstack.hasTag()) {

            if (this.itemstack.getTag().contains("CanDestroy", 9)) {

                ListNBT nbttaglist1 = this.itemstack.getTag().getList("CanDestroy", 8);

                if (!nbttaglist1.isEmpty()) {

                    list.add(new TranslationTextComponent("item.canBreak").setStyle(style));

                    TooltipHelper.getAdventureBlockInfo(list, style, nbttaglist1);
                }
            }

            if (this.itemstack.getTag().contains("CanPlaceOn", 9)) {

                ListNBT nbttaglist2 = this.itemstack.getTag().getList("CanPlaceOn", 8);

                if (!nbttaglist2.isEmpty()) {

                    list.add(new TranslationTextComponent("item.canPlace").setStyle(style));

                    TooltipHelper.getAdventureBlockInfo(list, style, nbttaglist2);
                }
            }

        }

    }

    protected void getDurability(List<ITextComponent> list, Style style, boolean force) {

        if ((!ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.showDurability.get() || ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.forceDurability.get()) && !force || !this.itemstack.isDamaged()) {
            return;
        }

        list.add(new TranslationTextComponent("item.durability", this.itemstack.getMaxDamage() -
                this.itemstack.getDamage(), this.itemstack.getMaxDamage()).setStyle(style));

    }

    protected void getNameID(List<ITextComponent> list, Style style) {

        ResourceLocation resourceLocation = ForgeRegistries.ITEMS.getKey(this.itemstack.getItem());
        if (resourceLocation != null) {
            list.add(new StringTextComponent(resourceLocation.toString()).setStyle(style));
        }

    }

    protected void getNBTAmount(List<ITextComponent> list, Style style) {

        if (this.itemstack.hasTag()) {
            list.add(new TranslationTextComponent("item.nbt_tags", this.itemstack.getTag().keySet().size()).setStyle(style));
        }

    }

    protected void getForgeInformation(List<ITextComponent> list, ITooltipFlag.TooltipFlags tooltipflag) {

        if (ConfigBuildHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.moddedTooltips.get()) {
            net.minecraftforge.event.ForgeEventFactory.onItemTooltip(this.itemstack, null, list, tooltipflag);
        }

    }

    protected void getLastLine(List<ITextComponent> list, Style style, int i) {

        list.add(new TranslationTextComponent("container.shulkerBox.more", i).setStyle(style));

    }

}

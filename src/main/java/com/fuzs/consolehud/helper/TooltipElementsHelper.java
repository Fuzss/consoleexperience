package com.fuzs.consolehud.helper;

import com.fuzs.consolehud.ConsoleHud;
import com.fuzs.consolehud.handler.ConfigHandler;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
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
import net.minecraftforge.registries.ForgeRegistries;

import java.util.IllegalFormatException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is basically ItemStack#getTooltip split into separate functions to be modular (and completely customisable in the future)
 */
@SuppressWarnings({"ConstantConditions", "WeakerAccess"})
public class TooltipElementsHelper {

    protected ItemStack itemstack = ItemStack.EMPTY;

    protected void getName(List<ITextComponent> list, Style style, ITooltipFlag.TooltipFlags tooltipflag) {

        list.add(new StringTextComponent("").appendSibling(this.itemstack.getDisplayName()).setStyle(new Style().setItalic(this.itemstack.hasDisplayName()).setColor(this.itemstack.getRarity().color)));

    }

    protected void getInformation(List<ITextComponent> list, Style style, ITooltipFlag.TooltipFlags tooltipflag) {

        List<ITextComponent> information = Lists.newArrayList();

        if (Block.getBlockFromItem(this.itemstack.getItem()) instanceof ShulkerBoxBlock) {
            TooltipShulkerBoxHelper.getContentsTooltip(information, this.itemstack, style, ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.rows.get() - 1);
        } else {
            this.itemstack.getItem().addInformation(this.itemstack, null, information, tooltipflag);
            // remove empty lines from a list of strings
            information = information.stream().filter(it -> !Strings.isNullOrEmpty(it.getString())).collect(Collectors.toList());

        }

        list.addAll(information);

    }

    protected void getEnchantments(List<ITextComponent> list, Style style) {

        if (this.itemstack.hasTag()) {

            ListNBT nbttaglist = this.itemstack.getEnchantmentTagList();

            for (int i = 0; i < nbttaglist.size(); i++) {

//                CompoundNBT nbttagcompound = nbttaglist.getCompound(i);
//                Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.tryCreate(nbttagcompound.getString("id")));

                CompoundNBT nbttagcompound = nbttaglist.getCompound(i);
                int k = nbttagcompound.getInt("id");
                int l = nbttagcompound.getInt("lvl");
                Enchantment enchantment = Enchantment.getEnchantmentByID(k);

                if (enchantment != null) {
                    //list.add(enchantment.getDisplayName(nbttagcompound.getInt("lvl")));
                    list.add(enchantment.getDisplayName(l).setStyle(style));
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
                        list.add(new TranslationTextComponent("item.color", String.format("#%06X", nbttagcompound.getInt("color"))).setStyle(style));
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
                            ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(s);
                            if (itextcomponent != null) {
                                list.add(TextComponentUtils.mergeStyles(itextcomponent, style));
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

        if ((!ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.showDurability.get() || ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.forceDurability.get()) && !force || !this.itemstack.isDamaged()) {
            return;
        }

        if (!ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.durabilityFormat.get().isEmpty()) {

            try {
                list.add(new StringTextComponent(String.format(ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.durabilityFormat.get(), this.itemstack.getMaxDamage() -
                        this.itemstack.getDamage(), this.itemstack.getMaxDamage())).setStyle(style));
            } catch (IllegalFormatException e) {
                ConsoleHud.LOGGER.error("Caught exception while parsing string format. Go to config file > helditemtooltips > appearance > Durability Format to fix this.");
            }

        } else  {

            list.add(new TranslationTextComponent("item.durability", this.itemstack.getMaxDamage() -
                    this.itemstack.getDamage(), this.itemstack.getMaxDamage()).setStyle(style));

        }

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

        if (ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.moddedTooltips.get()) {
            net.minecraftforge.event.ForgeEventFactory.onItemTooltip(this.itemstack, null, list, tooltipflag);
        }

    }

    protected void getLastLine(List<ITextComponent> list, Style style, int i) {

        if (!ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.lastLineFormat.get().isEmpty()) {

            try {
                list.add(new StringTextComponent(String.format(ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.lastLineFormat.get(), i)).setStyle(style));
            } catch (IllegalFormatException e) {
                ConsoleHud.LOGGER.error("Caught exception while parsing string format. Go to config file > helditemtooltips > appearance > Last Line Format to fix this.");
            }

        } else  {

            list.add(new TranslationTextComponent("container.shulkerBox.more", i).setStyle(style));

        }

    }

}

package com.fuzs.consolehud.helper;

import com.fuzs.consolehud.handler.ConfigHandler;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * This is basically ItemStack#getTooltip split into separate functions to be modular (and completely customisable in the future)
 */
@SuppressWarnings({"WeakerAccess", "ConstantConditions", "SameParameterValue", "unused"})
public class TooltipElementsHelper {

    protected ItemStack itemstack = ItemStack.EMPTY;

    protected void getName(List<ITextComponent> list, Style style, ITooltipFlag.TooltipFlags tooltipflag) {

        list.add(new TextComponentString("").appendSibling(this.itemstack.getDisplayName()).setStyle(new Style().setItalic(this.itemstack.hasDisplayName()).setColor(this.itemstack.getRarity().color)));

//        ITextComponent component = new TextComponentString("").appendSibling(this.itemstack.getDisplayName()).setStyle(new Style().setItalic(this.itemstack.hasDisplayName()).setColor(this.itemstack.getRarity().color));
//
//        if (!this.itemstack.hasDisplayName() && this.itemstack.getItem() == Items.FILLED_MAP) {
//            component.appendSibling(new TextComponentString(" #" + FilledMapItem.getMapId(this.itemstack)).setStyle(style));
//        }
//
//        list.add(component);

    }

    protected void getInformation(List<ITextComponent> list, Style style, ITooltipFlag.TooltipFlags tooltipflag, World world) {

        List<ITextComponent> information = Lists.newArrayList();

        if (Block.getBlockFromItem(this.itemstack.getItem()) instanceof BlockShulkerBox) {

            TooltipShulkerBoxHelper.getContentsTooltip(information, this.itemstack, style, ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.rows.get() - 1);

        } else {

            this.itemstack.getItem().addInformation(this.itemstack, world, information, tooltipflag);
            // remove empty lines from a list of strings
            information = information.stream().filter(it -> !Strings.isNullOrEmpty(it.getString())).collect(Collectors.toList());

        }

        list.addAll(information);

    }

    protected void getEnchantments(List<ITextComponent> list, Style style) {

        if (this.itemstack.hasTag()) {

            NBTTagList nbttaglist = this.itemstack.getEnchantmentTagList();

            for(int j = 0; j < nbttaglist.size(); ++j) {

                NBTTagCompound nbttagcompound = nbttaglist.getCompound(j);
                Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.makeResourceLocation(nbttagcompound.getString("id")));

                if (enchantment != null) {
                    list.add(enchantment.func_200305_d(nbttagcompound.getInt("lvl")));
                }

            }

        }

    }

    protected void getColorTag(List<ITextComponent> list, Style style, ITooltipFlag.TooltipFlags tooltipflag) {

        if (this.itemstack.hasTag()) {
            if (this.itemstack.getTag().contains("display", 10)) {
                NBTTagCompound nbttagcompound = this.itemstack.getTag().getCompound("display");

                if (nbttagcompound.contains("color", 3)) {
                    if (tooltipflag.isAdvanced()) {
                        list.add(new TextComponentTranslation("item.color", String.format(Locale.ROOT, "#%06X", nbttagcompound.getInt("color"))).setStyle(style));
                    }
                    else {
                        list.add(new TextComponentTranslation("item.dyed").setStyle(style.setItalic(true)));
                    }
                }
            }
        }

    }

    protected void getLoreTag(List<ITextComponent> list, Style style) {

        if (this.itemstack.hasTag()) {
            if (this.itemstack.getTag().contains("display", 10)) {
                NBTTagCompound nbttagcompound = this.itemstack.getTag().getCompound("display");

                if (nbttagcompound.getTagId("Lore") == 9) {
                    NBTTagList nbttaglist = nbttagcompound.getList("Lore", 8);

                    for(int j = 0; j < nbttaglist.size(); ++j) {

                        String s = nbttaglist.getString(j);

                        try {
                            ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(s);
                            if (itextcomponent != null) {
                                list.add(TextComponentUtils.mergeStyles(itextcomponent, style));
                            }
                        } catch (JsonParseException var19) {
                            nbttagcompound.removeTag("Lore");
                        }
                    }
                }
            }
        }

    }

    protected void getUnbreakable(List<ITextComponent> list, Style style) {

        if (this.itemstack.hasTag() && this.itemstack.getTag().getBoolean("Unbreakable")) {
            list.add(new TextComponentTranslation("item.unbreakable").setStyle(style));
        }

    }

    protected void getAdventureStats(List<ITextComponent> list, Style style) {

        if (this.itemstack.hasTag()) {

            if (this.itemstack.getTag().contains("CanDestroy", 9)) {

                NBTTagList nbttaglist1 = this.itemstack.getTag().getList("CanDestroy", 8);

                if (!nbttaglist1.isEmpty()) {

                    list.add(new TextComponentTranslation("item.canBreak").setStyle(style));

                    TooltipHelper.getAdventureBlockInfo(list, style, nbttaglist1);
                }
            }

            if (this.itemstack.getTag().contains("CanPlaceOn", 9)) {

                NBTTagList nbttaglist2 = this.itemstack.getTag().getList("CanPlaceOn", 8);

                if (!nbttaglist2.isEmpty()) {

                    list.add(new TextComponentTranslation("item.canPlace").setStyle(style));

                    TooltipHelper.getAdventureBlockInfo(list, style, nbttaglist2);
                }
            }

        }

    }

    protected void getDurability(List<ITextComponent> list, Style style, boolean force) {

        if ((!ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.showDurability.get() || ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.forceDurability.get()) && !force || !this.itemstack.isDamaged()) {
            return;
        }

        list.add(new TextComponentTranslation("item.durability", this.itemstack.getMaxDamage() -
                this.itemstack.getDamage(), this.itemstack.getMaxDamage()).setStyle(style));

    }

    protected void getNameID(List<ITextComponent> list, Style style) {

        ResourceLocation resourceLocation = ForgeRegistries.ITEMS.getKey(this.itemstack.getItem());
        if (resourceLocation != null) {
            list.add(new TextComponentString(resourceLocation.toString()).setStyle(style));
        }

    }

    protected void getNBTAmount(List<ITextComponent> list, Style style) {

        if (this.itemstack.hasTag()) {
            list.add(new TextComponentTranslation("item.nbt_tags", this.itemstack.getTag().keySet().size()).setStyle(style));
        }

    }

    protected void getForgeInformation(List<ITextComponent> list, ITooltipFlag.TooltipFlags tooltipflag) {

        if (ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.appearanceConfig.moddedTooltips.get()) {
            net.minecraftforge.event.ForgeEventFactory.onItemTooltip(this.itemstack, null, list, tooltipflag);
        }

    }

    protected void getLastLine(List<ITextComponent> list, Style style, int i) {

        list.add(new TextComponentTranslation("container.shulkerBox.more", i).setStyle(style));

    }

}

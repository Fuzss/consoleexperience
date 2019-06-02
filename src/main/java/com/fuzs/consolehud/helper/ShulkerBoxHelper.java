package com.fuzs.consolehud.helper;

import com.google.common.collect.Lists;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class ShulkerBoxHelper {

    public static void getLootTableTooltip(List<String> list, ItemStack stack) {

        NBTTagCompound nbttagcompound = stack.getTagCompound();

        if (nbttagcompound != null && nbttagcompound.hasKey("BlockEntityTag", 10))
        {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("BlockEntityTag");

            if (nbttagcompound1.hasKey("LootTable", 8))
            {
                list.add("???????");
            }
        }

    }

    public static void getContentsTooltip(List<String> list, ItemStack stack, int rows) {

        List<ItemStack> contents = contentsToList(stack);

        if (contents == null) {
            return;
        }

        if (contents.size() > rows) {

            for (ItemStack itemstack : contents.subList(0, rows - 1)) {

                list.add(String.format("%s x%d", itemstack.getItem().getItemStackDisplayName(itemstack), itemstack.getCount()));

            }

            list.add(new TextComponentTranslation("container.shulkerBox.more", contents.size() - rows + 1).setStyle(new Style().setItalic(true).setColor(TextFormatting.GRAY)).getFormattedText());

        } else {

            for (ItemStack itemstack : contents) {

                list.add(String.format("%s x%d", itemstack.getItem().getItemStackDisplayName(itemstack), itemstack.getCount()));

            }

        }

    }

    private static List<ItemStack> contentsToList(ItemStack stack) {

        NBTTagCompound nbttagcompound = stack.getTagCompound();

        if (nbttagcompound != null && nbttagcompound.hasKey("BlockEntityTag", 10))
        {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("BlockEntityTag");

            if (nbttagcompound1.hasKey("Items", 9))
            {
                NonNullList<ItemStack> nonnulllist = NonNullList.withSize(27, ItemStack.EMPTY);
                ItemStackHelper.loadAllItems(nbttagcompound1, nonnulllist);

                return mergeInventory(nonnulllist);
            }
        }

        return null;

    }

    private static List<ItemStack> mergeInventory(List<ItemStack> list) {

        List<ItemStack> contents = Lists.newArrayList();

        for (ItemStack itemstack : list) {

            if (contents.stream().anyMatch(it -> it.getItem() == itemstack.getItem())) {

                contents.forEach((it) -> {
                    if (it.getItem() == itemstack.getItem()) {
                        it.setCount(it.getCount() + itemstack.getCount());
                    }
                });

            } else if (!itemstack.isEmpty()) {
                contents.add(itemstack);
            }

        }

        return contents;

    }

}

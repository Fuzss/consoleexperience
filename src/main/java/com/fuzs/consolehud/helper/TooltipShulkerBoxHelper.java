package com.fuzs.consolehud.helper;

import com.google.common.collect.Lists;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class TooltipShulkerBoxHelper {

    public static void getLootTableTooltip(List<ITextComponent> tooltip, ItemStack stack) {

        CompoundNBT nbttagcompound = stack.getChildTag("BlockEntityTag");

        if (nbttagcompound != null) {

            if (nbttagcompound.contains("LootTable", 8)) {
                tooltip.add(new StringTextComponent("???????"));
            }

        }

    }

    public static void getContentsTooltip(List<ITextComponent> tooltip, ItemStack stack, Style style, int rows) {

        List<ItemStack> contents = contentsToList(stack);

        if (contents == null) {
            return;
        }

        if (contents.size() > rows) {

            for (ItemStack itemstack : contents.subList(0, rows - 1)) {

                tooltip.add(itemstack.getDisplayName().deepCopy().appendText(" x").appendText(String.valueOf(itemstack.getCount())).setStyle(style));

            }

            tooltip.add(new TranslationTextComponent("container.shulkerBox.more", contents.size() - rows + 1).setStyle(style.setItalic(true)));

        } else {

            for (ItemStack itemstack : contents) {

                tooltip.add(itemstack.getDisplayName().deepCopy().appendText(" x").appendText(String.valueOf(itemstack.getCount())).setStyle(style));

            }

        }

    }

    private static List<ItemStack> contentsToList(ItemStack stack) {

        CompoundNBT nbttagcompound = stack.getChildTag("BlockEntityTag");

        if (nbttagcompound != null) {

            if (nbttagcompound.contains("Items", 9)) {

                NonNullList<ItemStack> nonnulllist = NonNullList.withSize(27, ItemStack.EMPTY);
                ItemStackHelper.loadAllItems(nbttagcompound, nonnulllist);

                return mergeInventory(nonnulllist);

            }

        }

        return null;

    }

    private static List<ItemStack> mergeInventory(List<ItemStack> list) {

        List<ItemStack> contents = Lists.newArrayList();

        for (ItemStack itemstack : list) {

            if (contents.stream().anyMatch(it -> ItemStack.areItemsEqual(it, itemstack))) {

                contents.forEach((it) -> {
                    if (ItemStack.areItemsEqual(it, itemstack)) {
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

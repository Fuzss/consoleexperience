package com.fuzs.consoleexperience.helper;

import com.google.common.collect.Lists;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.*;

import java.util.List;

public class ItemTooltipHelper {

    public void getLootTableTooltip(List<ITextComponent> tooltip, ItemStack stack) {

        CompoundNBT nbttagcompound = stack.getChildTag("BlockEntityTag");

        if (nbttagcompound != null) {

            if (nbttagcompound.contains("LootTable", 8)) {
                tooltip.add(new StringTextComponent("???????"));
            }

        }

    }

    public void getContentsTooltip(List<ITextComponent> tooltip, ItemStack stack, Style style, int rows) {

        List<ItemStack> contents = contentsToList(stack);

        if (contents == null || rows == 0) {
            return;
        }

        if (contents.size() > rows) {

            for (ItemStack itemstack : contents.subList(0, rows - 1)) {

                IFormattableTextComponent iformattabletextcomponent = itemstack.getDisplayName().deepCopy();
                tooltip.add(iformattabletextcomponent.appendString(" x").appendString(String.valueOf(itemstack.getCount())).setStyle(style));

            }

            tooltip.add(new TranslationTextComponent("container.shulkerBox.more", contents.size() - rows + 1).setStyle(style.setItalic(true)));

        } else {

            for (ItemStack itemstack : contents) {

                IFormattableTextComponent iformattabletextcomponent = itemstack.getDisplayName().deepCopy();
                tooltip.add(iformattabletextcomponent.appendString(" x").appendString(String.valueOf(itemstack.getCount())).setStyle(style));

            }

        }

    }

    private List<ItemStack> contentsToList(ItemStack stack) {

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

    private List<ItemStack> mergeInventory(List<ItemStack> list) {

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

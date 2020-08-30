package com.fuzs.consoleexperience.helper;

import com.google.common.collect.Lists;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.*;

import java.util.List;

public interface IShulkerTooltip {

    default void addInformation(List<ITextComponent> tooltip, ItemStack stack, TextFormatting color, int rows) {

        CompoundNBT compoundnbt = stack.getChildTag("BlockEntityTag");
        if (compoundnbt == null || rows == 0) {

            return;
        }

        this.getLootTableTooltip(tooltip, compoundnbt, color);
        List<ItemStack> contents = this.contentsToList(compoundnbt);
        if (contents.size() > rows) {

            for (ItemStack itemstack : contents.subList(0, rows - 1)) {

                IFormattableTextComponent iformattabletextcomponent = itemstack.getDisplayName().deepCopy();
                tooltip.add(iformattabletextcomponent.appendString(" x").appendString(String.valueOf(itemstack.getCount())).mergeStyle(color));
            }

            tooltip.add(new TranslationTextComponent("container.shulkerBox.more", contents.size() - rows + 1).mergeStyle(color).mergeStyle(TextFormatting.ITALIC));
        } else {

            for (ItemStack itemstack : contents) {

                IFormattableTextComponent iformattabletextcomponent = itemstack.getDisplayName().deepCopy();
                iformattabletextcomponent.appendString(" x").appendString(String.valueOf(itemstack.getCount()));
                tooltip.add(iformattabletextcomponent.mergeStyle(color));
            }
        }
    }

    default void getLootTableTooltip(List<ITextComponent> tooltip, CompoundNBT compoundnbt, TextFormatting color) {

        if (compoundnbt.contains("LootTable", 8)) {

            tooltip.add(new StringTextComponent("???????").mergeStyle(color));
        }
    }

    default List<ItemStack> contentsToList(CompoundNBT compoundnbt) {

        if (compoundnbt.contains("Items", 9)) {

            NonNullList<ItemStack> nonnulllist = NonNullList.withSize(27, ItemStack.EMPTY);
            ItemStackHelper.loadAllItems(compoundnbt, nonnulllist);
            return this.mergeInventory(nonnulllist);
        }

        return Lists.newArrayList();
    }

    default List<ItemStack> mergeInventory(List<ItemStack> list) {

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

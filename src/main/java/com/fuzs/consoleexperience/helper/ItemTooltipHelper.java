package com.fuzs.consoleexperience.helper;

import com.fuzs.consoleexperience.handler.ConfigBuildHandler;
import com.google.common.collect.Lists;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ItemTooltipHelper {

    private final String[] TIME_FORMATS = new String[]{"H:mm", "H:mm:ss", "h:mm a", "h:mm:ss a"};

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

                tooltip.add(itemstack.getDisplayName().deepCopy().appendText(" x").appendText(String.valueOf(itemstack.getCount())).setStyle(style));

            }

            tooltip.add(new TranslationTextComponent("container.shulkerBox.more", contents.size() - rows + 1).setStyle(style.setItalic(true)));

        } else {

            for (ItemStack itemstack : contents) {

                tooltip.add(itemstack.getDisplayName().deepCopy().appendText(" x").appendText(String.valueOf(itemstack.getCount())).setStyle(style));

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

    public void getTimeTooltip(long worldTime, List<ITextComponent> tooltip) {

        if (!ConfigBuildHandler.MISCELLANEOUS_CONFIG.clockTime.get()) {
            return;
        }

        long l = (worldTime + 6000L) % 24000L;
        int hours = (int) l / 1000;
        float minutes = ((int) l % 1000) * 3 / 50.0F;
        int seconds = (int) ((minutes - (int) minutes) * 60.0F);

        int i = ConfigBuildHandler.MISCELLANEOUS_CONFIG.clockTwelve.get() ? 2 : 0;
        i += ConfigBuildHandler.MISCELLANEOUS_CONFIG.clockSeconds.get() ? 1 : 0;
        String s = TIME_FORMATS[i];

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(s);
        String time = LocalTime.of(hours, (int) minutes, seconds, 0).format(formatter);
        tooltip.add(1, new StringTextComponent(time).setStyle(new Style().setColor(TextFormatting.GRAY)));

    }

}

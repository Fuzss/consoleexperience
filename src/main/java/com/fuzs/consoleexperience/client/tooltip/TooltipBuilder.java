package com.fuzs.consoleexperience.client.tooltip;

import com.fuzs.consoleexperience.client.element.GameplayElements;
import com.fuzs.consoleexperience.client.element.SelectedItemElement;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TooltipBuilder {

    @SuppressWarnings("UnstableApiUsage")
    public static final Map<String, TooltipElementBase> TOOLTIP_ELEMENTS = Lists.newArrayList(
            new TooltipElements.Name(true, 2, 20, null),
            new TooltipElements.Information(true, 4, 19, null, ITooltipFlag.TooltipFlags.ADVANCED),
            new TooltipElements.Enchantments(true, 6, 17, null),
            new TooltipElements.Color(true, 8, 15, TextFormatting.GRAY, ITooltipFlag.TooltipFlags.ADVANCED),
            new TooltipElements.Lore(true, 10, 12, Style.EMPTY.setFormatting(TextFormatting.DARK_PURPLE).setItalic(true)),
            new TooltipElements.Modifiers(false, 12, 5, null),
            new TooltipElements.Unbreakable(true, 14, 7, TextFormatting.BLUE),
            new TooltipElements.Durability(true, 16, 18, null),
            new TooltipElements.NameID(false, 18, 5, TextFormatting.DARK_GRAY),
            new TooltipElements.NBTAmount(false, 20, 4, TextFormatting.DARK_GRAY))
            .stream().collect(ImmutableMap.toImmutableMap(TooltipElementBase::getName, Function.identity())
    );

    public List<ITextComponent> create(ItemStack itemstack) {

        return TOOLTIP_ELEMENTS.get("name").makeAndGet(itemstack, null);
    }

    public List<ITextComponent> create(ItemStack itemstack, @Nullable PlayerEntity playerIn, int rows) {

        boolean lastLine = ((SelectedItemElement) GameplayElements.SELECTED_ITEM).lastLine && rows > 1;
        List<TooltipElementBase> activeElements = getActiveElements();
        activeElements.forEach(element -> element.make(itemstack, playerIn));
        if (activeElements.stream().mapToInt(TooltipElementBase::size).sum() > rows && lastLine) {

            rows--;
        }

        activeElements.sort(Comparator.comparingInt(TooltipElementBase::getPriority));
        for (TooltipElementBase element : activeElements) {

            rows -= element.cut(Math.max(rows, 0));
        }

        activeElements.sort(Comparator.comparingInt(TooltipElementBase::getOrdering));
        List<ITextComponent> tooltip = activeElements.stream()
                .map(TooltipElementBase::get)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        rows -= this.applyForgeInformation(tooltip, itemstack, playerIn, Math.max(rows, 0));
        if (lastLine && rows < 0) {

            tooltip.add(this.getLastLine(-rows));
        }

        return tooltip;
    }

    public void reset() {

        getActiveElements().forEach(TooltipElementBase::reset);
    }

    private int applyForgeInformation(List<ITextComponent> tooltip, ItemStack itemstack, @Nullable PlayerEntity playerIn, int rows) {

        if (((SelectedItemElement) GameplayElements.SELECTED_ITEM).moddedInfo) {

            int size = tooltip.size();
            ForgeEventFactory.onItemTooltip(itemstack, playerIn, tooltip, ITooltipFlag.TooltipFlags.NORMAL);
            int newSize = tooltip.size();
            if (newSize - size > rows) {

                tooltip.subList(size + rows, newSize).clear();
            }

            return newSize - size;
        }

        return 0;
    }

    private ITextComponent getLastLine(int amount) {

        return new TranslationTextComponent("container.shulkerBox.more", amount).mergeStyle(((SelectedItemElement) GameplayElements.SELECTED_ITEM).textColor, TextFormatting.ITALIC);
    }

    public static List<TooltipElementBase> getActiveElements() {

        return TOOLTIP_ELEMENTS.values().stream().filter(TooltipElementBase::isEnabled).collect(Collectors.toList());
    }

}

package com.fuzs.consoleexperience.client.tooltip;

import com.fuzs.consoleexperience.config.ConfigManager;
import com.google.common.collect.Maps;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TooltipBuilder {

    private boolean moddedInfo;
    private boolean lastLine;

    public static final Map<String, TooltipElementBase> TOOLTIP_ELEMENTS = Maps.newLinkedHashMap();

    public List<ITextComponent> create(ItemStack itemstack) {

        return TOOLTIP_ELEMENTS.get("name").makeAndGet(itemstack, null);
    }

    public List<ITextComponent> create(ItemStack itemstack, @Nullable PlayerEntity playerIn, int rows) {

        boolean lastLine = this.lastLine && rows > 1;
        List<TooltipElementBase> activeElements = getActiveElements();
        activeElements.forEach(element -> element.make(itemstack, playerIn));
        if (activeElements.stream().mapToInt(TooltipElementBase::size).sum() > rows) {

            if (lastLine) {

                rows--;
            }

            activeElements.sort(Comparator.comparingInt(TooltipElementBase::getPriority));
            for (TooltipElementBase element : activeElements) {

                rows -= element.cut(Math.max(rows, 0));
            }
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

        if (this.moddedInfo) {

            int size = tooltip.size();
            ForgeEventFactory.onItemTooltip(itemstack, playerIn, tooltip, ITooltipFlag.TooltipFlags.NORMAL);
            if (tooltip.size() - size > rows) {

                tooltip.subList(size + rows, tooltip.size()).clear();
            }

            return tooltip.size() - size;
        }

        return 0;
    }

    private ITextComponent getLastLine(int amount) {

        return new TranslationTextComponent("container.shulkerBox.more", amount).mergeStyle(TextFormatting.GRAY, TextFormatting.ITALIC);
    }

    public void setupConfig(ForgeConfigSpec.Builder builder) {

        ConfigManager.registerEntry(ModConfig.Type.CLIENT, builder.comment("Enable tooltip information added by other mods to be included on the tooltip.").define("Modded Information", false), v -> this.moddedInfo = v);
        ConfigManager.registerEntry(ModConfig.Type.CLIENT, builder.comment("Show how many more lines there are that currently don't fit the tooltip.").define("Last Line", true), v -> this.lastLine = v);
    }

    public static List<TooltipElementBase> getActiveElements() {

        return TOOLTIP_ELEMENTS.values().stream().filter(TooltipElementBase::isEnabled).collect(Collectors.toList());
    }

    private static void add(TooltipElementBase element) {

        TOOLTIP_ELEMENTS.put(element.getName(), element);
    }

    static {

        add(new TooltipElements.Name(true, 1, 20, true));
        add(new TooltipElements.Durability(true, 10, 17, TextFormatting.GRAY));
        add(new TooltipElements.NameID(true, 12, 5, TextFormatting.DARK_GRAY));
        add(new TooltipElements.NBTAmount(true, 14, 4, TextFormatting.DARK_GRAY));
    }

}

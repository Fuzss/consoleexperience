package com.fuzs.consolehud.helper;

import com.fuzs.consolehud.ConsoleHud;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ReflectionHelper {

    private static final String GUIINGAME_HIGHLIGHTTICKS = "field_92017_k";
    private static final String GUIINGAME_HIGHLIGHTINGITEMSTACK = "field_92016_l";

    public static void setHighlightTicks(IngameGui instance, int highlightTicks) {

        try {

            ObfuscationReflectionHelper.setPrivateValue(IngameGui.class, instance, highlightTicks, GUIINGAME_HIGHLIGHTTICKS);

        } catch (Exception e) {

            ConsoleHud.LOGGER.error("setHighlightTicks() failed", e);

        }

    }

    public static void setHighlightingItemStack(IngameGui instance, ItemStack stack) {

        try {

            ObfuscationReflectionHelper.setPrivateValue(IngameGui.class, instance, stack, GUIINGAME_HIGHLIGHTINGITEMSTACK);

        } catch (Exception e) {

            ConsoleHud.LOGGER.error("setHighlightingItemStack() failed", e);

        }

    }

}

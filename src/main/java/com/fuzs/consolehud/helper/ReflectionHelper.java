package com.fuzs.consolehud.helper;

import com.fuzs.consolehud.ConsoleHud;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ReflectionHelper {

    private static final String GUIINGAME_HIGHLIGHTINGITEMSTACK = "field_92016_l";

    public static void setHighlightingItemStack(IngameGui instance, ItemStack stack) {

        try {

            ObfuscationReflectionHelper.setPrivateValue(IngameGui.class, instance, stack, GUIINGAME_HIGHLIGHTINGITEMSTACK);

        } catch (Exception e) {

            ConsoleHud.LOGGER.error("setHighlightingItemStack() failed", e);

        }

    }

}

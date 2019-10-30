package com.fuzs.consoleexperience.helper;

import com.fuzs.consoleexperience.ConsoleExperience;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Method;

public class ReflectionHelper {

    private static final String INGAMEGUI_HIGHLIGHTTICKS = "field_92017_k";
    private static final String INGAMEGUI_HIGHLIGHTINGITEMSTACK = "field_92016_l";
    private static final String SCREEN_ADDBUTTON = "addButton";

    public static void setHighlightTicks(IngameGui instance, int highlightTicks) {

        try {

            ObfuscationReflectionHelper.setPrivateValue(IngameGui.class, instance, highlightTicks, INGAMEGUI_HIGHLIGHTTICKS);

        } catch (Exception e) {

            ConsoleExperience.LOGGER.error("setHighlightTicks() failed", e);

        }

    }

    public static void setHighlightingItemStack(IngameGui instance, ItemStack stack) {

        try {

            ObfuscationReflectionHelper.setPrivateValue(IngameGui.class, instance, stack, INGAMEGUI_HIGHLIGHTINGITEMSTACK);

        } catch (Exception e) {

            ConsoleExperience.LOGGER.error("setHighlightingItemStack() failed", e);

        }

    }

    public static Method getAddButton() {

        try {

            return ObfuscationReflectionHelper.findMethod(Screen.class, SCREEN_ADDBUTTON, Widget.class);

        } catch (Exception e) {

            ConsoleExperience.LOGGER.error("getAddButton() failed", e);

        }

        return null;

    }

}

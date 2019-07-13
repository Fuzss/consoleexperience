package com.fuzs.consolehud.util;

import com.fuzs.consolehud.ConsoleHud;
import net.minecraft.client.gui.GuiIngame;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public interface IPrivateAccessor {

    String GUIINGAME_HIGHLIGHTTICKS = "field_92017_k";
    
    default void setHighlightTicks(GuiIngame instance, int highlightTicks) {

        try {

            ObfuscationReflectionHelper.setPrivateValue(GuiIngame.class, instance, highlightTicks, GUIINGAME_HIGHLIGHTTICKS);

        } catch (Exception ex) {

            ConsoleHud.LOGGER.error("setHighlightTicks() failed", ex);

        }

    }
}

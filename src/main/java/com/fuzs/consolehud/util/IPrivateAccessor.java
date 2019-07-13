package com.fuzs.consolehud.util;

import com.fuzs.consolehud.ConsoleHud;
import net.minecraft.client.gui.IngameGui;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public interface IPrivateAccessor {

    String GUIINGAME_HIGHLIGHTTICKS = "field_92017_k";
    
    default void setHighlightTicks(IngameGui instance, int highlightTicks) {

        try {

            ObfuscationReflectionHelper.setPrivateValue(IngameGui.class, instance, highlightTicks, GUIINGAME_HIGHLIGHTTICKS);

        } catch (Exception ex) {

            ConsoleHud.LOGGER.error("setHighlightTicks() failed", ex);

        }

    }
}

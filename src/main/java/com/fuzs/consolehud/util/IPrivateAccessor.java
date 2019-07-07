/*
** 2016 Juni 19
**
** The author disclaims copyright to this source code. In place of
** a legal notice, here is a blessing:
**    May you do good and not evil.
**    May you find forgiveness for yourself and forgive others.
**    May you share freely, never taking more than you give.
 */
package com.fuzs.consolehud.util;

import net.minecraft.client.gui.IngameGui;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public interface IPrivateAccessor {
    
    Logger LOGGER = LogManager.getLogger();

    String[] GUIINGAME_HIGHLIGHTTICKS = new String[]{"remainingHighlightTicks", "field_92017_k"};
    
    default void setHighlightTicks(IngameGui instance, int highlightTicks) {
        try {
            ObfuscationReflectionHelper.setPrivateValue(IngameGui.class, instance, highlightTicks, GUIINGAME_HIGHLIGHTTICKS[1]);
        } catch (Exception ex) {
            LOGGER.error("setHighlightTicks() failed", ex);
        }
    }

    default int getHighlightTicks(IngameGui instance) {
        try {
            return ObfuscationReflectionHelper.getPrivateValue(IngameGui.class, instance, GUIINGAME_HIGHLIGHTTICKS[1]);
        } catch (Exception ex) {
            LOGGER.error("getHighlightTicks() failed", ex);
        }
        return 0;
    }
}

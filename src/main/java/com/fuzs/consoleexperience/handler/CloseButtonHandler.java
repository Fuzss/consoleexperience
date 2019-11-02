package com.fuzs.consoleexperience.handler;

import com.fuzs.consoleexperience.helper.ReflectionHelper;
import com.fuzs.consoleexperience.util.CloseButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CloseButtonHandler {

    private final Minecraft mc = Minecraft.getInstance();

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post evt) {

        if (!ConfigBuildHandler.MISCELLANEOUS_CONFIG.closeButton.get() || !(evt.getGui() instanceof ContainerScreen)) {
            return;
        }

        ContainerScreen screen = (ContainerScreen) evt.getGui();

        // trying to exclude special containers like beacons
        if (screen.getXSize() != 176) {
            return;
        }

        int x = ConfigBuildHandler.MISCELLANEOUS_CONFIG.closeButtonXOffset.get();
        int y = ConfigBuildHandler.MISCELLANEOUS_CONFIG.closeButtonYOffset.get();
        CloseButton button = new CloseButton(x, y, p_213076_1_ -> this.mc.player.closeScreen(), screen);

        try {

            Method method = ReflectionHelper.getAddButton();
            if (method != null) {
                method.invoke(evt.getGui(), button);
            }

        } catch (IllegalAccessException | InvocationTargetException e) {

            e.printStackTrace();

        }

    }

}

package com.fuzs.consoleexperience.client.element;

import com.fuzs.consoleexperience.client.gui.screen.*;
import com.fuzs.consoleexperience.client.gui.screen.util.FancyScreenUtil;
import com.fuzs.consoleexperience.mixin.DisconnectedScreenAccessorMixin;
import com.fuzs.consoleexperience.mixin.MainMenuScreenAccessorMixin;
import com.fuzs.consoleexperience.mixin.WorldLoadProgressScreenAccessorMixin;
import net.minecraft.client.gui.screen.*;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;

public class FancyMenusElement extends GameplayElement {

    @Override
    public void setup() {

        this.addListener(this::onGuiOpen);
        this.addListener(this::onDrawScreenPre);
    }

    @Override
    public boolean getDefaultState() {

        return true;
    }

    @Override
    public String getDisplayName() {

        return "Fancy Menus";
    }

    @Override
    public String getDescription() {

        return "Replace boring dirt backgrounds with the fancy main menu panorama and some bold fonts.";
    }

    private void onGuiOpen(final GuiOpenEvent evt) {

        if (evt.getGui() instanceof MainMenuScreen) {

            ((MainMenuScreenAccessorMixin) evt.getGui()).setPanorama(FancyScreenUtil.MENU_PANORAMA);
        } else if (evt.getGui() instanceof WorkingScreen) {

            evt.setGui(new FancyWorkingScreen((WorkingScreen) evt.getGui()));
        } else if (evt.getGui() instanceof DownloadTerrainScreen) {

            evt.setGui(new FancyDownloadTerrainScreen());
        } else if (evt.getGui() instanceof DirtMessageScreen) {

            evt.setGui(new FancyDirtMessageScreen(evt.getGui().getTitle()));
        } else if (evt.getGui() instanceof WorldLoadProgressScreen) {

            evt.setGui(new FancyWorldLoadProgressScreen(((WorldLoadProgressScreenAccessorMixin) (evt.getGui())).getTracker()));
        } else if (evt.getGui() instanceof DisconnectedScreen) {

            DisconnectedScreenAccessorMixin accessor = (DisconnectedScreenAccessorMixin) evt.getGui();
            evt.setGui(new FancyDisconnectedScreen(accessor.getNextScreen(), evt.getGui().getTitle(), accessor.getMessage()));
        } else if (evt.getGui() instanceof ConnectingScreen) {

            FancyConnectingScreen.onGuiOpen();
        }
    }

    private void onDrawScreenPre(GuiScreenEvent.DrawScreenEvent.Pre evt) {

        if (evt.getGui() instanceof ConnectingScreen) {

            evt.setCanceled(true);
            FancyConnectingScreen.render(this.mc, evt.getMouseX(), evt.getMouseY(), evt.getRenderPartialTicks(), (ConnectingScreen) evt.getGui());
        }
    }

}

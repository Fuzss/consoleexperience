package com.fuzs.consoleexperience.client.element;

import com.fuzs.consoleexperience.client.gui.screen.*;
import com.fuzs.consoleexperience.config.JSONConfigUtil;
import com.fuzs.consoleexperience.mixin.DisconnectedScreenAccessorMixin;
import com.fuzs.consoleexperience.mixin.WorldLoadProgressScreenAccessorMixin;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.screen.*;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;

import java.io.FileReader;
import java.util.List;
import java.util.stream.Stream;

public class FancyMenusElement extends GameplayElement {

    private static final List<IFormattableTextComponent> TIPS_LIST = Lists.newArrayList();

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

        System.out.println(evt.getGui());
        if (evt.getGui() instanceof WorkingScreen) {

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
            FancyConnectingScreen.render(this.mc, evt.getMatrixStack(), evt.getMouseX(), evt.getMouseY(), evt.getRenderPartialTicks(), (ConnectingScreen) evt.getGui());
        }
    }

    public static IFormattableTextComponent getRandomTip() {

        if (TIPS_LIST.isEmpty()) {

            return new StringTextComponent("missingno");
        }

        return FancyMenusElement.TIPS_LIST.get((int) (FancyMenusElement.TIPS_LIST.size() * Math.random()));
    }

    public static void deserialize(FileReader reader) {

        TIPS_LIST.clear();
        Stream.of(JSONConfigUtil.GSON.fromJson(reader, String[].class))
                .forEach(tip -> TIPS_LIST.add(new TranslationTextComponent(tip)));
    }

}

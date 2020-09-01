package com.fuzs.consoleexperience.client.element;

import com.fuzs.consoleexperience.client.gui.button.CloseButton;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.ForgeConfigSpec;

public class CloseButtonElement extends GameplayElement {

    private int offsetX;
    private int offsetY;

    @Override
    public void setup() {

        this.addListener(this::onInitGui);
    }

    @Override
    protected boolean getDefaultState() {

        return true;
    }

    @Override
    protected String getDisplayName() {

        return "Close Button";
    }

    @Override
    protected String getDescription() {

        return "Add a button for closing to every container.";
    }

    @Override
    public void setupConfig(ForgeConfigSpec.Builder builder) {

        registerClientEntry(builder.comment("Offset on x-axis from gui right.").defineInRange("X-Offset", 5, Integer.MIN_VALUE, Integer.MAX_VALUE), v -> this.offsetX = v);
        registerClientEntry(builder.comment("Offset on y-axis from gui top.").defineInRange("Y-Offset", 5, Integer.MIN_VALUE, Integer.MAX_VALUE), v -> this.offsetY = v);
    }

    private void onInitGui(final GuiScreenEvent.InitGuiEvent.Post evt) {

        if (!(evt.getGui() instanceof ContainerScreen)) {

            return;
        }

        @SuppressWarnings("unchecked")
        ContainerScreen<Container> screen = (ContainerScreen<Container>) evt.getGui();

        // trying to exclude special containers like beacons
        if (screen.getXSize() != 176) {

            return;
        }

        evt.getGui().addButton(new CloseButton(this.offsetX, this.offsetY, button -> {

            assert (this.mc.player != null);
            this.mc.player.closeScreen();
        }, screen));
    }

}

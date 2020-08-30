package com.fuzs.consoleexperience.client.feature;

import com.fuzs.consoleexperience.util.CloseButton;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.ForgeConfigSpec;

public class CloseButtonFeature extends Feature {

    private ForgeConfigSpec.IntValue offsetX;
    private ForgeConfigSpec.IntValue offsetY;

    @Override
    public void setupFeature() {

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

        this.offsetX = builder.comment("Offset on x-axis from gui right.").defineInRange("Close Button X-Offset", 5, Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.offsetY = builder.comment("Offset on y-axis from gui top.").defineInRange("Close Button Y-Offset", 5, Integer.MIN_VALUE, Integer.MAX_VALUE);
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

        evt.getGui().addButton(new CloseButton(this.offsetX.get(), this.offsetY.get(), button -> {

            assert (this.mc.player != null);
            this.mc.player.closeScreen();
        }, screen));
    }

}

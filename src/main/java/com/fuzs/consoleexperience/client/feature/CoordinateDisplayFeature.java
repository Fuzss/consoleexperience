package com.fuzs.consoleexperience.client.feature;

import com.fuzs.consoleexperience.client.config.PositionPreset;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MainWindow;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeConfigSpec;

public class CoordinateDisplayFeature extends Feature {
    
    private ForgeConfigSpec.IntValue scale;
    private ForgeConfigSpec.IntValue xOffset;
    private ForgeConfigSpec.IntValue yOffset;
    private ForgeConfigSpec.EnumValue<PositionPreset> position;
    private ForgeConfigSpec.BooleanValue background;
    private ForgeConfigSpec.IntValue decimalPlaces;

    @Override
    public void setupFeature() {

        this.addListener(this::onRenderGameOverlayPre);
    }

    @Override
    protected boolean getDefaultState() {

        return false;
    }

    @Override
    protected String getDisplayName() {

        return "Coordinate Display";
    }

    @Override
    protected String getDescription() {

        return "Always show player coordinates on screen.";
    }

    @Override
    public void setupConfig(ForgeConfigSpec.Builder builder) {

        this.scale = builder.comment("Scale of coordinate display. Works in tandem with \"GUI Scale\" option in \"Video Settings\".").defineInRange("Scale", 6, 1, 24);
        this.xOffset = builder.comment("Offset on x-axis from screen border.").defineInRange("X-Offset", 0, 0, Integer.MAX_VALUE);
        this.yOffset = builder.comment("Offset on y-axis from screen border.").defineInRange("Y-Offset", 60, 0, Integer.MAX_VALUE);
        this.position = builder.comment("Define a screen corner to show the coordinate display in.").defineEnum("Screen Corner", PositionPreset.TOP_LEFT);
        this.background = builder.comment("Show black chat background behind coordinate display for better visibility.").define("Draw Background", true);
        this.decimalPlaces = builder.comment("Amount of decimal places for the three coordinates.").defineInRange("Decimal Places", 0, 0, Integer.MAX_VALUE);
    }

    @SuppressWarnings("deprecation")
    private void onRenderGameOverlayPre(final RenderGameOverlayEvent.Chat evt) {

        if (this.mc.gameSettings.showDebugInfo) {

            return;
        }

        assert this.mc.player != null;
        int decimalPlaces = this.decimalPlaces.get();
        double playerX = round(this.mc.player.getPosX(), decimalPlaces);
        double playerY = round(this.mc.player.getBoundingBox().minY, decimalPlaces);
        double playerZ = round(this.mc.player.getPosZ(), decimalPlaces);
        boolean noDecimalPlaces = decimalPlaces == 0;
        // no empty decimal place added like this
        IFormattableTextComponent component = new TranslationTextComponent("screen.coordinates",
                noDecimalPlaces ? (int) playerX : playerX, noDecimalPlaces ? (int) playerY : playerY, noDecimalPlaces ? (int) playerZ : playerZ);

        int opacity = (int) ((this.mc.gameSettings.chatOpacity * 0.9F + 0.1F) * 255.0F);
        int stringWidth = this.mc.fontRenderer.getStringWidth(component.getString()) + 3;
        int stringHeight = 7 + 4;
        float scale = this.scale.get() / 6.0F;
        MainWindow window = evt.getWindow();
        PositionPreset position = this.position.get();
        int posX = (int) (position.getX(stringWidth, window.getScaledWidth(), this.xOffset.get()) / scale);
        int posY = (int) (position.getY(stringHeight, window.getScaledHeight(), this.yOffset.get()) / scale);

        RenderSystem.pushMatrix();
        RenderSystem.scalef(scale, scale, 1.0F);

        if (this.background.get()) {

            AbstractGui.fill(evt.getMatrixStack(), posX, posY, posX + stringWidth, posY + stringHeight, opacity / 2 << 24);
        }

        AbstractGui.drawString(evt.getMatrixStack(), this.mc.fontRenderer, component, posX + 2, posY + 2, 16777215 + (opacity << 24));
        RenderSystem.scalef(1.0F / scale, 1.0F / scale, 1.0F);
        RenderSystem.popMatrix();
    }

    private static double round(double toRound, int decimalPlaces) {

        double power = Math.pow(10, decimalPlaces);
        return  Math.round(toRound * power) / power;
    }

}

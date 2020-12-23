package com.fuzs.consoleexperience.client.element;

import com.fuzs.consoleexperience.client.gui.PositionPreset;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeConfigSpec;

public class CoordinateDisplayElement extends GameplayElement {
    
    private int scale;
    private int xOffset;
    private int yOffset;
    private PositionPreset position;
    private boolean background;
    private int decimalPlaces;

    @Override
    public void setup() {

        this.addListener(this::onRenderGameOverlayPre);
    }

    @Override
    public boolean getDefaultState() {

        return false;
    }

    @Override
    public String getDisplayName() {

        return "Coordinate Display";
    }

    @Override
    public String getDescription() {

        return "Always show player coordinates on screen.";
    }

    @Override
    public void setupConfig(ForgeConfigSpec.Builder builder) {

        registerClientEntry(builder.comment("Scale of coordinate display. Works in tandem with \"GUI Scale\" option in \"Video Settings\".").defineInRange("Scale", 6, 1, 24), v -> this.scale = v);
        registerClientEntry(builder.comment("Offset on x-axis from screen border.").defineInRange("X-Offset", 0, 0, Integer.MAX_VALUE), v -> this.xOffset = v);
        registerClientEntry(builder.comment("Offset on y-axis from screen border.").defineInRange("Y-Offset", 60, 0, Integer.MAX_VALUE), v -> this.yOffset = v);
        registerClientEntry(builder.comment("Define a screen corner to show the coordinate display in.").defineEnum("Screen Corner", PositionPreset.TOP_LEFT), v -> this.position = v);
        registerClientEntry(builder.comment("Show black chat background behind coordinate display for better visibility.").define("Draw Background", true), v -> this.background = v);
        registerClientEntry(builder.comment("Amount of decimal places for the three coordinates.").defineInRange("Decimal Places", 0, 0, Integer.MAX_VALUE), v -> this.decimalPlaces = v);
    }

    private void onRenderGameOverlayPre(final RenderGameOverlayEvent.Chat evt) {

        if (this.mc.gameSettings.showDebugInfo) {

            return;
        }

        this.mc.getProfiler().startSection("coordinateDisplay");
        ClientPlayerEntity player = this.mc.player;
        assert player != null;

        IFormattableTextComponent component;
        if (this.decimalPlaces == 0) {

            // no empty decimal place added like this
            int playerX = (int) player.getPosX();
            int playerY = (int) player.getBoundingBox().minY;
            int playerZ = (int) player.getPosZ();
            component = new TranslationTextComponent("screen.coordinates", playerX, playerY, playerZ);
        } else {

            double playerX = round(player.getPosX(), this.decimalPlaces);
            double playerY = round(player.getBoundingBox().minY, this.decimalPlaces);
            double playerZ = round(player.getPosZ(), this.decimalPlaces);
            component = new TranslationTextComponent("screen.coordinates", playerX, playerY, playerZ);
        }

        int opacity = (int) ((this.mc.gameSettings.chatOpacity * 0.9F + 0.1F) * 255.0F);
        int stringWidth = this.mc.fontRenderer.getStringPropertyWidth(component) + 3;
        int stringHeight = this.mc.fontRenderer.FONT_HEIGHT + 2;
        float scale = this.scale / 6.0F;
        int posX = (int) (this.position.getX(stringWidth, evt.getWindow().getScaledWidth(), this.xOffset) / scale);
        // adjust for hovering hotbar, since this is rendered on chat which is moved as well
        int posY = (int) (this.position.getY(stringHeight, evt.getWindow().getScaledHeight(), this.yOffset) / scale);
        this.renderCoordinates(evt.getMatrixStack(), component, opacity, stringWidth, stringHeight, scale, posX, posY);
        this.mc.getProfiler().endSection();
    }

    @SuppressWarnings("deprecation")
    private void renderCoordinates(MatrixStack matrixStack, IFormattableTextComponent component, int opacity, int stringWidth, int stringHeight, float scale, int posX, int posY) {

        ((HoveringHotbarElement) GameplayElements.HOVERING_HOTBAR).run(() -> {

            RenderSystem.pushMatrix();
            RenderSystem.scalef(scale, scale, 1.0F);
            if (this.background) {

                AbstractGui.fill(matrixStack, posX, posY, posX + stringWidth, posY + stringHeight, opacity / 2 << 24);
            }

            AbstractGui.drawString(matrixStack, this.mc.fontRenderer, component, posX + 2, posY + 2, 16777215 + (opacity << 24));
            RenderSystem.scalef(1.0F / scale, 1.0F / scale, 1.0F);
            RenderSystem.popMatrix();
        });
    }

    private static double round(double toRound, int decimalPlaces) {

        double power = Math.pow(10, decimalPlaces);
        return  Math.round(toRound * power) / power;
    }

}

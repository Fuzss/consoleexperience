package com.fuzs.consoleexperience.client.feature;

import com.fuzs.consoleexperience.ConsoleExperience;
import com.fuzs.consoleexperience.client.config.PositionPreset;
import com.fuzs.consoleexperience.helper.BackgroundState;
import com.fuzs.consoleexperience.helper.PaperDollHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;

@SuppressWarnings({"FieldCanBeLocal", "deprecation"})
public class SaveIconFeature extends Feature {

    private static final ResourceLocation SAVE_ICONS = new ResourceLocation(ConsoleExperience.MODID,"textures/gui/auto_save.png");

    private ForgeConfigSpec.IntValue xOffset;
    private ForgeConfigSpec.IntValue yOffset;
    private ForgeConfigSpec.EnumValue<PositionPreset> position;
    private ForgeConfigSpec.IntValue displayTime;
    private ForgeConfigSpec.BooleanValue potionShift;
    private ForgeConfigSpec.BooleanValue showArrow;
    private ForgeConfigSpec.BooleanValue rotatingModel;

    private final BackgroundState state = new BackgroundState();
    private final int width = 18;
    private final int height = 30;
    private int remainingDisplayTicks;

    @Override
    public void setupFeature() {

        this.addListener(this.state::onBackgroundDrawn);
        this.addListener(this::onSaveWorld);
        this.addListener(this::onClientTick);
        this.addListener(this::onRenderGameOverlayPre);
        this.addListener(this::onBackgroundDrawn);
    }

    @Override
    protected boolean getDefaultState() {

        return true;
    }

    @Override
    protected String getDisplayName() {

        return "Save Icon";
    }

    @Override
    protected String getDescription() {

        return "Show an animated icon on the screen whenever the world is being saved (every 45 seconds by default). This only works in singleplayer.";
    }

    @Override
    public void setupConfig(ForgeConfigSpec.Builder builder) {

        this.xOffset = builder.comment("Offset on x-axis from screen border.").defineInRange("X-Offset", 17, 0, Integer.MAX_VALUE);
        this.yOffset = builder.comment("Offset on y-axis from screen border.").defineInRange("Y-Offset", 15, 0, Integer.MAX_VALUE);
        this.position = builder.comment("Define a screen corner to display the save icon in.").defineEnum("Screen Corner", PositionPreset.TOP_RIGHT);
        this.displayTime = builder.comment("Amount of ticks the save icon will be displayed for. Set to 0 to always display the icon.").defineInRange("Display Time", 40, 0, Integer.MAX_VALUE);
        this.potionShift = builder.comment("Shift the save icon downwards when it would otherwise overlap with the potion icons. Only applicable when the \"Screen Corner\" is set to \"TOP_RIGHT\".").define("Potion Shift", true);
        this.showArrow = builder.comment("Show a downwards pointing, animated arrow above the save icon.").define("Show Arrow", true);
        this.rotatingModel = builder.comment("Use an animated chest model instead of the static texture.").define("Fancy Model", true);
    }

    @Override
    public boolean isActive() {

        return this.remainingDisplayTicks > 0 || this.displayTime.get() == 0;
    }

    private void onSaveWorld(final WorldEvent.Save evt) {

        this.remainingDisplayTicks = this.displayTime.get();
    }

    private void onClientTick(final TickEvent.ClientTickEvent evt) {

        if (evt.phase != TickEvent.Phase.END) {

            return;
        }

        if (this.remainingDisplayTicks > 0) {

            this.remainingDisplayTicks--;
        }
    }

    private void onRenderGameOverlayPre(final RenderGameOverlayEvent.Pre evt) {

        if (!this.state.isActive() && evt.getType() == RenderGameOverlayEvent.ElementType.ALL) {

            this.drawIcon(evt.getMatrixStack(), evt.getWindow().getScaledWidth(), evt.getWindow().getScaledHeight(), true);
        }
    }

    private void onBackgroundDrawn(final GuiScreenEvent.BackgroundDrawnEvent evt) {

        // only try rendering while in-game
        if (this.mc.world != null) {

            this.drawIcon(evt.getMatrixStack(), this.mc.getMainWindow().getScaledWidth(), this.mc.getMainWindow().getScaledHeight(), false);
        }

    }

    private void drawIcon(MatrixStack matrixStack, int windowWidth, int windowHeight, boolean shift) {

        if (this.isActive()) {

            this.mc.getTextureManager().bindTexture(SAVE_ICONS);
            PositionPreset position = this.position.get();
            int posX = position.getX(this.width, windowWidth, this.xOffset.get());
            int posY = position.getY(this.height, windowHeight, this.yOffset.get());

            if (shift && this.potionShift.get() && position.shouldShift()) {

                assert this.mc.player != null;
                posY += PaperDollHelper.getPotionShift(this.mc.player.getActivePotionEffects());
            }

            RenderSystem.pushMatrix();
            RenderSystem.enableBlend();
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawModel(matrixStack, position, posX, posY);
            this.drawArrow(matrixStack, posX, posY);
            RenderSystem.disableBlend();
            RenderSystem.popMatrix();
        }
    }

    private void drawModel(MatrixStack matrixStack, PositionPreset position, int posX, int posY) {

        if (this.rotatingModel.get()) {

            int textureX = (int) ((this.remainingDisplayTicks % 12) * 0.5F) * 36;
            int textureY = 30 + ((int) ((this.remainingDisplayTicks % 48) * 0.5F) / 6) * 36;
            float scale = 0.5F;
            RenderSystem.scalef(scale, scale, 1.0F);
            AbstractGui.blit(matrixStack, (int) (posX / scale), (int) ((posY + 14) / scale), textureX, textureY, 36, 36, 256, 256);
            RenderSystem.scalef(1.0F / scale, 1.0F / scale, 1.0F);
        } else {

            AbstractGui.blit(matrixStack, posX, posY, position.isMirrored() ? 162 : 144, 0, this.width, this.height, 256, 256);
        }
    }

    private void drawArrow(MatrixStack matrixStack, int posX, int posY) {

        if (this.showArrow.get()) {

            int offsetX = (int) ((this.remainingDisplayTicks % 16) * 0.5F) * this.width;
            AbstractGui.blit(matrixStack, posX, posY, offsetX, 0, this.width, this.height, 256, 256);
        }
    }

}

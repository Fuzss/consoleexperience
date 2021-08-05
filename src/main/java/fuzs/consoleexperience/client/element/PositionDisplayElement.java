package fuzs.consoleexperience.client.element;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import fuzs.consoleexperience.client.gui.PositionPreset;
import fuzs.puzzleslib.config.option.OptionsBuilder;
import fuzs.puzzleslib.element.AbstractElement;
import fuzs.puzzleslib.element.side.IClientElement;
import fuzs.puzzleslib.util.PuzzlesUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class PositionDisplayElement extends AbstractElement implements IClientElement {
    
    private final Minecraft mc = Minecraft.getInstance();

    public boolean mapDisplay;
    public PositionFormat mapTextFormat;
    public int mapScale;
    public int mapXOffset;
    public int mapYOffset;
    public PositionPreset mapPosition;
    public boolean mapBackground;
    public int mapDecimalPlaces;
    public boolean hudDisplay;
    public PositionFormat hudTextFormat;
    public int hudScale;
    public int hudXOffset;
    public int hudYOffset;
    public PositionPreset hudPosition;
    public boolean hudBackground;
    public int hudDecimalPlaces;

    @Override
    public void constructClient() {

        this.addListener(this::onRenderGameOverlayPre);
    }

    @Override
    public String[] getDescription() {

        return new String[]{"Always show player position on held maps and on screen."};
    }

    @Override
    public void setupClientConfig(OptionsBuilder builder) {

        builder.push("map_display");
        builder.define("Map Display", true).comment("Draw player position on a held map item.").sync(v -> this.mapDisplay = v);
        builder.define("Text Format", PositionFormat.AXIS).comment("Text format style for position display.").sync(v -> this.mapTextFormat = v);
        builder.define("Scale", 8).range(1, 18).comment("Scale of position display. Also influenced by \"GUI Scale\" option in \"Video Settings\".").sync(v -> this.mapScale = v);
        builder.define("X-Offset", 0).min(0).comment("Offset on x-axis from map border.").sync(v -> this.mapXOffset = v);
        builder.define("Y-Offset", 0).min(0).comment("Offset on y-axis from map border.").sync(v -> this.mapYOffset = v);
        builder.define("Screen Corner", PositionPreset.TOP_LEFT).comment("Define a map corner to render position display in.").sync(v -> this.mapPosition = v);
        builder.define("Black Background", false).comment("Show black chat background behind position for better visibility.").sync(v -> this.mapBackground = v);
        builder.define("Decimal Places", 0).min(0).comment("Number of decimal places for coordinates.").sync(v -> mapDecimalPlaces = v);
        builder.pop();
        builder.push("hud_display");
        builder.define("Hud Display", false).comment("Always show player position on screen.").sync(v -> this.hudDisplay = v);
        builder.define("Text Format", PositionFormat.PREFIX).comment("Text format style for position display.").sync(v -> this.hudTextFormat = v);
        builder.define("Scale", 6).range(1, 24).comment("Scale of position display. Also influenced by \"GUI Scale\" option in \"Video Settings\".").sync(v -> this.hudScale = v);
        builder.define("X-Offset", 0).min(0).comment("Offset on x-axis from screen border.").sync(v -> this.hudXOffset = v);
        builder.define("Y-Offset", 60).min(0).comment("Offset on y-axis from screen border.").sync(v -> this.hudYOffset = v);
        builder.define("Screen Corner", PositionPreset.TOP_LEFT).comment("Define a screen corner to render position display in.").sync(v -> this.hudPosition = v);
        builder.define("Black Background", true).comment("Show black chat background behind position for better visibility.").sync(v -> this.hudBackground = v);
        builder.define("Decimal Places", 0).min(0).comment("Number of decimal places for coordinates.").sync(v -> hudDecimalPlaces = v);
        builder.pop();
    }

    private void onRenderGameOverlayPre(final RenderGameOverlayEvent.Chat evt) {

        if (!this.mc.options.renderDebug && this.hudDisplay) {

            this.mc.getProfiler().push("positionDisplay");
            ITextComponent coordinateComponent = getCoordinateComponent(this.mc.player, this.hudTextFormat, this.hudDecimalPlaces);
            int opacity = (int) ((this.mc.options.chatOpacity * 0.9F + 0.1F) * 255.0F);
            int stringWidth = this.mc.font.width(coordinateComponent) + 3;
            int stringHeight = this.mc.font.lineHeight + 2;
            float scale = this.hudScale / 6.0F;
            int posX = (int) (this.hudPosition.getX(stringWidth, evt.getWindow().getGuiScaledWidth(), this.hudXOffset) / scale);
            // adjust for hovering hotbar, since this is rendered on chat which is moved as well
            int posY = (int) (this.hudPosition.getY(stringHeight, evt.getWindow().getGuiScaledHeight(), this.hudYOffset) / scale);
            this.renderCoordinates(evt.getMatrixStack(), coordinateComponent, opacity, stringWidth, stringHeight, scale, posX, posY);
            this.mc.getProfiler().pop();
        }
    }

    @SuppressWarnings("deprecation")
    private void renderCoordinates(MatrixStack matrixStack, ITextComponent component, int opacity, int stringWidth, int stringHeight, float scale, int posX, int posY) {

        ((HoveringHotbarElement) GameplayElements.HOVERING_HOTBAR).run(() -> {

            RenderSystem.pushMatrix();
            RenderSystem.scalef(scale, scale, 1.0F);
            if (this.hudBackground) {

                AbstractGui.fill(matrixStack, posX, posY, posX + stringWidth, posY + stringHeight, opacity / 2 << 24);
            }

            AbstractGui.drawString(matrixStack, this.mc.font, component, posX + 2, posY + 2, 16777215 + (opacity << 24));
            RenderSystem.scalef(1.0F / scale, 1.0F / scale, 1.0F);
            RenderSystem.popMatrix();
        });
    }

    public static ITextComponent getCoordinateComponent(PlayerEntity player, PositionFormat format, int decimalPlaces) {

        if (decimalPlaces == 0) {

            // no empty decimal place added like this
            int playerX = (int) player.getX();
            int playerY = (int) player.getBoundingBox().minY;
            int playerZ = (int) player.getZ();

            return new TranslationTextComponent(format.translationKey, playerX, playerY, playerZ);
        }

        double playerX = PuzzlesUtil.round(player.getX(), decimalPlaces);
        double playerY = PuzzlesUtil.round(player.getBoundingBox().minY, decimalPlaces);
        double playerZ = PuzzlesUtil.round(player.getZ(), decimalPlaces);

        return new TranslationTextComponent(format.translationKey, playerX, playerY, playerZ);
    }

    public enum PositionFormat {

        PREFIX("screen.position"),
        AXIS("map.position");

        public final String translationKey;

        PositionFormat(String translationKey) {

            this.translationKey = translationKey;
        }

    }

}

package fuzs.consoleexperience.client.element;

import fuzs.consoleexperience.client.gui.screen.util.RenderTooltipUtil;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.ForgeConfigSpec;

public class TintedTooltipElement extends GameplayElement {

    private int backgroundColor;
    private int borderColor;

    @Override
    public void setup() {

        this.addListener(this::onRenderTooltipColor);
    }

    @Override
    public boolean getDefaultState() {

        return false;
    }

    @Override
    public String getDisplayName() {

        return "Tinted Tooltip";
    }

    @Override
    public String getDescription() {

        return "Color item tooltips in a cyan shade just like on Console Edition.";
    }

    @Override
    public void setupConfig(ForgeConfigSpec.Builder builder) {

        registerClientEntry(builder.comment("Color for tooltip background as RGBA in decimal form.").defineInRange("Background Color", RenderTooltipUtil.TOOLTIP_COLORS[0], Integer.MIN_VALUE, Integer.MAX_VALUE), v -> this.backgroundColor = v);
        registerClientEntry(builder.comment("Color for tooltip border as RGBA in decimal form.").defineInRange("Border Color", RenderTooltipUtil.TOOLTIP_COLORS[1], Integer.MIN_VALUE, Integer.MAX_VALUE), v -> this.borderColor = v);
    }

    private void onRenderTooltipColor(final RenderTooltipEvent.Color evt) {

        evt.setBackground(this.backgroundColor);
        evt.setBorderStart(this.borderColor);
        evt.setBorderEnd(this.borderColor);
    }

}

package com.fuzs.consoleexperience.client.element;

import net.minecraftforge.client.event.RenderTooltipEvent;

public class TintedTooltipElement extends GameplayElement {

    @Override
    public void setup() {

        this.addListener(this::onRenderTooltipColor);
    }

    @Override
    protected boolean getDefaultState() {

        return false;
    }

    @Override
    protected String getDisplayName() {

        return "Tinted Tooltip";
    }

    @Override
    protected String getDescription() {

        return "Make item tooltips colored similarly to Console Edition.";
    }

    private void onRenderTooltipColor(final RenderTooltipEvent.Color evt) {

        // change colors to something close to console edition
        evt.setBorderStart(0xEBFFFFFF);
        evt.setBorderEnd(0xE6FFFFFF);
        evt.setBackground(0xAA09202A);
    }

}

package com.fuzs.consoleexperience.client.element;

import com.fuzs.consoleexperience.client.util.BackgroundState;
import com.google.common.collect.Lists;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.List;

public class HideHudElement extends GameplayElement {

    // list of hud elements allowed to be hidden
    private static final List<RenderGameOverlayEvent.ElementType> VISIBLE_ELEMENTS = Lists.newArrayList(
            ElementType.ALL, ElementType.HELMET, ElementType.PORTAL, ElementType.VIGNETTE
    );
    private final BackgroundState state = new BackgroundState(8);

    @Override
    public void setup() {

        this.addListener(this.state::onBackgroundDrawn);
        this.addListener(this.state::onRenderGameOverlayPost);
        this.addListener(EventPriority.HIGHEST, this::onRenderGameOverlayPre);
    }

    @Override
    protected boolean getDefaultState() {

        return true;
    }

    @Override
    protected String getDisplayName() {

        return "Hide Hud";
    }

    @Override
    protected String getDescription() {

        return "Hide all hud elements when inside of a container.";
    }

    @Override
    public boolean isVisible() {

        return this.state.isActive();
    }

    private void onRenderGameOverlayPre(final RenderGameOverlayEvent.Pre evt) {

        if (this.isVisible() && !VISIBLE_ELEMENTS.contains(evt.getType())) {

            evt.setCanceled(true);
        }
    }

}

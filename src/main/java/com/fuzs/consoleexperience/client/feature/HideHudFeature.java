package com.fuzs.consoleexperience.client.feature;

import com.fuzs.consoleexperience.helper.BackgroundState;
import com.google.common.collect.Lists;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.List;

public class HideHudFeature extends Feature {

    private final BackgroundState state = new BackgroundState();
    // list of hud elements allowed to be hidden
    private final List<RenderGameOverlayEvent.ElementType> elements = Lists.newArrayList(
            ElementType.CROSSHAIRS, ElementType.BOSSHEALTH, ElementType.BOSSINFO, ElementType.ARMOR, ElementType.HEALTH,
            ElementType.FOOD, ElementType.AIR, ElementType.HOTBAR, ElementType.EXPERIENCE, ElementType.TEXT,
            ElementType.HEALTHMOUNT, ElementType.JUMPBAR, ElementType.CHAT, ElementType.PLAYER_LIST, ElementType.DEBUG,
            ElementType.POTION_ICONS, ElementType.SUBTITLES, ElementType.FPS_GRAPH
    );

    @Override
    public void setupFeature() {

        this.addListener(this.state::onBackgroundDrawn);
        this.addListener(EventPriority.HIGHEST, this::onRenderGameOverlayPre);
    }

    @Override
    protected boolean getDefaultState() {

        return true;
    }

    @Override
    protected String getDisplayName() {

        return "Hide Hud In Containers";
    }

    @Override
    protected String getDescription() {

        return "Hide hud elements when inside of a container.";
    }

    @Override
    public boolean isActive() {

        return this.state.isActive();
    }

    private void onRenderGameOverlayPre(final RenderGameOverlayEvent.Pre evt) {

        if (this.isActive()) {

            if (evt.getType() == ElementType.ALL) {

                this.state.tick();
            } else if (this.elements.contains(evt.getType())) {

                evt.setCanceled(true);
            }
        }
    }

}

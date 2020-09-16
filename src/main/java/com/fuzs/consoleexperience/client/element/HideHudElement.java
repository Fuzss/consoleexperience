package com.fuzs.consoleexperience.client.element;

import com.fuzs.consoleexperience.client.util.BackgroundState;
import com.google.common.collect.Lists;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.List;

public class HideHudElement extends GameplayElement implements IHasDisplayTime {

    // list of hud elements allowed to be hidden
    private static final List<RenderGameOverlayEvent.ElementType> VISIBLE_ELEMENTS = Lists.newArrayList(
            ElementType.ALL, ElementType.HELMET, ElementType.PORTAL, ElementType.VIGNETTE
    );

    private final int defaultDelay = 8;
    private final BackgroundState state = new BackgroundState(this.defaultDelay);

    @Override
    public void setup() {

        this.addListener(this.state::onBackgroundDrawn);
        this.addListener(this.state::onRenderGameOverlayPost);
        this.addListener(this::onRenderGameOverlayPre, EventPriority.HIGHEST);
    }

    @Override
    public boolean getDefaultState() {

        return true;
    }

    @Override
    public String getDisplayName() {

        return "Hide Hud";
    }

    @Override
    public String getDescription() {

        return "Hide all hud elements when inside of a container.";
    }

    @Override
    public void setupConfig(ForgeConfigSpec.Builder builder) {

        registerClientEntry(builder.comment("Delay after which hud elements appear again.").defineInRange("Hide Delay", this.defaultDelay, 1, Integer.MAX_VALUE), this.state::setCapacity);
    }

    @Override
    public boolean isVisible() {

        return this.state.isActive();
    }

    private void onRenderGameOverlayPre(final RenderGameOverlayEvent.Pre evt) {

        if (this.isVisible() && !VISIBLE_ELEMENTS.contains(evt.getType())) {

            if (evt.getType() != ElementType.CHAT || this.state.isChatHidden()) {

                evt.setCanceled(true);
            }
        }
    }

}

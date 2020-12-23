package com.fuzs.consoleexperience.client.element;

import com.fuzs.consoleexperience.client.util.BackgroundState;
import com.fuzs.consoleexperience.client.util.CompatibilityMode;
import com.google.common.collect.Lists;
import net.minecraft.util.math.MathHelper;
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

    private CompatibilityMode compatibilityMode;

    @Override
    public void setup() {

        this.addListener(this.state::onBackgroundDrawn);
        this.addListener(this.state::onRenderTick);
        this.addListener(this.state::onClientTick);
        this.addListener(this::onRenderGameOverlayPre1, EventPriority.HIGHEST);
        this.addListener(this::onRenderGameOverlayPre2, EventPriority.LOWEST, true);
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
        registerClientEntry(builder.comment("Compatibility mode for screen elements from mods that normally go unaffected. May have an unwanted impact on other elements, too. Tinker around with modes 1-3, setting to 0 will disable this mode.").define("Compatibility Mode", 0), v -> this.compatibilityMode = MathHelper.clamp(v, 0, 3) == v ? CompatibilityMode.values()[v] : CompatibilityMode.NONE);
    }

    @Override
    public boolean isVisible() {

        return this.state.isActive();
    }

    private void onRenderGameOverlayPre1(final RenderGameOverlayEvent.Pre evt) {

        if (this.isVisible() && (!VISIBLE_ELEMENTS.contains(evt.getType()) || CompatibilityMode.isEnabled(CompatibilityMode.PRE, this.compatibilityMode) && evt.getType() == ElementType.ALL)) {

            if (evt.getType() != ElementType.CHAT || this.state.isChatHidden()) {

                evt.setCanceled(true);
            }
        }
    }

    private void onRenderGameOverlayPre2(final RenderGameOverlayEvent.Pre evt) {

        if (this.isVisible() && CompatibilityMode.isEnabled(CompatibilityMode.PRE, this.compatibilityMode) && evt.getType() == ElementType.ALL) {

            evt.setCanceled(false);
        }
    }

    public CompatibilityMode getCompatibilityMode() {

        return this.isEnabled() ? this.compatibilityMode : CompatibilityMode.NONE;
    }

}

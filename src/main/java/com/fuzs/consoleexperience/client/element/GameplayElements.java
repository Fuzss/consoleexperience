package com.fuzs.consoleexperience.client.element;

import com.fuzs.consoleexperience.ConsoleExperience;
import com.google.common.collect.Maps;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Map;

public class GameplayElements {

    private static final Map<ResourceLocation, GameplayElement> ELEMENTS = Maps.newHashMap();

    public static final GameplayElement PAPER_DOLL = register("paper_doll", new PaperDollElement());
    public static final GameplayElement HOVERING_HOTBAR = register("hovering_hotbar", new HoveringHotbarElement());
    public static final GameplayElement SAVE_ICON = register("save_icon", new SaveIconElement());
    public static final GameplayElement COORDINATE_DISPLAY = register("coordinate_display", new CoordinateDisplayElement());
    public static final GameplayElement ELYTRA_TILT = register("elytra_tilt", new ElytraTiltElement());
    public static final GameplayElement SHULKER_TOOLTIP = register("shulker_tooltip", new ShulkerTooltipElement());
    public static final GameplayElement HIDE_HUD = register("hide_hud", new HideHudElement());
    public static final GameplayElement CLOSE_BUTTON = register("close_button", new CloseButtonElement());
    public static final GameplayElement TINTED_TOOLTIP = register("tinted_tooltip", new TintedTooltipElement());
    public static final GameplayElement EATING_ANIMATION = register("eating_animation", new EatingAnimationElement());

    private static GameplayElement register(String key, GameplayElement gameplayElement) {

        ELEMENTS.put(new ResourceLocation(ConsoleExperience.MODID, key), gameplayElement);
        return gameplayElement;
    }

    public static void setup(ForgeConfigSpec.Builder builder) {

        ELEMENTS.values().forEach(element -> {

            builder.push("general");
            element.setup(builder);
            builder.pop();
        });

        ELEMENTS.forEach((key, element) -> {

            builder.push(key.getPath());
            element.setupConfig(builder);
            builder.pop();
        });
    }

    public static void init() {

        ELEMENTS.values().forEach(GameplayElement::init);
    }

}

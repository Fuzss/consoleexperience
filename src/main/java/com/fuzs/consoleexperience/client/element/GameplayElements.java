package com.fuzs.consoleexperience.client.element;

import com.fuzs.consoleexperience.ConsoleExperience;
import com.google.common.collect.Maps;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Map;

public class GameplayElements {

    private static final Map<ResourceLocation, GameplayElement> ELEMENTS = Maps.newHashMap();

    public static final GameplayElement SELECTED_ITEM = register("selected_item", new SelectedItemElement());
    public static final GameplayElement PAPER_DOLL = register("paper_doll", new PaperDollElement());
    public static final GameplayElement HOVERING_HOTBAR = register("hovering_hotbar", new HoveringHotbarElement());
    public static final GameplayElement SAVE_ICON = register("save_icon", new SaveIconElement());
    public static final GameplayElement COORDINATE_DISPLAY = register("coordinate_display", new CoordinateDisplayElement());
    public static final GameplayElement ELYTRA_TILT = register("elytra_tilt", new ElytraTiltElement());
    public static final GameplayElement SHULKER_TOOLTIP = register("shulker_tooltip", new ShulkerTooltipElement());
    public static final GameplayElement HIDE_HUD = register("hide_hud", new HideHudElement());
    public static final GameplayElement CLOSE_BUTTON = register("close_button", new CloseButtonElement());
    public static final GameplayElement PLAYER_ANIMATIONS = register("player_animations", new PlayerAnimationsElement());
    public static final GameplayElement FALLING_ASLEEP = register("falling_asleep", new FallingAsleepElement());
    public static final GameplayElement POTION_TIME = register("potion_time", new PotionTimeElement());
    public static final GameplayElement FANCY_MENUS = register("fancy_menus", new FancyMenusElement());
    public static final GameplayElement MENU_PLAYER = register("menu_player", new MenuPlayerElement());

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

    public static void load() {

        ELEMENTS.values().forEach(GameplayElement::load);
    }

}

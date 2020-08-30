package com.fuzs.consoleexperience.client.feature;

import com.google.common.collect.Maps;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Map;

public class Features {

    private static final Map<String, Feature> FEATURES = Maps.newHashMap();

    public static final Feature SAVE_ICON = register("save_icon", new SaveIconFeature());
    public static final Feature COORDINATE_DISPLAY = register("coordinate_display", new CoordinateDisplayFeature());
    public static final Feature HIDE_HUD = register("hide_hud", new HideHudFeature());
    public static final Feature CLOSE_BUTTON = register("close_button", new CloseButtonFeature());

    private static Feature register(String key, Feature feature) {

        return FEATURES.put(key, feature);
    }

    public static void init() {

        FEATURES.values().forEach(Feature::init);
    }

    public static void setupConfig(ForgeConfigSpec.Builder builder) {

        FEATURES.values().forEach(feature -> {

            builder.push("general");
            feature.setupGeneralConfig(builder);
            builder.pop();
        });

        FEATURES.forEach((key, feature) -> {

            builder.push(key);
            feature.setupConfig(builder);
            builder.pop();
        });
    }

}

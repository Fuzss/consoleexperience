package com.fuzs.consoleexperience.config;

import com.fuzs.consoleexperience.ConsoleExperience;
import com.fuzs.consoleexperience.client.tooltip.TooltipBuilder;
import com.fuzs.consoleexperience.client.tooltip.TooltipElementBase;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

public class JSONConfigUtil {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void load(String jsonName, String modId) {

        File jsonFile = new File(FMLPaths.CONFIGDIR.get().toFile(), modId + File.separator + jsonName);
        if (!createIfAbsent(jsonName, jsonFile)) {

            loadFromFile(jsonName, jsonFile);
        }
    }

    private static boolean createIfAbsent(String jsonName, File jsonFile) {

        if (!jsonFile.exists()) {

            jsonFile.getParentFile().mkdir();
            saveToFile(jsonName, jsonFile);
            return true;
        }

        return false;
    }

    private static void saveToFile(String jsonName, File file) {

        try (FileWriter writer = new FileWriter(file)) {

            GSON.toJson(Maps.transformValues(TooltipBuilder.TOOLTIP_ELEMENTS, TooltipElementBase::serialize), writer);
        } catch (IOException e) {

            ConsoleExperience.LOGGER.error("Failed to create \"" + jsonName + "\" in config directory");
        }
    }

    private static void loadFromFile(String jsonName, File file) {

        try (FileReader reader = new FileReader(file)) {

            Type mapType = new TypeToken<Map<String, JsonElement>>() {}.getType();
            GSON.<Map<String, JsonElement>>fromJson(reader, mapType)
                    .forEach((key, value) -> TooltipBuilder.TOOLTIP_ELEMENTS.get(key).deserialize(value));
        } catch (IOException e) {

            ConsoleExperience.LOGGER.error("Failed to read \"" + jsonName + "\" in config directory");
        }
    }

}

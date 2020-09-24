package com.fuzs.consoleexperience.config;

import com.fuzs.consoleexperience.ConsoleExperience;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class JSONConfigUtil {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void load(String jsonName, String modId, BiConsumer<String, File> serializer, Consumer<FileReader> deserializer) {

        File jsonFile = getFilePath(jsonName, modId);
        createIfAbsent(jsonName, jsonFile, serializer);
        loadFromFile(jsonName, jsonFile, deserializer);
    }

    private static void createIfAbsent(String jsonName, File jsonFile, BiConsumer<String, File> serializer) {

        if (!jsonFile.exists()) {

            jsonFile.getParentFile().mkdir();
            serializer.accept(jsonName, jsonFile);
        }
    }

    public static void copyToFile(String jsonName, File jsonFile) {

        try (InputStream stream = JSONConfigUtil.class.getResourceAsStream(File.separator + jsonName)) {

            jsonFile.createNewFile();
            byte[] buffer = new byte[600000];
            FileOutputStream outStream = new FileOutputStream(jsonFile);
            int i;
            while ((i = stream.read(buffer)) != -1) {

                outStream.write(buffer, 0, i);
            }

            outStream.close();
        } catch (Exception e) {

            ConsoleExperience.LOGGER.error("Failed to copy \"" + jsonName + "\" in config directory!");
        }
    }

    public static void saveToFile(String jsonName, File jsonFile, JsonObject jsonobject) {

        try (FileWriter writer = new FileWriter(jsonFile)) {

            GSON.toJson(jsonobject, writer);
        } catch (Exception e) {

            ConsoleExperience.LOGGER.error("Failed to create \"" + jsonName + "\" in config directory");
        }
    }

    private static void loadFromFile(String jsonName, File file, Consumer<FileReader> deserializer) {

        try (FileReader reader = new FileReader(file)) {

            deserializer.accept(reader);
        } catch (Exception e) {

            ConsoleExperience.LOGGER.error("Failed to read \"" + jsonName + "\" in config directory");
        }
    }

    public static File getFilePath(String jsonName, String modId) {

        return new File(FMLPaths.CONFIGDIR.get().toFile(), modId + File.separator + jsonName);
    }

}

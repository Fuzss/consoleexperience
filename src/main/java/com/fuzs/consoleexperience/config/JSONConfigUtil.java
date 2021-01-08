package com.fuzs.consoleexperience.config;

import com.fuzs.consoleexperience.ConsoleExperience;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
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
            byte[] buffer = new byte[16384];
            FileOutputStream out = new FileOutputStream(jsonFile);
            int lengthRead;
            while ((lengthRead = stream.read(buffer)) != -1) {

                out.write(buffer, 0, lengthRead);
                out.flush();
            }

            out.close();
        } catch (Exception e) {

            ConsoleExperience.LOGGER.error("Failed to copy {} in config directory: {}", jsonName, e);
        }
    }

    public static void saveToFile(String jsonName, File jsonFile, JsonElement jsonelement) {

        try (FileWriter writer = new FileWriter(jsonFile)) {

            GSON.toJson(jsonelement, writer);
        } catch (Exception e) {

            ConsoleExperience.LOGGER.error("Failed to create {} in config directory: {}", jsonName, e);
        }
    }

    private static void loadFromFile(String jsonName, File file, Consumer<FileReader> deserializer) {

        try (FileReader reader = new FileReader(file)) {

            deserializer.accept(reader);
        } catch (Exception e) {

            ConsoleExperience.LOGGER.error("Failed to read {} in config directory: {}", jsonName, e);
        }
    }

    public static File getFilePath(String jsonName, String modId) {

        return new File(FMLPaths.CONFIGDIR.get().toFile(), modId + File.separator + jsonName);
    }

}

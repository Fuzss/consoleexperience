package com.fuzs.consoleexperience.config;

import com.fuzs.consoleexperience.ConsoleExperience;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class JsonBuildHandler {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public void load(String jsonName, String modId) {

        File jsonFile = new File(FMLPaths.CONFIGDIR.get().toFile(), modId + File.separator + jsonName);
        this.createIfAbsent(jsonName, jsonFile);
    }

    private void createIfAbsent(String jsonName, File jsonFile) {

        if (!jsonFile.exists()) {

            try (InputStream stream = this.getClass().getResourceAsStream(File.separator + jsonName)) {

                jsonFile.getParentFile().mkdir();
                jsonFile.createNewFile();
                byte[] buffer = new byte[600000];
                FileOutputStream outStream = new FileOutputStream(jsonFile);
                int i;
                while ((i = stream.read(buffer)) != -1) {

                    outStream.write(buffer, 0, i);
                }

                outStream.close();
            } catch (IOException e) {

                ConsoleExperience.LOGGER.error("Failed to copy \"" + jsonName + "\" into config directory!");
            }
        }
    }

}

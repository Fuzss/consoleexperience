package com.fuzs.consoleexperience;

import com.fuzs.consoleexperience.client.element.GameplayElements;
import com.fuzs.consoleexperience.config.ConfigManager;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
@Mod(ConsoleExperience.MODID)
public class ConsoleExperience {

    public static final String MODID = "consoleexperience";
    public static final String NAME = "Console Experience";
    public static final Logger LOGGER = LogManager.getLogger(ConsoleExperience.NAME);

    public ConsoleExperience() {

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onLoadComplete);

        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        GameplayElements.setupConfig(builder);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, builder.build());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ConfigManager::onModConfigReloading);
    }

    private void onClientSetup(final FMLClientSetupEvent evt) {

        ConfigManager.sync(ModConfig.Type.CLIENT);
        GameplayElements.init();
    }

    private void onLoadComplete(final FMLLoadCompleteEvent evt) {

        ConfigManager.sync(ModConfig.Type.CLIENT);
    }

}
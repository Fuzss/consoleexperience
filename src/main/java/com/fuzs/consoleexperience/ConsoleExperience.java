package com.fuzs.consoleexperience;

import com.fuzs.consoleexperience.client.feature.Features;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
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

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        Features.setupConfig(builder);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, builder.build());
    }

    private void clientSetup(final FMLClientSetupEvent evt) {

        Features.init();
    }

}
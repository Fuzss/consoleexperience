package com.fuzs.consoleexperience;

import com.fuzs.consoleexperience.handler.*;
import com.google.common.collect.ImmutableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings({"WeakerAccess", "unused"})
@Mod(ConsoleExperience.MODID)
public class ConsoleExperience {

    public static final String MODID = "consoleexperience";
    public static final String NAME = "Console Experience";
    public static final Logger LOGGER = LogManager.getLogger(ConsoleExperience.NAME);

    private final List<Supplier<Object>> handlers = ImmutableList.of(
            SelectedItemHandler::new,
            PaperDollHandler::new,
            HoveringHotbarHandler::new,
            SaveIconHandler::new,
            CoordinateDisplayHandler::new,
//                ControlHintHandler::new,
            ItemTooltipHandler::new,
            HideHudHandler::new,
            CloseButtonHandler::new,
            ElytraTiltHandler::new
    );

    public ConsoleExperience() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigBuildHandler.SPEC, MODID + ".toml");
    }

    private void clientSetup(final FMLClientSetupEvent evt) {
        this.handlers.forEach(handler -> MinecraftForge.EVENT_BUS.register(handler.get()));
    }

}
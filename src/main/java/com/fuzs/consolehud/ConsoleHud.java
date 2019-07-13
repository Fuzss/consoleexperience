package com.fuzs.consolehud;

import com.fuzs.consolehud.handler.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"WeakerAccess", "unused"})
@Mod(ConsoleHud.MODID)
public class ConsoleHud {

    public static final String MODID = "consolehud";
    public static final String NAME = "Console HUD";
    public static final Logger LOGGER = LogManager.getLogger(ConsoleHud.NAME);

    public ConsoleHud() {

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigHandler.SPEC, MODID + ".toml");
        MinecraftForge.EVENT_BUS.register(this);

    }

    private void clientSetup(final FMLClientSetupEvent evt) {

        MinecraftForge.EVENT_BUS.register(new SelectedItemHandler());
        MinecraftForge.EVENT_BUS.register(new PaperDollHandler());
        MinecraftForge.EVENT_BUS.register(new HoveringHotbarHandler());
        MinecraftForge.EVENT_BUS.register(new SaveIconHandler());
        MinecraftForge.EVENT_BUS.register(new CoordinateDisplayHandler());
        MinecraftForge.EVENT_BUS.register(new MiscHandler());

    }

}
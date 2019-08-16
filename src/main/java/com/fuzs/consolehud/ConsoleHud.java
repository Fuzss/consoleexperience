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

import java.util.Arrays;

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

        Class<?>[] handler = new Class<?>[]{
                SelectedItemHandler.class,
                PaperDollHandler.class,
                HoveringHotbarHandler.class,
                SaveIconHandler.class,
                CoordinateDisplayHandler.class,
                MiscHandler.class
        };

        Arrays.stream(handler).forEach(it -> {

            try {
                MinecraftForge.EVENT_BUS.register(it.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }

        });

    }

}
package com.fuzs.consoleexperience;

import com.fuzs.consoleexperience.client.element.GameplayElements;
import com.fuzs.consoleexperience.config.ConfigManager;
import com.fuzs.consoleexperience.config.JSONConfigUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
@Mod(ConsoleExperience.MODID)
public class ConsoleExperience {

    public static final String MODID = "consoleexperience";
    public static final String NAME = "Console Experience";
    public static final Logger LOGGER = LogManager.getLogger(ConsoleExperience.NAME);

    public ConsoleExperience() {

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onLoadComplete);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ConfigManager::onModConfigReloading);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {

            // this also creates the folder for the default Forge config
            JSONConfigUtil.load("helditemtooltips.json", MODID);
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
            GameplayElements.setup(builder);
            ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, builder.build(), ConfigManager.configNameForFolder(ModConfig.Type.CLIENT, MODID));

            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        });

        // clientSideOnly = true
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (remote, isServer) -> true));
    }

    private void onClientSetup(final FMLClientSetupEvent evt) {

        GameplayElements.load();
    }

    private void onLoadComplete(final FMLLoadCompleteEvent evt) {

        ConfigManager.sync();
    }

}
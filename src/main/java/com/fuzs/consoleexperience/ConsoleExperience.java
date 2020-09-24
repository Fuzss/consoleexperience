package com.fuzs.consoleexperience;

import com.fuzs.consoleexperience.client.element.FancyMenusElement;
import com.fuzs.consoleexperience.client.element.GameplayElements;
import com.fuzs.consoleexperience.client.tooltip.TooltipBuilder;
import com.fuzs.consoleexperience.config.ConfigManager;
import com.fuzs.consoleexperience.config.JSONConfigUtil;
import com.fuzs.consoleexperience.util.CommandRegisterer;
import com.mojang.brigadier.Command;
import net.minecraft.client.gui.screen.DemoScreen;
import net.minecraft.command.Commands;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
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

    private final String jsonConfigName = "helditemtooltips.json";
    private final String jsonTipsName = "tips.json";

    @SuppressWarnings("Convert2Lambda")
    public ConsoleExperience() {

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onLoadComplete);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ConfigManager::onModConfig);

        // Forge doesn't like this being a lambda
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> new DistExecutor.SafeRunnable() {

            @Override
            public void run() {

                // this also creates the folder for the default Forge config
                JSONConfigUtil.load(ConsoleExperience.this.jsonConfigName, MODID, TooltipBuilder::serialize, TooltipBuilder::deserialize);
                JSONConfigUtil.load(ConsoleExperience.this.jsonTipsName, MODID, JSONConfigUtil::copyToFile, FancyMenusElement::deserialize);
                ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
                GameplayElements.setup(builder);
                ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, builder.build(), ConfigManager.configNameForFolder(ModConfig.Type.CLIENT, MODID));

                FMLJavaModLoadingContext.get().getModEventBus().addListener(ConsoleExperience.this::onClientSetup);
            }

        });

        // clientSideOnly = true
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (remote, isServer) -> true));
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (client, parent) -> new DemoScreen());
    }

    private void onClientSetup(final FMLClientSetupEvent evt) {

        GameplayElements.load();
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    private void onLoadComplete(final FMLLoadCompleteEvent evt) {

        ConfigManager.sync();
    }

    private void onRegisterCommands(final RegisterCommandsEvent evt) {

        evt.getDispatcher().register(Commands.literal(ConsoleExperience.MODID).then(Commands.literal("reload").executes(ctx -> {

            CommandRegisterer.handleReload(this.jsonConfigName, TooltipBuilder::serialize, TooltipBuilder::deserialize, ctx);
            CommandRegisterer.handleReload(this.jsonTipsName, JSONConfigUtil::copyToFile, FancyMenusElement::deserialize, ctx);

            return Command.SINGLE_SUCCESS;
        })));
    }

}
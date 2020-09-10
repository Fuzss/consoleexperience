package com.fuzs.consoleexperience;

import com.fuzs.consoleexperience.client.element.GameplayElements;
import com.fuzs.consoleexperience.config.ConfigManager;
import com.fuzs.consoleexperience.config.JSONConfigUtil;
import com.mojang.brigadier.Command;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
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

    private final String jsonName = "helditemtooltips.json";

    public ConsoleExperience() {

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onLoadComplete);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ConfigManager::onModConfigReloading);

        // Forge doesn't like this being a lambda
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> new DistExecutor.SafeRunnable() {

            @Override
            public void run() {

                // this also creates the folder for the default Forge config
                JSONConfigUtil.load(ConsoleExperience.this.jsonName, MODID);
                ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
                GameplayElements.setup(builder);
                ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, builder.build(), ConfigManager.configNameForFolder(ModConfig.Type.CLIENT, MODID));

                FMLJavaModLoadingContext.get().getModEventBus().addListener(ConsoleExperience.this::onClientSetup);
            }

        });

        // clientSideOnly = true
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (remote, isServer) -> true));
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

            JSONConfigUtil.load(this.jsonName, MODID);
            ITextComponent itextcomponent = new StringTextComponent(this.jsonName).mergeStyle(TextFormatting.UNDERLINE)
                    .modifyStyle(style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, JSONConfigUtil.getFilePath(this.jsonName, MODID).getAbsolutePath())));
            ctx.getSource().sendFeedback(new TranslationTextComponent("command.reload", itextcomponent), true);

            return Command.SINGLE_SUCCESS;
        })));
    }

}
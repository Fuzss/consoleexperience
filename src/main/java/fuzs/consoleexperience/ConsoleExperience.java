package fuzs.consoleexperience;

import com.mojang.brigadier.Command;
import fuzs.consoleexperience.client.element.PlayerAnimationsElement;
import fuzs.consoleexperience.client.element.PositionDisplayElement;
import fuzs.consoleexperience.client.gui.screen.util.FancyScreenUtil;
import fuzs.consoleexperience.client.tooltip.TooltipBuilder;
import fuzs.consoleexperience.config.JSONConfigUtil;
import fuzs.consoleexperience.util.CommandRegisterer;
import fuzs.puzzleslib.PuzzlesLib;
import fuzs.puzzleslib.element.AbstractElement;
import fuzs.puzzleslib.element.ElementRegistry;
import net.minecraft.command.Commands;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("Convert2MethodRef")
@Mod(ConsoleExperience.MODID)
public class ConsoleExperience {

    public static final String MODID = "consoleexperience";
    public static final String NAME = "Console Experience";
    public static final Logger LOGGER = LogManager.getLogger(ConsoleExperience.NAME);

    private static final ElementRegistry REGISTRY = PuzzlesLib.create(MODID);

    public static final AbstractElement POSITION_DISPLAY = REGISTRY.register("position_display", () -> new PositionDisplayElement(), Dist.CLIENT);
    public static final AbstractElement PLAYER_ANIMATIONS = REGISTRY.register("player_animations", () -> new PlayerAnimationsElement(), Dist.CLIENT);

    private final String jsonConfigName = "helditemtooltips.json";
    private final String jsonTipsName = "tips.json";

    public ConsoleExperience() {

        PuzzlesLib.setup(true, MODID);
        PuzzlesLib.setSideOnly();

        JSONConfigUtil.load(ConsoleExperience.this.jsonConfigName, MODID, TooltipBuilder::serialize, TooltipBuilder::deserialize);
        JSONConfigUtil.load(ConsoleExperience.this.jsonTipsName, MODID, JSONConfigUtil::copyToFile, FancyScreenUtil::deserialize);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ConsoleExperience.this::onClientSetup);
    }

    private void onClientSetup(final FMLClientSetupEvent evt) {

        evt.enqueueWork(() -> MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands));
    }

    private void onRegisterCommands(final RegisterCommandsEvent evt) {

        evt.getDispatcher().register(Commands.literal(ConsoleExperience.MODID).then(Commands.literal("reload").executes(ctx -> {

            CommandRegisterer.handleReload(this.jsonConfigName, "command.reload.config", TooltipBuilder::serialize, TooltipBuilder::deserialize, ctx);
            CommandRegisterer.handleReload(this.jsonTipsName, "command.reload.tips", JSONConfigUtil::copyToFile, FancyScreenUtil::deserialize, ctx);

            return Command.SINGLE_SUCCESS;
        })));
    }

}
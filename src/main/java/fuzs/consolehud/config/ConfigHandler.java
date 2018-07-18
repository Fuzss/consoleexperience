package fuzs.consolehud.config;

import java.io.File;

import fuzs.consolehud.ConsoleHud;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class ConfigHandler {
    public static Configuration config;
    public static String categoryGeneral = "general";
    public static boolean heldItemTooltips;
    public static boolean paperDoll;
    public static int dollPosition;
    public static boolean fireOnDoll;

    public static void init(File configFile) {
        if (config == null) {
            config = new Configuration(configFile);
            loadConfiguration();
        }
    }

    private static void loadConfiguration() {
        config.getCategory(categoryGeneral);
        heldItemTooltips = config.getBoolean("Advanced Held Item Tooltips", categoryGeneral, true, "Enhances vanilla held item tooltips with information about enchantments, potions effects, shulker box contents, and much more.");
        paperDoll = config.getBoolean("Paper Doll", categoryGeneral, true, "Shows a small player model in a configurable corner of the screen while the player is sprinting, sneaking, or flying.");
        dollPosition = config.get(categoryGeneral, "Paper Doll Position", 0, "Defines a screen corner to display the paper doll in. [0: top left, 1: bottom left, 2: top right, 3: bottom right, default: 0]", 0, 3).getInt();
        fireOnDoll = config.getBoolean("Burning Paper Doll", categoryGeneral, false, "Disables flame overlay on the hud when on fire and displays the burning paper doll instead.");

        if (config.hasChanged()) {
            config.save();
        }
    }

    @SubscribeEvent
    public static void onConfigurationChanged(OnConfigChangedEvent event) {
        if (event.getModID().equalsIgnoreCase(ConsoleHud.MODID)) {
            loadConfiguration();
        }
    }
}

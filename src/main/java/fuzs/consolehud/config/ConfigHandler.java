package fuzs.consolehud.config;

import java.io.File;

import fuzs.consolehud.ConsoleHud;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class ConfigHandler {
    public static Configuration config;
    public static String categoryGeneral = "general";
    public static boolean heldItemTooltips;
    public static int heldItemTooltipsRows;
    public static boolean heldItemTooltipsModded;
    public static boolean paperDoll;
    public static int paperDollPosition;
    public static boolean paperDollAlways;
    public static boolean paperDollSprinting;
    public static boolean paperDollCrouching;
    public static boolean paperDollFlying;
    public static boolean paperDollElytraFlying;
    public static boolean paperDollBurning;
    public static boolean paperDollMounting;

    public static void init(File configFile) {
        if (config == null) {
            config = new Configuration(configFile);
            loadConfiguration();
        }
    }

    private static void loadConfiguration() {
        config.getCategory(categoryGeneral);
        heldItemTooltips = config.getBoolean("Advanced Held Item Tooltips", categoryGeneral, true, "Enhances vanilla held item tooltips with information about enchantments, potions effects, shulker box contents, and more.");
        heldItemTooltipsRows = config.getInt("Advanced Held Item Tooltips Rows", categoryGeneral, 5, 2, 7, "Maximum amount of rows to be displayed for held item tooltips.");
        heldItemTooltipsModded = config.getBoolean("Advanced Held Item Tooltips Modded", categoryGeneral, false, "Enables tooltip information added by other mods to be displayed as held item tooltips.");
        paperDoll = config.getBoolean("Paper Doll", categoryGeneral, true, "Shows a small player model in a configurable corner of the screen while the player is sprinting, sneaking, or flying.");
        paperDollPosition = config.get(categoryGeneral, "Paper Doll Position", 0, "Defines a screen corner to display the paper doll in. [0: top left, 1: bottom left, 2: top right, 3: bottom right, default: 0]", 0, 3).getInt();
        paperDollAlways = config.getBoolean("Paper Doll Always", categoryGeneral, false, "Always displays the paper doll, no matter what action the player is performing.");
        paperDollSprinting = config.getBoolean("Paper Doll Sprinting", categoryGeneral, true, "Enables the paper doll while the player is sprinting.");
        paperDollCrouching = config.getBoolean("Paper Doll Crouching", categoryGeneral, true, "Enables the paper doll while the player is crouching.");
        paperDollFlying = config.getBoolean("Paper Doll Flying", categoryGeneral, true, "Displays the paper doll when the player is using creative mode flight.");
        paperDollElytraFlying = config.getBoolean("Paper Doll Elytra Flying", categoryGeneral, true, "Shows the paper doll while the player is flying with an elytra.");
        paperDollBurning = config.getBoolean("Paper Doll Burning", categoryGeneral, false, "Disables flame overlay on the hud when on fire and displays the burning paper doll instead.");
        paperDollMounting = config.getBoolean("Paper Doll Mounting", categoryGeneral, false, "Shows the paper doll while the player is riding any entity.");

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

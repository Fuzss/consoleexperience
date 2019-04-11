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
    public static int heldItemTooltipsXOffset;
    public static int heldItemTooltipsYOffset;
    public static boolean heldItemTooltipsModded;
    public static boolean heldItemTooltipsDots;
    public static boolean paperDoll;
    public static int paperDollPosition;
    public static int paperDollScale;
    public static int paperDollXOffset;
    public static int paperDollYOffset;
    public static boolean paperDollAlways;
    public static boolean paperDollSprinting;
    public static boolean paperDollCrouching;
    public static boolean paperDollFlying;
    public static boolean paperDollElytraFlying;
    public static boolean paperDollBurning;
    public static boolean paperDollRiding;

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
        heldItemTooltipsXOffset = config.getInt("Advanced Held Item Tooltips X-Offset", categoryGeneral, 0, -2147483647, 2147483647, "Offset on x-axis from screen center.");
        heldItemTooltipsYOffset = config.getInt("Advanced Held Item Tooltips Y-Offset", categoryGeneral, 59, 0, 2147483647, "Offset on y-axis from screen bottom.");
        heldItemTooltipsModded = config.getBoolean("Advanced Held Item Tooltips Modded", categoryGeneral, false, "Enables tooltip information added by other mods to be displayed as held item tooltips.");
        heldItemTooltipsDots = config.getBoolean("Advanced Held Item Tooltips Dots", categoryGeneral, false, "Show three dots when the complete tooltip information can't be displayed like on Console Edition instead of the custom text.");
        paperDoll = config.getBoolean("Paper Doll", categoryGeneral, true, "Shows a small player model in a configurable corner of the screen while the player is sprinting, sneaking, or flying.");
        paperDollPosition = config.get(categoryGeneral, "Paper Doll Position Preset", 0, "Defines a screen corner to display the paper doll in. [0: top left, 1: bottom left, 2: top right, 3: bottom right, default: 0]", 0, 3).getInt();
        paperDollScale = config.getInt("Paper Doll Scale", categoryGeneral, 4, 1, 24, "Scale of the paper doll. This is additionally adjusted by the GUI Scale option in Video Settings.");
        paperDollXOffset = config.getInt("Paper Doll X-Offset", categoryGeneral, 0, -2147483647, 2147483647, "Offset on x-axis from original doll position.");
        paperDollYOffset = config.getInt("Paper Doll Y-Offset", categoryGeneral, 0, -2147483647, 2147483647, "Offset on y-axis from original doll position.");
        paperDollAlways = config.getBoolean("Paper Doll Always", categoryGeneral, false, "Always displays the paper doll, no matter what action the player is performing.");
        paperDollSprinting = config.getBoolean("Paper Doll Sprinting", categoryGeneral, true, "Enables the paper doll while the player is sprinting.");
        paperDollCrouching = config.getBoolean("Paper Doll Crouching", categoryGeneral, true, "Enables the paper doll while the player is crouching.");
        paperDollFlying = config.getBoolean("Paper Doll Flying", categoryGeneral, true, "Displays the paper doll when the player is using creative mode flight.");
        paperDollElytraFlying = config.getBoolean("Paper Doll Elytra Flying", categoryGeneral, true, "Shows the paper doll while the player is flying with an elytra.");
        paperDollBurning = config.getBoolean("Paper Doll Burning", categoryGeneral, false, "Disables flame overlay on the hud when on fire and displays the burning paper doll instead.");
        paperDollRiding = config.getBoolean("Paper Doll Riding", categoryGeneral, false, "Shows the paper doll while the player is riding any entity.");

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

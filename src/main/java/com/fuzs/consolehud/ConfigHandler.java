package com.fuzs.consolehud;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = ConsoleHud.MODID)
@Mod.EventBusSubscriber
public class ConfigHandler {

	@Config.Name("Advanced Held Item Tooltips")
	@Config.Comment("Enhances vanilla held item tooltips with information about enchantments, potions effects, shulker box contents, and more.")
	public static boolean heldItemTooltips = true;

	@Config.Name("Advanced Held Item Tooltips Blacklist")
	@Config.Comment("Disables held item tooltips for specified items and mods, mainly to prevent custom tooltips from overlapping.")
	public static String[] heldItemTooltipsBlacklist = new String[] { "psi:cad", "psi:psimetal_shovel", "psi:psimetal_pickaxe", "psi:psimetal_axe", "psi:psimetal_exosuit_helmet", "psi:psimetal_exosuit_chestplate", "psi:psimetal_exosuit_leggings", "psi:psimetal_exosuit_boots" };

	@Config.Name("Advanced Held Item Tooltips Rows")
	@Config.Comment("Maximum amount of rows to be displayed for held item tooltips.")
	@Config.RangeInt(min = 2, max = 7)
	public static int heldItemTooltipsRows = 5;

	@Config.Name("Advanced Held Item Tooltips X-Offset")
	@Config.Comment("Offset on x-axis from screen center.")
	@Config.RangeInt()
	public static int heldItemTooltipsXOffset = 0;

	@Config.Name("Advanced Held Item Tooltips Y-Offset")
	@Config.Comment("Offset on y-axis from screen center.")
	@Config.RangeInt(min = 0)
	public static int heldItemTooltipsYOffset = 59;

	@Config.Name("Advanced Held Item Tooltips Modded")
	@Config.Comment("Enables tooltip information added by other mods to be displayed as held item tooltips.")
	public static boolean heldItemTooltipsModded = false;

	@Config.Name("Advanced Held Item Tooltips Dots")
	@Config.Comment("Show three dots when the complete tooltip information can't be displayed like on Console Edition instead of the custom text.")
	public static boolean heldItemTooltipsDots = false;

	@Config.Name("Paper Doll")
	@Config.Comment("Shows a small player model in a configurable corner of the screen while the player is sprinting, sneaking, or flying.")
	public static boolean paperDoll = true;

	@Config.Name("Paper Doll Position Preset")
	@Config.Comment("Defines a screen corner to display the paper doll in. [0: top left, 1: bottom left, 2: top right, 3: bottom right, default: 0]")
	@Config.RangeInt(min = 0, max = 3)
	public static int paperDollPosition = 0;

	@Config.Name("Paper Doll Scale")
	@Config.Comment("Scale of the paper doll. This is additionally adjusted by the GUI Scale option in Video Settings.")
	@Config.RangeInt(min = 1, max = 24)
	public static int paperDollScale = 4;

	@Config.Name("Paper Doll X-Offset")
	@Config.Comment("Offset on x-axis from original doll position.")
	@Config.RangeInt()
	public static int paperDollXOffset = 0;

	@Config.Name("Paper Doll Y-Offset")
	@Config.Comment("Offset on y-axis from original doll position.")
	@Config.RangeInt()
	public static int paperDollYOffset = 0;

	@Config.Name("Paper Doll Always")
	@Config.Comment("Always displays the paper doll, no matter what action the player is performing.")
	public static boolean paperDollAlways = false;

	@Config.Name("Paper Doll Sprinting")
	@Config.Comment("Enables the paper doll while the player is sprinting.")
	public static boolean paperDollSprinting = true;

	@Config.Name("Paper Doll Crouching")
	@Config.Comment("Enables the paper doll while the player is crouching.")
	public static boolean paperDollCrouching = true;

	@Config.Name("Paper Doll Flying")
	@Config.Comment("Displays the paper doll when the player is using creative mode flight.")
	public static boolean paperDollFlying = true;

	@Config.Name("Paper Doll Elytra Flying")
	@Config.Comment("Shows the paper doll while the player is flying with an elytra.")
	public static boolean paperDollElytraFlying = true;

	@Config.Name("Paper Doll Burning")
	@Config.Comment("Disables flame overlay on the hud when on fire and displays the burning paper doll instead.")
	public static boolean paperDollBurning = false;

	@Config.Name("Paper Doll Riding")
	@Config.Comment("Shows the paper doll while the player is riding any entity.")
	public static boolean paperDollRiding = false;

	@SubscribeEvent
	public static void configChanged(ConfigChangedEvent.OnConfigChangedEvent evt) {
		if (evt.getModID().equals(ConsoleHud.MODID)) {
			ConfigManager.sync(ConsoleHud.MODID, Type.INSTANCE);
		}
	}
	
}

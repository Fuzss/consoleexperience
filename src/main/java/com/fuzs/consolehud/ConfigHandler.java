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

	@Config.Name("helditemtooltips")
	public static SelectedItemConfig heldItemTooltipsConfig = new SelectedItemConfig();

	@Config.Name("paperdoll")
	public static PaperDollConfig paperDollConfig = new PaperDollConfig();

	@Config.Name("hoveringhotbar")
	public static HoveringHotbarConfig hoveringHotbarConfig = new HoveringHotbarConfig();

	@Config.Name("Held Item Tooltips")
	@Config.Comment("Enhances vanilla held item tooltips with information about enchantments, potions effects, shulker box contents, and more.")
	public static boolean heldItemTooltips = true;

	@Config.Name("Paper Doll")
	@Config.Comment("Shows a small player model in a configurable corner of the screen while the player is sprinting, sneaking, or flying.")
	public static boolean paperDoll = true;

	@Config.Name("Hovering Hotbar")
	@Config.Comment("Enables the hotbar to hover anywhere on the screen. By default just moves it up a little from the screen bottom.")
	public static boolean hoveringHotbar = true;

	public static class SelectedItemConfig {

		@Config.Name("Blacklist")
		@Config.Comment("Disables held item tooltips for specified items and mods, mainly to prevent custom tooltips from overlapping.")
		public String[] blacklist = new String[]{"psi:cad", "psi:psimetal_shovel", "psi:psimetal_pickaxe", "psi:psimetal_axe", "psi:psimetal_exosuit_helmet", "psi:psimetal_exosuit_chestplate", "psi:psimetal_exosuit_leggings", "psi:psimetal_exosuit_boots"};

		@Config.Name("Rows")
		@Config.Comment("Maximum amount of rows to be displayed for held item tooltips.")
		@Config.RangeInt(min = 2, max = 7)
		public int rows = 5;

		@Config.Name("X-Offset")
		@Config.Comment("Offset on x-axis from screen center.")
		@Config.RangeInt()
		public int xOffset = 0;

		@Config.Name("Y-Offset")
		@Config.Comment("Offset on y-axis from screen bottom.")
		@Config.RangeInt(min = 0)
		public int yOffset = 59;

		@Config.Name("Modded")
		@Config.Comment("Enables tooltip information added by other mods like Hwyla to be displayed as held item tooltips.")
		public boolean modded = false;

		@Config.Name("Dots")
		@Config.Comment("Show three dots when the complete tooltip information can't be displayed like on Console Edition instead of the custom text.")
		public boolean dots = false;

	}

	public static class PaperDollConfig {

		@Config.Name("Position Preset")
		@Config.Comment("Defines a screen corner to display the paper doll in. [0: top left, 1: bottom left, 2: top right, 3: bottom right, default: 0]")
		@Config.RangeInt(min = 0, max = 3)
		public int position = 0;

		@Config.Name("Scale")
		@Config.Comment("Scale of the paper doll. This is additionally adjusted by the GUI Scale option in Video Settings.")
		@Config.RangeInt(min = 1, max = 24)
		public int scale = 4;

		@Config.Name("X-Offset")
		@Config.Comment("Offset on x-axis from original doll position.")
		@Config.RangeInt()
		public int xOffset = 0;

		@Config.Name("Y-Offset")
		@Config.Comment("Offset on y-axis from original doll position.")
		@Config.RangeInt()
		public int yOffset = 0;

		@Config.Name("Always")
		@Config.Comment("Always displays the paper doll, no matter what action the player is performing.")
		public boolean always = false;

		@Config.Name("Sprinting")
		@Config.Comment("Enables the paper doll while the player is sprinting.")
		public boolean sprinting = true;

		@Config.Name("Crouching")
		@Config.Comment("Enables the paper doll while the player is crouching.")
		public boolean crouching = true;

		@Config.Name("Flying")
		@Config.Comment("Displays the paper doll when the player is using creative mode flight.")
		public boolean flying = true;

		@Config.Name("Elytra Flying")
		@Config.Comment("Shows the paper doll while the player is flying with an elytra.")
		public boolean elytraFlying = true;

		@Config.Name("Burning")
		@Config.Comment("Disables flame overlay on the hud when on fire and displays the burning paper doll instead.")
		public boolean burning = false;

		@Config.Name("Riding")
		@Config.Comment("Shows the paper doll while the player is riding any entity.")
		public boolean riding = false;

	}

	public static class HoveringHotbarConfig {

		@Config.Name("X-Offset")
		@Config.Comment("Offset on x-axis from screen center.")
		@Config.RangeInt()
		public int xOffset = 0;

		@Config.Name("Y-Offset")
		@Config.Comment("Offset on y-axis from screen bottom.")
		@Config.RangeInt(min = 0)
		public int yOffset = 18;

		@Config.Name("Mod Compatibility")
		@Config.Comment("Attempt to be compatible with dysfunctional mods. Only enable when you experience problems like overlapping.")
		public boolean modCompat = false;

	}

	@SubscribeEvent
	public static void configChanged(ConfigChangedEvent.OnConfigChangedEvent evt) {
		if (evt.getModID().equals(ConsoleHud.MODID)) {
			ConfigManager.sync(ConsoleHud.MODID, Type.INSTANCE);
		}
	}
	
}

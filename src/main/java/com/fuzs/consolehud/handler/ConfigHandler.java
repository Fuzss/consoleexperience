package com.fuzs.consolehud.handler;

import com.fuzs.consolehud.ConsoleHud;
import com.fuzs.consolehud.util.EnumPositionPreset;
import com.fuzs.consolehud.util.EnumTextColor;
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

	@Config.Name("saveicon")
	public static SaveIconConfig saveIconConfig = new SaveIconConfig();

	@Config.Name("Held Item Tooltips")
	@Config.Comment("Enhances vanilla held item tooltips with information about enchantments, potions effects, shulker box contents and more.")
	public static boolean heldItemTooltips = true;

	@Config.Name("Paper Doll")
	@Config.Comment("Show a small player model in a configurable corner of the screen while the player is performing certain actions like sprinting, sneaking, or flying.")
	public static boolean paperDoll = true;

	@Config.Name("Hovering Hotbar")
	@Config.Comment("Enable the hotbar to hover anywhere on the screen. By default just moves it up a little from the screen bottom.")
	public static boolean hoveringHotbar = true;

	@Config.Name("Save Icon")
	@Config.Comment("Show an animated icon on the screen whenever the world is being saved (every 45 seconds by default). This only works in singleplayer.")
	public static boolean saveIcon = true;

	public static class SelectedItemConfig {

		@Config.Name("appearance")
		public AppearanceConfig appearanceConfig = new AppearanceConfig();

		@Config.Name("Blacklist")
		@Config.Comment("Disables held item tooltips for specified items and mods, mainly to prevent custom tooltips from overlapping.")
		public String[] blacklist = new String[]{"psi:cad", "psi:psimetal_shovel", "psi:psimetal_pickaxe", "psi:psimetal_axe", "psi:psimetal_exosuit_helmet", "psi:psimetal_exosuit_chestplate", "psi:psimetal_exosuit_leggings", "psi:psimetal_exosuit_boots"};

		@Config.Name("Rows")
		@Config.Comment("Maximum amount of rows to be displayed for held item tooltips.")
		@Config.RangeInt(min = 0, max = 9)
		@Config.SlidingOption
		public int rows = 4;

		@Config.Name("Display Time")
		@Config.Comment("Amount of ticks the held item tooltip will be displayed for.")
		@Config.RangeInt(min = 0)
		public int displayTime = 40;

		@Config.Name("X-Offset")
		@Config.Comment("Offset on x-axis from screen center.")
		@Config.RangeInt()
		public int xOffset = 0;

		@Config.Name("Y-Offset")
		@Config.Comment("Offset on y-axis from screen bottom.")
		@Config.RangeInt(min = 0)
		public int yOffset = 59;

		@Config.Name("Cache Tooltip")
		@Config.Comment("Cache the tooltip so it doesn't have to be remade every tick. This will prevent it from updating stats like durability while it is displayed.")
		public boolean cacheTooltip = true;

		@Config.Name("Text Color")
		@Config.Comment("Default text color. Only applied when the text doesn't already have a color assigned internally.")
		public EnumTextColor textColor = EnumTextColor.SILVER;

		public class AppearanceConfig {

			@Config.Name("Show Modded Tooltips")
			@Config.Comment("Enables tooltip information added by other mods like Hwyla to be displayed as a held item tooltip.")
			public boolean moddedTooltips = false;

			@Config.Name("Show Durability")
			@Config.Comment("Displays the item's durability as part of its held item tooltip.")
			public boolean showDurability = true;

			@Config.Name("Force Durability")
			@Config.Comment("Force the durability to always be on the tooltip. \"Show Durability\" has to be enabled for this to have any effect.")
			public boolean forceDurability = true;

			@Config.Name("Show Last Line")
			@Config.Comment("Show how many more lines there are that currently don't fit the tooltip.")
			public boolean showLastLine = true;

			@Config.Name("Sum Shulker Box Contents")
			@Config.Comment("Sum up stacks of equal items in a shulker box. Only affects the inventory tooltip, held item tooltips always use this.")
			public boolean sumShulkerBox = true;

			@Config.Name("Last Line Format")
			@Config.Comment("Define a custom format to be used for the last line of a tooltip when there are more lines than there is space. Leave this blank for the default, translatable string. Use %s (up to one time) in your custom format to include the amount of cut off lines.")
			public String lastLineFormat = "";

			@Config.Name("Durability Format")
			@Config.Comment("Define a custom format to be used for the durability line. Leave this blank for the default, translatable string. Use %s (up to two times) to include remaining uses and total uses in your custom format. \"Show Durability\" has to be enabled for this to have any effect.")
			public String durabilityFormat = "";

		}

	}

	public static class PaperDollConfig {

		@Config.Name("displayactions")
		public DisplayActionsConfig displayActionsConfig = new DisplayActionsConfig();

		@Config.Name("Screen Corner")
		@Config.Comment("Define a screen corner to display the paper doll in.")
		public EnumPositionPreset position = EnumPositionPreset.TOP_LEFT;

		@Config.Name("Scale")
		@Config.Comment("Scale of the paper doll. This is additionally adjusted by the GUI Scale option in Video Settings.")
		@Config.RangeInt(min = 1, max = 24)
		@Config.SlidingOption
		public int scale = 4;

		@Config.Name("X-Offset")
		@Config.Comment("Offset on x-axis from original doll position.")
		@Config.RangeInt()
		public int xOffset = 0;

		@Config.Name("Y-Offset")
		@Config.Comment("Offset on y-axis from original doll position.")
		@Config.RangeInt()
		public int yOffset = 0;

		@Config.Name("Display Time")
		@Config.Comment("Amount of ticks the paper doll will be kept on screen after its display conditions are no longer met. Obviously has no effect when the doll is always displayed.")
		@Config.RangeInt(min = 0)
		public int displayTime = 12;

		@Config.Name("Fix Rotation")
		@Config.Comment("Disable the paper doll from being slightly rotated every so often depending on the player rotation.")
		public boolean blockRotation = false;

		@Config.Name("Potion Shift")
		@Config.Comment("Shift the paper doll downwards when it would otherwise overlap with the potion icons. Only applicable when the \"Screen Corner\" is set to \"topright\".")
		public boolean potionShift = true;

		@Config.Name("Burning Doll")
		@Config.Comment("Disable flame overlay on the hud when on fire and display the burning paper doll instead.")
		public boolean burning = false;

		public class DisplayActionsConfig {

			@Config.Name("Always")
			@Config.Comment("Always display the paper doll, no matter what action the player is performing.")
			public boolean always = false;

			@Config.Name("Sprinting")
			@Config.Comment("Enable the paper doll while the player is sprinting.")
			public boolean sprinting = true;

			@Config.Name("Crouching")
			@Config.Comment("Enable the paper doll while the player is crouching.")
			public boolean crouching = true;

			@Config.Name("Flying")
			@Config.Comment("Display the paper doll when the player is using creative mode flight.")
			public boolean flying = true;

			@Config.Name("Elytra Flying")
			@Config.Comment("Show the paper doll while the player is flying with an elytra.")
			public boolean elytraFlying = true;

			@Config.Name("Riding")
			@Config.Comment("Show the paper doll while the player is riding any entity.")
			public boolean riding = false;

			@Config.Name("Hurt")
			@Config.Comment("Show the paper doll when the player is hurt.")
			public boolean hurt = false;

		}

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
		@Config.Comment("Attempt to be compatible with dysfunctional mods. Only enable this when modded hud elements aren't shifted together with the hotbar when they should be.")
		public boolean modCompat = false;

	}

	public static class SaveIconConfig {

		@Config.Name("X-Offset")
		@Config.Comment("Offset on x-axis from screen border.")
		@Config.RangeInt()
		public int xOffset = 17;

		@Config.Name("Y-Offset")
		@Config.Comment("Offset on y-axis from screen border.")
		@Config.RangeInt()
		public int yOffset = 15;

		@Config.Name("Screen Corner")
		@Config.Comment("Define a screen corner to display the save icon in.")
		public EnumPositionPreset position = EnumPositionPreset.TOP_RIGHT;

		@Config.Name("Display Time")
		@Config.Comment("Amount of ticks the save icon will be displayed for.")
		@Config.RangeInt(min = 0)
		public int displayTime = 40;

		@Config.Name("Potion Shift")
		@Config.Comment("Shift the save icon downwards when it would otherwise overlap with the potion icons. Only applicable when the \"Screen Corner\" is set to \"topright\".")
		public boolean potionShift = true;

		@Config.Name("Show Arrow")
		@Config.Comment("Show a downwards pointing, animated arrow above the save icon.")
		public boolean showArrow = true;

		@Config.Name("Rotating Model")
		@Config.Comment("Use an animated chest model instead of the static texture.")
		public boolean rotatingModel = true;

	}

	@SubscribeEvent
	public static void configChanged(ConfigChangedEvent.OnConfigChangedEvent evt) {
		if (evt.getModID().equals(ConsoleHud.MODID)) {
			ConfigManager.sync(ConsoleHud.MODID, Type.INSTANCE);
		}
	}
	
}

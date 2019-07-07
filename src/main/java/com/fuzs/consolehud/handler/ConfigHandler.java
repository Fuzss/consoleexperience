package com.fuzs.consolehud.handler;

import com.fuzs.consolehud.util.EnumPositionPreset;
import com.fuzs.consolehud.util.EnumTextColor;
import net.minecraftforge.common.ForgeConfigSpec;

@SuppressWarnings("WeakerAccess")
public class ConfigHandler {

	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

	public static final GeneralConfig GENERAL_CONFIG = new GeneralConfig("general");
	public static final SelectedItemConfig HELD_ITEM_TOOLTIPS_CONFIG = new SelectedItemConfig("helditemtooltips");
	public static final PaperDollConfig PAPER_DOLL_CONFIG = new PaperDollConfig("paperdoll");
	public static final HoveringHotbarConfig HOVERING_HOTBAR_CONFIG = new HoveringHotbarConfig("hoveringhotbar");
	public static final SaveIconConfig SAVE_ICON_CONFIG = new SaveIconConfig("saveicon");

	public static class GeneralConfig {

		public final ForgeConfigSpec.BooleanValue heldItemTooltips;
		public final ForgeConfigSpec.BooleanValue paperDoll;
		public final ForgeConfigSpec.BooleanValue hoveringHotbar;
		public final ForgeConfigSpec.BooleanValue saveIcon;
		public final ForgeConfigSpec.BooleanValue sumShulkerBox;

		private GeneralConfig(String name) {

			BUILDER.push(name);

			this.heldItemTooltips = ConfigHandler.BUILDER.comment("Enhances vanilla held item tooltips with information about enchantments, potions effects, shulker box contents and more.").define("Held Item Tooltips", true);
			this.paperDoll = ConfigHandler.BUILDER.comment("Show a small player model in a configurable corner of the screen while the player is performing certain actions like sprinting, sneaking, or flying.").define("Paper Doll", true);
			this.hoveringHotbar = ConfigHandler.BUILDER.comment("Enable the hotbar to hover anywhere on the screen. By default just moves it up a little from the screen bottom.").define("Hovering Hotbar", true);
			this.saveIcon = ConfigHandler.BUILDER.comment("Show an animated icon on the screen whenever the world is being saved (every 45 seconds by default). This only works in singleplayer.").define("Save Icon", true);
			this.sumShulkerBox = ConfigHandler.BUILDER.comment("Sum up stacks of equal items for the shulker box tooltip.").define("Sum Shulker Box Contents", true);

			BUILDER.pop();

		}

	}

	public static class SelectedItemConfig {

		public final AppearanceConfig appearanceConfig;
		public final ForgeConfigSpec.IntValue rows;
		public final ForgeConfigSpec.IntValue displayTime;
		public final ForgeConfigSpec.IntValue xOffset;
		public final ForgeConfigSpec.IntValue yOffset;
		public final ForgeConfigSpec.BooleanValue cacheTooltip;
		public final ForgeConfigSpec.EnumValue<EnumTextColor> textColor;

		private SelectedItemConfig(String name) {

			BUILDER.push(name);

			this.rows = ConfigHandler.BUILDER.comment("Maximum amount of rows to be displayed for held item tooltips.").defineInRange("Rows", 4, 0, 9);
			this.displayTime = ConfigHandler.BUILDER.comment("Amount of ticks the held item tooltip will be displayed for.").defineInRange("Display Time", 40, 0, Integer.MAX_VALUE);
			this.xOffset = ConfigHandler.BUILDER.comment("Offset on x-axis from screen center.").defineInRange("X-Offset", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
			this.yOffset = ConfigHandler.BUILDER.comment("Offset on y-axis from screen bottom.").defineInRange("Y-Offset", 59, 0, Integer.MAX_VALUE);
			this.cacheTooltip = ConfigHandler.BUILDER.comment("Cache the tooltip so it doesn't have to be remade every tick. This will prevent it from updating stats like durability while it is displayed.").define("Cache Tooltip", true);
			this.textColor = ConfigHandler.BUILDER.comment("Text Color").defineEnum("Text Color", EnumTextColor.SILVER);

			BUILDER.pop();

			this.appearanceConfig = new AppearanceConfig(name + "_appearance");

		}

		public class AppearanceConfig {

			public final ForgeConfigSpec.BooleanValue moddedTooltips;
			public final ForgeConfigSpec.BooleanValue showDurability;
			public final ForgeConfigSpec.BooleanValue forceDurability;
			public final ForgeConfigSpec.BooleanValue showLastLine;
			public final ForgeConfigSpec.ConfigValue<String> lastLineFormat;
			public final ForgeConfigSpec.ConfigValue<String> durabilityFormat;

			private AppearanceConfig(String name) {

				BUILDER.push(name);

				this.moddedTooltips = ConfigHandler.BUILDER.comment("Enables tooltip information added by other mods like Hwyla to be displayed as a held item tooltip.").define("Show Modded Tooltips", false);
				this.showDurability = ConfigHandler.BUILDER.comment("Displays the item's durability as part of its held item tooltip.").define("Show Durability", true);
				this.forceDurability = ConfigHandler.BUILDER.comment("Force the durability to always be on the tooltip. \"Show Durability\" has to be enabled for this to have any effect.").define("Force Durability", true);
				this.showLastLine = ConfigHandler.BUILDER.comment("Show how many more lines there are that currently don't fit the tooltip.").define("Show Last Line", true);
				this.lastLineFormat = ConfigHandler.BUILDER.comment("Define a custom format to be used for the last line of a tooltip when there are more lines than there is space. Leave this blank for the default, translatable string. Use %s (up to one time) in your custom format to include the amount of cut off lines.").define("Last Line Format", "");
				this.durabilityFormat = ConfigHandler.BUILDER.comment("Define a custom format to be used for the durability line. Leave this blank for the default, translatable string. Use %s (up to two times) to include remaining uses and total uses in your custom format. \"Show Durability\" has to be enabled for this to have any effect.").define("Durability Format", "");

				BUILDER.pop();

			}

		}

	}

	public static class PaperDollConfig {

		public final DisplayActionsConfig displayActionsConfig;
		public final ForgeConfigSpec.EnumValue<EnumPositionPreset> position;
		public final ForgeConfigSpec.IntValue scale;
		public final ForgeConfigSpec.IntValue xOffset;
		public final ForgeConfigSpec.IntValue yOffset;
		public final ForgeConfigSpec.IntValue displayTime;
		public final ForgeConfigSpec.BooleanValue blockRotation;
		public final ForgeConfigSpec.BooleanValue potionShift;
		public final ForgeConfigSpec.BooleanValue burning;
		public final ForgeConfigSpec.BooleanValue firstPerson;

		private PaperDollConfig(String name) {

			BUILDER.push(name);

			this.scale = ConfigHandler.BUILDER.comment("Scale of the paper doll. This is additionally adjusted by the GUI Scale option in Video Settings.").defineInRange("Scale", 4, 1, 24);
			this.xOffset = ConfigHandler.BUILDER.comment("Offset on x-axis from original doll position.").defineInRange("X-Offset", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
			this.yOffset = ConfigHandler.BUILDER.comment("Offset on y-axis from original doll position.").defineInRange("Y-Offset", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
			this.displayTime = ConfigHandler.BUILDER.comment("Amount of ticks the paper doll will be kept on screen after its display conditions are no longer met. Obviously has no effect when the doll is always displayed.").defineInRange("Display Time", 12, 0, Integer.MAX_VALUE);
			this.position = ConfigHandler.BUILDER.comment("Define a screen corner to display the paper doll in.").defineEnum("Screen Corner", EnumPositionPreset.TOP_LEFT);
			this.blockRotation = ConfigHandler.BUILDER.comment("Disable the paper doll from being slightly rotated every so often depending on the player rotation.").define("Fix Rotation", false);
			this.potionShift = ConfigHandler.BUILDER.comment("Shift the paper doll downwards when it would otherwise overlap with the potion icons. Only applicable when the \"Screen Corner\" is set to \"topright\".").define("Potion Shift", true);
			this.burning = ConfigHandler.BUILDER.comment("Disable flame overlay on the hud when on fire and display the burning paper doll instead.").define("Burning Doll", false);
			this.firstPerson = ConfigHandler.BUILDER.comment("Only show the paper doll when in first person mode.").define("First Person Only", true);
			
			BUILDER.pop();

			this.displayActionsConfig = new DisplayActionsConfig(name + "_displayactions");

		}

		public class DisplayActionsConfig {

			public final ForgeConfigSpec.BooleanValue always;
			public final ForgeConfigSpec.BooleanValue sprinting;
			public final ForgeConfigSpec.BooleanValue swimming;
			public final ForgeConfigSpec.BooleanValue crouching;
			public final ForgeConfigSpec.BooleanValue flying;
			public final ForgeConfigSpec.BooleanValue elytraFlying;
			public final ForgeConfigSpec.BooleanValue riding;
			public final ForgeConfigSpec.BooleanValue hurt;

			private DisplayActionsConfig(String name) {

				BUILDER.push(name);

				this.always = ConfigHandler.BUILDER.comment("Always display the paper doll, no matter what action the player is performing.").define("Always", false);
				this.sprinting = ConfigHandler.BUILDER.comment("Enable the paper doll while the player is sprinting.").define("Sprinting", true);
				this.swimming = ConfigHandler.BUILDER.comment("Enable the paper doll while the player is swimming.").define("Swimming", true);
				this.crouching = ConfigHandler.BUILDER.comment("Enable the paper doll while the player is crouching.").define("Crouching", true);
				this.flying = ConfigHandler.BUILDER.comment("Display the paper doll when the player is using creative mode flight.").define("Flying", true);
				this.elytraFlying = ConfigHandler.BUILDER.comment("Show the paper doll while the player is flying with an elytra.").define("Elytra Flying", true);
				this.riding = ConfigHandler.BUILDER.comment("Show the paper doll while the player is riding any entity.").define("Riding", false);
				this.hurt = ConfigHandler.BUILDER.comment("Show the paper doll when the player is hurt.").define("Hurt", false);

				BUILDER.pop();

			}

		}

	}

	public static class HoveringHotbarConfig {

		public final ForgeConfigSpec.IntValue xOffset;
		public final ForgeConfigSpec.IntValue yOffset;
		public final ForgeConfigSpec.BooleanValue modCompat;

		private HoveringHotbarConfig(String name) {

			BUILDER.push(name);

			this.xOffset = ConfigHandler.BUILDER.comment("Offset on x-axis from screen center.").defineInRange("X-Offset", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
			this.yOffset = ConfigHandler.BUILDER.comment("Offset on y-axis from screen center.").defineInRange("Y-Offset", 18, 0, Integer.MAX_VALUE);
			this.modCompat = ConfigHandler.BUILDER.comment("Attempt to be compatible with dysfunctional mods. Only enable this when modded hud elements aren't shifted together with the hotbar when they should be.").define("Mod Compatibility", false);

			BUILDER.pop();

		}

	}

	public static class SaveIconConfig {

		public final ForgeConfigSpec.IntValue xOffset;
		public final ForgeConfigSpec.IntValue yOffset;
		public final ForgeConfigSpec.EnumValue<EnumPositionPreset> position;
		public final ForgeConfigSpec.IntValue displayTime;
		public final ForgeConfigSpec.BooleanValue potionShift;
		public final ForgeConfigSpec.BooleanValue showArrow;
		public final ForgeConfigSpec.BooleanValue rotatingModel;

		private SaveIconConfig(String name) {

			BUILDER.push(name);

			this.xOffset = ConfigHandler.BUILDER.comment("Offset on x-axis from screen border.").defineInRange("X-Offset", 17, Integer.MIN_VALUE, Integer.MAX_VALUE);
			this.yOffset = ConfigHandler.BUILDER.comment("Offset on y-axis from screen border.").defineInRange("Y-Offset", 15, Integer.MIN_VALUE, Integer.MAX_VALUE);
			this.position = ConfigHandler.BUILDER.comment("Define a screen corner to display the save icon in.").defineEnum("Screen Corner", EnumPositionPreset.TOP_RIGHT);
			this.displayTime = ConfigHandler.BUILDER.comment("Amount of ticks the save icon will be displayed for.").defineInRange("Display Time", 40, 0, Integer.MAX_VALUE);
			this.potionShift = ConfigHandler.BUILDER.comment("Shift the save icon downwards when it would otherwise overlap with the potion icons. Only applicable when the \"Screen Corner\" is set to \"topright\".").define("Potion Shift", true);
			this.showArrow = ConfigHandler.BUILDER.comment("Show a downwards pointing, animated arrow above the save icon.").define("Show Arrow", true);
			this.rotatingModel = ConfigHandler.BUILDER.comment("Use an animated chest model instead of the static texture.").define("Rotating Model", true);

			BUILDER.pop();

		}

	}

	public static final ForgeConfigSpec SPEC = BUILDER.build();
	
}
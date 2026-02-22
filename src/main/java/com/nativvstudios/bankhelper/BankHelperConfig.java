package com.nativvstudios.bankhelper;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup("bankhelper")
public interface BankHelperConfig extends Config
{
	@ConfigSection(
		name = "Overlay",
		description = "Overlay display settings",
		position = 0
	)
	String overlaySection = "overlay";

	@ConfigSection(
		name = "Colors",
		description = "Customize the heat map gradient colors",
		position = 1
	)
	String colorsSection = "colors";

	@ConfigSection(
		name = "Price Range",
		description = "Customize the price range for the heat map",
		position = 2
	)
	String priceRangeSection = "priceRange";

	@ConfigItem(
		position = 0,
		keyName = "enabled",
		name = "Enabled",
		description = "Enable the bank helper heat map overlay",
		section = overlaySection
	)
	default boolean enabled()
	{
		return true;
	}

	@Range(
		max = 255
	)
	@ConfigItem(
		position = 1,
		keyName = "fillOpacity",
		name = "Fill opacity",
		description = "Configures the opacity of the heat map fill",
		section = overlaySection
	)
	default int fillOpacity()
	{
		return 100;
	}

	@ConfigItem(
		position = 2,
		keyName = "showQuestMarker",
		name = "Mark untradeable items",
		description = "Show an asterisk (*) on untradeable items (quest items, uniques, etc.)",
		section = overlaySection
	)
	default boolean showQuestMarker()
	{
		return true;
	}

	@ConfigItem(
		position = 0,
		keyName = "lowColor",
		name = "Low value color",
		description = "Color for items at or below the low price threshold",
		section = colorsSection
	)
	default Color lowColor()
	{
		return new Color(0, 0, 255);
	}

	@ConfigItem(
		position = 1,
		keyName = "midColor",
		name = "Mid value color",
		description = "Color for items at the mid price threshold",
		section = colorsSection
	)
	default Color midColor()
	{
		return new Color(255, 255, 0);
	}

	@ConfigItem(
		position = 2,
		keyName = "highColor",
		name = "High value color",
		description = "Color for items at or above the high price threshold",
		section = colorsSection
	)
	default Color highColor()
	{
		return new Color(255, 0, 0);
	}

	@ConfigItem(
		position = 3,
		keyName = "untradeableColor",
		name = "Untradeable color",
		description = "Color for untradeable items (quest items, uniques, etc.)",
		section = colorsSection
	)
	default Color untradeableColor()
	{
		return new Color(180, 0, 255);
	}

	@ConfigItem(
		position = 4,
		keyName = "junkLabelColor",
		name = "Junk label color",
		description = "Color of the JUNK text label",
		section = colorsSection
	)
	default Color junkLabelColor()
	{
		return new Color(255, 80, 80);
	}

	@ConfigItem(
		position = 5,
		keyName = "questMarkerColor",
		name = "Untradeable marker color",
		description = "Color of the asterisk (*) on untradeable items",
		section = colorsSection
	)
	default Color questMarkerColor()
	{
		return Color.WHITE;
	}

	@ConfigItem(
		position = 0,
		keyName = "junkThreshold",
		name = "Junk threshold",
		description = "Tradeable item stacks with a total value at or below this are labeled JUNK. Supports k/m/b (e.g. 500, 4k, 1m).",
		section = priceRangeSection
	)
	default String junkThreshold()
	{
		return "100";
	}

	@ConfigItem(
		position = 1,
		keyName = "lowPriceThreshold",
		name = "Low price (blue)",
		description = "Items at or below this price are colored blue (cold). Supports k/m/b (e.g. 0, 1k, 50k).",
		section = priceRangeSection
	)
	default String lowPriceThreshold()
	{
		return "0";
	}

	@ConfigItem(
		position = 2,
		keyName = "midPriceThreshold",
		name = "Mid price (yellow)",
		description = "Items at this price are colored with the mid gradient color. Supports k/m/b (e.g. 100k, 1m).",
		section = priceRangeSection
	)
	default String midPriceThreshold()
	{
		return "100k";
	}

	@ConfigItem(
		position = 3,
		keyName = "highPriceThreshold",
		name = "High price (red)",
		description = "Items at or above this price are colored red (hot). Supports k/m/b (e.g. 1m, 10m, 1b).",
		section = priceRangeSection
	)
	default String highPriceThreshold()
	{
		return "10m";
	}
}

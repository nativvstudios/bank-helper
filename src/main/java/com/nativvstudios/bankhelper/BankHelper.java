package com.nativvstudios.bankhelper;

import com.google.inject.Provides;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Bank Helper",
	description = "Applies a heat map overlay on bank items based on value: red = keep, blue = discard"
)
public class BankHelper extends Plugin
{
	private static final String CONFIG_GROUP = "bankhelper";
	private static final Set<String> PRICE_KEYS = Set.of(
		"junkThreshold", "lowPriceThreshold", "midPriceThreshold", "highPriceThreshold"
	);

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ConfigManager configManager;

	@Inject
	private BankHelperOverlay overlay;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
		overlay.invalidateCache();
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (!CONFIG_GROUP.equals(configChanged.getGroup()))
		{
			return;
		}

		overlay.invalidateCache();

		// Strip newlines from price fields so Enter acts as confirm
		if (PRICE_KEYS.contains(configChanged.getKey()) && configChanged.getNewValue() != null)
		{
			String cleaned = configChanged.getNewValue().replaceAll("[\\r\\n]", "").trim();
			if (!cleaned.equals(configChanged.getNewValue()))
			{
				configManager.setConfiguration(CONFIG_GROUP, configChanged.getKey(), cleaned);
			}
		}
	}

	@Provides
	BankHelperConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BankHelperConfig.class);
	}
}

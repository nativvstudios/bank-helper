package com.nativvstudios.bankhelper;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.text.ParseException;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemComposition;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.ui.overlay.components.TextComponent;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.QuantityFormatter;

@Slf4j
class BankHelperOverlay extends WidgetItemOverlay
{
	private final ItemManager itemManager;
	private final BankHelperConfig config;
	private final Cache<Long, Image> fillCache;

	@Inject
	private BankHelperOverlay(ItemManager itemManager, BankHelperConfig config)
	{
		this.itemManager = itemManager;
		this.config = config;
		showOnBank();
		fillCache = CacheBuilder.newBuilder()
			.concurrencyLevel(1)
			.maximumSize(128)
			.build();
	}

	@Override
	public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem)
	{
		if (!config.enabled())
		{
			return;
		}

		int canonicalId = itemManager.canonicalize(itemId);

		// Skip coins and platinum tokens
		if (canonicalId == ItemID.COINS || canonicalId == ItemID.PLATINUM)
		{
			return;
		}

		ItemComposition composition = itemManager.getItemComposition(canonicalId);
		boolean untradeable = !composition.isTradeable();
		int price = 0;

		Color color;
		if (untradeable)
		{
			color = config.untradeableColor();
		}
		else
		{
			int gePrice = itemManager.getItemPrice(canonicalId);
			int haPrice = composition.getHaPrice();
			price = Math.max(gePrice, haPrice);
			double score = computeScore(price);
			Color midLowColor = interpolate(config.lowColor(), config.midColor(), 0.5);
			Color midHighColor = interpolate(config.midColor(), config.highColor(), 0.5);
			color = scoreToColor(score,
				config.lowColor(), midLowColor, config.midColor(),
				midHighColor, config.highColor());
		}
		Color fillColor = ColorUtil.colorWithAlpha(color, config.fillOpacity());

		int qty = widgetItem.getQuantity();
		long key = (((long) canonicalId) << 32) | qty;
		// Encode color into key to handle opacity changes
		key ^= ((long) fillColor.getRGB()) << 16;

		Image image = fillCache.getIfPresent(key);
		if (image == null)
		{
			image = ImageUtil.fillImage(itemManager.getImage(canonicalId, qty, false), fillColor);
			fillCache.put(key, image);
		}

		Rectangle bounds = widgetItem.getCanvasBounds();
		graphics.drawImage(image, (int) bounds.getX(), (int) bounds.getY(), null);

		graphics.setFont(FontManager.getRunescapeSmallFont());

		// Draw "JUNK" label on worthless tradeable items (based on total stack value)
		long stackValue = (long) price * qty;
		if (!untradeable && stackValue <= parsePrice(config.junkThreshold()))
		{
			final TextComponent junkText = new TextComponent();
			junkText.setPosition(new Point(bounds.x, bounds.y + bounds.height - 1));
			junkText.setText("JUNK");
			junkText.setColor(config.junkLabelColor());
			junkText.setOutline(true);
			junkText.render(graphics);
		}

		// Draw asterisk on untradeable (quest/unique) items
		if (untradeable && config.showQuestMarker())
		{
			final TextComponent textComponent = new TextComponent();
			textComponent.setPosition(new Point(bounds.x + bounds.width - 8, bounds.y + 15));
			textComponent.setText("*");
			textComponent.setColor(config.questMarkerColor());
			textComponent.setOutline(true);
			textComponent.render(graphics);
		}
	}

	private double computeScore(int price)
	{
		long low = parsePrice(config.lowPriceThreshold());
		long mid = parsePrice(config.midPriceThreshold());
		long high = parsePrice(config.highPriceThreshold());

		if (price <= low)
		{
			return 0.0;
		}

		if (price >= high)
		{
			return 1.0;
		}

		// Clamp mid between low and high
		mid = Math.max(low + 1, Math.min(mid, high - 1));

		if (price <= mid)
		{
			// Map low..mid to 0.0..0.5 on a log scale
			double logPrice = Math.log10(Math.max(price, 1));
			double logLow = Math.log10(Math.max(low, 1));
			double logMid = Math.log10(Math.max(mid, 1));
			if (logMid <= logLow)
			{
				return 0.5;
			}
			return 0.5 * (logPrice - logLow) / (logMid - logLow);
		}
		else
		{
			// Map mid..high to 0.5..1.0 on a log scale
			double logPrice = Math.log10(Math.max(price, 1));
			double logMid = Math.log10(Math.max(mid, 1));
			double logHigh = Math.log10(Math.max(high, 1));
			if (logHigh <= logMid)
			{
				return 1.0;
			}
			return 0.5 + 0.5 * (logPrice - logMid) / (logHigh - logMid);
		}
	}

	private static Color scoreToColor(double score, Color c0, Color c25, Color c50, Color c75, Color c100)
	{
		if (score <= 0.25)
		{
			return interpolate(c0, c25, score / 0.25);
		}
		else if (score <= 0.5)
		{
			return interpolate(c25, c50, (score - 0.25) / 0.25);
		}
		else if (score <= 0.75)
		{
			return interpolate(c50, c75, (score - 0.5) / 0.25);
		}
		else
		{
			return interpolate(c75, c100, (score - 0.75) / 0.25);
		}
	}

	private static Color interpolate(Color c1, Color c2, double t)
	{
		int r = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * t);
		int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * t);
		int b = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * t);
		return new Color(r, g, b);
	}

	private static long parsePrice(String input)
	{
		if (input == null || input.trim().isEmpty())
		{
			return 0;
		}

		try
		{
			return QuantityFormatter.parseQuantity(input.trim());
		}
		catch (ParseException e)
		{
			log.debug("Failed to parse price '{}': {}", input, e.getMessage());
			return 0;
		}
	}

	void invalidateCache()
	{
		fillCache.invalidateAll();
	}
}

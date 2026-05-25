/*
* Copyleft © 2024-2026 L2Brproject
* * This file is part of L2Brproject derived from aCis409/RusaCis3.8
* * L2Brproject is free software: you can redistribute it and/or modify it
* under the terms of the GNU General Public License as published by the
* Free Software Foundation, either version 3 of the License.
* * L2Brproject is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* General Public License for more details.
* * You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
* Our main Developers, Dhousefe-L2JBR, Agazes33, Ban-L2jDev, Warman, SrEli.
* Our special thanks, Nattan Felipe, Diego Fonseca, Junin, ColdPlay, Denky, MecBew, Localhost, MundvayneHELLBOY, SonecaL2, Eduardo.SilvaL2J, biLL, xpower, xTech, kakuzo
* as a contribution for the forum L2JBrasil.com
 */
package ext.mods.gameserver.data.manager;

import java.util.HashMap;
import java.util.Map;

import ext.mods.Config;
import ext.mods.commons.config.ExProperties;
import ext.mods.commons.logging.CLogger;

/**
 * @author Dhousefe
 * @version 1.0
 * @since 2026-02-08
 * Administra itens configurados em items.properties que concedem tempo de premium VIP.
 * Os valores são adicionados ao tempo de premium atual do personagem.
 */
public final class ItemPremiumManager
{
	private static final CLogger LOGGER = new CLogger(ItemPremiumManager.class.getName());
	private static final String ITEMS_FILE = Config.CONFIG_PATH.resolve("items.properties").toString();
	private static final String PREFIX = "ItemPremium_";

	private final Map<Integer, PremiumConfig> _itemConfigs = new HashMap<>();

	protected ItemPremiumManager()
	{
		load();
	}

	private void load()
	{
		_itemConfigs.clear();
		try
		{
			final ExProperties props = Config.initProperties(ITEMS_FILE);
			for (final String key : props.stringPropertyNames())
			{
				if (!key.startsWith(PREFIX))
					continue;

				final String idStr = key.substring(PREFIX.length()).trim();
				final int itemId;
				try
				{
					itemId = Integer.parseInt(idStr);
				}
				catch (NumberFormatException e)
				{
					LOGGER.warn("Invalid item ID in items.properties: {}", idStr);
					continue;
				}

				final String value = props.getProperty(key, "0,0,0").trim();
				final String[] parts = value.split("[,;]+");
				final int minutes = parts.length >= 1 ? parseInt(parts[0].trim(), 0) : 0;
				final int hours = parts.length >= 2 ? parseInt(parts[1].trim(), 0) : 0;
				final int days = parts.length >= 3 ? parseInt(parts[2].trim(), 0) : 0;

				if (minutes > 0 || hours > 0 || days > 0)
					_itemConfigs.put(itemId, new PremiumConfig(minutes, hours, days));
			}
			LOGGER.info("ItemPremiumManager: loaded {} item configurations.", _itemConfigs.size());
		}
		catch (Exception e)
		{
			LOGGER.warn("Failed to load items.properties: {}", e.getMessage());
		}
	}

	private static int parseInt(String s, int defaultVal)
	{
		try
		{
			return Integer.parseInt(s);
		}
		catch (NumberFormatException e)
		{
			return defaultVal;
		}
	}

	public boolean isConfigured(int itemId)
	{
		return _itemConfigs.containsKey(itemId);
	}

	public PremiumConfig getConfig(int itemId)
	{
		return _itemConfigs.get(itemId);
	}

	public static ItemPremiumManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final ItemPremiumManager INSTANCE = new ItemPremiumManager();
	}

	public static final class PremiumConfig
	{
		public final int minutes;
		public final int hours;
		public final int days;

		public PremiumConfig(int minutes, int hours, int days)
		{
			this.minutes = minutes;
			this.hours = hours;
			this.days = days;
		}

		/** Retorna o tempo total em milissegundos. */
		public long toMilliseconds()
		{
			return minutes * 60_000L + hours * 3_600_000L + days * 86_400_000L;
		}
	}
}

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
import ext.mods.gameserver.data.xml.PlayerLevelData;

/**
 * @author Dhousefe
 * @version 1.0
 * @since 2026-02-08
 * Administra itens configurados em items.properties que podem dar level ou delevel.
 */
public final class ItemLevelDelevelManager
{
	private static final CLogger LOGGER = new CLogger(ItemLevelDelevelManager.class.getName());
	private static final String ITEMS_FILE = Config.CONFIG_PATH.resolve("items.properties").toString();
	private static final String PREFIX = "ItemLevelDelevel_";

	private final Map<Integer, LevelDelevelConfig> _itemConfigs = new HashMap<>();

	protected ItemLevelDelevelManager()
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

				final String value = props.getProperty(key, "0,0").trim();
				final String[] parts = value.split("[,;]+");
				final int levelAdd = parts.length >= 1 ? parseInt(parts[0].trim(), 0) : 0;
				final int levelRemove = parts.length >= 2 ? parseInt(parts[1].trim(), 0) : 0;

				_itemConfigs.put(itemId, new LevelDelevelConfig(levelAdd, levelRemove));
			}
			LOGGER.info("ItemLevelDelevelManager: loaded {} item configurations.", _itemConfigs.size());
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

	/**
	 * @param itemId ID do item
	 * @return true se o item está configurado
	 */
	public boolean isConfigured(int itemId)
	{
		return _itemConfigs.containsKey(itemId);
	}

	/**
	 * @param itemId ID do item
	 * @return quantidade de levels a adicionar (0 = nenhum, -1 = level máximo), ou 0 se não configurado
	 */
	public int getLevelAdd(int itemId)
	{
		final LevelDelevelConfig cfg = _itemConfigs.get(itemId);
		return cfg != null ? cfg.levelAdd : 0;
	}

	/**
	 * @param itemId ID do item
	 * @return quantidade de levels a remover (0 = nenhum, -1 = delevel total), ou 0 se não configurado
	 */
	public int getLevelRemove(int itemId)
	{
		final LevelDelevelConfig cfg = _itemConfigs.get(itemId);
		return cfg != null ? cfg.levelRemove : 0;
	}

	/**
	 * @param itemId ID do item
	 * @return configuração do item ou null
	 */
	public LevelDelevelConfig getConfig(int itemId)
	{
		return _itemConfigs.get(itemId);
	}

	/**
	 * @return nível máximo do servidor
	 */
	public int getMaxLevel()
	{
		return PlayerLevelData.getInstance().getRealMaxLevel();
	}

	public static ItemLevelDelevelManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final ItemLevelDelevelManager INSTANCE = new ItemLevelDelevelManager();
	}

	/**
	 * Configuração de level/delevel por item
	 */
	public static final class LevelDelevelConfig
	{
		public final int levelAdd;
		public final int levelRemove;

		public LevelDelevelConfig(int levelAdd, int levelRemove)
		{
			this.levelAdd = levelAdd;
			this.levelRemove = levelRemove;
		}
	}
}

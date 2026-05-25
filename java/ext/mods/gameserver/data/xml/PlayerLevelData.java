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
package ext.mods.gameserver.data.xml;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;

import ext.mods.gameserver.model.records.PlayerLevel;

import org.w3c.dom.Document;

/**
 * This class loads and stores Player level related variables.
 */
public class PlayerLevelData implements IXmlReader
{
	private final Map<Integer, PlayerLevel> _levels = new HashMap<>();
	
	private int _maxLevel;
	
	protected PlayerLevelData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("xml/playerLevels.xml");
		LOGGER.info("Loaded {} player levels.", _levels.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "playerLevel", levelNode ->
		{
			final StatSet set = parseAttributes(levelNode);
			final int level = set.getInteger("level");
			
			_levels.put(level, new PlayerLevel(set));
			
			if (level > _maxLevel)
				_maxLevel = level;
		}));
	}
	
	/**
	 * @param level : The level to check.
	 * @return the xp death penalty related to a level.
	 */
	public PlayerLevel getPlayerLevel(int level)
	{
		return _levels.get(level);
	}
	
	public long getRequiredExpForHighestLevel()
	{
		return _levels.get(_maxLevel).requiredExpToLevelUp();
	}
	
	/**
	 * If you want a max at 80 & 99.99%, you have to put 81.
	 * @return the first UNREACHABLE level.
	 */
	public int getMaxLevel()
	{
		return _maxLevel;
	}
	
	public int getRealMaxLevel()
	{
		return _maxLevel - 1;
	}
	
	public static PlayerLevelData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final PlayerLevelData INSTANCE = new PlayerLevelData();
	}
}
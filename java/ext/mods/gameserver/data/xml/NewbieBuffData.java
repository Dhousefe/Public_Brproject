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
import java.util.ArrayList;
import java.util.List;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;

import ext.mods.gameserver.model.records.NewbieBuff;

import org.w3c.dom.Document;

/**
 * This class loads and store {@link NewbieBuff} into a {@link List}.
 */
public class NewbieBuffData implements IXmlReader
{
	private final List<NewbieBuff> _buffs = new ArrayList<>();
	
	private int _magicLowestLevel = 100;
	private int _physicLowestLevel = 100;
	
	protected NewbieBuffData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("xml/newbieBuffs.xml");
		LOGGER.info("Loaded {} newbie buffs.", _buffs.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "buff", buffNode ->
		{
			final StatSet set = parseAttributes(buffNode);
			final int lowerLevel = set.getInteger("lowerLevel");
			if (set.getBool("isMagicClass"))
			{
				if (lowerLevel < _magicLowestLevel)
					_magicLowestLevel = lowerLevel;
			}
			else
			{
				if (lowerLevel < _physicLowestLevel)
					_physicLowestLevel = lowerLevel;
			}
			_buffs.add(new NewbieBuff(set));
		}));
	}
	
	/**
	 * @param isMage : If true, return buffs list associated to mage classes.
	 * @param level : Filter the list by the given level.
	 * @return The {@link List} of valid {@link NewbieBuff}s for the given class type and level.
	 */
	public List<NewbieBuff> getValidBuffs(boolean isMage, int level)
	{
		return _buffs.stream().filter(b -> b.isMagicClass() == isMage && level >= b.lowerLevel() && level <= b.upperLevel()).toList();
	}
	
	public int getLowestBuffLevel(boolean isMage)
	{
		return (isMage) ? _magicLowestLevel : _physicLowestLevel;
	}
	
	public static NewbieBuffData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final NewbieBuffData INSTANCE = new NewbieBuffData();
	}
}
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;

import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.records.Henna;

import org.w3c.dom.Document;

/**
 * This class loads and stores {@link Henna}s infos. Hennas are called "dye" ingame.
 */
public class HennaData implements IXmlReader
{
	private final Map<Integer, Henna> _hennas = new HashMap<>();
	
	protected HennaData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("xml/hennas.xml");
		LOGGER.info("Loaded {} hennas.", _hennas.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "henna", hennaNode ->
		{
			final StatSet set = parseAttributes(hennaNode);
			_hennas.put(set.getInteger("symbolId"), new Henna(set));
		}));
	}
	
	public Collection<Henna> getHennas()
	{
		return _hennas.values();
	}
	
	public Henna getHenna(int id)
	{
		return _hennas.get(id);
	}
	
	/**
	 * Retrieve all {@link Henna}s available for a {@link Player} class.
	 * @param player : The Player used as class parameter.
	 * @return a List of all available Hennas for this Player.
	 */
	public List<Henna> getAvailableHennasFor(Player player)
	{
		return _hennas.values().stream().filter(h -> h.canBeUsedBy(player)).toList();
	}
	
	public static HennaData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final HennaData INSTANCE = new HennaData();
	}
}
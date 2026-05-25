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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ext.mods.commons.data.xml.IXmlReader;

import ext.mods.gameserver.model.location.TeleportLocation;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

/**
 * This class loads and stores {@link TeleportLocation}s used as regular teleport positions.
 */
public class TeleportData implements IXmlReader
{
	private final Map<Integer, List<TeleportLocation>> _teleports = new HashMap<>();
	
	protected TeleportData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("xml/teleports.xml");
		LOGGER.info("Loaded {} teleport positions.", _teleports.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "telPosList", telPosListNode ->
		{
			final NamedNodeMap telPosListAttrs = telPosListNode.getAttributes();
			final int npcId = Integer.parseInt(telPosListAttrs.getNamedItem("npcId").getNodeValue());
			
			final List<TeleportLocation> teleports = new ArrayList<>();
			forEach(telPosListNode, "loc", locNode -> teleports.add(new TeleportLocation(parseAttributes(locNode))));
			
			_teleports.put(npcId, teleports);
		}));
	}
	
	public void reload()
	{
		_teleports.clear();
		
		load();
	}
	
	public List<TeleportLocation> getTeleports(int npcId)
	{
		return _teleports.get(npcId);
	}

	public List<TeleportLocation> getAllTeleports()
	{
		final List<TeleportLocation> result = new ArrayList<>();
		for (List<TeleportLocation> list : _teleports.values())
		{
			if (list != null && !list.isEmpty())
				result.addAll(list);
		}
		return result;
	}
	
	public static TeleportData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final TeleportData INSTANCE = new TeleportData();
	}
}
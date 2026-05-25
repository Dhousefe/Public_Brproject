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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ext.mods.commons.data.xml.IXmlReader;

import ext.mods.gameserver.model.records.custom.StaticSpawn;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

public class StaticSpawnData implements IXmlReader
{
	private final Map<Integer, StaticSpawn> _staticSpawn = new HashMap<>();
	
	protected StaticSpawnData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("xml/staticSpawn.xml");
		LOGGER.info("Loaded {} raidboss static spawn.", _staticSpawn.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "npc", npcNode ->
		{
			final NamedNodeMap attrs = npcNode.getAttributes();
			boolean isEnable = parseBoolean(attrs, "isEnable", false);
			int id = parseInteger(attrs, "id");
			
			List<String> days = Arrays.asList(parseString(attrs, "days").split(";"));
			List<String> time = Arrays.asList(parseString(attrs, "time").split(";"));
			
			int randomTime = parseInteger(attrs, "randomTime", 0) * 60;
			
			boolean earthQuake = parseBoolean(attrs, "earthQuake", false);
			boolean announce = parseBoolean(attrs, "announce", false);
			
			final StaticSpawn staticSpawn = new StaticSpawn(isEnable, id, days, time, randomTime, earthQuake, announce);
			_staticSpawn.put(id, staticSpawn);
		}));
	}
	
	public StaticSpawn getById(int id)
	{
		return _staticSpawn.get(id);
	}
	
	public static StaticSpawnData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final StaticSpawnData INSTANCE = new StaticSpawnData();
	}
}
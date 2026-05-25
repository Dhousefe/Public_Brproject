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
package ext.mods.dungeon.data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import ext.mods.commons.data.xml.IXmlReader;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import ext.mods.dungeon.DungeonTemplate;
import ext.mods.dungeon.enums.DungeonType;
import ext.mods.dungeon.holder.SpawnTemplate;
import ext.mods.dungeon.holder.StageTemplate;

public class DungeonData implements IXmlReader
{
	private final Map<Integer, DungeonTemplate> _dungeons = new HashMap<>();
	final AtomicLong cooldownMs = new AtomicLong(0);
	
	public DungeonData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("custom/mods/dungeon_event.xml");
		LOGGER.info("Loaded {} dungeons.", _dungeons.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "dungeon", this::parseDungeon));
	}
	
	private void parseDungeon(Node dungeonNode)
	{
		final NamedNodeMap attrs = dungeonNode.getAttributes();
		
		final int id = parseInteger(attrs, "id", -1);
		final String name = parseString(attrs, "name", null);
		
		final String typeAttr = parseString(attrs, "type", "solo");
		final DungeonType type = DungeonType.valueOf(typeAttr.toUpperCase());
		
		final boolean shared = parseBoolean(attrs, "sharedInstance", false);
		
		final List<StageTemplate> stages = new ArrayList<>();
		final Map<Integer, List<SpawnTemplate>> stageSpawns = new HashMap<>();
		
		forEach(dungeonNode, node ->
		{
			switch (node.getNodeName())
			{
				case "cooldown":
				{
					final NamedNodeMap cooldownAttrs = node.getAttributes();
					
					final int minutes = parseInteger(cooldownAttrs, "minutes", -1);
					final int hours = parseInteger(cooldownAttrs, "hours", -1);
					final int days = parseInteger(cooldownAttrs, "days", -1);
					
					if (minutes > 0)
						cooldownMs.set(TimeUnit.MINUTES.toMillis(minutes));
					else if (hours > 0)
						cooldownMs.set(TimeUnit.HOURS.toMillis(hours));
					else if (days > 0)
						cooldownMs.set(TimeUnit.DAYS.toMillis(days));
					
					break;
				}
				
				case "stage":
				{
					final NamedNodeMap stageAttrs = node.getAttributes();
					final int order = parseInteger(stageAttrs, "order", 0);
					final String[] loc = parseString(stageAttrs, "loc", "0,0,0").split(",");
					final int x = Integer.parseInt(loc[0]);
					final int y = Integer.parseInt(loc[1]);
					final int z = Integer.parseInt(loc[2]);
					final boolean teleport = parseBoolean(stageAttrs, "teleport", false);
					final int time = parseInteger(stageAttrs, "minutes", 0);
					
					stages.add(new StageTemplate(order, x, y, z, teleport, time));
					break;
				}
				case "spawns":
				{
					final int stageId = parseInteger(node, "stage", -1);
					final List<SpawnTemplate> spawns = new ArrayList<>();
					
					forEach(node, "spawn", spawnNode ->
					{
						final NamedNodeMap spawnAttrs = spawnNode.getAttributes();
						final int npcId = parseInteger(spawnAttrs, "npcId", 0);
						final String title = parseString(spawnAttrs, "title", "");
						final int count = parseInteger(spawnAttrs, "count", 1);
						final int range = parseInteger(spawnAttrs, "range", 0);
						final String[] loc = parseString(spawnAttrs, "loc", "0,0,0").split(",");
						final int x = Integer.parseInt(loc[0]);
						final int y = Integer.parseInt(loc[1]);
						final int z = Integer.parseInt(loc[2]);
						final String drops = parseString(spawnAttrs, "drops", null);
						
						spawns.add(new SpawnTemplate(npcId, title, count, range, x, y, z, drops));
					});
					
					stageSpawns.put(stageId, spawns);
					break;
				}
			}
		});
		
		_dungeons.put(id, new DungeonTemplate(id, name, type, shared, cooldownMs.get(), stages, stageSpawns));
		
	}
	
	public DungeonTemplate getDungeon(int id)
	{
		return _dungeons.get(id);
	}
	
	public static DungeonData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		private static final DungeonData _instance = new DungeonData();
	}
}

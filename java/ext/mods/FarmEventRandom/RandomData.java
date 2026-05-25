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
package ext.mods.FarmEventRandom;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection; 
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;
import ext.mods.commons.logging.CLogger;

import org.w3c.dom.Document;

import ext.mods.FarmEventRandom.holder.MessagesHolder;
import ext.mods.FarmEventRandom.holder.RamdomConfig;
import ext.mods.FarmEventRandom.holder.RandomSpawnHolder;
import ext.mods.FarmEventRandom.holder.RandomZoneData; 

public class RandomData implements IXmlReader
{
	private static final boolean DEBUG = false; 

	private final List<RamdomConfig> _configs = new ArrayList<>();
	private final Map<String, Map<Integer, RandomZoneData>> _zoneDataMap = new HashMap<>();
	private final Map<String, MessagesHolder> _messages = new HashMap<>();
	
	public RandomData()
	{
		load();
	}
	
	public void reload()
	{
		_configs.clear(); 
		_zoneDataMap.clear();
		_messages.clear();
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("custom/mods/random_event.xml");
		LOGGER.info("Loaded " + _configs.size() + " random events.");
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "ramdoms", eventsNode ->
		{
			forEach(eventsNode, "event", eventNode ->
			{
				final RamdomConfig[] configHolder = { null };
				
				forEach(eventNode, "configs", confisNode ->
				{
					forEach(confisNode, "config", configNode ->
					{
						StatSet set = parseAttributes(configNode);
						configHolder[0] = new RamdomConfig(set);
						_configs.add(configHolder[0]);
					});
				});
				
				RamdomConfig currentConfig = configHolder[0];
				
				if (currentConfig == null)
				{
					LOGGER.warn("RandomData: Found <event> tag with no <configs>. Skipping.");
					return;
				}
				
				final String eventName = currentConfig.getName();
				
				forEach(eventNode, "messages", msgsNode ->
				{
					MessagesHolder holder = new MessagesHolder();
					forEach(msgsNode, "message", msgNode ->
					{
						StatSet set2 = parseAttributes(msgNode);
						holder.addOnPrepare(set2.getString("onPrepare", ""));
						holder.addOnStart(set2.getString("onStart", ""));
						holder.addOnZone(set2.getString("onZone", ""));
						holder.addOnEnd(set2.getString("onEnd", ""));
						holder.addOnAuto(set2.getString("onAuto", ""));
					});
					_messages.put(eventName, holder);
				});
				
				Map<Integer, RandomZoneData> zoneData = new HashMap<>();
				
				forEach(eventNode, "spawns", spawnsNode ->
				{
					StatSet spawnAttrs = parseAttributes(spawnsNode);
					int zoneId = spawnAttrs.getInteger("zoneId");
					
					RandomZoneData zData = new RandomZoneData(spawnAttrs);
					
					forEach(spawnsNode, "spawn", spawnNode ->
					{
						StatSet sets = parseAttributes(spawnNode);
						RandomSpawnHolder customSpawn = new RandomSpawnHolder(sets);
						zData.addCustomSpawn(customSpawn);
					});
					
					zoneData.put(zoneId, zData);
				});
				
				_zoneDataMap.put(eventName, zoneData);
			});
		});
	}
	
	public RamdomConfig getFirstConfig()
	{
		if (_configs.isEmpty())
		{
			LOGGER.warn("RandomData: No event configs loaded.");
			return null;
		}
		return _configs.get(0);
	}
	
	public List<RamdomConfig> getConfigs()
	{
		return _configs;
	}
	
	public RandomZoneData getZoneData(String eventName, int zoneId)
	{
		Map<Integer, RandomZoneData> zones = _zoneDataMap.get(eventName);
		if (zones == null)
			return null;
		
		return zones.get(zoneId);
	}
	
	
	/**
	 * Retorna todos os dados de zona configurados para um evento.
	 * @param eventName O nome do evento
	 * @return Uma coleção de todos os RandomZoneData, ou uma lista vazia.
	 */
	public Collection<RandomZoneData> getAllZoneData(String eventName)
	{
		Map<Integer, RandomZoneData> zones = _zoneDataMap.get(eventName);
		if (zones == null)
		{
			return Collections.emptyList();
		}
		return zones.values();
	}
	
	
	public MessagesHolder getMessages(String eventName)
	{
		return _messages.get(eventName);
	}
	
	public static RandomData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final RandomData _instance = new RandomData();
	}
}
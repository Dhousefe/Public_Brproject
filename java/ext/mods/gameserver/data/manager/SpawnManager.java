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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import ext.mods.Config;
import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;
import ext.mods.commons.geometry.AShape;
import ext.mods.commons.geometry.Polygon;
import ext.mods.commons.geometry.Territory;
import ext.mods.commons.geometry.Triangle;
import ext.mods.commons.lang.StringUtil;
import ext.mods.commons.pool.ConnectionPool;
import ext.mods.gameserver.data.xml.NpcData;
import ext.mods.gameserver.enums.CabalType;
import ext.mods.gameserver.enums.SealType;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.location.Point2D;
import ext.mods.gameserver.model.memo.SpawnMemo;
import ext.mods.gameserver.model.records.PrivateData;
import ext.mods.gameserver.model.spawn.ASpawn;
import ext.mods.gameserver.model.spawn.MultiSpawn;
import ext.mods.gameserver.model.spawn.NpcMaker;
import ext.mods.gameserver.model.spawn.Spawn;
import ext.mods.gameserver.model.spawn.SpawnData;
import ext.mods.gameserver.scripting.Quest;

/**
 * Loads spawn list based on {@link Territory}s and {@link NpcMaker}s.<br>
 * Handles spawn/respawn/despawn of various {@link Npc} in the game using events.<br>
 * Locally stores individual {@link Spawn}s (e.g. quests, temporary spawned {@link Npc}s).<br>
 * Loads/stores {@link Npc}s' {@link SpawnData} to/from database.
 */
public class SpawnManager implements IXmlReader
{
	private static final String NPC_SEARCH_TAG = "[SpawnManager][NpcSearch]";
	
	private static void npcSearchLog(String format, Object... args)
	{
		if (Config.AUTOFARM_DEBUG_RETURN)
		{
			Object[] safeArgs = args != null ? new Object[args.length] : new Object[0];
			for (int i = 0; i < safeArgs.length; i++)
			{
				safeArgs[i] = args[i] != null ? args[i] : "null";
			}
			LOGGER.info(format, safeArgs);
		}
	}
	
	private static final String OTHER_XML_FOLDER = Config.DATA_PATH.resolve("xml/spawnlist/custom").toString();
	
	private static final String LOAD_SPAWN_DATAS = "SELECT * FROM spawn_data ORDER BY name";
	private static final String TRUNCATE_SPAWN_DATAS = "TRUNCATE spawn_data";
	private static final String SAVE_SPAWN_DATAS = "INSERT INTO spawn_data (name, status, current_hp, current_mp, loc_x, loc_y, loc_z, heading, db_value, respawn_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_SPAWN_DATAS = "UPDATE spawn_data SET name = ?, status = ?, current_hp = ?, current_mp = ?, loc_x = ?, loc_y = ?, loc_z = ?, heading = ?, db_value = ?, respawn_time =? WHERE name=?";
	
	private final Map<String, SpawnData> _spawnData = new ConcurrentHashMap<>();
	
	private final Set<Territory> _territories = ConcurrentHashMap.newKeySet();
	private final Set<NpcMaker> _makers = ConcurrentHashMap.newKeySet();
	private final Set<Spawn> _spawns = ConcurrentHashMap.newKeySet();
	
	private int _dynamicGroupId = 0;
	
	public SpawnManager()
	{
		load();
	}
	
	@Override
	public void load()
	{
		loadSpawnData();
		LOGGER.info("Loaded {} spawn data.", _spawnData.size());
		
		parseDataFile("xml/spawnlist/");
		LOGGER.info("Loaded {} territories.", _territories.size());
		LOGGER.info("Loaded {} NPC makers.", _makers.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		final List<Point2D> coords = new ArrayList<>();
		forEach(doc, "list", listNode ->
		{
			forEach(listNode, "territory", territoryNode ->
			{
				final NamedNodeMap terr = territoryNode.getAttributes();
				
				final String name = parseString(terr, "name");
				int minZ = parseInteger(terr, "minZ");
				int maxZ = parseInteger(terr, "maxZ");
				
				forEach(territoryNode, "node", locationNode ->
				{
					final NamedNodeMap loc = locationNode.getAttributes();
					coords.add(new Point2D(parseInteger(loc, "x"), parseInteger(loc, "y")));
				});
				
				try
				{
					_territories.add(new Territory(name, minZ, maxZ, coords));
				}
				catch (Exception e)
				{
					LOGGER.warn("Cannot load territory \"{}\", {}", name, e.getMessage());
				}
				
				coords.clear();
			});
			
			forEach(listNode, "npcmaker", npcmakerNode ->
			{
				final StatSet set = parseAttributes(npcmakerNode);
				
				Territory territory = findTerritory(set.getString("territory"));
				if (territory != null)
					set.put("t", territory);
				
				final String banName = set.getString("ban", null);
				if (banName != null)
				{
					territory = findTerritory(banName);
					if (territory != null)
						set.put("bt", territory);
				}
				
				final Map<String, String> makerAIParams = new HashMap<>();
				
				forEach(npcmakerNode, "ai", aiNode ->
				{
					set.put("maker", parseString(aiNode.getAttributes(), "type"));
					
					forEach(aiNode, "set", paramNode ->
					{
						final NamedNodeMap paramAttrs = paramNode.getAttributes();
						makerAIParams.put(parseString(paramAttrs, "name"), parseString(paramAttrs, "val").replace("@", ""));
					});
				});
				
				set.put("aiParams", makerAIParams);
				
				final NpcMaker maker = new NpcMaker(set);
				
				final List<MultiSpawn> spawns = new ArrayList<>();
				forEach(npcmakerNode, "npc", npcNode ->
				{
					final NamedNodeMap npc = npcNode.getAttributes();
					
					final int npcId = parseInteger(npc, "id");
					final NpcTemplate template = NpcData.getInstance().getTemplate(npcId);
					if (template == null)
					{
						LOGGER.warn("NpcTemplate was not found for NPC id {} in NpcMaker name {}.", npcId, maker.getName());
						return;
					}
					
					final int total = parseInteger(npc, "total");
					
					final int respawnDelay = StringUtil.getTimeStamp(parseString(npc, "respawn", null));
					final int respawnRandom = StringUtil.getTimeStamp(parseString(npc, "respawnRand", null));
					
					final List<PrivateData> privateData = new ArrayList<>();
					forEach(npcNode, "privates", privatesNode -> forEach(privatesNode, "private", privateNode -> privateData.add(new PrivateData(parseAttributes(privateNode)))));
					
					final SpawnMemo spawnMemo = new SpawnMemo();
					forEach(npcNode, "ai", aiNode -> forEach(aiNode, "set", paramNode ->
					{
						final NamedNodeMap paramAttrs = paramNode.getAttributes();
						spawnMemo.put(parseString(paramAttrs, "name"), parseString(paramAttrs, "val"));
					}));
					
					int[][] coords2 = null;
					final String pos = parseString(npc, "pos", null);
					if (pos != null)
					{
						String[] loc = pos.split(";");
						if (loc.length < 5)
						{
							coords2 = new int[1][4];
							coords2[0][0] = Integer.parseInt(loc[0]);
							coords2[0][1] = Integer.parseInt(loc[1]);
							coords2[0][2] = Integer.parseInt(loc[2]);
							coords2[0][3] = Integer.parseInt(loc[3]);
						}
						else
						{
							coords2 = new int[loc.length / 5][5];
							for (int i = 0; i < loc.length / 5; i++)
							{
								coords2[i][0] = Integer.parseInt(loc[i * 5]);
								coords2[i][1] = Integer.parseInt(loc[i * 5 + 1]);
								coords2[i][2] = Integer.parseInt(loc[i * 5 + 2]);
								coords2[i][3] = Integer.parseInt(loc[i * 5 + 3]);
								coords2[i][4] = Integer.parseInt(loc[i * 5 + 4].split("%")[0]);
							}
						}
					}
					
					final String dbName = parseString(npc, "dbName", null);
					
					SpawnData spawnData = null;
					if (dbName != null)
						spawnData = _spawnData.computeIfAbsent(dbName, sd -> new SpawnData(dbName));
					
					try
					{
						spawns.add(new MultiSpawn(maker, template, total, respawnDelay, respawnRandom, privateData, spawnMemo, coords2, spawnData));
					}
					catch (Exception e)
					{
						LOGGER.error("Can't create MultiSpawn for maker {}, npc id {}", e, maker.getName(), npcId);
					}
				});
				
				maker.setSpawns(spawns);
				
				_makers.add(maker);
			});
		});
	}
	
	public SpawnData getSpawnData(String name)
	{
		return _spawnData.get(name);
	}
	
	/**
	 * Reload {@link Territory}s and {@link NpcMaker}s and spawn NPCs.
	 */
	public void reload()
	{
		save();
		
		_spawnData.clear();
		_territories.clear();
		_makers.clear();
		_spawns.clear();
		
		load();
		spawn();
	}
	
	/**
	 * Save NPC data.
	 */
	public void save()
	{
		_makers.stream().map(NpcMaker::getSpawns).flatMap(List::stream).forEach(MultiSpawn::updateSpawnData);
		_spawns.forEach(Spawn::updateSpawnData);
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement delete = con.prepareStatement(TRUNCATE_SPAWN_DATAS);
			PreparedStatement ps = con.prepareStatement(SAVE_SPAWN_DATAS))
		{
			delete.execute();
			
			for (SpawnData data : _spawnData.values())
			{
				byte status = data.getStatus();
				if (status < 0)
					continue;
				
				try
				{
					data.save(ps);
					
					ps.addBatch();
				}
				catch (Exception e)
				{
					LOGGER.warn("Couldn't save spawn data for name \"{}\".", e, data.getName());
				}
			}
			
			ps.executeBatch();
		}
		catch (Exception e)
		{
			LOGGER.warn("Couldn't save spawn data.", e);
		}
	}
	
	public void save(SpawnData data)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_SPAWN_DATAS))
		{
			byte status = data.getStatus();
			if (status < 0)
				return;
			
			try
			{
				data.save(ps);
				ps.setString(11, data.getName());
				ps.addBatch();
			}
			catch (Exception e)
			{
				LOGGER.warn("Couldn't save spawn data for name \"{}\".", e, data.getName());
			}
			
			ps.executeBatch();
		}
		catch (Exception e)
		{
			LOGGER.warn("Couldn't save spawn data.", e);
		}
	}
	
	/**
	 * Load all {@link SpawnData}s from database.
	 */
	private final void loadSpawnData()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(LOAD_SPAWN_DATAS);
			ResultSet rs = ps.executeQuery();)
		{
			while (rs.next())
			{
				final String name = rs.getString("name");
				_spawnData.put(name, new SpawnData(name, rs));
			}
		}
		catch (Exception e)
		{
			LOGGER.warn("Couldn't load spawn data.", e);
		}
	}
	
	/**
	 * Spawn all possible {@link Npc} to the world at server start.<br>
	 * Native, day/night, events allowed on start, Seven Signs, etc.
	 */
	public void spawn()
	{
		if (Config.NO_SPAWNS)
			return;
			
		long total = _makers.stream().filter(NpcMaker::isOnStart).mapToInt(NpcMaker::spawnAll).sum();
		LOGGER.info("Spawned {} NPCs.", total);
		
		for (String event : Config.SPAWN_EVENTS)
			spawnEventNpcs(event, true);
		
		notifySevenSignsChange();
	}
	
	/**
	 * Spawn Seven Signs NPCs depending on period and status.
	 */
	public void notifySevenSignsChange()
	{
		
		despawnEventNpcs("ssq_seal1_none", false);
		despawnEventNpcs("ssq_seal1_dawn", false);
		despawnEventNpcs("ssq_seal1_twilight", false);
		
		despawnEventNpcs("ssq_seal2_none", false);
		despawnEventNpcs("ssq_seal2_dawn", false);
		despawnEventNpcs("ssq_seal2_twilight", false);
		
		despawnEventNpcs("ssq_event", false);
		
		switch (SevenSignsManager.getInstance().getCurrentPeriod())
		{
			case RECRUITING, COMPETITION:
				long spawn = spawnEventNpcs("ssq_event", false);
				LOGGER.info("Spawned {} Seven Signs - Event NPCs.", spawn);
				break;
			
			case RESULTS, SEAL_VALIDATION:
				final CabalType cabalWon = SevenSignsManager.getInstance().getWinningCabal();
				
				switch (SevenSignsManager.getInstance().getSealOwner(SealType.AVARICE))
				{
					case NORMAL:
						spawn = spawnEventNpcs("ssq_seal1_none", false);
						LOGGER.info("Spawned {} Seven Signs - Seal of Avarice NPCs, winning cabal none.", spawn);
						break;
					
					case DUSK:
						if (cabalWon == CabalType.DUSK)
						{
							spawn = spawnEventNpcs("ssq_seal1_twilight", false);
							LOGGER.info("Spawned {} Seven Signs - Seal of Avarice NPCs, winning cabal Dusk.", spawn);
						}
						else
						{
							spawn = spawnEventNpcs("ssq_seal1_none", false);
							LOGGER.info("Spawned {} Seven Signs - Seal of Avarice NPCs, winning cabal Dawn, seal cabal Dusk.", spawn);
						}
						break;
					
					case DAWN:
						if (cabalWon == CabalType.DAWN)
						{
							spawn = spawnEventNpcs("ssq_seal1_dawn", false);
							LOGGER.info("Spawned {} Seven Signs - Seal of Avarice NPCs, winning cabal Dawn.", spawn);
						}
						else
						{
							spawn = spawnEventNpcs("ssq_seal1_none", false);
							LOGGER.info("Spawned {} Seven Signs - Seal of Avarice NPCs, winning cabal Dusk, seal cabal Dawn.", spawn);
						}
						break;
				}
				
				switch (SevenSignsManager.getInstance().getSealOwner(SealType.GNOSIS))
				{
					case NORMAL:
						spawn = spawnEventNpcs("ssq_seal2_none", false);
						LOGGER.info("Spawned {} Seven Signs - Seal of Gnosis NPCs, winning cabal none.", spawn);
						break;
					
					case DUSK:
						if (cabalWon == CabalType.DUSK)
						{
							spawn = spawnEventNpcs("ssq_seal2_twilight", false);
							LOGGER.info("Spawned {} Seven Signs - Seal of Gnosis NPCs, winning cabal Dusk.", spawn);
						}
						else
						{
							spawn = spawnEventNpcs("ssq_seal2_none", false);
							LOGGER.info("Spawned {} Seven Signs - Seal of Gnosis NPCs, winning cabal Dawn, seal cabal Dusk.", spawn);
						}
						break;
					
					case DAWN:
						if (cabalWon == CabalType.DAWN)
						{
							spawn = spawnEventNpcs("ssq_seal2_dawn", false);
							LOGGER.info("Spawned {} Seven Signs - Seal of Gnosis NPCs, winning cabal Dawn.", spawn);
						}
						else
						{
							spawn = spawnEventNpcs("ssq_seal2_none", false);
							LOGGER.info("Spawned {} Seven Signs - Seal of Gnosis NPCs, winning cabal Dusk, seal cabal Dawn.", spawn);
						}
						break;
				}
				break;
		}
	}
	
	/**
	 * Despawn all NPCs from {@link NpcMaker} and individual spawns.
	 */
	public final void despawn()
	{
		long total = _makers.stream().mapToInt(NpcMaker::deleteAll).sum();
		LOGGER.info("Despawned {} NPCs.", total);
		
		_spawns.forEach(Spawn::doDelete);
	}
	
	/**
	 * @param name : The name.
	 * @return the {@link Territory} of given ID, null when none.
	 */
	public final Territory getTerritory(String name)
	{
		return _territories.stream().filter(t -> t.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	/**
	 * @param names : The name(s) of the {@link Territory}(s).
	 * @return the {@link Territory} of given name(s).
	 */
	private final Territory findTerritory(String names)
	{
		final String[] list = names.split(";");
		if (list.length == 0)
			return null;
		
		if (list.length == 1)
			return getTerritory(list[0]);
		
		final String groupedName = "grouped_" + String.format("%03d", _dynamicGroupId++);
		final Set<Triangle> shapes = new HashSet<>();
		
		int minZ = Integer.MAX_VALUE;
		int maxZ = Integer.MIN_VALUE;
		
		for (String name : list)
		{
			final Territory territory = getTerritory(name);
			if (territory == null)
			{
				LOGGER.warn("Territory {} does not exist.", name);
				return null;
			}
			
			minZ = Math.min(minZ, territory.getMinZ());
			maxZ = Math.max(maxZ, territory.getMaxZ());
			
			final AShape shape = territory.getShape();
			if (shape instanceof Polygon polygon)
				shapes.addAll(polygon.getShapes());
			else if (shape instanceof Triangle triangle)
				shapes.add(triangle);
		}
		
		final Territory t = new Territory(groupedName, minZ, maxZ, shapes);
		
		_territories.add(t);
		return t;
	}
	
	/**
	 * @param loc : The {@link Location} to test.
	 * @return the {@link List} of all {@link NpcMaker}s at a given {@link Location}.
	 */
	public final List<NpcMaker> getNpcMakers(Location loc)
	{
		return _makers.stream().filter(m -> m.getTerritory().isInside(loc)).toList();
	}
	
	/**
	 * @param name : The {@link String} used as name.
	 * @return the {@link NpcMaker} of given name, null when none.
	 */
	public final NpcMaker getNpcMaker(String name)
	{
		return _makers.stream().filter(nm -> nm.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	/**
	 * Add {@link Quest} to {@link NpcMaker} of given name, to handle all NPCs being dead event.
	 * @param name : The name.
	 * @param quest : The {@link Quest} to be added.
	 */
	public final void addQuestEventByName(String name, Quest quest)
	{
		_makers.stream().filter(nm -> nm.getName().equalsIgnoreCase(name)).forEach(nm -> nm.addQuestEvent(quest));
	}
	
	/**
	 * Add {@link Quest} to {@link NpcMaker} of given event name, to handle all NPCs being dead event.
	 * @param event : The event name.
	 * @param quest : The {@link Quest} to be added.
	 */
	public final void addQuestEventByEvent(String event, Quest quest)
	{
		_makers.stream().filter(nm -> event.equals(nm.getEvent())).forEach(nm -> nm.addQuestEvent(quest));
	}
	
	/**
	 * Spawn NPCs with given event name.
	 * @param event : Type of spawn.
	 * @param message : When true, display LOGGER message about spawn.
	 * @return the amount of spawned NPCs.
	 */
	public final long spawnEventNpcs(String event, boolean message)
	{
		if (event == null || event.length() == 0)
			return 0;
		
		long total = _makers.stream().filter(nm -> event.equals(nm.getEvent())).mapToInt(NpcMaker::spawnAll).sum();
		
		if (message)
			LOGGER.info("Spawned {} \"{}\" NPCs.", total, event);
		
		return total;
	}
	
	/**
	 * Despawn NPCs in {@link NpcMaker}s with given event name.
	 * @param event : Type of spawn.
	 * @param message : When true, display LOGGER message about despawn.
	 * @return the mount of despawned NPCs.
	 */
	public final long despawnEventNpcs(String event, boolean message)
	{
		if (event == null || event.length() == 0)
			return 0;
		
		long total = _makers.stream().filter(nm -> event.equals(nm.getEvent())).mapToInt(NpcMaker::deleteAll).sum();
		
		if (message)
			LOGGER.info("Despawned {} \"{}\" NPCs.", total, event);
		
		return total;
	}
	
	/**
	 * Spawn NPCs with given event name.
	 * @param time : time to spawn.
	 * @param param1 : time to spawn parameter 1.
	 * @param param2 : time to spawn parameter 2.
	 * @param param3 : time to spawn parameter 3.
	 * @param message : When true, display LOGGER message about spawn.
	 * @return the amount of spawned NPCs.
	 */
	public final long startSpawnTime(String time, String param1, String param2, String param3, boolean message)
	{
		if (time == null || time.isEmpty())
			return 0;
		
		long total = _makers.stream().filter(nm ->
		{
			if (nm.getMakerSpawnTime() == null)
				return false;
			
			if (!time.equalsIgnoreCase(nm.getMakerSpawnTime().getName()))
				return false;
			
			final String[] spawnTimeParams = nm.getMakerSpawnTimeParams();
			if (spawnTimeParams == null)
				return false;
			
			if (spawnTimeParams.length > 0)
			{
				if (param1 == null)
					return false;
				
				if (!param1.equalsIgnoreCase(spawnTimeParams[0]))
					return false;
				
				if (spawnTimeParams.length > 1)
				{
					if (param2 == null)
						return false;
					
					if (!param2.equalsIgnoreCase(spawnTimeParams[1]))
						return false;
				}
				
				if (spawnTimeParams.length > 2)
				{
					if (param3 == null)
						return false;
					
					if (!param3.equalsIgnoreCase(spawnTimeParams[2]))
						return false;
				}
			}
			
			return true;
		}).mapToInt(NpcMaker::spawnAll).sum();
		
		if (message)
			LOGGER.info("Spawned {} \"{}\" NPCs.", total, time);
		
		return total;
	}
	
	/**
	 * Despawn NPCs in {@link NpcMaker}s with given event name.
	 * @param time : time to despawn.
	 * @param param1 : time to despawn parameter 1.
	 * @param param2 : time to despawn parameter 2.
	 * @param param3 : time to despawn parameter 3.
	 * @param message : When true, display LOGGER message about despawn.
	 * @return the mount of despawned NPCs.
	 */
	public final long stopSpawnTime(String time, String param1, String param2, String param3, boolean message)
	{
		if (time == null || time.isEmpty())
			return 0;
		
		long total = _makers.stream().filter(nm ->
		{
			if (nm.getMakerSpawnTime() == null)
				return false;
			
			if (!time.equalsIgnoreCase(nm.getMakerSpawnTime().getName()))
				return false;
			
			final String[] spawnTimeParams = nm.getMakerSpawnTimeParams();
			if (spawnTimeParams == null)
				return false;
			
			if (spawnTimeParams.length > 0)
			{
				if (param1 == null)
					return false;
				
				if (!param1.equalsIgnoreCase(spawnTimeParams[0]))
					return false;
				
				if (spawnTimeParams.length > 1)
				{
					if (param2 == null)
						return false;
					
					if (!param2.equalsIgnoreCase(spawnTimeParams[1]))
						return false;
				}
				
				if (spawnTimeParams.length > 2)
				{
					if (param3 == null)
						return false;
					
					if (!param3.equalsIgnoreCase(spawnTimeParams[2]))
						return false;
				}
			}
			
			return true;
		}).mapToInt(NpcMaker::deleteAll).sum();
		
		if (message)
			LOGGER.info("Despawned {} \"{}\" NPCs.", total, time);
		
		return total;
	}
	
	/**
	 * Add an individual {@link Spawn}.
	 * @param spawn : {@link Spawn} to be added.
	 */
	public void addSpawn(Spawn spawn)
	{
		_spawns.add(spawn);
	}
	
	/**
	 * Adds a new spawn to the spawn table.
	 * @param spawn the spawn to add
	 * @param store if {@code true} it'll be saved in the spawn XML files
	 */
	public void addSpawn(Spawn spawn, boolean store)
	{
		addSpawn(spawn);
		
		if (store)
		{
			final File outputDirectory = new File(OTHER_XML_FOLDER);
			if (!outputDirectory.exists())
			{
				try
				{
					outputDirectory.mkdir();
				}
				catch (SecurityException se)
				{
				}
			}
			
			final String name = spawn.getNpc().getName().replaceAll("(\\s|')+", "").toLowerCase() + "_" + spawn.getNpc().getX() + spawn.getNpc().getY();
			final String npcMakerName = spawn.getNpc().getName().replaceAll("(\\s|')+", "").toLowerCase() + "_" + spawn.getNpc().getX() + spawn.getNpc().getY() + "1";
			final String fileName = spawn.getNpc().getName().replaceAll("(\\s|')+", "").toLowerCase();
			
			final int x = ((spawn.getLocX() - World.WORLD_X_MIN) >> 15) + World.TILE_X_MIN;
			final int y = ((spawn.getLocY() - World.WORLD_Y_MIN) >> 15) + World.TILE_Y_MIN;
			final File spawnFile = new File(OTHER_XML_FOLDER + "/" + fileName + "_" + x + "_" + y + ".xml");
			
			final String spawnId = String.valueOf(spawn.getNpcId());
			final String spawnLoc = String.valueOf(spawn.getLocX() + ";" + spawn.getLocY() + ";" + spawn.getLocZ() + ";" + spawn.getHeading());
			
			final var respawn = getSpawn(spawn.getNpc().getNpcId()).getRespawnDelay();
			final var respawnRnd = getSpawn(spawn.getNpc().getNpcId()).getRespawnRandom();
			final var respawnRndString = (respawnRnd == 0 ? "" : "\" respawnRand=\"" + respawnRnd + "sec");
			
			if (spawnFile.exists())
			{
				final File tempFile = new File(OTHER_XML_FOLDER + "/" + name + "_" + x + "_" + y + ".tmp");
				try (BufferedReader reader = new BufferedReader(new FileReader(spawnFile));
					BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile)))
				{
					String currentLine;
					while ((currentLine = reader.readLine()) != null)
					{
						if (currentLine.contains("</list>"))
						{
							writer.write("	<territory name=\"" + name + "\" minZ=\"" + (spawn.getLocZ()) + "\" maxZ=\"" + (spawn.getLocZ() + 16) + "\">\n");
							writer.write("		<node x=\"" + (spawn.getLocX() + 50) + "\" y=\"" + (spawn.getLocY() + 50) + "\" />\n");
							writer.write("		<node x=\"" + (spawn.getLocX() - 50) + "\" y=\"" + (spawn.getLocY() + 50) + "\" />\n");
							writer.write("		<node x=\"" + (spawn.getLocX() - 50) + "\" y=\"" + (spawn.getLocY() - 50) + "\" />\n");
							writer.write("		<node x=\"" + (spawn.getLocX() + 50) + "\" y=\"" + (spawn.getLocY() - 50) + "\" />\n");
							writer.write("	</territory>\n");
							writer.write("	<npcmaker name=\"" + npcMakerName + "\" territory=\"" + name + "\" maximumNpcs=\"" + 1 + "\">\n");
							writer.write("		<ai type=\"default_maker\"/>\n");
							writer.write("		<npc id=\"" + spawnId + "\" pos=\"" + spawnLoc + "\" total=\"" + 1 + "\" respawn=\"" + respawn + "sec" + respawnRndString + "\" /> <!-- " + NpcData.getInstance().getTemplate(spawn.getNpcId()).getName() + " -->\n");
							writer.write("	</npcmaker>\n");
							writer.write(currentLine + "\n");
							continue;
						}
						writer.write(currentLine + "\n");
					}
					writer.close();
					reader.close();
					spawnFile.delete();
					tempFile.renameTo(spawnFile);
				}
				catch (Exception e)
				{
					LOGGER.warn("Could not store spawn in the spawn XML files: " + e);
				}
			}
			else
			{
				try (BufferedWriter writer = new BufferedWriter(new FileWriter(spawnFile)))
				{
					writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
					writer.write("<list>\n");
					writer.write("	<territory name=\"" + name + "\" minZ=\"" + (spawn.getLocZ()) + "\" maxZ=\"" + (spawn.getLocZ() + 16) + "\">\n");
					writer.write("		<node x=\"" + (spawn.getLocX() + 50) + "\" y=\"" + (spawn.getLocY() + 50) + "\" />\n");
					writer.write("		<node x=\"" + (spawn.getLocX() - 50) + "\" y=\"" + (spawn.getLocY() + 50) + "\" />\n");
					writer.write("		<node x=\"" + (spawn.getLocX() - 50) + "\" y=\"" + (spawn.getLocY() - 50) + "\" />\n");
					writer.write("		<node x=\"" + (spawn.getLocX() + 50) + "\" y=\"" + (spawn.getLocY() - 50) + "\" />\n");
					writer.write("	</territory>\n");
					writer.write("	<npcmaker name=\"" + npcMakerName + "\" territory=\"" + name + "\" maximumNpcs=\"" + 1 + "\">\n");
					writer.write("		<ai type=\"default_maker\"/>\n");
					writer.write("		<npc id=\"" + spawnId + "\" pos=\"" + spawnLoc + "\" total=\"" + 1 + "\" respawn=\"" + respawn + "sec" + respawnRndString + "\" /> <!-- " + NpcData.getInstance().getTemplate(spawn.getNpcId()).getName() + " -->\n");
					writer.write("	</npcmaker>\n");
					writer.write("</list>\n");
					writer.close();
				}
				catch (Exception e)
				{
					LOGGER.warn("Spawn " + spawn + " could not be added to the spawn XML files: " + e);
				}
			}
		}
	}
	
	/**
	 * Remove an individual {@link Spawn}.
	 * @param spawn : {@link Spawn} to be removed.
	 */
	public void deleteSpawn(Spawn spawn)
	{
		_spawns.remove(spawn);
	}
	
	public void deleteSpawn(Spawn spawn, boolean store)
	{
		final String name = spawn.getNpc().getName().replaceAll("(\\s|')+", "").toLowerCase() + "_" + spawn.getSpawnLocation().getX() + spawn.getSpawnLocation().getY();
		final String npcMakerName = spawn.getNpc().getName().replaceAll("(\\s|')+", "").toLowerCase() + "_" + spawn.getSpawnLocation().getX() + spawn.getSpawnLocation().getY() + "1";
		
		final String fileName = spawn.getNpc().getName().replaceAll("(\\s|')+", "").toLowerCase();
		final int x = ((spawn.getLocX() - World.WORLD_X_MIN) >> 15) + World.TILE_X_MIN;
		final int y = ((spawn.getLocY() - World.WORLD_Y_MIN) >> 15) + World.TILE_Y_MIN;
		final File spawnFile = new File(OTHER_XML_FOLDER + "/" + fileName + "_" + x + "_" + y + ".xml");
		
		if (!spawnFile.exists())
		{
			return;
		}
		
		try
		{
			final File tempFile = new File(OTHER_XML_FOLDER + "/" + fileName + "_" + x + "_" + y + ".tmp");
			try (BufferedReader reader = new BufferedReader(new FileReader(spawnFile));
				BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile)))
			{
				String currentLine;
				boolean inTerritory = false;
				boolean inNpcMaker = false;
				boolean needToDelete = false;
				while ((currentLine = reader.readLine()) != null)
				{
					if (currentLine.contains("<territory name=\"" + name))
						inTerritory = true;
					
					if (currentLine.contains("<npcmaker name=\"" + npcMakerName))
						inNpcMaker = true;
					
					if (inTerritory || inNpcMaker)
					{
						if (currentLine.contains("</territory>") || currentLine.contains("</npcmaker>"))
						{
							inTerritory = false;
							inNpcMaker = false;
							if (!needToDelete)
								writer.write(currentLine + "\n");
						}
						needToDelete = true;
					}
					else
						writer.write(currentLine + "\n");
				}
				
				writer.close();
				reader.close();
				
				if (needToDelete)
				{
					spawnFile.delete();
					tempFile.renameTo(spawnFile);
				}
				else
					tempFile.delete();
			}
			if (isEmptyXml(spawnFile))
				spawnFile.delete();
		}
		catch (Exception e)
		{
			LOGGER.warn("Could not remove spawn from the spawn XML files: " + e);
		}
	}
	
	public static boolean isEmptyXml(File file)
	{
		try (BufferedReader reader = new BufferedReader(new FileReader(file)))
		{
			StringBuilder content = new StringBuilder();
			String line;
			
			while ((line = reader.readLine()) != null)
			{
				content.append(line.trim());
			}
			
			String expectedContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><list></list>";
			return content.toString().equals(expectedContent);
		}
		catch (Exception e)
		{
			LOGGER.warn("The content of the XML file could not be checked: " + e);
			return false;
		}
	}
	
	/**
	 * @param npcId : The {@link Npc} ID.
	 * @return The first found {@link ASpawn} of given {@link Npc}.
	 */
	public final ASpawn getSpawn(int npcId)
	{
		npcSearchLog("{} getSpawn(npcId={}) entry", NPC_SEARCH_TAG, npcId);
		ASpawn result = _makers.stream().flatMap(nm -> nm.getSpawns().stream()).filter(ms -> ms.getNpcId() == npcId).findFirst().orElse(null);
		if (result == null)
			result = _spawns.stream().filter(s -> s.getNpcId() == npcId).findFirst().orElse(null);
		npcSearchLog("{} getSpawn(npcId={}) return {}", NPC_SEARCH_TAG, npcId, result);
		return result;
	}
	
	/**
	 * Retorna a localizacao do NPC mais proximo do jogador.
	 * Prioriza posicoes de NPCs realmente spawados (getNpc() != null); senao usa spawnlist (coords/spawnData).
	 * Usa distancia 3D para evitar escolher spawn em andar/regiao diferente.
	 *
	 * @param player : O jogador para calcular a distancia.
	 * @param npcId  : O ID do NPC.
	 * @return A localizacao mais proxima do jogador onde o NPC existe ou deveria estar, ou null se nao encontrar.
	 */
	public final Location getClosestSpawnLocation(Player player, int npcId)
	{
		npcSearchLog("{} getClosestSpawnLocation() entry player={} npcId={}", NPC_SEARCH_TAG, player != null ? player.getName() : null, npcId);
		if (player == null)
		{
			npcSearchLog("{} getClosestSpawnLocation() return null (player null)", NPC_SEARCH_TAG);
			return null;
		}
		
		Location closestFromNpc = null;
		long bestDistFromNpc = Long.MAX_VALUE;
		
		Location closestFromSpawnlist = null;
		long bestDistFromSpawnlist = Long.MAX_VALUE;
		
		final int px = player.getX();
		final int py = player.getY();
		final int pz = player.getZ();
		
		for (NpcMaker nm : _makers)
		{
			for (ASpawn spawn : nm.getSpawns())
			{
				if (spawn.getNpcId() != npcId)
					continue;
				
				if (spawn.getNpc() != null)
				{
					final Npc npc = spawn.getNpc();
					final long dx = npc.getX() - px;
					final long dy = npc.getY() - py;
					final long dz = npc.getZ() - pz;
					final long dist = dx * dx + dy * dy + dz * dz;
					if (dist < bestDistFromNpc)
					{
						bestDistFromNpc = dist;
						closestFromNpc = new Location(npc.getX(), npc.getY(), npc.getZ());
					}
					continue;
				}
				
				Location loc = resolveSpawnLocation(player, spawn);
				if (loc != null)
				{
					final long dx = loc.getX() - px;
					final long dy = loc.getY() - py;
					final long dz = loc.getZ() - pz;
					final long dist = dx * dx + dy * dy + dz * dz;
					if (dist < bestDistFromSpawnlist)
					{
						bestDistFromSpawnlist = dist;
						closestFromSpawnlist = loc;
					}
				}
			}
		}
		
		for (Spawn spawn : _spawns)
		{
			if (spawn.getNpcId() != npcId)
				continue;
			
			if (spawn.getNpc() != null)
			{
				final Npc npc = spawn.getNpc();
				final long dx = npc.getX() - px;
				final long dy = npc.getY() - py;
				final long dz = npc.getZ() - pz;
				final long dist = dx * dx + dy * dy + dz * dz;
				if (dist < bestDistFromNpc)
				{
					bestDistFromNpc = dist;
					closestFromNpc = new Location(npc.getX(), npc.getY(), npc.getZ());
				}
				continue;
			}
			
			Location loc = new Location(spawn.getLocX(), spawn.getLocY(), spawn.getLocZ());
			final long dx = loc.getX() - px;
			final long dy = loc.getY() - py;
			final long dz = loc.getZ() - pz;
			final long dist = dx * dx + dy * dy + dz * dz;
			if (dist < bestDistFromSpawnlist)
			{
				bestDistFromSpawnlist = dist;
				closestFromSpawnlist = loc;
			}
		}
		
		final Location result = closestFromNpc != null ? closestFromNpc : closestFromSpawnlist;
		npcSearchLog("{} getClosestSpawnLocation() return {} (fromNpc={} fromSpawnlist={})", NPC_SEARCH_TAG, result, closestFromNpc, closestFromSpawnlist);
		if (closestFromNpc != null)
			return closestFromNpc;
		return closestFromSpawnlist;
	}
	
	private Location resolveSpawnLocation(Player player, ASpawn spawn)
	{
		npcSearchLog("{} resolveSpawnLocation() entry npcId={} hasNpc={}", NPC_SEARCH_TAG, spawn.getNpcId(), spawn.getNpc() != null);
		if (spawn.getNpc() != null)
		{
			Location loc = new Location(spawn.getNpc().getX(), spawn.getNpc().getY(), spawn.getNpc().getZ());
			npcSearchLog("{} resolveSpawnLocation() return {} (from getNpc())", NPC_SEARCH_TAG, loc);
			return loc;
		}
		
		SpawnData spawnData = spawn.getSpawnData();
		if (spawnData != null && (spawnData.getX() != 0 || spawnData.getY() != 0 || spawnData.getZ() != 0))
		{
			Location loc = new Location(spawnData.getX(), spawnData.getY(), spawnData.getZ());
			npcSearchLog("{} resolveSpawnLocation() return {} (from spawnData)", NPC_SEARCH_TAG, loc);
			return loc;
		}
		
		if (spawn instanceof MultiSpawn multi)
		{
			int[][] coords = multi.getCoords();
			if (coords != null && coords.length > 0)
			{
				int bestIdx = 0;
				long bestDist = Long.MAX_VALUE;
				for (int i = 0; i < coords.length; i++)
				{
					final long dx = coords[i][0] - player.getX();
					final long dy = coords[i][1] - player.getY();
					final long dist = dx * dx + dy * dy;
					if (dist < bestDist)
					{
						bestDist = dist;
						bestIdx = i;
					}
				}
				Location loc = new Location(coords[bestIdx][0], coords[bestIdx][1], coords[bestIdx].length > 2 ? coords[bestIdx][2] : 0);
				npcSearchLog("{} resolveSpawnLocation() return {} (from MultiSpawn coords, idx={})", NPC_SEARCH_TAG, loc, bestIdx);
				return loc;
			}
		}
		
		var spawnLoc = spawn.getSpawnLocation();
		if (spawnLoc != null)
		{
			Location loc = new Location(spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ());
			npcSearchLog("{} resolveSpawnLocation() return {} (from getSpawnLocation())", NPC_SEARCH_TAG, loc);
			return loc;
		}
		
		npcSearchLog("{} resolveSpawnLocation() return null (no source)", NPC_SEARCH_TAG);
		return null;
	}
	
	/**
	 * @param npcAlias : The {@link Npc} ID.
	 * @return The first found {@link ASpawn} of given {@link Npc}.
	 */
	public final ASpawn getSpawn(String npcAlias)
	{
		ASpawn result = _makers.stream().flatMap(nm -> nm.getSpawns().stream()).filter(ms -> ms.getTemplate().getAlias().equalsIgnoreCase(npcAlias)).findFirst().orElse(null);
		if (result == null)
			result = _spawns.stream().filter(s -> s.getTemplate().getAlias().equalsIgnoreCase(npcAlias)).findFirst().orElse(null);
		
		return result;
	}
	
	public static final SpawnManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SpawnManager INSTANCE = new SpawnManager();
	}
}
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
package ext.mods.gameserver.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.commons.logging.CLogger;

import ext.mods.gameserver.data.sql.PlayerInfoTable;
import ext.mods.gameserver.data.xml.RestartPointData;
import ext.mods.gameserver.enums.SayType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Pet;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.restart.RestartPoint;
import ext.mods.gameserver.model.spawn.ASpawn;
import ext.mods.gameserver.model.zone.type.subtype.ZoneType;
import ext.mods.gameserver.network.GameClient;
import ext.mods.gameserver.network.serverpackets.CreatureSay;
import ext.mods.gameserver.network.serverpackets.L2GameServerPacket;

public final class World
{
	private static final CLogger LOGGER = new CLogger(World.class.getName());
	
	public volatile int MAX_CONNECTED_COUNT = 0;
	public volatile int OFFLINE_TRADE_COUNT = 0;
	
	public static final int TILE_X_MIN = 16;
	public static final int TILE_X_MAX = 26;
	public static final int TILE_Y_MIN = 10;
	public static final int TILE_Y_MAX = 25;
	
	public static final int TILE_SIZE = 32768;
	public static final int WORLD_X_MIN = (TILE_X_MIN - 20) * TILE_SIZE;
	public static final int WORLD_X_MAX = (TILE_X_MAX - 19) * TILE_SIZE - 1;
	public static final int WORLD_Y_MIN = (TILE_Y_MIN - 18) * TILE_SIZE;
	public static final int WORLD_Y_MAX = (TILE_Y_MAX - 17) * TILE_SIZE - 1;
	public static final int WORLD_Z_MAX = 16410;
	
	public static final int REGION_SIZE = 2048;
	public static final int REGIONS_X = (WORLD_X_MAX - WORLD_X_MIN + 1) / REGION_SIZE;
	public static final int REGIONS_Y = (WORLD_Y_MAX - WORLD_Y_MIN + 1) / REGION_SIZE;
	public static final int REGION_X_OFFSET = Math.abs(WORLD_X_MIN / REGION_SIZE);
	public static final int REGION_Y_OFFSET = Math.abs(WORLD_Y_MIN / REGION_SIZE);
	
	private final Map<Integer, WorldObject> _objects = new ConcurrentHashMap<>();
	private final Map<Integer, Pet> _pets = new ConcurrentHashMap<>();
	private final Map<Integer, Player> _players = new ConcurrentHashMap<>();
	
	private int _maxOnline;
	
	private final WorldRegion[][] _worldRegions = new WorldRegion[REGIONS_X][REGIONS_Y];
	
	protected World()
	{
		for (int x = 0; x < REGIONS_X; x++)
		{
			for (int y = 0; y < REGIONS_Y; y++)
				_worldRegions[x][y] = new WorldRegion(x, y);
		}
		
		LOGGER.info("World grid ({} by {}) is now set up.", REGIONS_X, REGIONS_Y);
	}
	
	public void addObject(WorldObject object)
	{
		_objects.putIfAbsent(object.getObjectId(), object);
	}
	
	public void removeObject(WorldObject object)
	{
		_objects.remove(object.getObjectId());
	}
	
	public void removeObjects(Set<? extends WorldObject> objects)
	{
		if (objects == null || objects.isEmpty())
			return;
		
		_objects.keySet().removeAll(objects.stream().map(WorldObject::getObjectId).toList());
	}
	
	public Collection<WorldObject> getObjects()
	{
		return _objects.values();
	}
	
	public WorldObject getObject(int objectId)
	{
		return _objects.get(objectId);
	}
	
	public Npc getNpc(int npcId)
	{
		return (Npc) _objects.values().stream().filter(o -> (o instanceof Npc npc && npc.getNpcId() == npcId)).findFirst().orElse(null);
	}
	
	public List<Npc> getNpcs(int npcId)
	{
		return _objects.values().stream().filter(o -> (o instanceof Npc npc && npc.getNpcId() == npcId)).map(Npc.class::cast).toList();
	}
	
	public void addPlayer(Player cha)
	{
		_players.putIfAbsent(cha.getObjectId(), cha);
	}
	
	public void removePlayer(Player cha)
	{
		_players.remove(cha.getObjectId());
	}
	
	public Collection<Player> getPlayers()
	{
		return _players.values();
	}
	
	public Player getPlayer(String name)
	{
		return _players.get(PlayerInfoTable.getInstance().getPlayerObjectId(name));
	}
	
	public Player getPlayer(int objectId)
	{
		return _players.get(objectId);
	}
	
	public void addPet(int ownerId, Pet pet)
	{
		_pets.putIfAbsent(ownerId, pet);
	}
	
	public void removePet(int ownerId)
	{
		_pets.remove(ownerId);
	}
	
	public Pet getPet(int ownerId)
	{
		return _pets.get(ownerId);
	}
	
	public static int getRegionX(int regionX)
	{
		return (regionX - REGION_X_OFFSET) * REGION_SIZE;
	}
	
	public static int getRegionY(int regionY)
	{
		return (regionY - REGION_Y_OFFSET) * REGION_SIZE;
	}
	
	/**
	 * @param loc : The Location to check.
	 * @return a {@link WorldRegion} based on a {@link Location}.
	 */
	public WorldRegion getRegion(Location loc)
	{
		return getRegion(loc.getX(), loc.getY());
	}
	
	/**
	 * @param x : The X point to check.
	 * @param y : The Y point to check.
	 * @return a {@link WorldRegion} based on X/Y coordinates.
	 */
	public WorldRegion getRegion(int x, int y)
	{
		return _worldRegions[(x - WORLD_X_MIN) / REGION_SIZE][(y - WORLD_Y_MIN) / REGION_SIZE];
	}
	
	/**
	 * BEWARE : This method is kinda expensive, avoid to use it.
	 * @param zone : The ZoneType to check.
	 * @return the first encountered {@link WorldRegion} based on a {@link ZoneType}.
	 */
	public WorldRegion getRegion(ZoneType zone)
	{
		for (int x = 0; x < REGIONS_X; x++)
		{
			for (int y = 0; y < REGIONS_Y; y++)
			{
				final WorldRegion region = _worldRegions[x][y];
				if (region.containsZone(zone.getId()))
					return region;
			}
		}
		return null;
	}
	
	/**
	 * @return the whole 2d array containing the world regions used by ZoneData.java to setup zones inside the world regions
	 */
	public WorldRegion[][] getWorldRegions()
	{
		return _worldRegions;
	}
	
	public List<Creature> getAroundCharacters(int x, int y, int rangeX, int rangeY)
	{
		final List<Creature> result = new ArrayList<>();
		final int startX = (x - rangeX - WORLD_X_MIN) / REGION_SIZE;
		final int endX = (x + rangeX - WORLD_X_MIN) / REGION_SIZE;
		final int startY = (y - rangeY - WORLD_Y_MIN) / REGION_SIZE;
		final int endY = (y + rangeY - WORLD_Y_MIN) / REGION_SIZE;

		for (int rx = Math.max(0, startX); rx <= Math.min(REGIONS_X - 1, endX); rx++)
		{
			for (int ry = Math.max(0, startY); ry <= Math.min(REGIONS_Y - 1, endY); ry++)
			{
				for (WorldObject obj : _worldRegions[rx][ry].getObjects())
				{
					if (obj instanceof Creature creature)
					{
						if (Math.abs(creature.getX() - x) <= rangeX && Math.abs(creature.getY() - y) <= rangeY)
							result.add(creature);
					}
				}
			}
		}
		return result;
	}

	
	/**
	 * Delete all spawns in the world.
	 */
	public void deleteVisibleNpcSpawns()
	{
		LOGGER.info("Deleting all visible NPCs.");
		for (int i = 0; i < REGIONS_X; i++)
		{
			for (int j = 0; j < REGIONS_Y; j++)
			{
				for (WorldObject obj : _worldRegions[i][j].getObjects())
				{
					if (obj instanceof Npc npc)
					{
						final ASpawn spawn = npc.getSpawn();
						if (spawn != null)
							spawn.doDelete();
						else
							npc.deleteMe();
					}
				}
			}
		}
		LOGGER.info("All visibles NPCs are now deleted.");
	}
	
	/**
	 * Send a packet to all online {@link Player}s present in the {@link World}.
	 * @param packet : The packet to send.
	 */
	public static void toAllOnlinePlayers(L2GameServerPacket packet)
	{
		for (Player player : World.getInstance().getPlayers())
		{
			if (player.isOnline())
				player.sendPacket(packet);
		}
	}
	
	public static void announceToOnlinePlayers(String text)
	{
		toAllOnlinePlayers(new CreatureSay(SayType.ANNOUNCEMENT, null, text));
	}
	
	public static void announceToOnlinePlayers(String text, boolean critical)
	{
		toAllOnlinePlayers(new CreatureSay((critical) ? SayType.CRITICAL_ANNOUNCE : SayType.ANNOUNCEMENT, null, text));
	}
	
	public static void broadcastToSameRegion(Creature creature, L2GameServerPacket packet)
	{
		final RestartPoint creatureRp = RestartPointData.getInstance().getRestartPoint(creature);
		
		for (Player pl : World.getInstance().getPlayers())
		{
			if (!pl.isOnline())
				continue;
			
			final RestartPoint plRp = RestartPointData.getInstance().getRestartPoint(pl);
			if (creatureRp == plRp)
				pl.sendPacket(packet);
		}
	}
	
	/**
	 * Verify if a x/y coordinate is out of {@link World} valid coordinates.
	 * @param x : The X coordinate to test.
	 * @param y : The Y coordinate to test.
	 * @return True if the tested x/y coordinates are out of {@link World} valid coordinates, false otherwise.
	 */
	public static boolean isOutOfWorld(int x, int y)
	{
		return isOutOfWorld(x, x, y, y);
	}
	
	/**
	 * Verify if tested coordinates are out of {@link World} valid coordinates.
	 * @param minX : The minimum X coordinate to test.
	 * @param maxX : The maximum X coordinate to test.
	 * @param minY : The minimum Y coordinate to test.
	 * @param maxY : The maximum Y coordinate to test.
	 * @return True if the tested coordinates are out of {@link World} valid coordinates, false otherwise.
	 */
	public static boolean isOutOfWorld(int minX, int maxX, int minY, int maxY)
	{
		return minX < WORLD_X_MIN || maxX > WORLD_X_MAX || minY < WORLD_Y_MIN || maxY > WORLD_Y_MAX;
	}
	
	public int getTraderCount()
	{
		int traderCount = 0;
		
		for (Player player : _players.values())
		{
			if (player != null && player.isOnline())
			{
				GameClient client = player.getClient();
				if (player.isInStoreMode() || client != null && client.isDetached())
					traderCount++;
			}
		}
		
		return traderCount;
	}
	
	public int getMaxOnline()
	{
		int currentOnline = _players.size();
		if (currentOnline > _maxOnline)
			_maxOnline = currentOnline;
		
		return _maxOnline;
	}
	
	public int getOnlinePlayerCount()
	{
		int onlinePlayerCount = 0;
		
		for (Player player : _players.values())
		{
			if (player != null && player.isOnline())
			{
				GameClient client = player.getClient();
				if (client != null && !client.isDetached())
					onlinePlayerCount++;
			}
		}
		
		return onlinePlayerCount;
	}
	
	public static void announceToOnlinePlayers(int id, String name)
	{
		for (Player player : World.getInstance().getPlayers())
		{
			if (player.isOnline())
				player.sendPacket(new CreatureSay(SayType.ANNOUNCEMENT, null, player.getSysString(id, name)));
		}
	}
	
	public static void announceToOnlinePlayers(int id, Object... args)
	{
		for (Player player : World.getInstance().getPlayers())
		{
			if (player.isOnline())
				player.sendPacket(new CreatureSay(SayType.ANNOUNCEMENT, null, player.getSysString(id, args)));
		}
	}
	
	public static World getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final World INSTANCE = new World();
	}
}
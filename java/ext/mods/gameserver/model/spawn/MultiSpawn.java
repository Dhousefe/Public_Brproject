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
package ext.mods.gameserver.model.spawn;

import java.io.InvalidClassException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import ext.mods.commons.geometry.Territory;
import ext.mods.commons.random.Rnd;

import ext.mods.Config;
import ext.mods.gameserver.data.xml.StaticSpawnData;
import ext.mods.gameserver.enums.MakerSpawnTime;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.instance.GrandBoss;
import ext.mods.gameserver.model.actor.instance.RaidBoss;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.location.SpawnLocation;
import ext.mods.gameserver.model.memo.SpawnMemo;
import ext.mods.gameserver.model.records.PrivateData;
import ext.mods.gameserver.model.records.custom.StaticSpawn;
import ext.mods.gameserver.network.serverpackets.Earthquake;
import ext.mods.gameserver.taskmanager.MakerSpawnScheduleTaskManager;

/**
 * This class manages the spawn and respawn of {@link Npc}s defined by {@link NpcMaker} in a territory based system.<br>
 * The {@link SpawnLocation} can be:
 * <ul>
 * <li>Fixed coordinates.
 * <li>Random one of defined coordinates.
 * <li>Random coordinate from a {@link Territory} of linked {@link NpcMaker}.
 * </ul>
 */
public final class MultiSpawn extends ASpawn
{
	private static final int RANDOM_WALK_LOOP_LIMIT = 3;
	
	private final NpcMaker _npcMaker;
	private final int _total;
	private final int[][] _coords;
	
	private final Set<Npc> _npcs = ConcurrentHashMap.newKeySet();
	
	private AtomicInteger _spawnedCount = new AtomicInteger(0);
	
	public MultiSpawn(NpcMaker npcMaker, NpcTemplate template, int total, int respawnDelay, int respawnRandom, List<PrivateData> privateData, SpawnMemo aiParams, int[][] coords, SpawnData spawnData) throws SecurityException, ClassNotFoundException, NoSuchMethodException, InvalidClassException
	{
		super(template);
		
		_respawnDelay = Math.max(0, respawnDelay);
		_respawnRandom = Math.min(respawnDelay, Math.max(0, respawnRandom));
		
		_privateData = privateData;
		_aiParams = aiParams;
		
		_npcMaker = npcMaker;
		_coords = coords;
		_spawnData = spawnData;
		
		if (_spawnData != null)
			_total = 1;
		else if (_coords != null)
			_total = total;
		else
			_total = (int) Math.round(total * Config.SPAWN_MULTIPLIER);
	}
	
	@Override
	public Npc getNpc()
	{
		if (_npcs.isEmpty())
			return null;
		
		for (Npc npc : _npcs)
		{
			if (!npc.isDecayed())
				return npc;
		}
		
		return null;
	}
	
	@Override
	public SpawnLocation getSpawnLocation()
	{
		if (_coords == null)
			return _npcMaker.getTerritory().getRandomGeoLocation(_npcMaker.getBannedTerritory());
		
		if (_coords.length == 1)
		{
			final SpawnLocation spawnLoc = new SpawnLocation(_coords[0][0], _coords[0][1], _coords[0][2], _coords[0][3]);
			spawnLoc.setZ(GeoEngine.getInstance().getHeight(spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ() - Config.NPC_Z_INDEX));
			return spawnLoc;
		}
		
		int chance = Rnd.get(100);
		for (int[] coord : _coords)
		{
			chance -= coord[4];
			if (chance < 0)
			{
				final SpawnLocation spawnLoc = new SpawnLocation(coord[0], coord[1], coord[2], Rnd.get(65536));
				spawnLoc.setZ(GeoEngine.getInstance().getHeight(spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ() - Config.NPC_Z_INDEX));
				return spawnLoc;
			}
		}
		
		return null;
	}
	
	@Override
	public Location getRandomWalkLocation(Npc npc, int offset)
	{
		final Location loc = npc.getPosition().clone();
		
		if (!_npcMaker.getTerritory().isInside(loc))
			return _npcMaker.getTerritory().getRandomGeoLocation();
		
		for (int loop = 0; loop < RANDOM_WALK_LOOP_LIMIT; loop++)
		{
			loc.set(npc.getPosition());
			loc.addRandomOffsetBetween(offset / Rnd.get(2, 4), offset);
			
			if (!_npcMaker.getTerritory().isInside(loc))
				continue;
			
			if (_npcMaker.getBannedTerritory() != null && _npcMaker.getBannedTerritory().isInside(loc))
				continue;
			
			loc.set(GeoEngine.getInstance().getValidLocation(npc, loc));
			return loc;
		}
		
		loc.set(npc.getSpawnLocation());
		
		return loc;
	}
	
	@Override
	public boolean isInMyTerritory(WorldObject worldObject)
	{
		final Location loc = worldObject.getPosition().clone();
		
		if (_npcMaker.getBannedTerritory() != null && _npcMaker.getBannedTerritory().isInside(loc))
			return false;
		
		return _npcMaker.getTerritory().isInside(loc);
	}
	
	@Override
	public Npc doSpawn(boolean isSummonSpawn, Creature summoner)
	{
		Npc toRespawn = null;
		
		for (Npc npc : _npcs)
		{
			if (npc.isReadyForRespawn())
			{
				toRespawn = npc;
				break;
			}
		}
		
		if (toRespawn != null)
		{
			toRespawn.setReadyForRespawn(false);
			
			if (_npcs.remove(toRespawn))
			{
				doRespawn(toRespawn);
				_npcs.add(toRespawn);
			}
			else
				toRespawn = null;
		}
		
		if (toRespawn == null)
		{
			toRespawn = super.doSpawn(isSummonSpawn, summoner);
			if (toRespawn == null)
			{
				LOGGER.warn("Can not spawn id {} from maker {}.", getNpcId(), _npcMaker.getName());
				return null;
			}
			_npcs.add(toRespawn);
		}
		
		final MakerSpawnTime mst = _npcMaker.getMakerSpawnTime();
		if (mst != null && mst != MakerSpawnTime.DOOR_OPEN)
		{
			final String[] params = _npcMaker.getMakerSpawnTimeParams();
			if (params != null)
				toRespawn.setResidence(params[0]);
		}
		
		return toRespawn;
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		StaticSpawn staticSpawn = StaticSpawnData.getInstance().getById(getTemplate().getNpcId());
		if (staticSpawn != null && staticSpawn.isEnabled())
		{
			if (staticSpawn.earthQuake())
				npc.broadcastPacket(new Earthquake(npc, 20, 10, true));
			
			if (staticSpawn.announce())
				World.announceToOnlinePlayers(10_047, npc.getName());
		}
		
		if (npc instanceof RaidBoss raidboss && Config.ANNOUNCE_SPAWN_RAIDBOSS)
			World.announceToOnlinePlayers(10_237, raidboss.getName(), raidboss.getStatus().getLevel());
		
		if (npc instanceof GrandBoss grandboss && Config.ANNOUNCE_SPAWN_GRANDBOSS)
			World.announceToOnlinePlayers(10_238, grandboss.getName(), grandboss.getStatus().getLevel());
		
		npc.setReadyForRespawn(false);
		
		_npcMaker.onSpawn(npc);
		
		doSave();
	}
	
	@Override
	public void doDelete()
	{
		Set<Npc> tmpNpcs = Set.copyOf(_npcs);
		
		_npcs.clear();
		
		tmpNpcs.forEach(npc ->
		{
			npc.cancelRespawn();
			
			if (npc.isMaster())
				npc.getMinions().forEach(Npc::deleteMe);
			
			npc.deleteMe();
		});
		

		_spawnedCount.set(0);
		
		if (_spawnData != null)
			_spawnData.setStatus((byte) -1);
	}
	
	@Override
	public long calculateRespawnDelay()
	{
		if (_spawnData != null)
		{
			StaticSpawn staticSpawn = StaticSpawnData.getInstance().getById(getTemplate().getNpcId());
			if (staticSpawn != null && staticSpawn.isEnabled())
				return (staticSpawn.calcNextDate() - System.currentTimeMillis()) / 1000 + Rnd.get(staticSpawn.randomTime());
		}
		
		return super.calculateRespawnDelay();
	}
	
	@Override
	public void onDecay(Npc npc)
	{
		decreaseSpawnedCount(1);
		_npcMaker.onDecay(npc);
		
		if (getRespawnDelay() > 0)
		{
			long respawnDelay = calculateBossRespawnDelayMs(npc);
			if (respawnDelay == 0L)
			{
				SpawnData spawnData = npc.getSpawn().getSpawnData();
				if (spawnData != null && spawnData.getRespawnTime() > System.currentTimeMillis())
					respawnDelay = (spawnData.getRespawnTime() - System.currentTimeMillis());
				
				if (respawnDelay == 0L)
					respawnDelay = (npc.getSpawn().calculateRespawnDelay() * 1000L);
			}
			
			if (_spawnData != null)
				_spawnData.setRespawn(respawnDelay);
			
			npc.setReadyForRespawn(true);

			doSave();
		}
		else
		{
			_npcs.remove(npc);
		}
	}
	
	@Override
	public String toString()
	{
		return "MultiSpawn [id=" + getNpcId() + "]";
	}
	
	@Override
	public String getDescription()
	{
		return "NpcMaker: " + _npcMaker.getName();
	}
	
	@Override
	public void updateSpawnData()
	{
		if (_spawnData == null)
			return;
		
		_npcs.forEach(npc -> _spawnData.setStats(npc));
	}
	
	@Override
	public void sendScriptEvent(int eventId, int arg1, int arg2)
	{
		_npcs.forEach(npc -> npc.sendScriptEvent(eventId, arg1, arg2));
	}
	
	public NpcMaker getNpcMaker()
	{
		return _npcMaker;
	}
	
	public int[][] getCoords()
	{
		return _coords;
	}
	
	public int getTotal()
	{
		return _total;
	}
	
	public Set<Npc> getNpcs()
	{
		return _npcs;
	}
	
	public Set<Npc> getDecayedNpcs()
	{
		Set<Npc> decayedNpcs = ConcurrentHashMap.newKeySet();
		for (Npc npc : _npcs)
		{
			if (npc.isDecayed())
				decayedNpcs.add(npc);
		}
		
		return decayedNpcs;
	}
	
	public boolean increaseSpawnedCount(int count)
	{
		final int newspawnedCount = _spawnedCount.get() + count;
		if (newspawnedCount >= 0 && newspawnedCount <= _total)
		{
			_spawnedCount.set(newspawnedCount);
			return true;
		}
		return false;
	}
	
	public boolean decreaseSpawnedCount(int count)
	{
		final int newspawnedCount = _spawnedCount.get() - count;
		if (newspawnedCount >= 0 && newspawnedCount <= _total)
		{
			_spawnedCount.set(newspawnedCount);
			return true;
		}
		return false;
	}
	
	public int getSpawnedCount()
	{
		return _spawnedCount.get();
	}
	
	public Set<Npc> doSpawn(int count, boolean isSummonSpawn)
	{
		for (int i = 0; i < count; i++)
			doSpawn(isSummonSpawn, null);
		
		return _npcs;
	}
	
	public void scheduleSpawn(long delay)
	{
		MakerSpawnScheduleTaskManager.getInstance().addSpawn(this, delay);
	}
	
	public void cancelScheduledSpawns()
	{
		MakerSpawnScheduleTaskManager.getInstance().cancelMakerRespawns(this);
	}
	
	public int getRespawningNpcCount()
	{
		return MakerSpawnScheduleTaskManager.getInstance().getRespawningNpcCount(this);
	}
	
	public void loadDBNpcInfo()
	{
		_npcMaker.getMaker().onNpcDBInfo(this, _spawnData, _npcMaker);
	}
}
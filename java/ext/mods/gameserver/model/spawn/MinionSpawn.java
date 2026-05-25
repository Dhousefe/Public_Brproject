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

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.Config;
import ext.mods.gameserver.idfactory.IdFactory;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.instance.Monster;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.location.SpawnLocation;

/**
 * This class manages the spawn and respawn of a single {@link Npc} at given {@link SpawnLocation}.
 */
public final class MinionSpawn extends ASpawn
{
	private final SpawnLocation _loc = new SpawnLocation(0, 0, 0, 0);
	
	private Npc _npc;
	
	private final Npc _master;
	
	public MinionSpawn(int id, Npc master) throws SecurityException, ClassNotFoundException, NoSuchMethodException, InvalidClassException
	{
		super(id);
		
		_master = master;
	}
	
	public MinionSpawn(NpcTemplate template, Npc master) throws SecurityException, ClassNotFoundException, NoSuchMethodException, InvalidClassException
	{
		super(template);
		
		_master = master;
	}
	
	@Override
	public SpawnLocation getSpawnLocation()
	{
		if (!_loc.equals(SpawnLocation.DUMMY_SPAWNLOC))
			return _loc;
		
		final int minOffset = (int) (_master.getCollisionRadius() + 30);
		final int maxOffset = (int) (100 + _template.getCollisionRadius() + _master.getCollisionRadius());
		
		final SpawnLocation loc = _master.getPosition().clone();
		loc.addRandomOffsetBetween(minOffset, maxOffset);
		loc.set(GeoEngine.getInstance().getValidLocation(_master, loc));
		
		return loc;
	}
	
	@Override
	public Location getRandomWalkLocation(Npc npc, int offset)
	{
		final Location loc = npc.getPosition().clone();
		loc.addRandomOffset(offset);
		loc.set(GeoEngine.getInstance().getValidLocation(npc, loc));
		
		return loc;
	}
	
	@Override
	public boolean isInMyTerritory(WorldObject worldObject)
	{
		return true;
	}
	
	@Override
	public void doDelete()
	{
		if (_npc == null)
			return;
		
		_npc.cancelRespawn();
		_npc.deleteMe();
		_npc = null;
	}
	
	@Override
	public void onDecay(Npc npc)
	{
		if (_master != null && getRespawnDelay() <= 0)
		{
			npc.setMaster(null);
			
			_master.getMinions().remove(npc);
		}
	}
	
	@Override
	public void doRespawn(Npc npc)
	{
		if (_master == null || _master.isDead())
			return;
		
		npc.cancelRespawn();
		
		if (npc.isDecayed())
		{
			npc.refreshID();
			
			initializeAndSpawn(npc);
		}
	}
	
	@Override
	public long calculateRespawnDelay()
	{
		int respawnTime = _respawnDelay;
		
		if (_respawnRandom > 0)
			respawnTime += Rnd.get(-_respawnRandom, _respawnRandom);
		
		return respawnTime;
	}
	
	@Override
	public String toString()
	{
		return "Spawn [id=" + getNpcId() + "]";
	}
	
	@Override
	public String getDescription()
	{
		return "Master: " + _master;
	}
	
	@Override
	public void updateSpawnData()
	{
	}
	
	@Override
	public Npc doSpawn(boolean isSummonSpawn, Creature summoner)
	{
		try
		{
			final Npc npc = (Npc) _constructor.newInstance(IdFactory.getInstance().getNextId(), _template);
			
			if (summoner != null)
				npc._summoner = summoner;
			
			npc.setSpawn(this);
			npc.setShowSummonAnimation(isSummonSpawn);
			
			if (_master != null)
			{
				npc.setMaster(_master);
				
				if (_master.isRaidBoss() && npc instanceof Monster monster)
					monster.setRaidRelated();
			}
			
			return initializeAndSpawn(npc);
		}
		catch (Exception e)
		{
			LOGGER.warn("Error during spawn, NPC id={}", e, _template.getNpcId());
			return null;
		}
	}
	
	public Npc doSpawn(boolean isSummonSpawn, int param1, int param2, int param3)
	{
		try
		{
			final Npc npc = (Npc) _constructor.newInstance(IdFactory.getInstance().getNextId(), _template);
			npc._param1 = param1;
			npc._param2 = param2;
			npc._param3 = param3;
			
			npc.setSpawn(this);
			npc.setShowSummonAnimation(isSummonSpawn);
			
			if (_master != null)
			{
				npc.setMaster(_master);
				
				if (_master.isRaidBoss() && npc instanceof Monster monster)
					monster.setRaidRelated();
			}
			
			return initializeAndSpawn(npc);
		}
		catch (Exception e)
		{
			LOGGER.warn("Error during spawn, NPC id={}", e, _template.getNpcId());
			return null;
		}
	}
	
	@Override
	public Npc initializeAndSpawn(Npc npc)
	{
		npc.stopAllEffects();
		npc.setScriptValue(0);
		
		npc.setIsDead(false);
		npc.setDecayed(false);
		
		npc.getStatus().initializeValues();
		
		SpawnLocation loc = getSpawnLocation();
		if (loc == null)
		{
			LOGGER.warn("{} misses location informations.", this);
			return null;
		}
		
		double maxHp = npc.getStatus().getMaxHp();
		double maxMp = npc.getStatus().getMaxMp();
		
		npc.getStatus().setHpMp(maxHp, maxMp);
		
		npc.setSpawnLocation(loc);
		npc.spawnMe(loc);
		
		return npc;
	}
	
	/**
	 * @return the {@link Npc} instance of this {@link MinionSpawn}.
	 */
	@Override
	public Npc getNpc()
	{
		return _npc;
	}
	
	@Override
	public void sendScriptEvent(int eventId, int arg1, int arg2)
	{
		_npc.sendScriptEvent(eventId, arg1, arg2);
	}
	
	/**
	 * @return the {@link Npc} master of this {@link MinionSpawn}.
	 */
	public Npc getMaster()
	{
		return _master;
	}
	
	public void setLoc(SpawnLocation loc)
	{
		final int biasedZ = loc.getZ() - Config.NPC_Z_INDEX;
		_loc.set(loc.getX(), loc.getY(), GeoEngine.getInstance().getHeight(loc.getX(), loc.getY(), biasedZ), loc.getHeading());
	}
	
	public void setLoc(int x, int y, int z, int heading)
	{
		_loc.set(x, y, GeoEngine.getInstance().getHeight(x, y, z - Config.NPC_Z_INDEX), heading);
	}
}
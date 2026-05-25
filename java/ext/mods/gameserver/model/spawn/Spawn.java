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

import ext.mods.Config;
import ext.mods.gameserver.data.manager.SpawnManager;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.location.SpawnLocation;

/**
 * This class manages the spawn and respawn of a single {@link Npc} at given {@link SpawnLocation}.
 */
public final class Spawn extends ASpawn
{
	private final SpawnLocation _loc = new SpawnLocation(0, 0, 0, 0);
	
	private Npc _npc;
	private boolean _disableTerritoryCheck;
	
	public Spawn(NpcTemplate template, boolean disableTerritoryCheck) throws SecurityException, ClassNotFoundException, NoSuchMethodException, InvalidClassException
	{
		super(template);
		
		_disableTerritoryCheck = disableTerritoryCheck;
	}
	
	public Spawn(int id, boolean disableTerritoryCheck) throws SecurityException, ClassNotFoundException, NoSuchMethodException, InvalidClassException
	{
		super(id);
		
		_disableTerritoryCheck = disableTerritoryCheck;
	}
	
	public Spawn(NpcTemplate template) throws SecurityException, ClassNotFoundException, NoSuchMethodException, InvalidClassException
	{
		super(template);
	}
	
	public Spawn(int id) throws SecurityException, ClassNotFoundException, NoSuchMethodException, InvalidClassException
	{
		super(id);
	}
	
	@Override
	public SpawnLocation getSpawnLocation()
	{
		final SpawnLocation loc = _loc.clone();
		
		if (loc.getHeading() < 0)
			loc.setHeading(Rnd.get(65536));
		
		return loc;
	}
	
	@Override
	public Location getRandomWalkLocation(Npc npc, int offset)
	{
		final Location loc = _loc.clone();
		
		loc.addRandomOffset(offset);
		
		loc.set(GeoEngine.getInstance().getValidLocation(npc, loc));
		return loc;
	}
	
	@Override
	public boolean isInMyTerritory(WorldObject worldObject)
	{
		if (_disableTerritoryCheck)
			return true;
		
		return worldObject.isIn3DRadius(_loc, Config.MAX_DRIFT_RANGE);
	}
	
	@Override
	public Npc doSpawn(boolean isSummonSpawn, Creature summoner)
	{
		_npc = super.doSpawn(isSummonSpawn, summoner);
		if (_npc == null)
		{
			LOGGER.warn("Can not spawn id {} from loc {}.", getNpcId(), _loc);
		}
		else
			SpawnManager.getInstance().addSpawn(this);
		
		return _npc;
	}
	
	@Override
	public void doDelete()
	{
		if (_npc == null)
			return;
		
		if (_spawnData != null)
			_spawnData.setStatus((byte) -1);
		
		if (_npc.isMaster())
			_npc.getMinions().forEach(Npc::deleteMe);
		
		_npc.cancelRespawn();
		_npc.deleteMe();
		_npc = null;
	}
	
	@Override
	public void onDecay(Npc npc)
	{
		if (getRespawnDelay() > 0)
		{
			long respawnDelay = calculateBossRespawnDelayMs(npc);
			if (respawnDelay == 0L)
				respawnDelay = calculateRespawnDelay() * 1000;
			
			if (_spawnData != null)
				_spawnData.setRespawn(respawnDelay);
			else
				npc.scheduleRespawn(respawnDelay);
		}
		else
			SpawnManager.getInstance().deleteSpawn(this);
	}
	
	@Override
	public String toString()
	{
		return "Spawn [id=" + getNpcId() + "]";
	}
	
	@Override
	public String getDescription()
	{
		return "Location: " + _loc;
	}
	
	@Override
	public Npc getNpc()
	{
		return _npc;
	}
	
	@Override
	public void updateSpawnData()
	{
		if (_spawnData == null)
			return;
		
		_spawnData.setStats(_npc);
	}
	
	@Override
	public void sendScriptEvent(int eventId, int arg1, int arg2)
	{
		_npc.sendScriptEvent(eventId, arg1, arg2);
	}
	
	/**
	 * Sets the {@link SpawnLocation} of this {@link Spawn}.
	 * @param loc : The SpawnLocation to set.
	 */
	public void setLoc(SpawnLocation loc)
	{
		final int biasedZ = loc.getZ() - Config.NPC_Z_INDEX;
		_loc.set(loc.getX(), loc.getY(), GeoEngine.getInstance().getHeight(loc.getX(), loc.getY(), biasedZ), loc.getHeading());
	}
	
	/**
	 * Sets the {@link SpawnLocation} of this {@link Spawn} using separate coordinates.
	 * @param x : X coordinate.
	 * @param y : Y coordinate.
	 * @param z : Z coordinate.
	 * @param heading : Heading.
	 */
	public void setLoc(int x, int y, int z, int heading)
	{
		_loc.set(x, y, GeoEngine.getInstance().getHeight(x, y, z - Config.NPC_Z_INDEX), heading);
	}
	
	/**
	 * @return the X coordinate of the {@link SpawnLocation}.
	 */
	public int getLocX()
	{
		return _loc.getX();
	}
	
	/**
	 * @return the Y coordinate of the {@link SpawnLocation}.
	 */
	public int getLocY()
	{
		return _loc.getY();
	}
	
	/**
	 * @return the Z coordinate of the {@link SpawnLocation}.
	 */
	public int getLocZ()
	{
		return _loc.getZ();
	}
	
	/**
	 * @return the heading coordinate of the {@link SpawnLocation}.
	 */
	public int getHeading()
	{
		return _loc.getHeading();
	}
}
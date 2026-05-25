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
package ext.mods.gameserver.model.residence;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.data.xml.DoorData;
import ext.mods.gameserver.enums.SpawnType;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Door;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.residence.clanhall.ClanHall;

public abstract class Residence
{
	protected final int _id;
	protected final int _parentId;
	
	protected final String _alias;
	protected final String _name;
	
	protected final Map<SpawnType, List<Location>> _spawns = new EnumMap<>(SpawnType.class);
	
	protected final List<Door> _doors = new ArrayList<>();
	protected final List<Integer> _npcs = new ArrayList<>();
	
	protected int _ownerId;
	protected String _townName;
	
	protected final int _defaultTaxRate;
	protected final int _taxSysgetRate;
	protected final int _tributeRate;
	
	protected Residence(StatSet set)
	{
		_id = set.getInteger("id");
		_parentId = set.getInteger("parentId");
		
		_alias = set.getString("alias");
		_name = set.getString("name");
		
		_defaultTaxRate = set.getInteger("taxRate");
		_taxSysgetRate = set.getInteger("taxSysgetRate");
		_tributeRate = set.getInteger("tributeRate");
	}
	
	public final int getId()
	{
		return _id;
	}
	
	public final int getParentId()
	{
		return _parentId;
	}
	
	public final String getAlias()
	{
		return _alias;
	}
	
	public final String getName()
	{
		return _name;
	}
	
	/**
	 * Add a {@link Location} into the dedicated {@link SpawnType} {@link List}.<br>
	 * <br>
	 * If the key doesn't exist, generate a new {@link ArrayList}.
	 * @param type : The {@link SpawnType} to test.
	 * @param loc : The {@link Location} to add.
	 */
	public final void addSpawn(SpawnType type, Location loc)
	{
		_spawns.computeIfAbsent(type, k -> new ArrayList<>()).add(loc);
	}
	
	/**
	 * @param type : The {@link SpawnType} to test.
	 * @return the {@link List} of {@link Location}s based on {@link SpawnType} parameter. If that {@link SpawnType} doesn't exist, return the OWNER {@link List} of {@link Location}.
	 */
	public final List<Location> getSpawns(SpawnType type)
	{
		return _spawns.getOrDefault(type, _spawns.get(SpawnType.OWNER));
	}
	
	/**
	 * @param type : The {@link SpawnType} to test.
	 * @return a random {@link Location} based on {@link SpawnType} parameter. If that {@link SpawnType} doesn't exist, return a NORMAL random {@link Location}.
	 */
	public final Location getRndSpawn(SpawnType type)
	{
		return Rnd.get(getSpawns(type));
	}
	
	/**
	 * @return the {@link List} of all {@link Door}s this {@link ClanHall} owns.
	 */
	public final List<Door> getDoors()
	{
		return _doors;
	}
	
	/**
	 * Set {@link Residence} {@link Door}s.
	 * @param doors : The {@link String} to split.
	 */
	public void setDoors(String doors)
	{
		for (String doorName : doors.split(";"))
		{
			final Door door = DoorData.getInstance().getDoor(doorName);
			if (door != null)
			{
				_doors.add(door);
				
				door.setResidence(this);
			}
		}
	}
	
	/**
	 * @param doorId : The id to test.
	 * @return the {@link Door} based on a doorId.
	 */
	public final Door getDoor(int doorId)
	{
		return _doors.stream().filter(d -> d.getDoorId() == doorId).findFirst().orElse(null);
	}
	
	public void closeDoor(Player player, int doorId)
	{
		if (player != null && player.getClanId() != _ownerId)
			return;
		
		final Door door = getDoor(doorId);
		if (door != null)
			door.closeMe();
	}
	
	public void closeDoors()
	{
		_doors.forEach(Door::closeMe);
	}
	
	public void openDoor(Player player, int doorId)
	{
		if (player != null && player.getClanId() != _ownerId)
			return;
		
		final Door door = getDoor(doorId);
		if (door != null)
			door.openMe();
	}
	
	public void openDoors()
	{
		_doors.forEach(Door::openMe);
	}
	
	public List<Integer> getNpcs()
	{
		return _npcs;
	}
	
	public void setNpcs(String npcs)
	{
		for (String npc : npcs.split(";"))
			_npcs.add(Integer.parseInt(npc));
	}
	
	public final int getOwnerId()
	{
		return _ownerId;
	}
	
	public void setOwnerId(int ownerId)
	{
		_ownerId = ownerId;
	}
	
	/**
	 * @return true if this {@link Residence} is free.
	 */
	public boolean isFree()
	{
		return _ownerId == 0;
	}
	
	public String getTownName()
	{
		return _townName;
	}
	
	public final int getDefaultTaxRate()
	{
		return _defaultTaxRate;
	}
	
	public final int getTaxSysgetRate()
	{
		return _taxSysgetRate;
	}
	
	public final int getTributeRate()
	{
		return _tributeRate;
	}
}
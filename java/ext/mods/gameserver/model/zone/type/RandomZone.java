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
package ext.mods.gameserver.model.zone.type;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import ext.mods.Config;
import ext.mods.gameserver.enums.RestartType;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.enums.actors.ClassId;
import ext.mods.gameserver.enums.actors.ClassRace;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.zone.type.subtype.SpawnZoneType;
import ext.mods.gameserver.taskmanager.PvpFlagTaskManager;
import ext.mods.Crypta.RandomManager;

public class RandomZone extends SpawnZoneType
{
	private int _id;
	private String _name;
	private boolean _active = false;
	private boolean _flagzone = false;
	private Set<Integer> _restrictedClasses = new HashSet<>();
	private Set<Integer> _restrictedItems = new HashSet<>();

	private Location _returnLocation = new Location(0, 0, 0);
	
	public RandomZone(int id)
	{
		super(id);
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("id"))
			_id = Integer.parseInt(value);
		else if (name.equals("name"))
			_name = value;
		else if (name.equals("flagzone"))
			_flagzone = Boolean.parseBoolean(value);
		else if (name.equals("restrictedClasses"))
		{
			for (String className : value.split(","))
			{
				int classId = getClassIdFromName(className.trim());
				if (classId > 0)
					_restrictedClasses.add(classId);
			}
		}
		else if (name.equals("restrictedItems"))
		{
			for (String itemIdStr : value.split(","))
				_restrictedItems.add(Integer.parseInt(itemIdStr.trim()));
		}
		
		else if (name.equals("returnLocation"))
		{
			String[] coords = value.split(",");
			if (coords.length == 3)
				_returnLocation = new Location(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]));
		}
		
		else
			super.setParameter(name, value);
	}
	
	@Override
	protected void onEnter(Creature character)
	{
		if (_active)
			character.setInsideZone(ZoneId.RANDOM, true);
		
		if (character instanceof Player)
		{
			Player player = (Player) character;
			
			Object randomManager = RandomManager.getInstance();
			if (randomManager != null)
			{
				ext.mods.FarmEventRandom.holder.RandomZoneData zoneData = (ext.mods.FarmEventRandom.holder.RandomZoneData) RandomManager.getInstance().getZoneDataForZone(this);
				if (zoneData != null && zoneData.isDwarvenOnly())
				{
					if (player.getRace() != ClassRace.DWARF)
					{
						player.sendMessage("Esta zona é exclusiva para classes DWARVEN.");
						
						player.teleportTo(83417, 147991, -3400, 0);
						return;
					}
				}
				
				if (zoneData != null && zoneData.isEnchanterZone())
				{
					if (player.getPvpFlag() == 0)
					{
						player.updatePvPFlag(1);
					}
				}
			}
			
			if (_flagzone)
			{
				if (player.getPvpFlag() == 0)
				{
					player.updatePvPFlag(1);
				}
				PvpFlagTaskManager.getInstance().add(player, Config.PVP_NORMAL_TIME);
			}
			
			if (_restrictedClasses.contains(player.getClassId().getId()))
			{
				player.sendMessage("Your class is restricted in this event zone.");
				player.teleToLocation(_returnLocation);
				return;
			}

			
			for (int itemId : _restrictedItems)
			{
				if (player.getInventory().getItemByItemId(itemId) != null)
				{
					ItemInstance item = player.getInventory().getItemByItemId(itemId);
					
					player.getInventory().unequipItemInSlot(player.getInventory().getSlotFromItem(item));
					player.sendMessage("Some restricted items have been unequipped.");
				}
			}
		}
		
	}
	
	@Override
	protected void onExit(Creature character)
	{
		if (_active)
			character.setInsideZone(ZoneId.RANDOM, false);
		
		if (character instanceof Player)
		{
			Player player = (Player) character;
			
			Object randomManager = RandomManager.getInstance();
			if (randomManager != null)
			{
				ext.mods.FarmEventRandom.holder.RandomZoneData zoneData = (ext.mods.FarmEventRandom.holder.RandomZoneData) RandomManager.getInstance().getZoneDataForZone(this);
				if (zoneData != null && zoneData.isEnchanterZone())
				{
					PvpFlagTaskManager.getInstance().add(player, Config.PVP_NORMAL_TIME);
				}
			}
			
			if (_flagzone)
			{
				PvpFlagTaskManager.getInstance().add(player, Config.PVP_NORMAL_TIME);
			}
		}
	}
	
	public void setActive(boolean val)
	{
		_active = val;
		
		if (!val)
		{
			forEachCreatureInside(c -> c.setInsideZone(ZoneId.RANDOM, false));
		}
		
	}
	
	public void forEachCreatureInside(Consumer<Creature> action)
	{
		if (_creatures == null || _creatures.isEmpty())
			return;
		
		_creatures.stream().filter(c -> c != null && c.isInsideZone(ZoneId.RANDOM)).forEach(action);
	}
	
	public boolean isActive()
	{
		return _active;
	}
	
	public boolean isFlegZone()
	{
		return _flagzone;
	}
	
	public Set<Integer> getRestrictedClasses()
	{
		return Collections.unmodifiableSet(_restrictedClasses);
	}
	
	public Set<Integer> getRestrictedItems()
	{
		return Collections.unmodifiableSet(_restrictedItems);
	}
	
	public Location getReturnLocation()
	{
		return _returnLocation;
	}
	
	private int getClassIdFromName(String name)
	{
		for (ClassId cid : ClassId.VALUES)
		{
			if (cid.name().equalsIgnoreCase(name))
				return cid.getId();
		}
		return -1;
	}
	
	@Override
	public int getId()
	{
		return _id;
	}
	
	public String getName()
	{
		return _name;
	}
}

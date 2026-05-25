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
package ext.mods.InstanceMap;

import java.util.ArrayList;
import java.util.List;

import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Door;

public class MapInstance
{
	private int _id;
	private List<Door> _doors;
	
	public MapInstance(int id)
	{
		_id = id;
		_doors = new ArrayList<>();
	}
	
	public void open_doors()
	{
		for (Door door : _doors)
			door.openMe();
	}
	
	public void close_doors()
	{
		for (Door door : _doors)
			door.closeMe();
	}
	
	public void addDoor(Door door)
	{
		_doors.add(door);
	}
	
	public List<Door> get_doors()
	{
		return _doors;
	}
	
	public void checkProximityAndOpen_doors(List<Player> players)
	{
		if (_doors == null || players == null)
		{
			return;
		}
		for (Door door : _doors)
		{
			if (door == null || door.getInstanceMap() == null)
			{
				continue;
			}
			boolean shouldOpen = false;
			for (Player player : players)
			{
				if (player == null || player.getInstanceMap() == null)
				{
					continue;
				}
				if (door.getInstanceMap().getId() == player.getInstanceMap().getId() && door.isIn3DRadius(player, 400))
				{
					shouldOpen = true;
					break;
				}
			}
			if (shouldOpen && !door.isOpened())
			{
				door.openMe();
			}
		}
	}
	
	public void checkProximityAndClose_doors(List<Player> players)
	{
		if (_doors == null || players == null)
		{
			return;
		}
		for (Door door : _doors)
		{
			if (door == null || door.getInstanceMap() == null)
			{
				continue;
			}
			boolean shouldClose = true;
			for (Player player : players)
			{
				if (player == null || player.getInstanceMap() == null)
				{
					continue;
				}
				if (door.getInstanceMap().getId() == player.getInstanceMap().getId() && door.isIn3DRadius(player, 400))
				{
					shouldClose = false;
					break;
				}
			}
			if (shouldClose && door.isOpened())
			{
				door.closeMe();
			}
		}
	}
	
	public void checkDeleted(List<Player> players)
	{
		if (_doors == null || players == null)
		{
			return;
		}
		for (Door door : _doors)
		{
			if (door == null || door.getInstanceMap() == null)
			{
				continue;
			}
			boolean shouldClose = true;
			for (Player player : players)
			{
				if (player == null || player.getInstanceMap() == null)
				{
					continue;
				}
				if (door.getInstanceMap().getId() == player.getInstanceMap().getId())
				{
					shouldClose = false;
					break;
				}
			}
			if (shouldClose)
			{
				door.deleteMe();
			}
		}
	}
	
	public int getId()
	{
		return _id;
	}
	
}
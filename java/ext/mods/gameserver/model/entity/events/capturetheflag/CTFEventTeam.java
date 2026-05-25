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
package ext.mods.gameserver.model.entity.events.capturetheflag;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.gameserver.model.actor.Player;

public class CTFEventTeam
{
	private final String _name;
	
	private int[] _coordinates = new int[3];
	
	private short _points;
	
	private final Map<Integer, Player> _participatedPlayers = new ConcurrentHashMap<>();
	
	public CTFEventTeam(String name, int[] coordinates)
	{
		_name = name;
		_coordinates = coordinates;
		_points = 0;
	}
	
	public boolean addPlayer(Player player)
	{
		if (player == null)
			return false;
		
		_participatedPlayers.put(player.getObjectId(), player);
		return true;
	}
	
	public void removePlayer(int objectId)
	{
		_participatedPlayers.remove(objectId);
	}
	
	public void increasePoints()
	{
		++_points;
	}
	
	public void cleanMe()
	{
		_participatedPlayers.clear();
		_points = 0;
	}
	
	public boolean containsPlayer(int objectId)
	{
		return _participatedPlayers.containsKey(objectId);
	}
	
	public String getName()
	{
		return _name;
	}
	
	public int[] getCoordinates()
	{
		return _coordinates;
	}
	
	public short getPoints()
	{
		return _points;
	}
	
	public Map<Integer, Player> getParticipatedPlayers()
	{
		return _participatedPlayers;
	}
	
	public int getParticipatedPlayerCount()
	{
		return _participatedPlayers.size();
	}
}
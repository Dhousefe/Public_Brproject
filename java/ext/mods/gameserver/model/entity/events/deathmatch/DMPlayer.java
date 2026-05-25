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
package ext.mods.gameserver.model.entity.events.deathmatch;

import ext.mods.gameserver.model.actor.Player;

public class DMPlayer
{
	private Player _player;
	private short _points;
	private short _death;
	private String _hexCode;
	
	public DMPlayer(Player player, String hexCode)
	{
		_player = player;
		_points = 0;
		_death = 0;
		_hexCode = hexCode;
	}
	
	public Player getPlayer()
	{
		return _player;
	}
	
	public void setPlayer(Player player)
	{
		_player = player;
	}
	
	public short getPoints()
	{
		return _points;
	}
	
	public void setPoints(short points)
	{
		_points = points;
	}
	
	public void increasePoints()
	{
		++_points;
	}
	
	public short getDeath()
	{
		return _death;
	}
	
	public void setDeath(short death)
	{
		_death = death;
	}
	
	public void increaseDeath()
	{
		++_death;
	}
	
	public String getHexCode()
	{
		return _hexCode;
	}
}
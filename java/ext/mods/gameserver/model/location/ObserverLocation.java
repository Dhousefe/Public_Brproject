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
package ext.mods.gameserver.model.location;

import ext.mods.commons.data.StatSet;

public class ObserverLocation extends Location
{
	private final int _locId;
	
	private final int _yaw;
	private final int _pitch;
	private final int _cost;
	
	private final int _castleId;
	
	public ObserverLocation(int locId, int x, int y, int z, int yaw, int pitch, int cost, int castleId)
	{
		super(x, y, z);
		
		_locId = locId;
		
		_yaw = yaw;
		_pitch = pitch;
		_cost = cost;
		_castleId = castleId;
	}
	
	public ObserverLocation(int locId, Location loc, int yaw, int pitch, int cost, int castleId)
	{
		this(locId, loc.getX(), loc.getY(), loc.getZ(), yaw, pitch, cost, castleId);
	}
	
	public ObserverLocation(StatSet set)
	{
		this(set.getInteger("locId"), set.getInteger("x"), set.getInteger("y"), set.getInteger("z"), set.getInteger("yaw"), set.getInteger("pitch"), set.getInteger("cost"), set.getInteger("castle"));
	}
	
	public int getLocId()
	{
		return _locId;
	}
	
	public int getYaw()
	{
		return _yaw;
	}
	
	public int getPitch()
	{
		return _pitch;
	}
	
	public int getCost()
	{
		return _cost;
	}
	
	public int getCastleId()
	{
		return _castleId;
	}
}
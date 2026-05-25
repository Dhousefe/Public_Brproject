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
package ext.mods.gameserver.model.boat;

import ext.mods.gameserver.enums.boats.BoatDock;
import ext.mods.gameserver.model.location.BoatLocation;
import ext.mods.gameserver.network.serverpackets.ExServerPrimitive;

public class BoatItinerary
{
	private final BoatRouteInfo[] _info = new BoatRouteInfo[2];
	private final int _heading;
	
	public BoatItinerary(BoatDock dock1, BoatDock dock2, int item1, int item2, int heading, BoatLocation[][] routes)
	{
		_info[0] = new BoatRouteInfo(routes[0], dock1, item1);
		_info[1] = (dock2 == null) ? null : new BoatRouteInfo(routes[1], dock2, item2);
		
		_heading = heading;
	}
	
	public int getHeading()
	{
		return _heading;
	}
	
	public BoatRouteInfo[] getInfo()
	{
		return _info;
	}
	
	public boolean isOneWay()
	{
		return _info[1] == null;
	}
	
	public void visualize(ExServerPrimitive debug)
	{
		_info[0].visualize(debug);
		
		if (!isOneWay())
			_info[1].visualize(debug);
	}
}
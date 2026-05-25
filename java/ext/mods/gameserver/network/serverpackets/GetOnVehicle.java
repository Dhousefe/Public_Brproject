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
package ext.mods.gameserver.network.serverpackets;

import ext.mods.gameserver.model.actor.container.player.BoatInfo;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.location.SpawnLocation;

public class GetOnVehicle extends L2GameServerPacket
{
	private final int _objectId;
	private final int _boatId;
	private final Location _location = new Location(0, 0, 0);
	
	public GetOnVehicle(int objectId, int boatId, int x, int y, int z)
	{
		_objectId = objectId;
		_boatId = boatId;
		_location.set(x, y, z);
	}
	
	public GetOnVehicle(int objectId, int boatId, SpawnLocation loc)
	{
		_objectId = objectId;
		_boatId = boatId;
		_location.set(loc);
	}
	
	public GetOnVehicle(BoatInfo info)
	{
		_objectId = info.getPlayer().getObjectId();
		_boatId = info.getBoat().getObjectId();
		_location.set(info.getBoatPosition());
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0x5C);
		writeD(_objectId);
		writeD(_boatId);
		writeLoc(_location);
	}
}
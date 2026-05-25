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

import ext.mods.gameserver.model.actor.Boat;
import ext.mods.gameserver.model.actor.Player;

public class MoveToLocationInVehicle extends L2GameServerPacket
{
	private final int _objectId;
	private final int _boatId;
	private final int _tX;
	private final int _tY;
	private final int _tZ;
	private final int _oX;
	private final int _oY;
	private final int _oZ;
	
	public MoveToLocationInVehicle(Player player, Boat boat, int tX, int tY, int tZ, int oX, int oY, int oZ)
	{
		_objectId = player.getObjectId();
		_boatId = boat.getObjectId();
		_tX = tX;
		_tY = tY;
		_tZ = tZ;
		_oX = oX;
		_oY = oY;
		_oZ = oZ;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0x71);
		writeD(_objectId);
		writeD(_boatId);
		writeD(_tX);
		writeD(_tY);
		writeD(_tZ);
		writeD(_oX);
		writeD(_oY);
		writeD(_oZ);
	}
}
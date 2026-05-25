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

import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.location.Location;

public class Earthquake extends L2GameServerPacket
{
	private final Location _loc;
	private final int _intensity;
	private final int _duration;
	private final int _isNpc;
	
	public Earthquake(WorldObject object, int intensity, int duration, boolean isNpc)
	{
		_loc = object.getPosition().clone();
		_intensity = intensity;
		_duration = duration;
		_isNpc = (isNpc) ? 1 : 0;
	}
	
	public Earthquake(WorldObject object, int intensity, int duration)
	{
		_loc = object.getPosition().clone();
		_intensity = intensity;
		_duration = duration;
		_isNpc = 0;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xc4);
		writeLoc(_loc);
		writeD(_intensity);
		writeD(_duration);
		writeD(_isNpc);
	}
}
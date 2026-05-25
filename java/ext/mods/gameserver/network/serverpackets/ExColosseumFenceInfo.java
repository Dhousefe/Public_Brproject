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

import ext.mods.gameserver.model.actor.instance.Fence;

/**
 * Format: (ch)ddddddd d: object id d: type (00 - no fence, 01 - only 4 columns, 02 - columns with fences) d: x coord d: y coord d: z coord d: width d: height
 */
public class ExColosseumFenceInfo extends L2GameServerPacket
{
	private final int _objectId;
	private final Fence _fence;
	
	public ExColosseumFenceInfo(int objectId, Fence fence)
	{
		_objectId = objectId;
		_fence = fence;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0x09);
		
		writeD(_objectId);
		writeD(_fence.getType());
		writeD(_fence.getX());
		writeD(_fence.getY());
		writeD(_fence.getZ());
		writeD(_fence.getSizeX());
		writeD(_fence.getSizeY());
	}
}
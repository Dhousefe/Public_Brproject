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

import java.util.List;

import ext.mods.gameserver.model.actor.Npc;

public class MonRaceInfo extends L2GameServerPacket
{
	private final int _unknown1;
	private final int _unknown2;
	private final List<Npc> _monsters;
	private final int[][] _speeds;
	
	public MonRaceInfo(int unknown1, int unknown2, List<Npc> monsters, int[][] speeds)
	{
		/*
		 * -1 0 to initial the race 0 15322 to start race 13765 -1 in middle of race -1 0 to end the race
		 */
		_unknown1 = unknown1;
		_unknown2 = unknown2;
		_monsters = monsters;
		_speeds = speeds;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xdd);
		
		writeD(_unknown1);
		writeD(_unknown2);
		writeD(8);
		
		for (int i = 0; i < 8; i++)
		{
			final Npc npc = _monsters.get(i);
			
			writeD(npc.getObjectId());
			writeD(npc.getTemplate().getNpcId() + 1000000);
			writeD(14107);
			writeD(181875 + (58 * (7 - i)));
			writeD(-3566);
			writeD(12080);
			writeD(181875 + (58 * (7 - i)));
			writeD(-3566);
			writeF(npc.getCollisionHeight());
			writeF(npc.getCollisionRadius());
			writeD(120);
			
			for (int j = 0; j < 20; j++)
			{
				if (_unknown1 == 0)
					writeC(_speeds[i][j]);
				else
					writeC(0);
			}
			writeD(0);
		}
	}
}
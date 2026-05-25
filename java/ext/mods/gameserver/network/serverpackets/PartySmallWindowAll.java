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

import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.group.Party;

public final class PartySmallWindowAll extends L2GameServerPacket
{
	private final Party _party;
	private final Player _player;
	private final int _dist;
	private final int _leaderObjectId;
	
	public PartySmallWindowAll(Player player, Party party)
	{
		_player = player;
		_party = party;
		_leaderObjectId = _party.getLeaderObjectId();
		_dist = _party.getLootRule().ordinal();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x4e);
		writeD(_leaderObjectId);
		writeD(_dist);
		writeD(_party.getMembersCount() - 1);
		
		for (Player player : _party.getMembers())
		{
			if (player == _player)
				continue;
			
			writeD(player.getObjectId());
			writeS(player.getName());
			writeD((int) player.getStatus().getCp());
			writeD(player.getStatus().getMaxCp());
			writeD((int) player.getStatus().getHp());
			writeD(player.getStatus().getMaxHp());
			writeD((int) player.getStatus().getMp());
			writeD(player.getStatus().getMaxMp());
			writeD(player.getStatus().getLevel());
			writeD(player.getClassId().getId());
			writeD(0);
			writeD(player.getRace().ordinal());
		}
	}
}
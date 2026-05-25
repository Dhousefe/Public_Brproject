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

public final class PartySmallWindowAdd extends L2GameServerPacket
{
	private final Player _player;
	private final int _leaderId;
	private final int _distribution;
	
	public PartySmallWindowAdd(Player player, Party party)
	{
		_player = player;
		_leaderId = party.getLeaderObjectId();
		_distribution = party.getLootRule().ordinal();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x4f);
		writeD(_leaderId);
		writeD(_distribution);
		writeD(_player.getObjectId());
		writeS(_player.getName());
		writeD((int) _player.getStatus().getCp());
		writeD(_player.getStatus().getMaxCp());
		writeD((int) _player.getStatus().getHp());
		writeD(_player.getStatus().getMaxHp());
		writeD((int) _player.getStatus().getMp());
		writeD(_player.getStatus().getMaxMp());
		writeD(_player.getStatus().getLevel());
		writeD(_player.getClassId().getId());
		writeD(0);
		writeD(0);
	}
}
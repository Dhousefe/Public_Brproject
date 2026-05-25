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
import ext.mods.gameserver.model.pledge.ClanMember;

public final class PledgeShowMemberListUpdate extends L2GameServerPacket
{
	private final int _pledgeType;
	private final int _hasSponsor;
	private final String _name;
	private final int _level;
	private final int _classId;
	private final int _isOnline;
	private final int _race;
	private final int _sex;
	
	public PledgeShowMemberListUpdate(Player player)
	{
		_pledgeType = player.getPledgeType();
		_hasSponsor = (player.getSponsor() != 0 || player.getApprentice() != 0) ? 1 : 0;
		_name = player.getName();
		_level = player.getStatus().getLevel();
		_classId = player.getClassId().getId();
		_race = player.getRace().ordinal();
		_sex = player.getAppearance().getSex().ordinal();
		_isOnline = (player.isOnline()) ? player.getObjectId() : 0;
	}
	
	public PledgeShowMemberListUpdate(ClanMember member)
	{
		_name = member.getName();
		_level = member.getLevel();
		_classId = member.getClassId();
		_isOnline = (member.isOnline()) ? member.getObjectId() : 0;
		_pledgeType = member.getPledgeType();
		_hasSponsor = (member.getSponsor() != 0 || member.getApprentice() != 0) ? 1 : 0;
		
		if (_isOnline != 0)
		{
			_race = member.getPlayerInstance().getRace().ordinal();
			_sex = member.getPlayerInstance().getAppearance().getSex().ordinal();
		}
		else
		{
			_sex = 0;
			_race = 0;
		}
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x54);
		writeS(_name);
		writeD(_level);
		writeD(_classId);
		writeD(_sex);
		writeD(_race);
		writeD(_isOnline);
		writeD(_pledgeType);
		writeD(_hasSponsor);
	}
}
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

import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.model.pledge.ClanMember;

public class PledgeShowMemberListAll extends L2GameServerPacket
{
	private final Clan _clan;
	private final int _pledgeType;
	private final String _pledgeName;
	
	public PledgeShowMemberListAll(Clan clan, int pledgeType)
	{
		_clan = clan;
		_pledgeType = pledgeType;
		
		if (_pledgeType == 0)
			_pledgeName = clan.getName();
		else if (_clan.getSubPledge(_pledgeType) != null)
			_pledgeName = _clan.getSubPledge(_pledgeType).getName();
		else
			_pledgeName = "";
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x53);
		
		writeD((_pledgeType == 0) ? 0 : 1);
		writeD(_clan.getClanId());
		writeD(_pledgeType);
		writeS(_pledgeName);
		writeS(_clan.getSubPledgeLeaderName(_pledgeType));
		
		writeD(_clan.getCrestId());
		writeD(_clan.getLevel());
		writeD(_clan.getCastleId());
		writeD(_clan.getClanHallId());
		writeD(_clan.getRank());
		writeD(_clan.getReputationScore());
		writeD(_clan.getDissolvingExpiryTime() > 0 ? 3 : 0);
		writeD(0);
		writeD(_clan.getAllyId());
		writeS(_clan.getAllyName());
		writeD(_clan.getAllyCrestId());
		writeD(_clan.isAtWar() ? 1 : 0);
		writeD(_clan.getSubPledgeMembersCount(_pledgeType));
		
		for (ClanMember m : _clan.getMembers())
		{
			if (m.getPledgeType() != _pledgeType)
				continue;
			
			writeS(m.getName());
			writeD(m.getLevel());
			writeD(m.getClassId());
			writeD(m.getSex().ordinal());
			writeD(m.getRace().ordinal());
			writeD((m.isOnline()) ? m.getObjectId() : 0);
			writeD((m.getSponsor() != 0 || m.getApprentice() != 0) ? 1 : 0);
		}
	}
}
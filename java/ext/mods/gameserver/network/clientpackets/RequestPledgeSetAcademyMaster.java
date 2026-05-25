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
package ext.mods.gameserver.network.clientpackets;

import ext.mods.gameserver.enums.PrivilegeType;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.model.pledge.ClanMember;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public final class RequestPledgeSetAcademyMaster extends L2GameClientPacket
{
	private String _currPlayerName;
	private int _set;
	private String _targetPlayerName;
	
	@Override
	protected void readImpl()
	{
		_set = readD();
		_currPlayerName = readS();
		_targetPlayerName = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final Clan clan = player.getClan();
		if (clan == null)
			return;
		
		if (!player.hasClanPrivileges(PrivilegeType.SP_MASTER_RIGHTS))
		{
			player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_RIGHT_TO_DISMISS_AN_APPRENTICE);
			return;
		}
		
		final ClanMember currentMember = clan.getClanMember(_currPlayerName);
		if (currentMember == null)
			return;
		
		final ClanMember targetMember = clan.getClanMember(_targetPlayerName);
		if (targetMember == null)
			return;
		
		ClanMember apprenticeMember;
		ClanMember sponsorMember;
		
		if (currentMember.getPledgeType() == Clan.SUBUNIT_ACADEMY)
		{
			apprenticeMember = currentMember;
			sponsorMember = targetMember;
		}
		else
		{
			apprenticeMember = targetMember;
			sponsorMember = currentMember;
		}
		
		final Player apprentice = apprenticeMember.getPlayerInstance();
		final Player sponsor = sponsorMember.getPlayerInstance();
		
		SystemMessage sm = null;
		if (_set == 0)
		{
			if (apprentice != null)
				apprentice.setSponsor(0);
			else
				apprenticeMember.setApprenticeAndSponsor(0, 0);
			
			if (sponsor != null)
				sponsor.setApprentice(0);
			else
				sponsorMember.setApprenticeAndSponsor(0, 0);
			
			apprenticeMember.saveApprenticeAndSponsor(0, 0);
			sponsorMember.saveApprenticeAndSponsor(0, 0);
			
			sm = SystemMessage.getSystemMessage(SystemMessageId.S2_CLAN_MEMBER_S1_APPRENTICE_HAS_BEEN_REMOVED);
		}
		else
		{
			if (apprenticeMember.getSponsor() != 0 || sponsorMember.getApprentice() != 0 || apprenticeMember.getApprentice() != 0 || sponsorMember.getSponsor() != 0)
			{
				player.sendMessage("Remove previous connections first.");
				return;
			}
			
			if (apprentice != null)
				apprentice.setSponsor(sponsorMember.getObjectId());
			else
				apprenticeMember.setApprenticeAndSponsor(0, sponsorMember.getObjectId());
			
			if (sponsor != null)
				sponsor.setApprentice(apprenticeMember.getObjectId());
			else
				sponsorMember.setApprenticeAndSponsor(apprenticeMember.getObjectId(), 0);
			
			apprenticeMember.saveApprenticeAndSponsor(0, sponsorMember.getObjectId());
			sponsorMember.saveApprenticeAndSponsor(apprenticeMember.getObjectId(), 0);
			
			sm = SystemMessage.getSystemMessage(SystemMessageId.S2_HAS_BEEN_DESIGNATED_AS_APPRENTICE_OF_CLAN_MEMBER_S1);
		}
		sm.addString(sponsorMember.getName());
		sm.addString(apprenticeMember.getName());
		
		if (sponsor != player && sponsor != apprentice)
			player.sendPacket(sm);
		
		if (sponsor != null)
			sponsor.sendPacket(sm);
		
		if (apprentice != null)
			apprentice.sendPacket(sm);
		
		clan.broadcastToMembers(new PledgeShowMemberListUpdate(sponsorMember), new PledgeShowMemberListUpdate(apprenticeMember));
	}
}
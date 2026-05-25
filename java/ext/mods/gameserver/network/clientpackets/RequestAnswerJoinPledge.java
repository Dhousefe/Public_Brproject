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

import java.util.Map;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.manager.CastleManager;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.model.pledge.SubPledge;
import ext.mods.gameserver.model.residence.castle.Castle;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.JoinPledge;
import ext.mods.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import ext.mods.gameserver.network.serverpackets.PledgeShowMemberListAdd;
import ext.mods.gameserver.network.serverpackets.PledgeShowMemberListAll;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public final class RequestAnswerJoinPledge extends L2GameClientPacket
{
	private int _answer;
	
	@Override
	protected void readImpl()
	{
		_answer = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final Player requestor = player.getRequest().getPartner();
		if (requestor == null)
			return;
		
		if (_answer == 0)
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_DID_NOT_RESPOND_TO_S1_CLAN_INVITATION).addCharName(requestor));
			requestor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DID_NOT_RESPOND_TO_CLAN_INVITATION).addCharName(player));
		}
		else
		{
			if (!(requestor.getRequest().getRequestPacket() instanceof RequestJoinPledge rjp))
				return;
			
			final Clan clan = requestor.getClan();
			
			if (clan.checkClanJoinCondition(requestor, player, rjp.getPledgeType()))
			{
				player.sendPacket(new JoinPledge(requestor.getClanId()));
				
				player.setPledgeType(rjp.getPledgeType());
				
				switch (rjp.getPledgeType())
				{
					case Clan.SUBUNIT_ACADEMY:
						player.setPowerGrade(9);
						player.setLvlJoinedAcademy(player.getStatus().getLevel());
						break;
					
					case Clan.SUBUNIT_ROYAL1, Clan.SUBUNIT_ROYAL2:
						player.setPowerGrade(7);
						break;
					
					case Clan.SUBUNIT_KNIGHT1, Clan.SUBUNIT_KNIGHT2, Clan.SUBUNIT_KNIGHT3, Clan.SUBUNIT_KNIGHT4:
						player.setPowerGrade(8);
						break;
					
					default:
						player.setPowerGrade(6);
				}
				
				clan.addClanMember(player);
				
				player.sendPacket(SystemMessageId.ENTERED_THE_CLAN);
				
				clan.broadcastToMembersExcept(player, SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_JOINED_CLAN).addCharName(player), new PledgeShowMemberListAdd(player));
				clan.broadcastToMembers(new PledgeShowInfoUpdate(clan));
				
				for (Castle castle : CastleManager.getInstance().getCastles())
				{
					final Map<Integer, Integer> skill = player.isClanLeader() ? castle.getSkillsLeader() : castle.getSkillsMember();
					if (castle.getId() == player.getClan().getCastleId())
					{
						skill.forEach((skillId, skillLvl) ->
						{
							player.addSkill(SkillTable.getInstance().getInfo(skillId, skillLvl), true);
						});
					}
				}
				
				player.sendPacket(new PledgeShowMemberListAll(clan, 0));
				for (SubPledge sp : player.getClan().getAllSubPledges())
					player.sendPacket(new PledgeShowMemberListAll(clan, sp.getId()));
				
				player.setClanJoinExpiryTime(0);
				player.broadcastUserInfo();
				
				player.forEachKnownType(Player.class, attacker -> clan.getWarList().contains(attacker.getClanId()), Player::broadcastUserInfo);
			}
		}
		player.getRequest().onRequestResponse();
	}
}
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

import ext.mods.Config;
import ext.mods.gameserver.data.sql.ClanTable;
import ext.mods.gameserver.enums.PrivilegeType;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public final class RequestStartPledgeWar extends L2GameClientPacket
{
	private String _pledgeName;
	
	@Override
	protected void readImpl()
	{
		_pledgeName = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final Clan attackerClan = player.getClan();
		if (attackerClan == null)
			return;
		
		if (!player.hasClanPrivileges(PrivilegeType.SP_CLAN_WAR))
		{
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
		
		final Clan attackedClan = ClanTable.getInstance().getClanByName(_pledgeName);
		if (attackedClan == null)
		{
			player.sendPacket(SystemMessageId.CLAN_WAR_CANNOT_DECLARED_CLAN_NOT_EXIST);
			return;
		}
		
		if (attackedClan.getClanId() == attackerClan.getClanId())
		{
			player.sendPacket(SystemMessageId.CANNOT_DECLARE_AGAINST_OWN_CLAN);
			return;
		}
		
		if (attackerClan.getWarList().size() >= 30)
		{
			player.sendPacket(SystemMessageId.TOO_MANY_CLAN_WARS);
			return;
		}
		
		if (attackerClan.getLevel() < 3 || attackerClan.getMembersCount() < Config.CLAN_MEMBERS_FOR_WAR)
		{
			player.sendPacket(SystemMessageId.CLAN_WAR_DECLARED_IF_CLAN_LVL3_OR_15_MEMBER);
			return;
		}
		
		if (!attackerClan.getAttackerList().contains(attackedClan.getClanId()) && (attackedClan.getLevel() < 3 || attackedClan.getMembersCount() < Config.CLAN_MEMBERS_FOR_WAR))
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CLAN_CANNOT_DECLARE_WAR_TOO_LOW_LEVEL_OR_NOT_ENOUGH_MEMBERS).addString(attackedClan.getName()));
			return;
		}
		
		if (attackerClan.getAllyId() == attackedClan.getAllyId() && attackerClan.getAllyId() != 0)
		{
			player.sendPacket(SystemMessageId.CLAN_WAR_AGAINST_A_ALLIED_CLAN_NOT_WORK);
			return;
		}
		
		if (attackedClan.getDissolvingExpiryTime() > 0)
		{
			player.sendPacket(SystemMessageId.NO_CLAN_WAR_AGAINST_DISSOLVING_CLAN);
			return;
		}
		
		if (attackerClan.isAtWarWith(attackedClan.getClanId()))
		{
			player.sendPacket(SystemMessageId.WAR_ALREADY_DECLARED);
			return;
		}
		
		if (attackerClan.hasWarPenaltyWith(attackedClan.getClanId()))
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ALREADY_AT_WAR_WITH_S1_WAIT_5_DAYS).addString(attackedClan.getName()));
			return;
		}
		
		ClanTable.getInstance().storeClansWars(player.getClanId(), attackedClan.getClanId());
		
		for (Player member : attackedClan.getOnlineMembers())
			member.broadcastUserInfo();
		
		for (Player member : attackerClan.getOnlineMembers())
			member.broadcastUserInfo();
	}
}
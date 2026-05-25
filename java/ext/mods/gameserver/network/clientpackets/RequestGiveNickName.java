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

import ext.mods.commons.lang.StringUtil;

import ext.mods.Config;
import ext.mods.gameserver.enums.PrivilegeType;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.entity.autofarm.AutoFarmManager;
import ext.mods.gameserver.model.pledge.ClanMember;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public class RequestGiveNickName extends L2GameClientPacket
{
	private String _name;
	private String _title;
	
	@Override
	protected void readImpl()
	{
		_name = readS();
		_title = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (!StringUtil.isValidString(_title, Config.TITLE_TEMPLATE))
		{
			player.sendPacket(SystemMessageId.NOT_WORKING_PLEASE_TRY_AGAIN_LATER);
			return;
		}
		
		if (Config.AUTOFARM_CHANGE_PLAYER_TITLE && AutoFarmManager.getInstance().isPlayerActive(player.getObjectId()))
			return;
		
		if (player.isNoble() && _name.matches(player.getName()))
		{
			player.setTitle(_title);
			player.sendPacket(SystemMessageId.TITLE_CHANGED);
			player.broadcastTitleInfo();
		}
		else
		{
			if (!player.hasClanPrivileges(PrivilegeType.SP_MANAGE_TITLES))
			{
				player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			
			if (player.getClan().getLevel() < 3)
			{
				player.sendPacket(SystemMessageId.CLAN_LVL_3_NEEDED_TO_ENDOWE_TITLE);
				return;
			}
			
			final ClanMember member = player.getClan().getClanMember(_name);
			if (member != null)
			{
				final Player playerMember = member.getPlayerInstance();
				if (playerMember != null)
				{
					playerMember.setTitle(_title);
					
					playerMember.sendPacket(SystemMessageId.TITLE_CHANGED);
					if (player != playerMember)
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CLAN_MEMBER_S1_TITLE_CHANGED_TO_S2).addCharName(playerMember).addString(_title));
					
					playerMember.broadcastTitleInfo();
				}
				else
					player.sendPacket(SystemMessageId.TARGET_IS_NOT_FOUND_IN_THE_GAME);
			}
			else
				player.sendPacket(SystemMessageId.TARGET_MUST_BE_IN_CLAN);
		}
	}
}
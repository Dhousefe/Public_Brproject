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
package ext.mods.gameserver.handler.usercommandhandlers;

import java.text.SimpleDateFormat;

import ext.mods.commons.lang.StringUtil;

import ext.mods.gameserver.data.manager.CastleManager;
import ext.mods.gameserver.handler.IUserCommandHandler;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.model.residence.castle.Castle;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;

public class ClanPenalty implements IUserCommandHandler
{
	private static final String NO_PENALTY = "<tr><td width=170>No penalty is imposed.</td><td width=100 align=center></td></tr>";
	
	private static final int[] COMMAND_IDS =
	{
		100
	};
	
	@Override
	public void useUserCommand(int id, Player player)
	{
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		final StringBuilder sb = new StringBuilder();
		final long currentTime = System.currentTimeMillis();
		
		if (player.getClanJoinExpiryTime() > currentTime)
			StringUtil.append(sb, "<tr><td width=170>Unable to join a clan.</td><td width=100 align=center>", sdf.format(player.getClanJoinExpiryTime()), "</td></tr>");
		
		if (player.getClanCreateExpiryTime() > currentTime)
			StringUtil.append(sb, "<tr><td width=170>Unable to create a clan.</td><td width=100 align=center>", sdf.format(player.getClanCreateExpiryTime()), "</td></tr>");
		
		final Clan clan = player.getClan();
		if (clan != null)
		{
			if (clan.getCharPenaltyExpiryTime() > currentTime)
				StringUtil.append(sb, "<tr><td width=170>Unable to invite a clan member.</td><td width=100 align=center>", sdf.format(clan.getCharPenaltyExpiryTime()), "</td></tr>");
			
			final int penaltyType = clan.getAllyPenaltyType();
			if (penaltyType != 0)
			{
				final long expiryTime = clan.getAllyPenaltyExpiryTime();
				if (expiryTime > currentTime)
				{
					if (penaltyType == Clan.PENALTY_TYPE_CLAN_LEAVED || penaltyType == Clan.PENALTY_TYPE_CLAN_DISMISSED)
						StringUtil.append(sb, "<tr><td width=170>Unable to join an alliance.</td><td width=100 align=center>", sdf.format(expiryTime), "</td></tr>");
					else if (penaltyType == Clan.PENALTY_TYPE_DISMISS_CLAN)
						StringUtil.append(sb, "<tr><td width=170>Unable to invite a new alliance member.</td><td width=100 align=center>", sdf.format(expiryTime), "</td></tr>");
					else if (penaltyType == Clan.PENALTY_TYPE_DISSOLVE_ALLY)
						StringUtil.append(sb, "<tr><td width=170>Unable to create an alliance.</td><td width=100 align=center>", sdf.format(expiryTime), "</td></tr>");
				}
			}
			
			if (clan.getDissolvingExpiryTime() > currentTime)
				StringUtil.append(sb, "<tr><td width=170>The request to dissolve the clan is currently being processed.  (Restrictions are now going to be imposed on the use of clan functions.)</td><td width=100 align=center>", sdf.format(clan.getDissolvingExpiryTime()), "</td></tr>");
			
			boolean registeredOnAnySiege = false;
			for (Castle castle : CastleManager.getInstance().getCastles())
			{
				if (castle.getSiege().checkSides(clan))
				{
					registeredOnAnySiege = true;
					break;
				}
			}
			
			if (clan.getAllyId() != 0 || clan.isAtWar() || clan.hasCastle() || clan.hasClanHall() || registeredOnAnySiege)
				StringUtil.append(sb, "<tr><td width=170>Unable to dissolve a clan.</td><td></td></tr>");
		}
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(player.getLocale(), "html/clan_penalty.htm");
		html.replace("%content%", (sb.length() == 0) ? NO_PENALTY : sb.toString());
		player.sendPacket(html);
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}
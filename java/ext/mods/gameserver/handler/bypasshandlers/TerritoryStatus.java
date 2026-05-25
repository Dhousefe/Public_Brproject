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
package ext.mods.gameserver.handler.bypasshandlers;

import ext.mods.gameserver.data.sql.ClanTable;
import ext.mods.gameserver.handler.IBypassHandler;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.model.residence.castle.Castle;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;

public class TerritoryStatus implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"TerritoryStatus"
	};
	
	@Override
	public boolean useBypass(String command, Player player, Creature target)
	{
		if (target instanceof Npc npc)
		{
			final Castle castle = npc.getCastle();
			if (castle == null)
				return false;
			
			final NpcHtmlMessage html = new NpcHtmlMessage(target.getObjectId());
			
			final Clan clan = ClanTable.getInstance().getClan(castle.getOwnerId());
			if (clan != null)
			{
				html.setFile(player.getLocale(), "html/territorystatus.htm");
				html.replace("%clanName%", clan.getName());
				html.replace("%clanLeaderName%", clan.getLeaderName());
				html.replace("%taxPercent%", castle.getCurrentTaxPercent());
			}
			else
				html.setFile(player.getLocale(), "html/territorynoclan.htm");
			
			html.replace("%territory%", (castle.getId() > 6) ? NpcStringId.ID_1001100.getMessage() : NpcStringId.ID_1001000.getMessage());
			html.replace("%townName%", castle.getTownName());
			html.replace("%objectId%", target.getObjectId());
			
			player.sendPacket(html);
		}
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}

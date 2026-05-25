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
package ext.mods.gameserver.handler.voicedcommandhandlers;

import java.util.List;

import ext.mods.gameserver.handler.IVoicedCommandHandler;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;

public class PlayerInfo implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"info",
		"item"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player player, String target)
	{
		if (player.getTarget() == null || !(player.getTarget() instanceof Player))
		{
			player.sendMessage("Você precisa selecionar um jogador como alvo.");
			return false;
		}
		
		Player targetPlayer = (Player) player.getTarget();
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><title>Player Info</title><body>");
		sb.append("<font color=LEVEL><b>").append(targetPlayer.getName()).append("</b></font><br><br>");
		
		sb.append("<center><table width=300><tr>");
		sb.append("<td><button value=\"Status\" action=\"bypass -h voiced_info\" width=100 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		sb.append("<td><button value=\"Equipamentos\" action=\"bypass -h voiced_item\" width=100 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		sb.append("</tr></table></center><br>");
		
		if (command.equals("info"))
		{
			sb.append("<table width=300 bgcolor=000000>");
			sb.append("<tr><td width=120><font color=LEVEL>HP:</font></td><td>").append((int) targetPlayer.getStatus().getHp()).append(" / ").append(targetPlayer.getStatus().getMaxHp()).append("</td></tr>");
			sb.append("<tr><td><font color=LEVEL>MP:</font></td><td>").append((int) targetPlayer.getStatus().getMp()).append(" / ").append(targetPlayer.getStatus().getMaxMp()).append("</td></tr>");
			sb.append("<tr><td><font color=LEVEL>CP:</font></td><td>").append((int) targetPlayer.getStatus().getCp()).append(" / ").append(targetPlayer.getStatus().getMaxCp()).append("</td></tr>");
			sb.append("<tr><td><font color=LEVEL>Level:</font></td><td>").append(targetPlayer.getStatus().getLevel()).append("</td></tr>");
			sb.append("<tr><td><font color=LEVEL>Classe:</font></td><td>").append(targetPlayer.getTemplate().getClassName()).append("</td></tr>");
			sb.append("<tr><td><font color=LEVEL>P. Atk:</font></td><td>").append(targetPlayer.getStatus().getPAtk(null)).append("</td></tr>");
			sb.append("<tr><td><font color=LEVEL>M. Atk:</font></td><td>").append(targetPlayer.getStatus().getMAtk(null, null)).append("</td></tr>");
			sb.append("<tr><td><font color=LEVEL>P. Def:</font></td><td>").append(targetPlayer.getStatus().getPDef(null)).append("</td></tr>");
			sb.append("<tr><td><font color=LEVEL>M. Def:</font></td><td>").append(targetPlayer.getStatus().getMDef(null, null)).append("</td></tr>");
			sb.append("<tr><td><font color=LEVEL>P. atkSpeed:</font></td><td>").append(targetPlayer.getStatus().getPAtkSpd()).append("</td></tr>");
			sb.append("<tr><td><font color=LEVEL>C. Speed:</font></td><td>").append(targetPlayer.getStatus().getMAtkSpd()).append("</td></tr>");
			sb.append("<tr><td><font color=LEVEL>STR:</font></td><td>").append(targetPlayer.getStatus().getSTR()).append("</td></tr>");
			sb.append("<tr><td><font color=LEVEL>CON:</font></td><td>").append(targetPlayer.getStatus().getCON()).append("</td></tr>");
			sb.append("<tr><td><font color=LEVEL>DEX:</font></td><td>").append(targetPlayer.getStatus().getDEX()).append("</td></tr>");
			sb.append("<tr><td><font color=LEVEL>INT:</font></td><td>").append(targetPlayer.getStatus().getINT()).append("</td></tr>");
			sb.append("<tr><td><font color=LEVEL>MEN:</font></td><td>").append(targetPlayer.getStatus().getMEN()).append("</td></tr>");
			sb.append("<tr><td><font color=LEVEL>WIT:</font></td><td>").append(targetPlayer.getStatus().getWIT()).append("</td></tr>");
			sb.append("</table>");
		}
		
		else if (command.startsWith("item"))
		{
			int page = 1;
			if (command.contains(" "))
			{
				try
				{
					page = Integer.parseInt(command.split(" ")[1]);
				}
				catch (Exception e)
				{
					page = 1;
				}
			}
			
			List<ItemInstance> paperdoll = targetPlayer.getInventory().getPaperdollItems();
			int itemsPerPage = 7;
			int totalItems = paperdoll.size();
			int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
			
			int start = (page - 1) * itemsPerPage;
			int end = Math.min(start + itemsPerPage, totalItems);
			
			sb.append("<table width=300 bgcolor=000000>");
			for (int i = start; i < end; i++)
			{
				ItemInstance item = paperdoll.get(i);
				if (item != null)
				{
					sb.append("<tr>");
					sb.append("<td width=40><img src=\"").append(item.getItem().getIcon()).append("\" width=32 height=32></td>");
					sb.append("<td>").append(item.getItem().getName());
					if (item.getEnchantLevel() > 0)
						sb.append(" +").append(item.getEnchantLevel());
					sb.append("</td></tr>");
				}
			}
			sb.append("</table><br>");
			
			sb.append("<table width=300><tr>");
			if (page > 1)
				sb.append("<td><button value=\"<<\" action=\"bypass -h voiced_item ").append(page - 1).append("\" width=40 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
			else
				sb.append("<td width=40></td>");
			
			sb.append("<td align=center><font color=LEVEL>Página ").append(page).append(" / ").append(totalPages).append("</font></td>");
			
			if (page < totalPages)
				sb.append("<td align=right><button value=\">>\" action=\"bypass -h voiced_item ").append(page + 1).append("\" width=40 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
			else
				sb.append("<td width=40></td>");
			sb.append("</tr></table>");
		}
		
		sb.append("</body></html>");
		html.setHtml(sb.toString());
		player.sendPacket(html);
		
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}

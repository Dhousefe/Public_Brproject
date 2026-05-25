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

import ext.mods.gameserver.handler.IVoicedCommandHandler;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;

public class Color implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"color"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player player, String target)
	{
		if (command.equals("color"))
		{
			if (player.getPremiumService() <= 0)
			{
				player.sendMessage("Este comando é exclusivo para jogadores VIP.");
				return false;
			}
			
			String name = player.getName();
			String title = player.getTitle();
			
			StringBuilder sb = new StringBuilder();
			sb.append("<html><body>");
			sb.append("<table width=80>");
			sb.append("<tr><td><button value=\"\" action=\"bypass voiced_menu\" width=35 height=23 back=\"L2UI_CH3.calculate2_bs_down\" fore=\"L2UI_CH3.calculate2_bs\"></td>");
			sb.append("<td> Menu </td></tr></table>");
			sb.append("<center><br>");
			sb.append("<img src=\"l2ui_ch3.herotower_deco\" width=256 height=32><br>");
			sb.append("<img src=\"L2UI.SquareGray\" width=295 height=1>");
			sb.append("<table bgcolor=000000 width=300><tr><td><center><font color=\"ae9977\">Change the color name for " + name + "</font></center></td></tr></table>");
			sb.append("<img src=\"L2UI.SquareGray\" width=295 height=1><br>");
			sb.append("NAME COLOR");
			sb.append("<table width=300 height=80>");
			int[] nameColors =
			{
				39168,
				16744192,
				10043427,
				65535,
				5000268,
				10028082,
				7396243,
				10461087,
				16776960,
				39423,
				3342438,
				16738047
			};
			String[] nameColorHex =
			{
				"009900",
				"0099ff",
				"234099",
				"ffff00",
				"4c4c4c",
				"320499",
				"80FF80",
				"AAAAAA",
				"00FFFF",
				"ff9900",
				"660033",
				"ff66ff"
			};
			for (int i = 0; i < nameColors.length; i += 3)
			{
				sb.append("<tr>");
				for (int j = 0; j < 3; j++)
				{
					int idx = i + j;
					if (idx < nameColors.length)
					{
						sb.append("<td align=center><a action=\"bypass voiced_namecolor " + nameColorHex[idx] + "\"><font color=\"" + nameColorHex[idx] + "\">Select Color</font></a></td>");
					}
				}
				sb.append("</tr>");
			}
			sb.append("</table><br>");
			sb.append("<img src=\"L2UI.SquareGray\" width=295 height=1>");
			sb.append("<table bgcolor=000000 width=300><tr><td><center><font color=\"ae9977\">Change the color title for " + title + "</font></center></td></tr></table>");
			sb.append("<img src=\"L2UI.SquareGray\" width=295 height=1><br>");
			sb.append("TITLE COLOR");
			sb.append("<table width=300 height=80>");
			for (int i = 0; i < nameColors.length; i += 3)
			{
				sb.append("<tr>");
				for (int j = 0; j < 3; j++)
				{
					int idx = i + j;
					if (idx < nameColors.length)
					{
						sb.append("<td align=center><a action=\"bypass voiced_titlecolor " + nameColorHex[idx] + "\"><font color=\"" + nameColorHex[idx] + "\">Select Color</font></a></td>");
					}
				}
				sb.append("</tr>");
			}
			sb.append("</table><br><center>");
			
			sb.append("<br><img src=\"L2UI.SquareGray\" width=295 height=1><br>");
			sb.append("<font color=\"LEVEL\">Ou use um código hexadecimal manual:</font><br><br>");
			sb.append("<table width=200>");
			sb.append("<tr><td>Cor do Nome:</td><td><edit var=\"name\" width=80></td></tr>");
			sb.append("<tr><td>Cor do Título:</td><td><edit var=\"title\" width=80></td></tr>");
			sb.append("</table>");
			sb.append("<br><button value=\"Salvar\" action=\"bypass -h voiced_savecolor $name $title\" width=80 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
			sb.append("</center></body></html>");
			
			NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setHtml(sb.toString());
			player.sendPacket(html);
		}
		
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}

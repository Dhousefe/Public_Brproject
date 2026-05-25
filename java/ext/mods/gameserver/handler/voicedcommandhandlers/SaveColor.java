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

import java.util.StringTokenizer;

import ext.mods.gameserver.handler.IVoicedCommandHandler;
import ext.mods.gameserver.model.actor.Player;

public class SaveColor implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"savecolor",
		"namecolor",
		"titlecolor"
	
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player player, String args)
	{
		
		if (command.startsWith("namecolor"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			
			String nameColor = String.valueOf(st.nextToken());
			
			if (player.getPremiumService() <= 0)
			{
				player.sendMessage("Apenas VIPs podem personalizar as cores.");
				return false;
			}
			
			if (nameColor == null || nameColor.isEmpty())
			{
				player.sendMessage("Informe uma cor hexadecimal válida, ex: FF0000");
				return false;
			}
			
			player.getMemos().set("name_color", nameColor);
			
			player.getAppearance().setNameColor(Integer.parseInt(nameColor, 16));
			
			player.broadcastUserInfo();
			player.sendMessage("Cores aplicadas com sucesso!");
			
		}
		
		if (command.startsWith("titlecolor"))
		{
			if (player.getPremiumService() <= 0)
			{
				player.sendMessage("Apenas VIPs podem personalizar as cores.");
				return false;
			}
			
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			
			String titleColor = String.valueOf(st.nextToken());
			
			if (titleColor == null || titleColor.isEmpty())
			{
				player.sendMessage("Informe uma cor hexadecimal válida, ex: FF0000");
				return false;
			}
			
			player.getMemos().set("title_color", titleColor);
			
			player.getAppearance().setTitleColor(Integer.parseInt(titleColor, 16));
			player.broadcastUserInfo();
			
			player.sendMessage("Cores aplicadas com sucesso!");
			
		}
		
		if (command.startsWith("savecolor"))
		{
			if (player.getPremiumService() <= 0)
			{
				player.sendMessage("Apenas VIPs podem personalizar as cores.");
				return false;
			}
			
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			
			String titleColor = String.valueOf(st.nextToken());
			String nameColor = String.valueOf(st.nextToken());
			
			if (!nameColor.matches("[0-9A-Fa-f]{6}") || !titleColor.matches("[0-9A-Fa-f]{6}"))
			{
				player.sendMessage("Use códigos hexadecimais válidos, ex: FF0000.");
				return false;
			}
			
			player.getMemos().set("name_color", nameColor);
			player.getMemos().set("title_color", titleColor);
			
			player.getAppearance().setNameColor(Integer.parseInt(nameColor, 16));
			player.getAppearance().setTitleColor(Integer.parseInt(titleColor, 16));
			player.broadcastUserInfo();
			
			player.sendMessage("Cores aplicadas com sucesso!");
			
		}
		
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}

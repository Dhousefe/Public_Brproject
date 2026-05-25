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

import ext.mods.Config;
import ext.mods.gameserver.handler.IVoicedCommandHandler;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.taskmanager.AutoPotionTaskManager;

public class Acp implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"acp",
		"setCp",
		"setHp",
		"setMp",
		"acpEnabled",
		"acpDisabled"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player player, String target)
	{
		if (!Config.AUTO_POTIONS_ENABLED)
		{
			player.sendMessage(player.getSysString(10_200));
			return false;
		}
		
		if (player.getStatus().getLevel() < Config.AUTO_POTION_MIN_LEVEL)
		{
			player.sendMessage(player.getSysString(10_188, Config.AUTO_POTION_MIN_LEVEL));
			return false;
		}
		
		switch (command)
		{
			case "acp":
				sendAcpHtml(player, "");
				break;
			
			case "acpEnabled":
				AutoPotionTaskManager.getInstance().add(player);
				player.sendMessage(player.getSysString(10_189));
				break;
			
			case "acpDisabled":
				AutoPotionTaskManager.getInstance().remove(player);
				player.sendMessage(player.getSysString(10_190));
				break;
			
			default:
				if (command.startsWith("setCp") && Config.AUTO_CP_ENABLED)
					setAutoPotionValue(command, player, "Cp");
				else if (command.startsWith("setHp") && Config.AUTO_HP_ENABLED)
					setAutoPotionValue(command, player, "Hp");
				else if (command.startsWith("setMp") && Config.AUTO_MP_ENABLED)
					setAutoPotionValue(command, player, "Mp");
				break;
		}
		return true;
	}
	
	private void sendAcpHtml(Player player, String message)
	{
		NpcHtmlMessage htm = new NpcHtmlMessage(0);
		htm.setFile(player.getLocale(), "html/mods/acp.htm");
		htm.replace("%valueCp%", player.isAcpCp());
		htm.replace("%valueHp%", player.isAcpHp());
		htm.replace("%valueMp%", player.isAcpMp());
		htm.replace("%msg%", message);
		player.sendPacket(htm);
	}
	
	private void setAutoPotionValue(String command, Player player, String type)
	{
		NpcHtmlMessage htm = new NpcHtmlMessage(0);
		htm.setFile(player.getLocale(), "html/mods/acp.htm");
		
		String valueStr = command.length() > 6 ? command.substring(6).trim() : "";
		String message = "<font color=\"FF0000\">" + player.getSysString(10_191) + "</font>";
		if (!valueStr.isEmpty())
		{
			try
			{
				int value = Integer.parseInt(valueStr);
				if (value < 0 || value > 100)
					message = "<font color=\"FF0000\">" + player.getSysString(10_192) + "</font>";
				else
				{
					switch (type)
					{
						case "Cp":
							player.setAcpCp(value);
							break;
						
						case "Hp":
							player.setAcpHp(value);
							break;
						
						case "Mp":
							player.setAcpMp(value);
							break;
					}
					message = "<font color=\"00FF00\">"+ player.getSysString(10_193) +"</font>";
				}
			}
			catch (NumberFormatException e)
			{
			}
		}
		
		htm.replace("%msg%", message);
		htm.replace("%valueCp%", player.isAcpCp());
		htm.replace("%valueHp%", player.isAcpHp());
		htm.replace("%valueMp%", player.isAcpMp());
		player.sendPacket(htm);
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
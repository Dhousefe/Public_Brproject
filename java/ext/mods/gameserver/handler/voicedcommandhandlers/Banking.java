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
import ext.mods.gameserver.network.serverpackets.ItemList;

public class Banking implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"bank",
		"withdraw",
		"deposit"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player player, String target)
	{
		if (!Config.ENABLE_COMMAND_GOLDBAR)
		{
			player.sendMessage(player.getSysString(10_200));
			return false;
		}
		
		if (command.equalsIgnoreCase("bank"))
			player.sendMessage(player.getSysString(10_194, Config.BANKING_SYSTEM_ADENA, Config.BANKING_SYSTEM_GOLDBARS, Config.BANKING_SYSTEM_GOLDBARS, Config.BANKING_SYSTEM_ADENA));
		else if (command.equalsIgnoreCase("deposit"))
		{
			if (player.getAdena() >= Config.BANKING_SYSTEM_ADENA)
			{
				player.getInventory().reduceAdena(Config.BANKING_SYSTEM_ADENA);
				player.getInventory().addItem(3470, Config.BANKING_SYSTEM_GOLDBARS);
				player.sendPacket(new ItemList(player, true));
				player.sendMessage(player.getSysString(10_195, Config.BANKING_SYSTEM_GOLDBARS, Config.BANKING_SYSTEM_ADENA));
			}
			else
				player.sendMessage(player.getSysString(10_196, Config.BANKING_SYSTEM_ADENA));
		}
		else if (command.equalsIgnoreCase("withdraw"))
		{
			long a = player.getAdena();
			long b = Config.BANKING_SYSTEM_ADENA;
			
			if (a + b > Integer.MAX_VALUE)
			{
				player.sendMessage(player.getSysString(10_197));
				return false;
			}
			
			if (player.getInventory().getItemCount(3470, 0) >= Config.BANKING_SYSTEM_GOLDBARS)
			{
				player.getInventory().destroyItemByItemId(3470, Config.BANKING_SYSTEM_GOLDBARS);
				player.getInventory().addAdena(Config.BANKING_SYSTEM_ADENA);
				player.sendPacket(new ItemList(player, true));
				player.sendMessage(player.getSysString(10_198, Config.BANKING_SYSTEM_ADENA, Config.BANKING_SYSTEM_GOLDBARS));
			}
			else
				player.sendMessage(player.getSysString(10_199, Config.BANKING_SYSTEM_ADENA));
		}
		
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
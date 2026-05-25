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
package ext.mods.gameserver.handler.admincommandhandlers;

import ext.mods.gameserver.data.xml.DoorData;
import ext.mods.gameserver.handler.IAdminCommandHandler;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Door;
import ext.mods.gameserver.network.SystemMessageId;

public class AdminDoor implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_open",
		"admin_close"
	};
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		if (command.startsWith("admin_open"))
		{
			try
			{
				testDoor(player, DoorData.getInstance().getDoor(Integer.parseInt(command.substring(11))), true);
			}
			catch (Exception e)
			{
				testDoor(player, player.getTarget(), true);
			}
		}
		else if (command.startsWith("admin_close"))
		{
			try
			{
				testDoor(player, DoorData.getInstance().getDoor(Integer.parseInt(command.substring(12))), false);
			}
			catch (Exception e)
			{
				testDoor(player, player.getTarget(), false);
			}
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private static void testDoor(Player player, WorldObject worldObject, boolean isOpenCondition)
	{
		if (worldObject instanceof Door door)
			door.changeState(isOpenCondition, false);
		else
			player.sendPacket(SystemMessageId.INVALID_TARGET);
	}
}
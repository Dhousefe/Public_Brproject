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

import ext.mods.Config;
import ext.mods.gameserver.handler.IAdminCommandHandler;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.protection.hwid.manager.hwidBan;

public class hwidAdminBan implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_hwid_ban"
	};
	
	@Override
	public void useAdminCommand(final String command, final Player player)
	{
		if (!Config.ALLOW_GUARD_SYSTEM || player == null)
			return;
		
		if (command.startsWith("admin_hwid_ban"))
		{
			final WorldObject playerTarget = player.getTarget();
			if (!(playerTarget instanceof Player))
			{
				player.sendMessage("Target is empty");
				return;
			}
			final Player target = (Player) playerTarget;
			hwidBan.addHWIDBan(target.getClient());
			player.sendMessage(target.getName() + " banned in HWID");
			player.sendMessage(target.getName() + " Kick");
			target.logout(false);
		}
		return;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}

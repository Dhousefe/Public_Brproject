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
import ext.mods.gameserver.model.entity.events.teamvsteam.TvTEvent;
import ext.mods.gameserver.model.entity.events.teamvsteam.TvTEventTeleporter;
import ext.mods.gameserver.model.entity.events.teamvsteam.TvTManager;

public class AdminTvTEvent implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_tvt_add",
		"admin_tvt_remove",
		"admin_tvt_advance"
	};
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		if (command.startsWith("admin_tvt_add"))
		{
			WorldObject target = player.getTarget();
			
			if (!(target instanceof Player plr))
			{
				player.sendMessage(player.getSysString(10_089));
				return;
			}
			
			add(player, plr);
		}
		else if (command.startsWith("admin_tvt_remove"))
		{
			WorldObject target = player.getTarget();
			
			if (!(target instanceof Player plr))
			{
				player.sendMessage(player.getSysString(10_089));
				return;
			}
			
			remove(player, plr);
		}
		else if (command.startsWith("admin_tvt_advance"))
			TvTManager.getInstance().skipDelay();
		
		return;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private void add(Player activeChar, Player player)
	{
		if (TvTEvent.getInstance().isPlayerParticipant(player.getObjectId()))
		{
			activeChar.sendMessage(player.getSysString(10_090));
			return;
		}
		
		if (!TvTEvent.getInstance().addParticipant(player))
		{
			activeChar.sendMessage(player.getSysString(10_091));
			return;
		}
		
		if (TvTEvent.getInstance().isStarted())
			new TvTEventTeleporter(player, TvTEvent.getInstance().getParticipantTeamCoordinates(player.getObjectId()), true, false);
	}
	
	private void remove(Player activeChar, Player player)
	{
		if (!TvTEvent.getInstance().removeParticipant(player.getObjectId()))
		{
			activeChar.sendMessage(player.getSysString(10_092));
			return;
		}
		
		new TvTEventTeleporter(player, Config.TVT_EVENT_PARTICIPATION_NPC_COORDINATES, true, true);
	}
}
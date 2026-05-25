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
package ext.mods.gameserver.model.actor.instance;

import java.util.concurrent.Future;

import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.enums.SayType;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.network.serverpackets.NpcSay;

/**
 * This class manages all Mass Gatekeepers, an entity linked to Castle system. It inherits from {@link Folk}.<br>
 * <br>
 * Mass Gatekeepers allow Castle Defenders Players to teleport back to battle, after 30 seconds. The time can increase to 480 seconds (8 minutes) during an active siege where all ControlTowers shattered.
 */
public class CastleGatekeeper extends Folk
{
	private Future<?> _teleportTask;
	
	public CastleGatekeeper(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (command.startsWith("tele"))
		{
			if (_teleportTask == null)
				_teleportTask = ThreadPool.schedule(this::oustPlayers, getTeleportDelay() * 1000L);
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player.getLocale(), "html/castleteleporter/MassGK-1.htm");
			html.replace("%delay%", getTeleportDelay());
			player.sendPacket(html);
		}
		else
			super.onBypassFeedback(player, command);
	}
	
	@Override
	public void showChatWindow(Player player)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		
		if (_teleportTask == null)
		{
			if (getCastle().getSiege().isInProgress() && getCastle().getAliveLifeTowerCount() == 0)
				html.setFile(player.getLocale(), "html/castleteleporter/MassGK-2.htm");
			else
				html.setFile(player.getLocale(), "html/castleteleporter/MassGK.htm");
		}
		else
		{
			html.setFile(player.getLocale(), "html/castleteleporter/MassGK-1.htm");
			html.replace("%delay%", getTeleportDelay());
		}
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
	}
	
	/**
	 * Oust all {@link Player}s and broadcast a message to everyone set into the region, during an active siege event.
	 */
	private final void oustPlayers()
	{
		if (getCastle().getSiege().isInProgress())
			World.broadcastToSameRegion(this, new NpcSay(this, SayType.SHOUT, "The defenders of " + getCastle().getName() + " castle have been teleported to the inner castle."));
		
		getCastle().oustAllPlayers();
		
		_teleportTask = null;
	}
	
	/**
	 * @return The teleport delay, as following : 30 seconds for regular teleport, 480 seconds (8 minutes) during an active siege, and if all ControlTowers have been broken.
	 */
	private final int getTeleportDelay()
	{
		return (getCastle().getSiege().isInProgress() && getCastle().getAliveLifeTowerCount() == 0) ? 480 : 30;
	}
}
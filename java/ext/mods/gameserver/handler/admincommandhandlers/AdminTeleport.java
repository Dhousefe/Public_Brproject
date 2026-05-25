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

import java.util.StringTokenizer;

import ext.mods.gameserver.enums.RestartType;
import ext.mods.gameserver.enums.TeleportMode;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.handler.IAdminCommandHandler;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.network.SystemMessageId;

public class AdminTeleport implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_instant_move",
		"admin_recall",
		"admin_sendhome",
		"admin_tele",
		"admin_teleport",
		"admin_teleportto"
	};
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		if (command.equals("admin_tele"))
		{
			sendFile(player, "teleports.htm");
			return;
		}
		
		final StringTokenizer st = new StringTokenizer(command);
		st.nextToken();
		
		if (command.startsWith("admin_instant_move"))
		{
			if (!st.hasMoreTokens())
				player.setTeleportMode(TeleportMode.ONE_TIME);
			else
			{
				try
				{
					final int mode = Integer.parseInt(st.nextToken());
					if (mode < 0 || mode > 2)
					{
						player.sendMessage("Usage: //instant_move [0|1|2]");
						return;
					}
					
					player.setTeleportMode(TeleportMode.VALUES[mode]);
				}
				catch (Exception e)
				{
					player.sendMessage("Usage: //instant_move [0|1|2]");
				}
			}
		}
		else if (command.startsWith("admin_recall"))
		{
			if (!st.hasMoreTokens())
			{
				player.sendPacket(SystemMessageId.INVALID_TARGET);
				return;
			}
			
			final String param = st.nextToken();
			switch (param)
			{
				case "clan":
					if (!st.hasMoreTokens())
					{
						player.sendPacket(SystemMessageId.INVALID_TARGET);
						return;
					}
					
					Player worldPlayer = World.getInstance().getPlayer(st.nextToken());
					if (worldPlayer == null)
					{
						player.sendPacket(SystemMessageId.INVALID_TARGET);
						return;
					}
					
					final Clan clan = worldPlayer.getClan();
					if (clan == null)
						worldPlayer.teleportTo(player.getPosition(), 0);
					else
					{
						for (Player clanMember : clan.getOnlineMembers())
							clanMember.teleportTo(player.getPosition(), 0);
					}
					
					player.setTarget(null);
					break;
				
				case "party":
					if (!st.hasMoreTokens())
					{
						player.sendPacket(SystemMessageId.INVALID_TARGET);
						return;
					}
					
					worldPlayer = World.getInstance().getPlayer(st.nextToken());
					if (worldPlayer == null)
					{
						player.sendPacket(SystemMessageId.INVALID_TARGET);
						return;
					}
					
					final Party party = worldPlayer.getParty();
					if (party == null)
						worldPlayer.teleportTo(player.getPosition(), 0);
					else
					{
						for (Player partyMember : party.getMembers())
							partyMember.teleportTo(player.getPosition(), 0);
					}
					
					player.setTarget(null);
					break;
				
				default:
					worldPlayer = World.getInstance().getPlayer(param);
					if (worldPlayer == null)
					{
						player.sendPacket(SystemMessageId.INVALID_TARGET);
						return;
					}
					
					worldPlayer.teleportTo(player.getPosition(), 0);
					
					player.setTarget(null);
					break;
			}
		}
		else if (command.startsWith("admin_sendhome"))
		{
			Player targetPlayer;
			
			if (st.hasMoreTokens())
			{
				targetPlayer = World.getInstance().getPlayer(st.nextToken());
				if (targetPlayer == null)
				{
					player.sendPacket(SystemMessageId.INVALID_TARGET);
					return;
				}
			}
			else
				targetPlayer = getTargetPlayer(player, true);
			
			targetPlayer.teleportTo(RestartType.TOWN);
			targetPlayer.setIsIn7sDungeon(false);
		}
		else if (command.startsWith("admin_teleportto"))
		{
			if (!st.hasMoreTokens())
			{
				player.sendPacket(SystemMessageId.INVALID_TARGET);
				return;
			}
			
			final Player worldPlayer = World.getInstance().getPlayer(st.nextToken());
			if (worldPlayer == null)
			{
				player.sendPacket(SystemMessageId.INVALID_TARGET);
				return;
			}
			
			player.teleportTo(worldPlayer.getPosition(), 0);
		}
		else if (command.startsWith("admin_teleport"))
		{
			try
			{
				final int x = Integer.parseInt(st.nextToken());
				final int y = Integer.parseInt(st.nextToken());
				final int z = (st.hasMoreTokens()) ? Integer.parseInt(st.nextToken()) : GeoEngine.getInstance().getHeight(x, y, player.getZ());
				
				player.teleportTo(x, y, z, 0);
			}
			catch (Exception e)
			{
				sendFile(player, "teleports.htm");
			}
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
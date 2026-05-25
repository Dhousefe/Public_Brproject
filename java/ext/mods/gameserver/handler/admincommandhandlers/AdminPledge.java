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

import ext.mods.gameserver.data.sql.ClanTable;
import ext.mods.gameserver.handler.IAdminCommandHandler;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.model.pledge.ClanMember;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.GMViewPledgeInfo;

public class AdminPledge implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_pledge"
	};
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		final Player targetPlayer = getTargetPlayer(player, true);
		
		if (command.startsWith("admin_pledge"))
		{
			final StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			
			try
			{
				final String action = st.nextToken();
				
				if (action.equals("create"))
				{
					try
					{
						final String parameter = st.nextToken();
						final long cet = targetPlayer.getClanCreateExpiryTime();
						
						targetPlayer.setClanCreateExpiryTime(0);
						
						final Clan clan = ClanTable.getInstance().createClan(targetPlayer, parameter);
						if (clan != null)
							player.sendMessage("Clan " + parameter + " have been created. Clan leader is " + targetPlayer.getName() + ".");
						else
						{
							targetPlayer.setClanCreateExpiryTime(cet);
							player.sendMessage("There was a problem while creating the clan.");
						}
					}
					catch (Exception e)
					{
						player.sendMessage("Invalid string parameter for //pledge create.");
					}
				}
				else
				{
					final Clan targetClan = targetPlayer.getClan();
					if (targetClan == null)
					{
						player.sendPacket(SystemMessageId.TARGET_MUST_BE_IN_CLAN);
						sendFile(player, "game_menu.htm");
						return;
					}
					
					if (action.equals("dismiss"))
					{
						ClanTable.getInstance().destroyClan(targetClan);
						player.sendMessage("The clan is now disbanded.");
					}
					else if (action.equals("info"))
						player.sendPacket(new GMViewPledgeInfo(targetClan, targetPlayer));
					else if (action.equals("level"))
					{
						try
						{
							final int level = Integer.parseInt(st.nextToken());
							if (level >= 0 && level < 9)
							{
								targetClan.changeLevel(level);
								player.sendMessage("You have set clan " + targetClan.getName() + " to level " + level);
							}
							else
								player.sendMessage("This clan level is incorrect. Put a number between 0 and 8.");
						}
						catch (Exception e)
						{
							player.sendMessage("Invalid number parameter for //pledge setlevel.");
						}
					}
					else if (action.startsWith("rep"))
					{
						try
						{
							final int points = Integer.parseInt(st.nextToken());
							
							if (targetClan.getLevel() < 5)
							{
								player.sendMessage("Only clans of level 5 or above may receive reputation points.");
								sendFile(player, "game_menu.htm");
								return;
							}
							
							targetClan.addReputationScore(points);
							player.sendMessage("You " + (points > 0 ? "added " : "removed ") + Math.abs(points) + " points " + (points > 0 ? "to " : "from ") + targetClan.getName() + "'s reputation. Their current score is: " + targetClan.getReputationScore());
						}
						catch (Exception e)
						{
							player.sendMessage("Invalid number parameter for //pledge rep.");
						}
					}
					else if (action.startsWith("transfer"))
					{
						final ClanMember member = targetClan.getClanMember(targetPlayer.getObjectId());
						if (member == null)
						{
							player.sendMessage(targetPlayer.getName() + " can't be set as the new Clan leader of " + targetClan.getName() + ".");
							return;
						}
						
						if (targetClan.getLeader() == member)
						{
							player.sendMessage(targetPlayer.getName() + " is already the Clan leader of " + targetClan.getName() + ".");
							return;
						}
						
						targetClan.setNewLeader(member);
						player.sendMessage("You set " + member.getName() + " as the new Clan leader of " + targetClan.getName() + ".");
					}
				}
			}
			catch (Exception e)
			{
				player.sendMessage("Usage: //pledge create|dismiss|info|level|rep|transfer");
			}
		}
		sendFile(player, "game_menu.htm");
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
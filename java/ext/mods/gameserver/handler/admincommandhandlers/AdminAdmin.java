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

import java.awt.Color;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import ext.mods.commons.data.Pagination;
import ext.mods.commons.lang.StringUtil;
import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.data.manager.BuyListManager;
import ext.mods.gameserver.data.manager.SpawnManager;
import ext.mods.gameserver.data.xml.AdminData;
import ext.mods.gameserver.data.xml.BoatData;
import ext.mods.gameserver.data.xml.ManorAreaData;
import ext.mods.gameserver.data.xml.RestartPointData;
import ext.mods.gameserver.data.xml.WalkerRouteData;
import ext.mods.gameserver.enums.TeleportMode;
import ext.mods.gameserver.handler.IAdminCommandHandler;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Door;
import ext.mods.gameserver.model.boat.BoatItinerary;
import ext.mods.gameserver.model.buylist.NpcBuyList;
import ext.mods.gameserver.model.location.WalkerLocation;
import ext.mods.gameserver.model.manor.ManorArea;
import ext.mods.gameserver.model.records.AdminCommand;
import ext.mods.gameserver.model.restart.RestartArea;
import ext.mods.gameserver.model.restart.RestartPoint;
import ext.mods.gameserver.model.spawn.ASpawn;
import ext.mods.gameserver.network.serverpackets.BuyList;
import ext.mods.gameserver.network.serverpackets.CameraMode;
import ext.mods.gameserver.network.serverpackets.ExServerPrimitive;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.taskmanager.GameTimeTaskManager;

public class AdminAdmin implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_admin",
		"admin_buy",
		"admin_camera",
		"admin_gmlist",
		"admin_gmoff",
		"admin_help",
		"admin_link",
		"admin_msg",
		"admin_show",
		"admin_time"
	};
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		
		if (command.startsWith("admin_admin"))
			showMainPage(player, command);
		else if (command.startsWith("admin_camera"))
		{
			if (player.getTeleportMode() != TeleportMode.CAMERA_MODE)
			{
				player.setTeleportMode(TeleportMode.CAMERA_MODE);
				player.getAppearance().setVisible(false);
				
				player.sendPacket(new CameraMode(1));
			}
			
			else
			{
				player.setTeleportMode(TeleportMode.NONE);
				player.getAppearance().setVisible(true);
				
				player.sendPacket(new CameraMode(0));
			}
			player.teleportTo(player.getPosition(), 0);
		}
		else if (command.startsWith("admin_gmlist"))
			player.sendMessage((AdminData.getInstance().showOrHideGm(player)) ? "Removed from GMList." : "Registered into GMList.");
		else
		{
			final StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			
			if (command.startsWith("admin_buy"))
			{
				if (!st.hasMoreTokens())
				{
					sendFile(player, "gmshops.htm");
					return;
				}
				
				try
				{
					final NpcBuyList list = BuyListManager.getInstance().getBuyList(Integer.parseInt(st.nextToken()));
					if (list == null)
					{
						player.sendMessage("Invalid buylist id.");
						return;
					}
					
					player.sendPacket(new BuyList(list, player.getAdena(), 0));
				}
				catch (Exception e)
				{
					player.sendMessage("Invalid buylist id.");
				}
			}
			else if (command.startsWith("admin_gmoff"))
			{
				int duration = 1;
				if (st.hasMoreTokens())
				{
					try
					{
						duration = Integer.parseInt(st.nextToken());
					}
					catch (Exception e)
					{
						player.sendMessage("Invalid timer set for //gm ; default time is used.");
					}
				}
				
				final int previousAccessLevel = player.getAccessLevel().getLevel();
				
				player.setAccessLevel(0);
				player.sendMessage("You no longer have GM status, but will be rehabilitated after " + duration + " minutes.");
				
				ThreadPool.schedule(() ->
				{
					if (!player.isOnline())
						return;
					
					player.setAccessLevel(previousAccessLevel);
					player.sendMessage("Your previous access level has been rehabilitated.");
				}, duration * 60000L);
			}
			else if (command.startsWith("admin_help"))
			{
				try
				{
					final int page = (st.hasMoreTokens()) ? Integer.parseInt(st.nextToken()) : 1;
					final String search = (st.hasMoreTokens()) ? st.nextToken().toLowerCase() : "";
					
					sendHelp(player, page, search);
				}
				catch (Exception e)
				{
					sendHelp(player, 1, "");
				}
			}
			else if (command.startsWith("admin_link"))
			{
				try
				{
					sendFile(player, st.nextToken());
				}
				catch (Exception e)
				{
					sendFile(player, "main_menu.htm");
				}
			}
			else if (command.startsWith("admin_msg"))
			{
				try
				{
					player.sendPacket(SystemMessage.getSystemMessage(Integer.parseInt(st.nextToken())));
				}
				catch (Exception e)
				{
					player.sendMessage("Usage: //msg sysMsgId");
				}
			}
			else if (command.startsWith("admin_time"))
			{
				try
				{
					int hours = Integer.parseInt(st.nextToken());
					int minutes = Integer.parseInt(st.nextToken());
					
					GameTimeTaskManager.getInstance().setGameTime(hours, minutes);
					
					player.sendMessage("Game time has been set to: " + GameTimeTaskManager.getInstance().getGameTimeFormated());
				}
				catch (Exception e)
				{
					player.sendMessage("Usage: //setgametime <hours> <minutes>");
				}
			}
			else if (command.startsWith("admin_show"))
			{
				final Creature targetCreature = getTargetCreature(player, true);
				
				ExServerPrimitive debug;
				
				try
				{
					switch (st.nextToken().toLowerCase())
					{
						case "boat":
							debug = player.getDebugPacket("BOAT");
							debug.reset();
							
							for (BoatItinerary itinerary : BoatData.getInstance().getItineraries())
								itinerary.visualize(debug);
							
							debug.sendTo(player);
							break;
						
						case "clear":
							if (targetCreature instanceof Player targetPlayer)
								targetPlayer.clearDebugPackets();
							break;
						
						case "door":
							debug = player.getDebugPacket("DOOR");
							debug.reset();
							
							player.forEachKnownType(Door.class, d -> d.getTemplate().visualizeDoor(debug));
							
							debug.sendTo(player);
							break;
						
						case "html":
							NpcHtmlMessage.SHOW_FILE = !NpcHtmlMessage.SHOW_FILE;
							break;
						
						case "manor":
							debug = player.getDebugPacket("MANOR");
							debug.reset();
							
							for (ManorArea ma : ManorAreaData.getInstance().getManorAreas())
								ma.visualize(debug);
							
							debug.sendTo(player);
							break;
						
						case "move":
							boolean move = !targetCreature.getMove().isDebugMove();
							targetCreature.getMove().setDebugMove(move);
							
							if (move)
							{
								player.sendMessage("Debug move enabled on " + targetCreature.getName());
								if (player != targetCreature)
									targetCreature.sendMessage("Debug move was enabled.");
							}
							else
							{
								player.sendMessage("Debug move disabled on " + targetCreature.getName());
								if (player != targetCreature)
									targetCreature.sendMessage("Debug move was disabled.");
								
								World.getInstance().getPlayers().stream().filter(Player::isGM).forEach(p ->
								{
									final ExServerPrimitive debugMove = p.getDebugPacket("MOVE" + targetCreature.getObjectId());
									debugMove.reset();
									debugMove.sendTo(p);
								});
								
								if (targetCreature instanceof Player targetPlayer)
								{
									final ExServerPrimitive debugMove = targetPlayer.getDebugPacket("MOVE" + targetPlayer.getObjectId());
									debugMove.reset();
									debugMove.sendTo(targetPlayer);
								}
							}
							break;
						
						case "path":
							boolean path = !targetCreature.getMove().isDebugPath();
							targetCreature.getMove().setDebugPath(path);
							
							if (path)
							{
								player.sendMessage("Debug path enabled on " + targetCreature.getName());
								if (player != targetCreature)
									targetCreature.sendMessage("Debug path was enabled.");
							}
							else
							{
								player.sendMessage("Debug path disabled on " + targetCreature.getName());
								if (player != targetCreature)
									targetCreature.sendMessage("Debug path was disabled.");
								
								World.getInstance().getPlayers().stream().filter(Player::isGM).forEach(p ->
								{
									final ExServerPrimitive debugPath = p.getDebugPacket("PATH" + targetCreature.getObjectId());
									debugPath.reset();
									debugPath.sendTo(p);
								});
								
								if (targetCreature instanceof Player targetPlayer)
								{
									final ExServerPrimitive debugPath = targetPlayer.getDebugPacket("PATH" + targetPlayer.getObjectId());
									debugPath.reset();
									debugPath.sendTo(targetPlayer);
								}
							}
							break;
						
						case "restart":
							if (!st.hasMoreTokens())
							{
								player.sendMessage("Usage : //show restart area|point>");
								return;
							}
							
							final String subCommand = st.nextToken();
							switch (subCommand)
							{
								case "area":
									debug = player.getDebugPacket("RESTART_AREA");
									debug.reset();
									
									for (RestartArea ra : RestartPointData.getInstance().getRestartAreas())
										ra.visualize(debug);
									
									debug.sendTo(player);
									break;
								
								case "point":
									debug = player.getDebugPacket("RESTART_POINT");
									debug.reset();
									
									for (RestartPoint rp : RestartPointData.getInstance().getRestartPoints())
										rp.visualizeZone(debug);
									
									debug.sendTo(player);
									break;
							}
							break;
						
						case "walker":
							if (!st.hasMoreTokens())
							{
								sendWalkerInfos(player, 1);
								return;
							}
							
							int page = 1;
							String param = st.nextToken();
							if (StringUtil.isDigit(param))
							{
								page = Integer.parseInt(param);
								
								if (!st.hasMoreTokens())
								{
									sendWalkerInfos(player, page);
									return;
								}
								
								param = st.nextToken();
							}
							
							final List<WalkerLocation> route = WalkerRouteData.getInstance().getWalkerRoute(param, param);
							if (route.isEmpty())
							{
								player.sendMessage("The npcId " + param + " isn't linked to any WalkerRoute.");
								return;
							}
							
							debug = player.getDebugPacket("WALKER");
							debug.reset();
							
							for (int i = 0; i < route.size(); i++)
							{
								final int nextIndex = i + 1;
								debug.addLine("Segment #" + nextIndex, Color.YELLOW, true, route.get(i), (nextIndex == route.size()) ? route.get(0) : route.get(nextIndex));
							}
							
							debug.sendTo(player);
							
							sendWalkerInfos(player, page);
							break;
						
						default:
							player.sendMessage("Usage : //show <clear|door|html|manor|move|path|restart|walker>");
							break;
					}
				}
				catch (Exception e)
				{
					player.sendMessage("Usage : //show <clear|door|html|manor|move|path|restart|walker>");
				}
			}
		}
	}
	
	/**
	 * Send to the {@link Player} all {@link AdminCommand}s informations.
	 * @param player : The Player used as reference.
	 * @param page : The current page we are checking.
	 * @param search : The {@link String} used as search.
	 */
	private static void sendHelp(Player player, int page, String search)
	{
		final Pagination<AdminCommand> list = new Pagination<>(AdminData.getInstance().getAdminCommands().stream(), page, PAGE_LIMIT_7, ac -> ac.name().substring(6).contains(search) || ac.params().contains(search));
		list.append("<html><body>");
		
		list.generateSearch("bypass admin_help", 45);
		
		for (AdminCommand command : list)
		{
			list.append(((list.indexOf(command) % 2) == 0 ? "<table width=280 height=41 bgcolor=000000><tr>" : "<table width=280 height=41><tr>"));
			
			list.append("<td width=280 height=34><font color=\"LEVEL\">//", command.name().substring(6), "</font>");
			
			if (!command.params().isBlank())
				list.append(" <font color=\"33cccc\">", command.params(), "</font>");
			
			list.append("<br1>", command.desc(), "</td>");
			
			list.append("</tr></table><img src=\"L2UI.SquareGray\" width=280 height=1>");
		}
		list.generateSpace(42);
		list.generatePages("bypass admin_help %page% " + search);
		list.append("</body></html>");
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setHtml(list.getContent());
		player.sendPacket(html);
	}
	
	private static void sendWalkerInfos(Player player, int page)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(player.getLocale(), "html/admin/walker.htm");
		
		int row = 0;
		
		final Pagination<Entry<String, List<WalkerLocation>>> list = new Pagination<>(WalkerRouteData.getInstance().getWalkerRoutes().values().stream().flatMap(routes -> routes.entrySet().stream()), page, PAGE_LIMIT_15);
		for (Entry<String, List<WalkerLocation>> route : list)
		{
			list.append(((row % 2) == 0 ? "<table width=280 bgcolor=000000><tr>" : "<table width=280><tr>"));
			
			final ASpawn aSpawn = SpawnManager.getInstance().getSpawn(route.getKey());
			if (aSpawn == null)
			{
				final String teleLoc = route.getValue().get(0).toString().replace(",", "");
				
				list.append("<td width=150><a action=\"bypass admin_teleport ", teleLoc, "\">Unspawned</a></td><td width=40>-</td><td width=50 align=right>-</td><td width=40 align=right><a action=\"bypass admin_show walker ", page, " ", route.getKey(), "\">Show</a></td>");
			}
			else
			{
				final Npc npc = aSpawn.getNpc();
				if (npc == null)
				{
					final String teleLoc = route.getValue().get(0).toString().replace(",", "");
					
					list.append("<td width=150><a action=\"bypass admin_teleport ", teleLoc, "\">Unspawned</a></td><td width=40>-</td><td width=50 align=right>-</td><td width=40 align=right><a action=\"bypass admin_show walker ", page, " ", route.getKey(), "\">Show</a></td>");
				}
				else
				{
					final String teleLoc = route.getValue().get(npc.getAI().getRouteIndex()).toString().replace(",", "");
					
					list.append("<td width=150><a action=\"bypass admin_teleport ", teleLoc, "\">", StringUtil.trimAndDress(npc.getTemplate().getAlias(), 25), "</a></td><td width=40>", npc.getAI().getRouteIndex(), " / ", route.getValue().size(), "</td><td width=50 align=right>", ((npc.getAI().isReversePath()) ? "Reverse" : "Regular"), "</td><td width=40 align=right><a action=\"bypass admin_show walker ", page, " ", route.getKey(), "\">Show</a></td>");
				}
			}
			
			list.append("</tr></table><img src=\"L2UI.SquareGray\" width=280 height=1>");
			
			row++;
		}
		
		list.generateSpace(20);
		list.generatePages("bypass admin_show walker %page%");
		
		html.replace("%content%", list.getContent());
		player.sendPacket(html);
	}
	
	private void showMainPage(Player player, String command)
	{
		String filename = "main";
		
		final StringTokenizer st = new StringTokenizer(command);
		st.nextToken();
		
		if (st.hasMoreTokens())
		{
			final String param = st.nextToken();
			if (StringUtil.isDigit(param))
			{
				final int mode = Integer.parseInt(param);
				if (mode == 2)
					filename = "game";
				else if (mode == 3)
					filename = "effects";
				else if (mode == 4)
					filename = "server";
			}
			else if (HTMLData.getInstance().exists(player, "html/admin/" + param + "_menu.htm"))
				filename = param;
		}
		
		sendFile(player, filename + "_menu.htm");
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
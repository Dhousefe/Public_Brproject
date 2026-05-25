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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import ext.mods.commons.util.LinTime;

import ext.mods.gameserver.data.manager.CastleManorManager;
import ext.mods.gameserver.data.manager.SevenSignsManager;
import ext.mods.gameserver.data.xml.ScriptData;
import ext.mods.gameserver.enums.QuestStatus;
import ext.mods.gameserver.handler.IAdminCommandHandler;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class AdminTest implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_test",
	};
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		final StringTokenizer st = new StringTokenizer(command);
		st.nextToken();
		
		if (!st.hasMoreTokens())
		{
			player.sendMessage("Usage : //test setquest || ssq_change || manor_change || dt_set(add, reset, print)");
			return;
		}
		
		switch (st.nextToken())
		{
			case "setquest":
				try
				{
					WorldObject targetObject = getTarget(WorldObject.class, player, true);
					
					if (targetObject == null)
					{
						player.sendPacket(SystemMessageId.INVALID_TARGET);
						return;
					}
					
					if (targetObject instanceof Player targetPlayer)
					{
						if (st.hasMoreTokens())
						{
							final int questId = Integer.parseInt(st.nextToken());
							if (st.hasMoreTokens())
							{
								final int cond = Integer.parseInt(st.nextToken());
								final Quest quest = ScriptData.getInstance().getQuest(questId);
								
								if (quest == null)
								{
									player.sendMessage("Quest with id: " + questId + " not found");
									return;
								}
								
								final QuestState qs = quest.getQuestState(targetPlayer, cond == 0 || cond == 1);
								
								if (qs == null)
								{
									player.sendMessage("Cannot initialize new quest state with cond " + cond + " for player " + targetPlayer.getName() + ". To initialize new quest state, use cond 0.");
									return;
								}
								
								if (cond > 0)
								{
									qs.setState(QuestStatus.STARTED);
									qs.setCond(cond);
									player.sendMessage(targetPlayer.getName() + "'s " + quest.getName() + " quest condition set to " + cond);
								}
								else
									player.sendMessage(targetPlayer.getName() + "'s " + quest.getName() + " quest has been created. To start it, use //test startquest <questId> <cond>");
							}
							else
								player.sendMessage("Invalid command format. Use //test setquest <questId> <cond>");
						}
						else
							player.sendMessage("Invalid command format. Use //test setquest <questId> <cond>");
					}
				}
				catch (NumberFormatException e)
				{
					player.sendMessage("Invalid command format. Use //test setquest <questId> <cond>");
				}
				break;
			
			case "ssq_change":
				SevenSignsManager.getInstance().changePeriod();
				break;
			
			case "manor_change":
				CastleManorManager.getInstance().changeMode();
				break;
			
			case "dt_set":
			{
				final long time = parseTime(st.nextToken());
				LinTime.setDeltaTime(time);
				player.sendMessage("+ set dt " + time);
				break;
			}
			
			case "dt_add":
			{
				final long time = parseTime(st.nextToken());
				LinTime.addDeltaTime(time);
				player.sendMessage("+ add dt " + time);
				break;
			}
			
			case "dt_reset":
				LinTime.resetDeltaTime();
				player.sendMessage("+ reset dt ");
				break;
			
			case "dt_print":
				final String name = st.hasMoreTokens() ? st.nextToken() : "";
				final TimeUnit tu = switch (name)
				{
					case "min" -> TimeUnit.MINUTES;
					case "hour" -> TimeUnit.HOURS;
					case "day" -> TimeUnit.DAYS;
					case "time" -> TimeUnit.NANOSECONDS;
					case "sec" -> TimeUnit.SECONDS;
					default -> TimeUnit.MILLISECONDS;
				};
				
				if (tu == TimeUnit.NANOSECONDS)
				{
					Date date = new Date(LinTime.currentTimeMillis());
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					player.sendMessage("+ dt: " + sdf.format(date));
				}
				else
					player.sendMessage("+ dt: %s %s".formatted(tu.convert(LinTime.deltaTime(), TimeUnit.MILLISECONDS), name));
				
				break;
				
			default:
				player.sendMessage("Usage : //test setquest || ssq_change || manor_change || dt_set(add, reset, print)");
				break;
		}
	}
	
	public static long parseTime(String input)
	{
		if (input.endsWith("sec"))
			return TimeUnit.SECONDS.toMillis(Long.parseLong(input.substring(0, input.length() - 3)));
		
		if (input.endsWith("min"))
			return TimeUnit.MINUTES.toMillis(Long.parseLong(input.substring(0, input.length() - 3)));
		
		if (input.endsWith("hour"))
			return TimeUnit.HOURS.toMillis(Long.parseLong(input.substring(0, input.length() - 4)));
		
		if (input.endsWith("day"))
			return TimeUnit.DAYS.toMillis(Long.parseLong(input.substring(0, input.length() - 3)));
		
		return Long.parseLong(input);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.StringTokenizer;

import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ConnectionPool;

import ext.mods.Config;
import ext.mods.gameserver.handler.IAdminCommandHandler;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.taskmanager.PremiumTaskManager;

public class AdminPremium implements IAdminCommandHandler
{
	private static final CLogger LOGGER = new CLogger(AdminPremium.class.getName());
	
	private static final String UPDATE_PREMIUMSERVICE = "REPLACE INTO account_premium (premium_service,enddate,account_name) values(?,?,?)";
	private static final String DELETE_PREMIUMSERVICE = "DELETE FROM account_premium WHERE account_name=?";
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_premium_menu",
		"admin_premium_remove"
	};
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		if (!Config.USE_PREMIUM_SERVICE)
		{
			player.sendMessage(player.getSysString(10_093));
			return;
		}
		
		final StringTokenizer st = new StringTokenizer(command);
		st.nextToken();
		
		if (command.startsWith("admin_premium_menu"))
		{
			if (!st.hasMoreTokens())
			{
				sendFile(player, "premium_menu.htm");
				return;
			}
			
			final String action = st.nextToken();
			
			final Player targetPlayer = getTargetPlayer(player, true);
			final String accName = targetPlayer != null ? targetPlayer.getAccountName() : st.hasMoreTokens() ? st.nextToken() : null;
			
			if (accName == null)
			{
				player.sendMessage(player.getSysString(10_094));
				return;
			}
			
			switch (action)
			{
				case "add":
					try
					{
						if (!st.hasMoreTokens())
						{
							player.sendMessage(player.getSysString(10_095));
							return;
						}
						final int month = Integer.parseInt(st.nextToken());
						addPremiumServices(player, month, accName);
					}
					catch (NumberFormatException e)
					{
						player.sendMessage(player.getSysString(10_095));
					}
					break;
				case "add2":
					try
					{
						if (!st.hasMoreTokens())
						{
							player.sendMessage(player.getSysString(10_096));
							return;
						}
						final int dayOfmonth = Integer.parseInt(st.nextToken());
						addPremiumServices2(player, dayOfmonth, accName);
					}
					catch (NumberFormatException e)
					{
						player.sendMessage(player.getSysString(10_096));
					}
					break;
				case "add3":
					try
					{
						if (!st.hasMoreTokens())
						{
							player.sendMessage(player.getSysString(10_097));
							return;
						}
						final int hourOfDay = Integer.parseInt(st.nextToken());
						addPremiumServices3(player, hourOfDay, accName);
					}
					catch (NumberFormatException e)
					{
						player.sendMessage(player.getSysString(10_097));
					}
					break;
				
				default:
				{
					sendFile(player, "premium_menu.htm");
				}
			}
		}
		else if (command.startsWith("admin_premium_remove"))
		{
			final Player targetPlayer = getTargetPlayer(player, true);
			final String accName = targetPlayer != null ? targetPlayer.getAccountName() : st.hasMoreTokens() ? st.nextToken() : null;
			removePremiumServices(player, accName);
		}
	}
	
	private static void addPremiumServices(Player player, int field, int value, String accName)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement statement = con.prepareStatement(UPDATE_PREMIUMSERVICE))
		{
			Calendar finishtime = Calendar.getInstance();
			finishtime.add(field, value);
			
			statement.setInt(1, 1);
			statement.setLong(2, finishtime.getTimeInMillis());
			statement.setString(3, accName);
			statement.execute();
			
			PremiumTaskManager.getInstance().add(player);
			player.sendMessage(player.getSysString(10_098, finishtime.getTime(), accName));
		}
		catch (SQLException e)
		{
			LOGGER.warn(AdminPremium.class.getName() + " Could not add premium services:" + e);
		}
	}
	
	private static void addPremiumServices(Player player, int month, String accName)
	{
		addPremiumServices(player, Calendar.MONTH, month, accName);
	}
	
	private static void addPremiumServices2(Player player, int dayOfMonth, String accName)
	{
		addPremiumServices(player, Calendar.DAY_OF_MONTH, dayOfMonth, accName);
	}
	
	private static void addPremiumServices3(Player player, int hourOfDay, String accName)
	{
		addPremiumServices(player, Calendar.HOUR_OF_DAY, hourOfDay, accName);
	}
	
	private static void removePremiumServices(Player player, String accName)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement statement = con.prepareStatement(DELETE_PREMIUMSERVICE))
		{
			statement.setString(1, accName);
			statement.execute();
			
			player.sendMessage("The premium has been remove for account: " + accName);
		}
		catch (SQLException e)
		{
			LOGGER.warn(AdminPremium.class.getName() + " Could not remove premium services:" + e);
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
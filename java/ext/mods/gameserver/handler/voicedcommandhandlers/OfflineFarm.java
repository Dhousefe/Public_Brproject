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
import ext.mods.commons.pool.ThreadPool;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.handler.IVoicedCommandHandler;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.entity.autofarm.AutoFarmManager;
import ext.mods.gameserver.model.entity.autofarm.OfflineFarmManager;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.ConfirmDlg;
import ext.mods.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * Offline Farm Command Handler - adapted version for current autofarm system
 * Handles .away command for offline farm activation
 */
public class OfflineFarm implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"away"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player player, String args)
	{
		if (!command.equalsIgnoreCase("away"))
			return false;
		
		if (!Config.ENABLE_OFFLINE_FARM_COMMAND)
		{
			player.sendMessage("Offline farm command is disabled.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		if (player.isOfflineFarm())
		{
			player.sendMessage("You are already in away mode.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		if (!AutoFarmManager.getInstance().isPlayerActive(player.getObjectId()))
		{
			player.sendMessage("You must activate autofarm first to use away mode.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		if (!Config.OFFLINE_MODE_IN_PEACE_ZONE && player.isInsideZone(ZoneId.PEACE))
		{
			player.sendMessage("You cannot activate away mode in a peace zone.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		if (Config.DUALBOX_CHECK_MAX_OFFLINEPLAY_PER_IP > 0 || Config.DUALBOX_CHECK_MAX_OFFLINEPLAY_PREMIUM_PER_IP > 0)
		{
			int offlineCount = 0;
			int offlinePremiumCount = 0;
			final String playerIp = player.getClient().getConnection().getInetAddress().getHostAddress();
			
			for (Player p : World.getInstance().getPlayers())
			{
				if (p != null && p.isOfflineFarm() && p.getClient() != null && p.getClient().getConnection() != null && p.getClient().getConnection().getInetAddress() != null)
				{
					if (p.getClient().getConnection().getInetAddress().getHostAddress().equals(playerIp))
					{
						offlineCount++;
						if (p.getPremiumService() > 0)
							offlinePremiumCount++;
					}
				}
			}
			
			if (Config.DUALBOX_CHECK_MAX_OFFLINEPLAY_PER_IP > 0 && offlineCount >= Config.DUALBOX_CHECK_MAX_OFFLINEPLAY_PER_IP)
			{
				player.sendMessage("You have reached the maximum number of offline players per IP.");
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return false;
			}
			
			if (Config.DUALBOX_CHECK_MAX_OFFLINEPLAY_PREMIUM_PER_IP > 0 && player.getPremiumService() > 0 && offlinePremiumCount >= Config.DUALBOX_CHECK_MAX_OFFLINEPLAY_PREMIUM_PER_IP)
			{
				player.sendMessage("You have reached the maximum number of premium offline players per IP.");
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return false;
			}
		}
		
		if (Config.OFFLINE_FARM_PREMIUM && player.getPremiumService() == 0)
		{
			player.sendMessage("Away mode is only available for premium accounts.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		if (player.isInVehicle() || player.isOlympiadProtection())
		{
			player.sendMessage("You cannot activate away mode while in a vehicle or under Olympiad protection.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		final ConfirmDlg dlg = new ConfirmDlg(SystemMessageId.S1.getId());
		dlg.addString("Do you want to activate away mode?");
		dlg.addTime(30000);
		player.sendPacket(dlg);
		
		player.setLastCommand("away");
		
		return true;
	}
	
	/**
	 * Handle confirmation response for away command
	 * @param player The player who is confirming
	 * @param confirmed True if the player confirmed, false otherwise
	 * @return True if the confirmation was handled successfully
	 */
	public boolean handleConfirmation(Player player, boolean confirmed)
	{
		if (confirmed && "away".equals(player.getLastCommand()))
		{
			if (OfflineFarmManager.getInstance().startOfflineFarm(player))
			{
				player.sendMessage("Away mode activated successfully!");
				player.sendPacket(new ExShowScreenMessage("Away mode activated!", 3000, ExShowScreenMessage.SMPOS.BOTTOM_CENTER, false));
				player.setLastCommand(null);
				
				ThreadPool.schedule(() ->
				{
					if (player.getClient() != null)
					{
						player.setOnlineStatus(false, false);
						player.sendMessage("You are now in offline farm mode. Your character will continue farming automatically.");
						player.getClient().closeNow();
					}
				}, 2000);
				
				return true;
			}
			
			player.sendMessage("Failed to activate away mode.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			player.setLastCommand(null);
			return false;
		}
		
		player.setLastCommand(null);
		return false;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}


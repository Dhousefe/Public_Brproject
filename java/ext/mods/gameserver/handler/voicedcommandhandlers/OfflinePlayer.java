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
import ext.mods.gameserver.data.manager.FestivalOfDarknessManager;
import ext.mods.gameserver.data.sql.OfflineTradersTable;
import ext.mods.gameserver.handler.IVoicedCommandHandler;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.model.olympiad.OlympiadManager;
import ext.mods.gameserver.model.trade.TradeList;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.taskmanager.AttackStanceTaskManager;

public class OfflinePlayer implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"offline"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player player, String target)
	{
		if (!Config.OFFLINE_TRADE_ENABLE)
		{
			player.sendMessage(player.getSysString(10_200));
			return false;
		}
		
		if (player == null)
			return false;
		
		if ((!player.isInStoreMode() && (!player.isCrafting())) || !player.isSitting())
		{
			player.sendMessage(player.getSysString(10_080));
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		final TradeList storeListBuy = player.getBuyList();
		if (storeListBuy == null)
		{
			player.sendMessage(player.getSysString(10_081));
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		final TradeList storeListSell = player.getSellList();
		if (storeListSell == null)
		{
			player.sendMessage(player.getSysString(10_082));
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		if (AttackStanceTaskManager.getInstance().isInAttackStance(player))
		{
			player.sendPacket(SystemMessageId.CANT_OPERATE_PRIVATE_STORE_DURING_COMBAT);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		if (player.isInCombat() && !player.isGM())
		{
			player.sendMessage(player.getSysString(10_083));
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		if (player.isTeleporting() && !player.isGM())
		{
			player.sendMessage(player.getSysString(10_084));
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		if (player.isInOlympiadMode() || OlympiadManager.getInstance().isRegistered(player))
		{
			player.sendMessage(player.getSysString(10_085));
			return false;
		}
		
		if (player.isFestivalParticipant())
		{
			if (FestivalOfDarknessManager.getInstance().isFestivalInitialized())
			{
				player.sendMessage(player.getSysString(10_086));
				return false;
			}
			
			Party playerParty = player.getParty();
			if (playerParty != null)
				player.getParty().broadcastToPartyMembers(player, SystemMessage.sendString(player.getSysString(10_173, player.getName())));
		}
		
		if (!OfflineTradersTable.offlineMode(player))
		{
			player.sendMessage(player.getSysString(10_087));
			return false;
		}
		
		if (player.isInStoreMode() && Config.OFFLINE_TRADE_ENABLE || player.isCrafting() && Config.OFFLINE_CRAFT_ENABLE)
		{
			player.logout(false);
			return true;
		}
		
		OfflineTradersTable.getInstance().saveOfflineTraders(player);
		return false;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
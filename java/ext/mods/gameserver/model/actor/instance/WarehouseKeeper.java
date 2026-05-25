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

import java.util.Map;

import ext.mods.Config;
import ext.mods.gameserver.enums.PrivilegeType;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.itemcontainer.PcFreight;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.PackageToList;
import ext.mods.gameserver.network.serverpackets.WarehouseDepositList;
import ext.mods.gameserver.network.serverpackets.WarehouseWithdrawList;

/**
 * An instance type extending {@link Folk}, used by warehouse keepers.<br>
 * <br>
 * A warehouse keeper stores {@link Player} items in a personal container.
 */
public class WarehouseKeeper extends Folk
{
	public WarehouseKeeper(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public boolean isWarehouse()
	{
		return true;
	}
	
	@Override
	public String getHtmlPath(Player player, int npcId, int val)
	{
		String filename = "";
		if (val == 0)
			filename = "" + npcId;
		else
			filename = npcId + "-" + val;
		
		return "html/warehouse/" + filename + ".htm";
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!Config.KARMA_PLAYER_CAN_USE_WH && player.getKarma() > 0 && showPkDenyChatWindow(player, "warehouse"))
			return;
		
		if (player.isProcessingTransaction())
		{
			player.sendPacket(SystemMessageId.ALREADY_TRADING);
			return;
		}
		
		player.cancelActiveEnchant();
		
		if (command.startsWith("WithdrawP"))
		{
			player.setActiveWarehouse(player.getWarehouse());
			
			if (player.getActiveWarehouse().getSize() == 0)
			{
				player.sendPacket(SystemMessageId.NO_ITEM_DEPOSITED_IN_WH);
				return;
			}
			
			player.sendPacket(new WarehouseWithdrawList(player, WarehouseWithdrawList.PRIVATE));
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else if (command.equals("DepositP"))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			player.setActiveWarehouse(player.getWarehouse());
			player.tempInventoryDisable();
			player.sendPacket(new WarehouseDepositList(player, WarehouseDepositList.PRIVATE));
		}
		else if (command.equals("WithdrawC"))
		{
			if (!player.hasClanPrivileges(PrivilegeType.SP_WAREHOUSE_SEARCH))
			{
				player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_CLAN_WAREHOUSE);
				return;
			}
			
			final Clan clan = player.getClan();
			if (clan == null)
				return;
			
			if (clan.getLevel() == 0)
			{
				player.sendPacket(SystemMessageId.ONLY_LEVEL_1_CLAN_OR_HIGHER_CAN_USE_WAREHOUSE);
				return;
			}
			
			player.setActiveWarehouse(player.getClan().getWarehouse());
			
			if (player.getActiveWarehouse().getSize() == 0)
			{
				player.sendPacket(SystemMessageId.NO_ITEM_DEPOSITED_IN_WH);
				return;
			}
			
			player.sendPacket(new WarehouseWithdrawList(player, WarehouseWithdrawList.CLAN));
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else if (command.equals("DepositC"))
		{
			final Clan clan = player.getClan();
			if (clan == null)
				return;
			
			if (clan.getLevel() == 0)
			{
				player.sendPacket(SystemMessageId.ONLY_LEVEL_1_CLAN_OR_HIGHER_CAN_USE_WAREHOUSE);
				return;
			}
			
			player.setActiveWarehouse(player.getClan().getWarehouse());
			player.tempInventoryDisable();
			player.sendPacket(new WarehouseDepositList(player, WarehouseDepositList.CLAN));
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else if (command.startsWith("WithdrawF"))
		{
			if (!Config.ALLOW_FREIGHT)
				return;
			
			player.sendPacket(ActionFailed.STATIC_PACKET);
			
			final PcFreight freight = player.getFreight();
			if (freight == null || freight.getSize() <= 0)
			{
				player.sendPacket(SystemMessageId.NO_ITEM_DEPOSITED_IN_WH);
				return;
			}
			
			freight.setActiveLocation((Config.REGION_BASED_FREIGHT) ? getRegion().hashCode() : 0);
			
			player.setActiveWarehouse(freight);
			player.sendPacket(new WarehouseWithdrawList(player, WarehouseWithdrawList.FREIGHT));
		}
		else if (command.startsWith("DepositF"))
		{
			if (!Config.ALLOW_FREIGHT)
				return;
			
			player.sendPacket(ActionFailed.STATIC_PACKET);
			
			final Map<Integer, String> chars = player.getAccountChars();
			if (chars.isEmpty())
			{
				player.sendPacket(SystemMessageId.CHARACTER_DOES_NOT_EXIST);
				return;
			}
			
			if (chars.size() < 1)
				return;
			
			player.sendPacket(new PackageToList(chars));
		}
		else if (command.startsWith("FreightChar"))
		{
			if (!Config.ALLOW_FREIGHT)
				return;
			
			final String id = command.substring(command.lastIndexOf("_") + 1);
			final PcFreight freight = player.getDepositedFreight(Integer.parseInt(id));
			freight.setActiveLocation((Config.REGION_BASED_FREIGHT) ? getRegion().hashCode() : 0);
			
			player.sendPacket(ActionFailed.STATIC_PACKET);
			player.setActiveWarehouse(freight);
			player.tempInventoryDisable();
			player.sendPacket(new WarehouseDepositList(player, WarehouseDepositList.FREIGHT));
		}
		else
			super.onBypassFeedback(player, command);
	}
	
	@Override
	public void showChatWindow(Player player, int val)
	{
		if (!Config.KARMA_PLAYER_CAN_USE_WH && player.getKarma() > 0 && showPkDenyChatWindow(player, "warehouse"))
			return;
		
		showChatWindow(player, getHtmlPath(player, getNpcId(), val));
	}
}
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
package ext.mods.gameserver.network.clientpackets;

import ext.mods.commons.util.ArraysUtil;

import ext.mods.Config;
import ext.mods.gameserver.data.sql.OfflineTradersTable;
import ext.mods.gameserver.enums.actors.OperateType;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.trade.SellProcessItem;
import ext.mods.gameserver.model.trade.TradeList;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.PrivateStoreManageListSell;
import ext.mods.gameserver.network.serverpackets.PrivateStoreMsgSell;

public final class SetPrivateStoreListSell extends L2GameClientPacket
{
	private static final int BATCH_LENGTH = 12;
	
	private boolean _packageSale;
	private SellProcessItem[] _items = null;
	
	@Override
	protected void readImpl()
	{
		_packageSale = (readD() == 1);
		
		final int count = readD();
		if (count < 1 || count > Config.MAX_ITEM_IN_PACKET || count * BATCH_LENGTH != _buf.remaining())
			return;
		
		_items = new SellProcessItem[count];
		
		for (int i = 0; i < count; i++)
		{
			final int objectId = readD();
			final int cnt = readD();
			final int price = readD();
			
			_items[i] = new SellProcessItem(objectId, cnt, price);
		}
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final TradeList tradeList = player.getSellList();
		tradeList.clear();
		
		if (ArraysUtil.isEmpty(_items))
		{
			player.setOperateType(OperateType.NONE);
			player.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
			return;
		}
		
		if (!player.getInventory().canPassSellProcess(_items))
		{
			player.setOperateType(OperateType.NONE);
			return;
		}
		
		if (!player.getAccessLevel().allowTransaction())
		{
			player.setOperateType(OperateType.NONE);
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
		
		if (!player.canOpenPrivateStore(false))
			return;
		
		if (_items.length > player.getStatus().getPrivateSellStoreLimit())
		{
			player.setOperateType(OperateType.NONE);
			player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
			return;
		}
		
		tradeList.setPackaged(_packageSale);
		
		long totalCost = player.getAdena();
		
		for (SellProcessItem i : _items)
		{
			if (!i.addToTradeList(tradeList))
			{
				player.sendPacket(SystemMessageId.EXCEEDED_THE_MAXIMUM);
				player.sendPacket(new PrivateStoreManageListSell(player, _packageSale));
				return;
			}
			
			totalCost += i.getPrice();
			if (totalCost > Integer.MAX_VALUE)
			{
				player.sendPacket(SystemMessageId.EXCEEDED_THE_MAXIMUM);
				player.sendPacket(new PrivateStoreManageListSell(player, _packageSale));
				return;
			}
		}
		
		if (Config.RESTORE_STORE_ITEMS)
			player.saveTradeList();
		player.getMove().stop();
		player.sitDown();
		player.setOperateType((_packageSale) ? OperateType.PACKAGE_SELL : OperateType.SELL);
		player.broadcastUserInfo();
		player.broadcastPacket(new PrivateStoreMsgSell(player));
		OfflineTradersTable.getInstance().saveOfflineTraders(player);
	}
}
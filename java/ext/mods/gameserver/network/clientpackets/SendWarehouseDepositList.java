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

import static ext.mods.gameserver.model.itemcontainer.PcInventory.ADENA_ID;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import ext.mods.Config;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Folk;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.itemcontainer.ItemContainer;
import ext.mods.gameserver.model.itemcontainer.PcWarehouse;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public final class SendWarehouseDepositList extends L2GameClientPacket
{
	private static final Logger ITEM_LOG = Logger.getLogger("item");
	
	private static final int BATCH_LENGTH = 8;
	
	private IntIntHolder[] _items = null;
	
	@Override
	protected void readImpl()
	{
		final int count = readD();
		if (count <= 0 || count > Config.MAX_ITEM_IN_PACKET || count * BATCH_LENGTH != _buf.remaining())
			return;
		
		_items = new IntIntHolder[count];
		for (int i = 0; i < count; i++)
		{
			int objId = readD();
			int cnt = readD();
			
			if (objId < 1 || cnt < 0)
			{
				_items = null;
				return;
			}
			_items[i] = new IntIntHolder(objId, cnt);
		}
	}
	
	@Override
	protected void runImpl()
	{
		if (_items == null)
			return;
		
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (player.isProcessingTransaction())
		{
			player.sendPacket(SystemMessageId.ALREADY_TRADING);
			return;
		}
		
		player.cancelActiveEnchant();
		
		final ItemContainer warehouse = player.getActiveWarehouse();
		if (warehouse == null)
			return;
		
		final boolean isPrivate = warehouse instanceof PcWarehouse;
		
		final Folk folk = player.getCurrentFolk();
		if (folk == null || !folk.isWarehouse() || !player.getAI().canDoInteract(folk))
			return;
		
		if (!isPrivate && !player.getAccessLevel().allowTransaction())
		{
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
		
		if (!Config.KARMA_PLAYER_CAN_USE_WH && player.getKarma() > 0)
			return;
		
		final int fee = _items.length * 30;
		int currentAdena = player.getAdena();
		int slots = 0;
		
		for (IntIntHolder i : _items)
		{
			ItemInstance item = player.checkItemManipulation(i.getId(), i.getValue());
			if (item == null)
				return;
			
			if (item.getItemId() == ADENA_ID)
			{
				if (isPrivate)
				{
					if (Integer.MAX_VALUE - player.getWarehouse().getAdena() - i.getValue() < 0)
					{
						sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED));
						return;
					}
				}
				else if (player.getClan() == null || Integer.MAX_VALUE - player.getClan().getWarehouse().getAdena() - i.getValue() < 0)
				{
					sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED));
					return;
				}
			}
			
			if (item.getItemId() == ADENA_ID)
				currentAdena -= i.getValue();
			
			if (!item.isStackable())
				slots += i.getValue();
			else if (warehouse.getItemByItemId(item.getItemId()) == null)
				slots++;
		}
		
		if (!warehouse.validateCapacity(slots))
		{
			sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED));
			return;
		}
		
		if (currentAdena < fee || !player.reduceAdena(fee, false))
		{
			sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA));
			return;
		}
		
		if (player.getActiveTradeList() != null)
			return;
		
		for (IntIntHolder i : _items)
		{
			ItemInstance oldItem = player.checkItemManipulation(i.getId(), i.getValue());
			if (oldItem == null)
				return;
			
			if (!oldItem.isDepositable(isPrivate) || !oldItem.isAvailable(player, true, isPrivate, false))
				continue;
			
			if (Config.LOG_ITEMS)
			{
				final LogRecord logRecord = new LogRecord(Level.INFO, "DEPOSIT_WEREHOUSE");
				logRecord.setLoggerName("item");
				logRecord.setParameters(new Object[]
				{
					player,
					oldItem
				});
				ITEM_LOG.log(logRecord);
			}
			
			player.getInventory().transferItem(i.getId(), i.getValue(), warehouse);
		}
	}
}
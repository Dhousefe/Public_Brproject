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

import java.util.ArrayList;
import java.util.List;

import ext.mods.Config;
import ext.mods.gameserver.data.manager.CastleManorManager;
import ext.mods.gameserver.data.xml.ItemData;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Folk;
import ext.mods.gameserver.model.actor.instance.ManorManagerNpc;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.Item;
import ext.mods.gameserver.model.manor.CropProcure;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public class RequestProcureCropList extends L2GameClientPacket
{
	private static final int BATCH_LENGTH = 16;
	
	private List<CropHolder> _items;
	
	@Override
	protected void readImpl()
	{
		final int count = readD();
		if (count <= 0 || count > Config.MAX_ITEM_IN_PACKET || count * BATCH_LENGTH != _buf.remaining())
			return;
		
		_items = new ArrayList<>(count);
		for (int i = 0; i < count; i++)
		{
			final int objId = readD();
			final int itemId = readD();
			final int manorId = readD();
			final int cnt = readD();
			
			if (objId < 1 || itemId < 1 || manorId < 0 || cnt < 0)
			{
				_items = null;
				return;
			}
			
			_items.add(new CropHolder(objId, itemId, cnt, manorId));
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
		
		final CastleManorManager manor = CastleManorManager.getInstance();
		if (manor.isUnderMaintenance())
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final Folk folk = player.getCurrentFolk();
		if (!(folk instanceof ManorManagerNpc) || !player.getAI().canDoInteract(folk))
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final int castleId = folk.getCastle().getId();
		
		int slots = 0;
		int weight = 0;
		
		for (CropHolder i : _items)
		{
			final ItemInstance item = player.getInventory().getItemByObjectId(i.getObjectId());
			if (item == null || item.getCount() < i.getValue() || item.getItemId() != i.getId())
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			final CropProcure cp = i.getCropProcure();
			if (cp == null || cp.getAmount() < i.getValue())
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			final Item template = ItemData.getInstance().getTemplate(i.getRewardId());
			weight += (i.getValue() * template.getWeight());
			
			if (!template.isStackable())
				slots += i.getValue();
			else if (player.getInventory().getItemByItemId(i.getRewardId()) == null)
				slots++;
		}
		
		if (!player.getInventory().validateWeight(weight))
		{
			sendPacket(SystemMessage.getSystemMessage(SystemMessageId.WEIGHT_LIMIT_EXCEEDED));
			return;
		}
		
		if (!player.getInventory().validateCapacity(slots))
		{
			sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SLOTS_FULL));
			return;
		}
		
		for (CropHolder i : _items)
		{
			final int rewardPrice = ItemData.getInstance().getTemplate(i.getRewardId()).getReferencePrice();
			if (rewardPrice == 0)
				continue;
			
			final int rewardItemCount = i.getPrice() / rewardPrice;
			if (rewardItemCount < 1)
			{
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FAILED_IN_TRADING_S2_OF_CROP_S1).addItemName(i.getId()).addItemNumber(i.getValue()));
				continue;
			}
			
			final int fee = (castleId == i.getManorId()) ? 0 : ((int) (i.getPrice() * 0.05));
			if (fee != 0 && player.getAdena() < fee)
			{
				sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FAILED_IN_TRADING_S2_OF_CROP_S1).addItemName(i.getId()).addItemNumber(i.getValue()));
				sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA));
				continue;
			}
			
			final CropProcure cp = i.getCropProcure();
			if (!cp.decreaseAmount(i.getValue()) || (fee > 0 && !player.reduceAdena(fee, true)) || !player.destroyItem(i.getObjectId(), i.getValue(), true))
				continue;
			
			player.addItem(i.getRewardId(), rewardItemCount, true);
		}
	}
	
	private final class CropHolder extends IntIntHolder
	{
		private final int _manorId;
		private final int _objectId;
		
		private CropProcure _cp;
		private int _rewardId = 0;
		
		public CropHolder(int objectId, int id, int count, int manorId)
		{
			super(id, count);
			
			_objectId = objectId;
			_manorId = manorId;
		}
		
		public final int getManorId()
		{
			return _manorId;
		}
		
		public int getObjectId()
		{
			return _objectId;
		}
		
		public final int getPrice()
		{
			return getValue() * _cp.getPrice();
		}
		
		public final CropProcure getCropProcure()
		{
			if (_cp == null)
				_cp = CastleManorManager.getInstance().getCropProcure(_manorId, getId(), false);
			
			return _cp;
		}
		
		public final int getRewardId()
		{
			if (_rewardId == 0)
				_rewardId = CastleManorManager.getInstance().getSeedRewardByCrop(_cp.getId(), _cp.getReward());
			
			return _rewardId;
		}
	}
}
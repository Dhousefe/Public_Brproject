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

import ext.mods.Config;
import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.data.manager.BuyListManager;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Fisherman;
import ext.mods.gameserver.model.actor.instance.MercenaryManagerNpc;
import ext.mods.gameserver.model.actor.instance.Merchant;
import ext.mods.gameserver.model.buylist.NpcBuyList;
import ext.mods.gameserver.model.buylist.Product;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ItemList;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public final class RequestBuyItem extends L2GameClientPacket
{
	private static final int BATCH_LENGTH = 8;
	
	private int _listId;
	private IntIntHolder[] _items = null;
	
	@Override
	protected void readImpl()
	{
		_listId = readD();
		int count = readD();
		if (count <= 0 || count > Config.MAX_ITEM_IN_PACKET || count * BATCH_LENGTH != _buf.remaining())
			return;
		
		_items = new IntIntHolder[count];
		for (int i = 0; i < count; i++)
		{
			int itemId = readD();
			int cnt = readD();
			
			if (itemId < 1 || cnt < 1)
			{
				_items = null;
				return;
			}
			
			_items[i] = new IntIntHolder(itemId, cnt);
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
		
		final NpcBuyList buyList = BuyListManager.getInstance().getBuyList(_listId);
		if (buyList == null)
			return;
		
		double castleTaxRate = 0;
		Npc merchant = null;
		
		if (buyList.getNpcId() > 0)
		{
			final WorldObject target = player.getTarget();
			if (target instanceof Merchant || target instanceof MercenaryManagerNpc)
				merchant = (Npc) target;
			
			if (merchant == null || !buyList.isNpcAllowed(merchant.getNpcId()) || !player.getAI().canDoInteract(merchant))
				return;
			
			if (merchant.getCastle() != null)
				castleTaxRate = merchant.getCastle().getTaxRate();
		}
		
		long subTotal = 0;
		long slots = 0;
		long weight = 0;
		
		for (IntIntHolder i : _items)
		{
			int price = -1;
			
			final Product product = buyList.get(i.getId());
			if (product == null)
				return;
			
			if (!product.getItem().isStackable() && i.getValue() > 1)
			{
				sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED));
				return;
			}
			
			price = product.getPrice();
			if (i.getId() >= 3960 && i.getId() <= 4026)
				price *= Config.RATE_SIEGE_GUARDS_PRICE;
			
			if (price < 0)
				return;
			
			if (price == 0 && !player.isGM())
				return;
			
			if (product.hasLimitedStock())
			{
				if (i.getValue() > product.getCount())
					return;
			}
			
			if ((Integer.MAX_VALUE / i.getValue()) < price)
				return;
			
			price = (int) (price * (1 + castleTaxRate));
			subTotal += i.getValue() * price;
			
			if (subTotal > Integer.MAX_VALUE)
				return;
			
			weight += i.getValue() * product.getItem().getWeight();
			if (!product.getItem().isStackable())
				slots += i.getValue();
			else if (player.getInventory().getItemByItemId(i.getId()) == null)
				slots++;
		}
		
		if (weight > Integer.MAX_VALUE || weight < 0 || !player.getInventory().validateWeight((int) weight))
		{
			sendPacket(SystemMessage.getSystemMessage(SystemMessageId.WEIGHT_LIMIT_EXCEEDED));
			return;
		}
		
		if (slots > Integer.MAX_VALUE || slots < 0 || !player.getInventory().validateCapacity((int) slots))
		{
			sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SLOTS_FULL));
			return;
		}
		
		if (subTotal < 0 || !player.reduceAdena((int) subTotal, false))
		{
			sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA));
			return;
		}
		
		for (IntIntHolder i : _items)
		{
			final Product product = buyList.get(i.getId());
			if (product == null)
				continue;
			
			if (product.hasLimitedStock())
			{
				if (product.decreaseCount(i.getValue()))
					player.getInventory().addItem(i.getId(), i.getValue());
			}
			else
				player.getInventory().addItem(i.getId(), i.getValue());
		}
		
		if (merchant != null)
		{
			if (merchant.getCastle() != null)
				merchant.getCastle().riseTaxRevenue((int) (subTotal * castleTaxRate));
			
			String htmlFolder = "";
			if (merchant instanceof Fisherman)
				htmlFolder = "fisherman";
			else if (merchant instanceof Merchant)
				htmlFolder = "merchant";
			
			if (!htmlFolder.isEmpty())
			{
				if (HTMLData.getInstance().exists(player, "html/" + htmlFolder + "/" + merchant.getNpcId() + "-bought.htm"))
				{
					final String content = HTMLData.getInstance().getHtm(player, "html/" + htmlFolder + "/" + merchant.getNpcId() + "-bought.htm");
					final NpcHtmlMessage html = new NpcHtmlMessage(merchant.getObjectId());
					html.setHtml(content);
					html.replace("%objectId%", merchant.getObjectId());
					player.sendPacket(html);
				}
			}
		}
		player.sendPacket(new ItemList(player, true));
		player.getInventory().updateWeight();
	}
}
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

import java.util.EnumMap;
import java.util.Map;

import ext.mods.Config;
import ext.mods.gameserver.data.manager.BuyListManager;
import ext.mods.gameserver.enums.Paperdoll;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Merchant;
import ext.mods.gameserver.model.buylist.NpcBuyList;
import ext.mods.gameserver.model.buylist.Product;
import ext.mods.gameserver.model.item.kind.Item;
import ext.mods.gameserver.model.itemcontainer.Inventory;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.ShopPreviewInfo;

public final class RequestPreviewItem extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private int _unk;
	private int _listId;
	private int _count;
	private int[] _items;
	
	@Override
	protected void readImpl()
	{
		_unk = readD();
		_listId = readD();
		_count = readD();
		
		if (_count < 0)
			_count = 0;
		else if (_count > 100)
			return;
			
		_items = new int[_count];
		
		for (int i = 0; i < _count; i++)
			_items[i] = readD();
	}
	
	@Override
	protected void runImpl()
	{
		if (_items == null)
			return;
		
		if (_count < 1 || _listId >= 4000000)
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final WorldObject target = player.getTarget();
		if (!player.isGM() && (!(target instanceof Merchant) || !player.isIn3DRadius(target, Npc.INTERACTION_DISTANCE)))
			return;
		
		final Merchant merchant = (target instanceof Merchant targetMerchant) ? targetMerchant : null;
		if (merchant == null)
			return;
		
		final NpcBuyList buyList = BuyListManager.getInstance().getBuyList(_listId);
		if (buyList == null)
			return;
		
		long totalPrice = 0;
		
		_listId = buyList.getListId();
		
		final Map<Paperdoll, Integer> items = new EnumMap<>(Paperdoll.class);
		for (int i = 0; i < _count; i++)
		{
			int itemId = _items[i];
			
			final Product product = buyList.get(itemId);
			if (product == null)
				return;
			
			final Item template = product.getItem();
			if (template == null)
				continue;
			
			final Paperdoll slot = Inventory.getPaperdollIndex(template.getBodyPart());
			if (slot == Paperdoll.NULL)
				continue;
			
			if (items.containsKey(slot))
			{
				player.sendPacket(SystemMessageId.YOU_CAN_NOT_TRY_THOSE_ITEMS_ON_AT_THE_SAME_TIME);
				return;
			}
			items.put(slot, itemId);
			
			totalPrice += Config.WEAR_PRICE;
			if (totalPrice > Integer.MAX_VALUE)
				return;
		}
		
		if (totalPrice < 0 || !player.reduceAdena((int) totalPrice, true))
		{
			player.sendPacket(SystemMessageId.YOU_NOT_ENOUGH_ADENA);
			return;
		}
		
		if (!items.isEmpty())
		{
			player.sendPacket(new ShopPreviewInfo(items));
			
		}
	}
}
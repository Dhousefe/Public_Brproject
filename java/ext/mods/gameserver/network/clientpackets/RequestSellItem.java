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
import ext.mods.gameserver.communitybbs.custom.ShopBBSManager;
import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Fisherman;
import ext.mods.gameserver.model.actor.instance.MercenaryManagerNpc;
import ext.mods.gameserver.model.actor.instance.Merchant;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;

public final class RequestSellItem extends L2GameClientPacket
{
	private static final int BATCH_LENGTH = 12;
	
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
			int objectId = readD();
			int itemId = readD();
			int cnt = readD();
			
			if (objectId < 1 || itemId < 1 || cnt < 1)
			{
				_items = null;
				return;
			}
			
			_items[i] = new IntIntHolder(objectId, cnt);
		}
	}
	
	public void sellBbs(Player player)
	{
		long totalPrice = 0;
		
		for (IntIntHolder i : _items)
		{
			ItemInstance item = player.checkItemManipulation(i.getId(), i.getValue());
			if (item == null || (!item.isSellable()))
				continue;
			
			int price = item.getReferencePrice() / 2;
			totalPrice += price * i.getValue();
			
			if ((Integer.MAX_VALUE / i.getValue()) < price || totalPrice > Integer.MAX_VALUE)
				return;
			
			if ((Integer.MAX_VALUE - player.getInventory().getAdena() - totalPrice) < 0)
			{
				player.sendPacket(SystemMessageId.SELL_ATTEMPT_FAILED);
				return;
			}
			
			player.getInventory().destroyItem(i.getId(), i.getValue());
		}
		
		player.addAdena((int) totalPrice, true);
	}
	
	@Override
	protected void runImpl()
	{
		if (_items == null)
			return;
		
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (_listId == ShopBBSManager.BBS_SELL_LIST_ID)
		{
			sellBbs(player);
			return;
		}
		
		final Npc merchant = (player.getTarget() instanceof Merchant || player.getTarget() instanceof MercenaryManagerNpc) ? (Npc) player.getTarget() : null;
		if (merchant == null || !player.getAI().canDoInteract(merchant))
			return;
		
		if (_listId > 1000000 && merchant.getTemplate().getNpcId() != _listId - 1000000)
			return;
		
		long totalPrice = 0;
		
		for (IntIntHolder i : _items)
		{
			ItemInstance item = player.checkItemManipulation(i.getId(), i.getValue());
			if (item == null || (!item.isSellable()))
				continue;
			
			int price = item.getReferencePrice() / 2;
			totalPrice += price * i.getValue();
			
			if ((Integer.MAX_VALUE / i.getValue()) < price || totalPrice > Integer.MAX_VALUE)
				return;
			
			if ((Integer.MAX_VALUE - player.getInventory().getAdena() - totalPrice) < 0)
			{
				player.sendPacket(SystemMessageId.SELL_ATTEMPT_FAILED);
				return;
			}
			
			player.getInventory().destroyItem(i.getId(), i.getValue());
		}
		
		player.addAdena((int) totalPrice, false);
		
		String htmlFolder = "";
		if (merchant instanceof Fisherman)
			htmlFolder = "fisherman";
		else if (merchant instanceof Merchant)
			htmlFolder = "merchant";
		
		if (!htmlFolder.isEmpty())
		{
			if (HTMLData.getInstance().exists(player, "html/" + htmlFolder + "/" + merchant.getNpcId() + "-sold.htm"))
			{
				final String content = HTMLData.getInstance().getHtm(player, "html/" + htmlFolder + "/" + merchant.getNpcId() + "-sold.htm");
				final NpcHtmlMessage html = new NpcHtmlMessage(merchant.getObjectId());
				html.setHtml(content);
				html.replace("%objectId%", merchant.getObjectId());
				player.sendPacket(html);
			}
		}
	}
}
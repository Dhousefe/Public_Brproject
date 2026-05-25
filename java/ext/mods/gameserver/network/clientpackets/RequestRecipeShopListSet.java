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
import ext.mods.gameserver.data.xml.RecipeData;
import ext.mods.gameserver.enums.actors.OperateType;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.craft.ManufactureList;
import ext.mods.gameserver.model.records.ManufactureItem;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.RecipeShopManageList;
import ext.mods.gameserver.network.serverpackets.RecipeShopMsg;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public final class RequestRecipeShopListSet extends L2GameClientPacket
{
	private static final int BATCH_LENGTH = 8;
	
	private ManufactureItem[] _items;
	
	@Override
	protected void readImpl()
	{
		int count = readD();
		if (count < 0 || count > Config.MAX_ITEM_IN_PACKET || count * BATCH_LENGTH != _buf.remaining())
			count = 0;
		
		_items = new ManufactureItem[count];
		
		for (int i = 0; i < count; i++)
		{
			final int recipeId = readD();
			final int cost = readD();
			
			_items[i] = new ManufactureItem(recipeId, cost, RecipeData.getInstance().getRecipeList(recipeId).isDwarven());
		}
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (ArraysUtil.isEmpty(_items))
		{
			player.setOperateType(OperateType.MANUFACTURE_MANAGE);
			player.sendPacket(new RecipeShopManageList(player, player.getManufactureList().isDwarven()));
			player.sendPacket(SystemMessageId.NO_RECIPES_REGISTERED);
			return;
		}
		
		if (_items.length > 20)
		{
			player.setOperateType(OperateType.MANUFACTURE_MANAGE);
			player.sendPacket(new RecipeShopManageList(player, player.getManufactureList().isDwarven()));
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.UP_TO_S1_RECIPES_CAN_REGISTER).addNumber(20));
			return;
		}
		
		if (!player.getRecipeBook().canPassManufactureProcess(_items))
		{
			player.setOperateType(OperateType.MANUFACTURE_MANAGE);
			player.sendPacket(new RecipeShopManageList(player, player.getManufactureList().isDwarven()));
			return;
		}
		
		if (!player.canOpenPrivateStore(false))
		{
			player.setOperateType(OperateType.MANUFACTURE_MANAGE);
			player.sendPacket(new RecipeShopManageList(player, player.getManufactureList().isDwarven()));
			return;
		}
		
		final ManufactureList manufactureList = player.getManufactureList();
		manufactureList.clear();
		
		manufactureList.set(_items);
		
		if (Config.RESTORE_STORE_ITEMS)
			player.saveTradeList();
		player.getMove().stop();
		player.sitDown();
		player.setOperateType(OperateType.MANUFACTURE);
		player.broadcastUserInfo();
		player.broadcastPacket(new RecipeShopMsg(player));
		OfflineTradersTable.getInstance().saveOfflineTraders(player);
	}
}
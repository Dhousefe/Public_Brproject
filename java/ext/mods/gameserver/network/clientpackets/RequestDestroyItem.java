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

import java.sql.Connection;
import java.sql.PreparedStatement;

import ext.mods.commons.pool.ConnectionPool;
import ext.mods.gameserver.data.manager.CursedWeaponManager;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.SystemMessageId;

public final class RequestDestroyItem extends L2GameClientPacket
{
	private static final String DELETE_PET = "DELETE FROM pets WHERE item_obj_id=?";
	
	private int _objectId;
	private int _count;
	
	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_count = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (player.isProcessingTransaction() || player.isOperating())
		{
			player.sendPacket(SystemMessageId.CANNOT_TRADE_DISCARD_DROP_ITEM_WHILE_IN_SHOPMODE);
			return;
		}
		
		final ItemInstance itemToRemove = player.getInventory().getItemByObjectId(_objectId);
		if (itemToRemove == null)
			return;
		
		if (_count < 1 || _count > itemToRemove.getCount())
		{
			player.sendPacket(SystemMessageId.CANNOT_DESTROY_NUMBER_INCORRECT);
			return;
		}
		
		if (!itemToRemove.isStackable() && _count > 1)
			return;
		
		final int itemId = itemToRemove.getItemId();
		if (!itemToRemove.isDestroyable() || CursedWeaponManager.getInstance().isCursed(itemId))
		{
			player.sendPacket((itemToRemove.isHeroItem()) ? SystemMessageId.HERO_WEAPONS_CANT_DESTROYED : SystemMessageId.CANNOT_DISCARD_THIS_ITEM);
			return;
		}
		
		if (itemToRemove.isEquipped() && (!itemToRemove.isStackable() || (itemToRemove.isStackable() && _count >= itemToRemove.getCount())))
			player.useEquippableItem(itemToRemove, false);
		
		if (itemToRemove.isSummonItem())
		{
			if ((player.getSummon() != null && player.getSummon().getControlItemId() == _objectId) || (player.isMounted() && player.getMountObjectId() == _objectId))
			{
				player.sendPacket(SystemMessageId.PET_SUMMONED_MAY_NOT_DESTROYED);
				return;
			}
			
			try (Connection con = ConnectionPool.getConnection();
				PreparedStatement ps = con.prepareStatement(DELETE_PET))
			{
				ps.setInt(1, _objectId);
				ps.execute();
			}
			catch (Exception e)
			{
				LOGGER.error("Couldn't delete pet item with objectid {}.", e, _objectId);
			}
		}
		
		player.destroyItem(_objectId, _count, true);
	}
}
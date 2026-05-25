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
package ext.mods.gameserver.taskmanager;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.enums.Paperdoll;
import ext.mods.gameserver.enums.items.ItemState;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.itemcontainer.listeners.OnEquipListener;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

/**
 * Manage {@link ItemInstance} as shadow item, temporary weapons with short lifespans.<br>
 * <br>
 * A Shadow Weapon has the same abilities and appearance as an ordinary weapon, but it has a limited lifespan.<br>
 * <br>
 * When a Shadow Weapon's total duration or mana reaches 0, it will be removed from inventory and disappear.<br>
 * <br>
 * The remaining duration or mana is consumed from the time the item is equipped, and the consumption ends when it is no longer equipped.<br>
 * <br>
 * Warning: If the item is equipped and unequipped multiple times, the remaining duration or mana is consumed faster than normal. Logging in and out of the game with the weapon equipped will also decrease its duration.<br>
 * <br>
 * As a short-term item, shadow weapons cannot be moved through trading, drop, or cargo, and it can only be stored in a private warehouse. Short-term items also cannot be enchanted, augmented, given a special ability or crystallized.<br>
 * <br>
 * A weapon that is obtained by using a Shadow Weapon Exchange Coupon has a lower total duration or mana than a Shadow Weapon bought at a weapon shop.
 */
public class ShadowItemTaskManager implements Runnable, OnEquipListener
{
	private final Map<ItemInstance, Player> _shadowItems = new ConcurrentHashMap<>();
	
	protected ShadowItemTaskManager()
	{
		ThreadPool.scheduleAtFixedRate(this, 1000, 1000);
	}
	
	@Override
	public final void run()
	{
		if (_shadowItems.isEmpty())
			return;
		
		for (Entry<ItemInstance, Player> entry : _shadowItems.entrySet())
		{
			final ItemInstance item = entry.getKey();
			final Player player = entry.getValue();
			
			item.decreaseMana(1);
			
			if (item.getManaLeft() <= 0)
			{
				if (item.isEquipped())
					player.useEquippableItem(item, false);
				
				player.destroyItem(item, true);
				
				_shadowItems.remove(item);
			}
			else
			{
				if (item.getManaLeft() == 60)
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1S_REMAINING_MANA_IS_NOW_1).addItemName(item.getItemId()));
				else if (item.getManaLeft() == 300)
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1S_REMAINING_MANA_IS_NOW_5).addItemName(item.getItemId()));
				else if (item.getManaLeft() == 600)
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1S_REMAINING_MANA_IS_NOW_10).addItemName(item.getItemId()));
				
				if (item.getManaLeft() % 60 == 0)
					item.updateState(player, ItemState.MODIFIED);
			}
		}
	}
	
	@Override
	public final void onEquip(Paperdoll slot, ItemInstance item, Playable playable)
	{
		if (!item.isShadowItem())
			return;
		
		if (!(playable instanceof Player player))
			return;
		
		if (item.getManaLeft() != item.getItem().getDuration() * 60)
			item.decreaseMana(60);
		
		_shadowItems.put(item, player);
	}
	
	@Override
	public final void onUnequip(Paperdoll slot, ItemInstance item, Playable actor)
	{
		if (!item.isShadowItem())
			return;
		
		_shadowItems.remove(item);
	}
	
	public final void remove(Player player)
	{
		if (_shadowItems.isEmpty())
			return;
		
		_shadowItems.values().removeAll(Collections.singleton(player));
	}
	
	public static final ShadowItemTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ShadowItemTaskManager INSTANCE = new ShadowItemTaskManager();
	}
}
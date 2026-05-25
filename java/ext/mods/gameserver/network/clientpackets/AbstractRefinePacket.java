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

import java.util.HashMap;
import java.util.Map;

import ext.mods.gameserver.enums.items.CrystalType;
import ext.mods.gameserver.enums.items.ItemLocation;
import ext.mods.gameserver.enums.items.WeaponType;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.LifeStone;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.Weapon;
import ext.mods.gameserver.network.SystemMessageId;

public abstract class AbstractRefinePacket extends L2GameClientPacket
{
	public static final int GRADE_NONE = 0;
	public static final int GRADE_MID = 1;
	public static final int GRADE_HIGH = 2;
	public static final int GRADE_TOP = 3;
	
	private static final Map<Integer, LifeStone> _lifeStones = new HashMap<>();
	
	static
	{
		int itemId = 8723;
		
		for (int i = 0; i < 10; i++, itemId++)
			_lifeStones.put(itemId, new LifeStone(GRADE_NONE, i));
		
		for (int i = 0; i < 10; i++, itemId++)
			_lifeStones.put(itemId, new LifeStone(GRADE_MID, i));
		
		for (int i = 0; i < 10; i++, itemId++)
			_lifeStones.put(itemId, new LifeStone(GRADE_HIGH, i));
		
		for (int i = 0; i < 10; i++, itemId++)
			_lifeStones.put(itemId, new LifeStone(GRADE_TOP, i));
	}
	
	protected static final LifeStone getLifeStone(int itemId)
	{
		return _lifeStones.get(itemId);
	}
	
	/**
	 * @param player : The {@link Player} to test.
	 * @param item : The {@link ItemInstance} to test.
	 * @param refinerItem : The {@link ItemInstance} used as Lifestone to test.
	 * @param gemstoneItem : The {@link ItemInstance} used as gemstone to test.
	 * @return True if the augmentation process is doable, otherwise return false.
	 */
	protected static final boolean isValid(Player player, ItemInstance item, ItemInstance refinerItem, ItemInstance gemstoneItem)
	{
		if (!isValid(player, item, refinerItem))
			return false;
		
		if (gemstoneItem.getOwnerId() != player.getObjectId())
			return false;
		
		if (gemstoneItem.getLocation() != ItemLocation.INVENTORY)
			return false;
		
		final CrystalType grade = item.getItem().getCrystalType();
		
		if (grade.getGemstoneId() != gemstoneItem.getItemId())
			return false;
		
		return gemstoneItem.getCount() >= grade.getGemstoneCount();
	}
	
	/**
	 * @param player : The {@link Player} to test.
	 * @param item : The {@link ItemInstance} to test.
	 * @param refinerItem : The {@link ItemInstance} used as Lifestone to test.
	 * @return True if the conditions for augmentation process are met for the {@link Player}, the {@link ItemInstance} and the {@link ItemInstance} used as Lifestone set as parameters.
	 */
	protected static final boolean isValid(Player player, ItemInstance item, ItemInstance refinerItem)
	{
		if (refinerItem == null)
			return false;
		
		if (!isValid(player, item))
			return false;
		
		if (refinerItem.getOwnerId() != player.getObjectId())
			return false;
		
		if (refinerItem.getLocation() != ItemLocation.INVENTORY)
			return false;
		
		final LifeStone ls = _lifeStones.get(refinerItem.getItemId());
		if (ls == null)
			return false;
		
		if (player.getStatus().getLevel() < ls.getPlayerLevel())
		{
			player.sendPacket(SystemMessageId.HARDENER_LEVEL_TOO_HIGH);
			return false;
		}
		
		return true;
	}
	
	/**
	 * @param player : The {@link Player} to test.
	 * @param item : The {@link ItemInstance} to test.
	 * @return True if the conditions for augmentation process are met for the {@link Player} and the {@link ItemInstance} set as parameters.
	 */
	protected static final boolean isValid(Player player, ItemInstance item)
	{
		if (item == null)
			return false;
		
		if (!isValid(player))
			return false;
		
		if (item.getOwnerId() != player.getObjectId() || item.isAugmented() || item.isHeroItem() || item.isShadowItem() || item.getItem().getCrystalType().isLesser(CrystalType.C))
			return false;
		
		if (item.getLocation() != ItemLocation.INVENTORY && item.getLocation() != ItemLocation.PAPERDOLL)
			return false;
		
		if (!(item.getItem() instanceof Weapon weapon))
			return false;
		
		return weapon.getItemType() != WeaponType.NONE && weapon.getItemType() != WeaponType.FISHINGROD;
	}
	
	/**
	 * @param player : The {@link Player} to test.
	 * @return True if the conditions for augmentation process are met for the {@link Player} set as parameter.
	 */
	protected static final boolean isValid(Player player)
	{
		if (player == null)
			return false;
		
		if (player.isOperating())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_AUGMENT_ITEMS_WHILE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP_IS_IN_OPERATION);
			return false;
		}
		
		if (player.isProcessingTransaction())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_AUGMENT_ITEMS_WHILE_TRADING);
			return false;
		}
		
		if (player.isDead())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_AUGMENT_ITEMS_WHILE_DEAD);
			return false;
		}
		
		if (player.isParalyzed())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_AUGMENT_ITEMS_WHILE_PARALYZED);
			return false;
		}
		
		if (player.isFishing())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_AUGMENT_ITEMS_WHILE_FISHING);
			return false;
		}
		
		if (player.isSitting())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_AUGMENT_ITEMS_WHILE_SITTING_DOWN);
			return false;
		}
		
		return !player.isCursedWeaponEquipped();
	}
}
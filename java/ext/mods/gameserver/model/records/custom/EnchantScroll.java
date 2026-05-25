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
package ext.mods.gameserver.model.records.custom;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.util.ArraysUtil;

import ext.mods.gameserver.enums.items.CrystalType;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.Item;
import ext.mods.gameserver.model.item.kind.Weapon;

public record EnchantScroll(int scrollId, CrystalType grade, boolean isWeapon, boolean cristalize, int returnVal, int[] chance, int[] chanceF, int[] chanceM, boolean message, int[] enchants, int enchantAmount, int returnOnFail)
{
	public EnchantScroll(StatSet set)
	{
		this(set.getInteger("id"), set.getEnum("grade", CrystalType.class, CrystalType.NONE), set.getBool("isWeapon"), set.getBool("crystalize", true), set.getInteger("return", 0), set.getIntegerArray("rate", ArraysUtil.EMPTY_INT_ARRAY), set.getIntegerArray("rateF", ArraysUtil.EMPTY_INT_ARRAY), set.getIntegerArray("rateM", ArraysUtil.EMPTY_INT_ARRAY), set.getBool("message", false), set.getIntegerArray("enchants", ArraysUtil.EMPTY_INT_ARRAY), set.getInteger("enchantAmount", 1), set.getInteger("returnOnFail", -1));
	}
	
	public int getChance(ItemInstance item)
	{
		int level = item.getEnchantLevel();
		
		if (item.getItem().getBodyPart() == Item.SLOT_FULL_ARMOR && level <= 4)
			return 100;
		
		if (chance == ArraysUtil.EMPTY_INT_ARRAY && item.getItem().getType2() == Item.TYPE2_WEAPON && isWeapon && item.isWeapon())
			return ((Weapon) item.getItem()).isMagical() ? level >= chanceM.length ? 0 : chanceM[level] : level >= chanceF.length ? 0 : chanceF[level];
		
		return level >= chance.length ? 0 : chance[level];
	}
	
	public boolean announceTheEnchant(ItemInstance item)
	{
		return item != null && message && ArraysUtil.contains(enchants, item.getEnchantLevel());
	}
	
	public boolean isValid(ItemInstance item)
	{
		if (grade != item.getItem().getCrystalType())
			return false;
		
		if (getChance(item) == 0)
			return false;
		
		switch (item.getItem().getType2())
		{
			case Item.TYPE2_WEAPON:
				return isWeapon;
			case Item.TYPE2_SHIELD_ARMOR:
			case Item.TYPE2_ACCESSORY:
				return !isWeapon;
			default:
				return false;
		}
	}
}
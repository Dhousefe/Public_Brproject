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
package ext.mods.gameserver.model.item.kind;

import ext.mods.commons.data.StatSet;

import ext.mods.gameserver.enums.items.ArmorType;

/**
 * This class is dedicated to the management of armors.
 */
public final class Armor extends Item
{
	private ArmorType _type;
	
	public Armor(StatSet set)
	{
		super(set);
		
		_type = set.getEnum("armor_type", ArmorType.class, ArmorType.NONE);
		
		if (getBodyPart() == Item.SLOT_NECK || getBodyPart() == Item.SLOT_FACE || getBodyPart() == Item.SLOT_HAIR || getBodyPart() == Item.SLOT_HAIRALL || (getBodyPart() & Item.SLOT_L_EAR) != 0 || (getBodyPart() & Item.SLOT_L_FINGER) != 0 || (getBodyPart() & Item.SLOT_BACK) != 0)
		{
			_type1 = Item.TYPE1_WEAPON_RING_EARRING_NECKLACE;
			_type2 = Item.TYPE2_ACCESSORY;
		}
		else
		{
			if (_type == ArmorType.NONE && getBodyPart() == Item.SLOT_L_HAND)
				_type = ArmorType.SHIELD;
			
			_type1 = Item.TYPE1_SHIELD_ARMOR;
			_type2 = Item.TYPE2_SHIELD_ARMOR;
		}
	}
	
	@Override
	public ArmorType getItemType()
	{
		return _type;
	}
	
	@Override
	public final int getItemMask()
	{
		return getItemType().mask();
	}
}
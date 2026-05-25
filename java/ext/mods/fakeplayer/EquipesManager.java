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
package ext.mods.fakeplayer;

import java.util.logging.Logger;

import ext.mods.gameserver.data.xml.ItemData;
import ext.mods.gameserver.enums.Paperdoll;
import ext.mods.gameserver.enums.actors.ClassId;
import ext.mods.gameserver.enums.items.ItemLocation;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.Item;

import ext.mods.fakeplayer.data.EquipesData;
import ext.mods.fakeplayer.holder.EquipesHolder;

public class EquipesManager
{
	private static final Logger _log = Logger.getLogger(EquipesManager.class.getName());
	
	public static void applyEquipment(Player fakePlayer)
	{
		ClassId classId = fakePlayer.getClassId();
		int level = fakePlayer.getStatus().getLevel();
		
		EquipesHolder armorSet = EquipesData.getInstance().getArmorSet(classId.name(), level);
		
		if (armorSet == null)
		{
			return;
		}
		
		equipItem(fakePlayer, armorSet.getRhand(), Paperdoll.RHAND);
		equipItem(fakePlayer, armorSet.getLhand(), Paperdoll.LHAND);
		equipItem(fakePlayer, armorSet.getHead(), Paperdoll.HEAD);
		equipItem(fakePlayer, armorSet.getChest(), Paperdoll.CHEST);
		equipItem(fakePlayer, armorSet.getLegs(), Paperdoll.LEGS);
		equipItem(fakePlayer, armorSet.getHands(), Paperdoll.GLOVES);
		equipItem(fakePlayer, armorSet.getFeet(), Paperdoll.FEET);
		equipItem(fakePlayer, armorSet.getNeck(), Paperdoll.NECK);
		equipItem(fakePlayer, armorSet.getLear(), Paperdoll.LEAR);
		equipItem(fakePlayer, armorSet.getRear(), Paperdoll.REAR);
		equipItem(fakePlayer, armorSet.getLring(), Paperdoll.LFINGER);
		equipItem(fakePlayer, armorSet.getRring(), Paperdoll.RFINGER);
	}
	
	private static void equipItem(Player player, int itemId, Paperdoll paperdollSlot)
	{
		if (itemId <= 0)
			return;
		
		Item item = ItemData.getInstance().getTemplate(itemId);
		if (item == null)
		{
			_log.warning("Phantom [" + player.getObjectId() + "] Armor Item com ID " + itemId + " dont have in database.");
			return;
		}
		
		ItemInstance itemInstance = ItemData.getInstance().createDummyItem(itemId);
		
		itemInstance.setLocation(ItemLocation.PAPERDOLL);
		
		player.getInventory().setPaperdollItem(paperdollSlot, itemInstance);
		player.broadcastUserInfo();
	}
}

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
package ext.mods.gameserver.model.itemcontainer.listeners;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.xml.ArmorSetData;
import ext.mods.gameserver.enums.Paperdoll;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.ArmorSet;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.Item;
import ext.mods.gameserver.network.serverpackets.SkillList;
import ext.mods.gameserver.skills.L2Skill;

public class ArmorSetListener implements OnEquipListener
{
	@Override
	public void onEquip(Paperdoll slot, ItemInstance item, Playable actor)
	{
		if (!item.isEquipable())
			return;
		
		final Player player = (Player) actor;
		
		if (item.getItem().getBodyPart() == Item.SLOT_ALLDRESS)
		{
			player.sendPacket(new SkillList(player));
			return;
		}
		
		final int chestId = player.getInventory().getItemIdFrom(Paperdoll.CHEST);
		if (chestId == 0)
			return;
		
		final ArmorSet armorSet = ArmorSetData.getInstance().getSet(chestId);
		if (armorSet == null)
			return;
		
		if (armorSet.containsItem(slot, item.getItemId()))
		{
			if (armorSet.containsAll(player))
			{
				L2Skill skill = SkillTable.getInstance().getInfo(armorSet.getSkillId(), 1);
				if (skill != null)
				{
					player.addSkill(SkillTable.getInstance().getInfo(3006, 1), false);
					player.addSkill(skill, false);
					player.sendPacket(new SkillList(player));
				}
				
				if (armorSet.containsShield(player))
				{
					L2Skill skills = SkillTable.getInstance().getInfo(armorSet.getShieldSkillId(), 1);
					if (skills != null)
					{
						player.addSkill(skills, false);
						player.sendPacket(new SkillList(player));
					}
				}
				
				if (armorSet.isEnchanted6(player))
				{
					int skillId = armorSet.getEnchant6skillId();
					if (skillId > 0)
					{
						L2Skill skille = SkillTable.getInstance().getInfo(skillId, 1);
						if (skille != null)
						{
							player.addSkill(skille, false);
							player.sendPacket(new SkillList(player));
						}
					}
				}
			}
		}
		else if (armorSet.containsShield(item.getItemId()) && armorSet.containsAll(player))
		{
			L2Skill skills = SkillTable.getInstance().getInfo(armorSet.getShieldSkillId(), 1);
			if (skills != null)
			{
				player.addSkill(skills, false);
				player.sendPacket(new SkillList(player));
			}
		}
	}
	
	@Override
	public void onUnequip(Paperdoll slot, ItemInstance item, Playable actor)
	{
		final Player player = (Player) actor;
		
		if (item.getItem().getBodyPart() == Item.SLOT_ALLDRESS)
		{
			player.sendPacket(new SkillList(player));
			return;
		}
		
		boolean remove = false;
		int removeSkillId1 = 0;
		int removeSkillId2 = 0;
		int removeSkillId3 = 0;
		
		if (slot == Paperdoll.CHEST)
		{
			final ArmorSet armorSet = ArmorSetData.getInstance().getSet(item.getItemId());
			if (armorSet == null)
				return;
			
			remove = true;
			removeSkillId1 = armorSet.getSkillId();
			removeSkillId2 = armorSet.getShieldSkillId();
			removeSkillId3 = armorSet.getEnchant6skillId();
		}
		else
		{
			final int chestId = player.getInventory().getItemIdFrom(Paperdoll.CHEST);
			if (chestId == 0)
				return;
			
			final ArmorSet armorSet = ArmorSetData.getInstance().getSet(chestId);
			if (armorSet == null)
				return;
			
			if (armorSet.containsItem(slot, item.getItemId()))
			{
				remove = true;
				removeSkillId1 = armorSet.getSkillId();
				removeSkillId2 = armorSet.getShieldSkillId();
				removeSkillId3 = armorSet.getEnchant6skillId();
			}
			else if (armorSet.containsShield(item.getItemId()))
			{
				remove = true;
				removeSkillId2 = armorSet.getShieldSkillId();
			}
		}
		
		if (remove)
		{
			if (removeSkillId1 != 0)
			{
				player.removeSkill(3006, false);
				player.removeSkill(removeSkillId1, false);
			}
			
			if (removeSkillId2 != 0)
				player.removeSkill(removeSkillId2, false);
			
			if (removeSkillId3 != 0)
				player.removeSkill(removeSkillId3, false);
			
			player.sendPacket(new SkillList(player));
		}
	}
	
	public static final ArmorSetListener getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ArmorSetListener INSTANCE = new ArmorSetListener();
	}
}
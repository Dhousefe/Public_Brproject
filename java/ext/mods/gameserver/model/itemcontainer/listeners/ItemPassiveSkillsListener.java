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

import ext.mods.gameserver.enums.Paperdoll;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.Item;
import ext.mods.gameserver.model.item.kind.Weapon;
import ext.mods.gameserver.network.serverpackets.SkillCoolTime;
import ext.mods.gameserver.network.serverpackets.SkillList;
import ext.mods.gameserver.skills.L2Skill;

public class ItemPassiveSkillsListener implements OnEquipListener
{
	@Override
	public void onEquip(Paperdoll slot, ItemInstance item, Playable actor)
	{
		final Player player = (Player) actor;
		final Item it = item.getItem();
		
		boolean update = false;
		boolean updateTimeStamp = false;
		
		if (it instanceof Weapon weapon)
		{
			if (item.isAugmented())
				item.getAugmentation().applyBonus(player);
			
			if (player.getSkillLevel(L2Skill.SKILL_EXPERTISE) < weapon.getCrystalType().getId())
				return;
			
			if (item.getEnchantLevel() >= 4)
			{
				final L2Skill enchant4Skill = weapon.getEnchant4Skill();
				if (enchant4Skill != null)
				{
					player.addSkill(enchant4Skill, false);
					update = true;
				}
			}
		}
		
		final IntIntHolder[] skills = it.getSkills();
		if (skills != null)
		{
			for (IntIntHolder skillInfo : skills)
			{
				if (skillInfo == null)
					continue;
				
				final L2Skill itemSkill = skillInfo.getSkill();
				if (itemSkill != null)
				{
					player.addSkill(itemSkill, false);
					
					if (itemSkill.isActive())
					{
						if (!player.getReuseTimeStamp().containsKey(itemSkill.getReuseHashCode()))
						{
							final int equipDelay = itemSkill.getEquipDelay();
							if (equipDelay > 0)
							{
								player.addTimeStamp(itemSkill, equipDelay);
								player.disableSkill(itemSkill, equipDelay);
							}
						}
						updateTimeStamp = true;
					}
					update = true;
				}
			}
		}
		
		if (update)
		{
			player.sendPacket(new SkillList(player));
			
			if (updateTimeStamp)
				player.sendPacket(new SkillCoolTime(player));
		}
	}
	
	@Override
	public void onUnequip(Paperdoll slot, ItemInstance item, Playable actor)
	{
		final Player player = (Player) actor;
		final Item it = item.getItem();
		
		boolean update = false;
		
		if (it instanceof Weapon weapon)
		{
			if (item.isAugmented())
				item.getAugmentation().removeBonus(player);
			
			if (item.getEnchantLevel() >= 4)
			{
				final L2Skill enchant4Skill = weapon.getEnchant4Skill();
				if (enchant4Skill != null)
				{
					player.removeSkill(enchant4Skill.getId(), false, enchant4Skill.isPassive() || enchant4Skill.isToggle());
					update = true;
				}
			}
		}
		
		final IntIntHolder[] skills = it.getSkills();
		if (skills != null)
		{
			for (IntIntHolder skillInfo : skills)
			{
				if (skillInfo == null)
					continue;
				
				final L2Skill itemSkill = skillInfo.getSkill();
				if (itemSkill != null)
				{
					boolean found = false;
					
					for (ItemInstance pItem : player.getInventory().getPaperdollItems())
					{
						if (it.getItemId() == pItem.getItemId())
						{
							found = true;
							break;
						}
					}
					
					if (!found)
					{
						player.removeSkill(itemSkill.getId(), false, itemSkill.isPassive() || itemSkill.isToggle());
						update = true;
					}
				}
			}
		}
		
		if (update)
			player.sendPacket(new SkillList(player));
	}
	
	public static final ItemPassiveSkillsListener getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ItemPassiveSkillsListener INSTANCE = new ItemPassiveSkillsListener();
	}
}
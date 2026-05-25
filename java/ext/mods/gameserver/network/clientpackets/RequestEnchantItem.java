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

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.custom.data.EnchantData;
import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.xml.ArmorSetData;
import ext.mods.gameserver.enums.Paperdoll;
import ext.mods.gameserver.enums.actors.MissionType;
import ext.mods.gameserver.enums.items.ItemLocation;
import ext.mods.gameserver.enums.items.WeaponType;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.container.player.MissionList;
import ext.mods.gameserver.model.item.ArmorSet;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.Armor;
import ext.mods.gameserver.model.item.kind.Item;
import ext.mods.gameserver.model.item.kind.Weapon;
import ext.mods.gameserver.model.records.custom.EnchantScroll;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.EnchantResult;
import ext.mods.gameserver.network.serverpackets.ItemList;
import ext.mods.gameserver.network.serverpackets.SkillList;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.taskmanager.ItemInstanceTaskManager;
import ext.mods.Config;
import ext.mods.gameserver.idfactory.IdFactory;
import ext.mods.gameserver.model.item.instance.ItemInstance;

public final class RequestEnchantItem extends L2GameClientPacket
{
	private int _objectId;
	
	@Override
	protected void readImpl()
	{
		_objectId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null || _objectId == 0)
			return;
		
		if (!player.isOnline() || getClient().isDetached())
		{
			player.setActiveEnchantItem(null);
			return;
		}
		
		if (player.isProcessingTransaction() || player.isOperating())
		{
			player.sendPacket(SystemMessageId.CANNOT_ENCHANT_WHILE_STORE);
			player.setActiveEnchantItem(null);
			player.sendPacket(EnchantResult.CANCELLED);
			return;
		}
		
		final ItemInstance item = player.getInventory().getItemByObjectId(_objectId);
		ItemInstance scroll = player.getActiveEnchantItem();
		
		if (item == null || scroll == null)
		{
			player.sendPacket(SystemMessageId.ENCHANT_SCROLL_CANCELLED);
			player.setActiveEnchantItem(null);
			player.sendPacket(EnchantResult.CANCELLED);
			return;
		}
		
		final EnchantScroll enchant = EnchantData.getInstance().getEnchantScroll(scroll);
		if (enchant == null)
			return;
		
		if (!isEnchantable(item) || !enchant.isValid(item) || item.getOwnerId() != player.getObjectId())
		{
			player.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITION);
			player.setActiveEnchantItem(null);
			player.sendPacket(EnchantResult.CANCELLED);
			return;
		}
		
		scroll = player.getInventory().destroyItem(scroll.getObjectId(), 1);
		if (scroll == null)
		{
			player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
			player.setActiveEnchantItem(null);
			player.sendPacket(EnchantResult.CANCELLED);
			return;
		}
		
		if (player.getActiveTradeList() != null)
		{
			player.cancelActiveTrade();
			player.sendPacket(SystemMessageId.TRADE_ATTEMPT_FAILED);
			return;
		}
		
		final MissionList mission = player.getMissions();
		synchronized (item)
		{
			double chance = enchant.getChance(item);
			
			if (item.getOwnerId() != player.getObjectId() || !isEnchantable(item) || chance < 0)
			{
				player.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITION);
				player.setActiveEnchantItem(null);
				player.sendPacket(EnchantResult.CANCELLED);
				return;
			}
			
			if (Rnd.get(100) < enchant.getChance(item))
			{
				player.sendPacket(item.getEnchantLevel() == 0 ? SystemMessage.getSystemMessage(SystemMessageId.S1_SUCCESSFULLY_ENCHANTED).addItemName(item.getItemId()) : SystemMessage.getSystemMessage(SystemMessageId.S1_S2_SUCCESSFULLY_ENCHANTED).addNumber(item.getEnchantLevel()).addItemName(item.getItemId()));
				
				int newEnchant = item.getEnchantLevel() + enchant.enchantAmount();
				item.setEnchantLevel(newEnchant, player);
				if (Config.UPGRADE_BOSS_JEWELS_ENCHANT && Config.UPGRADEABLE_BOSS_JEWELS.contains(item.getItemId())) {
				    int currentEnchant = item.getEnchantLevel();
				    for (Config.JewelUpgrade upgrade : Config.UPGRADE_BOSS_JEWELS) {
				        if (upgrade.itemId == item.getItemId() && upgrade.enchantLevel == currentEnchant) {
				            player.destroyItem(item.getObjectId(), 1, false);
				            ItemInstance newItem = new ItemInstance(IdFactory.getInstance().getNextId(), upgrade.newItemId);
				            newItem.setEnchantLevel(currentEnchant, player);
				            player.addItem(newItem, true);
				            player.getInventory().equipItemAndRecord(newItem);
				            ItemInstanceTaskManager.getInstance().add(newItem);
				            break;
				        }
				    }
				}
				
				if (item.isEquipped())
				{
					final Item it = item.getItem();
					
					if (it instanceof Weapon weapon && item.getEnchantLevel() == 4)
					{
						final L2Skill enchant4Skill = weapon.getEnchant4Skill();
						if (enchant4Skill != null)
						{
							player.addSkill(enchant4Skill, false);
							player.sendPacket(new SkillList(player));
						}
					}
					else if (it instanceof Armor && item.getEnchantLevel() == 6)
					{
						final int chestId = player.getInventory().getItemIdFrom(Paperdoll.CHEST);
						if (chestId != 0)
						{
							final ArmorSet armorSet = ArmorSetData.getInstance().getSet(chestId);
							if (armorSet != null && armorSet.isEnchanted6(player))
							{
								final int skillId = armorSet.getEnchant6skillId();
								if (skillId > 0)
								{
									final L2Skill skill = SkillTable.getInstance().getInfo(skillId, 1);
									if (skill != null)
									{
										player.addSkill(skill, false);
										player.sendPacket(new SkillList(player));
									}
								}
							}
						}
					}
				}
				
				player.sendPacket(EnchantResult.SUCCESS);
				
				final MissionType type = item.isWeapon() ? MissionType.ENCHANT_WEAPON : MissionType.ENCHANT_OTHER;
				if (mission.getMission(type).getValue() < item.getEnchantLevel())
					mission.set(type, item.getEnchantLevel(), false, false);
				
				mission.update(MissionType.ENCHANT_SUCCESS);
				
				player.sendPacket(new ItemList(player, false));
				if (enchant.announceTheEnchant(item) && enchant.message())
					World.announceToOnlinePlayers(player.getSysString(10_060, player.getName(), item.getEnchantLevel(), item.getName()));
			}
			else
			{
				if (item.isEquipped() && (enchant.cristalize() || enchant.returnVal() != -1))
				{
					final Item it = item.getItem();
					
					if (it instanceof Weapon weapon && item.getEnchantLevel() >= 4)
					{
						final L2Skill enchant4Skill = weapon.getEnchant4Skill();
						if (enchant4Skill != null)
						{
							player.removeSkill(enchant4Skill.getId(), false);
							player.sendPacket(new SkillList(player));
						}
					}
					else if (it instanceof Armor && item.getEnchantLevel() >= 6)
					{
						final int chestId = player.getInventory().getItemIdFrom(Paperdoll.CHEST);
						if (chestId != 0)
						{
							final ArmorSet armorSet = ArmorSetData.getInstance().getSet(chestId);
							if (armorSet != null && armorSet.isEnchanted6(player))
							{
								final int skillId = armorSet.getEnchant6skillId();
								if (skillId > 0)
								{
									player.removeSkill(skillId, false);
									player.sendPacket(new SkillList(player));
								}
							}
						}
					}
				}
				
				if (!enchant.cristalize())
				{
					
					if (enchant.returnOnFail() != -1)
					{
						player.sendMessage("Failed in Enchant. The enchant value of the item became " + enchant.returnOnFail() + ".");
						item.setEnchantLevel(enchant.returnOnFail(), player);
					}
					
					player.sendPacket(EnchantResult.UNSUCCESS);
				}
				else
				{
					int crystalId = item.getItem().getCrystalItemId();
					int count = item.getCrystalCount() - (item.getItem().getCrystalCount() + 1) / 2;
					if (count < 1)
						count = 1;
					
					final ItemInstance destroyItem = player.getInventory().destroyItem(item);
					if (destroyItem == null)
					{
						player.setActiveEnchantItem(null);
						player.sendPacket(EnchantResult.CANCELLED);
						return;
					}
					
					if (crystalId != 0)
					{
						player.getInventory().addItem(crystalId, count);
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S).addItemName(crystalId).addItemNumber(count));
					}
					
					if (item.getEnchantLevel() > 0)
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ENCHANTMENT_FAILED_S1_S2_EVAPORATED).addNumber(item.getEnchantLevel()).addItemName(item.getItemId()));
					else
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ENCHANTMENT_FAILED_S1_EVAPORATED).addItemName(item.getItemId()));
					
					player.sendPacket((crystalId == 0) ? EnchantResult.UNK_RESULT_4 : EnchantResult.UNK_RESULT_1);
				}
				mission.update(MissionType.ENCHANT_FAILED);
			}
			
			player.broadcastUserInfo();
			player.setActiveEnchantItem(null);
		}
	}
	
	protected static final boolean isEnchantable(ItemInstance item)
	{
		if (item.isHeroItem() || item.isShadowItem() || item.isEtcItem() || item.getItem().getItemType() == WeaponType.FISHINGROD || !item.isEnchantable())
			return false;
		
		if (item.getLocation() != ItemLocation.INVENTORY && item.getLocation() != ItemLocation.PAPERDOLL)
			return false;
		
		if (item.isWeapon())
			return !item.getWeaponItem().isTravelerWeapon();
		
		return true;
	}
}
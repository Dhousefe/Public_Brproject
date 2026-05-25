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

import ext.mods.Config;
import ext.mods.gameserver.communitybbs.custom.AuctionBBSManager;
import ext.mods.gameserver.custom.data.EquipGradeRestrictionData;
import ext.mods.gameserver.data.manager.ZoneManager;
import ext.mods.gameserver.enums.FloodProtector;
import ext.mods.gameserver.enums.Paperdoll;
import ext.mods.gameserver.enums.items.ActionType;
import ext.mods.gameserver.enums.items.CrystalType;
import ext.mods.gameserver.enums.items.EtcItemType;
import ext.mods.gameserver.enums.items.WeaponType;
import ext.mods.gameserver.enums.skills.SkillType;
import ext.mods.gameserver.handler.IItemHandler;
import ext.mods.gameserver.handler.ItemHandler;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Pet;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.Item;
import ext.mods.gameserver.model.zone.type.RandomZone;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;
import ext.mods.gameserver.skills.L2Skill;

public final class UseItem extends L2GameClientPacket
{
	private int _objectId;
	private boolean _ctrlPressed;
	
	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_ctrlPressed = readD() != 0;
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (!getClient().performAction(FloodProtector.USE_ITEM))
			return;
		
		if (player.isOperating())
		{
			player.sendPacket(SystemMessageId.ITEMS_UNAVAILABLE_FOR_STORE_MANUFACTURE);
			return;
		}
		
		if (player.getActiveTradeList() != null)
		{
			player.sendPacket(SystemMessageId.CANNOT_PICKUP_OR_USE_ITEM_WHILE_TRADING);
			return;
		}
		
		final ItemInstance item = player.getInventory().getItemByObjectId(_objectId);
		if (item == null)
			return;
		
		final RandomZone random = ZoneManager.getInstance().getZone(player, RandomZone.class);
		if (random != null)
		{
			if (random.getRestrictedItems() != null)
			{
				if (random.getRestrictedItems().contains(item.getItemId()))
				{
					player.sendMessage("This item cannot be used in this zone.");
					return;
				}
			}
		}
		
		final IntIntHolder[] sHolders = item.getItem().getSkills();
		if (sHolders != null)
		{
			for (final IntIntHolder sHolder : sHolders)
			{
				final L2Skill skill = sHolder.getSkill();
				if (player.isItemDisabled(item))
				{
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_PREPARED_FOR_REUSE).addSkillName(skill));
					return;
				}
			}
		}
		
		if (item.getItem().getType2() == Item.TYPE2_QUEST)
		{
			player.sendPacket(SystemMessageId.CANNOT_USE_QUEST_ITEMS);
			return;
		}
		
		if (player.isAlikeDead() || player.isStunned() || player.isSleeping() || player.isParalyzed() || player.isAfraid())
			return;
		
		if (!Config.KARMA_PLAYER_CAN_TELEPORT && player.getKarma() > 0)
		{
			if (sHolders != null)
			{
				for (final IntIntHolder sHolder : sHolders)
				{
					final L2Skill skill = sHolder.getSkill();
					if (skill != null && (skill.getSkillType() == SkillType.TELEPORT || skill.getSkillType() == SkillType.RECALL))
						return;
				}
			}
		}
		
		if (player.isFishing() && item.getItem().getDefaultAction() != ActionType.fishingshot)
		{
			player.sendPacket(SystemMessageId.CANNOT_DO_WHILE_FISHING_3);
			return;
		}
		
		if (item.isPetItem())
		{
			if (!player.hasPet())
			{
				player.sendPacket(SystemMessageId.CANNOT_EQUIP_PET_ITEM);
				return;
			}
			
			final Pet pet = ((Pet) player.getSummon());
			
			if (!item.getItem().isConditionAttached())
			{
				player.sendPacket(SystemMessageId.PET_CANNOT_USE_ITEM);
				return;
			}
			
			if (pet.isDead())
			{
				player.sendPacket(SystemMessageId.CANNOT_GIVE_ITEMS_TO_DEAD_PET);
				return;
			}
			
			if (!pet.getInventory().validateCapacity(item))
			{
				player.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_ANY_MORE_ITEMS);
				return;
			}
			
			if (!pet.getInventory().validateWeight(item, 1))
			{
				player.sendPacket(SystemMessageId.UNABLE_TO_PLACE_ITEM_YOUR_PET_IS_TOO_ENCUMBERED);
				return;
			}
			
			player.transferItem(_objectId, 1, pet);
			
			if (item.isEquipped())
			{
				pet.getInventory().unequipItemInSlot(item.getLocationSlot());
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_TOOK_OFF_S1).addItemName(item));
			}
			else
			{
				pet.getInventory().equipPetItem(item);
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_PUT_ON_S1).addItemName(item));
			}
		}
		else
		{
			if (!player.getInventory().canManipulateWithItemId(item.getItemId()))
				return;
			
			if (!item.isEquipped() && !item.getItem().checkCondition(player, player, true))
				return;
			
			if (_ctrlPressed && AuctionBBSManager.getInstance().selectItem(player, item))
				return;
			
			final Item equipItem = item.getItem();
			final CrystalType grade = equipItem.getCrystalType();
			if (!EquipGradeRestrictionData.getInstance().isEquipAllowed(grade))
			{
				String message = EquipGradeRestrictionData.getInstance().getBlockMessage(grade);
				player.sendMessage(message);
				return;
			}
			
			if (item.isEquipable())
			{
				switch (item.getItem().getBodyPart())
				{
					case Item.SLOT_LR_HAND, Item.SLOT_L_HAND, Item.SLOT_R_HAND:
						if (player.isMounted())
						{
							player.sendPacket(SystemMessageId.CANNOT_EQUIP_ITEM_DUE_TO_BAD_CONDITION);
							return;
						}
						
						if (player.isCursedWeaponEquipped())
							return;
						
						player.getAI().tryToUseItem(_objectId);
						break;
					
					default:
						if (player.isCursedWeaponEquipped() && item.getItemId() == 6408)
							return;
						
						final ItemInstance itemToTest = player.getInventory().getItemByObjectId(_objectId);
						if (itemToTest == null)
							return;
						
						player.useEquippableItem(itemToTest, false);
						
						break;
				}
			}
			else
			{
				if (player.getAttackType() == WeaponType.FISHINGROD && item.getItem().getItemType() == EtcItemType.LURE)
				{
					player.getInventory().setPaperdollItem(Paperdoll.LHAND, item);
					player.broadcastUserInfo();
					
					return;
				}
				
				final IItemHandler handler = ItemHandler.getInstance().getHandler(item.getEtcItem());
				if (handler != null)
					handler.useItem(player, item, _ctrlPressed);
				
				for (final Quest quest : item.getQuestEvents())
				{
					final QuestState state = player.getQuestList().getQuestState(quest.getName());
					if (state == null || !state.isStarted())
						continue;
					
					quest.onItemUse(item, player, player.getTarget());
				}
			}
		}
	}
}
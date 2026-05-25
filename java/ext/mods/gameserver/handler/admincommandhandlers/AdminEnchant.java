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
package ext.mods.gameserver.handler.admincommandhandlers;

import java.util.StringTokenizer;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.xml.ArmorSetData;
import ext.mods.gameserver.enums.Paperdoll;
import ext.mods.gameserver.handler.IAdminCommandHandler;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.ArmorSet;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.Armor;
import ext.mods.gameserver.model.item.kind.Item;
import ext.mods.gameserver.model.item.kind.Weapon;
import ext.mods.gameserver.network.serverpackets.ItemList;
import ext.mods.gameserver.network.serverpackets.SkillList;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.Config;
import ext.mods.gameserver.idfactory.IdFactory;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.taskmanager.ItemInstanceTaskManager;

public class AdminEnchant implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_enchant"
	};
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		st.nextToken();
		
		if (st.countTokens() == 2)
		{
			try
			{
				final Paperdoll paperdoll = Paperdoll.getEnumByName(st.nextToken());
				if (paperdoll == Paperdoll.NULL)
				{
					player.sendMessage("Unknown paperdoll slot.");
					return;
				}
				
				final int enchant = Integer.parseInt(st.nextToken());
				if (enchant < 0 || enchant > 65535)
				{
					player.sendMessage("You must set the enchant level between 0 - 65535.");
					return;
				}
				
				final Player targetPlayer = getTargetPlayer(player, true);
				
				final ItemInstance item = targetPlayer.getInventory().getItemFrom(paperdoll);
				if (item == null)
				{
					player.sendMessage(targetPlayer.getName() + " doesn't wear any item in " + paperdoll + " slot.");
					sendFile(player, "enchant.htm");
					return;
				}
				
				final Item toTestItem = item.getItem();
				final int oldEnchant = item.getEnchantLevel();
				
				if (oldEnchant == enchant)
				{
					player.sendMessage(targetPlayer.getName() + "'s " + toTestItem.getName() + " enchant is already set to " + enchant + ".");
					sendFile(player, "enchant.htm");
					return;
				}
				
				item.setEnchantLevel(enchant, player);
				if (Config.UPGRADE_BOSS_JEWELS_ENCHANT && Config.UPGRADEABLE_BOSS_JEWELS.contains(item.getItemId())) {
				    int currentEnchant = item.getEnchantLevel();
				    for (Config.JewelUpgrade upgrade : Config.UPGRADE_BOSS_JEWELS) {
				        if (upgrade.itemId == item.getItemId() && upgrade.enchantLevel == currentEnchant) {
				            player.destroyItem(item.getObjectId(), 1, false);
				            ItemInstance newItem = new ItemInstance(IdFactory.getInstance().getNextId(), upgrade.newItemId);
				            newItem.setEnchantLevel(currentEnchant, targetPlayer);
				            player.addItem(newItem, true);
				            player.getInventory().equipItemAndRecord(newItem);
				            ItemInstanceTaskManager.getInstance().add(newItem);
				            break;
				        }
				    }
				}
				sendFile(player, "enchant.htm");
				
				if (item.isEquipped())
				{
					final int currentEnchant = item.getEnchantLevel();
					
					if (toTestItem instanceof Weapon weapon)
					{
						if (oldEnchant >= 4 && currentEnchant < 4)
						{
							final L2Skill enchant4Skill = weapon.getEnchant4Skill();
							if (enchant4Skill != null)
							{
								targetPlayer.removeSkill(enchant4Skill.getId(), false);
								targetPlayer.sendPacket(new SkillList(targetPlayer));
							}
						}
						else if (oldEnchant < 4 && currentEnchant >= 4)
						{
							final L2Skill enchant4Skill = weapon.getEnchant4Skill();
							if (enchant4Skill != null)
							{
								targetPlayer.addSkill(enchant4Skill, false);
								targetPlayer.sendPacket(new SkillList(targetPlayer));
							}
						}
					}
					else if (toTestItem instanceof Armor)
					{
						if (oldEnchant >= 6 && currentEnchant < 6)
						{
							final int itemId = targetPlayer.getInventory().getItemIdFrom(Paperdoll.CHEST);
							if (itemId > 0)
							{
								final ArmorSet armorSet = ArmorSetData.getInstance().getSet(itemId);
								if (armorSet != null)
								{
									final int skillId = armorSet.getEnchant6skillId();
									if (skillId > 0)
									{
										targetPlayer.removeSkill(skillId, false);
										targetPlayer.sendPacket(new SkillList(targetPlayer));
									}
								}
							}
						}
						else if (oldEnchant < 6 && currentEnchant >= 6)
						{
							final int itemId = targetPlayer.getInventory().getItemIdFrom(Paperdoll.CHEST);
							if (itemId > 0)
							{
								final ArmorSet armorSet = ArmorSetData.getInstance().getSet(itemId);
								if (armorSet != null && armorSet.isEnchanted6(targetPlayer))
								{
									final int skillId = armorSet.getEnchant6skillId();
									if (skillId > 0)
									{
										final L2Skill skill = SkillTable.getInstance().getInfo(skillId, 1);
										if (skill != null)
										{
											targetPlayer.addSkill(skill, false);
											targetPlayer.sendPacket(new SkillList(targetPlayer));
										}
									}
								}
							}
						}
					}
				}
				
				targetPlayer.broadcastUserInfo();
				player.sendPacket(new ItemList(player, false));
				
				player.sendMessage(targetPlayer.getName() + "'s " + toTestItem.getName() + " enchant was modified from " + oldEnchant + " to " + enchant + ".");
			}
			catch (Exception e)
			{
				player.sendMessage("Please specify a new enchant value.");
			}
		}
		else
		{
			player.sendMessage("Usage: //enchant slot enchant");
			player.sendMessage("Slots: under|lear|rear|neck|lfinger|rfinger|head|rhand|lhand");
			player.sendMessage("Slots: gloves|chest|legs|feet|cloak|face|hair|hairall");
			sendFile(player, "enchant.htm");
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
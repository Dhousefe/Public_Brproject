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

import java.util.ArrayList;
import java.util.List;

import ext.mods.Config;
import ext.mods.gameserver.enums.FloodProtector;
import ext.mods.gameserver.model.Augmentation;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Folk;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.itemcontainer.PcInventory;
import ext.mods.gameserver.model.multisell.Entry;
import ext.mods.gameserver.model.multisell.Ingredient;
import ext.mods.gameserver.model.multisell.PreparedListContainer;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public class MultiSellChoose extends L2GameClientPacket
{
	private static final int CLAN_REPUTATION = 65336;
	private static final int PC_BANG_POINTS = 65436;
	
	private int _listId;
	private int _entryId;
	private int _amount;
	
	@Override
	protected void readImpl()
	{
		_listId = readD();
		_entryId = readD();
		_amount = readD();
	}
	
	@Override
	public void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (!getClient().performAction(FloodProtector.MULTISELL))
		{
			player.setMultiSell(null);
			return;
		}
		
		if (_amount < 1 || _amount > Config.MULTISELL_MAX_AMOUNT)
		{
			player.setMultiSell(null);
			return;
		}
		
		final PreparedListContainer list = player.getMultiSell();
		if (list == null || list.getId() != _listId)
		{
			player.setMultiSell(null);
			return;
		}
		
		if (_entryId < 1 || _entryId > list.getEntries().size())
		{
			player.setMultiSell(null);
			return;
		}
		
		final Folk folk = player.getCurrentFolk();
		if ((folk != null && !list.isNpcAllowed(folk.getNpcId())) || (folk == null && list.isNpcOnly()))
		{
			player.setMultiSell(null);
			return;
		}
		
		if (folk != null && !player.getAI().canDoInteract(folk))
		{
			player.setMultiSell(null);
			return;
		}
		
		final PcInventory inv = player.getInventory();
		final Entry entry = list.getEntries().get(_entryId - 1);
		if (entry == null)
		{
			player.setMultiSell(null);
			return;
		}
		
		if (!entry.isStackable() && _amount > 1)
		{
			player.setMultiSell(null);
			return;
		}
		
		int slots = 0;
		int weight = 0;
		
		for (Ingredient e : entry.getProducts())
		{
			if (e.getItemId() < 0)
				continue;
			
			if (!e.isStackable())
				slots += e.getItemCount() * _amount;
			else if (player.getInventory().getItemByItemId(e.getItemId()) == null)
				slots++;
			
			weight += e.getItemCount() * _amount * e.getWeight();
		}
		
		if (!inv.validateWeight(weight))
		{
			player.sendPacket(SystemMessageId.WEIGHT_LIMIT_EXCEEDED);
			return;
		}
		
		if (!inv.validateCapacity(slots))
		{
			player.sendPacket(SystemMessageId.SLOTS_FULL);
			return;
		}
		
		List<Ingredient> ingredientsList = new ArrayList<>(entry.getIngredients().size());
		boolean newIng;
		
		for (Ingredient e : entry.getIngredients())
		{
			newIng = true;
			
			for (int i = ingredientsList.size(); --i >= 0;)
			{
				Ingredient ex = ingredientsList.get(i);
				
				if (ex.getItemId() == e.getItemId() && ex.getEnchantLevel() == e.getEnchantLevel())
				{
					if (ex.getItemCount() > Integer.MAX_VALUE - e.getItemCount())
					{
						player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED);
						return;
					}
					
					final Ingredient ing = ex.getCopy();
					ing.setItemCount(ex.getItemCount() + e.getItemCount());
					ingredientsList.set(i, ing);
					
					newIng = false;
					break;
				}
			}
			
			if (newIng)
				ingredientsList.add(e);
		}
		
		for (Ingredient e : ingredientsList)
		{
			if (e.getItemCount() > Integer.MAX_VALUE / _amount)
			{
				player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED);
				return;
			}
			
			if (e.getItemId() == CLAN_REPUTATION)
			{
				if (player.getClan() == null)
				{
					player.sendPacket(SystemMessageId.YOU_ARE_NOT_A_CLAN_MEMBER);
					return;
				}
				
				if (!player.isClanLeader())
				{
					player.sendPacket(SystemMessageId.ONLY_THE_CLAN_LEADER_IS_ENABLED);
					return;
				}
				
				if (player.getClan().getReputationScore() < e.getItemCount() * _amount)
				{
					player.sendPacket(SystemMessageId.THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW);
					return;
				}
			}
			else if (e.getItemId() == PC_BANG_POINTS)
			{
				if (player.getPcCafePoints() < e.getItemCount() * _amount)
				{
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SHORT_OF_ACCUMULATED_POINTS));
					return;
				}
			}
			else
			{
				if (inv.getItemCount(e.getItemId(), list.getMaintainEnchantment() ? e.getEnchantLevel() : -1, false) < (!e.getMaintainIngredient() ? (e.getItemCount() * _amount) : e.getItemCount()))
				{
					player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
					return;
				}
			}
		}
		
		List<Augmentation> augmentation = new ArrayList<>();
		
		for (Ingredient e : entry.getIngredients())
		{
			if (e.getItemId() == CLAN_REPUTATION)
			{
				final int amount = e.getItemCount() * _amount;
				
				player.getClan().takeReputationScore(amount);
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DEDUCTED_FROM_CLAN_REP).addNumber(amount));
			}
			else if (e.getItemId() == PC_BANG_POINTS)
				player.decreasePcCafePoints(e.getItemCount() * _amount);
			else
			{
				ItemInstance itemToTake = inv.getItemByItemId(e.getItemId());
				if (itemToTake == null)
				{
					player.setMultiSell(null);
					return;
				}
				
				if (!e.getMaintainIngredient())
				{
					if (itemToTake.isStackable())
					{
						if (!player.destroyItem(itemToTake.getObjectId(), (e.getItemCount() * _amount), true))
						{
							player.setMultiSell(null);
							return;
						}
					}
					else
					{
						
						if (list.getMaintainEnchantment())
						{
							ItemInstance[] inventoryContents = inv.getAllItemsByItemId(e.getItemId(), e.getEnchantLevel(), false);
							for (int i = 0; i < (e.getItemCount() * _amount); i++)
							{
								if (inventoryContents[i].isAugmented())
									augmentation.add(inventoryContents[i].getAugmentation());
								
								if (!player.destroyItem(inventoryContents[i].getObjectId(), 1, true))
								{
									player.setMultiSell(null);
									return;
								}
							}
						}
						else
						{
							for (int i = 1; i <= (e.getItemCount() * _amount); i++)
							{
								ItemInstance[] inventoryContents = inv.getAllItemsByItemId(e.getItemId(), false);
								
								itemToTake = inventoryContents[0];
								if (itemToTake.getEnchantLevel() > 0)
								{
									for (ItemInstance item : inventoryContents)
									{
										if (item.getEnchantLevel() < itemToTake.getEnchantLevel())
										{
											itemToTake = item;
											
											if (itemToTake.getEnchantLevel() == 0)
												break;
										}
									}
								}
								
								if (!player.destroyItem(itemToTake.getObjectId(), 1, true))
								{
									player.setMultiSell(null);
									return;
								}
							}
						}
					}
				}
			}
		}
		
		for (Ingredient e : entry.getProducts())
		{
			if (e.getItemId() == CLAN_REPUTATION)
				player.getClan().addReputationScore(e.getItemCount() * _amount);
			else
			{
				if (e.isStackable())
					inv.addItem(e.getItemId(), e.getItemCount() * _amount);
				else
				{
					for (int i = 0; i < (e.getItemCount() * _amount); i++)
					{
						ItemInstance product = inv.addItem(e.getItemId(), 1);
						if (product != null && list.getMaintainEnchantment())
						{
							if (i < augmentation.size())
								product.setAugmentation(new Augmentation(augmentation.get(i).getId(), augmentation.get(i).getSkill()), player);
							
							product.setEnchantLevel(e.getEnchantLevel(), player);
						}
					}
				}
				
				SystemMessage sm;
				
				if (e.getItemCount() * _amount > 1)
					sm = SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S).addItemName(e.getItemId()).addNumber(e.getItemCount() * _amount);
				else
				{
					if (list.getMaintainEnchantment() && e.getEnchantLevel() > 0)
						sm = SystemMessage.getSystemMessage(SystemMessageId.ACQUIRED_S1_S2).addNumber(e.getEnchantLevel()).addItemName(e.getItemId());
					else
						sm = SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1).addItemName(e.getItemId());
				}
				player.sendPacket(sm);
			}
		}
		
		player.sendPacket(SystemMessageId.SUCCESSFULLY_TRADED_WITH_NPC);
		
		if (folk != null && entry.getTaxAmount() > 0)
			folk.getCastle().riseTaxRevenue(entry.getTaxAmount() * _amount);
	}
}
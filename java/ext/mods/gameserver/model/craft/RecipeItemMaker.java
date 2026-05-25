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
package ext.mods.gameserver.model.craft;

import ext.mods.commons.random.Rnd;

import ext.mods.Config;
import ext.mods.gameserver.enums.actors.MissionType;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.itemcontainer.Inventory;
import ext.mods.gameserver.model.records.ManufactureItem;
import ext.mods.gameserver.model.records.Recipe;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.RecipeItemMakeInfo;
import ext.mods.gameserver.network.serverpackets.RecipeShopItemInfo;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.skills.L2Skill;

/**
 * The core of craft system, which allow {@link Player} to exchange materials for a final product. Numerous checks are made (integrity checks, such as item existence, mana left, adena cost).<br>
 * <br>
 * Required mats / final product infos are controlled by a {@link Recipe}.
 */
public class RecipeItemMaker implements Runnable
{
	private boolean _isValid;
	
	protected final Recipe _recipe;
	protected final Player _player;
	protected final Player _target;
	protected final int _skillId;
	protected final int _skillLevel;
	protected double _manaRequired;
	protected int _price;
	
	public RecipeItemMaker(Player player, Recipe recipe, Player target)
	{
		_player = player;
		_target = target;
		_recipe = recipe;
		
		_isValid = false;
		_skillId = (_recipe.isDwarven()) ? L2Skill.SKILL_CREATE_DWARVEN : L2Skill.SKILL_CREATE_COMMON;
		_skillLevel = _player.getSkillLevel(_skillId);
		
		_manaRequired = _recipe.mpCost();
		
		_player.setCrafting(true);
		
		if (_player.isAlikeDead() || _target.isAlikeDead())
		{
			_player.sendPacket(ActionFailed.STATIC_PACKET);
			abort();
			return;
		}
		
		if (_player.isProcessingTransaction() || _target.isProcessingTransaction())
		{
			_target.sendPacket(ActionFailed.STATIC_PACKET);
			abort();
			return;
		}
		
		if (_player.getCast().isCastingNow() || _player.isAllSkillsDisabled())
		{
			_player.sendPacket(SystemMessageId.NO_RECIPE_BOOK_WHILE_CASTING);
			return;
		}
		
		if (!_target.getInventory().validateWeight(_recipe.product().getWeight()))
		{
			_target.sendPacket(SystemMessageId.WEIGHT_LIMIT_EXCEEDED);
			_target.sendPacket(ActionFailed.STATIC_PACKET);
			abort();
			return;
		}
		
		if (_recipe.level() > _skillLevel)
		{
			_player.sendPacket(ActionFailed.STATIC_PACKET);
			abort();
			return;
		}
		
		if (_player != _target)
		{
			for (ManufactureItem temp : _player.getManufactureList())
			{
				if (temp.recipeId() == _recipe.id())
				{
					_price = temp.cost();
					if (_target.getAdena() < _price)
					{
						_target.sendPacket(SystemMessageId.YOU_NOT_ENOUGH_ADENA);
						abort();
						return;
					}
					break;
				}
			}
		}
		
		if (!listItems(false))
		{
			abort();
			return;
		}
		
		if (_player.getStatus().getMp() < _manaRequired)
		{
			_target.sendPacket(SystemMessageId.NOT_ENOUGH_MP);
			abort();
			return;
		}
		
		if (!_target.getInventory().validateCapacity(1))
		{
			_target.sendPacket(SystemMessageId.SLOTS_FULL);
			abort();
			return;
		}
		
		_player.setCrafting(false);
		_isValid = true;
	}
	
	@Override
	public void run()
	{
		if (!Config.IS_CRAFTING_ENABLED)
		{
			_target.sendMessage("Item creation is currently disabled.");
			abort();
			return;
		}
		
		if (_player == null || _target == null)
		{
			abort();
			return;
		}
		
		if (!_player.isOnline() || !_target.isOnline())
		{
			abort();
			return;
		}
		
		_player.getStatus().reduceMp(_manaRequired);
		
		if (_target != _player && _price > 0)
		{
			final ItemInstance adenaTransfer = _target.transferItem(_target.getInventory().getAdenaInstance().getObjectId(), _price, _player);
			if (adenaTransfer == null)
			{
				_target.sendPacket(SystemMessageId.YOU_NOT_ENOUGH_ADENA);
				abort();
				return;
			}
		}
		
		if (!listItems(true))
		{
			abort();
			return;
		}
		
		if (Rnd.get(100) < _recipe.successRate())
		{
			_player.getMissions().update(MissionType.CRAFT_SUCCESS);
			rewardPlayer();
			updateMakeInfo(true);
		}
		else
		{
			if (_target != _player)
			{
				_player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CREATION_OF_S2_FOR_S1_AT_S3_ADENA_FAILED).addCharName(_target).addItemName(_recipe.product().getId()).addItemNumber(_price));
				_target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_FAILED_TO_CREATE_S2_FOR_S3_ADENA).addCharName(_player).addItemName(_recipe.product().getId()).addItemNumber(_price));
			}
			else
				_target.sendPacket(SystemMessageId.ITEM_MIXING_FAILED);
			
			updateMakeInfo(false);
			_player.getMissions().update(MissionType.CRAFT_FAIL);
		}
		
		_player.setCrafting(false);
	}
	
	/**
	 * Send to the {@link Player} customer {@link RecipeItemMakeInfo} (self crafting) or {@link RecipeShopItemInfo} (private workshop) packet.
	 * @param success : The result under a boolean, used by packet.
	 */
	private void updateMakeInfo(boolean success)
	{
		if (_target == _player)
			_target.sendPacket(new RecipeItemMakeInfo(_recipe.id(), _target, (success) ? 1 : 0));
		else
			_target.sendPacket(new RecipeShopItemInfo(_player, _recipe.id()));
	}
	
	/**
	 * List all required materials.
	 * @param remove : If true we also delete items from customer inventory.
	 * @return true if the {@link Player} customer got every item (with correct amount) on inventory.
	 */
	private boolean listItems(boolean remove)
	{
		final Inventory inv = _target.getInventory();
		
		boolean gotAllMats = true;
		for (IntIntHolder material : _recipe.materials())
		{
			final int quantity = material.getValue();
			if (quantity > 0)
			{
				final ItemInstance item = inv.getItemByItemId(material.getId());
				if (item == null || item.getCount() < quantity)
				{
					_target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.MISSING_S2_S1_TO_CREATE).addItemName(material.getId()).addItemNumber((item == null) ? quantity : quantity - item.getCount()));
					gotAllMats = false;
				}
			}
		}
		
		if (!gotAllMats)
			return false;
		
		if (remove)
		{
			for (IntIntHolder material : _recipe.materials())
			{
				inv.destroyItemByItemId(material.getId(), material.getValue());
				
				if (material.getValue() > 1)
					_target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S2_S1_DISAPPEARED).addItemName(material.getId()).addItemNumber(material.getValue()));
				else
					_target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED).addItemName(material.getId()));
			}
		}
		return true;
	}
	
	/**
	 * Abort the crafting mode for the {@link Player}.
	 */
	private void abort()
	{
		updateMakeInfo(false);
		_player.setCrafting(false);
	}
	
	/**
	 * Reward a {@link Player} with the result of a craft (retained into a {@link IntIntHolder}).
	 */
	private void rewardPlayer()
	{
		final int itemId = _recipe.product().getId();
		final int itemCount = _recipe.product().getValue();
		
		_target.getInventory().addItem(itemId, itemCount);
		
		if (_target != _player)
		{
			if (itemCount == 1)
			{
				_player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S2_CREATED_FOR_S1_FOR_S3_ADENA).addString(_target.getName()).addItemName(itemId).addItemNumber(_price));
				_target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CREATED_S2_FOR_S3_ADENA).addString(_player.getName()).addItemName(itemId).addItemNumber(_price));
			}
			else
			{
				_player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S2_S3_S_CREATED_FOR_S1_FOR_S4_ADENA).addString(_target.getName()).addNumber(itemCount).addItemName(itemId).addItemNumber(_price));
				_target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CREATED_S2_S3_S_FOR_S4_ADENA).addString(_player.getName()).addNumber(itemCount).addItemName(itemId).addItemNumber(_price));
			}
		}
		
		if (itemCount > 1)
			_target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S).addItemName(itemId).addNumber(itemCount));
		else
			_target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1).addItemName(itemId));
		
		_target.sendIU();
	}
	
	public boolean isValid()
	{
		return _isValid;
	}
}
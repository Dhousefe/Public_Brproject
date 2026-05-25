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

import ext.mods.gameserver.enums.items.CrystalType;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.skills.L2Skill;

public final class RequestCrystallizeItem extends L2GameClientPacket
{
	private int _objectId;
	private int _count;
	
	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_count = readD();
	}
	
	@Override
	protected void runImpl()
	{
		if (_count <= 0)
			return;
		
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (player.isOperating() || player.isCrystallizing())
		{
			player.sendPacket(SystemMessageId.CANNOT_TRADE_DISCARD_DROP_ITEM_WHILE_IN_SHOPMODE);
			return;
		}
		
		final int skillLevel = player.getSkillLevel(L2Skill.SKILL_CRYSTALLIZE);
		if (skillLevel <= 0)
		{
			player.sendPacket(SystemMessageId.CRYSTALLIZE_LEVEL_TOO_LOW);
			return;
		}
		
		final ItemInstance item = player.getInventory().getItemByObjectId(_objectId);
		if (item == null || item.isHeroItem() || item.isShadowItem())
			return;
		
		if (!item.getItem().isCrystallizable() || item.getItem().getCrystalCount() <= 0 || item.getItem().getCrystalType() == CrystalType.NONE)
			return;
		
		_count = Math.min(_count, item.getCount());
		
		boolean canCrystallize = true;
		
		switch (item.getItem().getCrystalType())
		{
			case C:
				if (skillLevel <= 1)
					canCrystallize = false;
				break;
			
			case B:
				if (skillLevel <= 2)
					canCrystallize = false;
				break;
			
			case A:
				if (skillLevel <= 3)
					canCrystallize = false;
				break;
			
			case S:
				if (skillLevel <= 4)
					canCrystallize = false;
				break;
		}
		
		if (!canCrystallize)
		{
			player.sendPacket(SystemMessageId.CRYSTALLIZE_LEVEL_TOO_LOW);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		player.setCrystallizing(true);
		
		if (item.isEquipped())
		{
			player.getInventory().unequipItemInSlotAndRecord(item.getLocationSlot());
			
			if (item.getEnchantLevel() > 0)
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EQUIPMENT_S1_S2_REMOVED).addNumber(item.getEnchantLevel()).addItemName(item.getItemId()));
			else
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DISARMED).addItemName(item.getItemId()));
		}
		
		ItemInstance removedItem = player.getInventory().destroyItem(_objectId, _count);
		
		player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CRYSTALLIZED).addItemName(removedItem.getItemId()));
		
		player.addItem(item.getItem().getCrystalItemId(), item.getCrystalCount(), true);
		
		player.broadcastUserInfo();
		player.setCrystallizing(false);
	}
}
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
package ext.mods.gameserver.handler.itemhandlers;

import ext.mods.gameserver.data.manager.ItemLevelDelevelManager;
import ext.mods.gameserver.handler.IItemHandler;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Dhousefe
 * @version 1.0
 * @since 2026-02-08
 * Handler para itens que dão level ou delevel ao personagem.
 * Itens devem ter handler="ItemLevelDelevel" no etcitem XML.
 * Configuração em items.properties: ItemLevelDelevel_&lt;itemId&gt;=&lt;levelAdd&gt;,&lt;levelRemove&gt;
 */
public class ItemLevelDelevel implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player player))
			return;

		if (player.isInOlympiadMode())
		{
			player.sendPacket(SystemMessageId.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
			return;
		}

		final ItemLevelDelevelManager manager = ItemLevelDelevelManager.getInstance();
		if (!manager.isConfigured(item.getItemId()))
		{
			player.sendMessage("This item is not configured for level/delevel.");
			return;
		}

		final int levelAdd = manager.getLevelAdd(item.getItemId());
		final int levelRemove = manager.getLevelRemove(item.getItemId());
		final int maxLevel = manager.getMaxLevel();
		int currentLevel = player.getStatus().getLevel();
		final int initialLevel = currentLevel;
		final long initialExp = player.getStatus().getExp();

		if (levelRemove != 0)
		{
			final int toRemove = (levelRemove == -1) ? (currentLevel - 1) : Math.min(levelRemove, currentLevel - 1);
			if (toRemove > 0)
			{
				final int targetLevel = Math.max(1, currentLevel - toRemove);
				final long targetExp = player.getStatus().getExpForLevel(targetLevel);
				final long expToRemove = Math.max(1, player.getStatus().getExp() - targetExp);
				player.getStatus().removeExpAndSp(expToRemove, 0);
				currentLevel = player.getStatus().getLevel();
			}
		}

		if (levelAdd != 0)
		{
			final int toAdd;
			if (levelAdd == -1)
				toAdd = maxLevel - currentLevel;
			else
				toAdd = Math.min(levelAdd, maxLevel - currentLevel);

			if (toAdd > 0)
			{
				final long expForTarget = player.getStatus().getExpForLevel(currentLevel + toAdd);
				player.getStatus().addExp(Math.max(0, expForTarget - player.getStatus().getExp()));
			}
		}

		player.destroyItem(item, 1, false);
		final int newLevel = player.getStatus().getLevel();
		if (newLevel > initialLevel)
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_INCREASED_YOUR_LEVEL));
		else if (newLevel < initialLevel)
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EXP_DECREASED_BY_S1).addNumber((int) Math.min(Integer.MAX_VALUE, initialExp - player.getStatus().getExp())));
		player.broadcastUserInfo();
	}
}

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

import java.text.SimpleDateFormat;

import ext.mods.Config;
import ext.mods.gameserver.custom.data.DonateData;
import ext.mods.gameserver.data.manager.ItemPremiumManager;
import ext.mods.gameserver.handler.IItemHandler;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.taskmanager.PremiumTaskManager;

/**
 * @author Dhousefe
 * @version 1.0
 * @since 2026-02-08
 * Handler para itens que concedem tempo de premium VIP ao personagem.
 */
public class ItemPremium implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player player))
			return;

		if (!Config.USE_PREMIUM_SERVICE)
		{
			player.sendMessage(player.getSysString(10_200));
			return;
		}

		if (player.isInOlympiadMode())
		{
			player.sendPacket(SystemMessageId.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
			return;
		}

		final ItemPremiumManager manager = ItemPremiumManager.getInstance();
		if (!manager.isConfigured(item.getItemId()))
		{
			player.sendMessage("This item is not configured for premium.");
			return;
		}

		final ItemPremiumManager.PremiumConfig config = manager.getConfig(item.getItemId());
		final long addMs = config.toMilliseconds();
		if (addMs <= 0)
		{
			player.sendMessage("Invalid premium configuration for this item.");
			return;
		}

		final long currentEnd = player.getPremServiceData();
		final long now = System.currentTimeMillis();
		final long baseTime = (currentEnd > now) ? currentEnd : now;
		final long newEndTime = baseTime + addMs;

		DonateData.updateDatabasePremium(newEndTime, player.getAccountName());
		player.setPremiumService(1);
		PremiumTaskManager.getInstance().add(player);
		player.destroyItem(item, 1, false);
		final String expiresAt = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(newEndTime);
		player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1).addString(
			"Premium VIP adicionado! Expira em: " + expiresAt));
		player.broadcastUserInfo();
	}
}

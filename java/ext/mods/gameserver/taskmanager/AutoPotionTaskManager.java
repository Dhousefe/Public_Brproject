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
package ext.mods.gameserver.taskmanager;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.commons.pool.ThreadPool;

import ext.mods.Config;
import ext.mods.gameserver.handler.ItemHandler;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;

public class AutoPotionTaskManager implements Runnable
{
	private static final Set<Player> PLAYERS = ConcurrentHashMap.newKeySet();
	private static boolean _working = false;
	
	protected AutoPotionTaskManager()
	{
		ThreadPool.scheduleAtFixedRate(this, 0, Config.ACP_PERIOD);
	}
	
	@Override
	public void run()
	{
		if (_working)
			return;
		
		_working = true;
		
		if (!PLAYERS.isEmpty())
		{
			for (Player player : PLAYERS)
			{
				if (player == null || player.isAlikeDead() || player.isOnlineInt() != 1 || !Config.AUTO_POTIONS_IN_OLYMPIAD && player.isInOlympiadMode())
				{
					remove(player);
					continue;
				}
				
				if (Config.AUTO_CP_ENABLED)
				{
					final boolean restoreCP = ((player.getStatus().getCp() / player.getStatus().getMaxCp()) * 100) <= player.isAcpCp();
					for (int itemId : Config.AUTO_CP_ITEM_IDS)
					{
						final ItemInstance cpPotion = player.getInventory().getItemByItemId(itemId);
						if (cpPotion != null && cpPotion.getCount() > 0)
						{
							if (restoreCP)
							{
								ItemHandler.getInstance().getHandler(cpPotion.getEtcItem()).useItem(player, cpPotion, false);
								player.sendMessage("Auto potion: Restored CP.");
								break;
							}
						}
					}
				}
				
				if (Config.AUTO_HP_ENABLED)
				{
					final boolean restoreHP = ((player.getStatus().getHp() / player.getStatus().getMaxHp()) * 100) <= player.isAcpHp();
					for (int itemId : Config.AUTO_HP_ITEM_IDS)
					{
						final ItemInstance hpPotion = player.getInventory().getItemByItemId(itemId);
						if (hpPotion != null && hpPotion.getCount() > 0)
						{
							if (restoreHP)
							{
								ItemHandler.getInstance().getHandler(hpPotion.getEtcItem()).useItem(player, hpPotion, false);
								player.sendMessage("Auto potion: Restored HP.");
								break;
							}
						}
					}
				}
				
				if (Config.AUTO_MP_ENABLED)
				{
					final boolean restoreMP = ((player.getStatus().getMp() / player.getStatus().getMaxMp()) * 100) <= player.isAcpMp();
					for (int itemId : Config.AUTO_MP_ITEM_IDS)
					{
						final ItemInstance mpPotion = player.getInventory().getItemByItemId(itemId);
						if (mpPotion != null && mpPotion.getCount() > 0)
						{
							if (restoreMP)
							{
								ItemHandler.getInstance().getHandler(mpPotion.getEtcItem()).useItem(player, mpPotion, false);
								player.sendMessage("Auto potion: Restored MP.");
								break;
							}
						}
					}
				}
			}
		}
		
		_working = false;
	}
	
	public void add(Player player)
	{
		if (!PLAYERS.contains(player))
			PLAYERS.add(player);
	}
	
	public void remove(Player player)
	{
		PLAYERS.remove(player);
	}
	
	public static AutoPotionTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final AutoPotionTaskManager INSTANCE = new AutoPotionTaskManager();
	}
}
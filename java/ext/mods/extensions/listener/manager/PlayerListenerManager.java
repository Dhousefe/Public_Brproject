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
package ext.mods.extensions.listener.manager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ext.mods.extensions.listener.actor.player.OnAutoSoulShotListener;
import ext.mods.extensions.listener.actor.player.OnItemDestroyListener;
import ext.mods.extensions.listener.actor.player.OnItemPickupListener;
import ext.mods.extensions.listener.actor.player.OnLevelUpListener;
import ext.mods.extensions.listener.actor.player.OnPlayerEnterListener;
import ext.mods.extensions.listener.actor.player.OnPlayerExitListener;
import ext.mods.extensions.listener.actor.player.OnPvpPkKillListener;
import ext.mods.extensions.listener.actor.player.OnSetClassListener;
import ext.mods.extensions.listener.actor.player.OnSetLevelListener;
import ext.mods.extensions.listener.actor.player.OnSkillEnchantSuccessListener;
import ext.mods.extensions.listener.actor.player.OnTeleportListener;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;

public class PlayerListenerManager
{
	private static final PlayerListenerManager INSTANCE = new PlayerListenerManager();
	
	private final List<OnPlayerEnterListener> enterListeners = new CopyOnWriteArrayList<>();
	private final List<OnPlayerExitListener> exitListeners = new CopyOnWriteArrayList<>();
	private final List<OnAutoSoulShotListener> autoSoulShotListeners = new CopyOnWriteArrayList<>();
	private final List<OnLevelUpListener> levelUpListeners = new CopyOnWriteArrayList<>();
	private final List<OnPvpPkKillListener> pvpPkKillListeners = new CopyOnWriteArrayList<>();
	private final List<OnSetLevelListener> setLevelListeners = new CopyOnWriteArrayList<>();
	private final List<OnTeleportListener> teleportListeners = new CopyOnWriteArrayList<>();
	private final List<OnSetClassListener> classListeners = new CopyOnWriteArrayList<>();
	private final List<OnSkillEnchantSuccessListener> skillEnchantListeners = new CopyOnWriteArrayList<>();
	private final List<OnItemPickupListener> itemPickupListeners = new CopyOnWriteArrayList<>();
	private final List<OnItemDestroyListener> itemDestroyListeners = new CopyOnWriteArrayList<>();
	
	private PlayerListenerManager()
	{
	}
	
	public static PlayerListenerManager getInstance()
	{
		return INSTANCE;
	}
	
	public void registerEnterListener(OnPlayerEnterListener listener)
	{
		enterListeners.add(listener);
	}
	
	public void unregisterEnterListener(OnPlayerEnterListener listener)
	{
		enterListeners.remove(listener);
	}
	
	public void registerExitListener(OnPlayerExitListener listener)
	{
		exitListeners.add(listener);
	}
	
	public void unregisterExitListener(OnPlayerExitListener listener)
	{
		exitListeners.remove(listener);
	}
	
	public void notifyPlayerEnter(Player player)
	{
		for (OnPlayerEnterListener listener : enterListeners)
		{
			listener.onPlayerEnter(player);
		}
	}
	
	public void notifyPlayerExit(Player player)
	{
		for (OnPlayerExitListener listener : exitListeners)
		{
			listener.onPlayerExit(player);
		}
	}
	
	public void registerAutoSoulShotListener(OnAutoSoulShotListener listener)
	{
		autoSoulShotListeners.add(listener);
	}
	
	public void unregisterAutoSoulShotListener(OnAutoSoulShotListener listener)
	{
		autoSoulShotListeners.remove(listener);
	}
	
	public void notifyAutoSoulShot(Player player, int itemId, boolean enabled)
	{
		for (OnAutoSoulShotListener listener : autoSoulShotListeners)
		{
			listener.onAutoSoulShot(player, itemId, enabled);
		}
	}
	
	public void registerLevelUpListener(OnLevelUpListener listener)
	{
		levelUpListeners.add(listener);
	}
	
	public void unregisterLevelUpListener(OnLevelUpListener listener)
	{
		levelUpListeners.remove(listener);
	}
	
	public void notifyLevelUp(Player player)
	{
		for (OnLevelUpListener listener : levelUpListeners)
		{
			listener.onLevelUp(player);
		}
	}
	
	public void registerPvpPkKillListener(OnPvpPkKillListener listener)
	{
		pvpPkKillListeners.add(listener);
	}
	
	public void unregisterPvpPkKillListener(OnPvpPkKillListener listener)
	{
		pvpPkKillListeners.remove(listener);
	}
	
	public void notifyPvpPkKill(Player killer, Player victim, boolean isPvp)
	{
		for (OnPvpPkKillListener listener : pvpPkKillListeners)
		{
			listener.onPvpPkKill(killer, victim, isPvp);
		}
	}
	
	public void registerSetLevelListener(OnSetLevelListener listener)
	{
		setLevelListeners.add(listener);
	}
	
	public void unregisterSetLevelListener(OnSetLevelListener listener)
	{
		setLevelListeners.remove(listener);
	}
	
	public void notifySetLevel(Player player, int newLevel)
	{
		for (OnSetLevelListener listener : setLevelListeners)
		{
			listener.onSetLevel(player, newLevel);
		}
	}
	
	public void registerTeleportListener(OnTeleportListener listener)
	{
		teleportListeners.add(listener);
	}
	
	public void unregisterTeleportListener(OnTeleportListener listener)
	{
		teleportListeners.remove(listener);
	}
	
	public void notifyTeleport(Player player, int x, int y, int z)
	{
		for (OnTeleportListener listener : teleportListeners)
		{
			listener.onTeleport(player, x, y, z);
		}
	}
	
	public void registerSetClassListener(OnSetClassListener listener)
	{
		classListeners.add(listener);
	}
	
	public void unregisterSetClassListener(OnSetClassListener listener)
	{
		classListeners.remove(listener);
	}
	
	public void notifySetClass(Player player, int newClassId)
	{
		for (OnSetClassListener listener : classListeners)
		{
			listener.onSetClass(player, newClassId);
		}
	}
	
	public void registerSkillEnchantSuccessListener(OnSkillEnchantSuccessListener listener)
	{
		skillEnchantListeners.add(listener);
	}
	
	public void unregisterSkillEnchantSuccessListener(OnSkillEnchantSuccessListener listener)
	{
		skillEnchantListeners.remove(listener);
	}
	
	public void notifySkillEnchantSuccess(Player player, int skillId, int skillLevel)
	{
		for (OnSkillEnchantSuccessListener listener : skillEnchantListeners)
		{
			listener.onSkillEnchantSuccess(player, skillId, skillLevel);
		}
	}
	
	public void registerItemPickupListener(OnItemPickupListener listener)
	{
		itemPickupListeners.add(listener);
	}
	
	public void unregisterItemPickupListener(OnItemPickupListener listener)
	{
		itemPickupListeners.remove(listener);
	}
	
	public void notifyItemPickup(Player player, ItemInstance item)
	{
		for (OnItemPickupListener listener : itemPickupListeners)
		{
			listener.onItemPickup(player, item);
		}
	}
	
	public void registerItemDestroyListener(OnItemDestroyListener listener)
	{
		itemDestroyListeners.add(listener);
	}
	
	public void unregisterItemDestroyListener(OnItemDestroyListener listener)
	{
		itemDestroyListeners.remove(listener);
	}
	
	public void notifyItemDestroy(Player player, ItemInstance item)
	{
		for (OnItemDestroyListener listener : itemDestroyListeners)
		{
			listener.onItemDestroy(player, item);
		}
	}
	
}
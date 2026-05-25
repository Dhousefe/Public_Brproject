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

import ext.mods.extensions.listener.game.inventory.OnEquipListener;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.item.instance.ItemInstance;

public class InventoryListenerManager
{
	private static final InventoryListenerManager INSTANCE = new InventoryListenerManager();
	
	private final List<OnEquipListener> equipListeners = new CopyOnWriteArrayList<>();
	
	private InventoryListenerManager()
	{
	}
	
	public static InventoryListenerManager getInstance()
	{
		return INSTANCE;
	}
	
	public void registerEquipListener(OnEquipListener listener)
	{
		equipListeners.add(listener);
	}
	
	public void unregisterEquipListener(OnEquipListener listener)
	{
		equipListeners.remove(listener);
	}
	
	public void notifyEquip(int slotId, ItemInstance item, Playable playable)
	{
		for (OnEquipListener listener : equipListeners)
		{
			listener.onEquip(slotId, item, playable);
		}
	}
	
	public void notifyUnequip(int slotId, ItemInstance item, Playable playable)
	{
		for (OnEquipListener listener : equipListeners)
		{
			listener.onUnequip(slotId, item, playable);
		}
	}
}

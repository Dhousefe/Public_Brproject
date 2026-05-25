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
package ext.mods.gameserver.model.itemcontainer.listeners;

import java.util.ArrayList;
import java.util.List;

import ext.mods.gameserver.enums.Paperdoll;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.itemcontainer.Inventory;

/**
 * Recorder of alterations in a given {@link Inventory}.
 */
public class ChangeRecorderListener implements OnEquipListener
{
	private final List<ItemInstance> _changed = new ArrayList<>();
	
	public ChangeRecorderListener(Inventory inventory)
	{
		inventory.addPaperdollListener(this);
	}
	
	@Override
	public void onEquip(Paperdoll slot, ItemInstance item, Playable actor)
	{
		if (!_changed.contains(item))
			_changed.add(item);
	}
	
	@Override
	public void onUnequip(Paperdoll slot, ItemInstance item, Playable actor)
	{
		if (!_changed.contains(item))
			_changed.add(item);
	}
	
	/**
	 * @return The array of alterated {@link ItemInstance}.
	 */
	public ItemInstance[] getChangedItems()
	{
		return _changed.toArray(new ItemInstance[_changed.size()]);
	}
}
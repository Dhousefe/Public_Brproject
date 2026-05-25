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
package ext.mods.gameserver.network.serverpackets;

import java.util.Collection;

import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInfo;
import ext.mods.gameserver.model.item.kind.Item;

public class AbstractInventoryUpdate extends L2GameServerPacket
{
	private final Collection<ItemInfo> _items;
	private final boolean _isPlayer;
	
	public AbstractInventoryUpdate(Playable playable)
	{
		_items = playable.getInventory().getUpdateList();
		_isPlayer = playable instanceof Player;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(_isPlayer ? 0x27 : 0xb3);
		writeH(_items.size());
		
		for (ItemInfo temp : _items)
		{
			final Item item = temp.getItem();
			
			writeH(temp.getState().ordinal());
			writeH(item.getType1());
			writeD(temp.getObjectId());
			writeD(item.getItemId());
			writeD(temp.getCount());
			writeH(item.getType2());
			writeH(temp.getCustomType1());
			writeH(temp.getEquipped());
			writeD(item.getBodyPart());
			writeH(temp.getEnchant());
			writeH(temp.getCustomType2());
			
			if (_isPlayer)
			{
				writeD(temp.getAugmentation());
				writeD(item.isQuestItem() ? -1 : temp.getDisplayedManaLeft());
			}
			
		}
		
		_items.clear();
	}
}
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

import static ext.mods.gameserver.data.xml.MultisellData.PAGE_SIZE;

import ext.mods.gameserver.model.multisell.Entry;
import ext.mods.gameserver.model.multisell.Ingredient;
import ext.mods.gameserver.model.multisell.ListContainer;

public class MultiSellList extends L2GameServerPacket
{
	private final ListContainer _list;
	
	private int _index;
	private int _size;
	
	private boolean _finished;
	
	public MultiSellList(ListContainer list, int index)
	{
		_list = list;
		_index = index;
		
		_size = list.getEntries().size() - index;
		if (_size > PAGE_SIZE)
		{
			_finished = false;
			_size = PAGE_SIZE;
		}
		else
			_finished = true;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xd0);
		writeD(_list.getId());
		writeD(1 + (_index / PAGE_SIZE));
		writeD(_finished ? 1 : 0);
		writeD(PAGE_SIZE);
		writeD(_size);
		
		while (_size-- > 0)
		{
			Entry ent = _list.getEntries().get(_index++);
			
			writeD(_index);
			writeD(0x00);
			writeD(0x00);
			writeC(ent.isStackable() ? 1 : 0);
			writeH(ent.getProducts().size());
			writeH(ent.getIngredients().size());
			
			for (Ingredient ing : ent.getProducts())
			{
				writeH(ing.getItemId());
				if (ing.getTemplate() != null)
				{
					writeD(ing.getTemplate().getBodyPart());
					writeH(ing.getTemplate().getType2());
				}
				else
				{
					writeD(0);
					writeH(65535);
				}
				writeD(ing.getItemCount());
				writeH(ing.getEnchantLevel());
				writeD(0x00);
				writeD(0x00);
			}
			
			for (Ingredient ing : ent.getIngredients())
			{
				writeH(ing.getItemId());
				writeH(ing.getTemplate() != null ? ing.getTemplate().getType2() : 65535);
				writeD(ing.getItemCount());
				writeH(ing.getEnchantLevel());
				writeD(0x00);
				writeD(0x00);
			}
		}
	}
}
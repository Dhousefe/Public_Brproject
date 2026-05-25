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

import ext.mods.Config;
import ext.mods.gameserver.model.buylist.NpcBuyList;
import ext.mods.gameserver.model.buylist.Product;
import ext.mods.gameserver.model.item.kind.Item;

public class ShopPreviewList extends L2GameServerPacket
{
	private final int _listId;
	private final int _money;
	private final int _expertise;
	private final Collection<Product> _list;
	
	public ShopPreviewList(NpcBuyList list, int currentMoney, int expertiseIndex)
	{
		_listId = list.getListId();
		_list = list.values();
		_money = currentMoney;
		_expertise = expertiseIndex;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xef);
		writeC(0xc0);
		writeC(0x13);
		writeC(0x00);
		writeC(0x00);
		writeD(_money);
		writeD(_listId);
		
		int newlength = 0;
		for (Product product : _list)
		{
			if (product.getItem().getCrystalType().getId() <= _expertise && product.getItem().isEquipable())
				newlength++;
		}
		writeH(newlength);
		
		for (Product product : _list)
		{
			if (product.getItem().getCrystalType().getId() <= _expertise && product.getItem().isEquipable())
			{
				writeD(product.getItemId());
				writeH(product.getItem().getType2());
				
				if (product.getItem().getType1() != Item.TYPE1_ITEM_QUESTITEM_ADENA)
					writeH(product.getItem().getBodyPart());
				else
					writeH(0x00);
					
				writeD(Config.WEAR_PRICE);
			}
		}
	}
}
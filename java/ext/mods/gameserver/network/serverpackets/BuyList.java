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

public final class BuyList extends L2GameServerPacket
{
	private final int _money;
	private final int _listId;
	private final Collection<Product> _list;
	private double _taxRate = 0;
	
	public BuyList(NpcBuyList list, int currentMoney, double taxRate)
	{
		_money = currentMoney;
		_listId = list.getListId();
		_list = list.values();
		_taxRate = taxRate;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x11);
		writeD(_money);
		writeD(_listId);
		writeH(_list.size());
		
		for (Product product : _list)
		{
			if (product.getCount() > 0 || !product.hasLimitedStock())
			{
				writeH(product.getItem().getType1());
				writeD(product.getItemId());
				writeD(product.getItemId());
				writeD((product.getCount() < 0) ? 0 : product.getCount());
				writeH(product.getItem().getType2());
				writeH(0x00);
				writeD(product.getItem().getBodyPart());
				writeH(0x00);
				writeH(0x00);
				writeH(0x00);
				
				if (product.getItemId() >= 3960 && product.getItemId() <= 4026)
					writeD((int) (product.getPrice() * Config.RATE_SIEGE_GUARDS_PRICE * (1 + _taxRate)));
				else
					writeD((int) (product.getPrice() * (1 + _taxRate)));
			}
		}
	}
}
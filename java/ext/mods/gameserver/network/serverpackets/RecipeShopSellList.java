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

import java.util.ArrayList;
import java.util.List;

import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.records.ManufactureItem;

public class RecipeShopSellList extends L2GameServerPacket
{
	private final int _adena;
	
	private final int _objectId;
	private final int _mp;
	private final int _maxMp;
	private final List<ManufactureItem> _manufactureList;
	
	public RecipeShopSellList(Player buyer, Player manufacturer)
	{
		_adena = buyer.getAdena();
		
		_objectId = manufacturer.getObjectId();
		_mp = (int) manufacturer.getStatus().getMp();
		_maxMp = manufacturer.getStatus().getMaxMp();
		_manufactureList = new ArrayList<>(manufacturer.getManufactureList());
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xd9);
		writeD(_objectId);
		writeD(_mp);
		writeD(_maxMp);
		writeD(_adena);
		
		writeD(_manufactureList.size());
		
		for (ManufactureItem item : _manufactureList)
		{
			writeD(item.recipeId());
			writeD(0x00);
			writeD(item.cost());
		}
	}
}
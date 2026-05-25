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

import ext.mods.gameserver.model.actor.Player;

public class ExStorageMaxCount extends L2GameServerPacket
{
	private final int _inventoryLimit;
	private final int _warehouseLimit;
	private final int _freightLimit;
	private final int _privateSellLimit;
	private final int _privateBuyLimit;
	private final int _dwarfRecipeLimit;
	private final int _commonRecipeLimit;
	
	public ExStorageMaxCount(Player player)
	{
		_inventoryLimit = player.getStatus().getInventoryLimit();
		_warehouseLimit = player.getStatus().getWareHouseLimit();
		_freightLimit = player.getStatus().getFreightLimit();
		_privateSellLimit = player.getStatus().getPrivateSellStoreLimit();
		_privateBuyLimit = player.getStatus().getPrivateBuyStoreLimit();
		_dwarfRecipeLimit = player.getStatus().getDwarfRecipeLimit();
		_commonRecipeLimit = player.getStatus().getCommonRecipeLimit();
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xfe);
		writeH(0x2e);
		writeD(_inventoryLimit);
		writeD(_warehouseLimit);
		writeD(_freightLimit);
		writeD(_privateSellLimit);
		writeD(_privateBuyLimit);
		writeD(_dwarfRecipeLimit);
		writeD(_commonRecipeLimit);
	}
}
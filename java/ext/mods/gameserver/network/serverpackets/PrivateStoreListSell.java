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

import ext.mods.gameserver.data.manager.SellBuffsManager;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.trade.TradeItem;
import ext.mods.gameserver.model.trade.TradeList;

public class PrivateStoreListSell extends L2GameServerPacket
{
	private final int _objectId;
	private final int _playerAdena;
	private final TradeList _items;
	private final Player _player;
	private final Player _seller;
	
	public PrivateStoreListSell(Player player, Player storePlayer)
	{
		_objectId = storePlayer.getObjectId();
		_playerAdena = player.getAdena();
		_items = storePlayer.getSellList();
		_player = player;
		_seller = storePlayer;
	}
	
	@Override
	protected final void writeImpl()
	{
		if (_seller.isSellingBuffs())
			SellBuffsManager.getInstance().sendBuffMenu(_player, _seller, 1);
		else
		{
			writeC(0x9b);
			writeD(_objectId);
			writeD(_items.isPackaged() ? 1 : 0);
			writeD(_playerAdena);
			writeD(_items.size());
			
			for (TradeItem item : _items)
			{
				writeD(item.getItem().getType2());
				writeD(item.getObjectId());
				writeD(item.getItem().getItemId());
				writeD(item.getCount());
				writeH(0x00);
				writeH(item.getEnchant());
				writeH(0x00);
				writeD(item.getItem().getBodyPart());
				writeD(item.getPrice());
				writeD(item.getItem().getReferencePrice());
			}
		}
	}
}
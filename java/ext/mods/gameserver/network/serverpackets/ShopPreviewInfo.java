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

import java.util.Map;

import ext.mods.gameserver.enums.Paperdoll;

public class ShopPreviewInfo extends L2GameServerPacket
{
	private final Map<Paperdoll, Integer> _items;
	
	public ShopPreviewInfo(Map<Paperdoll, Integer> items)
	{
		_items = items;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xf0);
		writeD(Paperdoll.TOTAL_SLOTS);
		writeD(_items.getOrDefault(Paperdoll.REAR, 0));
		writeD(_items.getOrDefault(Paperdoll.LEAR, 0));
		writeD(_items.getOrDefault(Paperdoll.NECK, 0));
		writeD(_items.getOrDefault(Paperdoll.RFINGER, 0));
		writeD(_items.getOrDefault(Paperdoll.LFINGER, 0));
		writeD(_items.getOrDefault(Paperdoll.HEAD, 0));
		writeD(_items.getOrDefault(Paperdoll.RHAND, 0));
		writeD(_items.getOrDefault(Paperdoll.LHAND, 0));
		writeD(_items.getOrDefault(Paperdoll.GLOVES, 0));
		writeD(_items.getOrDefault(Paperdoll.CHEST, 0));
		writeD(_items.getOrDefault(Paperdoll.LEGS, 0));
		writeD(_items.getOrDefault(Paperdoll.FEET, 0));
		writeD(_items.getOrDefault(Paperdoll.CLOAK, 0));
		writeD(_items.getOrDefault(Paperdoll.FACE, 0));
		writeD(_items.getOrDefault(Paperdoll.HAIR, 0));
		writeD(_items.getOrDefault(Paperdoll.HAIRALL, 0));
		writeD(_items.getOrDefault(Paperdoll.UNDER, 0));
	}
}
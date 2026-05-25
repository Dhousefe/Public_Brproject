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

import java.util.List;

import ext.mods.gameserver.data.xml.HennaData;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.records.Henna;

public class HennaEquipList extends L2GameServerPacket
{
	private final int _adena;
	private final int _maxHennas;
	private final List<Henna> _availableHennas;
	
	public HennaEquipList(Player player)
	{
		_adena = player.getAdena();
		_maxHennas = player.getHennaList().getMaxSize();
		_availableHennas = HennaData.getInstance().getHennas().stream().filter(h -> h.canBeUsedBy(player) && player.getInventory().getItemByItemId(h.dyeId()) != null).toList();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xe2);
		writeD(_adena);
		writeD(_maxHennas);
		writeD(_availableHennas.size());
		
		for (Henna temp : _availableHennas)
		{
			writeD(temp.symbolId());
			writeD(temp.dyeId());
			writeD(Henna.DRAW_AMOUNT);
			writeD(temp.drawPrice());
			writeD(1);
		}
	}
}
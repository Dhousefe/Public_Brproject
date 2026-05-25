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

import java.util.Set;

import ext.mods.gameserver.data.sql.ClanTable;
import ext.mods.gameserver.model.pledge.Clan;

public class PledgeReceiveWarList extends L2GameServerPacket
{
	private final Set<Integer> _clanList;
	private final int _tab;
	private final int _page;
	
	public PledgeReceiveWarList(Set<Integer> clanList, int tab, int page)
	{
		_clanList = clanList;
		_tab = tab;
		_page = page;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xfe);
		writeH(0x3e);
		writeD(_tab);
		writeD(_page);
		writeD((_tab == 0) ? _clanList.size() : (_page == 0) ? (_clanList.size() >= 13) ? 13 : _clanList.size() : _clanList.size() % (13 * _page));
		
		int index = 0;
		for (int clanId : _clanList)
		{
			Clan clan = ClanTable.getInstance().getClan(clanId);
			if (clan == null)
				continue;
			
			if (_tab != 0)
			{
				if (index < _page * 13)
				{
					index++;
					continue;
				}
				
				if (index == (_page + 1) * 13)
					break;
				
				index++;
			}
			
			writeS(clan.getName());
			writeD(_tab);
			writeD(_page);
		}
	}
}
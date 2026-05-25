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

import ext.mods.commons.data.StatSet;

import ext.mods.gameserver.data.manager.HeroManager;
import ext.mods.gameserver.model.olympiad.Olympiad;

public class ExHeroList extends L2GameServerPacket
{
	private final Collection<StatSet> _sets;
	
	public ExHeroList()
	{
		_sets = HeroManager.getInstance().getHeroes().values();
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xfe);
		writeH(0x23);
		writeD(_sets.size());
		
		for (StatSet set : _sets)
		{
			writeS(set.getString(Olympiad.CHAR_NAME));
			writeD(set.getInteger(Olympiad.CLASS_ID));
			writeS(set.getString(HeroManager.CLAN_NAME, ""));
			writeD(set.getInteger(HeroManager.CLAN_CREST, 0));
			writeS(set.getString(HeroManager.ALLY_NAME, ""));
			writeD(set.getInteger(HeroManager.ALLY_CREST, 0));
			writeD(set.getInteger(HeroManager.COUNT));
		}
	}
}
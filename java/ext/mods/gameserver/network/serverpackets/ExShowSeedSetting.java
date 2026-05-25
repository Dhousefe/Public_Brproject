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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ext.mods.gameserver.data.manager.CastleManorManager;
import ext.mods.gameserver.model.manor.Seed;
import ext.mods.gameserver.model.manor.SeedProduction;

public class ExShowSeedSetting extends L2GameServerPacket
{
	private final int _manorId;
	private final Set<Seed> _seeds;
	private final Map<Integer, SeedProduction> _current = new HashMap<>();
	private final Map<Integer, SeedProduction> _next = new HashMap<>();
	
	public ExShowSeedSetting(int manorId)
	{
		final CastleManorManager manor = CastleManorManager.getInstance();
		
		_manorId = manorId;
		_seeds = manor.getSeedsForCastle(_manorId);
		
		for (Seed s : _seeds)
		{
			SeedProduction sp = manor.getSeedProduct(manorId, s.getSeedId(), false);
			if (sp != null)
				_current.put(s.getSeedId(), sp);
			
			sp = manor.getSeedProduct(manorId, s.getSeedId(), true);
			if (sp != null)
				_next.put(s.getSeedId(), sp);
		}
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xFE);
		writeH(0x1F);
		
		writeD(_manorId);
		writeD(_seeds.size());
		
		for (Seed s : _seeds)
		{
			writeD(s.getSeedId());
			writeD(s.getLevel());
			writeC(1);
			writeD(s.getReward1());
			writeC(1);
			writeD(s.getReward2());
			
			writeD(s.getSeedsLimit());
			writeD(s.getSeedReferencePrice());
			writeD(s.getSeedMinPrice());
			writeD(s.getSeedMaxPrice());
			
			SeedProduction sp = _current.get(s.getSeedId());
			if (sp != null)
			{
				writeD(sp.getStartAmount());
				writeD(sp.getPrice());
			}
			else
			{
				writeD(0);
				writeD(0);
			}
			
			sp = _next.get(s.getSeedId());
			if (sp != null)
			{
				writeD(sp.getStartAmount());
				writeD(sp.getPrice());
			}
			else
			{
				writeD(0);
				writeD(0);
			}
		}
	}
}
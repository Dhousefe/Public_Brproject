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

import ext.mods.gameserver.data.sql.ClanTable;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.model.pledge.ClanInfo;

public class AllianceInfo extends L2GameServerPacket
{
	private final String _name;
	private final int _total;
	private final int _online;
	private final String _clanName;
	private final String _leaderName;
	private final ClanInfo[] _allies;
	
	public AllianceInfo(int allianceId)
	{
		final Clan allianceClanLeader = ClanTable.getInstance().getClan(allianceId);
		
		_name = allianceClanLeader.getAllyName();
		_clanName = allianceClanLeader.getName();
		_leaderName = allianceClanLeader.getLeaderName();
		
		final Collection<Clan> allies = ClanTable.getInstance().getClanAllies(allianceId);
		
		_allies = new ClanInfo[allies.size()];
		
		int idx = 0;
		int total = 0;
		int online = 0;
		
		for (final Clan clan : allies)
		{
			final ClanInfo ci = new ClanInfo(clan);
			
			_allies[idx++] = ci;
			
			total += ci.getTotal();
			online += ci.getOnline();
		}
		
		_total = total;
		_online = online;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xb4);
		
		writeS(_name);
		writeD(_total);
		writeD(_online);
		writeS(_clanName);
		writeS(_leaderName);
		
		writeD(_allies.length);
		for (final ClanInfo aci : _allies)
		{
			writeS(aci.getClan().getName());
			writeD(0x00);
			writeD(aci.getClan().getLevel());
			writeS(aci.getClan().getLeaderName());
			writeD(aci.getTotal());
			writeD(aci.getOnline());
		}
	}
	
	public String getName()
	{
		return _name;
	}
	
	public int getTotal()
	{
		return _total;
	}
	
	public int getOnline()
	{
		return _online;
	}
	
	public String getClanName()
	{
		return _clanName;
	}
	
	public String getLeaderName()
	{
		return _leaderName;
	}
	
	public ClanInfo[] getAllies()
	{
		return _allies;
	}
}
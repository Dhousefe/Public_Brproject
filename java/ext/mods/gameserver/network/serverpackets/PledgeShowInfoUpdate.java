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

import ext.mods.gameserver.model.pledge.Clan;

public class PledgeShowInfoUpdate extends L2GameServerPacket
{
	private final int _clanId;
	private final int _crestId;
	private final int _level;
	private final int _castleId;
	private final int _chId;
	private final int _rank;
	private final int _reputation;
	private final int _dissolution;
	private final int _allyId;
	private final String _allyName;
	private final int _allyCrestId;
	private final int _atWar;
	
	public PledgeShowInfoUpdate(Clan clan)
	{
		_clanId = clan.getClanId();
		_crestId = clan.getCrestId();
		_level = clan.getLevel();
		_castleId = clan.getCastleId();
		_chId = clan.getClanHallId();
		_rank = clan.getRank();
		_reputation = clan.getReputationScore();
		_dissolution = clan.getDissolvingExpiryTime() > 0 ? 3 : 0;
		_allyId = clan.getAllyId();
		_allyName = clan.getAllyName();
		_allyCrestId = clan.getAllyCrestId();
		_atWar = (clan.isAtWar()) ? 1 : 0;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x88);
		writeD(_clanId);
		writeD(_crestId);
		writeD(_level);
		writeD(_castleId);
		writeD(_chId);
		writeD(_rank);
		writeD(_reputation);
		writeD(_dissolution);
		writeD(0);
		writeD(_allyId);
		writeS(_allyName);
		writeD(_allyCrestId);
		writeD(_atWar);
	}
}
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
import ext.mods.gameserver.model.records.Henna;

public class HennaItemInfo extends L2GameServerPacket
{
	private final Henna _henna;
	private final int _adena;
	private final int _int;
	private final int _str;
	private final int _con;
	private final int _men;
	private final int _dex;
	private final int _wit;
	
	public HennaItemInfo(Henna henna, Player player)
	{
		_henna = henna;
		_adena = player.getAdena();
		_int = player.getStatus().getINT();
		_str = player.getStatus().getSTR();
		_con = player.getStatus().getCON();
		_men = player.getStatus().getMEN();
		_dex = player.getStatus().getDEX();
		_wit = player.getStatus().getWIT();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xe3);
		writeD(_henna.symbolId());
		writeD(_henna.dyeId());
		writeD(Henna.DRAW_AMOUNT);
		writeD(_henna.drawPrice());
		writeD(1);
		writeD(_adena);
		writeD(_int);
		writeC(_int + _henna.INT());
		writeD(_str);
		writeC(_str + _henna.STR());
		writeD(_con);
		writeC(_con + _henna.CON());
		writeD(_men);
		writeC(_men + _henna.MEN());
		writeD(_dex);
		writeC(_dex + _henna.DEX());
		writeD(_wit);
		writeC(_wit + _henna.WIT());
	}
}
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
package ext.mods.gameserver.model.residence.clanhall;

import ext.mods.gameserver.data.sql.ClanTable;
import ext.mods.gameserver.model.pledge.Clan;

/**
 * A container used to retain ClanHall seller informations.<br>
 * <br>
 * A seller is a Player who registers a ClanHall on auction system. He is normally the Clan leader who sells his ClanHall.
 */
public class Seller
{
	private final String _name;
	private final String _clanName;
	
	private int _bid;
	
	public Seller(String name, String clanName, int bid)
	{
		_name = name;
		_clanName = clanName;
		_bid = bid;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public String getClanName()
	{
		return _clanName;
	}
	
	public int getBid()
	{
		return _bid;
	}
	
	public void setBid(int bid)
	{
		_bid = bid;
	}
	
	public Clan getClan()
	{
		return ClanTable.getInstance().getClanByName(_clanName);
	}
}
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

/**
 * A container used to retain ClanHall bidder informations.<br>
 * <br>
 * A bidder is a Player who bidded on a ClanHall auction. Biggest bidder is rewarded with the ClanHall in the end of auction time.
 */
public class Bidder extends Seller
{
	private long _time;
	
	public Bidder(String name, String clanName, int bid, long time)
	{
		super(name, clanName, bid);
		
		_time = time;
	}
	
	public long getTime()
	{
		return _time;
	}
	
	public void setTime(long time)
	{
		_time = time;
	}
}
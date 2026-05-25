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
package ext.mods.gameserver.communitybbs.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Favorite
{
	private final int _id;
	private final int _playerId;
	
	private final String _title;
	private final String _bypass;
	
	private final Timestamp _date;
	
	public Favorite(ResultSet rs) throws SQLException
	{
		_id = rs.getInt("id");
		_playerId = rs.getInt("player_id");
		_title = rs.getString("title");
		_bypass = rs.getString("bypass");
		_date = rs.getTimestamp("date");
	}
	
	public Favorite(int id, int playerId, String title, String bypass, Timestamp date)
	{
		_id = id;
		_playerId = playerId;
		_title = title;
		_bypass = bypass;
		_date = date;
	}
	
	public int getId()
	{
		return _id;
	}
	
	public int getPlayerId()
	{
		return _playerId;
	}
	
	public String getTitle()
	{
		return _title;
	}
	
	public String getBypass()
	{
		return _bypass;
	}
	
	public Timestamp getDate()
	{
		return _date;
	}
}
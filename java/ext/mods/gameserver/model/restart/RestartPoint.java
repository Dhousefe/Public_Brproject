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
package ext.mods.gameserver.model.restart;

import java.awt.Color;
import java.util.List;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.actors.ClassRace;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.network.serverpackets.ExServerPrimitive;

/**
 * A zone used as restart point when dead or scrolling out.<br>
 * <br>
 * It uses region-scale coordinates (eg. 22_11) in order to define which area you are set on.
 */
public class RestartPoint
{
	private final String _name;
	
	private final List<Location> _points;
	private final List<Location> _chaoPoints;
	private final List<IntIntHolder> _mapRegions;
	
	private final int _bbs;
	private final int _locName;
	
	private ClassRace _bannedRace;
	private String _bannedPoint;
	
	public RestartPoint(StatSet set)
	{
		_name = set.getString("name");
		
		_points = set.getList("points");
		_chaoPoints = set.getList("chaoPoints");
		_mapRegions = set.getList("mapRegions");
		
		_bbs = set.getInteger("bbs");
		_locName = set.getInteger("locName");
		
		if (set.containsKey("bannedRace"))
		{
			final String[] parsedBanned = set.getString("bannedRace").split(";");
			
			_bannedRace = Enum.valueOf(ClassRace.class, parsedBanned[0]);
			_bannedPoint = parsedBanned[1];
		}
	}
	
	public String getName()
	{
		return _name;
	}
	
	public Location getRandomPoint()
	{
		return Rnd.get(_points);
	}
	
	public Location getRandomChaoPoint()
	{
		return Rnd.get(_chaoPoints);
	}
	
	public List<IntIntHolder> getMapRegions()
	{
		return _mapRegions;
	}
	
	public int getBbs()
	{
		return _bbs;
	}
	
	public int getLocName()
	{
		return _locName;
	}
	
	public ClassRace getBannedRace()
	{
		return _bannedRace;
	}
	
	public String getBannedPoint()
	{
		return _bannedPoint;
	}
	
	public void visualizeZone(ExServerPrimitive debug)
	{
		for (Location point : _points)
			debug.addPoint("point", Color.GREEN, true, point);
		
		for (Location chaoPoint : _chaoPoints)
			debug.addPoint("chaoPoint", Color.RED, true, chaoPoint);
	}
}
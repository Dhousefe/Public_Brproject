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
package ext.mods.gameserver.model.zone.type.subtype;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.SpawnType;
import ext.mods.gameserver.model.location.Location;

/**
 * An abstract zone with spawn {@link Location}s, inheriting {@link ZoneType} behavior.
 */
public abstract class SpawnZoneType extends ZoneType
{
	private Map<SpawnType, List<Location>> _spawns = new EnumMap<>(SpawnType.class);
	
	protected SpawnZoneType(int id)
	{
		super(id);
	}
	
	/**
	 * Add a {@link Location} into the dedicated {@link SpawnType} {@link List}.<br>
	 * <br>
	 * If the key doesn't exist, generate a new {@link ArrayList}.
	 * @param type : The SpawnType to test.
	 * @param loc : The Location to add.
	 */
	public final void addSpawn(SpawnType type, Location loc)
	{
		_spawns.computeIfAbsent(type, k -> new ArrayList<>()).add(loc);
	}
	
	/**
	 * @param type : The SpawnType to test.
	 * @return the {@link List} of {@link Location}s based on {@link SpawnType} parameter. If that SpawnType doesn't exist, return the NORMAL List of Locations.
	 */
	public final List<Location> getSpawns(SpawnType type)
	{
		return _spawns.getOrDefault(type, _spawns.get(SpawnType.NORMAL));
	}
	
	/**
	 * @param type : The SpawnType to test.
	 * @return a random {@link Location} based on {@link SpawnType} parameter. If that SpawnType doesn't exist, return a NORMAL random Location.
	 */
	public final Location getRndSpawn(SpawnType type)
	{
		return Rnd.get(getSpawns(type));
	}
}
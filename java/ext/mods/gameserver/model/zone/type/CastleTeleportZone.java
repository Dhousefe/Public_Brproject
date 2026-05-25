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
package ext.mods.gameserver.model.zone.type;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.zone.type.subtype.ZoneType;

/**
 * A zone extending {@link ZoneType} used for Mass Gatekeepers to teleport players on a specific location.<br>
 * <br>
 * Summoning is forbidden. It holds a location under an int array, and castleId.
 */
public class CastleTeleportZone extends ZoneType
{
	private final int[] _spawnLoc;
	private int _castleId;
	
	public CastleTeleportZone(int id)
	{
		super(id);
		
		_spawnLoc = new int[5];
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("castleId"))
			_castleId = Integer.parseInt(value);
		else if (name.equals("spawnMinX"))
			_spawnLoc[0] = Integer.parseInt(value);
		else if (name.equals("spawnMaxX"))
			_spawnLoc[1] = Integer.parseInt(value);
		else if (name.equals("spawnMinY"))
			_spawnLoc[2] = Integer.parseInt(value);
		else if (name.equals("spawnMaxY"))
			_spawnLoc[3] = Integer.parseInt(value);
		else if (name.equals("spawnZ"))
			_spawnLoc[4] = Integer.parseInt(value);
		else
			super.setParameter(name, value);
	}
	
	@Override
	protected void onEnter(Creature creature)
	{
		creature.setInsideZone(ZoneId.NO_SUMMON_FRIEND, true);
	}
	
	@Override
	protected void onExit(Creature creature)
	{
		creature.setInsideZone(ZoneId.NO_SUMMON_FRIEND, false);
	}
	
	public void oustAllPlayers()
	{
		for (Player player : getKnownTypeInside(Player.class))
			player.teleportTo(Rnd.get(_spawnLoc[0], _spawnLoc[1]), Rnd.get(_spawnLoc[2], _spawnLoc[3]), _spawnLoc[4], 0);
	}
	
	public int getCastleId()
	{
		return _castleId;
	}
}
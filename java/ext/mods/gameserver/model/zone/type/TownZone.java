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

import ext.mods.Config;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.zone.type.subtype.SpawnZoneType;
import ext.mods.gameserver.model.zone.type.subtype.ZoneType;

/**
 * A zone extending {@link SpawnZoneType}, used by towns. A town zone is generally associated to a castle for taxes.
 */
public class TownZone extends ZoneType
{
	private int _townId;
	private int _castleId;
	
	private boolean _isPeaceZone = true;
	
	public TownZone(int id)
	{
		super(id);
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("townId"))
			_townId = Integer.parseInt(value);
		else if (name.equals("castleId"))
			_castleId = Integer.parseInt(value);
		else if (name.equals("isPeaceZone"))
			_isPeaceZone = Boolean.parseBoolean(value);
		else
			super.setParameter(name, value);
	}
	
	@Override
	protected void onEnter(Creature creature)
	{
		if (_isPeaceZone && Config.ZONE_TOWN != 2)
			creature.setInsideZone(ZoneId.PEACE, true);
		
		creature.setInsideZone(ZoneId.TOWN, true);
	}
	
	@Override
	protected void onExit(Creature creature)
	{
		if (_isPeaceZone)
			creature.setInsideZone(ZoneId.PEACE, false);
		
		creature.setInsideZone(ZoneId.TOWN, false);
	}
	
	public int getTownId()
	{
		return _townId;
	}
	
	public final int getCastleId()
	{
		return _castleId;
	}
}
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

import ext.mods.gameserver.data.manager.CastleManager;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.WorldRegion;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.residence.castle.Castle;
import ext.mods.gameserver.network.serverpackets.EventTrigger;

/**
 * A zone type extending {@link ZoneType} used for castle zones.
 */
public abstract class CastleZoneType extends ZoneType
{
	private int _castleId;
	private Castle _castle;
	
	private boolean _enabled;
	private int _eventId;
	
	protected CastleZoneType(int id)
	{
		super(id);
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("castleId"))
			_castleId = Integer.parseInt(value);
		else if (name.equals("eventId"))
			_eventId = Integer.parseInt(value);
		else
			super.setParameter(name, value);
	}
	
	@Override
	public void addKnownObject(WorldObject object)
	{
		if (_eventId > 0 && _enabled && object instanceof Player player)
			player.sendPacket(new EventTrigger(getEventId(), true));
	}
	
	@Override
	public void removeKnownObject(WorldObject object)
	{
		if (_eventId > 0 && object instanceof Player player)
			player.sendPacket(new EventTrigger(getEventId(), false));
	}
	
	public Castle getCastle()
	{
		if (_castleId > 0 && _castle == null)
			_castle = CastleManager.getInstance().getCastleById(_castleId);
		
		return _castle;
	}
	
	public int getEventId()
	{
		return _eventId;
	}
	
	public boolean isEnabled()
	{
		return _enabled;
	}
	
	public void setEnabled(boolean val)
	{
		_enabled = val;
		
		if (_eventId > 0)
		{
			final WorldRegion region = World.getInstance().getRegion(this);
			region.forEachSurroundingRegion(r -> r.forEachType(Player.class, null, p -> p.sendPacket(new EventTrigger(_eventId, val))));
		}
	}
}
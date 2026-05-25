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
package ext.mods.gameserver.model.actor.container.npc;

import ext.mods.gameserver.model.actor.Creature;

/**
 * This class contains all aggro informations (damage and hate) against a {@link Creature}.<br>
 * <br>
 * Values are limited to 999999999.
 */
public final class AggroInfo
{
	private final Creature _attacker;
	
	private double _damage;
	private double _hate;
	private long _timestamp;
	
	public AggroInfo(Creature attacker)
	{
		_attacker = attacker;
	}
	
	@Override
	public final boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		
		if (obj instanceof AggroInfo ai)
			return ai.getAttacker() == _attacker;
		
		return false;
	}
	
	@Override
	public final int hashCode()
	{
		return _attacker.getObjectId();
	}
	
	@Override
	public String toString()
	{
		return "AggroInfo [attacker=" + _attacker + ", damage=" + _damage + ", hate=" + _hate + "]";
	}
	
	public Creature getAttacker()
	{
		return _attacker;
	}
	
	public double getDamage()
	{
		return _damage;
	}
	
	public void addDamage(double value)
	{
		_damage = Math.min(_damage + value, 999999999);
	}
	
	public double getHate()
	{
		return _hate;
	}
	
	public void addHate(double value)
	{
		_hate = Math.min(_hate + value, 999999999);
	}
	
	public void stopHate()
	{
		_hate = 0;
	}
	
	public long getTimestamp()
	{
		return _timestamp;
	}
	
	public void setTimestamp(long timestamp)
	{
		_timestamp = timestamp;
	}
}
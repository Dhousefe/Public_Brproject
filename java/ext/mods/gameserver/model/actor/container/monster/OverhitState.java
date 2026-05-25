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
package ext.mods.gameserver.model.actor.container.monster;

import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Monster;

/**
 * A container holding all related informations of a {@link Monster} over-hit.<br>
 * <br>
 * An over-hit occurs when a {@link Player} procs a over-hit-friendly skill over a Monster. If that Monster dies due to that skill, the damage amount difference is calculated as exp points bonus.
 */
public class OverhitState
{
	private final Monster _owner;
	
	private boolean _isOverhit;
	private double _overhitDamage;
	private Creature _overhitAttacker;
	
	public OverhitState(Monster owner)
	{
		_owner = owner;
	}
	
	/**
	 * Set the over-hit flag on this {@link Monster}.
	 * @param status : The status of the over-hit flag.
	 */
	public void set(boolean status)
	{
		_isOverhit = status;
	}
	
	/**
	 * Test and eventually set the over-hit values based on the {@link Creature} who did the strike and the damage amount.
	 * @param attacker : The Creature who attacked this {@link Monster}.
	 * @param damage : The damage amount.
	 */
	public void test(Creature attacker, double damage)
	{
		if (!_isOverhit)
			return;
		
		if (damage <= 0)
		{
			_isOverhit = false;
			return;
		}
		
		final double overhitDamage = ((_owner.getStatus().getHp() - damage) * (-1));
		
		if (overhitDamage < 0)
		{
			_isOverhit = false;
			return;
		}
		
		_overhitDamage = overhitDamage;
		_overhitAttacker = attacker;
	}
	
	/**
	 * Clear all over-hit related variables.
	 */
	public void clear()
	{
		_isOverhit = false;
		_overhitDamage = 0;
		_overhitAttacker = null;
	}
	
	/**
	 * Calculate the bonus involved by over-hit over a given amount of exp points.
	 * @param normalExp : The base exp points.
	 * @return the calculated value (base exp points + bonus).
	 */
	public long calculateOverhitExp(long normalExp)
	{
		double overhitPercentage = ((_overhitDamage * 100) / _owner.getStatus().getMaxHp());
		
		if (overhitPercentage > 25)
			overhitPercentage = 25;
		
		double overhitExp = ((overhitPercentage / 100) * normalExp);
		
		return Math.round(overhitExp);
	}
	
	/**
	 * @param player : The Player to test.
	 * @return true if the hit is a valid over-hit (over-hit and attacker is the tested Player).
	 */
	public boolean isValidOverhit(Player player)
	{
		return _isOverhit && _overhitAttacker != null && _overhitAttacker.getActingPlayer() != null && player == _overhitAttacker.getActingPlayer();
	}
}
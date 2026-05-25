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
package ext.mods.gameserver.model.actor.container.attackable;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.instance.SiegeGuard;
import ext.mods.gameserver.scripting.script.ai.individual.DefaultNpc;

public class HateList extends ConcurrentHashMap<Creature, Double>
{
	private static final long serialVersionUID = 1L;
	
	private final Npc _owner;
	
	public HateList(Npc owner)
	{
		super();
		
		_owner = owner;
	}
	
	/**
	 * Add hate to the {@link Npc} owner, as stated by NPC variable SetAggressiveTime, linked to the {@link Creature} attacker.
	 * @param attacker : The {@link Creature} to hate.
	 */
	public void addDefaultHateInfo(Creature attacker)
	{
		int i0 = 0;
		
		if (_owner.isInMyTerritory())
		{
			final int aggressiveTime = DefaultNpc.getNpcIntAIParam(_owner, "SetAggressiveTime");
			if (aggressiveTime == -1)
			{
				if (_owner.getAI().getLifeTime() >= (Rnd.get(5) + 3))
					i0 = 1;
			}
			else if (aggressiveTime == 0)
				i0 = 1;
			else if (_owner.getAI().getLifeTime() > (aggressiveTime + Rnd.get(4)))
				i0 = 1;
			
			if (_owner.getAI().getLifeTime() > -1)
				i0 = 1;
		}
		
		addHateInfo(attacker, (isEmpty() && i0 == 1) ? 300 : 100);
	}
	
	/**
	 * Add hate to the {@link Npc} owner, linked to the {@link Creature} attacker.
	 * @param attacker : The {@link Creature} to hate.
	 * @param hateAmount : The hate to add.
	 */
	public void addHateInfo(Creature attacker, double hateAmount)
	{
		if (attacker == null || (_owner instanceof SiegeGuard && attacker instanceof SiegeGuard))
			return;
		
		merge(attacker, hateAmount, Double::sum);
	}
	
	/**
	 * @return The most hated {@link Creature} of the {@link Npc} owner, or null if none is found.
	 */
	public Creature getMostHatedCreature()
	{
		if (isEmpty() || _owner.isAlikeDead())
			return null;
		
		return Collections.max(entrySet(), Map.Entry.comparingByValue()).getKey();
	}
	
	/**
	 * @param target : The {@link Creature} whose hate level must be returned.
	 * @return The hate level of the {@link Npc} owner against the {@link Creature} set as target.
	 */
	public double getHate(Creature target)
	{
		return getOrDefault(target, 0.);
	}
	
	/**
	 * Clear the hate of a {@link Creature} target without removing it from the {@link HateList}.
	 * @param target : The {@link Creature} to clean hate.
	 */
	public void stopHate(Creature target)
	{
		remove(target);
	}
	
	/**
	 * Reduce hate for the whole {@link HateList}.<br>
	 * @param amount : The amount of hate to remove.
	 */
	public void reduceAllHate(double amount)
	{
		if (isEmpty())
			return;
		
		replaceAll((k, v) -> v - amount);
		
	}
	
	/**
	 * Clear the hate values of all registered hated {@link Creature}s, without dropping them.
	 */
	public void cleanAllHate()
	{
		if (isEmpty())
			return;
		
		replaceAll((k, v) -> 0.);
	}
	
	/**
	 * Drop invalid entries from this {@link HateList}, such as :
	 * <ul>
	 * <li>Dead and alike {@link Creature}s got their hate stopped.</li>
	 * <li>Invisible and unknown {@link Creature}s are simply dropped from the {@link HateList}.</li>
	 * </ul>
	 */
	public void refresh()
	{
		if (isEmpty())
			return;
		
		keySet().removeIf(c -> c.isAlikeDead() || !c.isVisible() || !_owner.knows(c) || (c.getActingPlayer() != null && !c.getActingPlayer().getAppearance().isVisible()));
	}
	
	/**
	 * Drop out of range entries from this {@link HateList} :
	 * @param range : The range to check in.
	 */
	public void removeIfOutOfRange(int range)
	{
		if (isEmpty())
			return;
		
		keySet().removeIf(c -> !_owner.isIn3DRadius(c, range));
	}
}
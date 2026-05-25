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

import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.Config;
import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.model.actor.Attackable;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.ai.Desire;
import ext.mods.gameserver.model.actor.container.npc.AggroInfo;
import ext.mods.gameserver.model.actor.instance.SiegeGuard;

public class AggroList extends ConcurrentHashMap<Creature, AggroInfo>
{
	private static final long serialVersionUID = 1L;
	
	private final Npc _owner;
	
	public AggroList(Npc owner)
	{
		super();
		
		_owner = owner;
	}
	
	/**
	 * Add damage and hate to the {@link AggroInfo} of the {@link Attackable} owner, linked to the {@link Creature} attacker.
	 * @param attacker : The {@link Creature} which dealt damages.
	 * @param damage : The amount of damages done.
	 * @param aggro : The hate to add.
	 */
	public void addDamageHate(Creature attacker, double damage, double aggro)
	{
		if (attacker == null)
			return;
		
		if (_owner instanceof SiegeGuard && attacker instanceof SiegeGuard)
			return;
		
		final AggroInfo ai = computeIfAbsent(attacker, AggroInfo::new);
		ai.addDamage(damage);
		ai.addHate(aggro);
		ai.setTimestamp(System.currentTimeMillis());
	}
	
	/**
	 * @return The most hated {@link AggroInfo} of the {@link Attackable} owner, or null if none is found.
	 */
	public AggroInfo getMostHated()
	{
		if (isEmpty() || _owner.isAlikeDead())
			return null;
		
		return values().stream().filter(ai -> ai.getHate() > 0).max(Comparator.comparing(AggroInfo::getHate)).orElse(null);
	}
	
	/**
	 * @return The most hated {@link Creature} of the {@link Attackable} owner, or null if none is found.
	 */
	public Creature getMostHatedCreature()
	{
		final AggroInfo ai = getMostHated();
		return (ai == null) ? null : ai.getAttacker();
	}
	
	/**
	 * @param target : The {@link Creature} whose hate level must be returned.
	 * @return The hate level of the {@link Attackable} owner against the {@link Creature} set as target.
	 */
	public double getHate(Creature target)
	{
		final AggroInfo ai = get(target);
		return (ai == null) ? 0 : ai.getHate();
	}
	
	/**
	 * Clear the hate of a {@link Creature} target without removing it from the {@link AggroList}.<br>
	 * <br>
	 * If none most hated {@link Creature} is found anymore, return the {@link Attackable} owner back to peace.
	 * @param target : The {@link Creature} to clean hate.
	 */
	public void stopHate(Creature target)
	{
		if (target == null || isEmpty())
			return;
		
		final AggroInfo ai = get(target);
		if (ai != null)
			ai.stopHate();
		
		if (getMostHated() == null && _owner.getAI().getHateList().getMostHatedCreature() == null)
			_owner.getAI().setBackToPeace();
	}
	
	/**
	 * Reduce hate for the whole {@link AggroList}.<br>
	 * <br>
	 * If none most hated {@link Creature} is found anymore, return the {@link Attackable} owner back to peace.
	 * @param amount : The amount of hate to remove.
	 */
	public void reduceAllHate(double amount)
	{
		if (isEmpty())
			return;
		
		for (AggroInfo ai : values())
			ai.addHate(-amount);
		
		if (getMostHated() == null && _owner.getAI().getHateList().getMostHatedCreature() == null)
			_owner.getAI().setBackToPeace();
	}
	
	/**
	 * Clear the hate values of all registered aggroed {@link Creature}s, without dropping them.
	 */
	public void cleanAllHate()
	{
		for (AggroInfo ai : values())
			ai.stopHate();
	}
	
	/**
	 * Method used when the {@link Attackable} owner can't attack his current target (immobilize state, for exemple).
	 * <ul>
	 * <li>If the {@link AggroList} is filled, pickup a new {@link Creature} from it.</li>
	 * <li>If the {@link AggroList} isn't filled, check if the {@link Attackable} owner is aggro type and pickup a new {@link Creature} using his knownlist.</li>
	 * </ul>
	 * @param range : The range to check. If set to 0, don't use the distance check.
	 * @return A {@link Creature} used as target, or null if no conditions are met.
	 */
	public Creature reconsiderTarget(int range)
	{
		if (size() > 1)
		{
			final AggroInfo mostHated = getMostHated();
			
			for (AggroInfo ai : values())
			{
				if (mostHated != null && mostHated.getAttacker() == ai.getAttacker())
					continue;
				
				if (ai.getHate() <= 0)
					continue;
				
				if (range > 0 && !_owner.isIn3DRadius(ai.getAttacker(), range))
					continue;
				
				if (!_owner.canAutoAttack(ai.getAttacker()))
					continue;
				
				if (mostHated == null)
					addDamageHate(ai.getAttacker(), 0, 2000);
				else
				{
					mostHated.stopHate();
					addDamageHate(ai.getAttacker(), 0, mostHated.getHate());
				}
				return ai.getAttacker();
			}
		}
		
		if (!(_owner instanceof SiegeGuard) && _owner.isAggressive())
		{
			for (Creature creature : _owner.getKnownTypeInRadius(Creature.class, _owner.getTemplate().getAggroRange()))
			{
				if (range > 0 && !_owner.isIn3DRadius(creature, range))
					continue;
				
				if (!_owner.canAutoAttack(creature))
					continue;
				
				addDamageHate(creature, 0, 1);
				return creature;
			}
		}
		
		return null;
	}
	
	/**
	 * Pick the most hated {@link AggroInfo}, then choose another {@link AggroInfo} and set it as the highest hated.
	 */
	public void randomizeAttack()
	{
		if (size() < 2)
			return;
		
		final AggroInfo mostHatedInfo = getMostHated();
		if (mostHatedInfo == null)
			return;
		
		final AggroInfo randomInfo = values().stream().filter(a -> a != mostHatedInfo && a.getHate() > 0 && _owner.canAutoAttack(a.getAttacker(), Config.PARTY_RANGE, true)).findAny().orElse(null);
		if (randomInfo == null)
			return;
		
		final double mostHate = mostHatedInfo.getHate();
		final double randomHate = randomInfo.getHate();
		
		mostHatedInfo.addHate(randomHate - mostHate);
		mostHatedInfo.setTimestamp(System.currentTimeMillis());
		
		randomInfo.addHate(mostHate - randomHate);
		randomInfo.setTimestamp(System.currentTimeMillis());
		
		for (Desire desire : _owner.getAI().getDesires())
		{
			if (desire.getType() == IntentionType.ATTACK)
			{
				if (desire.getFinalTarget() == mostHatedInfo.getAttacker())
					desire.setWeight(mostHatedInfo.getHate());
				else if (desire.getFinalTarget() == randomInfo.getAttacker())
					desire.setWeight(randomInfo.getHate());
			}
		}
	}
	
	/**
	 * Drop invalid entries from this {@link AggroList}, such as :
	 * <ul>
	 * <li>Dead and alike {@link Creature}s got their hate stopped.</li>
	 * <li>Invisible and unknown {@link Creature}s are simply dropped from the {@link AggroList}.</li>
	 * </ul>
	 */
	public void refresh()
	{
		if (isEmpty())
			return;
		
		for (AggroInfo ai : values())
		{
			final Creature creature = ai.getAttacker();
			
			if (creature.isAlikeDead())
				ai.stopHate();
			else if (!creature.isVisible() || !_owner.knows(creature) || (creature.getActingPlayer() != null && !creature.getActingPlayer().getAppearance().isVisible()))
				remove(creature);
		}
	}
}
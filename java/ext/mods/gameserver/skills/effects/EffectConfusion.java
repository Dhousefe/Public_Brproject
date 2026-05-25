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
package ext.mods.gameserver.skills.effects;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.skills.EffectFlag;
import ext.mods.gameserver.enums.skills.EffectType;
import ext.mods.gameserver.model.actor.Attackable;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Chest;
import ext.mods.gameserver.model.actor.instance.Door;
import ext.mods.gameserver.skills.AbstractEffect;
import ext.mods.gameserver.skills.L2Skill;

public class EffectConfusion extends AbstractEffect
{
	public EffectConfusion(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.CONFUSION;
	}
	
	@Override
	public boolean onStart()
	{
		if (getEffected() instanceof Player)
			return true;
		
		getEffected().getMove().stop();
		
		getEffected().updateAbnormalEffect();
		
		final Creature target = Rnd.get(getEffected().getKnownType(Creature.class, wo -> (wo instanceof Attackable || wo instanceof Playable) && wo != getEffected() && !(wo instanceof Door || wo instanceof Chest) && wo.distance2D(getEffected()) <= 1000));
		if (target == null)
			return true;
		
		if (getEffected() instanceof Playable targetPlayable)
			targetPlayable.getAI().tryToAttack(target, false, false);
		else if (getEffected() instanceof Npc targetNpc)
			targetNpc.getAI().addAttackDesire(target, Integer.MAX_VALUE);
		
		return true;
	}
	
	@Override
	public void onExit()
	{
		getEffected().updateAbnormalEffect();
		
		if (getEffected() instanceof Playable targetPlayable)
			targetPlayable.getAI().tryToFollow(getEffected().getActingPlayer(), false);
		else if (getEffected() instanceof Npc targetNpc)
			targetNpc.getAI().getAggroList().stopHate(targetNpc.getAI().getAggroList().getMostHatedCreature());
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
	
	@Override
	public int getEffectFlags()
	{
		return EffectFlag.CONFUSED.getMask();
	}
}
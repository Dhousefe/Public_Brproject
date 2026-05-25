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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.skills.L2Skill;

public class WarriorCastSelfRangeDDMagicAggressive extends Warrior
{
	public WarriorCastSelfRangeDDMagicAggressive()
	{
		super("ai/individual/Monster/WarriorBase/Warrior");
	}
	
	public WarriorCastSelfRangeDDMagicAggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21310,
		21381,
		21387,
		21401
	};
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (!(creature instanceof Playable))
			return;
		
		if (npc.getAI().getLifeTime() > 7 && npc.isInMyTerritory() && npc.getAI().getTopDesireTarget() == null)
		{
			if (npc.distance2D(creature) > 200)
			{
				final L2Skill longRangeDD = getNpcSkillByType(npc, NpcSkillType.W_LONG_RANGE_DD_MAGIC);
				npc.getAI().addCastDesire(creature, longRangeDD, 1000000);
			}
			
			tryToAttack(npc, creature);
			
			super.onSeeCreature(npc, creature);
		}
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
			if (topDesireTarget != null)
			{
				double dist = npc.distance2D(topDesireTarget);
				
				final L2Skill dryadRoot = SkillTable.getInstance().getInfo(1201, 1);
				if (dist > 40 && getAbnormalLevel(npc, dryadRoot) >= 0)
				{
					if (npc.getAttack().canAttack(topDesireTarget))
					{
						double f0 = getHateRatio(npc, attacker);
						f0 = (((1.0 * damage) / (npc.getStatus().getLevel() + 7)) + ((f0 / 100) * ((1.0 * damage) / (npc.getStatus().getLevel() + 7))));
						
						npc.getAI().addAttackDesire(attacker, f0 * 100);
					}
					else if (!npc.getAttack().canAttack(topDesireTarget) && dist < 600)
					{
						final L2Skill longRangeDD = getNpcSkillByType(npc, NpcSkillType.W_LONG_RANGE_DD_MAGIC);
						npc.getAI().addCastDesire(topDesireTarget, longRangeDD, 1000000);
					}
					else
					{
						npc.getAI().getAggroList().stopHate(topDesireTarget);
						
						double f0 = getHateRatio(npc, attacker);
						f0 = (((1.0 * damage) / (npc.getStatus().getLevel() + 7)) + ((f0 / 100) * ((1.0 * damage) / (npc.getStatus().getLevel() + 7))));
						
						npc.getAI().addAttackDesire(attacker, f0 * 100);
					}
				}
				
				dist = npc.distance2D(attacker);
				
				if (dist > 200 && dist < 600 && topDesireTarget == attacker)
				{
					final L2Skill longRangeDD = getNpcSkillByType(npc, NpcSkillType.W_LONG_RANGE_DD_MAGIC);
					npc.getAI().addCastDesire(attacker, longRangeDD, 1000000);
				}
				
				if (dist < 200 && topDesireTarget != attacker && Rnd.get(100) < 33)
				{
					final L2Skill selfRangeDD = getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_DD_MAGIC);
					npc.getAI().addCastDesire(attacker, selfRangeDD, 1000000);
				}
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable && called.getAI().getLifeTime() > 7)
		{
			final Creature topDesireTarget = called.getAI().getTopDesireTarget();
			if (topDesireTarget != null && topDesireTarget != attacker)
			{
				if (called.distance2D(attacker) < 200 && Rnd.get(100) < 33)
				{
					final L2Skill selfRangeDD = getNpcSkillByType(called, NpcSkillType.SELF_RANGE_DD_MAGIC);
					called.getAI().addCastDesire(called, selfRangeDD, 1000000);
				}
			}
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
	
	@Override
	public void onUseSkillFinished(Npc npc, Creature creature, L2Skill skill, boolean success)
	{
		final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
		if (topDesireTarget instanceof Player)
		{
			if (npc.distance2D(topDesireTarget) > 200)
			{
				final L2Skill longRangeDD = getNpcSkillByType(npc, NpcSkillType.W_LONG_RANGE_DD_MAGIC);
				npc.getAI().addCastDesire(topDesireTarget, longRangeDD, 1000000);
			}
		}
		super.onUseSkillFinished(npc, creature, skill, success);
	}
}
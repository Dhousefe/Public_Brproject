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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCastingCurse.WarriorCastingCurseAggressive;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.skills.L2Skill;

public class SpecialBloodyQueen extends WarriorCastingCurseAggressive
{
	public SpecialBloodyQueen()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCastingCurse/WarriorCastingCurseAggressive");
	}
	
	public SpecialBloodyQueen(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21084
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai0 = 0;
		
		super.onCreated(npc);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			if (npc._i_ai0 == 0)
			{
				final int i1 = Rnd.get(100);
				
				final double dist = npc.distance2D(attacker);
				if (dist > 300)
				{
					if (i1 < 80)
					{
						final L2Skill ddMagic = getNpcSkillByType(npc, NpcSkillType.DD_MAGIC);
						npc.getAI().addCastDesire(attacker, ddMagic, 1000000);
						
						npc._i_ai0 = 1;
					}
				}
				else if (dist > 100)
				{
					final Creature toDesireTarget = npc.getAI().getTopDesireTarget();
					if (toDesireTarget != null)
					{
						if ((toDesireTarget == attacker && i1 < 70) || i1 < 30)
						{
							final L2Skill ddMagic = getNpcSkillByType(npc, NpcSkillType.DD_MAGIC);
							npc.getAI().addCastDesire(attacker, ddMagic, 1000000);
							
							npc._i_ai0 = 1;
						}
					}
				}
				else if (i1 < 10)
				{
					final Creature toDesireTarget = npc.getAI().getTopDesireTarget();
					if (toDesireTarget != null && toDesireTarget == attacker)
					{
						final L2Skill ddMagic = getNpcSkillByType(npc, NpcSkillType.DD_MAGIC);
						npc.getAI().addCastDesire(attacker, ddMagic, 1000000);
						
						npc._i_ai0 = 1;
					}
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
			if (called.distance2D(attacker) > 100 && called._i_ai0 == 0)
			{
				if (Rnd.get(100) < 40)
				{
					final L2Skill ddMagic = getNpcSkillByType(called, NpcSkillType.DD_MAGIC);
					called.getAI().addCastDesire(attacker, ddMagic, 1000000);
					
					called._i_ai0 = 1;
				}
			}
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (npc.getAI().getLifeTime() > 7 && npc.isInMyTerritory())
		{
			if (npc.distance2D(creature) > 100 && npc._i_ai0 == 0)
			{
				if (Rnd.get(100) < 20)
				{
					final L2Skill ddMagic = getNpcSkillByType(npc, NpcSkillType.DD_MAGIC);
					npc.getAI().addCastDesire(creature, ddMagic, 1000000);
					
					npc._i_ai0 = 1;
				}
			}
		}
		super.onSeeCreature(npc, creature);
	}
}
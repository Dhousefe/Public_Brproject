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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCastSummonPC;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.Warrior;
import ext.mods.gameserver.skills.L2Skill;

public class WarriorCastSummonPC extends Warrior
{
	public WarriorCastSummonPC()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCastSummonPC");
	}
	
	public WarriorCastSummonPC(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		20213
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai0 = 0;
		npc._i_ai1 = 0;
		
		super.onCreated(npc);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			int i6 = Rnd.get(100);
			
			npc._c_ai0 = attacker;
			
			if (npc._i_ai0 == 0)
			{
				final double dist = npc.distance2D(attacker);
				if (dist > 300)
				{
					if (i6 < 50)
					{
						final L2Skill summonMagic = getNpcSkillByType(npc, NpcSkillType.SUMMON_MAGIC);
						if (npc.getCast().meetsHpMpDisabledConditions(attacker, summonMagic))
						{
							npc.getAI().addCastDesire(attacker, summonMagic, 1000000);
							
							npc._i_ai0 = 1;
							npc._i_ai1 = 1;
						}
					}
				}
				else if (dist > 100)
				{
					final Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
					if (mostHated != null)
					{
						if ((mostHated == attacker && i6 < 50) || i6 < 10)
						{
							final L2Skill summonMagic = getNpcSkillByType(npc, NpcSkillType.SUMMON_MAGIC);
							if (npc.getCast().meetsHpMpDisabledConditions(attacker, summonMagic))
							{
								npc.getAI().addCastDesire(attacker, summonMagic, 1000000);
								
								npc._i_ai0 = 1;
								npc._i_ai1 = 1;
							}
						}
					}
				}
			}
		}
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onUseSkillFinished(Npc npc, Creature creature, L2Skill skill, boolean success)
	{
		if (npc._c_ai0 != null && success && npc._i_ai1 == 1)
		{
			npc.abortAll(false);
			npc._c_ai0.teleportTo(npc.getPosition(), 0);
			
			npc._i_ai1 = 0;
		}
		
		final Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
		if (mostHated != null)
		{
			if (mostHated == npc._c_ai0 && Rnd.get(100) < 33)
			{
				final L2Skill physicalSpecial = getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL);
				if (physicalSpecial != null)
					npc.getAI().addCastDesire(npc._c_ai0, physicalSpecial, 1000000);
			}
		}
	}
}
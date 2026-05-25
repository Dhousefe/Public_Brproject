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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WizardBase.Wizard;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.skills.L2Skill;

public class RoyalRushHealer2 extends Wizard
{
	public RoyalRushHealer2()
	{
		super("ai/individual/Monster/WizardBase/Wizard");
	}
	
	public RoyalRushHealer2(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		18186,
		18221
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			if (npc._i_ai0 == 0)
			{
				final L2Skill wSelfRangeDebuff = getNpcSkillByType(npc, NpcSkillType.W_SELF_RANGE_DEBUFF);
				final L2Skill wLongRangeDDMagic = getNpcSkillByType(npc, NpcSkillType.W_LONG_RANGE_DD_MAGIC);
				final L2Skill wSelfRangeDDMagic = getNpcSkillByType(npc, NpcSkillType.W_SELF_RANGE_DD_MAGIC);
				
				if (Rnd.get(100) < 33)
				{
					if (npc.getCast().meetsHpMpConditions(npc, wSelfRangeDebuff))
						npc.getAI().addCastDesire(npc, wSelfRangeDebuff, 1000000, false);
					else
					{
						npc._i_ai0 = 1;
						npc.getAI().addAttackDesire(attacker, 1000);
					}
				}
				if (Rnd.get(100) < 33 && npc.distance2D(attacker) > 100)
				{
					if (npc.getCast().meetsHpMpConditions(attacker, wLongRangeDDMagic))
						npc.getAI().addCastDesire(attacker, wLongRangeDDMagic, 1000000, false);
					else
					{
						npc._i_ai0 = 1;
						npc.getAI().addAttackDesire(attacker, 1000);
					}
				}
				else if (npc.getCast().meetsHpMpConditions(npc, wSelfRangeDDMagic))
					npc.getAI().addCastDesire(npc, wSelfRangeDDMagic, 1000000, false);
				else
				{
					npc._i_ai0 = 1;
					npc.getAI().addAttackDesire(attacker, 1000);
				}
			}
		}
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		if (Rnd.get(100) < 33)
		{
			final L2Skill wRangeHeal = getNpcSkillByType(called, NpcSkillType.W_RANGE_DEBUFF);
			
			if (called.getCast().meetsHpMpConditions(attacker, wRangeHeal))
				called.getAI().addCastDesire(attacker, wRangeHeal, 1000000, false);
			else
			{
				called._i_ai0 = 1;
				called.getAI().addAttackDesire(attacker, 1000);
			}
		}
		
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
	
	@Override
	public void onUseSkillFinished(Npc npc, Creature creature, L2Skill skill, boolean success)
	{
		final Creature mostHated = npc.getAI().getHateList().getMostHatedCreature();
		if (mostHated != null && npc._i_ai0 != 1)
		{
			final L2Skill wSelfRangeDDMagic = getNpcSkillByType(npc, NpcSkillType.W_SELF_RANGE_DD_MAGIC);
			final L2Skill wLongRangeDDMagic = getNpcSkillByType(npc, NpcSkillType.W_LONG_RANGE_DD_MAGIC);
			
			if (Rnd.get(100) < 33 && npc.distance2D(mostHated) > 100)
			{
				if (npc.getCast().meetsHpMpConditions(npc, wLongRangeDDMagic))
					npc.getAI().addCastDesire(mostHated, wLongRangeDDMagic, 1000000, false);
			}
			else if (npc.getCast().meetsHpMpConditions(npc, wSelfRangeDDMagic))
				npc.getAI().addCastDesire(npc, wSelfRangeDDMagic, 1000000, false);
			else
			{
				npc._i_ai0 = 1;
				npc.getAI().addAttackDesire(mostHated, 1000);
			}
		}
	}
}
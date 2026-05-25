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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WizardBase.PartyLeaderWizard;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.skills.L2Skill;

public class PartyLeaderWizardRangeCurseAggressive extends PartyLeaderWizardDD2
{
	public PartyLeaderWizardRangeCurseAggressive()
	{
		super("ai/individual/Monster/WizardBase/PartyLeaderWizard");
	}
	
	public PartyLeaderWizardRangeCurseAggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21312
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai1 = 1;
		npc._i_ai3 = 0;
		npc._i_ai4 = 0;
		npc._c_ai2 = npc;
		
		super.onCreated(npc);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (!(creature instanceof Playable))
			return;
		
		tryToAttack(npc, creature);
		
		super.onSeeCreature(npc, creature);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (npc._i_ai1 == 1)
		{
			final L2Skill rangeBuff = getNpcSkillByType(npc, NpcSkillType.RANGE_BUFF);
			npc.getAI().addCastDesire(npc, rangeBuff, 1000000);
			
			npc._i_ai1 = 0;
		}
		
		if (npc._i_ai4 == 0)
		{
			if (npc._i_ai3 == 0)
			{
				npc._i_ai3 = 1;
				startQuestTimer("5001", npc, null, 5000);
			}
			else if (npc._i_ai3 == 1)
				npc._i_ai4 = 1;
		}
		
		final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
		if (topDesireTarget != null && topDesireTarget != attacker && Rnd.get(100) < 10)
		{
			final L2Skill rangeDD = getNpcSkillByType(npc, NpcSkillType.RANGE_DD);
			if (npc.getCast().meetsHpMpConditions(attacker, rangeDD))
				npc.getAI().addCastDesire(attacker, rangeDD, 1000000);
			else
			{
				npc._i_ai0 = 1;
				npc.getAI().addAttackDesire(attacker, 1000);
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		if (npc._c_ai2 != null && npc._c_ai2 != npc)
		{
			final L2Skill debuffCancel = getNpcSkillByType(npc, NpcSkillType.DEBUFF_CANCEL);
			debuffCancel.getEffects(npc._c_ai2, npc._c_ai2);
		}
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("5001"))
		{
			if (npc._i_ai3 == 1)
			{
				if (npc._i_ai4 == 1)
				{
					startQuestTimer("5001", npc, null, 5000);
					
					npc._i_ai4 = 0;
				}
				else if (npc._i_ai4 == 0)
				{
					final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
					if (topDesireTarget != null)
					{
						final L2Skill debuff = getNpcSkillByType(npc, NpcSkillType.DEBUFF);
						npc.getAI().addCastDesire(topDesireTarget, debuff, 9999999);
						
						npc._c_ai2 = topDesireTarget;
					}
				}
			}
		}
		return super.onTimer(name, npc, player);
	}
}
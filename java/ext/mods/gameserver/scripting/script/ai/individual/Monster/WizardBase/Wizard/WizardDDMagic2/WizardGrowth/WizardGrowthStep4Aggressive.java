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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WizardBase.Wizard.WizardDDMagic2.WizardGrowth;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.script.ai.individual.Monster.WizardBase.Wizard.WizardDDMagic2.WizardDDMagic2Aggressive;
import ext.mods.gameserver.skills.L2Skill;

public class WizardGrowthStep4Aggressive extends WizardDDMagic2Aggressive
{
	public WizardGrowthStep4Aggressive()
	{
		super("ai/individual/Monster/WizardBase/Wizard/WizardDDMagic2/WizardGrowth");
	}
	
	public WizardGrowthStep4Aggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21469,
		21488,
		21507,
		21825,
		21827,
		21829
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai1 = 0;
		npc._i_ai2 = 0;
		npc._i_ai4 = 1;
		npc._c_ai0 = (Creature) World.getInstance().getObject(npc._param1);
		npc._i_ai3 = npc._param2;
		if (npc._c_ai0 != null)
			npc.getAI().addAttackDesire(npc._c_ai0, 100);
		
		npc._i_ai2 = 0;
		
		broadcastScriptEvent(npc, 10018, npc.getObjectId(), 700);
		if (Rnd.get(100) < 33)
			npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.SELF_BUFF), 1000000);
		
		if (npc._c_ai0 != null)
		{
			final L2Skill wLongRangeDDMagic = getNpcSkillByType(npc, NpcSkillType.W_LONG_RANGE_DD_MAGIC);
			final L2Skill wShortRangeDDMagic = getNpcSkillByType(npc, NpcSkillType.W_SHORT_RANGE_DD_MAGIC);
			
			if (npc.distance2D(npc._c_ai0) > 100)
			{
				if (npc.getCast().meetsHpMpConditions(npc, wLongRangeDDMagic))
					npc.getAI().addCastDesire(npc._c_ai0, wLongRangeDDMagic, 1000000);
				else
				{
					npc._i_ai0 = 1;
					npc.getAI().addAttackDesire(npc._c_ai0, 1000);
				}
			}
			else if (npc.getCast().meetsHpMpConditions(npc, wShortRangeDDMagic))
				npc.getAI().addCastDesire(npc._c_ai0, wShortRangeDDMagic, 1000000);
			else
			{
				npc._i_ai0 = 1;
				npc.getAI().addAttackDesire(npc._c_ai0, 1000);
			}
		}
		
		super.onCreated(npc);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		super.onAttacked(npc, attacker, damage, skill);
		
		final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
		
		if (npc._i_ai1 == 1 && topDesireTarget == attacker)
			npc._i_ai2 = 1;
		else
		{
			startQuestTimer("2001", npc, null, 5000);
			npc._i_ai1 = 1;
		}
		
		if (attacker instanceof Playable)
		{
			if (npc._i_ai0 == 0)
			{
				final Creature mostHated = npc.getAI().getHateList().getMostHatedCreature();
				
				if (mostHated != null && mostHated != attacker)
				{
					final L2Skill selfRangeDDMagic = getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_DD_MAGIC);
					if (npc.getCast().meetsHpMpConditions(npc, selfRangeDDMagic))
						npc.getAI().addCastDesire(attacker, selfRangeDDMagic, 1000000);
					else
					{
						npc._i_ai0 = 1;
						npc.getAI().addAttackDesire(attacker, 1000);
					}
				}
			}
		}
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("2001"))
		{
			if (npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK && npc.getAI().getCurrentIntention().getType() != IntentionType.CAST)
			{
				npc._i_ai1 = 0;
				npc._i_ai2 = 0;
			}
			else if (npc._i_ai2 == 0)
			{
				final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
				if (topDesireTarget != null && Rnd.get(100) < 50)
				{
					final L2Skill holdMagic = getNpcSkillByType(npc, NpcSkillType.HOLD_MAGIC);
					if (getAbnormalLevel(topDesireTarget, holdMagic) <= 0)
						npc.getAI().addCastDesire(topDesireTarget, holdMagic, 1000000);
				}
			}
			
			startQuestTimer("2001", npc, null, 5000);
			npc._i_ai2 = 0;
		}
		
		return super.onTimer(name, npc, player);
	}
}
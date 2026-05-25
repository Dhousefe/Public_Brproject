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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WizardBase.Wizard.WizardDDMagic2;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.enums.actors.ClassId;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.skills.L2Skill;

public class WizardHallDDMagic2Aggressive extends WizardDDMagic2Aggressive
{
	public WizardHallDDMagic2Aggressive()
	{
		super("ai/individual/Monster/WizardBase/Wizard/WizardDDMagic2");
	}
	
	public WizardHallDDMagic2Aggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		18330,
		18338
	};
	
	@Override
	public void onCreated(Npc npc)
	{
	}
	
	@Override
	public void onNoDesire(Npc npc)
	{
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (npc.distance2D(attacker) > 100)
		{
			final L2Skill wLongRangeDDMagic = getNpcSkillByType(npc, NpcSkillType.W_LONG_RANGE_DD_MAGIC);
			if (npc.getCast().meetsHpMpConditions(attacker, wLongRangeDDMagic))
				npc.getAI().addCastDesire(attacker, wLongRangeDDMagic, 1000000);
			else
			{
				npc._i_ai0 = 1;
				
				npc.getAI().addAttackDesire(attacker, 1000);
			}
		}
		else
		{
			final L2Skill wShortRangeDDMagic = getNpcSkillByType(npc, NpcSkillType.W_SHORT_RANGE_DD_MAGIC);
			if (npc.getCast().meetsHpMpConditions(attacker, wShortRangeDDMagic))
				npc.getAI().addCastDesire(attacker, wShortRangeDDMagic, 1000000);
			else
			{
				npc._i_ai0 = 1;
				
				npc.getAI().addAttackDesire(attacker, 1000);
			}
		}
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		if (Rnd.get(100) < 50 && ClassId.isInGroup(caster, "@cleric_group"))
		{
			npc.removeAllDesire();
			
			if (npc.distance2D(caster) > 100)
			{
				final L2Skill wLongRangeDDMagic = getNpcSkillByType(npc, NpcSkillType.W_LONG_RANGE_DD_MAGIC);
				if (npc.getCast().meetsHpMpConditions(caster, wLongRangeDDMagic))
					npc.getAI().addCastDesire(caster, wLongRangeDDMagic, 1000000);
				else
				{
					npc._i_ai0 = 1;
					
					npc.getAI().addAttackDesire(caster, 1000);
				}
			}
			else
			{
				final L2Skill wShortRangeDDMagic = getNpcSkillByType(npc, NpcSkillType.W_SHORT_RANGE_DD_MAGIC);
				if (npc.getCast().meetsHpMpConditions(caster, wShortRangeDDMagic))
					npc.getAI().addCastDesire(caster, wShortRangeDDMagic, 1000000);
				else
				{
					npc._i_ai0 = 1;
					
					npc.getAI().addAttackDesire(caster, 1000);
				}
			}
		}
	}
	
	@Override
	public void onScriptEvent(Npc npc, int eventId, int arg1, int arg2)
	{
		if (arg1 == 10033)
		{
			final Creature c0 = (Creature) World.getInstance().getObject(arg2);
			if (c0 != null)
			{
				npc.removeAllDesire();
				
				if (npc.distance2D(c0) > 100)
				{
					final L2Skill wLongRangeDDMagic = getNpcSkillByType(npc, NpcSkillType.W_LONG_RANGE_DD_MAGIC);
					if (npc.getCast().meetsHpMpConditions(c0, wLongRangeDDMagic))
						npc.getAI().addCastDesire(c0, wLongRangeDDMagic, 1000000);
					else
					{
						npc._i_ai0 = 1;
						
						npc.getAI().addAttackDesire(c0, 1000);
					}
				}
				else
				{
					final L2Skill wShortRangeDDMagic = getNpcSkillByType(npc, NpcSkillType.W_SHORT_RANGE_DD_MAGIC);
					if (npc.getCast().meetsHpMpConditions(c0, wShortRangeDDMagic))
						npc.getAI().addCastDesire(c0, wShortRangeDDMagic, 1000000);
					else
					{
						npc._i_ai0 = 1;
						
						npc.getAI().addAttackDesire(c0, 1000);
					}
				}
			}
		}
		else if (arg1 == 10042)
		{
			final Creature c0 = (Creature) World.getInstance().getObject(arg2);
			if (c0 != null)
				npc.getAI().addMoveToDesire(c0.getPosition(), 10000000);
		}
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (!(creature instanceof Playable))
			return;
		
		npc.getAI().getDesires().removeIf(d -> d.getType() == IntentionType.MOVE_TO);
		
		if (npc.distance2D(creature) > 100)
		{
			final L2Skill wLongRangeDDMagic = getNpcSkillByType(npc, NpcSkillType.W_LONG_RANGE_DD_MAGIC);
			if (npc.getCast().meetsHpMpConditions(creature, wLongRangeDDMagic))
				npc.getAI().addCastDesire(creature, wLongRangeDDMagic, 1000000);
			else
			{
				npc._i_ai0 = 1;
				
				npc.getAI().addAttackDesire(creature, 1000);
			}
		}
		else
		{
			final L2Skill wShortRangeDDMagic = getNpcSkillByType(npc, NpcSkillType.W_SHORT_RANGE_DD_MAGIC);
			if (npc.getCast().meetsHpMpConditions(creature, wShortRangeDDMagic))
				npc.getAI().addCastDesire(creature, wShortRangeDDMagic, 1000000);
			else
			{
				npc._i_ai0 = 1;
				
				npc.getAI().addAttackDesire(creature, 1000);
			}
		}
		
		npc.getAI().getHateList().addHateInfo(creature, 200);
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		if (called.distance2D(attacker) > 100)
		{
			final L2Skill wLongRangeDDMagic = getNpcSkillByType(called, NpcSkillType.W_LONG_RANGE_DD_MAGIC);
			if (called.getCast().meetsHpMpConditions(attacker, wLongRangeDDMagic))
				called.getAI().addCastDesire(attacker, wLongRangeDDMagic, 1000000);
			else
			{
				called._i_ai0 = 1;
				
				called.getAI().addAttackDesire(attacker, 1000);
			}
		}
		else
		{
			final L2Skill wShortRangeDDMagic = getNpcSkillByType(called, NpcSkillType.W_SHORT_RANGE_DD_MAGIC);
			if (called.getCast().meetsHpMpConditions(attacker, wShortRangeDDMagic))
				called.getAI().addCastDesire(attacker, wShortRangeDDMagic, 1000000);
			else
			{
				called._i_ai0 = 1;
				
				called.getAI().addAttackDesire(attacker, 1000);
			}
		}
	}
}
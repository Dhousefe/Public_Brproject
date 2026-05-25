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

import ext.mods.Config;
import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.container.attackable.HateList;
import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.skills.L2Skill;

public class PartyLeaderWizardSaintAggressive extends PartyLeaderWizard
{
	public PartyLeaderWizardSaintAggressive()
	{
		super("ai/individual/Monster/WizardBase/PartyLeaderWizard");
	}
	
	public PartyLeaderWizardSaintAggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21544,
		21541
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		startQuestTimer("1005", npc, null, 5000);
		
		super.onCreated(npc);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("1005"))
		{
			if (npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK || Rnd.get(100) < 33)
			{
				final L2Skill selfRangeBuff = getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_BUFF);
				npc.getAI().addCastDesire(npc, selfRangeBuff, 1000000);
			}
			
			startQuestTimer("1005", npc, null, 120000);
		}
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (!(creature instanceof Playable))
		{
			super.onSeeCreature(npc, creature);
			return;
		}
		
		final HateList hateList = npc.getAI().getHateList();
		
		if (hateList.isEmpty() && npc.getAI().getLifeTime() > 7 && npc.isInMyTerritory())
		{
			if (Rnd.get(100) < 33 && creature.distance2D(creature) < 40)
			{
				final L2Skill rangeDebuff = getNpcSkillByType(npc, NpcSkillType.RANGE_DEBUFF);
				if (npc.getCast().meetsHpMpConditions(creature, rangeDebuff))
					npc.getAI().addCastDesire(creature, rangeDebuff, 1000000);
				else
				{
					npc._i_ai0 = 1;
					
					npc.getAI().addAttackDesire(creature, 1000);
				}
			}
			else
			{
				final L2Skill wLongRangeDDMagic1 = getNpcSkillByType(npc, NpcSkillType.W_LONG_RANGE_DD_MAGIC1);
				if (npc.getCast().meetsHpMpConditions(creature, wLongRangeDDMagic1))
					npc.getAI().addCastDesire(creature, wLongRangeDDMagic1, 1000000, false);
				else
				{
					npc._i_ai0 = 1;
					
					npc.getAI().addAttackDesire(creature, 1000);
				}
			}
			
			hateList.addDefaultHateInfo(creature);
			
			super.onSeeCreature(npc, creature);
		}
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		final HateList hateList = npc.getAI().getHateList();
		
		if (attacker instanceof Playable)
		{
			double f0 = getHateRatio(npc, attacker);
			f0 = (((1.0 * damage) / (npc.getStatus().getLevel() + 7)) + ((f0 / 100) * ((1.0 * damage) / (npc.getStatus().getLevel() + 7))));
			
			if (hateList.isEmpty())
				hateList.addHateInfo(attacker, (f0 * 100) + 300);
			else
				hateList.addHateInfo(attacker, f0 * 100);
		}
		
		if (attacker instanceof Playable)
		{
			if (npc._i_ai0 == 0)
			{
				final Creature mostHated = hateList.getMostHatedCreature();
				if (mostHated != null)
				{
					if (Rnd.get(100) < 80)
					{
						final L2Skill wLongRangeDDMagic1 = getNpcSkillByType(npc, NpcSkillType.W_LONG_RANGE_DD_MAGIC1);
						if (npc.getCast().meetsHpMpConditions(attacker, wLongRangeDDMagic1))
							npc.getAI().addCastDesire(attacker, wLongRangeDDMagic1, 1000000, false);
						else
						{
							npc._i_ai0 = 1;
							
							npc.getAI().addAttackDesire(attacker, 1000);
						}
					}
					else
					{
						final Party party0 = attacker.getParty();
						final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
						
						if (party0 != null || topDesireTarget != attacker)
						{
							if (Rnd.get(100) < 33)
							{
								final L2Skill wLongRangeDDMagic2 = getNpcSkillByType(npc, NpcSkillType.W_LONG_RANGE_DD_MAGIC2);
								if (npc.getCast().meetsHpMpConditions(attacker, wLongRangeDDMagic2))
									npc.getAI().addCastDesire(attacker, wLongRangeDDMagic2, 1000000, false);
								else
								{
									npc._i_ai0 = 1;
									
									npc.getAI().addAttackDesire(attacker, 1000);
								}
							}
							else
							{
								final L2Skill rangeDebuff = getNpcSkillByType(npc, NpcSkillType.RANGE_DEBUFF);
								if (npc.getCast().meetsHpMpConditions(attacker, rangeDebuff))
									npc.getAI().addCastDesire(attacker, rangeDebuff, 1000000);
								else
								{
									npc._i_ai0 = 1;
									
									npc.getAI().addAttackDesire(attacker, 1000);
								}
							}
						}
					}
				}
				else if (Rnd.get(100) < 10)
				{
					final L2Skill wLongRangeDDMagic1 = getNpcSkillByType(npc, NpcSkillType.W_LONG_RANGE_DD_MAGIC1);
					if (npc.getCast().meetsHpMpConditions(attacker, wLongRangeDDMagic1))
						npc.getAI().addCastDesire(attacker, wLongRangeDDMagic1, 1000000, false);
					else
					{
						npc._i_ai0 = 1;
						
						npc.getAI().addAttackDesire(attacker, 1000);
					}
				}
			}
			else
			{
				double f0 = getHateRatio(npc, attacker);
				f0 = (((1.0 * damage) / (npc.getStatus().getLevel() + 7)) + ((f0 / 100) * ((1.0 * damage) / (npc.getStatus().getLevel() + 7))));
				
				npc.getAI().addAttackDesire(attacker, f0 * 100);
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onPartyAttacked(Npc caller, Npc called, Creature target, int damage)
	{
		if (Rnd.get(100) < 80)
		{
			final L2Skill wLongRangeDDMagic2 = getNpcSkillByType(called, NpcSkillType.W_LONG_RANGE_DD_MAGIC2);
			called.getAI().addCastDesire(target, wLongRangeDDMagic2, 1000000, false);
		}
		else
		{
			final L2Skill rangeDebuff = getNpcSkillByType(called, NpcSkillType.RANGE_DEBUFF);
			called.getAI().addCastDesire(target, rangeDebuff, 1000000);
		}
		super.onPartyAttacked(caller, called, target, damage);
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		final HateList hateList = called.getAI().getHateList();
		hateList.refresh();
		
		if (attacker instanceof Playable && called.getAI().getLifeTime() > 7 && hateList.isEmpty())
		{
			final L2Skill wLongRangeDDMagic1 = getNpcSkillByType(called, NpcSkillType.W_LONG_RANGE_DD_MAGIC1);
			if (called.getCast().meetsHpMpConditions(attacker, wLongRangeDDMagic1))
				called.getAI().addCastDesire(attacker, wLongRangeDDMagic1, 1000000, false);
			else
			{
				called._i_ai0 = 1;
				
				called.getAI().addAttackDesire(attacker, 1000);
			}
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
	
	@Override
	public void onMoveToFinished(Npc npc, int x, int y, int z)
	{
		final Creature mostHated = npc.getAI().getHateList().getMostHatedCreature();
		if (mostHated != null)
		{
			final L2Skill wLongRangeDDMagic1 = getNpcSkillByType(npc, NpcSkillType.W_LONG_RANGE_DD_MAGIC1);
			if (npc.getCast().meetsHpMpConditions(mostHated, wLongRangeDDMagic1))
				npc.getAI().addCastDesire(mostHated, wLongRangeDDMagic1, 1000000, false);
			else
			{
				npc._i_ai0 = 1;
				
				npc.getAI().addAttackDesire(mostHated, 1000);
			}
		}
	}
	
	@Override
	public void onUseSkillFinished(Npc npc, Creature creature, L2Skill skill, boolean success)
	{
		final Creature mostHated = npc.getAI().getHateList().getMostHatedCreature();
		if (mostHated != null)
		{
			if (npc.distance2D(mostHated) < 100)
				npc.getAI().addFleeDesire(mostHated, Config.MAX_DRIFT_RANGE, 1000000);
			else
			{
				final L2Skill wLongRangeDDMagic1 = getNpcSkillByType(npc, NpcSkillType.W_LONG_RANGE_DD_MAGIC1);
				if (npc.getCast().meetsHpMpConditions(mostHated, wLongRangeDDMagic1))
					npc.getAI().addCastDesire(mostHated, wLongRangeDDMagic1, 1000000, false);
				else
				{
					npc._i_ai0 = 1;
					
					npc.getAI().addAttackDesire(mostHated, 1000);
				}
			}
		}
	}
}
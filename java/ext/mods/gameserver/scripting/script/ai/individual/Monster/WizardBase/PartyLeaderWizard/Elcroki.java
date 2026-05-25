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
import ext.mods.gameserver.model.actor.container.attackable.HateList;
import ext.mods.gameserver.skills.L2Skill;

public class Elcroki extends PartyLeaderWizard
{
	public Elcroki()
	{
		super("ai/individual/Monster/WizardBase/PartyLeaderWizard");
	}
	
	public Elcroki(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		22214
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_quest0 = 0;
		
		npc._i_ai3 = 0;
		npc._i_ai4 = 0;
		
		super.onCreated(npc);
	}
	
	@Override
	public void onNoDesire(Npc npc)
	{
		if (!npc.isInCombat())
		{
			if ((npc.getStatus().getHpRatio() * 100) >= getNpcIntAIParamOrDefault(npc, "HpChkRate2", 80))
			{
				if (npc._i_ai3 > 0)
				{
					broadcastScriptEvent(npc, 11039, 0, getNpcIntAIParamOrDefault(npc, "BroadCastRange", 300));
					
					npc._i_ai3 = 0;
					npc._i_ai4 = 0;
				}
				npc._i_quest0 = 0;
			}
			npc.getAI().addMoveToDesire(npc.getSpawnLocation(), 30);
		}
		super.onNoDesire(npc);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		final double hpRatio = npc.getStatus().getHpRatio();
		
		if ((hpRatio * 100) <= getNpcIntAIParamOrDefault(npc, "HpChkRate3", 60))
		{
			if (npc._i_quest0 == 0)
				npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_BUFF1), 1000000);
		}
		
		if ((hpRatio * 100) <= getNpcIntAIParamOrDefault(npc, "HpChkRate5", 30) && Rnd.get(100) < getNpcIntAIParamOrDefault(npc, "ProbSelfRangeDeBuff1", 30))
			npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_DEBUFF1), 1000000);
		
		if (Rnd.get(100) <= getNpcIntAIParamOrDefault(npc, "ProbCond4", 30))
			broadcastScriptEvent(npc, 10002, npc.getObjectId(), getNpcIntAIParamOrDefault(npc, "BroadCastRange", 300));
		
		super.onAttacked(npc, attacker, damage, skill);
		
		if (attacker instanceof Playable)
		{
			if (npc._i_ai0 == 0)
			{
				final HateList h0 = npc.getAI().getHateList();
				final int i0 = !h0.isEmpty() ? 1 : 0;
				
				if (npc.distance3D(attacker) > getNpcIntAIParamOrDefault(npc, "LongRangeSkillDist", 100))
				{
					if (i0 == 1 || Rnd.get(100) < 2)
					{
						if (npc.getCast().meetsHpMpConditions(attacker, getNpcSkillByType(npc, NpcSkillType.LONG_RANGE_DD_MAGIC1)))
							npc.getAI().addCastDesire(attacker, getNpcSkillByType(npc, NpcSkillType.LONG_RANGE_DD_MAGIC1), 1000000);
						else
						{
							npc._i_ai0 = 1;
							
							npc.getAI().addAttackDesire(attacker, 1000);
						}
					}
				}
				else if (i0 == 1 || Rnd.get(100) < 2)
					npc.getAI().addCastDesire(attacker, getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_DD_MAGIC1), 1000000);
				else if (Rnd.get(100) < 2)
				{
					if (npc.getCast().meetsHpMpConditions(attacker, getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_DD_MAGIC1)))
						npc.getAI().addCastDesire(attacker, getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_DD_MAGIC1), 1000000);
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
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		final HateList hateList = called.getAI().getHateList();
		hateList.refresh();
		
		if (attacker instanceof Playable && called.getAI().getLifeTime() > 7 && hateList.isEmpty())
		{
			if (called.distance3D(attacker) > getNpcIntAIParamOrDefault(called, "LongRangeSkillDist", 100))
			{
				if (called.getCast().meetsHpMpConditions(attacker, getNpcSkillByType(called, NpcSkillType.LONG_RANGE_DD_MAGIC1)))
					called.getAI().addCastDesire(attacker, getNpcSkillByType(called, NpcSkillType.LONG_RANGE_DD_MAGIC1), 1000000);
				else
				{
					called._i_ai0 = 1;
					
					called.getAI().addAttackDesire(attacker, 1000);
				}
			}
			else
				called.getAI().addCastDesire(attacker, getNpcSkillByType(called, NpcSkillType.SELF_RANGE_DD_MAGIC1), 1000000);
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
	
	@Override
	public void onPartyAttacked(Npc caller, Npc called, Creature target, int damage)
	{
		final HateList hateList = called.getAI().getHateList();
		hateList.refresh();
		
		if (target instanceof Playable && hateList.isEmpty())
		{
			if (called.distance3D(target) > getNpcIntAIParamOrDefault(called, "LongRangeSkillDist", 100))
			{
				if (called.getCast().meetsHpMpConditions(target, getNpcSkillByType(called, NpcSkillType.LONG_RANGE_DD_MAGIC1)))
					called.getAI().addCastDesire(target, getNpcSkillByType(called, NpcSkillType.LONG_RANGE_DD_MAGIC1), 1000000);
				else
				{
					called._i_ai0 = 1;
					
					called.getAI().addAttackDesire(target, 1000);
				}
			}
			else
				called.getAI().addCastDesire(target, getNpcSkillByType(called, NpcSkillType.SELF_RANGE_DD_MAGIC1), 1000000);
		}
		super.onPartyAttacked(caller, called, target, damage);
	}
	
	@Override
	public void onUseSkillFinished(Npc npc, Creature creature, L2Skill skill, boolean success)
	{
		if (skill == getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_BUFF1) && success)
			npc._i_quest0 = 1;
		
		final Creature topHateInfoCreature = npc.getAI().getHateList().getMostHatedCreature();
		if (topHateInfoCreature != null)
		{
			if (npc._i_ai0 != 1)
			{
				if (npc.distance3D(topHateInfoCreature) > getNpcIntAIParamOrDefault(npc, "LongRangeSkillDist", 100))
				{
					if (npc.getCast().meetsHpMpConditions(topHateInfoCreature, getNpcSkillByType(npc, NpcSkillType.LONG_RANGE_DD_MAGIC1)))
						npc.getAI().addCastDesire(topHateInfoCreature, getNpcSkillByType(npc, NpcSkillType.LONG_RANGE_DD_MAGIC1), 1000000);
					else
					{
						npc._i_ai0 = 1;
						
						npc.getAI().addAttackDesire(topHateInfoCreature, 1000);
					}
				}
				npc.getAI().addCastDesire(topHateInfoCreature, getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_DD_MAGIC1), 1000000);
			}
		}
	}
	
	@Override
	public void onPartyDied(Npc caller, Npc called)
	{
		if (caller != called)
		{
			if (called._i_ai3 > 0)
				called._i_ai3--;
			
			final int maxConcurrentPrivates = getNpcIntAIParamOrDefault(called, "max_concurrent_privates", 2);
			if (called.isInCombat() && called._i_ai3 < 2 && called._i_ai4 < getNpcIntAIParamOrDefault(called, "max_spawn_privates", 5) && called.getMinions().size() < maxConcurrentPrivates)
			{
				final Creature topDesireTarget = called.getAI().getTopDesireTarget();
				final int silhouette1 = getNpcIntAIParamOrDefault(called, "silhouette1", 0);
				
				if (Rnd.get(2) == 0 && called.getMinions().size() < maxConcurrentPrivates)
				{
					if (topDesireTarget != null)
						createOnePrivateEx(called, silhouette1, called.getX() + Rnd.get(100), called.getY() + Rnd.get(100), called.getZ(), 0, 0, false, 1000, topDesireTarget.getObjectId(), 0);
					else
						createOnePrivateEx(called, silhouette1, called.getX() + Rnd.get(100), called.getY() + Rnd.get(100), called.getZ(), 0, 0, false, 0, 0, 0);
					
					called._i_ai3++;
					called._i_ai4++;
				}
				
				if (called.getMinions().size() < maxConcurrentPrivates)
				{
					if (topDesireTarget != null)
						createOnePrivateEx(called, silhouette1, called.getX() + Rnd.get(100), called.getY() + Rnd.get(100), called.getZ(), 0, 0, false, 1000, topDesireTarget.getObjectId(), 0);
					else
						createOnePrivateEx(called, silhouette1, called.getX() + Rnd.get(100), called.getY() + Rnd.get(100), called.getZ(), 0, 0, false, 0, 0, 0);
					
					called._i_ai3++;
					called._i_ai4++;
				}
			}
		}
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (creature instanceof Player && getNpcIntAIParam(npc, "ag_type") == 1 && npc.isInMyTerritory())
		{
			final HateList hateList = npc.getAI().getHateList();
			if (hateList.isEmpty())
			{
				final int maxConcurrentPrivates = getNpcIntAIParamOrDefault(npc, "max_concurrent_privates", 2);
				final int silhouette1 = getNpcIntAIParam(npc, "silhouette1");
				final int silhouette2 = getNpcIntAIParam(npc, "silhouette2");
				final int maxSpawnPrivates = getNpcIntAIParamOrDefault(npc, "max_spawn_privates", 5);
				
				if (npc.getMinions().size() < maxConcurrentPrivates)
				{
					if (Rnd.get(2) == 0 && npc._i_ai3 < 2 && npc._i_ai4 < maxSpawnPrivates)
					{
						createOnePrivateEx(npc, silhouette1, npc.getX() + Rnd.get(100), npc.getY() + Rnd.get(100), npc.getZ(), npc.getHeading(), 0, false, 1000, creature.getObjectId(), 0);
						
						npc._i_ai3++;
						npc._i_ai4++;
					}
					else if (npc._i_ai3 < 2 && npc._i_ai4 < maxSpawnPrivates)
					{
						createOnePrivateEx(npc, silhouette2, npc.getX() + Rnd.get(100), npc.getY() + Rnd.get(100), npc.getZ(), npc.getHeading(), 0, false, 1000, creature.getObjectId(), 0);
						
						npc._i_ai3++;
						npc._i_ai4++;
					}
				}
				
				if (npc.getMinions().size() < maxConcurrentPrivates)
				{
					if (Rnd.get(2) == 0 && npc._i_ai3 < 2 && npc._i_ai4 < maxSpawnPrivates)
					{
						createOnePrivateEx(npc, silhouette1, npc.getX() + Rnd.get(100), npc.getY() + Rnd.get(100), npc.getZ(), npc.getHeading(), 0, false, 1000, creature.getObjectId(), 0);
						
						npc._i_ai3++;
						npc._i_ai4++;
					}
					else if (npc._i_ai3 < 2 && npc._i_ai4 < maxSpawnPrivates)
					{
						createOnePrivateEx(npc, silhouette2, npc.getX() + Rnd.get(100), npc.getY() + Rnd.get(100), npc.getZ(), npc.getHeading(), 0, false, 1000, creature.getObjectId(), 0);
						
						npc._i_ai3++;
						npc._i_ai4++;
					}
				}
				
				if (npc.distance3D(creature) > getNpcIntAIParamOrDefault(npc, "LongRangeSkillDist", 100))
				{
					if (npc.getCast().meetsHpMpConditions(creature, getNpcSkillByType(npc, NpcSkillType.LONG_RANGE_DD_MAGIC1)))
						npc.getAI().addCastDesire(creature, getNpcSkillByType(npc, NpcSkillType.LONG_RANGE_DD_MAGIC1), 1000000);
					else
					{
						npc._i_ai0 = 1;
						
						npc.getAI().addAttackDesire(creature, 1000);
					}
				}
				
				npc.getAI().addCastDesire(creature, getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_DD_MAGIC1), 1000000);
				
				hateList.addDefaultHateInfo(creature);
				
				super.onSeeCreature(npc, creature);
			}
		}
	}
}
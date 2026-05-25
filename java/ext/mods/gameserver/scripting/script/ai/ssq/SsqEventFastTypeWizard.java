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
package ext.mods.gameserver.scripting.script.ai.ssq;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.container.attackable.HateList;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.taskmanager.GameTimeTaskManager;

public class SsqEventFastTypeWizard extends SsqEventBasicWizard
{
	public SsqEventFastTypeWizard()
	{
		super("ai/ssq");
	}
	
	public SsqEventFastTypeWizard(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		18013,
		18023,
		18033,
		18043,
		18053,
		18063,
		18073,
		18083,
		18093,
		18103
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		
		npc._i_ai3 = 0;
		npc._i_ai4 = 0;
		
		if ((npc._param3 & 0x10000000) != 0)
		{
			npc._i_ai3 = 1;
			npc._param3 = npc._param3 - 0x10000000;
		}
		
		startQuestTimer("2002", npc, null, 5000);
		super.onCreated(npc);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("2002"))
		{
			npc.lookNeighbor(600);
			startQuestTimer("2002", npc, null, 5000);
		}
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (npc._i_ai4 == 1)
			return;
		
		super.onAttacked(npc, attacker, damage, skill);
		
		if (!npc.getSpawn().isInMyTerritory(attacker))
			return;
		
		final HateList hateList = npc.getAI().getHateList();
		final Creature mostHated = hateList.getMostHatedCreature();
		if (mostHated != null)
		{
			if (attacker instanceof Playable)
			{
				if (npc._i_ai0 == 0)
				{
					int i0 = 0;
					
					final Creature mostHatedHI = npc.getAI().getHateList().getMostHatedCreature();
					if (mostHatedHI != null)
						i0 = 1;
					
					if (npc.distance2D(attacker) > 100 && Rnd.get(100) < 80)
					{
						if (i0 == 1 || Rnd.get(100) < 2)
						{
							final L2Skill wLongRangeDDMagic = getNpcSkillByType(npc, NpcSkillType.W_LONG_RANGE_DD_MAGIC);
							if (npc.getCast().meetsHpMpConditions(attacker, wLongRangeDDMagic))
								npc.getAI().addCastDesire(attacker, wLongRangeDDMagic, 1000000, false);
							else
							{
								npc._i_ai0 = 1;
								
								npc.getAI().addAttackDesire(attacker, 1000);
							}
						}
					}
					else if (i0 == 1 || Rnd.get(100) < 2)
					{
						final L2Skill wShortRangeDDMagic = getNpcSkillByType(npc, NpcSkillType.W_SHORT_RANGE_DD_MAGIC);
						if (npc.getCast().meetsHpMpConditions(attacker, wShortRangeDDMagic))
							npc.getAI().addCastDesire(attacker, wShortRangeDDMagic, 1000000, false);
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
		}
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		if (called._i_ai4 == 1)
			return;
		
		if (!called.getSpawn().isInMyTerritory(attacker))
			return;
		
		final HateList hateList = called.getAI().getHateList();
		hateList.refresh();
		
		if (attacker instanceof Playable && hateList.isEmpty())
		{
			if (called.distance2D(attacker) > 100)
			{
				final L2Skill wLongRangeDDMagic = getNpcSkillByType(called, NpcSkillType.W_LONG_RANGE_DD_MAGIC);
				if (called.getCast().meetsHpMpConditions(attacker, wLongRangeDDMagic))
					called.getAI().addCastDesire(attacker, wLongRangeDDMagic, 1000000, false);
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
					called.getAI().addCastDesire(attacker, wShortRangeDDMagic, 1000000, false);
				else
				{
					called._i_ai0 = 1;
					
					called.getAI().addAttackDesire(attacker, 1000);
				}
			}
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
	
	@Override
	public void onUseSkillFinished(Npc npc, Creature creature, L2Skill skill, boolean success)
	{
		if (npc._i_ai4 == 1)
			return;
		
		final Creature mostHatedHI = npc.getAI().getHateList().getMostHatedCreature();
		if (mostHatedHI != null)
		{
			if (npc._i_ai0 != 1)
			{
				if (npc.distance2D(mostHatedHI) > 100)
				{
					final L2Skill wLongRangeDDMagic = getNpcSkillByType(npc, NpcSkillType.W_LONG_RANGE_DD_MAGIC);
					if (npc.getCast().meetsHpMpConditions(mostHatedHI, wLongRangeDDMagic))
						npc.getAI().addCastDesire(mostHatedHI, wLongRangeDDMagic, 1000000, false);
					else
					{
						npc._i_ai0 = 1;
						
						npc.getAI().addAttackDesire(mostHatedHI, 1000);
					}
				}
				else
				{
					final L2Skill wShortRangeDDMagic = getNpcSkillByType(npc, NpcSkillType.W_SHORT_RANGE_DD_MAGIC);
					if (npc.getCast().meetsHpMpConditions(mostHatedHI, wShortRangeDDMagic))
						npc.getAI().addCastDesire(mostHatedHI, wShortRangeDDMagic, 1000000, false);
					else
					{
						npc._i_ai0 = 1;
						
						npc.getAI().addAttackDesire(mostHatedHI, 1000);
					}
				}
			}
		}
	}
	
	@Override
	public void onScriptEvent(Npc npc, int eventId, int arg1, int arg2)
	{
		final Creature creature = (Creature) World.getInstance().getObject(arg2);
		if (creature != null)
		{
			if (npc.getSpawn().isInMyTerritory(creature))
			{
				if (eventId == 10007)
				{
					if (npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK && npc.getAI().getCurrentIntention().getType() != IntentionType.CAST)
						npc.getAI().addMoveToDesire(new Location(npc._param1, arg1, npc.getZ()), 50);
				}
				
				if (eventId == 10014)
				{
					final Creature creature1 = (Creature) World.getInstance().getObject(arg1);
					
					int i0 = (int) ((1.0 * GameTimeTaskManager.getInstance().getCurrentTick() - npc._i_ai0) / (60 * 17 * 2) * 100);
					if (i0 > Rnd.get(100) && creature1 != null)
					{
						npc.removeAllAttackDesire();
						npc.getAI().getHateList().cleanAllHate();
						
						if (creature1 instanceof Playable)
							npc.getAI().getHateList().addHateInfo(creature1, 200);
					}
				}
				
				if (eventId == 10015)
				{
					npc.removeAllAttackDesire();
					npc._i_ai4 = 1;
				}
			}
		}
		
		super.onScriptEvent(npc, eventId, arg1, arg2);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (!(creature instanceof Playable))
			return;
		
		if (!npc.getSpawn().isInMyTerritory(creature))
			return;
		
		if (npc._i_ai0 == 1)
		{
			if (creature instanceof Playable)
				npc.getAI().addAttackDesire(creature, 200);
		}
		
		final Creature mostHatedHI = npc.getAI().getHateList().getMostHatedCreature();
		final HateList hateList = npc.getAI().getHateList();
		
		if (npc.getSpawn().isInMyTerritory(creature) && hateList != null)
		{
			if (npc.distance2D(creature) > 100)
			{
				final L2Skill wLongRangeDDMagic = getNpcSkillByType(npc, NpcSkillType.W_LONG_RANGE_DD_MAGIC);
				if (npc.getCast().meetsHpMpConditions(mostHatedHI, wLongRangeDDMagic))
					npc.getAI().addCastDesire(mostHatedHI, wLongRangeDDMagic, 1000000, false);
				else
				{
					npc._i_ai0 = 1;
					
					npc.getAI().addAttackDesire(mostHatedHI, 1000);
				}
			}
			else
			{
				final L2Skill wShortRangeDDMagic = getNpcSkillByType(npc, NpcSkillType.W_SHORT_RANGE_DD_MAGIC);
				if (npc.getCast().meetsHpMpConditions(mostHatedHI, wShortRangeDDMagic))
					npc.getAI().addCastDesire(mostHatedHI, wShortRangeDDMagic, 1000000, false);
				else
				{
					npc._i_ai0 = 1;
					
					npc.getAI().addAttackDesire(mostHatedHI, 1000);
				}
			}
			
			hateList.addHateInfo(creature, 300);
		}
		
		super.onSeeCreature(npc, creature);
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		if (npc._i_ai3 == 0)
			broadcastScriptEvent(npc, 10009, npc._param3, 1500);
		else
			broadcastScriptEvent(npc, 10010, npc._param3, 1500);
		
		if (killer instanceof Player player)
		{
			if (npc._i_ai1 == 1)
				ssqEventGiveItem(npc, player, 1);
			else
				ssqEventGiveItem(npc, player, 7);
		}
	}
}
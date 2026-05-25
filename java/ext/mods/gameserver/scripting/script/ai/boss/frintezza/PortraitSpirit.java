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
package ext.mods.gameserver.scripting.script.ai.boss.frintezza;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.script.ai.individual.DefaultNpc;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.taskmanager.GameTimeTaskManager;

public class PortraitSpirit extends DefaultNpc
{
	private static final L2Skill BOMB = SkillTable.getInstance().getInfo(5011, 1);
	private static final L2Skill HOLD = SkillTable.getInstance().getInfo(5012, 1);
	private static final L2Skill VAMPIRIC = SkillTable.getInstance().getInfo(5013, 1);
	
	public PortraitSpirit()
	{
		super("ai/boss/frintezza");
	}
	
	public PortraitSpirit(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		29050
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai4 = 0;
		npc._i_quest1 = 0;
		npc._i_quest2 = 0;
		
		npc.getAI().addWanderDesire(200, 5);
		
		startQuestTimer("5000", npc, null, 10 * 1000L);
		
		super.onCreated(npc);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable && npc._i_ai4 == 0)
		{
			npc.getAI().addCastDesire(attacker, getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL), 1000000);
			npc.getAI().addAttackDesire(attacker, 50);
			
			npc._i_quest1 = GameTimeTaskManager.getInstance().getCurrentTick();
			
			startQuestTimer("2001", npc, null, 5000);
		}
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable && called._i_ai4 == 0 && called.getAI().getLifeTime() > 7)
		{
			called.getAI().addCastDesire(attacker, getNpcSkillByType(called, NpcSkillType.PHYSICAL_SPECIAL), 1000000);
			called.getAI().addAttackDesire(attacker, 50);
		}
	}
	
	@Override
	public void onUseSkillFinished(Npc npc, Creature creature, L2Skill skill, boolean success)
	{
		if (skill == BOMB && success)
		{
			npc.deleteMe();
			return;
		}
		
		if (npc._i_ai4 == 0)
		{
			final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
			if (topDesireTarget instanceof Player)
			{
				if (getAbnormalLevel(topDesireTarget, HOLD) > 0)
				{
					if (npc._i_quest2 < 3)
					{
						npc._i_quest2++;
						
						npc.getAI().addCastDesire(topDesireTarget, VAMPIRIC, 1000000);
					}
				}
				else
					npc.getAI().addCastDesire(topDesireTarget, getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL), 1000000);
				
				npc.getAI().addAttackDesire(topDesireTarget, 50);
			}
		}
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (npc._i_ai4 == 0)
		{
			final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
			
			if (name.equalsIgnoreCase("2001"))
			{
				if (topDesireTarget != null && getElapsedTicks(npc._i_quest1) > 5)
					npc.getAI().addCastDesire(topDesireTarget, HOLD, 1000000);
			}
			else if (name.equalsIgnoreCase("2002"))
			{
				if (topDesireTarget != null)
				{
					if (getAbnormalLevel(topDesireTarget, HOLD) > 0)
					{
						if (npc._i_quest2 < 3)
						{
							npc._i_quest2++;
							
							npc.getAI().addCastDesire(topDesireTarget, VAMPIRIC, 1000000);
						}
						
						startQuestTimer("2002", npc, null, 5000);
					}
					else
						npc.getAI().addCastDesire(topDesireTarget, getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL), 1000000);
					
					npc.getAI().addAttackDesire(topDesireTarget, 50);
				}
			}
			else if (name.equalsIgnoreCase("5000"))
			{
				if (!npc.hasMaster() || npc.getMaster().isDead())
				{
					if (npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK && npc.getAI().getCurrentIntention().getType() != IntentionType.CAST)
						npc.deleteMe();
					else if (topDesireTarget != null && npc.distance2D(topDesireTarget) > 5000)
						npc.deleteMe();
					else
						startQuestTimer("5000", npc, player, 10000);
				}
				else
					startQuestTimer("5000", npc, player, 10000);
			}
		}
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (creature instanceof Playable && npc._i_ai4 == 0 && npc.getAI().getLifeTime() > 7)
		{
			npc.getAI().addCastDesire(creature, getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL), 1000000);
			npc.getAI().addAttackDesire(creature, 50);
		}
	}
	
	@Override
	public void onScriptEvent(Npc npc, int eventId, int arg1, int arg2)
	{
		if (arg1 == 20000)
		{
			npc.removeAllDesire();
			
			npc.getAI().addCastDesire(npc, BOMB, 1000000);
		}
		else if (arg1 == 40000)
		{
			npc.removeAllDesire();
			
			npc.deleteMe();
		}
		else if (arg1 == 50000)
		{
			npc._i_ai4 = 1;
			
			npc.removeAllDesire();
		}
	}
}
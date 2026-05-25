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

import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.container.attackable.HateList;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.scripting.script.ai.individual.Monster.WizardBase.WizardBase;
import ext.mods.gameserver.skills.L2Skill;

public class PartyLeaderWizard extends WizardBase
{
	public PartyLeaderWizard()
	{
		super("ai/individual/Monster/WizardBase/PartyLeaderWizard");
	}
	
	public PartyLeaderWizard(String descr)
	{
		super(descr);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		startQuestTimerAtFixedRate("1002", npc, null, 10000, 10000);
		
		npc._weightPoint = 10;
		npc._i_ai0 = 0;
		
		if (getNpcIntAIParam(npc, "SummonPrivateRate") == 0)
		{
			createPrivates(npc);
			
			npc._i_ai2 = 1;
		}
		else
			npc._i_ai2 = 0;
		
		super.onCreated(npc);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		final HateList hateList = npc.getAI().getHateList();
		
		if (name.equalsIgnoreCase("1002"))
		{
			hateList.refresh();
			hateList.removeIfOutOfRange(1000);
		}
		else if (name.equalsIgnoreCase("1003"))
		{
			if (npc.isMuted())
				startQuestTimer("1003", npc, null, 10000);
			else
			{
				npc.removeAllAttackDesire();
				
				npc._i_ai0 = 0;
				
				final Creature mostHated = hateList.getMostHatedCreature();
				if (mostHated != null)
					onAttacked(npc, mostHated, 100, null);
			}
		}
		return super.onTimer(name, npc, player);
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
		
		if (npc.isMuted())
		{
			npc._i_ai0 = 1;
			
			startQuestTimer("1003", npc, null, 10000);
		}
		
		if (npc._i_ai2 == 0)
		{
			if (Rnd.get(100) < getNpcIntAIParam(npc, "SummonPrivateRate"))
			{
				createPrivates(npc);
				
				switch (Rnd.get(4))
				{
					case 0:
						npc.broadcastNpcSay(NpcStringId.ID_1000294);
						break;
					
					case 1:
						npc.broadcastNpcSay(NpcStringId.ID_1000403);
						break;
					
					case 2:
						npc.broadcastNpcSay(NpcStringId.ID_1000404);
						break;
					
					case 3:
						npc.broadcastNpcSay(NpcStringId.ID_1000405);
						break;
				}
				
				npc._i_ai2 = 1;
			}
		}
		
		if (attacker instanceof Player && getNpcIntAIParam(npc, "ShoutTarget") == 1)
		{
			if (Rnd.get(100) < 50 && attacker.getStatus().getHpRatio() < 0.4)
			{
				final Creature mostHated = hateList.getMostHatedCreature();
				if (mostHated == attacker)
				{
					switch (Rnd.get(3))
					{
						case 0:
							npc.broadcastNpcSay(NpcStringId.ID_1000291, attacker.getName());
							break;
						
						case 1:
							npc.broadcastNpcSay(NpcStringId.ID_1000398, attacker.getName());
							break;
						
						case 2:
							npc.broadcastNpcSay(NpcStringId.ID_1000399, attacker.getName());
							break;
					}
					hateList.clear();
					
					double f0 = getHateRatio(npc, attacker);
					f0 = (((1.0 * damage) / (npc.getStatus().getLevel() + 7)) + ((f0 / 100) * ((1.0 * damage) / (npc.getStatus().getLevel() + 7))));
					
					hateList.addHateInfo(attacker, (f0 * 1000) + 300);
					
					npc._flag = attacker.getObjectId();
					
					broadcastScriptEvent(npc, 10002, npc.getObjectId(), 300);
				}
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onPartyAttacked(Npc caller, Npc called, Creature target, int damage)
	{
		if (target instanceof Playable && caller != called)
		{
			final HateList hateList = called.getAI().getHateList();
			
			double f0 = getHateRatio(called, target);
			f0 = (((1.0 * damage) / (called.getStatus().getLevel() + 7)) + ((f0 / 100) * ((1.0 * damage) / (called.getStatus().getLevel() + 7))));
			
			if (hateList.isEmpty())
				hateList.addHateInfo(target, (((((f0 * damage) * caller._weightPoint) * 10) + 300)));
			else
				hateList.addHateInfo(target, ((((f0 * damage) * caller._weightPoint) * 10)));
		}
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable && called.getAI().getLifeTime() > 7)
		{
			final HateList hateList = called.getAI().getHateList();
			
			double f0 = getHateRatio(called, attacker);
			
			if (hateList.isEmpty())
				hateList.addHateInfo(attacker, (((((f0 * damage) * 30) * 10) + 300)));
			else
				hateList.addHateInfo(attacker, ((((f0 * damage) * 30) * 10)));
		}
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		final int effectPoints = skill.getAggroPoints();
		if (effectPoints > 0 && !skill.isOffensive())
		{
			final HateList hateList = npc.getAI().getHateList();
			
			double f0 = getHateRatio(npc, caster);
			f0 = (((1. * effectPoints) / (npc.getStatus().getLevel() + 7)) + ((f0 / 100) * ((1. * effectPoints) / (npc.getStatus().getLevel() + 7))));
			
			final Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
			if (npc.getAI().getCurrentIntention().getType() == IntentionType.ATTACK && mostHated != null && mostHated == caster)
				hateList.addHateInfo(caster, ((f0 * 150)));
			else
				hateList.addHateInfo(caster, ((f0 * 75)));
		}
	}
	
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		final int shoutMsg4 = getNpcIntAIParam(npc, "ShoutMsg4");
		if (shoutMsg4 > 0 && Rnd.get(100) < 30)
			npc.broadcastNpcShout(NpcStringId.get(shoutMsg4));
	}
	
	@Override
	public void onPartyDied(Npc caller, Npc called)
	{
		if (caller.getSpawn().getRespawnDelay() != 0)
		{
			if (called.isMaster() && !called.isDead())
				caller.scheduleRespawn((caller.getSpawn().getRespawnDelay() * 1000));
		}
	}
}
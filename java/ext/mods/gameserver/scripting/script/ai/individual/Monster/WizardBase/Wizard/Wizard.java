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

import ext.mods.Config;
import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.container.attackable.HateList;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.scripting.script.ai.individual.Monster.WizardBase.WizardBase;
import ext.mods.gameserver.skills.L2Skill;

public class Wizard extends WizardBase
{
	public Wizard()
	{
		super("ai/individual/Monster/WizardBase/Wizard");
	}
	
	public Wizard(String descr)
	{
		super(descr);
	}
	
	@Override
	public void onNoDesire(Npc npc)
	{
		npc.getAI().addWanderDesire(5, 5);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		final int shoutMsg1 = getNpcIntAIParam(npc, "ShoutMsg1");
		if (shoutMsg1 > 0)
			npc.broadcastNpcShout(NpcStringId.get(shoutMsg1));
		
		if (getNpcIntAIParam(npc, "MoveAroundSocial") > 0 || getNpcIntAIParam(npc, "ShoutMsg2") > 0 || getNpcIntAIParam(npc, "ShoutMsg3") > 0)
			startQuestTimerAtFixedRate("1001", npc, null, 10000, 10000);
		
		startQuestTimerAtFixedRate("1002", npc, null, 10000, 10000);
		
		npc._i_ai0 = 0;
		
		super.onCreated(npc);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		final HateList hateList = npc.getAI().getHateList();
		
		if (name.equalsIgnoreCase("1001"))
		{
			switch (npc.getAI().getCurrentIntention().getType())
			{
				case IDLE, WANDER:
					final int moveAroundSocial2 = getNpcIntAIParam(npc, "MoveAroundSocial2");
					final int moveAroundSocial1 = getNpcIntAIParam(npc, "MoveAroundSocial1");
					final int moveAroundSocial = getNpcIntAIParam(npc, "MoveAroundSocial");
					
					if (moveAroundSocial2 > 0 && Rnd.get(100) < Config.MONSTER_ANIMATION)
						npc.getAI().addSocialDesire(3, moveAroundSocial2 * 1000 / 30, 50);
					else if (moveAroundSocial1 > 0 && Rnd.get(100) < Config.MONSTER_ANIMATION)
						npc.getAI().addSocialDesire(2, moveAroundSocial1 * 1000 / 30, 50);
					else if (moveAroundSocial > 0 && Rnd.get(100) < Config.MONSTER_ANIMATION)
						npc.getAI().addSocialDesire(1, moveAroundSocial * 1000 / 30, 50);
					break;
				
				case ATTACK:
					final int shoutMsg3 = getNpcIntAIParam(npc, "ShoutMsg3");
					if (shoutMsg3 > 0 && Rnd.get(100) < 10)
						npc.broadcastNpcShout(NpcStringId.get(shoutMsg3));
					break;
			}
		}
		else if (name.equalsIgnoreCase("1002"))
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
		if (attacker instanceof Playable)
		{
			final HateList hateList = npc.getAI().getHateList();
			
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
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable && called.getAI().getLifeTime() > 7)
		{
			final HateList hateList = called.getAI().getHateList();
			
			double f0 = getHateRatio(called, attacker);
			f0 = (((1.0 * damage) / (called.getStatus().getLevel() + 7)) + ((f0 / 100) * ((1.0 * damage) / (called.getStatus().getLevel() + 7))));
			
			if (hateList.isEmpty())
				hateList.addHateInfo(attacker, (f0 * 30) + 300);
			else
				hateList.addHateInfo(attacker, f0 * 30);
		}
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		if (skill.getAggroPoints() > 0 && !skill.isOffensive())
		{
			final HateList hateList = npc.getAI().getHateList();
			
			double f0 = getHateRatio(npc, caster);
			f0 = (((1. * skill.getAggroPoints()) / (npc.getStatus().getLevel() + 7)) + ((f0 / 100) * ((1. * skill.getAggroPoints()) / (npc.getStatus().getLevel() + 7))));
			
			final Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
			if (npc.getAI().getCurrentIntention().getType() == IntentionType.ATTACK && mostHated != null && mostHated == caster)
				hateList.addHateInfo(caster, f0 * 150);
			else
				hateList.addHateInfo(caster, f0 * 75);
		}
	}
	
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		final int shoutMsg4 = getNpcIntAIParam(npc, "ShoutMsg4");
		if (shoutMsg4 > 0 && Rnd.get(100) < 30)
			npc.broadcastNpcShout(NpcStringId.get(shoutMsg4));
	}
}
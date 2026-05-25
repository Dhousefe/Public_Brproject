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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.MonsterBehavior;

import ext.mods.commons.random.Rnd;

import ext.mods.Config;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.script.ai.individual.Monster.MonsterAI;
import ext.mods.gameserver.skills.L2Skill;

public class MonsterBehavior extends MonsterAI
{
	public MonsterBehavior()
	{
		super("ai/individual/MonsterBehavior");
	}
	
	public MonsterBehavior(String descr)
	{
		super(descr);
	}
	
	@Override
	public void onNoDesire(Npc npc)
	{
		if (getNpcIntAIParamOrDefault(npc, "MovingAttack", 1) == 1)
		{
			if (getNpcIntAIParam(npc, "Party_Type") == 1 && npc.hasMaster() && !npc.getMaster().isDead())
				npc.getAI().addFollowDesire(npc.getMaster(), 5);
			else
				npc.getAI().addWanderDesire(5, 5);
		}
		
		super.onNoDesire(npc);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		if (getNpcIntAIParam(npc, "Party_Type") == 2)
			createPrivates(npc);
		else if (getNpcIntAIParam(npc, "Party_Type") == 1 && getNpcIntAIParamOrDefault(npc, "PrivateFollowBoss", 1) == 1 && npc.hasMaster() && !npc.getMaster().isDead())
		{
			startQuestTimer("1005", npc, null, 120000);
			startQuestTimer("1006", npc, null, 20000);
		}
		
		if (getNpcIntAIParam(npc, "AttackRange") == 1)
			npc._c_ai4 = null;
		
		super.onCreated(npc);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (getNpcIntAIParam(npc, "AttackRange") == 1 && npc.distance2D(attacker) < 100 && npc._i_ai4 == 0)
		{
			npc._i_ai4 = 1;
			npc._c_ai4 = attacker;
			
			startQuestTimer("5001", npc, null, 10000);
		}
		
		if (!npc.isStunned() && !npc.isParalyzed())
		{
			if (getNpcIntAIParam(npc, "SoulShot") != 0 && Rnd.get(100) < getNpcIntAIParam(npc, "SoulShotRate"))
				npc.rechargeShots(true, false);
			
			if (getNpcIntAIParam(npc, "SpiritShot") != 0 && Rnd.get(100) < getNpcIntAIParam(npc, "SpiritShotRate"))
				npc.rechargeShots(false, true);
		}
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("1005"))
		{
			if (getNpcIntAIParam(npc, "Party_Type") == 1 && getNpcIntAIParamOrDefault(npc, "PrivateFollowBoss", 1) == 1)
			{
				if (!npc.hasMaster() || npc.getMaster().isDead())
				{
					if (!npc.isInCombat() && !npc.getCast().isCastingNow())
					{
						npc.deleteMe();
						return null;
					}
				}
				
				if (!npc.isInMyTerritory() && npc.hasMaster() && !npc.getMaster().isDead() && !npc.isInCombat())
				{
					npc.teleportTo(npc.getMaster().getPosition(), 0);
					npc.removeAllAttackDesire();
				}
			}
			
			startQuestTimer("1005", npc, player, 120000);
		}
		else if (name.equalsIgnoreCase("1006"))
		{
			if (getNpcIntAIParam(npc, "Party_Type") == 1 && getNpcIntAIParamOrDefault(npc, "PrivateFollowBoss", 1) == 1)
			{
				if (!npc.hasMaster() || npc.getMaster().isDead())
				{
					if (!npc.isInCombat() && !npc.getCast().isCastingNow())
					{
						npc.deleteMe();
						return null;
					}
				}
			}
		}
		else if (name.equalsIgnoreCase("5001"))
		{
			if (npc._i_ai4 == 1 && npc._c_ai4 != null)
			{
				npc.getAI().addFleeDesire(npc._c_ai4, Config.MAX_DRIFT_RANGE, 10000000);
				npc._i_ai4 = 0;
				npc._c_ai4 = null;
			}
		}
		
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onPartyDied(Npc caller, Npc called)
	{
		final int partyType = getNpcIntAIParam(called, "Party_Type");
		if (partyType == 1)
		{
			if (called.hasMaster() && caller == called.getMaster() && getNpcIntAIParamOrDefault(called, "PrivateFollowBoss", 1) == 1)
				startQuestTimer("1006", called, null, 20000);
		}
		else if (partyType == 2 && called.isMaster() && !called.isDead())
			caller.scheduleRespawn((caller.getSpawn().getRespawnDelay() * 1000));
		
		super.onPartyDied(caller, called);
	}
}
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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.PartyLeaderWarrior;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.Warrior;
import ext.mods.gameserver.skills.L2Skill;

public class PartyLeaderWarrior extends Warrior
{
	private static final NpcStringId[] SUMMON_PRIVATES_SHOUTS =
	{
		NpcStringId.ID_1000294,
		NpcStringId.ID_1000403,
		NpcStringId.ID_1000404,
		NpcStringId.ID_1000405
	};
	
	private static final NpcStringId[] TARGET_SHOUTS =
	{
		NpcStringId.ID_1000291,
		NpcStringId.ID_1000398,
		NpcStringId.ID_1000399
	};
	
	public PartyLeaderWarrior()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/PartyLeaderWarrior");
	}
	
	public PartyLeaderWarrior(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		27113,
		27036,
		27093,
		27112,
		27068,
		27065,
		27062,
		27114,
		27110,
		27108
	};
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("1007"))
		{
			if (npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK && !npc.isInMyTerritory())
			{
				npc.teleportTo(npc.getSpawnLocation(), 0);
				npc.removeAllAttackDesire();
			}
		}
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (npc._i_ai2 == 0 && Rnd.get(100) < getNpcIntAIParam(npc, "SummonPrivateRate"))
		{
			createPrivates(npc);
			
			npc.broadcastNpcSay(Rnd.get(SUMMON_PRIVATES_SHOUTS));
			
			npc._i_ai2 = 1;
		}
		
		if (attacker instanceof Player && getNpcIntAIParam(npc, "ShoutTarget") == 1 && Rnd.get(100) < 50 && attacker.getStatus().getHpRatio() < 0.4)
		{
			final Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
			if (mostHated == attacker)
			{
				npc.broadcastNpcSay(Rnd.get(TARGET_SHOUTS), attacker.getName());
				
				npc.removeAllAttackDesire();
				npc.getAI().addAttackDesire(attacker, 1000);
				
				npc._flag = attacker.getObjectId();
				
				broadcastScriptEvent(npc, 10002, npc.getObjectId(), 300);
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		if (getNpcIntAIParam(npc, "SummonPrivateRate") == 0)
		{
			createPrivates(npc);
			
			npc._i_ai2 = 1;
		}
		else
			npc._i_ai2 = 0;
		
		npc._weightPoint = 10;
		
		startQuestTimerAtFixedRate("1007", npc, null, 120000, 120000);
		
		super.onCreated(npc);
	}
	
	@Override
	public void onPartyAttacked(Npc caller, Npc called, Creature target, int damage)
	{
		if (target instanceof Playable && called.isMaster())
		{
			double hateRatio = getHateRatio(called, target);
			hateRatio = (((1.0 * damage) / (called.getStatus().getLevel() + 7)) + ((hateRatio / 100) * ((1.0 * damage) / (called.getStatus().getLevel() + 7))));
			
			called.getAI().addAttackDesire(target, (int) (((hateRatio * damage) * caller._weightPoint) * 10));
		}
	}
	
	@Override
	public void onPartyDied(Npc caller, Npc called)
	{
		if (called.isMaster() && !called.isDead() && caller.getSpawn().getRespawnDelay() != 0)
			caller.scheduleRespawn(caller.getSpawn().getRespawnDelay() * 1000L);
	}
}
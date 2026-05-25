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
package ext.mods.gameserver.scripting.script.ai.individual;

import ext.mods.commons.random.Rnd;

import ext.mods.Config;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.skills.L2Skill;

public class RoyalRushMissionNpc extends DefaultNpc
{
	public RoyalRushMissionNpc()
	{
		super("ai/individual");
	}
	
	public RoyalRushMissionNpc(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		18150,
		18151,
		18152,
		18153,
		18154,
		18155,
		18156,
		18157
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		startQuestTimer("3000", npc, null, 300000);
		startQuestTimerAtFixedRate("3001", npc, null, 5000, 5000);
		npc.lookNeighbor(300);
		npc._i_ai0 = 0;
	}
	
	@Override
	public void onNoDesire(Npc npc)
	{
		npc.getAI().addWanderDesire(5, 5);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (!(creature instanceof Playable))
		{
			if (Rnd.get(100) < 80)
				((Npc) creature).sendScriptEvent(1234, npc.getObjectId(), 0);
		}
		else
		{
			npc.getAI().addFollowDesire(creature, 100);
			
			if (npc._i_ai0 == 0)
				npc.broadcastNpcSay(NpcStringId.ID_1010483, creature.getName());
			else
			{
				switch (Rnd.get(4))
				{
					case 0:
						npc.getAI().addCastDesire(creature, 4384, 1, 1000000);
						break;
					
					case 1:
						npc.getAI().addCastDesire(creature, 4385, 1, 1000000);
						break;
					
					case 2:
						npc.getAI().addCastDesire(creature, 4386, 1, 1000000);
						break;
					
					case 3:
						npc.getAI().addCastDesire(creature, 4387, 1, 1000000);
						break;
				}
			}
		}
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		npc.getAI().addFleeDesire(attacker, Config.MAX_DRIFT_RANGE, 100);
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("3000"))
		{
			if (!npc.isDead())
			{
				createOnePrivateEx(npc, getNpcIntAIParam(npc, "KeyBox"), npc.getX(), npc.getY(), npc.getZ(), 0, 0, true);
				npc._i_ai0 = 1;
				
				npc.broadcastNpcSay(NpcStringId.ID_1000503);
				npc.lookNeighbor(300);
			}
		}
		else if (name.equalsIgnoreCase("3001"))
		{
			npc.lookNeighbor(300);
			if (npc._i_ai0 == 0)
				npc.broadcastNpcSay(NpcStringId.ID_1010484);
		}
		
		return null;
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		if (npc._i_ai0 == 0)
			createOnePrivateEx(npc, getNpcIntAIParam(npc, "StrongNPC"), npc.getX(), npc.getY(), npc.getZ(), 0, 0, true);
	}
}
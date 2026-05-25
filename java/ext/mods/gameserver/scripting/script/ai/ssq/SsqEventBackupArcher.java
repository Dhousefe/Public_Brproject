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

import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.enums.actors.ClassId;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.taskmanager.GameTimeTaskManager;

public class SsqEventBackupArcher extends SsqEventBasicWarrior
{
	public SsqEventBackupArcher()
	{
		super("ai/ssq");
	}
	
	public SsqEventBackupArcher(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		18011,
		18021,
		18031,
		18041,
		18051,
		18061,
		18071,
		18081,
		18091,
		18101
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		startQuestTimer("2002", npc, null, 5000);
		npc._i_ai4 = 0;
		
		int i0 = 60 * 5 - (GameTimeTaskManager.getInstance().getCurrentTick() - npc._param2);
		if (i0 < 1)
		{
			npc.deleteMe();
			return;
		}
		
		startQuestTimer("2000", npc, null, i0 * 1000);
		
		super.onCreated(npc);
	}
	
	@Override
	public void onNoDesire(Npc npc)
	{
		npc.getAI().addMoveToDesire(new Location(npc.getSpawnLocation().getX(), npc.getSpawnLocation().getY(), npc.getSpawnLocation().getZ()), 30);
	}
	
	@Override
	public void onMoveToFinished(Npc npc, int x, int y, int z)
	{
		if (x == npc.getSpawnLocation().getX() && y == npc.getSpawnLocation().getX() && z == npc.getSpawnLocation().getZ())
			npc.getAI().addDoNothingDesire(40, 30);
		else
			npc.getAI().addMoveToDesire(new Location(npc.getSpawnLocation().getX(), npc.getSpawnLocation().getY(), npc.getSpawnLocation().getZ()), 30);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (npc._i_ai4 == 1)
			return;
		
		if (npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
		{
			if (!(creature instanceof Playable))
				return;
			
			if (npc.getSpawn().isInMyTerritory(creature))
				npc.getAI().addAttackDesireHold(creature, 200);
			
			if (creature instanceof Player player && (ClassId.isInGroup(player, "@mage_group") || ClassId.isInGroup(player, "@wizard_group")))
			{
				if (!(creature instanceof Playable))
					return;
				
				if (npc.getSpawn().isInMyTerritory(creature))
					npc.getAI().addAttackDesireHold(creature, 200);
				
				if (npc.getSpawn().isInMyTerritory(creature))
					npc.getAI().addAttackDesireHold(creature, 200);
			}
		}
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("2000"))
		{
			npc.deleteMe();
			return null;
		}
		
		if (name.equalsIgnoreCase("2002"))
		{
			npc.lookNeighbor(500);
			final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
			if (npc.getAI().getCurrentIntention().getType() == IntentionType.ATTACK && topDesireTarget != null)
			{
				if (npc.distance2D(topDesireTarget) > 500)
					npc.removeAttackDesire(topDesireTarget);
			}
			startQuestTimer("2002", npc, null, 5000);
		}
		
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (npc._i_ai4 == 1)
			return;
		
		if (!npc.getSpawn().isInMyTerritory(attacker))
			return;
		
		if (attacker instanceof Playable)
			npc.getAI().addAttackDesire(attacker, (1.0 * damage / (npc.getStatus().getLevel() + 7)) * 100);
		
		if (getAbnormalLevel(npc, 1069, 1) >= 0 && npc._i_ai0 == 0)
		{
			startQuestTimer("2000", npc, null, 5000);
			npc._i_ai0 = 1;
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
	}
	
	@Override
	public void onScriptEvent(Npc npc, int eventId, int arg1, int arg2)
	{
		if (eventId == 10005 && npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
		{
			if (arg1 == npc._param1)
				npc.deleteMe();
		}
		
		if (eventId == 10015)
		{
			npc.removeAllAttackDesire();
			npc._i_ai4 = 1;
		}
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		if (killer instanceof Player player)
			ssqEventGiveItem(npc, player, 3);
	}
}
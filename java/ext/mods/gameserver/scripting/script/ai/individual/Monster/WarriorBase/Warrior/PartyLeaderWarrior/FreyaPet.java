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

import ext.mods.gameserver.data.manager.SpawnManager;
import ext.mods.gameserver.enums.actors.ClassId;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.spawn.NpcMaker;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.skills.L2Skill;

public class FreyaPet extends PartyLeaderWarriorAggressive
{
	public FreyaPet()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/PartyLeaderWarrior");
	}
	
	public FreyaPet(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		22104
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._c_ai0 = null;
		npc._c_ai1 = null;
		npc._c_ai2 = null;
		
		super.onCreated(npc);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (creature instanceof Player playerCreature && ClassId.isInGroup(playerCreature, "@cleric_group"))
		{
			if (npc._c_ai0 == null)
				npc._c_ai0 = creature;
			else if (npc._c_ai1 == null)
				npc._c_ai1 = creature;
			else if (npc._c_ai2 == null)
				npc._c_ai2 = creature;
		}
		super.onSeeCreature(npc, creature);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (Rnd.get(100) < 5)
			npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.SELF_BUFF), 1000000);
		
		if (Rnd.get(100) < 3)
		{
			if (npc.getAI().getTopDesireTarget() == attacker)
				npc.broadcastNpcSay(NpcStringId.ID_1000399, attacker.getName());
			
			npc.getAI().getAggroList().cleanAllHate();
			npc.getAI().addAttackDesire(attacker, 1000000);
			
			npc._flag = attacker.getObjectId();
			
			broadcastScriptEvent(npc, 10002, npc.getObjectId(), 2000);
		}
		
		if (npc.getStatus().getHpRatio() < 0.8)
			broadcastScriptEvent(npc, 10034, 0, 2000);
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onPartyDied(Npc caller, Npc called)
	{
		if (caller != called)
		{
			called.lookNeighbor(1000);
			
			if (called._c_ai0 != null && !called._c_ai0.isDead())
			{
				called.broadcastNpcSay(NpcStringId.ID_1000399, called._c_ai0.getName());
				
				called.getAI().getAggroList().cleanAllHate();
				called.getAI().addAttackDesire(called._c_ai0, 1000000);
				
				called._flag = called._c_ai0.getObjectId();
				
				broadcastScriptEvent(called, 10002, called.getObjectId(), 500);
			}
			
			if (called._c_ai1 != null && !called._c_ai1.isDead())
			{
				called.broadcastNpcSay(NpcStringId.ID_1000399, called._c_ai1.getName());
				
				called.getAI().getAggroList().cleanAllHate();
				called.getAI().addAttackDesire(called._c_ai1, 1000000);
				
				called._flag = called._c_ai1.getObjectId();
				
				broadcastScriptEvent(called, 10002, called.getObjectId(), 500);
			}
			
			if (called._c_ai2 != null && !called._c_ai2.isDead())
			{
				called.broadcastNpcSay(NpcStringId.ID_1000399, called._c_ai2.getName());
				
				called.getAI().getAggroList().cleanAllHate();
				called.getAI().addAttackDesire(called._c_ai2, 1000000);
				
				called._flag = called._c_ai2.getObjectId();
				
				broadcastScriptEvent(called, 10002, called.getObjectId(), 500);
			}
		}
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		final NpcMaker maker0 = SpawnManager.getInstance().getNpcMaker("schuttgart13_mb2314_05m1");
		if (maker0 != null)
			maker0.getMaker().onMakerScriptEvent("10005", maker0, 0, 0);
		
		broadcastScriptEvent(npc, 11036, 2, 7000);
	}
}
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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.PartyLeaderWarrior.PrimevalRoamer;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.PartyLeaderWarrior.PartyLeaderWarrior;
import ext.mods.gameserver.skills.L2Skill;

public class PrimevalRoamer extends PartyLeaderWarrior
{
	public PrimevalRoamer()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/PartyLeaderWarrior/PrimevalRoamer");
	}
	
	public PrimevalRoamer(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		22200,
		22224,
		22203,
		22225,
		22196,
		22208,
		22226,
		22223,
		22211,
		22227,
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai2 = 0;
		npc._i_ai3 = 1;
		
		super.onCreated(npc);
	}
	
	@Override
	public void onNoDesire(Npc npc)
	{
		if (!npc.isInCombat())
		{
			npc._i_ai2 = 0;
			npc._i_ai3 = 1;
			
			if (getNpcIntAIParam(npc, "mobile_type") == 0)
				npc.getAI().addMoveToDesire(npc.getSpawnLocation(), 30);
		}
		super.onNoDesire(npc);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
		final double hpRatio = npc.getStatus().getHpRatio();
		
		if (hpRatio * 100 <= getNpcIntAIParamOrDefault(npc, "HpChkRate4", 50))
			npc._i_ai3 = 2;
		else
			npc._i_ai3 = 1;
		
		if (npc._i_ai2 == 0 && hpRatio * 100 <= getNpcIntAIParamOrDefault(npc, "HpChkRate5", 30))
		{
			if (topDesireTarget != null)
				npc._c_ai1 = topDesireTarget;
			
			npc.removeAllAttackDesire();
			npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_BUFF1), 10000000);
		}
		
		if (Rnd.get(100) <= (getNpcIntAIParamOrDefault(npc, "ProbCond5", 20) * npc._i_ai3))
			broadcastScriptEvent(npc, 10002, npc.getObjectId(), getNpcIntAIParamOrDefault(npc, "BroadCastRange", 300));
		
		if (topDesireTarget != null)
		{
			if (Rnd.get(100) <= (getNpcIntAIParam(npc, "ProbPhysicalSpecial1") * npc._i_ai3))
				npc.getAI().addCastDesire(topDesireTarget, getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL1), 1000000);
			
			if (Rnd.get(100) <= (getNpcIntAIParam(npc, "ProbPhysicalSpecial2") * npc._i_ai3))
				npc.getAI().addCastDesire(topDesireTarget, getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL2), 1000000);
		}
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onUseSkillFinished(Npc npc, Creature creature, L2Skill skill, boolean success)
	{
		if (skill == getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_BUFF1) && success)
		{
			npc._i_ai2 = 1;
			
			final Creature c_ai1 = npc._c_ai1;
			if (c_ai1 != null)
			{
				if (c_ai1 instanceof Playable)
					npc.getAI().addAttackDesire(c_ai1, 100);
				
				npc._c_ai1 = null;
			}
		}
		super.onUseSkillFinished(npc, creature, skill, success);
	}
	
	@Override
	public void onScriptEvent(Npc npc, int eventId, int arg1, int arg2)
	{
		if (eventId == 10016)
		{
			final Creature c0 = (Creature) World.getInstance().getObject(arg1);
			if (c0 instanceof Player && getNpcIntAIParam(npc, "BroadCastReception") == 1)
			{
				npc.removeAllAttackDesire();
				
				onAttacked(npc, c0, 1000, null);
			}
		}
		super.onScriptEvent(npc, eventId, arg1, arg2);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (creature instanceof Player && getNpcIntAIParam(npc, "ag_type") == 1)
		{
			if (Rnd.get(100) <= (getNpcIntAIParam(npc, "ProbPhysicalSpecial1") * npc._i_ai3))
				npc.getAI().addCastDesire(creature, getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL1), 1000000);
			else if (Rnd.get(100) <= (getNpcIntAIParam(npc, "ProbPhysicalSpecial2") * npc._i_ai3))
				npc.getAI().addCastDesire(creature, getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL2), 1000000);
			
			tryToAttack(npc, creature);
			
			super.onSeeCreature(npc, creature);
		}
	}
}
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
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.spawn.NpcMaker;
import ext.mods.gameserver.skills.L2Skill;

public class FreyaGardener extends PartyLeaderWarriorAggressive
{
	public FreyaGardener()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/PartyLeaderWarrior");
	}
	
	public FreyaGardener(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		22100
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai1 = 0;
		npc._c_ai0 = null;
		npc._c_ai1 = null;
		
		super.onCreated(npc);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			if (npc._i_ai1 < 3 && getAbnormalLevel(attacker, getNpcSkillByType(npc, NpcSkillType.DEBUFF)) == -1)
			{
				if (npc._i_ai1 == 0)
				{
					npc._c_ai0 = attacker;
					
					npc.getAI().addCastDesire(npc._c_ai0, getNpcSkillByType(npc, NpcSkillType.DEBUFF), 1000000);
				}
				else if (npc._i_ai1 == 1 && npc._c_ai0 != attacker)
				{
					npc._c_ai1 = attacker;
					
					npc.getAI().addCastDesire(npc._c_ai1, getNpcSkillByType(npc, NpcSkillType.DEBUFF), 1000000);
				}
				else if (npc._i_ai1 == 2 && npc._c_ai0 != attacker && npc._c_ai1 != attacker)
					npc.getAI().addCastDesire(attacker, getNpcSkillByType(npc, NpcSkillType.DEBUFF), 1000000);
				
				npc._i_ai1++;
			}
			else if (Rnd.get(100) < 20)
				npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.RANGE_HOLD_A), 1000000);
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onUseSkillFinished(Npc npc, Creature creature, L2Skill skill, boolean success)
	{
		if (skill == getNpcSkillByType(npc, NpcSkillType.DEBUFF))
			npc.getAI().getAggroList().stopHate(creature);
		
		super.onUseSkillFinished(npc, creature, skill, success);
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		final NpcMaker maker0 = SpawnManager.getInstance().getNpcMaker("schuttgart13_npc2314_2m1");
		if (maker0 != null)
			maker0.getMaker().onMakerScriptEvent("10005", maker0, 0, 0);
	}
}
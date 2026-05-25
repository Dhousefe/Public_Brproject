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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorPhysicalSpecial;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.skills.L2Skill;

public class WarriorPatrolWeaponEquipped extends WarriorPhysicalSpecial
{
	public WarriorPatrolWeaponEquipped()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorPhysicalSpecial");
	}
	
	public WarriorPatrolWeaponEquipped(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		22124,
		22126,
		22129
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc.getAI().addWanderDesire(5, 5);
		npc._c_ai0 = npc;
		npc._i_ai3 = 0;
		
		super.onCreated(npc);
	}
	
	@Override
	public void onNoDesire(Npc npc)
	{
		npc._i_ai3 = 0;
		
		super.onNoDesire(npc);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (creature instanceof Player)
		{
			final Player player = creature.getActingPlayer();
			if (player.getActiveWeaponInstance() != null)
			{
				npc.getAI().addCastDesire(creature, getNpcSkillByType(npc, NpcSkillType.DEBUFF), 1000000);
				npc._c_ai0 = creature;
			}
		}
		
		super.onSeeCreature(npc, creature);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
			if (topDesireTarget != null)
			{
				if (topDesireTarget == attacker)
				{
					if (Rnd.get(100) < 33)
						npc.getAI().addCastDesire(attacker, getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_PHYSICAL_SPECIAL), 1000000);
					
					if (Rnd.get(100) < 33)
						npc.getAI().addCastDesire(attacker, getNpcSkillByType(npc, NpcSkillType.DEBUFF2), 1000000);
				}
				if (damage == 0)
					damage = 1;
				
				npc.getAI().addAttackDesire(attacker, (((1.0 * damage) / (npc.getStatus().getLevel() + 7)) * 10000));
			}
		}
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onUseSkillFinished(Npc npc, Creature creature, L2Skill skill, boolean success)
	{
		if (skill == getNpcSkillByType(npc, NpcSkillType.DEBUFF))
		{
			if (npc._i_ai3 == 0)
			{
				npc.broadcastNpcSay(NpcStringId.ID_1121006);
				npc._i_ai3 = 1;
			}
			npc.getAI().addAttackDesire(npc._c_ai0, 10000);
		}
	}
}
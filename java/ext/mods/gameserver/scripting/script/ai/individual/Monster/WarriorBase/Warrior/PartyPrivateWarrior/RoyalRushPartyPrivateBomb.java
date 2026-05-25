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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.PartyPrivateWarrior;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.skills.L2Skill;

public class RoyalRushPartyPrivateBomb extends PartyPrivateWarrior
{
	public RoyalRushPartyPrivateBomb()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/PartyPrivateWarrior");
	}
	
	public RoyalRushPartyPrivateBomb(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21437
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
			
			if (topDesireTarget != null && topDesireTarget == attacker)
			{
				if (Rnd.get(100) < 33)
					npc.getAI().addCastDesire(attacker, getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL1), 1000000);
				
				if (Rnd.get(100) < 33 && npc.getStatus().getHpRatio() < 0.4 && npc.hasMaster() && !npc.getMaster().isDead())
				{
					npc.getAI().addCastDesire(npc.getMaster(), getNpcSkillByType(npc, NpcSkillType.MAGIC_HEAL), 1000000);
					npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.BOMB), 1000000);
				}
			}
		}
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onPartyAttacked(Npc caller, Npc called, Creature target, int damage)
	{
		if (Rnd.get(100) < 33 && called.getStatus().getHpRatio() < 0.4 && called.hasMaster() && !called.getMaster().isDead())
		{
			called.getAI().addCastDesire(called.getMaster(), getNpcSkillByType(called, NpcSkillType.MAGIC_HEAL), 1000000);
			called.getAI().addCastDesire(called, getNpcSkillByType(called, NpcSkillType.BOMB), 1000000);
		}
		
		super.onPartyAttacked(caller, called, target, damage);
	}
}
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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCastDDMagicPhysicalSpecial;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.Warrior;
import ext.mods.gameserver.skills.L2Skill;

public class WarriorCastDDMagicPhysicalSpecial extends Warrior
{
	public WarriorCastDDMagicPhysicalSpecial()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCastDDMagicPhysicalSpecial");
	}
	
	public WarriorCastDDMagicPhysicalSpecial(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		22046,
		22049
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
			
			if (mostHated != null)
			{
				if (npc.distance2D(attacker) > 200 && mostHated == attacker)
				{
					L2Skill longRangeDD = getNpcSkillByType(npc, NpcSkillType.W_LONG_RANGE_DD_MAGIC);
					
					npc.getAI().addCastDesire(attacker, longRangeDD, 1000000);
				}
				
				if (Rnd.get(100) < 33 && mostHated != attacker && npc.distance2D(attacker) < 200)
				{
					L2Skill physicalSpecialRange = getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL_RANGE);
					
					npc.getAI().addCastDesire(attacker, physicalSpecialRange, 1000000);
				}
			}
		}
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		if (called.getAI().getLifeTime() > 7 && attacker instanceof Playable)
		{
			Creature mostHated = called.getAI().getAggroList().getMostHatedCreature();
			
			if (mostHated != null)
			{
				if (called.distance2D(attacker) < 200 && Rnd.get(100) < 33 && mostHated != attacker)
				{
					L2Skill physicalSpecialRange = getNpcSkillByType(called, NpcSkillType.PHYSICAL_SPECIAL_RANGE);
					
					called.getAI().addCastDesire(attacker, physicalSpecialRange, 1000000);
				}
			}
		}
		
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
	
	@Override
	public void onUseSkillFinished(Npc npc, Creature creature, L2Skill skill, boolean success)
	{
		Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
		
		if (mostHated != null)
		{
			if (npc.distance2D(mostHated) > 200 && mostHated instanceof Player)
			{
				L2Skill longRangeDD = getNpcSkillByType(npc, NpcSkillType.W_LONG_RANGE_DD_MAGIC);
				
				npc.getAI().addCastDesire(mostHated, longRangeDD, 1000000);
			}
		}
		
		super.onUseSkillFinished(npc, creature, skill, success);
	}
}

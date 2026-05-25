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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCast3SkillsMagical2;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.Warrior;
import ext.mods.gameserver.skills.L2Skill;

public class WarriorCast3SkillsMagical2 extends Warrior
{
	public WarriorCast3SkillsMagical2()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCast3SkillsMagical2");
	}
	
	public WarriorCast3SkillsMagical2(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		20411,
		20416,
		20505,
		20618,
		20862,
		27184,
		21396,
		20202,
		21020,
		21108,
		20154,
		20223,
		20225,
		20799,
		20109,
		20609,
		20415,
		21595
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			if (npc.distance2D(attacker) > 100)
			{
				Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
				
				if (mostHated != null)
				{
					if (mostHated == attacker)
					{
						if (Rnd.get(100) < 33)
						{
							L2Skill DDMagic1 = getNpcSkillByType(npc, NpcSkillType.DD_MAGIC1);
							
							npc.getAI().addCastDesire(attacker, DDMagic1, 1000000);
						}
						
						if (Rnd.get(100) < 33)
						{
							L2Skill DDMagic2 = getNpcSkillByType(npc, NpcSkillType.DD_MAGIC2);
							
							npc.getAI().addCastDesire(attacker, DDMagic2, 1000000);
						}
						
						L2Skill debuff = getNpcSkillByType(npc, NpcSkillType.DEBUFF);
						if (Rnd.get(100) < 33 && getAbnormalLevel(attacker, debuff) <= 0)
							npc.getAI().addCastDesire(attacker, debuff, 1000000);
					}
				}
			}
		}
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		if ((called.getAI().getLifeTime() > 7 && attacker instanceof Playable) && called.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
		{
			if (called.distance2D(attacker) > 100)
			{
				if (Rnd.get(100) < 33)
				{
					L2Skill DDMagic1 = getNpcSkillByType(called, NpcSkillType.DD_MAGIC1);
					
					called.getAI().addCastDesire(attacker, DDMagic1, 1000000);
				}
				
				if (Rnd.get(100) < 33)
				{
					L2Skill DDMagic2 = getNpcSkillByType(called, NpcSkillType.DD_MAGIC2);
					
					called.getAI().addCastDesire(attacker, DDMagic2, 1000000);
				}
			}
			
			L2Skill debuff = getNpcSkillByType(called, NpcSkillType.DEBUFF);
			if (Rnd.get(100) < 33 && getAbnormalLevel(attacker, debuff) <= 0)
				called.getAI().addCastDesire(attacker, debuff, 1000000);
		}
		
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
}
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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCastingHeal;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.skills.L2Skill;

public class WarriorCastingHealSleep extends WarriorCastingHeal
{
	public WarriorCastingHealSleep()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCastingHeal");
	}
	
	public WarriorCastingHealSleep(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21664,
		21687,
		21710,
		21733,
		21756,
		21779
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
		
		if (attacker instanceof Playable && mostHated != null)
		{
			if (Rnd.get(100) < 33 && mostHated != attacker)
			{
				L2Skill magicSleep = getNpcSkillByType(npc, NpcSkillType.MAGIC_SLEEP);
				
				npc.getAI().addCastDesire(attacker, magicSleep, 1000000);
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		Creature mostHated = called.getAI().getAggroList().getMostHatedCreature();
		
		if (mostHated != null)
		{
			if (Rnd.get(100) < 33 && mostHated == attacker)
			{
				L2Skill magicSleep = getNpcSkillByType(called, NpcSkillType.MAGIC_SLEEP);
				
				called.getAI().addCastDesire(attacker, magicSleep, 1000000);
			}
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
}
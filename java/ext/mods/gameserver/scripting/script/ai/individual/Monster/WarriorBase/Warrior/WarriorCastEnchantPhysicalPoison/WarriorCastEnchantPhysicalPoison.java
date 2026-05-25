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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCastEnchantPhysicalPoison;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.Warrior;
import ext.mods.gameserver.skills.L2Skill;

public class WarriorCastEnchantPhysicalPoison extends Warrior
{
	public WarriorCastEnchantPhysicalPoison()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCastEnchantPhysicalPoison");
	}
	
	public WarriorCastEnchantPhysicalPoison(String descr)
	{
		super(descr);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		final L2Skill buff1 = getNpcSkillByType(npc, NpcSkillType.BUFF1);
		npc.getAI().addCastDesire(npc, buff1, 1000000);
		
		final L2Skill buff2 = getNpcSkillByType(npc, NpcSkillType.BUFF2);
		npc.getAI().addCastDesire(npc, buff2, 1000000);
		
		super.onCreated(npc);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		final L2Skill buff1 = getNpcSkillByType(npc, NpcSkillType.BUFF1);
		if (getAbnormalLevel(npc, buff1) <= 0)
			npc.getAI().addCastDesire(npc, buff1, 1000000);
		
		final L2Skill buff2 = getNpcSkillByType(npc, NpcSkillType.BUFF2);
		if (getAbnormalLevel(npc, buff2) <= 0)
			npc.getAI().addCastDesire(npc, buff2, 1000000);
		
		if (Rnd.get(100) < 33)
		{
			final L2Skill physicalSpecial = getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL);
			npc.getAI().addCastDesire(attacker, physicalSpecial, 1000000);
		}
		
		maybeCastDebuffs(attacker, npc);
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		maybeCastDebuffs(attacker, called);
		
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		maybeCastDebuffs(caster, npc);
		
		super.onSeeSpell(npc, caster, skill, targets, isPet);
	}
	
	private static void maybeCastDebuffs(Creature attacker, Npc npc)
	{
		if (Rnd.get(100) < 10)
		{
			final L2Skill debuff1 = getNpcSkillByType(npc, NpcSkillType.DEBUFF1);
			
			final int i1 = getAbnormalLevel(attacker, debuff1);
			if (i1 <= 0)
				npc.getAI().addCastDesire(attacker, debuff1, 1000000);
			else if (i1 < 10)
				npc.getAI().addCastDesire(attacker, debuff1.getId(), debuff1.getLevel() + i1, 1000000);
		}
		
		if (Rnd.get(100) < 10)
		{
			final L2Skill debuff2 = getNpcSkillByType(npc, NpcSkillType.DEBUFF2);
			
			final int i1 = getAbnormalLevel(attacker, debuff2);
			if (i1 <= -1)
				npc.getAI().addCastDesire(attacker, debuff2, 1000000);
			else if (i1 < 10)
				npc.getAI().addCastDesire(attacker, debuff2.getId(), debuff2.getLevel() + i1, 1000000);
		}
	}
}
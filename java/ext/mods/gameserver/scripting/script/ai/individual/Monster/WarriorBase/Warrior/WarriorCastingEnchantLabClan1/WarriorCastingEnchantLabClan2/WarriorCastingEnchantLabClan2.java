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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCastingEnchantLabClan1.WarriorCastingEnchantLabClan2;

import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCastingEnchantLabClan1.WarriorCastingEnchantLabClan1;
import ext.mods.gameserver.skills.L2Skill;

public class WarriorCastingEnchantLabClan2 extends WarriorCastingEnchantLabClan1
{
	public WarriorCastingEnchantLabClan2()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCastingEnchantLabClan1/WarriorCastingEnchantLabClan2");
	}
	
	public WarriorCastingEnchantLabClan2(String descr)
	{
		super(descr);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		final Party party0 = attacker.getParty();
		if (party0 != null)
		{
			if (party0.getMembersCount() >= 8)
			{
				final L2Skill selfBuff2 = getNpcSkillByType(npc, NpcSkillType.SELF_BUFF2);
				if (getAbnormalLevel(npc, selfBuff2) <= 0)
					npc.getAI().addCastDesire(attacker, selfBuff2, 1000000);
			}
			else if (party0.getMembersCount() >= 6)
			{
				final L2Skill selfBuff1 = getNpcSkillByType(npc, NpcSkillType.SELF_BUFF1);
				if (getAbnormalLevel(npc, selfBuff1) <= 0)
					npc.getAI().addCastDesire(attacker, selfBuff1, 1000000);
			}
		}
		
		super.onAttacked(npc, attacker, damage, skill);
	}
}
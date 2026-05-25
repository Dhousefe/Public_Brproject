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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCastingEnchantLabClan1;

import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.Warrior;
import ext.mods.gameserver.skills.L2Skill;

public class WarriorCastingEnchantLabClan1 extends Warrior
{
	public WarriorCastingEnchantLabClan1()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCastingEnchantLabClan1");
	}
	
	public WarriorCastingEnchantLabClan1(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		27318
	};
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		final L2Skill buff1 = getNpcSkillByType(called, NpcSkillType.BUFF1);
		final L2Skill buff2 = getNpcSkillByType(called, NpcSkillType.BUFF1);
		final L2Skill buff3 = getNpcSkillByType(called, NpcSkillType.BUFF1);
		final L2Skill buff4 = getNpcSkillByType(called, NpcSkillType.BUFF1);
		
		if (getAbnormalLevel(caller, buff1) <= 0)
			called.getAI().addCastDesire(caller, buff1, 1000000);
		else if (getAbnormalLevel(caller, buff1) >= 0)
			called.getAI().addCastDesire(caller, buff2, 1000000);
		else if (getAbnormalLevel(caller, buff2) >= 0)
			called.getAI().addCastDesire(caller, buff3, 1000000);
		else if (getAbnormalLevel(caller, buff3) >= 0)
			called.getAI().addCastDesire(caller, buff4, 1000000);
		
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
}
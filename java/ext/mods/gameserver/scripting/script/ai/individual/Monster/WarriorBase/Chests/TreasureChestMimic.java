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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Chests;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.WarriorBase;
import ext.mods.gameserver.skills.L2Skill;

public class TreasureChestMimic extends WarriorBase
{
	public TreasureChestMimic()
	{
		super("ai/individual/Monster/WarriorBase/Chests");
	}
	
	public TreasureChestMimic(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21671,
		21694,
		21717,
		21740,
		21763,
		21786,
		21801,
		21802,
		21803,
		21804,
		21805,
		21806,
		21807,
		21808,
		21809,
		21810,
		21811,
		21812,
		21813,
		21814,
		21815,
		21816,
		21817,
		21818,
		21819,
		21820,
		21821,
		21822,
		18005
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (skill != null && (skill.getId() == 27 || skill.getId() == 2065))
		{
			int skillLvl = npc.getStatus().getLevel() - 5;
			
			L2Skill mimicOfWrath = SkillTable.getInstance().getInfo(4245, 1);
			L2Skill mimicStrongAttack = SkillTable.getInstance().getInfo(4144, skillLvl);
			
			npc.getAI().addCastDesire(npc, mimicOfWrath, 1000000);
			npc.getAI().addCastDesire(attacker, mimicStrongAttack, 1000000);
			npc.getAI().addAttackDesire(attacker, (((skill.getLevel()) / (npc.getStatus().getLevel() + 7)) * 150));
		}
		
		if (attacker instanceof Playable)
		{
			if (damage == 0)
				damage = 1;
			
			npc.getAI().addAttackDesire(attacker, ((1.0 * damage) / (npc.getStatus().getLevel() + 7)) * 100);
		}
	}
}
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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.skills.L2Skill;


public class WarriorTimerBomb extends Warrior
{
	public WarriorTimerBomb()
	{
		super("ai/individual/Monster/WarriorBase/Warrior");
	}
	
	public WarriorTimerBomb(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		18340,
		18341
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai0 = 0;
		
		int i0 = Rnd.get(59);
		
		if (i0 < 10)
			i0 = (i0 + 10);
		
		startQuestTimer("1009", npc, null, i0 * 1000);
		
		int i1 = Rnd.get(9) + 1;
		
		startQuestTimer("1010", npc, null, i1 * 1000);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (npc.distance2D(attacker) < 200)
			npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_DD_MAGIC), 1000000);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("1009"))
			npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_DD_MAGIC), 1000000);
		
		if (name.equalsIgnoreCase("1010"))
		{
			if (npc._i_ai0 == 0)
			{
				npc.setWalkOrRun(false);
				npc._i_ai0 = 1;
			}
			else
			{
				npc.setWalkOrRun(true);
				npc._i_ai0 = 0;
			}
			
			int i1 = Rnd.get(9);
			
			if (i1 <= 2)
				i1 = (i1 + 3);
			
			startQuestTimer("1010", npc, null, i1 * 1000);
		}
		
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onUseSkillFinished(Npc npc, Creature creature, L2Skill skill, boolean success)
	{
		if (skill == getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_DD_MAGIC) && success)
			npc.doDie(npc);
		
		super.onUseSkillFinished(npc, creature, skill, success);
	}
}
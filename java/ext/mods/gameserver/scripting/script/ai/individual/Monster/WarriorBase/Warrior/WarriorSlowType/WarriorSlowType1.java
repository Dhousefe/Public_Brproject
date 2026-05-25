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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorSlowType;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.skills.L2Skill;

public class WarriorSlowType1 extends WarriorSlowType
{
	public WarriorSlowType1()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorSlowType");
	}
	
	public WarriorSlowType1(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		20101,
		20378,
		20379,
		20380,
		20526,
		21128,
		21131,
		20346,
		20566,
		20565,
		20083,
		20368,
		20333,
		21103,
		20511,
		20559,
		20016,
		20591,
		20597,
		20521
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		final double dist = npc.distance2D(attacker);
		
		final Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
		if (mostHated != null && attacker != mostHated && npc.getAI().getCurrentIntention().getType() == IntentionType.ATTACK && npc.distance2D(mostHated) > 100 && dist < 100 && Rnd.get(100) < 80)
		{
			npc.removeAllAttackDesire();
			
			if (attacker instanceof Playable)
			{
				double f0 = getHateRatio(npc, attacker);
				f0 = (((1.0 * damage) / (npc.getStatus().getLevel() + 7)) + ((f0 / 100) * ((1.0 * damage) / (npc.getStatus().getLevel() + 7))));
				npc.getAI().addAttackDesire(attacker, f0 * 30);
			}
		}
		
		if (dist < 200 && Rnd.get(100) < 10)
			npc.getAI().addCastDesire(mostHated, getNpcSkillByType(npc, NpcSkillType.DD_MAGIC_SLOW), 1000000);
		
		super.onAttacked(npc, attacker, damage, skill);
	}
}
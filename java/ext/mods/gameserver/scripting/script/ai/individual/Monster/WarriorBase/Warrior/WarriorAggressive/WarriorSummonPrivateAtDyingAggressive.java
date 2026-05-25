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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorAggressive;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.skills.L2Skill;

public class WarriorSummonPrivateAtDyingAggressive extends WarriorAggressive
{
	public WarriorSummonPrivateAtDyingAggressive()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorAggressive");
	}
	
	public WarriorSummonPrivateAtDyingAggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21653,
		21655,
		21656,
		21654,
		21657,
		21652
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
		if (topDesireTarget != null && topDesireTarget == attacker)
			npc._i_ai3 = attacker.getObjectId();
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		if (Rnd.get(100) < getNpcIntAIParam(npc, "SummonPrivateRate"))
		{
			createPrivates(npc);
			
			broadcastScriptEvent(npc, 10020, npc._i_ai3, 500);
			
			startQuestTimer("5001", npc, null, 1500);
		}
		super.onMyDying(npc, killer);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("5001"))
		{
			broadcastScriptEvent(npc, 10020, npc._i_ai3, 500);
			
			startQuestTimer("5001", npc, null, 1500);
			
			return super.onTimer(name, npc, player);
		}
		return null;
	}
}
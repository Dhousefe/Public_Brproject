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
package ext.mods.gameserver.scripting.script.ai.individual.RoyalRushDefaultNpc;

import ext.mods.commons.random.Rnd;

import ext.mods.Config;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.skills.L2Skill;

public class RoyalRushAfflict extends RoyalRushDefaultNpc
{
	public RoyalRushAfflict()
	{
		super("ai/individual/RoyalRushDefaultNpc");
	}
	
	public RoyalRushAfflict(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		18244,
		18245,
		18246,
		18247,
		18248,
		18249,
		18250,
		18251,
		18252,
		18253,
		18254,
		18255
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		final int i0 = Rnd.get(3);
		switch (i0)
		{
			case 0:
				npc._i_ai0 = 0;
				startQuestTimer("3001", npc, null, 5000);
				break;
			
			case 1:
				npc._i_ai0 = 1;
				startQuestTimer("3002", npc, null, 5000);
				break;
			
			case 2:
				npc._i_ai0 = 2;
				startQuestTimer("3002", npc, null, 5000);
				break;
		}
		
		npc.lookNeighbor(300);
		
		super.onCreated(npc);
	}
	
	@Override
	public void onNoDesire(Npc npc)
	{
		npc.getAI().addWanderDesire(5, 5);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (creature instanceof Player && Rnd.get(100) < 50)
			npc.getAI().addFollowDesire(creature, 100);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		npc.getAI().addFleeDesire(attacker, Config.MAX_DRIFT_RANGE, 100);
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("3001"))
			npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.AFFLICT_SKILL1), 1000000);
		
		if (name.equalsIgnoreCase("3002"))
		{
			if (npc._i_ai0 == 1)
				npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.AFFLICT_SKILL2), 1000000);
			else if (npc._i_ai0 == 2)
				npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.AFFLICT_SKILL3), 1000000);
			
			startQuestTimer("3003", npc, null, 5000);
		}
		
		if (name.equalsIgnoreCase("3003"))
			npc.lookNeighbor(300);
		
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onUseSkillFinished(Npc npc, Creature creature, L2Skill skill, boolean success)
	{
		switch (npc._i_ai0)
		{
			case 0:
				npc.deleteMe();
				break;
			
			case 1:
				npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.AFFLICT_SKILL2), 1000000);
				break;
			
			case 2:
				npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.AFFLICT_SKILL3), 1000000);
				break;
		}
	}
}
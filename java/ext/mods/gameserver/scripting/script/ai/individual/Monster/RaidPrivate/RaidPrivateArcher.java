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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.RaidPrivate;

import ext.mods.commons.random.Rnd;

import ext.mods.Config;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.skills.L2Skill;

public class RaidPrivateArcher extends RaidPrivateStandard
{
	public RaidPrivateArcher()
	{
		super("ai/individual/Monster/RaidPrivate/RaidPrivateArcher");
	}
	
	public RaidPrivateArcher(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		25005,
		25011,
		25027,
		25036,
		25042,
		25058,
		25068,
		25086,
		25107,
		25116,
		25132,
		25147,
		25150,
		25171,
		25186,
		25212,
		25218,
		25257,
		25261,
		25270,
		25300,
		25344,
		25364,
		25389,
		25413,
		25446,
		25477,
		25491,
		25502,
		29098
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai2 = 0;
		
		super.onCreated(npc);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (npc._i_ai2 == 0 && npc.distance2D(attacker) < 100)
		{
			startQuestTimer("100002", npc, null, 2000);
			
			npc._i_ai2 = 1;
			npc._c_ai1 = attacker;
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("100002"))
		{
			npc.getAI().addFleeDesire(npc._c_ai1, Config.MAX_DRIFT_RANGE, 10000);
			npc._i_ai2 = 0;
		}
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onPartyAttacked(Npc caller, Npc called, Creature target, int damage)
	{
		if (target instanceof Playable)
		{
			final Creature topDesireTarget = called.getAI().getTopDesireTarget();
			if (topDesireTarget != null)
			{
				if (target == topDesireTarget && Rnd.get(20) < 1)
				{
					final L2Skill longRangePhysicalSpecial_a = getNpcSkillByType(called, NpcSkillType.LONG_RANGE_PHYSICAL_SPECIAL_A);
					called.getAI().addCastDesire(target, longRangePhysicalSpecial_a, 1000000);
				}
			}
		}
		super.onPartyAttacked(caller, called, target, damage);
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
		if (topDesireTarget != null && caster == topDesireTarget && Rnd.get(20) < 1)
		{
			final L2Skill longRangePhysicalSpecial_a = getNpcSkillByType(npc, NpcSkillType.LONG_RANGE_PHYSICAL_SPECIAL_A);
			npc.getAI().addCastDesire(caster, longRangePhysicalSpecial_a, 1000000);
		}
		super.onSeeSpell(npc, caster, skill, targets, isPet);
	}
}
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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.RaidBoss.RaidBossParty;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.skills.L2Skill;

public class RaidBossType3 extends RaidBossParty
{
	public RaidBossType3()
	{
		super("ai/individual/Monster/RaidBoss/RaidBossAlone/RaidBossParty/RaidBossType3");
	}
	
	public RaidBossType3(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		25269,
		25035,
		25342,
		25299,
		25146,
		25004,
		25010,
		25026,
		25041,
		25057,
		25067,
		25085,
		25106,
		25115,
		25131,
		25149,
		25170,
		25185,
		25211,
		25217,
		25241,
		25256,
		25260,
		25362,
		25388,
		25412,
		25444,
		25475,
		25490,
		25501,
		29096
	};
	
	@Override
	public void onPartyAttacked(Npc caller, Npc called, Creature target, int damage)
	{
		if (target instanceof Playable)
		{
			final Creature topDesireTarget = called.getAI().getTopDesireTarget();
			if (target == topDesireTarget && Rnd.get(15) < 1)
			{
				final L2Skill physicalSpecial_b = getNpcSkillByType(called, NpcSkillType.PHYSICAL_SPECIAL_B);
				called.getAI().addCastDesire(target, physicalSpecial_b, 1000000);
			}
		}
		super.onPartyAttacked(caller, called, target, damage);
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
		if (topDesireTarget != null && caster == topDesireTarget && Rnd.get(15) < 1)
		{
			final L2Skill physicalSpecial_b = getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL_B);
			npc.getAI().addCastDesire(caster, physicalSpecial_b, 1000000);
		}
		super.onSeeSpell(npc, caster, skill, targets, isPet);
	}
}
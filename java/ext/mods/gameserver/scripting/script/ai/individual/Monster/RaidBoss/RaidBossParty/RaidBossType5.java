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

public class RaidBossType5 extends RaidBossParty
{
	public RaidBossType5()
	{
		super("ai/individual/Monster/RaidBoss/RaidBossAlone/RaidBossParty/RaidBossType5");
	}
	
	public RaidBossType5(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		25109,
		25322,
		25346,
		25220,
		25296,
		25302,
		25283,
		25328,
		25514,
		25001,
		25032,
		25060,
		25070,
		25082,
		25089,
		25095,
		25112,
		25119,
		25128,
		25137,
		25155,
		25179,
		25189,
		25199,
		25208,
		25214,
		25223,
		25249,
		25263,
		25273,
		25352,
		25360,
		25373,
		25378,
		25383,
		25392,
		25401,
		25404,
		25407,
		25410,
		25418,
		25426,
		25429,
		25434,
		25438,
		25460,
		25470,
		25473,
		25484,
		25487,
		25496,
		29033,
		25506,
		25504
	};
	
	@Override
	public void onPartyAttacked(Npc caller, Npc called, Creature target, int damage)
	{
		L2Skill rangeHold_a = getNpcSkillByType(called, NpcSkillType.RANGE_HOLD_A);
		if ((target instanceof Playable && getAbnormalLevel(target, rangeHold_a) == -1) && Rnd.get((10 * 15)) < 1)
			called.getAI().addCastDesire(target, rangeHold_a, 1000000);
		super.onPartyAttacked(caller, called, target, damage);
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		L2Skill rangeHold_a = getNpcSkillByType(npc, NpcSkillType.RANGE_HOLD_A);
		if (getAbnormalLevel(caster, rangeHold_a) == -1 && Rnd.get((10 * 15)) < 1)
			npc.getAI().addCastDesire(caster, rangeHold_a, 1000000);
		super.onSeeSpell(npc, caster, skill, targets, isPet);
	}
}
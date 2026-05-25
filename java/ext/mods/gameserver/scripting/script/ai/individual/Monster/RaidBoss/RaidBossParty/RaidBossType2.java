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

public class RaidBossType2 extends RaidBossParty
{
	public RaidBossType2()
	{
		super("ai/individual/Monster/RaidBoss/RaidBossAlone/RaidBossParty/RaidBossType2");
	}
	
	public RaidBossType2(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		25286,
		25524,
		25339,
		25202,
		25051,
		25312,
		25007,
		25016,
		25020,
		25029,
		25044,
		25073,
		25079,
		25099,
		25134,
		25143,
		25159,
		25166,
		25173,
		25182,
		25226,
		25235,
		25238,
		25277,
		25357,
		25366,
		25375,
		25395,
		25431,
		25447,
		25456,
		25463,
		25481,
		29030
	};
	
	@Override
	public void onPartyAttacked(Npc caller, Npc called, Creature target, int damage)
	{
		if ((target instanceof Playable) && Rnd.get(150) < 1)
		{
			final L2Skill rangeDDMagic_a = getNpcSkillByType(called, NpcSkillType.RANGE_DD_MAGIC_A);
			called.getAI().addCastDesire(target, rangeDDMagic_a, 1000000);
		}
		super.onPartyAttacked(caller, called, target, damage);
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		if (Rnd.get(150) < 1)
		{
			final L2Skill rangeDDMagic_a = getNpcSkillByType(npc, NpcSkillType.RANGE_DD_MAGIC_A);
			npc.getAI().addCastDesire(caster, rangeDDMagic_a, 1000000);
		}
		super.onSeeSpell(npc, caster, skill, targets, isPet);
	}
}
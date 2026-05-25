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
package ext.mods.gameserver.scripting.script.ai.siegeguards.GludioHold;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.skills.L2Skill;

public class GludioSwordGuardUseSkill extends GludioSwordGuard
{
	public GludioSwordGuardUseSkill()
	{
		super("ai/siegeguards/GludioHold");
	}
	
	public GludioSwordGuardUseSkill(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		35060
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
		if (topDesireTarget != null && getPledgeCastleState(npc, attacker) != 2)
		{
			if (attacker == topDesireTarget && Rnd.get(25) < 1)
				npc.getAI().addCastDesire(attacker, getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL), 1000000);
			
			if (npc.distance2D(attacker) < 150 && Rnd.get((50 * 15)) < 1)
				npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.DISPELL), 1000000);
		}
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		final Creature topDesireTarget = called.getAI().getTopDesireTarget();
		if (topDesireTarget != null && getPledgeCastleState(called, attacker) != 2)
		{
			if (attacker == topDesireTarget && Rnd.get(25) < 1)
				called.getAI().addCastDesire(attacker, getNpcSkillByType(called, NpcSkillType.PHYSICAL_SPECIAL), 1000000);
			
			if (called.distance2D(attacker) < 150 && Rnd.get((50 * 15)) < 1)
				called.getAI().addCastDesire(called, getNpcSkillByType(called, NpcSkillType.DISPELL), 1000000);
		}
		
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (getPledgeCastleState(npc, creature) != 2)
		{
			final int i0 = Rnd.get(10000);
			if (i0 < 1)
				npc.broadcastNpcShout(NpcStringId.ID_1800012);
			else if (i0 < 2)
				npc.broadcastNpcShout(NpcStringId.ID_1800013);
			
			npc.getAI().addAttackDesire(creature, 200);
		}
	}
}
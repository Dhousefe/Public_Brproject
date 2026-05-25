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
package ext.mods.gameserver.scripting.script.ai.siegablehall;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.scripting.script.ai.individual.DefaultNpc;
import ext.mods.gameserver.skills.L2Skill;

public class Agit02Dietrich extends DefaultNpc
{
	public Agit02Dietrich()
	{
		super("ai/siegeablehall");
	}
	
	public Agit02Dietrich(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		35408
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc.broadcastNpcShout(NpcStringId.ID_1000277);
		
		startQuestTimerAtFixedRate("1001", npc, null, 1000, 60000);
		
		npc._c_ai0 = npc;
		npc._i_ai0 = 0;
		
		super.onCreated(npc);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("1001"))
		{
			if (npc.hasMaster() && !npc.isInMyTerritory() && Rnd.get(3) < 1)
			{
				npc.teleportTo(npc.getMaster().getPosition(), 0);
				npc.removeAllAttackDesire();
			}
			
			if (Rnd.get(5) < 1)
				npc.getAI().getAggroList().randomizeAttack();
		}
		
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onPartyAttacked(Npc caller, Npc called, Creature target, int damage)
	{
		if (called._c_ai0 != called && Rnd.get(3) < 1 && called.getSpawn().isInMyTerritory(called._c_ai0) && called.getCast().canAttemptCast(called, SkillTable.getInstance().getInfo(4238, 1)))
			called.getAI().addMoveToDesire(called._c_ai0.getPosition(), 100000000);
		
		if (target instanceof Playable)
			called.getAI().addAttackDesire(target, (((damage * 1.0 / called.getStatus().getMaxHp()) / 0.05) * damage) * caller._weightPoint / 1000000);
		
		if (called._i_ai0 == 0 && called.hasMaster() && called.getMaster().getStatus().getHpRatio() < 0.05)
		{
			called._i_ai0 = 1;
			called.getAI().addCastDesire(called, 4235, 1, 1000000000);
			called.broadcastNpcSay(NpcStringId.ID_1000280);
		}
	}
	
	@Override
	public void onUseSkillFinished(Npc npc, Creature creature, L2Skill skill, boolean success)
	{
		if (skill.getId() == 4235)
		{
			npc.teleportTo(177134, -18807, -2263, 0);
			npc.removeAllAttackDesire();
		}
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		switch (caster.getClassId())
		{
			case BISHOP, PROPHET, ELVEN_ELDER, SHILLIEN_ELDER, OVERLORD, WARCRYER:
				npc._c_ai0 = caster;
				break;
		}
		
		if (skill.getAggroPoints() > 0 && npc.getAI().getCurrentIntention().getType() == IntentionType.ATTACK && npc.getAI().getTopDesireTarget() == caster)
			npc.getAI().addAttackDesire(caster, (((skill.getAggroPoints() * 1.0 / npc.getStatus().getMaxHp()) / 0.05) * 150));
	}
	
	@Override
	public void onMoveToFinished(Npc npc, int x, int y, int z)
	{
		npc.getAI().addCastDesire(npc, 4238, 1, 1000000);
	}
}
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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.LV3Monster;

import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.skills.L2Skill;

public class LV3PartyPrivateMonster extends LV3Monster
{
	public LV3PartyPrivateMonster()
	{
		super("ai/individual/Monster/LV3Monster");
	}
	
	public LV3PartyPrivateMonster(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		27261,
		27291,
		27268
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._param1 = npc.getMaster()._param1;
		npc._param2 = npc.getMaster()._param2;
		npc._param3 = npc.getMaster()._param3;
		
		startQuestTimer("3007", npc, null, 5000);
		
		super.onCreated(npc);
	}
	
	@Override
	public void onPartyAttacked(Npc caller, Npc called, Creature target, int damage)
	{
		if (target instanceof Playable)
			called.getAI().addAttackDesire(target, (((damage * 1.0) / called.getStatus().getMaxHp()) / 0.050000) * 50);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (!npc.hasMaster() || npc.getMaster().isDead())
		{
			if (attacker instanceof Playable)
			{
				if (damage == 0)
					damage = 1;
				
				npc.getAI().addAttackDesire(attacker, ((1.0 * damage) / (npc.getStatus().getLevel() + 7)) * 100);
			}
		}
		else if (attacker instanceof Playable)
		{
			if (damage == 0)
				damage = 1;
			
			npc.getAI().addAttackDesire(attacker, ((1.0 * damage) / (npc.getStatus().getLevel() + 7)) * 10);
		}
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
			called.getAI().addAttackDesire(attacker, ((1.0 * damage) / (called.getStatus().getLevel() + 7)) * 30);
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		if (skill.getAggroPoints() > 0 && (!npc.hasMaster() || npc.getMaster().isDead()) && npc.getAttack().isAttackingNow() && targets.length > 0 && npc.getAI().getTopDesireTarget() == targets[0])
		{
			final int i0 = skill.getAggroPoints();
			double f0 = getHateRatio(npc, caster);
			f0 = (((1.000000 * i0) / (npc.getStatus().getLevel() + 7)) + ((f0 / 100) * ((1.000000 * i0) / (npc.getStatus().getLevel() + 7))));
			npc.getAI().addAttackDesire(caster, (f0 * 150));
		}
	}
	
	@Override
	public void onPartyDied(Npc caller, Npc called)
	{
		if (caller == called.getMaster())
			called.deleteMe();
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("1005"))
		{
			if (npc.hasMaster() && !npc.isInCombat() && !npc.isInMyTerritory())
			{
				npc.teleportTo(npc.getSpawnLocation(), 0);
				npc.removeAllAttackDesire();
			}
			startQuestTimer("1005", npc, player, 120000);
		}
		else if (name.equalsIgnoreCase("3007"))
		{
			if (!npc.hasMaster() || npc.getMaster().isDead())
				npc.deleteMe();
			else
				startQuestTimer("3007", npc, player, 5000);
		}
		return super.onTimer(name, npc, player);
	}
}
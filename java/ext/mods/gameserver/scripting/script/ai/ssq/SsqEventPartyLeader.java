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
package ext.mods.gameserver.scripting.script.ai.ssq;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.taskmanager.GameTimeTaskManager;

public class SsqEventPartyLeader extends SsqPartyLeader
{
	public SsqEventPartyLeader()
	{
		super("ai/ssq");
	}
	
	public SsqEventPartyLeader(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		18009,
		18019,
		18029,
		18039,
		18049,
		18059,
		18069,
		18079,
		18089,
		18099
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._flag = npc._param1;
		
		int i0 = 60 * 5 - (GameTimeTaskManager.getInstance().getCurrentTick() - npc._param2);
		if (i0 < 1)
		{
			npc.deleteMe();
			return;
		}
		
		startQuestTimer("2000", npc, null, i0 * 1000);
		startQuestTimer("2002", npc, null, 5000);
		
		super.onCreated(npc);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
		{
			if (creature instanceof Playable && npc.getSpawn().isInMyTerritory(creature))
				npc.getAI().addAttackDesire(creature, 200);
		}
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("2000"))
		{
			if (npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
				npc.deleteMe();
		}
		
		if (name.equalsIgnoreCase("2002"))
		{
			npc.lookNeighbor(500);
			startQuestTimer("2001", npc, null, 5000);
		}
		
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (!npc.getSpawn().isInMyTerritory(attacker))
			return;
		
		if (Rnd.get(100) < 10 && npc.distance2D(attacker) > 100)
			npc.getAI().addCastDesire(attacker, getNpcSkillByType(npc, NpcSkillType.DD_MAGIC), 1000000000);
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onPartyAttacked(Npc caller, Npc called, Creature target, int damage)
	{
		if (!called.getSpawn().isInMyTerritory(target))
			return;
		
		if (caller != called && called._param1 == caller._flag)
		{
			if (target instanceof Playable)
				return;
			
			if (damage == 0)
				damage = 1;
			
			called.getAI().addAttackDesire(target, (int) (((1.0 * damage) / (called.getStatus().getLevel() + 7)) * damage * caller._weightPoint * 10));
		}
		
		if (called.getStatus().getHpRatio() * 100 < 50 && called._i_ai0 == 0)
		{
			broadcastScriptEvent(called, 10004, called._param1, 1500);
			called._i_ai0 = 1;
		}
	}
	
	@Override
	public void onPartyDied(Npc caller, Npc called)
	{
		if (caller == called)
			broadcastScriptEvent(called, 10005, called._param1, 1500);
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		if (killer instanceof Player player)
		{
			if (npc._i_ai1 == 1)
				ssqEventGiveItem(npc, player, 3);
			else
				ssqEventGiveItem(npc, player, 15);
		}
	}
}
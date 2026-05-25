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
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.skills.L2Skill;

public class SsqEventSlowType extends SsqEventBasicWarrior
{
	public SsqEventSlowType()
	{
		super("ai/ssq");
	}
	
	public SsqEventSlowType(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		18012,
		18014,
		18022,
		18024,
		18032,
		18034,
		18042,
		18044,
		18052,
		18054,
		18062,
		18064,
		18072,
		18074,
		18082,
		18084,
		18092,
		18094,
		18102,
		18104
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		
		startQuestTimer("2002", npc, null, 5000);
		super.onCreated(npc);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("2002"))
		{
			npc.lookNeighbor(600);
			startQuestTimer("2002", npc, null, 5000);
		}
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (!npc.getSpawn().isInMyTerritory(attacker))
			return;
		
		int i0 = 0;
		
		if (attacker instanceof Playable)
			npc.getAI().addAttackDesire(attacker, 1, 100);
		
		if (getNpcIntAIParam(npc, "isStrong") == 1)
			i0 = 40;
		else
			i0 = 3;
		
		if (attacker instanceof Playable)
		{
			final Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
			if (mostHated != null)
			{
				if (Rnd.get(100) < i0 && mostHated == attacker)
					npc.getAI().addCastDesire(attacker, getNpcSkillByType(npc, NpcSkillType.DD_MAGIC), 1000000000);
			}
		}
		
		if (Rnd.get(100) < 10 && attacker instanceof Player && npc.hasMaster() && !npc.getMaster().isDead())
			npc.sendScriptEvent(10012, npc.getObjectId(), 0);
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		if (Rnd.get(100) < 10 && attacker instanceof Player && called.hasMaster() && !called.getMaster().isDead())
			called.sendScriptEvent(10012, called.getObjectId(), 0);
		
		super.onClanAttacked(caller, called, attacker, damage, skill);
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
	public void onScriptEvent(Npc npc, int eventId, int arg1, int arg2)
	{
		final Creature creature = (Creature) World.getInstance().getObject(arg2);
		if (creature != null)
		{
			if (npc.getSpawn().isInMyTerritory(creature))
			{
				if (eventId == 10007)
				{
					if (npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK && npc.getAI().getCurrentIntention().getType() != IntentionType.CAST)
						npc.getAI().addMoveToDesire(new Location(npc._param1, arg1, npc.getZ()), 50);
				}
				
				if (eventId == 10013)
				{
					final Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
					final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
					if (mostHated != null && topDesireTarget instanceof Playable)
					{
						broadcastScriptEvent(npc, 10014, topDesireTarget.getObjectId(), 800);
						
						switch (Rnd.get(3))
						{
							case 0:
								npc.broadcastNpcSay(NpcStringId.ID_1000291, topDesireTarget.getName());
								break;
							case 1:
								npc.broadcastNpcSay(NpcStringId.ID_1000398, topDesireTarget.getName());
								break;
							case 2:
								npc.broadcastNpcSay(NpcStringId.ID_1000399, topDesireTarget.getName());
								break;
						}
					}
				}
			}
		}
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		broadcastScriptEvent(npc, 10008, npc._param3, 1500);
		if (killer instanceof Player player)
		{
			if (npc._i_ai1 == 1)
				ssqEventGiveItem(npc, player, 3);
			else if (getNpcIntAIParam(npc, "IsStrong") == 0)
				ssqEventGiveItem(npc, player, 10);
			else
				ssqEventGiveItem(npc, player, 15);
		}
	}
}
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

import ext.mods.commons.util.ArraysUtil;

import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.skills.L2Skill;

public class SsqEventPartyPrivate extends SsqEventBasicWarrior
{
	public SsqEventPartyPrivate()
	{
		super("ai/ssq");
	}
	
	public SsqEventPartyPrivate(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		18010,
		18020,
		18030,
		18040,
		18050,
		18060,
		18070,
		18080,
		18090,
		18100
	};
	
	@Override
	public void onNoDesire(Npc npc)
	{
		if (npc.hasMaster() && !npc.getMaster().isDead())
			npc.getAI().addFollowDesire(npc.getMaster(), 5);
		else
			npc.getAI().addWanderDesire(5, 5);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		startQuestTimer("2002", npc, null, 5000);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("2002"))
		{
			if (!npc.hasMaster() || npc.getMaster().isDead())
			{
				if (npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
					npc.deleteMe();
				else
				{
					final Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
					if (mostHated != null)
					{
						if (!npc.getSpawn().isInMyTerritory(mostHated))
							npc.removeAttackDesire(mostHated);
					}
					startQuestTimer("2002", npc, null, 5000);
				}
			}
			else
				startQuestTimer("2002", npc, null, 5000);
		}
		return null;
	}
	
	@Override
	public void onPartyDied(Npc caller, Npc called)
	{
		
	}
	
	@Override
	public void onPartyAttacked(Npc caller, Npc called, Creature target, int damage)
	{
		if (!called.getSpawn().isInMyTerritory(target))
			return;
		
		if (called.distance2D(caller) < 300)
		{
			if (target instanceof Playable)
			{
				if (damage == 0)
					damage = 1;
				
				called.getAI().addAttackDesire(target, (int) (((1.0 * damage) / (called.getStatus().getLevel() + 7)) * 100));
			}
		}
		
		super.onPartyAttacked(caller, called, target, damage);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (!npc.getSpawn().isInMyTerritory(attacker))
			return;
		
		if (!npc.hasMaster() || npc.getMaster().isDead())
		{
			if (attacker instanceof Playable)
			{
				if (damage == 0)
					damage = 1;
				
				npc.getAI().addAttackDesire(attacker, (int) (((1.0 * damage) / (npc.getStatus().getLevel() + 7)) * 100));
			}
		}
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		if (!npc.hasMaster() || npc.getMaster().isDead())
		{
			final Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
			
			if ((skill.getAggroPoints() > 0 || skill.getPower() > 0 || skill.isOffensive()) && npc.getAI().getCurrentIntention().getType() == IntentionType.ATTACK && ArraysUtil.contains(targets, mostHated))
			{
				double i0 = Math.max(Math.max(skill.getAggroPoints(), skill.getPower(targets[0])), 20);
				double hateRatio = getHateRatio(npc, caster);
				hateRatio = (((1.0 * i0) / (npc.getStatus().getLevel() + 7)) + ((hateRatio / 100) * ((1.0 * i0) / (npc.getStatus().getLevel() + 7))));
				
				npc.getAI().addAttackDesire(caster, hateRatio * 150);
			}
			
			if (caster == mostHated && npc.getStatus().getHpRatio() != 1.0 && npc.getMove().getGeoPathFailCount() >= 10)
			{
				npc.abortAll(false);
				npc.teleportTo(caster.getX(), caster.getY(), caster.getZ(), 0);
			}
		}
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		if (killer instanceof Player player)
		{
			if (npc._i_ai1 == 1)
				ssqEventGiveItem(npc, player, 3);
			else
				ssqEventGiveItem(npc, player, 8);
		}
	}
}
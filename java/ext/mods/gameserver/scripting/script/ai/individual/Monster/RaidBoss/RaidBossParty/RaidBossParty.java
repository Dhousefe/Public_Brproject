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

import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.scripting.script.ai.individual.Monster.RaidBoss.RaidBossStandard;

public class RaidBossParty extends RaidBossStandard
{
	public RaidBossParty()
	{
		super("ai/individual/Monster/RaidBoss/RaidBossAlone/RaidBossParty");
	}
	
	public RaidBossParty(String descr)
	{
		super(descr);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._weightPoint = 10;
		npc.getMinions().clear();
		
		createPrivates(npc);
		
		super.onCreated(npc);
	}
	
	@Override
	public void onPartyAttacked(Npc caller, Npc called, Creature target, int damage)
	{
		if (caller != called)
		{
			if (target.getStatus().getLevel() <= (called.getStatus().getLevel() + 8))
			{
				if (target instanceof Playable)
				{
					if (damage == 0)
						damage = 1;
					
					called.getAI().addAttackDesire(target, (int) (((1.0 * damage) / (called.getStatus().getLevel() + 7)) * 20000));
				}
			}
			
			if (called.getMove().getGeoPathFailCount() > (8 + Rnd.get(13)))
			{
				final Creature topDesireTarget = called.getAI().getTopDesireTarget();
				if (topDesireTarget != null && called.distance2D(topDesireTarget) < 1000)
					called.teleportTo(topDesireTarget.getPosition(), 0);
				else
				{
					called.removeAllAttackDesire();
					
					if (target instanceof Playable)
					{
						if (damage == 0)
							damage = 1;
						
						called.getAI().addAttackDesire(target, (int) (((1.0 * damage) / (called.getStatus().getLevel() + 7)) * 20000));
					}
					called.teleportTo(target.getPosition(), 0);
				}
			}
		}
		
		if (called.isInsideZone(ZoneId.PEACE))
		{
			called.teleportTo(called.getSpawnLocation(), 0);
			called.removeAllAttackDesire();
		}
	}
	
	@Override
	public void onPartyDied(Npc caller, Npc called)
	{
		if (caller != called && called.isMaster() && !called.isDead())
			caller.scheduleRespawn((caller.getSpawn().getRespawnDelay() * 1000));
	}
}
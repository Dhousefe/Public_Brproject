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

import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.skills.L2Skill;

public class RaidBossType3AnaisAggressive extends RaidBossType3
{
	public RaidBossType3AnaisAggressive()
	{
		super("ai/individual/Monster/RaidBoss/RaidBossAlone/RaidBossParty/RaidBossType3");
	}
	
	public RaidBossType3AnaisAggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		29096
	};
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (!(creature instanceof Playable))
			return;
		
		if (npc.isInMyTerritory())
			npc.getAI().addAttackDesire(creature, 200);
	}
	
	@Override
	public void onNoDesire(Npc npc)
	{
		if (!npc.isInMyTerritory())
		{
			npc.teleportTo(new Location(112800, -76160, 10), 0);
			startQuestTimer("3001", npc, null, 1000);
		}
		super.onNoDesire(npc);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (!npc.isInMyTerritory())
		{
			npc.teleportTo(new Location(112800, -76160, 10), 0);
			startQuestTimer("3001", npc, null, 1000);
		}
		
		super.onAttacked(npc, attacker, damage, null);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("3001"))
		{
			npc.removeAllAttackDesire();
			npc.getAI().getHateList().cleanAllHate();
		}
		
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onPartyAttacked(Npc caller, Npc called, Creature target, int damage)
	{
		if (!caller.isInMyTerritory() && !called.isInMyTerritory())
		{
			called.teleportTo(new Location(112800, -76160, 10), 0);
			startQuestTimer("3001", called, null, 1000);
		}
		if (!caller.isInMyTerritory())
			return;
		
		super.onPartyAttacked(caller, called, target, damage);
	}
	
	@Override
	public void onUseSkillFinished(Npc npc, Creature creature, L2Skill skill, boolean success)
	{
		if (!npc.isInMyTerritory())
		{
			npc.teleportTo(new Location(112800, -76160, 10), 0);
			startQuestTimer("3001", npc, null, 1000);
		}
	}
}
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

import ext.mods.Config;
import ext.mods.gameserver.enums.actors.ClassType;
import ext.mods.gameserver.model.TimeAttackEventRoom;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.scripting.script.ai.individual.DefaultNpc;
import ext.mods.gameserver.skills.L2Skill;

public class SsqEventPresentNpc extends DefaultNpc
{
	public SsqEventPresentNpc()
	{
		super("ai/ssq");
	}
	
	public SsqEventPresentNpc(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		18109,
		18110,
		18111,
		18112,
		18113,
		18114,
		18115,
		18116,
		18117,
		18118
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai1 = 0;
		startQuestTimer("1001", npc, null, 2 * 60 * 1000);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("1001"))
			npc.deleteMe();
		
		return null;
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (getNpcIntAIParam(npc, "RoomIndex") != 0 && getNpcIntAIParam(npc, "SSQPart") != 0)
		{
			if (!npc.getSpawn().isInMyTerritory(attacker))
				npc._i_ai1 = 1;
			else
			{
				Party party0 = TimeAttackEventRoom.getInstance().getParty(getNpcIntAIParam(npc, "RoomIndex"), getNpcIntAIParam(npc, "SSQPart"));
				Party party1 = attacker.getParty();
				if (party0 == null || party1 == null)
					npc._i_ai1 = 1;
				else if (party0 != party1 && attacker instanceof Player)
					npc._i_ai1 = 1;
			}
		}
		else
			npc.broadcastNpcSay("Puncture: The monster’s affiliation is unclear.");
		
		if (skill != null && skill.getId() != 0)
			npc.getAI().addFleeDesire(attacker, Config.MAX_DRIFT_RANGE, 100);
		else if (Rnd.get(100) < 30)
			npc.getAI().addFleeDesire(attacker, Config.MAX_DRIFT_RANGE, 100);
		
		final Player player = attacker.getActingPlayer();
		if (player != null && player.getClassId().getType() == ClassType.MYSTIC)
			npc._i_ai1 = 1;
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		if (skill != null && skill.getId() != 0)
			called.getAI().addFleeDesire(attacker, Config.MAX_DRIFT_RANGE, 100);
		else if (Rnd.get(100) < 30)
			called.getAI().addFleeDesire(attacker, Config.MAX_DRIFT_RANGE, 100);
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		if (killer instanceof Player player)
		{
			if (npc._i_ai1 == 1)
				ssqEventGiveItem(npc, player, 3);
			else
				ssqEventGiveItem(npc, player, 30);
		}
	}
}
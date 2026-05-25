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
package ext.mods.gameserver.scripting.script.ai.individual;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.EventHandler;
import ext.mods.gameserver.enums.actors.ClassType;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.scripting.Quest;

/**
 * This Npc buffs or debuffs (with an equal 50% chance) the {@link Player} upon interaction, based on {@link Player}'s {@link ClassType}.<br>
 * <br>
 * The 30 seconds timer only affect chat ability.
 */
public class DaimonTheWhiteEyed extends Quest
{
	private static final NpcStringId[] DEBUFF_CHAT =
	{
		NpcStringId.ID_1000458,
		NpcStringId.ID_1000459,
		NpcStringId.ID_1000460
	};
	
	private static final NpcStringId[] BUFF_CHAT =
	{
		NpcStringId.ID_1000461,
		NpcStringId.ID_1000462,
		NpcStringId.ID_1000463
	};
	
	public DaimonTheWhiteEyed()
	{
		super(-1, "ai/individual");
		
		addEventIds(31705, EventHandler.CREATED, EventHandler.FIRST_TALK);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("6543"))
			npc._i_ai0 = 0;
		
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		npc.getAI().addMoveRouteDesire("argos_daemon_roaming", 2000);
		
		npc._i_ai0 = 0;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		if (Rnd.nextBoolean())
		{
			if (npc._i_ai0 == 0)
			{
				npc.broadcastNpcSay(Rnd.get(DEBUFF_CHAT));
				npc._i_ai0 = 1;
				
				startQuestTimer("6543", npc, null, 30000);
			}
			
			if (player.getClassId().getType() == ClassType.FIGHTER)
				npc.getAI().addCastDesire(player, 1206, 19, 1000000);
			else
				npc.getAI().addCastDesire(player, 1083, 17, 1000000);
		}
		else
		{
			if (npc._i_ai0 == 0)
			{
				npc.broadcastNpcSay(Rnd.get(BUFF_CHAT));
				npc._i_ai0 = 1;
				
				startQuestTimer("6543", npc, null, 30000);
			}
			
			if (player.getClassId().getType() == ClassType.FIGHTER)
				npc.getAI().addCastDesire(player, 1086, 2, 1000000);
			else
				npc.getAI().addCastDesire(player, 1059, 3, 1000000);
		}
		return null;
	}
}
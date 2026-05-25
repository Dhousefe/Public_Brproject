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
package ext.mods.gameserver.scripting.script.feature;

import ext.mods.commons.lang.StringUtil;

import ext.mods.gameserver.enums.EventHandler;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.scripting.Quest;

public class BlackJudge extends Quest
{
	private static final IntIntHolder[] DEATH_PENALTIES =
	{
		new IntIntHolder(76, 144000),
		new IntIntHolder(61, 86400),
		new IntIntHolder(52, 50400),
		new IntIntHolder(40, 25200),
		new IntIntHolder(20, 8640),
		new IntIntHolder(1, 3600)
	};
	
	public BlackJudge()
	{
		super(-1, "feature");
		
		addEventIds(30981, EventHandler.FIRST_TALK, EventHandler.TALKED);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		if (event.equalsIgnoreCase("test_dp"))
		{
			final int playerLevel = player.getStatus().getLevel();
			if (playerLevel >= 76)
				event = "black_judge007.htm";
			else if (playerLevel >= 61)
				event = "black_judge006.htm";
			else if (playerLevel >= 52)
				event = "black_judge005.htm";
			else if (playerLevel >= 40)
				event = "black_judge004.htm";
			else if (playerLevel >= 20)
				event = "black_judge003.htm";
			else if (playerLevel >= 1)
				event = "black_judge002.htm";
		}
		else if (event.startsWith("remove_dp"))
		{
			if (player.getDeathPenaltyBuffLevel() <= 0)
				event = "black_judge009.htm";
			else
			{
				final String[] split = event.split(" ");
				if (split.length < 2 || !StringUtil.isDigit(split[1]))
					return null;
				
				final IntIntHolder deathPenalty = DEATH_PENALTIES[Integer.parseInt(split[1])];
				if (player.getStatus().getLevel() < deathPenalty.getId())
					return null;
				
				if (player.getInventory().getAdena() < deathPenalty.getValue())
					event = "black_judge008.htm";
				else
				{
					takeItems(player, 57, deathPenalty.getValue());
					
					player.reduceDeathPenaltyBuffLevel();
					return null;
				}
			}
		}
		return event;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return "black_judge001.htm";
	}
}
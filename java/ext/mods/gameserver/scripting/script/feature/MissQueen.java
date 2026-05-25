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

import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.Quest;

public class MissQueen extends Quest
{
	private static final int TRAINEES_COUPON = 7832;
	private static final int TRAVELERS_COUPON = 7833;
	
	public MissQueen()
	{
		super(-1, "feature");
		
		addTalkId(31760);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		
		if (event.equalsIgnoreCase("newbie_coupon"))
		{
			if (player.getClassId().getLevel() == 0 && player.getStatus().getLevel() >= 6 && player.getStatus().getLevel() <= 25 && player.getPkKills() <= 0)
			{
				if (player.getMemos().containsKey("MissQueen_Trainees"))
					htmltext = "31760-01.htm";
				else
				{
					htmltext = "31760-02.htm";
					player.getMemos().set("MissQueen_Trainees", true);
					giveItems(player, TRAINEES_COUPON, 1);
				}
			}
			else
				htmltext = "31760-03.htm";
		}
		else if (event.equalsIgnoreCase("traveller_coupon"))
		{
			if (player.getClassId().getLevel() == 1 && player.getStatus().getLevel() >= 6 && player.getStatus().getLevel() <= 25 && player.getPkKills() <= 0)
			{
				if (player.getMemos().containsKey("MissQueen_Traveler"))
					htmltext = "31760-04.htm";
				else
				{
					htmltext = "31760-05.htm";
					player.getMemos().set("MissQueen_Traveler", true);
					giveItems(player, TRAVELERS_COUPON, 1);
				}
			}
			else
				htmltext = "31760-06.htm";
		}
		
		return htmltext;
	}
}
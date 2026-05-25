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
package ext.mods.gameserver.scripting.script.ai.boss.baium;

import ext.mods.Config;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.memo.GlobalMemo;
import ext.mods.gameserver.scripting.Quest;

public class BaiumAngelicVortex extends Quest
{
	public BaiumAngelicVortex()
	{
		super(-1, "ai/boss/baium");
		
		addFirstTalkId(31862);
		addTalkId(31862);
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		String htmltext = "";
		
		htmltext = "31862-00.htm";
		
		return htmltext;
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = "";
		
		if (event.equalsIgnoreCase("baium_story"))
		{
			htmltext = "31862-02.htm";
			
			return htmltext;
		}
		else if (event.equalsIgnoreCase("enter"))
		{
			Creature c0 = GlobalMemo.getInstance().getCreature("2");
			
			if (player.isFlying())
				htmltext = "31862-05.htm";
			else if (c0 != null && !c0.isDead())
			{
				if (((Npc) c0).getSpawn().getSpawnData().getDBValue() == 0)
				{
					if (Config.NEED_ITEM_BAIUM)
					{
						if (!player.getInventory().hasItems(4295))
						{
							htmltext = "31862-03.htm";
							return htmltext;
						}
						
						takeItems(player, 4295, 1);
					}
					
					player.teleportTo(114077, 15882, 10078, 0);
				}
				else
					htmltext = "31862-01.htm";
			}
			else
				htmltext = "31862-04.htm";
		}
		return htmltext;
	}
}

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
package ext.mods.gameserver.scripting.script.ai.boss.antharas;

import ext.mods.commons.random.Rnd;

import ext.mods.Config;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.memo.GlobalMemo;
import ext.mods.gameserver.scripting.Quest;

public class HeartOfWarding extends Quest
{
	private static final int GM_ID1 = 1;
	private static final int GM_ID2 = 9;
	private static final int GM_ID3 = 10;
	
	public HeartOfWarding()
	{
		super(-1, "ai/boss/antharas");
		
		addFirstTalkId(13001);
		addTalkId(13001);
		addCreated(13001);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai0 = 0;
		npc._i_ai1 = 0;
		
		super.onCreated(npc);
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return "13001-01.htm";
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("3001"))
		{
			if (npc._i_ai1 <= 90)
			{
				final Npc c_ai0 = (Npc) npc._c_ai0;
				if (c_ai0 != null)
					c_ai0.sendScriptEvent(0, 0, 0);
			}
			else if (npc._i_ai1 <= 135)
			{
				final Npc c_ai1 = (Npc) npc._c_ai1;
				if (c_ai1 != null)
					c_ai1.sendScriptEvent(0, 0, 0);
			}
			else
			{
				final Npc c_ai2 = (Npc) npc._c_ai2;
				if (c_ai2 != null)
					c_ai2.sendScriptEvent(0, 0, 0);
			}
			
			npc._i_ai0 = 0;
			npc._i_ai1 = 0;
		}
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = "";
		
		if (event.equalsIgnoreCase("enter"))
		{
			final int i0 = GlobalMemo.getInstance().getInteger(String.valueOf(GM_ID1), -1);
			final int i1 = GlobalMemo.getInstance().getInteger(String.valueOf(GM_ID2), -1);
			final int i2 = GlobalMemo.getInstance().getInteger(String.valueOf(GM_ID3), -1);
			
			if (i0 != -1 && i1 != -1 && i2 != -1)
			{
				final Npc c0 = (Npc) GlobalMemo.getInstance().getCreature(String.valueOf(GM_ID1));
				final Npc c1 = (Npc) GlobalMemo.getInstance().getCreature(String.valueOf(GM_ID2));
				final Npc c2 = (Npc) GlobalMemo.getInstance().getCreature(String.valueOf(GM_ID3));
				
				if (!c0.isDead() && !c1.isDead() && !c2.isDead())
				{
					final int c0Value = c0.getSpawn().getSpawnData().getDBValue();
					final int c1Value = c1.getSpawn().getSpawnData().getDBValue();
					final int c2Value = c2.getSpawn().getSpawnData().getDBValue();
					
					if ((c0Value == 0 || c0Value == 1) && ((c1Value == 0 || c1Value == 1) && (c2Value == 0 || c2Value == 1)))
					{
						if (Config.NEED_ITEM_ANTHARAS)
						{
							if (!player.getInventory().hasItems(3865))
							{
								htmltext = "13001-04.htm";
								return htmltext;
							}
							
							takeItems(player, 3865, 1);
						}
						
						final int i3 = (179700 + Rnd.get(700));
						final int i4 = (113800 + Rnd.get(2100));
						
						player.teleportTo(i3, i4, -7709, 0);
						
						npc._i_ai1++;
						
						if (npc._i_ai0 == 0)
						{
							startQuestTimer("3001", npc, null, Config.WAIT_TIME_ANTHARAS);
							
							npc._c_ai0 = c0;
							npc._c_ai1 = c1;
							npc._c_ai2 = c2;
							
							npc._i_ai0 = 1;
						}
					}
					else if (c0Value == 2 || c0Value == 3 || c1Value == 2 || c1Value == 3 || c2Value == 2 || c2Value == 3)
						htmltext = "13001-03.htm";
				}
				else
					htmltext = "13001-02.htm";
			}
			else
				htmltext = "13001-02.htm";
		}
		return htmltext;
	}
}
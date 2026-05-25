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

import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.memo.GlobalMemo;
import ext.mods.gameserver.scripting.Quest;

public class BaiumStone extends Quest
{
	private final int GM_ID = 2;
	private final int BAIUM_STONE = 29025;
	
	public BaiumStone()
	{
		super(-1, "ai/boss/baium");
		
		addCreated(BAIUM_STONE);
		addTalkId(BAIUM_STONE);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai0 = 0;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		if (npc._i_ai0 == 0)
		{
			npc._i_ai0 = 1;
			
			final int i0 = GlobalMemo.getInstance().getInteger(String.valueOf(GM_ID));
			if (i0 != -1)
			{
				final Npc c0 = (Npc) World.getInstance().getObject(i0);
				if (c0 != null)
					c0.sendScriptEvent(10025, player.getObjectId(), 0);
			}
			npc.deleteMe();
		}
		
		return null;
	}
}
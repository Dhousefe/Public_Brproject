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
package ext.mods.gameserver.scripting.script.teleport;

import ext.mods.gameserver.data.xml.DoorData;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.Quest;

public class PaganTeleporter extends Quest
{
	private static final int VISITOR_MARK = 8064;
	private static final int FADED_VISITOR_MARK = 8065;
	private static final int PAGAN_MARK = 8067;
	
	public PaganTeleporter()
	{
		super(-1, "teleport");
		
		addTalkId(32034, 32035, 32036, 32037, 32039, 32040);
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = "";
		
		switch (npc.getNpcId())
		{
			case 32034:
				if (player.getInventory().hasItems(VISITOR_MARK) || player.getInventory().hasItems(FADED_VISITOR_MARK) || player.getInventory().hasItems(PAGAN_MARK))
				{
					if (player.getActingPlayer().destroyItemByItemId(VISITOR_MARK, 1, false))
						player.getActingPlayer().addItem(FADED_VISITOR_MARK, 1, true);
					
					DoorData.getInstance().getDoor(19160001).openMe();
					htmltext = "FadedMark.htm";
				}
				else
					htmltext = "32034-1.htm";
				break;
			
			case 32035:
				DoorData.getInstance().getDoor(19160001).openMe();
				htmltext = "FadedMark.htm";
				break;
			
			case 32036:
				if (!player.getInventory().hasItems(PAGAN_MARK))
					htmltext = "32036-1.htm";
				else
				{
					DoorData.getInstance().getDoor(19160010).openMe();
					DoorData.getInstance().getDoor(19160011).openMe();
					htmltext = "32036-2.htm";
				}
				break;
			
			case 32037:
				DoorData.getInstance().getDoor(19160010).openMe();
				DoorData.getInstance().getDoor(19160011).openMe();
				htmltext = "FadedMark.htm";
				break;
			
			case 32039:
				player.teleportTo(34962, -49758, -763, 0);
				break;
			
			case 32040:
				player.teleportTo(-12766, -35840, -10856, 0);
				break;
		}
		return htmltext;
	}
}
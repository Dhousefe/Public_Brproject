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

import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;

public class SacrificialVictim extends DefaultNpc
{
	public SacrificialVictim()
	{
		super("ai/individual");
	}
	
	public SacrificialVictim(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		32038
	};
	
	@Override
	public void onScriptEvent(Npc npc, int eventId, int arg1, int arg2)
	{
		if (eventId == 3)
			startQuestTimer("998", npc, null, 10000);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("998"))
		{
			npc.getAI().addSocialDesire(1, 7000, 1000);
			startQuestTimer("999", npc, player, 5000);
		}
		else if (name.equalsIgnoreCase("999"))
		{
			createOnePrivateEx(npc, 22145, npc.getX(), npc.getY(), npc.getZ(), 0, 0, true);
			npc.deleteMe();
		}
		
		return null;
	}
}
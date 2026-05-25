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
package ext.mods.gameserver.scripting.script.ai.individual.RoyalRushDefaultNpc;

import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;

public class RoyalRushKeyBox extends RoyalRushDefaultNpc
{
	public RoyalRushKeyBox()
	{
		super("ai/individual/RoyalRushDefaultNpc");
	}
	
	public RoyalRushKeyBox(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		31455,
		31456,
		31457,
		31458,
		31459,
		31460,
		31461,
		31462,
		31463,
		31464,
		31465,
		31466,
		31467
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai0 = 0;
		
		super.onCreated(npc);
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		if (npc._i_ai0 == 0)
		{
			giveItems(player, 7260, 1);
			npc._i_ai0 = 1;
		}
		
		npc.deleteMe();
		
		return null;
	}
}
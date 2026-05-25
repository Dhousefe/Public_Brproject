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
import ext.mods.gameserver.network.serverpackets.PlaySound;
import ext.mods.gameserver.network.serverpackets.SpecialCamera;

public class TriolCamera extends DefaultNpc
{
	public TriolCamera()
	{
		super("ai/individual");
	}
	
	public TriolCamera(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		13015
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai0 = 0;
	}
	
	@Override
	public void onScriptEvent(Npc npc, int eventId, int arg1, int arg2)
	{
		if (eventId == 3)
		{
			npc.broadcastPacket(new PlaySound(1, "BS04_A", npc));
			npc._i_ai0 = arg1;
			startQuestTimer("1001", npc, null, 1000);
		}
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("1001"))
		{
			SpecialCamera(npc, 1500, 88, 89, 0, 5000, 10000, 0, 110, 1, 0, 1);
			startQuestTimer("1002", npc, null, 300);
		}
		else if (name.equalsIgnoreCase("1002"))
		{
			SpecialCamera(npc, 1500, 88, 89, 0, 5000, 10000, 0, 110, 1, 0, 1);
			startQuestTimer("1003", npc, null, 300);
		}
		else if (name.equalsIgnoreCase("1003"))
		{
			SpecialCamera(npc, 450, 88, 3, 5500, 5000, 10000, 0, 350, 1, 0, 1);
			startQuestTimer("1004", npc, null, 9400);
		}
		else if (name.equalsIgnoreCase("1004"))
		{
			SpecialCamera(npc, 500, 88, 4, 5000, 5000, 10000, 0, 0, 1, 0, 1);
			startQuestTimer("1005", npc, null, 5000);
		}
		else if (name.equalsIgnoreCase("1005"))
		{
			SpecialCamera(npc, 3000, 88, 4, 6000, 5000, 6900, 0, 15, 1, 0, 1);
			startQuestTimer("1006", npc, null, 6000);
		}
		else if (name.equalsIgnoreCase("1006"))
		{
			if (npc._i_ai0 != 0)
				broadcastScriptEvent(npc, 4, npc._i_ai0, 4000);
		}
		
		return super.onTimer(name, npc, player);
	}
	
	public static void SpecialCamera(Npc npc, int force, int angle1, int angle2, int time, int range, int duration, int relYaw, int relPitch, int isWide, int relAngle, int unk)
	{
		npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), force, angle1, angle2, time, duration, relYaw, relPitch, isWide, relAngle, unk));
	}
}
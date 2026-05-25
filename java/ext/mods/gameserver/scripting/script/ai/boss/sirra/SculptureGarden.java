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
package ext.mods.gameserver.scripting.script.ai.boss.sirra;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.model.memo.GlobalMemo;
import ext.mods.gameserver.scripting.script.ai.individual.DefaultNpc;
import ext.mods.gameserver.skills.L2Skill;

public class SculptureGarden extends DefaultNpc
{
	private final L2Skill RESIST_COLD = SkillTable.getInstance().getInfo(4479, 1);
	
	public SculptureGarden()
	{
		super("ai/boss/sirra");
	}
	
	public SculptureGarden(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		32030
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		final Creature c0 = GlobalMemo.getInstance().getCreature("7");
		if (c0 == null)
			GlobalMemo.getInstance().set("7", npc.getObjectId());
		
		super.onCreated(npc);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (creature instanceof Player)
			npc._c_ai0 = creature;
		
		super.onSeeCreature(npc, creature);
	}
	
	@Override
	public void onScriptEvent(Npc npc, int eventId, int arg1, int arg2)
	{
		if (eventId == 10027)
			npc.getSpawn().instantTeleportInMyTerritory(115792, -125760, -3373, 200);
		else if (eventId == 11038)
		{
			npc.lookNeighbor(1000);
			
			if (npc._c_ai0 != null)
			{
				final Party party0 = npc._c_ai0.getParty();
				if (party0 != null)
					for (Player partyMember : party0.getMembers())
						if (npc.getSpawn().isInMyTerritory(partyMember))
							callSkill(npc, partyMember, RESIST_COLD);
			}
		}
	}
}
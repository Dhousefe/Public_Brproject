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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.PartyPrivateWarrior;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.network.NpcStringId;

public class FreyaBear extends PartyPrivateWarrior
{
	public FreyaBear()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/PartyPrivateWarrior");
	}
	
	public FreyaBear(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		22103
	};
	
	@Override
	public void onScriptEvent(Npc npc, int eventId, int arg1, int arg2)
	{
		if (npc.isDead())
			return;
		
		if (eventId == 10002)
		{
			if (!npc.hasMaster())
				return;
			
			final Creature c0 = (Creature) World.getInstance().getObject(arg1);
			if (c0 == npc.getMaster())
			{
				final Creature c1 = (Creature) World.getInstance().getObject(npc.getMaster()._flag);
				
				final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
				if (topDesireTarget != null && c1 == topDesireTarget)
					return;
				
				switch (Rnd.get(4))
				{
					case 0:
						npc.broadcastNpcSay(NpcStringId.ID_1000292);
						break;
					
					case 1:
						npc.broadcastNpcSay(NpcStringId.ID_1000400);
						break;
					
					case 2:
						npc.broadcastNpcSay(NpcStringId.ID_1000401);
						break;
					
					case 3:
						npc.broadcastNpcSay(NpcStringId.ID_1000402);
						break;
				}
				
				if (c1 != null)
				{
					npc.getAI().getAggroList().cleanAllHate();
					npc.getAI().addAttackDesire(c1, 1000000);
				}
			}
		}
		else if (eventId == 10034)
			npc.getAI().addCastDesire(npc.getMaster(), getNpcSkillByType(npc, NpcSkillType.MAGIC_HEAL), 1000000);
		else if (eventId == 11039)
			npc.deleteMe();
	}
}
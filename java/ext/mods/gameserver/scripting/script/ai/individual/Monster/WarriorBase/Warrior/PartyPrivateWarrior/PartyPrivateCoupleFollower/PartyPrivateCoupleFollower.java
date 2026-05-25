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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.PartyPrivateWarrior.PartyPrivateCoupleFollower;

import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.PartyPrivateWarrior.PartyPrivateWarrior;

public class PartyPrivateCoupleFollower extends PartyPrivateWarrior
{
	public PartyPrivateCoupleFollower()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/PartyPrivateWarrior/PartyPrivateCoupleFollower");
	}
	
	public PartyPrivateCoupleFollower(String descr)
	{
		super(descr);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai0 = 0;
		
		super.onCreated(npc);
	}
	
	@Override
	public void onPartyDied(Npc caller, Npc called)
	{
		if ((!called.hasMaster() || called.getMaster().isDead()) && called._i_ai0 == 0 && called.getStatus().getHpRatio() > 0.7)
		{
			final Creature topDesireTarget = called.getAI().getTopDesireTarget();
			if (topDesireTarget != null)
			{
				final int silhouette = getNpcIntAIParam(called, "silhouette");
				final int x = called.getX();
				final int y = called.getY();
				final int z = called.getZ();
				
				createOnePrivateEx(called, silhouette, x + 10, y, z, 0, 0, false, 1000, topDesireTarget.getObjectId(), 0);
				createOnePrivateEx(called, silhouette, x, y + 10, z, 0, 0, false, 1000, topDesireTarget.getObjectId(), 0);
				createOnePrivateEx(called, silhouette, x + 5, y + 5, z, 0, 0, false, 1000, topDesireTarget.getObjectId(), 0);
				
				called._i_ai0 = 1;
			}
		}
	}
}
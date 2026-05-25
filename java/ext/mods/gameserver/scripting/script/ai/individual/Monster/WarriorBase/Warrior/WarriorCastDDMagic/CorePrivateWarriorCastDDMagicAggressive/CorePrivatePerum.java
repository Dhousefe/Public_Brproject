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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCastDDMagic.CorePrivateWarriorCastDDMagicAggressive;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;

public class CorePrivatePerum extends CorePrivateWarriorCastDDMagicAggressive
{
	public CorePrivatePerum()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCastDDMagic/CorePrivateWarriorCastDDMagicAggressive");
	}
	
	public CorePrivatePerum(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		29012,
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		startQuestTimer("4001", npc, null, 90000 + Rnd.get(240000));
		
		super.onCreated(npc);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("4001"))
		{
			if (Rnd.get(100) < 66)
				npc.getAI().getAggroList().randomizeAttack();
			
			startQuestTimer("4001", npc, null, 90000 + Rnd.get(240000));
		}
		
		return super.onTimer(name, npc, player);
	}
}
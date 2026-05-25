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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorPhysicalSpecial;

import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.taskmanager.GameTimeTaskManager;

public class WarriorPhysicalSpecialVelociraptor extends WarriorPhysicalSpecialAggressive
{
	public WarriorPhysicalSpecialVelociraptor()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorPhysicalSpecial");
	}
	
	public WarriorPhysicalSpecialVelociraptor(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		22218
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai4 = GameTimeTaskManager.getInstance().getCurrentTick();
		npc._i_quest0 = 0;
		
		super.onCreated(npc);
	}
	
	@Override
	public void onScriptEvent(Npc npc, int eventId, int arg1, int arg2)
	{
		if (eventId == 11049)
			npc._i_quest0 = 1;
		
		super.onScriptEvent(npc, eventId, arg1, arg2);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		npc._i_ai4 = GameTimeTaskManager.getInstance().getCurrentTick();
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		npc._i_ai4 = GameTimeTaskManager.getInstance().getCurrentTick();
		
		super.onSeeCreature(npc, creature);
	}
	
	@Override
	public void onNoDesire(Npc npc)
	{
		if (npc._i_quest0 == 1 && getElapsedTicks(npc._i_ai4) > 600)
			broadcastScriptEvent(npc, 11051, 0, 8000);
	}
}
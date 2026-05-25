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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior;

import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;

public class WarriorAggressiveImmediate extends Warrior
{
	public WarriorAggressiveImmediate()
	{
		super("ai/individual/Monster/WarriorBase/Warrior");
	}
	
	public WarriorAggressiveImmediate(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		27178,
		20780,
		27151,
		27056,
		27082,
		27160,
		27138,
		27149,
		27135,
		27172,
		27088,
		27162,
		27168,
		27169,
		27170,
		27164,
		27044,
		27045,
		27157,
		27083,
		27152,
		27092,
		27080,
		27034,
		27033,
		27032,
		27089,
		27136,
		27161,
		27144,
		27148,
		27145,
		27147,
		27146,
		27035,
		27134,
		27159,
		27158,
		27163,
		27043,
		27081,
		27143,
		27094,
		27137,
		27150,
		27180,
		27194
	};
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (!(creature instanceof Playable))
			return;
		
		if (npc.isInMyTerritory())
			npc.getAI().addAttackDesire(creature, 200);
		
		super.onSeeCreature(npc, creature);
	}
}
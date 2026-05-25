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
package ext.mods.gameserver.scripting.script.ai.individual.Guard;

import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.skills.L2Skill;

public class GuardFixed extends GuardStand
{
	public GuardFixed()
	{
		super("ai/individual/Guard");
	}
	
	public GuardFixed(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		30040,
		30041,
		30044,
		30045,
		30072,
		30073,
		30075,
		30122,
		30123,
		30197,
		30199,
		30200,
		30201,
		30217,
		30218,
		30220,
		30331,
		30334,
		30335,
		30336,
		30338,
		30346,
		30349,
		30355,
		30356,
		30379,
		30380,
		30381,
		30383,
		30385,
		30465,
		30478,
		30541,
		30542,
		30546,
		30547,
		30577,
		30578,
		30579,
		30581,
		30709,
		30713,
		30733,
		30871,
		30873,
		30875,
		30877,
		30883,
		30885,
		30887,
		30889,
		30917,
		30919,
		30921,
		30923,
		31671,
		31672,
		31673,
		31674
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		npc.getAI().addAttackDesireHold(attacker, 2000);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (creature instanceof Player && creature.getActingPlayer().getKarma() > 0)
			npc.getAI().addAttackDesireHold(creature, 1500);
	}
}
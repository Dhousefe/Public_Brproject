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
package ext.mods.gameserver.scripting.script.ai.ssq;

import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.skills.L2Skill;

public class SsqPartyLeader extends SsqEventBasicWarrior
{
	public SsqPartyLeader()
	{
		super("ai/ssq");
	}
	
	public SsqPartyLeader(String descr)
	{
		super(descr);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._weightPoint = 10;
		
		createPrivates(npc);
		super.onCreated(npc);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (!npc.getSpawn().isInMyTerritory(attacker))
			return;
		
		if (attacker instanceof Playable)
		{
			if (damage == 0)
				damage = 1;
			
			npc.getAI().addAttackDesire(attacker, (1.0 * damage / (npc.getStatus().getLevel() + 7)) * 100);
		}
		
		final Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
		if (mostHated != null)
		{
			final int i0 = getAbnormalLevel(npc, 1201, 1);
			if (i0 >= 0 && npc.distance2D(attacker) > 40)
			{
				if (npc.getAttack().canAttack(mostHated))
				{
					if (attacker instanceof Playable)
					{
						if (damage == 0)
							damage = 1;
						
						npc.getAI().addAttackDesire(attacker, (1.0 * damage / (npc.getStatus().getLevel() + 7)) * 100);
					}
				}
				else
				{
					npc.getAI().getAggroList().stopHate(mostHated);
					
					if (attacker instanceof Playable)
					{
						if (damage == 0)
							damage = 1;
						
						npc.getAI().addAttackDesire(attacker, (1.0 * damage / (npc.getStatus().getLevel() + 7)) * 100);
					}
				}
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
}
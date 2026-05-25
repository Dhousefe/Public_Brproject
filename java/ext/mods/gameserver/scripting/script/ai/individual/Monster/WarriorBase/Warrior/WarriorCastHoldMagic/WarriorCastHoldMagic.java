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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCastHoldMagic;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.enums.items.WeaponType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.Warrior;
import ext.mods.gameserver.skills.L2Skill;

public class WarriorCastHoldMagic extends Warrior
{
	public WarriorCastHoldMagic()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCastHoldMagic");
	}
	
	public WarriorCastHoldMagic(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21628,
		27118,
		20663,
		20844,
		21626,
		21627
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai0 = 0;
		
		super.onCreated(npc);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			final int i6 = Rnd.get(100);
			
			startQuestTimer("2001", npc, null, 12000);
			
			if (npc._i_ai0 == 0)
			{
				final double distance = npc.distance2D(attacker);
				if (distance > 300)
				{
					if (i6 < 50)
					{
						final L2Skill holdMagic = getNpcSkillByType(npc, NpcSkillType.HOLD_MAGIC);
						npc.getAI().addCastDesire(attacker, holdMagic, 1000000);
						
						npc._i_ai0 = 1;
					}
				}
				else if (distance > 100)
				{
					final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
					if (topDesireTarget != null)
					{
						if ((topDesireTarget == attacker && i6 < 50) || i6 < 10)
						{
							final L2Skill holdMagic = getNpcSkillByType(npc, NpcSkillType.HOLD_MAGIC);
							npc.getAI().addCastDesire(attacker, holdMagic, 1000000);
							
							npc._i_ai0 = 1;
						}
					}
				}
				
				if (attacker.getAttackType() == WeaponType.BOW)
				{
					if (Rnd.get(100) < 50)
					{
						final L2Skill holdMagic = getNpcSkillByType(npc, NpcSkillType.HOLD_MAGIC);
						npc.getAI().addCastDesire(attacker, holdMagic, 1000000);
						
						npc._i_ai0 = 1;
					}
				}
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("2001"))
		{
			final Creature c0 = npc.getLastAttacker();
			if (c0 != null && Rnd.get(100) < 80 && npc._i_ai0 == 0)
			{
				final L2Skill holdMagic = getNpcSkillByType(npc, NpcSkillType.HOLD_MAGIC);
				npc.getAI().addCastDesire(c0, holdMagic, 1000000);
				
				npc._i_ai0 = 1;
			}
		}
		
		return super.onTimer(name, npc, player);
	}
}
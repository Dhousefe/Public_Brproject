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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.LV3Monster;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.skills.L2Skill;

public class LV3Orc extends LV3Monster
{
	public LV3Orc()
	{
		super("ai/individual/Monster/LV3Monster");
	}
	
	public LV3Orc(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		27294,
		27295
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai0 = 0;
		npc.setEnchantEffect(10);
		npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.BIG_BODY_SKILL), 1000000);
		
		startQuestTimerAtFixedRate("4000", npc, null, 1000, 1000);
		
		super.onCreated(npc);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("4000"))
		{
			if (!npc.isStunned())
			{
				if (npc._i_ai0 == 1)
				{
					npc.removeAllDesire();
					npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.DEBUFF1), 1000000);
					npc._i_ai0++;
					npc.getAI().addAttackDesire(npc._c_ai0, 200);
				}
				else if (npc._i_ai0 == 3)
				{
					npc.removeAllDesire();
					npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.DEBUFF2), 1000000);
					npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.NORMAL_BODY_SKILL), 1000000);
					npc._i_ai0++;
					npc.getAI().addAttackDesire(npc._c_ai0, 200);
				}
				else if (npc._i_ai0 == 5)
				{
					npc.removeAllDesire();
					npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.DEBUFF3), 1000000);
					npc._i_ai0++;
					npc.getAI().addAttackDesire(npc._c_ai0, 1, 200);
				}
			}
		}
		
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			if (damage == 0)
				damage = 1;
			
			npc.getAI().addAttackDesire(attacker, (((1.0 * damage) / (npc.getStatus().getLevel() + 7)) * 100));
		}
		if (skill != null && npc.isStunned() && skill.getId() == 1245)
		{
			switch (npc._i_ai0)
			{
				case 0:
					npc._i_ai0++;
					npc.setEnchantEffect(0);
					break;
				
				case 2:
					npc._i_ai0++;
					break;
				
				case 4:
					npc._i_ai0++;
					break;
			}
		}
		
		if (attacker instanceof Playable)
		{
			if (npc.getAI().getTopDesireTarget() != null)
			{
				if (Rnd.get(100) < 33 && npc.getAI().getTopDesireTarget() == attacker)
				{
					switch (Rnd.get(3))
					{
						case 0:
							npc.getAI().addCastDesire(attacker, getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL1), 1000);
							break;
						
						case 1:
							npc.getAI().addCastDesire(attacker, getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL2), 1000);
							break;
						
						case 2:
							npc.getAI().addCastDesire(attacker, getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL3), 1000);
							break;
					}
				}
			}
		}
		
		super.onAttacked(npc, attacker, damage, skill);
	}
}
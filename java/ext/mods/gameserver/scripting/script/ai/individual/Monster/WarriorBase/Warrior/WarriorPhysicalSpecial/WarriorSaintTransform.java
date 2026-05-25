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

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.skills.L2Skill;

public class WarriorSaintTransform extends WarriorPhysicalSpecial
{
	public WarriorSaintTransform()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorPhysicalSpecial");
	}
	
	public WarriorSaintTransform(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21534,
		21528,
		21522,
		21538
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
		Party party = attacker.getParty();
		if (party != null)
		{
			if (Rnd.get(100) < 33)
			{
				L2Skill selfRangeDDMagic = getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_DD_MAGIC);
				if (npc.getCast().meetsHpMpConditions(npc, selfRangeDDMagic))
					npc.getAI().addCastDesire(npc, selfRangeDDMagic, 1000000);
				else
					npc._i_ai0 = 1;
			}
		}
		
		if (attacker instanceof Playable)
		{
			Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
			Creature finalTarget = mostHated != null ? mostHated : attacker;
			L2Skill dispell = getNpcSkillByType(npc, NpcSkillType.DISPELL);
			
			if (Rnd.get(100) < 90 && ((npc.getStatus().getHp() / npc.getStatus().getMaxHp()) * 100) > 90 && npc._i_ai0 == 0)
			{
				if (npc.getCast().meetsHpMpConditions(finalTarget, dispell))
					npc.getAI().addCastDesire(finalTarget, dispell, 1000000);
				else
					npc.getAI().addAttackDesire(finalTarget, 1000);
				npc._i_ai0 = 1;
			}
			else if (Rnd.get(100) < 80 && ((npc.getStatus().getHp() / npc.getStatus().getMaxHp()) * 100) > 40 && ((npc.getStatus().getHp() / npc.getStatus().getMaxHp()) * 100) < 50 && npc._i_ai0 < 1)
			{
				if (npc.getCast().meetsHpMpConditions(finalTarget, dispell))
					npc.getAI().addCastDesire(finalTarget, dispell, 1000000);
				else
					npc.getAI().addAttackDesire(finalTarget, 1000);
				
				npc._i_ai0 = 2;
			}
		}
		
		super.onAttacked(npc, attacker, damage, skill);
	}
}
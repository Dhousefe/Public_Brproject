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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCast3SkillsMagical;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.Warrior;
import ext.mods.gameserver.skills.L2Skill;

public class WarriorCast3SkillsMagical extends Warrior
{
	public WarriorCast3SkillsMagical()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCast3SkillsMagical");
	}
	
	public WarriorCast3SkillsMagical(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		20610,
		20617,
		21176,
		21179,
		21182,
		21185,
		20797,
		21003,
		20658,
		20639,
		20802,
		20672
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
			
			if (mostHated == null)
			{
			}
			else if (mostHated != attacker)
			{
				if (npc._i_ai0 == 0)
					npc._i_ai0 = 1;
				else if (npc._i_ai0 == 1 && Rnd.get(100) < 30 && npc.getStatus().getHp() > (npc.getStatus().getMaxHp() / 10.0))
				{
					L2Skill sleepMagic = getNpcSkillByType(npc, NpcSkillType.SLEEP_MAGIC);
					npc.getAI().addCastDesire(attacker, sleepMagic, 1000000);
				}
			}
			else if (npc.distance2D(attacker) > 100)
			{
				if (mostHated == attacker && Rnd.get(100) < 33)
				{
					L2Skill DDMagic = getNpcSkillByType(npc, NpcSkillType.DD_MAGIC);
					
					npc.getAI().addCastDesire(attacker, DDMagic, 1000000);
				}
			}
			else if (Rnd.get(200) < 1 && npc._i_ai1 == 0 && npc.getStatus().getHp() > (npc.getStatus().getMaxHp() * 0.6))
			{
				L2Skill checkMagic = getNpcSkillByType(npc, NpcSkillType.CHECK_MAGIC);
				L2Skill checkMagic1 = getNpcSkillByType(npc, NpcSkillType.CHECK_MAGIC1);
				L2Skill checkMagic2 = getNpcSkillByType(npc, NpcSkillType.CHECK_MAGIC2);
				L2Skill cancelMagic = getNpcSkillByType(npc, NpcSkillType.CANCEL_MAGIC);
				L2Skill sleepMagic = getNpcSkillByType(npc, NpcSkillType.SLEEP_MAGIC);
				
				if ((sleepMagic == null || getAbnormalLevel(attacker, sleepMagic) <= 0) && (checkMagic == null || getAbnormalLevel(attacker, checkMagic) <= 0) && ((checkMagic1 == null || getAbnormalLevel(attacker, checkMagic1) <= 0) && (checkMagic2 == null || getAbnormalLevel(attacker, checkMagic2) <= 0)))
				{
					npc.getAI().addCastDesire(attacker, cancelMagic, 1000000);
					npc._i_ai1 = 1;
				}
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		if ((called.getAI().getLifeTime() > 7 && attacker instanceof Playable) && called.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
		{
			if (called.distance2D(attacker) > 100 && Rnd.get(100) < 33)
			{
				L2Skill DDMagic = getNpcSkillByType(called, NpcSkillType.DD_MAGIC);
				
				called.getAI().addCastDesire(attacker, DDMagic, 1000000);
			}
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
}
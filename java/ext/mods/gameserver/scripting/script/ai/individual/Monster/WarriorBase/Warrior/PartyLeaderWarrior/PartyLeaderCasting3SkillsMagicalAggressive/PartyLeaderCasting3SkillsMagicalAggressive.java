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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.PartyLeaderWarrior.PartyLeaderCasting3SkillsMagicalAggressive;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.PartyLeaderWarrior.PartyLeaderWarrior;
import ext.mods.gameserver.skills.L2Skill;

public class PartyLeaderCasting3SkillsMagicalAggressive extends PartyLeaderWarrior
{
	private static final L2Skill DD_MAGIC = SkillTable.getInstance().getInfo(4001, 1);
	private static final L2Skill DEBUFF = SkillTable.getInstance().getInfo(4037, 1);
	
	public PartyLeaderCasting3SkillsMagicalAggressive()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/PartyLeaderWarrior/PartyLeaderCasting3SkillsMagicalAggressive");
	}
	
	public PartyLeaderCasting3SkillsMagicalAggressive(String descr)
	{
		super(descr);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (!(creature instanceof Playable))
		{
			super.onSeeCreature(npc, creature);
			return;
		}
		
		if (npc.getAI().getLifeTime() > 7 && npc.isInMyTerritory())
		{
			if (npc.distance3D(creature) > 100)
			{
				if (Rnd.get(100) < 33)
				{
					final L2Skill DDMagic1 = getNpcSkillByTypeOrDefault(npc, NpcSkillType.DD_MAGIC1, DD_MAGIC);
					npc.getAI().addCastDesire(creature, DDMagic1, 1000000);
				}
				
				if (Rnd.get(100) < 33)
				{
					final L2Skill DDMagic2 = getNpcSkillByTypeOrDefault(npc, NpcSkillType.DD_MAGIC2, DD_MAGIC);
					npc.getAI().addCastDesire(creature, DDMagic2, 1000000);
				}
			}
			
			if (npc.getAI().getCurrentIntention().getType() == IntentionType.WANDER)
			{
				final L2Skill debuff = getNpcSkillByTypeOrDefault(npc, NpcSkillType.DEBUFF, DEBUFF);
				if (Rnd.get(100) < 33 && getAbnormalLevel(creature, debuff) <= 0)
					npc.getAI().addCastDesire(creature, debuff, 1000000);
			}
		}
		
		tryToAttack(npc, creature);
		
		super.onSeeCreature(npc, creature);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			if (npc.distance3D(attacker) > 100)
			{
				final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
				if (topDesireTarget != null && topDesireTarget == attacker)
				{
					if (Rnd.get(100) < 33)
					{
						final L2Skill DDMagic1 = getNpcSkillByTypeOrDefault(npc, NpcSkillType.DD_MAGIC1, DD_MAGIC);
						npc.getAI().addCastDesire(attacker, DDMagic1, 1000000);
					}
					
					if (Rnd.get(100) < 33)
					{
						final L2Skill DDMagic2 = getNpcSkillByTypeOrDefault(npc, NpcSkillType.DD_MAGIC2, DD_MAGIC);
						npc.getAI().addCastDesire(attacker, DDMagic2, 1000000);
					}
					
					final L2Skill debuff = getNpcSkillByTypeOrDefault(npc, NpcSkillType.DEBUFF, DEBUFF);
					if (Rnd.get(100) < 33 && getAbnormalLevel(attacker, debuff) <= 0)
						npc.getAI().addCastDesire(attacker, debuff, 1000000);
				}
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable && called.getAI().getLifeTime() > 7 && called.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
		{
			if (called.distance3D(attacker) > 100)
			{
				if (Rnd.get(100) < 33)
				{
					final L2Skill DDMagic1 = getNpcSkillByTypeOrDefault(called, NpcSkillType.DD_MAGIC1, DD_MAGIC);
					called.getAI().addCastDesire(attacker, DDMagic1, 1000000);
				}
				
				if (Rnd.get(100) < 33)
				{
					final L2Skill DDMagic2 = getNpcSkillByTypeOrDefault(called, NpcSkillType.DD_MAGIC2, DD_MAGIC);
					called.getAI().addCastDesire(attacker, DDMagic2, 1000000);
				}
			}
			
			final L2Skill debuff = getNpcSkillByTypeOrDefault(called, NpcSkillType.DEBUFF, DEBUFF);
			if (Rnd.get(100) < 33 && getAbnormalLevel(attacker, debuff) <= 0)
				called.getAI().addCastDesire(attacker, debuff, 1000000);
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
}
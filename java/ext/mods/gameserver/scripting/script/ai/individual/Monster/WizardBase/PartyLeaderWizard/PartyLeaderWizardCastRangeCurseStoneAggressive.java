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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WizardBase.PartyLeaderWizard;

import ext.mods.commons.random.Rnd;
import ext.mods.commons.util.ArraysUtil;

import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.skills.L2Skill;

public class PartyLeaderWizardCastRangeCurseStoneAggressive extends PartyLeaderWizardDD2
{
	public PartyLeaderWizardCastRangeCurseStoneAggressive()
	{
		super("ai/individual/Monster/WizardBase/PartyLeaderWizard");
	}
	
	public PartyLeaderWizardCastRangeCurseStoneAggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21347,
		21373
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			if (maybeCastPetrify(npc, attacker))
				return;
			
			if (npc._i_ai3 == 0 && npc.getStatus().getHpRatio() > 0.5)
			{
				final L2Skill buff1 = getNpcSkillByType(npc, NpcSkillType.BUFF1);
				npc.getAI().addCastDesire(npc, buff1, 9999999);
				
				final L2Skill buff2 = getNpcSkillByType(npc, NpcSkillType.BUFF2);
				npc.getAI().addCastDesire(npc, buff2, 9999999);
				
				npc._i_ai3 = 1;
			}
			
			final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
			if (topDesireTarget != null && topDesireTarget != attacker)
			{
				final L2Skill rangeDebuff = getNpcSkillByType(npc, NpcSkillType.RANGE_DEBUFF);
				if (Rnd.get(100) < 33 && getAbnormalLevel(attacker, rangeDebuff) <= 0)
				{
					if (npc.getCast().meetsHpMpConditions(attacker, rangeDebuff))
						npc.getAI().addCastDesire(npc, rangeDebuff, 9999999);
					else
					{
						npc._i_ai0 = 1;
						
						npc.getAI().addAttackDesire(attacker, 1000);
					}
				}
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			if (maybeCastPetrify(called, attacker))
				return;
			
			if (called.getAI().getLifeTime() > 7)
			{
				if (called._i_ai3 == 0 && called.getStatus().getHpRatio() > 0.5)
				{
					final L2Skill buff1 = getNpcSkillByType(called, NpcSkillType.BUFF1);
					called.getAI().addCastDesire(called, buff1, 9999999);
					
					final L2Skill buff2 = getNpcSkillByType(called, NpcSkillType.BUFF2);
					called.getAI().addCastDesire(called, buff2, 9999999);
					
					called._i_ai3 = 1;
				}
				
				final L2Skill rangeDebuff = getNpcSkillByType(called, NpcSkillType.RANGE_DEBUFF);
				if (Rnd.get(100) < 33 && getAbnormalLevel(attacker, rangeDebuff) <= 0)
				{
					if (called.getCast().meetsHpMpConditions(attacker, rangeDebuff))
						called.getAI().addCastDesire(called, rangeDebuff, 9999999);
					else
					{
						called._i_ai0 = 1;
						
						called.getAI().addAttackDesire(attacker, 1000);
					}
				}
			}
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai3 = 0;
		
		super.onCreated(npc);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		final Player player = creature.getActingPlayer();
		if (player == null)
			return;
		
		if (player.isAlliedWithVarka() && ArraysUtil.contains(npc.getTemplate().getClans(), "varka_silenos_clan"))
			return;
		
		if (player.isAlliedWithKetra() && ArraysUtil.contains(npc.getTemplate().getClans(), "ketra_orc_clan"))
			return;
		
		tryToAttack(npc, creature);
		
		super.onSeeCreature(npc, creature);
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		if (skill.getAggroPoints() > 0 && !skill.isOffensive() && maybeCastPetrify(npc, caster))
			return;
		
		super.onSeeSpell(npc, caster, skill, targets, isPet);
	}
}
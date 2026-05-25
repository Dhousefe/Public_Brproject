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
package ext.mods.gameserver.scripting.script.ai.individual;

import java.util.ArrayList;
import java.util.List;

import ext.mods.commons.random.Rnd;
import ext.mods.gameserver.data.xml.NpcData;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.scripting.script.ai.individual.Monster.MonsterAI;
import ext.mods.gameserver.skills.L2Skill;

/**
 * Script AI genérico para GrandBoss custom (IDs 55000+) com idTemplate.
 * 
 * Este script automaticamente registra todos os GrandBoss custom que:
 * - Têm ID >= 55000
 * - São do tipo "GrandBoss"
 * - Têm canBeAttacked = true
 * - Têm idTemplate definido
 */
public class CustomGrandBossAI extends MonsterAI
{
	public CustomGrandBossAI()
	{
		super("ai/individual");
	}
	
	protected final int[] _npcIds = findCustomGrandBosses();
	
	private static int[] findCustomGrandBosses()
	{
		final List<Integer> customGrandBossIds = new ArrayList<>();
		
		for (int npcId = 55000; npcId < 60000; npcId++)
		{
			final NpcTemplate template = NpcData.getInstance().getTemplate(npcId);
			
			if (template == null)
				continue;
			
			if (template.getIdTemplate() == template.getNpcId())
				continue;
			
			if (!"GrandBoss".equals(template.getType()))
				continue;
			
			if (!template.canBeAttacked())
				continue;
			
			customGrandBossIds.add(npcId);
		}
		
		if (!customGrandBossIds.isEmpty())
		{
			System.out.println("[CustomGrandBossAI] Registrados " + customGrandBossIds.size() + " GrandBoss custom:");
			for (Integer id : customGrandBossIds)
			{
				final NpcTemplate t = NpcData.getInstance().getTemplate(id);
				System.out.println("  - ID " + id + ": " + t.getName() + " (Template: " + t.getIdTemplate() + ")");
			}
		}
		
		return customGrandBossIds.stream().mapToInt(Integer::intValue).toArray();
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("1001"))
		{
			if (Rnd.get(5) < 2)
			{
				final L2Skill selfBuff = getNpcSkillByType(npc, NpcSkillType.SELF_BUFF_A);
				if (selfBuff != null)
					npc.getAI().addCastDesire(npc, selfBuff, 1000000);
			}
		}
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (!(creature instanceof Playable))
			return;
		
		if (!npc.getTemplate().getAggro())
			return;
		
		tryToAttack(npc, creature);
		
		super.onSeeCreature(npc, creature);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			npc.getAI().addAttackDesire(attacker, (damage * 5000) / (npc.getStatus().getLevel() + 7));
			
			final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
			final double distance = npc.distance2D(attacker);
			
			if (attacker == topDesireTarget && Rnd.get(10) < 1)
			{
				final L2Skill physicalSpecial_a = getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL_A);
				if (physicalSpecial_a != null)
					npc.getAI().addCastDesire(attacker, physicalSpecial_a, 1000000);
			}
			
			if (attacker != topDesireTarget && distance < 150 && Rnd.get(250) < 1)
			{
				final L2Skill selfRangePhysicalSpecial_a = getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_PHYSICAL_SPECIAL_A);
				if (selfRangePhysicalSpecial_a != null)
					npc.getAI().addCastDesire(npc, selfRangePhysicalSpecial_a, 1000000);
			}
			
			if (distance < 150 && Rnd.get(500) < 1)
			{
				final L2Skill selfRangeCancel_a = getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_CANCEL_A);
				if (selfRangeCancel_a != null)
					npc.getAI().addCastDesire(npc, selfRangeCancel_a, 1000000);
			}
		}
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable && called.getAI().getLifeTime() > 7)
		{
			called.getAI().addAttackDesire(attacker, (damage * 2000) / (called.getStatus().getLevel() + 7));
		}
		
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
		final double distance = npc.distance2D(caster);
		
		if (caster == topDesireTarget && Rnd.get(10) < 1)
		{
			final L2Skill physicalSpecial_a = getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL_A);
			if (physicalSpecial_a != null)
				npc.getAI().addCastDesire(caster, physicalSpecial_a, 1000000);
		}
		
		if (caster != topDesireTarget && distance < 150 && Rnd.get(250) < 1)
		{
			final L2Skill selfRangePhysicalSpecial_a = getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_PHYSICAL_SPECIAL_A);
			if (selfRangePhysicalSpecial_a != null)
				npc.getAI().addCastDesire(npc, selfRangePhysicalSpecial_a, 1000000);
		}
		
		if (distance < 150 && Rnd.get(500) < 1)
		{
			final L2Skill selfRangeCancel_a = getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_CANCEL_A);
			if (selfRangeCancel_a != null)
				npc.getAI().addCastDesire(npc, selfRangeCancel_a, 1000000);
		}
		
		super.onSeeSpell(npc, caster, skill, targets, isPet);
	}
}


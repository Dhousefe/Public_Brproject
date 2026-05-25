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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WizardBase.Wizard.WizardFiendArcher;

import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.container.attackable.HateList;
import ext.mods.gameserver.scripting.script.ai.individual.Monster.WizardBase.Wizard.Wizard;
import ext.mods.gameserver.skills.L2Skill;

public class WizardFiendArcher extends Wizard
{
	public WizardFiendArcher()
	{
		super("ai/individual/Monster/WizardBase/Wizard/WizardFiendArcher");
	}
	
	public WizardFiendArcher(String descr)
	{
		super(descr);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		HateList hateList = npc.getAI().getHateList();
		
		if (attacker instanceof Playable)
		{
			double f0 = getHateRatio(npc, attacker);
			f0 = (((1.0 * damage) / (npc.getStatus().getLevel() + 7)) + ((f0 / 100) * ((1.0 * damage) / (npc.getStatus().getLevel() + 7))));
			
			if (hateList.isEmpty())
				hateList.addHateInfo(attacker, (f0 * 100) + 300);
			else
				hateList.addHateInfo(attacker, f0 * 100);
			
			L2Skill wFiendArcher = getNpcSkillByType(npc, NpcSkillType.W_FIEND_ARCHER);
			
			if (!hateList.isEmpty())
			{
				if (npc.getCast().meetsHpMpConditions(attacker, wFiendArcher))
					npc.getAI().addCastDesire(attacker, wFiendArcher, 1000000, false);
				else
				{
					npc._i_ai0 = 1;
					npc.getAI().addAttackDesire(attacker, 1000);
				}
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		HateList hateList = called.getAI().getHateList();
		
		hateList.refresh();
		
		if (called.getAI().getLifeTime() > 7 && attacker instanceof Playable && hateList.isEmpty())
		{
			L2Skill wFiendArcher = getNpcSkillByType(called, NpcSkillType.W_FIEND_ARCHER);
			
			if (called.getCast().meetsHpMpConditions(attacker, wFiendArcher))
				called.getAI().addCastDesire(attacker, wFiendArcher, 1000000, false);
			else
			{
				called._i_ai0 = 1;
				called.getAI().addAttackDesire(attacker, 1000);
			}
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
	
	@Override
	public void onUseSkillFinished(Npc npc, Creature creature, L2Skill skill, boolean success)
	{
		Creature mostHatedHI = npc.getAI().getHateList().getMostHatedCreature();
		
		if (mostHatedHI != null)
		{
			L2Skill wFiendArcher = getNpcSkillByType(npc, NpcSkillType.W_FIEND_ARCHER);
			
			if (npc.getCast().meetsHpMpConditions(mostHatedHI, wFiendArcher))
				npc.getAI().addCastDesire(mostHatedHI, wFiendArcher, 1000000, false);
			else
			{
				npc._i_ai0 = 1;
				npc.getAI().addAttackDesire(mostHatedHI, 1000);
			}
		}
		super.onUseSkillFinished(npc, creature, skill, success);
	}
}
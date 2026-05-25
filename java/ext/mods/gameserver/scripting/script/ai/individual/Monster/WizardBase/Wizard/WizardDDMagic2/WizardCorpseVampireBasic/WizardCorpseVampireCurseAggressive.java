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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WizardBase.Wizard.WizardDDMagic2.WizardCorpseVampireBasic;

import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.skills.L2Skill;

public class WizardCorpseVampireCurseAggressive extends WizardCorpseVampireBasicAggressive
{
	public WizardCorpseVampireCurseAggressive()
	{
		super("ai/individual/Monster/WizardBase/Wizard/WizardDDMagic2/WizardCorpseVampireBasic");
	}
	
	public WizardCorpseVampireCurseAggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21585
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai4 = 0;
		
		super.onCreated(npc);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			if (npc._i_ai4 == 0)
			{
				final L2Skill debuff1 = getNpcSkillByType(npc, NpcSkillType.DEBUFF1);
				npc.getAI().addCastDesire(attacker, debuff1, 1000000);
				
				npc._i_ai4 = 1;
				npc._c_ai0 = attacker;
			}
			
			if (npc._i_ai4 == 1 && npc._c_ai0 != null && npc.getStatus().getHpRatio() < 0.1 && npc.distance2D(npc._c_ai0) < 100)
			{
				final L2Skill cancel = getNpcSkillByType(npc, NpcSkillType.CANCEL);
				npc.getAI().addCastDesire(npc._c_ai0, cancel, 1000000);
				
				npc._i_ai4 = 2;
			}
		}
		
		super.onAttacked(npc, attacker, damage, skill);
	}
}
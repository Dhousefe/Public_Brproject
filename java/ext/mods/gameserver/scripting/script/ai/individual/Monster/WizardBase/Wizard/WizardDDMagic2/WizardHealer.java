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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WizardBase.Wizard.WizardDDMagic2;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.skills.L2Skill;

public class WizardHealer extends WizardDDMagic2
{
	public WizardHealer()
	{
		super("ai/individual/Monster/WizardBase/Wizard/WizardDDMagic2");
	}
	
	public WizardHealer(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21300,
		21305
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.BUFF), 1000000);
		
		npc._i_ai1 = 0;
		npc._i_ai2 = 0;
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		final double hpRatio = npc.getStatus().getHpRatio();
		if (Rnd.get(100) < 33 && hpRatio > 0.5)
		{
			if (npc._i_ai2 == 0)
			{
				npc.getAI().addFleeDesire(attacker, 500, 1000000);
				
				if (npc.getMove().getGeoPathFailCount() > 3 && attacker == npc.getAI().getTopDesireTarget() && hpRatio < 1.)
					npc._i_ai2 = 1;
			}
			else
				super.onAttacked(npc, attacker, damage, skill);
		}
		else
			super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		final int i0 = Rnd.get(100);
		
		if (caller.getStatus().getHpRatio() > 0.5)
		{
			if (i0 < 33)
			{
				called.getAI().addCastDesire(caller, getNpcSkillByType(called, NpcSkillType.BUFF), 1000000);
				called.getAI().addCastDesire(attacker, getNpcSkillByType(called, NpcSkillType.DEBUFF), 1000000);
			}
		}
		else if (i0 < 50)
			called.getAI().addCastDesire(caller, getNpcSkillByType(called, NpcSkillType.HEAL), 1000000);
		
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
	
	@Override
	public void onClanDied(Npc caller, Npc called, Creature killer)
	{
		called.getAI().addFleeDesire(killer, 500, 1000000);
	}
}
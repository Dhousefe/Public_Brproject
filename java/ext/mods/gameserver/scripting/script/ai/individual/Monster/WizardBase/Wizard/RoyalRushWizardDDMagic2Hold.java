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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WizardBase.Wizard;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.skills.L2Skill;

public class RoyalRushWizardDDMagic2Hold extends Wizard
{
	public RoyalRushWizardDDMagic2Hold()
	{
		super("ai/individual/Monster/WizardBase/Wizard");
	}
	
	public RoyalRushWizardDDMagic2Hold(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		18137,
		18170,
		18191,
		18226
	};
	
	@Override
	public void onNoDesire(Npc npc)
	{
		npc.getAI().addMoveToDesire(npc.getSpawnLocation(), 30);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		super.onAttacked(npc, attacker, damage, skill);
		
		if (attacker instanceof Playable)
		{
			if (npc._i_ai0 == 0)
			{
				final boolean isNullHate = npc.getAI().getHateList().isEmpty();
				if (npc.distance2D(attacker) > 100 && Rnd.get(100) < 80)
				{
					if (!isNullHate || Rnd.get(100) < 2)
						npc.getAI().addCastDesire(attacker, getNpcSkillByType(npc, NpcSkillType.W_LONG_RANGE_DD_MAGIC), 1000000);
				}
				else if (!isNullHate || Rnd.get(100) < 2)
					npc.getAI().addCastDesire(attacker, getNpcSkillByType(npc, NpcSkillType.W_SHORT_RANGE_DD_MAGIC), 1000000);
			}
			else
			{
				double f0 = getHateRatio(npc, attacker);
				f0 = (((1.0 * damage) / (npc.getStatus().getLevel() + 7)) + ((f0 / 100) * ((1.0 * damage) / (npc.getStatus().getLevel() + 7))));
				npc.getAI().addAttackDesire(attacker, f0 * 100);
			}
		}
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		called.getAI().getHateList().refresh();
		if ((called.getAI().getLifeTime() > 7 && attacker instanceof Playable) && called.getAI().getHateList().isEmpty())
		{
			if (caller.distance2D(attacker) > 100)
				called.getAI().addCastDesire(attacker, getNpcSkillByType(called, NpcSkillType.W_LONG_RANGE_DD_MAGIC), 1000000);
			else
				called.getAI().addCastDesire(attacker, getNpcSkillByType(called, NpcSkillType.W_SHORT_RANGE_DD_MAGIC), 1000000);
		}
		
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
	
	@Override
	public void onUseSkillFinished(Npc npc, Creature creature, L2Skill skill, boolean success)
	{
		final Creature mostHated = npc.getAI().getHateList().getMostHatedCreature();
		if (mostHated != null && npc._i_ai0 != 1)
		{
			if (npc.distance2D(npc.getAI().getHateList().getMostHatedCreature()) > 100)
				npc.getAI().addCastDesire(mostHated, getNpcSkillByType(npc, NpcSkillType.W_LONG_RANGE_DD_MAGIC), 1000000);
			else
				npc.getAI().addCastDesire(mostHated, getNpcSkillByType(npc, NpcSkillType.W_SHORT_RANGE_DD_MAGIC), 1000000);
		}
	}
}
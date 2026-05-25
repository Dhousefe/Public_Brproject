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
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.skills.L2Skill;

public class LV3WizardPrivate extends LV3PartyPrivateMonster
{
	public LV3WizardPrivate()
	{
		super("ai/individual/Monster/LV3Monster");
	}
	
	public LV3WizardPrivate(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		27253
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._c_ai0 = (Creature) World.getInstance().getObject(npc._param1);
		if (npc._c_ai0 != null)
		{
			if (npc.distance2D(npc._c_ai0) < 100 && npc.getAI().getLifeTime() > 3)
				npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_DD_MAGIC1), 1000000);
			else
				npc.getAI().addAttackDesire(npc._c_ai0, 300);
		}
		
		npc.getMaster().sendScriptEvent(1000, npc.getObjectId(), 0);
		
		startQuestTimerAtFixedRate("4000", npc, null, 1000, 1000);
		
		super.onCreated(npc);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("4000"))
		{
			if (npc._c_ai0 != null)
			{
				if (npc.distance2D(npc._c_ai0) < 100)
					npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_DD_MAGIC1), 1000000);
				
				npc.getAI().addAttackDesire(npc._c_ai0, 50);
			}
		}
		
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onUseSkillFinished(Npc npc, Creature creature, L2Skill skill, boolean success)
	{
		if (skill == getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_DD_MAGIC1))
		{
			if (npc._c_ai0 != null)
			{
				if (Rnd.get(100) < 50 && npc.distance2D(npc._c_ai0) < 200)
					npc.getAI().addCastDesire(npc._c_ai0, getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_DD_MAGIC2), 3000);
				else
					npc.getAI().addCastDesire(npc._c_ai0, getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_DD_MAGIC3), 3000);
			}
			npc.removeAllAttackDesire();
		}
		else
			npc.doDie(npc);
	}
}

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

public class LV3Wizard extends LV3Monster
{
	public LV3Wizard()
	{
		super("ai/individual/Monster/LV3Monster");
	}
	
	public LV3Wizard(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		27250,
		27251,
		27252,
		27257
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai0 = 5;
		npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.SUMMON_MODE), 1000000);
		
		createOnePrivateEx(npc, 27253, npc.getX(), npc.getY(), npc.getZ(), 0, 0, false, npc._param1, npc._param2, npc._param3);
		
		super.onCreated(npc);
	}
	
	@Override
	public void onPartyDied(Npc caller, Npc called)
	{
		if (called != caller)
		{
			if (called._i_ai0 > 0)
			{
				createOnePrivateEx(called, 27253, called.getX(), called.getY(), called.getZ(), 0, 0, false, called._param1, called._param2, called._param3);
				called.getAI().addCastDesire(called, getNpcSkillByType(called, NpcSkillType.SUMMON_EFFECT), 1000000);
				called._i_ai0 = (called._i_ai0 - 1);
			}
			else
				called.getAI().addCastDesire(called, getNpcSkillByType(called, NpcSkillType.SUMMON_MODE), 1000000);
		}
	}
	
	@Override
	public void onScriptEvent(Npc npc, int eventId, int arg1, int arg2)
	{
		if (eventId == 1000)
		{
			final Creature c0 = (Creature) World.getInstance().getObject(arg1);
			if (c0 != null)
			{
				if (Rnd.get(100) < 50)
					npc.getAI().addCastDesire(c0, getNpcSkillByType(npc, NpcSkillType.SUMMON_HEAL1), 1000000);
				else
					npc.getAI().addCastDesire(c0, getNpcSkillByType(npc, NpcSkillType.SUMMON_HEAL2), 1000000);
			}
		}
	}
}
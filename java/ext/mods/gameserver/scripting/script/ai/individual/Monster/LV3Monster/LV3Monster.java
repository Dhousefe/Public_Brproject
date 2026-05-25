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

import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.script.ai.individual.Monster.MonsterAI;
import ext.mods.gameserver.skills.L2Skill;

public class LV3Monster extends MonsterAI
{
	public LV3Monster()
	{
		super("ai/individual/Monster/LV3Monster");
	}
	
	public LV3Monster(String descr)
	{
		super(descr);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		if (npc._param1 != 0)
		{
			npc._c_ai0 = (Creature) World.getInstance().getObject(npc._param1);
			if (npc._c_ai0 != null)
				npc.getAI().addAttackDesire(npc._c_ai0, 200);
		}
		
		if (npc._param3 != 0 && npc.hasMaster())
			npc._c_ai1 = npc.getMaster();
		
		startQuestTimer("3000", npc, null, 12 * 60 * 1000L);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		final Player c0 = attacker.getActingPlayer();
		if (c0 != null)
		{
			if (c0.getObjectId() != npc._param2)
			{
				if (npc._c_ai1 != null)
					((Npc) npc._c_ai1).sendScriptEvent(1000, 0, 0);
				
				final Npc npc0 = (Npc) World.getInstance().getObject(npc._param3);
				if (npc0 != null)
					npc0._i_quest0 = 0;
				
				npc.deleteMe();
			}
			
			if (attacker instanceof Playable)
			{
				double f0 = getHateRatio(npc, attacker);
				f0 = (((1.0 * damage) / (npc.getStatus().getLevel() + 7)) + ((f0 / 100) * ((1.0 * damage) / (npc.getStatus().getLevel() + 7))));
				npc.getAI().addAttackDesire(attacker, f0 * 100);
			}
		}
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("3000"))
		{
			final Npc npc0 = (Npc) World.getInstance().getObject(npc._param3);
			if (npc0 != null)
				npc0._i_quest0 = 0;
			
			npc.deleteMe();
		}
		
		return null;
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		if (caster != null && caster.getObjectId() != npc._param2)
		{
			if (npc._c_ai1 != null)
				((Npc) npc._c_ai1).sendScriptEvent(1000, 0, 0);
			
			final Npc npc0 = (Npc) World.getInstance().getObject(npc._param3);
			if (npc0 != null)
				npc0._i_quest0 = 0;
			
			npc.deleteMe();
		}
	}
}
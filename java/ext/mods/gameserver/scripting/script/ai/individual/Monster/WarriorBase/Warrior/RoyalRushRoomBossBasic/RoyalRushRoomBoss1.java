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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.RoyalRushRoomBossBasic;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.skills.L2Skill;

public class RoyalRushRoomBoss1 extends RoyalRushRoomBossBasic
{
	public RoyalRushRoomBoss1()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/RoyalRushRoomBossBasic");
	}
	
	public RoyalRushRoomBoss1(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		18121,
		18124,
		18127,
		18130,
		18174,
		18177,
		18180,
		18183
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai0 = 0;
		npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.SELF_DEBUFF1), 1000000);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
			if (topDesireTarget != null && topDesireTarget == attacker && Rnd.get(100) < 33)
				npc.getAI().addCastDesire(attacker, getNpcSkillByType(npc, NpcSkillType.RANGE_DD_MAGIC1), 1000000);
		}
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanDied(Npc caller, Npc called, Creature killer)
	{
		if (caller != called)
		{
			switch (called._i_ai0)
			{
				case 2:
					called.getAI().addCastDesire(called, getNpcSkillByType(called, NpcSkillType.SELF_DEBUFF1), 1000000);
					break;
				
				case 4:
					called.getAI().addCastDesire(called, getNpcSkillByType(called, NpcSkillType.SELF_DEBUFF2), 1000000);
					break;
				
				case 6:
					called.getAI().addCastDesire(called, getNpcSkillByType(called, NpcSkillType.SELF_DEBUFF3), 1000000);
					break;
			}
			called._i_ai0 = (called._i_ai0 + 1);
		}
		
		super.onClanDied(caller, called, killer);
	}
}
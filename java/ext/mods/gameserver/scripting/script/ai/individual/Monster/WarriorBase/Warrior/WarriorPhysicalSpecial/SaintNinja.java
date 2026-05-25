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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorPhysicalSpecial;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.skills.L2Skill;

public class SaintNinja extends WarriorPhysicalSpecial
{
	public SaintNinja()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorPhysicalSpecial");
	}
	
	public SaintNinja(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21539,
		21540,
		21524,
		21525,
		21531,
		21658
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai0 = 0;
		
		npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.SELF_BUFF), 1000000);
		
		if (getNpcIntAIParam(npc, "IsMainForm") == 0)
			startQuestTimer("2000", npc, null, (60000 * 5));
		
		startQuestTimer("2001", npc, null, 60000);
		
		super.onCreated(npc);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (Rnd.get(100) < 80 && getNpcIntAIParam(npc, "IsMainForm") == 1 && npc._i_ai0 == 0)
		{
			createOnePrivateEx(npc, getNpcIntAIParam(npc, "OtherSelf"), npc.getX() + Rnd.get(20), npc.getY() + Rnd.get(20), npc.getZ(), 32768, 0, false, 1000, attacker.getObjectId(), 1);
			
			npc._i_ai0 = 1;
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("2000"))
		{
			if (npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
			{
				npc.deleteMe();
				return null;
			}
			
			startQuestTimer("2000", npc, null, (60000 * 5));
		}
		else if (name.equalsIgnoreCase("2001"))
		{
			if (npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
				npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.TELEPORT_EFFECT), 1000000);
			
			startQuestTimer("2001", npc, null, (60000 * 5));
		}
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		if (called.getAI().getCurrentIntention().getType() != IntentionType.ATTACK && called.distance2D(attacker) > 300)
		{
			called.abortAll(false);
			called.teleportTo(attacker.getPosition(), 0);
			called.getAI().addCastDesire(attacker, getNpcSkillByType(called, NpcSkillType.TELEPORT_EFFECT), 1000000);
			
			if (attacker instanceof Playable)
			{
				double f0 = getHateRatio(called, attacker);
				f0 = (((1.0 * damage) / (called.getStatus().getLevel() + 7)) + ((f0 / 100) * ((1.0 * damage) / (called.getStatus().getLevel() + 7))));
				
				called.getAI().addAttackDesire(attacker, (int) (f0 * 30));
			}
		}
	}
}
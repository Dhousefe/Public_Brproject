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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.PartyPrivateWarrior;

import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.skills.L2Skill;

public class PartyPrivatePriestBuffer extends PartyPrivateWarrior
{
	public PartyPrivatePriestBuffer()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/PartyPrivateWarrior");
	}
	
	public PartyPrivatePriestBuffer(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		22131
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		startQuestTimerAtFixedRate("5001", npc, null, 20000, 20000);
		
		super.onCreated(npc);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (!npc.hasMaster())
		{
			super.onSeeCreature(npc, creature);
			return;
		}
		
		if ((creature instanceof Npc npcCreature && npcCreature.hasMaster() && npcCreature.getMaster() == npc.getMaster()) || creature == npc.getMaster())
		{
			final L2Skill buff1 = getNpcSkillByType(npc, NpcSkillType.BUFF1);
			final L2Skill buff2 = getNpcSkillByType(npc, NpcSkillType.BUFF2);
			final L2Skill buff3 = getNpcSkillByType(npc, NpcSkillType.BUFF3);
			final L2Skill buff4 = getNpcSkillByType(npc, NpcSkillType.BUFF4);
			final L2Skill buff5 = getNpcSkillByType(npc, NpcSkillType.BUFF5);
			final L2Skill buff6 = getNpcSkillByType(npc, NpcSkillType.BUFF6);
			
			if (npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
			{
				if (getAbnormalLevel(creature, buff1) <= 0)
					npc.getAI().addCastDesire(creature, buff1, 1000000);
				
				if (getAbnormalLevel(creature, buff2) <= 0)
					npc.getAI().addCastDesire(creature, buff2, 1000000);
				
				if (getAbnormalLevel(creature, buff3) <= 0)
					npc.getAI().addCastDesire(creature, buff3, 1000000);
				
				if (getAbnormalLevel(creature, buff4) <= 0)
					npc.getAI().addCastDesire(creature, buff4, 1000000);
				
				if (getAbnormalLevel(creature, buff5) <= 0)
					npc.getAI().addCastDesire(creature, buff5, 1000000);
				
				if (getAbnormalLevel(creature, buff6) <= 0)
					npc.getAI().addCastDesire(creature, buff6, 1000000);
			}
		}
		
		super.onSeeCreature(npc, creature);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("5001"))
			npc.lookNeighbor(300);
		
		return super.onTimer(name, npc, null);
	}
}
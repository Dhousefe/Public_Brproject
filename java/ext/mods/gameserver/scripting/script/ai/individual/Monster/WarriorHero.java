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
package ext.mods.gameserver.scripting.script.ai.individual.Monster;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.skills.L2Skill;

public class WarriorHero extends MonsterAI
{
	public WarriorHero()
	{
		super("ai/individual/Monster");
	}
	
	public WarriorHero(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21260
	};
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("3001"))
			npc.deleteMe();
		else if (name.equalsIgnoreCase("3002"))
			npc.broadcastNpcSay(NpcStringId.get(1000434 + Rnd.get(7)));
		
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		final Creature c0 = (Creature) World.getInstance().getObject(npc._param3);
		if (c0 != null)
		{
			final L2Skill heroSkill = getNpcSkillByType(npc, NpcSkillType.HERO_SKILL);
			npc.getAI().addCastDesire(c0, heroSkill, 1000000);
			
			npc.getAI().addAttackDesire(c0, 1000);
		}
		
		startQuestTimer("3001", npc, null, 15000);
		startQuestTimer("3002", npc, null, 8000);
		
		super.onCreated(npc);
	}
}
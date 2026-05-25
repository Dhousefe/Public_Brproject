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
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.skills.L2Skill;

/**
 * Fishing monster behavior, occuring at 5% of a successful fishing action.
 */
public class WarriorFishingBlock extends WarriorPhysicalSpecial
{
	public WarriorFishingBlock()
	{
		super("ai/group");
	}
	
	protected final int[] _npcIds =
	{
		18319,
		18320,
		18321,
		18322,
		18323,
		18324,
		18325,
		18326
	};
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("3000"))
		{
			final IntentionType type = npc.getAI().getCurrentIntention().getType();
			if (type != IntentionType.ATTACK && type != IntentionType.CAST)
				npc.deleteMe();
		}
		return null;
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (Rnd.get(100) < 33)
			npc.broadcastNpcSay(retrieveNpcStringId(npc, 3), attacker.getName());
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		if (npc._summoner == null)
			npc.deleteMe();
		else
		{
			npc.getAI().addAttackDesire(npc._summoner, 2000);
			npc.broadcastNpcSay(retrieveNpcStringId(npc, 0), npc._summoner.getName());
			
			startQuestTimerAtFixedRate("3000", npc, null, 50000, 50000);
		}
		super.onCreated(npc);
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		npc.broadcastNpcSay(retrieveNpcStringId(npc, 6), killer.getName());
		
		super.onMyDying(npc, killer);
	}
	
	private static final NpcStringId retrieveNpcStringId(Npc npc, int index)
	{
		return NpcStringId.get(1010400 + index + ((npc.getNpcId() - 18319) * 9) + Rnd.get(3));
	}
}
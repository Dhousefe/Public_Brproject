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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.PartyLeaderWarrior.PartyLeaderCasting3SkillsMagicalAggressive;

import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;

public class AntharasPrivateBehemothDragon extends PartyLeaderCasting3SkillsMagicalAggressive
{
	public AntharasPrivateBehemothDragon()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/PartyLeaderWarrior/PartyLeaderCasting3SkillsMagicalAggressive");
	}
	
	public AntharasPrivateBehemothDragon(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		29069
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		if (npc._param1 == 4)
			createPrivates(npc, 29070, 3);
		else if (npc._param1 == 5)
			createPrivates(npc, 29070, 5);
		else if (npc._param1 == 6)
			createPrivates(npc, 29070, 7);
		
		if (getNpcIntAIParam(npc, "MoveAroundSocial") > 0 || getNpcIntAIParam(npc, "ShoutMsg2") > 0 || getNpcIntAIParam(npc, "ShoutMsg3") > 0)
			startQuestTimer("1001", npc, null, 10000);
		
		startQuestTimer("1007", npc, null, 120000);
		startQuestTimer("1155", npc, null, (240 * 60) * 1000);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("1155"))
			npc.deleteMe();
		
		return super.onTimer(name, npc, player);
	}
	
	private void createPrivates(Npc master, int npcId, int count)
	{
		for (int i = 0; i < count; i++)
		{
			Npc bomber = createOnePrivate(master, npcId, 0, false);
			bomber.getSpawn().setRespawnDelay(0);
		}
	}
}
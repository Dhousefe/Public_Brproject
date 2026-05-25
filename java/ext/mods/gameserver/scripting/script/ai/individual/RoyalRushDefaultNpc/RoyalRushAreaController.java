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
package ext.mods.gameserver.scripting.script.ai.individual.RoyalRushDefaultNpc;

import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.skills.L2Skill;

public class RoyalRushAreaController extends RoyalRushDefaultNpc
{
	public RoyalRushAreaController()
	{
		super("ai/individual/RoyalRushDefaultNpc");
	}
	
	public RoyalRushAreaController(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		18196,
		18197,
		18198,
		18199,
		18200,
		18201,
		18202,
		18203,
		18204,
		18205,
		18206,
		18207,
		18208,
		18209,
		18210,
		18211
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		final int type = getNpcIntAIParam(npc, "type");
		switch (type)
		{
			case 0:
				startQuestTimer("3001", npc, null, (1000 * 60) * 2);
				npc.broadcastNpcShout(NpcStringId.ID_1010474);
				break;
			
			case 1:
				startQuestTimer("3001", npc, null, 1000 * 60);
				npc.broadcastNpcShout(NpcStringId.ID_1010473);
				break;
			
			case 2:
				npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.STATUS_EFFECT), 1000000);
				npc.broadcastNpcShout(NpcStringId.ID_1010472);
				startQuestTimer("3002", npc, null, 1000 * 30);
				break;
			
			case 3:
				startQuestTimer("3001", npc, null, (1000 * 60) * 3);
				npc.broadcastNpcShout(NpcStringId.ID_1010475);
				break;
		}
		
		super.onCreated(npc);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("3001"))
		{
			npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.STATUS_EFFECT), 1000000);
			final int type = getNpcIntAIParam(npc, "type");
			switch (type)
			{
				case 0:
					npc.broadcastNpcShout(NpcStringId.ID_1010477);
					startQuestTimer("3002", npc, null, (1000 * 30));
					break;
				
				case 1:
					npc.broadcastNpcShout(NpcStringId.ID_1010476);
					startQuestTimer("3002", npc, null, (1000 * 30));
					break;
				
				case 2:
					npc.broadcastNpcShout(NpcStringId.ID_1010472);
					break;
				
				case 3:
					npc.broadcastNpcShout(NpcStringId.ID_1010478);
					startQuestTimer("3002", npc, null, (1000 * 30));
					break;
			}
		}
		
		if (name.equalsIgnoreCase("3002"))
		{
			npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.STATUS_EFFECT), 1000000);
			startQuestTimer("3002", npc, null, (1000 * 30));
		}
		
		return super.onTimer(name, npc, null);
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		final int type = getNpcIntAIParam(npc, "type");
		switch (type)
		{
			case 0:
				npc.broadcastNpcShout(NpcStringId.ID_1010481);
				break;
			
			case 1:
				npc.broadcastNpcShout(NpcStringId.ID_1010480);
				break;
			
			case 2:
				npc.broadcastNpcShout(NpcStringId.ID_1010479);
				break;
			
			case 3:
				npc.broadcastNpcShout(NpcStringId.ID_1010482);
				break;
		}
	}
	
	@Override
	public void onUseSkillFinished(Npc npc, Creature creature, L2Skill skill, boolean success)
	{
		npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.STATUS_EFFECT), 1000000, false);
	}
}
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
package ext.mods.gameserver.scripting.script.ai.ssq;

import java.util.List;

import ext.mods.gameserver.data.manager.FestivalOfDarknessManager;
import ext.mods.gameserver.data.manager.SevenSignsManager;
import ext.mods.gameserver.enums.CabalType;
import ext.mods.gameserver.enums.PeriodType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.scripting.script.ai.individual.DefaultNpc;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.taskmanager.GameTimeTaskManager;

public class SsqMainEventAcolyte extends DefaultNpc
{
	public static final int FESTIVAL_COUNT = 5;
	
	public SsqMainEventAcolyte()
	{
		super("ai/ssq");
	}
	
	public SsqMainEventAcolyte(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		31127,
		31128,
		31129,
		31130,
		31131,
		31137,
		31138,
		31139,
		31140,
		31141
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai0 = 0;
		npc.getSpawn().instantTeleportInMyTerritory(new Location(getNpcIntAIParam(npc, "escape_x"), getNpcIntAIParam(npc, "escape_y"), getNpcIntAIParam(npc, "coord_z")), 100);
		startQuestTimer("3001", npc, null, 1000);
	}
	
	@Override
	public void onUseSkillFinished(Npc npc, Creature creature, L2Skill skill, boolean success)
	{
		var party0 = creature.getParty();
		if (party0 != null)
		{
			for (Player partyMember : party0.getMembers())
			{
				if (partyMember.isIn3DRadius(npc, 1000))
					partyMember.teleportTo(getNpcIntAIParam(npc, "SibylPosX"), getNpcIntAIParam(npc, "SibylPosY"), getNpcIntAIParam(npc, "SibylPosZ"), 0);
			}
		}
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("3001"))
		{
			if (SevenSignsManager.getInstance().getCurrentPeriod() == PeriodType.COMPETITION)
			{
				int i0 = getMin();
				int i1 = FestivalOfDarknessManager.getInstance().getTimeOfSSQ() - GameTimeTaskManager.getInstance().getCurrentTick();
				
				CabalType cabalType = null;
				if (getNpcIntAIParam(npc, "part_type") == 1)
					cabalType = CabalType.DUSK;
				else if (getNpcIntAIParam(npc, "part_type") == 2)
					cabalType = CabalType.DAWN;
				
				if ((i0 == 58 || i0 == 18 || i0 == 38) && i1 >= 18 * 60)
				{
					if (npc._i_ai0 < 1)
					{
						List<?> participants = FestivalOfDarknessManager.getInstance().getPreviousParticipants(cabalType, getNpcIntAIParam(npc, "RoomIndex"));
						if (participants != null)
							participants.clear();
						
						if (getNpcIntAIParam(npc, "ShoutSysMsg") == 1)
							npc.broadcastNpcShout(NpcStringId.getNpcMessage(1000317));
						
						npc._i_ai0++;
					}
				}
				else if ((i0 == 0 || i0 == 20 || i0 == 40) && i1 >= 18 * 60)
				{
					if (npc._i_ai0 < 1)
					{
						if (getNpcIntAIParam(npc, "ShoutSysMsg") == 1)
							npc.broadcastNpcShout(NpcStringId.getNpcMessage(1000318));
						
						npc._i_ai0++;
						
						createOnePrivateEx(npc, getNpcIntAIParam(npc, "SibylSilhouette"), getNpcIntAIParam(npc, "SibylPosX"), getNpcIntAIParam(npc, "SibylPosY"), getNpcIntAIParam(npc, "SibylPosZ"), 0, 0, false);
					}
				}
				else if (i0 == 13 || i0 == 33 || i0 == 53)
				{
					if (npc._i_ai0 < 1)
					{
						if (getNpcIntAIParam(npc, "ShoutSysMsg") == 1)
							npc.broadcastNpcShout(NpcStringId.getNpcMessage(1000319));
						
						npc._i_ai0++;
					}
				}
				else if (i0 == 16 || i0 == 36 || i0 == 56)
				{
					if (npc._i_ai0 < 1 && getNpcIntAIParam(npc, "ShoutSysMsg") == 1)
					{
						if (i1 >= 20 * 60)
							npc.broadcastNpcShout(NpcStringId.getNpcMessage(1000320));
						else
							npc.broadcastNpcShout(NpcStringId.getNpcMessage(1000453));
						
						npc._i_ai0++;
					}
				}
				else
					npc._i_ai0 = 0;
			}
			startQuestTimer("3001", npc, null, 7000);
		}
		return null;
	}
	
	@Override
	public void onDecayed(Npc npc)
	{
		cancelQuestTimers("3001", npc);
		
		super.onDecayed(npc);
	}
}
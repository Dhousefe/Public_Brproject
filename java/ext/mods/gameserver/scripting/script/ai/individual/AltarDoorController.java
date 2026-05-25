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
package ext.mods.gameserver.scripting.script.ai.individual;

import ext.mods.gameserver.data.manager.SpawnManager;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.spawn.NpcMaker;
import ext.mods.gameserver.network.NpcStringId;

public class AltarDoorController extends DefaultNpc
{
	private static final String MAKER_NAME_UNIQUE_NPC_2ND_FLOOR = "altar_start_02";
	private static final String MAKER_NAME_JAKO_2ND_FLOOR = "altar_start_01";
	private static final String MAKER_NAME_STAGE_CON = "rune14_andreas_m2";
	
	public AltarDoorController()
	{
		super("ai/individual");
		
		addFirstTalkId(_npcIds);
	}
	
	public AltarDoorController(String descr)
	{
		super(descr);
		
		addFirstTalkId(_npcIds);
	}
	
	protected final int[] _npcIds =
	{
		32051
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai0 = 0;
		npc._i_ai1 = 0;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return null;
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (npc._i_ai0 == 0)
			broadcastScriptEvent(npc, 1, creature.getObjectId(), 4000);
		else if (npc._i_ai0 == 2)
			broadcastScriptEvent(npc, 3, creature.getObjectId(), 4000);
	}
	
	@Override
	public void onScriptEvent(Npc npc, int eventId, int arg1, int arg2)
	{
		if (eventId == 0)
		{
			final String doorName = getNpcStringAIParam(npc, "DoorName");
			if (doorName != null)
				openCloseDoor(doorName, 0);
			
			final String doorName3rdFloor = getNpcStringAIParam(npc, "DoorName3rdFloor");
			if (doorName3rdFloor != null)
				openCloseDoor(doorName3rdFloor, 1);
		}
		else if (eventId == 1)
		{
			NpcMaker maker0 = SpawnManager.getInstance().getNpcMaker(MAKER_NAME_UNIQUE_NPC_2ND_FLOOR);
			if (maker0 != null)
				maker0.getMaker().onMakerScriptEvent("1001", maker0, 0, 0);
			
			maker0 = SpawnManager.getInstance().getNpcMaker(MAKER_NAME_JAKO_2ND_FLOOR);
			if (maker0 != null)
				maker0.getMaker().onMakerScriptEvent("1001", maker0, 0, 0);
			
			maker0 = SpawnManager.getInstance().getNpcMaker(MAKER_NAME_STAGE_CON);
			if (maker0 != null)
				maker0.getMaker().onMakerScriptEvent("1001", maker0, 0, 0);
			
			startQuestTimer("2001", npc, null, 3 * 60 * 1000L);
		}
		else if (eventId == 2)
		{
			npc.broadcastNpcShout(NpcStringId.ID_10079);
			
			final String doorName3rdFloor = getNpcStringAIParam(npc, "DoorName3rdFloor");
			if (doorName3rdFloor != null)
				openCloseDoor(doorName3rdFloor, 0);
			
			startQuestTimer("2002", npc, null, 3 * 60 * 1000L);
		}
		else if (eventId == 3)
		{
			npc._i_ai1 = 1;
			
			startQuestTimer("2003", npc, null, 2 * 60 * 1000L);
		}
		else if (eventId == 5)
		{
			final String doorName3rdFloor = getNpcStringAIParam(npc, "DoorName3rdFloor");
			if (doorName3rdFloor != null)
				openCloseDoor(doorName3rdFloor, 0);
			
			final String doorName = getNpcStringAIParam(npc, "DoorName");
			if (doorName != null)
				openCloseDoor(doorName, 0);
			
			final NpcMaker maker0 = SpawnManager.getInstance().getNpcMaker(MAKER_NAME_STAGE_CON);
			if (maker0 != null)
				maker0.getMaker().onMakerScriptEvent("1000", maker0, 0, 0);
			
			startQuestTimer("2004", npc, null, 3 * 60 * 1000L);
		}
		else if (eventId == 6)
		{
			final String doorName3rdFloor = getNpcStringAIParam(npc, "DoorName3rdFloor");
			if (doorName3rdFloor != null)
				openCloseDoor(doorName3rdFloor, 1);
			
			startQuestTimer("2005", npc, null, 3 * 60 * 1000L);
		}
		else if (eventId == 7)
		{
			final String doorName3rdFloor = getNpcStringAIParam(npc, "DoorName3rdFloor");
			if (doorName3rdFloor != null)
				openCloseDoor(doorName3rdFloor, 0);
			
			startQuestTimer("2006", npc, null, 60 * 60 * 1000L);
		}
		
		if (eventId < 9)
			npc._i_ai0 = eventId;
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("2001"))
		{
			final String doorName = getNpcStringAIParam(npc, "DoorName");
			if (doorName != null)
				openCloseDoor(doorName, 1);
		}
		else if (name.equalsIgnoreCase("2002"))
		{
			if (npc._i_ai1 == 0)
			{
				final String doorName3rdFloor = getNpcStringAIParam(npc, "DoorName3rdFloor");
				if (doorName3rdFloor != null)
					openCloseDoor(doorName3rdFloor, 1);
			}
		}
		else if (name.equalsIgnoreCase("2003"))
		{
			npc._i_ai1 = 0;
			
			startQuestTimer("2002", npc, player, 60000);
		}
		else if (name.equalsIgnoreCase("2004"))
			broadcastScriptEvent(npc, 6, npc.getObjectId(), 4000);
		else if (name.equalsIgnoreCase("2005"))
			npc.deleteMe();
		else if (name.equalsIgnoreCase("2006"))
		{
			NpcMaker maker0 = SpawnManager.getInstance().getNpcMaker(MAKER_NAME_UNIQUE_NPC_2ND_FLOOR);
			if (maker0 != null)
				maker0.getMaker().onMakerScriptEvent("1001", maker0, 0, 0);
			
			maker0 = SpawnManager.getInstance().getNpcMaker(MAKER_NAME_STAGE_CON);
			if (maker0 != null)
				maker0.getMaker().onMakerScriptEvent("1001", maker0, 0, 0);
			
			final String doorName = getNpcStringAIParam(npc, "DoorName");
			if (doorName != null)
				openCloseDoor(doorName, 0);
			
			npc._i_ai0 = 0;
			
			broadcastScriptEvent(npc, 0, npc.getObjectId(), 4000);
		}
		
		return null;
	}
}
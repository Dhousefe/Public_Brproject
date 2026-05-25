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
package ext.mods.gameserver.scripting.script.ai.boss.frintezza;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.data.manager.SpawnManager;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.spawn.NpcMaker;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.scripting.script.ai.individual.DefaultNpc;

public class AlarmDevice extends DefaultNpc
{
	private static final String T_DOOR_NAME_1 = "grave_pathway_1";
	private static final String T_DOOR_NAME_2 = "grave_pathway_2";
	
	private static final String WALL_DOOR_NAME_1 = "wall_door_a_center_1";
	private static final String WALL_DOOR_NAME_2 = "wall_door_a_center_2";
	private static final String WALL_DOOR_NAME_3 = "wall_door_a_center_3";
	private static final String WALL_DOOR_NAME_4 = "wall_door_a_center_4";
	private static final String WALL_DOOR_NAME_5 = "wall_door_a_center_5";
	private static final String WALL_DOOR_NAME_6 = "wall_door_a_center_6";
	private static final String WALL_DOOR_NAME_7 = "wall_door_a_center_7";
	private static final String WALL_DOOR_NAME_8 = "wall_door_a_center_8";
	
	
	private static final String MAKER_NAME_SELF_DES = "godard32_2515_09m1";
	private static final String MAKER_NAME_GUARD = "godard32_2515_06m1";
	private static final String MAKER_NAME_PATROL = "godard32_2515_05m1";
	private static final String MAKER_NAME_CAPTAIN = "godard32_2515_07m1";
	private static final String MAKER_NAME_WIZARD_1 = "godard32_2515_01m1";
	private static final String MAKER_NAME_WIZARD_2 = "godard32_2515_02m1";
	private static final String MAKER_NAME_WIZARD_3 = "godard32_2515_03m1";
	private static final String MAKER_NAME_WIZARD_4 = "godard32_2515_04m1";
	private static final String MAKER_NAME_WIZARD_5 = "godard32_2515_08m1";
	private static final String MAKER_NAME_ALARM_1 = "godard32_2515_22m1";
	private static final String MAKER_NAME_ALARM_2 = "godard32_2515_19m1";
	private static final String MAKER_NAME_ALARM_3 = "godard32_2515_20m1";
	private static final String MAKER_NAME_ALARM_4 = "godard32_2515_21m1";
	
	public AlarmDevice()
	{
		super("ai/boss/frintezza");
	}
	
	public AlarmDevice(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		18328
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai0 = 0;
		
		
		npc.broadcastNpcShout(NpcStringId.ID_1010630);
	}
	
	@Override
	public void onScriptEvent(Npc npc, int eventId, int arg1, int arg2)
	{
		if (eventId == 10025)
			npc._i_ai0 = arg1;
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		int i0 = 0;
		
		if (npc._i_ai0 == 1)
		{
			final NpcMaker maker0 = SpawnManager.getInstance().getNpcMaker(MAKER_NAME_CAPTAIN);
			if (maker0 != null)
			{
				if (Rnd.get(100) < 10)
					i0 = 100;
				else
					maker0.getMaker().onMakerScriptEvent("1001", maker0, 0, 0);
			}
		}
		else if (npc._i_ai0 == 2)
		{
			final NpcMaker maker0 = SpawnManager.getInstance().getNpcMaker(MAKER_NAME_SELF_DES);
			if (maker0 != null)
			{
				if (Rnd.get(100) < 20)
					i0 = 100;
				else
					maker0.getMaker().onMakerScriptEvent("1001", maker0, 0, 0);
			}
		}
		else if (npc._i_ai0 == 3)
		{
			final NpcMaker maker0 = SpawnManager.getInstance().getNpcMaker(MAKER_NAME_GUARD);
			if (maker0 != null)
			{
				if (Rnd.get(100) < 30)
					i0 = 100;
				else
					maker0.getMaker().onMakerScriptEvent("1001", maker0, 0, 0);
			}
		}
		else if (npc._i_ai0 == 4)
			i0 = 100;
		
		if (i0 == 100)
		{
			npc.broadcastNpcShout(NpcStringId.ID_1010631);
			
			NpcMaker maker0 = SpawnManager.getInstance().getNpcMaker(MAKER_NAME_ALARM_1);
			if (maker0 != null)
				maker0.getMaker().onMakerScriptEvent("1000", maker0, 0, 0);
			
			maker0 = SpawnManager.getInstance().getNpcMaker(MAKER_NAME_ALARM_2);
			if (maker0 != null)
				maker0.getMaker().onMakerScriptEvent("1000", maker0, 0, 0);
			
			maker0 = SpawnManager.getInstance().getNpcMaker(MAKER_NAME_ALARM_3);
			if (maker0 != null)
				maker0.getMaker().onMakerScriptEvent("1000", maker0, 0, 0);
			
			maker0 = SpawnManager.getInstance().getNpcMaker(MAKER_NAME_ALARM_4);
			if (maker0 != null)
				maker0.getMaker().onMakerScriptEvent("1000", maker0, 0, 0);
			
			maker0 = SpawnManager.getInstance().getNpcMaker(MAKER_NAME_CAPTAIN);
			if (maker0 != null)
				maker0.getMaker().onMakerScriptEvent("1000", maker0, 0, 0);
			
			maker0 = SpawnManager.getInstance().getNpcMaker(MAKER_NAME_GUARD);
			if (maker0 != null)
				maker0.getMaker().onMakerScriptEvent("1000", maker0, 0, 0);
			
			maker0 = SpawnManager.getInstance().getNpcMaker(MAKER_NAME_PATROL);
			if (maker0 != null)
				maker0.getMaker().onMakerScriptEvent("1000", maker0, 0, 0);
			
			maker0 = SpawnManager.getInstance().getNpcMaker(MAKER_NAME_WIZARD_1);
			if (maker0 != null)
				maker0.getMaker().onMakerScriptEvent("1000", maker0, 0, 0);
			
			maker0 = SpawnManager.getInstance().getNpcMaker(MAKER_NAME_WIZARD_2);
			if (maker0 != null)
				maker0.getMaker().onMakerScriptEvent("1000", maker0, 0, 0);
			
			maker0 = SpawnManager.getInstance().getNpcMaker(MAKER_NAME_WIZARD_3);
			if (maker0 != null)
				maker0.getMaker().onMakerScriptEvent("1000", maker0, 0, 0);
			
			maker0 = SpawnManager.getInstance().getNpcMaker(MAKER_NAME_WIZARD_4);
			if (maker0 != null)
				maker0.getMaker().onMakerScriptEvent("1000", maker0, 0, 0);
			
			maker0 = SpawnManager.getInstance().getNpcMaker(MAKER_NAME_WIZARD_5);
			if (maker0 != null)
				maker0.getMaker().onMakerScriptEvent("1000", maker0, 0, 0);
			
			maker0 = SpawnManager.getInstance().getNpcMaker(MAKER_NAME_SELF_DES);
			if (maker0 != null)
				maker0.getMaker().onMakerScriptEvent("1000", maker0, 0, 0);
				
			
			openCloseDoor(T_DOOR_NAME_1, 0);
			openCloseDoor(T_DOOR_NAME_2, 0);
			openCloseDoor(WALL_DOOR_NAME_1, 0);
			openCloseDoor(WALL_DOOR_NAME_2, 0);
			openCloseDoor(WALL_DOOR_NAME_3, 0);
			openCloseDoor(WALL_DOOR_NAME_4, 0);
			openCloseDoor(WALL_DOOR_NAME_5, 0);
			openCloseDoor(WALL_DOOR_NAME_6, 0);
			openCloseDoor(WALL_DOOR_NAME_7, 0);
			openCloseDoor(WALL_DOOR_NAME_8, 0);
		}
	}
}
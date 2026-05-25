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
package ext.mods.gameserver.scripting.script.ai.boss.sailren;

import ext.mods.Config;
import ext.mods.gameserver.data.manager.SpawnManager;
import ext.mods.gameserver.data.manager.ZoneManager;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.model.spawn.NpcMaker;
import ext.mods.gameserver.model.zone.type.BossZone;
import ext.mods.gameserver.scripting.script.ai.individual.DefaultNpc;

public class StatueOfShilen extends DefaultNpc
{
	public StatueOfShilen()
	{
		super("ai/boss/sailren");
		
		addTalkId(32109);
	}
	
	public StatueOfShilen(String descr)
	{
		super(descr);
		
		addTalkId(32109);
	}
	
	protected final int[] _npcIds =
	{
		32109
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai1 = 0;
		npc._i_ai2 = 0;
		npc._i_ai3 = 0;
		npc._i_ai4 = 0;
		
		super.onCreated(npc);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = "";
		
		Party party0 = player.getParty();
		if (event.equalsIgnoreCase("enter"))
		{
			if (party0 != null)
			{
				if (npc._i_ai2 == 1)
				{
					if (npc._i_ai1 == 0)
					{
						if (party0.getLeader() == player)
						{
							if (Config.NEED_ITEM_SHILEN)
							{
								if (!player.getInventory().hasItems(8784))
								{
									htmltext = "statue_of_shilen003.htm";
									return htmltext;
								}
								
								takeItems(player, 8784, 1);
							}
							
							for (Player partyMember : party0.getMembers())
							{
								if (!partyMember.isInStrictRadius(player, 1000))
									return "party_range.htm";
							}
							
							npc._i_ai1 = 1;
							
							final BossZone nest = ZoneManager.getInstance().getZoneById(110011, BossZone.class);
							if (nest != null)
							{
								for (Player partyMember : party0.getMembers())
								{
									nest.allowPlayerEntry(partyMember, 30);
									partyMember.teleportTo(27549, -6638, -2008, 0);
								}
							}
							
							NpcMaker maker0 = SpawnManager.getInstance().getNpcMaker("rune20_mb2017_01m1");
							if (maker0 != null)
								maker0.getMaker().onMakerScriptEvent("11053", maker0, 0, 0);
							
							startQuestTimer("1001", npc, null, 60000);
							
							maker0 = SpawnManager.getInstance().getNpcMaker("rune20_mb2017_02m1");
							if (maker0 != null)
								maker0.getMaker().onMakerScriptEvent("11053", maker0, 0, 0);
							
							maker0 = SpawnManager.getInstance().getNpcMaker("rune20_mb2017_03m1");
							if (maker0 != null)
								maker0.getMaker().onMakerScriptEvent("11053", maker0, 0, 0);
							
							maker0 = SpawnManager.getInstance().getNpcMaker("rune16_npc2017_13m1");
							if (maker0 != null)
								maker0.getMaker().onMakerScriptEvent("11048", maker0, 0, 0);
						}
						else
							htmltext = "statue_of_shilen004.htm";
					}
					else
						htmltext = "statue_of_shilen006.htm";
				}
				else
					htmltext = "statue_of_shilen005.htm";
			}
			else
				htmltext = "statue_of_shilen002.htm";
		}
		else if (event.equalsIgnoreCase("battle_prep"))
			htmltext = "statue_of_shilen002a.htm";
		else if (event.equalsIgnoreCase("enter_details"))
			htmltext = "statue_of_shilen003a.htm";
		else if (event.equalsIgnoreCase("learn_more"))
			htmltext = "statue_of_shilen004a.htm";
		
		return htmltext;
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("1001"))
		{
			final NpcMaker maker0 = SpawnManager.getInstance().getNpcMaker("rune20_mb2017_01m1");
			if (maker0 != null)
				maker0.getMaker().onMakerScriptEvent("1001", maker0, 0, 0);
		}
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onScriptEvent(Npc npc, int eventId, int arg1, int arg2)
	{
		if (eventId == 11041)
			npc._i_ai2 = 1;
		else if (eventId == 11043)
			npc._i_ai1 = 0;
		else if (eventId == 11045)
		{
			npc._i_ai1 = 0;
			npc._i_ai2 = 0;
		}
		else if (eventId == 11047)
			npc._i_ai1 = 1;
		else if (eventId == 11050)
			npc._i_ai1 = 0;
	}
}
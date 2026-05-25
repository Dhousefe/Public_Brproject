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
package ext.mods.gameserver.scripting.script.siegablehall;

import java.text.SimpleDateFormat;
import java.util.Map.Entry;

import ext.mods.gameserver.data.sql.ClanTable;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.SpawnLocation;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.scripting.QuestState;

/**
 * In order to participate in a battle to occupy the Brigand's Hideaway clan hall, the head of a clan of level 4 or above must complete the Brigand's Hideaway quest within a certain time before the clan hall battle is started. Only the first five clans that complete the hideaway quest first can
 * participate. The number of clan members that can participate per clan is limited to 18 people. The quest to participate in the Brigand's Hideaway clan hall battle is only valid for participating in the clan hall battle on that date. Having completed the quest previously does not make it possible
 * to participate in the clan battle.<br>
 * <br>
 * Once the decision is made to participate in the clan battle, the clan leader must register the 18 clan members and select the NPC that he will protect by conversing with the herald NPC. Clans that currently occupy the clan hall must register the 18 clan members to participate in the defense.
 */
public final class BanditStronghold extends FlagWar
{
	public static final String QUEST_NAME = "Q504_CompetitionForTheBanditStronghold";
	private static final int TARLK_AMULET = 4332;
	private static final int CONTEST_CERTIFICATE = 4333;
	private static final int TROPHY_OF_ALLIANCE = 5009;
	
	public BanditStronghold()
	{
		super("siegablehall", BANDIT_STRONGHOLD);
		
		MAKER_NAME = "oren15_azit_teleporter01";
		
		ROYAL_FLAG = 35422;
		FLAG_RED = 35423;
		FLAG_YELLOW = 35424;
		FLAG_GREEN = 35425;
		FLAG_BLUE = 35426;
		FLAG_PURPLE = 35427;
		
		ALLY_1 = 35428;
		ALLY_2 = 35429;
		ALLY_3 = 35430;
		ALLY_4 = 35431;
		ALLY_5 = 35432;
		
		TELEPORT_1 = 35560;
		
		MESSENGER = 35437;
		
		OUTTER_DOORS = new int[2];
		OUTTER_DOORS[0] = 22170001;
		OUTTER_DOORS[1] = 22170002;
		
		INNER_DOORS = new int[2];
		INNER_DOORS[0] = 22170003;
		INNER_DOORS[1] = 22170004;
		
		FLAG_COORDS = new SpawnLocation[7];
		FLAG_COORDS[0] = new SpawnLocation(83699, -17468, -1774, 19048);
		FLAG_COORDS[1] = new SpawnLocation(82053, -17060, -1784, 5432);
		FLAG_COORDS[2] = new SpawnLocation(82142, -15528, -1799, 58792);
		FLAG_COORDS[3] = new SpawnLocation(83544, -15266, -1770, 44976);
		FLAG_COORDS[4] = new SpawnLocation(84609, -16041, -1769, 35816);
		FLAG_COORDS[5] = new SpawnLocation(81981, -15708, -1858, 60392);
		FLAG_COORDS[6] = new SpawnLocation(84375, -17060, -1860, 27712);
		
		CENTER = new SpawnLocation(82882, -16280, -1894, 0);
		
		attachListeners();
	}
	
	@Override
	public String getFlagHtml(int flag)
	{
		switch (flag)
		{
			case 35423:
				return "agit_oel_mahum_messeger_4a.htm";
			
			case 35424:
				return "agit_oel_mahum_messeger_4b.htm";
			
			case 35425:
				return "agit_oel_mahum_messeger_4c.htm";
			
			case 35426:
				return "agit_oel_mahum_messeger_4d.htm";
			
			case 35427:
				return "agit_oel_mahum_messeger_4e.htm";
		}
		return null;
	}
	
	@Override
	public String getAllyHtml(int ally)
	{
		switch (ally)
		{
			case 35428:
				return "agit_oel_mahum_messeger_17.htm";
			
			case 35429:
				return "agit_oel_mahum_messeger_18.htm";
			
			case 35430:
				return "agit_oel_mahum_messeger_19.htm";
			
			case 35431:
				return "agit_oel_mahum_messeger_20.htm";
			
			case 35432:
				return "agit_oel_mahum_messeger_23.htm";
		}
		return null;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		String html = null;
		
		if (npc.getNpcId() == MESSENGER)
		{
			if (!_hall.isFree())
			{
				Clan clan = ClanTable.getInstance().getClan(_hall.getOwnerId());
				
				html = getHtmlText("azit_messenger001.htm", player);
				html = html.replaceAll("%my_pledge_name%", (clan == null) ? "no owner" : clan.getName());
				html = html.replaceAll("%my_owner_name%", (clan == null) ? "no owner" : clan.getLeaderName());
			}
			else
				html = "azit_messenger002.htm";
		}
		
		if (html == null)
			return super.onFirstTalk(npc, player);
		
		return html;
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String html = event;
		Clan clan = player.getClan();
		
		if (event.equalsIgnoreCase("register"))
		{
			if (player.isOverweight())
			{
				player.sendPacket(SystemMessageId.INVENTORY_LESS_THAN_80_PERCENT);
				return "";
			}
			
			if (_hall.isWaitingBattle())
				html = "agit_oel_mahum_messeger_1.htm";
			else
			{
				html = getHtmlText("azit_messenger_q0504_09.htm", player);
				html = html.replaceAll("%next_siege%", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(_hall.getSiegeDate().getTime()));
			}
		}
		else if (event.equalsIgnoreCase("view_participants"))
			html = getRegisteredPledgeList(player);
		else if (event.equalsIgnoreCase("register_clan"))
		{
			if (!_hall.isWaitingBattle())
			{
				html = getHtmlText("azit_messenger_q0504_03.htm", player);
				html = html.replaceAll("%next_siege%", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(_hall.getSiegeDate().getTime()));
				return html;
			}
			
			if (player.getClan() != null && _hall.getOwnerId() == player.getClanId())
				html = "agit_oel_mahum_messeger_22.htm";
			else
				return getClanRegisterStatus(player, false);
		}
		else if (event.equalsIgnoreCase("register_clan_member"))
		{
			if (!_hall.isWaitingBattle())
			{
				html = getHtmlText("azit_messenger_q0504_03.htm", player);
				html = html.replaceAll("%next_siege%", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(_hall.getSiegeDate().getTime()));
				return html;
			}
			
			if (!player.isClanLeader())
				html = registerClanMember(player);
			else
				html = "agit_oel_mahum_messeger_5.htm";
		}
		else if (event.equalsIgnoreCase("register_with_adena"))
		{
			if (!_hall.isWaitingBattle())
			{
				html = getHtmlText("azit_messenger_q0504_03.htm", player);
				html = html.replaceAll("%next_siege%", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(_hall.getSiegeDate().getTime()));
				return html;
			}
			
			Clan pledge0 = player.getClan();
			if (pledge0 != null)
			{
				if (!pledge0.hasClanHall())
				{
					if (player.isClanLeader())
					{
						if (clan.getLevel() >= 4)
							return getClanRegisterStatus(player, true);
						
						html = "azit_messenger_q0504_04.htm";
					}
					else
						html = "azit_messenger_q0504_05.htm";
				}
				else
					html = "azit_messenger_q0504_10.htm";
			}
			else
				html = "azit_messenger_q0504_10.htm";
		}
		else if (event.equalsIgnoreCase("select_clan_npc") || event.equalsIgnoreCase("view_clan_npc"))
		{
			if (!_hall.isWaitingBattle())
			{
				html = getHtmlText("azit_messenger_q0504_03.htm", player);
				html = html.replaceAll("%next_siege%", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(_hall.getSiegeDate().getTime()));
				return html;
			}
			html = getNpcType(player);
		}
		else if (event.startsWith("select_npc_"))
		{
			try
			{
				final int npcId = 35427 + Integer.parseInt(event.substring("select_npc_".length()));
				
				html = setNpcType(player, npcId);
			}
			catch (NumberFormatException e)
			{
				LOGGER.error("Invalid NPC index in event: " + event);
			}
		}
		else if (event.equalsIgnoreCase("reselect_npc"))
		{
			html = getHtmlText("agit_oel_mahum_messeger_6.htm", player);
		}
		else if (event.startsWith("info_npc_"))
		{
			try
			{
				final int npcIndex = Integer.parseInt(event.substring("info_npc_".length()));
				String htmlFilename = String.format("agit_oel_mahum_messeger_%d.htm", 10 + npcIndex);
				return getHtmlText(htmlFilename, player);
			}
			catch (NumberFormatException e)
			{
				LOGGER.error("Invalid NPC index in event: " + event);
			}
		}
		
		return html;
	}
	
	private String checkAndRegisterClan(Player player)
	{
		final Clan pledge0 = player.getClan();
		
		if (!_hall.isWaitingBattle() || pledge0 == null || getAttackerClans().size() >= (5 + (_hall.getOwnerId() == 0 ? 1 : 0)))
			return "agit_oel_mahum_messeger_3.htm";
		
		if (_data.get(pledge0.getClanId()) == null)
		{
			takeItems(player, TARLK_AMULET, 30);
			takeItems(player, CONTEST_CERTIFICATE, -1);
			takeItems(player, TROPHY_OF_ALLIANCE, -1);
			QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
			if (st != null)
			{
				playSound(player, SOUND_FINISH);
				st.exitQuest(true);
			}
			
			registerClan(pledge0);
		}
		
		return getFlagHtml(_data.get(pledge0.getClanId()).flag);
	}
	
	private String getClanRegisterStatus(Player player, boolean withAdena)
	{
		String html = "";
		
		if (player.getClan() != null && _data.get(player.getClanId()) != null)
			html = getFlagHtml(_data.get(player.getClanId()).flag);
		else if (getAttackerClans().size() >= (5 + (_hall.getOwnerId() == 0 ? 1 : 0)))
			html = "agit_oel_mahum_messeger_21.htm";
		else if (!withAdena)
		{
			if (player.getClan() != null && _hall.getOwnerId() == player.getClanId())
				html = "agit_oel_mahum_messeger_22.htm";
			else if (player.getInventory().hasItems(5009))
			{
				player.destroyItemByItemId(5009, 1, true);
				html = checkAndRegisterClan(player);
			}
			else
				html = "agit_oel_mahum_messeger_24.htm";
		}
		else if (withAdena)
		{
			if (player.getClan() != null && _hall.getOwnerId() == player.getClanId())
				html = "agit_oel_mahum_messeger_22.htm";
			else
			{
				final Clan pledge0 = player.getClan();
				if (pledge0 != null)
				{
					if (!pledge0.hasClanHall())
					{
						if (player.isClanLeader())
						{
							if (pledge0.getLevel() >= 4)
							{
								if (player.getInventory().getItemCount(57) >= 200000)
								{
									player.destroyItemByItemId(57, 200000, true);
									html = checkAndRegisterClan(player);
								}
								else
									html = "agit_oel_mahum_messeger_26.htm";
							}
							else
								html = "azit_messenger_q0504_04.htm";
						}
						else
							html = "azit_messenger_q0504_05.htm";
					}
					else
						html = "azit_messenger_q0504_10.htm";
				}
				else
					html = "azit_messenger_q0504_10.htm";
			}
		}
		
		return html;
	}
	
	private String getNpcType(Player player)
	{
		String html = "";
		
		if (_hall.getOwnerId() == player.getClanId() && player.getClan() != null)
			return "agit_oel_mahum_messeger_25.htm";
		
		if (player.getClan() == null || _data.get(player.getClanId()) == null)
			html = "agit_oel_mahum_messeger_7.htm";
		else if (_data.get(player.getClanId()) != null)
		{
			if (_data.get(player.getClanId()).npc == 0)
			{
				if (player.isClanLeader())
					html = "agit_oel_mahum_messeger_6.htm";
				else
					html = "agit_oel_mahum_messeger_10.htm";
			}
			else
				return getAllyHtml(_data.get(player.getClanId()).npc);
		}
		
		return html;
	}
	
	private String setNpcType(Player player, int allyNpcId)
	{
		if (!player.isClanLeader() || !_data.containsKey(player.getClanId()))
			return "agit_oel_mahum_messeger_7.htm";
		else if (allyNpcId >= 35428 && allyNpcId <= 35432)
		{
			Clan clan = player.getClan();
			_data.get(clan.getClanId()).npc = allyNpcId;
			saveNpc(allyNpcId, clan.getClanId());
			
			return "agit_oel_mahum_messeger_9.htm";
		}
		
		return "";
	}
	
	private String getRegisteredPledgeList(Player player)
	{
		String html = getHtmlText("azit_messenger003.htm", player);
		
		int i = 0;
		for (Entry<Integer, ClanData> entry : _data.entrySet())
		{
			final Clan attacker = ClanTable.getInstance().getClan(entry.getKey());
			if (attacker == null || attacker.getClanId() == _hall.getOwnerId())
				continue;
			
			html = html.replaceAll("%clan" + i + "%", attacker.getName());
			html = html.replaceAll("%clanMem" + i + "%", String.valueOf(entry.getValue().players.size()));
			i++;
		}
		
		if (_data.size() < 5)
		{
			for (int c = 0; c < 5; c++)
			{
				html = html.replaceAll("%clan" + c + "%", "**unregistered**");
				html = html.replaceAll("%clanMem" + c + "%", "");
			}
		}
		
		return html;
	}
	
	private String registerClanMember(Player player)
	{
		String html = "";
		final Clan clan = player.getClan();
		
		if (clan == null || !_hall.isRegistering())
			html = "agit_oel_mahum_messeger_7.htm";
		else
		{
			final ClanData cd = _data.get(clan.getClanId());
			if (cd == null)
				html = "agit_oel_mahum_messeger_7.htm";
			else if (cd.players.size() >= 18)
				html = "agit_oel_mahum_messeger_8.htm";
			else
			{
				cd.players.add(player.getObjectId());
				
				saveMember(clan.getClanId(), player.getObjectId());
				
				html = "agit_oel_mahum_messeger_9.htm";
			}
		}
		
		return html;
	}
	
	@Override
	public void spawnNpcs()
	{
	}
	
	@Override
	public void unspawnNpcs()
	{
	}
}
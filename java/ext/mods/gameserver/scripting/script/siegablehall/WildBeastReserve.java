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

public final class WildBeastReserve extends FlagWar
{
	public static final String QUEST_NAME = "Q655_AGrandPlanForTamingWildBeasts";
	private static final int CRYSTAL_OF_PURITY = 8084;
	private static final int TRAINER_LICENSE = 8293;
	
	public WildBeastReserve()
	{
		super("siegablehall", BEAST_FARM);
		
		MAKER_NAME = "rune07_azit2115_11m1";
		
		ROYAL_FLAG = 35606;
		FLAG_RED = 35607;
		FLAG_YELLOW = 35608;
		FLAG_GREEN = 35609;
		FLAG_BLUE = 35610;
		FLAG_PURPLE = 35611;
		
		ALLY_1 = 35618;
		ALLY_2 = 35619;
		ALLY_3 = 35620;
		ALLY_4 = 35621;
		ALLY_5 = 35622;
		
		TELEPORT_1 = 35612;
		
		MESSENGER = 35627;
		
		FLAG_COORDS = new SpawnLocation[7];
		FLAG_COORDS[0] = new SpawnLocation(56963, -92211, -1303, 60611);
		FLAG_COORDS[1] = new SpawnLocation(58090, -91641, -1303, 47274);
		FLAG_COORDS[2] = new SpawnLocation(58908, -92556, -1303, 34450);
		FLAG_COORDS[3] = new SpawnLocation(58336, -93600, -1303, 21100);
		FLAG_COORDS[4] = new SpawnLocation(57152, -93360, -1303, 8400);
		FLAG_COORDS[5] = new SpawnLocation(59116, -93251, -1302, 31000);
		FLAG_COORDS[6] = new SpawnLocation(56432, -92864, -1303, 64000);
		
		OUTTER_DOORS = new int[2];
		OUTTER_DOORS[0] = 21150003;
		OUTTER_DOORS[1] = 21150004;
		
		INNER_DOORS = new int[2];
		INNER_DOORS[0] = 21150001;
		INNER_DOORS[1] = 21150002;
		
		CENTER = new SpawnLocation(57762, -92696, -1359, 0);
		
		attachListeners();
	}
	
	@Override
	public String getFlagHtml(int flag)
	{
		switch (flag)
		{
			case 35607:
				return "farm_kel_mahum_messenger_4a.htm";
			
			case 35608:
				return "farm_kel_mahum_messenger_4b.htm";
			
			case 35609:
				return "farm_kel_mahum_messenger_4c.htm";
			
			case 35610:
				return "farm_kel_mahum_messenger_4d.htm";
			
			case 35611:
				return "farm_kel_mahum_messenger_4e.htm";
		}
		return null;
	}
	
	@Override
	public String getAllyHtml(int ally)
	{
		switch (ally)
		{
			case 35618:
				return "farm_kel_mahum_messenger_17.htm";
			
			case 35619:
				return "farm_kel_mahum_messenger_18.htm";
			
			case 35620:
				return "farm_kel_mahum_messenger_19.htm";
			
			case 35621:
				return "farm_kel_mahum_messenger_20.htm";
			
			case 35622:
				return "farm_kel_mahum_messenger_23.htm";
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
				
				html = getHtmlText("farm_messenger001.htm", player);
				html = html.replaceAll("%my_pledge_name%", (clan == null) ? "no owner" : clan.getName());
				html = html.replaceAll("%my_owner_name%", (clan == null) ? "no owner" : clan.getLeaderName());
			}
			else
				html = "farm_messenger002.htm";
		}
		
		if (html == null)
			return super.onFirstTalk(npc, player);
		
		return html;
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String html = event;
		
		if (event.equalsIgnoreCase("register"))
		{
			if (player.isOverweight())
			{
				player.sendPacket(SystemMessageId.INVENTORY_LESS_THAN_80_PERCENT);
				return "";
			}
			
			if (_hall.isWaitingBattle())
				html = "farm_kel_mahum_messenger_1.htm";
			else
			{
				html = getHtmlText("farm_messenger_q0655_11.htm", player);
				html = html.replaceAll("%next_siege%", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(_hall.getSiegeDate().getTime()));
			}
		}
		else if (event.equalsIgnoreCase("view_participants"))
			html = getRegisteredPledgeList(player);
		else if (event.equalsIgnoreCase("register_clan"))
		{
			if (!_hall.isWaitingBattle())
			{
				html = getHtmlText("farm_messenger_q0655_11.htm", player);
				html = html.replaceAll("%next_siege%", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(_hall.getSiegeDate().getTime()));
				return html;
			}
			
			if (player.getClan() != null && _hall.getOwnerId() == player.getClanId())
				html = "farm_kel_mahum_messenger_22.htm";
			else
				return getClanRegisterStatus(player);
		}
		else if (event.equalsIgnoreCase("register_clan_member"))
		{
			if (!_hall.isWaitingBattle())
			{
				html = getHtmlText("farm_messenger_q0655_11.htm", player);
				html = html.replaceAll("%next_siege%", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(_hall.getSiegeDate().getTime()));
				return html;
			}
			
			if (!player.isClanLeader())
				html = registerClanMember(player);
			else
				html = "farm_kel_mahum_messenger_5.htm";
		}
		else if (event.equalsIgnoreCase("select_clan_npc") || event.equalsIgnoreCase("view_clan_npc"))
		{
			if (!_hall.isWaitingBattle())
			{
				html = getHtmlText("farm_messenger_q0655_11.htm", player);
				html = html.replaceAll("%next_siege%", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(_hall.getSiegeDate().getTime()));
				return html;
			}
			html = getNpcType(player);
		}
		else if (event.startsWith("select_npc_"))
		{
			try
			{
				final int npcId = 35617 + Integer.parseInt(event.substring("select_npc_".length()));
				
				html = setNpcType(player, npcId);
			}
			catch (NumberFormatException e)
			{
				LOGGER.error("Invalid NPC index in event: " + event);
			}
		}
		else if (event.equalsIgnoreCase("reselect_npc"))
			html = getHtmlText("farm_kel_mahum_messenger_6.htm", player);
		else if (event.startsWith("info_npc_"))
		{
			try
			{
				final int npcIndex = Integer.parseInt(event.substring("info_npc_".length()));
				String htmlFilename = String.format("farm_kel_mahum_messenger_%d.htm", 10 + npcIndex);
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
			return "farm_kel_mahum_messenger_3.htm";
		
		if (_data.get(pledge0.getClanId()) == null)
		{
			takeItems(player, CRYSTAL_OF_PURITY, -1);
			takeItems(player, TRAINER_LICENSE, 1);
			QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
			if (st != null)
			{
				st.exitQuest(true);
				playSound(player, SOUND_FINISH);
			}
			
			registerClan(pledge0);
		}
		
		return getFlagHtml(_data.get(pledge0.getClanId()).flag);
	}
	
	private String getClanRegisterStatus(Player player)
	{
		String html = "";
		
		if (player.getClan() != null && _data.get(player.getClanId()) != null)
			html = getFlagHtml(_data.get(player.getClanId()).flag);
		else if (getAttackerClans().size() >= (5 + (_hall.getOwnerId() == 0 ? 1 : 0)))
			html = "farm_kel_mahum_messenger_21.htm";
		else
		{
			if (player.getClan() != null && _hall.getOwnerId() == player.getClanId())
				html = "farm_kel_mahum_messenger_22.htm";
			else if (player.getInventory().hasItems(8293))
			{
				player.destroyItemByItemId(8293, 1, true);
				html = checkAndRegisterClan(player);
			}
			else
				html = "farm_kel_mahum_messenger_27.htm";
		}
		
		return html;
	}
	
	private String getNpcType(Player player)
	{
		String html = "";
		
		if (_hall.getOwnerId() == player.getClanId() && player.getClan() != null)
			return "farm_kel_mahum_messenger_25.htm";
		
		if (player.getClan() == null || _data.get(player.getClanId()) == null)
			html = "farm_kel_mahum_messenger_7.htm";
		else if (_data.get(player.getClanId()) != null)
		{
			if (_data.get(player.getClanId()).npc == 0)
			{
				if (player.isClanLeader())
					html = "farm_kel_mahum_messenger_6.htm";
				else
					html = "farm_kel_mahum_messenger_10.htm";
			}
			else
				return getAllyHtml(_data.get(player.getClanId()).npc);
		}
		
		return html;
	}
	
	private String setNpcType(Player player, int allyNpcId)
	{
		if (!player.isClanLeader() || !_data.containsKey(player.getClanId()))
			return "farm_kel_mahum_messenger_7.htm";
		else if (allyNpcId >= 35618 && allyNpcId <= 35622)
		{
			Clan clan = player.getClan();
			_data.get(clan.getClanId()).npc = allyNpcId;
			saveNpc(allyNpcId, clan.getClanId());
			
			return "farm_kel_mahum_messenger_9.htm";
		}
		
		return "";
	}
	
	private String getRegisteredPledgeList(Player player)
	{
		String html = getHtmlText("farm_messenger003.htm", player);
		
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
			html = "farm_kel_mahum_messenger_7.htm";
		else
		{
			final ClanData cd = _data.get(clan.getClanId());
			if (cd == null)
				html = "farm_kel_mahum_messenger_7.htm";
			else if (cd.players.size() >= 18)
				html = "farm_kel_mahum_messenger_8.htm";
			else
			{
				cd.players.add(player.getObjectId());
				
				saveMember(clan.getClanId(), player.getObjectId());
				
				html = "farm_kel_mahum_messenger_9.htm";
			}
		}
		
		return html;
	}
	
	@Override
	public boolean canPayRegistration()
	{
		return false;
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
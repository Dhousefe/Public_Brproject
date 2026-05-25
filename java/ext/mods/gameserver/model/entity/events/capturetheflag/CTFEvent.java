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
package ext.mods.gameserver.model.entity.events.capturetheflag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ext.mods.commons.random.Rnd;

import ext.mods.Config;
import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.manager.AntiFeedManager;
import ext.mods.gameserver.data.manager.SpawnManager;
import ext.mods.gameserver.data.xml.ItemData;
import ext.mods.gameserver.data.xml.NpcData;
import ext.mods.gameserver.enums.EventState;
import ext.mods.gameserver.enums.MessageType;
import ext.mods.gameserver.enums.Paperdoll;
import ext.mods.gameserver.enums.SayType;
import ext.mods.gameserver.enums.StatusType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.Summon;
import ext.mods.gameserver.model.actor.instance.Pet;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.entity.Events;
import ext.mods.Safedisconect.SafeDisconnectManager;
import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.Item;
import ext.mods.gameserver.model.itemcontainer.PcInventory;
import ext.mods.gameserver.model.olympiad.OlympiadManager;
import ext.mods.gameserver.model.spawn.Spawn;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.CreatureSay;
import ext.mods.gameserver.network.serverpackets.ExShowScreenMessage;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.network.serverpackets.StatusUpdate;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.skills.L2Skill;

public class CTFEvent extends Events
{
	private final String htmlPath = "html/mods/events/ctf/";
	
	private CTFEventTeam[] _teams = new CTFEventTeam[2];
	
	private EventState _state = EventState.INACTIVE;
	
	private Spawn _npcSpawn;
	private Npc _lastNpcSpawn;
	
	private Spawn _flag1Spawn;
	private Spawn _flag2Spawn;
	
	private Npc _lastFlag1Spawn;
	private Npc _lastFlag2Spawn;
	
	private Player _team1Carrier;
	private Player _team2Carrier;
	
	private ItemInstance _team1CarrierRHand;
	private ItemInstance _team2CarrierRHand;
	
	private ItemInstance _team1CarrierLHand;
	private ItemInstance _team2CarrierLHand;
	
	private Map<Integer, Boolean> _teleported = new HashMap<>();
	
	private CTFEvent()
	{
	}
	
	public void init()
	{
		AntiFeedManager.getInstance().registerEvent(AntiFeedManager.CTF_ID);
		_teams[0] = new CTFEventTeam(Config.CTF_EVENT_TEAM_1_NAME, Config.CTF_EVENT_TEAM_1_COORDINATES);
		_teams[1] = new CTFEventTeam(Config.CTF_EVENT_TEAM_2_NAME, Config.CTF_EVENT_TEAM_2_COORDINATES);
	}
	
	public boolean startParticipation()
	{
		final NpcTemplate tmpl = NpcData.getInstance().getTemplate(Config.CTF_EVENT_PARTICIPATION_NPC_ID);
		
		if (tmpl == null)
		{
			LOGGER.warn("CTFEvent: EventManager is a NullPointer -> Invalid npc id in configs?");
			return false;
		}
		
		try
		{
			_npcSpawn = new Spawn(tmpl);
			_npcSpawn.setLoc(Config.CTF_EVENT_PARTICIPATION_NPC_COORDINATES[0], Config.CTF_EVENT_PARTICIPATION_NPC_COORDINATES[1], Config.CTF_EVENT_PARTICIPATION_NPC_COORDINATES[2], Config.CTF_EVENT_PARTICIPATION_NPC_COORDINATES[3]);
			_npcSpawn.setRespawnDelay(60000);
			
			SpawnManager.getInstance().addSpawn(_npcSpawn);
			_lastNpcSpawn = _npcSpawn.doSpawn(false);
		}
		catch (Exception e)
		{
			LOGGER.warn("CTFEventEngine: exception: " + e.getMessage(), e);
			return false;
		}
		
		setState(EventState.PARTICIPATING);
		return true;
	}
	
	private List<Player> sortPlayersByLevel(List<Player> players)
	{
		return players.stream().sorted(Comparator.comparingInt(p -> -p.getStatus().getLevel())).toList();
	}
	
	public boolean startFight()
	{
		setState(EventState.STARTING);
		
		final Map<Integer, Player> allParticipants = new HashMap<>();
		
		allParticipants.putAll(_teams[0].getParticipatedPlayers());
		allParticipants.putAll(_teams[1].getParticipatedPlayers());
		
		_teams[0].cleanMe();
		_teams[1].cleanMe();
		
		Player player;
		Iterator<Player> iter;
		if (needParticipationFee())
		{
			iter = allParticipants.values().iterator();
			while (iter.hasNext())
			{
				player = iter.next();
				if (!hasParticipationFee(player))
					iter.remove();
			}
		}
		
		int[] balance =
		{
			0,
			0
		};
		int priority = 0;
		List<Player> allParticipantsSorted = new ArrayList<>(sortPlayersByLevel(new ArrayList<>(allParticipants.values())));
		while (!allParticipantsSorted.isEmpty())
		{
			Player highestLevelPlayer = allParticipantsSorted.remove(allParticipantsSorted.size() - 1);
			_teams[priority].addPlayer(highestLevelPlayer);
			balance[priority] += highestLevelPlayer.getStatus().getLevel();
			
			if (allParticipantsSorted.isEmpty())
				break;
			
			priority = 1 - priority;
			highestLevelPlayer = allParticipantsSorted.remove(allParticipantsSorted.size() - 1);
			_teams[priority].addPlayer(highestLevelPlayer);
			balance[priority] += highestLevelPlayer.getStatus().getLevel();
			
			priority = balance[0] > balance[1] ? 1 : 0;
		}
		
		if ((_teams[0].getParticipatedPlayerCount() < Config.CTF_EVENT_MIN_PLAYERS_IN_TEAMS) || (_teams[1].getParticipatedPlayerCount() < Config.CTF_EVENT_MIN_PLAYERS_IN_TEAMS))
		{
			setState(EventState.INACTIVE);
			
			for (Player players : allParticipants.values())
				players.addItem(Config.CTF_EVENT_PARTICIPATION_FEE[0], Config.CTF_EVENT_PARTICIPATION_FEE[1], true);
			
			_teams[0].cleanMe();
			_teams[1].cleanMe();
			
			unSpawnNpc();
			AntiFeedManager.getInstance().clear(AntiFeedManager.CTF_ID);
			return false;
		}
		
		if (needParticipationFee())
		{
			iter = _teams[0].getParticipatedPlayers().values().iterator();
			while (iter.hasNext())
			{
				player = iter.next();
				if (!payParticipationFee(player))
					iter.remove();
			}
			
			iter = _teams[1].getParticipatedPlayers().values().iterator();
			while (iter.hasNext())
			{
				player = iter.next();
				if (!payParticipationFee(player))
					iter.remove();
			}
		}
		
		spawnFirstHeadQuarters();
		spawnSecondHeadQuarters();
		
		closeDoors(Config.CTF_DOORS_IDS_TO_CLOSE);
		
		setState(EventState.STARTED);
		
		for (CTFEventTeam team : _teams)
		{
			for (Player playerInstance : team.getParticipatedPlayers().values())
			{
				if (playerInstance != null)
					new CTFEventTeleporter(playerInstance, team.getCoordinates(), false, false);
			}
		}
		
		return true;
	}
	
	public String calculateRewards()
	{
		if (_teams[0].getPoints() == _teams[1].getPoints())
		{
			if ((_teams[0].getParticipatedPlayerCount() == 0) || (_teams[1].getParticipatedPlayerCount() == 0))
			{
				setState(EventState.REWARDING);
				return "CTF Event: Event has ended. No team won due to inactivity!";
			}
			
			sysMsgToAllParticipants("Event has ended, both teams have tied.");
			if (Config.CTF_REWARD_TEAM_TIE)
			{
				rewardTeam(_teams[0]);
				rewardTeam(_teams[1]);
				return "CTF Event: Event has ended with both teams tying.";
			}
			
			return "CTF Event: Event has ended with both teams tying.";
		}
		
		setState(EventState.REWARDING);
		
		CTFEventTeam team = _teams[_teams[0].getPoints() > _teams[1].getPoints() ? 0 : 1];
		rewardTeam(team);
		return "CTF Event: Event finish. Team " + team.getName() + " won with " + team.getPoints() + " points.";
	}
	
	private void rewardTeam(CTFEventTeam team)
	{
		for (Player player : team.getParticipatedPlayers().values())
		{
			if (player == null)
				continue;
			
			SystemMessage systemMessage = null;
			
			for (IntIntHolder reward : Config.CTF_EVENT_REWARDS)
			{
				final PcInventory inv = player.getInventory();
				
				if (ItemData.getInstance().getTemplate(reward.getId()).isStackable())
				{
					inv.addItem(reward.getId(), reward.getValue());
					
					if (reward.getValue() > 1)
						systemMessage = SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S).addItemName(reward.getId()).addItemNumber(reward.getValue());
					else
						systemMessage = SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1).addItemName(reward.getId());
					
					player.sendPacket(systemMessage);
				}
				else
				{
					for (int i = 0; i < reward.getValue(); ++i)
					{
						inv.addItem(reward.getId(), 1);
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1).addItemName(reward.getId()));
					}
				}
			}
			
			StatusUpdate statusUpdate = new StatusUpdate(player);
			NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(0);
			
			statusUpdate.addAttribute(StatusType.CUR_LOAD, player.getCurrentWeight());
			npcHtmlMessage.setHtml(HTMLData.getInstance().getHtm(player, htmlPath + "Reward.htm"));
			player.sendPacket(statusUpdate);
			player.sendPacket(npcHtmlMessage);
		}
	}
	
	public void stopFight()
	{
		setState(EventState.INACTIVATING);
		
		unSpawnNpc();
		
		openDoors(Config.CTF_DOORS_IDS_TO_CLOSE);
		
		if (_team1Carrier != null)
			removeFlagCarrier(_team1Carrier);
		
		if (_team2Carrier != null)
			removeFlagCarrier(_team2Carrier);
		
		for (CTFEventTeam team : _teams)
		{
			for (Player player : team.getParticipatedPlayers().values())
			{
				if (player != null)
					new CTFEventTeleporter(player, Config.CTF_EVENT_PARTICIPATION_NPC_COORDINATES, false, false);
			}
		}
		
		_teams[0].cleanMe();
		_teams[1].cleanMe();
		
		_teleported = new HashMap<>();
		
		setState(EventState.INACTIVE);
		AntiFeedManager.getInstance().clear(AntiFeedManager.CTF_ID);
	}
	
	public synchronized boolean addParticipant(Player player)
	{
		if (player == null)
			return false;
		
		byte teamId = 0;
		
		if (_teams[0] == null || _teams[1] == null)
			return false;
		
		if (_teams[0].getParticipatedPlayerCount() == _teams[1].getParticipatedPlayerCount())
			teamId = (byte) (Rnd.get(2));
		else
			teamId = (byte) (_teams[0].getParticipatedPlayerCount() > _teams[1].getParticipatedPlayerCount() ? 1 : 0);
		
		return _teams[teamId].addPlayer(player);
	}
	
	public boolean removeParticipant(int objectId)
	{
		byte teamId = getParticipantTeamId(objectId);
		
		if (teamId != -1)
		{
			_teams[teamId].removePlayer(objectId);
			return true;
		}
		
		return false;
	}
	
	public boolean needParticipationFee()
	{
		return (Config.CTF_EVENT_PARTICIPATION_FEE[0] != 0) && (Config.CTF_EVENT_PARTICIPATION_FEE[1] != 0);
	}
	
	public boolean hasParticipationFee(Player player)
	{
		return player.getInventory().getItemCount(Config.CTF_EVENT_PARTICIPATION_FEE[0], -1) >= Config.CTF_EVENT_PARTICIPATION_FEE[1];
	}
	
	public boolean payParticipationFee(Player player)
	{
		return player.destroyItemByItemId(Config.CTF_EVENT_PARTICIPATION_FEE[0], Config.CTF_EVENT_PARTICIPATION_FEE[1], true);
	}
	
	public String getParticipationFee()
	{
		int itemId = Config.CTF_EVENT_PARTICIPATION_FEE[0];
		int itemNum = Config.CTF_EVENT_PARTICIPATION_FEE[1];
		
		if (itemId == 0 || itemNum == 0)
			return "-";
		
		return String.valueOf(itemNum) + " " + ItemData.getInstance().getTemplate(itemId).getName();
	}
	
	public void sysMsgToAllParticipants(String message)
	{
		CreatureSay cs = new CreatureSay(0, SayType.PARTY, "Event Manager", message);
		
		for (Player player : _teams[0].getParticipatedPlayers().values())
		{
			if (player != null)
				player.sendPacket(cs);
		}
		
		for (Player player : _teams[1].getParticipatedPlayers().values())
		{
			if (player != null)
				player.sendPacket(cs);
		}
	}
	
	private void spawnFirstHeadQuarters()
	{
		NpcTemplate tmpl = NpcData.getInstance().getTemplate(Config.CTF_EVENT_TEAM_1_HEADQUARTERS_ID);
		
		if (tmpl == null)
		{
			LOGGER.warn("CTFEvent: First Head Quater is a NullPointer -> Invalid npc id in configs?");
			return;
		}
		
		try
		{
			_flag1Spawn = new Spawn(tmpl);
			_flag1Spawn.setLoc(Config.CTF_EVENT_TEAM_1_FLAG_COORDINATES[0], Config.CTF_EVENT_TEAM_1_FLAG_COORDINATES[1], Config.CTF_EVENT_TEAM_1_FLAG_COORDINATES[2], Config.CTF_EVENT_TEAM_1_FLAG_COORDINATES[3]);
			_flag1Spawn.setRespawnDelay(60000);
			
			SpawnManager.getInstance().addSpawn(_flag1Spawn);
			
			_lastFlag1Spawn = _flag1Spawn.doSpawn(false);
			_lastFlag1Spawn.setTitle(Config.CTF_EVENT_TEAM_1_NAME);
		}
		catch (Exception e)
		{
			LOGGER.warn("SpawnFirstHeadQuaters: exception: " + e.getMessage(), e);
			return;
		}
	}
	
	private void spawnSecondHeadQuarters()
	{
		NpcTemplate tmpl = NpcData.getInstance().getTemplate(Config.CTF_EVENT_TEAM_2_HEADQUARTERS_ID);
		
		if (tmpl == null)
		{
			LOGGER.warn("CTFEvent: Second Head Quater is a NullPointer -> Invalid npc id in configs?");
			return;
		}
		
		try
		{
			_flag2Spawn = new Spawn(tmpl);
			_flag2Spawn.setLoc(Config.CTF_EVENT_TEAM_2_FLAG_COORDINATES[0], Config.CTF_EVENT_TEAM_2_FLAG_COORDINATES[1], Config.CTF_EVENT_TEAM_2_FLAG_COORDINATES[2], Config.CTF_EVENT_TEAM_2_FLAG_COORDINATES[3]);
			_flag2Spawn.setRespawnDelay(60000);
			
			SpawnManager.getInstance().addSpawn(_flag2Spawn);
			
			_lastFlag2Spawn = _flag2Spawn.doSpawn(false);
			_lastFlag2Spawn.setTitle(Config.CTF_EVENT_TEAM_2_NAME);
		}
		catch (Exception e)
		{
			LOGGER.warn("SpawnSecondHeadQuaters: exception: " + e.getMessage(), e);
			return;
		}
	}
	
	private void unSpawnNpc()
	{
		_lastNpcSpawn.deleteMe();
		SpawnManager.getInstance().deleteSpawn((Spawn) _lastNpcSpawn.getSpawn());
		_npcSpawn.doDelete();
		
		_npcSpawn = null;
		_lastNpcSpawn = null;
		
		if (_lastFlag1Spawn != null)
		{
			_lastFlag1Spawn.deleteMe();
			_lastFlag2Spawn.deleteMe();
			SpawnManager.getInstance().deleteSpawn((Spawn) _lastFlag1Spawn.getSpawn());
			SpawnManager.getInstance().deleteSpawn((Spawn) _lastFlag2Spawn.getSpawn());
			_flag1Spawn = null;
			_flag2Spawn = null;
			_lastFlag1Spawn = null;
			_lastFlag2Spawn = null;
		}
	}
	
	public void onLogin(Player player)
	{
		if ((player == null) || (!isStarting() && !isStarted()))
			return;
		
		byte teamId = getParticipantTeamId(player.getObjectId());
		if (teamId == -1)
			return;
		
		_teams[teamId].addPlayer(player);
		new CTFEventTeleporter(player, _teams[teamId].getCoordinates(), true, false);
	}
	
	public void onLogout(Player player)
	{
		if ((player != null) && (isStarting() || isStarted() || isParticipating()))
		{
			if (SafeDisconnectManager.getInstance().isSafeDisconnectActive(player) && Config.SAFEDISCONNECT_INTEGRATION_CTF)
			{
				if (playerIsCarrier(player))
					removeFlagCarrier(player);
				return;
			}
			
			if (removeParticipant(player.getObjectId()))
				player.teleportTo((Config.CTF_EVENT_PARTICIPATION_NPC_COORDINATES[0] + Rnd.get(101)) - 50, (Config.CTF_EVENT_PARTICIPATION_NPC_COORDINATES[1] + Rnd.get(101)) - 50, Config.CTF_EVENT_PARTICIPATION_NPC_COORDINATES[2], 0);
		}
	}
	
	public synchronized void onBypass(String command, Player player)
	{
		if (player == null || !isParticipating())
			return;
		
		final String htmContent;
		
		if (command.equals("ctf_event_participation"))
		{
			NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(0);
			int playerLevel = player.getStatus().getLevel();
			
			if (player.isCursedWeaponEquipped())
			{
				htmContent = HTMLData.getInstance().getHtm(player, htmlPath + "CursedWeaponEquipped.htm");
				if (htmContent != null)
					npcHtmlMessage.setHtml(htmContent);
			}
			else if (OlympiadManager.getInstance().isRegistered(player))
			{
				htmContent = HTMLData.getInstance().getHtm(player, htmlPath + "Olympiad.htm");
				if (htmContent != null)
					npcHtmlMessage.setHtml(htmContent);
			}
			else if (player.getKarma() > 0)
			{
				htmContent = HTMLData.getInstance().getHtm(player, htmlPath + "Karma.htm");
				if (htmContent != null)
					npcHtmlMessage.setHtml(htmContent);
			}
			else if ((playerLevel < Config.CTF_EVENT_MIN_LVL) || (playerLevel > Config.CTF_EVENT_MAX_LVL))
			{
				htmContent = HTMLData.getInstance().getHtm(player, htmlPath + "Level.htm");
				if (htmContent != null)
				{
					npcHtmlMessage.setHtml(htmContent);
					npcHtmlMessage.replace("%min%", String.valueOf(Config.CTF_EVENT_MIN_LVL));
					npcHtmlMessage.replace("%max%", String.valueOf(Config.CTF_EVENT_MAX_LVL));
				}
			}
			else if ((_teams[0].getParticipatedPlayerCount() == Config.CTF_EVENT_MAX_PLAYERS_IN_TEAMS) && (_teams[1].getParticipatedPlayerCount() == Config.CTF_EVENT_MAX_PLAYERS_IN_TEAMS))
			{
				htmContent = HTMLData.getInstance().getHtm(player, htmlPath + "TeamsFull.htm");
				if (htmContent != null)
				{
					npcHtmlMessage.setHtml(htmContent);
					npcHtmlMessage.replace("%max%", String.valueOf(Config.CTF_EVENT_MAX_PLAYERS_IN_TEAMS));
				}
			}
			else if ((Config.CTF_EVENT_MAX_PARTICIPANTS_PER_IP > 0) && !AntiFeedManager.getInstance().tryAddPlayer(AntiFeedManager.CTF_ID, player, Config.CTF_EVENT_MAX_PARTICIPANTS_PER_IP))
			{
				htmContent = HTMLData.getInstance().getHtm(player, htmlPath + "IPRestriction.htm");
				if (htmContent != null)
				{
					npcHtmlMessage.setHtml(htmContent);
					npcHtmlMessage.replace("%max%", String.valueOf(AntiFeedManager.getInstance().getLimit(player, Config.CTF_EVENT_MAX_PARTICIPANTS_PER_IP)));
				}
			}
			else if (!payParticipationFee(player))
			{
				htmContent = HTMLData.getInstance().getHtm(player, htmlPath + "ParticipationFee.htm");
				if (htmContent != null)
				{
					npcHtmlMessage.setHtml(htmContent);
					npcHtmlMessage.replace("%fee%", getParticipationFee());
				}
			}
			else if (addParticipant(player))
				npcHtmlMessage.setHtml(HTMLData.getInstance().getHtm(player, htmlPath + "Registered.htm"));
			else
				return;
			
			player.sendPacket(npcHtmlMessage);
		}
		else if (command.equals("ctf_event_remove_participation"))
		{
			removeParticipant(player.getObjectId());
			NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(0);
			npcHtmlMessage.setHtml(HTMLData.getInstance().getHtm(player, htmlPath + "Unregistered.htm"));
			player.sendPacket(npcHtmlMessage);
		}
	}
	
	public boolean onAction(Player player, int objectId)
	{
		if ((player == null) || !isStarted())
			return true;
		
		if (player.isGM())
			return true;
		
		byte playerTeamId = getParticipantTeamId(player.getObjectId());
		byte targetedPlayerTeamId = getParticipantTeamId(objectId);
		
		if (((playerTeamId != -1) && (targetedPlayerTeamId == -1)) || ((playerTeamId == -1) && (targetedPlayerTeamId != -1)))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		if ((playerTeamId != -1) && (targetedPlayerTeamId != -1) && (playerTeamId == targetedPlayerTeamId) && (player.getObjectId() != objectId) && !Config.CTF_EVENT_TARGET_TEAM_MEMBERS_ALLOWED)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		return true;
	}
	
	public boolean onScrollUse(int objectId)
	{
		if (!isStarted())
			return true;
		
		return isPlayerParticipant(objectId) && !Config.CTF_EVENT_SCROLL_ALLOWED;
	}
	
	public boolean onPotionUse(int objectId)
	{
		if (!isStarted())
			return true;
		
		return isPlayerParticipant(objectId) && !Config.CTF_EVENT_POTIONS_ALLOWED;
	}
	
	public boolean onEscapeUse(int objectId)
	{
		if (!isStarted())
			return true;
		
		return isPlayerParticipant(objectId);
	}
	
	public boolean onItemSummon(int objectId)
	{
		if (!isStarted())
			return true;
		
		return isPlayerParticipant(objectId) && !Config.CTF_EVENT_SUMMON_BY_ITEM_ALLOWED;
	}
	
	public void onKill(Creature killer, Player player)
	{
		if (player == null || !isStarted() || getParticipantTeamId(player.getObjectId()) == -1)
			return;
		
		new CTFEventTeleporter(player, _teams[getParticipantTeamId(player.getObjectId())].getCoordinates(), false, false);
		
		Player attackingPlayer = null;
		if (killer instanceof Pet || killer instanceof Summon)
			attackingPlayer = ((Summon) killer).getOwner();
		else if (killer instanceof Player)
			attackingPlayer = (Player) killer;
		
		if (attackingPlayer != null)
		{
			byte killerTeamId = getParticipantTeamId(attackingPlayer.getObjectId());
			byte killedTeamId = getParticipantTeamId(player.getObjectId());
			if (killerTeamId != -1 && killedTeamId != -1 && killerTeamId != killedTeamId)
				sysMsgToAllParticipants(attackingPlayer.getName() + " Hunted Player " + player.getName() + "!");
		}
	}
	
	public void onTeleported(Player player)
	{
		if (!isStarted() || player == null || !isPlayerParticipant(player.getObjectId()))
			return;
		
		if (!_teleported.containsKey(player.getObjectId()) || !_teleported.get(player.getObjectId()))
			spawnProtection(player);
		
		Map<Integer, Integer> buffs = player.isMageClass() ? Config.CTF_EVENT_MAGE_BUFFS : Config.CTF_EVENT_FIGHTER_BUFFS;
		
		if (buffs == null || buffs.isEmpty())
			return;
		
		buffs.forEach((key, value) ->
		{
			L2Skill skill = SkillTable.getInstance().getInfo(key, value);
			if (skill != null)
				skill.getEffects(player, player);
		});
		
		if (player.getParty() != null)
		{
			Party party = player.getParty();
			party.removePartyMember(player, MessageType.LEFT);
		}
		
		player.stopAllEffectsDebuff();
		_teleported.put(player.getObjectId(), true);
	}
	
	private void setState(EventState state)
	{
		synchronized (_state)
		{
			_state = state;
		}
	}
	
	public boolean isInactive()
	{
		boolean isInactive;
		
		synchronized (_state)
		{
			isInactive = _state == EventState.INACTIVE;
		}
		
		return isInactive;
	}
	
	public boolean isParticipating()
	{
		boolean isParticipating;
		
		synchronized (_state)
		{
			isParticipating = _state == EventState.PARTICIPATING;
		}
		
		return isParticipating;
	}
	
	public boolean isStarting()
	{
		boolean isStarting;
		
		synchronized (_state)
		{
			isStarting = _state == EventState.STARTING;
		}
		
		return isStarting;
	}
	
	public boolean isStarted()
	{
		boolean isStarted;
		
		synchronized (_state)
		{
			isStarted = _state == EventState.STARTED;
		}
		
		return isStarted;
	}
	
	public byte getParticipantTeamId(int objectId)
	{
		if (_teams[0] == null || _teams[1] == null)
			return -1;
		
		return (byte) (_teams[0].containsPlayer(objectId) ? 0 : (_teams[1].containsPlayer(objectId) ? 1 : -1));
	}
	
	public CTFEventTeam getParticipantTeam(int objectId)
	{
		return (_teams[0].containsPlayer(objectId) ? _teams[0] : (_teams[1].containsPlayer(objectId) ? _teams[1] : null));
	}
	
	public CTFEventTeam getParticipantEnemyTeam(int objectId)
	{
		return (_teams[0].containsPlayer(objectId) ? _teams[1] : (_teams[1].containsPlayer(objectId) ? _teams[0] : null));
	}
	
	public int[] getParticipantTeamCoordinates(int objectId)
	{
		return _teams[0].containsPlayer(objectId) ? _teams[0].getCoordinates() : (_teams[1].containsPlayer(objectId) ? _teams[1].getCoordinates() : null);
	}
	
	public boolean isPlayerParticipant(int objectId)
	{
		if (!isParticipating() && !isStarting() && !isStarted())
			return false;
		
		return _teams[0].containsPlayer(objectId) || _teams[1].containsPlayer(objectId);
	}
	
	public int[] getTeamsPlayerCounts()
	{
		return new int[]
		{
			_teams[0].getParticipatedPlayerCount(),
			_teams[1].getParticipatedPlayerCount()
		};
	}
	
	public int[] getTeamsPoints()
	{
		return new int[]
		{
			_teams[0].getPoints(),
			_teams[1].getPoints()
		};
	}
	
	public void removeFlagCarrier(Player player)
	{
		if (player.getInventory().hasItemIn(Paperdoll.RHAND))
		{
			if (player.getInventory().hasItemIn(Paperdoll.RHAND))
				player.getInventory().unequipItemInBodySlotAndRecord(Item.SLOT_R_HAND);
		}
		else
		{
			player.getInventory().unequipItemInBodySlotAndRecord(Item.SLOT_LR_HAND);
			if (player.getInventory().hasItemIn(Paperdoll.LHAND))
				player.getInventory().unequipItemInBodySlotAndRecord(Item.SLOT_L_HAND);
		}
		
		player.destroyItemByItemId(getEnemyTeamFlagId(player), 1, false);
		
		player.getInventory().unblock();
		
		final ItemInstance carrierRHand = _teams[0].containsPlayer(player.getObjectId()) ? _team1CarrierRHand : _team2CarrierRHand;
		final ItemInstance carrierLHand = _teams[0].containsPlayer(player.getObjectId()) ? _team1CarrierLHand : _team2CarrierLHand;
		if ((carrierRHand != null) && (player.getInventory().getItemByItemId(carrierRHand.getItemId()) != null))
			player.getInventory().equipItem(carrierRHand);
		
		if ((carrierLHand != null) && (player.getInventory().getItemByItemId(carrierLHand.getItemId()) != null))
			player.getInventory().equipItem(carrierLHand);
		
		setCarrierUnequippedWeapons(player, null, null);
		
		if (_teams[0].containsPlayer(player.getObjectId()))
			_team1Carrier = null;
		else
			_team2Carrier = null;
		
		player.broadcastUserInfo();
	}
	
	public void setTeamCarrier(Player player)
	{
		if (_teams[0].containsPlayer(player.getObjectId()))
			_team1Carrier = player;
		else
			_team2Carrier = player;
	}
	
	public Player getTeamCarrier(Player player)
	{
		if (((_teams[0].containsPlayer(player.getObjectId()) == true) && (_team1Carrier != null) && (!_team1Carrier.isOnline() || ((_teams[1].containsPlayer(player.getObjectId()) == true) && (_team2Carrier != null) && (!_team2Carrier.isOnline())))))
		{
			player.destroyItemByItemId(getEnemyTeamFlagId(player), 1, false);
			return null;
		}
		
		return (_teams[0].containsPlayer(player.getObjectId()) ? _team1Carrier : _team2Carrier);
	}
	
	public Player getEnemyCarrier(Player player)
	{
		if (((_teams[0].containsPlayer(player.getObjectId()) == true) && (_team2Carrier != null) && (!_team2Carrier.isOnline() || ((_teams[1].containsPlayer(player.getObjectId()) == true) && (_team1Carrier != null) && (!_team1Carrier.isOnline())))))
		{
			player.destroyItemByItemId(getEnemyTeamFlagId(player), 1, false);
			return null;
		}
		
		return (_teams[0].containsPlayer(player.getObjectId()) ? _team2Carrier : _team1Carrier);
	}
	
	public boolean playerIsCarrier(Player player)
	{
		return ((player == _team1Carrier) || (player == _team2Carrier)) ? true : false;
	}
	
	public int getEnemyTeamFlagId(Player player)
	{
		return (_teams[0].containsPlayer(player.getObjectId()) ? Config.CTF_EVENT_TEAM_2_FLAG : Config.CTF_EVENT_TEAM_1_FLAG);
	}
	
	public void setCarrierUnequippedWeapons(Player player, ItemInstance itemRight, ItemInstance itemLeft)
	{
		if (_teams[0].containsPlayer(player.getObjectId()))
		{
			_team1CarrierRHand = itemRight;
			_team1CarrierLHand = itemLeft;
		}
		else
		{
			_team2CarrierRHand = itemRight;
			_team2CarrierLHand = itemLeft;
		}
	}
	
	public void broadcastScreenMessage(String message, int duration)
	{
		for (CTFEventTeam team : _teams)
		{
			for (Player player : team.getParticipatedPlayers().values())
			{
				if (player != null)
					player.sendPacket(new ExShowScreenMessage(message, duration * 1000));
			}
		}
	}
	
	public static final CTFEvent getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final CTFEvent INSTANCE = new CTFEvent();
	}
}
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
package ext.mods.gameserver.model.entity.events.teamvsteam;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ext.mods.commons.pool.ThreadPool;
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
import ext.mods.gameserver.enums.SayType;
import ext.mods.gameserver.enums.StatusType;
import ext.mods.gameserver.enums.TeamType;
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
import ext.mods.gameserver.network.serverpackets.UserInfo;
import ext.mods.gameserver.skills.L2Skill;

public class TvTEvent extends Events
{
	private final String htmlPath = "html/mods/events/tvt/";
	
	public TvTEventTeam[] _teams = new TvTEventTeam[2];
	
	private EventState _state = EventState.INACTIVE;
	
	private Spawn _npcSpawn;
	
	private Npc _lastNpcSpawn;
	
	private Map<Integer, Boolean> _teleported = new HashMap<>();
	
	private TvTEvent()
	{
	}
	
	public void init()
	{
		AntiFeedManager.getInstance().registerEvent(AntiFeedManager.TVT_ID);
		_teams[0] = new TvTEventTeam(Config.TVT_EVENT_TEAM_1_NAME, Config.TVT_EVENT_TEAM_1_COORDINATES);
		_teams[1] = new TvTEventTeam(Config.TVT_EVENT_TEAM_2_NAME, Config.TVT_EVENT_TEAM_2_COORDINATES);
	}
	
	public boolean startParticipation()
	{
		NpcTemplate tmpl = NpcData.getInstance().getTemplate(Config.TVT_EVENT_PARTICIPATION_NPC_ID);
		
		if (tmpl == null)
		{
			LOGGER.warn("TvTEvent.startParticipation(): NpcTemplate is a NullPointer -> Invalid npc id in Configs?");
			return false;
		}
		
		try
		{
			_npcSpawn = new Spawn(tmpl);
			
			_npcSpawn.setLoc(Config.TVT_EVENT_PARTICIPATION_NPC_COORDINATES[0], Config.TVT_EVENT_PARTICIPATION_NPC_COORDINATES[1], Config.TVT_EVENT_PARTICIPATION_NPC_COORDINATES[2], Config.TVT_EVENT_PARTICIPATION_NPC_COORDINATES[3]);
			_npcSpawn.setRespawnDelay(1);
			
			SpawnManager.getInstance().addSpawn(_npcSpawn);
			_lastNpcSpawn = _npcSpawn.doSpawn(false);
		}
		catch (Exception e)
		{
			LOGGER.warn("TvTEvent.startParticipation(): exception: " + e.getMessage(), e);
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
		
		Map<Integer, Player> allParticipants = new HashMap<>();
		allParticipants.putAll(_teams[0].getParticipatedPlayers());
		allParticipants.putAll(_teams[1].getParticipatedPlayers());
		_teams[0].cleanMe();
		_teams[1].cleanMe();
		
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
		
		if ((_teams[0].getParticipatedPlayerCount() < Config.TVT_EVENT_MIN_PLAYERS_IN_TEAMS) || (_teams[1].getParticipatedPlayerCount() < Config.TVT_EVENT_MIN_PLAYERS_IN_TEAMS))
		{
			setState(EventState.INACTIVE);
			
			for (Player players : allParticipants.values())
				players.addItem(Config.TVT_EVENT_PARTICIPATION_FEE[0], Config.TVT_EVENT_PARTICIPATION_FEE[1], true);
			
			_teams[0].cleanMe();
			_teams[1].cleanMe();
			
			unSpawnNpc();
			AntiFeedManager.getInstance().clear(AntiFeedManager.TVT_ID);
			return false;
		}
		
		closeDoors(Config.TVT_DOORS_IDS_TO_CLOSE);
		
		setState(EventState.STARTED);
		
		for (TvTEventTeam team : _teams)
		{
			for (Player player : team.getParticipatedPlayers().values())
			{
				if (player != null)
				{
					new TvTEventTeleporter(player, team.getCoordinates(), false, false);
					if (Config.TVT_EVENT_ON_KILL.equalsIgnoreCase("title") || Config.TVT_EVENT_ON_KILL.equalsIgnoreCase("pmtitle"))
					{
						player._originalTitle = player.getTitle();
						player.setTitle("Kills: " + player.getPointScore());
						player.broadcastTitleInfo();
					}
					player.sendPacket(new ExShowScreenMessage("TvT Afk system is started, if you stay Afk you will be kicked!", 6000));
				}
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
				
				return "Team vs Team: Event has ended. No team won due to inactivity!";
			}
			
			sysMsgToAllParticipants("Event has ended, both teams have tied.");
			if (Config.TVT_REWARD_TEAM_TIE)
			{
				rewardTeam(_teams[0]);
				rewardTeam(_teams[1]);
				return "Team vs Team: Event has ended with both teams tying.";
			}
			return "Team vs Team: Event has ended with both teams tying.";
		}
		
		setState(EventState.REWARDING);
		
		TvTEventTeam team = _teams[_teams[0].getPoints() > _teams[1].getPoints() ? 0 : 1];
		rewardTeam(team);
		return "Team vs Team: Event finish! Team " + team.getName() + " won with " + team.getPoints() + " kills!";
	}
	
	private void rewardTeam(TvTEventTeam team)
	{
		for (Player player : team.getParticipatedPlayers().values())
		{
			if (player == null)
				continue;
			
			if (Config.TVT_REWARD_PLAYER)
				if (!team.onScoredPlayer(player.getObjectId()))
					continue;
				
			SystemMessage systemMessage = null;
			
			for (IntIntHolder reward : Config.TVT_EVENT_REWARDS)
			{
				PcInventory inv = player.getInventory();
				
				if (ItemData.getInstance().getTemplate(reward.getId()).isStackable())
				{
					inv.addItem(reward.getId(), reward.getValue());
					
					if (reward.getValue() > 1)
						systemMessage = SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S).addItemName(reward.getId()).addItemNumber(reward.getValue());
					else
						systemMessage = SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1).addItemName(reward.getValue());
					
					player.sendPacket(systemMessage);
				}
				else
				{
					for (int i = 0; i < reward.getValue(); ++i)
					{
						inv.addItem(reward.getId(), 1);
						systemMessage = SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1);
						systemMessage.addItemName(reward.getId());
						player.sendPacket(systemMessage);
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
		
		openDoors(Config.TVT_DOORS_IDS_TO_CLOSE);
		
		for (TvTEventTeam team : _teams)
		{
			for (Player player : team.getParticipatedPlayers().values())
			{
				if (player != null)
				{
					new TvTEventTeleporter(player, Config.TVT_EVENT_PARTICIPATION_NPC_COORDINATES, false, false);
					player.clearPoints();
					if (Config.TVT_EVENT_ON_KILL.equalsIgnoreCase("title") || Config.TVT_EVENT_ON_KILL.equalsIgnoreCase("pmtitle"))
					{
						ThreadPool.schedule(() ->
						{
							player.setTitle(player._originalTitle);
							player.broadcastTitleInfo();
							player.clearPoints();
						}, Config.TVT_EVENT_START_LEAVE_TELEPORT_DELAY * 1000);
					}
				}
			}
			
			team.cleanMe();
		}
		
		_teams[0].cleanMe();
		_teams[1].cleanMe();
		
		_teleported = new HashMap<>();
		
		setState(EventState.INACTIVE);
		AntiFeedManager.getInstance().clear(AntiFeedManager.TVT_ID);
	}
	
	public synchronized boolean addParticipant(Player player)
	{
		if (player == null)
			return false;
		
		byte teamId = 0;
		
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
	
	public boolean payParticipationFee(Player player)
	{
		return player.destroyItemByItemId(Config.TVT_EVENT_PARTICIPATION_FEE[0], Config.TVT_EVENT_PARTICIPATION_FEE[1], true);
	}
	
	public String getParticipationFee()
	{
		int itemId = Config.TVT_EVENT_PARTICIPATION_FEE[0];
		int itemNum = Config.TVT_EVENT_PARTICIPATION_FEE[1];
		
		if ((itemId == 0) || (itemNum == 0))
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
	
	private void unSpawnNpc()
	{
		_lastNpcSpawn.deleteMe();
		SpawnManager.getInstance().deleteSpawn((Spawn) _lastNpcSpawn.getSpawn());
		_npcSpawn.doDelete();
		
		_npcSpawn = null;
		_lastNpcSpawn = null;
	}
	
	public void onLogin(Player player)
	{
		if ((player == null) || (!isStarting() && !isStarted()))
			return;
		
		byte teamId = getParticipantTeamId(player.getObjectId());
		
		if (teamId == -1)
			return;
		
		_teams[teamId].addPlayer(player);
		new TvTEventTeleporter(player, _teams[teamId].getCoordinates(), true, false);
	}
	
	public void onLogout(Player player)
	{
		if ((player != null) && (isStarting() || isStarted() || isParticipating()))
		{
			if (SafeDisconnectManager.getInstance().isSafeDisconnectActive(player) && Config.SAFEDISCONNECT_INTEGRATION_TVT)
				return;
			
			if (removeParticipant(player.getObjectId()))
			{
				player.teleportTo((Config.TVT_EVENT_PARTICIPATION_NPC_COORDINATES[0] + Rnd.get(101)) - 50, (Config.TVT_EVENT_PARTICIPATION_NPC_COORDINATES[1] + Rnd.get(101)) - 50, Config.TVT_EVENT_PARTICIPATION_NPC_COORDINATES[2], 0);
				player.setTeam(TeamType.NONE);
			}
			
			if (Config.TVT_EVENT_ON_KILL.equalsIgnoreCase("title") || Config.TVT_EVENT_ON_KILL.equalsIgnoreCase("pmtitle"))
			{
				player.setTitle(player._originalTitle);
				player.broadcastTitleInfo();
			}
		}
	}
	
	public synchronized void onBypass(String command, Player player)
	{
		if (player == null || !isParticipating())
			return;
		
		final String htmContent;
		
		if (command.equals("tvt_event_participation"))
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
			else if (Config.DISABLE_ID_CLASSES_TVT.contains(player.getClassId().getId()))
			{
				htmContent = HTMLData.getInstance().getHtm(player, htmlPath + "Class.htm");
				if (htmContent != null)
					npcHtmlMessage.setHtml(htmContent);
			}
			else if ((playerLevel < Config.TVT_EVENT_MIN_LVL) || (playerLevel > Config.TVT_EVENT_MAX_LVL))
			{
				htmContent = HTMLData.getInstance().getHtm(player, htmlPath + "Level.htm");
				if (htmContent != null)
				{
					npcHtmlMessage.setHtml(htmContent);
					npcHtmlMessage.replace("%min%", String.valueOf(Config.TVT_EVENT_MIN_LVL));
					npcHtmlMessage.replace("%max%", String.valueOf(Config.TVT_EVENT_MAX_LVL));
				}
			}
			else if ((_teams[0].getParticipatedPlayerCount() == Config.TVT_EVENT_MAX_PLAYERS_IN_TEAMS) && (_teams[1].getParticipatedPlayerCount() == Config.TVT_EVENT_MAX_PLAYERS_IN_TEAMS))
			{
				htmContent = HTMLData.getInstance().getHtm(player, htmlPath + "TeamsFull.htm");
				if (htmContent != null)
				{
					npcHtmlMessage.setHtml(htmContent);
					npcHtmlMessage.replace("%max%", String.valueOf(Config.TVT_EVENT_MAX_PLAYERS_IN_TEAMS));
				}
			}
			else if ((Config.TVT_EVENT_MAX_PARTICIPANTS_PER_IP > 0) && !AntiFeedManager.getInstance().tryAddPlayer(AntiFeedManager.TVT_ID, player, Config.TVT_EVENT_MAX_PARTICIPANTS_PER_IP))
			{
				htmContent = HTMLData.getInstance().getHtm(player, htmlPath + "IPRestriction.htm");
				if (htmContent != null)
				{
					npcHtmlMessage.setHtml(htmContent);
					npcHtmlMessage.replace("%max%", String.valueOf(AntiFeedManager.getInstance().getLimit(player, Config.TVT_EVENT_MAX_PARTICIPANTS_PER_IP)));
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
		else if (command.equals("tvt_event_remove_participation"))
		{
			removeParticipant(player.getObjectId());
			
			if (Config.TVT_EVENT_MAX_PARTICIPANTS_PER_IP > 0)
				AntiFeedManager.getInstance().removePlayer(AntiFeedManager.TVT_ID, player);
			
			NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(0);
			npcHtmlMessage.setHtml(HTMLData.getInstance().getHtm(player, htmlPath + "Unregistered.htm"));
			player.sendPacket(npcHtmlMessage);
		}
	}
	
	public boolean onAction(Player player, int objectId)
	{
		if (player == null || !isStarted())
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
		
		if ((playerTeamId != -1) && (targetedPlayerTeamId != -1) && (playerTeamId == targetedPlayerTeamId) && (player.getObjectId() != objectId) && !Config.TVT_EVENT_TARGET_TEAM_MEMBERS_ALLOWED)
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
		
		return isPlayerParticipant(objectId) && !Config.TVT_EVENT_SCROLL_ALLOWED;
	}
	
	public boolean onPotionUse(int objectId)
	{
		if (!isStarted())
			return true;
		
		return isPlayerParticipant(objectId) && !Config.TVT_EVENT_POTIONS_ALLOWED;
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
		
		return isPlayerParticipant(objectId) && !Config.TVT_EVENT_SUMMON_BY_ITEM_ALLOWED;
	}
	
	public void onKill(Creature killer, Player player)
	{
		if (player == null || !isStarted())
			return;
		
		byte killedTeamId = getParticipantTeamId(player.getObjectId());
		if (killedTeamId == -1)
			return;
		
		new TvTEventTeleporter(player, _teams[killedTeamId].getCoordinates(), false, false);
		
		Player attackingPlayer = null;
		if (killer instanceof Pet || killer instanceof Summon)
			attackingPlayer = ((Summon) killer).getOwner();
		else if (killer instanceof Player)
			attackingPlayer = (Player) killer;
		
		if (attackingPlayer != null)
		{
			byte killerTeamId = getParticipantTeamId(attackingPlayer.getObjectId());
			if (killerTeamId != -1 && killerTeamId != killedTeamId)
			{
				TvTEventTeam killerTeam = _teams[killerTeamId];
				killerTeam.increasePoints();
				killerTeam.increasePoints(attackingPlayer.getObjectId());
				attackingPlayer.sendPacket(new UserInfo(attackingPlayer));
				
				String killMessage = attackingPlayer.getName() + " Hunted Player " + player.getName() + "!";
				switch (Config.TVT_EVENT_ON_KILL.toLowerCase())
				{
					case "pm":
						sysMsgToAllParticipants(killMessage);
						break;
					case "title":
					case "pmtitle":
						attackingPlayer.increasePointScore();
						attackingPlayer.setTitle("Kills: " + attackingPlayer.getPointScore());
						attackingPlayer.broadcastTitleInfo();
						if (Config.TVT_EVENT_ON_KILL.equalsIgnoreCase("pmtitle"))
							sysMsgToAllParticipants(killMessage);
						break;
				}
			}
		}
	}
	
	public void onTeleported(Player player)
	{
		if (!isStarted() || (player == null) || !isPlayerParticipant(player.getObjectId()))
			return;
		
		if (!_teleported.containsKey(player.getObjectId()) || !_teleported.get(player.getObjectId()))
			spawnProtection(player);
		
		Map<Integer, Integer> buffs = player.isMageClass() ? Config.TVT_EVENT_MAGE_BUFFS : Config.TVT_EVENT_FIGHTER_BUFFS;
		
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
		
		TvTAntiAFK.getInstance();
		
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
		return (byte) (_teams[0].containsPlayer(objectId) ? 0 : (_teams[1].containsPlayer(objectId) ? 1 : -1));
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
	
	public static final TvTEvent getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final TvTEvent INSTANCE = new TvTEvent();
	}
}
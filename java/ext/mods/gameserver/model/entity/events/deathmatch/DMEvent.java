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
package ext.mods.gameserver.model.entity.events.deathmatch;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

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
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.Summon;
import ext.mods.gameserver.model.actor.instance.Pet;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.entity.Events;
import ext.mods.Safedisconect.SafeDisconnectManager;
import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.model.itemcontainer.PcInventory;
import ext.mods.gameserver.model.spawn.Spawn;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.CreatureSay;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.network.serverpackets.StatusUpdate;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.skills.L2Skill;

public class DMEvent extends Events
{
	private final String htmlPath = "html/mods/events/dm/";
	
	private EventState _state = EventState.INACTIVE;
	
	private Spawn _npcSpawn;
	
	private Npc _lastNpcSpawn;
	
	private Map<Integer, DMPlayer> _player = new HashMap<>();
	private Map<Integer, Boolean> _teleported = new HashMap<>();
	
	public DMEvent()
	{
	}
	
	public void init()
	{
		AntiFeedManager.getInstance().registerEvent(AntiFeedManager.DM_ID);
	}
	
	public boolean startParticipation()
	{
		NpcTemplate tmpl = NpcData.getInstance().getTemplate(Config.DM_EVENT_PARTICIPATION_NPC_ID);
		
		if (tmpl == null)
		{
			LOGGER.warn("DMEvent.startParticipation(): NpcTemplate is a NullPointer -> Invalid npc id in configs?");
			return false;
		}
		
		try
		{
			_npcSpawn = new Spawn(tmpl);
			_npcSpawn.setLoc(Config.DM_EVENT_PARTICIPATION_NPC_COORDINATES[0], Config.DM_EVENT_PARTICIPATION_NPC_COORDINATES[1], Config.DM_EVENT_PARTICIPATION_NPC_COORDINATES[2], Config.DM_EVENT_PARTICIPATION_NPC_COORDINATES[3]);
			_npcSpawn.setRespawnDelay(1);
			
			SpawnManager.getInstance().addSpawn(_npcSpawn);
			_lastNpcSpawn = _npcSpawn.doSpawn(false);
		}
		catch (Exception e)
		{
			LOGGER.warn("DMEvent.startParticipation(): exception: " + e.getMessage(), e);
			return false;
		}
		
		setState(EventState.PARTICIPATING);
		return true;
	}
	
	public boolean startFight()
	{
		setState(EventState.STARTING);
		
		if (getPlayerCounts() < Config.DM_EVENT_MIN_PLAYERS)
		{
			setState(EventState.INACTIVE);
			
			for (DMPlayer player : _player.values())
				player.getPlayer().addItem(Config.DM_EVENT_PARTICIPATION_FEE[0], Config.DM_EVENT_PARTICIPATION_FEE[1], true);
			
			_player.clear();
			
			unSpawnNpc();
			AntiFeedManager.getInstance().clear(AntiFeedManager.DM_ID);
			return false;
		}
		
		closeDoors(Config.DM_DOORS_IDS_TO_CLOSE);
		
		setState(EventState.STARTED);
		
		for (DMPlayer player : _player.values())
		{
			if (player != null)
				new DMEventTeleporter(player.getPlayer(), false, false);
		}
		
		return true;
	}
	
	public TreeSet<DMPlayer> orderPosition(Collection<DMPlayer> listPlayer)
	{
		TreeSet<DMPlayer> players = new TreeSet<>(Comparator.comparing(DMPlayer::getPoints, Comparator.reverseOrder()).thenComparing(DMPlayer::getDeath).thenComparing(DMPlayer::getHexCode));
		players.addAll(listPlayer);
		return players;
	}
	
	public String calculateRewards()
	{
		TreeSet<DMPlayer> players = orderPosition(_player.values());
		String msg = "";
		
		for (int i = 0; i < Config.DM_REWARD_FIRST_PLAYERS; i++)
		{
			if (players.isEmpty())
				break;
			
			DMPlayer player = players.first();
			
			if (player.getPoints() == 0)
				break;
			
			rewardPlayer(player, i + 1);
			players.remove(player);
			int playerPointPrev = player.getPoints();
			
			while (!players.isEmpty())
			{
				if (player.getPoints() != playerPointPrev)
					break;
				
				rewardPlayer(player, i + 1);
				players.remove(player);
				msg += " Player: " + player.getPlayer().getName();
				msg += " Killed: " + player.getPoints();
				msg += "\n";
				
				if (!Config.DM_REWARD_PLAYERS_TIE)
					break;
			}
		}
		
		setState(EventState.REWARDING);
		
		return "Deathmatch: ended, thanks to everyone who participated!\nWinner(s):\n" + msg;
	}
	
	private void rewardPlayer(DMPlayer dmplayer, int pos)
	{
		Player player = dmplayer.getPlayer();
		
		if (player == null)
			return;
		
		SystemMessage systemMessage = null;
		
		List<int[]> rewards = Config.DM_EVENT_REWARDS.get(pos);
		
		for (int[] reward : rewards)
		{
			PcInventory inv = player.getInventory();
			
			if (ItemData.getInstance().getTemplate(reward[0]).isStackable())
			{
				inv.addItem(reward[0], reward[1]);
				
				if (reward[1] > 1)
					systemMessage = SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S).addItemName(reward[0]).addItemNumber(reward[1]);
				else
					systemMessage = SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1).addItemName(reward[0]);
				
				player.sendPacket(systemMessage);
			}
			else
			{
				for (int i = 0; i < reward[1]; ++i)
				{
					inv.addItem(reward[0], 1);
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1).addItemName(reward[0]));
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
	
	public void stopFight()
	{
		setState(EventState.INACTIVATING);
		
		unSpawnNpc();
		
		openDoors(Config.DM_DOORS_IDS_TO_CLOSE);
		
		String[] topPositions;
		String htmltext = "";
		if (Config.DM_SHOW_TOP_RANK)
		{
			topPositions = getFirstPosition(Config.DM_TOP_RANK);
			Boolean c = true;
			String c1 = "D9CC46";
			String c2 = "FFFFFF";
			if (topPositions != null)
				for (int i = 0; i < topPositions.length; i++)
				{
					String color = (c ? c1 : c2);
					String[] row = topPositions[i].split("\\,");
					htmltext += "<tr>";
					htmltext += "<td width=\"35\" align=\"center\"><font color=\"" + color + "\">" + String.valueOf(i + 1) + "</font></td>";
					htmltext += "<td width=\"100\" align=\"left\"><font color=\"" + color + "\">" + row[0] + "</font></td>";
					htmltext += "<td width=\"125\" align=\"right\"><font color=\"" + color + "\">" + row[1] + "</font></td>";
					htmltext += "</tr>";
					c = !c;
				}
		}
		
		for (DMPlayer player : _player.values())
		{
			if (player != null)
			{
				Player activeChar = player.getPlayer();
				if (Config.DM_SHOW_TOP_RANK)
				{
					NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(0);
					npcHtmlMessage.setHtml(HTMLData.getInstance().getHtm(activeChar, htmlPath + "TopRank.htm"));
					npcHtmlMessage.replace("%toprank%", htmltext);
					player.getPlayer().sendPacket(npcHtmlMessage);
				}
				
				new DMEventTeleporter(player.getPlayer(), Config.DM_EVENT_PARTICIPATION_NPC_COORDINATES, false, false);
			}
		}
		
		_teleported = new HashMap<>();
		_player = new HashMap<>();
		
		setState(EventState.INACTIVE);
		AntiFeedManager.getInstance().clear(AntiFeedManager.DM_ID);
	}
	
	public synchronized boolean addParticipant(Player player)
	{
		if (player == null)
			return false;
		
		if (isPlayerParticipant(player))
			return false;
		
		String hexCode = hexToString(generateHex(16));
		_player.put(player.getObjectId(), new DMPlayer(player, hexCode));
		return true;
	}
	
	public boolean isPlayerParticipant(Player player)
	{
		return player != null && _player.containsKey(player.getObjectId());
	}
	
	public boolean isPlayerParticipant(int objectId)
	{
		Player player = World.getInstance().getPlayer(objectId);
		if (player == null)
			return false;
		
		return isPlayerParticipant(player);
	}
	
	public boolean removeParticipant(Player player)
	{
		return player != null && _player.remove(player.getObjectId()) != null;
	}
	
	public boolean payParticipationFee(Player player)
	{
		return player.destroyItemByItemId(Config.DM_EVENT_PARTICIPATION_FEE[0], Config.DM_EVENT_PARTICIPATION_FEE[1], true);
	}
	
	public String getParticipationFee()
	{
		int itemId = Config.DM_EVENT_PARTICIPATION_FEE[0];
		int itemNum = Config.DM_EVENT_PARTICIPATION_FEE[1];
		
		if (itemId == 0 || itemNum == 0)
			return "-";
		
		return String.valueOf(itemNum) + " " + ItemData.getInstance().getTemplate(itemId).getName();
	}
	
	public void sysMsgToAllParticipants(String message)
	{
		CreatureSay cs = new CreatureSay(0, SayType.PARTY, "Event Manager", message);
		
		for (DMPlayer player : _player.values())
			if (player != null)
				player.getPlayer().sendPacket(cs);
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
		if (player == null || (!isStarting() && !isStarted()))
			return;
		
		if (!isPlayerParticipant(player))
			return;
		
		new DMEventTeleporter(player, false, false);
	}
	
	public void onLogout(Player player)
	{
		if (player != null && (isStarting() || isStarted() || isParticipating()))
		{
			if (SafeDisconnectManager.getInstance().isSafeDisconnectActive(player) && Config.SAFEDISCONNECT_INTEGRATION_DM)
				return;
			
			if (removeParticipant(player))
				player.teleportTo(Config.DM_EVENT_PARTICIPATION_NPC_COORDINATES[0] + Rnd.get(101) - 50, Config.DM_EVENT_PARTICIPATION_NPC_COORDINATES[1] + Rnd.get(101) - 50, Config.DM_EVENT_PARTICIPATION_NPC_COORDINATES[2], 0);
		}
	}
	
	public synchronized void onBypass(String command, Player player)
	{
		if (player == null || !isParticipating())
			return;
		
		final String htmContent;
		
		if (command.equals("dm_event_participation"))
		{
			NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(0);
			int playerLevel = player.getStatus().getLevel();
			
			if (player.isCursedWeaponEquipped())
			{
				htmContent = HTMLData.getInstance().getHtm(player, htmlPath + "CursedWeaponEquipped.htm");
				if (htmContent != null)
					npcHtmlMessage.setHtml(htmContent);
			}
			else if (player.isInOlympiadMode())
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
			else if (Config.DISABLE_ID_CLASSES_DM.contains(player.getClassId().getId()))
			{
				htmContent = HTMLData.getInstance().getHtm(player, htmlPath + "Class.htm");
				if (htmContent != null)
					npcHtmlMessage.setHtml(htmContent);
			}
			else if (playerLevel < Config.DM_EVENT_MIN_LVL || playerLevel > Config.DM_EVENT_MAX_LVL)
			{
				htmContent = HTMLData.getInstance().getHtm(player, htmlPath + "Level.htm");
				if (htmContent != null)
				{
					npcHtmlMessage.setHtml(htmContent);
					npcHtmlMessage.replace("%min%", String.valueOf(Config.DM_EVENT_MIN_LVL));
					npcHtmlMessage.replace("%max%", String.valueOf(Config.DM_EVENT_MAX_LVL));
				}
			}
			else if (getPlayerCounts() == Config.DM_EVENT_MAX_PLAYERS)
			{
				htmContent = HTMLData.getInstance().getHtm(player, htmlPath + "Full.htm");
				if (htmContent != null)
				{
					npcHtmlMessage.setHtml(htmContent);
					npcHtmlMessage.replace("%max%", String.valueOf(Config.DM_EVENT_MAX_PLAYERS));
				}
			}
			else if ((Config.DM_EVENT_MAX_PARTICIPANTS_PER_IP > 0) && !AntiFeedManager.getInstance().tryAddPlayer(AntiFeedManager.DM_ID, player, Config.DM_EVENT_MAX_PARTICIPANTS_PER_IP))
			{
				htmContent = HTMLData.getInstance().getHtm(player, htmlPath + "IPRestriction.htm");
				if (htmContent != null)
				{
					npcHtmlMessage.setHtml(htmContent);
					npcHtmlMessage.replace("%max%", String.valueOf(AntiFeedManager.getInstance().getLimit(player, Config.DM_EVENT_MAX_PARTICIPANTS_PER_IP)));
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
			else if (isPlayerParticipant(player))
				npcHtmlMessage.setHtml(HTMLData.getInstance().getHtm(player, htmlPath + "Registered.htm"));
			else if (addParticipant(player))
				npcHtmlMessage.setHtml(HTMLData.getInstance().getHtm(player, htmlPath + "Registered.htm"));
			else
				return;
			
			player.sendPacket(npcHtmlMessage);
		}
		else if (command.equals("dm_event_remove_participation"))
		{
			if (isPlayerParticipant(player))
			{
				removeParticipant(player);
				
				NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(0);
				
				npcHtmlMessage.setHtml(HTMLData.getInstance().getHtm(player, htmlPath + "Unregistered.htm"));
				player.sendPacket(npcHtmlMessage);
			}
		}
	}
	
	public boolean onAction(Player player, int objectId)
	{
		if (player == null || !isStarted())
			return true;
		
		if (player.isGM())
			return true;
		
		if (!isPlayerParticipant(player) && isPlayerParticipant(objectId))
			return false;
		
		if (isPlayerParticipant(player) && !isPlayerParticipant(objectId))
			return false;
		
		return true;
	}
	
	public boolean onScrollUse(int objectId)
	{
		if (!isStarted())
			return true;
		
		return isPlayerParticipant(objectId) && !Config.DM_EVENT_SCROLL_ALLOWED;
	}
	
	public boolean onPotionUse(int objectId)
	{
		if (!isStarted())
			return true;
		
		return isPlayerParticipant(objectId) && !Config.DM_EVENT_POTIONS_ALLOWED;
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
		
		return isPlayerParticipant(objectId) && !Config.DM_EVENT_SUMMON_BY_ITEM_ALLOWED;
	}
	
	public void onKill(Creature killer, Player player)
	{
		if (player == null || !isStarted() || !isPlayerParticipant(player.getObjectId()))
			return;
		
		new DMEventTeleporter(player, false, false);
		
		Player attackingPlayer = null;
		if (killer instanceof Pet || killer instanceof Summon)
			attackingPlayer = ((Summon) killer).getOwner();
		else if (killer instanceof Player)
			attackingPlayer = (Player) killer;
		
		if (attackingPlayer != null && isPlayerParticipant(attackingPlayer))
		{
			_player.get(attackingPlayer.getObjectId()).increasePoints();
			sysMsgToAllParticipants(attackingPlayer.getName() + " Hunted Player " + player.getName() + "!");
			_player.get(attackingPlayer.getObjectId()).increaseDeath();
		}
	}
	
	public void onTeleported(Player player)
	{
		if (!isStarted() || player == null || !isPlayerParticipant(player.getObjectId()))
			return;
		
		if (!_teleported.containsKey(player.getObjectId()) || !_teleported.get(player.getObjectId()))
			spawnProtection(player);
		
		Map<Integer, Integer> buffs = player.isMageClass() ? Config.DM_EVENT_MAGE_BUFFS : Config.DM_EVENT_FIGHTER_BUFFS;
		
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
	
	public int getPlayerCounts()
	{
		return _player.size();
	}
	
	public String[] getFirstPosition(int countPos)
	{
		TreeSet<DMPlayer> players = orderPosition(_player.values());
		String text = "";
		for (int j = 0; j < countPos; j++)
		{
			if (players.isEmpty())
				break;
			
			DMPlayer player = players.first();
			
			if (player.getPoints() == 0)
				break;
			
			text += player.getPlayer().getName() + "," + String.valueOf(player.getPoints()) + ";";
			players.remove(player);
			
			int playerPointPrev = player.getPoints();
			
			if (!Config.DM_REWARD_PLAYERS_TIE)
				continue;
			
			while (!players.isEmpty())
			{
				player = players.first();
				if (player.getPoints() != playerPointPrev)
					break;
				
				text += player.getPlayer().getName() + "," + String.valueOf(player.getPoints()) + ";";
				players.remove(player);
			}
		}
		
		if (text != "")
			return text.split("\\;");
		
		return null;
	}
	
	public byte[] generateHex(int size)
	{
		byte[] array = new byte[size];
		Rnd.nextBytes(array);
		return array;
	}
	
	public String hexToString(byte[] hex)
	{
		return new BigInteger(hex).toString(16);
	}
	
	public static final DMEvent getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final DMEvent INSTANCE = new DMEvent();
	}
}
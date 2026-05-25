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
package ext.mods.gameserver.model.entity.events.lastman;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

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
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.model.itemcontainer.PcInventory;
import ext.mods.gameserver.model.spawn.Spawn;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.CreatureSay;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.network.serverpackets.StatusUpdate;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.skills.L2Skill;

public class LMEvent extends Events
{
	private final String htmlPath = "html/mods/events/lm/";
	
	private EventState _state = EventState.INACTIVE;
	
	private Spawn _npcSpawn;
	
	private Npc _lastNpcSpawn;
	
	private Map<Integer, LMPlayer> _player = new HashMap<>();
	private Map<Integer, Boolean> _teleported = new HashMap<>();
	
	public LMEvent()
	{
	}
	
	public void init()
	{
		AntiFeedManager.getInstance().registerEvent(AntiFeedManager.LM_ID);
	}
	
	public boolean startParticipation()
	{
		NpcTemplate tmpl = NpcData.getInstance().getTemplate(Config.LM_EVENT_PARTICIPATION_NPC_ID);
		
		if (tmpl == null)
		{
			LOGGER.warn("LMEvent.startParticipation(): NpcTemplate is a NullPointer -> Invalid npc id in Configs?");
			return false;
		}
		
		try
		{
			_npcSpawn = new Spawn(tmpl);
			
			_npcSpawn.setLoc(Config.LM_EVENT_PARTICIPATION_NPC_COORDINATES[0], Config.LM_EVENT_PARTICIPATION_NPC_COORDINATES[1], Config.LM_EVENT_PARTICIPATION_NPC_COORDINATES[2], Config.LM_EVENT_PARTICIPATION_NPC_COORDINATES[3]);
			_npcSpawn.setRespawnDelay(1);
			
			SpawnManager.getInstance().addSpawn(_npcSpawn);
			_lastNpcSpawn = _npcSpawn.doSpawn(false);
		}
		catch (Exception e)
		{
			LOGGER.warn("LMEvent.startParticipation(): exception: " + e.getMessage(), e);
			return false;
		}
		
		setState(EventState.PARTICIPATING);
		return true;
	}
	
	public boolean startFight()
	{
		setState(EventState.STARTING);
		
		if (getPlayerCounts() < Config.LM_EVENT_MIN_PLAYERS)
		{
			setState(EventState.INACTIVE);
			
			for (LMPlayer player : _player.values())
				player.getPlayer().addItem(Config.LM_EVENT_PARTICIPATION_FEE[0], Config.LM_EVENT_PARTICIPATION_FEE[1], true);
			
			_player.clear();
			
			unSpawnNpc();
			AntiFeedManager.getInstance().clear(AntiFeedManager.LM_ID);
			return false;
		}
		
		closeDoors(Config.LM_DOORS_IDS_TO_CLOSE);
		
		setState(EventState.STARTED);
		
		for (LMPlayer player : _player.values())
		{
			if (player != null)
				new LMEventTeleporter(player.getPlayer(), false, false);
		}
		
		return true;
	}
	
	public TreeSet<LMPlayer> orderPosition(Collection<LMPlayer> listPlayer)
	{
		TreeSet<LMPlayer> players = new TreeSet<>(Comparator.<LMPlayer> comparingInt(LMPlayer::getCredits).reversed().thenComparing(LMPlayer::getPoints).thenComparing(LMPlayer::getHexCode));
		players.addAll(listPlayer);
		return players;
	}
	
	public String calculateRewards()
	{
		TreeSet<LMPlayer> players = orderPosition(_player.values());
		String msg = "";
		
		if (!Config.LM_REWARD_PLAYERS_TIE && getPlayerCounts() > 1)
			return "Last Man ended, thanks to everyone who participated!\nHe did not have winners!";
		
		for (int i = 0; i < players.size(); i++)
		{
			if (players.isEmpty())
				break;
			
			LMPlayer player = players.first();
			
			if (player.getCredits() == 0 || player.getPoints() == 0)
				break;
			
			rewardPlayer(player);
			players.remove(player);
			msg += " Player: " + player.getPlayer().getName();
			msg += " Killed: " + player.getPoints();
			msg += " Died: " + String.valueOf(Config.LM_EVENT_PLAYER_CREDITS - player.getCredits());
			msg += "\n";
			if (!Config.LM_REWARD_PLAYERS_TIE)
				break;
		}
		
		setState(EventState.REWARDING);
		
		return "Last Man ended, thanks to everyone who participated!\nWinner(s):\n" + msg;
	}
	
	private void rewardPlayer(LMPlayer lmplayer)
	{
		Player player = lmplayer.getPlayer();
		
		if (player == null)
			return;
		
		if (Config.LM_EVENT_HERO)
			setHero(player, Config.LV_EVENT_HERO_DAYS);
		
		SystemMessage systemMessage = null;
		
		for (IntIntHolder reward : Config.LM_EVENT_REWARDS)
		{
			PcInventory inv = player.getInventory();
			
			if (ItemData.getInstance().getTemplate(reward.getId()).isStackable())
			{
				inv.addItem(reward.getId(), reward.getValue());
				
				if (reward.getId() > 1)
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
	
	public void stopFight()
	{
		setState(EventState.INACTIVATING);
		
		unSpawnNpc();
		
		openDoors(Config.LM_DOORS_IDS_TO_CLOSE);
		
		for (LMPlayer player : _player.values())
		{
			if (player != null)
				new LMEventTeleporter(player.getPlayer(), Config.LM_EVENT_PARTICIPATION_NPC_COORDINATES, false, false);
		}
		
		_teleported = new HashMap<>();
		_player = new HashMap<>();
		
		setState(EventState.INACTIVE);
		AntiFeedManager.getInstance().clear(AntiFeedManager.LM_ID);
	}
	
	public synchronized boolean addParticipant(Player player)
	{
		if (player == null)
			return false;
		
		if (isPlayerParticipant(player))
			return false;
		
		String hexCode = hexToString(generateHex(16));
		_player.put(player.getObjectId(), new LMPlayer(player, hexCode));
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
		return player.destroyItemByItemId(Config.LM_EVENT_PARTICIPATION_FEE[0], Config.LM_EVENT_PARTICIPATION_FEE[1], true);
	}
	
	public String getParticipationFee()
	{
		int itemId = Config.LM_EVENT_PARTICIPATION_FEE[0];
		int itemNum = Config.LM_EVENT_PARTICIPATION_FEE[1];
		
		if (itemId == 0 || itemNum == 0)
			return "-";
		
		return String.valueOf(itemNum) + " " + ItemData.getInstance().getTemplate(itemId).getName();
	}
	
	public void sysMsgToAllParticipants(String message)
	{
		CreatureSay cs = new CreatureSay(0, SayType.HERO_VOICE, "Event Manager", message);
		
		for (LMPlayer player : _player.values())
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
		
		new LMEventTeleporter(player, false, false);
	}
	
	public void onLogout(Player player)
	{
		if (player != null && (isStarting() || isStarted() || isParticipating()))
		{
			if (SafeDisconnectManager.getInstance().isSafeDisconnectActive(player) && Config.SAFEDISCONNECT_INTEGRATION_LM)
				return;
			
			if (removeParticipant(player))
				player.teleportTo(Config.LM_EVENT_PARTICIPATION_NPC_COORDINATES[0] + Rnd.get(101) - 50, Config.LM_EVENT_PARTICIPATION_NPC_COORDINATES[1] + Rnd.get(101) - 50, Config.LM_EVENT_PARTICIPATION_NPC_COORDINATES[2], 0);
		}
	}
	
	public synchronized void onBypass(String command, Player player)
	{
		if (player == null || !isParticipating())
			return;
		
		final String htmContent;
		
		if (command.equals("lm_event_participation"))
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
			else if (Config.DISABLE_ID_CLASSES_LM.contains(player.getClassId().getId()))
			{
				htmContent = HTMLData.getInstance().getHtm(player, htmlPath + "Class.htm");
				if (htmContent != null)
				{
					npcHtmlMessage.setHtml(htmContent);
				}
			}
			else if (playerLevel < Config.LM_EVENT_MIN_LVL || playerLevel > Config.LM_EVENT_MAX_LVL)
			{
				htmContent = HTMLData.getInstance().getHtm(player, htmlPath + "Level.htm");
				if (htmContent != null)
				{
					npcHtmlMessage.setHtml(htmContent);
					npcHtmlMessage.replace("%min%", String.valueOf(Config.LM_EVENT_MIN_LVL));
					npcHtmlMessage.replace("%max%", String.valueOf(Config.LM_EVENT_MAX_LVL));
				}
			}
			else if (getPlayerCounts() == Config.LM_EVENT_MAX_PLAYERS)
			{
				htmContent = HTMLData.getInstance().getHtm(player, htmlPath + "Full.htm");
				if (htmContent != null)
				{
					npcHtmlMessage.setHtml(htmContent);
					npcHtmlMessage.replace("%max%", String.valueOf(Config.LM_EVENT_MAX_PLAYERS));
				}
			}
			else if ((Config.LM_EVENT_MAX_PARTICIPANTS_PER_IP > 0) && !AntiFeedManager.getInstance().tryAddPlayer(AntiFeedManager.LM_ID, player, Config.LM_EVENT_MAX_PARTICIPANTS_PER_IP))
			{
				htmContent = HTMLData.getInstance().getHtm(player, htmlPath + "IPRestriction.htm");
				if (htmContent != null)
				{
					npcHtmlMessage.setHtml(htmContent);
					npcHtmlMessage.replace("%max%", String.valueOf(AntiFeedManager.getInstance().getLimit(player, Config.LM_EVENT_MAX_PARTICIPANTS_PER_IP)));
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
		else if (command.equals("lm_event_remove_participation"))
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
		
		return isPlayerParticipant(objectId) && !Config.LM_EVENT_SCROLL_ALLOWED;
	}
	
	public boolean onPotionUse(int objectId)
	{
		if (!isStarted())
			return true;
		
		return isPlayerParticipant(objectId) && !Config.LM_EVENT_POTIONS_ALLOWED;
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
		
		return isPlayerParticipant(objectId) && !Config.LM_EVENT_SUMMON_BY_ITEM_ALLOWED;
	}
	
	public void onKill(Creature killer, Player player)
	{
		if (player == null || !isStarted() || !isPlayerParticipant(player.getObjectId()))
			return;
		
		boolean isTeleported = false;
		short killedCredits = _player.get(player.getObjectId()).getCredits();
		
		if (killedCredits <= 1)
		{
			removeParticipant(player);
			isTeleported = true;
		}
		else
			_player.get(player.getObjectId()).decreaseCredits();
		
		new LMEventTeleporter(player, isTeleported, !isTeleported);
		
		if (killer == null)
			return;
		
		Player attackingPlayer = null;
		if (killer instanceof Pet || killer instanceof Summon)
			attackingPlayer = ((Summon) killer).getOwner();
		else if (killer instanceof Player)
			attackingPlayer = (Player) killer;
		
		if (attackingPlayer != null && isPlayerParticipant(attackingPlayer))
		{
			_player.get(attackingPlayer.getObjectId()).increasePoints();
			
			attackingPlayer.sendPacket(new CreatureSay(attackingPlayer.getObjectId(), SayType.TELL, "Last Man", "You killed " + _player.get(attackingPlayer.getObjectId()).getPoints() + " player(s)!"));
			attackingPlayer.sendPacket(new CreatureSay(attackingPlayer.getObjectId(), SayType.TELL, "Last Man", killedCredits <= 1 ? "You do not have credits, leaving the event!" : "Now you have " + (killedCredits - 1) + " credit(s)!"));
		}
		
		if (getPlayerCounts() == 1)
			LMManager.getInstance().skipDelay();
	}
	
	public void onTeleported(Player player)
	{
		if (!isStarted() || player == null || !isPlayerParticipant(player.getObjectId()))
			return;
		
		if (!_teleported.containsKey(player.getObjectId()) || !_teleported.get(player.getObjectId()))
			spawnProtection(player);
		
		Map<Integer, Integer> buffs = player.isMageClass() ? Config.LM_EVENT_MAGE_BUFFS : Config.LM_EVENT_FIGHTER_BUFFS;
		
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
	
	public void setHero(Player player, int days)
	{
		player.setHero(true);
		long currentTime = System.currentTimeMillis();
		long existingHeroUntil = player.getHeroUntil();
		long newHeroUntil = currentTime + TimeUnit.DAYS.toMillis(days);
		
		if (existingHeroUntil > currentTime)
			newHeroUntil += existingHeroUntil - currentTime;
		
		player.setHeroUntil(newHeroUntil);
		player.store();
		player.sendMessage(player.getSysString(10_024, days));
		player.broadcastUserInfo();
	}
	
	public static final LMEvent getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final LMEvent INSTANCE = new LMEvent();
	}
}
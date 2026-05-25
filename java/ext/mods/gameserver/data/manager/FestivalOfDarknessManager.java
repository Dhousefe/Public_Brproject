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
package ext.mods.gameserver.data.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ConnectionPool;
import ext.mods.commons.util.LinTime;

import ext.mods.Config;
import ext.mods.gameserver.data.sql.ClanTable;
import ext.mods.gameserver.data.sql.PlayerInfoTable;
import ext.mods.gameserver.enums.CabalType;
import ext.mods.gameserver.enums.FestivalType;
import ext.mods.gameserver.enums.MessageType;
import ext.mods.gameserver.enums.RestartType;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.FestivalMonster;
import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.model.zone.type.PeaceZone;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public class FestivalOfDarknessManager
{
	private static final CLogger LOGGER = new CLogger(FestivalOfDarknessManager.class.getName());
	
	private static final String RESTORE_FESTIVAL = "SELECT festivalId, cabal, cycle, date, score, members FROM seven_signs_festival";
	private static final String RESTORE_FESTIVAL_2 = "SELECT festival_cycle, accumulated_bonus0, accumulated_bonus1, accumulated_bonus2, accumulated_bonus3, accumulated_bonus4 FROM seven_signs_status WHERE id=0";
	private static final String INSERT_OR_UPDATE_FESTIVAL = "INSERT INTO seven_signs_festival (festivalId, cabal, cycle, date, score, members) VALUES (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE date = VALUES(date), score = VALUES(score), members = VALUES(members)";
	private static final String GET_CLAN_NAME = "SELECT clan_name FROM clan_data WHERE clan_id = (SELECT clanid FROM characters WHERE char_name = ?)";
	
	public static final int FESTIVAL_COUNT = 5;
	
	public static final int FESTIVAL_OFFERING_ID = 5901;
	public static final int FESTIVAL_OFFERING_VALUE = 5;
	
	/*
	 * The following contains all the necessary spawn data for: - Player Start Locations - Witches - Monsters - Chests All data is given by: X, Y, Z (coords), Heading, NPC ID (if necessary) This may be moved externally in time, but the data should not change.
	 */
	public static final int[][] FESTIVAL_DAWN_PLAYER_SPAWNS =
	{
		{
			-79187,
			113186,
			-4895,
			0
		},
		{
			-75918,
			110137,
			-4895,
			0
		},
		{
			-73835,
			111969,
			-4895,
			0
		},
		{
			-76170,
			113804,
			-4895,
			0
		},
		{
			-78927,
			109528,
			-4895,
			0
		}
	};
	
	public static final int[][] FESTIVAL_DUSK_PLAYER_SPAWNS =
	{
		{
			-77200,
			88966,
			-5151,
			0
		},
		{
			-76941,
			85307,
			-5151,
			0
		},
		{
			-74855,
			87135,
			-5151,
			0
		},
		{
			-80208,
			88222,
			-5151,
			0
		},
		{
			-79954,
			84697,
			-5151,
			0
		}
	};
	
	public FestivalManager _managerInstance;
	
	protected int _signsCycle = SevenSignsManager.getInstance().getCurrentCycle();
	protected int _festivalCycle;
	protected long _nextFestivalCycleStart;
	protected long _nextFestivalStart;
	protected boolean _festivalInitialized;
	protected boolean _festivalInProgress;
	protected List<Integer> _accumulatedBonuses = new ArrayList<>();
	
	private List<PeaceZone> _dawnPeace;
	private List<PeaceZone> _duskPeace;
	
	protected Map<Integer, List<Integer>> _dawnFestivalParticipants = new HashMap<>();
	protected Map<Integer, List<Integer>> _duskFestivalParticipants = new HashMap<>();
	
	protected Map<Integer, List<Integer>> _dawnPreviousParticipants = new HashMap<>();
	protected Map<Integer, List<Integer>> _duskPreviousParticipants = new HashMap<>();
	
	private Map<Integer, Integer> _dawnFestivalScores = new HashMap<>();
	private Map<Integer, Integer> _duskFestivalScores = new HashMap<>();
	
	private Map<Integer, Map<Integer, StatSet>> _festivalData = new ConcurrentHashMap<>();
	
	public FestivalOfDarknessManager()
	{
		restoreFestivalData();
		
		if (SevenSignsManager.getInstance().isSealValidationPeriod())
		{
			LOGGER.info("Seven Signs Festival initialization was bypassed due to Seal Validation being under effect.");
			return;
		}
		
		startFestivalManager();
	}
	
	/**
	 * Used to start the Festival Manager, if the current period is not Seal Validation.
	 */
	public void startFestivalManager()
	{
		int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
		int minsToNextFestival;
		
		if (currentMinute >= 40)
			minsToNextFestival = 60 - currentMinute;
		else if (currentMinute >= 20)
			minsToNextFestival = 40 - currentMinute;
		else
			minsToNextFestival = 20 - currentMinute;
		
		setNextFestivalStart(minsToNextFestival);
	}
	
	/**
	 * Restores saved festival data, basic settings from the properties file and past high score data from the database.
	 */
	protected void restoreFestivalData()
	{
		try (Connection con = ConnectionPool.getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement(RESTORE_FESTIVAL);
				ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					int festivalCycle = rs.getInt("cycle");
					int festivalId = rs.getInt("festivalId");
					final String cabal = rs.getString("cabal");
					
					final StatSet set = new StatSet();
					set.set("festivalId", festivalId);
					set.set("cabal", Enum.valueOf(CabalType.class, cabal));
					set.set("cycle", festivalCycle);
					set.set("date", rs.getString("date"));
					set.set("score", rs.getInt("score"));
					set.set("members", rs.getString("members"));
					
					if (cabal.equalsIgnoreCase("dawn"))
						festivalId += FESTIVAL_COUNT;
					
					final Map<Integer, StatSet> map = _festivalData.computeIfAbsent(festivalCycle, m -> new HashMap<>());
					map.put(festivalId, set);
				}
			}
			
			try (PreparedStatement ps = con.prepareStatement(RESTORE_FESTIVAL_2);
				ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					_festivalCycle = rs.getInt("festival_cycle");
					
					for (int i = 0; i < FESTIVAL_COUNT; i++)
						_accumulatedBonuses.add(i, rs.getInt("accumulated_bonus" + i));
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't load Seven Signs Festival data.", e);
		}
	}
	
	/**
	 * Stores current festival data, basic settings to the properties file and past high score data to the database.<BR>
	 * <BR>
	 * If updateSettings = true, then all Seven Signs data is updated in the database.
	 * @param updateSettings if true, will save Seven Signs status aswell.
	 */
	public void saveFestivalData(boolean updateSettings)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(INSERT_OR_UPDATE_FESTIVAL))
		{
			for (Map<Integer, StatSet> map : _festivalData.values())
			{
				for (StatSet set : map.values())
				{
					ps.setInt(1, set.getInteger("festivalId"));
					ps.setString(2, set.getString("cabal"));
					ps.setInt(3, set.getInteger("cycle"));
					ps.setLong(4, set.getLong("date"));
					ps.setInt(5, set.getInteger("score"));
					ps.setString(6, set.getString("members"));
					ps.addBatch();
				}
			}
			ps.executeBatch();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't save Seven Signs Festival data.", e);
		}
		
		if (updateSettings)
			SevenSignsManager.getInstance().saveSevenSignsStatus();
	}
	
	public void rewardHighestRanked()
	{
		for (int i = 0; i < FESTIVAL_COUNT; i++)
		{
			final StatSet set = getOverallHighestScoreData(i);
			if (set != null)
			{
				for (String playerName : set.getString("members").split(","))
					addReputationPointsForPartyMemberClan(playerName);
			}
		}
	}
	
	private static void addReputationPointsForPartyMemberClan(String playerName)
	{
		final Player player = World.getInstance().getPlayer(playerName);
		if (player != null)
		{
			if (player.getClan() != null)
			{
				player.getClan().addReputationScore(100);
				player.getClan().broadcastToMembers(SystemMessage.getSystemMessage(SystemMessageId.CLAN_MEMBER_S1_WAS_IN_HIGHEST_RANKED_PARTY_IN_FESTIVAL_OF_DARKNESS_AND_GAINED_S2_REPUTATION).addString(playerName).addNumber(100));
			}
		}
		else
		{
			try (Connection con = ConnectionPool.getConnection();
				PreparedStatement ps = con.prepareStatement(GET_CLAN_NAME))
			{
				ps.setString(1, playerName);
				
				try (ResultSet rs = ps.executeQuery())
				{
					if (rs.next())
					{
						final String clanName = rs.getString("clan_name");
						if (clanName != null)
						{
							final Clan clan = ClanTable.getInstance().getClanByName(clanName);
							if (clan != null)
							{
								clan.addReputationScore(100);
								clan.broadcastToMembers(SystemMessage.getSystemMessage(SystemMessageId.CLAN_MEMBER_S1_WAS_IN_HIGHEST_RANKED_PARTY_IN_FESTIVAL_OF_DARKNESS_AND_GAINED_S2_REPUTATION).addString(playerName).addNumber(100));
							}
						}
					}
				}
			}
			catch (Exception e)
			{
				LOGGER.error("Couldn't get clan name of {}.", e, playerName);
			}
		}
	}
	
	/**
	 * Used to reset all festival data at the beginning of a new quest event period.
	 * @param updateSettings
	 */
	public void resetFestivalData(boolean updateSettings)
	{
		_festivalCycle = 0;
		_signsCycle = SevenSignsManager.getInstance().getCurrentCycle();
		
		for (int i = 0; i < FESTIVAL_COUNT; i++)
			_accumulatedBonuses.set(i, 0);
		
		_dawnFestivalParticipants.clear();
		_duskFestivalParticipants.clear();
		
		_dawnPreviousParticipants.clear();
		_duskPreviousParticipants.clear();
		
		_dawnFestivalScores.clear();
		_duskFestivalScores.clear();
		
		final Map<Integer, StatSet> map = new HashMap<>();
		
		for (int i = 0; i < FESTIVAL_COUNT * 2; i++)
		{
			int festivalId = i;
			
			if (i >= FESTIVAL_COUNT)
				festivalId -= FESTIVAL_COUNT;
			
			final StatSet set = new StatSet();
			set.set("festivalId", festivalId);
			set.set("cycle", _signsCycle);
			set.set("date", "0");
			set.set("score", 0);
			set.set("members", "");
			set.set("cabal", (i >= FESTIVAL_COUNT) ? CabalType.DAWN : CabalType.DUSK);
			
			map.put(i, set);
		}
		
		_festivalData.put(_signsCycle, map);
		
		saveFestivalData(updateSettings);
		
		for (Player player : World.getInstance().getPlayers())
		{
			ItemInstance bloodOfferings = player.getInventory().getItemByItemId(FESTIVAL_OFFERING_ID);
			if (bloodOfferings != null)
				player.destroyItem(bloodOfferings, false);
		}
		
		LOGGER.info("Reinitialized Seven Signs Festival for next competition period.");
	}
	
	public final int getCurrentFestivalCycle()
	{
		return _festivalCycle;
	}
	
	public final boolean isFestivalInitialized()
	{
		return _festivalInitialized;
	}
	
	public final boolean isFestivalInProgress()
	{
		return _festivalInProgress;
	}
	
	public void setNextFestivalStart(long milliFromNow)
	{
		_nextFestivalStart = System.currentTimeMillis() + milliFromNow;
	}
	
	public final int getMinsToNextCycle()
	{
		if (SevenSignsManager.getInstance().isSealValidationPeriod())
			return -1;
		
		return Math.round((_nextFestivalCycleStart - System.currentTimeMillis()) / 60000);
	}
	
	public final String getTimeToNextFestivalStr(Player player)
	{
		if (SevenSignsManager.getInstance().isSealValidationPeriod())
			return player.getSysString(10_164);
		
		int currentMinute = getMin();
		int minsToNextFestival = 0;
		
		if (currentMinute == 58 || currentMinute == 18 || currentMinute == 38 || currentMinute == 59 || currentMinute == 19 || currentMinute == 39)
			return NpcStringId.getNpcMessage(1000441);
		else
		{
			if (currentMinute > 39)
				minsToNextFestival = 58 - currentMinute;
			else if (currentMinute > 19)
				minsToNextFestival = 38 - currentMinute;
			else
				minsToNextFestival = 18 - currentMinute;
		}
		
		return player.getSysString(10_165, minsToNextFestival);
	}
	
	public final int getTimeOfSSQ()
	{
		return (int) (_nextFestivalStart / 1000);
	}
	
	/**
	 * Returns the current festival ID and oracle ID that the specified player is in, but will return the default of {-1, -1} if the player is not found as a participant.
	 * @param player
	 * @return int[] playerFestivalInfo
	 */
	public final int[] getFestivalForPlayer(Player player)
	{
		int[] playerFestivalInfo =
		{
			-1,
			-1
		};
		int festivalId = 0;
		
		while (festivalId < FESTIVAL_COUNT)
		{
			List<Integer> participants = _dawnFestivalParticipants.get(festivalId);
			
			if (participants != null && participants.contains(player.getObjectId()))
			{
				playerFestivalInfo[0] = CabalType.DAWN.ordinal();
				playerFestivalInfo[1] = festivalId;
				
				return playerFestivalInfo;
			}
			
			participants = _duskFestivalParticipants.get(++festivalId);
			
			if (participants != null && participants.contains(player.getObjectId()))
			{
				playerFestivalInfo[0] = CabalType.DUSK.ordinal();
				playerFestivalInfo[1] = festivalId;
				
				return playerFestivalInfo;
			}
			
			festivalId++;
		}
		
		return playerFestivalInfo;
	}
	
	public final boolean isParticipant(Player player)
	{
		if (SevenSignsManager.getInstance().isSealValidationPeriod())
			return false;
			
		
		for (List<Integer> participants : _dawnFestivalParticipants.values())
			if (participants != null && participants.contains(player.getObjectId()))
				return true;
			
		for (List<Integer> participants : _duskFestivalParticipants.values())
			if (participants != null && participants.contains(player.getObjectId()))
				return true;
			
		return false;
	}
	
	public final List<Integer> getParticipants(CabalType oracle, int festivalId)
	{
		if (oracle == CabalType.DAWN)
			return _dawnFestivalParticipants.get(festivalId);
		
		return _duskFestivalParticipants.get(festivalId);
	}
	
	public final List<Integer> getPreviousParticipants(CabalType oracle, int festivalId)
	{
		if (oracle == CabalType.DAWN)
			return _dawnPreviousParticipants.get(festivalId);
		
		return _duskPreviousParticipants.get(festivalId);
	}
	
	public void setParticipants(CabalType oracle, int festivalId, Party festivalParty)
	{
		List<Integer> participants = null;
		
		if (festivalParty != null)
		{
			participants = new ArrayList<>(festivalParty.getMembersCount());
			for (Player player : festivalParty.getMembers())
				participants.add(player.getObjectId());
		}
		
		if (oracle == CabalType.DAWN)
			_dawnFestivalParticipants.put(festivalId, participants);
		else
			_duskFestivalParticipants.put(festivalId, participants);
	}
	
	public void updateParticipants(Player player, Party festivalParty)
	{
		if (!isParticipant(player))
			return;
		
		final int[] playerFestInfo = getFestivalForPlayer(player);
		final CabalType oracle = CabalType.VALUES[playerFestInfo[0]];
		final int festivalId = playerFestInfo[1];
		
		if (festivalId > -1)
		{
			if (_festivalInitialized)
			{
				L2DarknessFestival festivalInst = _managerInstance.getFestivalInstance(oracle, festivalId);
				
				if (festivalParty == null)
				{
					for (int partyMemberObjId : getParticipants(oracle, festivalId))
					{
						Player partyMember = World.getInstance().getPlayer(partyMemberObjId);
						if (partyMember == null)
							continue;
						
						festivalInst.relocatePlayer(partyMember, true);
					}
				}
				else
					festivalInst.relocatePlayer(player, true);
			}
			
			setParticipants(oracle, festivalId, festivalParty);
			
			if (festivalParty != null && festivalParty.getMembersCount() < Config.FESTIVAL_MIN_PLAYER)
			{
				updateParticipants(player, null);
				festivalParty.removePartyMember(player, MessageType.EXPELLED);
			}
		}
	}
	
	public final int getFinalScore(CabalType oracle, int festivalId)
	{
		if (oracle == CabalType.DAWN)
			return _dawnFestivalScores.get(festivalId);
		
		return _duskFestivalScores.get(festivalId);
	}
	
	public final int getHighestScore(CabalType oracle, int festivalId)
	{
		return getHighestScoreData(oracle, festivalId).getInteger("score");
	}
	
	/**
	 * @param oracle : The {@link CabalType} to test.
	 * @param festivalId : The festival id to test.
	 * @return The {@link StatSet} containing the highest score <b>this cycle</b> for the the specified {@link CabalType} and festival id.
	 */
	public final StatSet getHighestScoreData(CabalType oracle, int festivalId)
	{
		int offsetId = festivalId;
		if (oracle == CabalType.DAWN)
			offsetId += 5;
		
		return _festivalData.get(_signsCycle).get(offsetId);
	}
	
	/**
	 * @param festivalId : The festival id to test.
	 * @return The {@link StatSet} containing the highest ever recorded score data for the specified festival id.
	 */
	public final StatSet getOverallHighestScoreData(int festivalId)
	{
		StatSet set = null;
		int highestScore = 0;
		
		for (Map<Integer, StatSet> map : _festivalData.values())
		{
			for (StatSet setToTest : map.values())
			{
				int currFestID = setToTest.getInteger("festivalId");
				int festivalScore = setToTest.getInteger("score");
				
				if (currFestID != festivalId)
					continue;
				
				if (festivalScore > highestScore)
				{
					highestScore = festivalScore;
					set = setToTest;
				}
			}
		}
		
		return set;
	}
	
	/**
	 * Set the final score details for the last participants of the specified festival data. Returns <b>true</b> if the score is higher than that previously recorded <b>this cycle</b>.
	 * @param player
	 * @param oracle
	 * @param festival
	 * @param offeringScore
	 * @return boolean isHighestScore
	 */
	public boolean setFinalScore(Player player, CabalType oracle, FestivalType festival, int offeringScore)
	{
		final int festivalId = festival.ordinal();
		
		int currDawnHighScore = getHighestScore(CabalType.DAWN, festivalId);
		int currDuskHighScore = getHighestScore(CabalType.DUSK, festivalId);
		
		int thisCabalHighScore = 0;
		@SuppressWarnings("unused")
		int otherCabalHighScore = 0;
		
		if (oracle == CabalType.DAWN)
		{
			thisCabalHighScore = currDawnHighScore;
			otherCabalHighScore = currDuskHighScore;
			
			_dawnFestivalScores.put(festivalId, offeringScore);
		}
		else
		{
			thisCabalHighScore = currDuskHighScore;
			otherCabalHighScore = currDawnHighScore;
			
			_duskFestivalScores.put(festivalId, offeringScore);
		}
		
		StatSet set = getHighestScoreData(oracle, festivalId);
		
		if (offeringScore > thisCabalHighScore)
		{
			final List<String> partyMembers = new ArrayList<>();
			for (int partyMember : getPreviousParticipants(oracle, festivalId))
				partyMembers.add(PlayerInfoTable.getInstance().getPlayerName(partyMember));
			
			set.set("date", String.valueOf(System.currentTimeMillis()));
			set.set("score", offeringScore);
			set.set("members", String.join(",", partyMembers));
			
			saveFestivalData(true);
			
			return true;
		}
		
		return false;
	}
	
	public int getFestivalScore(CabalType type)
	{
		if (type == CabalType.NORMAL)
			return 0;
		
		final CabalType oposite = type == CabalType.DUSK ? CabalType.DAWN : CabalType.DUSK;
		int result = 0;
		
		for (FestivalType festivalType : FestivalType.values())
		{
			final int festivalScore = getHighestScore(type, festivalType.ordinal());
			final int festivalScoreOposite = getHighestScore(oposite, festivalType.ordinal());
			if (festivalScore > festivalScoreOposite)
				result += festivalType.getMaxScore();
		}
		
		return result;
	}
	
	public final int getAccumulatedBonus(int festivalId)
	{
		return _accumulatedBonuses.get(festivalId);
	}
	
	public final int getTotalAccumulatedBonus()
	{
		int totalAccumBonus = 0;
		
		for (int accumBonus : _accumulatedBonuses)
			totalAccumBonus += accumBonus;
		
		return totalAccumBonus;
	}
	
	public void addAccumulatedBonus(int festivalId, int stoneType, int stoneAmount)
	{
		int eachStoneBonus = switch (stoneType)
		{
			case SevenSignsManager.SEAL_STONE_BLUE_ID -> eachStoneBonus = SevenSignsManager.SEAL_STONE_BLUE_VALUE;
			case SevenSignsManager.SEAL_STONE_GREEN_ID -> eachStoneBonus = SevenSignsManager.SEAL_STONE_GREEN_VALUE;
			case SevenSignsManager.SEAL_STONE_RED_ID -> eachStoneBonus = SevenSignsManager.SEAL_STONE_RED_VALUE;
			default -> 0;
		};
		
		int newTotalBonus = _accumulatedBonuses.get(festivalId) + (stoneAmount * eachStoneBonus);
		_accumulatedBonuses.set(festivalId, newTotalBonus);
	}
	
	/**
	 * Calculate and return the proportion of the accumulated bonus for the festival where the player was in the winning party, if the winning party's cabal won the event. The accumulated bonus is then updated, with the player's share deducted.
	 * @param player
	 * @return playerBonus (the share of the bonus for the party)
	 */
	public final int distribAccumulatedBonus(Player player)
	{
		if (SevenSignsManager.getInstance().getPlayerCabal(player.getObjectId()) != SevenSignsManager.getInstance().getWinningCabal())
			return 0;
		
		final Map<Integer, StatSet> map = _festivalData.get(_signsCycle);
		if (map == null)
			return 0;
		
		final String playerName = player.getName();
		
		int playerBonus = 0;
		for (StatSet set : map.values())
		{
			final String members = set.getString("members");
			if (members.indexOf(playerName) > -1)
			{
				final int festivalId = set.getInteger("festivalId");
				final int numPartyMembers = members.split(",").length;
				final int totalAccumBonus = _accumulatedBonuses.get(festivalId);
				
				playerBonus = totalAccumBonus / numPartyMembers;
				_accumulatedBonuses.set(festivalId, totalAccumBonus - playerBonus);
				break;
			}
		}
		
		return playerBonus;
	}
	
	/**
	 * Add zone for use with announcements in the oracles.
	 * @param zone : Zone to be added.
	 * @param dawn : Is dawn zone.
	 */
	public void addPeaceZone(PeaceZone zone, boolean dawn)
	{
		if (dawn)
		{
			if (_dawnPeace == null)
				_dawnPeace = new ArrayList<>(2);
			
			if (!_dawnPeace.contains(zone))
				_dawnPeace.add(zone);
		}
		else
		{
			if (_duskPeace == null)
				_duskPeace = new ArrayList<>(2);
			
			if (!_duskPeace.contains(zone))
				_duskPeace.add(zone);
		}
	}
	
	public class FestivalManager
	{
		public static Map<Integer, L2DarknessFestival> _festivalInstances = new HashMap<>();
		
		public FestivalManager()
		{
			_managerInstance = this;
			
			_festivalCycle++;
			
			int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
			int minsToNextFestival;
			
			if (currentMinute >= 40)
				minsToNextFestival = 60 - currentMinute;
			else if (currentMinute >= 20)
				minsToNextFestival = 40 - currentMinute;
			else
				minsToNextFestival = 20 - currentMinute;
			
			setNextFestivalStart(minsToNextFestival * 60 * 1000);
		}
		
		/**
		 * Returns the running instance of a festival for the given Oracle and festivalID. <BR>
		 * A <B>null</B> value is returned if there are no participants in that festival.
		 * @param oracle
		 * @param festivalId
		 * @return L2DarknessFestival festivalInst
		 */
		public final L2DarknessFestival getFestivalInstance(CabalType oracle, int festivalId)
		{
			if (!isFestivalInitialized())
				return null;
			
			/*
			 * Compute the offset if a Dusk instance is required. ID: 0 1 2 3 4 Dusk 1: 10 11 12 13 14 Dawn 2: 20 21 22 23 24
			 */
			
			festivalId += (oracle == CabalType.DUSK) ? 10 : 20;
			return _festivalInstances.get(festivalId);
		}
		
		public final static Map<Integer, L2DarknessFestival> getFestivalInstance()
		{
			return _festivalInstances;
		}
		
		public final static void setFestivalInstance(int festivalId, L2DarknessFestival festivalInstance)
		{
			if (festivalInstance == null)
				throw new IllegalArgumentException("Festival instance cannot be null");
			
			_festivalInstances.put(festivalId, festivalInstance);
		}
	}
	
	public class L2DarknessFestival
	{
		protected final CabalType _cabal;
		protected final int _levelRange;
		
		private FestivalSpawn _startLocation;
		
		protected final List<FestivalMonster> _npcInsts;
		
		private List<Integer> _participants;
		private final Map<Integer, FestivalSpawn> _originalLocations;
		
		public L2DarknessFestival(CabalType cabal, int levelRange)
		{
			_cabal = cabal;
			_levelRange = levelRange;
			_originalLocations = new HashMap<>();
			_npcInsts = new ArrayList<>();
			
			if (cabal == CabalType.DAWN)
			{
				_participants = _dawnFestivalParticipants.get(levelRange);
				_startLocation = new FestivalSpawn(FESTIVAL_DAWN_PLAYER_SPAWNS[levelRange]);
			}
			else
			{
				_participants = _duskFestivalParticipants.get(levelRange);
				_startLocation = new FestivalSpawn(FESTIVAL_DUSK_PLAYER_SPAWNS[levelRange]);
			}
			
			if (_participants == null)
				_participants = new ArrayList<>();
			
			festivalInit();
		}
		
		public void festivalInit()
		{
			if (_participants != null && !_participants.isEmpty())
			{
				for (int participantObjId : _participants)
				{
					Player participant = World.getInstance().getPlayer(participantObjId);
					if (participant == null)
						continue;
					
					_originalLocations.put(participantObjId, new FestivalSpawn(participant.getX(), participant.getY(), participant.getZ(), participant.getHeading()));
					
					int x = _startLocation._x;
					int y = _startLocation._y;
					
					participant.getAI().tryToIdle();
					participant.teleportTo(x, y, _startLocation._z, 0);
					
					if (participant.getSummon() != null)
						participant.getSummon().unSummon(participant);
					
					participant.stopAllEffectsExceptThoseThatLastThroughDeath();
					
					ItemInstance bloodOfferings = participant.getInventory().getItemByItemId(FESTIVAL_OFFERING_ID);
					if (bloodOfferings != null)
						participant.destroyItem(bloodOfferings, true);
				}
			}
		}
		
		public void festivalEnd()
		{
			if (_participants != null && !_participants.isEmpty())
			{
				for (int participantObjId : _participants)
				{
					Player participant = World.getInstance().getPlayer(participantObjId);
					if (participant == null)
						continue;
					
					relocatePlayer(participant, false);
					participant.sendMessage(participant.getSysString(10_106));
				}
				
				if (_cabal == CabalType.DAWN)
					_dawnPreviousParticipants.put(_levelRange, _participants);
				else
					_duskPreviousParticipants.put(_levelRange, _participants);
			}
			_participants = null;
		}
		
		public void relocatePlayer(Player participant, boolean isRemoving)
		{
			if (participant == null)
				return;
			
			try
			{
				FestivalSpawn origPosition = _originalLocations.get(participant.getObjectId());
				
				if (isRemoving)
					_originalLocations.remove(participant.getObjectId());
				
				participant.getAI().tryToIdle();
				participant.teleportTo(origPosition._x, origPosition._y, origPosition._z, 20);
				participant.sendMessage(participant.getSysString(10_107));
			}
			catch (Exception e)
			{
				participant.teleportTo(RestartType.TOWN);
				participant.sendMessage(participant.getSysString(10_107));
			}
		}
	}
	
	public static class FestivalSpawn
	{
		public final int _x;
		public final int _y;
		public final int _z;
		
		public FestivalSpawn(int x, int y, int z, int heading)
		{
			_x = x;
			_y = y;
			_z = z;
		}
		
		public FestivalSpawn(int[] spawnData)
		{
			_x = spawnData[0];
			_y = spawnData[1];
			_z = spawnData[2];
		}
	}
	
	public Map<Integer, List<Integer>> getParticipantsDusk()
	{
		return _duskFestivalParticipants;
	}
	
	public Map<Integer, List<Integer>> getParticipantsDawn()
	{
		return _dawnFestivalParticipants;
	}
	
	public static int getMin()
	{
		var c = Calendar.getInstance();
		c.setTimeInMillis(LinTime.currentTimeMillis());
		return c.get(Calendar.MINUTE);
	}
	
	public static FestivalOfDarknessManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final FestivalOfDarknessManager INSTANCE = new FestivalOfDarknessManager();
	}
}
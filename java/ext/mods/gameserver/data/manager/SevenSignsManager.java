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
import java.util.Calendar;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ConnectionPool;
import ext.mods.commons.pool.ThreadPool;

import ext.mods.Config;
import ext.mods.gameserver.data.SkillTable.FrequentSkill;
import ext.mods.gameserver.enums.CabalType;
import ext.mods.gameserver.enums.PeriodType;
import ext.mods.gameserver.enums.RestartType;
import ext.mods.gameserver.enums.SealType;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.PlaySound;
import ext.mods.gameserver.network.serverpackets.SSQInfo;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public class SevenSignsManager
{
	private static final CLogger LOGGER = new CLogger(SevenSignsManager.class.getName());
	
	private static final String LOAD_DATA = "SELECT char_obj_id, cabal, seal, red_stones, green_stones, blue_stones, ancient_adena_amount, contribution_score FROM seven_signs";
	private static final String LOAD_STATUS = "SELECT * FROM seven_signs_status WHERE id=0";
	private static final String INSERT_PLAYER = "INSERT INTO seven_signs (char_obj_id, cabal, seal) VALUES (?,?,?)";
	private static final String UPDATE_PLAYER = "UPDATE seven_signs SET cabal=?, seal=?, red_stones=?, green_stones=?, blue_stones=?, ancient_adena_amount=?, contribution_score=? WHERE char_obj_id=?";
	private static final String UPDATE_STATUS = "UPDATE seven_signs_status SET current_cycle=?, active_period=?, previous_winner=?, " + "dawn_stone_score=?, dawn_festival_score=?, dusk_stone_score=?, dusk_festival_score=?, " + "avarice_owner=?, gnosis_owner=?, strife_owner=?, avarice_dawn_score=?, gnosis_dawn_score=?, " + "strife_dawn_score=?, avarice_dusk_score=?, gnosis_dusk_score=?, strife_dusk_score=?, " + "festival_cycle=?, accumulated_bonus0=?, accumulated_bonus1=?, accumulated_bonus2=?," + "accumulated_bonus3=?, accumulated_bonus4=?, date=? WHERE id=0";
	
	public static final String SEVEN_SIGNS_HTML_PATH = "html/seven_signs/";
	
	public static final int PERIOD_START_HOUR = 18;
	public static final int PERIOD_START_MINS = 00;
	public static final int PERIOD_START_DAY = Calendar.MONDAY;
	
	public static final int PERIOD_MINOR_LENGTH = 900000;
	public static final int PERIOD_MAJOR_LENGTH = 604800000 - PERIOD_MINOR_LENGTH;
	
	public static final int RECORD_SEVEN_SIGNS_ID = 5707;
	public static final int CERTIFICATE_OF_APPROVAL_ID = 6388;
	public static final int RECORD_SEVEN_SIGNS_COST = 500;
	public static final int ADENA_JOIN_DAWN_COST = 50000;
	
	public static final int SEAL_STONE_BLUE_ID = 6360;
	public static final int SEAL_STONE_GREEN_ID = 6361;
	public static final int SEAL_STONE_RED_ID = 6362;
	
	public static final int SEAL_STONE_BLUE_VALUE = 3;
	public static final int SEAL_STONE_GREEN_VALUE = 5;
	public static final int SEAL_STONE_RED_VALUE = 10;
	
	private final Calendar _nextPeriodChange = Calendar.getInstance();
	private Calendar _lastSave = Calendar.getInstance();
	
	protected PeriodType _activePeriod;
	protected int _currentCycle;
	protected double _dawnStoneScore;
	protected double _duskStoneScore;
	protected CabalType _previousWinner;
	
	private final Map<Integer, StatSet> _playersData = new HashMap<>();
	private final Map<SealType, CabalType> _sealOwners = new EnumMap<>(SealType.class);
	private final Map<SealType, Integer> _duskScores = new EnumMap<>(SealType.class);
	private final Map<SealType, Integer> _dawnScores = new EnumMap<>(SealType.class);
	
	protected SevenSignsManager()
	{
		restoreSevenSignsData();
		
		LOGGER.info("Currently on {} period.", _activePeriod.getName());
		initializeSeals();
		
		long milliToChange = 0;
		if (isNextPeriodChangeInPast())
			LOGGER.info("Next Seven Signs period is already computed.");
		else
		{
			setCalendarForNextPeriodChange();
			milliToChange = getMilliToPeriodChange();
		}
		
		ThreadPool.schedule(new SevenSignsPeriodChange(), milliToChange);
		
		double numSecs = (milliToChange / 1000) % 60;
		double countDown = ((milliToChange / 1000) - numSecs) / 60;
		int numMins = (int) Math.floor(countDown % 60);
		countDown = (countDown - numMins) / 60;
		int numHours = (int) Math.floor(countDown % 24);
		int numDays = (int) Math.floor((countDown - numHours) / 24);
		
		LOGGER.info("Next Seven Signs period begins in {} days, {} hours and {} mins.", numDays, numHours, numMins);
	}
	
	private boolean isNextPeriodChangeInPast()
	{
		Calendar lastPeriodChange = Calendar.getInstance();
		switch (_activePeriod)
		{
			case SEAL_VALIDATION, COMPETITION:
				lastPeriodChange.set(Calendar.DAY_OF_WEEK, PERIOD_START_DAY);
				lastPeriodChange.set(Calendar.HOUR_OF_DAY, PERIOD_START_HOUR);
				lastPeriodChange.set(Calendar.MINUTE, PERIOD_START_MINS);
				lastPeriodChange.set(Calendar.SECOND, 0);
				if (Calendar.getInstance().before(lastPeriodChange))
					lastPeriodChange.add(Calendar.HOUR, -24 * 7);
				break;
			
			case RECRUITING, RESULTS:
				lastPeriodChange.setTimeInMillis(_lastSave.getTimeInMillis() + PERIOD_MINOR_LENGTH);
				break;
		}
		
		return _lastSave.getTimeInMillis() > 7 && _lastSave.before(lastPeriodChange);
	}
	
	public static int calcScore(int blueCount, int greenCount, int redCount)
	{
		return blueCount * SEAL_STONE_BLUE_VALUE + greenCount * SEAL_STONE_GREEN_VALUE + redCount * SEAL_STONE_RED_VALUE;
	}
	
	public final int getCurrentCycle()
	{
		return _currentCycle;
	}
	
	public final PeriodType getCurrentPeriod()
	{
		return _activePeriod;
	}
	
	private final int getDaysToPeriodChange()
	{
		int numDays = _nextPeriodChange.get(Calendar.DAY_OF_WEEK) - PERIOD_START_DAY;
		
		if (numDays < 0)
			return 0 - numDays;
		
		return 7 - numDays;
	}
	
	public final long getMilliToPeriodChange()
	{
		return _nextPeriodChange.getTimeInMillis() - System.currentTimeMillis();
	}
	
	/**
	 * Calculate the number of days until the next period.<BR>
	 * A period starts at 18:00 pm (local time), like on official servers.
	 */
	protected void setCalendarForNextPeriodChange()
	{
		switch (_activePeriod)
		{
			case SEAL_VALIDATION, COMPETITION:
				int daysToChange = getDaysToPeriodChange();
				
				if (daysToChange == 7 && (_nextPeriodChange.get(Calendar.HOUR_OF_DAY) < PERIOD_START_HOUR || (_nextPeriodChange.get(Calendar.HOUR_OF_DAY) == PERIOD_START_HOUR && _nextPeriodChange.get(Calendar.MINUTE) < PERIOD_START_MINS)))
					daysToChange = 0;
					
				if (daysToChange > 0)
					_nextPeriodChange.add(Calendar.DATE, daysToChange);
				
				_nextPeriodChange.set(Calendar.HOUR_OF_DAY, PERIOD_START_HOUR);
				_nextPeriodChange.set(Calendar.MINUTE, PERIOD_START_MINS);
				_nextPeriodChange.set(Calendar.SECOND, 0);
				_nextPeriodChange.set(Calendar.MILLISECOND, 0);
				break;
			
			case RECRUITING, RESULTS:
				_nextPeriodChange.add(Calendar.MILLISECOND, PERIOD_MINOR_LENGTH);
				break;
		}
		LOGGER.info("Next Seven Signs period change set to {}.", _nextPeriodChange.getTime());
	}
	
	public final boolean isRecruitingPeriod()
	{
		return _activePeriod == PeriodType.RECRUITING;
	}
	
	public final boolean isSealValidationPeriod()
	{
		return _activePeriod == PeriodType.SEAL_VALIDATION;
	}
	
	public final boolean isCompResultsPeriod()
	{
		return _activePeriod == PeriodType.RESULTS;
	}
	
	public final int getCurrentScore(CabalType cabal)
	{
		double totalStoneScore = _dawnStoneScore + _duskStoneScore;
		
		switch (cabal)
		{
			case DAWN:
				return Math.round((float) (_dawnStoneScore / ((float) totalStoneScore == 0 ? 1 : totalStoneScore)) * 500) + getCurrentFestivalScore(cabal);
			
			case DUSK:
				return Math.round((float) (_duskStoneScore / ((float) totalStoneScore == 0 ? 1 : totalStoneScore)) * 500) + getCurrentFestivalScore(cabal);
		}
		
		return 0;
	}
	
	public final double getCurrentStoneScore(CabalType cabal)
	{
		switch (cabal)
		{
			case DAWN:
				return _dawnStoneScore;
			
			case DUSK:
				return _duskStoneScore;
		}
		
		return 0;
	}
	
	public final int getCurrentFestivalScore(CabalType cabal)
	{
		return FestivalOfDarknessManager.getInstance().getFestivalScore(cabal);
	}
	
	public final CabalType getWinningCabal()
	{
		final int duskScore = getCurrentScore(CabalType.DUSK);
		final int dawnScore = getCurrentScore(CabalType.DAWN);
		
		if (duskScore == dawnScore)
			return CabalType.NORMAL;
		
		if (duskScore > dawnScore)
			return CabalType.DUSK;
		
		return CabalType.DAWN;
	}
	
	public final CabalType getLosingCabal()
	{
		final int duskScore = getCurrentScore(CabalType.DUSK);
		final int dawnScore = getCurrentScore(CabalType.DAWN);
		
		if (duskScore == dawnScore)
			return CabalType.NORMAL;
		
		if (duskScore > dawnScore)
			return CabalType.DAWN;
		
		return CabalType.DUSK;
	}
	
	public final CabalType getSealOwner(SealType seal)
	{
		return _sealOwners.get(seal);
	}
	
	public final Map<SealType, CabalType> getSealOwners()
	{
		return _sealOwners;
	}
	
	public final int getSealProportion(SealType seal, CabalType cabal)
	{
		switch (cabal)
		{
			case DAWN:
				return _dawnScores.get(seal);
			
			case DUSK:
				return _duskScores.get(seal);
		}
		
		return 0;
	}
	
	public final int getTotalMembers(CabalType cabal)
	{
		int cabalMembers = 0;
		
		for (StatSet set : _playersData.values())
			if (set.getEnum("cabal", CabalType.class) == cabal)
				cabalMembers++;
			
		return cabalMembers;
	}
	
	public int getPlayerStoneContrib(int objectId)
	{
		final StatSet set = _playersData.get(objectId);
		if (set == null)
			return 0;
		
		return set.getInteger("red_stones") + set.getInteger("green_stones") + set.getInteger("blue_stones");
	}
	
	public int getPlayerContribScore(int objectId)
	{
		final StatSet set = _playersData.get(objectId);
		if (set == null)
			return 0;
		
		return set.getInteger("contribution_score");
	}
	
	public int getPlayerAdenaCollect(int objectId)
	{
		final StatSet set = _playersData.get(objectId);
		if (set == null)
			return 0;
		
		return set.getInteger("ancient_adena_amount");
	}
	
	public SealType getPlayerSeal(int objectId)
	{
		final StatSet set = _playersData.get(objectId);
		if (set == null)
			return SealType.NONE;
		
		return set.getEnum("seal", SealType.class);
	}
	
	public CabalType getPlayerCabal(int objectId)
	{
		final StatSet set = _playersData.get(objectId);
		if (set == null)
			return CabalType.NORMAL;
		
		return set.getEnum("cabal", CabalType.class);
	}
	
	/**
	 * Restores all Seven Signs data and settings, usually called at server startup.
	 */
	protected void restoreSevenSignsData()
	{
		try (Connection con = ConnectionPool.getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement(LOAD_DATA);
				ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					final int objectId = rs.getInt("char_obj_id");
					
					final StatSet set = new StatSet();
					set.set("char_obj_id", objectId);
					set.set("cabal", Enum.valueOf(CabalType.class, rs.getString("cabal")));
					set.set("seal", Enum.valueOf(SealType.class, rs.getString("seal")));
					set.set("red_stones", rs.getInt("red_stones"));
					set.set("green_stones", rs.getInt("green_stones"));
					set.set("blue_stones", rs.getInt("blue_stones"));
					set.set("ancient_adena_amount", rs.getDouble("ancient_adena_amount"));
					set.set("contribution_score", rs.getDouble("contribution_score"));
					
					_playersData.put(objectId, set);
				}
			}
			
			try (PreparedStatement ps = con.prepareStatement(LOAD_STATUS);
				ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					_currentCycle = rs.getInt("current_cycle");
					_activePeriod = Enum.valueOf(PeriodType.class, rs.getString("active_period"));
					_previousWinner = Enum.valueOf(CabalType.class, rs.getString("previous_winner"));
					
					_dawnStoneScore = rs.getDouble("dawn_stone_score");
					_duskStoneScore = rs.getDouble("dusk_stone_score");
					
					_sealOwners.put(SealType.AVARICE, Enum.valueOf(CabalType.class, rs.getString("avarice_owner")));
					_sealOwners.put(SealType.GNOSIS, Enum.valueOf(CabalType.class, rs.getString("gnosis_owner")));
					_sealOwners.put(SealType.STRIFE, Enum.valueOf(CabalType.class, rs.getString("strife_owner")));
					
					_dawnScores.put(SealType.AVARICE, rs.getInt("avarice_dawn_score"));
					_dawnScores.put(SealType.GNOSIS, rs.getInt("gnosis_dawn_score"));
					_dawnScores.put(SealType.STRIFE, rs.getInt("strife_dawn_score"));
					
					_duskScores.put(SealType.AVARICE, rs.getInt("avarice_dusk_score"));
					_duskScores.put(SealType.GNOSIS, rs.getInt("gnosis_dusk_score"));
					_duskScores.put(SealType.STRIFE, rs.getInt("strife_dusk_score"));
					
					_lastSave.setTimeInMillis(rs.getLong("date"));
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't load Seven Signs data.", e);
		}
	}
	
	/**
	 * Saves all Seven Signs player data.<br>
	 * Should be called on period change and shutdown only.
	 */
	public void saveSevenSignsData()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_PLAYER))
		{
			for (StatSet set : _playersData.values())
			{
				ps.setString(1, set.getString("cabal"));
				ps.setString(2, set.getString("seal"));
				ps.setInt(3, set.getInteger("red_stones"));
				ps.setInt(4, set.getInteger("green_stones"));
				ps.setInt(5, set.getInteger("blue_stones"));
				ps.setDouble(6, set.getDouble("ancient_adena_amount"));
				ps.setDouble(7, set.getDouble("contribution_score"));
				ps.setInt(8, set.getInteger("char_obj_id"));
				ps.addBatch();
			}
			ps.executeBatch();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't save Seven Signs player data.", e);
		}
	}
	
	public final void saveSevenSignsStatus()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_STATUS))
		{
			ps.setInt(1, _currentCycle);
			ps.setString(2, _activePeriod.toString());
			ps.setString(3, _previousWinner.toString());
			ps.setDouble(4, _dawnStoneScore);
			ps.setInt(5, 0);
			ps.setDouble(6, _duskStoneScore);
			ps.setInt(7, 0);
			ps.setString(8, _sealOwners.get(SealType.AVARICE).toString());
			ps.setString(9, _sealOwners.get(SealType.GNOSIS).toString());
			ps.setString(10, _sealOwners.get(SealType.STRIFE).toString());
			ps.setInt(11, _dawnScores.get(SealType.AVARICE));
			ps.setInt(12, _dawnScores.get(SealType.GNOSIS));
			ps.setInt(13, _dawnScores.get(SealType.STRIFE));
			ps.setInt(14, _duskScores.get(SealType.AVARICE));
			ps.setInt(15, _duskScores.get(SealType.GNOSIS));
			ps.setInt(16, _duskScores.get(SealType.STRIFE));
			ps.setInt(17, FestivalOfDarknessManager.getInstance().getCurrentFestivalCycle());
			
			for (int i = 0; i < FestivalOfDarknessManager.FESTIVAL_COUNT; i++)
				ps.setInt(18 + i, FestivalOfDarknessManager.getInstance().getAccumulatedBonus(i));
			
			_lastSave = Calendar.getInstance();
			ps.setLong(18 + FestivalOfDarknessManager.FESTIVAL_COUNT, _lastSave.getTimeInMillis());
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't save Seven Signs status data.", e);
		}
	}
	
	/**
	 * Used to reset the cabal details of all players, and update the database.<BR>
	 * Primarily used when beginning a new cycle, and should otherwise never be called.
	 */
	protected void resetPlayerData()
	{
		for (StatSet set : _playersData.values())
		{
			set.set("cabal", CabalType.NORMAL);
			set.set("seal", SealType.NONE);
			set.set("contribution_score", 0);
		}
	}
	
	/**
	 * Used to specify cabal-related details for the specified player.<br>
	 * This method checks to see if the player has registered before and will update the database if necessary.
	 * @param objectId
	 * @param cabal
	 * @param seal
	 * @return the cabal ID the player has joined.
	 */
	public CabalType setPlayerInfo(int objectId, CabalType cabal, SealType seal)
	{
		StatSet set = _playersData.get(objectId);
		if (set != null)
		{
			set.set("cabal", cabal);
			set.set("seal", seal);
		}
		else
		{
			set = new StatSet();
			set.set("char_obj_id", objectId);
			set.set("cabal", cabal);
			set.set("seal", seal);
			set.set("red_stones", 0);
			set.set("green_stones", 0);
			set.set("blue_stones", 0);
			set.set("ancient_adena_amount", 0);
			set.set("contribution_score", 0);
			
			_playersData.put(objectId, set);
			
			try (Connection con = ConnectionPool.getConnection();
				PreparedStatement ps = con.prepareStatement(INSERT_PLAYER))
			{
				ps.setInt(1, objectId);
				ps.setString(2, cabal.toString());
				ps.setString(3, seal.toString());
				ps.execute();
			}
			catch (Exception e)
			{
				LOGGER.error("Couldn't save Seven Signs player info data.", e);
			}
		}
		
		if (cabal == CabalType.DAWN)
			_dawnScores.put(seal, _dawnScores.get(seal) + 1);
		else
			_duskScores.put(seal, _duskScores.get(seal) + 1);
		
		return cabal;
	}
	
	/**
	 * @param objectId
	 * @return the amount of ancient adena the specified player can claim, if any.
	 */
	public int getAncientAdenaReward(int objectId)
	{
		StatSet set = _playersData.get(objectId);
		int rewardAmount = set.getInteger("ancient_adena_amount");
		
		set.set("red_stones", 0);
		set.set("green_stones", 0);
		set.set("blue_stones", 0);
		set.set("ancient_adena_amount", 0);
		
		return rewardAmount;
	}
	
	/**
	 * Used to add the specified player's seal stone contribution points to the current total for their cabal. Returns the point score the contribution was worth.<br>
	 * Each stone count <B>must be</B> broken down and specified by the stone's color.
	 * @param objectId The objectId of the player.
	 * @param blueCount Amount of blue stones.
	 * @param greenCount Amount of green stones.
	 * @param redCount Amount of red stones.
	 * @return
	 */
	public int addPlayerStoneContrib(int objectId, int blueCount, int greenCount, int redCount)
	{
		StatSet set = _playersData.get(objectId);
		
		int contribScore = calcScore(blueCount, greenCount, redCount);
		int totalAncientAdena = set.getInteger("ancient_adena_amount") + contribScore;
		int totalContribScore = set.getInteger("contribution_score") + contribScore;
		
		if (totalContribScore > Config.MAXIMUM_PLAYER_CONTRIB)
			return -1;
		
		set.set("red_stones", set.getInteger("red_stones") + redCount);
		set.set("green_stones", set.getInteger("green_stones") + greenCount);
		set.set("blue_stones", set.getInteger("blue_stones") + blueCount);
		set.set("ancient_adena_amount", totalAncientAdena);
		set.set("contribution_score", totalContribScore);
		
		switch (getPlayerCabal(objectId))
		{
			case DAWN:
				_dawnStoneScore += contribScore;
				break;
			
			case DUSK:
				_duskStoneScore += contribScore;
				break;
		}
		
		return contribScore;
	}
	
	/**
	 * Used to initialize the seals for each cabal. (Used at startup or at beginning of a new cycle). This method should be called after <B>resetSeals()</B> and <B>calcNewSealOwners()</B> on a new cycle.
	 */
	protected void initializeSeals()
	{
		for (Entry<SealType, CabalType> sealEntry : _sealOwners.entrySet())
		{
			final SealType currentSeal = sealEntry.getKey();
			final CabalType sealOwner = sealEntry.getValue();
			
			if (sealOwner != CabalType.NORMAL)
			{
				if (isSealValidationPeriod())
					LOGGER.info("The {} have won {}.", sealOwner.getFullName(), currentSeal.getFullName());
				else
					LOGGER.info("The {} is currently owned by {}.", currentSeal.getFullName(), sealOwner.getFullName());
			}
			else
				LOGGER.info("The {} remains unclaimed.", currentSeal.getFullName());
		}
	}
	
	/**
	 * Only really used at the beginning of a new cycle, this method resets all seal-related data.
	 */
	protected void resetSeals()
	{
		_dawnScores.put(SealType.AVARICE, 0);
		_dawnScores.put(SealType.GNOSIS, 0);
		_dawnScores.put(SealType.STRIFE, 0);
		
		_duskScores.put(SealType.AVARICE, 0);
		_duskScores.put(SealType.GNOSIS, 0);
		_duskScores.put(SealType.STRIFE, 0);
	}
	
	/**
	 * Calculates the ownership of the three Seals of the Seven Signs, based on various criterias.<BR>
	 * Should only ever called at the beginning of a new cycle.
	 */
	protected void calcNewSealOwners()
	{
		for (SealType seal : _dawnScores.keySet())
		{
			final CabalType prevSealOwner = _sealOwners.get(seal);
			
			final int dawnProportion = getSealProportion(seal, CabalType.DAWN);
			final int totalDawnMembers = Math.max(1, getTotalMembers(CabalType.DAWN));
			final int dawnPercent = Math.round(((float) dawnProportion / (float) totalDawnMembers) * 100);
			
			final int duskProportion = getSealProportion(seal, CabalType.DUSK);
			final int totalDuskMembers = Math.max(1, getTotalMembers(CabalType.DUSK));
			final int duskPercent = Math.round(((float) duskProportion / (float) totalDuskMembers) * 100);
			
			CabalType newSealOwner = CabalType.NORMAL;
			
			switch (prevSealOwner)
			{
				case NORMAL:
					switch (getWinningCabal())
					{
						case DAWN:
							if (dawnPercent >= 35)
								newSealOwner = CabalType.DAWN;
							break;
						
						case DUSK:
							if (duskPercent >= 35)
								newSealOwner = CabalType.DUSK;
							break;
					}
					break;
				
				case DAWN:
					switch (getWinningCabal())
					{
						case NORMAL:
							if (dawnPercent >= 10)
								newSealOwner = CabalType.DAWN;
							break;
						
						case DAWN:
							if (dawnPercent >= 10)
								newSealOwner = CabalType.DAWN;
							break;
						
						case DUSK:
							if (duskPercent >= 35)
								newSealOwner = CabalType.DUSK;
							else if (dawnPercent >= 10)
								newSealOwner = CabalType.DAWN;
							break;
					}
					break;
				
				case DUSK:
					switch (getWinningCabal())
					{
						case NORMAL:
							if (duskPercent >= 10)
								newSealOwner = CabalType.DUSK;
							break;
						
						case DAWN:
							if (dawnPercent >= 35)
								newSealOwner = CabalType.DAWN;
							else if (duskPercent >= 10)
								newSealOwner = CabalType.DUSK;
							break;
						
						case DUSK:
							if (duskPercent >= 10)
								newSealOwner = CabalType.DUSK;
							break;
					}
					break;
			}
			
			_sealOwners.put(seal, newSealOwner);
			
			switch (seal)
			{
				case AVARICE:
					if (newSealOwner == CabalType.DAWN)
						World.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.DAWN_OBTAINED_AVARICE));
					else if (newSealOwner == CabalType.DUSK)
						World.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.DUSK_OBTAINED_AVARICE));
					break;
				
				case GNOSIS:
					if (newSealOwner == CabalType.DAWN)
						World.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.DAWN_OBTAINED_GNOSIS));
					else if (newSealOwner == CabalType.DUSK)
						World.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.DUSK_OBTAINED_GNOSIS));
					break;
				
				case STRIFE:
					if (newSealOwner == CabalType.DAWN)
						World.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.DAWN_OBTAINED_STRIFE));
					else if (newSealOwner == CabalType.DUSK)
						World.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.DUSK_OBTAINED_STRIFE));
					
					CastleManager.getInstance().validateTaxes(newSealOwner);
					break;
			}
		}
	}
	
	/**
	 * This method is called to remove all players from catacombs and necropolises, who belong to the losing cabal.<BR>
	 * <b>Should only ever called at the beginning of Seal Validation.</b>
	 * @param winningCabal
	 */
	protected void teleLosingCabalFromDungeons(CabalType winningCabal)
	{
		for (Player player : World.getInstance().getPlayers())
		{
			if (player.isGM() || !player.isIn7sDungeon())
				continue;
			
			final StatSet set = _playersData.get(player.getObjectId());
			if (set != null)
			{
				final CabalType playerCabal = set.getEnum("cabal", CabalType.class);
				if (isSealValidationPeriod() || isCompResultsPeriod())
				{
					if (playerCabal == winningCabal)
						continue;
				}
				else if (playerCabal == CabalType.NORMAL)
					continue;
			}
			
			player.teleportTo(RestartType.TOWN);
			player.setIsIn7sDungeon(false);
		}
	}
	
	/**
	 * The primary controller of period change of the Seven Signs system. This runs all related tasks depending on the period that is about to begin.
	 */
	protected class SevenSignsPeriodChange implements Runnable
	{
		@Override
		public void run()
		{
			final PeriodType periodEnded = _activePeriod;
			
			_activePeriod = PeriodType.VALUES[(_activePeriod.ordinal() + 1) % PeriodType.VALUES.length];
			
			switch (periodEnded)
			{
				case RECRUITING:
					FestivalOfDarknessManager.getInstance().startFestivalManager();
					
					CastleManager.getInstance().resetCertificates();
					
					World.toAllOnlinePlayers(new PlaySound(0, "SSQ_Neutral_01"));
					
					World.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.QUEST_EVENT_PERIOD_BEGUN));
					break;
				
				case COMPETITION:
					World.toAllOnlinePlayers(new PlaySound(0, "SSQ_Neutral_01"));
					
					World.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.QUEST_EVENT_PERIOD_ENDED));
					
					final CabalType winningCabal = getWinningCabal();
					
					FestivalOfDarknessManager.getInstance().rewardHighestRanked();
					
					calcNewSealOwners();
					
					switch (winningCabal)
					{
						case DAWN:
							World.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.DAWN_WON));
							break;
						
						case DUSK:
							World.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.DUSK_WON));
							break;
					}
					
					_previousWinner = winningCabal;
					break;
				
				case RESULTS:
					initializeSeals();
					
					giveSosEffect(getSealOwner(SealType.STRIFE));
					
					World.toAllOnlinePlayers(new PlaySound(0, _previousWinner == CabalType.DAWN ? "SSQ_Dawn_01" : "SSQ_Dusk_01"));
					
					World.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.SEAL_VALIDATION_PERIOD_BEGUN));
					
					LOGGER.info("The {} have won the competition with {} points.", _previousWinner.getFullName(), getCurrentScore(_previousWinner));
					break;
				
				case SEAL_VALIDATION:
					_activePeriod = PeriodType.RECRUITING;
					
					World.toAllOnlinePlayers(new PlaySound(0, "SSQ_Neutral_01"));
					
					World.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.SEAL_VALIDATION_PERIOD_ENDED));
					
					removeSosEffect();
					
					resetPlayerData();
					resetSeals();
					
					_currentCycle++;
					
					FestivalOfDarknessManager.getInstance().resetFestivalData(false);
					
					_dawnStoneScore = 0;
					_duskStoneScore = 0;
					break;
			}
			
			saveSevenSignsData();
			saveSevenSignsStatus();
			
			if (!Config.CATACOMBS_IN_ANY_PERIOD)
				teleLosingCabalFromDungeons(getWinningCabal());
			
			World.toAllOnlinePlayers(SSQInfo.sendSky());
			SpawnManager.getInstance().notifySevenSignsChange();
			
			LOGGER.info("The {} period of Seven Signs has begun.", _activePeriod.getName());
			
			setCalendarForNextPeriodChange();
			
			ThreadPool.schedule(new SevenSignsPeriodChange(), getMilliToPeriodChange());
		}
	}
	
	/**
	 * Buff/debuff players following their membership to Seal of Strife.
	 * @param strifeOwner The cabal owning the Seal of Strife.
	 */
	public void giveSosEffect(CabalType strifeOwner)
	{
		for (Player player : World.getInstance().getPlayers())
		{
			final CabalType cabal = getPlayerCabal(player.getObjectId());
			if (cabal != CabalType.NORMAL)
			{
				if (cabal == strifeOwner)
					player.addSkill(FrequentSkill.THE_VICTOR_OF_WAR.getSkill(), false);
				else
					player.addSkill(FrequentSkill.THE_VANQUISHED_OF_WAR.getSkill(), false);
			}
		}
	}
	
	/**
	 * Stop Seal of Strife effects on all online characters.
	 */
	public void removeSosEffect()
	{
		for (Player player : World.getInstance().getPlayers())
		{
			player.removeSkill(FrequentSkill.THE_VICTOR_OF_WAR.getSkill().getId(), false);
			player.removeSkill(FrequentSkill.THE_VANQUISHED_OF_WAR.getSkill().getId(), false);
		}
	}
	
	public void changePeriod()
	{
		ThreadPool.schedule(new SevenSignsPeriodChange(), 10);
	}
	
	public boolean allowCatacombsInAnyPeriod()
	{
		return Config.CATACOMBS_IN_ANY_PERIOD;
	}
	
	public static SevenSignsManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SevenSignsManager INSTANCE = new SevenSignsManager();
	}
}
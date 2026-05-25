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

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ConnectionPool;
import ext.mods.commons.pool.ThreadPool;
import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.data.xml.NpcData;
import ext.mods.gameserver.idfactory.IdFactory;
import ext.mods.gameserver.model.HistoryInfo;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.zone.type.DerbyTrackZone;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.DeleteObject;
import ext.mods.gameserver.network.serverpackets.MonRaceInfo;
import ext.mods.gameserver.network.serverpackets.PlaySound;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public class DerbyTrackManager
{
	private static final CLogger LOGGER = new CLogger(DerbyTrackManager.class.getName());
	
	private static final String SAVE_HISTORY = "INSERT INTO mdt_history (race_id, first, second, odd_rate) VALUES (?,?,?,?)";
	private static final String LOAD_HISTORY = "SELECT * FROM mdt_history";
	private static final String LOAD_BETS = "SELECT * FROM mdt_bets";
	private static final String SAVE_BETS = "REPLACE INTO mdt_bets (lane_id, bet) VALUES (?,?)";
	private static final String CLEAR_BETS = "UPDATE mdt_bets SET bet = 0";
	
	public enum RaceState
	{
		ACCEPTING_BETS,
		WAITING,
		STARTING_RACE,
		RACE_END
	}
	
	private static final PlaySound SOUND_1 = new PlaySound(1, "S_Race");
	private static final PlaySound SOUND_2 = new PlaySound("ItemSound2.race_start");
	
	private static final int[][] CODES =
	{
		{
			-1,
			0
		},
		{
			0,
			15322
		},
		{
			13765,
			-1
		}
	};
	
	private final List<Npc> _runners = new ArrayList<>();
	private final TreeMap<Integer, HistoryInfo> _history = new TreeMap<>();
	private final Map<Integer, Long> _betsPerLane = new ConcurrentHashMap<>();
	private final List<Double> _odds = new ArrayList<>();
	
	private int _raceNumber = 1;
	private int _finalCountdown = 0;
	private RaceState _state = RaceState.RACE_END;
	
	private MonRaceInfo _packet;
	
	private List<Npc> _chosenRunners;
	private int[][] _speeds;
	private int _firstIndex;
	private int _secondIndex;
	
	protected DerbyTrackManager()
	{
		loadHistory();
		
		loadBets();
		
		try
		{
			for (int i = 31003; i < 31027; i++)
			{
				final NpcTemplate template = NpcData.getInstance().getTemplate(i);
				if (template == null)
					continue;
				
				final Constructor<?> constructor = Class.forName("ext.mods.gameserver.model.actor.instance." + template.getType()).getConstructors()[0];
				
				_runners.add((Npc) constructor.newInstance(IdFactory.getInstance().getNextId(), template));
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't initialize runners.", e);
		}
		
		_speeds = new int[8][20];
		
		ThreadPool.scheduleAtFixedRate(this::countdown, 0, 1000);
	}
	
	public List<Npc> getRunners()
	{
		return _chosenRunners;
	}
	
	/**
	 * @param index : The actual index of List to check on.
	 * @return the name of the Npc.
	 */
	public String getRunnerName(int index)
	{
		final Npc npc = _chosenRunners.get(index);
		return (npc == null) ? "" : npc.getName();
	}
	
	public int[][] getSpeeds()
	{
		return _speeds;
	}
	
	public int getFirst()
	{
		return _firstIndex;
	}
	
	public int getSecond()
	{
		return _secondIndex;
	}
	
	public MonRaceInfo getRacePacket()
	{
		return _packet;
	}
	
	public RaceState getCurrentRaceState()
	{
		return _state;
	}
	
	public int getRaceNumber()
	{
		return _raceNumber;
	}
	
	public List<HistoryInfo> getLastHistoryEntries()
	{
		return _history.descendingMap().values().stream().limit(8).toList();
	}
	
	public HistoryInfo getHistoryInfo(int raceNumber)
	{
		return _history.get(raceNumber);
	}
	
	public List<Double> getOdds()
	{
		return _odds;
	}
	
	private void newRace()
	{
		_history.put(_raceNumber, new HistoryInfo(_raceNumber, 0, 0, 0));
		
		Collections.shuffle(_runners);
		
		_chosenRunners = _runners.subList(0, 8);
	}
	
	private void newSpeeds()
	{
		_speeds = new int[8][20];
		
		int total = 0;
		int winnerDistance = 0;
		int secondDistance = 0;
		
		for (int i = 0; i < 8; i++)
		{
			total = 0;
			
			for (int j = 0; j < 20; j++)
			{
				if (j == 19)
					_speeds[i][j] = 100;
				else
					_speeds[i][j] = Rnd.get(60) + 65;
				
				total += _speeds[i][j];
			}
			
			if (total >= winnerDistance)
			{
				_secondIndex = _firstIndex;
				
				secondDistance = winnerDistance;
				
				_firstIndex = i;
				
				winnerDistance = total;
			}
			else if (total >= secondDistance)
			{
				_secondIndex = i;
				
				secondDistance = total;
			}
		}
	}
	
	/**
	 * Load past races informations, feeding _history arrayList.<br>
	 * Also sets _raceNumber, based on latest HistoryInfo loaded.
	 */
	private void loadHistory()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(LOAD_HISTORY);
			ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
			{
				final int savedRaceNumber = rs.getInt("race_id");
				
				_history.put(savedRaceNumber, new HistoryInfo(savedRaceNumber, rs.getInt("first"), rs.getInt("second"), rs.getDouble("odd_rate")));
				
				if (_raceNumber <= savedRaceNumber)
					_raceNumber = savedRaceNumber + 1;
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Can't load Derby Track history.", e);
		}
		LOGGER.info("Loaded {} Derby Track records, currently on race #{}.", _history.size(), _raceNumber);
	}
	
	/**
	 * Load current bets per lane ; initialize the map keys.
	 */
	private void loadBets()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(LOAD_BETS);
			ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
				setBetOnLane(rs.getInt("lane_id"), rs.getLong("bet"), false);
		}
		catch (Exception e)
		{
			LOGGER.error("Can't load Derby Track bets.", e);
		}
	}
	
	/**
	 * Clear all lanes bets, either on database or Map.
	 */
	private void clearBets()
	{
		for (int key : _betsPerLane.keySet())
			_betsPerLane.put(key, 0L);
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(CLEAR_BETS))
		{
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.error("Can't clear Derby Track bets.", e);
		}
	}
	
	/**
	 * Setup lane bet, based on previous value (if any).
	 * @param lane : The lane to edit.
	 * @param amount : The amount to add.
	 * @param saveOnDb : Should it be saved on the database or not.
	 */
	public void setBetOnLane(int lane, long amount, boolean saveOnDb)
	{
		final long sum = _betsPerLane.getOrDefault(lane, 0L) + amount;
		
		_betsPerLane.put(lane, sum);
		
		if (saveOnDb)
		{
			try (Connection con = ConnectionPool.getConnection();
				PreparedStatement ps = con.prepareStatement(SAVE_BETS))
			{
				ps.setInt(1, lane);
				ps.setLong(2, sum);
				ps.execute();
			}
			catch (Exception e)
			{
				LOGGER.error("Can't save Derby Track bet.", e);
			}
		}
	}
	
	/**
	 * Calculate odds for every lane, based on others lanes.
	 */
	private void calculateOdds()
	{
		_odds.clear();
		
		final Map<Integer, Long> sortedLanes = new TreeMap<>(_betsPerLane);
		
		long sumOfAllLanes = 0;
		for (long amount : sortedLanes.values())
			sumOfAllLanes += amount;
		
		for (long amount : sortedLanes.values())
			_odds.add((amount == 0) ? 0D : Math.max(1.25, sumOfAllLanes * 0.7 / amount));
	}
	
	private void countdown()
	{
		if (_finalCountdown > 1200)
			_finalCountdown = 0;
		
		switch (_finalCountdown)
		{
			case 0:
				newRace();
				newSpeeds();
				
				_state = RaceState.ACCEPTING_BETS;
				_packet = new MonRaceInfo(CODES[0][0], CODES[0][1], getRunners(), getSpeeds());
				
				ZoneManager.toAllPlayersInZoneType(DerbyTrackZone.class, _packet, SystemMessage.getSystemMessage(SystemMessageId.MONSRACE_TICKETS_AVAILABLE_FOR_S1_RACE).addNumber(_raceNumber));
				break;
			
			case 30, 60, 90, 120, 150, 180, 210, 240, 270, 330, 360, 390, 420, 450, 480, 510, 540, 570, 630, 660, 690, 720, 750, 780, 810, 870:
				ZoneManager.toAllPlayersInZoneType(DerbyTrackZone.class, SystemMessage.getSystemMessage(SystemMessageId.MONSRACE_TICKETS_NOW_AVAILABLE_FOR_S1_RACE).addNumber(_raceNumber));
				break;
			
			case 300:
				ZoneManager.toAllPlayersInZoneType(DerbyTrackZone.class, SystemMessage.getSystemMessage(SystemMessageId.MONSRACE_TICKETS_NOW_AVAILABLE_FOR_S1_RACE).addNumber(_raceNumber), SystemMessage.getSystemMessage(SystemMessageId.MONSRACE_TICKETS_STOP_IN_S1_MINUTES).addNumber(10));
				break;
			
			case 600:
				ZoneManager.toAllPlayersInZoneType(DerbyTrackZone.class, SystemMessage.getSystemMessage(SystemMessageId.MONSRACE_TICKETS_NOW_AVAILABLE_FOR_S1_RACE).addNumber(_raceNumber), SystemMessage.getSystemMessage(SystemMessageId.MONSRACE_TICKETS_STOP_IN_S1_MINUTES).addNumber(5));
				break;
			
			case 840:
				ZoneManager.toAllPlayersInZoneType(DerbyTrackZone.class, SystemMessage.getSystemMessage(SystemMessageId.MONSRACE_TICKETS_NOW_AVAILABLE_FOR_S1_RACE).addNumber(_raceNumber), SystemMessage.getSystemMessage(SystemMessageId.MONSRACE_TICKETS_STOP_IN_S1_MINUTES).addNumber(1));
				break;
			
			case 900:
				_state = RaceState.WAITING;
				
				calculateOdds();
				
				ZoneManager.toAllPlayersInZoneType(DerbyTrackZone.class, SystemMessage.getSystemMessage(SystemMessageId.MONSRACE_TICKETS_NOW_AVAILABLE_FOR_S1_RACE).addNumber(_raceNumber), SystemMessage.getSystemMessage(SystemMessageId.MONSRACE_S1_TICKET_SALES_CLOSED));
				break;
			
			case 960, 1020:
				final int minutes = (_finalCountdown == 960) ? 2 : 1;
				ZoneManager.toAllPlayersInZoneType(DerbyTrackZone.class, SystemMessage.getSystemMessage(SystemMessageId.MONSRACE_S2_BEGINS_IN_S1_MINUTES).addNumber(minutes));
				break;
			
			case 1050:
				ZoneManager.toAllPlayersInZoneType(DerbyTrackZone.class, SystemMessage.getSystemMessage(SystemMessageId.MONSRACE_S1_BEGINS_IN_30_SECONDS));
				break;
			
			case 1070:
				ZoneManager.toAllPlayersInZoneType(DerbyTrackZone.class, SystemMessage.getSystemMessage(SystemMessageId.MONSRACE_S1_COUNTDOWN_IN_FIVE_SECONDS));
				break;
			
			case 1075, 1076, 1077, 1078, 1079:
				final int seconds = 1080 - _finalCountdown;
				ZoneManager.toAllPlayersInZoneType(DerbyTrackZone.class, SystemMessage.getSystemMessage(SystemMessageId.MONSRACE_BEGINS_IN_S1_SECONDS).addNumber(seconds));
				break;
			
			case 1080:
				_state = RaceState.STARTING_RACE;
				_packet = new MonRaceInfo(CODES[1][0], CODES[1][1], getRunners(), getSpeeds());
				
				ZoneManager.toAllPlayersInZoneType(DerbyTrackZone.class, SystemMessage.getSystemMessage(SystemMessageId.MONSRACE_RACE_START), SOUND_1, SOUND_2, _packet);
				break;
			
			case 1085:
				_packet = new MonRaceInfo(CODES[2][0], CODES[2][1], getRunners(), getSpeeds());
				
				ZoneManager.toAllPlayersInZoneType(DerbyTrackZone.class, _packet);
				break;
			
			case 1115:
				_state = RaceState.RACE_END;
				
				final HistoryInfo info = getHistoryInfo(_raceNumber);
				if (info != null)
				{
					info.setFirst(getFirst());
					info.setSecond(getSecond());
					info.setOddRate(_odds.get(getFirst()));
					
					try (Connection con = ConnectionPool.getConnection();
						PreparedStatement ps = con.prepareStatement(SAVE_HISTORY))
					{
						ps.setInt(1, info.getRaceId());
						ps.setInt(2, info.getFirst());
						ps.setInt(3, info.getSecond());
						ps.setDouble(4, info.getOddRate());
						ps.execute();
					}
					catch (Exception e)
					{
						LOGGER.error("Can't save Derby Track history.", e);
					}
				}
				
				clearBets();
				
				ZoneManager.toAllPlayersInZoneType(DerbyTrackZone.class, SystemMessage.getSystemMessage(SystemMessageId.MONSRACE_FIRST_PLACE_S1_SECOND_S2).addNumber(getFirst() + 1).addNumber(getSecond() + 1), SystemMessage.getSystemMessage(SystemMessageId.MONSRACE_S1_RACE_END).addNumber(_raceNumber));
				_raceNumber++;
				break;
			
			case 1140:
				ZoneManager.toAllPlayersInZoneType(DerbyTrackZone.class, new DeleteObject(getRunners().get(0)), new DeleteObject(getRunners().get(1)), new DeleteObject(getRunners().get(2)), new DeleteObject(getRunners().get(3)), new DeleteObject(getRunners().get(4)), new DeleteObject(getRunners().get(5)), new DeleteObject(getRunners().get(6)), new DeleteObject(getRunners().get(7)));
				break;
		}
		_finalCountdown += 1;
	}
	
	public static DerbyTrackManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final DerbyTrackManager INSTANCE = new DerbyTrackManager();
	}
}
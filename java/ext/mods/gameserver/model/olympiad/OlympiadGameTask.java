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
package ext.mods.gameserver.model.olympiad;

import java.util.HashSet;
import java.util.Set;

import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ThreadPool;

import ext.mods.Config;
import ext.mods.gameserver.enums.SpawnType;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.zone.type.OlympiadStadiumZone;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public final class OlympiadGameTask implements Runnable
{
	protected static final CLogger LOGGER = new CLogger(OlympiadGameTask.class.getName());
	
	protected static final long BATTLE_PERIOD = Config.OLY_BATTLE;
	
	public static final int[] TELEPORT_TO_ARENA =
	{
		120,
		60,
		30,
		15,
		10,
		5,
		4,
		3,
		2,
		1,
		0
	};
	public static final int[] BATTLE_START_TIME =
	{
		60,
		50,
		40,
		30,
		20,
		10,
		5,
		4,
		3,
		2,
		1,
		0
	};
	public static final int[] TELEPORT_TO_TOWN =
	{
		40,
		30,
		20,
		10,
		5,
		4,
		3,
		2,
		1,
		0
	};
	
	private final OlympiadStadiumZone _zone;
	private AbstractOlympiadGame _game;
	private GameState _state = GameState.IDLE;
	private boolean _needAnnounce = false;
	private int _countDown = 0;
	private boolean _pausedByDisconnect = false;
	private final Set<Integer> _pausedPlayers = new HashSet<>();
	
	private enum GameState
	{
		BEGIN,
		TELE_TO_ARENA,
		GAME_STARTED,
		BATTLE_COUNTDOWN,
		BATTLE_STARTED,
		BATTLE_IN_PROGRESS,
		GAME_STOPPED,
		TELE_TO_TOWN,
		CLEANUP,
		IDLE
	}
	
	public OlympiadGameTask(OlympiadStadiumZone zone)
	{
		_zone = zone;
		zone.registerTask(this);
	}
	
	public final boolean isRunning()
	{
		return _state != GameState.IDLE;
	}
	
	public final boolean isGameStarted()
	{
		return _state.ordinal() >= GameState.GAME_STARTED.ordinal() && _state.ordinal() <= GameState.CLEANUP.ordinal();
	}
	
	public final boolean isInTimerTime()
	{
		return _state == GameState.BATTLE_COUNTDOWN;
	}
	
	public final boolean isBattleStarted()
	{
		return _state == GameState.BATTLE_IN_PROGRESS;
	}
	
	public final boolean isBattleFinished()
	{
		return _state == GameState.TELE_TO_TOWN;
	}
	
	public final boolean needAnnounce()
	{
		if (_needAnnounce)
		{
			_needAnnounce = false;
			return true;
		}
		return false;
	}
	
	public final OlympiadStadiumZone getZone()
	{
		return _zone;
	}
	
	public final AbstractOlympiadGame getGame()
	{
		return _game;
	}
	
	public final void attachGame(AbstractOlympiadGame game)
	{
		if (game != null && _state != GameState.IDLE)
			return;
		
		_game = game;
		_state = GameState.BEGIN;
		_needAnnounce = false;
		
		ThreadPool.execute(this);
	}
	
	@Override
	public final void run()
	{
		try
		{
			if (_pausedByDisconnect)
			{
				ThreadPool.schedule(this, 1000L);
				return;
			}
			
			int delay = 1;
			switch (_state)
			{
				case BEGIN:
					_state = GameState.TELE_TO_ARENA;
					_countDown = Config.OLY_WAIT_TIME;
					break;
				
				case TELE_TO_ARENA:
					_game.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_WILL_ENTER_THE_OLYMPIAD_STADIUM_IN_S1_SECOND_S).addNumber(_countDown));
					
					delay = getDelay(TELEPORT_TO_ARENA);
					if (_countDown <= 0)
						_state = GameState.GAME_STARTED;
					break;
				
				case GAME_STARTED:
					if (!startGame())
					{
						_state = GameState.GAME_STOPPED;
						break;
					}
					
					_state = GameState.BATTLE_COUNTDOWN;
					_countDown = Config.OLY_WAIT_BATTLE;
					delay = getDelay(BATTLE_START_TIME);
					break;
				
				case BATTLE_COUNTDOWN:
					_zone.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.THE_GAME_WILL_START_IN_S1_SECOND_S).addNumber(_countDown));
					
					delay = getDelay(BATTLE_START_TIME);
					if (_countDown <= 0)
						_state = GameState.BATTLE_STARTED;
					
					break;
				
				case BATTLE_STARTED:
					_countDown = 0;
					
					_game.buffPlayers();
					_game.healPlayers();
					_game.resetDamage();
					
					_state = GameState.BATTLE_IN_PROGRESS;
					if (!startBattle())
						_state = GameState.GAME_STOPPED;
					
					break;
				
				case BATTLE_IN_PROGRESS:
					_countDown += 1000;
					if (checkBattle() || _countDown > Config.OLY_BATTLE)
						_state = GameState.GAME_STOPPED;
					
					break;
				
				case GAME_STOPPED:
					_state = GameState.TELE_TO_TOWN;
					_countDown = Config.OLY_WAIT_END;
					stopGame();
					delay = getDelay(TELEPORT_TO_TOWN);
					break;
				
				case TELE_TO_TOWN:
					_game.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_WILL_BE_MOVED_TO_TOWN_IN_S1_SECONDS).addNumber(_countDown));
					
					delay = getDelay(TELEPORT_TO_TOWN);
					if (_countDown <= 0)
						_state = GameState.CLEANUP;
					
					break;
				
				case CLEANUP:
					cleanupGame();
					_state = GameState.IDLE;
					_game = null;
					return;
			}
			ThreadPool.schedule(this, delay * 1000L);
		}
		catch (Exception e)
		{
			switch (_state)
			{
				case GAME_STOPPED, TELE_TO_TOWN, CLEANUP, IDLE:
					LOGGER.error("Couldn't return players back in town.", e);
					_state = GameState.IDLE;
					_game = null;
					return;
			}
			
			LOGGER.error("Couldn't return players back in town.", e);
			_state = GameState.GAME_STOPPED;
			
			ThreadPool.schedule(this, 1000);
		}
	}

	public synchronized void pauseForDisconnect(Player player)
	{
		if (_game == null || !_stateBetween(GameState.GAME_STARTED, GameState.BATTLE_IN_PROGRESS))
			return;
		
		_pausedPlayers.add(player.getObjectId());
		if (!_pausedByDisconnect)
		{
			_pausedByDisconnect = true;
			_zone.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.THE_GAME_WILL_START_IN_S1_SECOND_S).addNumber(Config.OLY_WAIT_BATTLE));
		}
	}

	public synchronized void resumeForReconnect(Player player)
	{
		if (!_pausedByDisconnect || _game == null)
			return;
		
		_pausedPlayers.remove(player.getObjectId());
		if (!_pausedPlayers.isEmpty())
			return;
		
		if (_game instanceof OlympiadGameNormal normal && !normal.areParticipantsOnline())
			return;
		
		restartBattleAfterReconnect();
		ThreadPool.execute(this);
	}

	public synchronized void handleTimeoutDisconnect(Player player)
	{
		if (_game == null)
			return;
		
		_pausedPlayers.remove(player.getObjectId());
		_pausedByDisconnect = false;
		_game.handleDisconnect(player);
		_state = GameState.GAME_STOPPED;
		ThreadPool.execute(this);
	}

	private void restartBattleAfterReconnect()
	{
		if (_game == null)
			return;
		
		if (_game instanceof OlympiadGameNormal normal)
			normal.clearDisconnections();
		
		if (!_game.portPlayersToArena(_zone.getSpawns(SpawnType.NORMAL)))
		{
			_state = GameState.GAME_STOPPED;
			_pausedByDisconnect = false;
			return;
		}
		if (_game instanceof OlympiadGameNormal normalGame)
			normalGame.setParticipantsSavedLocationToGiran();
		
		_game.removals();
		_game.cleanEffects();
		_game.healPlayers();
		_game.resetDamage();
		
		_state = GameState.BATTLE_COUNTDOWN;
		_countDown = Config.OLY_WAIT_BATTLE;
		_pausedByDisconnect = false;
	}

	private boolean _stateBetween(GameState min, GameState max)
	{
		return _state.ordinal() >= min.ordinal() && _state.ordinal() <= max.ordinal();
	}
	
	private final int getDelay(int[] times)
	{
		int time;
		for (int i = 0; i < times.length - 1; i++)
		{
			time = times[i];
			if (time >= _countDown)
				continue;
			
			final int delay = _countDown - time;
			_countDown = time;
			return delay;
		}
		_countDown = -1;
		return 1;
	}
	
	/**
	 * Second stage: check for defections, port players to arena, announce game.
	 * @return true if no participants defected.
	 */
	private final boolean startGame()
	{
		if (_game.checkDefection())
			return false;
		
		if (!_game.portPlayersToArena(_zone.getSpawns(SpawnType.NORMAL)))
			return false;
		
		_game.removals();
		_needAnnounce = true;
		
		OlympiadGameManager.getInstance().startBattle();
		
		return true;
	}
	
	/**
	 * Fourth stage: last checks, start competition itself.
	 * @return true if all participants online and ready on the stadium.
	 */
	private final boolean startBattle()
	{
		if (_game.checkBattleStatus() && _game.makeCompetitionStart())
		{
			_game.broadcastOlympiadInfo(_zone);
			_zone.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.STARTS_THE_GAME));
			_zone.updateZoneStatus();
			return true;
		}
		return false;
	}
	
	/**
	 * Fifth stage: battle is running, returns true if winner found.
	 * @return
	 */
	private final boolean checkBattle()
	{
		return _game.haveWinner();
	}
	
	/**
	 * Sixth stage: winner's validations
	 */
	private final void stopGame()
	{
		_game.validateWinner(_zone);
		_zone.updateZoneStatus();
		_game.cleanEffects();
	}
	
	/**
	 * Seventh stage: game cleanup (port players back, closing doors, etc)
	 */
	private final void cleanupGame()
	{
		_game.playersStatusBack();
		_game.portPlayersBack();
		_game.clearPlayers();
	}
}
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
package ext.mods.tour.battle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.model.actor.Player;
import ext.mods.Safedisconect.SafeDisconnectManager;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.network.serverpackets.ExOlympiadMode;
import ext.mods.gameserver.network.serverpackets.ExOlympiadUserInfo;

import ext.mods.InstanceMap.InstanceManager;
import ext.mods.InstanceMap.MapInstance;
import ext.mods.tour.holder.TourHolder;
import ext.mods.tour.ranking.TournamentRankingManager;

public class BattleInstance
{
	private final int _id;
	private final List<Player> _players = new ArrayList<>();
	private final List<Player> _teamOne = new ArrayList<>();
	private final List<Player> _teamTwo = new ArrayList<>();
	private boolean _isActive;
	private ScheduledFuture<?> _battleTimeout;
	private ScheduledFuture<?> _checkPlayers;
	private final TourHolder _holder;
	private final MapInstance instance;
	public int index = 0;
	private boolean _battlePaused = false;
	private long _battleRemainingMs = 0L;
	private int _disconnectPauseCount = 0;
	private long _battleStartTime = 0L;
	
	public BattleInstance(int id, TourHolder holder, Player... players)
	{
		_id = id;
		_players.addAll(Arrays.asList(players));
		_holder = holder;
		this.instance = InstanceManager.getInstance().createInstance();
		
		splitTeams();
	}
	
	private void splitTeams()
	{
		int halfSize = _players.size() / 2;
		for (int i = 0; i < _players.size(); i++)
		{
			if (i < halfSize)
				_teamOne.add(_players.get(i));
			else
				_teamTwo.add(_players.get(i));
		}
	}
	
	public int getId()
	{
		return _id;
	}
	
	public boolean contains(Player player)
	{
		return _players.contains(player);
	}
	
	public void start()
	{
		_isActive = true;
		_battlePaused = false;
		_battleRemainingMs = 0L;
		_disconnectPauseCount = 0;
		_battleStartTime = System.currentTimeMillis();
		
		int middleX = _holder.getX();
		int middleY = _holder.getY();
		int z = _holder.getZ();
		int distance = 700;
		
		for (Player player : _teamOne)
		{
			setupPlayer(player, middleX - distance / 2, middleY, z, 32768, _teamTwo);
			index++;
		}
		
		for (Player player : _teamTwo)
		{
			setupPlayer(player, middleX + distance / 2, middleY, z, 49152, _teamOne);
			index++;
		}
		
		showOpponentStatus();
		
		_battleTimeout = ThreadPool.schedule(() -> timeoutBattle(), _holder.getDuration() * 60 * 1000L);
		if (_checkPlayers == null || _checkPlayers.isCancelled())
			_checkPlayers = ThreadPool.scheduleAtFixedRate(() -> checkPlayers(), 500, 1000);
	}

	public synchronized void pauseForDisconnect(Player player)
	{
		if (!_isActive)
			return;
		
		_disconnectPauseCount++;
		if (_disconnectPauseCount == 1)
			pauseBattleTimer();
	}

	public synchronized void resumeForReconnect(Player player)
	{
		if (!_isActive)
			return;
		
		if (_disconnectPauseCount > 0)
			_disconnectPauseCount--;
		
		if (_disconnectPauseCount == 0)
			resumeBattleTimer();
	}

	private void pauseBattleTimer()
	{
		if (_battlePaused)
			return;
		
		final long totalMs = _holder.getDuration() * 60L * 1000L;
		final long elapsed = System.currentTimeMillis() - _battleStartTime;
		_battleRemainingMs = Math.max(0L, totalMs - elapsed);
		_battlePaused = true;
		
		if (_battleTimeout != null)
		{
			_battleTimeout.cancel(true);
			_battleTimeout = null;
		}
		if (_checkPlayers != null)
		{
			_checkPlayers.cancel(true);
			_checkPlayers = null;
		}
		
		broadcastMessage("Battle paused. Waiting for reconnect...");
	}

	private void resumeBattleTimer()
	{
		if (!_battlePaused)
			return;
		
		final long remaining = Math.max(1000L, _battleRemainingMs);
		final long totalMs = _holder.getDuration() * 60L * 1000L;
		_battleStartTime = System.currentTimeMillis() - (totalMs - remaining);
		_battleTimeout = ThreadPool.schedule(() -> timeoutBattle(), remaining);
		if (_checkPlayers == null || _checkPlayers.isCancelled())
			_checkPlayers = ThreadPool.scheduleAtFixedRate(() -> checkPlayers(), 500, 1000);
		
		_battlePaused = false;
		_battleRemainingMs = 0L;
		
		broadcastMessage("Battle resumed.");
	}
	
	private void setupPlayer(Player player, int x, int y, int z, int heading, List<Player> opponents)
	{
		player.saveTournamentData();
		player.setInstanceMap(instance, true);
		player.setTournamentBattle(this);
		player.setTarget(null);
		player.setInvul(false);
		
		player.getStatus().setCpHpMp(player.getStatus().getCp(), player.getStatus().getHp(), player.getStatus().getMp());
		player.sendMessage("Get ready! The fight has begun!");
		
		if (player.getSummon() != null)
		{
			player.getSummon().setInstanceMap(instance, true);
			player.getSummon().teleportTo(x, y, z, 75);
		}
		player.teleportTo(x, y, z, 75);
		
		player.setTournamentOpponents(new ArrayList<>(opponents));
	}
	
	private void checkPlayers()
	{
		if (!_isActive || _battlePaused)
			return;
		
		for (Player player : _players)
		{
			if (player == null)
			{
				_isActive = false;
				cancelTasks();
				broadcastMessage("The battle ended in a draw due to disconnection.");
				cleanUp();
				TournamentManager.getInstance().removeBattle(this);
				return;
			}
			
			if (!player.isOnline())
			{
				if (SafeDisconnectManager.getInstance().isSafeDisconnectActive(player))
				{
					pauseForDisconnect(player);
					continue;
				}
				
				_isActive = false;
				cancelTasks();
				broadcastMessage("The battle ended in a draw due to disconnection.");
				cleanUp();
				TournamentManager.getInstance().removeBattle(this);
				return;
			}
		}
	}
	
	public void onPlayerDeath(Player dead)
	{
		if (!_isActive)
			return;
		
		if (_teamOne.contains(dead))
			_teamOne.remove(dead);
		else if (_teamTwo.contains(dead))
			_teamTwo.remove(dead);
		
		if (_teamOne.isEmpty())
			winBattle(_teamTwo);
		else if (_teamTwo.isEmpty())
			winBattle(_teamOne);
	}
	
	private void winBattle(List<Player> winners)
	{
		if (!_isActive)
			return;
		
		for (Player winner : winners)
		{
			for (IntIntHolder reward : _holder.getRewards())
			{
				if (reward.getId() > 0 && reward.getValue() > 0)
					winner.addItem(reward.getId(), reward.getValue(), true);
			}
			winner.sendMessage("Your team won the duel!");
			TournamentRankingManager.getInstance().addWin(winner);
		}
		
		for (Player player : _players)
		{
			if (!winners.contains(player))
			{
				TournamentRankingManager.getInstance().addLoss(player);
			}
		}
		
		broadcastMessage("The battle is over!");
		
		_isActive = false;
		cleanUp();
		cancelTasks();
		TournamentManager.getInstance().removeBattle(this);
	}
	
	public void forceEnd()
	{
		if (!_isActive)
			return;
		
		_isActive = false;
		cancelTasks();
		broadcastMessage("The battle is over.");
		
		for (Player player : _players)
		{
			TournamentRankingManager.getInstance().addDraw(player);
		}
		
		cleanUp();
		TournamentManager.getInstance().removeBattle(this);
	}
	
	private void timeoutBattle()
	{
		if (!_isActive || _battlePaused)
			return;
		
		_isActive = false;
		cancelTasks();
		broadcastMessage("Time's up! The battle ended in a draw.");
		
		for (Player player : _players)
		{
			TournamentRankingManager.getInstance().addDraw(player);
		}
		
		cleanUp();
		TournamentManager.getInstance().removeBattle(this);
	}
	
	private void cancelTasks()
	{
		if (_battleTimeout != null)
		{
			_battleTimeout.cancel(false);
			_battleTimeout = null;
		}
		
		if (_checkPlayers != null)
		{
			_checkPlayers.cancel(false);
			_checkPlayers = null;
		}
	}
	
	private void cleanUp()
	{
		_battlePaused = false;
		_battleRemainingMs = 0L;
		_disconnectPauseCount = 0;
		for (Player player : _players)
		{
			if (player != null)
			{
				player.restoreTournamentData();
				player.setOlympiadSide(-1);
				player.setOlympiadGameId(-1);
				player.sendPacket(new ExOlympiadMode(0));
				
				player.setInstanceMap(InstanceManager.getInstance().getInstance(0), true);
				
			}
		}
		
		InstanceManager.getInstance().deleteInstance(instance.getId());
	}
	
	private void showOpponentStatus()
	{
		int side = 1;
		for (Player player : _players)
		{
			player.setOlympiadSide(side);
			player.sendPacket(new ExOlympiadMode(side));
			player.getStatus().broadcastStatusUpdate();
			for (Player opponent : player.getTournamentOpponents())
			{
				player.sendPacket(new ExOlympiadUserInfo(opponent));
				opponent.getStatus().broadcastStatusUpdate();
			}
			side = (side == 1) ? 2 : 1;
		}
	}
	
	private void broadcastMessage(String text)
	{
		for (Player player : _players)
		{
			if (player != null)
				player.sendMessage(text);
		}
	}
}

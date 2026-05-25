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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import ext.mods.commons.pool.ConnectionPool;
import ext.mods.commons.random.Rnd;

import ext.mods.Config;
import ext.mods.gameserver.custom.data.OlympiadEnchantData;
import ext.mods.protection.hwid.hwidManager;
import ext.mods.gameserver.enums.OlympiadType;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.zone.type.OlympiadStadiumZone;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ExOlympiadUserInfo;
import ext.mods.gameserver.network.serverpackets.L2GameServerPacket;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public abstract class OlympiadGameNormal extends AbstractOlympiadGame
{
	private static final String INSERT_RESULT = "INSERT INTO olympiad_fights (charOneId, charTwoId, charOneClass, charTwoClass, winner, start, time, classed) values(?,?,?,?,?,?,?,?)";
	
	protected int _damageP1 = 0;
	protected int _damageP2 = 0;
	
	protected Participant _playerOne;
	protected Participant _playerTwo;
	
	protected OlympiadGameNormal(int id, Participant[] opponents)
	{
		super(id);
		
		_playerOne = opponents[0];
		_playerTwo = opponents[1];
		
		_playerOne.getPlayer().setOlympiadGameId(id);
		_playerTwo.getPlayer().setOlympiadGameId(id);
	}
	
	protected static final Participant[] createListOfParticipants(List<Integer> list)
	{
		if (list == null || list.isEmpty() || list.size() < 2)
			return null;
		
		int playerOneObjectId = 0;
		Player playerOne = null;
		Player playerTwo = null;
		final int maxAttempts = Math.max(10, list.size() * list.size());
		int attempts = 0;
		
		while (list.size() > 1 && attempts++ < maxAttempts)
		{
			playerOneObjectId = list.remove(Rnd.get(list.size()));
			playerOne = World.getInstance().getPlayer(playerOneObjectId);
			if (playerOne == null || !playerOne.isOnline())
			{
				list.add(playerOneObjectId);
				continue;
			}
			
			final int playerTwoObjectId = list.remove(Rnd.get(list.size()));
			playerTwo = World.getInstance().getPlayer(playerTwoObjectId);
			if (playerTwo == null || !playerTwo.isOnline())
			{
				list.add(playerOneObjectId);
				list.add(playerTwoObjectId);
				continue;
			}
			
			if (Config.OLY_BLOCK_SAME_HWID && hwidManager.hasSameHwid(playerOne, playerTwo))
			{
				list.add(playerOneObjectId);
				list.add(playerTwoObjectId);
				continue;
			}
			
			Participant[] result = new Participant[2];
			result[0] = new Participant(playerOne, 1);
			result[1] = new Participant(playerTwo, 2);
			
			return result;
		}
		return null;
	}
	
	@Override
	public final boolean containsParticipant(int objectId)
	{
		return _playerOne.getObjectId() == objectId || _playerTwo.getObjectId() == objectId;
	}
	
	@Override
	public final void sendOlympiadInfo(Creature player)
	{
		player.sendPacket(new ExOlympiadUserInfo(_playerOne.getPlayer()));
		_playerOne.getPlayer().updateEffectIcons();
		player.sendPacket(new ExOlympiadUserInfo(_playerTwo.getPlayer()));
		_playerTwo.getPlayer().updateEffectIcons();
	}
	
	@Override
	public final void broadcastOlympiadInfo(OlympiadStadiumZone stadium)
	{
		stadium.broadcastPacket(new ExOlympiadUserInfo(_playerOne.getPlayer()));
		_playerOne.getPlayer().updateEffectIcons();
		stadium.broadcastPacket(new ExOlympiadUserInfo(_playerTwo.getPlayer()));
		_playerTwo.getPlayer().updateEffectIcons();
	}
	
	@Override
	protected final void broadcastPacket(L2GameServerPacket packet)
	{
		_playerOne.updatePlayer();
		if (_playerOne.getPlayer() != null)
			_playerOne.getPlayer().sendPacket(packet);
		
		_playerTwo.updatePlayer();
		if (_playerTwo.getPlayer() != null)
			_playerTwo.getPlayer().sendPacket(packet);
	}
	
	@Override
	protected final boolean portPlayersToArena(List<Location> spawns)
	{
		boolean result = true;
		result &= portPlayerToArena(_playerOne, spawns.get(0), _stadiumId);
		result &= portPlayerToArena(_playerTwo, spawns.get(1), _stadiumId);
		
		return result;
	}
	
	@Override
	protected final void removals()
	{
		if (_aborted)
			return;
		
		removals(_playerOne.getPlayer(), true);
		removals(_playerTwo.getPlayer(), true);
	}
	
	@Override
	protected final void buffPlayers()
	{
		if (_aborted)
			return;
		
		buffPlayer(_playerOne.getPlayer());
		buffPlayer(_playerTwo.getPlayer());
	}
	
	@Override
	protected final void healPlayers()
	{
		if (_aborted)
			return;
		
		_playerOne.getPlayer().getStatus().setMaxCpHpMp();
		_playerTwo.getPlayer().getStatus().setMaxCpHpMp();
	}
	
	@Override
	protected final boolean makeCompetitionStart()
	{
		if (!super.makeCompetitionStart())
			return false;
		
		if (_playerOne.getPlayer() == null || _playerTwo.getPlayer() == null)
			return false;
		
		_playerOne.getPlayer().setOlympiadStart(true);
		_playerTwo.getPlayer().setOlympiadStart(true);
		OlympiadEnchantData.getInstance().applyOlympiadEnchant(_playerOne.getPlayer());
		OlympiadEnchantData.getInstance().applyOlympiadEnchant(_playerTwo.getPlayer());
		return true;
	}
	
	@Override
	protected final void cleanEffects()
	{
		if (_playerOne.getPlayer() != null && !_playerOne.isDefecting() && !_playerOne.isDisconnected() && _playerOne.getPlayer().getOlympiadGameId() == _stadiumId)
			cleanEffects(_playerOne.getPlayer());
		
		if (_playerTwo.getPlayer() != null && !_playerTwo.isDefecting() && !_playerTwo.isDisconnected() && _playerTwo.getPlayer().getOlympiadGameId() == _stadiumId)
			cleanEffects(_playerTwo.getPlayer());
	}
	
	@Override
	protected final void portPlayersBack()
	{
		_playerOne.updatePlayer();
		_playerTwo.updatePlayer();
		final boolean safeDisconnectUsed = _playerOne.isDisconnected() || _playerTwo.isDisconnected();
		if (safeDisconnectUsed)
		{
			if (_playerOne.getPlayer() != null && !_playerOne.isDefecting())
				AbstractOlympiadGame.portPlayerToGiranPersistent(_playerOne.getPlayer());
			if (_playerTwo.getPlayer() != null && !_playerTwo.isDefecting())
				AbstractOlympiadGame.portPlayerToGiranPersistent(_playerTwo.getPlayer());
		}
		else
		{
			if (_playerOne.getPlayer() != null && !_playerOne.isDefecting())
				portPlayerBack(_playerOne.getPlayer());
			if (_playerTwo.getPlayer() != null && !_playerTwo.isDefecting())
				portPlayerBack(_playerTwo.getPlayer());
		}
	}
	
	@Override
	protected final void playersStatusBack()
	{
		if (_playerOne.getPlayer() != null && !_playerOne.isDefecting() && !_playerOne.isDisconnected() && _playerOne.getPlayer().getOlympiadGameId() == _stadiumId)
			playerStatusBack(_playerOne.getPlayer());
		
		if (_playerTwo.getPlayer() != null && !_playerTwo.isDefecting() && !_playerTwo.isDisconnected() && _playerTwo.getPlayer().getOlympiadGameId() == _stadiumId)
			playerStatusBack(_playerTwo.getPlayer());
		
		OlympiadEnchantData.getInstance().restoreEnchant(_playerTwo.getPlayer());
		OlympiadEnchantData.getInstance().restoreEnchant(_playerOne.getPlayer());
		
	}
	
	@Override
	protected final void clearPlayers()
	{
		if (_playerOne != null)
			OlympiadEnchantData.getInstance().restoreEnchant(_playerOne.getPlayer());
		
		if (_playerTwo != null)
			OlympiadEnchantData.getInstance().restoreEnchant(_playerTwo.getPlayer());
		
		_playerOne.setPlayer(null);
		_playerOne = null;
		
		_playerTwo.setPlayer(null);
		_playerTwo = null;
	}
	
	@Override
	protected final void handleDisconnect(Player player)
	{
		if (player.getObjectId() == _playerOne.getObjectId())
			_playerOne.setDisconnection(true);
		else if (player.getObjectId() == _playerTwo.getObjectId())
			_playerTwo.setDisconnection(true);
		
		OlympiadEnchantData.getInstance().restoreEnchant(player);
	}

	public void clearDisconnections()
	{
		_playerOne.setDisconnection(false);
		_playerTwo.setDisconnection(false);
	}
	
	/**
	 * Define a saved location de ambos os participantes para Giran.
	 * Usado no restart apos Safe Disconnect para que, ao fim da luta, retornem a Giran e nao a arena.
	 */
	public void setParticipantsSavedLocationToGiran()
	{
		_playerOne.updatePlayer();
		_playerTwo.updatePlayer();
		if (_playerOne.getPlayer() != null)
			AbstractOlympiadGame.setSavedLocationToGiran(_playerOne.getPlayer());
		if (_playerTwo.getPlayer() != null)
			AbstractOlympiadGame.setSavedLocationToGiran(_playerTwo.getPlayer());
	}

	public boolean areParticipantsOnline()
	{
		_playerOne.updatePlayer();
		_playerTwo.updatePlayer();
		
		final Player p1 = _playerOne.getPlayer();
		final Player p2 = _playerTwo.getPlayer();
		return p1 != null && p1.isOnline() && p2 != null && p2.isOnline();
	}
	
	@Override
	protected final boolean checkBattleStatus()
	{
		if (_aborted)
			return false;
		
		if (_playerOne.getPlayer() == null || _playerOne.isDisconnected())
			return false;
		
		if (_playerTwo.getPlayer() == null || _playerTwo.isDisconnected())
			return false;
		
		return true;
	}
	
	@Override
	protected final boolean haveWinner()
	{
		if (!checkBattleStatus())
			return true;
		
		boolean playerOneLost = true;
		try
		{
			if (_playerOne.getPlayer().getOlympiadGameId() == _stadiumId)
				playerOneLost = _playerOne.getPlayer().isDead();
		}
		catch (Exception e)
		{
			playerOneLost = true;
		}
		
		boolean playerTwoLost = true;
		try
		{
			if (_playerTwo.getPlayer().getOlympiadGameId() == _stadiumId)
				playerTwoLost = _playerTwo.getPlayer().isDead();
		}
		catch (Exception e)
		{
			playerTwoLost = true;
		}
		
		return playerOneLost || playerTwoLost;
	}
	
	@Override
	protected void validateWinner(OlympiadStadiumZone stadium)
	{
		if (_aborted)
			return;
		
		final int playerOnePoints = _playerOne.getStatSet().getInteger(POINTS);
		final int playerTwoPoints = _playerTwo.getStatSet().getInteger(POINTS);
		
		if (_playerOne.isDefecting() || _playerTwo.isDefecting())
		{
			if (_playerOne.isDefecting())
				removePointsFromParticipant(_playerOne, Math.min(playerOnePoints / 3, Config.OLY_MAX_POINTS));
			
			if (_playerTwo.isDefecting())
				removePointsFromParticipant(_playerTwo, Math.min(playerTwoPoints / 3, Config.OLY_MAX_POINTS));
			
			return;
		}
		
		final boolean pOneCrash = (_playerOne.getPlayer() == null || _playerOne.isDisconnected());
		final boolean pTwoCrash = (_playerTwo.getPlayer() == null || _playerTwo.isDisconnected());
		
		final long fightTime = (System.currentTimeMillis() - _startTime);
		final int pointDiff = Math.clamp(Math.min(playerOnePoints, playerTwoPoints) / getDivider(), 1, Config.OLY_MAX_POINTS);
		
		if (pOneCrash || pTwoCrash)
		{
			if (pTwoCrash && !pOneCrash)
			{
				stadium.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_WON_THE_GAME).addString(_playerOne.getName()));
				
				_playerOne.updateStat(COMP_WON, 1);
				addPointsToParticipant(_playerOne, pointDiff);
				
				_playerTwo.updateStat(COMP_LOST, 1);
				removePointsFromParticipant(_playerTwo, pointDiff);
				
				rewardParticipant(_playerOne.getPlayer(), getReward());
			}
			else if (pOneCrash && !pTwoCrash)
			{
				stadium.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_WON_THE_GAME).addString(_playerTwo.getName()));
				
				_playerTwo.updateStat(COMP_WON, 1);
				addPointsToParticipant(_playerTwo, pointDiff);
				
				_playerOne.updateStat(COMP_LOST, 1);
				removePointsFromParticipant(_playerOne, pointDiff);
				
				rewardParticipant(_playerTwo.getPlayer(), getReward());
			}
			else if (pOneCrash && pTwoCrash)
			{
				stadium.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.THE_GAME_ENDED_IN_A_TIE));
				
				_playerOne.updateStat(COMP_LOST, 1);
				removePointsFromParticipant(_playerOne, pointDiff);
				
				_playerTwo.updateStat(COMP_LOST, 1);
				removePointsFromParticipant(_playerTwo, pointDiff);
			}
			
			_playerOne.updateStat(COMP_DRAWN, 1);
			_playerTwo.updateStat(COMP_DRAWN, 1);
			
			return;
		}
		
		double playerOneHp = 0;
		if (_playerOne.getPlayer() != null && !_playerOne.getPlayer().isDead())
		{
			playerOneHp = _playerOne.getPlayer().getStatus().getHp() + _playerOne.getPlayer().getStatus().getCp();
			if (playerOneHp < 0.5)
				playerOneHp = 0;
		}
		
		double playerTwoHp = 0;
		if (_playerTwo.getPlayer() != null && !_playerTwo.getPlayer().isDead())
		{
			playerTwoHp = _playerTwo.getPlayer().getStatus().getHp() + _playerTwo.getPlayer().getStatus().getCp();
			if (playerTwoHp < 0.5)
				playerTwoHp = 0;
		}
		
		_playerOne.updatePlayer();
		_playerTwo.updatePlayer();
		
		if ((_playerOne.getPlayer() == null || !_playerOne.getPlayer().isOnline()) && (_playerTwo.getPlayer() == null || !_playerTwo.getPlayer().isOnline()))
		{
			_playerOne.updateStat(COMP_WON, 1);
			_playerTwo.updateStat(COMP_LOST, 1);
			stadium.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.THE_GAME_ENDED_IN_A_TIE));
		}
		else if (_playerTwo.getPlayer() == null || !_playerTwo.getPlayer().isOnline() || (playerTwoHp == 0 && playerOneHp != 0) || (_damageP1 > _damageP2 && playerTwoHp != 0 && playerOneHp != 0))
		{
			stadium.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_WON_THE_GAME).addString(_playerOne.getName()));
			
			_playerTwo.updateStat(COMP_WON, 1);
			_playerOne.updateStat(COMP_LOST, 1);
			
			addPointsToParticipant(_playerOne, pointDiff);
			removePointsFromParticipant(_playerTwo, pointDiff);
			
			saveResults(_playerOne, _playerTwo, 1, _startTime, fightTime, getType());
			rewardParticipant(_playerOne.getPlayer(), getReward());
		}
		else if (_playerOne.getPlayer() == null || !_playerOne.getPlayer().isOnline() || (playerOneHp == 0 && playerTwoHp != 0) || (_damageP2 > _damageP1 && playerOneHp != 0 && playerTwoHp != 0))
		{
			stadium.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_WON_THE_GAME).addString(_playerTwo.getName()));
			
			_playerOne.updateStat(COMP_DONE, 1);
			_playerTwo.updateStat(COMP_DONE, 1);
			
			addPointsToParticipant(_playerTwo, pointDiff);
			removePointsFromParticipant(_playerOne, pointDiff);
			
			saveResults(_playerOne, _playerTwo, 2, _startTime, fightTime, getType());
			rewardParticipant(_playerTwo.getPlayer(), getReward());
		}
		else
		{
			saveResults(_playerOne, _playerTwo, 0, _startTime, fightTime, getType());
			
			stadium.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.THE_GAME_ENDED_IN_A_TIE));
			
			removePointsFromParticipant(_playerOne, Math.min(playerOnePoints / getDivider(), Config.OLY_MAX_POINTS));
			removePointsFromParticipant(_playerTwo, Math.min(playerTwoPoints / getDivider(), Config.OLY_MAX_POINTS));
		}
		
		_playerOne.updateStat(COMP_DONE, 1);
		_playerTwo.updateStat(COMP_DONE, 1);
	}
	
	@Override
	protected final void addDamage(Player player, int damage)
	{
		if (_playerOne.getPlayer() == null || _playerTwo.getPlayer() == null)
			return;
		
		if (player == _playerOne.getPlayer())
			_damageP1 += damage;
		else if (player == _playerTwo.getPlayer())
			_damageP2 += damage;
	}
	
	@Override
	public final String[] getPlayerNames()
	{
		return new String[]
		{
			_playerOne.getName(),
			_playerTwo.getName()
		};
	}
	
	@Override
	public boolean checkDefection()
	{
		_playerOne.updatePlayer();
		_playerTwo.updatePlayer();
		
		SystemMessage sm = checkDefection(_playerOne.getPlayer());
		if (sm != null)
		{
			_playerOne.setDefection(true);
			if (_playerTwo.getPlayer() != null)
				_playerTwo.getPlayer().sendPacket(sm);
		}
		
		sm = checkDefection(_playerTwo.getPlayer());
		if (sm != null)
		{
			_playerTwo.setDefection(true);
			if (_playerOne.getPlayer() != null)
				_playerOne.getPlayer().sendPacket(sm);
		}
		
		return _playerOne.isDefecting() || _playerTwo.isDefecting();
	}
	
	@Override
	public final void resetDamage()
	{
		_damageP1 = 0;
		_damageP2 = 0;
	}
	
	protected static final void saveResults(Participant one, Participant two, int winner, long startTime, long fightTime, OlympiadType type)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(INSERT_RESULT))
		{
			ps.setInt(1, one.getObjectId());
			ps.setInt(2, two.getObjectId());
			ps.setInt(3, one.getBaseClass());
			ps.setInt(4, two.getBaseClass());
			ps.setInt(5, winner);
			ps.setLong(6, startTime);
			ps.setLong(7, fightTime);
			ps.setInt(8, (type == OlympiadType.CLASSED ? 1 : 0));
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't save Olympiad results.", e);
		}
	}
}
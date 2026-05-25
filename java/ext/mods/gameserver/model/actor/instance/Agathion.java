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
package ext.mods.gameserver.model.actor.instance;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import ext.mods.aghation.holder.AgathionHolder;
import ext.mods.Crypta.AgathionData;
import ext.mods.commons.pool.ThreadPool;
import ext.mods.commons.random.Rnd;
import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.enums.StatusType;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.MagicSkillUse;
import ext.mods.gameserver.network.serverpackets.StatusUpdate;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.geoengine.GeoEngine;

public class Agathion extends Folk
{
	
	private ScheduledFuture<?> _task;
	
	private long lastCheckPostionTime = 0;
	
	private long lastRandomAnimationTime = 0;
	
	private long lastHealTime = 0;
	private int _itemId;
	private Player _player;
	private int _lastPlayerInstanceId = 0;
	
	public Agathion(int objectId, NpcTemplate template, Player player, int itemId)
	{
		
		super(objectId, template);
		
		_player = player;
		_itemId = itemId;
	}
	
	@Override
	public void onInteract(Player player)
	{
		return;
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		if (!super.doDie(killer))
		{
			return false;
		}
		return true;
	}
	
	@Override
	public void onSpawn()
	{
		setInvul(true);
		start();
		if (_player != null)
		{
			_lastPlayerInstanceId = _player.getInstanceMap().getId();
			
			final int safeOffset = 20;
			final int angleDeg = (int) ((Math.random() * 360));
			final double rad = Math.toRadians(angleDeg);
			final int dx = (int) (Math.cos(rad) * safeOffset);
			final int dy = (int) (Math.sin(rad) * safeOffset);
			final Location loc = new Location(_player.getX() + dx, _player.getY() + dy, _player.getZ());
			teleportTo(loc, 0);
			
			getAI().setFollowStatus(true);
			thinking();
		}
	}
	
	@Override
	public void deleteMe()
	{
		stop();
		decayMe();
		
	}
	
	public Player getPlayer()
	{
		return _player;
	}
	
	/**
	 * Verifica se o player mudou de instância e move o Agathion para a nova instância
	 */
	private void checkInstanceChange()
	{
		if (_player == null)
			return;
			
		int currentPlayerInstanceId = _player.getInstanceMap().getId();
		
		if (currentPlayerInstanceId != _lastPlayerInstanceId)
		{
			setInstanceMap(_player.getInstanceMap(), false);
			
			final int safeOffset = 30;
			final int angleDeg = (int) ((Math.random() * 360));
			final double rad = Math.toRadians(angleDeg);
			final int dx = (int) (Math.cos(rad) * safeOffset);
			final int dy = (int) (Math.sin(rad) * safeOffset);
			final Location newLoc = new Location(_player.getX() + dx, _player.getY() + dy, _player.getZ());
			teleportTo(newLoc, 0);
			
			_lastPlayerInstanceId = currentPlayerInstanceId;
			
			getAI().setFollowStatus(true);
		}
	}
	
	/**
	 * Força o Agathion a seguir o player quando ele teleporta
	 * Este método pode ser chamado externamente quando necessário
	 */
	public void forceFollowPlayer()
	{
		if (_player == null)
			return;
			
		if (getInstanceMap().getId() != _player.getInstanceMap().getId())
		{
			setInstanceMap(_player.getInstanceMap(), false);
			_lastPlayerInstanceId = _player.getInstanceMap().getId();
		}
		
		final int safeOffset = 25;
		final int angleDeg = (int) ((Math.random() * 360));
		final double rad = Math.toRadians(angleDeg);
		final int dx = (int) (Math.cos(rad) * safeOffset);
		final int dy = (int) (Math.sin(rad) * safeOffset);
		final Location newLoc = new Location(_player.getX() + dx, _player.getY() + dy, _player.getZ());
		teleportTo(newLoc, 0);
		
		getAI().setFollowStatus(true);
	}
	
	private void thinking()
	{
		if (_task != null)
		{
			
			Player onlinePlayer = World.getInstance().getPlayer(_player.getObjectId());
			
			if (onlinePlayer != null)
			{
				if (!onlinePlayer.isOnline())
				{
					if (onlinePlayer.getCurrentAgation() != null)
					{
						
						onlinePlayer.deletedAgation(onlinePlayer.getCurrentAgation());
						onlinePlayer.setCurrentAgation(null);
						deleteMe();
					}
				}
				_player = onlinePlayer;
				
				checkInstanceChange();
			}
			else
			{
				
				deleteMe();
			}
			
			moviment();
			animationRandom();
			heal();
		}
	}
	
	private void heal()
	{
		Object agathionDataInstance = AgathionData.getInstance();
		if (agathionDataInstance == null)
		{
			_player.sendMessage("AgathionData não está disponível.");
			return;
		}
		
		List<AgathionHolder> agationList = null;
		try
		{
			@SuppressWarnings("unchecked")
			List<AgathionHolder> tempList = (List<AgathionHolder>) AgathionData.getInstance().getAgathionsByItemId(_itemId);
			agationList = tempList;
		}
		catch (Exception e)
		{
			_player.sendMessage("Erro ao acessar dados do Agathion.");
			e.printStackTrace();
			return;
		}
		
		if (agationList == null || agationList.isEmpty())
		{
			_player.sendMessage("No Agathion is associated with this item.");
			return;
		}
		
		for (AgathionHolder agation : agationList)
		{
			long currentTime = System.currentTimeMillis();
			long healCooldown = TimeUnit.SECONDS.toMillis(agation.getHealDelay());
			
			if (currentTime - lastHealTime >= healCooldown)
			{
				int heal = agation.getHealAmount();
				
				if (!_player.isInOlympiadMode() || !_player.isAlikeDead() || !_player.isDead())
				{
					if (_player.getStatus().getCp() < _player.getStatus().getMaxCp())
					{
						_player.getStatus().setCp(Math.min(_player.getStatus().getMaxCp(), heal + _player.getStatus().getCp()));
						StatusUpdate su = new StatusUpdate(_player);
						su.addAttribute(StatusType.CUR_CP, _player.getStatus().getMaxCp());
						_player.sendPacket(su);
						_player.broadcastPacket(new MagicSkillUse(_player, _player, 2005, 1, 5, 0));
						_player.sendMessage(heal + " CP Restored");
					}
					
					if (_player.getStatus().getHp() < _player.getStatus().getMaxHp())
					{
						_player.getStatus().setHp(Math.min(_player.getStatus().getMaxHp(), heal + _player.getStatus().getHp()));
						StatusUpdate su = new StatusUpdate(_player);
						su.addAttribute(StatusType.CUR_HP, _player.getStatus().getMaxHp());
						_player.sendPacket(su);
						_player.broadcastPacket(new MagicSkillUse(_player, _player, 2037, 1, 5, 0));
						_player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_HP_RESTORED).addNumber(heal));
					}
					
					if (_player.getStatus().getMp() < _player.getStatus().getMaxMp())
					{
						_player.getStatus().setMp(Math.min(_player.getStatus().getMaxMp(), heal + _player.getStatus().getMp()));
						StatusUpdate su = new StatusUpdate(_player);
						su.addAttribute(StatusType.CUR_MP, _player.getStatus().getMaxMp());
						_player.sendPacket(su);
						_player.broadcastPacket(new MagicSkillUse(_player, _player, 2005, 1, 5, 0));
						_player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_MP_RESTORED).addNumber(heal));
					}
				}
				lastHealTime = currentTime;
			}
		}
	}
	
	private void animationRandom()
	{
		Object agathionDataInstance = AgathionData.getInstance();
		if (agathionDataInstance == null)
		{
			return;
		}
		
		List<AgathionHolder> agationList = null;
		try
		{
			@SuppressWarnings("unchecked")
			List<AgathionHolder> tempList = (List<AgathionHolder>) AgathionData.getInstance().getAgathionsByItemId(_itemId);
			agationList = tempList;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
		
		if (agationList == null || agationList.isEmpty())
		{
			return;
		}
		
		for (AgathionHolder agation : agationList)
		{
			long currentTime = System.currentTimeMillis();
			long animCooldown = TimeUnit.SECONDS.toMillis(agation.getRandomAnimDelay());
			
			if (currentTime - lastRandomAnimationTime >= animCooldown)
			{
				if (!isMoving() || getAI().getCurrentIntention().getType() == IntentionType.IDLE)
				{
					getAI().onRandomAnimation(Rnd.get(2));
				}
				lastRandomAnimationTime = currentTime;
			}
		}
	}
	
	private void moviment()
	{
		Object agathionDataInstance = AgathionData.getInstance();
		if (agathionDataInstance == null)
		{
			return;
		}
		
		List<AgathionHolder> agationList = null;
		try
		{
			@SuppressWarnings("unchecked")
			List<AgathionHolder> tempList = (List<AgathionHolder>) AgathionData.getInstance().getAgathionsByItemId(_itemId);
			agationList = tempList;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
		
		if (agationList == null || agationList.isEmpty())
		{
			return;
		}
		
		for (AgathionHolder agation : agationList)
		{
			long currentTime = System.currentTimeMillis();
			long followCooldown = TimeUnit.SECONDS.toMillis(agation.getFollowCheckDelay());
			
			if (currentTime - lastCheckPostionTime >= followCooldown)
			{
					if (_player != null)
				{
						if (distance2D(_player) > 1500 || _player.isTeleporting())
					{
						final int safeOffset = 45;
						final int angleDeg = (int) ((Math.random() * 360));
						final double rad = Math.toRadians(angleDeg);
						final int dx = (int) (Math.cos(rad) * safeOffset);
						final int dy = (int) (Math.sin(rad) * safeOffset);
							final Location ownerPos = _player.getPosition().clone();
						ownerPos.setX(ownerPos.getX() + dx);
						ownerPos.setY(ownerPos.getY() + dy);
							teleportTo(ownerPos, 0);
						return;
					}
					final int followDistance = 20;
					if (!isIn2DRadius(_player, followDistance))
					{
						final Location playerLoc = _player.getPosition();
						final int dx = playerLoc.getX() - getX();
						final int dy = playerLoc.getY() - getY();
						final double distance = Math.sqrt(dx * dx + dy * dy);
						
						if (distance > 0)
						{
							final double ratio = (distance - followDistance) / distance;
							final int targetX = getX() + (int) (dx * ratio);
							final int targetY = getY() + (int) (dy * ratio);
							final int targetZ = playerLoc.getZ();
							
						if (GeoEngine.getInstance().canMoveToTarget(getX(), getY(), getZ(), targetX, targetY, targetZ))
							{
								final Location targetLoc = new Location(targetX, targetY, targetZ);
								getMove().maybeMoveToLocation(targetLoc, 0, true, false);
							}
							else
							{
								teleportTo(playerLoc, followDistance);
							}
						}
					}
					
					if (_player.isTeleporting())
					{
						Location loc = new Location(_player.getX(), _player.getY(), _player.getZ());
						teleportTo(loc, 45);
					}
				}
				lastCheckPostionTime = currentTime;
			}
		}
	}

	@Override
	public boolean isInMyTerritory() {
		return true;
	}
	
	public void start()
	{
		if (_task == null)
		{
			_task = ThreadPool.scheduleAtFixedRate(this::thinking, 500, 500);
		}
	}
	
	public void stop()
	{
		if (_task != null)
		{
			_task.cancel(false);
			_task = null;
			
		}
	}
	
	public boolean running()
	{
		return _task != null;
	}
}

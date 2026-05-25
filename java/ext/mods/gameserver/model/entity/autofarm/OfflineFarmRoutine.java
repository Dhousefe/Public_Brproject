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
package ext.mods.gameserver.model.entity.autofarm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import ext.mods.commons.pool.ThreadPool;
import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.enums.skills.SkillType;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Monster;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.skills.AbstractEffect;
import ext.mods.gameserver.skills.L2Skill;

/**
 * Offline Farm Routine - handles individual player's offline farming
 * Sistema completo com salvamento de buffs e renascimento automático
 */
public class OfflineFarmRoutine
{
	/**
	 * Classe para armazenar informações de buff salvo
	 */
	private static class SavedBuff
	{
		private final int _skillId;
		private final int _skillLevel;
		private final int _remainingTime;
		
		public SavedBuff(int skillId, int skillLevel, int remainingTime)
		{
			_skillId = skillId;
			_skillLevel = skillLevel;
			_remainingTime = remainingTime;
		}
		
		public int getSkillId()
		{
			return _skillId;
		}
		
		public int getSkillLevel()
		{
			return _skillLevel;
		}
		
		public int getRemainingTime()
		{
			return _remainingTime;
		}
	}
	
	private final Player _player;
	private ScheduledFuture<?> _routineTask;
	private boolean _isRunning;
	
	private List<SavedBuff> _savedBuffs = new ArrayList<>();
	
	private Location _deathLocation = null;
	private boolean _wasDead = false;
	
	private long _targetLastHitTime = 0;
	private long _targetLockedAt = 0;
	private Monster _lockedTarget = null;
	private static final int TARGET_STUCK_TIMEOUT_MS = 15000;
	private static final int TARGET_MAX_LOCK_TIME_MS = 30000;
	
	public OfflineFarmRoutine(Player player)
	{
		_player = player;
		_isRunning = false;
	}
	
	public void start()
	{
		if (_isRunning)
			return;
		
		_isRunning = true;
		_deathLocation = null;
		_wasDead = false;
		
		saveBuffs();
		
		_routineTask = ThreadPool.scheduleAtFixedRate(() ->
		{
			if (_player == null || !_player.isOfflineFarm() || !_player.isOnline())
			{
				stop();
				return;
			}
			
			if (_player.isDead())
			{
				if (_deathLocation == null)
				{
					saveDeathLocation();
				}
				
				respawnAtDeathLocation();
				return;
			}
			
			if (_wasDead && !_player.isDead())
			{
				restoreBuffs();
				_wasDead = false;
				_deathLocation = null;
			}
		}, 1000, 1000);
	}
	
	public void stop()
	{
		_isRunning = false;
		_deathLocation = null;
		_wasDead = false;
		_savedBuffs.clear();
		
		if (_routineTask != null)
		{
			_routineTask.cancel(false);
			_routineTask = null;
		}
	}
	
	/**
	 * Salva todos os buffs ativos do player quando inicia o offline farm
	 */
	private void saveBuffs()
	{
		if (_player == null || _player.isDead())
			return;
		
		_savedBuffs.clear();
		
		for (AbstractEffect effect : _player.getAllEffects())
		{
			if (effect == null)
				continue;
			
			final L2Skill skill = effect.getSkill();
			if (skill == null)
				continue;
			
			if (effect.isHerbEffect() || skill.isToggle() || 
				skill.getSkillType() == SkillType.CONT ||
				effect.getEffectType() == ext.mods.gameserver.enums.skills.EffectType.HEAL_OVER_TIME)
				continue;
			
			final int remainingTime = effect.getPeriod() - effect.getTime();
			if (remainingTime <= 0)
				continue;
			
			_savedBuffs.add(new SavedBuff(skill.getId(), skill.getLevel(), remainingTime));
		}
	}
	
	/**
	 * Restaura todos os buffs salvos no player
	 */
	private void restoreBuffs()
	{
		if (_player == null || _player.isDead() || _savedBuffs.isEmpty())
			return;
		
		for (SavedBuff savedBuff : _savedBuffs)
		{
			try
			{
				final L2Skill skill = SkillTable.getInstance().getInfo(savedBuff.getSkillId(), savedBuff.getSkillLevel());
				if (skill == null)
					continue;
				
				if (_player.getFirstEffect(skill) != null)
					continue;
				
				final List<AbstractEffect> effects = skill.getEffects(_player, _player);
				if (effects == null || effects.isEmpty())
					continue;
				
				for (AbstractEffect effect : effects)
				{
					if (effect == null)
						continue;
					
					effect.setPeriod(savedBuff.getRemainingTime());
					effect.setTime(0);
					effect.scheduleEffect();
				}
				
				_player.updateAbnormalEffect();
			}
			catch (Exception e)
			{
				System.err.println("Erro ao restaurar buff " + savedBuff.getSkillId() + " para " + _player.getName() + ": " + e.getMessage());
			}
		}
	}
	
	/**
	 * Salva a localização de morte do player
	 */
	public void saveDeathLocation()
	{
		if (_player == null || !_player.isDead())
			return;
		
		_deathLocation = _player.getPosition();
		_wasDead = true;
	}
	
	/**
	 * Renasce o player no mesmo local onde morreu e restaura buffs
	 * Método público para ser chamado externamente (ex: onPlayerDeath)
	 * Usa a mesma lógica do comando Fixed do GM
	 */
	public void respawnAndRestoreBuffs()
	{
		if (_player == null || !_player.isDead())
			return;
		
		if (_deathLocation == null)
		{
			saveDeathLocation();
		}
		
		ThreadPool.schedule(() ->
		{
			if (_player == null || !_player.isDead())
				return;
			
			respawnAtDeathLocation();
			
			ThreadPool.schedule(() ->
			{
				if (_player != null && !_player.isDead())
				{
					restoreBuffs();
					_wasDead = false;
					_deathLocation = null;
				}
			}, 500);
		}, 100);
	}
	
	/**
	 * Renasce o player no mesmo local onde morreu
	 * Usa a mesma lógica do comando Fixed do GM
	 */
	private void respawnAtDeathLocation()
	{
		if (_player == null || !_player.isDead())
			return;
		
		try
		{
			Location respawnLocation = _deathLocation != null ? _deathLocation : _player.getPosition();
			
			_player.doRevive();
			
			_player.getStatus().setHp(_player.getStatus().getMaxHp());
			_player.getStatus().setMp(_player.getStatus().getMaxMp());
			_player.getStatus().setCp(_player.getStatus().getMaxCp());
			
			_player.teleportTo(respawnLocation.getX(), respawnLocation.getY(), respawnLocation.getZ(), 20);
			
			_player.getStatus().broadcastStatusUpdate();
			_player.broadcastTitleInfo();
			_player.broadcastUserInfo();
		}
		catch (Exception e)
		{
			System.err.println("Erro ao renascer player " + _player.getName() + " no local de morte: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Verifica se o alvo está travado (sem causar dano por muito tempo)
	 */
	private boolean checkTargetStuck(Monster target)
	{
		if (target == null)
			return false;
		
		long currentTime = System.currentTimeMillis();
		
		if (_lockedTarget == null || _lockedTarget != target)
		{
			_lockedTarget = target;
			_targetLockedAt = currentTime;
			_targetLastHitTime = currentTime;
			return false;
		}
		
		if (_lockedTarget == target)
		{
			if (target.getStatus().getHp() < target.getStatus().getMaxHp() * 0.99)
			{
				_targetLastHitTime = currentTime;
			}
			
			long timeSinceLastHit = currentTime - _targetLastHitTime;
			long timeSinceLocked = currentTime - _targetLockedAt;
			
			if (timeSinceLastHit > TARGET_STUCK_TIMEOUT_MS || timeSinceLocked > TARGET_MAX_LOCK_TIME_MS)
			{
				_player.sendMessage("Target stuck detected, changing target...");
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Método público para ser chamado quando o player causa dano em um monstro
	 */
	public void onTargetHit(Monster target)
	{
		if (target != null && target == _lockedTarget)
		{
			_targetLastHitTime = System.currentTimeMillis();
		}
	}
	
	/**
	 * Verifica se deve trocar de alvo devido a travamento
	 */
	public boolean shouldChangeTarget(Monster currentTarget)
	{
		return checkTargetStuck(currentTarget);
	}
	
	/**
	 * Obtém a lista de buffs salvos (para debug)
	 */
	public int getSavedBuffsCount()
	{
		return _savedBuffs.size();
	}
}
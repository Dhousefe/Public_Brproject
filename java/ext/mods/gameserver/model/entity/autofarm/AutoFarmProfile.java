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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import ext.mods.Config;
import ext.mods.gameserver.enums.skills.SkillType;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.entity.autofarm.AutoFarmManager.AutoFarmMacro;
import ext.mods.gameserver.model.entity.autofarm.AutoFarmManager.AutoFarmType;
import ext.mods.gameserver.model.entity.autofarm.zone.AutoFarmArea;
import ext.mods.gameserver.model.entity.autofarm.zone.AutoFarmOpen;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.skills.L2Skill;

public class AutoFarmProfile
{
	private String _playerTitle;
	private int _currentSelectAreaId;
	private int _currentBuldingAreaId;
	private int _currentSkillSlot;
	private int _radius;
	private int _lastAttackRange;
	private int _macroAdditionalId;
	private int _lastClassId;
	private long _startTime;
	private long _endTime;
	private long _lastActiveTime;
	private boolean _autoPotion;
	private boolean _attackRaid;
	private boolean _attackSummon;
	private boolean _isEnabled;
	private boolean _isRunning;
	private boolean _isAddingLocation;
	private boolean _isAddingLocationLocked;
	private boolean _pickupItems;
	private boolean _openPreviewShown = false;
	
    private boolean _defensiveMode;
    private boolean _offensiveMode;

	private Set<String> _targets;
	private Map<Integer, Integer> _skills;
	private Map<Integer, AutoFarmArea> _areas = new HashMap<>();
	private final ReentrantLock _lock = new ReentrantLock();
	private Location _lastLocation;
	private Player _player;
	private AutoFarmMacro _macro;
	private AutoFarmRoutine _routine;
	private Location _anchorLocation;
	
	private long _dailyTimeUsed = 0;
	private long _sessionStartTime = 0;
	
	private boolean _useSpoilSweep = false;
	private boolean _deathReturnEnabled = true;
	
	public AutoFarmProfile(Player player)
	{
		_player = player;
	}
	
	public Player getPlayer()
	{
		return _player;
	}
	
	public boolean useSpoilSweep()
	{
		return _useSpoilSweep;
	}
	
	public void toggleSpoilSweep()
	{
		_useSpoilSweep = !_useSpoilSweep;
	}
	
	public boolean isDeathReturnEnabled()
	{
		return _deathReturnEnabled;
	}
	
	public void setDeathReturnEnabled(boolean enabled)
	{
		_deathReturnEnabled = enabled;
	}
	
	public void toggleDeathReturn()
	{
		_deathReturnEnabled = !_deathReturnEnabled;
	}
	
	public void updatePlayer(Player player)
	{
		_player = player;
	}
	
	public boolean isEnabled()
	{
		return _isEnabled;
	}
	
	public void setEnabled(boolean status)
	{
	    _isEnabled = status;
	    
	    if (_isEnabled && _player != null) 
	    {
	        _anchorLocation = _player.getPosition().clone();
	    }

	    if (!_isEnabled)
	    {
	        if (getSelectedArea() != null && getSelectedArea().getType() == AutoFarmType.ROTA)
	            getSelectedArea().getRouteZone().reset();
	    }
	    else
	        _lastActiveTime = System.currentTimeMillis();
	    
	    _startTime = status ? System.currentTimeMillis() : 0;
	}
	
	public boolean isRunning()
	{
		return _isRunning;
	}
	
	public void setRunning(boolean status)
	{
		_isRunning = status;
	}
	
	public int getBuildingAreaId()
	{
		return _currentBuldingAreaId;
	}
	
	public void setBuildingAreaId(int value)
	{
		_currentBuldingAreaId = value;
	}
	
	public int getSelectedAreaId()
	{
		return _currentSelectAreaId;
	}
	
	public void setSelectedAreaId(int id)
	{
		if (_currentSelectAreaId == id)
			return;
		
		if (_currentSelectAreaId != 0 && getAreaById(_currentSelectAreaId) != null)
			getAreaById(_currentSelectAreaId).getMonsterHistory().clear();
		
		_radius = 0;
		_currentSelectAreaId = id;
		getTargets().clear();
		_lastActiveTime = System.currentTimeMillis();
	}
	
	public AutoFarmArea getAreaById(int id)
	{
		if (id == 1 && !getAreas().containsKey(1))
		{
			final AutoFarmArea area = new AutoFarmOpen(_player.getObjectId());
			getAreas().put(area.getId(), area);
		}
		
		return getAreas().getOrDefault(id, null);
	}
	
	public AutoFarmArea getSelectedArea()
	{
		return getAreas().getOrDefault(_currentSelectAreaId, null);
	}
	
	public AutoFarmArea getBuildingArea()
	{
		return getAreas().getOrDefault(_currentBuldingAreaId, null);
	}
	
	public Map<Integer, AutoFarmArea> getAreas()
	{
		if (_areas == null)
			_areas = new HashMap<>();
		
		return _areas;
	}
	
	public void startRoutine()
	{
		if (_routine == null)
			_routine = new AutoFarmRoutine(this);
		
		if (_lock.tryLock())
		{
			try
			{
				_routine.start();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				_lock.unlock();
			}
		}
	}
	
	public boolean isAddingLocation()
	{
		return _isAddingLocation;
	}
	
	public void setAddingLocation(boolean status)
	{
		if (!status)
			_isAddingLocationLocked = false;
		
		_isAddingLocation = status;
	}
	
	public boolean isAddingLocationLocked()
	{
		return _isAddingLocationLocked;
	}
	
	public void toggleAddingLocationLock()
	{
		_isAddingLocation = !_isAddingLocationLocked;
		_isAddingLocationLocked = !_isAddingLocationLocked;
	}
	
	public int getCurrentSkillSlot()
	{
		return _currentSkillSlot;
	}
	
	public void setCurrentSkillSlot(int value)
	{
		_currentSkillSlot = value;
	}
	
	public Set<String> getTargets()
	{
		if (_targets == null)
			_targets = new HashSet<>();
		
		return _targets;
	}
	
	public Map<Integer, Integer> getSkills()
	{
		if (_skills == null)
			_skills = new HashMap<>(6);
		
		return _skills;
	}
	
	public long getStartTime()
	{
		return _startTime;
	}
	
	public long getEndTime()
	{
		return _endTime;
	}
	
	public void setEndTime(long value)
	{
		if (value == 0)
		{
			_macro = null;
			_macroAdditionalId = 0;
		}
		
		_endTime = value;
	}
	
	public long getFinalEndTime()
	{
		return _endTime + _startTime;
	}
	
	public long getLastActiveTime()
	{
		return _lastActiveTime;
	}
	
	public boolean useAutoPotion()
	{
		return _autoPotion;
	}
	
	public void toggleAutoPotion()
	{
		_autoPotion = !_autoPotion;
	}
	
	public boolean attackRaid()
	{
		return _attackRaid;
	}
	
	public void toggleAttackRaid()
	{
		_attackRaid = !_attackRaid;
	}
	
	public boolean attackSummon()
	{
		return _attackSummon;
	}
	
	public void toggleAttackSummon()
	{
		_attackSummon = !_attackSummon;
	}
	
	public boolean isPickingUpItems()
	{
		return _pickupItems;
	}

	public boolean isOpenPreviewShown()
	{
		return _openPreviewShown;
	}
	
	public void markOpenPreviewShown()
	{
		_openPreviewShown = true;
	}
	
	public void toggleItemPickup()
	{
		_pickupItems = !_pickupItems;
	}
	
    public boolean isDefensiveMode()
    {
        return _defensiveMode;
    }

    public void setDefensiveMode(boolean val)
    {
        _defensiveMode = val;
        if (val) _offensiveMode = false;
    }

    public boolean isOffensiveMode()
    {
        return _offensiveMode;
    }

    public void setOffensiveMode(boolean val)
    {
        _offensiveMode = val;
        if (val) _defensiveMode = false;
    }

	public void setMacro(AutoFarmMacro m, int additionalId)
	{
		_macro = m;
		_macroAdditionalId = additionalId;
	}
	
	public AutoFarmMacro getMacro()
	{
		return _macro;
	}
	
	public int getMacroAdditionalId()
	{
		return _macroAdditionalId;
	}
	
	public String getPlayerTitle()
	{
		return _playerTitle;
	}
	
	public void setPlayerTitle(String value)
	{
		_playerTitle = value;
	}
	
	public void updatePlayerLocation()
	{
		_lastLocation = _player.getPosition().clone();
	}
	
	public Location getLastPlayerLocation()
	{
		return _lastLocation;
	}
	
	public void checkLastClassId()
	{
		if (_lastClassId == _player.getActiveClass())
			return;
		
		if (_lastClassId != 0)
			_skills.clear();
		
		_lastClassId = _player.getActiveClass();
	}
	
	public int getAreaMaxRadius()
	{
		if (_currentSelectAreaId == 0)
			return 0;
		else if (getSelectedArea().getType() == AutoFarmType.OPEN && Config.AUTOFARM_MAX_OPEN_RADIUS > 0)
			return Config.AUTOFARM_MAX_OPEN_RADIUS;
		else
			return getAttackRange();
	}
	
	public int getFinalRadius() 
	{
		if (_radius == 0 && _currentSelectAreaId != 0 && getSelectedArea().getType() == AutoFarmType.OPEN)
		{
			if (Config.AUTOFARM_MAX_OPEN_RADIUS != 0)
				return Config.AUTOFARM_MAX_OPEN_RADIUS > 1000 ? Config.AUTOFARM_MAX_OPEN_RADIUS / 2 : Config.AUTOFARM_MAX_OPEN_RADIUS;
			
			if (getAttackRange() < 100)
				return 900;
			
			return getAttackRange();
		}
		else if (_radius == 0 || (_radius > getAttackRange() && _currentSelectAreaId != 0 && getSelectedArea().getType() != AutoFarmType.OPEN))
			return getAreaMaxRadius();
		else
			return _radius;
	}
	
	public void setRadius(int value)
	{
		_lastAttackRange = _radius;
		_radius = value;
		
		if (isPickingUpItems() && !getSelectedArea().isMovementAllowed())
			toggleItemPickup();
	}
	
	public boolean isRadiusChanged()
	{
		if (_lastAttackRange != 0 && _lastAttackRange != _radius)
		{
			_lastAttackRange = _radius;
			return true;
		}
		
		return false;
	}
	
	public int getAttackRange()
	{
		if (getAttackSkills().isEmpty())
			return _player.getStatus().getPhysicalAttackRange();
		
		return getAttackSkills().stream().mapToInt(s -> s.getCastRange() > 0 ? s.getCastRange() : s.getSkillRadius()).max().orElse(0);
	}
	
	public List<L2Skill> getAttackSkills()
	{
		return getSkills().values().stream().map(s -> _player.getSkill(s)).filter(s -> s != null && s.isSkillTypeOffensive()).collect(Collectors.toList());
	}
	
	public List<L2Skill> getAttackSkills(boolean debuff)
	{
		return getSkills().values().stream().map(s -> _player.getSkill(s)).filter(s -> s != null && s.isDebuff() == debuff && s.isSkillTypeOffensive()).collect(Collectors.toList());
	}
	
	public List<L2Skill> getBuffSkills()
	{
		return getSkills().values().stream().map(s -> _player.getSkill(s)).filter(s -> s != null && !s.isDebuff() && !s.isSkillTypeOffensive() && !s.getSkillType().name().contains("HEAL")).toList();
	}
	
	public List<L2Skill> getDebuffSkills()
	{
		return getSkills().values().stream().map(s -> _player.getSkill(s)).filter(s -> s != null && s.isDebuff()).toList();
	}
	
	public List<L2Skill> getHpHealSkills()
	{
		return getSkills().values().stream().map(s -> _player.getSkill(s)).filter(s -> s != null && (s.getSkillType() == SkillType.HEAL || s.getSkillType() == SkillType.HEAL_PERCENT || s.getSkillType() == SkillType.HEAL_STATIC)).toList();
	}
	
	public List<L2Skill> getMpHealSkills()
	{
		return getSkills().values().stream().map(s -> _player.getSkill(s)).filter(s -> s != null && (s.getSkillType() == SkillType.MANAHEAL || s.getSkillType() == SkillType.MANAHEAL_PERCENT)).toList();
	}
	
	public long getDailyTimeUsed()
	{
		return _dailyTimeUsed;
	}
	
	public void setDailyTimeUsed(long timeUsed)
	{
		_dailyTimeUsed = timeUsed;
	}
	
	public long getSessionStartTime()
	{
		return _sessionStartTime;
	}
	
	public void setSessionStartTime(long startTime)
	{
		_sessionStartTime = startTime;
	}
	
	public long getRemainingTime()
	{
		if (_player == null)
			return 0;
			
		if (_player.getPremiumService() > 0)
			return Long.MAX_VALUE;
		
		long totalAvailableTime = 90000000 + getExtraTimeAvailable();
		return Long.MAX_VALUE;
	}
	
	public boolean canUseAutoFarm()
	{
		if (_player == null)
			return false;
			
		if (_player.getPremiumService() > 0)
			return true;
		
		long remainingTime = getRemainingTime();
		return remainingTime > 1000;
	}
	
	public void addExtraTime(long extraTime)
	{
		if (_player == null)
			return;
			
		_dailyTimeUsed = Math.max(0, _dailyTimeUsed - extraTime);
		AutoFarmData.getInstance().addExtraTime(_player.getObjectId(), extraTime);
	}
	
	public long getExtraTimeAvailable()
	{
		if (_player == null)
			return 0;
			
		return AutoFarmData.getInstance().getExtraTime(_player.getObjectId());
	}
	
	public long getTotalAvailableTime()
	{
		if (_player == null)
			return 0;
			
		if (_player.getPremiumService() > 0)
			return Long.MAX_VALUE;
		
		return 90000000 + getExtraTimeAvailable();
	}
	
	public void startTimeTracking()
	{
		_sessionStartTime = System.currentTimeMillis();
	}
	
	public void stopTimeTracking()
	{
		if (_sessionStartTime > 0)
		{
			long sessionTime = System.currentTimeMillis() - _sessionStartTime;
			_dailyTimeUsed += sessionTime;
			_sessionStartTime = 0;
			
			if (_player != null && _player.isOnline())
			{
				AutoFarmData.getInstance().updatePlayerTimeUsage(_player.getObjectId(), sessionTime);
			}
		}
	}
	
	public long getCurrentSessionTime()
	{
		if (_sessionStartTime > 0)
			return System.currentTimeMillis() - _sessionStartTime;
		return 0;
	}
	
	public void checkTimeLimit()
	{
		if (_player == null)
			return;
			
		if (_player.getPremiumService() > 0)
			return;
		
		if (getRemainingTime() <= 0)
		{
		}
	}
	
	public void saveTimeUsage()
	{
		if (_player != null && _player.isOnline() && _dailyTimeUsed > 0)
		{
			AutoFarmData.getInstance().updatePlayerTimeUsage(_player.getObjectId(), _dailyTimeUsed);
			_dailyTimeUsed = 0;
		}
	}
	
	public Location getAnchorLocation()
	{
	    return _anchorLocation;
	}

    
    /**
     * Retorna o nome do tipo da área como String (Ex: "OPEN", "ZONA", "ROTA")
     * Isso evita que o Kotlin precise acessar o Enum interno do Manager.
     */
    public String getAreaTypeName()
    {
        if (getSelectedArea() == null)
            return "NONE";
        
        return getSelectedArea().getType().name();
    }

    /**
     * Retorna o nome do Macro atual como String.
     */
    public String getMacroName()
    {
        if (_macro == null)
            return "NONE";
            
        return _macro.name();
    }
}
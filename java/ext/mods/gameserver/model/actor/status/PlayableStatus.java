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
package ext.mods.gameserver.model.actor.status;

import ext.mods.extensions.listener.manager.PlayerListenerManager;
import ext.mods.gameserver.data.manager.ZoneManager;
import ext.mods.gameserver.data.xml.PlayerLevelData;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.enums.skills.Stats;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.zone.type.SwampZone;

public class PlayableStatus<T extends Playable> extends CreatureStatus<T>
{
	private long _exp = 0;
	private int _sp = 0;
	
	protected int _level = 1;
	
	public PlayableStatus(T actor)
	{
		super(actor);
	}
	
	@Override
	public int getLevel()
	{
		return _level;
	}
	
	public void setLevel(int value)
	{
		_level = value;
	}
	
	@Override
	public float getMoveSpeed()
	{
		float baseValue = getBaseMoveSpeed();
		
		if (_actor.isInsideZone(ZoneId.SWAMP))
		{
			final SwampZone zone = ZoneManager.getInstance().getZone(_actor, SwampZone.class);
			if (zone != null)
				baseValue *= (100 + zone.getMoveBonus()) / 100.0;
		}
		
		return (float) calcStat(Stats.RUN_SPEED, baseValue, null, null);
	}
	
	public long getExp()
	{
		return _exp;
	}
	
	public void setExp(long value)
	{
		_exp = value;
	}
	
	public int getSp()
	{
		return _sp;
	}
	
	public void setSp(int value)
	{
		_sp = value;
	}
	
	public boolean addExp(long value)
	{
		if ((getExp() + value) < 0)
			return true;
		
		if (getExp() + value >= PlayerLevelData.getInstance().getRequiredExpForHighestLevel())
			value = PlayerLevelData.getInstance().getRequiredExpForHighestLevel() - 1 - getExp();
		
		setExp(getExp() + value);
		
		byte level = 0;
		for (level = 1; level <= PlayerLevelData.getInstance().getMaxLevel(); level++)
		{
			if (getExp() >= getExpForLevel(level))
				continue;
			
			level--;
			break;
		}
		
		if (level != getLevel())
			addLevel((byte) (level - getLevel()));
		
		return true;
	}
	
	public boolean removeExp(long value)
	{
		if ((getExp() - value) < 0)
			value = getExp() - 1;
		
		setExp(getExp() - value);
		
		byte level = 0;
		for (level = 1; level <= PlayerLevelData.getInstance().getMaxLevel(); level++)
		{
			if (getExp() >= getExpForLevel(level))
				continue;
			
			level--;
			break;
		}
		
		if (level != getLevel())
			addLevel((byte) (level - getLevel()));
		
		return true;
	}
	
	public boolean addExpAndSp(long addToExp, int addToSp)
	{
		boolean expAdded = false;
		boolean spAdded = false;
		
		if (addToExp >= 0)
			expAdded = addExp(addToExp);
		
		if (addToSp >= 0)
			spAdded = addSp(addToSp);
		
		return expAdded || spAdded;
	}
	
	public boolean removeExpAndSp(long removeExp, int removeSp)
	{
		boolean expRemoved = false;
		boolean spRemoved = false;
		
		if (removeExp > 0)
			expRemoved = removeExp(removeExp);
		
		if (removeSp > 0)
			spRemoved = removeSp(removeSp);
		
		return expRemoved || spRemoved;
	}
	
	public boolean addLevel(byte value)
	{
		
		if (getLevel() + value > PlayerLevelData.getInstance().getRealMaxLevel())
		{
			if (getLevel() < PlayerLevelData.getInstance().getRealMaxLevel())
				value = (byte) (PlayerLevelData.getInstance().getRealMaxLevel() - getLevel());
			else
				return false;
		}
		
		boolean levelIncreased = (getLevel() + value > getLevel());
		value += getLevel();
		setLevel(value);
		
		if (getExp() >= getExpForLevel(getLevel() + 1) || getExpForLevel(getLevel()) > getExp())
			setExp(getExpForLevel(getLevel()));
		
		if (!levelIncreased)
			return false;
		
		PlayerListenerManager.getInstance().notifySetLevel((Player) _actor, value);
		PlayerListenerManager.getInstance().notifyLevelUp((Player) _actor);
		
		setMaxHpMp();
		
		return true;
	}
	
	public boolean addSp(int value)
	{
		if (value < 0)
			return false;
		
		int currentSp = getSp();
		if (currentSp == Integer.MAX_VALUE)
			return false;
		
		if (currentSp > Integer.MAX_VALUE - value)
			value = Integer.MAX_VALUE - currentSp;
		
		setSp(currentSp + value);
		return true;
	}
	
	public boolean removeSp(int value)
	{
		setSp(Math.max(0, getSp() - value));
		return true;
	}
	
	public long getExpForLevel(int level)
	{
		return 0;
	}
	
	public long getExpForThisLevel()
	{
		return 0;
	}
	
	public long getExpForNextLevel()
	{
		return 0;
	}
}
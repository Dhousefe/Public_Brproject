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
package ext.mods.gameserver.taskmanager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.skills.L2Skill;

/**
 * Controls game time, informs spawn manager about day/night spawns and players about daytime change. Informs players about their extended activity in game.
 */
public final class GameTimeTaskManager implements Runnable
{
	private static final int MINUTES_PER_DAY = 24 * 60;
	
	public static final int HOURS_PER_GAME_DAY = 4;
	public static final int MINUTES_PER_GAME_DAY = HOURS_PER_GAME_DAY * 60;
	public static final int SECONDS_PER_GAME_DAY = MINUTES_PER_GAME_DAY * 60;
	private static final int MILLISECONDS_PER_GAME_MINUTE = SECONDS_PER_GAME_DAY / (MINUTES_PER_DAY) * 1000;
	
	private static final int BREAK_TIME_MINUTES = 2 * MINUTES_PER_DAY / HOURS_PER_GAME_DAY;
	
	private List<Quest> _questEvents = Collections.emptyList();
	
	private int _time;
	protected boolean _isNight;
	private long _startTime;
	
	protected GameTimeTaskManager()
	{
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		_time = (int) (System.currentTimeMillis() - cal.getTimeInMillis()) / MILLISECONDS_PER_GAME_MINUTE;
		_isNight = isNight();
		_startTime = System.currentTimeMillis();
		
		ThreadPool.scheduleAtFixedRate(this, MILLISECONDS_PER_GAME_MINUTE, MILLISECONDS_PER_GAME_MINUTE);
	}
	
	@Override
	public final void run()
	{
		_time++;
		
		final int gameTime = getGameTime();
		for (Quest quest : _questEvents)
			quest.onGameTime(gameTime);
		
		L2Skill skill = null;
		
		if (_isNight != isNight())
		{
			_isNight = !_isNight;
			
			skill = SkillTable.getInstance().getInfo(L2Skill.SKILL_SHADOW_SENSE, 1);
		}
		
		final Collection<Player> players = World.getInstance().getPlayers();
		if (players.isEmpty())
			return;
		
		final boolean isGameBreakTime = gameTime % BREAK_TIME_MINUTES == 0;
		
		for (Player player : players)
		{
			if (!player.isOnline())
				continue;
			
			if (skill != null && player.hasSkill(L2Skill.SKILL_SHADOW_SENSE))
			{
				player.removeSkill(L2Skill.SKILL_SHADOW_SENSE, false);
				player.addSkill(skill, false);
				
				player.sendPacket(SystemMessage.getSystemMessage(_isNight ? SystemMessageId.NIGHT_S1_EFFECT_APPLIES : SystemMessageId.DAY_S1_EFFECT_DISAPPEARS).addSkillName(L2Skill.SKILL_SHADOW_SENSE));
			}
			
			if (isGameBreakTime)
				player.sendPacket(SystemMessageId.PLAYING_FOR_LONG_TIME);
		}
	}
	
	public void addQuestEvent(Quest quest)
	{
		if (_questEvents.isEmpty())
			_questEvents = new ArrayList<>(3);
		
		_questEvents.add(quest);
	}
	
	/**
	 * Returns how many game days have left since last server start.
	 * @return int : Game day.
	 */
	public final int getGameDay()
	{
		return _time / MINUTES_PER_DAY;
	}
	
	/**
	 * Returns game time in minute format (0-1439).
	 * @return int : Game time.
	 */
	public final int getGameTime()
	{
		return _time % MINUTES_PER_DAY;
	}
	
	/**
	 * Returns game hour (0-23).
	 * @return int : Game hour.
	 */
	public final int getGameHour()
	{
		return (_time % MINUTES_PER_DAY) / 60;
	}
	
	/**
	 * Returns game minute (0-59).
	 * @return int : Game minute.
	 */
	public final int getGameMinute()
	{
		return _time % 60;
	}
	
	/**
	 * Returns game time standard format (00:00-23:59).
	 * @return String : Game time.
	 */
	public final String getGameTimeFormated()
	{
		return String.format("%02d:%02d", getGameHour(), getGameMinute());
	}
	
	public void setGameTime(int hours, int minutes)
	{
		int newTime = hours * 60 + minutes;
		
		_time = newTime;
		
		_isNight = isNight();
	}
	
	/**
	 * Returns game daytime. Night is between 00:00 and 06:00.
	 * @return boolean : True, when there is night.
	 */
	public final boolean isNight()
	{
		return getGameTime() < 360;
	}
	
	/**
	 * This method is used as a monotonic clockwall.
	 * @return The elapsed time since server startup, in seconds.
	 */
	public int getCurrentTick()
	{
		return (int) ((System.currentTimeMillis() - _startTime) / 1000);
	}
	
	public static final GameTimeTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final GameTimeTaskManager INSTANCE = new GameTimeTaskManager();
	}
}
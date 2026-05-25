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
package ext.mods.gameserver.scripting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import ext.mods.commons.data.MemoSet;
import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ConnectionPool;

import ext.mods.gameserver.enums.QuestStatus;
import ext.mods.gameserver.enums.actors.MissionType;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.ExShowQuestMark;
import ext.mods.gameserver.network.serverpackets.QuestList;

/**
 * A container holding one {@link Player}'s {@link Quest} progress. It extends {@link MemoSet}.<br>
 * <br>
 * The main variables for a {@link QuestState} are :
 * <ul>
 * <li>state : the current state of the {@link Quest}, which can be CREATED, STARTED, COMPLETED ;</li>
 * <li>flags : allow to bypass one or multiple client-side {@link Quest} log(s) ;</li>
 * <li>cond : help server-side to trigger events, help client-side to show the correct {@link Quest} log.</li>
 * </ul>
 */
public final class QuestState extends MemoSet
{
	private static final long serialVersionUID = 1L;
	
	private static final CLogger LOGGER = new CLogger(QuestState.class.getName());
	
	private static final String QUEST_SET_VAR = "INSERT INTO character_quests (charId,name,var,value) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE value=VALUES(value)";
	private static final String QUEST_DEL_VAR = "DELETE FROM character_quests WHERE charId=? AND name=? AND var=?";
	private static final String QUEST_DELETE = "DELETE FROM character_quests WHERE charId=? AND name=?";
	private static final String QUEST_COMPLETE = "DELETE FROM character_quests WHERE charId=? AND name=? AND var<>'<state>'";
	
	public static final String COND = "<cond>";
	public static final String FLAGS = "<flags>";
	public static final String STATE = "<state>";
	
	private final Player _player;
	private final Quest _quest;
	
	/**
	 * Constructor of the new {@link Player}'s {@link QuestState} with {@link QuestStatus} CREATED.
	 * @param quest : The {@link Quest} associated with the {@link QuestState}.
	 * @param player : The {@link Player} carrying the {@link Quest}.
	 */
	public QuestState(Player player, Quest quest)
	{
		_player = player;
		_quest = quest;
		
		put(STATE, String.valueOf(QuestStatus.CREATED));
		
		_player.getQuestList().add(this);
	}
	
	@Override
	public boolean getBool(final String key)
	{
		return getBool(key, false);
	}
	
	@Override
	public int getInteger(final String key)
	{
		return getInteger(key, 0);
	}
	
	@Override
	public long getLong(final String key)
	{
		return getLong(key, 0);
	}
	
	@Override
	public double getDouble(final String key)
	{
		return getDouble(key, 0);
	}
	
	@Override
	protected void onSet(String key, String value)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(QUEST_SET_VAR))
		{
			ps.setInt(1, _player.getObjectId());
			ps.setString(2, _quest.getName());
			ps.setString(3, key);
			ps.setString(4, value);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't set quest {} variable {}.", e, _quest.getName(), key);
		}
	}
	
	@Override
	protected void onUnset(String key)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(QUEST_DEL_VAR))
		{
			ps.setInt(1, _player.getObjectId());
			ps.setString(2, _quest.getName());
			ps.setString(3, key);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't remove quest {} variable {}.", e, _quest.getName(), key);
		}
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof QuestState qs))
			return false;
		
		if (!_quest.isRealQuest())
			return false;
		
		if (_player != qs._player)
			return false;
		
		return _quest.getQuestId() == qs._quest.getQuestId();
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(_player.getObjectId(), _quest.getQuestId());
	}
	
	/**
	 * Load variable and value from SQL data row.
	 * @param rs : The loaded {@link ResultSet} carrying variable and value data.
	 * @throws SQLException : When value reading fails.
	 */
	public void loadFromDB(ResultSet rs) throws SQLException
	{
		final String variable = rs.getString("var");
		final String value = rs.getString("value");
		if (variable == null || variable.isEmpty() || value == null || value.isEmpty())
			return;
		
		put(variable, value);
	}
	
	/**
	 * @return The {@link Player} associated to this {@link QuestState}.
	 */
	public Player getPlayer()
	{
		return _player;
	}
	
	/**
	 * @return The {@link Quest} associated to this {@link QuestState}.
	 */
	public Quest getQuest()
	{
		return _quest;
	}
	
	/**
	 * @return The {@link QuestStatus} associated to this {@link QuestState}.
	 */
	public QuestStatus getState()
	{
		return getEnum(STATE, QuestStatus.class, null);
	}
	
	/**
	 * @return True if the {@link QuestState} is under QuestStatus.CREATED, or false otherwise.
	 */
	public boolean isCreated()
	{
		return (getState() == QuestStatus.CREATED);
	}
	
	/**
	 * @return True if the {@link QuestState} is under QuestStatus.COMPLETED, or false otherwise.
	 */
	public boolean isCompleted()
	{
		return (getState() == QuestStatus.COMPLETED);
	}
	
	/**
	 * @return True if the {@link QuestState} is under QuestStatus.STARTED, or false otherwise.
	 */
	public boolean isStarted()
	{
		return (getState() == QuestStatus.STARTED);
	}
	
	/**
	 * Set the {@link QuestStatus} of the {@link QuestState}.
	 * @param state : The desired {@link QuestStatus}.
	 */
	public void setState(QuestStatus state)
	{
		if (getState() == state)
			return;
		
		if (state == QuestStatus.COMPLETED)
			_player.getMissions().update(MissionType.QUEST_COMPLETE);
		
		set(STATE, state);
	}
	
	/**
	 * @return The condition value of the {@link Player}'s {@link Quest}.
	 */
	public int getCond()
	{
		return getInteger(COND, 0);
	}
	
	/**
	 * Set condition value of the {@link Player}'s {@link Quest}.<br>
	 * Note: Handle the quest progression. If condition skip is detected, calculate {@link Quest}'s flags.
	 * @param cond : The condition to be set.
	 */
	public void setCond(int cond)
	{
		final int previous = getCond();
		if (cond == previous)
			return;
		
		int flags = getInteger(FLAGS, 0);
		if (flags == 0)
		{
			if (previous != 0 && cond > (previous + 1))
			{
				flags = calculateFlags();
				flags |= 1 << (cond - 1);
				set(FLAGS, flags);
			}
		}
		else
		{
			if (cond > previous)
			{
				flags |= 1 << (cond - 1);
				set(FLAGS, flags);
			}
			else if (cond > 2)
			{
				int negate = (1 << cond) - 1;
				flags &= negate;
				flags |= 0x80000000;
				set(FLAGS, flags);
			}
			else
			{
				unset(FLAGS);
			}
		}
		
		set(COND, cond);
		
		if (_quest.isRealQuest())
		{
			_player.sendPacket(new QuestList(_player));
			_player.sendPacket(new ExShowQuestMark(_quest.getQuestId()));
		}
	}
	
	/**
	 * @return The {@link Quest}'s flags or condition values.
	 */
	public int getFlags()
	{
		return getInteger(FLAGS, calculateFlags());
	}
	
	/**
	 * Exit a {@link Quest}, destroying elements used by it (quest items, and progression).
	 * @param repeatable : If true, we delete progression and if false, we set the {@link QuestStatus} to QuestStatus.COMPLETED.
	 */
	public void exitQuest(boolean repeatable)
	{
		if (!isStarted())
			return;
		
		clear();
		
		if (repeatable)
			_player.getQuestList().remove(this);
		else
			setState(QuestStatus.COMPLETED);
		
		if (_quest.isRealQuest())
			_player.sendPacket(new QuestList(_player));
		
		int[] itemIds = _quest.getItemsIds();
		if (itemIds != null)
		{
			for (int itemId : itemIds)
				Quest.takeItems(_player, itemId, -1);
		}
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement((repeatable) ? QUEST_DELETE : QUEST_COMPLETE))
		{
			ps.setInt(1, _player.getObjectId());
			ps.setString(2, _quest.getName());
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't delete quest.", e);
		}
	}
	
	/**
	 * Calculate the {@link Quest}'s flags for {@link QuestList} packet.
	 * @return The calculated flag.
	 */
	private int calculateFlags()
	{
		final int cond = getCond();
		if (cond == 0)
			return 0;
		
		int flags = (1 << cond) - 1;
		flags |= 0x80000000;
		return flags;
	}
}
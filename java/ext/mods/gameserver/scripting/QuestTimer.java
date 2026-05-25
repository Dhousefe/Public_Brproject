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

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;

public class QuestTimer
{
	private final Quest _quest;
	private final String _name;
	private final Npc _npc;
	private final Player _player;
	
	private ScheduledFuture<?> _schedular;
	
	QuestTimer(Quest quest, String name, Npc npc, Player player, long initial, long period)
	{
		_quest = quest;
		_name = name;
		_npc = npc;
		_player = player;
		
		if (period > 0)
			_schedular = ThreadPool.scheduleAtFixedRate(this::runTick, initial, period);
		else
			_schedular = ThreadPool.schedule(this::runOnce, initial);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(_name, _npc, _player, _quest);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (!(obj instanceof QuestTimer other))
			return false;
		
		return Objects.equals(_name, other._name) && Objects.equals(_npc, other._npc) && Objects.equals(_player, other._player) && Objects.equals(_quest, other._quest);
	}
	
	@Override
	public final String toString()
	{
		return _name;
	}
	
	/**
	 * @return The name of the {@link QuestTimer}.
	 */
	public final String getName()
	{
		return _name;
	}
	
	/**
	 * @return The {@link Npc} of the {@link QuestTimer}.
	 */
	public final Npc getNpc()
	{
		return _npc;
	}
	
	/**
	 * @return The {@link Player} of the {@link QuestTimer}.
	 */
	public final Player getPlayer()
	{
		return _player;
	}
	
	private void runTick()
	{
		_quest.notifyTimer(_name, _npc, _player);
	}
	
	private void runOnce()
	{
		_quest.removeQuestTimer(this);
		
		_quest.notifyTimer(_name, _npc, _player);
	}
	
	/**
	 * Cancel the {@link QuestTimer}.
	 */
	public final void cancel()
	{
		if (_schedular != null)
		{
			_schedular.cancel(false);
			_schedular = null;
		}
		
		_quest.removeQuestTimer(this);
	}
}
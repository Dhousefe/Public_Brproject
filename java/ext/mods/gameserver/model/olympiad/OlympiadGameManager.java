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

import java.util.Collection;
import java.util.List;

import ext.mods.commons.logging.CLogger;

import ext.mods.gameserver.data.manager.ZoneManager;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.zone.type.OlympiadStadiumZone;
import ext.mods.gameserver.network.SystemMessageId;

public class OlympiadGameManager implements Runnable
{
	private static final CLogger LOGGER = new CLogger(OlympiadGameManager.class.getName());
	
	private volatile boolean _battleStarted = false;
	private final OlympiadGameTask[] _tasks;
	
	protected OlympiadGameManager()
	{
		final Collection<OlympiadStadiumZone> zones = ZoneManager.getInstance().getAllZones(OlympiadStadiumZone.class);
		if (zones == null || zones.isEmpty())
			throw new Error("No olympiad stadium zones defined !");
		
		_tasks = new OlympiadGameTask[zones.size()];
		int i = 0;
		for (OlympiadStadiumZone zone : zones)
			_tasks[i++] = new OlympiadGameTask(zone);
		
		LOGGER.info("Loaded {} stadiums.", _tasks.length);
	}
	
	@Override
	public final void run()
	{
		if (Olympiad.getInstance().isOlympiadEnd())
			return;
		
		if (Olympiad.getInstance().isInCompPeriod())
		{
			List<List<Integer>> readyClassed = OlympiadManager.getInstance().hasEnoughClassBasedParticipants();
			boolean readyNonClassed = OlympiadManager.getInstance().hasEnoughNonClassBasedParticipants();
			
			if (readyClassed == null && !readyNonClassed)
			{
				for (List<Integer> classList : OlympiadManager.getInstance().getClassBasedParticipants().values())
				{
					for (int objectId : classList)
					{
						final Player player = World.getInstance().getPlayer(objectId);
						if (player != null)
							player.sendPacket(SystemMessageId.GAMES_DELAYED);
					}
				}
				
				for (int objectId : OlympiadManager.getInstance().getNonClassBasedParticipants())
				{
					final Player player = World.getInstance().getPlayer(objectId);
					if (player != null)
						player.sendPacket(SystemMessageId.GAMES_DELAYED);
				}
				
				return;
			}
			
			for (int i = 0; i < _tasks.length; i++)
			{
				OlympiadGameTask task = _tasks[i];
				synchronized (task)
				{
					if (!task.isRunning())
					{
						if (readyClassed != null && (i % 2) == 0)
						{
							final AbstractOlympiadGame newGame = OlympiadGameClassed.createGame(i, readyClassed);
							if (newGame != null)
							{
								task.attachGame(newGame);
								continue;
							}
							
							readyClassed = null;
						}
						
						if (readyNonClassed)
						{
							final AbstractOlympiadGame newGame = OlympiadGameNonClassed.createGame(i, OlympiadManager.getInstance().getNonClassBasedParticipants());
							if (newGame != null)
							{
								task.attachGame(newGame);
								continue;
							}
							
							readyNonClassed = false;
						}
					}
				}
				
				if (readyClassed == null && !readyNonClassed)
					break;
			}
		}
		else
		{
			if (isAllTasksFinished())
			{
				OlympiadManager.getInstance().clearParticipants();
				
				_battleStarted = false;
				
				LOGGER.info("All current Olympiad games finished.");
			}
		}
	}
	
	protected final boolean isBattleStarted()
	{
		return _battleStarted;
	}
	
	protected final void startBattle()
	{
		_battleStarted = true;
	}
	
	public final boolean isAllTasksFinished()
	{
		for (OlympiadGameTask task : _tasks)
		{
			if (task.isRunning())
				return false;
		}
		return true;
	}
	
	public final OlympiadGameTask getOlympiadTask(int id)
	{
		if (id < 0 || id >= _tasks.length)
			return null;
		
		return _tasks[id];
	}
	
	public OlympiadGameTask[] getOlympiadTasks()
	{
		return _tasks;
	}
	
	public final int getNumberOfStadiums()
	{
		return _tasks.length;
	}
	
	public final void notifyCompetitorDamage(Player player, int damage)
	{
		if (player == null)
			return;
		
		final int id = player.getOlympiadGameId();
		if (id < 0 || id >= _tasks.length)
			return;
		
		final AbstractOlympiadGame game = _tasks[id].getGame();
		if (game != null)
			game.addDamage(player, damage);
	}
	
	public static final OlympiadGameManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final OlympiadGameManager INSTANCE = new OlympiadGameManager();
	}
}
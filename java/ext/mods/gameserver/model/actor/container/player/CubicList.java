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
package ext.mods.gameserver.model.actor.container.player;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Cubic;
import ext.mods.gameserver.skills.L2Skill;

/**
 * An ordered, concurrent queue container holding {@link Cubic}s of a {@link Player}.
 */
public class CubicList extends ConcurrentLinkedQueue<Cubic>
{
	private static final long serialVersionUID = 1L;
	
	public static final int SKILL_CUBIC_MASTERY = 143;
	
	private final Player _owner;
	
	public CubicList(Player owner)
	{
		_owner = owner;
	}
	
	/**
	 * @param id : The id to check.
	 * @return The {@link Cubic} corresponding to the id set as parameter, or null if not existing.
	 */
	public Cubic getCubic(int id)
	{
		return stream().filter(c -> c.getId() == id).findFirst().orElse(null);
	}
	
	/**
	 * Add, or refresh a {@link Cubic} to/from this {@link Player} _cubics {@link Queue}.
	 * @param id : The {@link Cubic} id.
	 * @param level : The level of {@link L2Skill}.
	 * @param matk : The magic power of the used {@link L2Skill}.
	 * @param activationTime : The tick time of called action.
	 * @param activationChance : The activation rate of called action.
	 * @param totalLifetime : The total lifetime of the {@link Cubic}.
	 * @param givenByOther : If true, it means the {@link Cubic} is a party {@link Cubic}.
	 */
	public void addOrRefreshCubic(int id, int level, double matk, int activationTime, int activationChance, int totalLifetime, boolean givenByOther)
	{
		final Cubic cubic = getCubic(id);
		if (cubic != null)
			cubic.refreshDisappearTask(totalLifetime);
		else
		{
			if (isFull())
				poll().stop(false);
			
			add(new Cubic(_owner, id, level, (int) matk, activationTime, activationChance, totalLifetime, givenByOther));
			
			_owner.broadcastUserInfo();
		}
	}
	
	/**
	 * Remove the {@link Cubic}, based on its id, from this {@link Player} _cubics {@link Queue}.
	 * @param id : The id to check.
	 */
	public void removeCubic(int id)
	{
		remove(getCubic(id));
	}
	
	/**
	 * Stop all {@link Cubic}s from this {@link Player}.
	 * @param doBroadcast : If true, we broadcast UserInfo/CharInfo.
	 */
	public final void stopCubics(boolean doBroadcast)
	{
		if (!isEmpty())
		{
			for (final Cubic cubic : this)
				cubic.stop(false);
			
			if (doBroadcast)
				_owner.broadcastUserInfo();
		}
	}
	
	/**
	 * Stop all {@link Cubic}s given by other {@link Player}s from this {@link Player}.
	 */
	public final void stopCubicsGivenByOthers()
	{
		if (!isEmpty())
		{
			boolean doBroadcast = false;
			for (final Cubic cubic : this)
			{
				if (cubic.givenByOther())
				{
					cubic.stop(false);
					doBroadcast = true;
				}
			}
			if (doBroadcast)
				_owner.broadcastUserInfo();
		}
	}
	
	/**
	 * @return True if the _cubics queue is already filled, or false otherwise.
	 */
	public final boolean isFull()
	{
		return size() > _owner.getSkillLevel(SKILL_CUBIC_MASTERY);
	}
}
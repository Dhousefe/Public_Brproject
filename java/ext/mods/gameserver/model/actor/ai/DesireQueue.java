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
package ext.mods.gameserver.model.actor.ai;

import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Set of {@link Desire}s, which is used to determine what kind of {@link Intention} the owner should do.<br>
 * <br>
 * {@link #getLast()} method returns the {@link Desire} with the highest weight.<br>
 * <br>
 * If the {@link Desire} with a set of parameters is already in the list, only the weight will be added to the existing one.
 */
public class DesireQueue
{
	private static final int MAX_CAPACITY = 50;
	
	private final Set<Desire> _desires = ConcurrentHashMap.newKeySet();
	
	public Set<Desire> getDesires()
	{
		return _desires;
	}
	
	/**
	 * Add a new {@link Desire} to the {@link Set}, or update the weight of an existing one.<br>
	 * <br>
	 * If a {@link Desire} with the same characteristics is already in, its weight is increased by the amount of the {@link Desire} set as parameter.<br>
	 * If it is not present and the {@link Set} has not reached its maximum capacity, the new {@link Desire} is added.
	 * @param desire : The {@link Desire} to add or update.
	 */
	public void addOrUpdate(Desire desire)
	{
		_desires.stream().filter(d -> d.equals(desire)).findFirst().ifPresentOrElse(d -> d.addWeight(desire.getWeight()), () ->
		{
			if (_desires.size() < MAX_CAPACITY)
				_desires.add(desire);
		});
	}
	
	/**
	 * Decrease the weight of each {@link Desire} in the set.
	 */
	public void autoDecreaseWeight()
	{
		if (_desires.isEmpty())
			return;
		
		_desires.forEach(Desire::autoDecreaseWeight);
	}
	
	/**
	 * @return The {@link Desire} with the highest weight, or null if the {@link Set} is empty.
	 */
	public Desire getLast()
	{
		if (_desires.isEmpty())
			return null;
		
		return _desires.stream().max(Comparator.comparingDouble(Desire::getWeight)).orElse(null);
	}
}
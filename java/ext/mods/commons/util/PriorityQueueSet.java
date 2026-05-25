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
package ext.mods.commons.util;

import java.util.HashSet;
import java.util.PriorityQueue;

public class PriorityQueueSet<T> extends PriorityQueue<T>
{
	private static final long serialVersionUID = 1L;
	
	private final HashSet<T> _set;
	
	public PriorityQueueSet()
	{
		_set = new HashSet<>();
	}
	
	@Override
	public boolean add(T element)
	{
		if (_set.add(element))
			return super.add(element);
		
		return false;
	}
	
	@Override
	public boolean remove(Object element)
	{
		if (_set.remove(element))
			return super.remove(element);
		
		return false;
	}
	
	@Override
	public boolean contains(Object element)
	{
		return _set.contains(element);
	}
}
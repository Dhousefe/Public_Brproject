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
package ext.mods.InstanceMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.gameserver.data.xml.DoorData;
import ext.mods.gameserver.model.actor.instance.Door;

public class InstanceManager
{
	private Map<Integer, MapInstance> instances;
	
	protected InstanceManager()
	{
		instances = new ConcurrentHashMap<>();
		instances.put(0, new MapInstance(0));
	}
	
	public void addDoor(int instanceid, Door door)
	{
		if (!instances.containsKey(instanceid) || instanceid == 0)
			return;
		
		instances.get(instanceid).addDoor(door);
	}
	
	public void addDoor(int instanceid, int doorid)
	{
		if (!instances.containsKey(instanceid) || instanceid == 0)
			return;
		
		Door data = DoorData.getInstance().getDoor(doorid);
		
		instances.get(instanceid).addDoor(data);
	}
	
	public void deleteInstance(int id)
	{
		if (id == 0)
		{
			System.out.println("Attempt to delete instance with id 0.");
			return;
		}
		
		instances.remove(id);
	}
	
	public synchronized MapInstance createInstance()
	{
		MapInstance instance = new MapInstance(InstanceIdFactory.getNextAvailable());
		instances.put(instance.getId(), instance);
		return instance;
	}
	
	public MapInstance getInstance(int id)
	{
		return instances.get(id);
	}
	
	public static InstanceManager getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static final class SingletonHolder
	{
		protected static final InstanceManager instance = new InstanceManager();
	}
}

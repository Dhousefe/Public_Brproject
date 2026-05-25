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
package ext.mods.extensions.listener.manager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ext.mods.extensions.listener.zone.OnZoneEnterLeaveListener;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.zone.type.subtype.ZoneType;

public class ZoneListenerManager
{
	private static final ZoneListenerManager INSTANCE = new ZoneListenerManager();
	
	private final List<OnZoneEnterLeaveListener> zoneListeners = new CopyOnWriteArrayList<>();
	
	private ZoneListenerManager()
	{
	}
	
	public static ZoneListenerManager getInstance()
	{
		return INSTANCE;
	}
	
	public void addZoneListener(OnZoneEnterLeaveListener listener)
	{
		zoneListeners.add(listener);
	}
	
	public void removeZoneListener(OnZoneEnterLeaveListener listener)
	{
		zoneListeners.remove(listener);
	}
	
	public void notifyZoneEnter(ZoneType zone, Creature creature)
	{
		for (OnZoneEnterLeaveListener listener : zoneListeners)
		{
			try
			{
				listener.onZoneEnter(zone, creature);
			}
			catch (Exception e)
			{
				System.err.println("[ZoneListenerManager] Erro no onZoneEnter: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	public void notifyZoneLeave(ZoneType zone, Creature creature)
	{
		for (OnZoneEnterLeaveListener listener : zoneListeners)
		{
			try
			{
				listener.onZoneLeave(zone, creature);
			}
			catch (Exception e)
			{
				System.err.println("[ZoneListenerManager] Erro no onZoneLeave: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}

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

import ext.mods.extensions.listener.siege.OnSiegeListener;
import ext.mods.extensions.listener.siege.OnSiegeRegisterAttackerListener;
import ext.mods.extensions.listener.siege.OnSiegeRegisterDefenderListener;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.residence.castle.Siege;

public class SiegeListenerManager
{
	private static final SiegeListenerManager INSTANCE = new SiegeListenerManager();
	private final List<OnSiegeListener> listeners = new CopyOnWriteArrayList<>();
	private final List<OnSiegeRegisterAttackerListener> attackerListeners = new CopyOnWriteArrayList<>();
	private final List<OnSiegeRegisterDefenderListener> defenderListeners = new CopyOnWriteArrayList<>();
	
	private SiegeListenerManager()
	{
	}
	
	public static SiegeListenerManager getInstance()
	{
		return INSTANCE;
	}
	
	public void registerListener(OnSiegeListener listener)
	{
		listeners.add(listener);
	}
	
	public void unregisterListener(OnSiegeListener listener)
	{
		listeners.remove(listener);
	}
	
	public void notifySiegeStart(Siege siege)
	{
		for (OnSiegeListener l : listeners)
		{
			try
			{
				l.onSiegeStart(siege);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void notifySiegeEnd(Siege siege)
	{
		for (OnSiegeListener l : listeners)
		{
			try
			{
				l.onSiegeEnd(siege);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void registerAttackerListener(OnSiegeRegisterAttackerListener listener)
	{
		attackerListeners.add(listener);
	}
	
	public void unregisterAttackerListener(OnSiegeRegisterAttackerListener listener)
	{
		attackerListeners.remove(listener);
	}
	
	public void registerDefenderListener(OnSiegeRegisterDefenderListener listener)
	{
		defenderListeners.add(listener);
	}
	
	public void unregisterDefenderListener(OnSiegeRegisterDefenderListener listener)
	{
		defenderListeners.remove(listener);
	}
	
	public void notifyRegisterAttacker(Siege siege, Player player)
	{
		for (OnSiegeRegisterAttackerListener l : attackerListeners)
		{
			try
			{
				l.onRegisterAttacker(siege, player);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void notifyRegisterDefender(Siege siege, Player player)
	{
		for (OnSiegeRegisterDefenderListener l : defenderListeners)
		{
			try
			{
				l.onRegisterDefender(siege, player);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}

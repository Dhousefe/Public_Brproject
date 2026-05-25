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

import ext.mods.extensions.listener.game.OnCharacterDeleteListener;
import ext.mods.extensions.listener.game.OnShutdownListener;
import ext.mods.extensions.listener.game.OnStartListener;

public class GameListenerManager
{
	private static final GameListenerManager INSTANCE = new GameListenerManager();
	
	private final List<OnCharacterDeleteListener> characterDeleteListeners = new CopyOnWriteArrayList<>();
	private final List<OnShutdownListener> shutdownListeners = new CopyOnWriteArrayList<>();
	private final List<OnStartListener> startListeners = new CopyOnWriteArrayList<>();
	
	private GameListenerManager()
	{
	}
	
	public static GameListenerManager getInstance()
	{
		return INSTANCE;
	}
	
	public void registerCharacterDeleteListener(OnCharacterDeleteListener listener)
	{
		characterDeleteListeners.add(listener);
	}
	
	public void unregisterCharacterDeleteListener(OnCharacterDeleteListener listener)
	{
		characterDeleteListeners.remove(listener);
	}
	
	public void registerShutdownListener(OnShutdownListener listener)
	{
		shutdownListeners.add(listener);
	}
	
	public void unregisterShutdownListener(OnShutdownListener listener)
	{
		shutdownListeners.remove(listener);
	}
	
	public void registerStartListener(OnStartListener listener)
	{
		startListeners.add(listener);
	}
	
	public void unregisterStartListener(OnStartListener listener)
	{
		startListeners.remove(listener);
	}

	public void notifyCharacterDelete(int objectId)
	{
		for (OnCharacterDeleteListener listener : characterDeleteListeners)
		{
			listener.onCharacterDelete(objectId);
		}
	}
	
	public void notifyShutdown()
	{
		for (OnShutdownListener listener : shutdownListeners)
		{
			listener.onShutdown();
		}
	}
	
	public void notifyStart()
	{
		for (OnStartListener listener : startListeners)
		{
			listener.onStart();
		}
	}
}

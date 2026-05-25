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

import ext.mods.extensions.listener.actor.npc.OnDecayListener;
import ext.mods.extensions.listener.actor.npc.OnInteractListener;
import ext.mods.extensions.listener.actor.npc.OnNpcKillListener;
import ext.mods.extensions.listener.actor.npc.OnSpawnListener;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;

public class NpcListenerManager
{
	private static final NpcListenerManager INSTANCE = new NpcListenerManager();
	
	private final List<OnSpawnListener> npcSpawnListeners = new CopyOnWriteArrayList<>();
	private final List<OnDecayListener> npcDecayListeners = new CopyOnWriteArrayList<>();
	private final List<OnInteractListener> npcInteractListeners = new CopyOnWriteArrayList<>();
	private final List<OnNpcKillListener> npcKillListeners = new CopyOnWriteArrayList<>();

	private NpcListenerManager()
	{
	}
	
	public static NpcListenerManager getInstance()
	{
		return INSTANCE;
	}
	
	public void registerNpcSpawnListener(OnSpawnListener listener)
	{
		npcSpawnListeners.add(listener);
	}
	
	public void unregisterNpcSpawnListener(OnSpawnListener listener)
	{
		npcSpawnListeners.remove(listener);
	}
	
	public void notifyNpcSpawn(Npc npc)
	{
		for (OnSpawnListener listener : npcSpawnListeners)
		{
			listener.onSpawn(npc);
		}
	}
	
	public void registerNpcDecayListener(OnDecayListener listener)
	{
		npcDecayListeners.add(listener);
	}
	
	public void unregisterNpcDecayListener(OnDecayListener listener)
	{
		npcDecayListeners.remove(listener);
	}
	
	public void notifyNpcDecay(Npc npc)
	{
		for (OnDecayListener listener : npcDecayListeners)
		{
			listener.onDecay(npc);
		}
	}
	
	public void registerNpcInteractListener(OnInteractListener listener)
	{
	    npcInteractListeners.add(listener);
	}

	public void unregisterNpcInteractListener(OnInteractListener listener)
	{
	    npcInteractListeners.remove(listener);
	}

	public boolean notifyNpcInteract(Npc npc, Player player)
	{
	    for (OnInteractListener listener : npcInteractListeners)
	    {
	        if (listener.onInteract(npc, player))
	            return true;
	    }
	    return false;
	}
	
	public void registerNpcKillListener(OnNpcKillListener listener)
	{
	    npcKillListeners.add(listener);
	}

	public void unregisterNpcKillListener(OnNpcKillListener listener)
	{
	    npcKillListeners.remove(listener);
	}

	public void notifyNpcKill(Npc npc, Player killer)
	{
	    for (OnNpcKillListener listener : npcKillListeners)
	    {
	        listener.onKill(npc, killer);
	    }
	}
}
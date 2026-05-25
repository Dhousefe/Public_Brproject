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

import ext.mods.extensions.listener.OnAttackHitListener;
import ext.mods.extensions.listener.OnAttackListener;
import ext.mods.extensions.listener.OnCurrentHpDamageListener;
import ext.mods.extensions.listener.OnDeathListener;
import ext.mods.extensions.listener.OnKillListener;
import ext.mods.extensions.listener.OnMoveListener;
import ext.mods.extensions.listener.OnReviveListener;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.skills.L2Skill;

public class CreatureListenerManager
{
	private static final CreatureListenerManager INSTANCE = new CreatureListenerManager();
	
	private final List<OnReviveListener> reviveListeners = new CopyOnWriteArrayList<>();
	private final List<OnMoveListener> moveListeners = new CopyOnWriteArrayList<>();
	private final List<OnKillListener> killListeners = new CopyOnWriteArrayList<>();
	private final List<OnDeathListener> deathListeners = new CopyOnWriteArrayList<>();
	private final List<OnCurrentHpDamageListener> hpDamageListeners = new CopyOnWriteArrayList<>();
	private final List<OnAttackListener> attackListeners = new CopyOnWriteArrayList<>();
	private final List<OnAttackHitListener> attackHitListeners = new CopyOnWriteArrayList<>();
	
	private CreatureListenerManager()
	{
	}
	
	public static CreatureListenerManager getInstance()
	{
		return INSTANCE;
	}
	
	public void addReviveListener(OnReviveListener listener)
	{
		reviveListeners.add(listener);
	}
	
	public void addMoveListener(OnMoveListener listener)
	{
		moveListeners.add(listener);
	}
	
	public void addKillListener(OnKillListener listener)
	{
		killListeners.add(listener);
	}
	
	public void addDeathListener(OnDeathListener listener)
	{
		deathListeners.add(listener);
	}
	
	public void addHpDamageListener(OnCurrentHpDamageListener listener)
	{
		hpDamageListeners.add(listener);
	}
	
	public void addAttackListener(OnAttackListener listener)
	{
		attackListeners.add(listener);
	}
	
	public void addAttackHitListener(OnAttackHitListener listener)
	{
		attackHitListeners.add(listener);
	}
	
	
	public void notifyRevive(Creature creature)
	{
		for (OnReviveListener listener : reviveListeners)
			listener.onRevive(creature);
	}
	
	public void notifyMove(Creature creature, Location loc)
	{
		for (OnMoveListener listener : moveListeners)
			listener.onMove(creature, loc);
	}
	
	public void notifyKill(Creature killer, Creature victim)
	{
		for (OnKillListener listener : killListeners)
		{
			
			if (((Player) killer).hasPet() || ((Player) killer).getSummon() != null)
			{
				if (listener.ignorePetOrSummon())
					continue;
			}
			listener.onKill(killer, victim);
		}
	}
	
	public void notifyDeath(Creature killer, Creature victim)
	{
		for (OnDeathListener listener : deathListeners)
			listener.onDeath(killer, victim);
	}
	
	public void notifyHpDamage(Creature creature, double damageHp, Creature target, L2Skill skill)
	{
		for (OnCurrentHpDamageListener listener : hpDamageListeners)
			listener.onCurrentHpDamage(creature, damageHp, target, skill);
	}
	
	public void notifyAttack(Creature creature, Creature target)
	{
		for (OnAttackListener listener : attackListeners)
			listener.onAttack(creature, target);
	}
	
	public void notifyAttackHit(Creature creature, Creature target)
	{
		for (OnAttackHitListener listener : attackHitListeners)
			listener.onAttackHit(creature, target);
	}
}

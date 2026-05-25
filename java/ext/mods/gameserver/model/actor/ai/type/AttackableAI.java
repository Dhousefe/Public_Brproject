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
package ext.mods.gameserver.model.actor.ai.type;

import java.util.concurrent.ScheduledFuture;

import ext.mods.commons.pool.ThreadPool;
import ext.mods.commons.random.Rnd;
import ext.mods.commons.util.ArraysUtil;

import ext.mods.Config;
import ext.mods.gameserver.enums.AiEventType;
import ext.mods.gameserver.enums.EventHandler;
import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.model.actor.Attackable;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.scripting.Quest;

public class AttackableAI<T extends Attackable> extends NpcAI<T>
{
	private ScheduledFuture<?> _wanderTask;
	
	public AttackableAI(T attackable)
	{
		super(attackable);
	}
	
	@Override
	protected void thinkWander()
	{
		if (_currentIntention.getType() != IntentionType.WANDER)
			return;
		
		_actor.setWalkOrRun(false);
		
		if (_actor.isMoving())
			return;
		
		if (_lastDesire == null || _lastDesire.getType() != IntentionType.WANDER)
		{
			if (_wanderTask != null)
				_wanderTask.cancel(false);
			
			if (_actor.returnHome())
				return;
			
			if (!_actor.isInMyTerritory())
			{
				clearCurrentDesire();
				return;
			}
			
			_actor.moveFromSpawnPointUsingRandomOffset((int) _actor.getStatus().getRealMoveSpeed(true) * 3);
		}
		else
		{
			_wanderTask = ThreadPool.schedule(() ->
			{
				if (_wanderTask != null)
				{
					_wanderTask.cancel(false);
					_wanderTask = null;
				}
				
				if (Config.RANDOM_WALK_RATE > 0 && Rnd.get(100) < Config.RANDOM_WALK_RATE)
				{
					if (!_actor.isMoving() && _currentIntention.getType() == IntentionType.WANDER)
					{
						if (_actor.returnHome())
							return;
						
						if (!_actor.isInMyTerritory())
						{
							clearCurrentDesire();
							return;
						}
						
						_actor.moveFromSpawnPointUsingRandomOffset((int) _actor.getStatus().getRealMoveSpeed(true) * 3);
					}
				}
				else
					thinkWander();
			}, _currentIntention.getTimer() * 1000L);
		}
	}
	
	@Override
	protected void onEvtFinishedAttackBow()
	{
	}
	
	@Override
	protected void onEvtBowAttackReuse()
	{
		if (_nextIntention.isBlank())
			notifyEvent(AiEventType.THINK, null, null);
		else
			doIntention(_nextIntention);
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker)
	{
		_actor.addAttacker(attacker);
		
		super.onEvtAttacked(attacker);
	}
	
	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{
		for (Quest quest : _actor.getTemplate().getEventQuests(EventHandler.ATTACKED))
			quest.onAttacked(_actor, target, aggro, null);
		
		if (_actor.isMaster() || _actor.hasMaster())
		{
			for (Quest quest : _actor.getTemplate().getEventQuests(EventHandler.PARTY_ATTACKED))
				quest.onPartyAttacked(_actor, _actor, target, aggro);
			
			final Npc master = _actor.getMaster();
			if (master != null && !master.isDead())
			{
				for (Quest quest : master.getTemplate().getEventQuests(EventHandler.PARTY_ATTACKED))
					quest.onPartyAttacked(_actor, master, target, aggro);
			}
			
			for (Npc minion : _actor.getMinions())
			{
				if (minion.isDead())
					continue;
				
				for (Quest quest : minion.getTemplate().getEventQuests(EventHandler.PARTY_ATTACKED))
					quest.onPartyAttacked(_actor, minion, target, aggro);
			}
		}
		
		final String[] actorClans = _actor.getTemplate().getClans();
		if (actorClans != null && _actor.getTemplate().getClanRange() > 0)
		{
			for (Quest quest : _actor.getTemplate().getEventQuests(EventHandler.CLAN_ATTACKED))
				quest.onClanAttacked(_actor, _actor, target, aggro, null);
			
			_actor.forEachKnownTypeInRadius(Attackable.class, _actor.getTemplate().getClanRange(), called ->
			{
				if (called.isDead() || called != _actor)
					return;
				
				if (!ArraysUtil.contains(actorClans, called.getTemplate().getClans()))
					return;
				
				if (ArraysUtil.contains(called.getTemplate().getIgnoredIds(), _actor.getNpcId()))
					return;
				
				if (!GeoEngine.getInstance().canSeeTarget(_actor, called))
					return;
				
				for (Quest quest : called.getTemplate().getEventQuests(EventHandler.CLAN_ATTACKED))
					quest.onClanAttacked(_actor, called, target, aggro, null);
			});
		}
	}
	
	/**
	 * This method holds behavioral information on which Intentions are scheduled and which are cast immediately.
	 * <ul>
	 * <li>All possible intentions are scheduled for AttackableAI.</li>
	 * </ul>
	 * @param oldIntention : The {@link IntentionType} to test against.
	 * @param newIntention : The {@link IntentionType} to test.
	 * @return True if the {@link IntentionType} set as parameter can be sheduled after this {@link IntentionType}, otherwise cast it immediately.
	 */
	@Override
	public boolean canScheduleAfter(IntentionType oldIntention, IntentionType newIntention)
	{
		return false;
	}
}
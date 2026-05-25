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

import ext.mods.gameserver.enums.AiEventType;
import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.enums.skills.SkillType;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Boat;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.ai.Intention;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.network.serverpackets.AutoAttackStart;
import ext.mods.gameserver.network.serverpackets.AutoAttackStop;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.taskmanager.AttackStanceTaskManager;

/**
 * A layer used to process {@link Intention}s of an {@link Creature}.<br>
 * <br>
 * Each {@link Creature} holds 3 {@link Intention}s : past, current and future.<br>
 * <br>
 * AI behavior is then affected by potential {@link AiEventType}s, or by own AI type.
 * @param <T> : The {@link Creature} used as actor.
 */
abstract class AbstractAI<T extends Creature>
{
	protected final T _actor;
	
	protected Intention _currentIntention = new Intention();
	protected Intention _nextIntention = new Intention();
	
	protected AbstractAI(T actor)
	{
		_actor = actor;
	}
	
	protected abstract void onEvtAggression(Creature target, int aggro);
	
	protected abstract void onEvtArrived();
	
	protected abstract void onEvtArrivedBlocked();
	
	protected abstract void onEvtAttacked(Creature attacker);
	
	protected abstract void onEvtBowAttackReuse();
	
	protected abstract void onEvtCancel();
	
	protected abstract void onEvtDead();
	
	protected abstract void onEvtEvaded(Creature attacker);
	
	protected abstract void onEvtFinishedAttack();
	
	protected abstract void onEvtFinishedAttackBow();
	
	protected abstract void onEvtFinishedCasting();
	
	protected abstract void onEvtOwnerAttacked(Creature attacker);
	
	protected abstract void onEvtSatDown(WorldObject target);
	
	protected abstract void onEvtStoodUp();
	
	protected abstract void onEvtTeleported();
	
	protected abstract void thinkAttack();
	
	protected abstract void thinkCast();
	
	protected abstract void thinkFakeDeath();
	
	protected abstract void thinkFlee();
	
	protected abstract void thinkFollow();
	
	protected abstract void thinkIdle();
	
	protected abstract void thinkInteract();
	
	protected abstract void thinkMoveRoute();
	
	protected abstract void thinkMoveTo();
	
	protected abstract void thinkNothing();
	
	protected abstract ItemInstance thinkPickUp();
	
	protected abstract void thinkSit();
	
	protected abstract void thinkSocial();
	
	protected abstract void thinkStand();
	
	protected abstract void thinkUseItem();
	
	protected abstract void thinkWander();
	
	@Override
	public String toString()
	{
		return "Actor: " + _actor;
	}
	
	public final synchronized Intention getCurrentIntention()
	{
		return _currentIntention;
	}
	
	public final synchronized Intention getNextIntention()
	{
		return _nextIntention;
	}
	
	protected final synchronized void setNextIntention(Intention intention)
	{
		_nextIntention.updateUsing(intention);
	}
	
	/**
	 * The core method used by sub-intentions, doing following actions :
	 * <ul>
	 * <li>Stop follow movement if necessary.</li>
	 * <li>Refresh previous intention with current {@link Intention}.</li>
	 * <li>Reset next {@link Intention}.</li>
	 * </ul>
	 */
	protected synchronized void prepareIntention()
	{
		_actor.getMove().cancelFollowTask();
		
		_nextIntention.updateAsIdle();
	}
	
	protected synchronized void doAttackIntention(Creature target, boolean isCtrlPressed, boolean isShiftPressed, boolean canMoveToTarget)
	{
		prepareIntention();
		
		_currentIntention.updateAsAttack(target, isCtrlPressed, isShiftPressed, canMoveToTarget);
		
		thinkAttack();
	}
	
	protected synchronized void doCastIntention(Creature target, L2Skill skill, boolean isCtrlPressed, boolean isShiftPressed, int itemObjectId, boolean canMoveToTarget)
	{
		prepareIntention();
		
		_currentIntention.updateAsCast(_actor, target, skill, isCtrlPressed, isShiftPressed, itemObjectId, canMoveToTarget);
		
		thinkCast();
	}
	
	protected synchronized void doFakeDeathIntention(boolean startFakeDeath)
	{
		prepareIntention();
		
		_currentIntention.updateAsFakeDeath(startFakeDeath);
		
		thinkFakeDeath();
	}
	
	protected synchronized void doFleeIntention(Creature target, Location startLoc, int distance)
	{
		prepareIntention();
		
		_currentIntention.updateAsFlee(target, startLoc, distance);
		
		thinkFlee();
	}
	
	protected synchronized void doFollowIntention(Creature target, boolean isShiftPressed)
	{
		prepareIntention();
		
		_currentIntention.updateAsFollow(target, isShiftPressed);
		
		thinkFollow();
	}
	
	protected synchronized void doIdleIntention()
	{
		prepareIntention();
		
		_currentIntention.updateAsIdle();
		
		thinkIdle();
	}
	
	protected synchronized void doInteractIntention(WorldObject target, boolean isCtrlPressed, boolean isShiftPressed)
	{
		prepareIntention();
		
		_currentIntention.updateAsInteract(target, isCtrlPressed, isShiftPressed);
		
		thinkInteract();
	}
	
	protected synchronized void doMoveRouteIntention(String routeName)
	{
		prepareIntention();
		
		_currentIntention.updateAsMoveRoute(routeName);
		
		thinkMoveRoute();
	}
	
	public synchronized void doMoveToIntention(Location loc, Boat boat)
	{
		prepareIntention();
		
		_currentIntention.updateAsMoveTo(loc, boat);
		
		thinkMoveTo();
	}
	
	protected synchronized void doNothingIntention(int timer)
	{
		prepareIntention();
		
		_currentIntention.updateAsNothing(timer);
		
		thinkNothing();
	}
	
	protected synchronized void doPickUpIntention(int itemObjectId, boolean isShiftPressed)
	{
		prepareIntention();
		
		_currentIntention.updateAsPickUp(itemObjectId, isShiftPressed);
		
		thinkPickUp();
	}
	
	protected synchronized void doSitIntention(WorldObject target)
	{
		prepareIntention();
		
		_currentIntention.updateAsSit(target);
		
		thinkSit();
	}
	
	protected synchronized void doSocialIntention(int id, int timer)
	{
		prepareIntention();
		
		_currentIntention.updateAsSocial(id, timer);
		
		thinkSocial();
	}
	
	protected synchronized void doStandIntention()
	{
		prepareIntention();
		
		_currentIntention.updateAsStand();
		
		thinkStand();
	}
	
	protected synchronized void doUseItemIntention(int itemObjectId)
	{
		prepareIntention();
		
		_currentIntention.updateAsUseItem(itemObjectId);
		
		thinkUseItem();
	}
	
	protected synchronized void doWanderIntention(int timer)
	{
		prepareIntention();
		
		_currentIntention.updateAsWander(timer);
		
		thinkWander();
	}
	
	/**
	 * Launch the onIntention method corresponding to an existing {@link Intention}.
	 * @param intention : The new {@link Intention} to set.
	 */
	protected final synchronized void doIntention(Intention intention)
	{
		switch (intention.getType())
		{
			case ATTACK:
				doAttackIntention(intention.getFinalTarget(), intention.isCtrlPressed(), intention.isShiftPressed(), intention.canMoveToTarget());
				break;
			
			case CAST:
				doCastIntention(intention.getFinalTarget(), intention.getSkill(), intention.isCtrlPressed(), intention.isShiftPressed(), intention.getItemObjectId(), intention.canMoveToTarget());
				break;
			
			case FAKE_DEATH:
				doFakeDeathIntention(intention.isCtrlPressed());
				break;
			
			case FLEE:
				doFleeIntention(intention.getFinalTarget(), intention.getLoc(), intention.getItemObjectId());
				break;
			
			case FOLLOW:
				doFollowIntention(intention.getFinalTarget(), intention.isShiftPressed());
				break;
			
			case IDLE:
				doIdleIntention();
				break;
			
			case INTERACT:
				doInteractIntention(intention.getTarget(), intention.isCtrlPressed(), intention.isShiftPressed());
				break;
			
			case MOVE_ROUTE:
				doMoveRouteIntention(intention.getRouteName());
				break;
			
			case MOVE_TO:
				doMoveToIntention(intention.getLoc(), intention.getBoat());
				break;
			
			case NOTHING:
				doNothingIntention(intention.getTimer());
				break;
			
			case PICK_UP:
				doPickUpIntention(intention.getItemObjectId(), intention.isShiftPressed());
				break;
			
			case SIT:
				doSitIntention(intention.getTarget());
				break;
			
			case SOCIAL:
				doSocialIntention(intention.getItemObjectId(), intention.getTimer());
				break;
			
			case STAND:
				doStandIntention();
				break;
			
			case USE_ITEM:
				doUseItemIntention(intention.getItemObjectId());
				break;
			
			case WANDER:
				doWanderIntention(intention.getTimer());
				break;
		}
	}
	
	/**
	 * Launch the onEvt method corresponding to the {@link AiEventType} set as parameter.<br>
	 * <br>
	 * <font color=#FF0000><b><u>Caution</u>: The current general intention won't be change (ex: if the character attack and is stunned, he will attack again after the stunned period).</b></font>
	 * @param evt : The {@link AiEventType} whose the AI must be notified.
	 * @param firstParameter : The first {@link Object} parameter of the event.
	 * @param secondParameter : The second {@link Object} parameter of the event.
	 */
	public final void notifyEvent(AiEventType evt, Object firstParameter, Object secondParameter)
	{
		if ((!_actor.isVisible() && !_actor.isTeleporting()))
			return;
		
		switch (evt)
		{
			case THINK:
				onEvtThink();
				break;
			
			case ATTACKED:
				onEvtAttacked((Creature) firstParameter);
				break;
			
			case AGGRESSION:
				onEvtAggression((Creature) firstParameter, ((Number) secondParameter).intValue());
				break;
			
			case EVADED:
				onEvtEvaded((Creature) firstParameter);
				break;
			
			case FINISHED_ATTACK:
				onEvtFinishedAttack();
				break;
			
			case FINISHED_ATTACK_BOW:
				onEvtFinishedAttackBow();
				break;
			
			case BOW_ATTACK_REUSED:
				onEvtBowAttackReuse();
				break;
			
			case ARRIVED:
				onEvtArrived();
				break;
			
			case ARRIVED_BLOCKED:
				onEvtArrivedBlocked();
				break;
			
			case CANCEL:
				onEvtCancel();
				break;
			
			case DEAD:
				onEvtDead();
				break;
			
			case FINISHED_CASTING:
				onEvtFinishedCasting();
				break;
			
			case SAT_DOWN:
				onEvtSatDown((WorldObject) firstParameter);
				break;
			
			case STOOD_UP:
				onEvtStoodUp();
				break;
			
			case OWNER_ATTACKED:
				onEvtOwnerAttacked((Creature) firstParameter);
				break;
			
			case TELEPORTED:
				onEvtTeleported();
				break;
		}
	}
	
	protected synchronized void onEvtThink()
	{
		switch (_currentIntention.getType())
		{
			case ATTACK:
				thinkAttack();
				break;
			
			case CAST:
				thinkCast();
				break;
			
			case FAKE_DEATH:
				thinkFakeDeath();
				break;
			
			case FOLLOW:
				thinkFollow();
				break;
			
			case IDLE:
				thinkIdle();
				break;
			
			case INTERACT:
				thinkInteract();
				break;
			
			case MOVE_TO:
				thinkMoveTo();
				break;
			
			case PICK_UP:
				thinkPickUp();
				break;
			
			case SIT:
				thinkSit();
				break;
			
			case STAND:
				thinkStand();
				break;
			
			case USE_ITEM:
				thinkUseItem();
				break;
		}
	}
	
	/**
	 * This method holds behavioral information on which Intentions are scheduled and which are cast immediately.
	 * <ul>
	 * <li>Nothing is scheduled after FOLLOW, PICK_UP, INTERACT.</li>
	 * <li>Any action that occurs during isAttackingNow / isCastingNow is automatically scheduled, so checks for ATTACK and CAST are useless.</li>
	 * <li>Only STAND is scheduled after SIT and FAKE_DEATH. Anything else is illegal and is cast immediately so it can be rejected.</li>
	 * <li>Only SIT is scheduled after MOVE_TO. Anything else is cast immediately.</li>
	 * <li>All possible intentions are scheduled after STAND.</li>
	 * </ul>
	 * @param oldIntention : The {@link IntentionType} to test against.
	 * @param newIntention : The {@link IntentionType} to test.
	 * @return True if the {@link IntentionType} set as parameter can be sheduled after this {@link IntentionType}, otherwise cast it immediately.
	 */
	public boolean canScheduleAfter(IntentionType oldIntention, IntentionType newIntention)
	{
		switch (oldIntention)
		{
			case SIT, FAKE_DEATH:
				return newIntention == IntentionType.STAND;
			
			case STAND:
				return true;
			
			case MOVE_TO:
				return newIntention == IntentionType.SIT;
		}
		return false;
	}
	
	/**
	 * Cancel action client side by sending ActionFailed packet to the Player actor.
	 */
	public void clientActionFailed()
	{
	}
	
	/**
	 * Activate the attack stance, broadcasting {@link AutoAttackStart} packet. Refresh the timer if already registered on {@link AttackStanceTaskManager}.
	 */
	public void startAttackStance()
	{
		if (!AttackStanceTaskManager.getInstance().isInAttackStance(_actor))
			_actor.broadcastPacket(new AutoAttackStart(_actor.getObjectId()));
		
		AttackStanceTaskManager.getInstance().add(_actor);
	}
	
	/**
	 * Deactivate the attack stance, broadcasting {@link AutoAttackStop} packet if the actor was registered on {@link AttackStanceTaskManager}.
	 */
	public void stopAttackStance()
	{
		if (AttackStanceTaskManager.getInstance().remove(_actor))
			_actor.broadcastPacket(new AutoAttackStop(_actor.getObjectId()));
	}
	
	/**
	 * Send the state of this actor to a {@link Player}.
	 * @param player : The {@link Player} to notify with the state of this actor.
	 */
	public void describeStateToPlayer(Player player)
	{
		if (_actor.isMoving())
			_actor.getMove().describeMovementTo(player);
		else if (_actor.getCast().isCastingNow())
			_actor.getCast().describeCastTo(player);
	}
	
	/**
	 * Stop all tasks related to AI.
	 */
	public void stopAITask()
	{
		_actor.getMove().cancelFollowTask();
	}
	
	/**
	 * @param target : The {@link WorldObject} used as parameter.
	 * @return True if the given {@link WorldObject} is missing out (null, not registered, not on same region).
	 */
	public boolean isTargetLost(WorldObject target)
	{
		if (target == null)
			return true;
		
		if (World.getInstance().getObject(target.getObjectId()) == null)
			return true;
		
		return !_actor.knows(target);
	}
	
	/**
	 * @param target : The {@link WorldObject} used as parameter.
	 * @param skill : The {@link L2Skill} used as parameter.
	 * @return True if the given {@link WorldObject} is missing out (null, not registered, not on same region), giving a given {@link L2Skill}.
	 */
	public boolean isTargetLost(WorldObject target, L2Skill skill)
	{
		if (target == null)
			return true;
		
		if (World.getInstance().getObject(target.getObjectId()) == null)
			return true;
		
		if (skill != null && skill.getSkillType() == SkillType.SUMMON_FRIEND)
			return false;
		
		return !_actor.knows(target);
	}
}
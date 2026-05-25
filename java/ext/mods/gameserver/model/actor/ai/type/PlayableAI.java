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

import ext.mods.Config;
import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.enums.AiEventType;
import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.enums.items.ItemLocation;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Boat;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.ai.Intention;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.MoveToPawn;
import ext.mods.gameserver.skills.L2Skill;

public abstract class PlayableAI<T extends Playable> extends CreatureAI<T>
{
	protected Intention _previousIntention = new Intention();
	
	protected PlayableAI(T actor)
	{
		super(actor);
	}
	
	@Override
	protected synchronized void prepareIntention()
	{
		_actor.getMove().cancelFollowTask();
		
		_previousIntention.updateUsing(_currentIntention);
		
		_nextIntention.updateAsIdle();
	}
	
	@Override
	protected void onEvtFinishedCasting()
	{
		if (_nextIntention.isBlank())
		{
			if (_currentIntention.getType() == IntentionType.CAST)
			{
				final L2Skill skill = _currentIntention.getSkill();
				final Creature target = _currentIntention.getFinalTarget();
				
				if (skill.nextActionIsAttack() && target.isAttackableWithoutForceBy(_actor))
					doAttackIntention(target, _currentIntention.isCtrlPressed(), _currentIntention.isShiftPressed(), true);
				else if (!skill.isToggle() && Config.STOP_TOGGLE)
					doIdleIntention();
			}
			else
				doIdleIntention();
		}
		else
			doIntention(_nextIntention);
	}
	
	@Override
	protected void onEvtFinishedAttack()
	{
		if (_nextIntention.isBlank())
		{
			if (_actor.canKeepAttacking(_currentIntention.getFinalTarget()))
				notifyEvent(AiEventType.THINK, null, null);
			else
				doIdleIntention();
		}
		else
			doIntention(_nextIntention);
	}
	
	@Override
	protected void thinkCast()
	{
		if (_actor.denyAiAction() || _actor.getAllSkillsDisabled() || _actor.getCast().isCastingNow())
		{
			doIdleIntention();
			return;
		}
		
		final Creature target = _currentIntention.getFinalTarget();
		final L2Skill skill = _currentIntention.getSkill();
		
		if (isTargetLost(target, skill))
		{
			doIdleIntention();
			return;
		}
		
		if (!_actor.getCast().canAttemptCast(target, skill))
			return;
		
		final boolean isShiftPressed = _currentIntention.isShiftPressed();
		if (_actor.getMove().maybeStartOffensiveFollow(target, skill.getCastRange()))
		{
			if (isShiftPressed)
				doIdleIntention();
			
			return;
		}
		
		if (skill.getHitTime() > 50)
		{
			_actor.getMove().stop();
			
			if (target != _actor)
				_actor.getPosition().setHeadingTo(target);
		}
		
		if (!_actor.getCast().canCast(target, skill, _currentIntention.isCtrlPressed(), _currentIntention.getItemObjectId()))
		{
			if (target != _actor)
				_actor.broadcastPacket(new MoveToPawn(_actor, target, (int) _actor.distance3D(target)));
			
			doIdleIntention();
			return;
		}
		
		_actor.getCast().doCast(skill, target, null);
	}
	
	@Override
	protected void thinkFlee()
	{
		if (_actor.isMovementDisabled())
		{
			doIdleIntention();
			return;
		}
		
		final Creature target = _currentIntention.getFinalTarget();
		if (_actor == target)
		{
			doIdleIntention();
			return;
		}
		
		final int distance = _currentIntention.getItemObjectId();
		if (distance < 10)
		{
			doIdleIntention();
			return;
		}
		
		_actor.fleeFrom(target, distance);
	}
	
	@Override
	protected void thinkFollow()
	{
		clientActionFailed();
		
		if (_actor.denyAiAction() || _actor.isMovementDisabled())
		{
			doIdleIntention();
			return;
		}
		
		final Creature target = _currentIntention.getFinalTarget();
		if (_actor == target)
		{
			doIdleIntention();
			return;
		}
		
		final boolean isShiftPressed = _currentIntention.isShiftPressed();
		if (isShiftPressed)
		{
			doIdleIntention();
			return;
		}
		
		_actor.getMove().maybeStartFriendlyFollow(target, 70);
	}
	
	@Override
	protected void thinkMoveTo()
	{
		if (_actor.denyAiAction() || _actor.isMovementDisabled())
		{
			doIdleIntention();
			clientActionFailed();
			return;
		}
		
		
		
		_actor.getMove().maybeMoveToLocation(_currentIntention.getLoc(), 0, true, false);
	}
	
	@Override
	protected ItemInstance thinkPickUp()
	{
		clientActionFailed();
		
		if (_actor.denyAiAction() || _actor.isSitting())
		{
			doIdleIntention();
			return null;
		}
		
		final WorldObject target = World.getInstance().getObject(_currentIntention.getItemObjectId());
		if (!(target instanceof ItemInstance item) || isTargetLost(target))
		{
			doIdleIntention();
			return null;
		}
		
		if (item.getLocation() != ItemLocation.VOID)
		{
			doIdleIntention();
			return null;
		}
		
		final boolean isShiftPressed = _currentIntention.isShiftPressed();
		if (_actor.getMove().maybeMoveToLocation(target.getPosition(), 36, false, isShiftPressed))
		{
			if (isShiftPressed)
				doIdleIntention();
			
			return null;
		}
		
		doIdleIntention();
		
		return item;
	}
	
	public synchronized void tryToAttack(Creature target, boolean isCtrlPressed, boolean isShiftPressed)
	{
		if (_actor.denyAiAction() || _actor.isAfraid())
		{
			clientActionFailed();
			return;
		}
		
		if (_actor.getAttack().isAttackingNow() || _actor.getCast().isCastingNow() || _actor.isSittingNow() || _actor.isStandingNow() || canScheduleAfter(_currentIntention.getType(), IntentionType.ATTACK))
		{
			getNextIntention().updateAsAttack(target, isCtrlPressed, isShiftPressed, true);
			clientActionFailed();
			return;
		}
		
		if (target instanceof Playable)
		{
			final Player targetPlayer = target.getActingPlayer();
			final Player actorPlayer = _actor.getActingPlayer();
			
			if (!isCtrlPressed && !target.isInsideZone(ZoneId.PVP))
			{
				if (targetPlayer.getProtectionBlessing() && (actorPlayer.getStatus().getLevel() - targetPlayer.getStatus().getLevel()) >= 10 && actorPlayer.getKarma() > 0)
				{
					actorPlayer.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
					clientActionFailed();
					return;
				}
				
				if (actorPlayer.getProtectionBlessing() && (targetPlayer.getStatus().getLevel() - actorPlayer.getStatus().getLevel()) >= 10 && targetPlayer.getKarma() > 0)
				{
					actorPlayer.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
					clientActionFailed();
					return;
				}
			}
			
			if (!isCtrlPressed && targetPlayer.isCursedWeaponEquipped() && actorPlayer.getStatus().getLevel() <= 20)
			{
				actorPlayer.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
				clientActionFailed();
				return;
			}
			
			if (!isCtrlPressed && actorPlayer.isCursedWeaponEquipped() && targetPlayer.getStatus().getLevel() <= 20)
			{
				actorPlayer.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
				clientActionFailed();
				return;
			}
			
			if (actorPlayer != null && targetPlayer != null && actorPlayer != targetPlayer)
				actorPlayer.updatePvPStatus(target);
		}
		
		doAttackIntention(target, isCtrlPressed, isShiftPressed, true);
	}
	
	public synchronized void tryToAttack(Creature target)
	{
		tryToAttack(target, false, false);
	}
	
	public synchronized void tryToCast(Creature target, L2Skill skill, boolean isCtrlPressed, boolean isShiftPressed, int itemObjectId)
	{
		if (_actor.denyAiAction())
		{
			clientActionFailed();
			return;
		}
		
		final Creature finalTarget = skill.getFinalTarget(_actor, target);
		if (finalTarget == null || !_actor.getCast().canAttemptCast(finalTarget, skill))
		{
			clientActionFailed();
			return;
		}
		
		if (skill != null && skill.isOffensive())
		{
			final Player actorPlayer = _actor.getActingPlayer();
			final Player targetPlayer = finalTarget.getActingPlayer();
			if (actorPlayer != null && targetPlayer != null && actorPlayer != targetPlayer)
				actorPlayer.updatePvPStatus(finalTarget);
		}
		
		if (_actor.getAttack().isAttackingNow() || _actor.getCast().isCastingNow() || _actor.isSittingNow() || _actor.isStandingNow() || canScheduleAfter(_currentIntention.getType(), IntentionType.CAST))
		{
			getNextIntention().updateAsCast(_actor, target, skill, isCtrlPressed, isShiftPressed, itemObjectId, true);
			clientActionFailed();
			return;
		}
		
		doCastIntention(target, skill, isCtrlPressed, isShiftPressed, itemObjectId, true);
	}
	
	public synchronized void tryToCast(Creature target, L2Skill skill)
	{
		tryToCast(target, skill, false, false, 0);
	}
	
	public synchronized void tryToCast(Creature target, int id, int level)
	{
		final L2Skill skill = SkillTable.getInstance().getInfo(id, level);
		if (skill != null)
			tryToCast(target, skill, false, false, 0);
	}
	
	public synchronized void tryToFollow(Creature target, boolean isShiftPressed)
	{
		if (_actor == target || _actor.denyAiAction())
		{
			clientActionFailed();
			return;
		}
		
		if (_actor.getAttack().isAttackingNow() || _actor.getCast().isCastingNow() || _actor.isSittingNow() || _actor.isStandingNow() || canScheduleAfter(_currentIntention.getType(), IntentionType.FOLLOW))
		{
			getNextIntention().updateAsFollow(target, isShiftPressed);
			clientActionFailed();
			return;
		}
		
		doFollowIntention(target, isShiftPressed);
	}
	
	public synchronized void tryToIdle()
	{
		if (_actor.denyAiAction())
		{
			clientActionFailed();
			return;
		}
		
		if (_actor.getAttack().isAttackingNow() || _actor.getCast().isCastingNow() || _actor.isSittingNow() || _actor.isStandingNow() || canScheduleAfter(_currentIntention.getType(), IntentionType.IDLE))
		{
			getNextIntention().updateAsIdle();
			clientActionFailed();
			return;
		}
		
		doIdleIntention();
	}
	
	public synchronized void tryToInteract(WorldObject target, boolean isCtrlPressed, boolean isShiftPressed)
	{
		if (_actor.denyAiAction())
		{
			clientActionFailed();
			return;
		}
		
		if (_actor.getAttack().isAttackingNow() || _actor.getCast().isCastingNow() || _actor.isSittingNow() || _actor.isStandingNow() || canScheduleAfter(_currentIntention.getType(), IntentionType.INTERACT))
		{
			getNextIntention().updateAsInteract(target, isCtrlPressed, isShiftPressed);
			clientActionFailed();
			return;
		}
		
		doInteractIntention(target, isCtrlPressed, isShiftPressed);
	}
	
	public synchronized void tryToMoveTo(Location loc, Boat boat)
	{
		if (_actor.denyAiAction())
		{
			clientActionFailed();
			return;
		}
		
		if (_actor.getAttack().isAttackingNow() || _actor.getCast().isCastingNow() || _actor.isSittingNow() || _actor.isStandingNow() || canScheduleAfter(_currentIntention.getType(), IntentionType.MOVE_TO))
		{
			getNextIntention().updateAsMoveTo(loc, boat);
			clientActionFailed();
			return;
		}
		
		doMoveToIntention(loc, boat);
	}
	
	public synchronized void tryToPickUp(int itemObjectId, boolean isShiftPressed)
	{
		if (_actor.denyAiAction())
		{
			clientActionFailed();
			return;
		}
		
		if (_actor.getAttack().isAttackingNow() || _actor.getCast().isCastingNow() || _actor.isSittingNow() || _actor.isStandingNow() || canScheduleAfter(_currentIntention.getType(), IntentionType.PICK_UP))
		{
			getNextIntention().updateAsPickUp(itemObjectId, isShiftPressed);
			clientActionFailed();
			return;
		}
		
		doPickUpIntention(itemObjectId, isShiftPressed);
	}
	
	public synchronized void tryToSit(WorldObject target)
	{
		if (_actor.denyAiAction())
		{
			clientActionFailed();
			return;
		}
		
		if (_actor.getAttack().isAttackingNow() || _actor.getCast().isCastingNow() || _actor.isSittingNow() || _actor.isStandingNow() || canScheduleAfter(_currentIntention.getType(), IntentionType.SIT))
		{
			getNextIntention().updateAsSit(target);
			return;
		}
		
		doSitIntention(target);
	}
	
	public synchronized void tryToStand()
	{
		if (_actor.denyAiAction())
		{
			clientActionFailed();
			return;
		}
		
		if (_actor.getAttack().isAttackingNow() || _actor.getCast().isCastingNow() || _actor.isSittingNow() || _actor.isStandingNow() || canScheduleAfter(_currentIntention.getType(), IntentionType.STAND))
		{
			getNextIntention().updateAsStand();
			return;
		}
		
		doStandIntention();
	}
	
	public synchronized void tryToUseItem(int itemObjectId)
	{
		if (_actor.denyAiAction())
		{
			clientActionFailed();
			return;
		}
		
		if (_actor.getAttack().isAttackingNow() || _actor.getCast().isCastingNow() || _actor.isSittingNow() || _actor.isStandingNow() || canScheduleAfter(_currentIntention.getType(), IntentionType.USE_ITEM))
		{
			getNextIntention().updateAsUseItem(itemObjectId);
			return;
		}
		
		doUseItemIntention(itemObjectId);
	}
}
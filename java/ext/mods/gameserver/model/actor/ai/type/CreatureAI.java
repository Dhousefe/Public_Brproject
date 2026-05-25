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
import ext.mods.commons.pool.ThreadPool;
import ext.mods.gameserver.enums.AiEventType;
import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.enums.items.WeaponType;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.serverpackets.Die;
import ext.mods.gameserver.network.serverpackets.MoveToLocation;
import ext.mods.gameserver.network.serverpackets.MoveToPawn;
import ext.mods.gameserver.network.serverpackets.SocialAction;
import ext.mods.gameserver.skills.L2Skill;

public class CreatureAI<T extends Creature> extends AbstractAI<T>
{
	public CreatureAI(T actor)
	{
		super(actor);
	}
	
	@Override
	protected void onEvtFinishedAttack()
	{
        if (_actor instanceof Npc npc)
        {
            final Creature target = _currentIntention.getFinalTarget();
            if (_currentIntention.getType() == IntentionType.ATTACK && target != null && !target.isAlikeDead())
            {
                final int aggroRange = npc.getTemplate().getAggroRange() > 0 ? npc.getTemplate().getAggroRange() : npc.getSeeRange();
                if (npc.isIn3DRadius(target, aggroRange + 100))
                {
                    if (!npc.knows(target))
                        npc.refreshKnownlist();
                    
                    ThreadPool.schedule(() -> {
                        if (_currentIntention.getType() == IntentionType.ATTACK &&
                            _currentIntention.getFinalTarget() == target &&
                            !target.isAlikeDead() &&
                            npc.isIn3DRadius(target, aggroRange + 100))
                        {
                            notifyEvent(AiEventType.THINK, null, null);
                        }
                    }, 100);
                    return;
                }
            }
        }
		
		if (_nextIntention.isBlank())
			notifyEvent(AiEventType.THINK, null, null);
		else
			doIntention(_nextIntention);
	}
	
	@Override
	protected void onEvtFinishedAttackBow()
	{
		if (!_nextIntention.isBlank())
			doIntention(_nextIntention);
	}
	
	@Override
	protected void onEvtBowAttackReuse()
	{
		if (_nextIntention.isBlank())
			notifyEvent(AiEventType.THINK, null, null);
	}
	
	@Override
	protected void onEvtFinishedCasting()
	{
		if (_nextIntention.isBlank())
			doIdleIntention();
		else
			doIntention(_nextIntention);
	}
	
	@Override
	protected void onEvtArrived()
	{
		if (_currentIntention.getType() == IntentionType.FOLLOW)
			return;
		
		if (_nextIntention.isBlank())
		{
			if (_currentIntention.getType() == IntentionType.MOVE_TO)
				doIdleIntention();
			else
				notifyEvent(AiEventType.THINK, null, null);
		}
		else
			doIntention(_nextIntention);
	}
	
	@Override
	protected void onEvtArrivedBlocked()
	{
		_actor.broadcastPacket(new MoveToLocation(_actor, _actor.getPosition()));
	}
	
	@Override
	protected void onEvtDead()
	{
		stopAITask();
		
		_actor.broadcastPacket(new Die(_actor));
		
		stopAttackStance();
		
		doIdleIntention();
	}
	
	@Override
	protected void onEvtTeleported()
	{
		doIdleIntention();
	}
	
	@Override
	public void thinkAttack() {
		if (_actor.denyAiAction()) {
			return;
		}

		final Creature target = _currentIntention.getFinalTarget();
		if (target == null || target.isAlikeDead() || !_actor.knows(target)) {
			doIdleIntention();
			return;
		}

		final int attackRange = _actor.getStatus().getPhysicalAttackRange();
		final int totalRange = (int) (attackRange + _actor.getCollisionRadius() + target.getCollisionRadius());
		final double dist = _actor.distance2D(target);
		if (dist > totalRange) {
			_actor.getMove().maybeStartOffensiveFollow(target, attackRange);
			return;
		}

		if (_actor.getAttack().isAttackingNow() || _actor.getAttack().isBowCoolingDown()) {
			return;
		}

		if (!_actor.getAttack().canAttack(target)) {
			if (_actor instanceof Npc && dist <= totalRange + 100) {
				_actor.getMove().startOffensiveFollow(target, attackRange);
			} else {
				_actor.getMove().maybeStartOffensiveFollow(target, attackRange);
			}
			return;
		}

		
		if (_actor.isMoving()) {
			_actor.getMove().stop();
		}

		_actor.getPosition().setHeadingTo(target);

		_actor.rechargeShots(true, false);
		_actor.getAttack().doAttack(target);
	}
	
	@Override
	protected void thinkCast()
	{
		if (_actor.denyAiAction() || _actor.getAllSkillsDisabled() || _actor.getCast().isCastingNow())
			return;
		
		final Creature target = _currentIntention.getFinalTarget();
		final L2Skill skill = _currentIntention.getSkill();
		
		if (isTargetLost(target, skill))
			return;
		
		if (!_actor.getCast().canAttemptCast(target, skill))
			return;
		
		if (_actor.getMove().maybeStartOffensiveFollow(target, skill.getCastRange()))
		{
			_actor.setWalkOrRun(true);
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
			if (!skill.isToggle())
				doIdleIntention();
			
			if (target != _actor && _currentIntention.canMoveToTarget())
				_actor.broadcastPacket(new MoveToPawn(_actor, target, (int) _actor.distance3D(target)));
			
			return;
		}
		
		_actor.getCast().doCast(skill, target, null);
	}
	
	@Override
	protected void thinkFakeDeath()
	{
	}
	
	@Override
	protected void thinkFlee()
	{
		if (_actor.isMovementDisabled())
			return;
		
		final Creature target = _currentIntention.getFinalTarget();
		if (_actor == target)
			return;
		
		final int distance = _currentIntention.getItemObjectId();
		if (distance < 10)
			return;
		
		final double passedDistance = _actor.distance2D(_currentIntention.getLoc());
		if (passedDistance >= distance)
			return;
		
		_actor.fleeFrom(target, distance);
	}
	
	@Override
	protected void thinkFollow()
	{
		if (_actor.denyAiAction() || _actor.isMovementDisabled())
			return;
		
		final Creature target = _currentIntention.getFinalTarget();
		if (_actor == target)
			return;
		
		final boolean isShiftPressed = _currentIntention.isShiftPressed();
		if (isShiftPressed)
			return;
		
		_actor.getMove().maybeStartFriendlyFollow(target, 70);
	}
	
	@Override
	protected void thinkIdle()
	{
		_actor.getMove().stop();
	}
	
	@Override
	protected void thinkInteract()
	{
	}
	
	@Override
	protected void thinkMoveRoute()
	{
	}
	
	@Override
	protected void thinkMoveTo()
	{
		if (_actor.denyAiAction() || _actor.isMovementDisabled())
			return;
		
		_actor.getMove().maybeMoveToLocation(_currentIntention.getLoc(), 0, true, false);
	}
	
	@Override
	protected void thinkNothing()
	{
	}
	
	@Override
	protected ItemInstance thinkPickUp()
	{
		return null;
	}
	
	@Override
	protected void thinkSit()
	{
	}
	
	@Override
	protected void thinkSocial()
	{
		if (_actor.denyAiAction())
			return;
		
		_actor.broadcastPacket(new SocialAction(_actor, _currentIntention.getItemObjectId()));
	}
	
	@Override
	protected void thinkStand()
	{
	}
	
	@Override
	protected void thinkUseItem()
	{
	}
	
	@Override
	protected void thinkWander()
	{
	}
	
	@Override
	protected void onEvtSatDown(WorldObject target)
	{
	}
	
	@Override
	protected void onEvtStoodUp()
	{
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker)
	{
		startAttackStance();
	}
	
	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{
	}
	
	@Override
	protected void onEvtEvaded(Creature attacker)
	{
	}
	
	@Override
	protected void onEvtOwnerAttacked(Creature attacker)
	{
	}
	
	@Override
	protected void onEvtCancel()
	{
	}
	
	public boolean getFollowStatus()
	{
		return false;
	}
	
	public void setFollowStatus(boolean followStatus)
	{
	}
	
	public boolean canDoInteract(WorldObject target)
	{
		return false;
	}
	
	public boolean canAttemptInteract()
	{
		return false;
	}
}
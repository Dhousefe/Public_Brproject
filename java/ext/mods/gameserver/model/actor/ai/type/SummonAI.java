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
import ext.mods.gameserver.data.manager.CursedWeaponManager;
import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.enums.items.ArmorType;
import ext.mods.gameserver.enums.items.EtcItemType;
import ext.mods.gameserver.enums.items.WeaponType;
import ext.mods.gameserver.handler.IItemHandler;
import ext.mods.gameserver.handler.ItemHandler;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.Summon;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.AutoAttackStart;
import ext.mods.gameserver.network.serverpackets.AutoAttackStop;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.taskmanager.AttackStanceTaskManager;
import ext.mods.gameserver.taskmanager.ItemsOnGroundTaskManager;

public class SummonAI extends PlayableAI<Summon>
{
	private volatile boolean _followOwner = true;
	
	public SummonAI(Summon summon)
	{
		super(summon);
	}
	
	
	
	
	
	@Override
	protected void onEvtFinishedCasting()
	{
		if (_nextIntention.isBlank())
		{
			if (_previousIntention.getType() == IntentionType.ATTACK)
				doIntention(_previousIntention);
			else
				doIdleIntention();
		}
		else
			doIntention(_nextIntention);
	}
	
	@Override
	public void onEvtAttacked(Creature attacker)
	{
		super.onEvtAttacked(attacker);
		
		_actor.getMove().avoidAttack(attacker);
	}
	
	@Override
	protected void onEvtEvaded(Creature attacker)
	{
		super.onEvtEvaded(attacker);
		
		_actor.getMove().avoidAttack(attacker);
	}
	
	@Override
	public void thinkAttack()
	{
		if (_actor.denyAiAction())
		{
			doIdleIntention();
			return;
		}
		
		final Creature target = _currentIntention.getFinalTarget();
		if (isTargetLost(target))
		{
			doIdleIntention();
			return;
		}
		
		final boolean isShiftPressed = _currentIntention.isShiftPressed();
		if (_actor.getMove().maybeStartOffensiveFollow(target, _actor.getStatus().getPhysicalAttackRange()))
		{
			if (isShiftPressed)
				doIdleIntention();
			
			return;
		}
		
		_actor.getMove().stop();
		
		if (!_actor.getAttack().canAttack(target))
		{
			doIdleIntention();
			return;
		}
		
		_actor.getAttack().doAttack(target);
		if (!Config.ATTACK_PTS)
			setNextIntention(_currentIntention);
	}
	
	
	
	@Override
	protected void thinkInteract()
	{
		final WorldObject target = _currentIntention.getTarget();
		if (isTargetLost(target))
		{
			doIdleIntention();
			return;
		}
		
		final boolean isShiftPressed = _currentIntention.isShiftPressed();
		if (_actor.getMove().maybeMoveToLocation(target.getPosition(), _actor.getStatus().getPhysicalAttackRange(), true, isShiftPressed))
		{
			if (isShiftPressed)
				doIdleIntention();
			
			return;
		}
	}
	
	@Override
	protected ItemInstance thinkPickUp()
	{
		final ItemInstance item = super.thinkPickUp();
		if (item == null)
			return null;
		
		if (CursedWeaponManager.getInstance().isCursed(item.getItemId()))
		{
			_actor.getOwner().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FAILED_TO_PICKUP_S1).addItemName(item.getItemId()));
			return null;
		}
		
		if (item.getItem().getItemType() == EtcItemType.ARROW || item.getItem().getItemType() == EtcItemType.SHOT)
		{
			_actor.getOwner().sendPacket(SystemMessageId.ITEM_NOT_FOR_PETS);
			return null;
		}
		
		synchronized (item)
		{
			if (!item.isVisible())
				return null;
			
			if (!_actor.getInventory().validateCapacity(item))
			{
				_actor.getOwner().sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_ANY_MORE_ITEMS);
				return null;
			}
			
			if (!_actor.getInventory().validateWeight(item.getWeight()))
			{
				_actor.getOwner().sendPacket(SystemMessageId.UNABLE_TO_PLACE_ITEM_YOUR_PET_IS_TOO_ENCUMBERED);
				return null;
			}
			
			if (item.getOwnerId() != 0 && !_actor.getOwner().isLooterOrInLooterParty(item.getOwnerId()))
			{
				if (item.getItemId() == 57)
					_actor.getOwner().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FAILED_TO_PICKUP_S1_ADENA).addNumber(item.getCount()));
				else if (item.getCount() > 1)
					_actor.getOwner().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FAILED_TO_PICKUP_S2_S1_S).addItemName(item.getItemId()).addNumber(item.getCount()));
				else
					_actor.getOwner().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FAILED_TO_PICKUP_S1).addItemName(item.getItemId()));
				
				return null;
			}
			
			if (item.hasDropProtection())
				item.removeDropProtection();
			
			item.pickupMe(_actor);
			
			ItemsOnGroundTaskManager.getInstance().remove(item);
		}
		
		if (item.getItemType() == EtcItemType.HERB)
		{
			final IItemHandler handler = ItemHandler.getInstance().getHandler(item.getEtcItem());
			if (handler != null)
				handler.useItem(_actor, item, false);
			
			item.destroyMe();
			_actor.getStatus().broadcastStatusUpdate();
		}
		else
		{
			if (item.getItemType() instanceof ArmorType || item.getItemType() instanceof WeaponType)
			{
				SystemMessage sm;
				if (item.getEnchantLevel() > 0)
					sm = SystemMessage.getSystemMessage(SystemMessageId.ATTENTION_S1_PET_PICKED_UP_S2_S3).addCharName(_actor.getOwner()).addNumber(item.getEnchantLevel()).addItemName(item.getItemId());
				else
					sm = SystemMessage.getSystemMessage(SystemMessageId.ATTENTION_S1_PET_PICKED_UP_S2).addCharName(_actor.getOwner()).addItemName(item.getItemId());
				
				_actor.getOwner().broadcastPacketInRadius(sm, 1400);
			}
			
			if (_actor.getOwner().isInParty())
				_actor.getOwner().getParty().distributeItem(_actor.getOwner(), item, _actor);
			else
				_actor.addItem(item, true);
		}
		
		return item;
	}
	
	@Override
	public void startAttackStance()
	{
		if (!AttackStanceTaskManager.getInstance().isInAttackStance(_actor.getOwner()))
		{
			_actor.broadcastPacket(new AutoAttackStart(_actor.getObjectId()));
			_actor.getOwner().broadcastPacket(new AutoAttackStart(_actor.getOwner().getObjectId()));
		}
		
		AttackStanceTaskManager.getInstance().add(_actor.getOwner());
	}
	
	@Override
	public void stopAttackStance()
	{
		_actor.broadcastPacket(new AutoAttackStop(_actor.getObjectId()));
	}
	
	public void switchFollowStatus()
	{
		setFollowStatus(!_followOwner);
	}
	
	@Override
	public void setFollowStatus(boolean state)
	{
		_followOwner = state;
		
		if (_followOwner)
			tryToFollow(_actor.getOwner(), false);
		else
			tryToIdle();
	}
	
	@Override
	public boolean getFollowStatus()
	{
		return _followOwner;
	}
	
	@Override
	public boolean isTargetLost(WorldObject object)
	{
		final boolean isTargetLost = super.isTargetLost(object);
		if (isTargetLost)
			setFollowStatus(true);
		
		return isTargetLost;
	}
	
	@Override
	public boolean isTargetLost(WorldObject object, L2Skill skill)
	{
		final boolean isTargetLost = super.isTargetLost(object, skill);
		if (isTargetLost)
			setFollowStatus(true);
		
		return isTargetLost;
	}

	    @Override
   protected void thinkIdle() {
       if (this._followOwner) {
         this.doFollowIntention(this._actor.getOwner(), false);
       } else {
          this.prepareIntention();
          this._currentIntention.updateAsIdle();
          this._actor.getMove().stop();
       }
    }

    @Override
    protected void onEvtTeleported() {
       this._followOwner = true;
       this.doFollowIntention(this._actor.getOwner(), false);
    }

    @Override
    protected void thinkFollow() {
       if (!this._actor.denyAiAction() && !this._actor.isMovementDisabled()) {
          Creature target = this._currentIntention.getFinalTarget();
          if (this._actor != target) {
             Player owner = this._actor.getOwner();
             if (owner != null) {
                double distance = this._actor.distance2D(owner);
                if (distance > 1500 || owner.isTeleporting()) {
                   this._actor.teleportTo(owner.getPosition(), 50);
                   return;
                }
                if (this._actor.getMove().getGeoPathFailCount() >= 3 && !this._actor.isIn3DRadius(owner, 300)) {
                   this._actor.teleportTo(owner.getPosition(), 50);
                   this._actor.getMove().resetGeoPathFailCount();
                   return;
                }
             }
             boolean isShiftPressed = this._currentIntention.isShiftPressed();
             if (!isShiftPressed) {
                this._actor.getMove().maybeStartFriendlyFollow(target, 70);
             }
          }
       }
    }
}
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
package ext.mods.gameserver.model.actor.attack;

import ext.mods.commons.logging.CLogger;
import ext.mods.extensions.listener.manager.CreatureListenerManager;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.kind.Weapon;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.Config;

/**
 * This class groups all attack data related to a {@link Creature}.
 */
public class PlayerAttack extends PlayableAttack<Player>
{
	private static final CLogger LOGGER = new CLogger(PlayerAttack.class.getName());
	
	public PlayerAttack(Player creature)
	{
		super(creature);
	}
	
	@Override
	public void doAttack(Creature target)
	{
		if (isMeleeDebug())
		{
			LOGGER.info("[MeleeDebug][PlayerAttack][doAttack] actor={} target={} dist={}",
				_actor.getObjectId(), target.getObjectId(), _actor.distance2D(target));
		}
		super.doAttack(target);
		
		_actor.clearRecentFakeDeath();
	}
	
	@Override
	public boolean canAttack(Creature target)
	{
		if (isMeleeDebug())
		{
			LOGGER.info("[MeleeDebug][PlayerAttack][canAttack] actor={} target={} dist={} range={}",
				_actor.getObjectId(), target.getObjectId(), _actor.distance2D(target), _actor.getStatus().getPhysicalAttackRange());
		}
		
		if (!super.canAttack(target))
			return false;
		
		final Weapon weaponItem = _actor.getActiveWeaponItem();
		
		switch (weaponItem.getItemType())
		{
			case FISHINGROD:
				_actor.sendPacket(SystemMessageId.CANNOT_ATTACK_WITH_FISHING_POLE);
				return false;
			
			case BOW:
				if (!_actor.checkAndEquipArrows())
				{
					_actor.sendPacket(SystemMessageId.NOT_ENOUGH_ARROWS);
					return false;
				}
				
				final int mpConsume = weaponItem.getMpConsume();
				if (mpConsume > 0 && mpConsume > _actor.getStatus().getMp())
				{
					_actor.sendPacket(SystemMessageId.NOT_ENOUGH_MP);
					return false;
				}
		}
		
		CreatureListenerManager.getInstance().notifyAttack(_actor, target);
		return true;
	}
	
	private boolean isMeleeDebug()
	{
		return Config.DEBUG_MELEE_ATTACK && _actor.getStatus().getPhysicalAttackRange() <= 80;
	}
	
	@Override
	public void stop()
	{
		super.stop();
		
		_actor.getAI().clientActionFailed();
	}
}
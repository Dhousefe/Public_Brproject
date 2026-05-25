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

import ext.mods.Config;
import ext.mods.commons.logging.CLogger;
import ext.mods.extensions.listener.manager.CreatureListenerManager;
import ext.mods.gameserver.enums.AiEventType;
import ext.mods.gameserver.enums.EventHandler;
import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.ai.Intention;
import ext.mods.gameserver.scripting.Quest;

/**
 * This class groups all attack data related to a {@link Npc}.
 */
public class NpcAttack extends CreatureAttack<Npc>
{
	private static final CLogger LOGGER = new CLogger(NpcAttack.class.getName());
	public NpcAttack(Npc actor)
	{
		super(actor);
	}
	
	@Override
	public boolean canAttack(Creature target) {
		if (!super.canAttack(target)) {
			return false;
		}

		int attackRange = (int) _actor.getStatus().getPhysicalAttackRange();
		int totalAttackRange = (int) (attackRange + _actor.getCollisionRadius() + target.getCollisionRadius());
		
		final double dist = _actor.distance2D(target);
		
		if (dist > totalAttackRange) {
			
			if (_actor.getAI().getCurrentIntention().canMoveToTarget()) {
			
				_actor.getMove().maybeStartOffensiveFollow(target, attackRange);
			}
			if (Config.DEBUG_MELEE_ATTACK && attackRange <= 200) {
				LOGGER.info("[MeleeDebug] canAttack: outOfRange npc={} target={} dist={} range={}", _actor.getObjectId(), target.getObjectId(), dist, totalAttackRange);
			}
			return false;
		}

		_actor.getPosition().setHeadingTo(target);
		CreatureListenerManager.getInstance().notifyAttack(_actor, target);
		
		final boolean canAttackResult = !target.isFakeDeath();
		if (Config.DEBUG_MELEE_ATTACK && attackRange <= 200) {
			LOGGER.info("[MeleeDebug] canAttack: result={} npc={} target={} dist={}", canAttackResult, _actor.getObjectId(), target.getObjectId(), dist);
		}
		return canAttackResult;
	}
	
	@Override
	protected void onFinishedAttackBow(Creature mainTarget)
	{
		for (Quest quest : _actor.getTemplate().getEventQuests(EventHandler.ATTACK_FINISHED))
			quest.onAttackFinished(_actor, mainTarget);
		
		super.onFinishedAttackBow(mainTarget);
	}
	
	@Override
	protected void onFinishedAttack(Creature mainTarget)
	{
		for (Quest quest : _actor.getTemplate().getEventQuests(EventHandler.ATTACK_FINISHED))
			quest.onAttackFinished(_actor, mainTarget);
		
		super.onFinishedAttack(mainTarget);
	}
}
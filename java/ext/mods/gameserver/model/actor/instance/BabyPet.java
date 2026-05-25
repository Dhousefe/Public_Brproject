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
package ext.mods.gameserver.model.actor.instance;

import java.util.concurrent.Future;

import ext.mods.commons.pool.ThreadPool;
import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.skills.L2Skill;

/**
 * A BabyPet can heal his owner. It got 2 heal power, weak or strong.
 * <ul>
 * <li>If the owner's HP is more than 80%, do nothing.</li>
 * <li>If the owner's HP is under 15%, have 75% chances of using a strong heal.</li>
 * <li>Otherwise, have 25% chances for weak heal.</li>
 * </ul>
 */
public final class BabyPet extends Pet
{
	private Future<?> _castTask;
	
	public BabyPet(int objectId, NpcTemplate template, Player owner, ItemInstance control)
	{
		super(objectId, template, owner, control);
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		startCastTask();
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		if (!super.doDie(killer))
			return false;
		
		stopCastTask();
		
		return true;
	}
	
	@Override
	public synchronized void unSummon(Player owner)
	{
		stopCastTask();
		
		super.unSummon(owner);
	}
	
	@Override
	public void doRevive()
	{
		super.doRevive();
		
		startCastTask();
	}
	
	@Override
	public final int getSkillLevel(int skillId)
	{
		if (getStatus().getLevel() < 70)
			return Math.max(1, getStatus().getLevel() / 10);
		
		return Math.min(12, 7 + ((getStatus().getLevel() - 70) / 5));
	}
	
	private final void startCastTask()
	{
		if (_castTask == null && !isDead())
			_castTask = ThreadPool.scheduleAtFixedRate(this::castSkill, 3000, 1000);
	}
	
	private final void stopCastTask()
	{
		if (_castTask != null)
		{
			_castTask.cancel(false);
			_castTask = null;
		}
	}
	
	private void castSkill()
	{
		final Player owner = getOwner();
		if (owner == null || owner.isDead() || owner.isInvul())
			return;
		
		final double hpRatio = owner.getStatus().getHpRatio();
		
		if (Rnd.get(100) <= 25)
		{
			final L2Skill playfulHeal = SkillTable.getInstance().getInfo(4717, getSkillLevel(4717));
			if (!isSkillDisabled(playfulHeal) && getStatus().getMp() >= playfulHeal.getMpConsume() && hpRatio < 0.8)
			{
				getAI().tryToCast(owner, playfulHeal);
				owner.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_USES_S1).addSkillName(playfulHeal));
				return;
			}
		}
		
		if (Rnd.get(100) <= 75)
		{
			final L2Skill urgentHeal = SkillTable.getInstance().getInfo(4718, getSkillLevel(4718));
			if (!isSkillDisabled(urgentHeal) && getStatus().getMp() >= urgentHeal.getMpConsume() && hpRatio < 0.15)
			{
				getAI().tryToCast(owner, urgentHeal);
				owner.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_USES_S1).addSkillName(urgentHeal));
				return;
			}
		}
	}
}
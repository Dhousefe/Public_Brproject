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

import ext.mods.gameserver.enums.actors.NpcRace;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.Summon;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.olympiad.OlympiadGameManager;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.SetSummonRemainTime;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.skills.l2skills.L2SkillSummon;
import ext.mods.gameserver.taskmanager.DecayTaskManager;

public class Servitor extends Summon
{
	private float _expPenalty = 0;
	
	private int _itemConsumeId = 0;
	private int _itemConsumeCount = 0;
	private int _itemConsumeSteps = 0;
	private int _totalLifeTime = 1200000;
	private int _timeLostIdle = 1000;
	private int _timeLostActive = 1000;
	private int _timeRemaining;
	private int _nextItemConsumeTime;
	private int _lastShownTimeRemaining;
	
	private Future<?> _summonLifeTask;
	
	public Servitor(int objectId, NpcTemplate template, Player owner, L2Skill skill)
	{
		super(objectId, template, owner);
		
		if (skill != null)
		{
			final L2SkillSummon summonSkill = (L2SkillSummon) skill;
			
			_itemConsumeId = summonSkill.getItemConsumeIdOT();
			_itemConsumeCount = summonSkill.getItemConsumeOT();
			_itemConsumeSteps = summonSkill.getItemConsumeSteps();
			_totalLifeTime = summonSkill.getTotalLifeTime();
			_timeLostIdle = summonSkill.getTimeLostIdle();
			_timeLostActive = summonSkill.getTimeLostActive();
		}
		_timeRemaining = _totalLifeTime;
		_lastShownTimeRemaining = _totalLifeTime;
		
		if (_itemConsumeId == 0 || _itemConsumeSteps == 0)
			_nextItemConsumeTime = -1;
		else
			_nextItemConsumeTime = _totalLifeTime - _totalLifeTime / (_itemConsumeSteps + 1);
		
		_summonLifeTask = ThreadPool.scheduleAtFixedRate(this::processLifeTime, 1000, 1000);
	}
	
	@Override
	public void addItem(ItemInstance item, boolean sendMessage)
	{
	}
	
	@Override
	public ItemInstance addItem(int itemId, int count, boolean sendMessage)
	{
		return null;
	}
	
	@Override
	public int getSummonType()
	{
		return 1;
	}
	
	@Override
	public void sendDamageMessage(Creature target, int damage, boolean mcrit, boolean pcrit, boolean miss)
	{
		if (miss || getOwner() == null)
			return;
		
		if (target.getObjectId() != getOwner().getObjectId())
		{
			if (pcrit || mcrit)
				sendPacket(SystemMessageId.CRITICAL_HIT_BY_SUMMONED_MOB);
			
			if (target.isInvul())
			{
				if (target.isParalyzed())
					sendPacket(SystemMessageId.OPPONENT_PETRIFIED);
				else
					sendPacket(SystemMessageId.ATTACK_WAS_BLOCKED);
			}
			else
				sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SUMMON_GAVE_DAMAGE_S1).addNumber(damage));
			
			if (getOwner().isInOlympiadMode() && target instanceof Player targetPlayer && targetPlayer.isInOlympiadMode() && targetPlayer.getOlympiadGameId() == getOwner().getOlympiadGameId())
				OlympiadGameManager.getInstance().notifyCompetitorDamage(getOwner(), damage);
		}
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		if (!super.doDie(killer))
			return false;
		
		if (_summonLifeTask != null)
		{
			_summonLifeTask.cancel(false);
			_summonLifeTask = null;
		}
		
		sendPacket(SystemMessageId.SERVITOR_PASSED_AWAY);
		
		DecayTaskManager.getInstance().add(this, getTemplate().getCorpseTime());
		return true;
	}
	
	@Override
	public void unSummon(Player owner)
	{
		if (_summonLifeTask != null)
		{
			_summonLifeTask.cancel(false);
			_summonLifeTask = null;
		}
		super.unSummon(owner);
	}
	
	@Override
	public boolean destroyItem(int objectId, int count, boolean sendMessage)
	{
		return getOwner().destroyItem(objectId, count, sendMessage);
	}
	
	@Override
	public boolean destroyItemByItemId(int itemId, int count, boolean sendMessage)
	{
		return getOwner().destroyItemByItemId(itemId, count, sendMessage);
	}
	
	@Override
	public boolean isUndead()
	{
		return getTemplate().getRace() == NpcRace.UNDEAD;
	}
	
	public void setExpPenalty(float expPenalty)
	{
		_expPenalty = expPenalty;
	}
	
	public float getExpPenalty()
	{
		return _expPenalty;
	}
	
	public int getItemConsumeCount()
	{
		return _itemConsumeCount;
	}
	
	public int getItemConsumeId()
	{
		return _itemConsumeId;
	}
	
	public int getItemConsumeSteps()
	{
		return _itemConsumeSteps;
	}
	
	public int getNextItemConsumeTime()
	{
		return _nextItemConsumeTime;
	}
	
	public int getTotalLifeTime()
	{
		return _totalLifeTime;
	}
	
	public int getTimeLostIdle()
	{
		return _timeLostIdle;
	}
	
	public int getTimeLostActive()
	{
		return _timeLostActive;
	}
	
	public int getTimeRemaining()
	{
		return _timeRemaining;
	}
	
	public void setNextItemConsumeTime(int value)
	{
		_nextItemConsumeTime = value;
	}
	
	public void decNextItemConsumeTime(int value)
	{
		_nextItemConsumeTime -= value;
	}
	
	public int decTimeRemaining(int value)
	{
		return _timeRemaining -= value;
	}
	
	public void addExpAndSp(int addToExp, int addToSp)
	{
		getOwner().addExpAndSp(addToExp, addToSp);
	}
	
	private void processLifeTime()
	{
		final double oldTimeRemaining = getTimeRemaining();
		
		final double newTimeRemaining = decTimeRemaining((isInCombat()) ? getTimeLostActive() : getTimeLostIdle());
		if (newTimeRemaining < 0)
			unSummon(getOwner());
		else if ((newTimeRemaining <= getNextItemConsumeTime()) && (oldTimeRemaining > getNextItemConsumeTime()))
		{
			decNextItemConsumeTime(getTotalLifeTime() / (getItemConsumeSteps() + 1));
			
			if (getItemConsumeCount() > 0 && getItemConsumeId() != 0 && !isDead() && !destroyItemByItemId(getItemConsumeId(), getItemConsumeCount(), true))
				unSummon(getOwner());
		}
		
		if ((_lastShownTimeRemaining - newTimeRemaining) > getTotalLifeTime() / 352)
		{
			sendPacket(new SetSummonRemainTime(getTotalLifeTime(), (int) newTimeRemaining));
			_lastShownTimeRemaining = (int) newTimeRemaining;
			updateEffectIcons();
		}
	}
}
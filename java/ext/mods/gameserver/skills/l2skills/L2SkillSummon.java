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
package ext.mods.gameserver.skills.l2skills;

import ext.mods.commons.data.StatSet;

import ext.mods.Config;
import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.xml.NpcData;
import ext.mods.gameserver.enums.items.ShotType;
import ext.mods.gameserver.enums.skills.SkillTargetType;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.idfactory.IdFactory;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.Summon;
import ext.mods.gameserver.model.actor.instance.Servitor;
import ext.mods.gameserver.model.actor.instance.SiegeSummon;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.location.SpawnLocation;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.skills.L2Skill;

public class L2SkillSummon extends L2Skill
{
	private final int _npcId;
	private final float _expPenalty;
	private final boolean _isCubic;
	
	private final int _activationTime;
	private final int _activationChance;
	
	private final int _summonTotalLifeTime;
	private final int _summonTimeLostIdle;
	private final int _summonTimeLostActive;
	
	private final int _itemConsumeTime;
	private final int _itemConsumeOT;
	private final int _itemConsumeIdOT;
	private final int _itemConsumeSteps;
	
	private static final int SUMMON_SOULLESS = 1278;
	private static final int LIFE_CUBIC_FOR_BEGINNERS = 4338;
	
	public L2SkillSummon(StatSet set)
	{
		super(set);
		
		_npcId = set.getInteger("npcId", 0);
		_expPenalty = set.getFloat("expPenalty", 0.f);
		_isCubic = set.getBool("isCubic", false);
		
		_activationTime = set.getInteger("activationtime", 8);
		_activationChance = set.getInteger("activationchance", 30);
		
		_summonTotalLifeTime = set.getInteger("summonTotalLifeTime", 1200000);
		_summonTimeLostIdle = set.getInteger("summonTimeLostIdle", 0);
		_summonTimeLostActive = set.getInteger("summonTimeLostActive", 0);
		
		_itemConsumeOT = set.getInteger("itemConsumeCountOT", 0);
		_itemConsumeIdOT = set.getInteger("itemConsumeIdOT", 0);
		_itemConsumeTime = set.getInteger("itemConsumeTime", 0);
		_itemConsumeSteps = set.getInteger("itemConsumeSteps", 0);
	}
	
	public boolean checkCondition(Creature creature)
	{
		if (creature instanceof Player player)
		{
			if (isCubic())
			{
				if (getTargetType() != SkillTargetType.SELF)
					return true;
				
				if (player.getCubicList().isFull())
				{
					player.sendPacket(SystemMessageId.CUBIC_SUMMONING_FAILED);
					return false;
				}
			}
			else
			{
				if (player.isInObserverMode())
					return false;
				
				if (player.getSummon() != null)
				{
					player.sendPacket(SystemMessageId.SUMMON_ONLY_ONE);
					return false;
				}
				
				if (player.getAttack().isAttackingNow())
				{
					player.sendPacket(SystemMessageId.YOU_CANNOT_SUMMON_IN_COMBAT);
					return false;
				}
				
				if (player.isInBoat())
				{
					player.sendPacket(SystemMessageId.NOT_CALL_PET_FROM_THIS_LOCATION);
					return false;
				}
			}
		}
		return super.checkCondition(creature, null, false);
	}
	
	@Override
	public void useSkill(Creature creature, WorldObject[] targets)
	{
		if (!(creature instanceof Player player) || player.isAlikeDead())
			return;
		
		if (_npcId == 0)
		{
			player.sendMessage("Summon skill " + getId() + " not described yet");
			return;
		}
		
		if (_isCubic)
		{
			int skillLevel = getLevel();
			if (getId() == LIFE_CUBIC_FOR_BEGINNERS)
				skillLevel = 8;
			else if (skillLevel > 100)
				skillLevel = Math.round(((getLevel() - 100) / 7) + 8);
			
			if (targets.length > 1)
			{
				for (WorldObject target : targets)
				{
					if (!(target instanceof Player targetPlayer))
						continue;
					
					targetPlayer.getCubicList().addOrRefreshCubic(_npcId, skillLevel, getPower(), _activationTime, _activationChance, _summonTotalLifeTime, (targetPlayer != player));
				}
			}
			else
				player.getCubicList().addOrRefreshCubic(_npcId, skillLevel, getPower(), _activationTime, _activationChance, _summonTotalLifeTime, false);
		}
		else
		{
			if (player.getSummon() != null || player.isMounted())
				return;
			
			final NpcTemplate summonTemplate = NpcData.getInstance().getTemplate(_npcId);
			if (summonTemplate == null)
			{
				LOGGER.warn("Couldn't properly spawn with id {} ; the template is missing.", _npcId);
				return;
			}
			
			Servitor summon;
			if (summonTemplate.isType("SiegeSummon"))
				summon = new SiegeSummon(IdFactory.getInstance().getNextId(), summonTemplate, player, this);
			else
				summon = new Servitor(IdFactory.getInstance().getNextId(), summonTemplate, player, this);
			
			player.setSummon(summon);
			
			summon.setName(summonTemplate.getName());
			summon.setTitle(player.getName());
			summon.setExpPenalty(_expPenalty);
			summon.getStatus().setMaxHpMp();
			summon.forceRunStance();
			
			final SpawnLocation spawnLoc = player.getPosition().clone();
			spawnLoc.addStrictOffset(Config.SUMMON_DRIFT_RANGE);
			spawnLoc.setHeadingTo(player.getPosition());
			spawnLoc.set(GeoEngine.getInstance().getValidLocation(player, spawnLoc));
			
			summon.spawnMe(spawnLoc);
			summon.getAI().setFollowStatus(true);
			
			if (getId() == SUMMON_SOULLESS)
				SkillTable.getInstance().getInfo(Summon.CONTRACT_PAYMENT, Math.clamp(getLevel() - 2, 1, 12)).getEffects(player, player);
		}
		
		player.setChargedShot(player.isChargedShot(ShotType.BLESSED_SPIRITSHOT) ? ShotType.BLESSED_SPIRITSHOT : ShotType.SPIRITSHOT, isStaticReuse());
	}
	
	public final boolean isCubic()
	{
		return _isCubic;
	}
	
	public final int getTotalLifeTime()
	{
		return _summonTotalLifeTime;
	}
	
	public final int getTimeLostIdle()
	{
		return _summonTimeLostIdle;
	}
	
	public final int getTimeLostActive()
	{
		return _summonTimeLostActive;
	}
	
	/**
	 * @return Returns the itemConsume count over time.
	 */
	public final int getItemConsumeOT()
	{
		return _itemConsumeOT;
	}
	
	/**
	 * @return Returns the itemConsumeId over time.
	 */
	public final int getItemConsumeIdOT()
	{
		return _itemConsumeIdOT;
	}
	
	public final int getItemConsumeSteps()
	{
		return _itemConsumeSteps;
	}
	
	/**
	 * @return Returns the itemConsume time in milliseconds.
	 */
	public final int getItemConsumeTime()
	{
		return _itemConsumeTime;
	}

	public final int getNpcId()
	{
		return _npcId;
	}
}
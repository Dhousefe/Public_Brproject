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
package ext.mods.gameserver.model.actor.container.player;

import java.util.concurrent.Future;

import ext.mods.commons.pool.ThreadPool;
import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.data.manager.FishingChampionshipManager;
import ext.mods.gameserver.data.xml.FishData;
import ext.mods.gameserver.enums.actors.MissionType;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.records.Fish;
import ext.mods.gameserver.model.spawn.Spawn;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ExFishingEnd;
import ext.mods.gameserver.network.serverpackets.ExFishingHpRegen;
import ext.mods.gameserver.network.serverpackets.ExFishingStart;
import ext.mods.gameserver.network.serverpackets.ExFishingStartCombat;
import ext.mods.gameserver.network.serverpackets.PlaySound;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.skills.AbstractEffect;

/**
 * This class handles all fishing aspects and variables.<br>
 * <br>
 * The fishing stance starts with a task named _lookingForFish. This task handles the waiting process, up to the caught time.<br>
 * <br>
 * Once a {@link Fish} is found, a new task occurs, named _fishCombat. The {@link Player} will play a minigame to reduce the resilience of the Fish. If he succeeds, he caught the Fish.
 */
public class FishingStance
{
	private final Player _fisher;
	
	private final Location _loc = new Location(0, 0, 0);
	
	private int _time;
	private int _stop;
	private int _goodUse;
	private int _anim;
	private int _mode;
	private int _deceptiveMode;
	
	private Future<?> _lookingForFish;
	private Future<?> _fishCombat;
	
	private boolean _thinking;
	
	private Fish _fish;
	private int _fishCurHp;
	
	private boolean _isUpperGrade;
	
	private int _lureId;
	private int _lureType;
	private boolean _isNightLure;
	
	public FishingStance(Player fisher)
	{
		_fisher = fisher;
	}
	
	/**
	 * @param group : The group based on lure type (beginner, normal, upper grade).
	 * @return a random {@link Fish} type, based on randomness and lure's group.
	 */
	private int getRandomFishType(int group)
	{
		int check = Rnd.get(100);
		int type = 1;
		switch (group)
		{
			case 0:
				switch (_lureId)
				{
					case 7807:
						if (check <= 54)
							type = 5;
						else if (check <= 77)
							type = 4;
						else
							type = 6;
						break;
					
					case 7808:
						if (check <= 54)
							type = 4;
						else if (check <= 77)
							type = 6;
						else
							type = 5;
						break;
					
					case 7809:
						if (check <= 54)
							type = 6;
						else if (check <= 77)
							type = 5;
						else
							type = 4;
						break;
					
					case 8486:
						if (check <= 33)
							type = 4;
						else if (check <= 66)
							type = 5;
						else
							type = 6;
						break;
				}
				break;
			
			case 1:
				switch (_lureId)
				{
					case 7610, 7611, 7612, 7613:
						type = 3;
						break;
					
					case 8548:
						if (check <= 32)
							type = 10;
						else if (check <= 64)
							type = 2;
						else if (check <= 96)
							type = 0;
						break;
					
					case 6519, 8505, 6520, 6521, 8507:
						if (check <= 54)
							type = 1;
						else if (check <= 74)
							type = 0;
						else if (check <= 94)
							type = 2;
						else
							type = 3;
						break;
					
					case 6522, 8508, 6523, 6524, 8510:
						if (check <= 54)
							type = 0;
						else if (check <= 74)
							type = 1;
						else if (check <= 94)
							type = 2;
						else
							type = 3;
						break;
					
					case 6525, 8511, 6526, 6527, 8513:
						if (check <= 55)
							type = 2;
						else if (check <= 74)
							type = 1;
						else if (check <= 94)
							type = 0;
						else
							type = 3;
						break;
					case 8484:
						if (check <= 33)
							type = 0;
						else if (check <= 66)
							type = 1;
						else
							type = 2;
						break;
				}
				break;
			
			case 2:
				switch (_lureId)
				{
					case 8506:
						if (check <= 54)
							type = 8;
						else if (check <= 77)
							type = 7;
						else
							type = 9;
						break;
					
					case 8509:
						if (check <= 54)
							type = 7;
						else if (check <= 77)
							type = 9;
						else
							type = 8;
						break;
					
					case 8512:
						if (check <= 54)
							type = 9;
						else if (check <= 77)
							type = 8;
						else
							type = 7;
						break;
					
					case 8485:
						if (check <= 33)
							type = 7;
						else if (check <= 66)
							type = 8;
						else
							type = 9;
						break;
				}
		}
		return type;
	}
	
	/**
	 * @return a fish level, based on multiple factors. It never goes out 1-27, and is slightly impacted by randomness.
	 */
	private int getRandomFishLvl()
	{
		final AbstractEffect effect = _fisher.getFirstEffect(2274);
		
		int level = (effect != null) ? (int) effect.getSkill().getPower() : _fisher.getSkillLevel(1315);
		if (level <= 0)
			return 1;
		
		final int check = Rnd.get(100);
		if (check < 35)
			level--;
		else if (check < 50)
			level++;
		
		return Math.clamp(level, 1, 27);
	}
	
	/**
	 * @return the bait {@link Location}.
	 */
	public Location getLoc()
	{
		return _loc;
	}
	
	/**
	 * @return true if currently looking for fish (waiting stance), false otherwise.
	 */
	public boolean isLookingForFish()
	{
		return _lookingForFish != null;
	}
	
	/**
	 * @return true if currently fighting fish (minigame), false otherwise.
	 */
	public boolean isUnderFishCombat()
	{
		return _fishCombat != null;
	}
	
	/**
	 * Modify {@link Fish} current HP during the minigame. We also handle the different events (HP reaching 0, or HP going past maximum HP).
	 * @param hp : The HP amount to add or remove.
	 * @param penalty : The penalty amount.
	 */
	public void changeHp(int hp, int penalty)
	{
		_fishCurHp -= hp;
		if (_fishCurHp < 0)
			_fishCurHp = 0;
		
		_fisher.broadcastPacket(new ExFishingHpRegen(_fisher, _time, _fishCurHp, _mode, _goodUse, _anim, penalty, _deceptiveMode));
		_anim = 0;
		
		if (_fishCurHp > _fish.hp() * 2)
		{
			_fishCurHp = _fish.hp() * 2;
			end(false);
		}
		else if (_fishCurHp == 0)
			end(true);
	}
	
	protected void aiTask()
	{
		if (_thinking)
			return;
		
		_thinking = true;
		_time--;
		
		try
		{
			if (_mode == 1)
			{
				if (_deceptiveMode == 0)
					_fishCurHp += _fish.hpRegen();
			}
			else
			{
				if (_deceptiveMode == 1)
					_fishCurHp += _fish.hpRegen();
			}
			
			if (_stop == 0)
			{
				_stop = 1;
				int check = Rnd.get(100);
				if (check >= 70)
					_mode = _mode == 0 ? 1 : 0;
				
				if (_isUpperGrade)
				{
					check = Rnd.get(100);
					if (check >= 90)
						_deceptiveMode = _deceptiveMode == 0 ? 1 : 0;
				}
			}
			else
				_stop--;
		}
		finally
		{
			_thinking = false;
			if (_anim != 0)
				_fisher.broadcastPacket(new ExFishingHpRegen(_fisher, _time, _fishCurHp, _mode, 0, _anim, 0, _deceptiveMode));
			else
				_fisher.sendPacket(new ExFishingHpRegen(_fisher, _time, _fishCurHp, _mode, 0, _anim, 0, _deceptiveMode));
		}
	}
	
	public void useRealing(int dmg, int penalty)
	{
		_anim = 2;
		if (Rnd.get(100) > 90)
		{
			_fisher.sendPacket(SystemMessageId.FISH_RESISTED_ATTEMPT_TO_BRING_IT_IN);
			_goodUse = 0;
			changeHp(0, penalty);
			return;
		}
		
		if (_fisher == null)
			return;
		
		if (_mode == 1)
		{
			if (_deceptiveMode == 0)
			{
				_fisher.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.REELING_SUCCESFUL_S1_DAMAGE).addNumber(dmg));
				if (penalty == 50)
					_fisher.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.REELING_SUCCESSFUL_PENALTY_S1).addNumber(penalty));
				
				_goodUse = 1;
				changeHp(dmg, penalty);
			}
			else
			{
				_fisher.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FISH_RESISTED_REELING_S1_HP_REGAINED).addNumber(dmg));
				_goodUse = 2;
				changeHp(-dmg, penalty);
			}
		}
		else
		{
			if (_deceptiveMode == 0)
			{
				_fisher.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FISH_RESISTED_REELING_S1_HP_REGAINED).addNumber(dmg));
				_goodUse = 2;
				changeHp(-dmg, penalty);
			}
			else
			{
				_fisher.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.REELING_SUCCESFUL_S1_DAMAGE).addNumber(dmg));
				if (penalty == 50)
					_fisher.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.REELING_SUCCESSFUL_PENALTY_S1).addNumber(penalty));
				
				_goodUse = 1;
				changeHp(dmg, penalty);
			}
		}
	}
	
	public void usePomping(int dmg, int penalty)
	{
		_anim = 1;
		if (Rnd.get(100) > 90)
		{
			_fisher.sendPacket(SystemMessageId.FISH_RESISTED_ATTEMPT_TO_BRING_IT_IN);
			_goodUse = 0;
			changeHp(0, penalty);
			return;
		}
		
		if (_fisher == null)
			return;
		
		if (_mode == 0)
		{
			if (_deceptiveMode == 0)
			{
				_fisher.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PUMPING_SUCCESFUL_S1_DAMAGE).addNumber(dmg));
				if (penalty == 50)
					_fisher.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PUMPING_SUCCESSFUL_PENALTY_S1).addNumber(penalty));
				
				_goodUse = 1;
				changeHp(dmg, penalty);
			}
			else
			{
				_fisher.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FISH_RESISTED_PUMPING_S1_HP_REGAINED).addNumber(dmg));
				_goodUse = 2;
				changeHp(-dmg, penalty);
			}
		}
		else
		{
			if (_deceptiveMode == 0)
			{
				_fisher.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FISH_RESISTED_PUMPING_S1_HP_REGAINED).addNumber(dmg));
				_goodUse = 2;
				changeHp(-dmg, penalty);
			}
			else
			{
				_fisher.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PUMPING_SUCCESFUL_S1_DAMAGE).addNumber(dmg));
				if (penalty == 50)
					_fisher.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PUMPING_SUCCESSFUL_PENALTY_S1).addNumber(penalty));
				
				_goodUse = 1;
				changeHp(dmg, penalty);
			}
		}
	}
	
	/**
	 * Start the fishing process.
	 * <ul>
	 * <li>The {@link Player} is immobilized, if he was moving.</li>
	 * <li>The bait {@link Location} and the lure are set.</li>
	 * <li>The {@link Fish} to caught is calculated.</li>
	 * <li>The _lookingForFish task is processed.</li>
	 * </ul>
	 * @param baitLoc : The bait location.
	 * @param lure : The used lure.
	 */
	public void start(Location baitLoc, ItemInstance lure)
	{
		if (_fisher.isDead())
			return;
		
		_fisher.getMove().stop();
		_fisher.setIsImmobilized(true);
		
		_loc.set(baitLoc);
		_lureId = lure.getItemId();
		_isNightLure = lure.getItem().isNightLure();
		
		int group = 1;
		
		switch (_lureId)
		{
			case 7807, 7808, 7809, 8486:
				group = 0;
				break;
			
			case 8485, 8506, 8509, 8512:
				group = 2;
				break;
		}
		
		_fish = FishData.getInstance().getFish(getRandomFishLvl(), getRandomFishType(group), group);
		if (_fish == null)
		{
			end(false);
			return;
		}
		
		_fisher.sendPacket(SystemMessageId.CAST_LINE_AND_START_FISHING);
		
		_fisher.broadcastPacket(new ExFishingStart(_fisher, _fish.getType(_isNightLure), _loc, _isNightLure));
		_fisher.sendPacket(new PlaySound(1, "SF_P_01"));
		
		if (_lookingForFish == null)
		{
			final boolean isNoob = _fish.group() == 0;
			final boolean isUpperGrade = _fish.group() == 2;
			
			int checkDelay = 0;
			if (_lureId == 6519 || _lureId == 6522 || _lureId == 6525 || _lureId == 8505 || _lureId == 8508 || _lureId == 8511)
				checkDelay = Math.round((float) (_fish.gutsCheckTime() * (1.33)));
			else if (_lureId == 6520 || _lureId == 6523 || _lureId == 6526 || (_lureId >= 8505 && _lureId <= 8513) || (_lureId >= 7610 && _lureId <= 7613) || (_lureId >= 7807 && _lureId <= 7809) || (_lureId >= 8484 && _lureId <= 8486) || _lureId == 8548)
				checkDelay = Math.round((float) (_fish.gutsCheckTime() * (1.00)));
			else if (_lureId == 6521 || _lureId == 6524 || _lureId == 6527 || _lureId == 8507 || _lureId == 8510 || _lureId == 8513)
				checkDelay = Math.round((float) (_fish.gutsCheckTime() * (0.66)));
			
			final long timer = System.currentTimeMillis() + _fish.waitTime() + 10000;
			
			_lookingForFish = ThreadPool.scheduleAtFixedRate(() ->
			{
				if (System.currentTimeMillis() >= timer)
				{
					end(false);
					return;
				}
				
				if (_fish.getType(_isNightLure) == -1)
					return;
				
				if (_fish.guts() > Rnd.get(1000))
				{
					if (_lookingForFish != null)
					{
						_lookingForFish.cancel(false);
						_lookingForFish = null;
					}
					
					_fishCurHp = _fish.hp();
					_time = _fish.combatTime() / 1000;
					_isUpperGrade = isUpperGrade;
					
					if (isUpperGrade)
					{
						_deceptiveMode = Rnd.get(100) >= 90 ? 1 : 0;
						_lureType = 2;
					}
					else
					{
						_deceptiveMode = 0;
						_lureType = isNoob ? 0 : 1;
					}
					_mode = Rnd.get(100) >= 80 ? 1 : 0;
					
					_fisher.broadcastPacket(new ExFishingStartCombat(_fisher, _time, _fish.hp(), _mode, _lureType, _deceptiveMode));
					_fisher.sendPacket(new PlaySound(1, "SF_S_01"));
					
					_fisher.sendPacket(SystemMessageId.GOT_A_BITE);
					
					if (_fishCombat == null)
						_fishCombat = ThreadPool.scheduleAtFixedRate(() ->
						{
							if (_fish == null)
								return;
							
							if (_fishCurHp >= _fish.hp() * 2)
							{
								_fisher.sendPacket(SystemMessageId.BAIT_STOLEN_BY_FISH);
								end(false);
							}
							else if (_time <= 0)
							{
								_fisher.sendPacket(SystemMessageId.FISH_SPIT_THE_HOOK);
								end(false);
							}
							else
								aiTask();
						}, 1000, 1000);
				}
			}, 10000, checkDelay);
		}
	}
	
	/**
	 * Ends the fishing process.
	 * <ul>
	 * <li>Process the reward ({@link Fish} or 5% {@link Npc}), if "win" is set as True.</li>
	 * <li>Cleanup all variables.</li>
	 * <li>Give back the movement ability to the {@link Player}.</li>
	 * <li>End all running tasks.</li>
	 * </ul>
	 * @param win : If true, a Fish or a PenaltyMonster has been caught.
	 */
	public void end(boolean win)
	{
		if (win)
		{
			if (Rnd.get(100) < 5)
			{
				try
				{
					final Spawn spawn = new Spawn(18319 + Math.min(_fisher.getStatus().getLevel() / 11, 7));
					spawn.setLoc(_fisher.getPosition());
					spawn.doSpawn(false, _fisher);
					
					_fisher.sendPacket(SystemMessageId.YOU_CAUGHT_SOMETHING_SMELLY_THROW_IT_BACK);
				}
				catch (Exception e)
				{
				}
			}
			else
			{
				_fisher.sendPacket(SystemMessageId.YOU_CAUGHT_SOMETHING);
				_fisher.addItem(_fish.id(), 1, true);
				
				FishingChampionshipManager.getInstance().newFish(_fisher, _lureId);
			}
			_fisher.getMissions().update(MissionType.FISHING);
		}
		
		if (_fish == null)
			_fisher.sendPacket(SystemMessageId.BAIT_LOST_FISH_GOT_AWAY);
		
		_time = 0;
		_stop = 0;
		_goodUse = 0;
		_anim = 0;
		_mode = 0;
		_deceptiveMode = 0;
		
		_thinking = false;
		
		_fish = null;
		_fishCurHp = 0;
		
		_isUpperGrade = false;
		
		_lureId = 0;
		_lureType = 0;
		_isNightLure = false;
		
		_loc.clean();
		
		_fisher.broadcastPacket(new ExFishingEnd(win, _fisher.getObjectId()));
		_fisher.sendPacket(SystemMessageId.REEL_LINE_AND_STOP_FISHING);
		_fisher.setIsImmobilized(false);
		
		if (_lookingForFish != null)
		{
			_lookingForFish.cancel(false);
			_lookingForFish = null;
		}
		
		if (_fishCombat != null)
		{
			_fishCombat.cancel(false);
			_fishCombat = null;
		}
	}
}
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
package ext.mods.gameserver.model.actor.status;

import java.util.Map;

import ext.mods.commons.random.Rnd;

import ext.mods.Config;
import ext.mods.gameserver.data.manager.CastleManager;
import ext.mods.gameserver.data.manager.ClanHallManager;
import ext.mods.gameserver.data.manager.DuelManager;
import ext.mods.gameserver.data.manager.ZoneManager;
import ext.mods.gameserver.data.xml.PlayerLevelData;
import ext.mods.gameserver.enums.SiegeSide;
import ext.mods.gameserver.enums.StatusType;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.enums.actors.ClassRace;
import ext.mods.gameserver.enums.actors.OperateType;
import ext.mods.gameserver.enums.actors.WeightPenalty;
import ext.mods.gameserver.enums.duels.DuelState;
import ext.mods.gameserver.enums.skills.EffectType;
import ext.mods.gameserver.enums.skills.Stats;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.Summon;
import ext.mods.gameserver.model.actor.container.npc.RewardInfo;
import ext.mods.gameserver.model.actor.instance.ClassMaster;
import ext.mods.gameserver.model.actor.instance.Pet;
import ext.mods.gameserver.model.actor.instance.Servitor;
import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.model.olympiad.OlympiadGameManager;
import ext.mods.gameserver.model.olympiad.OlympiadGameTask;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.model.pledge.ClanMember;
import ext.mods.gameserver.model.records.PlayerLevel;
import ext.mods.gameserver.model.residence.castle.Castle;
import ext.mods.gameserver.model.residence.castle.Castle.CastleFunction;
import ext.mods.gameserver.model.residence.castle.Siege;
import ext.mods.gameserver.model.residence.clanhall.ClanHall;
import ext.mods.gameserver.model.residence.clanhall.ClanHallFunction;
import ext.mods.gameserver.model.zone.type.CastleZone;
import ext.mods.gameserver.model.zone.type.MotherTreeZone;
import ext.mods.gameserver.model.zone.type.SwampZone;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ExDuelUpdateUserInfo;
import ext.mods.gameserver.network.serverpackets.ExOlympiadUserInfo;
import ext.mods.gameserver.network.serverpackets.PartySmallWindowUpdate;
import ext.mods.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import ext.mods.gameserver.network.serverpackets.SocialAction;
import ext.mods.gameserver.network.serverpackets.StatusUpdate;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.network.serverpackets.UserInfo;
import ext.mods.gameserver.scripting.QuestState;
import ext.mods.gameserver.skills.L2Skill;

public class PlayerStatus extends PlayableStatus<Player>
{
	private double _cp = .0;
	
	private double _cpUpdateIncCheck = .0;
	private double _cpUpdateDecCheck = .0;
	private double _cpUpdateInterval = .0;
	
	private double _mpUpdateIncCheck = .0;
	private double _mpUpdateDecCheck = .0;
	private double _mpUpdateInterval = .0;
	
	private int _oldMaxHp;
	private int _oldMaxMp;
	private int _oldMaxCp;
	
	public PlayerStatus(Player actor)
	{
		super(actor);
	}
	
	@Override
	public void initializeValues()
	{
		super.initializeValues();
		
		final double maxCp = getMaxCp();
		final double maxMp = getMaxMp();
		
		_cpUpdateInterval = maxCp / BAR_SIZE;
		_cpUpdateIncCheck = maxCp;
		_cpUpdateDecCheck = maxCp - _cpUpdateInterval;
		
		_mpUpdateInterval = maxMp / BAR_SIZE;
		_mpUpdateIncCheck = maxMp;
		_mpUpdateDecCheck = maxMp - _mpUpdateInterval;
	}
	
	@Override
	public final void reduceHp(double value, Creature attacker)
	{
		reduceHp(value, attacker, true, false, false, false);
	}
	
	@Override
	public final void reduceHp(double value, Creature attacker, boolean awake, boolean isDOT, boolean isHPConsumption)
	{
		reduceHp(value, attacker, awake, isDOT, isHPConsumption, false);
	}
	
	public final void reduceHp(double value, Creature attacker, boolean awake, boolean isDOT, boolean isHPConsumption, boolean ignoreCP)
	{
		if (_actor.isDead())
			return;
		
		if (Config.OFFLINE_MODE_NO_DAMAGE && (_actor.getClient() != null) && _actor.getClient().isDetached() && ((Config.OFFLINE_TRADE_ENABLE && ((_actor.getOperateType() == OperateType.SELL) || (_actor.getOperateType() == OperateType.BUY))) || (Config.OFFLINE_CRAFT_ENABLE && (_actor.isCrafting() || (_actor.getOperateType() == OperateType.MANUFACTURE)))))
			return;
		
		if (_actor.isInvul())
		{
			if (attacker != _actor)
				return;
			
			if (!isDOT && !isHPConsumption)
				return;
		}
		
		if (!isHPConsumption)
		{
			_actor.stopEffects(EffectType.SLEEP);
			_actor.stopEffects(EffectType.IMMOBILE_UNTIL_ATTACKED);
			
			if (_actor.isSitting() && !_actor.isInStoreMode())
				_actor.standUp();
			
			if (!isDOT && _actor.isStunned() && Rnd.get(10) == 0)
			{
				_actor.stopEffects(EffectType.STUN);
				
				_actor.updateAbnormalEffect();
			}
		}
		
		if (attacker != null && attacker != _actor)
		{
			final Player attackerPlayer = attacker.getActingPlayer();
			if (attackerPlayer != null && !attackerPlayer.getAccessLevel().canGiveDamage())
				return;
			
			if (_actor.isInDuel())
			{
				final DuelState playerState = _actor.getDuelState();
				if (playerState == DuelState.DEAD || playerState == DuelState.WINNER)
					return;
				
				if (attackerPlayer == null || attackerPlayer.getDuelId() != _actor.getDuelId() || playerState != DuelState.DUELLING)
					_actor.setDuelState(DuelState.INTERRUPTED);
			}
			
			int fullValue = (int) value;
			int tDmg = 0;
			
			final Summon summon = _actor.getSummon();
			if (summon instanceof Servitor && summon.isIn3DRadius(_actor, 900))
			{
				tDmg = (int) (value * calcStat(Stats.TRANSFER_DAMAGE_PERCENT, 0, null, null) / 100.);
				
				tDmg = Math.min((int) summon.getStatus().getHp() - 1, tDmg);
				if (tDmg > 0)
				{
					summon.reduceCurrentHp(tDmg, attacker, null);
					value -= tDmg;
					fullValue = (int) value;
				}
			}
			
			if (!ignoreCP && attacker instanceof Playable)
			{
				if (_cp >= value)
				{
					setCp(_cp - value);
					value = 0;
				}
				else
				{
					value -= _cp;
					setCp(0, false);
				}
			}
			
			if (fullValue > 0 && !isDOT)
			{
				_actor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_GAVE_YOU_S2_DMG).addCharName(attacker).addNumber(fullValue));
				
				if (tDmg > 0 && attackerPlayer != null)
					attackerPlayer.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.GIVEN_S1_DAMAGE_TO_YOUR_TARGET_AND_S2_DAMAGE_TO_SERVITOR).addNumber(fullValue).addNumber(tDmg));
			}
		}
		
		if (value > 0)
		{
			value = _hp - value;
			if (value <= 0)
			{
				if (_actor.isInDuel())
				{
					if (_actor.getDuelState() == DuelState.DUELLING)
					{
						_actor.disableAllSkills();
						
						stopHpMpRegeneration();
						
						DuelManager.getInstance().onPlayerDefeat(_actor);
					}
					value = 1;
				}
				else
					value = (_actor.isMortal()) ? 0 : 1;
			}
			setHp(value);
		}
		
		if (isHPConsumption && _hp < 0.5)
			_hp = 0.6;
		if (_hp < 0.5)
		{
			if (_actor.isInOlympiadMode())
			{
				_actor.abortAll(false);
				
				stopHpMpRegeneration();
				_actor.setIsDead(true);
				
				final Summon summon = _actor.getSummon();
				if (summon != null)
					summon.getAI().tryToIdle();
				
				return;
			}
			
			_actor.doDie(attacker);
			
			final QuestState qs = _actor.getQuestList().getQuestState("Tutorial");
			if (qs != null)
				qs.getQuest().notifyEvent("CE30", null, _actor);
		}
	}
	
	@Override
	public final void setHp(double newHp, boolean broadcastPacket)
	{
		super.setHp(newHp, broadcastPacket);
		
		final QuestState qs = _actor.getQuestList().getQuestState("Tutorial");
		if (qs != null && getHpRatio() < 0.3)
			qs.getQuest().notifyEvent("CE45", null, _actor);
	}
	
	public final double getCp()
	{
		return _cp;
	}
	
	public final void setCp(double newCp)
	{
		setCp(newCp, true);
	}
	
	/**
	 * Set current CPs to the amount set as parameter. We also start or stop the regeneration task if needed.
	 * @param newCp : The new amount to set.
	 * @param broadcastPacket : If true, call {@link #broadcastStatusUpdate()}.
	 */
	public final void setCp(double newCp, boolean broadcastPacket)
	{
		final int maxCp = getMaxCp();
		
		synchronized (this)
		{
			if (_actor.isDead())
				return;
			
			if (newCp < 0)
				newCp = 0;
			
			if (newCp >= maxCp)
			{
				_cp = maxCp;
				_flagsRegenActive &= ~REGEN_FLAG_CP;
				
				if (_flagsRegenActive == 0)
					stopHpMpRegeneration();
			}
			else
			{
				_cp = newCp;
				_flagsRegenActive |= REGEN_FLAG_CP;
				
				startHpMpRegeneration();
			}
		}
		
		if (broadcastPacket)
			broadcastStatusUpdate();
	}
	
	/**
	 * Set both CPs, HPs and MPs to given values set as parameters. The udpate is called only one time, during MPs allocation.
	 * @param newCp : The new HP value.
	 * @param newHp : The new HP value.
	 * @param newMp : The new MP value.
	 */
	public final void setCpHpMp(double newCp, double newHp, double newMp)
	{
		setCp(newCp, false);
		
		super.setHpMp(newHp, newMp);
	}
	
	/**
	 * Set both CPs, HPs and MPs to the maximum values. The udpate is called only one time, during MPs allocation.
	 */
	public final void setMaxCpHpMp()
	{
		setCp(getMaxCp(), false);
		
		super.setMaxHpMp();
	}
	
	@Override
	protected void doRegeneration()
	{
		if (_cp < getMaxCp())
			setCp(_cp + Math.max(1, getRegenCp()), false);
		
		super.doRegeneration();
	}
	
	/**
	 * @return True if a CP update should be done, otherwise false.
	 */
	private boolean needCpUpdate()
	{
		final double cp = _cp;
		final double maxCp = getMaxCp();
		
		if (cp <= 1.0 || maxCp < BAR_SIZE)
			return true;
		
		if (cp <= _cpUpdateDecCheck || cp >= _cpUpdateIncCheck)
		{
			if (cp == maxCp)
			{
				_cpUpdateIncCheck = cp + 1;
				_cpUpdateDecCheck = cp - _cpUpdateInterval;
			}
			else
			{
				final double doubleMulti = cp / _cpUpdateInterval;
				int intMulti = (int) doubleMulti;
				
				_cpUpdateDecCheck = _cpUpdateInterval * (doubleMulti < intMulti ? intMulti-- : intMulti);
				_cpUpdateIncCheck = _cpUpdateDecCheck + _cpUpdateInterval;
			}
			return true;
		}
		return false;
	}
	
	/**
	 * @return True if a MP update should be done, otherwise false.
	 */
	private boolean needMpUpdate()
	{
		final double mp = _mp;
		final double maxMp = getMaxMp();
		
		if (mp <= 1.0 || maxMp < BAR_SIZE)
			return true;
		
		if (mp <= _mpUpdateDecCheck || mp >= _mpUpdateIncCheck)
		{
			if (mp == maxMp)
			{
				_mpUpdateIncCheck = mp + 1;
				_mpUpdateDecCheck = mp - _mpUpdateInterval;
			}
			else
			{
				final double doubleMulti = mp / _mpUpdateInterval;
				int intMulti = (int) doubleMulti;
				
				_mpUpdateDecCheck = _mpUpdateInterval * (doubleMulti < intMulti ? intMulti-- : intMulti);
				_mpUpdateIncCheck = _mpUpdateDecCheck + _mpUpdateInterval;
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Send {@link StatusUpdate} packet to current {@link Player} and current HP, MP and Level to all other {@link Player}s of the {@link Party}.
	 */
	@Override
	public void broadcastStatusUpdate()
	{
		final StatusUpdate su = new StatusUpdate(_actor);
		su.addAttribute(StatusType.CUR_HP, (int) _hp);
		su.addAttribute(StatusType.CUR_MP, (int) _mp);
		su.addAttribute(StatusType.CUR_CP, (int) _cp);
		su.addAttribute(StatusType.MAX_CP, getMaxCp());
		_actor.sendPacket(su);
		
		final boolean needCpUpdate = needCpUpdate();
		final boolean needHpUpdate = needHpUpdate();
		
		final Party party = _actor.getParty();
		if (party != null && (needCpUpdate || needHpUpdate || needMpUpdate()))
			party.broadcastToPartyMembers(_actor, new PartySmallWindowUpdate(_actor));
		
		if (_actor.isInTournament() && _actor.getTournamentOpponents() != null && !_actor.getTournamentOpponents().isEmpty())
		{
			ExOlympiadUserInfo update = new ExOlympiadUserInfo(_actor);
			for (Player opponent : _actor.getTournamentOpponents())
			{
				opponent.sendPacket(update);
			}
		}
		
		if (_actor.isInOlympiadMode() && _actor.isOlympiadStart() && (needCpUpdate || needHpUpdate))
		{
			final OlympiadGameTask game = OlympiadGameManager.getInstance().getOlympiadTask(_actor.getOlympiadGameId());
			if (game != null && game.isBattleStarted())
				game.getZone().broadcastStatusUpdate(_actor);
		}
		
		if (_actor.isInDuel() && (needCpUpdate || needHpUpdate))
		{
			final ExDuelUpdateUserInfo update = new ExDuelUpdateUserInfo(_actor);
			DuelManager.getInstance().broadcastToOppositeTeam(_actor, update);
		}
	}
	
	@Override
	public final int getSTR()
	{
		return (int) calcStat(Stats.STAT_STR, _actor.getTemplate().getBaseSTR(), null, null);
	}
	
	@Override
	public final int getDEX()
	{
		return (int) calcStat(Stats.STAT_DEX, _actor.getTemplate().getBaseDEX(), null, null);
	}
	
	@Override
	public final int getCON()
	{
		return (int) calcStat(Stats.STAT_CON, _actor.getTemplate().getBaseCON(), null, null);
	}
	
	@Override
	public int getINT()
	{
		return (int) calcStat(Stats.STAT_INT, _actor.getTemplate().getBaseINT(), null, null);
	}
	
	@Override
	public final int getMEN()
	{
		return (int) calcStat(Stats.STAT_MEN, _actor.getTemplate().getBaseMEN(), null, null);
	}
	
	@Override
	public final int getWIT()
	{
		return (int) calcStat(Stats.STAT_WIT, _actor.getTemplate().getBaseWIT(), null, null);
	}
	
	@Override
	public boolean addExp(long value)
	{
		if (!super.addExp(value))
			return false;
		
		_actor.sendPacket(new UserInfo(_actor));
		return true;
	}
	
	/**
	 * Add Experience and SP rewards to the Player, remove its Karma (if necessary) and Launch increase level task.
	 * <ul>
	 * <li>Remove Karma when the player kills Monster</li>
	 * <li>Send StatusUpdate to the Player</li>
	 * <li>Send a Server->Client System Message to the Player</li>
	 * <li>If the Player increases its level, send SocialAction (broadcast)</li>
	 * <li>If the Player increases its level, manage the increase level task (Max MP, Max MP, Recommandation, Expertise and beginner skills...)</li>
	 * <li>If the Player increases its level, send UserInfo to the Player</li>
	 * </ul>
	 * @param addToExp The Experience value to add
	 * @param addToSp The SP value to add
	 */
	@Override
	public boolean addExpAndSp(long addToExp, int addToSp)
	{
		if (!super.addExpAndSp(addToExp, addToSp))
			return false;
		
		SystemMessage sm;
		
		if (addToExp == 0 && addToSp > 0)
			sm = SystemMessage.getSystemMessage(SystemMessageId.ACQUIRED_S1_SP).addNumber(addToSp);
		else if (addToExp > 0 && addToSp == 0)
			sm = SystemMessage.getSystemMessage(SystemMessageId.EARNED_S1_EXPERIENCE).addNumber((int) addToExp);
		else
			sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_EARNED_S1_EXP_AND_S2_SP).addNumber((int) addToExp).addNumber(addToSp);
		
		_actor.sendPacket(sm);
		
		return true;
	}
	
	/**
	 * Add Experience and SP rewards to the Player, remove its Karma (if necessary) and Launch increase level task.
	 * <ul>
	 * <li>Remove Karma when the player kills Monster</li>
	 * <li>Send StatusUpdate to the Player</li>
	 * <li>Send a Server->Client System Message to the Player</li>
	 * <li>If the Player increases its level, send SocialAction (broadcast)</li>
	 * <li>If the Player increases its level, manage the increase level task (Max MP, Max MP, Recommandation, Expertise and beginner skills...)</li>
	 * <li>If the Player increases its level, send UserInfo to the Player</li>
	 * </ul>
	 * @param addToExp The Experience value to add
	 * @param addToSp The SP value to add
	 * @param rewards The list of players and summons, who done damage
	 * @return
	 */
	public boolean addExpAndSp(long addToExp, int addToSp, Map<Creature, RewardInfo> rewards)
	{
		if (_actor.hasPet())
		{
			final Pet pet = (Pet) _actor.getSummon();
			if (pet.getStatus().getExp() <= (pet.getTemplate().getPetDataEntry(81).maxExp() + 10000) && !pet.isDead() && pet.isIn3DRadius(_actor, Config.PARTY_RANGE))
			{
				long petExp = 0;
				int petSp = 0;
				
				int ratio = pet.getPetData().expType();
				if (ratio == -1)
				{
					RewardInfo r = rewards.get(pet);
					RewardInfo reward = rewards.get(_actor);
					if (r != null && reward != null)
					{
						final double damageDoneByPet = r.getDamage() / reward.getDamage();
						
						petExp = (long) (addToExp * damageDoneByPet);
						petSp = (int) (addToSp * damageDoneByPet);
					}
				}
				else
				{
					if (ratio > 100)
						ratio = 100;
					
					petExp = Math.round(addToExp * (1 - (ratio / 100.0)));
					petSp = (int) Math.round(addToSp * (1 - (ratio / 100.0)));
				}
				
				addToExp -= petExp;
				addToSp -= petSp;
				pet.addExpAndSp(petExp, petSp);
			}
		}
		return addExpAndSp(addToExp, addToSp);
	}
	
	@Override
	public boolean removeExpAndSp(long removeExp, int removeSp)
	{
		return removeExpAndSp(removeExp, removeSp, true);
	}
	
	public boolean removeExpAndSp(long removeExp, int removeSp, boolean sendMessage)
	{
		final int oldLevel = getLevel();
		
		if (!super.removeExpAndSp(removeExp, removeSp))
			return false;
		
		if (sendMessage)
		{
			if (removeExp > 0)
				_actor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EXP_DECREASED_BY_S1).addNumber((int) removeExp));
			
			if (removeSp > 0)
				_actor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SP_DECREASED_S1).addNumber(removeSp));
			
			if (getLevel() < oldLevel)
				broadcastStatusUpdate();
		}
		return true;
	}
	
	@Override
	public final boolean addLevel(byte value)
	{
		if (getLevel() + value > PlayerLevelData.getInstance().getRealMaxLevel())
			return false;
		
		boolean levelIncreased = super.addLevel(value);
		
		if (levelIncreased)
		{
			final QuestState qs = _actor.getQuestList().getQuestState("Tutorial");
			if (qs != null)
				qs.getQuest().notifyEvent("CE40", null, _actor);
			
			setCp(getMaxCp());
			
			_actor.broadcastPacket(new SocialAction(_actor, 15));
			_actor.sendPacket(SystemMessageId.YOU_INCREASED_YOUR_LEVEL);
			
			ClassMaster.showQuestionMark(_actor);
		}
		
		_actor.giveSkills();
		
		final Clan clan = _actor.getClan();
		if (clan != null)
		{
			final ClanMember member = clan.getClanMember(_actor.getObjectId());
			if (member != null)
				member.refreshLevel();
			
			clan.broadcastToMembers(new PledgeShowMemberListUpdate(_actor));
		}
		
		final Party party = _actor.getParty();
		if (party != null)
			party.recalculateLevel();
		
		_actor.refreshWeightPenalty();
		_actor.refreshExpertisePenalty();
		_actor.sendPacket(new UserInfo(_actor));
		
		return levelIncreased;
	}
	
	@Override
	public final long getExp()
	{
		if (_actor.isSubClassActive())
			return _actor.getSubClasses().get(_actor.getClassIndex()).getExp();
		
		return super.getExp();
	}
	
	@Override
	public final void setExp(long value)
	{
		if (_actor.isSubClassActive())
			_actor.getSubClasses().get(_actor.getClassIndex()).setExp(value);
		else
			super.setExp(value);
	}
	
	@Override
	public final int getLevel()
	{
		if (_actor.isSubClassActive())
			return _actor.getSubClasses().get(_actor.getClassIndex()).getLevel();
		
		return super.getLevel();
	}
	
	@Override
	public final void setLevel(int value)
	{
		value = Math.min(value, PlayerLevelData.getInstance().getRealMaxLevel());
		
		if (_actor.isSubClassActive())
			_actor.getSubClasses().get(_actor.getClassIndex()).setLevel(value);
		else
			super.setLevel(value);
	}
	
	@Override
	public final int getMaxCp()
	{
		int val = (int) calcStat(Stats.MAX_CP, _actor.getTemplate().getBaseCpMax(getLevel()), null, null);
		if (val != _oldMaxCp)
		{
			_oldMaxCp = val;
			
			if (_cp != val)
				setCp(_cp);
		}
		return val;
	}
	
	@Override
	public final int getMaxHp()
	{
		int val = super.getMaxHp();
		if (val != _oldMaxHp)
		{
			_oldMaxHp = val;
			
			if (_hp != val)
				setHp(_hp);
		}
		
		return val;
	}
	
	@Override
	public final int getMaxMp()
	{
		int val = super.getMaxMp();
		
		if (val != _oldMaxMp)
		{
			_oldMaxMp = val;
			
			if (_mp != val)
				setMp(_mp);
		}
		
		return val;
	}
	
	@Override
	public final double getRegenHp()
	{
		double value = super.getRegenHp();
		
		final Clan clan = _actor.getClan();
		if (clan != null)
		{
			final Siege siege = CastleManager.getInstance().getActiveSiege(_actor);
			if (siege != null && siege.checkSide(clan, SiegeSide.ATTACKER))
			{
				final Npc flag = clan.getFlag();
				if (flag != null && _actor.isIn3DRadius(flag, 200))
					value *= 1.5;
			}
			
			if (clan.hasCastle() && _actor.isInsideZone(ZoneId.CASTLE))
			{
				final CastleZone zone = ZoneManager.getInstance().getZone(_actor, CastleZone.class);
				final int zoneCastleId = zone == null ? -1 : zone.getResidenceId();
				if (zoneCastleId == clan.getCastleId())
				{
					final CastleFunction cf = CastleManager.getInstance().getCastleByOwner(clan).getFunction(Castle.FUNC_RESTORE_HP);
					if (cf != null)
						value *= 1 + cf.getLvl() / 100.0;
				}
			}
			
			if (_actor.isInsideZone(ZoneId.CLAN_HALL))
			{
				final int chId = clan.getClanHallId();
				if (chId > 0)
				{
					final ClanHall ch = ClanHallManager.getInstance().getClanHall(chId);
					if (ch != null)
					{
						final ClanHallFunction chf = ch.getFunction(ClanHall.FUNC_RESTORE_HP);
						if (chf != null)
							value *= 1 + chf.getLvl() / 100.0;
					}
				}
			}
		}
		
		if (_actor.isSitting())
			value *= 1.5;
		else if (!_actor.isMoving())
			value *= 1.1;
		else if (_actor.isRunning())
			value *= 0.7;
		
		final WeightPenalty wp = _actor.getWeightPenalty();
		if (wp != WeightPenalty.NONE)
			value *= wp.getRegenerationMultiplier();
		
		if (_actor.isInsideZone(ZoneId.MOTHER_TREE))
		{
			final MotherTreeZone zone = ZoneManager.getInstance().getZone(_actor, MotherTreeZone.class);
			if (zone != null)
				value += zone.getHpRegenBonus();
		}
		
		return value;
	}
	
	@Override
	public final double getRegenMp()
	{
		double value = super.getRegenMp();
		
		final Clan clan = _actor.getClan();
		if (clan != null)
		{
			final Siege siege = CastleManager.getInstance().getActiveSiege(_actor);
			if (siege != null && siege.checkSide(clan, SiegeSide.ATTACKER))
			{
				final Npc flag = clan.getFlag();
				if (flag != null && _actor.isIn3DRadius(flag, 200))
					value *= 1.5;
			}
			
			if (clan.hasCastle() && _actor.isInsideZone(ZoneId.CASTLE))
			{
				final CastleZone zone = ZoneManager.getInstance().getZone(_actor, CastleZone.class);
				final int zoneCastleId = zone == null ? -1 : zone.getResidenceId();
				if (zoneCastleId == clan.getCastleId())
				{
					final CastleFunction cf = CastleManager.getInstance().getCastleByOwner(clan).getFunction(Castle.FUNC_RESTORE_MP);
					if (cf != null)
						value *= 1 + cf.getLvl() / 100.0;
				}
			}
			
			if (_actor.isInsideZone(ZoneId.CLAN_HALL))
			{
				final int chId = _actor.getClan().getClanHallId();
				if (chId > 0)
				{
					final ClanHall ch = ClanHallManager.getInstance().getClanHall(chId);
					if (ch != null)
					{
						final ClanHallFunction chf = ch.getFunction(ClanHall.FUNC_RESTORE_MP);
						if (chf != null)
							value *= 1 + chf.getLvl() / 100.0;
					}
				}
			}
		}
		
		if (_actor.isSitting())
			value *= 1.5;
		else if (!_actor.isMoving())
			value *= 1.1;
		else if (_actor.isRunning())
			value *= 0.7;
		
		final WeightPenalty wp = _actor.getWeightPenalty();
		if (wp != WeightPenalty.NONE)
			value *= wp.getRegenerationMultiplier();
		
		if (_actor.isInsideZone(ZoneId.MOTHER_TREE))
		{
			final MotherTreeZone zone = ZoneManager.getInstance().getZone(_actor, MotherTreeZone.class);
			if (zone != null)
				value += zone.getMpRegenBonus();
		}
		
		return value;
	}
	
	/**
	 * @return The CP regeneration of this {@link Creature}.
	 */
	public final double getRegenCp()
	{
		double value = calcStat(Stats.REGENERATE_CP_RATE, _actor.getTemplate().getBaseCpRegen(getLevel()) * Config.CP_REGEN_MULTIPLIER, null, null);
		
		if (_actor.isSitting())
			value *= 1.5;
		else if (!_actor.isMoving())
			value *= 1.1;
		else if (_actor.isRunning())
			value *= 0.7;
		
		final WeightPenalty wp = _actor.getWeightPenalty();
		if (wp != WeightPenalty.NONE)
			value *= wp.getRegenerationMultiplier();
		
		return value;
	}
	
	@Override
	public final int getSp()
	{
		if (_actor.isSubClassActive())
			return _actor.getSubClasses().get(_actor.getClassIndex()).getSp();
		
		return super.getSp();
	}
	
	@Override
	public final void setSp(int value)
	{
		if (_actor.isSubClassActive())
			_actor.getSubClasses().get(_actor.getClassIndex()).setSp(value);
		else
			super.setSp(value);
		
		StatusUpdate su = new StatusUpdate(_actor);
		su.addAttribute(StatusType.SP, getSp());
		_actor.sendPacket(su);
	}
	
	@Override
	public int getBaseRunSpeed()
	{
		if (_actor.isMounted())
		{
			int base = (_actor.isFlying()) ? _actor.getPetDataEntry().mountFlySpeed() : _actor.getPetDataEntry().mountBaseSpeed();
			
			if (_actor.getMountLevel() - getLevel() > 9)
				base /= 2;
			
			if (_actor.checkFoodState(_actor.getPetTemplate().getHungryLimit()))
				base /= 2;
			
			return base;
		}
		
		return super.getBaseRunSpeed();
	}
	
	public int getBaseSwimSpeed()
	{
		if (_actor.isMounted())
		{
			int base = _actor.getPetDataEntry().mountWaterSpeed();
			
			if (_actor.getMountLevel() - getLevel() > 9)
				base /= 2;
			
			if (_actor.checkFoodState(_actor.getPetTemplate().getHungryLimit()))
				base /= 2;
			
			return base;
		}
		
		return _actor.getTemplate().getBaseSwimSpeed();
	}
	
	@Override
	public float getMoveSpeed()
	{
		float baseValue = (_actor.isInWater()) ? getBaseSwimSpeed() : getBaseMoveSpeed();
		
		if (_actor.isInsideZone(ZoneId.SWAMP))
		{
			final SwampZone zone = ZoneManager.getInstance().getZone(_actor, SwampZone.class);
			if (zone != null)
				baseValue *= (100 + zone.getMoveBonus()) / 100.0;
		}
		
		final WeightPenalty wp = _actor.getWeightPenalty();
		if (wp != WeightPenalty.NONE)
			baseValue *= wp.getSpeedMultiplier();
		
		final int penalty = _actor.getArmorGradePenalty();
		if (penalty == 1)
			baseValue /= 1.2;
		else if (penalty == 2)
			baseValue /= 1.44;
		else if (penalty == 3)
			baseValue /= 1.728;
		else if (penalty >= 4)
			baseValue /= 2;
		
		if (_actor.isGM())
			return (float) calcStat(Stats.RUN_SPEED, baseValue, null, null);
		
		return Math.min((float) calcStat(Stats.RUN_SPEED, baseValue, null, null), Config.MAX_RUN_SPEED);
	}
	
	@Override
	public float getRealMoveSpeed(boolean isStillWalking)
	{
		float baseValue = (_actor.isInWater()) ? getBaseSwimSpeed() : ((isStillWalking || !_actor.isRunning()) ? getBaseWalkSpeed() : getBaseRunSpeed());
		
		if (_actor.isInsideZone(ZoneId.SWAMP))
		{
			final SwampZone zone = ZoneManager.getInstance().getZone(_actor, SwampZone.class);
			if (zone != null)
				baseValue *= (100 + zone.getMoveBonus()) / 100.0;
		}
		
		final WeightPenalty wp = _actor.getWeightPenalty();
		if (wp != WeightPenalty.NONE)
			baseValue *= wp.getSpeedMultiplier();
		
		final int penalty = _actor.getArmorGradePenalty();
		if (penalty == 1)
			baseValue /= 1.2;
		else if (penalty == 2)
			baseValue /= 1.44;
		else if (penalty == 3)
			baseValue /= 1.728;
		else if (penalty >= 4)
			baseValue /= 2;
		
		if (_actor.isGM())
			return (float) calcStat(Stats.RUN_SPEED, baseValue, null, null);
		
		return Math.min((float) calcStat(Stats.RUN_SPEED, baseValue, null, null), Config.MAX_RUN_SPEED);
	}
	
	@Override
	public int getMAtk(Creature target, L2Skill skill)
	{
		int val = super.getMAtk(target, skill);
		
		if (_actor.isMounted())
		{
			double base = _actor.getPetDataEntry().mountMatk();
			
			final int diffLevel = _actor.getMountLevel() - getLevel();
			if (diffLevel > 4)
				base *= 0.5 - ((Math.min(diffLevel, 10) - 5) * 0.05);
			
			return (int) calcStat(Stats.MAGIC_ATTACK, base, null, null);
		}
		
		if (_actor.isGM())
			return val;
		
		return Math.min(val, Config.MAX_MATK);
	}
	
	@Override
	public int getMAtkSpd()
	{
		double base = 333;
		
		if (_actor.isMounted() && _actor.checkFoodState(_actor.getPetTemplate().getHungryLimit()))
			base /= 2;
		
		final int penalty = _actor.getArmorGradePenalty();
		if (penalty == 1)
			base /= 1.2;
		else if (penalty == 2)
			base /= 1.44;
		else if (penalty == 3)
			base /= 1.728;
		else if (penalty >= 4)
			base /= 2;
		
		if (_actor.isGM())
			return (int) calcStat(Stats.MAGIC_ATTACK_SPEED, base, null, null);
		
		return Math.min((int) calcStat(Stats.MAGIC_ATTACK_SPEED, base, null, null), Config.MAX_MATK_SPEED);
	}
	
	@Override
	public int getPAtk(Creature target)
	{
		int val = super.getPAtk(target);
		
		if (_actor.isMounted())
		{
			double base = _actor.getPetDataEntry().mountPatk();
			
			final int diffLevel = _actor.getMountLevel() - getLevel();
			if (diffLevel > 4)
				base *= 0.5 - ((Math.min(diffLevel, 10) - 5) * 0.05);
			
			return (int) calcStat(Stats.POWER_ATTACK, base, null, null);
		}
		
		if (_actor.isGM())
			return val;
		
		return Math.min(val, Config.MAX_PATK);
	}
	
	@Override
	public int getPAtkSpd()
	{
		int val = super.getPAtkSpd();
		
		if (_actor.isFlying())
			return (_actor.checkFoodState(_actor.getPetTemplate().getHungryLimit())) ? 150 : 300;
		
		final int penalty = _actor.getArmorGradePenalty();
		if (penalty == 1)
			val /= 1.2;
		else if (penalty == 2)
			val /= 1.44;
		else if (penalty == 3)
			val /= 1.728;
		else if (penalty >= 4)
			val /= 2;
		
		if (_actor.isRiding())
		{
			double base = _actor.getPetDataEntry().mountAtkSpd();
			
			if (_actor.checkFoodState(_actor.getPetTemplate().getHungryLimit()))
				base /= 2;
			
			return (int) calcStat(Stats.POWER_ATTACK_SPEED, base, null, null);
		}
		
		if (_actor.isGM())
			return val;
		
		return Math.min(val, Config.MAX_PATK_SPEED);
	}
	
	@Override
	public int getEvasionRate(Creature target)
	{
		int val = super.getEvasionRate(target);
		
		final int penalty = _actor.getArmorGradePenalty();
		if (penalty > 0)
			val -= (2 * penalty);
		
		if (_actor.isGM())
			return val;
		
		return Math.min(val, Config.MAX_EVASION);
	}
	
	@Override
	public int getAccuracy()
	{
		int val = super.getAccuracy();
		
		if (_actor.getWeaponGradePenalty())
			val -= 20;
		
		return val;
	}
	
	@Override
	public int getCriticalHit(Creature target, L2Skill skill)
	{
		int val = super.getCriticalHit(target, skill);
		
		if (_actor.isGM())
			return val;
		
		return Math.min(val, Config.MAX_PCRIT_RATE);
	}
	
	@Override
	public int getPhysicalAttackRange()
	{
		return (int) calcStat(Stats.POWER_ATTACK_RANGE, _actor.getAttackType().getRange(), null, null);
	}
	
	@Override
	public long getExpForLevel(int level)
	{
		final PlayerLevel pl = PlayerLevelData.getInstance().getPlayerLevel(level);
		if (pl == null)
			return 0;
		
		return pl.requiredExpToLevelUp();
	}
	
	@Override
	public long getExpForThisLevel()
	{
		final PlayerLevel pl = PlayerLevelData.getInstance().getPlayerLevel(getLevel());
		if (pl == null)
			return 0;
		
		return pl.requiredExpToLevelUp();
	}
	
	@Override
	public long getExpForNextLevel()
	{
		final PlayerLevel pl = PlayerLevelData.getInstance().getPlayerLevel(getLevel() + 1);
		if (pl == null)
			return 0;
		
		return pl.requiredExpToLevelUp();
	}
	
	/**
	 * A check used in multiple scenarii (subclass, olympiad registration, quest bypass, etc).
	 * @return True if this {@link Player} exceeded the authorized inventory slots ratio (which is 80%), or false otherwise.
	 */
	public boolean isOverburden()
	{
		return (double) _actor.getInventory().getSize() / getInventoryLimit() >= 0.8;
	}
	
	public int getInventoryLimit()
	{
		return ((_actor.getRace() == ClassRace.DWARF) ? Config.INVENTORY_MAXIMUM_DWARF : Config.INVENTORY_MAXIMUM_NO_DWARF) + (int) calcStat(Stats.INV_LIM, 0, null, null);
	}
	
	public int getWareHouseLimit()
	{
		return ((_actor.getRace() == ClassRace.DWARF) ? Config.WAREHOUSE_SLOTS_DWARF : Config.WAREHOUSE_SLOTS_NO_DWARF) + (int) calcStat(Stats.WH_LIM, 0, null, null);
	}
	
	public int getPrivateSellStoreLimit()
	{
		return ((_actor.getRace() == ClassRace.DWARF) ? Config.MAX_PVTSTORESELL_SLOTS_DWARF : Config.MAX_PVTSTORESELL_SLOTS_OTHER) + (int) calcStat(Stats.P_SELL_LIM, 0, null, null);
	}
	
	public int getPrivateBuyStoreLimit()
	{
		return ((_actor.getRace() == ClassRace.DWARF) ? Config.MAX_PVTSTOREBUY_SLOTS_DWARF : Config.MAX_PVTSTOREBUY_SLOTS_OTHER) + (int) calcStat(Stats.P_BUY_LIM, 0, null, null);
	}
	
	public int getFreightLimit()
	{
		return Config.FREIGHT_SLOTS + (int) calcStat(Stats.FREIGHT_LIM, 0, null, null);
	}
	
	public int getDwarfRecipeLimit()
	{
		return Config.DWARF_RECIPE_LIMIT + (int) calcStat(Stats.REC_D_LIM, 0, null, null);
	}
	
	public int getCommonRecipeLimit()
	{
		return Config.COMMON_RECIPE_LIMIT + (int) calcStat(Stats.REC_C_LIM, 0, null, null);
	}
}
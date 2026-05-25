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
package ext.mods.gameserver.model.actor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.Config;
import ext.mods.gameserver.data.SkillTable.FrequentSkill;
import ext.mods.gameserver.data.manager.CastleManager;
import ext.mods.gameserver.enums.AiEventType;
import ext.mods.gameserver.enums.SiegeSide;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.enums.boats.BoatDock;
import ext.mods.gameserver.enums.duels.DuelState;
import ext.mods.gameserver.enums.skills.EffectFlag;
import ext.mods.gameserver.enums.skills.EffectType;
import ext.mods.gameserver.model.actor.ai.type.PlayableAI;
import ext.mods.gameserver.model.actor.attack.PlayableAttack;
import ext.mods.gameserver.model.actor.cast.PlayableCast;
import ext.mods.gameserver.model.actor.container.npc.AggroInfo;
import ext.mods.gameserver.model.actor.instance.Monster;
import ext.mods.gameserver.model.actor.instance.SiegeGuard;
import ext.mods.gameserver.model.actor.status.PlayableStatus;
import ext.mods.gameserver.model.actor.template.CreatureTemplate;
import ext.mods.gameserver.model.entity.autofarm.AutoFarmManager;
import ext.mods.gameserver.model.entity.autofarm.AutoFarmProfile;
import ext.mods.gameserver.model.entity.Duel;
import ext.mods.gameserver.model.entity.events.capturetheflag.CTFEvent;
import ext.mods.gameserver.model.entity.events.deathmatch.DMEvent;
import ext.mods.gameserver.model.entity.events.lastman.LMEvent;
import ext.mods.gameserver.model.entity.events.teamvsteam.TvTEvent;
import ext.mods.gameserver.model.group.CommandChannel;
import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.EtcItem;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.location.Point2D;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.model.residence.castle.Siege;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.ExUseSharedGroupItem;
import ext.mods.gameserver.network.serverpackets.MagicSkillUse;
import ext.mods.gameserver.network.serverpackets.MoveToLocationInVehicle;
import ext.mods.gameserver.network.serverpackets.Revive;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.skills.AbstractEffect;
import ext.mods.gameserver.skills.L2Skill;

/**
 * This class represents all {@link Playable} actors in the world : {@link Player}s and their different {@link Summon} types.
 */
public abstract class Playable extends Creature
{
	private final Map<Integer, Long> _disabledItems = new ConcurrentHashMap<>();
	
	protected Playable(int objectId, CreatureTemplate template)
	{
		super(objectId, template);
	}
	
	/**
	 * @return The max weight this {@link Playable} can carry.
	 */
	public abstract int getWeightLimit();
	
	/**
	 * @return The karma value of this {@link Playable} - in case of Summon, his owner.
	 */
	public abstract int getKarma();
	
	/**
	 * @return The pvp flag value of this {@link Playable} - in case of Summon, his owner.
	 */
	public abstract byte getPvpFlag();
	
	/**
	 * @return The {@link Clan} of this {@link Playable} - in case of Summon, his owner.
	 */
	public abstract Clan getClan();
	
	/**
	 * @return The {@link Clan} id of this {@link Playable} - in case of Summon, his owner.
	 */
	public abstract int getClanId();
	
	/**
	 * Add an {@link ItemInstance} to this {@link Playable}'s inventory.
	 * @param item : The {@link ItemInstance} to add.
	 * @param sendMessage : If true, send client message.
	 */
	public abstract void addItem(ItemInstance item, boolean sendMessage);
	
	/**
	 * Add an item to this {@link Playable}.
	 * @param itemId : The itemId of item to add.
	 * @param count : The quantity of items to add.
	 * @param sendMessage : Send {@link SystemMessage} client notification if set to true.
	 * @return an {@link ItemInstance} of a newly generated item for this {@link Playable}, using itemId and count.
	 */
	public abstract ItemInstance addItem(int itemId, int count, boolean sendMessage);
	
	@Override
	public PlayableAI<?> getAI()
	{
		return (PlayableAI<?>) _ai;
	}
	
	@Override
	public PlayableStatus<? extends Playable> getStatus()
	{
		return (PlayableStatus<?>) _status;
	}
	
	@Override
	public void setStatus()
	{
		_status = new PlayableStatus<>(this);
	}
	
	@Override
	public void setCast()
	{
		_cast = new PlayableCast<>(this);
	}
	
	@Override
	public void setAttack()
	{
		_attack = new PlayableAttack<>(this);
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		synchronized (this)
		{
			if (isDead())
				return false;
			
			getStatus().setHp(0);
			
			setIsDead(true);
		}
		
		abortAll(true);
		
		getStatus().stopHpMpRegeneration();
		
		if (isPhoenixBlessed())
		{
			if (getCharmOfLuck())
				stopCharmOfLuck(null);
			if (isNoblesseBlessed())
				stopNoblesseBlessing(null);
		}
		else if (isNoblesseBlessed() || (LMEvent.getInstance().isStarted() && Config.LM_EVENT_EFFECTS_REMOVAL == 2 || TvTEvent.getInstance().isStarted() && Config.TVT_EVENT_EFFECTS_REMOVAL == 2 || DMEvent.getInstance().isStarted() && Config.DM_EVENT_EFFECTS_REMOVAL == 2 || CTFEvent.getInstance().isStarted() && Config.CTF_EVENT_EFFECTS_REMOVAL == 2))
		{
			stopNoblesseBlessing(null);
			
			if (getCharmOfLuck())
				stopCharmOfLuck(null);
		}
		else
			stopAllEffectsExceptThoseThatLastThroughDeath();
		
		getStatus().broadcastStatusUpdate();
		
		getAI().notifyEvent(AiEventType.DEAD, null, null);
		
		final Player actingPlayer = getActingPlayer();
		actingPlayer.getQuestList().getQuests(Quest::isTriggeredOnDeath).forEach(q -> q.onDeath((killer == null ? this : killer), actingPlayer));
		
		if (killer != null)
		{
			final Player player = killer.getActingPlayer();
			if (player != null)
				player.onKillUpdatePvPKarma(this);
		}
		
		return true;
	}
	
	@Override
	public void doRevive()
	{
		if (!isDead() || isTeleporting())
			return;
		
		setIsDead(false);
		
		if (isPhoenixBlessed())
		{
			stopPhoenixBlessing(null);
			
			getStatus().setMaxHpMp();
		}
		else
			getStatus().setHp(getStatus().getMaxHp() * Config.RESPAWN_RESTORE_HP);
		
		broadcastPacket(new Revive(this));
	}
	
	@Override
	public boolean isMovementDisabled()
	{
		return super.isMovementDisabled() || getStatus().getMoveSpeed() == 0;
	}
	
	public boolean checkIfPvP(Playable target)
	{
		if (target == null || target == this)
			return false;
		
		if (getKarma() != 0)
			return false;
		
		if (target.getKarma() != 0 || target.getPvpFlag() == 0)
			return false;
		
		return true;
	}
	
	/**
	 * Send a {@link SystemMessage} packet using a {@link SystemMessageId} to the {@link Player} associated to this {@link Playable}.
	 * @param id : The {@link SystemMessageId} to send.
	 */
	public void sendPacket(SystemMessageId id)
	{
	}
	
	public final boolean isNoblesseBlessed()
	{
		return _effects.isAffected(EffectFlag.NOBLESS_BLESSING);
	}
	
	public final void stopNoblesseBlessing(AbstractEffect effect)
	{
		if (effect == null)
			stopEffects(EffectType.NOBLESSE_BLESSING);
		else
			removeEffect(effect);
		updateAbnormalEffect();
	}
	
	public final boolean isPhoenixBlessed()
	{
		return _effects.isAffected(EffectFlag.PHOENIX_BLESSING);
	}
	
	public final void stopPhoenixBlessing(AbstractEffect effect)
	{
		if (effect == null)
			stopEffects(EffectType.PHOENIX_BLESSING);
		else
			removeEffect(effect);
		
		updateAbnormalEffect();
	}
	
	@Override
	public boolean isSilentMoving()
	{
		return _effects.isAffected(EffectFlag.SILENT_MOVE);
	}
	
	public final boolean getProtectionBlessing()
	{
		return _effects.isAffected(EffectFlag.PROTECTION_BLESSING);
	}
	
	public void stopProtectionBlessing(AbstractEffect effect)
	{
		if (effect == null)
			stopEffects(EffectType.PROTECTION_BLESSING);
		else
			removeEffect(effect);
		
		updateAbnormalEffect();
	}
	
	public final boolean getCharmOfLuck()
	{
		return _effects.isAffected(EffectFlag.CHARM_OF_LUCK);
	}
	
	public final void stopCharmOfLuck(AbstractEffect effect)
	{
		if (effect == null)
			stopEffects(EffectType.CHARM_OF_LUCK);
		else
			removeEffect(effect);
		
		updateAbnormalEffect();
	}
	
	@Override
	public void updateEffectIcons(boolean partyOnly)
	{
		_effects.updateEffectIcons(partyOnly);
	}
	
	/**
	 * This method allows to easily send relations. Overridden in L2Summon and Player.
	 */
	public void broadcastRelationsChanges()
	{
	}
	
	@Override
	public boolean isInArena()
	{
		return isInsideZone(ZoneId.PVP) && !isInsideZone(ZoneId.SIEGE);
	}
	
	public void addItemSkillTimeStamp(L2Skill itemSkill, ItemInstance itemInstance)
	{
		final EtcItem etcItem = itemInstance.getEtcItem();
		final int reuseDelay = Math.max(itemSkill.getReuseDelay(), etcItem.getReuseDelay());
		
		addTimeStamp(itemSkill, reuseDelay);
		if (reuseDelay != 0)
			disableSkill(itemSkill, reuseDelay);
		
		final int group = etcItem.getSharedReuseGroup();
		if (group >= 0)
			sendPacket(new ExUseSharedGroupItem(etcItem.getItemId(), group, reuseDelay, reuseDelay));
	}
	
	/**
	 * Disable this ItemInstance id for the duration of the delay in milliseconds.
	 * @param item
	 * @param delay (seconds * 1000)
	 */
	public void disableItem(ItemInstance item, long delay)
	{
		if (item == null)
			return;
		
		_disabledItems.put(item.getObjectId(), System.currentTimeMillis() + delay);
	}
	
	/**
	 * Check if an item is disabled. All skills disabled are identified by their reuse objectIds in <B>_disabledItems</B>.
	 * @param item The ItemInstance to check
	 * @return true if the item is currently disabled.
	 */
	public boolean isItemDisabled(ItemInstance item)
	{
		if (_disabledItems.isEmpty())
			return false;
		
		if (item == null || isAllSkillsDisabled())
			return true;
		
		final int hashCode = item.getObjectId();
		
		final Long timeStamp = _disabledItems.get(hashCode);
		if (timeStamp == null)
			return false;
		
		if (timeStamp < System.currentTimeMillis())
		{
			_disabledItems.remove(hashCode);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Check pvp conditions for a {@link Playable}->{@link Playable} offensive {@link L2Skill} cast.
	 * @param target : The {@link Playable} instance used as target.
	 * @param skill : The {@link L2Skill} being casted.
	 * @param isCtrlPressed : If true, the control key was used to cast.
	 * @return True if the {@link L2Skill} is a pvp {@link L2Skill} and target is a valid pvp target, false otherwise.
	 */
	public boolean canCastOffensiveSkillOnPlayable(Playable target, L2Skill skill, boolean isCtrlPressed)
	{
		final Player targetPlayer = target.getActingPlayer();
		
		if (targetPlayer == getActingPlayer())
			return false;
		
		if (isInSameActiveOlympiadMatch(targetPlayer))
			return true;
		
		if (isInSameActiveDuel(targetPlayer))
			return true;
		
		final boolean sameParty = isInSameParty(targetPlayer);
		final boolean sameCommandChannel = isInSameCommandChannel(targetPlayer);
		
		if (isInArena() && target.isInArena() && !(sameParty || sameCommandChannel))
		{
			if ((CTFEvent.getInstance().isStarted() || TvTEvent.getInstance().isStarted()) && targetPlayer.getTeam() == getActingPlayer().getTeam())
				return false;
			
			return true;
		}
		
		final boolean isMainTarget = getAI().getCurrentIntention().getFinalTarget() == target;
		final boolean isCtrlDamagingTheMainTarget = isCtrlPressed && skill.isDamage() && isMainTarget;
		if (sameParty || sameCommandChannel || isInSameClan(targetPlayer) || isInSameAlly(targetPlayer) || isInSameActiveSiegeSide(targetPlayer))
			return isCtrlDamagingTheMainTarget;
		
		if (this instanceof Player)
		{
			Player player = getActingPlayer();
			Player targett = target.getActingPlayer();
			if (player.isInTournament() && targett.isInTournament())
			{
				return true;
			}
			
		}
		
		if (isInsideZone(ZoneId.PVP) && target.isInsideZone(ZoneId.PVP))
			return true;
		
		if (targetPlayer.getProtectionBlessing() && (getActingPlayer().getStatus().getLevel() - targetPlayer.getStatus().getLevel() >= 10) && getActingPlayer().getKarma() > 0)
			return false;
		
		if (getActingPlayer().getProtectionBlessing() && (targetPlayer.getStatus().getLevel() - getActingPlayer().getStatus().getLevel() >= 10) && targetPlayer.getKarma() > 0)
			return false;
		
		if (targetPlayer.isCursedWeaponEquipped() && getActingPlayer().getStatus().getLevel() <= 20)
			return false;
		
		if (getActingPlayer().isCursedWeaponEquipped() && targetPlayer.getStatus().getLevel() <= 20)
			return false;
		
		if (targetPlayer.getPvpFlag() > 0 || targetPlayer.getKarma() > 0)
			return true;
			
		if (isCtrlPressed && this instanceof Player player)
		{
			final AutoFarmProfile profile = AutoFarmManager.getInstance().getProfile(player);
			if (profile != null && profile.isEnabled() && profile.isOffensiveMode())
				return true;
		}
		
		if (isAtWarWith(targetPlayer))
			return isCtrlPressed;
			
		
		return isCtrlDamagingTheMainTarget;
	}
	
	@Override
	public boolean isAttackableBy(Creature attacker)
	{
		if (!super.isAttackableBy(attacker))
			return false;
		
		if (attacker instanceof Monster)
			return true;
		
		if (attacker instanceof SiegeGuard)
		{
			if (getClan() != null)
			{
				final Siege siege = CastleManager.getInstance().getActiveSiege(this);
				if (siege != null && siege.checkSides(getClan(), SiegeSide.DEFENDER, SiegeSide.OWNER))
					return false;
			}
			
			return true;
		}
		
		if ((CTFEvent.getInstance().isStarted() || TvTEvent.getInstance().isStarted()) && getActingPlayer() != null && attacker.getActingPlayer() != null && getActingPlayer().getTeam() == attacker.getActingPlayer().getTeam())
			return false;
		else if ((DMEvent.getInstance().isStarted() || LMEvent.getInstance().isStarted()) && getActingPlayer() != null && attacker.getActingPlayer() != null && getActingPlayer().getTeam() == attacker.getActingPlayer().getTeam())
			return true;
		
		if (attacker instanceof Playable attackerPlayable)
		{
			if (getActingPlayer().isInOlympiadMode() && !getActingPlayer().isOlympiadStart())
				return false;
			
			if (isInsideZone(ZoneId.PVP))
				return true;
			
			if (getProtectionBlessing() && (attackerPlayable.getStatus().getLevel() - getStatus().getLevel() >= 10) && attackerPlayable.getKarma() > 0)
				return false;
			
			if (attackerPlayable.getProtectionBlessing() && (getStatus().getLevel() - attackerPlayable.getStatus().getLevel() >= 10) && getKarma() > 0)
				return false;
			
			if (getActingPlayer().isCursedWeaponEquipped() && attackerPlayable.getStatus().getLevel() <= 20)
				return false;
			
			if (attackerPlayable.getActingPlayer().isCursedWeaponEquipped() && getStatus().getLevel() <= 20)
				return false;
		}
		
		return true;
	}
	
	@Override
	public boolean isAttackableWithoutForceBy(Playable attacker)
	{
		final Player attackerPlayer = attacker.getActingPlayer();
		
		if (attackerPlayer == getActingPlayer())
			return false;
		
		if (isInSameActiveOlympiadMatch(attackerPlayer))
			return true;
		
		if (isInSameActiveDuel(attackerPlayer))
			return true;
		
		if (this instanceof Player)
		{
			Player player = getActingPlayer();
			
			if (player.isInTournament() && attackerPlayer.isInTournament())
			{
				return true;
			}
		}
		
		final boolean sameParty = isInSameParty(attackerPlayer);
		final boolean sameCommandChannel = isInSameCommandChannel(attackerPlayer);
		
		if (isInArena() && attacker.isInArena() && !(sameParty || sameCommandChannel))
		{
			if ((CTFEvent.getInstance().isStarted() || TvTEvent.getInstance().isStarted()) && getActingPlayer().getTeam() == attacker.getActingPlayer().getTeam())
				return false;
			else if ((DMEvent.getInstance().isStarted() || LMEvent.getInstance().isStarted()) && getActingPlayer().getTeam() == attacker.getActingPlayer().getTeam())
				return true;
			
			return true;
		}
		
		if (sameParty || sameCommandChannel || isInSameClan(attackerPlayer) || isInSameAlly(attackerPlayer) || isInSameActiveSiegeSide(attackerPlayer))
			return false;
		
		if (isInsideZone(ZoneId.PVP) && attacker.isInsideZone(ZoneId.PVP))
			return true;
		
		if (getKarma() > 0 || getPvpFlag() > 0)
			return true;
		
		return false;
	}
	
	/**
	 * @param target : The {@link Creature} used as target.
	 * @return True if this {@link Playable} can continue to attack the {@link Creature} set as target, false otherwise.
	 */
	public boolean canKeepAttacking(Creature target)
	{
		if (target == null)
			return false;
		
		if (target instanceof Playable)
		{
			final Player targetPlayer = target.getActingPlayer();
			
			if (targetPlayer.getKarma() > 0)
				return true;
			
			if (isInSameActiveOlympiadMatch(targetPlayer))
				return true;
			
			if (isInSameActiveDuel(targetPlayer))
				return true;
			
			
			if (this instanceof Player)
			{
				Player player = getActingPlayer();
				Player targett = target.getActingPlayer();
				if (player.isInTournament() && targett.isInTournament())
				{
					return true;
				}
				
			}
			
			if (isInsideZone(ZoneId.PVP) && target.isInsideZone(ZoneId.PVP))
				return true;
			
			if (this instanceof Summon && isBetrayed())
				return true;
			
			return false;
		}
		return true;
	}
	
	@Override
	public boolean testCursesOnAttack(Npc npc, int npcId)
	{
		if (Config.RAID_DISABLE_CURSE || !(npc instanceof Attackable attackable))
			return false;
		
		if (getStatus().getLevel() - attackable.getStatus().getLevel() > 8)
		{
			final L2Skill curse = FrequentSkill.RAID_CURSE2.getSkill();
			if (getFirstEffect(curse) == null)
			{
				broadcastPacket(new MagicSkillUse(attackable, this, curse.getId(), curse.getLevel(), 300, 0));
				curse.getEffects(attackable, this);
				
				attackable.getAI().getAggroList().stopHate(this);
				return true;
			}
		}
		
		if (attackable.getNpcId() == npcId && this instanceof Player player && player.isMounted())
		{
			final L2Skill curse = FrequentSkill.RAID_ANTI_STRIDER_SLOW.getSkill();
			if (getFirstEffect(curse) == null)
			{
				broadcastPacket(new MagicSkillUse(attackable, player, curse.getId(), curse.getLevel(), 300, 0));
				curse.getEffects(attackable, player);
			}
		}
		return false;
	}
	
	@Override
	public boolean testCursesOnAttack(Npc npc)
	{
		return testCursesOnAttack(npc, npc.getNpcId());
	}
	
	@Override
	public boolean testCursesOnAggro(Npc npc)
	{
		return testCursesOnAttack(npc, -1);
	}
	
	@Override
	public boolean testCursesOnSkillSee(L2Skill skill, Creature[] targets)
	{
		if (Config.RAID_DISABLE_CURSE)
			return false;
		
		final boolean isAggressive = skill.isOffensive() || skill.isDebuff();
		
		if (isAggressive)
		{
			for (final Creature target : targets)
			{
				if (!(target instanceof Attackable targetAttackable) || !targetAttackable.isRaidRelated())
					continue;
				
				if (getStatus().getLevel() - targetAttackable.getStatus().getLevel() > 8)
				{
					final L2Skill curse = FrequentSkill.RAID_CURSE2.getSkill();
					if (getFirstEffect(curse) == null)
					{
						broadcastPacket(new MagicSkillUse(targetAttackable, this, curse.getId(), curse.getLevel(), 300, 0));
						curse.getEffects(targetAttackable, this);
						
						targetAttackable.getAI().getAggroList().stopHate(this);
						return true;
					}
				}
			}
			return false;
		}
		
		final List<Attackable> list = getKnownTypeInRadius(Attackable.class, 1000);
		if (!list.isEmpty())
		{
			for (final Creature target : targets)
			{
				if (!(target instanceof Playable))
					continue;
				
				for (Attackable attackable : list)
				{
					if (!attackable.isRaidRelated())
						continue;
					
					if (getStatus().getLevel() - attackable.getStatus().getLevel() > 8)
					{
						final AggroInfo ai = attackable.getAI().getAggroList().get(target);
						if (ai != null && ai.getHate() > 0)
						{
							final L2Skill curse = FrequentSkill.RAID_CURSE.getSkill();
							if (getFirstEffect(curse) == null)
							{
								broadcastPacket(new MagicSkillUse(attackable, this, curse.getId(), curse.getLevel(), 300, 0));
								curse.getEffects(attackable, this);
								
								attackable.getAI().getAggroList().stopHate(this);
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * @param playable : The {@link Playable} to test.
	 * @return True if both this {@link Playable} and tested {@link Playable} share the same active {@link Duel}.
	 */
	public final boolean isInSameActiveDuel(Playable playable)
	{
		return getActingPlayer().getDuelState() == DuelState.DUELLING && playable.getActingPlayer().getDuelState() == DuelState.DUELLING && getActingPlayer().getDuelId() == playable.getActingPlayer().getDuelId();
	}
	
	/**
	 * @param playable : The {@link Playable} to test.
	 * @return True if both this {@link Playable} and tested {@link Playable} share the same active {@link Duel}.
	 */
	public final boolean isInSameActiveOlympiadMatch(Playable playable)
	{
		return getActingPlayer().isOlympiadStart() && playable.getActingPlayer().isOlympiadStart() && getActingPlayer().getOlympiadGameId() == playable.getActingPlayer().getOlympiadGameId();
	}
	
	/**
	 * @param playable : The {@link Playable} to test.
	 * @return True if both this {@link Playable} and tested {@link Playable} share the same {@link Party}.
	 */
	public final boolean isInSameParty(Playable playable)
	{
		return isInParty() && getParty().containsPlayer(playable.getActingPlayer());
	}
	
	/**
	 * @param playable : The {@link Playable} to test.
	 * @return True if both this {@link Playable} and tested {@link Playable} share the same {@link CommandChannel}.
	 */
	public final boolean isInSameCommandChannel(Playable playable)
	{
		return isInParty() && getParty().getCommandChannel() != null && getParty().getCommandChannel().containsPlayer(playable.getActingPlayer());
	}
	
	/**
	 * @param playable : The {@link Playable} to test.
	 * @return True if both this {@link Playable} and tested {@link Playable} share the same {@link Clan}.
	 */
	public final boolean isInSameClan(Playable playable)
	{
		return getClanId() > 0 && getClanId() == playable.getClanId();
	}
	
	/**
	 * @param playable : The {@link Playable} to test.
	 * @return True if both this {@link Playable} and tested {@link Playable} share the same alliance id.
	 */
	public final boolean isInSameAlly(Playable playable)
	{
		return getActingPlayer().getAllyId() > 0 && getActingPlayer().getAllyId() == playable.getActingPlayer().getAllyId();
	}
	
	/**
	 * @param playable : The {@link Playable} to test.
	 * @return True if and only if an active siege is set, where both this {@link Playable} and tested {@link Playable} share the same {@link SiegeSide}.
	 */
	public final boolean isInSameActiveSiegeSide(Playable playable)
	{
		if (!isInsideZone(ZoneId.SIEGE) || !playable.isInsideZone(ZoneId.SIEGE))
			return false;
		
		final Siege siege = CastleManager.getInstance().getActiveSiege(this);
		if (siege == null)
			return false;
		
		return !siege.isOnOppositeSide(getClan(), playable.getClan());
	}
	
	/**
	 * @param playable : The {@link Playable} to test.
	 * @return True if this {@link Playable} is at war with tested {@link Playable}.
	 */
	public final boolean isAtWarWith(Playable playable)
	{
		final Clan aClan = getClan();
		final Clan tClan = playable.getClan();
		return aClan != null && tClan != null && aClan.isAtWarWith(tClan.getClanId()) && tClan.isAtWarWith(aClan.getClanId());
	}
	
	@Override
	public void fleeFrom(Creature attacker, int distance)
	{
		if (attacker == null || attacker == this || distance < 10)
			return;
		
		forceRunStance();
		
		final Location loc = getPosition().clone();
		loc.setFleeing(attacker.getPosition(), distance);
		
		getAI().tryToMoveTo(loc, null);
	}
	
	@Override
	public void moveUsingRandomOffset(int offset)
	{
		if (offset < 10)
			return;
		
		final Location loc = getPosition().clone();
		loc.addRandomOffset(offset);
		
		getAI().tryToMoveTo(loc, null);
	}
	
	public ItemInstance checkItemManipulation(int objectId, int count)
	{
		return null;
	}
	
	public ItemInstance transferItem(int objectId, int amount, Playable target)
	{
		final ItemInstance newItem = getInventory().transferItem(objectId, amount, target);
		if (newItem == null)
			return null;
		
		return newItem;
	}
	
	public Boat getDockedBoat()
	{
		return getKnownType(Boat.class).stream().filter(b -> b.getEngine().getDock() != null).findFirst().orElse(null);
	}
	
	public boolean tryToPassBoatEntrance(Point2D targetLoc, boolean isArrowMovement)
	{
		final Boat boat = getDockedBoat();
		if (boat == null)
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		final BoatDock dock = boat.getDock();
		if (boat.getDock() == null)
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		final Point2D point = dock.getBoardingPoint(getPosition(), targetLoc, isInBoat());
		if (point == null)
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		if (isArrowMovement && distance2D(new Location(point.getX(), point.getY(), getZ())) < 48)
		{
			final Player player = getActingPlayer();
			final Point2D dest = dock.convertWorldToBoatCoordinates(targetLoc.getX(), targetLoc.getY());
			final Point2D orig = dock.convertWorldToBoatCoordinates(player.getX(), player.getY());
			
			player.getBoatInfo().getBoatPosition().set(dest.getX(), dest.getY(), 0);
			player.broadcastPacket(new MoveToLocationInVehicle(player, boat, dest.getX(), dest.getY(), 0, orig.getX(), orig.getY(), 0));
		}
		else
			moveToBoatEntrance(point, boat);
		
		return true;
	}
	
	public void moveToBoatEntrance(Point2D point, Boat boat)
	{
		final Location destination = new Location(point.getX(), point.getY(), -3624);
		
		if (distance2D(destination) > 50)
		{
			getAI().tryToMoveTo(destination, boat);
			return;
		}
		
		if (this instanceof Player player)
			player.getBoatInfo().setCanBoard(true);
		
		sendPacket(ActionFailed.STATIC_PACKET);
	}
}
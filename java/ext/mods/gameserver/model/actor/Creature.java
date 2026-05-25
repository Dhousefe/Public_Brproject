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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import ext.mods.Config;
import ext.mods.commons.lang.StringUtil;
import ext.mods.commons.random.Rnd;
import ext.mods.extensions.listener.manager.CreatureListenerManager;
import ext.mods.gameserver.data.manager.BotsPreventionManager;
import ext.mods.gameserver.data.manager.ZoneManager;
import ext.mods.gameserver.data.xml.NpcData;
import ext.mods.gameserver.enums.AiEventType;
import ext.mods.gameserver.enums.EventHandler;
import ext.mods.gameserver.enums.RestartType;
import ext.mods.gameserver.enums.StatusType;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.enums.actors.MissionType;
import ext.mods.gameserver.enums.actors.MoveType;
import ext.mods.gameserver.enums.items.WeaponType;
import ext.mods.gameserver.enums.skills.AbnormalEffect;
import ext.mods.gameserver.enums.skills.EffectFlag;
import ext.mods.gameserver.enums.skills.EffectType;
import ext.mods.gameserver.enums.skills.SkillType;
import ext.mods.gameserver.enums.skills.Stats;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.WorldRegion;
import ext.mods.gameserver.model.actor.ai.type.CreatureAI;
import ext.mods.gameserver.model.actor.attack.CreatureAttack;
import ext.mods.gameserver.model.actor.cast.CreatureCast;
import ext.mods.gameserver.model.actor.container.creature.ChanceSkillList;
import ext.mods.gameserver.model.actor.container.creature.EffectList;
import ext.mods.gameserver.model.actor.container.creature.FusionSkill;
import ext.mods.gameserver.model.actor.container.player.MissionList;
import ext.mods.gameserver.model.actor.instance.GrandBoss;
import ext.mods.gameserver.model.actor.instance.Monster;
import ext.mods.gameserver.model.actor.instance.RaidBoss;
import ext.mods.gameserver.model.actor.instance.SiegeGuard;
import ext.mods.gameserver.model.actor.move.CreatureMove;
import ext.mods.gameserver.model.actor.status.CreatureStatus;
import ext.mods.gameserver.model.actor.template.CreatureTemplate;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.Item;
import ext.mods.gameserver.model.item.kind.Weapon;
import ext.mods.gameserver.model.itemcontainer.Inventory;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.zone.type.WaterZone;
import ext.mods.gameserver.network.serverpackets.AbstractNpcInfo.NpcInfo;
import ext.mods.gameserver.network.serverpackets.ChangeMoveType;
import ext.mods.gameserver.network.serverpackets.L2GameServerPacket;
import ext.mods.gameserver.network.serverpackets.Revive;
import ext.mods.gameserver.network.serverpackets.ServerObjectInfo;
import ext.mods.gameserver.network.serverpackets.StatusUpdate;
import ext.mods.gameserver.network.serverpackets.TeleportToLocation;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.skills.AbstractEffect;
import ext.mods.gameserver.skills.Calculator;
import ext.mods.gameserver.skills.IChanceSkillTrigger;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.skills.basefuncs.Func;
import ext.mods.gameserver.skills.effects.EffectChanceSkillTrigger;
import ext.mods.gameserver.skills.funcs.FuncAtkAccuracy;
import ext.mods.gameserver.skills.funcs.FuncAtkCritical;
import ext.mods.gameserver.skills.funcs.FuncAtkEvasion;
import ext.mods.gameserver.skills.funcs.FuncMAtkMod;
import ext.mods.gameserver.skills.funcs.FuncMAtkSpeed;
import ext.mods.gameserver.skills.funcs.FuncMDefMod;
import ext.mods.gameserver.skills.funcs.FuncMaxHpMul;
import ext.mods.gameserver.skills.funcs.FuncMaxMpMul;
import ext.mods.gameserver.skills.funcs.FuncMoveSpeed;
import ext.mods.gameserver.skills.funcs.FuncPAtkMod;
import ext.mods.gameserver.skills.funcs.FuncPAtkSpeed;
import ext.mods.gameserver.skills.funcs.FuncPDefMod;
import ext.mods.gameserver.skills.funcs.FuncRegenHpMul;
import ext.mods.gameserver.skills.funcs.FuncRegenMpMul;
import ext.mods.gameserver.taskmanager.AttackStanceTaskManager;
import ext.mods.gameserver.taskmanager.DecayTaskManager;

public abstract class Creature extends WorldObject
{
	protected String _title;
	
	private CreatureTemplate _template;
	private NpcTemplate _polymorphTemplate;
	
	protected CreatureAI<? extends Creature> _ai;
	protected CreatureStatus<? extends Creature> _status;
	protected CreatureMove<? extends Creature> _move;
	protected CreatureAttack<? extends Creature> _attack;
	protected CreatureCast<? extends Creature> _cast;
	
	private WorldObject _target;
	
	private boolean _isImmobilized;
	private boolean _isParalyzed;
	private boolean _isDead;
	private boolean _isRunning;
	private boolean _showSummonAnimation;
	
	protected AtomicBoolean _isTeleporting = new AtomicBoolean();
	
	private boolean _isInvul;
	private boolean _isMortal = true;
	
	private final Calculator[] _calculators;
	
	private ChanceSkillList _chanceSkills;
	private FusionSkill _fusionSkill;
	
	private final byte[] _zones = new byte[ZoneId.VALUES.length];
	protected byte _zoneValidateCounter = 4;
	
	protected final EffectList _effects = new EffectList(this);
	private int _abnormalEffects;
	
	private final Map<Integer, Long> _disabledSkills = new ConcurrentHashMap<>();
	private boolean _allSkillsDisabled;
	
	private int _premiumService;
	
	private boolean _isFlying;
	
    private boolean _townZone;

	public Creature(int objectId, CreatureTemplate template)
	{
		super(objectId);
		
		_template = template;
		_calculators = new Calculator[Stats.NUM_STATS];
		
		addFuncsToNewCharacter();
		
		setAI();
		setStatus();
		setMove();
		setAttack();
		setCast();
	}
	
	public abstract void updateAbnormalEffect();
	public abstract ItemInstance getActiveWeaponInstance();
	public abstract Weapon getActiveWeaponItem();
	public abstract ItemInstance getSecondaryWeaponInstance();
	public abstract Item getSecondaryWeaponItem();
	
	@Override
	public boolean knows(WorldObject target)
	{
		if (!super.knows(target))
			return false;
		
		if (!isGM())
		{
			final Player player = target.getActingPlayer();
			if (player != null && !player.getAppearance().isVisible())
				return false;
		}
		return true;
	}
	
	public void addFuncsToNewCharacter()
	{
		addStatFunc(FuncPAtkMod.getInstance());
		addStatFunc(FuncMAtkMod.getInstance());
		addStatFunc(FuncPDefMod.getInstance());
		addStatFunc(FuncMDefMod.getInstance());
		
		addStatFunc(FuncMaxHpMul.getInstance());
		addStatFunc(FuncMaxMpMul.getInstance());
		addStatFunc(FuncRegenHpMul.getInstance());
		addStatFunc(FuncRegenMpMul.getInstance());
		
		addStatFunc(FuncAtkAccuracy.getInstance());
		addStatFunc(FuncAtkEvasion.getInstance());
		
		addStatFunc(FuncPAtkSpeed.getInstance());
		addStatFunc(FuncMAtkSpeed.getInstance());
		
		addStatFunc(FuncMoveSpeed.getInstance());
		addStatFunc(FuncAtkCritical.getInstance());
	}
	
	public void onDecay()
	{
		decayMe();
	}
	
	public void onTeleported()
	{
		if (!_isTeleporting.compareAndSet(true, false))
			return;
		
		setRegion(World.getInstance().getRegion(getPosition()));
	}
	
	public Inventory getInventory()
	{
		return null;
	}
	
	public boolean destroyItemByItemId(int itemId, int count, boolean sendMessage)
	{
		return true;
	}
	
	public boolean destroyItem(int objectId, int count, boolean sendMessage)
	{
		return true;
	}
	
	@Override
	public boolean isInsideZone(ZoneId zone)
	{
		return zone == ZoneId.PVP ? _zones[ZoneId.PVP.getId()] > 0 && _zones[ZoneId.PEACE.getId()] == 0 : _zones[zone.getId()] > 0;
	}
	
	public void setInsideZone(ZoneId zone, boolean state)
	{
		if (state)
			_zones[zone.getId()]++;
		else
		{
			_zones[zone.getId()]--;
			if (_zones[zone.getId()] < 0)
				_zones[zone.getId()] = 0;
		}
	}
	
	public boolean isGM()
	{
		return false;
	}
	
	/**
	 * OTIMIZAÇÃO ZERO GC: Evita alocação de lambdas se possível.
	 * Se 'forEachKnownType' usar lambdas internamente, considere iterar manualmente a KnownList.
	 * Aqui mantivemos o método mas é crucial que ele não aloque iteradores.
	 */
	public void broadcastPacket(L2GameServerPacket packet)
	{
		broadcastPacket(packet, true);
	}
	
	public void broadcastPacket(L2GameServerPacket packet, boolean selfToo)
	{
		forEachKnownType(Player.class, player ->
		{
			if (isVisibleTo(player))
				player.sendPacket(packet);
		});
	}
	
	public void broadcastPacketInRadius(L2GameServerPacket packet, int radius)
	{
		if (radius < 0)
			radius = 600;
		
		forEachKnownTypeInRadius(Player.class, radius, player ->
		{
			if (isVisibleTo(player))
				player.sendPacket(packet);
		});
	}
	
	public void sendPacket(L2GameServerPacket packet) {}
	public void sendMessage(String text) {}
	
	public void instantTeleportTo(int x, int y, int z, int randomOffset)
	{
		if (isDead())
			return;
		
		if (randomOffset > 0)
		{
			final int nx = x + Rnd.get(-randomOffset, randomOffset);
			final int ny = y + Rnd.get(-randomOffset, randomOffset);
			final Location loc = GeoEngine.getInstance().getValidLocation(x, y, z, nx, ny, z, null);
			x = loc.getX();
			y = loc.getY();
		}
		
		if (!isFlying() && ZoneManager.getInstance().getZone(x, y, z, WaterZone.class) == null)
			z = GeoEngine.getInstance().getHeight(x, y, z);
		
		broadcastPacket(new TeleportToLocation(this, x, y, z, true));
		getPosition().set(x, y, z);
		
		getAI().notifyEvent(AiEventType.TELEPORTED, null, null);
		refreshKnownlist();
		forceSeeCreature();
		
		getMove().resetGeoPathFailCount();
        getMove().stop();
	}
	
	public void instantTeleportTo(Location loc, int randomOffset)
	{
		instantTeleportTo(loc.getX(), loc.getY(), loc.getZ(), randomOffset);
	}
	
	public boolean teleportTo(int x, int y, int z, int randomOffset)
	{
		if (isDead())
			return false;
		
		if (!_isTeleporting.compareAndSet(false, true))
			return false;
		
		abortAll(true);
		
		if (randomOffset > 0)
		{
			final int nx = x + Rnd.get(-randomOffset, randomOffset);
			final int ny = y + Rnd.get(-randomOffset, randomOffset);
			final Location loc = GeoEngine.getInstance().getValidLocation(x, y, z, nx, ny, z, null);
			x = loc.getX();
			y = loc.getY();
		}
		
		if (!isFlying() && ZoneManager.getInstance().getZone(x, y, z, WaterZone.class) == null)
			z = GeoEngine.getInstance().getHeight(x, y, z);
		
		broadcastPacket(new TeleportToLocation(this, x, y, z, false));
		setRegion(null);
		getPosition().set(x, y, z);
		
		if (!(this instanceof Player player) || (player.getClient() != null && player.getClient().isDetached()))
			onTeleported();
		
		getAI().notifyEvent(AiEventType.TELEPORTED, null, null);
		forceSeeCreature();
		
		getMove().resetGeoPathFailCount();
        getMove().stop();

		return true;
	}
	
	public void teleportTo(Location loc, int randomOffset)
	{
		teleportTo(loc.getX(), loc.getY(), loc.getZ(), randomOffset);
	}
	
	public void teleportTo(RestartType type) {}
	
	public void addTimeStamp(L2Skill skill, long reuse) {}
	
	public void startFusionSkill(Creature target, L2Skill skill)
	{
		if (_fusionSkill == null)
			_fusionSkill = new FusionSkill(this, target, skill);
	}
	
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
		stopAllEffectsExceptThoseThatLastThroughDeath();
		
		calculateRewards(killer);
		calculateMission(killer);
		
		if (Config.BOTS_PREVENTION)
			BotsPreventionManager.getInstance().updateCounter(killer, this);
		
		getStatus().broadcastStatusUpdate();
		getAI().notifyEvent(AiEventType.DEAD, null, null);
		
		return true;
	}
	
	public void deleteMe()
	{
		getStatus().stopHpMpRegeneration();
		getAI().stopAITask();
	}
	
	protected void calculateRewards(Creature creature) {}
	
	public void doRevive()
	{
		if (!isDead() || isTeleporting())
			return;
		
		setIsDead(false);
		_status.setHp(_status.getMaxHp() * Config.RESPAWN_RESTORE_HP);
		broadcastPacket(new Revive(this));
		CreatureListenerManager.getInstance().notifyRevive(this);
	}
	
	public void doRevive(double revivePower)
	{
		doRevive();
	}
	
	public boolean isRaidBoss() { return false; }
	public boolean isRaidRelated() { return false; }
	
	public final boolean isAfraid() { return isAffected(EffectFlag.FEAR); }
	public final boolean isConfused() { return isAffected(EffectFlag.CONFUSED); }
	public final boolean isMuted() { return isAffected(EffectFlag.MUTED); }
	public final boolean isPhysicalMuted() { return isAffected(EffectFlag.PHYSICAL_MUTED); }
	public final boolean isRooted() { return isAffected(EffectFlag.ROOTED); }
	public final boolean isSleeping() { return isAffected(EffectFlag.SLEEP); }
	public final boolean isStunned() { return isAffected(EffectFlag.STUNNED); }
	public final boolean isBetrayed() { return isAffected(EffectFlag.BETRAYED); }
	public final boolean isImmobileUntilAttacked() { return isAffected(EffectFlag.MEDITATING); }
	
	public final boolean isAllSkillsDisabled()
	{
		return getAllSkillsDisabled() || isStunned() || isImmobileUntilAttacked() || isSleeping() || isParalyzed() || isAfraid();
	}
	
	public boolean isAttackingDisabled()
	{
		return isFlying() || isStunned() || isImmobileUntilAttacked() || isSleeping() || isParalyzed() || isAlikeDead() || isAfraid();
	}
	
	public boolean denyAiAction()
	{
		return isStunned() || isImmobileUntilAttacked() || isSleeping() || isParalyzed() || isTeleporting() || isDead() || isAfraid();
	}
	
	public boolean isMovementDisabled()
	{
		return isStunned() || isImmobileUntilAttacked() || isRooted() || isSleeping() || isParalyzed() || isImmobilized() || isAlikeDead() || isTeleporting() || isSitting() || isSittingNow() || isStandingNow();
	}
	
	public boolean isOutOfControl()
	{
		return isStunned() || isImmobileUntilAttacked() || isSleeping() || isParalyzed() || isAfraid() || isConfused() || isTeleporting() || isDead();
	}
	
	public final Calculator[] getCalculators()
	{
		return _calculators;
	}
	
	public boolean isImmobilized() { return _isImmobilized; }
	public void setIsImmobilized(boolean value) { _isImmobilized = value; }
	
	public boolean isAlikeDead() { return _isDead; }
	public boolean isFakeDeath() { return false; }
	public final boolean isDead() { return _isDead; }
	
	@Override
	public boolean isAttackableBy(Creature attacker) { return attacker != this; }
	@Override
	public boolean isAttackableWithoutForceBy(Playable attacker) { return false; }
	
	public final void setIsDead(boolean value) { _isDead = value; }
	
	public final boolean isParalyzed()
	{
		return _isParalyzed || isAffected(EffectFlag.PARALYZED);
	}
	
	public final void setIsParalyzed(boolean value) { _isParalyzed = value; }
	
	public Summon getSummon() { return null; }
	
	public boolean isOperating() { return false; }
	public boolean isSeated() { return false; }
	public boolean isStandingNow() { return false; }
	public boolean isStanding() { return false; }
	public boolean isSittingNow() { return false; }
	public boolean isSitting() { return false; }
	public boolean isRiding() { return false; }
	public boolean isFlying() { return _isFlying; }
	public final boolean isRunning() { return _isRunning; }
	
	public void setWalkOrRun(boolean value)
	{
		_isRunning = value;
		if (_status.getMoveSpeed() != 0)
			broadcastPacket(new ChangeMoveType(this));
	}
	
	public final void forceWalkStance()
	{
		if (isRunning())
			setWalkOrRun(false);
	}
	
	public final void forceRunStance()
	{
		if (!isRunning())
			setWalkOrRun(true);
	}
	
	public final boolean isTeleporting() { return _isTeleporting.get(); }
	public final void setRunning(boolean value) { _isRunning = value; }
	
	public boolean isInvul()
	{
		return _isInvul || isTeleporting();
	}
	
	public void setInvul(boolean value) { _isInvul = value; }
	
	public boolean isMortal() { return _isMortal; }
	public void setMortal(boolean value) { _isMortal = value; }
	public boolean isLethalable() { return true; }
	public boolean isUndead() { return false; }
	
	public CreatureAI<? extends Creature> getAI() { return _ai; }
	public void setAI() { _ai = new CreatureAI<>(this); }
	
	public CreatureStatus<? extends Creature> getStatus() { return _status; }
	public void setStatus() { _status = new CreatureStatus<>(this); }
	
	public CreatureMove<? extends Creature> getMove() { return _move; }
	public void setMove() { _move = new CreatureMove<>(this); }
	
	public CreatureAttack<? extends Creature> getAttack() { return _attack; }
	public void setAttack() { _attack = new CreatureAttack<>(this); }
	
	public CreatureCast<? extends Creature> getCast() { return _cast; }
	public void setCast() { _cast = new CreatureCast<>(this); }
	
	public CreatureTemplate getTemplate() { return _template; }
	protected final void setTemplate(CreatureTemplate template) { _template = template; }
	
	public final String getTitle() { return _title; }
	public void setTitle(String value)
	{
		_title = StringUtil.trim(value, 16, "");
	}
	
	public int getAbnormalEffect()
	{
		int ae = _abnormalEffects;
		if (isStunned()) ae |= AbnormalEffect.STUN.getMask();
		if (isRooted()) ae |= AbnormalEffect.ROOT.getMask();
		if (isSleeping()) ae |= AbnormalEffect.SLEEP.getMask();
		if (isConfused()) ae |= AbnormalEffect.FEAR.getMask();
		if (isAfraid()) ae |= AbnormalEffect.FEAR.getMask();
		if (isMuted()) ae |= AbnormalEffect.MUTED.getMask();
		if (isPhysicalMuted()) ae |= AbnormalEffect.MUTED.getMask();
		if (isImmobileUntilAttacked()) ae |= AbnormalEffect.FLOATING_ROOT.getMask();
		return ae;
	}
	
	public final void startAbnormalEffect(AbnormalEffect mask) { _abnormalEffects |= mask.getMask(); updateAbnormalEffect(); }
	public final void startAbnormalEffect(int mask) { _abnormalEffects |= mask; updateAbnormalEffect(); }
	public final void stopAbnormalEffect(AbnormalEffect mask) { _abnormalEffects &= ~mask.getMask(); updateAbnormalEffect(); }
	public final void stopAbnormalEffect(int mask) { _abnormalEffects &= ~mask; updateAbnormalEffect(); }
	
	public void addEffect(AbstractEffect effect)
	{
		if (effect.getEffected() instanceof Npc npc)
		{
			for (Quest quest : npc.getTemplate().getEventQuests(EventHandler.SEE_SPELL))
				quest.onAbnormalStatusChanged(npc, effect.getEffector(), effect.getSkill());
		}
		_effects.queueEffect(effect, false);
	}
	
	public final void removeEffect(AbstractEffect effect) { _effects.queueEffect(effect, true); }
	public void stopAllEffects() { _effects.stopAllEffects(); }
	public void stopAllEffectsDebuff() { _effects.stopAllEffectDebuff(); }
	public void stopAllEffectsExceptThoseThatLastThroughDeath() { _effects.stopAllEffectsExceptThoseThatLastThroughDeath(); }
	
	public final void stopSkillEffects(int skillId) { _effects.stopSkillEffects(skillId); }
	public final void stopSkillEffects(SkillType skillType, int negateLvl) { _effects.stopSkillEffects(skillType, negateLvl); }
	public final void stopSkillEffects(SkillType skillType) { _effects.stopSkillEffects(skillType, -1); }
	public final void stopEffects(EffectType type) { _effects.stopEffects(type); }
	
	public final AbstractEffect[] getAllEffects() { return _effects.getAllEffects(); }
	public final AbstractEffect getFirstEffect(int skillId) { return _effects.getFirstEffect(skillId); }
	public final AbstractEffect getFirstEffect(L2Skill skill) { return _effects.getFirstEffect(skill); }
	public final AbstractEffect getFirstEffect(EffectType type) { return _effects.getFirstEffect(type); }
	
	public final void updateEffectIcons() { updateEffectIcons(false); }
	public void updateEffectIcons(boolean partyOnly) {}
	
	public final void addStatFunc(Func function)
	{
		if (function == null) return;
		final int stat = function.getStat().ordinal();
		synchronized (_calculators)
		{
			if (_calculators[stat] == null) _calculators[stat] = new Calculator();
			_calculators[stat].addFunc(function);
		}
	}
	
	/**
	 * OTIMIZAÇÃO: Remove a criação de ArrayList temporário
	 */
	public final void addStatFuncs(List<Func> funcs)
	{
		if (funcs == null || funcs.isEmpty()) return;
        
		for (Func f : funcs)
		{
			addStatFunc(f);
		}
        broadcastModifiedStats(funcs.stream().map(Func::getStat).collect(java.util.stream.Collectors.toList()));
	}
	
	public final void removeStatsByOwner(Object owner)
	{
		List<Stats> modifiedStats = null;
		int i = 0;
		synchronized (_calculators)
		{
			for (final Calculator calc : _calculators)
			{
				if (calc != null)
				{
					if (modifiedStats != null)
						modifiedStats.addAll(calc.removeOwner(owner));
					else
						modifiedStats = calc.removeOwner(owner);
					
					if (calc.size() == 0)
						_calculators[i] = null;
				}
				i++;
			}
			
			if (owner instanceof AbstractEffect ae)
			{
				if (!ae.cantUpdateAnymore())
					broadcastModifiedStats(modifiedStats);
			}
			else
				broadcastModifiedStats(modifiedStats);
		}
	}
	
	private void broadcastModifiedStats(List<Stats> stats)
	{
		if (stats == null || stats.isEmpty()) return;
		
		boolean broadcastFull = false;
		StatusUpdate su = null;
		
		if (this instanceof Summon summon && summon.getOwner() != null)
			summon.updateAndBroadcastStatusAndInfos(1);
		else
		{
			for (final Stats stat : stats)
			{
				if (stat == Stats.POWER_ATTACK_SPEED)
				{
					if (su == null) su = new StatusUpdate(this);
					su.addAttribute(StatusType.ATK_SPD, _status.getPAtkSpd());
				}
				else if (stat == Stats.MAGIC_ATTACK_SPEED)
				{
					if (su == null) su = new StatusUpdate(this);
					su.addAttribute(StatusType.CAST_SPD, _status.getMAtkSpd());
				}
				else if (stat == Stats.MAX_HP && this instanceof Attackable)
				{
					if (su == null) su = new StatusUpdate(this);
					su.addAttribute(StatusType.MAX_HP, _status.getMaxHp());
				}
				else if (stat == Stats.RUN_SPEED)
					broadcastFull = true;
			}
		}
		
		if (this instanceof Player player)
		{
			if (broadcastFull)
				player.updateAndBroadcastStatus(2);
			else
			{
				player.updateAndBroadcastStatus(1);
				if (su != null) broadcastPacket(su);
			}
		}
		else if (this instanceof Npc npc)
		{
			if (broadcastFull)
			{
				forEachKnownType(Player.class, player ->
				{
					if (_status.getMoveSpeed() == 0)
						player.sendPacket(new ServerObjectInfo(npc, player));
					else
						player.sendPacket(new NpcInfo(npc, player));
				});
			}
			else if (su != null)
				broadcastPacket(su);
		}
		else if (su != null)
			broadcastPacket(su);
	}
	
	public boolean isInCombat()
	{
		return AttackStanceTaskManager.getInstance().isInAttackStance(this);
	}
	
	public final boolean isMoving()
	{
		return getMove().getTask() != null;
	}
	
	public void abortAll(boolean resetTarget)
	{
		_move.stop();
		_attack.stop();
		_cast.stop();
		if (resetTarget) setTarget(null);
	}
	
	public boolean isInWater()
	{
		if (this instanceof Npc npc)
		{
			if (npc.getNpcId() == 32108)
				_move.removeMoveType(MoveType.SWIM);
		}
		return _move.getMoveType() == MoveType.SWIM;
	}
	
	public void revalidateZone(boolean force)
	{
		if (getRegion() == null) return;
		if (force) _zoneValidateCounter = 4;
		else
		{
			_zoneValidateCounter--;
			if (_zoneValidateCounter < 0) _zoneValidateCounter = 4;
			else return;
		}
		getRegion().revalidateZones(this);
	}
	
	public boolean isShowSummonAnimation() { return _showSummonAnimation; }
	public void setShowSummonAnimation(boolean showSummonAnimation) { _showSummonAnimation = showSummonAnimation; }
	
	public void setTarget(WorldObject object)
	{
		if (object != null && !object.isVisible()) object = null;
		_target = object;
	}
	
	public final int getTargetId() { return (_target != null) ? _target.getObjectId() : -1; }
	public final WorldObject getTarget() { return _target; }
	
	public boolean checkAndEquipArrows() { return true; }
	public void addExpAndSp(long addToExp, int addToSp) {}
	
	public WeaponType getAttackType()
	{
		final Weapon weapon = getActiveWeaponItem();
		return (weapon == null) ? WeaponType.NONE : weapon.getItemType();
	}
	
	public void reduceArrowCount() {}
	
	@Override
	public void onAction(Player player, boolean isCtrlPressed, boolean isShiftPressed)
	{
		if (player.getTarget() != this)
			player.setTarget(this);
		else
		{
			if (isAttackableWithoutForceBy(player) || (isCtrlPressed && isAttackableBy(player)))
				player.getAI().tryToAttack(this, isCtrlPressed, isShiftPressed);
			else
				player.getAI().tryToInteract(this, isCtrlPressed, isShiftPressed);
		}
	}
	
	public boolean isInActiveRegion()
	{
		final WorldRegion region = getRegion();
		return region != null && region.isActive();
	}
	
	public boolean isInParty() { return false; }
	public Party getParty() { return null; }
	
	public ChanceSkillList getChanceSkills() { return _chanceSkills; }
	
	public void removeChanceSkill(int id)
	{
		if (_chanceSkills == null) return;
		for (final IChanceSkillTrigger trigger : _chanceSkills.keySet())
		{
			if (!(trigger instanceof L2Skill skill)) continue;
			if (skill.getId() == id) _chanceSkills.remove(skill);
		}
	}
	
	public void addChanceTrigger(IChanceSkillTrigger trigger)
	{
		if (_chanceSkills == null) _chanceSkills = new ChanceSkillList(this);
		_chanceSkills.put(trigger, trigger.getTriggeredChanceCondition());
	}
	
	public void removeChanceEffect(EffectChanceSkillTrigger effect)
	{
		if (_chanceSkills == null) return;
		_chanceSkills.remove(effect);
	}
	
	public Map<Integer, L2Skill> getSkills() { return Collections.emptyMap(); }
	
	public int getSkillLevel(int skillId)
	{
		final L2Skill skill = getSkills().get(skillId);
		return (skill == null) ? 0 : skill.getLevel();
	}
	
	public L2Skill getSkill(int skillId) { return getSkills().get(skillId); }
	public boolean hasSkill(int skillId) { return getSkills().containsKey(skillId); }
	
	public int getBuffCount() { return _effects.getBuffCount(); }
	public int getDanceCount() { return _effects.getDanceCount(); }
	public Map<Integer, Long> getDisabledSkills() { return _disabledSkills; }
	
	public void enableSkill(L2Skill skill)
	{
		if (skill == null) return;
		_disabledSkills.remove(skill.getReuseHashCode());
	}
	
	public void disableSkill(L2Skill skill, long delay)
	{
		if (skill == null) return;
		_disabledSkills.put(skill.getReuseHashCode(), (delay > 10) ? System.currentTimeMillis() + delay : Long.MAX_VALUE);
	}
	
	public boolean isSkillDisabled(L2Skill skill)
	{
		if (_disabledSkills.isEmpty()) return false;
		if (skill == null || isAllSkillsDisabled()) return true;
		final int hashCode = skill.getReuseHashCode();
		final Long timeStamp = _disabledSkills.get(hashCode);
		if (timeStamp == null) return false;
		if (timeStamp < System.currentTimeMillis())
		{
			_disabledSkills.remove(hashCode);
			return false;
		}
		return true;
	}
	
	public void disableAllSkills() { _allSkillsDisabled = true; }
	public void enableAllSkills() { _allSkillsDisabled = false; }
	public boolean getAllSkillsDisabled() { return _allSkillsDisabled; }
	
	public void reduceCurrentHp(double i, Creature attacker, L2Skill skill)
	{
		reduceCurrentHp(i, attacker, true, false, skill);
	}
	
	public void reduceCurrentHpByDOT(double i, Creature attacker, L2Skill skill)
	{
		reduceCurrentHp(i, attacker, !skill.isToggle(), true, skill);
	}
	
	public void reduceCurrentHp(double i, Creature attacker, boolean awake, boolean isDOT, L2Skill skill)
	{
		if (isChampion() && Config.CHAMPION_HP != 0)
			getStatus().reduceHp(i / Config.CHAMPION_HP, attacker, awake, isDOT, false);
		else
			getStatus().reduceHp(i, attacker, awake, isDOT, false);
		
		CreatureListenerManager.getInstance().notifyHpDamage(this, i, attacker, skill);
	}
	
	public boolean isChampion() { return false; }
	
	public void sendDamageMessage(Creature target, int damage, boolean mcrit, boolean pcrit, boolean miss) {}
	
	public FusionSkill getFusionSkill() { return _fusionSkill; }
	public void setFusionSkill(FusionSkill fb) { _fusionSkill = fb; }
	
	public boolean isAffected(EffectFlag flag) { return _effects.isAffected(flag); }
	
	public int getMaxBuffCount() { return Config.MAX_BUFFS_AMOUNT + getSkillLevel(L2Skill.SKILL_DIVINE_INSPIRATION); }
	
	public final double getRandomDamageMultiplier()
	{
		final Weapon activeWeapon = getActiveWeaponItem();
		int random;
		if (activeWeapon != null) random = activeWeapon.getRandomDamage();
		else random = 5 + (int) Math.sqrt(getStatus().getLevel());
		return (1 + ((double) Rnd.get(0 - random, random) / 100));
	}
	
	public boolean isInArena() { return false; }
	
	public double getCollisionRadius() { return getTemplate().getCollisionRadius(); }
	public double getCollisionHeight() { return getTemplate().getCollisionHeight(); }
	
	public void fleeFrom(Creature attacker, int distance)
	{
		if (attacker == null || attacker == this || distance < 10) return;
		forceRunStance();
		final Location loc = getPosition().clone();
		loc.setFleeing(attacker.getPosition(), distance);
		getMove().maybeMoveToLocation(loc, 0, true, false);
	}
	
	public void moveUsingRandomOffset(int offset)
	{
		if (offset < 10) return;
		final Location loc = getPosition().clone();
		loc.addRandomOffset(offset);
		getMove().maybeMoveToLocation(loc, 0, true, false);
	}
	
	@Override
	public final void setRegion(WorldRegion newRegion)
	{
		if (getRegion() != null)
		{
			if (newRegion == null) getRegion().removeFromZones(this);
			else if (newRegion != getRegion()) getRegion().revalidateZones(this);
		}
		super.setRegion(newRegion);
		revalidateZone(true);
	}
	
	@Override
	public void removeKnownObject(WorldObject object)
	{
		if (object == getTarget()) setTarget(null);
	}
	
	public void setPremiumService(int premiumService) { _premiumService = premiumService; }
	public int getPremiumService() { return _premiumService; }
	
	public void forEachKnownGM(Consumer<Player> action)
	{
		forEachKnownType(Player.class, Player::isGM, action);
	}
	
	public boolean testCursesOnAttack(Npc npc, int npcId) { return false; }
	public boolean testCursesOnAttack(Npc npc) { return false; }
	public boolean testCursesOnAggro(Npc npc) { return false; }
	public boolean testCursesOnSkillSee(L2Skill skill, Creature[] targets) { return false; }
	
	public void checkCondition(double curHp, double newHp) {}
	
	public final NpcTemplate getPolymorphTemplate() { return _polymorphTemplate; }
	
	public boolean polymorph(int id)
	{
		if (!(this instanceof Npc) && !(this instanceof Player)) return false;
		final NpcTemplate template = NpcData.getInstance().getTemplate(id);
		if (template == null) return false;
		_polymorphTemplate = template;
		decayMe();
		spawnMe();
		return true;
	}
	
	public void unpolymorph()
	{
		_polymorphTemplate = null;
		decayMe();
		spawnMe();
	}
	
	public boolean canBeHealed() { return !isDead() && !isInvul(); }
	
	public void forceDecay()
	{
		if (DecayTaskManager.getInstance().cancel(this))
			onDecay();
	}
	
	public void forceSeeCreature()
	{
		if (!(this instanceof Playable)) return;
		if (getActingPlayer() != null && (getActingPlayer().isSpawnProtected() || getActingPlayer().isFlying())) return;
		
		forEachKnownType(Npc.class, npc -> isInStrictRadius(npc, npc.getSeeRange()) && Math.abs(npc.getZ() - getZ()) <= 500, npc ->
		{
			for (Quest quest : npc.getTemplate().getEventQuests(EventHandler.SEE_CREATURE))
				quest.onSeeCreature(npc, this);
		});
	}
	
	public boolean isSilentMoving() { return false; }
	public void sendIU() {}
	public boolean isInBoat() { return false; }
	public void setIsFlying(boolean mode) { _isFlying = mode; }
	
	protected void calculateMission(Creature killer)
	{
		if (killer == null) return;
		final Player player = killer instanceof Summon ? ((Summon) killer).getOwner() : killer instanceof Player ? (Player) killer : null;
		if (player == null || player == this) return;
		
		final MissionList missions = player.getMissions();
		if (this instanceof GrandBoss)
		{
			player.getMissions().updateParty(MissionType.GRANDBOSS);
			final GrandBoss grandboss = (GrandBoss) this;
			switch (grandboss.getNpcId())
			{
				case 29019:
				case 29066:
				case 29067:
				case 29068:
					missions.updateParty(MissionType.ANTHARAS);
					break;
				case 29001:
					missions.updateParty(MissionType.QUEEN_ANT);
					break;
				case 29006:
					missions.updateParty(MissionType.CORE);
					break;
				case 29014:
					missions.updateParty(MissionType.ORFEN);
					break;
				case 29022:
					missions.updateParty(MissionType.ZAKEN);
					break;
				case 29020:
					missions.updateParty(MissionType.BAIUM);
					break;
				case 29028:
					missions.updateParty(MissionType.VALAKAS);
					break;
				case 29047:
					missions.updateParty(MissionType.HALISHA);
					break;
			}
		}
		
		if ((player.getStatus().getLevel() - getStatus().getLevel()) > 8) return;
		
		if (this instanceof RaidBoss)
			missions.updateParty(MissionType.RAIDBOSS);
		else if (this instanceof SiegeGuard)
			missions.update(MissionType.GUARD);
		else if (this instanceof Monster)
		{
			final Monster monster = (Monster) this;
			switch (monster.getNpcId())
			{
				case 22215:
				case 22216:
				case 22217:
					missions.update(MissionType.TYRANNOSAURUS);
					break;
				default:
					missions.update(MissionType.MONSTER);
					break;
			}
		}
	}
	
	@Override
	public boolean getTownZone()
	{
		return _townZone;
	}
	
	@Override
	public void setTownZone(boolean val)
	{
		_townZone = val;
	}
}
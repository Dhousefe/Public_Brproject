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
package ext.mods.gameserver.model.entity.autofarm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import ext.mods.commons.logging.CLogger;
import ext.mods.commons.random.Rnd;
import ext.mods.commons.util.ArraysUtil;
import ext.mods.Config;
import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.enums.items.WeaponType;
import ext.mods.gameserver.enums.skills.SkillType;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.model.actor.move.MovementIntegration;
import ext.mods.gameserver.model.actor.ai.type.PlayerAI;
import ext.mods.gameserver.handler.IItemHandler;
import ext.mods.gameserver.handler.ItemHandler;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.Summon;
import ext.mods.gameserver.model.actor.instance.Chest;
import ext.mods.gameserver.model.actor.instance.GrandBoss;
import ext.mods.gameserver.model.actor.instance.Monster;
import ext.mods.gameserver.model.entity.autofarm.AutoFarmManager.AutoFarmType;
import ext.mods.gameserver.model.entity.autofarm.zone.AutoFarmArea;
import ext.mods.gameserver.model.entity.autofarm.zone.AutoFarmRoute;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.network.serverpackets.AutoAttackStop;
import ext.mods.gameserver.skills.AbstractEffect;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.skills.effects.EffectTemplate;
import ext.mods.gameserver.skills.extractable.ExtractableProductItem;
import ext.mods.gameserver.skills.l2skills.L2SkillCreateItem;
import ext.mods.gameserver.taskmanager.AttackStanceTaskManager;

/**
 * Sistema de rotina de autofarm melhorado com lógica inteligente de ataque.
 * 
 * <h3>Melhorias Implementadas:</h3>
 * <ul>
 *   <li><b>Classes Melee:</b> Verificação inicial inteligente de range, renovação de intenção de ataque durante movimento,
 *       aproximação com maybeStartOffensiveFollow, verificação canAttack + tryToAttack para garantir ataque,
 *       priorização de skills ofensivas sobre ataque normal, busca de TODAS as skills disponíveis</li>
 *   <li><b>Arqueiros:</b> Respeito ao range do arco e das skills, intercalação de skills, uso de maybeStartOffensiveFollow</li>
 *   <li><b>Magos:</b> Priorização de skills ofensivas mágicas (power > 0), intercalação inteligente, nunca ataca fisicamente</li>
 *   <li><b>Sistema de Rotação:</b> Intercalação automática entre skills disponíveis, remoção de chance aleatória de não usar skill</li>
 *   <li><b>Movimento:</b> Substituição de tryToMoveTo por maybeStartOffensiveFollow para melhor performance</li>
 * </ul>
 * 
 * <h3>Fluxo de Ataque Melee:</h3>
 * <ol>
 *   <li>Verifica range (dist2D > totalAttackRange?) → RENOVA intenção → maybeStartOffensiveFollow se necessário</li>
 *   <li>Verifica geodata (pode ver?) → RENOVA intenção → maybeStartOffensiveFollow se obstáculo</li>
 *   <li>Para movimento (dist2D <= totalAttackRange * 0.95)</li>
 *   <li>RENOVA intenção de ataque (se necessário) → Evita ficar ocioso</li>
 *   <li>Verifica canAttack() → Se true, busca TODAS as skills ofensivas disponíveis</li>
 *   <li>Se encontrou skill → USA A SKILL (prioriza sobre ataque normal)</li>
 *   <li>Se não encontrou → USA ATAQUE NORMAL</li>
 *   <li>Intercala skills respeitando todas as condições (cooldown, range, MP, itens, checkCondition)</li>
 * </ol>
 * 
 * <h3>Fluxo de Ataque Mago:</h3>
 * <ol>
 *   <li>Obtém melhor skill ofensiva (selectBestMageSkill - prioriza power > 0)</li>
 *   <li>Verifica range da skill → maybeStartOffensiveFollow se fora</li>
 *   <li>Verifica pode ver target → maybeStartOffensiveFollow se não</li>
 *   <li>tryToCast() → Usa skill</li>
 *   <li>NUNCA ataca fisicamente se tem skills configuradas</li>
 * </ol>
 * 
 * @see AutoFarmProfile
 * @see AutoFarmManager
 * @see <a href="AUTOFARM_IMPROVEMENTS.md">Documentação Completa das Melhorias</a>
 * 
 * @author Sistema de Melhorias AutoFarm
 * @version 2.1 - Priorização de Skills Ofensivas e Renovação de Intenção
 */
public class AutoFarmRoutine
{
	private static final CLogger LOGGER = new CLogger(AutoFarmRoutine.class.getName());
	private static final long DEFENSIVE_AGGRO_MEMORY_MS = 120000L;
	private static final ConcurrentMap<Integer, ConcurrentMap<Integer, Long>> _pvpAggressors = new ConcurrentHashMap<>();
	private static volatile boolean _pvpAggressorListenerRegistered = false;
	private final AutoFarmProfile _autoFarmProfile;
	private final Map<String, Integer> _cachedMessages = new HashMap<>();
	private final Map<Integer, Long> _unreachableTargets = new HashMap<>();
	private boolean _noAttackSkillItems;
	private int _skillAttackFailCount;
	
	private long _playerOverrideUntil = 0;
	
	
	private int _skillRotationIndex = 0;
	
	private long _zoneLastMovementTime = 0;
	private static final int ZONE_MOVEMENT_DELAY_MS = 1000;
	
	private long _openLastExplorationTime = 0;
	private static final int OPEN_EXPLORATION_DELAY_MS = 2000;
	private Location _lastOpenExplorationPos = null;

	private long _lastArcherKiteTime = 1500;
	private static final int ARCHER_KITE_DELAY_MS = 1500;
	private static final int ARCHER_KITE_AFTER_SHOT_DELAY_MS = 500;
	private long _lastArcherShotTime = 0;
	
	private static final int MELEE_ATTACK_COOLDOWN_MS = 500;
	private long _lastMeleeAttackTime = 0;

	private enum CombatRole
	{
		MAGE,
		FIGHTER,
		ARCHER
	}
	
	private long _routeLastExplorationTime = 0;
	private static final int ROUTE_EXPLORATION_DELAY_MS = 1500;
	private static final int ROUTE_NODE_PROXIMITY_RADIUS = 500;
	private static final int ROUTE_TARGET_MAX_NODE_DISTANCE = 600;
	private int _routeCurrentIndex = -1;
	private boolean _routeReversePath = false;
	private int _routeNodeStep = 0;
	private final Map<Integer, Integer> _routeTargetBans = new HashMap<>();
	
	private long _stuckRecoveryUntil = 0;
	private int _stuckRecoveryTargetId = 0;
	
	private long _targetLastHitTime = 0;
	private Monster _lockedTarget = null;
	private static final int TARGET_STUCK_TIMEOUT_MS = 15000;
	private static final int UNREACHABLE_BAN_TIME_MS = 3000;
	
	private List<Location> _currentPath = new ArrayList<>();
	private long _lastPathCalculation = 0;
	private static final int PATHFINDING_COOLDOWN_MS = 1000;
	private static final int MAX_PATH_DISTANCE = 5000;
	private Location _lastPathTarget = null;
	
	public AutoFarmRoutine(AutoFarmProfile autoFarmProfile)
	{
		_autoFarmProfile = autoFarmProfile;
		ensurePvpAggressorListenerRegistered();
	}
	
	public synchronized void start()
	{
		if (!_autoFarmProfile.isEnabled())
			return;
		
		if (!Config.AUTOFARM_ENABLED)
		{
			stop("System cannot be used now");
			return;
		}
		
		final Player player = _autoFarmProfile.getPlayer();
		if (player == null)
			return;
		
		if (player.isDead())
		{
			if (!_autoFarmProfile.isDeathReturnEnabled())
				stop("Your character is dead sem Return");
			return;
		}
		
		/*if (!_autoFarmProfile.canUseAutoFarm())
		{
			stop("Daily autofarm time limit reached. Use items to extend time or become premium!");
			return;
		}*/
		
		if (_skillAttackFailCount >= 20)
		{
			stop("Your character cannot attack anymore");
			return;
		}
		
		if (_autoFarmProfile.getEndTime() != 0 && _autoFarmProfile.getFinalEndTime() < System.currentTimeMillis())
		{
			if (!isPlayerAttacking(player))
				stop("Scheduled duration has concluded");
			
			return;
		}
		
		run(player);
	}
	
	public synchronized void stop(String msg)
	{
		_skillAttackFailCount = 0;
		_cachedMessages.clear();
		_unreachableTargets.clear();
		_routeTargetBans.clear();
		_routeNodeStep = 0;
		_currentPath.clear();
		_lastPathTarget = null;
		_skillRotationIndex = 0;
		final Player player = _autoFarmProfile.getPlayer();
		if (player != null)
			AutoFarmManager.getInstance().stopPlayer(player, msg);
		onEnd();
	}
	
	private synchronized void run(Player player)
	{
		cleanupUnreachableTargets();
		cleanupRouteTargetBans();
		cleanupPvpAggressors(player);
		
		if (System.currentTimeMillis() < _playerOverrideUntil)
		{
			return;
		}
		
		if (player.isDead() || player.isSleeping() || player.isStunned() || player.isImmobilized())
		{
			if (player.isDead() && !_autoFarmProfile.isDeathReturnEnabled())
				stop("Your character is dead");
			return;
		}
		
		final AutoFarmArea selectedArea = _autoFarmProfile.getSelectedArea();
		if (selectedArea != null && selectedArea.isHandlingDeath())
		{
			if (Config.AUTOFARM_DEBUG_RETURN)
				LOGGER.info("[AutoFarmRoutine][DeathReturn] run() skip: AutoFarmArea.isHandlingDeath() true, clearing Monster target and setting AI intention IDLE");
			if (player.getTarget() instanceof Monster)
				player.setTarget(null);
			player.getAI().getCurrentIntention().updateAsIdle();
			return;
		}
		
		if (_autoFarmProfile.useAutoPotion())
		{
			testHpPotions(player);
			testMpPotions(player);
		}
		if (testHealSkill(player) || testBuffSkill(player))
		{
			sendAdminMessage(player, "Action: Self-healing or buffing.");
			return;
		}
		
		if (_autoFarmProfile.isDefensiveMode() || _autoFarmProfile.isOffensiveMode())
		{
			final Player pvpTarget = findPvpTarget(player);
			if (pvpTarget != null)
			{
				if (player.getTarget() != pvpTarget)
					player.setTarget(pvpTarget);
				
				handlePlayerCombat(player, pvpTarget);
				return;
			}
		}
		
		
		final Monster attacker = findFirstAttacker(player);
		if (attacker != null)
		{
			if (player.getTarget() != attacker)
			{
				player.setTarget(attacker);
				sendAdminMessage(player, "Priority switch to attacker: " + attacker.getName());
			}
			handleCombat(player, attacker);
			return;
		}
		
		if (_autoFarmProfile.isPickingUpItems() && player.getAI().getCurrentIntention().getType() == IntentionType.PICK_UP)
			return;
		
		if (player.getAI().getCurrentIntention().getType() != IntentionType.PICK_UP)
		{
			final Monster liveTarget = (player.getTarget() instanceof Monster m && !m.isDead()) ? m : null;
			final boolean hasValidLiveTarget = liveTarget != null && !isInvalidTarget(liveTarget);
			if (_autoFarmProfile.isPickingUpItems() && !hasValidLiveTarget && tryPickUpItems(player))
			{
				sendAdminMessage(player, "Action: Picking up items.");
				return;
			}
			
			if (_autoFarmProfile.useSpoilSweep())
			{
				int sweeps = 0;
				final int maxSweepsPerTick = 3;
				while (sweeps < maxSweepsPerTick && trySweep(player))
				{
					sweeps++;
					if (player.getAI().getCurrentIntention().getType() == IntentionType.CAST)
						break;
					if (findFirstAttacker(player) != null)
						break;
				}
				if (sweeps > 0)
				{
					sendAdminMessage(player, "Action: Sweeping x" + sweeps + ".");
					return;
				}
			}
		}
		
		
		Monster currentTarget = (player.getTarget() instanceof Monster) ? (Monster) player.getTarget() : null;
		Player currentPlayerTarget = (player.getTarget() instanceof Player) ? (Player) player.getTarget() : null;
		
		if (currentPlayerTarget != null)
		{
			if (!isValidPvpTarget(player, currentPlayerTarget))
			{
				player.setTarget(null);
				currentPlayerTarget = null;
			}
			else
			{
				handlePlayerCombat(player, currentPlayerTarget);
				return;
			}
		}
		
		if (currentTarget != null)
		{
			if (isInvalidTarget(currentTarget))
			{
				sendAdminMessage(player, "Canceling invalid target: " + currentTarget.getName());
				player.setTarget(null);
				currentTarget = null;
				resetStuckLogic();
			}
			else if (_autoFarmProfile.getSelectedArea().getType() == AutoFarmType.ROTA)
			{
				if (isRouteTargetBanned(currentTarget.getObjectId()))
				{
					player.setTarget(null);
					currentTarget = null;
					resetStuckLogic();
				}
				else
				{
					final AutoFarmRoute route = _autoFarmProfile.getSelectedArea().getRouteZone();
					final List<Location> nodes = route != null ? route.getNodes() : new ArrayList<>();
					if (!isTargetNearRouteNodes(currentTarget, nodes))
					{
						banRouteTarget(currentTarget.getObjectId());
						player.setTarget(null);
						currentTarget = null;
						resetStuckLogic();
					}
				}
			}
		}
		
		if (currentTarget == null || currentTarget.isDead())
		{
			if (currentTarget != null)
			{
				final AutoFarmArea area = _autoFarmProfile.getSelectedArea();
				if (area != null)
					area.updateLastKilledMonsterLocation(currentTarget.getPosition());
				player.setTarget(null);
			}
			
			if (_autoFarmProfile.useSpoilSweep())
			{
				int sweeps = 0;
				final int maxSweepsPostKill = 8;
				while (sweeps < maxSweepsPostKill && trySweep(player))
				{
					sweeps++;
					if (player.getAI().getCurrentIntention().getType() == IntentionType.CAST)
						break;
				}
				if (sweeps > 0)
				{
					sendAdminMessage(player, "Action: Sweeping x" + sweeps + " (post-kill).");
					return;
				}
			}
			
			if (_autoFarmProfile.isPickingUpItems() && tryPickUpItems(player))
			{
				sendAdminMessage(player, "Action: Picking up items (post-kill).");
				return;
			}
			
			final Monster newTarget = findNewTarget(player);
			if (newTarget != null)
			{
				player.setTarget(newTarget);
				sendAdminMessage(player, "Action: Found new target: " + newTarget.getName());
				handleCombat(player, newTarget);
			}
			else
			{
				if (_autoFarmProfile.getSelectedArea().getType() == AutoFarmType.ROTA)
				{
				}
				handleNoTargetMovement(player);
			}
		}
		else
		{
			handleCombat(player, currentTarget);
		}
	}
	
	
	/**
	 * Helper method para padronizar chamadas de intenções (similar ao padrão Kotlin).
	 */
	private void setIntention(Player player, IntentionType intentionType, Object... args)
	{
		final PlayerAI ai = player.getAI();
		if (ai != null)
		{
			ai.setAutoFarmIntention(intentionType, args);
		}
	}
	
	/**
	 * Verifica se um Monster é inválido para ataque.
	 * @param monster O monstro a verificar.
	 * @return true se for inválido, false caso contrário.
	 */
	private boolean isInvalidTarget(Monster monster)
	{
		if (monster instanceof Chest && ((Chest) monster).isBox())
			return true;
		if (monster instanceof GrandBoss && !_autoFarmProfile.attackRaid())
			return true;
		if (monster.isRaidRelated() && !_autoFarmProfile.attackRaid())
			return true;
		return false;
	}

	private boolean isValidPvpTarget(Player player, Player target)
	{
		if (target == null || target == player || target.isDead())
			return false;
		if (target.isGM() || player.isInsideZone(ZoneId.PEACE) || target.isInsideZone(ZoneId.PEACE))
			return false;
		if (target.isInObserverMode())
			return false;
		
		if (player.isInSameParty(target) || player.isInSameCommandChannel(target)
			|| player.isInSameClan(target) || player.isInSameAlly(target) || player.isInSameActiveSiegeSide(target))
		{
			return false;
		}
		
		if (_autoFarmProfile.getSelectedArea().getType() == AutoFarmType.ZONA && !target.isInsideZone(ZoneId.AUTO_FARM))
			return false;
		
		if (!target.isAttackableBy(player))
			return false;
		
		if (_autoFarmProfile.isDefensiveMode())
			return wasRecentlyAttackedBy(player, target);
		
		return _autoFarmProfile.isOffensiveMode();
	}

	private static void ensurePvpAggressorListenerRegistered()
	{
		if (_pvpAggressorListenerRegistered)
			return;
		
		synchronized (AutoFarmRoutine.class)
		{
			if (_pvpAggressorListenerRegistered)
				return;
			
			ext.mods.extensions.listener.manager.CreatureListenerManager.getInstance().addHpDamageListener((creature, damageHp, attacker, skill) ->
			{
				if (damageHp <= 0)
					return;
				
				final Player victim = creature != null ? creature.getActingPlayer() : null;
				final Player aggressor = attacker != null ? attacker.getActingPlayer() : null;
				if (victim == null || aggressor == null || victim == aggressor)
					return;
				
				_pvpAggressors
					.computeIfAbsent(victim.getObjectId(), id -> new ConcurrentHashMap<>())
					.put(aggressor.getObjectId(), System.currentTimeMillis());
			});
			
			_pvpAggressorListenerRegistered = true;
		}
	}

	private boolean wasRecentlyAttackedBy(Player player, Player target)
	{
		final ConcurrentMap<Integer, Long> attackers = _pvpAggressors.get(player.getObjectId());
		if (attackers == null)
			return false;
		
		final Long lastHit = attackers.get(target.getObjectId());
		if (lastHit == null)
			return false;
		
		return System.currentTimeMillis() - lastHit <= DEFENSIVE_AGGRO_MEMORY_MS;
	}

	private void cleanupPvpAggressors(Player player)
	{
		final ConcurrentMap<Integer, Long> attackers = _pvpAggressors.get(player.getObjectId());
		if (attackers == null || attackers.isEmpty())
			return;
		
		final long now = System.currentTimeMillis();
		attackers.entrySet().removeIf(entry -> now - entry.getValue() > DEFENSIVE_AGGRO_MEMORY_MS);
		if (attackers.isEmpty())
			_pvpAggressors.remove(player.getObjectId());
	}

	private Player findPvpTarget(Player player)
	{
		if (!_autoFarmProfile.isDefensiveMode() && !_autoFarmProfile.isOffensiveMode())
			return null;
		
		final int searchRange = Math.max(600, _autoFarmProfile.getAttackRange());
		
		final Player current = (player.getTarget() instanceof Player) ? (Player) player.getTarget() : null;
		if (current != null && isValidPvpTarget(player, current))
			return current;
		
		return player.getKnownTypeInRadius(Player.class, searchRange).stream()
			.filter(p -> p != null && isValidPvpTarget(player, p))
			.min(Comparator.comparingDouble(p -> player.distance3D(p)))
			.orElse(null);
	}

	private void handlePlayerCombat(Player player, Player target)
	{
		final boolean useCtrl = _autoFarmProfile.isOffensiveMode() || _autoFarmProfile.isDefensiveMode();
		if (_autoFarmProfile.attackSummon())
			trySummonPvpAttack(player, target, useCtrl);
		
		final double dist = player.distance3D(target);
		final double dist2D = player.distance2D(target);
		final int physicalAttackRange = player.getStatus().getPhysicalAttackRange();
		final boolean isBowWeapon = player.getAttackType() == WeaponType.BOW;
		final CombatRole combatRole = getCombatRole(player, physicalAttackRange, isBowWeapon);
		final boolean isMageRole = combatRole == CombatRole.MAGE;
		final boolean isFighterRole = combatRole != CombatRole.MAGE;
		final boolean isArcherRole = combatRole == CombatRole.ARCHER;
		
		if (isArcherRole)
		{
			final int bowRange = physicalAttackRange;
			final int totalBowRange = bowRange + (int)(player.getCollisionRadius() + target.getCollisionRadius());
			handleArcherCombatPvp(player, target, dist, dist2D, bowRange, totalBowRange);
			return;
		}
		
		final boolean isMeleeWeapon = physicalAttackRange <= 80;
		final boolean isOpenMode = _autoFarmProfile.getSelectedArea().getType() == AutoFarmType.OPEN;
		final boolean isRouteMode = _autoFarmProfile.getSelectedArea().getType() == AutoFarmType.ROTA;
		
		int attackRange;
		if (isRouteMode)
		{
			attackRange = physicalAttackRange;
		}
		else if (isMeleeWeapon && isOpenMode && isFighterRole)
		{
			attackRange = physicalAttackRange;
		}
		else
		{
			attackRange = Math.max(physicalAttackRange, _autoFarmProfile.getAttackRange());
		}
		
		final int totalAttackRange = (int)(attackRange + player.getCollisionRadius() + target.getCollisionRadius());
		
		boolean canSee = checkLineOfSightForAttack(player, target, dist2D);
		if (!canSee)
		{
			if (tryPathfindingAroundObstacle(player, target))
				return;
			
			final Location bypass = calculateBypassPosition(player, target);
			if (bypass != null)
			{
				player.getMove().maybePlayerMoveToLocation(bypass, 0, Config.SISTEMA_PATHFINDING, false);
				return;
			}
		}
		
		if (isMageRole)
		{
			handleMageCombatPvp(player, target, dist);
			return;
		}
		
		final L2Skill meleeSkill = selectPvpSkill(player, target, dist);
		if (meleeSkill != null)
		{
			int skillRange = meleeSkill.getCastRange() > 0 ? meleeSkill.getCastRange() : meleeSkill.getSkillRadius();
			if (skillRange <= 0)
				skillRange = attackRange;
			
			final int totalSkillRange = (int)(skillRange + player.getCollisionRadius() + target.getCollisionRadius());
			if (dist > totalSkillRange || !MovementIntegration.canSeeTarget(player, target))
			{
				setIntention(player, IntentionType.CAST, meleeSkill, target);
				if (player.getMove().maybeStartOffensiveFollow(target, skillRange))
					return;
			}
			else
			{
				player.getMove().stop();
				player.updatePvPStatus(target);
				setIntention(player, IntentionType.CAST, meleeSkill, target);
				ensureCtrlAttackIntention(player, target, useCtrl);
				player.getAI().tryToCast(target, meleeSkill, useCtrl, false, 0);
				return;
			}
		}
		
		if (dist2D > totalAttackRange || !MovementIntegration.canSeeTarget(player, target))
		{
			setIntention(player, IntentionType.ATTACK, target);
			player.getMove().maybeStartOffensiveFollow(target, attackRange);
			return;
		}
		
		if (player.getAttack().canAttack(target))
		{
			setIntention(player, IntentionType.ATTACK, target);
			player.getMove().stop();
			player.updatePvPStatus(target);
			player.getAI().tryToAttack(target, useCtrl, false);
		}
	}
	
	/**
	 * Encontra o monstro agressivo mais próximo que está atacando o jogador.
	 * @param player O jogador.
	 * @return O monstro atacante mais próximo, ou null se não houver nenhum.
	 */
	private Monster findFirstAttacker(Player player)
	{
		return player.getKnownTypeInRadius(Monster.class, 1200).stream()
			.filter(m -> !m.isDead())
			.filter(m -> !isInvalidTarget(m))
			.filter(this::isTargetInsideZone)
			.filter(m -> m.getAI().getAggroList().getHate(player) > 0)
			.min(Comparator.comparingDouble(m -> player.distance3D(m)))
			.orElse(null);
	}
	
	/**
	 * Procura e retorna o alvo mais próximo que corresponde aos critérios do perfil.
	 * Sistema melhorado com busca progressiva: expande o range até encontrar um target.
	 * Usa pathfinding para verificar se pode chegar ao target antes de selecioná-lo.
	 * 
	 * @param player O jogador.
	 * @return O melhor monstro para atacar, ou null se nenhum for encontrado.
	 */
	private Monster findNewTarget(Player player)
	{
		final AutoFarmArea area = _autoFarmProfile.getSelectedArea();
		
		final int baseRange = getBaseSearchRange(area);
		final int maxRange = getMaxSearchRange(area);
		final int stepSize = 200;
		
		for (int currentRange = baseRange; currentRange <= maxRange; currentRange += stepSize)
		{
			final Monster target = findTargetInRange(player, area, currentRange);
			if (target != null)
			{
				sendAdminMessage(player, "Target found at range: " + currentRange + " - " + target.getName());
				return target;
			}
		}
		
		return null;
	}
	
	/**
	 * Busca target em um range específico com verificações de pathfinding.
	 * 
	 * @param player O jogador
	 * @param area A área do autofarm
	 * @param range O range de busca
	 * @return O melhor target encontrado ou null
	 */
	private Monster findTargetInRange(Player player, AutoFarmArea area, int range)
	{
		final boolean isRouteMode = area.getType() == AutoFarmType.ROTA;
		final AutoFarmRoute route = isRouteMode ? area.getRouteZone() : null;
		final List<Location> routeNodes = isRouteMode && route != null ? route.getNodes() : new ArrayList<>();
		
		List<Monster> candidates;
		switch (area.getType())
		{
			case ROTA:
				candidates = findCandidatesForRoute(player, range);
				break;
			case ZONA:
				candidates = findCandidatesForZone(player);
				break;
			case OPEN:
				candidates = findCandidatesForOpen(player, range);
				break;
			default:
				candidates = area.getMonsters() != null ? area.getMonsters() : new ArrayList<>();
				break;
		}
		
		return candidates.stream()
			.filter(m -> m != null && !m.isDead())
			.filter(m -> !isInvalidTarget(m))
			.filter(m -> !_unreachableTargets.containsKey(m.getObjectId()))
			.filter(m -> !isRouteMode || (!isRouteTargetBanned(m.getObjectId()) && isTargetNearRouteNodes(m, routeNodes)))
			.filter(m -> _autoFarmProfile.getTargets().isEmpty() || _autoFarmProfile.getTargets().contains(m.getName()))
			.filter(m -> player.distance3D(m) <= range)
			.sorted(Comparator.comparingDouble(m -> player.distance3D(m)))
			.filter(monster -> {
				if (!MovementIntegration.canSeeTargetForAutoFarm(player, monster))
				{
					return canReachTargetWithPathfinding(player, monster);
				}
				return true;
			})
			.findFirst()
			.orElse(null);
	}
	
	/**
	 * Verifica se pode chegar ao target usando pathfinding.
	 * Usa GeoEngine legado para calcular caminho e verificar se é viável.
	 * 
	 * @param player O jogador
	 * @param target O target
	 * @return true se pode chegar ao target usando pathfinding
	 */
	private boolean canReachTargetWithPathfinding(Player player, Monster target)
	{
		if (MovementIntegration.canMoveToTargetForAutoFarm(
			player.getX(), player.getY(), player.getZ(),
			target.getX(), target.getY(), target.getZ()))
		{
			return true;
		}
		
		final List<Location> path = GeoEngine.getInstance().findPath(
			player.getX(), player.getY(), player.getZ(),
			target.getX(), target.getY(), target.getZ(),
			true,
			null
		);
		
		if (path != null && path.size() >= 2)
		{
			final double pathDistance = calculatePathDistance(player.getPosition(), path, target.getPosition());
			final double directDistance = player.distance3D(target);
			
			return pathDistance <= directDistance * 3.0;
		}
		
		return false;
	}
	
	/**
	 * Calcula a distância total de um caminho de pathfinding.
	 * 
	 * @param start Posição inicial
	 * @param path Lista de pontos do caminho
	 * @param end Posição final
	 * @return Distância total do caminho
	 */
	private double calculatePathDistance(Location start, List<Location> path, Location end)
	{
		double totalDistance = 0.0;
		Location current = start;
		
		for (Location next : path)
		{
			totalDistance += current.distance3D(next);
			current = next;
		}
		
		if (end != null)
		{
			totalDistance += current.distance3D(end);
		}
		
		return totalDistance;
	}
	
	/**
	 * Obtém o range base de busca baseado no tipo de área.
	 * 
	 * @param area A área do autofarm
	 * @return Range base em unidades
	 */
	private int getBaseSearchRange(AutoFarmArea area)
	{
		switch (area.getType())
		{
			case ROTA:
				return 300;
			case ZONA:
				return _autoFarmProfile.getFinalRadius();
			case OPEN:
				return Math.max(300, _autoFarmProfile.getFinalRadius());
			default:
				return 300;
		}
	}
	
	/**
	 * Obtém o range máximo de busca baseado no tipo de área.
	 * 
	 * @param area A área do autofarm
	 * @return Range máximo em unidades
	 */
	private int getMaxSearchRange(AutoFarmArea area)
	{
		switch (area.getType())
		{
			case ROTA:
				return 800;
			case ZONA:
				return _autoFarmProfile.getFinalRadius() * 2;
			case OPEN:
				return Math.max(2000, _autoFarmProfile.getFinalRadius() * 3);
			default:
				return 1000;
		}
	}
	
	/**
	 * Encontra candidatos para modo ROTA com range específico.
	 */
	private List<Monster> findCandidatesForRoute(Player player, int range)
	{
		final List<Monster> areaCandidates = _autoFarmProfile.getSelectedArea().getMonsters();
		if (areaCandidates != null && !areaCandidates.isEmpty())
		{
			return areaCandidates.stream()
				.filter(m -> player.distance3D(m) <= range)
				.collect(Collectors.toList());
		}
		return player.getKnownTypeInRadius(Monster.class, range);
	}
	
	/**
	 * Encontra candidatos para modo ZONA (padrão Kotlin).
	 */
	private List<Monster> findCandidatesForZone(Player player)
	{
		final List<Monster> monsters = _autoFarmProfile.getSelectedArea().getMonsters();
		return monsters != null ? monsters : new ArrayList<>();
	}
	
	/**
	 * Encontra candidatos para modo OPEN com range específico.
	 */
	private List<Monster> findCandidatesForOpen(Player player, int range)
	{
		return player.getKnownTypeInRadius(Monster.class, range);
	}
	
	private void handleNoTargetMovement(Player player)
	{
		final AutoFarmArea area = _autoFarmProfile.getSelectedArea();
		switch (area.getType())
		{
			case ZONA:
				handleZoneMovement(player, area);
				break;
			case ROTA:
				handleRouteMovement(player, area);
				break;
			case OPEN:
				handleOpenMovement(player, area);
				break;
			default:
				if (!handleMovementGeneric(player, area))
				{
					trySendMessage(player, "waiting for new monsters to spawn.");
				}
				break;
		}
	}
	
	private boolean handleMovementGeneric(Player player, AutoFarmArea area)
	{
		return false;
	}
	
	/**
	 * Lógica específica para movimento em modo ZONA.
	 * Garante que o jogador sempre se mova dentro da zona procurando por monstros.
	 */
	private boolean handleZoneMovement(Player player, AutoFarmArea area)
	{
		if (!player.isInsideZone(ZoneId.AUTO_FARM))
		{
			if (moveBackInsideZone(player, area))
			{
				sendAdminMessage(player, "Returning to zone interior");
				return true;
			}
			else
			{
				stop("Character outside the zone and cannot return.");
				return true;
			}
		}
		
		long currentTime = System.currentTimeMillis();
		if (currentTime - _zoneLastMovementTime < ZONE_MOVEMENT_DELAY_MS)
		{
			return false;
		}
		
		if (moveBackInsideZone(player, area))
		{
			_zoneLastMovementTime = currentTime;
			sendAdminMessage(player, "Exploring zone for monsters");
			return true;
		}
		
		forceZoneExploration(player, area);
		_zoneLastMovementTime = currentTime;
		return true;
	}
	
	/**
	 * Força exploração da zona quando não há movimento automático disponível.
	 */
	private void forceZoneExploration(Player player, AutoFarmArea area)
	{
		List<Location> nodes = area.getNodes();
		if (!nodes.isEmpty())
		{
			Location randomNode = nodes.get(Rnd.get(nodes.size()));
			player.getMove().maybePlayerMoveToLocation(randomNode, 0, Config.SISTEMA_PATHFINDING, false);
		}
		else
		{
			player.getMove().maybePlayerMoveToLocation(player.getPosition(), 0, false, false);
		}
		sendAdminMessage(player, "Forcing zone exploration");
	}
	
	/**
	 * Lógica específica para movimento em modo ROTA.
	 * Player percorre os pontos da rota sequencialmente (primeira à última, depois volta).
	 * Usa maybePlayerMoveToLocation com pathfinding para contornar obstáculos.
	 * Antes de mover, verifica se há target próximo que pode ser atacado.
	 */
	private boolean handleRouteMovement(Player player, AutoFarmArea area)
	{
		final AutoFarmRoute route = area.getRouteZone();
		final List<Location> nodes = route.getNodes();
		
		if (nodes.isEmpty())
			return false;
		
		if (System.currentTimeMillis() - _routeLastExplorationTime < ROUTE_EXPLORATION_DELAY_MS)
			return false;
		
		if (_routeCurrentIndex < 0 || _routeCurrentIndex >= nodes.size())
		{
			Location nearestNode = findNearestRouteNode(player, nodes);
			if (nearestNode == null)
				return false;
			
			_routeCurrentIndex = nodes.indexOf(nearestNode);
			_routeReversePath = false;
			
			if (player.isIn3DRadius(nearestNode, 50))
			{
				if (_routeCurrentIndex < nodes.size() - 1)
					_routeCurrentIndex++;
				else if (nodes.size() > 1)
					_routeCurrentIndex = 0;
			}
		}
		
		Location currentTargetNode = nodes.get(_routeCurrentIndex);
		if (player.isIn3DRadius(currentTargetNode, 50))
		{
			if (_routeReversePath)
			{
				if (_routeCurrentIndex > 0)
				{
					_routeCurrentIndex--;
				}
				else
				{
					_routeReversePath = false;
					if (nodes.size() > 1)
						_routeCurrentIndex = 1;
					else
						_routeCurrentIndex = 0;
				}
			}
			else
			{
				if (_routeCurrentIndex < nodes.size() - 1)
				{
					_routeCurrentIndex++;
				}
				else
				{
					_routeReversePath = true;
					if (nodes.size() > 1)
						_routeCurrentIndex = nodes.size() - 2;
					else
						_routeCurrentIndex = 0;
				}
			}
			
			if (_routeCurrentIndex >= 0 && _routeCurrentIndex < nodes.size())
				currentTargetNode = nodes.get(_routeCurrentIndex);
			else
				return false;
			
			onRouteNodeAdvanced();
		}
		
		final Monster nearbyTarget = findNearbyAttackableTarget(player);
		if (nearbyTarget != null)
		{
			return false;
		}
		
		return tryMoveToNextRouteNode(player, currentTargetNode, nodes);
	}
	
	/**
	 * Encontra a node de rota mais próxima do player.
	 * @param player O jogador
	 * @param nodes Lista de nodes da rota
	 * @return A node mais próxima ou null se a lista estiver vazia
	 */
	private Location findNearestRouteNode(Player player, List<Location> nodes)
	{
		if (nodes.isEmpty())
			return null;
		
		return nodes.stream()
			.min(Comparator.comparingDouble(node -> player.distance3D(node)))
			.orElse(null);
	}
	
	/**
	 * Tenta mover para a próxima node da rota usando maybePlayerMoveToLocation com pathfinding.
	 * @param player O jogador
	 * @param targetNode A node alvo
	 * @param nodes Lista completa de nodes (para validação de proximidade)
	 * @return true se conseguiu iniciar o movimento
	 */
	private boolean tryMoveToNextRouteNode(Player player, Location targetNode, List<Location> nodes)
	{
		boolean isTargetNearRoute = nodes.stream().anyMatch(node -> 
			targetNode.distance3D(node) <= ROUTE_NODE_PROXIMITY_RADIUS);
		
		if (!isTargetNearRoute)
		{
			sendAdminMessage(player, "Target node too far from route");
			return false;
		}
		
		Location currentPos = player.getPosition();
		
		if (MovementIntegration.canMoveToTargetForAutoFarm(
			currentPos.getX(), currentPos.getY(), currentPos.getZ(),
			targetNode.getX(), targetNode.getY(), targetNode.getZ()))
		{
			if (player.getMove().maybePlayerMoveToLocation(targetNode, 0, Config.SISTEMA_PATHFINDING, false))
			{
				_routeLastExplorationTime = System.currentTimeMillis();
				sendAdminMessage(player, "Moving to route node " + (_routeCurrentIndex + 1));
				return true;
			}
		}
		else
		{
			final List<Location> path = GeoEngine.getInstance().findPath(
				currentPos.getX(), currentPos.getY(), currentPos.getZ(),
				targetNode.getX(), targetNode.getY(), targetNode.getZ(),
				true, null
			);
			
			if (path != null && !path.isEmpty())
			{
				final Location pathPoint = path.get(0);
				
				boolean pathPointNearRoute = nodes.stream().anyMatch(node -> 
					pathPoint.distance3D(node) <= ROUTE_NODE_PROXIMITY_RADIUS);
				
				if (pathPointNearRoute && player.distance3D(pathPoint) > 50)
				{
					if (player.getMove().maybePlayerMoveToLocation(pathPoint, 0, false, false))
					{
						_routeLastExplorationTime = System.currentTimeMillis();
						sendAdminMessage(player, "Moving to route node (pathfinding around obstacle)");
						return true;
					}
				}
				else
				{
					Location nearestNode = findNearestRouteNode(player, nodes);
					if (nearestNode != null && player.getMove().maybePlayerMoveToLocation(nearestNode, 0, Config.SISTEMA_PATHFINDING, false))
					{
						_routeLastExplorationTime = System.currentTimeMillis();
						sendAdminMessage(player, "Moving to nearest route node (pathfinding point too far)");
						return true;
					}
				}
			}
			else
			{
				Location nearestNode = findNearestRouteNode(player, nodes);
				if (nearestNode != null && player.getMove().maybePlayerMoveToLocation(nearestNode, 0, Config.SISTEMA_PATHFINDING, false))
				{
					_routeLastExplorationTime = System.currentTimeMillis();
					sendAdminMessage(player, "Moving to nearest route node (pathfinding failed)");
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Verifica se há um target próximo que pode ser atacado antes de mover na rota.
	 * Verifica range de arma ou skill com todos os parâmetros de restrições.
	 * @param player O jogador
	 * @return Monster próximo que pode ser atacado, ou null se não houver
	 */
	private Monster findNearbyAttackableTarget(Player player)
	{
		final int searchRange = 400;
		final AutoFarmArea area = _autoFarmProfile.getSelectedArea();
		final boolean isRouteMode = area.getType() == AutoFarmType.ROTA;
		final AutoFarmRoute route = isRouteMode ? area.getRouteZone() : null;
		final List<Location> routeNodes = isRouteMode && route != null ? route.getNodes() : new ArrayList<>();
		
		final List<Monster> candidates = player.getKnownTypeInRadius(Monster.class, searchRange).stream()
			.filter(m -> m != null && !m.isDead())
			.filter(m -> !isInvalidTarget(m))
			.filter(m -> !_unreachableTargets.containsKey(m.getObjectId()))
			.filter(m -> !isRouteMode || (!isRouteTargetBanned(m.getObjectId()) && isTargetNearRouteNodes(m, routeNodes)))
			.filter(m -> _autoFarmProfile.getTargets().isEmpty() || _autoFarmProfile.getTargets().contains(m.getName()))
			.sorted(Comparator.comparingDouble(m -> player.distance3D(m)))
			.collect(Collectors.toList());
		
		if (candidates.isEmpty())
			return null;
		
		for (Monster target : candidates)
		{
			final double dist = player.distance3D(target);
			
			final int physicalAttackRange = player.getStatus().getPhysicalAttackRange();
			final boolean isMeleeWeapon = physicalAttackRange <= 80;
			int attackRange;
			
			if (isMeleeWeapon)
			{
				attackRange = 300;
			}
			else
			{
				attackRange = physicalAttackRange;
			}
			
			final int totalAttackRange = attackRange + (int)(player.getCollisionRadius() + target.getCollisionRadius());
			
			if (dist <= totalAttackRange)
			{
				if (!_autoFarmProfile.getAttackSkills().isEmpty())
				{
					final List<L2Skill> attackSkills = _autoFarmProfile.getAttackSkills(false);
					for (L2Skill skill : attackSkills)
					{
						if (canUseSkillOnTarget(player, target, skill, dist))
						{
							return target;
						}
					}
				}
				else
				{
					if (dist <= totalAttackRange)
					{
						if (MovementIntegration.canSeeTargetForAutoFarm(player, target))
						{
							return target;
						}
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Lógica específica para movimento em modo OPEN.
	 * Quando não há targets próximos, explora a área procurando por novos monstros.
	 * Usa pathfinding inteligente para contornar obstáculos.
	 */
	private boolean handleOpenMovement(Player player, AutoFarmArea area)
	{
		if (System.currentTimeMillis() - _openLastExplorationTime < OPEN_EXPLORATION_DELAY_MS)
			return false;
		
		final AutoFarmRoute availableRoute = findAvailableRouteForExploration();
		if (availableRoute != null && !availableRoute.getNodes().isEmpty())
		{
			if (availableRoute.tryMoveToNextNode())
			{
				_openLastExplorationTime = System.currentTimeMillis();
				return true;
			}
		}
		
		exploreInCircleWithPathfinding(player, _autoFarmProfile.getFinalRadius());
		_openLastExplorationTime = System.currentTimeMillis();
		return true;
	}
	
	/**
	 * Encontra uma rota disponível que possa ser usada para exploração no modo OPEN.
	 * @return Uma rota disponível ou null se não houver nenhuma.
	 */
	private AutoFarmRoute findAvailableRouteForExploration()
	{
		for (AutoFarmArea area : _autoFarmProfile.getAreas().values())
		{
			if (area.getType() == AutoFarmType.ROTA && area instanceof AutoFarmRoute)
			{
				final AutoFarmRoute route = (AutoFarmRoute) area;
				if (route.getRouteZone() != null && !route.getNodes().isEmpty())
				{
					return route;
				}
			}
		}
		return null;
	}
	
	/**
	 * Realiza movimento exploratório circular com pathfinding para contornar obstáculos.
	 * Sistema exclusivo para AutoFarm - não interfere com movimento normal do player.
	 */
	private void exploreInCircleWithPathfinding(Player player, int radius)
	{
		Location currentPos = player.getPosition();
		if (_lastOpenExplorationPos == null)
			_lastOpenExplorationPos = currentPos.clone();
		
		final long time = System.currentTimeMillis() / 1000;
		final double angle = (time % 8) * (Math.PI / 4);
		final int dist = Math.min(radius / 2, 500);
		
		final int targetX = currentPos.getX() + (int)(Math.cos(angle) * dist);
		final int targetY = currentPos.getY() + (int)(Math.sin(angle) * dist);
		final int targetZ = currentPos.getZ();
		
		final Location targetLoc = new Location(targetX, targetY, targetZ);
		
		if (MovementIntegration.canMoveToTargetForAutoFarm(currentPos.getX(), currentPos.getY(), currentPos.getZ(), targetX, targetY, targetZ))
		{
			player.getMove().maybePlayerMoveToLocation(targetLoc, 0, Config.SISTEMA_PATHFINDING, false);
			_lastOpenExplorationPos = targetLoc;
			sendAdminMessage(player, "Exploring area (circular)");
		}
		else
		{
			final List<Location> path = GeoEngine.getInstance().findPath(
				currentPos.getX(), currentPos.getY(), currentPos.getZ(),
				targetX, targetY, targetZ,
				true, null
			);
			
			if (path != null && !path.isEmpty())
			{
				final Location pathPoint = path.get(0);
				if (player.distance3D(pathPoint) > 50)
				{
					player.getMove().maybePlayerMoveToLocation(pathPoint, 0, Config.SISTEMA_PATHFINDING, false);
					_lastOpenExplorationPos = pathPoint;
					sendAdminMessage(player, "Exploring area (pathfinding around obstacle)");
					return;
				}
			}
			
			final int opX = currentPos.getX() - (int)(Math.cos(angle) * dist);
			final int opY = currentPos.getY() - (int)(Math.sin(angle) * dist);
			final Location opLoc = new Location(opX, opY, targetZ);
			
			if (MovementIntegration.canMoveToTargetForAutoFarm(currentPos.getX(), currentPos.getY(), currentPos.getZ(), opX, opY, targetZ))
			{
				player.getMove().maybePlayerMoveToLocation(opLoc, 0, Config.SISTEMA_PATHFINDING, false);
				_lastOpenExplorationPos = opLoc;
			}
			else
			{
				final List<Location> opPath = GeoEngine.getInstance().findPath(
					currentPos.getX(), currentPos.getY(), currentPos.getZ(),
					opX, opY, targetZ,
					true, null
				);
				
				if (opPath != null && !opPath.isEmpty())
				{
					final Location opPathPoint = opPath.get(0);
					if (player.distance3D(opPathPoint) > 50)
					{
						player.getMove().maybePlayerMoveToLocation(opPathPoint, 0, Config.SISTEMA_PATHFINDING, false);
						_lastOpenExplorationPos = opPathPoint;
						sendAdminMessage(player, "Exploring area (pathfinding opposite direction)");
					}
				}
			}
		}
	}
	
	/**
	 * Lógica principal de combate melhorada com verificação inteligente de range e garantia de ataque.
	 * 
	 * <h3>Melhorias Implementadas:</h3>
	 * <ul>
	 *   <li><b>Verificação Inicial Inteligente:</b> Diferencia melee (range <= 80) de arqueiros (range > 200)</li>
	 *   <li><b>Classes Melee:</b> Usa dist2D, verifica canAttack + tryToAttack para garantir ataque</li>
	 *   <li><b>Arqueiros:</b> Respeita range do arco e intercala skills</li>
	 *   <li><b>Magos:</b> Prioriza skills ofensivas mágicas, nunca ataca fisicamente</li>
	 *   <li><b>Movimento:</b> Usa maybeStartOffensiveFollow ao invés de tryToMoveTo</li>
	 * </ul>
	 * 
	 * @param player O jogador que está atacando
	 * @param target O monstro alvo
	 * 
	 * @see #getRotatedSkillForFighter(Player, Monster, double)
	 * @see #getRotatedSkillForMage(Player, Monster, double)
	 * @see #selectBestMageSkill(Player, Monster, List, double)
	 */
	private void handleCombat(Player player, Monster target)
	{
		if (_lastPathTarget != null && !_lastPathTarget.equals(target.getPosition()))
		{
			_currentPath.clear();
			_lastPathTarget = null;
		}
		
		if (checkTargetStuck(player, target))
		{
			sendAdminMessage(player, "Target stuck, returning to route...");
			player.setTarget(null);
			resetStuckLogic();
			markAsUnreachable(target.getObjectId());
			_currentPath.clear();
			_lastPathTarget = null;
			if (_autoFarmProfile.getSelectedArea().getType() == AutoFarmType.ROTA)
			{
			}
			return;
		}

		final AutoFarmArea area = _autoFarmProfile.getSelectedArea();
		if (area != null && area.getType() == AutoFarmType.ZONA)
		{
			if (!player.isInsideZone(ZoneId.AUTO_FARM))
			{
				if (moveBackInsideZone(player, area))
					return;
				
				stop("Character outside the zone and cannot return.");
				return;
			}
			
			if (!isTargetInsideZone(target))
			{
				sendAdminMessage(player, "Target left zone - returning to zone interior");
				markAsUnreachable(target.getObjectId());
				player.setTarget(null);
				moveBackInsideZone(player, area);
				return;
			}
		}
		
		if (player.getTarget() == target)
		{
			if (player.getAI().getCurrentIntention().getType() == IntentionType.CAST && player.getAI().getCurrentIntention().getFinalTarget() == target)
			{
				if (player.getCast().isCastingNow())
					return;
			}
		}
		
		if (_autoFarmProfile.useSpoilSweep() && !target.getSpoilState().isSpoiled() && 
			!player.getAttack().isAttackingNow() && !player.getCast().isCastingNow())
		{
			L2Skill spoilSkill = player.getSkill(302);
			if (spoilSkill == null)
				spoilSkill = player.getSkill(254);
			
			if (spoilSkill != null && !player.isSkillDisabled(spoilSkill))
			{
				final double spoilDist = player.distance3D(target);
				final double spoilDist2D = player.distance2D(target);
				
				if (spoilDist <= spoilSkill.getCastRange())
				{
					boolean canSpoil = MovementIntegration.canSeeTargetForAutoFarm(player, target);
					
					if (!canSpoil && spoilDist2D < 150)
					{
						final int heightDiff = Math.abs(player.getZ() - target.getZ());
						if (heightDiff < 150)
						{
							canSpoil = true;
						}
					}
					
					if (canSpoil)
					{
						setIntention(player, IntentionType.CAST, spoilSkill, target);
						sendAdminMessage(player, "Spoiling " + target.getName());
						return;
					}
				}
			}
		}
		
		if (_autoFarmProfile.attackSummon() && player.getSummon() != null)
		{
			player.getSummon().setTarget(target);
			player.getSummon().getAI().tryToAttack(target);
		}
		
		final double dist = player.distance3D(target);
		final double dist2D = player.distance2D(target);
		
		final int physicalAttackRange = player.getStatus().getPhysicalAttackRange();
		final boolean isBowWeapon = player.getAttackType() == WeaponType.BOW;
		final CombatRole combatRole = getCombatRole(player, physicalAttackRange, isBowWeapon);
		final boolean isMageRole = combatRole == CombatRole.MAGE;
		final boolean isFighterRole = combatRole != CombatRole.MAGE;
		final boolean isArcherRole = combatRole == CombatRole.ARCHER;
		
		if (isArcherRole)
		{
			final int bowRange = physicalAttackRange;
			final int totalBowRange = bowRange + (int)(player.getCollisionRadius() + target.getCollisionRadius());
			handleArcherCombat(player, target, dist, dist2D, bowRange, totalBowRange);
			return;
		}
		
		final boolean isMeleeWeapon = physicalAttackRange <= 80;
		final boolean isOpenMode = _autoFarmProfile.getSelectedArea().getType() == AutoFarmType.OPEN;
		final boolean isRouteMode = _autoFarmProfile.getSelectedArea().getType() == AutoFarmType.ROTA;
		
		int attackRange;
		if (isRouteMode)
		{
			if (isMeleeWeapon)
			{
				attackRange = physicalAttackRange;
			}
			else
			{
				attackRange = physicalAttackRange;
			}
		}
		else if (isMeleeWeapon && isOpenMode && isFighterRole)
		{
			attackRange = physicalAttackRange;
		}
		else
		{
			attackRange = Math.max(physicalAttackRange, _autoFarmProfile.getAttackRange());
		}
		
		final double collisionBuffer = player.getCollisionRadius() + target.getCollisionRadius();
		final int totalAttackRange = (int)(attackRange + collisionBuffer);
		
		boolean canSee = checkLineOfSightForAttack(player, target, dist2D);
		
		if (!canSee)
		{
			if (tryPathfindingAroundObstacle(player, target))
			{
				sendAdminMessage(player, "Moving around obstacle (pathfinding) to reach target");
				return;
			}
			
			if (player.getMove().maybeStartOffensiveFollow(target, attackRange))
			{
				sendAdminMessage(player, "Moving to attack range (obstacle detected)");
				return;
			}
			else
			{
				sendAdminMessage(player, "Target unreachable - returning to route");
				markAsUnreachable(target.getObjectId());
				player.setTarget(null);
				if (_autoFarmProfile.getSelectedArea().getType() == AutoFarmType.ROTA)
				{
				}
				return;
			}
		}
		
		if (isMageRole && !_autoFarmProfile.getAttackSkills().isEmpty())
		{
			handleMageCombat(player, target, dist);
			return;
		}
		
		if (isMageRole && !_autoFarmProfile.getAttackSkills().isEmpty())
		{
			if (dist > 300)
			{
				final L2Skill farSkill = getFarthestAttackSkill(player);
				final int range = farSkill != null ? (farSkill.getCastRange() > 0 ? farSkill.getCastRange() : farSkill.getSkillRadius()) : 300;
				if (player.getMove().maybeStartOffensiveFollow(target, range))
				{
					sendAdminMessage(player, "Mage: All skills on cooldown, moving closer");
					return;
				}
				return;
			}
			else
			{
				player.getMove().stop();
				sendAdminMessage(player, "Mage: All skills on cooldown, waiting (close to target)");
				return;
			}
		}
		
		if (isMeleeWeapon && isFighterRole)
		{
			handleMeleeCombat(player, target, dist, dist2D, attackRange, totalAttackRange);
			return;
		}
		
		final boolean isInAttackRange = isMeleeWeapon && isOpenMode && isFighterRole
			? dist <= totalAttackRange 
			: dist <= attackRange;
		
		if (isInAttackRange)
		{
			if (isMeleeWeapon && isOpenMode && isFighterRole && dist2D <= totalAttackRange * 0.9)
			{
				player.getMove().stop();
			}
			
			if (isRouteMode && Config.SISTEMA_PATHFINDING)
			{
				if (player.getMove().maybeStartPlayerOffensiveFollow(target, attackRange))
				{
					sendAdminMessage(player, "ROTA: Moving to optimal attack position");
					return;
				}
			}
			
			if (dist2D > 200 && !MovementIntegration.canSeeTarget(player, target))
			{
				if (player.getMove().maybeStartOffensiveFollow(target, attackRange))
				{
					sendAdminMessage(player, "Cannot attack through obstacle, using offensive follow");
					return;
				}
				if (_autoFarmProfile.getSelectedArea().getType() == AutoFarmType.ROTA)
				{
					sendAdminMessage(player, "Cannot attack through obstacle - returning to route");
					markAsUnreachable(target.getObjectId());
					player.setTarget(null);
					return;
				}
				sendAdminMessage(player, "Cannot attack through obstacle - waiting for better position");
				return;
			}
			
			if (!isMeleeWeapon && isFighterRole)
			{
				if (areaTypeIsZoneAndNoSkills())
				{
					setIntention(player, IntentionType.ATTACK, target);
					sendAdminMessage(player, "Zone mode: Using normal attack");
				}
				else
				{
					final L2Skill skill = getRotatedSkillForFighter(player, target, dist);
					
					if (skill != null)
					{
						final int skillRange = skill.getCastRange() > 0 ? skill.getCastRange() : skill.getSkillRadius();
						final int totalSkillRange = skillRange + (int)(player.getCollisionRadius() + target.getCollisionRadius());
						
						if (dist <= totalSkillRange + 50)
						{
							if (canUseSkillOnTarget(player, target, skill, dist))
							{
								player.getMove().stop();
								setIntention(player, IntentionType.CAST, skill, target);
								sendAdminMessage(player, "Archer casting: " + skill.getName() + " (range: " + (int)dist2D + "/" + totalSkillRange + ")");
								_cachedMessages.clear();
								return;
							}
						}
					}
					
					setIntention(player, IntentionType.ATTACK, target);
					sendAdminMessage(player, "Archer attack (range: " + (int)dist2D + "/" + totalAttackRange + ")");
				}
			}
			_cachedMessages.clear();
		}
		else
		{
			if (isMageRole && !_autoFarmProfile.getAttackSkills().isEmpty())
			{
				final L2Skill farSkill = getFarthestAttackSkill(player);
				if (farSkill != null)
				{
					final int skillRange = farSkill.getCastRange() > 0 ? farSkill.getCastRange() : farSkill.getSkillRadius();
					if (player.getMove().maybeStartOffensiveFollow(target, skillRange))
					{
						sendAdminMessage(player, "Mage: Moving to skill range (" + farSkill.getName() + " range: " + skillRange + ")");
						return;
					}
				}
				else
				{
					if (player.getMove().maybeStartOffensiveFollow(target, 300))
					{
						sendAdminMessage(player, "Mage: All skills on cooldown, moving closer");
						return;
					}
				}
			}
			else if (!isMageRole)
			{
				final L2Skill farSkill = getFarthestAttackSkill(player);
				final int range = farSkill != null ? (farSkill.getCastRange() > 0 ? farSkill.getCastRange() : farSkill.getSkillRadius()) : attackRange;
				if (player.getMove().maybeStartOffensiveFollow(target, range))
				{
					return;
				}
			}
		}
	}

	private void handleArcherCombat(Player player, Monster target, double dist, double dist2D, int bowRange, int totalBowRange)
	{
		final boolean kiteEnabled = _autoFarmProfile.getSelectedArea().getType() != AutoFarmType.ZONA
			&& _autoFarmProfile.getSelectedArea().getType() != AutoFarmType.ROTA;
		
		if (dist <= totalBowRange)
		{
			player.getMove().stop();
			
			if (dist2D > 200 && !MovementIntegration.canSeeTarget(player, target))
			{
				if (tryPathfindingAroundObstacle(player, target))
				{
					sendAdminMessage(player, "Bow: Moving around obstacle (pathfinding)");
					return;
				}
				
				final Location bypass = calculateBypassPosition(player, target);
				if (bypass != null)
				{
					player.getMove().maybePlayerMoveToLocation(bypass, 0, Config.SISTEMA_PATHFINDING, false);
					sendAdminMessage(player, "Bow: Moving around obstacle (bypass)");
					return;
				}
				
				moveToAttackPosition(player, target, bowRange);
				setIntention(player, IntentionType.ATTACK, target);
				sendAdminMessage(player, "Bow: Cannot attack through obstacle, moving to attack position");
				return;
			}
			
			if (kiteEnabled)
			{
				final long now = System.currentTimeMillis();
				if (now - _lastArcherKiteTime < ARCHER_KITE_DELAY_MS)
				{
					if (tryArcherKiteRetreat(player, target, bowRange))
					{
						sendAdminMessage(player, "Bow: Waiting kite cooldown, moving away");
					}
					return;
				}
			}
			
			final boolean trySkill = !_autoFarmProfile.getAttackSkills().isEmpty() && Rnd.get(100) < 30;
			if (trySkill)
			{
				final L2Skill skill = findBestAvailableOffensiveSkill(player, target, dist);
				if (skill != null)
				{
					_lastArcherShotTime = System.currentTimeMillis();
					setIntention(player, IntentionType.CAST, skill, target);
					final int skillRange = skill.getCastRange() > 0 ? skill.getCastRange() : skill.getSkillRadius();
					final int totalSkillRange = skillRange + (int)(player.getCollisionRadius() + target.getCollisionRadius());
					sendAdminMessage(player, "Bow: Casting skill " + skill.getName() + " (range: " + (int)dist2D + "/" + totalSkillRange + ")");
					_cachedMessages.clear();
					return;
				}
			}
			
			_lastArcherShotTime = System.currentTimeMillis();
			setIntention(player, IntentionType.ATTACK, target);
			sendAdminMessage(player, "Bow: Basic attack (range: " + (int)dist2D + "/" + totalBowRange + ")");
			_cachedMessages.clear();
			
			if (kiteEnabled)
			{
				tryArcherKiteStep(player, target, bowRange);
			}
			return;
		}
		
		moveToAttackPosition(player, target, bowRange);
		setIntention(player, IntentionType.ATTACK, target);
		sendAdminMessage(player, "Bow: Moving to attack range (range: " + bowRange + ")");
	}

	private void handleMageCombat(Player player, Monster target, double dist)
	{
		L2Skill skill = getCastSkill(player);
		if (skill == null)
		{
			skill = getRotatedSkillFromListForMage(player, target, dist, _autoFarmProfile.getAttackSkills(false));
		}
		if (skill == null)
		{
			skill = getBestAvailableSkillForMage(player, target);
		}
		if (skill == null)
		{
			skill = selectMageSkillCascade(player, target, dist);
		}
		
		if (skill != null)
		{
			if (maybeApproachForMageSkill(player, target, skill, dist))
			{
				return;
			}
			
			player.getMove().stop();
			
			if (MovementIntegration.canSeeTargetForAutoFarm(player, target))
			{
				player.getAI().tryToCast(target, skill, false, false, 0);
				sendAdminMessage(player, "Mage Cast: " + skill.getName() + " -> " + target.getName());
				_cachedMessages.clear();
				return;
			}
			if (tryPathfindingAroundObstacle(player, target))
			{
				sendAdminMessage(player, "Mage: Moving around obstacle (pathfinding)");
				return;
			}
			
			final Location bypass = calculateBypassPosition(player, target);
			if (bypass != null)
			{
				player.getMove().maybePlayerMoveToLocation(bypass, 0, Config.SISTEMA_PATHFINDING, false);
				sendAdminMessage(player, "Mage: Moving around obstacle (bypass)");
				return;
			}
			
			return;
		}
		
		final L2Skill farSkill = getFarthestAttackSkill(player);
		final int range = farSkill != null ? (farSkill.getCastRange() > 0 ? farSkill.getCastRange() : farSkill.getSkillRadius()) : 300;
		if (dist > range)
		{
			moveToAttackPosition(player, target, range);
			if (farSkill != null)
			{
				setIntention(player, IntentionType.CAST, farSkill, target);
			}
			return;
		}
		
		player.getMove().stop();
		player.getAI().tryToIdle();
		player.sendPacket(ext.mods.gameserver.network.serverpackets.ActionFailed.STATIC_PACKET);
		sendAdminMessage(player, "Mage Waiting (Skills CD)");
	}

	/**
	 * Aproxima o mago antes de castar a skill, com verificações inteligentes de range e LoS.
	 * @return true se iniciou movimento, false se já pode tentar castar.
	 */
	private boolean maybeApproachForMageSkill(Player player, Monster target, L2Skill skill, double dist)
	{
		int skillRange = skill.getCastRange() > 0 ? skill.getCastRange() : skill.getSkillRadius();
		if (skillRange <= 0)
		{
			skillRange = 300;
		}
		
		final int safeRange = skillRange + (int)(player.getCollisionRadius() + target.getCollisionRadius()) + 50;
		final double dist2D = player.distance2D(target);
		
		if (dist > safeRange || dist2D > safeRange)
		{
			setIntention(player, IntentionType.CAST, skill, target);
			moveToAttackPosition(player, target, skillRange);
			setIntention(player, IntentionType.CAST, skill, target);
			sendAdminMessage(player, "Mage: Approaching skill range (" + skillRange + ")");
			return true;
		}
		
		if (!MovementIntegration.canSeeTargetForAutoFarm(player, target))
		{
			setIntention(player, IntentionType.CAST, skill, target);
			if (tryPathfindingAroundObstacle(player, target))
			{
				sendAdminMessage(player, "Mage: Approaching with pathfinding (LoS blocked)");
				return true;
			}
			
			final Location bypass = calculateBypassPosition(player, target);
			if (bypass != null)
			{
				player.getMove().maybePlayerMoveToLocation(bypass, 0, Config.SISTEMA_PATHFINDING, false);
				sendAdminMessage(player, "Mage: Approaching with bypass (LoS blocked)");
				return true;
			}
			
			moveToAttackPosition(player, target, skillRange);
			setIntention(player, IntentionType.CAST, skill, target);
			return true;
		}
		
		return false;
	}

	private boolean moveToAttackPosition(Player player, Creature target, int desiredRange)
	{
		if (isMeleeDebug(player))
		{
			LOGGER.info("[MeleeDebug] moveToAttackPosition: player={} target={} range={} dist={}",
				player.getObjectId(), target.getObjectId(), desiredRange, player.distance2D(target));
		}

		if (player.getMove().maybeStartPlayerOffensiveFollow(target, desiredRange))
		{
			if (isMeleeDebug(player))
			{
				LOGGER.info("[MeleeDebug] moveToAttackPosition: startOffensiveFollow player={} target={} range={}",
					player.getObjectId(), target.getObjectId(), desiredRange);
			}
			return true;
		}
		
		Location approach = calculateApproachLocation(player, target, desiredRange);
		Location finalApproach = approach;
		final long now = System.currentTimeMillis();
		final int targetId = target.getObjectId();
		if (_stuckRecoveryTargetId != targetId)
		{
			_stuckRecoveryTargetId = targetId;
			_stuckRecoveryUntil = 0;
		}
		
		if (!MovementIntegration.canMoveToTargetForAutoFarm(
			player.getX(), player.getY(), player.getZ(),
			approach.getX(), approach.getY(), approach.getZ()))
		{
			if (isMeleeDebug(player))
			{
				LOGGER.info("[MeleeDebug] moveToAttackPosition: blocked player={} target={} approach={}",
					player.getObjectId(), target.getObjectId(), approach);
			}

			if (_stuckRecoveryUntil == 0)
			{
				_stuckRecoveryUntil = now + 15000;
			}
			else if (now > _stuckRecoveryUntil)
			{
				markAsUnreachable(targetId);
				_stuckRecoveryUntil = 0;
				_stuckRecoveryTargetId = 0;
				return false;
			}
			
			final Location safePoint = findSafePointNear(player.getPosition(), approach);
			if (safePoint != null)
			{
				if (isMeleeDebug(player))
				{
					LOGGER.info("[MeleeDebug] moveToAttackPosition: safePoint player={} target={} safePoint={}",
						player.getObjectId(), target.getObjectId(), safePoint);
				}
				finalApproach = safePoint;
			}
			else
			{
				final Location bypass = calculateBypassPosition(player, target);
				if (bypass != null)
				{
					if (isMeleeDebug(player))
					{
						LOGGER.info("[MeleeDebug] moveToAttackPosition: bypass player={} target={} bypass={}",
							player.getObjectId(), target.getObjectId(), bypass);
					}
					finalApproach = bypass;
				}
				else
				{
					final Location valid = GeoEngine.getInstance().getValidLocation(
						player.getX(), player.getY(), player.getZ(),
						approach.getX(), approach.getY(), approach.getZ(), null);
					if (valid != null)
					{
						if (isMeleeDebug(player))
						{
							LOGGER.info("[MeleeDebug] moveToAttackPosition: validLoc player={} target={} valid={}",
								player.getObjectId(), target.getObjectId(), valid);
						}
						finalApproach = valid;
					}
				}
			}
		}
		else
		{
			_stuckRecoveryUntil = 0;
		}
		
		return player.getMove().maybePlayerMoveToLocation(finalApproach, 0, Config.SISTEMA_PATHFINDING, false);
	}

	private Location calculateApproachLocation(Player player, Creature target, int desiredRange)
	{
		final int range = Math.max(0, desiredRange);
		final double dist2D = player.distance2D(target);
		
		if (dist2D <= range || dist2D < 50)
		{
			return new Location(target.getX(), target.getY(), target.getZ());
		}
		
		final double dx = target.getX() - player.getX();
		final double dy = target.getY() - player.getY();
		final double ratio = (dist2D - range) / dist2D;
		
		final int x = player.getX() + (int)Math.round(dx * ratio);
		final int y = player.getY() + (int)Math.round(dy * ratio);
		final int z = target.getZ();
		
		final Location valid = GeoEngine.getInstance().getValidLocation(
			player.getX(), player.getY(), player.getZ(),
			x, y, z, null
		);
		
		if (valid != null)
		{
			return valid;
		}
		
		return new Location(x, y, z);
	}

	private CombatRole getCombatRole(Player player)
	{
		final int physicalAttackRange = player.getStatus().getPhysicalAttackRange();
		final boolean isBowWeapon = player.getAttackType() == WeaponType.BOW;
		return getCombatRole(player, physicalAttackRange, isBowWeapon);
	}

	private CombatRole getCombatRole(Player player, int physicalAttackRange, boolean isBowWeapon)
	{
		final int pAtk = player.getStatus().getPAtk(null);
		final int mAtk = player.getStatus().getMAtk(null, null);
		if (mAtk > pAtk)
			return CombatRole.MAGE;
		
		if (isBowWeapon || physicalAttackRange > 200)
			return CombatRole.ARCHER;
		
		return CombatRole.FIGHTER;
	}

	private boolean isMageRole(Player player)
	{
		return getCombatRole(player) == CombatRole.MAGE;
	}

	private void handleMeleeCombat(Player player, Monster target, double dist, double dist2D, int attackRange, int totalAttackRange)
	{
		if (dist2D > totalAttackRange)
		{
			moveToAttackPosition(player, target, attackRange);
			setIntention(player, IntentionType.ATTACK, target);
			sendAdminMessage(player, "Melee: Moving to attack range (range: " + (int)dist2D + "/" + totalAttackRange + ")");
			return;
		}
		
		player.getMove().stop();
		final boolean canSee = dist2D > 200
			? MovementIntegration.canSeeTarget(player, target)
			: MovementIntegration.canSeeTargetForAutoFarm(player, target);
		
		if (!canSee)
		{
			if (tryPathfindingAroundObstacle(player, target))
			{
				sendAdminMessage(player, "Melee: Moving around obstacle (pathfinding)");
				return;
			}
			
			final Location bypass = calculateBypassPosition(player, target);
			if (bypass != null)
			{
				player.getMove().maybePlayerMoveToLocation(bypass, 0, Config.SISTEMA_PATHFINDING, false);
				sendAdminMessage(player, "Melee: Moving around obstacle (bypass)");
				return;
			}
			
			moveToAttackPosition(player, target, attackRange);
			setIntention(player, IntentionType.ATTACK, target);
			sendAdminMessage(player, "Melee: Repositioning for LoS");
			return;
		}
		
		setIntention(player, IntentionType.ATTACK, target);
		
		if (!areaTypeIsZoneAndNoSkills() && Rnd.get(100) < 40)
		{
			L2Skill selectedSkill = getRotatedSkillForFighter(player, target, dist2D);
			if (selectedSkill != null && (!canUseSkillOnTarget(player, target, selectedSkill, dist2D) || !selectedSkill.checkCondition(player, target, false)))
			{
				selectedSkill = getNextRotatedSkillForFighter(player, target, dist2D, selectedSkill);
			}
			if (selectedSkill != null && canUseSkillOnTarget(player, target, selectedSkill, dist2D) && selectedSkill.checkCondition(player, target, false))
			{
				player.getMove().stop();
				setIntention(player, IntentionType.CAST, selectedSkill, target);
				final int skillRange = selectedSkill.getCastRange() > 0 ? selectedSkill.getCastRange() : selectedSkill.getSkillRadius();
				final int totalSkillRange = skillRange + (int)(player.getCollisionRadius() + target.getCollisionRadius());
				sendAdminMessage(player, "Melee: Using offensive skill: " + selectedSkill.getName() + " (range: " + (int)dist2D + "/" + totalSkillRange + ")");
				_cachedMessages.clear();
				return;
			}
		}
		
		if (player.getAttack().canAttack(target) && !player.getAttack().isAttackingNow())
		{
			final long now = System.currentTimeMillis();
			if (now - _lastMeleeAttackTime < MELEE_ATTACK_COOLDOWN_MS)
			{
				return;
			}
			
			
			player.getMove().stop();
			setIntention(player, IntentionType.ATTACK, target);
			_lastMeleeAttackTime = now;
			sendAdminMessage(player, "Melee: Basic attack (range: " + (int)dist2D + "/" + totalAttackRange + ")");
			_cachedMessages.clear();
		}
	}
	
	private boolean areaTypeIsZoneAndNoSkills()
	{
		return _autoFarmProfile.getSelectedArea().getType() == AutoFarmType.ZONA && _autoFarmProfile.getAttackSkills().isEmpty();
	}

	private boolean isTargetInsideZone(Monster target)
	{
		final AutoFarmArea area = _autoFarmProfile.getSelectedArea();
		if (area == null || area.getType() != AutoFarmType.ZONA || area.getFarmZone() == null || area.getFarmZone().getZoneZ() == null)
			return true;
		
		return area.getFarmZone().getZoneZ().isInsideZone(target.getX(), target.getY(), target.getZ());
	}

	private boolean isLocationInsideZone(Location loc)
	{
		final AutoFarmArea area = _autoFarmProfile.getSelectedArea();
		if (area == null || area.getType() != AutoFarmType.ZONA || area.getFarmZone() == null || area.getFarmZone().getZoneZ() == null)
			return true;
		
		return area.getFarmZone().getZoneZ().isInsideZone(loc.getX(), loc.getY(), loc.getZ());
	}

	private boolean moveBackInsideZone(Player player, AutoFarmArea area)
	{
		if (area == null || area.getType() != AutoFarmType.ZONA || area.getFarmZone() == null)
			return false;
		
		if (area.getFarmZone().tryGoBackInside())
			return true;
		
		final Location center = area.getFarmZone().getZoneZ().findPointInCenter();
		if (center != null && player.getMove().maybePlayerMoveToLocation(center, 0, Config.SISTEMA_PATHFINDING, false))
			return true;
		
		final Location randomPoint = area.getFarmZone().getZoneZ().getRandomPoint();
		if (randomPoint != null)
		{
			player.getMove().maybePlayerMoveToLocation(randomPoint, 0, Config.SISTEMA_PATHFINDING, false);
			return true;
		}
		
		return false;
	}

	/**
	 * Tenta contornar obstáculos com pathfinding exclusivo do autofarm.
	 * @return true se iniciou movimento para um ponto do caminho.
	 */
	private boolean tryPathfindingAroundObstacle(Player player, Creature target)
	{
		if (isMeleeDebug(player))
		{
			LOGGER.info("[MeleeDebug] pathfinding: start player={} target={} dist={}",
				player.getObjectId(), target.getObjectId(), player.distance2D(target));
		}

		final Location playerPos = player.getPosition();
		final Location targetPos = target.getPosition();
		
		if (MovementIntegration.canMoveToTargetForAutoFarm(
			playerPos.getX(), playerPos.getY(), playerPos.getZ(),
			targetPos.getX(), targetPos.getY(), targetPos.getZ()))
		{
			return false;
		}
		
		Location pathPoint = calculatePathfindingApproach(player, target);
		if (pathPoint != null)
		{
			if (MovementIntegration.canMoveToTargetForAutoFarm(
				playerPos.getX(), playerPos.getY(), playerPos.getZ(),
				pathPoint.getX(), pathPoint.getY(), pathPoint.getZ()))
			{
				if (player.distance3D(pathPoint) > 50)
				{
					if (isMeleeDebug(player))
					{
						LOGGER.info("[MeleeDebug] pathfinding: pathPoint player={} target={} point={}",
							player.getObjectId(), target.getObjectId(), pathPoint);
					}
					return player.getMove().maybePlayerMoveToLocation(pathPoint, 0, Config.SISTEMA_PATHFINDING, false);
				}
			}
			
			_currentPath.clear();
			_lastPathTarget = null;
			pathPoint = calculatePathfindingApproach(player, target);
			if (pathPoint != null && player.distance3D(pathPoint) > 50)
			{
				return player.getMove().maybePlayerMoveToLocation(pathPoint, 0, Config.SISTEMA_PATHFINDING, false);
			}
		}
		
		final Location bypass = calculateBypassPosition(player, target);
		if (bypass != null)
		{
			if (isMeleeDebug(player))
			{
				LOGGER.info("[MeleeDebug] pathfinding: bypass player={} target={} bypass={}",
					player.getObjectId(), target.getObjectId(), bypass);
			}
			return player.getMove().maybePlayerMoveToLocation(bypass, 0, Config.SISTEMA_PATHFINDING, false);
		}
		
		final Location validLoc = GeoEngine.getInstance().getValidLocation(
			playerPos.getX(), playerPos.getY(), playerPos.getZ(),
			targetPos.getX(), targetPos.getY(), targetPos.getZ(), null
		);
		if (validLoc != null && player.distance3D(validLoc) > 50)
		{
			if (isMeleeDebug(player))
			{
				LOGGER.info("[MeleeDebug] pathfinding: validLoc player={} target={} validLoc={}",
					player.getObjectId(), target.getObjectId(), validLoc);
			}
			return player.getMove().maybePlayerMoveToLocation(validLoc, 0, Config.SISTEMA_PATHFINDING, false);
		}
		
		return false;
	}

	private void tryArcherKiteStep(Player player, Creature target, int bowRange)
	{
		if (_autoFarmProfile.getSelectedArea().getType() == AutoFarmType.ZONA
			|| _autoFarmProfile.getSelectedArea().getType() == AutoFarmType.ROTA)
		{
			return;
		}
		
		final long now = System.currentTimeMillis();
		if (player.getAttack().isAttackingNow() || player.getAttack().isBowCoolingDown()
			|| (now - _lastArcherShotTime < ARCHER_KITE_AFTER_SHOT_DELAY_MS))
		{
			return;
		}
		
		if (now - _lastArcherKiteTime < ARCHER_KITE_DELAY_MS)
			return;
		
		_lastArcherKiteTime = now;
		
		final double awayAngle = Math.atan2(player.getY() - target.getY(), player.getX() - target.getX());
		final double jitter = Math.toRadians(Rnd.get(-15, 15));
		final double moveAngle = awayAngle + jitter;
		final int step = Math.min(350, Math.max(180, bowRange / 3));
		final double currentDist = player.distance2D(target);
		final double maxDist = Math.max(120, bowRange - 30);
		final double desiredDist = Math.min(maxDist, currentDist + step);
		
		if (desiredDist <= currentDist + 10)
			return;
		
		final int x = (int) (target.getX() + Math.cos(moveAngle) * desiredDist);
		final int y = (int) (target.getY() + Math.sin(moveAngle) * desiredDist);
		final int z = player.getZ();
		final Location kiteLoc = new Location(x, y, z);
		
		if (!isLocationInsideZone(kiteLoc))
			return;
		
		if (MovementIntegration.canMoveToTargetForAutoFarm(player.getX(), player.getY(), player.getZ(), x, y, z))
		{
			player.getMove().maybePlayerMoveToLocation(kiteLoc, 0, Config.SISTEMA_PATHFINDING, false);
		}
	}

	private boolean tryArcherKiteRetreat(Player player, Creature target, int bowRange)
	{
		if (_autoFarmProfile.getSelectedArea().getType() == AutoFarmType.ZONA
			|| _autoFarmProfile.getSelectedArea().getType() == AutoFarmType.ROTA)
		{
			return false;
		}
		
		
		
		final double awayAngle = Math.atan2(player.getY() - target.getY(), player.getX() - target.getX());
		final double jitter = Math.toRadians(Rnd.get(-20, 20));
		final double moveAngle = awayAngle + jitter;
		final int step = Math.min(350, Math.max(180, bowRange / 3));
		final double currentDist = player.distance2D(target);
		final double maxDist = Math.max(120, bowRange - 30);
		final double desiredDist = Math.min(maxDist, currentDist + step);
		
		if (desiredDist <= currentDist + 10)
			return false;
		
		final int x = (int) (target.getX() + Math.cos(moveAngle) * desiredDist);
		final int y = (int) (target.getY() + Math.sin(moveAngle) * desiredDist);
		final int z = player.getZ();
		final Location kiteLoc = new Location(x, y, z);
		
		if (!isLocationInsideZone(kiteLoc))
			return false;
		
		if (MovementIntegration.canMoveToTargetForAutoFarm(player.getX(), player.getY(), player.getZ(), x, y, z))
		{
			return player.getMove().maybePlayerMoveToLocation(kiteLoc, 0, Config.SISTEMA_PATHFINDING, false);
		}
		
		return false;
	}

	private boolean isSurrenderSkill(L2Skill skill)
	{
		return skill != null && skill.getName() != null && skill.getName().toLowerCase().contains("surrender");
	}

	private boolean isVortexSkill(L2Skill skill)
	{
		return skill != null && skill.getName() != null && skill.getName().toLowerCase().contains("vortex");
	}

	private L2Skill chooseRotatedSkill(List<L2Skill> skills)
	{
		if (skills == null || skills.isEmpty())
			return null;
		
		if (_skillRotationIndex >= skills.size())
			_skillRotationIndex = 0;
		
		final L2Skill selectedSkill = skills.get(_skillRotationIndex);
		_skillRotationIndex = (_skillRotationIndex + 1) % skills.size();
		
		return selectedSkill;
	}

	private L2Skill selectMageSkillCascade(Player player, Monster target, double dist)
	{
		final List<L2Skill> attackSkills = _autoFarmProfile.getAttackSkills(false);
		if (attackSkills.isEmpty())
			return null;
		
		final List<L2Skill> available = new ArrayList<>();
		for (L2Skill skill : attackSkills)
		{
			if (skill == null)
				continue;
			
			if (!canUseSkillOnTarget(player, target, skill, dist))
				continue;
			
			if (!skill.checkCondition(player, target, false))
				continue;
			
			available.add(skill);
		}
		
		if (available.isEmpty())
			return null;
		
		final List<L2Skill> surrenderSkills = available.stream()
			.filter(this::isSurrenderSkill)
			.filter(skill -> target.getFirstEffect(skill) == null)
			.toList();
		
		if (!surrenderSkills.isEmpty())
			return chooseRotatedSkill(surrenderSkills);
		
		final List<L2Skill> vortexSkills = available.stream()
			.filter(this::isVortexSkill)
			.toList();
		
		if (!vortexSkills.isEmpty())
			return chooseRotatedSkill(vortexSkills);
		
		return chooseRotatedSkill(available);
	}

	/**
	 * Seleciona a skill ofensiva (não debuff) com maior alcance ignorando a distância atual.
	 * Se múltiplas tiverem mesmo alcance, prioriza a primeira não desabilitada e com item disponível.
	 */
	private L2Skill getFarthestAttackSkill(Player player)
	{
		L2Skill selected = null;
		int bestRange = -1;
		
		for (L2Skill s : _autoFarmProfile.getAttackSkills(false))
		{
			if (s == null || player.isSkillDisabled(s))
				continue;
			
			if (s.getItemConsumeId() != 0)
			{
				final ItemInstance consumable = player.getInventory().getItemByItemId(s.getItemConsumeId());
				if (consumable == null || consumable.getCount() < s.getItemConsume())
					continue;
			}
			
			final int range = s.getCastRange() > 0 ? s.getCastRange() : s.getSkillRadius();
			if (range > bestRange)
			{
				bestRange = range;
				selected = s;
			}
		}
		return selected;
	}
	
	private boolean trySweep(Player player)
	{
		L2Skill sweepSkill = player.getSkill(444);
		if (sweepSkill == null)
			sweepSkill = player.getSkill(42);
		if (sweepSkill == null)
			sweepSkill = player.getSkill(41);
		
		if (sweepSkill == null || player.getAI().getCurrentIntention().getType() == IntentionType.CAST)
			return false;
		
		final int range = Math.max(sweepSkill.getCastRange(), _autoFarmProfile.getAttackRange());
		
		Monster targetToSweep = player.getKnownTypeInRadius(Monster.class, range).stream()
			.filter(m -> m.isDead()
			&& m.getSpoilState().isSpoiled()
			&& m.getSpoilState().isActualSpoiler(player)
			&& MovementIntegration.canSeeTargetForAutoFarm(player, m))
			.min(Comparator.comparingDouble(m -> player.distance3D(m)))
			.orElse(null);
		
		if (targetToSweep != null)
		{
			player.setTarget(targetToSweep);
			setIntention(player, IntentionType.CAST, sweepSkill, targetToSweep);
			sendAdminMessage(player, "Sweeping " + targetToSweep.getName());
			return true;
		}
		
		return false;
	}
	
	private boolean tryPickUpItems(Player player)
	{
		if (player.getAI().getCurrentIntention().getType() == IntentionType.PICK_UP)
			return false;
		
		ItemInstance closestItem = player.getKnownTypeInRadius(ItemInstance.class, 750).stream()
			.filter(item -> !item.hasDropProtection())
			.filter(item -> MovementIntegration.canSeeTargetForAutoFarm(player, item))
			.min(Comparator.comparingDouble(item -> player.distance3D(item)))
			.orElse(null);
		
		if (closestItem != null)
		{
			setIntention(player, IntentionType.PICK_UP, closestItem);
			return true;
		}
		
		return false;
	}
	
	private synchronized void onEnd()
	{
		if (_autoFarmProfile.getMacro() == null)
			return;
		
		final Player player = _autoFarmProfile.getPlayer();
		if (player == null)
			return;
		
		if (player.isInCombat())
		{
			AttackStanceTaskManager.getInstance().remove(player);
			player.broadcastPacket(new AutoAttackStop(player.getObjectId()));
		}
		
		switch (_autoFarmProfile.getMacro())
		{
			case ESCAPE:
				setIntention(player, IntentionType.CAST, player.getSkill(2099), player);
				break;
			
			case LOGOUT:
				player.logout(true);
				break;
			
			case ITEM:
				final ItemInstance item = player.getInventory().getItemByItemId(_autoFarmProfile.getMacroAdditionalId());
				if (item == null)
				{
					player.sendMessage("Could not execute the macro. The item is not available.");
					break;
				}
				
				final IItemHandler handler = ItemHandler.getInstance().getHandler(item.getEtcItem());
				handler.useItem(player, item, false);
				break;
			
			case SKILL:
				final L2Skill skill = player.getSkill(_autoFarmProfile.getMacroAdditionalId());
				if (skill == null || player.isSkillDisabled(skill))
				{
					player.sendMessage("Could not execute the macro. The skill is not available.");
					break;
				}
				
				setIntention(player, IntentionType.CAST, skill, player);
		}
	}
	
	private L2Skill getCastSkill(Player player)
	{
		if (player.getTarget() == null || !(player.getTarget() instanceof Monster))
			return null;
		
		final Monster target = (Monster) player.getTarget();
		
		if (Rnd.get(100) <= Config.AUTOFARM_DEBUFF_CHANCE || !target.getAI().getAggroList().contains(player))
		{
			final L2Skill debuff = getAttackSkill(player, true);
			if (debuff != null)
				return debuff;
		}
		
		if (!isMageRole(player) && Rnd.nextBoolean())
			return null;
		
		return getAttackSkill(player, false);
	}
	
	/**
	 * Verifica se é possível usar uma skill no target.
	 * Verifica range, cooldown, itens, LoS e condições de cast.
	 * 
	 * @param player O jogador
	 * @param target O alvo
	 * @param skill A skill a verificar
	 * @param dist Distância atual até o target
	 * @return true se é possível usar a skill
	 */
	private boolean canUseSkillOnTarget(Player player, Monster target, L2Skill skill, double dist)
	{
		if (skill == null)
			return false;
		
		if (player.isSkillDisabled(skill))
			return false;
		
		final int skillRange = skill.getCastRange() > 0 ? skill.getCastRange() : skill.getSkillRadius();
		if (skillRange > 0)
		{
			final int totalSkillRange = skillRange + (int)(player.getCollisionRadius() + target.getCollisionRadius());
			if (dist > totalSkillRange)
				return false;
		}
		
		if (skill.getItemConsumeId() != 0)
		{
			final ItemInstance consumable = player.getInventory().getItemByItemId(skill.getItemConsumeId());
			if (consumable == null || consumable.getCount() < skill.getItemConsume())
				return false;
		}
		
		if (!player.getCast().canCast(target, skill, false, 0))
			return false;
		
		return true;
	}
	
	private boolean canUseSkillOnTarget(Player player, Player target, L2Skill skill, double dist, boolean useCtrl)
	{
		if (skill == null)
			return false;
		
		if (player.isSkillDisabled(skill))
			return false;
		
		final int skillRange = skill.getCastRange() > 0 ? skill.getCastRange() : skill.getSkillRadius();
		if (skillRange > 0)
		{
			final int totalSkillRange = skillRange + (int)(player.getCollisionRadius() + target.getCollisionRadius());
			if (dist > totalSkillRange)
				return false;
		}
		
		if (skill.getItemConsumeId() != 0)
		{
			final ItemInstance consumable = player.getInventory().getItemByItemId(skill.getItemConsumeId());
			if (consumable == null || consumable.getCount() < skill.getItemConsume())
				return false;
		}
		
		if (!player.getCast().canCast(target, skill, useCtrl, 0))
			return false;
		
		return true;
	}
	
	private L2Skill selectPvpSkill(Player player, Player target, double dist)
	{
		final boolean useCtrl = _autoFarmProfile.isOffensiveMode() || _autoFarmProfile.isDefensiveMode();
		final List<L2Skill> candidates = new ArrayList<>();
		final List<L2Skill> debuffs = _autoFarmProfile.getAttackSkills(true);
		final List<L2Skill> damage = _autoFarmProfile.getAttackSkills(false);
		final boolean unflaggedCtrlTarget = (_autoFarmProfile.isDefensiveMode() || _autoFarmProfile.isOffensiveMode())
			&& target instanceof Player targetPlayer
			&& targetPlayer.getPvpFlag() == 0 && targetPlayer.getKarma() == 0;
		
		if (!unflaggedCtrlTarget)
			addAvailablePvpSkills(player, target, dist, debuffs, true, useCtrl, candidates);
		
		for (L2Skill skill : damage)
		{
			if (skill == null)
				continue;
			
			if (unflaggedCtrlTarget && !skill.isDamage())
				continue;
			
			if (!canUseSkillOnTarget(player, target, skill, dist, useCtrl))
				continue;
			
			if (!skill.checkCondition(player, target, false))
				continue;
			
			candidates.add(skill);
		}
		
		return chooseBestPvpSkill(player, candidates);
	}
	
	private L2Skill findBestAvailableOffensiveSkillForPvp(Player player, Player target, double dist, List<L2Skill> skills, boolean debuff)
	{
		if (skills == null || skills.isEmpty())
			return null;
		
		final boolean useCtrl = _autoFarmProfile.isOffensiveMode() || _autoFarmProfile.isDefensiveMode();
		final List<L2Skill> available = new ArrayList<>();
		addAvailablePvpSkills(player, target, dist, skills, debuff, useCtrl, available);
		return chooseBestPvpSkill(player, available);
	}
	
	private boolean trySummonPvpAttack(Player player, Creature target, boolean useCtrl)
	{
		final Summon summon = player.getSummon();
		if (summon == null || summon.isDead() || target == null)
			return false;
		
		summon.setTarget(target);
		
		final double dist = summon.distance3D(target);
		final List<L2Skill> candidates = new ArrayList<>();
		final boolean unflaggedCtrlTarget = (_autoFarmProfile.isDefensiveMode() || _autoFarmProfile.isOffensiveMode())
			&& target instanceof Player targetPlayer
			&& targetPlayer.getPvpFlag() == 0 && targetPlayer.getKarma() == 0;
		for (L2Skill skill : summon.getSkills().values())
		{
			if (skill == null || skill.isPassive() || !skill.isSkillTypeOffensive())
				continue;
			
			if (unflaggedCtrlTarget && !skill.isDamage())
				continue;
			
			if (!canUseSkillOnTarget(summon, target, skill, dist, useCtrl))
				continue;
			
			if (!skill.checkCondition(summon, target, false))
				continue;
			
			candidates.add(skill);
		}
		
		final L2Skill bestSkill = chooseBestSkillByPriority(summon, candidates);
		if (bestSkill != null)
		{
			int skillRange = bestSkill.getCastRange() > 0 ? bestSkill.getCastRange() : bestSkill.getSkillRadius();
			if (skillRange <= 0)
				skillRange = summon.getStatus().getPhysicalAttackRange();
			
			final int totalSkillRange = skillRange + (int)(summon.getCollisionRadius() + target.getCollisionRadius());
			if (dist > totalSkillRange || !MovementIntegration.canSeeTarget(summon, target))
			{
				summon.getMove().maybeStartOffensiveFollow(target, skillRange);
				return true;
			}
			
			ensureCtrlAttackIntention(summon, target, useCtrl);
			summon.getAI().tryToCast(target, bestSkill, useCtrl, false, 0);
			return true;
		}
		
		summon.getAI().tryToAttack(target);
		return true;
	}
	
	private boolean canUseSkillOnTarget(Summon summon, Creature target, L2Skill skill, double dist, boolean useCtrl)
	{
		if (skill == null)
			return false;
		
		if (summon.isSkillDisabled(skill))
			return false;
		
		final int skillRange = skill.getCastRange() > 0 ? skill.getCastRange() : skill.getSkillRadius();
		if (skillRange > 0)
		{
			final int totalSkillRange = skillRange + (int)(summon.getCollisionRadius() + target.getCollisionRadius());
			if (dist > totalSkillRange)
				return false;
		}
		
		if (skill.getItemConsumeId() != 0)
		{
			final var inventory = summon.getInventory();
			if (inventory == null)
				return false;
			
			final ItemInstance consumable = inventory.getItemByItemId(skill.getItemConsumeId());
			if (consumable == null || consumable.getCount() < skill.getItemConsume())
				return false;
		}
		
		if (!summon.getCast().canCast(target, skill, useCtrl, 0))
			return false;
		
		return true;
	}
	
	private L2Skill chooseBestSkillByPriority(Creature caster, List<L2Skill> available)
	{
		return chooseBestSkillByPriority(caster, available, false);
	}
	
	private L2Skill chooseBestSkillByPriority(Creature caster, List<L2Skill> available, boolean preferPowerOverHitTime)
	{
		if (available == null || available.isEmpty())
			return null;
		
		if (preferPowerOverHitTime)
		{
			available.sort(
				Comparator
					.comparingInt(L2Skill::getReuseDelay)
					.thenComparing((a, b) -> Double.compare(b.getPower(caster), a.getPower(caster)))
					.thenComparingInt(L2Skill::getHitTime)
			);
		}
		else
		{
			available.sort(
				Comparator
					.comparingInt(L2Skill::getReuseDelay)
					.thenComparingInt(L2Skill::getHitTime)
					.thenComparing((a, b) -> Double.compare(b.getPower(caster), a.getPower(caster)))
			);
		}
		
		return available.get(0);
	}

	private void ensureCtrlAttackIntention(Player player, Player target, boolean useCtrl)
	{
		if (!useCtrl || player == null || target == null)
			return;
		
		if (player.getAI().getCurrentIntention().getFinalTarget() != target)
			player.getAI().tryToAttack(target, true, false);
	}

	private void ensureCtrlAttackIntention(Summon summon, Creature target, boolean useCtrl)
	{
		if (!useCtrl || summon == null || target == null)
			return;
		
		if (summon.getAI().getCurrentIntention().getFinalTarget() != target)
			summon.getAI().tryToAttack(target, true, false);
	}

	private boolean isMeleeDebug(Player player)
	{
		return Config.DEBUG_MELEE_ATTACK && player != null && player.getStatus().getPhysicalAttackRange() <= 80;
	}
	
	private void addAvailablePvpSkills(Player player, Player target, double dist, List<L2Skill> skills, boolean debuff, boolean useCtrl, List<L2Skill> out)
	{
		if (skills == null || skills.isEmpty())
			return;
		
		for (L2Skill skill : skills)
		{
			if (skill == null)
				continue;
			
			if (debuff && target.getFirstEffect(skill) != null)
				continue;
			
			if (!canUseSkillOnTarget(player, target, skill, dist, useCtrl))
				continue;
			
			if (!skill.checkCondition(player, target, false))
				continue;
			
			out.add(skill);
		}
	}
	
	private L2Skill chooseBestPvpSkill(Player player, List<L2Skill> available)
	{
		final boolean preferPowerOverHitTime = _autoFarmProfile.isDefensiveMode();
		return chooseBestSkillByPriority(player, available, preferPowerOverHitTime);
	}

	private void handleArcherCombatPvp(Player player, Player target, double dist, double dist2D, int bowRange, int totalBowRange)
	{
		final boolean useCtrl = _autoFarmProfile.isOffensiveMode() || _autoFarmProfile.isDefensiveMode();
		final boolean kiteEnabled = _autoFarmProfile.getSelectedArea().getType() != AutoFarmType.ZONA
			&& _autoFarmProfile.getSelectedArea().getType() != AutoFarmType.ROTA;
		
		if (dist <= totalBowRange)
		{
			player.getMove().stop();
			
			if (dist2D > 200 && !MovementIntegration.canSeeTargetForAutoFarm(player, target))
			{
				if (tryPathfindingAroundObstacle(player, target))
					return;
				
				final Location bypass = calculateBypassPosition(player, target);
				if (bypass != null)
				{
					player.getMove().maybePlayerMoveToLocation(bypass, 0, Config.SISTEMA_PATHFINDING, false);
					return;
				}
				
				moveToAttackPosition(player, target, bowRange);
				setIntention(player, IntentionType.ATTACK, target);
				return;
			}
			
			if (kiteEnabled)
			{
				final long now = System.currentTimeMillis();
				if (now - _lastArcherKiteTime < ARCHER_KITE_DELAY_MS)
				{
					if (tryArcherKiteRetreat(player, target, bowRange))
					{
					}
					return;
				}
			}
			
			final boolean trySkill = !_autoFarmProfile.getAttackSkills().isEmpty() && Rnd.get(100) < 30;
			if (trySkill)
			{
				final L2Skill skill = selectPvpSkill(player, target, dist);
				if (skill != null)
				{
					_lastArcherShotTime = System.currentTimeMillis();
					player.updatePvPStatus(target);
					setIntention(player, IntentionType.CAST, skill, target);
					ensureCtrlAttackIntention(player, target, useCtrl);
					final int skillRange = skill.getCastRange() > 0 ? skill.getCastRange() : skill.getSkillRadius();
					final int totalSkillRange = skillRange + (int)(player.getCollisionRadius() + target.getCollisionRadius());
					if (dist2D > totalSkillRange)
					{
						player.getMove().maybeStartOffensiveFollow(target, skillRange);
					}
					else
					{
						player.getAI().tryToCast(target, skill, useCtrl, false, 0);
					}
					return;
				}
			}
			
			_lastArcherShotTime = System.currentTimeMillis();
			player.updatePvPStatus(target);
			setIntention(player, IntentionType.ATTACK, target);
			
			if (kiteEnabled)
			{
				tryArcherKiteStep(player, target, bowRange);
			}
			return;
		}
		
		moveToAttackPosition(player, target, bowRange);
		setIntention(player, IntentionType.ATTACK, target);
	}

	private void handleMageCombatPvp(Player player, Player target, double dist)
	{
		final boolean useCtrl = _autoFarmProfile.isOffensiveMode() || _autoFarmProfile.isDefensiveMode();
		L2Skill skill = selectPvpSkill(player, target, dist);
		if (skill == null)
		{
			final L2Skill farSkill = getFarthestAttackSkill(player);
			final int followRange = farSkill != null
				? (farSkill.getCastRange() > 0 ? farSkill.getCastRange() : farSkill.getSkillRadius())
				: 300;
			
			moveToAttackPosition(player, target, followRange);
			if (farSkill != null)
				setIntention(player, IntentionType.CAST, farSkill, target);
			return;
		}
		
		if (maybeApproachForPvpSkill(player, target, skill, dist))
			return;
		
		player.getMove().stop();
		if (MovementIntegration.canSeeTargetForAutoFarm(player, target))
		{
			player.updatePvPStatus(target);
			setIntention(player, IntentionType.CAST, skill, target);
			ensureCtrlAttackIntention(player, target, useCtrl);
			player.getAI().tryToCast(target, skill, useCtrl, false, 0);
			return;
		}
		
		if (tryPathfindingAroundObstacle(player, target))
			return;
		
		final Location bypass = calculateBypassPosition(player, target);
		if (bypass != null)
		{
			player.getMove().maybePlayerMoveToLocation(bypass, 0, Config.SISTEMA_PATHFINDING, false);
			return;
		}
	}

	private boolean maybeApproachForPvpSkill(Player player, Player target, L2Skill skill, double dist)
	{
		int skillRange = skill.getCastRange() > 0 ? skill.getCastRange() : skill.getSkillRadius();
		if (skillRange <= 0)
			skillRange = 300;
		
		final int safeRange = skillRange + (int)(player.getCollisionRadius() + target.getCollisionRadius()) + 50;
		final double dist2D = player.distance2D(target);
		
		if (dist > safeRange || dist2D > safeRange)
		{
			setIntention(player, IntentionType.CAST, skill, target);
			moveToAttackPosition(player, target, skillRange);
			setIntention(player, IntentionType.CAST, skill, target);
			return true;
		}
		
		if (!MovementIntegration.canSeeTargetForAutoFarm(player, target))
		{
			setIntention(player, IntentionType.CAST, skill, target);
			if (tryPathfindingAroundObstacle(player, target))
				return true;
			
			final Location bypass = calculateBypassPosition(player, target);
			if (bypass != null)
			{
				player.getMove().maybePlayerMoveToLocation(bypass, 0, Config.SISTEMA_PATHFINDING, false);
				return true;
			}
			
			moveToAttackPosition(player, target, skillRange);
			setIntention(player, IntentionType.CAST, skill, target);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Busca a melhor skill ofensiva disponível que pode ser usada no target.
	 * Verifica TODAS as skills ofensivas e retorna a primeira que pode ser usada,
	 * respeitando condições (cooldown, range, MP, itens, checkCondition).
	 * Usa rotação para intercalar entre skills disponíveis.
	 * 
	 * @param player O jogador (melee)
	 * @param target O alvo
	 * @param dist Distância até o target
	 * @return Melhor skill ofensiva disponível ou null
	 */
	private L2Skill findBestAvailableOffensiveSkill(Player player, Monster target, double dist)
	{
		final List<L2Skill> attackSkills = _autoFarmProfile.getAttackSkills(false);
		if (attackSkills.isEmpty())
			return null;
		
		final List<L2Skill> availableSkills = new ArrayList<>();
		for (L2Skill skill : attackSkills)
		{
			if (skill == null)
				continue;
			
			if (canUseSkillOnTarget(player, target, skill, dist))
			{
				if (skill.checkCondition(player, target, false))
				{
					availableSkills.add(skill);
				}
			}
		}
		
		if (availableSkills.isEmpty())
			return null;
		
		if (_skillRotationIndex >= availableSkills.size())
			_skillRotationIndex = 0;
		
		final L2Skill selectedSkill = availableSkills.get(_skillRotationIndex);
		
		_skillRotationIndex = (_skillRotationIndex + 1) % availableSkills.size();
		
		return selectedSkill;
	}
	
	/**
	 * Obtém skill com rotação para fighter.
	 * Intercala entre skills disponíveis para não usar apenas uma skill.
	 * MELHORADO: Prioriza skills ofensivas, respeita range de arma e skills.
	 * 
	 * @param player O jogador (fighter)
	 * @param target O alvo
	 * @param dist Distância até o target
	 * @return Skill disponível com rotação ou null
	 */
	private L2Skill getRotatedSkillForFighter(Player player, Monster target, double dist)
	{
		final List<L2Skill> attackSkills = _autoFarmProfile.getAttackSkills(false);
		if (attackSkills.isEmpty())
			return null;
		
		if (Rnd.get(100) <= Config.AUTOFARM_DEBUFF_CHANCE || !target.getAI().getAggroList().contains(player))
		{
			final List<L2Skill> debuffSkills = _autoFarmProfile.getAttackSkills(true);
			if (!debuffSkills.isEmpty())
			{
				final L2Skill debuff = getRotatedSkillFromList(player, target, dist, debuffSkills, true);
				if (debuff != null)
					return debuff;
			}
		}
		
		
		return getRotatedSkillFromList(player, target, dist, attackSkills, false);
	}
	
	/**
	 * Obtém próxima skill com rotação para fighter (quando a primeira não pode ser usada).
	 * 
	 * @param player O jogador (fighter)
	 * @param target O alvo
	 * @param dist Distância até o target
	 * @param currentSkill Skill atual que não pode ser usada
	 * @return Próxima skill disponível ou null
	 */
	private L2Skill getNextRotatedSkillForFighter(Player player, Monster target, double dist, L2Skill currentSkill)
	{
		final List<L2Skill> attackSkills = _autoFarmProfile.getAttackSkills(false);
		if (attackSkills.isEmpty())
			return null;
		
		return getNextRotatedSkillFromList(player, target, dist, attackSkills, currentSkill, false);
	}
	
	/**
	 * Obtém skill com rotação para mago.
	 * MELHORADO: Prioriza skills ofensivas mágicas, intercala entre skills disponíveis.
	 * 
	 * @param player O jogador (mago)
	 * @param target O alvo
	 * @param dist Distância até o target
	 * @return Skill disponível com rotação ou null
	 */
	private L2Skill getRotatedSkillForMage(Player player, Monster target, double dist)
	{
		final List<L2Skill> attackSkills = _autoFarmProfile.getAttackSkills(false);
		if (attackSkills.isEmpty())
			return null;
		
		return selectBestMageSkill(player, target, attackSkills, dist);
	}
	
	/**
	 * Seleciona a melhor skill mágica ofensiva disponível.
	 * Prioriza skills com power > 0 (ofensivas) sobre debuffs (power == 0).
	 * Intercala skills do profile respeitando cooldowns.
	 * 
	 * @param player O jogador (mago)
	 * @param target O alvo
	 * @param skills Lista de skills disponíveis
	 * @param dist Distância até o target
	 * @return Melhor skill disponível ou null
	 */
	private L2Skill selectBestMageSkill(Player player, Monster target, List<L2Skill> skills, double dist)
	{
		if (skills == null || skills.isEmpty())
			return null;
		
		final List<L2Skill> candidates = new ArrayList<>();
		for (L2Skill skill : skills)
		{
			if (skill == null || player.isSkillDisabled(skill))
				continue;
			
			if (player.getStatus().getMp() < skill.getMpConsume())
				continue;
			
			final int skillRange = skill.getCastRange() > 0 ? skill.getCastRange() : skill.getSkillRadius();
			if (skillRange > 0 && dist > skillRange + 100)
				continue;
			
			if (skill.getItemConsumeId() != 0)
			{
				final ItemInstance consumable = player.getInventory().getItemByItemId(skill.getItemConsumeId());
				if (consumable == null || consumable.getCount() < skill.getItemConsume())
					continue;
			}
			
			if (skill.getPower() == 0.0)
			{
				if (target.getFirstEffect(skill) != null)
					continue;
			}
			
			candidates.add(skill);
		}
		
		if (candidates.isEmpty())
			return null;
		
		candidates.sort((s1, s2) -> {
			final boolean s1IsDebuff = s1.getPower() == 0.0;
			final boolean s2IsDebuff = s2.getPower() == 0.0;
			
			if (s1IsDebuff && !s2IsDebuff)
				return 1;
			if (!s1IsDebuff && s2IsDebuff)
				return -1;
			
			final double p1 = s1.getPower();
			final double p2 = s2.getPower();
			if (p1 != p2)
				return Double.compare(p2, p1);
			
			return Integer.compare(s1.getReuseDelay(), s2.getReuseDelay());
		});
		
		int attempts = 0;
		while (attempts < candidates.size())
		{
			if (_skillRotationIndex >= candidates.size())
				_skillRotationIndex = 0;
			
			final L2Skill selectedSkill = candidates.get(_skillRotationIndex);
			
			if (!player.isSkillDisabled(selectedSkill))
			{
				_skillRotationIndex = (_skillRotationIndex + 1) % candidates.size();
				return selectedSkill;
			}
			
			_skillRotationIndex = (_skillRotationIndex + 1) % candidates.size();
			attempts++;
		}
		
		return candidates.get(0);
	}
	
	/**
	 * Obtém skill com rotação para mago, pulando skills em cooldown.
	 * Apenas usa próxima skill se a anterior estiver em cooldown.
	 * 
	 * @param player O jogador
	 * @param target O alvo
	 * @param dist Distância até o target
	 * @param skills Lista de skills
	 * @return Skill disponível com rotação ou null
	 */
	private L2Skill getRotatedSkillFromListForMage(Player player, Monster target, double dist, List<L2Skill> skills)
	{
		if (skills == null || skills.isEmpty())
			return null;
		
		final List<L2Skill> availableSkills = new ArrayList<>();
		for (L2Skill skill : skills)
		{
			if (skill == null || player.isSkillDisabled(skill))
				continue;
			
			final int skillRange = skill.getCastRange() > 0 ? skill.getCastRange() : skill.getSkillRadius();
			if (skillRange > 0 && dist > skillRange + 100)
				continue;
			
			if (skill.getItemConsumeId() != 0)
			{
				final ItemInstance consumable = player.getInventory().getItemByItemId(skill.getItemConsumeId());
				if (consumable == null || consumable.getCount() < skill.getItemConsume())
					continue;
			}
			
			availableSkills.add(skill);
		}
		
		if (availableSkills.isEmpty())
			return null;
		
		int attempts = 0;
		while (attempts < availableSkills.size())
		{
			if (_skillRotationIndex >= availableSkills.size())
				_skillRotationIndex = 0;
			
			final L2Skill selectedSkill = availableSkills.get(_skillRotationIndex);
			
			if (!player.isSkillDisabled(selectedSkill))
			{
				_skillRotationIndex = (_skillRotationIndex + 1) % availableSkills.size();
				return selectedSkill;
			}
			
			_skillRotationIndex = (_skillRotationIndex + 1) % availableSkills.size();
			attempts++;
		}
		
		return null;
	}
	
	/**
	 * Obtém próxima skill com rotação para mago (quando a primeira não pode ser usada).
	 * 
	 * @param player O jogador (mago)
	 * @param target O alvo
	 * @param dist Distância até o target
	 * @param currentSkill Skill atual que não pode ser usada
	 * @return Próxima skill disponível ou null
	 */
	private L2Skill getNextRotatedSkillForMage(Player player, Monster target, double dist, L2Skill currentSkill)
	{
		final List<L2Skill> attackSkills = _autoFarmProfile.getAttackSkills(false);
		if (attackSkills.isEmpty())
			return null;
		
		return getNextRotatedSkillFromList(player, target, dist, attackSkills, currentSkill, false);
	}
	
	/**
	 * Obtém skill com rotação de uma lista de skills.
	 * Intercala entre skills disponíveis usando índice de rotação.
	 * 
	 * @param player O jogador
	 * @param target O alvo
	 * @param dist Distância até o target
	 * @param skills Lista de skills
	 * @param isDebuff Se são skills de debuff
	 * @return Skill disponível com rotação ou null
	 */
	private L2Skill getRotatedSkillFromList(Player player, Monster target, double dist, List<L2Skill> skills, boolean isDebuff)
	{
		if (skills == null || skills.isEmpty())
			return null;
		
		final List<L2Skill> availableSkills = new ArrayList<>();
		for (L2Skill skill : skills)
		{
			if (skill == null || player.isSkillDisabled(skill))
				continue;
			
			final int skillRange = skill.getCastRange() > 0 ? skill.getCastRange() : skill.getSkillRadius();
			if (skillRange > 0 && dist > skillRange + 100)
				continue;
			
			if (skill.getItemConsumeId() != 0)
			{
				final ItemInstance consumable = player.getInventory().getItemByItemId(skill.getItemConsumeId());
				if (consumable == null || consumable.getCount() < skill.getItemConsume())
					continue;
			}
			
			if (isDebuff && target.getFirstEffect(skill) != null)
				continue;
			
			availableSkills.add(skill);
		}
		
		if (availableSkills.isEmpty())
			return null;
		
		if (_skillRotationIndex >= availableSkills.size())
			_skillRotationIndex = 0;
		
		final L2Skill selectedSkill = availableSkills.get(_skillRotationIndex);
		
		_skillRotationIndex = (_skillRotationIndex + 1) % availableSkills.size();
		
		return selectedSkill;
	}
	
	/**
	 * Obtém próxima skill com rotação de uma lista (pula a skill atual).
	 * 
	 * @param player O jogador
	 * @param target O alvo
	 * @param dist Distância até o target
	 * @param skills Lista de skills
	 * @param currentSkill Skill atual a pular
	 * @param isDebuff Se são skills de debuff
	 * @return Próxima skill disponível ou null
	 */
	private L2Skill getNextRotatedSkillFromList(Player player, Monster target, double dist, List<L2Skill> skills, L2Skill currentSkill, boolean isDebuff)
	{
		if (skills == null || skills.isEmpty())
			return null;
		
		final List<L2Skill> availableSkills = new ArrayList<>();
		for (L2Skill skill : skills)
		{
			if (skill == null || skill == currentSkill || player.isSkillDisabled(skill))
				continue;
			
			final int skillRange = skill.getCastRange() > 0 ? skill.getCastRange() : skill.getSkillRadius();
			if (skillRange > 0 && dist > skillRange + 100)
				continue;
			
			if (skill.getItemConsumeId() != 0)
			{
				final ItemInstance consumable = player.getInventory().getItemByItemId(skill.getItemConsumeId());
				if (consumable == null || consumable.getCount() < skill.getItemConsume())
					continue;
			}
			
			if (isDebuff && target.getFirstEffect(skill) != null)
				continue;
			
			availableSkills.add(skill);
		}
		
		if (availableSkills.isEmpty())
			return null;
		
		if (_skillRotationIndex >= availableSkills.size())
			_skillRotationIndex = 0;
		
		final L2Skill selectedSkill = availableSkills.get(_skillRotationIndex);
		_skillRotationIndex = (_skillRotationIndex + 1) % availableSkills.size();
		
		return selectedSkill;
	}
	
	/**
	 * Obtém a melhor skill disponível para mago, priorizando skills de ataque.
	 * Verifica cooldown, range e itens necessários.
	 * Sistema L2OFF like: mago sempre prioriza skills.
	 * 
	 * @param player O jogador (mago)
	 * @param target O alvo
	 * @return Melhor skill disponível ou null se todas estão em cooldown
	 */
	private L2Skill getBestAvailableSkillForMage(Player player, Monster target)
	{
		if (player.getTarget() == null || !(player.getTarget() instanceof Monster))
			return null;
		
		final double dist = player.distance3D(target);
		final List<L2Skill> attackSkills = _autoFarmProfile.getAttackSkills(false);
		
		if (attackSkills.isEmpty())
			return null;
		
		L2Skill bestSkill = null;
		int bestRange = -1;
		
		for (L2Skill skill : attackSkills)
		{
			if (skill == null || player.isSkillDisabled(skill))
				continue;
			
			final int skillRange = skill.getCastRange() > 0 ? skill.getCastRange() : skill.getSkillRadius();
			if (skillRange <= 0)
				continue;
			
			if (dist > skillRange + 100)
				continue;
			
			if (skill.getItemConsumeId() != 0)
			{
				final ItemInstance consumable = player.getInventory().getItemByItemId(skill.getItemConsumeId());
				if (consumable == null || consumable.getCount() < skill.getItemConsume())
					continue;
			}
			
			if (skillRange > bestRange)
			{
				bestRange = skillRange;
				bestSkill = skill;
			}
		}
		
		if (bestSkill == null)
		{
			final List<L2Skill> debuffSkills = _autoFarmProfile.getAttackSkills(true);
			for (L2Skill skill : debuffSkills)
			{
				if (skill == null || player.isSkillDisabled(skill))
					continue;
				
				final int skillRange = skill.getCastRange() > 0 ? skill.getCastRange() : skill.getSkillRadius();
				if (skillRange <= 0 || dist > skillRange + 100)
					continue;
				
				if (target.getFirstEffect(skill) != null)
					continue;
				
				if (skill.getItemConsumeId() != 0)
				{
					final ItemInstance consumable = player.getInventory().getItemByItemId(skill.getItemConsumeId());
					if (consumable == null || consumable.getCount() < skill.getItemConsume())
						continue;
				}
				
				if (skillRange > bestRange)
				{
					bestRange = skillRange;
					bestSkill = skill;
				}
			}
		}
		
		return bestSkill;
	}
	
	/**
	 * Obtém skill para cast verificando cooldown.
	 * Se todas as skills estão em cooldown, retorna null para atacar normalmente (não deixa ocioso).
	 * Sistema L2OFF like: não deixa o player ocioso esperando cooldown.
	 * 
	 * @param player O jogador
	 * @return Skill disponível ou null se todas estão em cooldown
	 */
	private L2Skill getCastSkillWithCooldownCheck(Player player)
	{
		if (player.getTarget() == null || !(player.getTarget() instanceof Monster))
			return null;
		
		final Monster target = (Monster) player.getTarget();
		
		if (Rnd.get(100) <= Config.AUTOFARM_DEBUFF_CHANCE || !target.getAI().getAggroList().contains(player))
		{
			final L2Skill debuff = getAttackSkill(player, true);
			if (debuff != null && !player.isSkillDisabled(debuff))
				return debuff;
		}
		
		if (!isMageRole(player) && Rnd.nextBoolean())
			return null;
		
		final L2Skill skill = getAttackSkill(player, false);
		
		if (skill != null && !player.isSkillDisabled(skill))
			return skill;
		
		final List<L2Skill> allSkills = _autoFarmProfile.getAttackSkills(false);
		boolean hasAvailableSkill = false;
		
		for (L2Skill s : allSkills)
		{
			if (s != null && !player.isSkillDisabled(s))
			{
				if (player.distance3D(target) <= (s.getCastRange() > 0 ? s.getCastRange() : s.getSkillRadius()))
				{
					if (s.getItemConsumeId() != 0)
					{
						final ItemInstance consumable = player.getInventory().getItemByItemId(s.getItemConsumeId());
						if (consumable == null || consumable.getCount() < s.getItemConsume())
							continue;
					}
					
					hasAvailableSkill = true;
					break;
				}
			}
		}
		
		return hasAvailableSkill ? skill : null;
	}
	
	private L2Skill getAttackSkill(Player player, boolean debuff)
	{
		final List<L2Skill> skills = _autoFarmProfile.getAttackSkills(debuff);
		if (skills.isEmpty())
			return null;
		
		Collections.shuffle(skills);
		
		for (L2Skill skill : skills)
		{
			if (player.isSkillDisabled(skill))
				continue;
			
			if (player.distance3D(player.getTarget()) > (skill.getCastRange() > 0 ? skill.getCastRange() : skill.getSkillRadius()))
				continue;
			
			if (skill.getId() == 409 && player.getFirstEffect(skill) != null)
				continue;
			
			if (skill.getItemConsumeId() != 0)
			{
				final ItemInstance consumable = player.getInventory().getItemByItemId(skill.getItemConsumeId());
				_noAttackSkillItems = consumable == null || consumable.getCount() < skill.getItemConsume();
				
				if (_noAttackSkillItems)
					continue;
			}
			
			if (!debuff)
				return skill;
			
			if (player.getTarget() instanceof Monster && ((Monster) player.getTarget()).getFirstEffect(skill) == null)
				return skill;
		}
		
		return null;
	}
	
	private boolean testHealSkill(Player player)
	{
		if (player.getStatus().getHpRatio() < Config.AUTOFARM_HP_HEAL_RATE)
		{
			for (L2Skill skill : _autoFarmProfile.getHpHealSkills())
			{
				if (!player.isSkillDisabled(skill))
				{
					setIntention(player, IntentionType.CAST, skill, player);
					return true;
				}
			}
		}
		
		if (player.getStatus().getMpRatio() < Config.AUTOFARM_MP_HEAL_RATE)
		{
			for (L2Skill skill : _autoFarmProfile.getMpHealSkills())
			{
				if (!player.isSkillDisabled(skill))
				{
					setIntention(player, IntentionType.CAST, skill, player);
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean testBuffSkill(Player player)
	{
		if (player.getCast().isCastingNow())
			return false;
		
		for (L2Skill skill : _autoFarmProfile.getBuffSkills())
		{
			if ((skill.hasEffects() && player.getFirstEffect(skill) != null) || player.isSkillDisabled(skill))
				continue;
			
			if (skill.getItemConsumeId() != 0)
			{
				final ItemInstance consumable = player.getInventory().getItemByItemId(skill.getItemConsumeId());
				if (consumable == null || consumable.getCount() < skill.getItemConsume())
					continue;
			}
			
			if (skill.getSkillType() == SkillType.CREATE_ITEM || skill.getSkillType() == SkillType.EXTRACTABLE)
			{
				if (testCreateItemSkill(player, skill))
					return true;
				
				continue;
			}
			else if (skill.getSkillType() == SkillType.NEGATE)
			{
				if (testNegateSkill(player, skill))
					return true;
				
				continue;
			}
			
			setIntention(player, IntentionType.CAST, skill, player);
			return true;
		}
		
		return false;
	}
	
	private static boolean testNegateSkill(Player player, L2Skill skill)
	{
		boolean doCast = false;
		for (AbstractEffect effect : player.getAllEffects())
		{
			if (ArraysUtil.contains(skill.getNegateId(), effect.getSkill().getId()))
				doCast = true;
			else
			{
				for (SkillType skillType : skill.getNegateStats())
				{
					final L2Skill effectSkill = effect.getSkill();
					if (effectSkill.getEffectType() != null && effectSkill.getEffectAbnormalLvl() >= 0)
					{
						if (effectSkill.getEffectType() == skillType && effectSkill.getEffectAbnormalLvl() <= skill.getNegateLvl())
							doCast = true;
					}
					else if (effectSkill.getSkillType() == skillType && effectSkill.getAbnormalLvl() <= skill.getNegateLvl())
						doCast = true;
				}
			}
		}
		
		if (doCast)
			player.getAI().tryToCast(player, skill);
		
		return doCast;
	}
	
	private static boolean testCreateItemSkill(Player player, L2Skill skill)
	{
		boolean doCast = false;
		if (skill.getSkillType() == SkillType.CREATE_ITEM)
		{
			if (skill.getId() == 1324)
				doCast = true;
			else
			{
				final L2SkillCreateItem createSkill = (L2SkillCreateItem) skill;
				final int createItemCount = createSkill._createItemCount;
				
				for (int createItemId : createSkill._createItemId)
				{
					if (player.getInventory().getItemCount(createItemId) < createItemCount)
						doCast = true;
				}
			}
		}
		else if (skill.getSkillType() == SkillType.EXTRACTABLE)
		{
			final ExtractableProductItem extractable = skill.getExtractableSkill().getProductItems().iterator().next();
			if (player.getInventory().getItemCount(extractable.getItems().iterator().next().getId()) < 200)
				doCast = true;
		}
		
		if (doCast)
			player.getAI().tryToCast(player, skill);
		
		return doCast;
	}
	
	private static void testHpPotions(Player player)
	{
		if (player.getStatus().getHpRatio() > Config.AUTOFARM_HP_HEAL_RATE)
			return;
		
		testAutoPotions(player, Config.AUTOFARM_HP_POTIONS);
	}
	
	private static void testMpPotions(Player player)
	{
		if (player.getStatus().getMpRatio() > Config.AUTOFARM_MP_HEAL_RATE)
			return;
		
		testAutoPotions(player, Config.AUTOFARM_MP_POTIONS);
	}
	
	private static void testAutoPotions(Player player, int[] ids)
	{
		for (int i : ids)
		{
			final ItemInstance potion = player.getInventory().getItemByItemId(i);
			if (potion == null)
				continue;
			
			if (player.isItemDisabled(potion))
				continue;
			
			boolean useItem = true;
			for (IntIntHolder holder : potion.getEtcItem().getSkills())
			{
				if (player.isSkillDisabled(holder.getSkill()))
				{
					useItem = false;
					break;
				}
				
				if (holder.getSkill().hasEffects())
				{
					for (AbstractEffect ae : player.getAllEffects())
					{
						for (EffectTemplate effect : holder.getSkill().getEffectTemplates())
						{
							if (ae.getTemplate().getStackType().equals(effect.getStackType()))
							{
								useItem = false;
								break;
							}
						}
					}
				}
			}
			
			if (!useItem)
				continue;
			
			final IItemHandler handler = ItemHandler.getInstance().getHandler(potion.getEtcItem());
			handler.useItem(player, potion, false);
			break;
		}
	}
	
	private static boolean isPlayerAttacking(Player player)
	{
		return player.getAttack().isAttackingNow() || player.getCast().isCastingNow();
	}
	
	private static void sendAdminMessage(Player player, String msg)
	{
		if (!player.isGM())
			return;
		
		player.sendMessage("AutoFarmLog: " + msg);
	}
	
	private static void sendMessage(Player player, String msg)
	{
		if (!Config.AUTOFARM_SEND_LOG_MESSAGES)
			return;
		
		player.sendMessage(String.format("AutoFarm (%s): %s", new SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis()), msg));
	}
	
	private void trySendMessage(Player player, String msg)
	{
		if (!Config.AUTOFARM_SEND_LOG_MESSAGES)
			return;
		
		final int count = _cachedMessages.merge(msg, 1, Integer::sum);
		if (count >= 30)
		{
			_cachedMessages.remove(msg);
			return;
		}
		
		if (count == 1 || count >= 30)
			player.sendMessage(String.format("AutoFarm (%s): %s", new SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis()), msg));
	}
	
	/**
	 * Verifica se o alvo está travado (sem causar dano por muito tempo)
	 */
	private boolean checkTargetStuck(Player player, Monster target)
	{
		if (_lockedTarget != target)
		{
			_lockedTarget = target;
			_targetLastHitTime = System.currentTimeMillis();
			return false;
		}
		
		if (target.getStatus().getHp() < target.getStatus().getMaxHp() * 0.99)
		{
			_targetLastHitTime = System.currentTimeMillis();
		}
		
		final long timeSinceLastHit = System.currentTimeMillis() - _targetLastHitTime;
		if (timeSinceLastHit > TARGET_STUCK_TIMEOUT_MS)
		{
			sendAdminMessage(player, "Target Stuck detected! (" + target + ")");
			return true;
		}
		return false;
	}
	
	private void resetStuckLogic()
	{
		_lockedTarget = null;
		_targetLastHitTime = 0;
	}
	
	private void markAsUnreachable(int objectId)
	{
		_unreachableTargets.put(objectId, System.currentTimeMillis() + UNREACHABLE_BAN_TIME_MS);
	}
	
	private void cleanupUnreachableTargets()
	{
		final long now = System.currentTimeMillis();
		_unreachableTargets.entrySet().removeIf(entry -> entry.getValue() < now);
	}
	
	private void onRouteNodeAdvanced()
	{
		_routeNodeStep++;
		cleanupRouteTargetBans();
	}
	
	private void banRouteTarget(int objectId)
	{
		_routeTargetBans.put(objectId, _routeNodeStep + 2);
	}
	
	private boolean isRouteTargetBanned(int objectId)
	{
		final Integer untilStep = _routeTargetBans.get(objectId);
		if (untilStep == null)
			return false;
		
		if (_routeNodeStep >= untilStep)
		{
			_routeTargetBans.remove(objectId);
			return false;
		}
		
		return true;
	}
	
	private void cleanupRouteTargetBans()
	{
		_routeTargetBans.entrySet().removeIf(entry -> _routeNodeStep >= entry.getValue());
	}
	
	private boolean isTargetNearRouteNodes(Monster target, List<Location> nodes)
	{
		if (nodes == null || nodes.isEmpty())
			return true;
		
		double minDist = Double.MAX_VALUE;
		for (Location node : nodes)
		{
			final double dist = target.distance2D(node);
			if (dist < minDist)
				minDist = dist;
			
			if (minDist <= ROUTE_TARGET_MAX_NODE_DISTANCE)
				return true;
		}
		
		return false;
	}
	
	/**
	 * Calcula caminho usando pathfinding legado (GeoEngine) exclusivo para autofarm.
	 * Este método contorna obstáculos de forma inteligente sem afetar o movimento normal do player.
	 * Sistema L2OFF like: verifica geodata rigorosamente e evita bordas de obstáculos.
	 * 
	 * @param player O jogador
	 * @param target O alvo (Monster)
	 * @return Location do próximo ponto do caminho, ou null se não houver caminho válido
	 */
	private Location calculatePathfindingApproach(Player player, Creature target)
	{
		final long now = System.currentTimeMillis();
		final Location targetPos = target.getPosition();
		
		boolean needsRecalculation = false;
		if (_lastPathTarget == null || !_lastPathTarget.equals(targetPos))
		{
			needsRecalculation = true;
			_lastPathTarget = targetPos.clone();
		}
		else if (now - _lastPathCalculation > PATHFINDING_COOLDOWN_MS)
		{
			needsRecalculation = true;
		}
		
		if (!needsRecalculation && !_currentPath.isEmpty())
		{
			final Location nextPoint = _currentPath.get(0);
			final double distToNext = player.distance3D(nextPoint);
			
			if (distToNext < 100)
			{
				_currentPath.remove(0);
				if (!_currentPath.isEmpty())
				{
					return smoothPathPoint(player, _currentPath.get(0));
				}
				else
				{
					if (MovementIntegration.canSeeTargetForAutoFarm(player, target))
					{
						_currentPath.clear();
						_lastPathTarget = null;
						return null;
					}
				}
			}
			else
			{
				if (MovementIntegration.canSeeTargetForAutoFarm(player, target))
				{
					_currentPath.clear();
					_lastPathTarget = null;
					return null;
				}
				return smoothPathPoint(player, nextPoint);
			}
		}
		
		final double distance = player.distance3D(target);
		if (distance > MAX_PATH_DISTANCE)
		{
			sendAdminMessage(player, "Target too far for pathfinding: " + (int)distance);
			return null;
		}
		
		final Location playerPos = player.getPosition();
		
		if (MovementIntegration.canMoveToTargetForAutoFarm(
			playerPos.getX(), playerPos.getY(), playerPos.getZ(),
			targetPos.getX(), targetPos.getY(), targetPos.getZ()))
		{
			if (MovementIntegration.canSeeTargetForAutoFarm(player, target))
			{
				_currentPath.clear();
				_lastPathTarget = null;
				return null;
			}
		}
		
		final List<Location> path = GeoEngine.getInstance().findPath(
			playerPos.getX(), playerPos.getY(), playerPos.getZ(),
			targetPos.getX(), targetPos.getY(), targetPos.getZ(),
			true,
			null
		);
		
		if (path == null || path.isEmpty())
		{
			sendAdminMessage(player, "No path found to target");
			_currentPath.clear();
			_lastPathTarget = null;
			return null;
		}
		
		_currentPath = new ArrayList<>(path);
		if (!_currentPath.isEmpty())
		{
			final Location firstPoint = _currentPath.get(0);
			if (player.distance3D(firstPoint) < 50)
			{
				_currentPath.remove(0);
			}
		}
		
		_currentPath = smoothPath(_currentPath, playerPos);
		
		_lastPathCalculation = now;
		
		if (_currentPath.isEmpty())
		{
			return calculateBypassPosition(player, target);
		}
		
		sendAdminMessage(player, "Pathfinding calculated: " + _currentPath.size() + " waypoints (smoothed)");
		return _currentPath.get(0);
	}
	
	/**
	 * Suaviza um ponto do caminho para evitar bordas de obstáculos.
	 * Sistema L2OFF like: mantém distância segura de obstáculos.
	 * 
	 * @param player O jogador
	 * @param point O ponto a suavizar
	 * @return Ponto suavizado ou o original se não precisar ajuste
	 */
	private Location smoothPathPoint(Player player, Location point)
	{
		final int checkRadius = 50;
		
		if (isNearObstacle(point.getX(), point.getY(), point.getZ()))
		{
			final int midX = (player.getX() + point.getX()) / 2;
			final int midY = (player.getY() + point.getY()) / 2;
			final int midZ = point.getZ();
			if (MovementIntegration.canMoveToTargetForAutoFarm(
				player.getX(), player.getY(), player.getZ(),
				midX, midY, midZ) && !isNearObstacle(midX, midY, midZ))
			{
				return new Location(midX, midY, midZ);
			}
			
			final int dx = point.getX() - player.getX();
			final int dy = point.getY() - player.getY();
			if (dx != 0 || dy != 0)
			{
				final double len = Math.sqrt((double) dx * dx + (double) dy * dy);
				final int offset = 50;
				final int lx = (int) Math.round(point.getX() - (dy / len) * offset);
				final int ly = (int) Math.round(point.getY() + (dx / len) * offset);
				final int rx = (int) Math.round(point.getX() + (dy / len) * offset);
				final int ry = (int) Math.round(point.getY() - (dx / len) * offset);
				
				if (MovementIntegration.canMoveToTargetForAutoFarm(
					player.getX(), player.getY(), player.getZ(),
					lx, ly, point.getZ()) && !isNearObstacle(lx, ly, point.getZ()))
				{
					return new Location(lx, ly, point.getZ());
				}
				if (MovementIntegration.canMoveToTargetForAutoFarm(
					player.getX(), player.getY(), player.getZ(),
					rx, ry, point.getZ()) && !isNearObstacle(rx, ry, point.getZ()))
				{
					return new Location(rx, ry, point.getZ());
				}
			}
		}
		
		for (int angle = 0; angle < 360; angle += 45)
		{
			final double rad = Math.toRadians(angle);
			final int testX = point.getX() + (int)(Math.cos(rad) * checkRadius);
			final int testY = point.getY() + (int)(Math.sin(rad) * checkRadius);
			final int testZ = point.getZ();
			
			if (MovementIntegration.canMoveToTargetForAutoFarm(
				player.getX(), player.getY(), player.getZ(),
				testX, testY, testZ))
			{
				if (!isNearObstacle(testX, testY, testZ))
				{
					return new Location(testX, testY, testZ);
				}
			}
		}
		
		return point;
	}
	
	/**
	 * Suaviza todo o caminho para evitar bordas de obstáculos.
	 * Remove pontos muito próximos de obstáculos e ajusta o caminho.
	 * 
	 * @param path O caminho original
	 * @param startPos Posição inicial
	 * @return Caminho suavizado
	 */
	private List<Location> smoothPath(List<Location> path, Location startPos)
	{
		if (path == null || path.isEmpty())
			return path;
		
		final List<Location> smoothedPath = new ArrayList<>();
		Location currentPos = startPos;
		
		for (int i = 0; i < path.size(); i++)
		{
			final Location point = path.get(i);
			
			if (isNearObstacle(point.getX(), point.getY(), point.getZ()))
			{
				final Location safePoint = findSafePointNear(currentPos, point);
				if (safePoint != null)
				{
					smoothedPath.add(safePoint);
					currentPos = safePoint;
					continue;
				}
			}
			
			smoothedPath.add(point);
			currentPos = point;
		}
		
		return smoothedPath.isEmpty() ? path : smoothedPath;
	}
	
	/**
	 * Verifica se uma posição está próxima de um obstáculo.
	 * 
	 * @param x Coordenada X
	 * @param y Coordenada Y
	 * @param z Coordenada Z
	 * @return true se está próximo de um obstáculo
	 */
	private boolean isNearObstacle(int x, int y, int z)
	{
		final int checkDistance = 50;
		
		if (!GeoEngine.getInstance().canMoveToTarget(x, y, z, x, y + checkDistance, z))
			return true;
		
		if (!GeoEngine.getInstance().canMoveToTarget(x, y, z, x, y - checkDistance, z))
			return true;
		
		if (!GeoEngine.getInstance().canMoveToTarget(x, y, z, x + checkDistance, y, z))
			return true;
		
		if (!GeoEngine.getInstance().canMoveToTarget(x, y, z, x - checkDistance, y, z))
			return true;
		
		return false;
	}
	
	/**
	 * Encontra um ponto seguro próximo a uma posição que evita obstáculos.
	 * 
	 * @param from Posição de origem
	 * @param target Posição alvo
	 * @return Ponto seguro ou null se não encontrar
	 */
	private Location findSafePointNear(Location from, Location target)
	{
		final int maxAttempts = 8;
		final int[] offsets = {30, 50, 70};
		
		for (int offset : offsets)
		{
			for (int i = 0; i < maxAttempts; i++)
			{
				final double angle = (i * 45) * Math.PI / 180;
				final int testX = target.getX() + (int)(Math.cos(angle) * offset);
				final int testY = target.getY() + (int)(Math.sin(angle) * offset);
				final int testZ = target.getZ();
				
				if (MovementIntegration.canMoveToTargetForAutoFarm(
					from.getX(), from.getY(), from.getZ(),
					testX, testY, testZ) && !isNearObstacle(testX, testY, testZ))
				{
					return new Location(testX, testY, testZ);
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Calcula uma posição de contorno quando o pathfinding direto falha.
	 * Tenta encontrar uma posição ao redor do obstáculo.
	 * 
	 * @param player O jogador
	 * @param target O alvo
	 * @return Location de contorno ou null se não encontrar
	 */
	private Location calculateBypassPosition(Player player, Creature target)
	{
		final Location playerPos = player.getPosition();
		final Location targetPos = target.getPosition();
		
		final int dx = targetPos.getX() - playerPos.getX();
		final int dy = targetPos.getY() - playerPos.getY();
		final double angle = Math.atan2(dy, dx);
		
		final int[] offsets = {50, 100, 150, 200};
		
		for (int offset : offsets)
		{
			for (int i = 0; i < 4; i++)
			{
				final double testAngle = angle + (i - 1) * (Math.PI / 4);
				final int testX = playerPos.getX() + (int)(Math.cos(testAngle) * offset);
				final int testY = playerPos.getY() + (int)(Math.sin(testAngle) * offset);
				final int testZ = playerPos.getZ();
				
				final Location testLoc = new Location(testX, testY, testZ);
				
				if (MovementIntegration.canMoveToTargetForAutoFarm(
					playerPos.getX(), playerPos.getY(), playerPos.getZ(),
					testX, testY, testZ))
				{
				if (GeoEngine.getInstance().canSee(
					testX, testY, testZ, player.getCollisionHeight(),
					targetPos.getX(), targetPos.getY(), targetPos.getZ(), target.getCollisionHeight(),
					null, null))
				{
					sendAdminMessage(player, "Found bypass position at offset " + offset);
					return testLoc;
				}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Verifica Line of Sight (LoS) para ataque com lógica inteligente.
	 * Similar ao PlayerAI: verifica geodata rigorosamente para não atacar através de paredes.
	 * 
	 * @param player O jogador
	 * @param target O alvo
	 * @param dist2D Distância 2D até o alvo
	 * @return true se pode ver o alvo para atacar
	 */
	private boolean checkLineOfSightForAttack(Player player, Creature target, double dist2D)
	{
		if (dist2D < 100)
		{
			boolean canSee = MovementIntegration.canSeeTargetForAutoFarm(player, target);
			
			if (!canSee)
			{
				final int heightDiff = Math.abs(player.getZ() - target.getZ());
				final double dist3D = Math.sqrt(dist2D * dist2D + heightDiff * heightDiff);
				
				if (dist3D < 250 && heightDiff < 200)
				{
					if (MovementIntegration.canMoveToTargetForAutoFarm(
						player.getX(), player.getY(), player.getZ(),
						target.getX(), target.getY(), target.getZ()))
					{
						return true;
					}
				}
			}
			
			if (!canSee && isMeleeDebug(player))
			{
				LOGGER.info("[MeleeDebug] LoS: blocked player={} target={} dist2D={}",
					player.getObjectId(), target.getObjectId(), dist2D);
			}
			return canSee;
		}
		
		if (dist2D < 200)
		{
			boolean canSee = MovementIntegration.canSeeTargetForAutoFarm(player, target);
			
			if (!canSee)
			{
				final int heightDiff = Math.abs(player.getZ() - target.getZ());
				final double dist3D = Math.sqrt(dist2D * dist2D + heightDiff * heightDiff);
				
				if (dist3D < 250 && heightDiff < 200)
				{
					if (MovementIntegration.canMoveToTargetForAutoFarm(
						player.getX(), player.getY(), player.getZ(),
						target.getX(), target.getY(), target.getZ()))
					{
						return true;
					}
				}
			}
			
			if (!canSee && isMeleeDebug(player))
			{
				LOGGER.info("[MeleeDebug] LoS: blocked player={} target={} dist2D={}",
					player.getObjectId(), target.getObjectId(), dist2D);
			}
			return canSee;
		}
		
		return MovementIntegration.canSeeTarget(player, target);
	}
	
	/**
	 * Método público para ser chamado quando o player causa dano em um monstro
	 */
	public void onTargetHit(Monster target)
	{
		if (target != null && target == _lockedTarget)
		{
			_targetLastHitTime = System.currentTimeMillis();
		}
	}
}


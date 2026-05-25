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
package ext.mods.gameserver.model.actor.ai.type;

import java.util.List;

import ext.mods.Config;
import ext.mods.commons.pool.ThreadPool;
import ext.mods.extensions.listener.manager.PlayerListenerManager;
import ext.mods.gameserver.data.manager.CursedWeaponManager;
import ext.mods.gameserver.enums.AiEventType;
import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.enums.LootRule;
import ext.mods.gameserver.enums.items.ArmorType;
import ext.mods.gameserver.enums.items.EtcItemType;
import ext.mods.gameserver.enums.items.WeaponType;
import ext.mods.gameserver.enums.skills.SkillTargetType;
import ext.mods.gameserver.enums.skills.SkillType;
import ext.mods.gameserver.model.actor.move.MovementIntegration;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.geoengine.pathfinding.SmoothObstacleAvoidance;
import ext.mods.gameserver.handler.IItemHandler;
import ext.mods.gameserver.handler.ItemHandler;
import ext.mods.gameserver.handler.admincommandhandlers.AdminInfo;
import ext.mods.gameserver.handler.bypasshandlers.DropListUI;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Boat;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.Summon;
import ext.mods.gameserver.model.actor.container.player.BoatInfo;
import ext.mods.gameserver.model.actor.instance.Chest;
import ext.mods.gameserver.model.actor.instance.Door;
import ext.mods.gameserver.model.actor.instance.FestivalMonster;
import ext.mods.gameserver.model.actor.instance.GrandBoss;
import ext.mods.gameserver.model.actor.instance.Monster;
import ext.mods.gameserver.model.actor.instance.RaidBoss;
import ext.mods.gameserver.model.actor.instance.StaticObject;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.AutoAttackStart;
import ext.mods.gameserver.network.serverpackets.ChairSit;
import ext.mods.gameserver.network.serverpackets.MoveToPawn;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.network.serverpackets.StopMove;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.taskmanager.AttackStanceTaskManager;
import ext.mods.gameserver.taskmanager.ItemsOnGroundTaskManager;
import ext.mods.sellBuffEngine.BuffShopBypassHandler;
import ext.mods.sellBuffEngine.BuffShopManager;
import ext.mods.commons.logging.CLogger;
import ext.mods.gameserver.model.entity.autofarm.AutoFarmManager;
import ext.mods.gameserver.model.entity.autofarm.zone.AutoFarmArea;

public class PlayerAI extends PlayableAI<Player>
{
    private static final CLogger LOGGER = new CLogger(PlayerAI.class.getName());
    
    public PlayerAI(Player player)
    {
        super(player);
    }
    
    @Override
    protected void thinkIdle()
    {
        final var profile = AutoFarmManager.getInstance().getPlayer(_actor.getObjectId());
        if (profile != null)
        {
            final AutoFarmArea area = profile.getSelectedArea();
            if (area != null && area.isHandlingDeath())
            {
                if (Config.AUTOFARM_DEBUG_RETURN)
                    LOGGER.info("[PlayerAI][DeathReturn] thinkIdle() skip super.thinkIdle() (Creature.getMove().stop()): AutoFarmArea.isHandlingDeath() true");
                return;
            }
        }
        super.thinkIdle();
    }
    
    @Override
    protected void onEvtArrived()
    {
        final BoatInfo info = _actor.getBoatInfo();
        
        info.setBoatMovement(false);
        
        if (_currentIntention.getType() == IntentionType.MOVE_TO)
        {
            final Boat boat = _currentIntention.getBoat();
            if (boat != null)
            {
                info.setCanBoard(true);
                
                if (_actor.getSummon() != null)
                    _actor.sendPacket(SystemMessageId.RELEASE_PET_ON_BOAT);
            }
        }
        
        super.onEvtArrived();
    }
    
    @Override
    protected void onEvtArrivedBlocked()
    {
        final BoatInfo info = _actor.getBoatInfo();
        
        info.setBoatMovement(false);
        
        if (_currentIntention.getType() == IntentionType.INTERACT)
        {
            clientActionFailed();
            
            final WorldObject target = _currentIntention.getTarget();
            if (_actor.getAI().canDoInteract(target))
            {
                _actor.broadcastPacket(new StopMove(_actor));
                
                target.onInteract(_actor);
            }
            else
                super.onEvtArrivedBlocked();
            
            doIdleIntention();
        }
        else if (_currentIntention.getType() == IntentionType.CAST)
        {
            _actor.sendPacket(SystemMessageId.DIST_TOO_FAR_CASTING_STOPPED);
            super.onEvtArrivedBlocked();
        }
        else
            super.onEvtArrivedBlocked();
    }
    
    @Override
    protected void onEvtSatDown(WorldObject target)
    {
        if (_nextIntention.isBlank())
            doIdleIntention();
        else
            doIntention(_nextIntention);
    }
    
    @Override
    protected void onEvtStoodUp()
    {
        if (_actor.getThroneId() != 0)
        {
            final WorldObject object = World.getInstance().getObject(_actor.getThroneId());
            if (object instanceof StaticObject staticObject)
                staticObject.setBusy(false);
            
            _actor.setThroneId(0);
        }
        
        if (_nextIntention.isBlank())
            doIdleIntention();
        else
            doIntention(_nextIntention);
    }
    
    @Override
    protected void onEvtBowAttackReuse()
    {
        if (_actor.getAttackType() == WeaponType.BOW)
        {
            if (_nextIntention.getType() == IntentionType.ATTACK)
            {
                doIntention(_nextIntention);
                return;
            }
            
            if (_currentIntention.getType() == IntentionType.ATTACK)
            {
                if (_actor.canKeepAttacking(_currentIntention.getFinalTarget()))
                    notifyEvent(AiEventType.THINK, null, null);
                else
                    doIdleIntention();
            }
        }
    }
    
    @Override
    protected void onEvtAttacked(Creature attacker)
    {
        if (_actor.getTamedBeast() != null)
            _actor.getTamedBeast().getAI().notifyEvent(AiEventType.OWNER_ATTACKED, attacker, null);
        
        if (_actor.isSitting())
            doStandIntention();
        
        super.onEvtAttacked(attacker);
    }
    
    @Override
    protected void onEvtCancel()
    {
        _actor.getCast().stop();
        _actor.getMove().cancelFollowTask();
        
        doIdleIntention();
    }
    
    @Override
    public void thinkAttack()
    {
        final Creature target = _currentIntention.getFinalTarget();
        if (target == null)
            return;
            
        final boolean isShiftPressed = _currentIntention.isShiftPressed();
        
        if (tryShiftClick(target, isShiftPressed))
        {
            _actor.sendMessage("Você não pode atacar usando o Shift");
            return;
        }
        
        if (_actor.denyAiAction() || _actor.isSitting())
        {
            doIdleIntention();
            clientActionFailed();
            _actor.sendMessage("Você não pode atacar enquanto está sentado");
            return;
        }
        
        if (isTargetLost(target))
        {
            doIdleIntention();
            clientActionFailed();
            _actor.sendMessage("Você não pode atacar um alvo perdido");
            return;
        }
        
        final int attackRange = _actor.getStatus().getPhysicalAttackRange();
        
        if (checkPlayerStuckInCollision(target, attackRange, isShiftPressed))
            return;
        
        if (checkObstacleAndMove(target, isShiftPressed) && Config.ATTACK_USE_PATHFINDER)
            return;
        
        if (Config.SISTEMA_PATHFINDING)
        {
            if (_actor.getMove().maybeStartPlayerOffensiveFollow(target, attackRange))
            {
                if (isShiftPressed)
                {
                    doIdleIntention();
                    _actor.sendMessage("Você está usando o Shift para atacar");
                }
                return;
            }
        }
        else
        {
            if (_actor.getMove().maybeMoveToPawn(target, attackRange, isShiftPressed))
            {
                if (isShiftPressed)
                {
                    doIdleIntention();
                    _actor.sendMessage("Você está usando o Shift para atacar");
                }
                return;
            }
        }
        
        _actor.getMove().stop();
        
        if ((_actor.getAttackType() == WeaponType.BOW && _actor.getAttack().isBowCoolingDown()) 
            || _actor.getAttack().isAttackingNow() 
            || _actor.getCast().isCastingNow())
        {
            setNextIntention(_currentIntention);
            return;
        }
        
        if (!_actor.getAttack().canAttack(target))
        {
            doIdleIntention();
            return;
        }
        
        if (target.isDead())
        {
            doIdleIntention();
            return;
        }

		if (Config.SISTEMA_PATHFINDING && !MovementIntegration.canSeeTarget(_actor, target))
		{
				_actor.sendPacket(SystemMessageId.CANT_SEE_TARGET);
				doIdleIntention();
				return;
		}
        
        final double distance2D = _actor.distance2D(target);
        final double collisionBuffer = _actor.getCollisionRadius() + target.getCollisionRadius();
        final int totalAttackRange = (int)(attackRange + collisionBuffer);
        
        final boolean isInAttackRange = _actor.getMove().getMoveType() == ext.mods.gameserver.enums.actors.MoveType.GROUND
            ? distance2D <= totalAttackRange
            : _actor.distance3D(target) <= totalAttackRange;
        
        if (!isInAttackRange)
        {
            if (Config.SISTEMA_PATHFINDING)
            {
                if (_actor.getMove().maybeStartPlayerOffensiveFollow(target, attackRange))
                    return;
            }
            else
            {
                if (_actor.getMove().maybeMoveToPawn(target, attackRange, isShiftPressed))
                    return;
            }
            
            doIdleIntention();
            return;
        }
        
        _actor.getAttack().doAttack(target);
        
        if (!Config.ATTACK_PTS)
            setNextIntention(_currentIntention);
    }
    
    @Override
    protected void thinkCast()
    {
        if (_actor.denyAiAction() || _actor.getAllSkillsDisabled() || _actor.getCast().isCastingNow())
        {
            doIdleIntention();
            clientActionFailed();
            return;
        }
        
        final Creature target = _currentIntention.getFinalTarget();
        if (target == null)
        {
            doIdleIntention();
            return;
        }
        
        final L2Skill skill = _currentIntention.getSkill();
        if (isTargetLost(target, skill))
        {
            doIdleIntention();
            return;
        }
        
        if (!_actor.getCast().canAttemptCast(target, skill))
            return;
        
        final boolean isShiftPressed = _currentIntention.isShiftPressed();
        final int skillRange = skill.getCastRange() > 0 ? skill.getCastRange() : skill.getSkillRadius();
        
        switch (skill.getTargetType())
        {
            case GROUND:
            {
                final Location signetLocation = _actor.getCast().getSignetLocation();
                final double groundDistance = _actor.distance2D(signetLocation);
                
                boolean hasGroundObstacle = false;
                if (Config.SISTEMA_PATHFINDING && groundDistance > 20.0 && groundDistance < 1000)
                {
                    boolean canMove = MovementIntegration.canMoveToTarget(
                        _actor.getX(), _actor.getY(), _actor.getZ(),
                        signetLocation.getX(), signetLocation.getY(), signetLocation.getZ()
                    );
                    if (!canMove)
                        hasGroundObstacle = true;
                }
                
                final int effectiveGroundRange = hasGroundObstacle ? skillRange + 500 : skillRange;
                
                if (_actor.getMove().maybePlayerMoveToLocation(signetLocation, effectiveGroundRange, Config.SISTEMA_PATHFINDING, isShiftPressed))
                {
                    if (isShiftPressed && !hasGroundObstacle && groundDistance > skillRange * 2.0)
                    {
                        _actor.sendPacket(SystemMessageId.TARGET_TOO_FAR);
                        _actor.sendMessage("Você está fora de alcance para usar a skill");
                        doIdleIntention();
                    }
                    return;
                }
                break;
            }
            
            default:
            {
                if (Config.SISTEMA_PATHFINDING)
                {
                    if (checkObstacleAndMove(target, isShiftPressed) && Config.ATTACK_USE_PATHFINDER)
                        return;
                        
                    final double distance = _actor.distance2D(target);
                    final int totalRadius = (int) (skillRange + _actor.getCollisionRadius() + target.getCollisionRadius());
                    
                    boolean hasObstacle = false;
                    if (distance > Config.NPC_MOVEMENT_PLAYER_RANGE && distance > 20)
                    {
                        if (!MovementIntegration.canSeeTarget(_actor, target))
                            hasObstacle = true;
                    }

                    final int effectiveSkillRange = hasObstacle ? skillRange + 500 : skillRange;
                    
                    if (_actor.getMove().maybeStartPlayerOffensiveFollow(target, effectiveSkillRange))
                    {
                        if (isShiftPressed && !hasObstacle && distance > totalRadius * 2.0)
                        {
                            _actor.sendPacket(SystemMessageId.TARGET_TOO_FAR);
                            _actor.sendMessage("Você está fora de alcance para usar a skill");
                            doIdleIntention();
                        }
                        return;
                    }
                }
                else
                {
                    if (_actor.getMove().maybeMoveToPawn(target, skillRange, isShiftPressed))
                    {
                        if (isShiftPressed)
                        {
                            _actor.sendPacket(SystemMessageId.TARGET_TOO_FAR);
                            _actor.sendMessage("Você está fora de alcance para usar a skill");
                            doIdleIntention();
                        }
                        return;
                    }
                }
                break;
            }
        }
        
        _actor.getMove().stop();
        
        if (skill.isToggle())
        {
            if (Config.STOP_TOGGLE)
                _actor.getMove().stop();
            
            _actor.getCast().doToggleCast(skill, target);
            return;
        }
        
        final boolean isCtrlPressed = _currentIntention.isCtrlPressed();
        final int itemObjectId = _currentIntention.getItemObjectId();
        
        if (!_actor.getCast().canCast(target, skill, isCtrlPressed, itemObjectId))
        {
            if (Config.SISTEMA_PATHFINDING && skill.getTargetType() != SkillTargetType.GROUND 
                && !_actor.isMovementDisabled() && _currentIntention.canMoveToTarget())
            {
                if (checkObstacleAndMove(target, isShiftPressed) && Config.ATTACK_USE_PATHFINDER)
                    return;
                    
                _actor.getMove().maybeStartPlayerOffensiveFollow(target, skillRange);
                return;
            }
            
            if (skill.nextActionIsAttack() && target.isAttackableWithoutForceBy(_actor))
                doAttackIntention(target, isCtrlPressed, isShiftPressed, true);
            
            return;
        }
        
        if (Config.SISTEMA_PATHFINDING && skill.getTargetType() != SkillTargetType.GROUND && 
            !_actor.isMovementDisabled() && _currentIntention.canMoveToTarget())
        {
            if (checkObstacleAndMove(target, isShiftPressed) && Config.ATTACK_USE_PATHFINDER)
                return;
        }
        
        
        if (skill.getHitTime() > 50)
            _actor.getMove().stop();
        
        if (skill.getHitTime() > 50 && target != _actor)
            _actor.getPosition().setHeadingTo(target);
        
        if (skill.getSkillType() == SkillType.FUSION || skill.getSkillType() == SkillType.SIGNET_CASTTIME)
            _actor.getCast().doFusionCast(skill, target);
        else
            _actor.getCast().doCast(skill, target, _actor.getInventory().getItemByObjectId(itemObjectId));
    }
    
    /**
     * Verifica se o player está preso em colisão com NPCs ou monstros.
     * Se estiver preso, calcula uma posição alternativa para desviar antes de atacar.
     * Evita bugs de ataque fora do range e através de paredes.
     * 
     * @param target O alvo do ataque
     * @param attackRange O range de ataque
     * @param isShiftPressed Se o shift está pressionado (impede movimento)
     * @return true se o player está preso e um movimento de desvio foi iniciado, false caso contrário
     */
    private boolean checkPlayerStuckInCollision(Creature target, int attackRange, boolean isShiftPressed)
    {
        return false;
    }
    
    /**
     * Verifica se há obstáculos no caminho para o alvo e inicia o movimento de desvio se necessário.
     * Centraliza a lógica de desvio para ataques físicos e mágicos.
     * * @param target O alvo
     * @param isShiftPressed Se o shift está pressionado (impede movimento)
     * @return true se um movimento de desvio foi iniciado, false caso contrário
     */
    private boolean checkObstacleAndMove(Creature target, boolean isShiftPressed)
    {
        if (isShiftPressed || !Config.SISTEMA_PATHFINDING || !Config.ATTACK_USE_PATHFINDER || _actor.isMovementDisabled() || !_currentIntention.canMoveToTarget())
            return false;
            
        final double distance = _actor.distance2D(target);
        
        if (distance <= Config.NPC_MOVEMENT_PLAYER_RANGE)
        {
            boolean canSee = MovementIntegration.canSeeTarget(_actor, target);
            
            if (!canSee)
            {
                Location bypassPath = calculateBypassPathForTarget(target);
                
                if (bypassPath != null)
                {
                    _actor.getMove().maybePlayerMoveToLocation(bypassPath, 0, true, false);
                    setNextIntention(_currentIntention);
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    protected void thinkFakeDeath()
    {
        if (_actor.denyAiAction() || _actor.isMounted())
        {
            clientActionFailed();
            return;
        }
        
        if (_currentIntention.isCtrlPressed())
        {
            _actor.getMove().stop();
            _actor.startFakeDeath();
        }
        else
            _actor.stopFakeDeath(false);
    }
    
    @Override
    protected ItemInstance thinkPickUp()
    {
        final ItemInstance item = super.thinkPickUp();
        if (item == null)
            return null;
        
        synchronized (item)
        {
            if (!item.isVisible())
                return null;
            
            if (((_actor.isInParty() && _actor.getParty().getLootRule() == LootRule.ITEM_LOOTER) || !_actor.isInParty()) && !_actor.getInventory().validateCapacity(item))
            {
                _actor.sendPacket(SystemMessageId.SLOTS_FULL);
                return null;
            }
            
            if (!_actor.getInventory().validateWeight(item.getWeight()))
            {
                _actor.sendPacket(SystemMessageId.WEIGHT_LIMIT_EXCEEDED);
                return null;
            }
            
            if (_actor.getActiveTradeList() != null)
            {
                _actor.sendPacket(SystemMessageId.CANNOT_PICKUP_OR_USE_ITEM_WHILE_TRADING);
                return null;
            }
            
            if (item.getOwnerId() != 0 && !_actor.isLooterOrInLooterParty(item.getOwnerId()))
            {
                if (item.getItemId() == 57)
                    _actor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FAILED_TO_PICKUP_S1_ADENA).addNumber(item.getCount()));
                else if (item.getCount() > 1)
                    _actor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FAILED_TO_PICKUP_S2_S1_S).addItemName(item).addNumber(item.getCount()));
                else
                    _actor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FAILED_TO_PICKUP_S1).addItemName(item));
                
                return null;
            }
            
            if (item.hasDropProtection())
                item.removeDropProtection();
            
            item.pickupMe(_actor);
            PlayerListenerManager.getInstance().notifyItemPickup(_actor, item);
            ItemsOnGroundTaskManager.getInstance().remove(item);
        }
        
        if (item.getItemType() == EtcItemType.HERB)
        {
            final IItemHandler handler = ItemHandler.getInstance().getHandler(item.getEtcItem());
            if (handler != null)
                handler.useItem(_actor, item, false);
            
            item.destroyMe();
        }
        else if (CursedWeaponManager.getInstance().isCursed(item.getItemId()))
            _actor.addItem(item, true);
        else
        {
            if (item.getItemType() instanceof ArmorType || item.getItemType() instanceof WeaponType)
            {
                SystemMessage sm;
                if (item.getEnchantLevel() > 0)
                    sm = SystemMessage.getSystemMessage(SystemMessageId.ATTENTION_S1_PICKED_UP_S2_S3).addString(_actor.getName()).addNumber(item.getEnchantLevel()).addItemName(item.getItemId());
                else
                    sm = SystemMessage.getSystemMessage(SystemMessageId.ATTENTION_S1_PICKED_UP_S2).addString(_actor.getName()).addItemName(item.getItemId());
                
                _actor.broadcastPacketInRadius(sm, 1400);
            }
            
            if (_actor.isInParty())
                _actor.getParty().distributeItem(_actor, item, null);
            else if (item.getItemId() == 57 && _actor.getInventory().getAdenaInstance() != null)
            {
                _actor.addAdena(item.getCount(), true);
                item.destroyMe();
            }
            else
                _actor.addItem(item, true);
        }
        
        ThreadPool.schedule(() -> _actor.setIsParalyzed(false), 200);
        _actor.setIsParalyzed(true);
        
        return item;
    }
    
    @Override
    protected void thinkInteract()
    {
        final WorldObject target = _currentIntention.getTarget();
        final boolean isShiftPressed = _currentIntention.isShiftPressed();
        
        if (tryShiftClick(target, isShiftPressed))
            return;
        
        clientActionFailed();
        
        if (_actor.denyAiAction() || _actor.isSitting() || _actor.isFlying())
        {
            doIdleIntention();
            return;
        }
        
        if (target instanceof Player && BuffShopManager.getInstance().getSellers().containsKey(target.getObjectId()))
        {
            Player sellerNpc = (Player) target;
            Integer ownerId = BuffShopManager.getInstance().getSellers().get(sellerNpc.getObjectId());
            if (ownerId != null && ownerId.equals(_actor.getObjectId()))
            {
                _actor.sendMessage("Você não pode comprar buffs da sua própria loja.");
                
                _actor.sendPacket(ActionFailed.STATIC_PACKET);
                doIdleIntention(); 
                return;
            }
            if (_actor.distance3D(target) > 100)
            {
                
                _actor.getMove().maybePlayerMoveToLocation(target.getPosition(), 100, true, false);
                
                BuffShopBypassHandler.getInstance().handleBypass(_actor, "showShop " + target.getObjectId() + " 1 1");
                return; 
            }
            
            
            BuffShopBypassHandler.getInstance().handleBypass(_actor, "showShop " + target.getObjectId() + " 1 1");
            _actor.sendPacket(ActionFailed.STATIC_PACKET);
            doIdleIntention(); 
            return; 
            
        }
        
        if (isTargetLost(target))
        {
            doIdleIntention();
            return;
        }
        
        if (!canAttemptInteract())
        {
            doIdleIntention();
            return;
        }
        
        if (_actor.getMove().maybeMoveToPawn(target, 100, isShiftPressed))
        {
            if (isShiftPressed)
                doIdleIntention();
            
            return;
        }
        
        if (!canDoInteract(target))
        {
            doIdleIntention();
            return;
        }
        
        if (target instanceof Npc targetNpc && targetNpc.isMoving())
            _actor.broadcastPacket(new StopMove(_actor));
        else
        {
            _actor.getPosition().setHeadingTo(target);
            _actor.broadcastPacket(new MoveToPawn(_actor, target, Npc.INTERACTION_DISTANCE));
        }
        
        target.onInteract(_actor);
        
        doIdleIntention();
    }
    
    private boolean tryShiftClick(WorldObject target, boolean isShiftPressed)
    {
        if (isShiftPressed)
        {
            if (_actor.isGM())
            {
                if (target instanceof Npc)
                {
                    final NpcHtmlMessage html = new NpcHtmlMessage(0);
                    AdminInfo.sendGeneralInfos(_actor, (Npc) target, html, 0);
                    _actor.sendPacket(html);
                    clientActionFailed();
                    return true;
                }
                else if (target instanceof Door)
                {
                    final NpcHtmlMessage html = new NpcHtmlMessage(0);
                    AdminInfo.showDoorInfo(_actor, (Door) target, html);
                    _actor.sendPacket(html);
                    clientActionFailed();
                    return true;
                }
                else if (target instanceof Summon)
                {
                    final NpcHtmlMessage html = new NpcHtmlMessage(0);
                    AdminInfo.showPetInfo((Summon) target, _actor, html);
                    _actor.sendPacket(html);
                    clientActionFailed();
                    return true;
                }
            }
            else if (Config.SHOW_NPC_INFO)
            {
                if (target instanceof Monster || target instanceof RaidBoss || target instanceof GrandBoss || target instanceof FestivalMonster || target instanceof Chest)
                {
                    
                    DropListUI.sendNpcDrop(_actor, target.getMonster().getNpcId(), 1);
                    
                    return false;
                }
            }
        }
        return false;
    }
    
    @Override
    protected void thinkSit()
    {
        if (_actor.denyAiAction() || _actor.isSitting() || _actor.isOperating() || _actor.isMounted())
        {
            doIdleIntention();
            clientActionFailed();
            return;
        }
        
        _actor.getMove().stop();
        
        _actor.sitDown();
        
        final WorldObject target = _currentIntention.getTarget();
        if (target instanceof StaticObject targetStaticObject && targetStaticObject.getType() == 1 && !targetStaticObject.isBusy() && _actor.isIn3DRadius(targetStaticObject, Npc.INTERACTION_DISTANCE))
        {
            _actor.setThroneId(targetStaticObject.getObjectId());
            
            targetStaticObject.setBusy(true);
            _actor.broadcastPacket(new ChairSit(_actor.getObjectId(), targetStaticObject.getStaticObjectId()));
        }
    }
    
    @Override
    protected void thinkStand()
    {
        if (_actor.denyAiAction() || !_actor.isSitting() || _actor.isMounted())
        {
            doIdleIntention();
            clientActionFailed();
            return;
        }
        
        if (_actor.isFakeDeath())
            _actor.stopFakeDeath(true);
        else
            _actor.standUp();
    }
    
    @Override
    protected void thinkUseItem()
    {
        final ItemInstance itemToTest = _actor.getInventory().getItemByObjectId(_currentIntention.getItemObjectId());
        if (itemToTest == null)
            return;
        
        _actor.useEquippableItem(itemToTest, false);
        
        if (_previousIntention.getType() != IntentionType.CAST && _previousIntention.getType() != IntentionType.USE_ITEM)
            doIntention(_previousIntention);
    }
    
    @Override
    public boolean canAttemptInteract()
    {
        if (_actor.isOperating() || _actor.isProcessingTransaction())
            return false;
        
        return true;
    }
    
    @Override
    public boolean canDoInteract(WorldObject target)
    {
        if (_actor.isOperating() || _actor.isProcessingTransaction())
            return false;
        
        if (target == null)
            return false;
        
        return target.isIn3DRadius(_actor, Npc.INTERACTION_DISTANCE);
    }
    
    @Override
    public void startAttackStance()
    {
        if (!AttackStanceTaskManager.getInstance().isInAttackStance(_actor))
        {
            final Summon summon = _actor.getSummon();
            if (summon != null)
                summon.broadcastPacket(new AutoAttackStart(summon.getObjectId()));
            
            _actor.broadcastPacket(new AutoAttackStart(_actor.getObjectId()));
        }
        
        AttackStanceTaskManager.getInstance().add(_actor);
    }
    
    /**
     * Método helper para AutoFarm - padroniza chamadas de intenções.
     * Similar ao padrão usado no Kotlin para melhor eficiência.
     * * @param intentionType Tipo da intenção
     * @param args Argumentos variados dependendo do tipo:
     * - ATTACK: Creature target
     * - CAST: L2Skill skill, Creature target
     * - MOVE_TO: Location loc
     * - PICK_UP: ItemInstance item ou int itemObjectId
     */
    public void setAutoFarmIntention(IntentionType intentionType, Object... args)
    {
        switch (intentionType)
        {
            case ATTACK:
                if (args.length > 0 && args[0] instanceof Creature)
                    doAttackIntention((Creature) args[0], false, false, true);
                break;
            
            case CAST:
                if (args.length >= 2 && args[0] instanceof L2Skill && args[1] instanceof Creature)
                {
                    final L2Skill skill = (L2Skill) args[0];
                    final Creature target = (Creature) args[1];
                    doCastIntention(target, skill, false, false, 0, true);
                }
                break;
            
            case MOVE_TO:
                if (args.length > 0)
                {
                    if (args[0] instanceof Location)
                        doMoveToIntention((Location) args[0], null);
                    else if (args[0] instanceof ext.mods.gameserver.model.location.SpawnLocation)
                        doMoveToIntention((ext.mods.gameserver.model.location.SpawnLocation) args[0], null);
                }
                break;
            
            case PICK_UP:
                if (args.length > 0)
                {
                    if (args[0] instanceof ItemInstance)
                        doPickUpIntention(((ItemInstance) args[0]).getObjectId(), false);
                    else if (args[0] instanceof Integer)
                        doPickUpIntention((Integer) args[0], false);
                }
                break;
            
            default:
                break;
        }
    }
    
    /**
     * Calcula rota para contornar obstáculo antes de atacar/castar.
     * Usa PathFinder otimizado quando ATTACK_USE_PATHFINDER está ativo.
     * Verifica colisão com monstros na direção do alvo e aplica contorno respeitando colisão.
     * Similar ao AutoFarmRoutine, mas adaptado para PlayerAI.
     * * @param target O alvo
     * @return Location do primeiro ponto do caminho para contornar obstáculo, ou null se não houver caminho
     */
    private Location calculateBypassPathForTarget(Creature target)
    {
        if (target == null || target.isDead())
            return null;
        
        final Location playerPos = _actor.getPosition();
        final Location targetPos = target.getPosition();
        final int playerX = playerPos.getX();
        final int playerY = playerPos.getY();
        final int playerZ = playerPos.getZ();
        final int targetX = targetPos.getX();
        final int targetY = targetPos.getY();
        final int targetZ = targetPos.getZ();
        
        boolean hasObstacle = !MovementIntegration.canSeeTarget(_actor, target);
        
        if (!hasObstacle)
        {
            if (!GeoEngine.getInstance().canMoveToTarget(playerX, playerY, playerZ, targetX, targetY, targetZ))
            {
                hasObstacle = true;
            }
        }
        
        if (!hasObstacle)
            return null;
        
        Location finalDestination = targetPos;
            
        if (Config.SISTEMA_PATHFINDING)
        {
            List<Location> path = GeoEngine.getInstance().findPath(
                playerX, playerY, playerZ,
                finalDestination.getX(), finalDestination.getY(), finalDestination.getZ(),
                true,
                null
            );
            
            if (path != null && !path.isEmpty())
            {
                if (Config.ENABLE_SMOOTH_OBSTACLE_AVOIDANCE)
                {
                    List<Location> smoothPath = SmoothObstacleAvoidance.getInstance().createSmoothPath(path, null);
                    if (smoothPath != null && !smoothPath.isEmpty())
                    {
                        path = smoothPath;
                    }
                }
                
                final Location firstPoint = path.get(0);
                if (_actor.distance3D(firstPoint) < 40 && path.size() > 1)
                {
                    return path.get(1);
                }
                return firstPoint;
            }
        }
        
        return null;
    }
    
    @Override
    public void clientActionFailed()
    {
    }
}
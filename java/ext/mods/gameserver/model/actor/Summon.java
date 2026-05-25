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

import ext.mods.Config;
import ext.mods.gameserver.enums.TeamType;
import ext.mods.gameserver.enums.items.ActionType;
import ext.mods.gameserver.enums.items.ShotType;
import ext.mods.gameserver.handler.IItemHandler;
import ext.mods.gameserver.handler.ItemHandler;
import ext.mods.gameserver.model.actor.ai.type.SummonAI;
import ext.mods.gameserver.model.actor.instance.Pet;
import ext.mods.gameserver.model.actor.move.SummonMove;
import ext.mods.gameserver.model.actor.status.SummonStatus;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.Weapon;
import ext.mods.gameserver.model.itemcontainer.PetInventory;
import ext.mods.gameserver.model.olympiad.OlympiadGameManager;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.AbstractNpcInfo.SummonInfo;
import ext.mods.gameserver.network.serverpackets.L2GameServerPacket;
import ext.mods.gameserver.network.serverpackets.PetDelete;
import ext.mods.gameserver.network.serverpackets.PetInfo;
import ext.mods.gameserver.network.serverpackets.PetItemList;
import ext.mods.gameserver.network.serverpackets.PetStatusShow;
import ext.mods.gameserver.network.serverpackets.PetStatusUpdate;
import ext.mods.gameserver.network.serverpackets.RelationChanged;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.skills.L2Skill;

public abstract class Summon extends Playable
{
	private Player _owner;
	private boolean _previousFollowStatus = true;
	private int _shotsMask = 0;
	
	public static final int CONTRACT_PAYMENT = 4140;
	
	protected Summon(int objectId, NpcTemplate template, Player owner)
	{
		super(objectId, template);
		
		for (L2Skill skill : template.getPassives())
			addStatFuncs(skill.getStatFuncs(this));
		
		setShowSummonAnimation(true);
		
		_owner = owner;
	}
	
	public abstract int getSummonType();
	
	@Override
	public SummonAI getAI()
	{
		return (SummonAI) _ai;
	}
	
	@Override
	public void setAI()
	{
		_ai = new SummonAI(this);
	}
	
	@Override
	public SummonStatus<? extends Summon> getStatus()
	{
		return (SummonStatus<?>) _status;
	}
	
	@Override
	public void setStatus()
	{
		_status = new SummonStatus<>(this);
	}
	
	@Override
	public SummonMove getMove()
	{
		return (SummonMove) _move;
	}
	
	@Override
	public void setMove()
	{
		_move = new SummonMove(this);
	}
	
	@Override
	public NpcTemplate getTemplate()
	{
		return (NpcTemplate) super.getTemplate();
	}
	
	@Override
	public void setWalkOrRun(boolean value)
	{
		super.setWalkOrRun(value);
		
		getStatus().broadcastStatusUpdate();
	}
	
	@Override
	public void updateAbnormalEffect()
	{
		forEachKnownType(Player.class, player -> player.sendPacket(new SummonInfo(this, player, 1)));
	}
	
	@Override
	public boolean isGM()
	{
		return _owner.isGM();
	}
	
	@Override
	public void onInteract(Player player)
	{
		player.sendPacket(new PetStatusShow(this));
	}
	
	@Override
	public void onAction(Player player, boolean isCtrlPressed, boolean isShiftPressed)
	{
		if (player.getTarget() != this)
			player.setTarget(this);
		else
		{
			if (player == _owner)
			{
				if (isCtrlPressed)
					player.getAI().tryToAttack(this, isCtrlPressed, isShiftPressed);
				else
					player.getAI().tryToInteract(this, isCtrlPressed, isShiftPressed);
			}
			else
			{
				if (isAttackableWithoutForceBy(player) || (isCtrlPressed && isAttackableBy(player)))
					player.getAI().tryToAttack(this, isCtrlPressed, isShiftPressed);
				else
					player.getAI().tryToFollow(this, isShiftPressed);
			}
		}
	}
	
	@Override
	public final int getKarma()
	{
		return (_owner != null) ? _owner.getKarma() : 0;
	}
	
	@Override
	public final byte getPvpFlag()
	{
		return (_owner != null) ? _owner.getPvpFlag() : 0;
	}
	
	@Override
	public int getWeightLimit()
	{
		return 0;
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		if (!super.doDie(killer))
			return false;
		
		if (isPhoenixBlessed())
			_owner.reviveRequest(_owner, null, true);
		
		_owner.disableBeastShots();
		
		return true;
	}
	
	@Override
	public void onDecay()
	{
		if (_owner.getSummon() != this)
			return;
		
		deleteMe(_owner);
	}
	
	@Override
	public PetInventory getInventory()
	{
		return null;
	}
	
	@Override
	public ItemInstance getActiveWeaponInstance()
	{
		return null;
	}
	
	@Override
	public Weapon getActiveWeaponItem()
	{
		return null;
	}
	
	@Override
	public ItemInstance getSecondaryWeaponInstance()
	{
		return null;
	}
	
	@Override
	public Weapon getSecondaryWeaponItem()
	{
		return null;
	}
	
	@Override
	public boolean isInvul()
	{
		return super.isInvul() || _owner.isSpawnProtected();
	}
	
	@Override
	public Party getParty()
	{
		return (_owner == null) ? null : _owner.getParty();
	}
	
	@Override
	public boolean isInParty()
	{
		return _owner != null && _owner.getParty() != null;
	}
	
	@Override
	public void setIsImmobilized(boolean value)
	{
		super.setIsImmobilized(value);
		
		if (value)
		{
			_previousFollowStatus = getAI().getFollowStatus();
			if (_previousFollowStatus)
				getAI().setFollowStatus(false);
		}
		else
		{
			getAI().setFollowStatus(_previousFollowStatus);
		}
	}
	
	@Override
	public void sendDamageMessage(Creature target, int damage, boolean mcrit, boolean pcrit, boolean miss)
	{
		if (miss || _owner == null)
			return;
		
		if (target.getObjectId() != _owner.getObjectId())
		{
			if (pcrit || mcrit)
				sendPacket(SystemMessageId.CRITICAL_HIT_BY_PET);
			
			if (target.isInvul())
			{
				if (target.isParalyzed())
					sendPacket(SystemMessageId.OPPONENT_PETRIFIED);
				else
					sendPacket(SystemMessageId.ATTACK_WAS_BLOCKED);
			}
			else
				sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_HIT_FOR_S1_DAMAGE).addNumber(damage));
			
			if (_owner.isInOlympiadMode() && target instanceof Player targetPlayer && targetPlayer.isInOlympiadMode() && targetPlayer.getOlympiadGameId() == _owner.getOlympiadGameId())
				OlympiadGameManager.getInstance().notifyCompetitorDamage(_owner, damage);
		}
	}
	
	@Override
	public boolean isOutOfControl()
	{
		return super.isOutOfControl() || isBetrayed();
	}
	
	@Override
	public boolean isInCombat()
	{
		return _owner != null && _owner.isInCombat();
	}
	
	@Override
	public Player getActingPlayer()
	{
		return _owner;
	}
	
	@Override
	public void sendPacket(L2GameServerPacket packet)
	{
		if (_owner != null)
			_owner.sendPacket(packet);
	}
	
	@Override
	public void sendPacket(SystemMessageId id)
	{
		if (_owner != null)
			_owner.sendPacket(id);
	}
	
	@Override
	public void deleteMe()
	{
		super.deleteMe();
		
		stopAllEffects();
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		if (Config.SHOW_SUMMON_CREST)
			sendPacket(new SummonInfo(this, _owner, 0));
		
		sendPacket(new RelationChanged(this, _owner.getRelation(_owner), false));
		broadcastRelationsChanges();
		
		forceSeeCreature();
	}
	
	@Override
	public void broadcastRelationsChanges()
	{
		_owner.forEachKnownType(Player.class, player -> player.sendPacket(new RelationChanged(this, _owner.getRelation(player), isAttackableWithoutForceBy(player))));
	}
	
	@Override
	public void sendInfo(Player player)
	{
		if (player == _owner)
		{
			player.sendPacket(new PetInfo(this, 0));
			
			updateEffectIcons(true);
			
			if (this instanceof Pet)
				player.sendPacket(new PetItemList(this));
		}
		else
			player.sendPacket(new SummonInfo(this, player, 0));
	}
	
	@Override
	public void stopAllEffects()
	{
		super.stopAllEffects();
		
		sendPetInfosToOwner();
	}
	
	@Override
	public void stopAllEffectsExceptThoseThatLastThroughDeath()
	{
		super.stopAllEffectsExceptThoseThatLastThroughDeath();
		
		sendPetInfosToOwner();
	}
	
	@Override
	public boolean isChargedShot(ShotType type)
	{
		return (_shotsMask & type.getMask()) == type.getMask();
	}
	
	@Override
	public void setChargedShot(ShotType type, boolean charged)
	{
		if (charged)
			_shotsMask |= type.getMask();
		else
			_shotsMask &= ~type.getMask();
	}
	
	@Override
	public void rechargeShots(boolean physical, boolean magic)
	{
		if (_owner.getAutoSoulShot() == null || _owner.getAutoSoulShot().isEmpty())
			return;
		
		for (int itemId : _owner.getAutoSoulShot())
		{
			ItemInstance item = _owner.getInventory().getItemByItemId(itemId);
			if (item != null)
			{
				if (magic && item.getItem().getDefaultAction() == ActionType.summon_spiritshot)
				{
					final IItemHandler handler = ItemHandler.getInstance().getHandler(item.getEtcItem());
					if (handler != null)
						handler.useItem(_owner, item, false);
				}
				
				if (physical && item.getItem().getDefaultAction() == ActionType.summon_soulshot)
				{
					final IItemHandler handler = ItemHandler.getInstance().getHandler(item.getEtcItem());
					if (handler != null)
						handler.useItem(_owner, item, false);
				}
			}
			else
				_owner.removeAutoSoulShot(itemId);
		}
	}
	
	@Override
	public int getSkillLevel(int skillId)
	{
		final L2Skill skill = getSkill(skillId);
		return (skill == null) ? 0 : skill.getLevel();
	}
	
	@Override
	public L2Skill getSkill(int skillId)
	{
		return getTemplate().getSkills().values().stream().filter(s -> s.getId() == skillId).findFirst().orElse(null);
	}
	
	@Override
	public void onTeleported()
	{
		super.onTeleported();
		
		if (Config.SHOW_SUMMON_CREST)
			sendPacket(new SummonInfo(this, _owner, 0));
	}
	
	@Override
	public Clan getClan()
	{
		return (_owner != null) ? _owner.getClan() : null;
	}
	
	@Override
	public int getClanId()
	{
		return (_owner != null) ? _owner.getClanId() : 0;
	}
	
	@Override
	public boolean checkIfPvP(Playable target)
	{
		if (target == _owner)
			return false;
		
		return super.checkIfPvP(target);
	}
	
	public final TeamType getTeam()
	{
		return (_owner != null) ? _owner.getTeam() : TeamType.NONE;
	}
	
	public final Player getOwner()
	{
		return _owner;
	}
	
	public void setOwner(Player newOwner)
	{
		_owner = newOwner;
	}
	
	public boolean isMountable()
	{
		return false;
	}
	
	public final int getNpcId()
	{
		return getTemplate().getNpcId();
	}
	
	public int getSoulShotsPerHit()
	{
		return getTemplate().getSsCount();
	}
	
	public int getSpiritShotsPerHit()
	{
		return getTemplate().getSpsCount();
	}
	
	public int getAttackRange()
	{
		return 36;
	}
	
	public int getControlItemId()
	{
		return 0;
	}
	
	public Weapon getActiveWeapon()
	{
		return null;
	}
	
	public void store()
	{
	}
	
	public int getWeapon()
	{
		return 0;
	}
	
	public int getArmor()
	{
		return 0;
	}
	
	public void deleteMe(Player owner)
	{
		doUnsummon(owner);
	}
	
	public void unSummon(Player owner)
	{
		if (!isVisible() || isDead())
			return;
		
		doUnsummon(owner);
	}
	
	private void doUnsummon(Player owner)
	{
		abortAll(true);
		
		getStatus().stopHpMpRegeneration();
		stopAllEffects();
		store();
		
		owner.setSummon(null);
		owner.sendPacket(new PetDelete(getSummonType(), getObjectId()));
		
		decayMe();
		
		_owner.stopSkillEffects(CONTRACT_PAYMENT);
		
		_owner.disableBeastShots();
		
		super.deleteMe();
	}
	
	public void updateAndBroadcastStatusAndInfos(int val)
	{
		sendPacket(new PetInfo(this, val));
		
		updateEffectIcons(true);
		
		updateAndBroadcastStatus(val);
	}
	
	public void sendPetInfosToOwner()
	{
		sendPacket(new PetInfo(this, 2));
		
		updateEffectIcons(true);
	}
	
	public void updateAndBroadcastStatus(int val)
	{
		sendPacket(new PetStatusUpdate(this));
		
		if (isVisible())
			forEachKnownType(Player.class, player -> player != _owner, player -> player.sendPacket(new SummonInfo(this, player, val)));
	}
}
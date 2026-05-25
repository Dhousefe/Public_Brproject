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
package ext.mods.fakeplayer;

import ext.mods.commons.math.MathUtil;
import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.data.manager.AntiFeedManager;
import ext.mods.gameserver.data.manager.CastleManager;
import ext.mods.gameserver.data.manager.SevenSignsManager;
import ext.mods.gameserver.enums.CabalType;
import ext.mods.gameserver.enums.SealType;
import ext.mods.gameserver.enums.SiegeSide;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.enums.items.WeaponType;
import ext.mods.gameserver.enums.skills.SkillTargetType;
import ext.mods.gameserver.enums.skills.SkillType;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Attackable;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.container.player.Appearance;
import ext.mods.gameserver.model.actor.instance.Door;
import ext.mods.gameserver.model.actor.instance.Monster;
import ext.mods.gameserver.model.actor.template.PlayerTemplate;
import ext.mods.gameserver.model.entity.events.capturetheflag.CTFEvent;
import ext.mods.gameserver.model.entity.events.deathmatch.DMEvent;
import ext.mods.gameserver.model.entity.events.lastman.LMEvent;
import ext.mods.gameserver.model.entity.events.teamvsteam.TvTEvent;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.residence.castle.Siege;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.network.serverpackets.TeleportToLocation;
import ext.mods.gameserver.skills.AbstractEffect;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.taskmanager.WaterTaskManager;

import ext.mods.fakeplayer.ai.FakePlayerAI;
import ext.mods.fakeplayer.helper.FakePlayerHelpers;
import ext.mods.playergod.PlayerGodManager;

public class FakePlayer extends Player
{
	private FakePlayerAI _ai;
	private Location _saveLoc;
	
	public FakePlayer(int objectId, PlayerTemplate template, String accountName, Appearance app)
	{
		super(objectId, template, accountName, app);
	}
	
	public Location getFakeSaveLocation()
	{
		return _saveLoc;
	}
	
	public void setFakeSaveLocation(Location loc)
	{
		_saveLoc = loc;
	}
	
	public Location isFakeLocationRandom()
	{
		
		int dx = Rnd.get(-200, 200);
		int dy = Rnd.get(-200, 200);
		
		return new Location(getX() + dx, getY() + dy, getZ());
	}
	
	public Location isFakePostion()
	{
		return new Location(getX(), getY(), getZ());
	}
	
	public void teleportToLocation(int x, int y, int z, int randomOffset)
	{
		getMove().stop();
		abortAll(true);
		
		getAI().tryToIdle();
		if (randomOffset > 0)
		{
			x += Rnd.get(-randomOffset, randomOffset);
			y += Rnd.get(-randomOffset, randomOffset);
		}
		z += 5;
		broadcastPacket(new TeleportToLocation(this, x, y, z, true));
		decayMe();
		setXYZ(x, y, z);
		onTeleported();
		spawnMe();
		revalidateZone(true);
	}
	
	public boolean checkUseMagicConditions(L2Skill skill, boolean forceUse, boolean dontMove)
	{
		if (skill == null)
			return false;
		
		if (isDead() || isOutOfControl())
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		if (isSkillDisabled(skill))
			return false;
		
		SkillType sklType = skill.getSkillType();
		
		if (isFishing() && (sklType != SkillType.PUMPING && sklType != SkillType.REELING && sklType != SkillType.FISHING))
		{
			return false;
		}
		
		if (isInObserverMode())
		{
			getCast().canAbortCast();
			return false;
		}
		
		if (isSitting())
		{
			if (skill.isToggle())
			{
				AbstractEffect effect = getFirstEffect(skill.getId());
				if (effect != null)
				{
					effect.exit();
					return false;
				}
			}
			return false;
		}
		
		if (skill.isToggle())
		{
			AbstractEffect effect = getFirstEffect(skill.getId());
			
			if (effect != null)
			{
				if (skill.getId() != 60)
					effect.exit();
				
				sendPacket(ActionFailed.STATIC_PACKET);
				return false;
			}
		}
		
		if (isFakeDeath())
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		WorldObject target = null;
		SkillTargetType sklTargetType = skill.getTargetType();
		
		if (sklTargetType == SkillTargetType.GROUND)
		{
			LOGGER.info("WorldPosition is null for skill: " + skill.getName() + ", player: " + getName() + ".");
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		switch (sklTargetType)
		{
			case AURA:
			case FRONT_AURA:
			case BEHIND_AURA:
			case AURA_UNDEAD:
			case PARTY:
			case ALLY:
			case CLAN:
			case GROUND:
			case SELF:
			case CORPSE_ALLY:
			case AREA_SUMMON:
				target = this;
				break;
			case OWNER_PET:
			case SUMMON:
				target = getSummon();
				break;
			default:
				target = getTarget();
				break;
		}
		
		if (target == null)
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		if (target instanceof Door)
		{
			if (!((Door) target).isAttackableBy(this) || (((Door) target).isUnlockable() && skill.getSkillType() != SkillType.UNLOCK))
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				return false;
			}
		}
		
		if (isInDuel())
		{
			if (target instanceof Playable)
			{
				Player cha = target.getActingPlayer();
				if (cha.getDuelId() != getDuelId())
				{
					sendPacket(ActionFailed.STATIC_PACKET);
					return false;
				}
			}
		}
		
		if (skill.isSiegeSummonSkill())
		{
			final Siege siege = CastleManager.getInstance().getActiveSiege(this);
			if (siege == null || !siege.checkSide(getClan(), SiegeSide.ATTACKER) || (isInsideZone(ZoneId.CASTLE)))
			{
				sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NOT_CALL_PET_FROM_THIS_LOCATION));
				return false;
			}
			
			if (SevenSignsManager.getInstance().isSealValidationPeriod() && SevenSignsManager.getInstance().getSealOwner(SealType.STRIFE) == CabalType.DAWN && SevenSignsManager.getInstance().getPlayerCabal(getObjectId()) == CabalType.DUSK)
			{
				sendPacket(SystemMessageId.SEAL_OF_STRIFE_FORBIDS_SUMMONING);
				return false;
			}
		}
		
		if (!skill.checkCondition(this, (Creature) target, false))
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		if (skill.isOffensive())
		{
			if (isInsideZone(ZoneId.PEACE))
			{
				sendPacket(SystemMessageId.TARGET_IN_PEACEZONE);
				sendPacket(ActionFailed.STATIC_PACKET);
				return false;
			}
			
			if (isInOlympiadMode() && !isOlympiadStart())
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				return false;
			}
			if (!target.isAttackableBy(this) && !getAccessLevel().canGiveDamage())
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				return false;
			}
			
			if (!target.isAttackableBy(this) && !forceUse)
			{
				switch (sklTargetType)
				{
					case AURA:
					case FRONT_AURA:
					case BEHIND_AURA:
					case AURA_UNDEAD:
					case CLAN:
					case ALLY:
					case PARTY:
					case SELF:
					case GROUND:
					case CORPSE_ALLY:
					case AREA_SUMMON:
						break;
					default:
						sendPacket(ActionFailed.STATIC_PACKET);
						return false;
				}
			}
			
			if (dontMove)
			{
				
				if (skill.getCastRange() > 0 && !isIn3DRadius(target, (int) (skill.getCastRange() + getCollisionRadius())))
				{
					sendPacket(SystemMessageId.TARGET_TOO_FAR);
					
					sendPacket(ActionFailed.STATIC_PACKET);
					return false;
				}
			}
			
		}
		
		if (!skill.isOffensive() && target instanceof Monster && !forceUse)
		{
			switch (sklTargetType)
			{
				case OWNER_PET:
				case SUMMON:
				case AURA:
				case FRONT_AURA:
				case BEHIND_AURA:
				case AURA_UNDEAD:
				case CLAN:
				case SELF:
				case CORPSE_ALLY:
				case PARTY:
				case ALLY:
				case CORPSE_MOB:
				case AREA_CORPSE_MOB:
				case GROUND:
					break;
				default:
				{
					switch (sklType)
					{
						case BEAST_FEED:
						case DELUXE_KEY_UNLOCK:
						case UNLOCK:
							break;
						default:
							sendPacket(ActionFailed.STATIC_PACKET);
							return false;
					}
					break;
				}
			}
		}
		
		if (sklType == SkillType.SPOIL)
		{
			if (!(target instanceof Monster))
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				return false;
			}
		}
		
		if (sklType == SkillType.SWEEP && target instanceof Attackable)
		{
			if (((Attackable) target).isDead())
			{
				return false;
			}
		}
		
		if (sklType == SkillType.DRAIN_SOUL)
		{
			if (!(target instanceof Monster))
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				return false;
			}
		}
		
		switch (sklTargetType)
		{
			case PARTY:
			case ALLY:
			case CLAN:
			case AURA:
			case FRONT_AURA:
			case BEHIND_AURA:
			case AURA_UNDEAD:
			case GROUND:
			case SELF:
			case CORPSE_ALLY:
			case AREA_SUMMON:
				break;
			default:
				if (!getAccessLevel().canGiveDamage())
				{
					sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
					
					sendPacket(ActionFailed.STATIC_PACKET);
					return false;
				}
		}
		
		if (sklTargetType == SkillTargetType.HOLY)
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		return true;
	}
	
	public FakePlayerAI getAi()
	{
		return _ai;
	}
	
	public void setAi(FakePlayerAI _fakeAi)
	{
		_ai = _fakeAi;
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		if (!super.doDie(killer))
			return false;
		
		if (isMounted())
			stopFeed();
		
		clearCharges();
		
		synchronized (this)
		{
			if (isFakeDeath())
				stopFakeDeath(true);
		}
		
		if (killer != null)
		{
			final Player pk = killer.getActingPlayer();
			
			if (pk != null)
			{
				CTFEvent.getInstance().onKill(killer, this);
				DMEvent.getInstance().onKill(killer, this);
				LMEvent.getInstance().onKill(killer, this);
				TvTEvent.getInstance().onKill(killer, this);
				
				PlayerGodManager.getInstance().onKill(pk);
				
			}
			setExpBeforeDeath(0);
		}
		
		forEachKnownType(Creature.class, creature -> creature.getFusionSkill() != null && creature.getFusionSkill().getTarget() == this, creature -> creature.getCast().stop());
		
		if (getFusionSkill() != null)
			getCast().stop();
		
		calculateDeathPenaltyBuffLevel(killer);
		
		if (isPhoenixBlessed())
			reviveRequest(this, null, false);
		
		WaterTaskManager.getInstance().remove(this);
		
		disableBeastShots();
		
		AntiFeedManager.getInstance().setLastDeathTime(getObjectId());
		
		return true;
	}
	
	public void assignDefaultAI()
	{
		try
		{
			setAi(FakePlayerHelpers.getAIbyClassId(getClassId()).getConstructor(FakePlayer.class).newInstance(this));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void forceAutoAttack(Creature creature)
	{
		if (getTarget() == null || isInsideZone(ZoneId.PEACE) || isConfused())
			return;
		
		Creature target = (Creature) getTarget();
		
		if (isInOlympiadMode() && target instanceof Playable)
		{
			Player playerTarget = target.getActingPlayer();
			if (playerTarget == null || (playerTarget.isInOlympiadMode() && (!isOlympiadStart() || getOlympiadGameId() != playerTarget.getOlympiadGameId())))
				return;
		}
		
		if (!target.isAttackableBy(creature) && !getAccessLevel().canGiveDamage())
			return;
		
		if (!GeoEngine.getInstance().canSeeTarget(this, target))
		{
			return;
		}
		
		if (!GeoEngine.getInstance().canMoveToTarget(getX(), getY(), getZ(), target.getX(), target.getY(), target.getZ()))
		{
			return;
		}
		
		if (isArcher() && MathUtil.calculateDistance(this, target, false) < getStatus().getPhysicalAttackRange() * 0.5)
		{
			int dx = getX() - target.getX();
			int dy = getY() - target.getY();
			double length = Math.sqrt(dx * dx + dy * dy);
			
			if (length > 0)
			{
				int kiteX = getX() + (int) (dx / length * 300);
				int kiteY = getY() + (int) (dy / length * 300);
				int kiteZ = GeoEngine.getInstance().getHeight(kiteX, kiteY, getZ());
				
				if (GeoEngine.getInstance().canMoveToTarget(getX(), getY(), getZ(), kiteX, kiteY, kiteZ))
				{
					getAI().tryToMoveTo(new Location(kiteX, kiteY, kiteZ), null);
					return;
				}
			}
		}
		
		if (isDagger() && MathUtil.calculateDistance(this, target, false) < getStatus().getPhysicalAttackRange() + 50 && Rnd.get(100) < 40)
		{
			double angle = Rnd.get(0, 360);
			double radians = Math.toRadians(angle);
			int distance = 100;
			
			int moveX = target.getX() + (int) (Math.cos(radians) * distance);
			int moveY = target.getY() + (int) (Math.sin(radians) * distance);
			int moveZ = GeoEngine.getInstance().getHeight(moveX, moveY, target.getZ());
			
			if (GeoEngine.getInstance().canMoveToTarget(getX(), getY(), getZ(), moveX, moveY, moveZ))
			{
				getAI().tryToMoveTo(new Location(moveX, moveY, moveZ), null);
				
			}
		}
		
		getAI().tryToAttack(target);
	}
	
	public boolean isArcher()
	{
		return getActiveWeaponItem() != null && getAttackType() == WeaponType.BOW;
	}
	
	public boolean isDagger()
	{
		return getActiveWeaponItem() != null && getAttackType() == WeaponType.DAGGER;
	}
	
	public synchronized void despawnPlayer()
	{
		try
		{
			FakePlayerNames.releaseName(getName(), getAppearance().getSex());
			setOnlineStatus(false, true);
			abortAll(true);
			stopAllToggles();
			getInventory().destroyAllItems();
			
			for (AbstractEffect effect : getAllEffects())
			{
				if (effect.getSkill().isToggle())
				{
					effect.exit();
					continue;
				}
				
				switch (effect.getEffectType())
				{
					case SIGNET_GROUND:
					case SIGNET_EFFECT:
						effect.exit();
						break;
					default:
						break;
				}
			}
			
			decayMe();
			
			if (getSummon() != null)
				getSummon().unSummon(this);
			
			if (getActiveRequester() != null)
			{
				setActiveRequester(null);
				cancelActiveTrade();
			}
			
			World.getInstance().removePlayer(this);
			
		}
		catch (Exception e)
		{
			System.out.println("Exception on deleteMe()" + e.getMessage() + e);
		}
	}
	
	public void heal()
	{
		getStatus().setMaxCpHpMp();
	}
}

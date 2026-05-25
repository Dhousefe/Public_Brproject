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
package ext.mods.fakeplayer.ai;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ext.mods.commons.math.MathUtil;
import ext.mods.commons.pool.ThreadPool;
import ext.mods.commons.random.Rnd;
import ext.mods.fakeplayer.FakePlayer;
import ext.mods.fakeplayer.model.BotSkill;
import ext.mods.fakeplayer.model.HealingSpell;
import ext.mods.fakeplayer.model.OffensiveSpell;
import ext.mods.fakeplayer.model.SupportSpell;
import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.enums.items.ShotType;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.skills.L2Skill;

public abstract class CombatAI extends FakePlayerAI
{
	private long lastActionTime = System.currentTimeMillis();
	private final long INACTIVITY_TIMEOUT_MS = 60000;
	private static final int RETURN_X = 83400;
	private static final int RETURN_Y = 147943;
	private static final int RETURN_Z = -3400;
	
	private long lastPeaceActionTime = 0;
	private final long PEACE_ACTION_INTERVAL = 10000;
	private boolean isSitting = false;
	private Location waypoint = getCityWaypoint("Giran", _fakePlayer);
	
	public CombatAI(FakePlayer character)
	{
		super(character);
	}
	
	@Override
	public void thinkAndAct()
	{
		handleDeath();
		_fakePlayer.setRunning(true);
		if (colision())
		{
			return;
		}
		
		if (isPeaceZone())
		{
			performPeaceZoneBehavior();
			return;
		}
		
		if (isFighting() || _fakePlayer.isMoving())
		{
			lastActionTime = System.currentTimeMillis();
		}
		else if ((System.currentTimeMillis() - lastActionTime) > INACTIVITY_TIMEOUT_MS)
		{
			teleportToSafeZone();
			lastActionTime = System.currentTimeMillis();
		}
	}
	
	private boolean isPeaceZone()
	{
		if (_fakePlayer.isInsideZone(ZoneId.PEACE))
		{
			return true;
		}
		return false;
	}
	
	private boolean colision()
	{
		if (!_fakePlayer.isInsideZone(ZoneId.PEACE))
			return true;
		
	
		final double minSafeDistance = 120.0;
		
		final int fx = _fakePlayer.getX();
		final int fy = _fakePlayer.getY();
		

		for (Player player : _fakePlayer.getKnownType(Player.class))
		{
			if (player == null || !player.isVisible() || player.isDead())
				continue;
			
			double dx = fx - player.getX();
			double dy = fy - player.getY();
			double distance = Math.sqrt(dx * dx + dy * dy);
			
		
			if (distance < minSafeDistance)
			{
				Location newLoc = findFreeLocationNearby(_fakePlayer, minSafeDistance);
				if (newLoc != null)
				{
					_fakePlayer.getMove().maybeMoveToLocation(newLoc, 0, true, false);
				}
				break;
			}
		}
		
		return false;
	}
	
	private Location findFreeLocationNearby(FakePlayer fakePlayer, double radius)
	{
		final int tryLimit = 20;
		
		for (int i = 0; i < tryLimit; i++)
		{
			int offsetX = Rnd.get(-((int) radius), ((int) radius));
			int offsetY = Rnd.get(-((int) radius), ((int) radius));
			
			int newX = fakePlayer.getX() + offsetX;
			int newY = fakePlayer.getY() + offsetY;
			int newZ = fakePlayer.getZ();
			
			if (GeoEngine.getInstance().canMove(fakePlayer.getX(), fakePlayer.getY(), fakePlayer.getZ(), newX, newY, newZ, null))
			{
				return new Location(newX, newY, newZ);
			}
		}
		return null;
	}
	
	private void performPeaceZoneBehavior()
	{
		if ((System.currentTimeMillis() - lastPeaceActionTime) < PEACE_ACTION_INTERVAL)
			return;
		
		int action = Rnd.get(7);
		switch (action)
		{
			case 0:
				
				if (waypoint != null)
				{
					Location correctedLoc = GeoEngine.getInstance().getValidLocation(_fakePlayer.isFakePostion(), waypoint);
					
					if (correctedLoc != null && GeoEngine.getInstance().canMove(_fakePlayer.getX(), _fakePlayer.getY(), _fakePlayer.getZ(), correctedLoc.getX(), correctedLoc.getY(), correctedLoc.getZ(), null))
					{
						_fakePlayer.getMove().maybeMoveToLocation(correctedLoc, 75, true, false);
					}
				}
				
				break;
			
			case 1:
				Npc nearestNpc = getNearestNpc();
				if (nearestNpc != null && GeoEngine.getInstance().canMoveToTarget(_fakePlayer, nearestNpc))
				{
					Location locNpc = new Location(nearestNpc.getX(), nearestNpc.getY(), nearestNpc.getZ());
					
					if (GeoEngine.getInstance().getValidLocation(_fakePlayer.isFakePostion(), locNpc) != null)
						_fakePlayer.getMove().maybeMoveToLocation(locNpc, 75, true, false);
					
				}
				break;
			
			case 2:
				
				int dCx = Rnd.get(-_fakePlayer.getHeading(), _fakePlayer.getHeading());
				int dCy = Rnd.get(-_fakePlayer.getHeading(), _fakePlayer.getHeading());
				Location locMove = new Location(_fakePlayer.getX() + dCx, _fakePlayer.getY() + dCy, _fakePlayer.getZ());
				if (GeoEngine.getInstance().getValidLocation(_fakePlayer.isFakePostion(), locMove) != null)
					_fakePlayer.getMove().maybeMoveToLocation(locMove, 75, true, false);
				break;
			
			case 3:
				if (!isSitting)
				{
					_fakePlayer.sitDown();
					isSitting = true;
					
					ThreadPool.schedule(() ->
					{
						_fakePlayer.standUp();
						isSitting = false;
					}, 60000);
				}
				break;
			
			case 4:
				Npc teleportNpc = getNearestTeleportNpc();
				if (teleportNpc != null && GeoEngine.getInstance().canMoveToTarget(_fakePlayer, teleportNpc))
				{
					Location loc = new Location(teleportNpc.getX(), teleportNpc.getY(), teleportNpc.getZ());
					
					Location correctedLoc = GeoEngine.getInstance().getValidLocation(_fakePlayer.isFakePostion(), loc);
					
					if (correctedLoc != null && GeoEngine.getInstance().canMove(_fakePlayer.getX(), _fakePlayer.getY(), _fakePlayer.getZ(), correctedLoc.getX(), correctedLoc.getY(), correctedLoc.getZ(), null))
					{
						_fakePlayer.getMove().maybeMoveToLocation(correctedLoc, 75, true, false);
					}
					
				}
			case 5:
				Location savedLoc = _fakePlayer.getFakeSaveLocation();
				if (savedLoc != null)
				{
					_fakePlayer.teleportToLocation(savedLoc.getX(), savedLoc.getY(), savedLoc.getZ(), 75);
					if (GeoEngine.getInstance().getValidLocation(_fakePlayer.isFakePostion(), savedLoc) != null)
						_fakePlayer.getMove().maybeMoveToLocation(savedLoc, 75, true, false);
					
				}
				break;
			
			case 6:
				if (waypoint != null)
				{
					Location correctedLoc = GeoEngine.getInstance().getValidLocation(_fakePlayer.isFakePostion(), waypoint);
					
					if (correctedLoc != null && GeoEngine.getInstance().canMove(_fakePlayer.getX(), _fakePlayer.getY(), _fakePlayer.getZ(), correctedLoc.getX(), correctedLoc.getY(), correctedLoc.getZ(), null))
					{
						_fakePlayer.getMove().maybeMoveToLocation(correctedLoc, 75, true, false);
					}
				}
				break;
			
		}
		
		lastPeaceActionTime = System.currentTimeMillis();
	}
	
	private Npc getNearestNpc()
	{
		return _fakePlayer.getKnownType(Npc.class).stream().sorted(Comparator.comparingDouble(n -> MathUtil.calculateDistance(_fakePlayer, n, false))).findFirst().orElse(null);
	}
	
	private Npc getNearestTeleportNpc()
	{
		return _fakePlayer.getKnownType(Npc.class).stream().filter(n -> n.getTemplate().getType().equalsIgnoreCase("Gatekeeper")).sorted(Comparator.comparingDouble(n -> MathUtil.calculateDistance(_fakePlayer, n, false))).findFirst().orElse(null);
	}
	
	protected void tryAttackingUsingFighterOffensiveSkill()
	{
		if (_fakePlayer.getTarget() == null || !(_fakePlayer.getTarget() instanceof Creature))
			return;
		
		_fakePlayer.forceAutoAttack((Creature) _fakePlayer.getTarget());
		
		if (Rnd.nextDouble() > changeOfUsingSkill())
			return;
		
		L2Skill skill = getBestAvailableFighterSkill();
		
		if (skill != null)
			castSpell(skill);
	}
	
	protected L2Skill getBestAvailableFighterSkill()
	{
		if (getOffensiveSpells().isEmpty())
			return null;
		
		return getOffensiveSpells().stream().sorted(Comparator.comparingInt(BotSkill::getPriority)).map(spell -> _fakePlayer.getSkill(spell.getSkillId())).filter(Objects::nonNull).filter(skill -> _fakePlayer.checkUseMagicConditions(skill, true, false)).findFirst().orElse(null);
	}
	
	protected void tryAttackingUsingMageOffensiveSkill()
	{
		if (_fakePlayer.getTarget() == null || _fakePlayer.getCast().isCastingNow())
			return;
		
		BotSkill selectedSkill = getBestAvailableSpellForTarget();
		
		if (selectedSkill == null)
			return;
		
		L2Skill skill = _fakePlayer.getSkill(selectedSkill.getSkillId());
		if (skill != null)
			castSpell(skill);
	}
	
	protected BotSkill getBestAvailableSpellForTarget()
	{
		List<OffensiveSpell> sortedSpells = getOffensiveSpells().stream().sorted(Comparator.comparingInt(BotSkill::getPriority)).toList();
		
		for (BotSkill botSkill : sortedSpells)
		{
			L2Skill skill = _fakePlayer.getSkill(botSkill.getSkillId());
			if (skill == null)
				continue;
			
			if (skill.getCastRange() > 0 && !GeoEngine.getInstance().canSeeTarget(_fakePlayer, _fakePlayer.getTarget()))
				continue;
			
			if (_fakePlayer.checkUseMagicConditions(skill, true, false))
				return botSkill;
		}
		return null;
	}
	
	public HealingSpell getBestAvailableHealingSpell()
	{
		if (getHealingSpells().isEmpty() || _fakePlayer == null || _fakePlayer.getTarget() == null)
			return null;
		
		return getHealingSpells().stream().sorted(Comparator.comparingInt(BotSkill::getPriority)).filter(this::canCastSkill).map(skill -> skill).findFirst().orElse(null);
	}
	
	private boolean canCastSkill(BotSkill botSkill)
	{
		L2Skill skill = _fakePlayer.getSkill(botSkill.getSkillId());
		if (skill == null)
			return false;
		
		if (skill.getCastRange() > 0 && !GeoEngine.getInstance().canSeeTarget(_fakePlayer, _fakePlayer.getTarget()))
			return false;
		
		return _fakePlayer.checkUseMagicConditions(skill, true, false);
	}
	
	protected void selfSupportBuffs()
	{
		List<Integer> activeEffects = Arrays.stream(_fakePlayer.getAllEffects()).map(x -> x.getSkill().getId()).collect(Collectors.toList());
		
		for (SupportSpell selfBuff : getSelfSupportSpells())
		{
			if (activeEffects.contains(selfBuff.getSkillId()))
				continue;
			
			L2Skill skill = SkillTable.getInstance().getInfo(selfBuff.getSkillId(), _fakePlayer.getSkillLevel(selfBuff.getSkillId()));
			
			if (!_fakePlayer.checkUseMagicConditions(skill, true, false))
				continue;
			
			switch (selfBuff.getCondition())
			{
				case LESSHPPERCENT:
					if (Math.round(100.0 / _fakePlayer.getStatus().getMaxHp() * _fakePlayer.getStatus().getHp()) <= selfBuff.getConditionValue())
					{
						castSelfSpell(skill);
					}
					break;
				case MISSINGCP:
					if (getMissingHealth() >= selfBuff.getConditionValue())
					{
						castSelfSpell(skill);
					}
					break;
				case NONE:
					castSelfSpell(skill);
				default:
					break;
			}
			
		}
	}
	
	private double getMissingHealth()
	{
		return _fakePlayer.getStatus().getMaxCp() - _fakePlayer.getStatus().getCp();
	}
	
	protected double changeOfUsingSkill()
	{
		return 1.0;
	}
	
	protected int getShotId()
	{
		int playerLevel = _fakePlayer.getStatus().getLevel();
		if (playerLevel < 20)
			return getShotType() == ShotType.SOULSHOT ? 1835 : 3947;
		if (playerLevel >= 20 && playerLevel < 40)
			return getShotType() == ShotType.SOULSHOT ? 1463 : 3948;
		if (playerLevel >= 40 && playerLevel < 52)
			return getShotType() == ShotType.SOULSHOT ? 1464 : 3949;
		if (playerLevel >= 52 && playerLevel < 61)
			return getShotType() == ShotType.SOULSHOT ? 1465 : 3950;
		if (playerLevel >= 61 && playerLevel < 76)
			return getShotType() == ShotType.SOULSHOT ? 1466 : 3951;
		if (playerLevel >= 76)
			return getShotType() == ShotType.SOULSHOT ? 1467 : 3952;
		
		return 0;
	}
	
	protected int getArrowId()
	{
		int playerLevel = _fakePlayer.getStatus().getLevel();
		if (playerLevel < 20)
			return 17;
		if (playerLevel >= 20 && playerLevel < 40)
			return 1341;
		if (playerLevel >= 40 && playerLevel < 52)
			return 1342;
		if (playerLevel >= 52 && playerLevel < 61)
			return 1343;
		if (playerLevel >= 61 && playerLevel < 76)
			return 1344;
		if (playerLevel >= 76)
			return 1345;
			
		return 0;
	}
	
	protected void handleShots()
	{
		if (_fakePlayer.getInventory().getItemByItemId(getShotId()) != null)
		{
			if (_fakePlayer.getInventory().getItemByItemId(getShotId()).getCount() <= 20)
			{
				_fakePlayer.getInventory().addItem(getShotId(), 500);
			}
		}
		else
		{
			_fakePlayer.getInventory().addItem(getShotId(), 500);
		}
		
		if (_fakePlayer.getAutoSoulShot().isEmpty())
		{
			_fakePlayer.addAutoSoulShot(getShotId());
			
			final ItemInstance item = _fakePlayer.getInventory().getItemByItemId(getShotId());
			if (item == null)
				return;
			
			if (_fakePlayer.getActiveWeaponInstance() != null && item.getItem().getCrystalType() == _fakePlayer.getActiveWeaponItem().getCrystalType())
				_fakePlayer.rechargeShots(true, true);
			
			if (_fakePlayer.getSummon() != null)
				_fakePlayer.getSummon().rechargeShots(true, true);
		}
	}
	
	private boolean isFighting()
	{
		if (_fakePlayer.getAttack().isAttackingNow() || _fakePlayer.getAttack().isAttackingNow())
			return true;
		
		if (_fakePlayer.getCast().isCastingNow())
			return true;
		
		return false;
	}
	
	private void teleportToSafeZone()
	{
		_fakePlayer.setFakeSaveLocation(_fakePlayer.isFakeLocationRandom());
		_fakePlayer.teleportToLocation(RETURN_X, RETURN_Y, RETURN_Z, 75);
		System.out.println("⚠️ FakePlayer teleportado por inatividade: " + _fakePlayer.getName());
		
	}
	
	protected abstract ShotType getShotType();
	
	protected abstract List<OffensiveSpell> getOffensiveSpells();
	
	protected abstract List<SupportSpell> getSelfSupportSpells();
	
	protected abstract List<HealingSpell> getHealingSpells();
}

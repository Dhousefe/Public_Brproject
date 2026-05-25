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
import java.util.Map;
import java.util.stream.Collectors;

import ext.mods.commons.math.MathUtil;
import ext.mods.commons.random.Rnd;
import ext.mods.fakeplayer.FakePlayer;
import ext.mods.fakeplayer.holder.WalkerHolder;
import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.enums.skills.EffectType;
import ext.mods.gameserver.enums.skills.SkillTargetType;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Attackable;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.skills.AbstractEffect;
import ext.mods.gameserver.skills.L2Skill;

public abstract class FakePlayerAI
{
	protected final FakePlayer _fakePlayer;
	protected boolean _isBusyThinking = false;
	protected int iterationsOnDeath = 0;
	private final int toVillageIterationsOnDeath = 10;
	
	public FakePlayerAI(FakePlayer character)
	{
		_fakePlayer = character;
		setup();
	}
	
	public void setup()
	{
		_fakePlayer.setRunning(true);
	}
	
	protected void applyDefaultBuffs()
	{
		for (int[] buff : getBuffs())
		{
			try
			{
				Map<Integer, AbstractEffect> activeEffects = Arrays.stream(_fakePlayer.getAllEffects()).filter(x -> x.getEffectType() == EffectType.BUFF).collect(Collectors.toMap(x -> x.getSkill().getId(), x -> x));
				
				if (!activeEffects.containsKey(buff[0]))
					SkillTable.getInstance().getInfo(buff[0], buff[1]).getEffects(_fakePlayer, _fakePlayer);
				else
				{
					if ((activeEffects.get(buff[0]).getPeriod() - activeEffects.get(buff[0]).getTime()) <= 20)
					{
						SkillTable.getInstance().getInfo(buff[0], buff[1]).getEffects(_fakePlayer, _fakePlayer);
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void castSpell(L2Skill skill)
	{
		if (!_fakePlayer.getCast().isCastingNow())
		{
			if (skill.getTargetType() == SkillTargetType.GROUND)
			{
				_fakePlayer.getAI().tryToMoveTo(new Location(_fakePlayer.getTarget().getX() + (int) _fakePlayer.getTemplate().getCollisionRadius(), _fakePlayer.getTarget().getY() + (int) _fakePlayer.getTemplate().getCollisionRadius(), _fakePlayer.getTarget().getZ()), null);
			}
			else
			{
				if (checkTargetLost(_fakePlayer.getTarget()))
				{
					if (skill.isOffensive() && _fakePlayer.getTarget() != null)
						_fakePlayer.setTarget(null);
					
					_fakePlayer.getCast().stop();
					return;
				}
				
				if (skill.getHitTime() > 50 && !skill.isSimultaneousCast())
					_fakePlayer.getMove().stop();
				
				_fakePlayer.getAI().tryToCast((Creature) _fakePlayer.getTarget(), skill);
			}
		}
	}
	
	protected boolean checkTargetLost(WorldObject target)
	{
		if (target instanceof Player)
		{
			final Player victim = (Player) target;
			if (victim.isFakeDeath())
			{
				victim.stopFakeDeath(true);
				return false;
			}
		}
		
		if (target == null)
		{
			_fakePlayer.getAI().tryToIdle();
			
			return true;
		}
		return false;
	}
	
	protected void tryTargetNearestCreatureByTypeInRadius(Class<? extends Creature> creatureClass, int radius)
	{
		
		if (_fakePlayer.getTarget() != null)
		{
			Creature currentTarget = (Creature) _fakePlayer.getTarget();
			
			if (currentTarget.isDead() || !GeoEngine.getInstance().canSeeTarget(_fakePlayer, currentTarget) || !GeoEngine.getInstance().canMoveToTarget(_fakePlayer.getX(), _fakePlayer.getY(), _fakePlayer.getZ(), currentTarget.getX(), currentTarget.getY(), currentTarget.getZ()))
			{
				_fakePlayer.setTarget(null);
			}
			else
			{
				return;
			}
		}
		
		List<Creature> potentialTargets = _fakePlayer.getKnownTypeInRadius(creatureClass, radius).stream().filter(creature -> !creature.isDead()).filter(creature -> (creature instanceof Player || creature instanceof Attackable)).filter(creature ->
		{
			if (!GeoEngine.getInstance().canSeeTarget(_fakePlayer, creature))
				return false;
			
			if (!GeoEngine.getInstance().canMoveToTarget(_fakePlayer.getX(), _fakePlayer.getY(), _fakePlayer.getZ(), creature.getX(), creature.getY(), creature.getZ()))
				return false;
			
			if (creature instanceof Player playerTarget && !isAttackablePlayer(playerTarget))
				return false;
			
			return true;
		}).sorted(Comparator.comparingDouble(c -> MathUtil.calculateDistance(_fakePlayer, c, false))).collect(Collectors.toList());
		
		for (Creature target : potentialTargets)
		{
			boolean isTargetedByAlly = false;
			boolean isTargetedByEnemy = false;
			
			for (Player other : World.getInstance().getPlayers())
			{
				if (other == null || other == _fakePlayer || other.getTarget() == null)
					continue;
				
				if (other.getTarget() == target)
				{
					if (other instanceof FakePlayer otherFake && isSamePartyOrClan(_fakePlayer, otherFake))
						isTargetedByAlly = true;
					else
					{
						isTargetedByEnemy = true;
						break;
					}
				}
			}
			
			if (!isTargetedByEnemy || isTargetedByAlly)
			{
				_fakePlayer.setTarget(target);
				return;
			}
		}
	}
	
	public static boolean isAttackablePlayer(Player player)
	{
		return player.getPvpFlag() > 0 || player.getKarma() > 0 || player.getClanId() > 0 || player.isInsideZone(ZoneId.PVP);
	}
	
	private static boolean isSamePartyOrClan(FakePlayer player1, FakePlayer player2)
	{
		if (player1 == null || player2 == null)
			return false;
		
		if (player1.getParty() != null && player1.getParty().equals(player2.getParty()))
			return true;
		
		if (player1.getClan() != null && player1.getClan().equals(player2.getClan()))
			return true;
		
		return false;
	}
	
	protected void handleDeath()
	{
		if (_fakePlayer.isDead())
		{
			if (iterationsOnDeath >= toVillageIterationsOnDeath)
			{
				toVillageOnDeath();
			}
			iterationsOnDeath++;
			return;
		}
		
		iterationsOnDeath = 0;
	}
	
	protected void toVillageOnDeath()
	{
		if (_fakePlayer.isDead())
			_fakePlayer.doRevive();
		
	}
	
	protected void castSelfSpell(L2Skill skill)
	{
		if (!_fakePlayer.getCast().isCastingNow() && !_fakePlayer.isSkillDisabled(skill))
		{
			_fakePlayer.getAI().tryToCast(_fakePlayer, skill);
		}
	}
	
	protected Location getCityWaypoint(String townName, Player fakePlayer)
	{
		List<Location> waypoints = WalkerHolder.getCityWaypoints(townName);
		if (waypoints == null || waypoints.isEmpty())
		{
			return null;
		}
		
		List<Location> validWaypoints = waypoints.stream().filter(loc -> GeoEngine.getInstance().canMove(fakePlayer.getX(), fakePlayer.getY(), fakePlayer.getZ(), loc.getX(), loc.getY(), loc.getZ(), null)).collect(Collectors.toList());
		
		return !validWaypoints.isEmpty() ? validWaypoints.get(Rnd.get(validWaypoints.size())) : waypoints.stream().min(Comparator.comparingDouble(loc -> MathUtil.calculateDistance(fakePlayer, loc, false))).orElse(null);
	}
	
	public void setBusyThinking(boolean thinking)
	{
		_isBusyThinking = thinking;
	}
	
	public boolean isBusyThinking()
	{
		return _isBusyThinking;
	}
	
	public abstract void thinkAndAct();
	
	protected abstract int[][] getBuffs();
}

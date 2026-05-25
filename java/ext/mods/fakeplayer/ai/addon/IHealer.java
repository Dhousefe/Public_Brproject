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
package ext.mods.fakeplayer.ai.addon;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;

import ext.mods.fakeplayer.FakePlayer;
import ext.mods.fakeplayer.ai.CombatAI;
import ext.mods.fakeplayer.model.HealingSpell;

public interface IHealer
{
	default void tryTargetingLowestHpAllyInRadius(FakePlayer healer, Class<? extends Creature> creatureClass, int radius)
	{
		if (healer.getTarget() instanceof Creature currentTarget)
		{
			if (!currentTarget.isDead() && currentTarget instanceof FakePlayer fake && isSamePartyOrClan(healer, fake))
				return;
			
			healer.setTarget(null);
		}
		
		List<FakePlayer> potentialTargets = healer.getKnownTypeInRadius(creatureClass, radius).stream().filter(creature -> !creature.isDead()).filter(creature -> creature instanceof FakePlayer).map(creature -> (FakePlayer) creature).filter(ally -> isSamePartyOrClan(healer, ally)).filter(ally -> GeoEngine.getInstance().canSeeTarget(healer, ally)).filter(ally -> GeoEngine.getInstance().canMoveToTarget(healer.getX(), healer.getY(), healer.getZ(), ally.getX(), ally.getY(), ally.getZ())).sorted(Comparator.comparingDouble(c -> c.getStatus().getHp())).collect(Collectors.toList());
		
		if (!healer.isDead() && healer.getStatus().getHpRatio() < 0.8)
			potentialTargets.add(healer);
		
		if (potentialTargets.isEmpty() && healer.getStatus().getHpRatio() < 0.8)
		{
		    healer.setTarget(healer);
		    return;
		}
		
		for (FakePlayer target : potentialTargets)
		{
			boolean alreadyTargetedByAllyHealer = World.getInstance().getPlayers().stream().filter(p -> p instanceof FakePlayer && p != healer).map(p -> (FakePlayer) p).filter(p -> isSamePartyOrClan(healer, p)).anyMatch(p -> p.getTarget() == target);
			
			if (!alreadyTargetedByAllyHealer)
			{
				healer.setTarget(target);
				return;
			}
		}
	}
	
	default void tryHealingTarget(FakePlayer player)
	{
		
		if (player.getTarget() != null && player.getTarget() instanceof Creature)
		{
			Creature target = (Creature) player.getTarget();
			if (target != null)
			{
				if (player.getAi() instanceof CombatAI)
				{
					HealingSpell skill = ((CombatAI) player.getAi()).getBestAvailableHealingSpell();
					
					if (skill != null)
					{
						
						switch (skill.getCondition())
						{
							case LESSHPPERCENT:
								double currentHpPercentage = (target.getStatus().getHp() / target.getStatus().getMaxHp()) * 100.0;

								if (currentHpPercentage <= skill.getConditionValue())
								{
									player.getAi().castSpell(player.getSkill(skill.getSkillId()));
								}
								break;
							default:
								break;
						}
					}
					
				}
			}
			
		}
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
}

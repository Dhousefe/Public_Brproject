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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.RaidBoss;

import ext.mods.commons.random.Rnd;

import ext.mods.Config;
import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.manager.SevenSignsManager;
import ext.mods.gameserver.enums.CabalType;
import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.enums.PeriodType;
import ext.mods.gameserver.enums.SealType;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.Summon;
import ext.mods.gameserver.network.serverpackets.PlaySound;
import ext.mods.gameserver.scripting.script.ai.individual.Monster.MonsterAI;
import ext.mods.gameserver.skills.L2Skill;

public class RaidBossStandard extends MonsterAI
{
	public RaidBossStandard()
	{
		super("ai/individual/Monster/RaidBoss/RaidBossStandard");
	}
	
	public RaidBossStandard(String descr)
	{
		super(descr);
	}
	
	@Override
	public void onNoDesire(Npc npc)
	{
		npc.getAI().addDoNothingDesire(5, 5);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("1001"))
		{
			if (!npc.isInMyTerritory() && Rnd.nextBoolean() && npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
			{
				npc.abortAll(true);
				npc.removeAllAttackDesire();
				npc.teleportTo(npc.getSpawnLocation(), 0);
			}
			
			if (Rnd.get(5) < 1)
				npc.getAI().getAggroList().randomizeAttack();
		}
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Player player)
		{
			if (player.getMountType() == 1)
			{
				final L2Skill striderSlow = SkillTable.getInstance().getInfo(4258, 1);
				if (getAbnormalLevel(attacker, striderSlow) <= 0)
					npc.getAI().addCastDesire(attacker, striderSlow, 1000000);
			}
		}
		
		if (!Config.RAID_DISABLE_CURSE && attacker.getStatus().getLevel() > (npc.getStatus().getLevel() + 8))
		{
			final L2Skill raidCurse = SkillTable.getInstance().getInfo(4515, 1);
			if (getAbnormalLevel(attacker, raidCurse) == -1)
			{
				npc.getAI().addCastDesire(attacker, raidCurse, 1000000);
				npc.getAI().getAggroList().stopHate(attacker);
				return;
			}
		}
		
		if (!npc.isInsideZone(ZoneId.PEACE) && !Config.CATACOMBS_IN_ANY_PERIOD)
		{
			var SSQLoserTeleport = getNpcIntAIParam(npc, "SSQLoserTeleport");
			
			SealType sealType = null;
			if (SSQLoserTeleport == 1)
				sealType = SealType.AVARICE;
			else if (SSQLoserTeleport == 2)
				sealType = SealType.GNOSIS;
			
			if (SSQLoserTeleport != 0)
			{
				if (SSQLoserTeleport != 1 && SSQLoserTeleport != 2)
					LOGGER.info("An invalid value was entered in SSQLoserTeleport. Value = " + SSQLoserTeleport);
				else if (SevenSignsManager.getInstance().getCurrentPeriod() == PeriodType.SEAL_VALIDATION)
				{
					var i0 = SevenSignsManager.getInstance().getSealOwner(sealType);
					if (attacker instanceof Player player)
					{
						var cabal = SevenSignsManager.getInstance().getPlayerCabal(player.getObjectId());
						if (i0 == CabalType.NORMAL && cabal == CabalType.NORMAL)
						{
							npc.removeAttackDesire(player);
							player.teleportTo(getNpcIntAIParam(npc, "SSQTelPosX"), getNpcIntAIParam(npc, "SSQTelPosY"), getNpcIntAIParam(npc, "SSQTelPosZ"), 0);
							return;
						}
						else if (i0 == CabalType.DUSK && cabal != CabalType.DUSK)
						{
							npc.removeAttackDesire(player);
							player.teleportTo(getNpcIntAIParam(npc, "SSQTelPosX"), getNpcIntAIParam(npc, "SSQTelPosY"), getNpcIntAIParam(npc, "SSQTelPosZ"), 0);
							return;
						}
						else if (i0 == CabalType.DAWN && cabal != CabalType.DAWN)
						{
							npc.removeAttackDesire(player);
							player.teleportTo(getNpcIntAIParam(npc, "SSQTelPosX"), getNpcIntAIParam(npc, "SSQTelPosY"), getNpcIntAIParam(npc, "SSQTelPosZ"), 0);
							return;
						}
					}
					else if (attacker instanceof Summon summon)
					{
						Player owner = summon.getOwner();
						if (owner == null)
							return;
						
						var cabal = SevenSignsManager.getInstance().getPlayerCabal(owner.getObjectId());
						if (i0 == CabalType.NORMAL && cabal == CabalType.NORMAL)
						{
							npc.removeAttackDesire(summon);
							owner.teleportTo(getNpcIntAIParam(npc, "SSQTelPosX"), getNpcIntAIParam(npc, "SSQTelPosY"), getNpcIntAIParam(npc, "SSQTelPosZ"), 0);
							return;
						}
						else if (i0 == CabalType.DUSK && cabal != CabalType.DUSK)
						{
							npc.removeAttackDesire(summon);
							owner.teleportTo(getNpcIntAIParam(npc, "SSQTelPosX"), getNpcIntAIParam(npc, "SSQTelPosY"), getNpcIntAIParam(npc, "SSQTelPosZ"), 0);
							return;
						}
						else if (i0 == CabalType.DAWN && cabal != CabalType.DAWN)
						{
							npc.removeAttackDesire(summon);
							owner.teleportTo(getNpcIntAIParam(npc, "SSQTelPosX"), getNpcIntAIParam(npc, "SSQTelPosY"), getNpcIntAIParam(npc, "SSQTelPosZ"), 0);
							return;
						}
					}
				}
				else if (SevenSignsManager.getInstance().getCurrentPeriod() == PeriodType.COMPETITION)
				{
					var i0 = SevenSignsManager.getInstance().getSealOwner(sealType);
					if (attacker instanceof Player player)
					{
						var cabal = SevenSignsManager.getInstance().getPlayerCabal(player.getObjectId());
						if (i0 == CabalType.NORMAL && cabal == CabalType.NORMAL)
						{
							npc.removeAttackDesire(player);
							player.teleportTo(getNpcIntAIParam(npc, "SSQTelPosX"), getNpcIntAIParam(npc, "SSQTelPosY"), getNpcIntAIParam(npc, "SSQTelPosZ"), 0);
							return;
						}
					}
					else if (attacker instanceof Summon summon)
					{
						Player owner = summon.getOwner();
						if (owner == null)
							return;
						
						var cabal = SevenSignsManager.getInstance().getPlayerCabal(owner.getObjectId());
						if (i0 == CabalType.NORMAL && cabal == CabalType.NORMAL)
						{
							npc.removeAttackDesire(summon);
							owner.teleportTo(getNpcIntAIParam(npc, "SSQTelPosX"), getNpcIntAIParam(npc, "SSQTelPosY"), getNpcIntAIParam(npc, "SSQTelPosZ"), 0);
							return;
						}
					}
				}
			}
		}
		
		if (attacker instanceof Playable)
		{
			if (damage == 0)
				damage = 1;
			
			npc.getAI().addAttackDesire(attacker, ((1.0 * damage) / (npc.getStatus().getLevel() + 7)) * 20000);
		}
		
		if (npc.getMove().getGeoPathFailCount() > 10)
		{
			final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
			if (topDesireTarget != null && npc.distance2D(topDesireTarget) < 1000)
				npc.teleportTo(topDesireTarget.getPosition(), 0);
			else
			{
				npc.removeAllAttackDesire();
				
				if (attacker instanceof Playable)
				{
					if (damage == 0)
						damage = 1;
					
					npc.getAI().addAttackDesire(attacker, ((1.0 * damage) / (npc.getStatus().getLevel() + 7)) * 20000);
				}
				
				npc.teleportTo(attacker.getPosition(), 0);
			}
		}
		
		/*if (npc.isInsideZone(ZoneId.PEACE))
		{
			npc.teleportTo(npc.getSpawnLocation(), 0);
			npc.removeAllAttackDesire();
		}*/
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (!npc.isInsideZone(ZoneId.PEACE) && !Config.CATACOMBS_IN_ANY_PERIOD)
		{
			var SSQLoserTeleport = getNpcIntAIParam(npc, "SSQLoserTeleport");
			
			SealType sealType = null;
			if (SSQLoserTeleport == 1)
				sealType = SealType.AVARICE;
			else if (SSQLoserTeleport == 2)
				sealType = SealType.GNOSIS;
			
			if (SSQLoserTeleport != 0)
			{
				if (SSQLoserTeleport != 1 && SSQLoserTeleport != 2)
					LOGGER.info("An invalid value was entered in SSQLoserTeleport. Value = " + SSQLoserTeleport);
				else if (SevenSignsManager.getInstance().getCurrentPeriod() == PeriodType.SEAL_VALIDATION)
				{
					var i0 = SevenSignsManager.getInstance().getSealOwner(sealType);
					if (creature instanceof Player player)
					{
						var cabal = SevenSignsManager.getInstance().getPlayerCabal(player.getObjectId());
						if (i0 == CabalType.NORMAL && cabal == CabalType.NORMAL)
						{
							npc.removeAttackDesire(player);
							player.teleportTo(getNpcIntAIParam(npc, "SSQTelPosX"), getNpcIntAIParam(npc, "SSQTelPosY"), getNpcIntAIParam(npc, "SSQTelPosZ"), 0);
							return;
						}
						else if (i0 == CabalType.DUSK && cabal != CabalType.DUSK)
						{
							npc.removeAttackDesire(player);
							player.teleportTo(getNpcIntAIParam(npc, "SSQTelPosX"), getNpcIntAIParam(npc, "SSQTelPosY"), getNpcIntAIParam(npc, "SSQTelPosZ"), 0);
							return;
						}
						else if (i0 == CabalType.DAWN && cabal != CabalType.DAWN)
						{
							npc.removeAttackDesire(player);
							player.teleportTo(getNpcIntAIParam(npc, "SSQTelPosX"), getNpcIntAIParam(npc, "SSQTelPosY"), getNpcIntAIParam(npc, "SSQTelPosZ"), 0);
							return;
						}
					}
					else if (creature instanceof Summon summon)
					{
						Player owner = summon.getOwner();
						if (owner == null)
							return;
						
						var cabal = SevenSignsManager.getInstance().getPlayerCabal(owner.getObjectId());
						if (i0 == CabalType.NORMAL && cabal == CabalType.NORMAL)
						{
							npc.removeAttackDesire(summon);
							owner.teleportTo(getNpcIntAIParam(npc, "SSQTelPosX"), getNpcIntAIParam(npc, "SSQTelPosY"), getNpcIntAIParam(npc, "SSQTelPosZ"), 0);
							return;
						}
						else if (i0 == CabalType.DUSK && cabal != CabalType.DUSK)
						{
							npc.removeAttackDesire(summon);
							owner.teleportTo(getNpcIntAIParam(npc, "SSQTelPosX"), getNpcIntAIParam(npc, "SSQTelPosY"), getNpcIntAIParam(npc, "SSQTelPosZ"), 0);
							return;
						}
						else if (i0 == CabalType.DAWN && cabal != CabalType.DAWN)
						{
							npc.removeAttackDesire(summon);
							owner.teleportTo(getNpcIntAIParam(npc, "SSQTelPosX"), getNpcIntAIParam(npc, "SSQTelPosY"), getNpcIntAIParam(npc, "SSQTelPosZ"), 0);
							return;
						}
					}
				}
				else if (SevenSignsManager.getInstance().getCurrentPeriod() == PeriodType.COMPETITION)
				{
					var i0 = SevenSignsManager.getInstance().getSealOwner(sealType);
					if (creature instanceof Player player)
					{
						var cabal = SevenSignsManager.getInstance().getPlayerCabal(player.getObjectId());
						if (i0 == CabalType.NORMAL && cabal == CabalType.NORMAL)
						{
							npc.removeAttackDesire(player);
							player.teleportTo(getNpcIntAIParam(npc, "SSQTelPosX"), getNpcIntAIParam(npc, "SSQTelPosY"), getNpcIntAIParam(npc, "SSQTelPosZ"), 0);
							return;
						}
					}
					else if (creature instanceof Summon summon)
					{
						Player owner = summon.getOwner();
						if (owner == null)
							return;
						
						var cabal = SevenSignsManager.getInstance().getPlayerCabal(owner.getObjectId());
						if (i0 == CabalType.NORMAL && cabal == CabalType.NORMAL)
						{
							npc.removeAttackDesire(summon);
							owner.teleportTo(getNpcIntAIParam(npc, "SSQTelPosX"), getNpcIntAIParam(npc, "SSQTelPosY"), getNpcIntAIParam(npc, "SSQTelPosZ"), 0);
							return;
						}
					}
				}
			}
		}
		super.onSeeCreature(npc, creature);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		npc.broadcastPacket(new PlaySound(1, npc.getTemplate().getAiParams().getOrDefault("RaidSpawnMusic", "Rm01_A"), npc));
		
		startQuestTimerAtFixedRate("1001", npc, null, 1000, 60000);
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		if (!Config.RAID_DISABLE_CURSE && caster.getStatus().getLevel() > (npc.getStatus().getLevel() + 8))
		{
			final L2Skill raidMute = SkillTable.getInstance().getInfo(4215, 1);
			if (getAbnormalLevel(caster, raidMute) <= 0)
			{
				npc.getAI().addCastDesire(caster, raidMute, 1000000);
				return;
			}
		}
		
		if (!npc.isInsideZone(ZoneId.PEACE) && !Config.CATACOMBS_IN_ANY_PERIOD)
		{
			var SSQLoserTeleport = getNpcIntAIParam(npc, "SSQLoserTeleport");
			
			SealType sealType = null;
			if (SSQLoserTeleport == 1)
				sealType = SealType.AVARICE;
			else if (SSQLoserTeleport == 2)
				sealType = SealType.GNOSIS;
			
			if (SSQLoserTeleport != 0)
			{
				if (SSQLoserTeleport != 1 && SSQLoserTeleport != 2)
					LOGGER.info("An invalid value was entered in SSQLoserTeleport. Value = " + SSQLoserTeleport);
				else if (SevenSignsManager.getInstance().getCurrentPeriod() == PeriodType.SEAL_VALIDATION)
				{
					var i0 = SevenSignsManager.getInstance().getSealOwner(sealType);
					var cabal = SevenSignsManager.getInstance().getPlayerCabal(caster.getObjectId());
					if (i0 == CabalType.NORMAL && cabal == CabalType.NORMAL)
					{
						npc.removeAttackDesire(caster);
						caster.teleportTo(getNpcIntAIParam(npc, "SSQTelPosX"), getNpcIntAIParam(npc, "SSQTelPosY"), getNpcIntAIParam(npc, "SSQTelPosZ"), 0);
						return;
					}
					else if (i0 == CabalType.DUSK && cabal != CabalType.DUSK)
					{
						npc.removeAttackDesire(caster);
						caster.teleportTo(getNpcIntAIParam(npc, "SSQTelPosX"), getNpcIntAIParam(npc, "SSQTelPosY"), getNpcIntAIParam(npc, "SSQTelPosZ"), 0);
						return;
					}
					else if (i0 == CabalType.DAWN && cabal != CabalType.DAWN)
					{
						npc.removeAttackDesire(caster);
						caster.teleportTo(getNpcIntAIParam(npc, "SSQTelPosX"), getNpcIntAIParam(npc, "SSQTelPosY"), getNpcIntAIParam(npc, "SSQTelPosZ"), 0);
						return;
					}
				}
				else if (SevenSignsManager.getInstance().getCurrentPeriod() == PeriodType.COMPETITION)
				{
					var i0 = SevenSignsManager.getInstance().getSealOwner(sealType);
					var cabal = SevenSignsManager.getInstance().getPlayerCabal(caster.getObjectId());
					if (i0 == CabalType.NORMAL && cabal == CabalType.NORMAL)
					{
						npc.removeAttackDesire(caster);
						caster.teleportTo(getNpcIntAIParam(npc, "SSQTelPosX"), getNpcIntAIParam(npc, "SSQTelPosY"), getNpcIntAIParam(npc, "SSQTelPosZ"), 0);
						return;
					}
				}
			}
		}
		
		final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
		if (topDesireTarget != null)
		{
			if (skill.getAggroPoints() > 0 && npc.getAI().getCurrentIntention().getType() == IntentionType.ATTACK && topDesireTarget != caster)
				npc.getAI().addAttackDesire(caster, (((skill.getAggroPoints() / npc.getStatus().getMaxHp()) * 4000) * 150));
			
			if (npc.getMove().getGeoPathFailCount() > 10 && caster == topDesireTarget && npc.getStatus().getHpRatio() < 1.)
				npc.teleportTo(caster.getPosition(), 0);
		}
	}
}
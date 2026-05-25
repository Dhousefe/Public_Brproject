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
package ext.mods.gameserver.scripting.script.ai.boss.queenant;

import ext.mods.commons.random.Rnd;

import ext.mods.Config;
import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.enums.skills.ElementType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.PlaySound;
import ext.mods.gameserver.scripting.script.ai.individual.DefaultNpc;
import ext.mods.gameserver.skills.L2Skill;

public class QueenAnt extends DefaultNpc
{
	public QueenAnt()
	{
		super("ai/boss/queenant");
	}
	
	public QueenAnt(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		29001
	};
	
	@Override
	public void onNoDesire(Npc npc)
	{
		npc.getAI().addDoNothingDesire(40, 5);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		if (Rnd.get(100) < 33)
			npc.getSpawn().instantTeleportInMyTerritory(-19480, 187344, -5600, 200);
		else if (Rnd.get(100) < 50)
			npc.getSpawn().instantTeleportInMyTerritory(-17928, 180912, -5520, 200);
		else
			npc.getSpawn().instantTeleportInMyTerritory(-23808, 182368, -5600, 200);
		
		npc.broadcastPacket(new PlaySound(1, "BS01_A", npc));
		
		npc._weightPoint = 10;
		
		createPrivates(npc);
		
		createOnePrivateEx(npc, 29002, -21600, 179482, -5832, Rnd.get(360), 0, false);
		
		startQuestTimerAtFixedRate("1001", npc, null, 10000, 10000);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("1001"))
		{
			if (npc.getAI().getCurrentIntention().getType() == IntentionType.WANDER && Rnd.get(100) < 30)
				npc.getAI().addSocialDesire((Rnd.nextBoolean()) ? 3 : 4, (50 * 1000) / 30, 30);
		}
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onPartyDied(Npc caller, Npc called)
	{
		if (caller != called)
		{
			if (caller.getNpcId() == 29003)
				caller.scheduleRespawn(10000);
			else
				caller.scheduleRespawn((280 + Rnd.get(40)) * 1000L);
		}
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (!Config.RAID_DISABLE_CURSE && attacker.getStatus().getLevel() > (npc.getStatus().getLevel() + 8))
		{
			npc.getAI().addCastDesire(attacker, 4515, 1, 1000000);
			
			npc.getAI().getAggroList().stopHate(attacker);
		}
		
		if (!npc.isInMyTerritory() || !npc.getSpawn().isInMyTerritory(attacker))
		{
			npc.teleportTo(-21610, 181594, -5734, 0);
			npc.removeAllAttackDesire();
		}
		
		if (attacker instanceof Playable)
		{
			if (attacker.isRiding() && getAbnormalLevel(attacker, 4258, 1) <= 0)
				npc.getAI().addCastDesire(attacker, 4258, 1, 1000000);
			
			if (skill != null && skill.getElement() == ElementType.FIRE && Rnd.get(100) < 70)
				npc.getAI().addCastDesire(attacker, 4018, 1, 1000000);
			else
			{
				final double dist = npc.distance2D(attacker);
				if (dist > 500 && Rnd.get(100) < 10)
					npc.getAI().addCastDesireHold(attacker, 4019, 1, 1000000);
				else if (dist > 150 && Rnd.get(100) < 10)
				{
					if (Rnd.get(100) < 80)
						npc.getAI().addCastDesireHold(attacker, 4018, 1, 1000000);
					else
						npc.getAI().addCastDesireHold(attacker, 4019, 1, 1000000);
				}
				else if (dist < 250 && Rnd.get(100) < 5)
					npc.getAI().addCastDesireHold(npc, 4017, 1, 1000000);
				else if (Rnd.get(100) < 1)
					npc.getAI().addSocialDesire(1, (60 * 1000) / 30, 3000000);
			}
			
			npc.getAI().addAttackDesireHold(attacker, (int) (((((((double) damage) / npc.getStatus().getMaxHp()) / 0.05) * damage) * npc._weightPoint) * 1000));
			
			final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
			if (topDesireTarget != null && !npc.canAutoAttack(topDesireTarget))
				npc.getAI().getAggroList().stopHate(topDesireTarget);
		}
	}
	
	@Override
	public void onPartyAttacked(Npc caller, Npc called, Creature target, int damage)
	{
		if (target instanceof Playable && caller != called)
		{
			final double dist = called.distance2D(target);
			if (dist > 500 && Rnd.get(100) < 5)
				called.getAI().addCastDesireHold(target, 4019, 1, 1000000);
			else if (dist > 150 && Rnd.get(100) < 5)
			{
				if (Rnd.get(100) < 80)
					called.getAI().addCastDesireHold(target, 4018, 1, 1000000);
				else
					called.getAI().addCastDesireHold(target, 4019, 1, 1000000);
			}
			else if (dist < 250 && Rnd.get(100) < 2)
				called.getAI().addCastDesireHold(called, 4017, 1, 1000000);
			
			called.getAI().addAttackDesireHold(target, (int) (((((((double) damage) / called.getStatus().getMaxHp()) / 0.05) * damage) * caller._weightPoint) * 1000));
		}
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			final double dist = called.distance2D(attacker);
			if (dist > 500 && Rnd.get(100) < 3)
				called.getAI().addCastDesireHold(attacker, 4019, 1, 1000000);
			else if (dist > 150 && Rnd.get(100) < 3)
			{
				if (Rnd.get(100) < 80)
					called.getAI().addCastDesireHold(attacker, 4018, 1, 1000000);
				else
					called.getAI().addCastDesireHold(attacker, 4019, 1, 1000000);
			}
			else if (dist < 250 && Rnd.get(100) < 2)
				called.getAI().addCastDesireHold(called, 4017, 1, 1000000);
			
			called.getAI().addAttackDesireHold(attacker, (int) (((((double) damage) / called.getStatus().getMaxHp()) / 0.05) * 500));
		}
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		if (!Config.RAID_DISABLE_CURSE && caster.getStatus().getLevel() > (npc.getStatus().getLevel() + 8))
		{
			npc.getAI().addCastDesire(caster, 4215, 1, 1000000);
			return;
		}
		
		if (skill.getAggroPoints() > 0 && Rnd.get(100) < 15)
			npc.getAI().addCastDesire(caster, 4018, 1, 1000000);
	}
	
	@Override
	public void onOutOfTerritory(Npc npc)
	{
		npc.teleportTo(-21610, 181594, -5734, 0);
		npc.removeAllAttackDesire();
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		npc.broadcastPacket(new PlaySound(1, "BS02_D", npc));
	}
}
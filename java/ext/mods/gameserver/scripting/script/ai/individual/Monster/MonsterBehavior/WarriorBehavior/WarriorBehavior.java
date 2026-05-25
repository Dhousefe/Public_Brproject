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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.MonsterBehavior.WarriorBehavior;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.Summon;
import ext.mods.gameserver.scripting.script.ai.individual.Monster.MonsterBehavior.MonsterBehavior;
import ext.mods.gameserver.skills.L2Skill;

public class WarriorBehavior extends MonsterBehavior
{
	protected static final double ATTACK_BOOST_VALUE = 300.0;
	protected static final double USE_SKILL_BOOST_VALUE = 100000.0;
	protected static final double ATTACKED_WEIGHT_POINT = 10.0;
	protected static final double CLAN_ATTACKED_WEIGHT_POINT = 1.0;
	protected static final double PARTY_ATTACKED_WEIGHT_POINT = 1.0;
	protected static final double SEE_SPELL_WEIGHT_POINT = 10.0;
	protected static final double HATE_SKILL_WEIGHT_POINT = 10.0;
	
	public WarriorBehavior()
	{
		super("ai/WarriorBehavior");
	}
	
	public WarriorBehavior(String descr)
	{
		super(descr);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (getNpcIntAIParam(npc, "IsHealer") == 1)
		{
			super.onAttacked(npc, attacker, damage, skill);
			return;
		}
		
		final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
		
		if (getNpcIntAIParamOrDefault(npc, "MovingAttack", 1) == 1)
		{
			if (getNpcIntAIParam(npc, "Party_Type") == 2 && Rnd.get(10000) < getNpcIntAIParam(npc, "Party_OneShot") && topDesireTarget != null)
			{
				if (attacker == topDesireTarget)
					broadcastScriptEventEx(npc, 15000001, npc.getObjectId(), topDesireTarget.getObjectId(), 500);
			}
			
			npc.getAI().addAttackDesire(attacker, damage * ATTACKED_WEIGHT_POINT);
			
			if (topDesireTarget != null)
			{
				if (npc.getMove().getGeoPathFailCount() > 10 && attacker == topDesireTarget && npc.getStatus().getHpRatio() < 1.)
					npc.teleportTo(attacker.getPosition(), 0);
				
				if (npc.isRooted() && npc.distance2D(topDesireTarget) > 40)
				{
					if (!npc.canAutoAttack(topDesireTarget))
						npc.removeAttackDesire(topDesireTarget);
					
					npc.getAI().addAttackDesire(attacker, damage * ATTACKED_WEIGHT_POINT);
				}
			}
		}
		else
		{
			if (npc.canAutoAttack(attacker))
				npc.getAI().addAttackDesireHold(attacker, damage * ATTACKED_WEIGHT_POINT);
			else if (topDesireTarget != null && attacker == topDesireTarget)
				npc.removeAttackDesire(topDesireTarget);
		}
		
		super.onAttacked(npc, attacker, damage, skill);
		
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		if (called.getAI().getLifeTime() < getNpcIntAIParam(called, "Aggressive_Time"))
		{
			super.onClanAttacked(caller, called, attacker, damage, skill);
			return;
		}
		
		final int partyType = getNpcIntAIParam(called, "Party_Type");
		final int partyLoyalty = getNpcIntAIParam(called, "Party_Loyalty");
		
		if ((partyType == 0 || (partyType == 1 && partyLoyalty == 0) || partyType == 2) && getNpcIntAIParam(called, "IsHealer") != 1)
		{
			final Creature topDesireTarget = called.getAI().getTopDesireTarget();
			
			if (getNpcIntAIParamOrDefault(called, "MovingAttack", 1) == 1)
			{
				called.getAI().addAttackDesire(attacker, damage * CLAN_ATTACKED_WEIGHT_POINT);
				
				if (topDesireTarget != null)
				{
					if (called.getMove().getGeoPathFailCount() > 10 && attacker == topDesireTarget && called.getStatus().getHpRatio() < 1.)
						called.teleportTo(attacker.getPosition(), 0);
					
					if (called.isRooted() && called.distance2D(topDesireTarget) > 40)
					{
						if (!called.canAutoAttack(topDesireTarget))
							called.removeAttackDesire(topDesireTarget);
						
						called.getAI().addAttackDesire(attacker, damage * CLAN_ATTACKED_WEIGHT_POINT);
					}
				}
			}
			else
			{
				if (called.canAutoAttack(attacker))
					called.getAI().addAttackDesireHold(attacker, damage * CLAN_ATTACKED_WEIGHT_POINT);
				else if (topDesireTarget != null && attacker == topDesireTarget)
					called.removeAttackDesire(topDesireTarget);
			}
		}
		
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
	
	@Override
	public void onAttackFinished(Npc npc, Creature target)
	{
		if (target.isDead() && target instanceof Summon)
		{
			final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
			final Player master = target.getActingPlayer();
			
			if (getNpcIntAIParamOrDefault(npc, "MovingAttack", 1) == 1)
			{
				npc.getAI().addAttackDesire(master, 500 * ATTACKED_WEIGHT_POINT);
				
				if (topDesireTarget != null)
				{
					if (npc.getMove().getGeoPathFailCount() > 10 && master == topDesireTarget && npc.getStatus().getHpRatio() < 1.)
						npc.teleportTo(master.getPosition(), 0);
					
					if (npc.isRooted() && npc.distance2D(topDesireTarget) > 40)
					{
						if (!npc.canAutoAttack(topDesireTarget))
							npc.removeAttackDesire(topDesireTarget);
						
						npc.getAI().addAttackDesire(master, 500 * ATTACKED_WEIGHT_POINT);
					}
				}
			}
			else
			{
				if (npc.canAutoAttack(master))
					npc.getAI().addAttackDesireHold(master, 500 * ATTACKED_WEIGHT_POINT);
				else if (topDesireTarget != null && master == topDesireTarget)
					npc.removeAttackDesire(topDesireTarget);
			}
		}
	}
	
	@Override
	public void onPartyAttacked(Npc caller, Npc called, Creature target, int damage)
	{
		final int partyType = getNpcIntAIParam(called, "Party_Type");
		final int partyLoyalty = getNpcIntAIParam(called, "Party_Loyalty");
		
		if (partyType == 0 || !called.hasMaster())
		{
			super.onPartyAttacked(caller, called, target, damage);
			return;
		}
		
		if ((partyType == 1 && (partyLoyalty == 0 || partyLoyalty == 1)) || (partyType == 1 && partyLoyalty == 2 && caller == called.getMaster()) || (partyType == 2 && caller != called.getMaster()))
		{
			if (getNpcIntAIParam(called, "IsHealer") != 1)
			{
				final Creature topDesireTarget = called.getAI().getTopDesireTarget();
				
				if (getNpcIntAIParamOrDefault(called, "MovingAttack", 1) == 1)
				{
					called.getAI().addAttackDesire(target, damage * PARTY_ATTACKED_WEIGHT_POINT);
					
					if (topDesireTarget != null)
					{
						if (called.getMove().getGeoPathFailCount() > 10 && target == topDesireTarget && called.getStatus().getHpRatio() < 1)
							called.teleportTo(target.getPosition(), 0);
						
						if (called.isRooted() && called.distance2D(topDesireTarget) > 40)
						{
							if (!called.canAutoAttack(topDesireTarget))
								called.removeAttackDesire(topDesireTarget);
							
							called.getAI().addAttackDesire(target, damage * PARTY_ATTACKED_WEIGHT_POINT);
						}
					}
				}
				else
				{
					if (called.canAutoAttack(target))
						called.getAI().addAttackDesireHold(target, damage * PARTY_ATTACKED_WEIGHT_POINT);
					else if (topDesireTarget != null && target == topDesireTarget)
						called.removeAttackDesire(topDesireTarget);
				}
			}
		}
		
		super.onPartyAttacked(caller, called, target, damage);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (creature instanceof Playable && getNpcIntAIParam(npc, "IsAggressive") != 0 && npc.getAI().getLifeTime() > getNpcIntAIParam(npc, "Aggressive_Time"))
		{
			if (getNpcIntAIParamOrDefault(npc, "MovingAttack", 1) == 1)
				npc.getAI().addAttackDesire(creature, 300);
			else
				npc.getAI().addAttackDesireHold(creature, 300);
		}
		
		super.onSeeCreature(npc, creature);
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		if (targets.length > 0 && (skill.getAggroPoints() > 0 || skill.getPower(npc) > 0 || skill.isOffensive()))
			if (npc.isInCombat())
				npc.getAI().addAttackDesire(caster, 100 * SEE_SPELL_WEIGHT_POINT);
			
		super.onSeeSpell(npc, caster, skill, targets, isPet);
	}
	
	
	@Override
	public void onScriptEvent(Npc npc, int eventId, int arg1, int arg2)
	{
		if (eventId == 15000001)
		{
			final Creature c0 = (Creature) World.getInstance().getObject(arg1);
			if (c0 != null && c0 == npc.getMaster())
			{
				final Creature c1 = (Creature) World.getInstance().getObject(arg2);
				if (c1 != null)
				{
					npc.removeAllAttackDesire();
					onAttacked(npc, c1, 100, null);
				}
			}
		}
		
		super.onScriptEvent(npc, eventId, arg1, arg2);
	}
}
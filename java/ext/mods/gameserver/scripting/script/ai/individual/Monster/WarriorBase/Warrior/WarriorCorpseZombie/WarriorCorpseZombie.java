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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorCorpseZombie;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.Warrior;
import ext.mods.gameserver.skills.L2Skill;

public class WarriorCorpseZombie extends Warrior
{
	public WarriorCorpseZombie()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorCorpseZombie");
	}
	
	public WarriorCorpseZombie(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21554,
		21548
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai0 = 0;
		
		if (getNpcIntAIParam(npc, "IsPrivate") == 1)
			startQuestTimerAtFixedRate("1004", npc, null, 20000, 20000);
		
		super.onCreated(npc);
	}
	
	@Override
	public void onNoDesire(Npc npc)
	{
		if (getNpcIntAIParam(npc, "IsPrivate") == 1)
		{
			final Npc master = npc.getMaster();
			if (master != null && !master.isDead())
				npc.getAI().addFollowDesire(master, 50);
			else
				npc.getAI().addWanderDesire(5, 5);
		}
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("1004"))
		{
			final Npc master = npc.getMaster();
			if (master != null && master.isDead() && getNpcIntAIParam(npc, "IsPrivate") == 1)
			{
				if (npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK && npc.getAI().getCurrentIntention().getType() != IntentionType.CAST)
				{
					npc.deleteMe();
					return null;
				}
			}
		}
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		if (Rnd.get(100) < 10)
		{
			final L2Skill selfRangeDebuff = getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_DEBUFF);
			npc.getAI().addCastDesire(npc, selfRangeDebuff, 1000000);
		}
		super.onMyDying(npc, killer);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (getNpcIntAIParam(npc, "IsTeleport") != 0)
		{
			if (npc.distance2D(attacker) > 100)
			{
				final Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
				if (mostHated == attacker && Rnd.get(100) < 10)
				{
					npc.teleportTo(attacker.getPosition(), 0);
					
					final L2Skill avTeleport = SkillTable.getInstance().getInfo(4671, 1);
					npc.getAI().addCastDesire(attacker, avTeleport, 1000000);
					
					if (attacker instanceof Playable)
					{
						double f0 = getHateRatio(npc, attacker);
						f0 = (((1.0 * damage) / (npc.getStatus().getLevel() + 7)) + ((f0 / 100) * ((1.0 * damage) / (npc.getStatus().getLevel() + 7))));
						
						npc.getAI().addAttackDesire(attacker, f0 * 30);
					}
				}
			}
		}
		
		if (npc.getStatus().getHpRatio() < 0.1 && npc._i_ai0 == 0 && Rnd.get(100) < 10)
		{
			final L2Skill selfRangeDebuff = getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_DEBUFF);
			npc.getAI().addCastDesire(npc, selfRangeDebuff, 1000000);
			
			npc._i_ai0 = 1;
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		if (getNpcIntAIParam(called, "IsTeleport") != 0)
		{
			if (called.getAI().getCurrentIntention().getType() != IntentionType.ATTACK && called.distance2D(attacker) > 100 && Rnd.get(100) < 10)
			{
				called.teleportTo(attacker.getPosition(), 0);
				
				final L2Skill avTeleport = SkillTable.getInstance().getInfo(4671, 1);
				called.getAI().addCastDesire(attacker, avTeleport, 1000000);
				
				if (attacker instanceof Playable)
				{
					double f0 = getHateRatio(called, attacker);
					f0 = (((1.0 * damage) / (called.getStatus().getLevel() + 7)) + ((f0 / 100) * ((1.0 * damage) / (called.getStatus().getLevel() + 7))));
					
					called.getAI().addAttackDesire(attacker, (int) (f0 * 30));
				}
			}
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
}
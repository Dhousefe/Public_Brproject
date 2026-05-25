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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.PartyPrivateWarrior;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.Warrior;
import ext.mods.gameserver.skills.L2Skill;

public class PartyPrivateWarrior extends Warrior
{
	private static final NpcStringId[] SHOUTS =
	{
		NpcStringId.ID_1000292,
		NpcStringId.ID_1000400,
		NpcStringId.ID_1000401,
		NpcStringId.ID_1000402
	};
	
	public PartyPrivateWarrior()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/PartyPrivateWarrior");
	}
	
	public PartyPrivateWarrior(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		20377,
		20399,
		20445,
		20454,
		20524,
		20739,
		20766,
		27037,
		29070,
		29071,
		29072,
		29073,
		29074,
		29075,
		29076,
		22079,
		22095,
		27189
	};
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("1005"))
		{
			final Npc master = npc.getMaster();
			if (master != null && !master.isDead() && npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK && !npc.isInMyTerritory())
			{
				npc.teleportTo(master.getPosition(), 0);
				
				npc.removeAllAttackDesire();
			}
		}
		else if (name.equalsIgnoreCase("1006"))
		{
			final Npc master = npc.getMaster();
			if (master == null || master.isDead())
			{
				final IntentionType currentIntention = npc.getAI().getCurrentIntention().getType();
				if (currentIntention != IntentionType.ATTACK && currentIntention != IntentionType.CAST)
				{
					npc.deleteMe();
					return null;
				}
			}
		}
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onNoDesire(Npc npc)
	{
		final Npc master = npc.getMaster();
		if (master != null && !master.isDead())
			npc.getAI().addFollowDesire(master, 50);
		else
			npc.getAI().addWanderDesire(5, 5);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		startQuestTimerAtFixedRate("1005", npc, null, 120000, 120000);
		startQuestTimerAtFixedRate("1006", npc, null, 20000, 20000);
		
		super.onCreated(npc);
	}
	
	@Override
	public void onPartyAttacked(Npc caller, Npc called, Creature target, int damage)
	{
		if (!called.isMaster() && target instanceof Playable)
		{
			double hateRatio = getHateRatio(called, target);
			hateRatio = (((1.0 * damage) / (called.getStatus().getLevel() + 7)) + ((hateRatio / 100) * ((1.0 * damage) / (called.getStatus().getLevel() + 7))));
			
			called.getAI().addAttackDesire(target, (int) ((hateRatio * damage) * called._weightPoint * 10));
		}
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			double hateRatio = getHateRatio(npc, attacker);
			hateRatio = (((1.0 * damage) / (npc.getStatus().getLevel() + 7)) + ((hateRatio / 100) * ((1.0 * damage) / (npc.getStatus().getLevel() + 7))));
			
			final Npc master = npc.getMaster();
			if (master != null && master.isDead())
				npc.getAI().addAttackDesire(attacker, hateRatio * 100);
			else
				npc.getAI().addAttackDesire(attacker, hateRatio * damage * npc._weightPoint * 10);
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable && called.getAI().getLifeTime() > 7)
		{
			final Npc master = called.getMaster();
			if (master != null && master.isDead())
			{
				double hateRatio = getHateRatio(called, attacker);
				hateRatio = (((1.0 * damage) / (called.getStatus().getLevel() + 7)) + ((hateRatio / 100) * ((1.0 * damage) / (called.getStatus().getLevel() + 7))));
				
				called.getAI().addAttackDesire(attacker, (int) (hateRatio * 30));
			}
		}
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		final Npc master = npc.getMaster();
		if (master != null && master.isDead())
			super.onSeeSpell(npc, caster, skill, targets, isPet);
	}
	
	@Override
	public void onScriptEvent(Npc npc, int eventId, int arg1, int arg2)
	{
		if (npc.isDead())
			return;
		
		if (eventId == 10002)
		{
			if (!npc.hasMaster())
				return;
			
			final Npc master = (Npc) World.getInstance().getObject(arg1);
			if (master == null)
				return;
			
			final Creature creatureFromFlag = (Creature) World.getInstance().getObject(master._flag);
			if (creatureFromFlag == null)
				return;
			
			if (master == npc.getMaster())
			{
				final Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
				if (mostHated != null && mostHated == creatureFromFlag)
					return;
				
				npc.broadcastNpcSay(Rnd.get(SHOUTS));
				
				npc.removeAllAttackDesire();
				
				npc.getAI().addAttackDesire(creatureFromFlag, 2000);
			}
		}
	}
}
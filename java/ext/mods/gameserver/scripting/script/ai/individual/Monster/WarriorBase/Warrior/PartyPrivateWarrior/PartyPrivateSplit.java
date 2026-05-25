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

import ext.mods.Config;
import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.NpcStringId;

public class PartyPrivateSplit extends PartyPrivateWarrior
{
	public PartyPrivateSplit()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/PartyPrivateWarrior");
	}
	
	public PartyPrivateSplit(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		22087,
		22093
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._c_ai0 = (Creature) World.getInstance().getObject(npc._param2);
		
		if (npc._c_ai0 != null)
			npc.getAI().setTopDesireTarget(npc._c_ai0);
		
		super.onCreated(npc);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (creature == npc.getAI().getTopDesireTarget())
		{
			if (Rnd.get(100) < 50)
				npc.getAI().addCastDesire(creature, getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL1), 1000000);
			else
				npc.getAI().addCastDesire(creature, getNpcSkillByType(npc, NpcSkillType.PHYSICAL_SPECIAL2), 1000000);
			
			if (!(creature instanceof Playable))
				return;
			
			tryToAttack(npc, creature);
		}
		
		super.onSeeCreature(npc, creature);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("1006"))
		{
			if (!npc.hasMaster() || npc.getMaster().isDead())
			{
				if (npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK && npc.getAI().getCurrentIntention().getType() != IntentionType.CAST)
				{
					npc.deleteMe();
					return null;
				}
				
				startQuestTimer("1006", npc, player, 180000);
			}
		}
		
		if (name.equalsIgnoreCase("1004"))
			startQuestTimer("1006", npc, player, 180000);
		
		if (name.equalsIgnoreCase("1001"))
		{
			final IntentionType currentIntention = npc.getAI().getCurrentIntention().getType();
			
			if ((currentIntention == IntentionType.IDLE || currentIntention == IntentionType.WANDER || currentIntention == IntentionType.MOVE_ROUTE) && npc.getStatus().getHpRatio() > 0.4 && !npc.isDead())
			{
				final int moveAroundSocial = getNpcIntAIParam(npc, "MoveAroundSocial");
				final int moveAroundSocial1 = getNpcIntAIParam(npc, "MoveAroundSocial1");
				final int moveAroundSocial2 = getNpcIntAIParam(npc, "MoveAroundSocial2");
				
				if (moveAroundSocial > 0 || moveAroundSocial1 > 0 || moveAroundSocial2 > 0)
				{
					if (moveAroundSocial2 > 0 && Rnd.get(100) < Config.MONSTER_ANIMATION)
						npc.getAI().addSocialDesire(3, (moveAroundSocial2 * 1000) / 30, 50);
					else if (moveAroundSocial1 > 0 && Rnd.get(100) < Config.MONSTER_ANIMATION)
						npc.getAI().addSocialDesire(2, (moveAroundSocial1 * 1000) / 30, 50);
					else if (moveAroundSocial > 0 && Rnd.get(100) < Config.MONSTER_ANIMATION)
						npc.getAI().addSocialDesire(1, (moveAroundSocial * 1000) / 30, 50);
				}
				final int shoutMsg2 = getNpcIntAIParam(npc, "ShoutMsg2");
				if (shoutMsg2 > 0 && Rnd.get(1000) < 17)
				{
					if (getNpcIntAIParam(npc, "IsSay") == 0)
						npc.broadcastNpcShout(NpcStringId.get(shoutMsg2));
					else
						npc.broadcastNpcSay(NpcStringId.get(shoutMsg2));
				}
			}
			else if (currentIntention == IntentionType.ATTACK)
			{
				final int shoutMsg3 = getNpcIntAIParam(npc, "ShoutMsg3");
				if (shoutMsg3 > 0 && Rnd.get(100) < 10)
				{
					if (getNpcIntAIParam(npc, "IsSay") == 0)
						npc.broadcastNpcShout(NpcStringId.get(shoutMsg3));
					else
						npc.broadcastNpcSay(NpcStringId.get(shoutMsg3));
				}
			}
			
			startQuestTimer("1001", npc, player, 10000);
		}
		if (name.equalsIgnoreCase("1"))
		{
			if (getNpcIntAIParam(npc, "AttackLowLevel") == 1)
				npc.lookNeighbor(300);
		}
		
		if (name.equalsIgnoreCase("2"))
		{
			if (getNpcIntAIParam(npc, "IsVs") == 1)
				npc._c_ai0 = npc;
		}
		
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onPartyDied(Npc caller, Npc called)
	{
		if (caller != called && caller == called.getMaster())
			startQuestTimer("1004", called, null, 180000);
	}
}
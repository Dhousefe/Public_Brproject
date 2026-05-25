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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WizardBase.PartyLeaderWizard;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.container.attackable.HateList;
import ext.mods.gameserver.skills.L2Skill;

public class PartyLeaderWizardCorpseNecroAggressive extends PartyLeaderWizardDD2
{
	public PartyLeaderWizardCorpseNecroAggressive()
	{
		super("ai/individual/Monster/WizardBase/PartyLeaderWizard");
	}
	
	public PartyLeaderWizardCorpseNecroAggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21596,
		21599
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		startQuestTimerAtFixedRate("3456", npc, null, 5000, 5000);
		
		super.onCreated(npc);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (!(creature instanceof Playable))
		{
			super.onSeeCreature(npc, creature);
			return;
		}
		
		tryToAttack(npc, creature);
		
		if (creature.isDead())
		{
			if (npc.getAI().getCurrentIntention().getType() == IntentionType.ATTACK && Rnd.get(100) < 50 && npc.distance2D(creature) < 100)
			{
				final Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
				if (mostHated != null)
				{
					createOnePrivateEx(npc, getNpcIntAIParam(npc, "SummonPrivate"), creature.getX(), creature.getY(), creature.getZ(), 0, 0, false, 1000, mostHated.getObjectId(), 0);
					
					final L2Skill clearCorpse = getNpcSkillByType(npc, NpcSkillType.CLEAR_CORPSE);
					npc.getAI().addCastDesire(creature, clearCorpse, 1000000);
				}
			}
		}
		super.onSeeCreature(npc, creature);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("3456"))
			npc.lookNeighbor(200);
		
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		super.onAttacked(npc, attacker, damage, skill);
		
		if (attacker instanceof Playable)
		{
			final HateList hateList = npc.getAI().getHateList();
			
			final Creature mostHated = hateList.getMostHatedCreature();
			if (mostHated != null && npc._i_ai0 == 0)
			{
				final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
				if (topDesireTarget == attacker)
				{
					if (Rnd.get(100) < 33 && npc.getStatus().getHpRatio() < 0.4)
					{
						final L2Skill DDMagic1 = getNpcSkillByType(npc, NpcSkillType.DD_MAGIC1);
						npc.getAI().addCastDesire(attacker, DDMagic1, 1000000);
					}
				}
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
}
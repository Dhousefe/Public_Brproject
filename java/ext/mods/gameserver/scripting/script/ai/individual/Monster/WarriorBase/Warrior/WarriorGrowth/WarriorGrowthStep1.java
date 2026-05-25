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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorGrowth;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.Warrior;
import ext.mods.gameserver.skills.L2Skill;

public class WarriorGrowthStep1 extends Warrior
{
	public WarriorGrowthStep1()
	{
		super("ai/individual/Monster/WarriorBase/Warrior/WarriorGrowth");
	}
	
	public WarriorGrowthStep1(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21451,
		21470,
		21489
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai4 = 0;
		super.onCreated(npc);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		final L2Skill feedID1 = getNpcSkillAIParam(npc, "FeedID1");
		final L2Skill feedID2 = getNpcSkillAIParam(npc, "FeedID2");
		final int takeSocial = getNpcIntAIParamOrDefault(npc, "TakeSocial", 5);
		
		if (skill == feedID1 || skill == feedID2)
		{
			npc._i_ai3 = skill.getId();
			if (npc._i_ai4 == 0)
			{
				startQuestTimer("2001", npc, null, (takeSocial * 1000) / 30);
				npc._i_ai4 = 1;
				npc._c_ai0 = attacker;
				if (skill.getId() == npc._i_ai3 && takeSocial != 0)
				{
					final int moveAroundSocial = getNpcIntAIParam(npc, "MoveAroundSocial");
					npc.getAI().addSocialDesire(1, (moveAroundSocial * 1000) / 30, 200);
				}
				
				if (Rnd.get(100) < 5)
				{
					final int i0 = Rnd.get(10) + 2004;
					npc.broadcastNpcSay(NpcStringId.get(i0));
				}
			}
			else if (npc._i_ai4 != 1 || npc._c_ai0 != attacker)
			{
				npc._i_ai4 = 2;
				if (attacker instanceof Playable)
				{
					double hateRatio = getHateRatio(npc, attacker);
					hateRatio = (((1.0 * damage) / (npc.getStatus().getLevel() + 7)) + ((hateRatio / 100) * ((1.0 * damage) / (npc.getStatus().getLevel() + 7))));
					npc.getAI().addAttackDesire(attacker, hateRatio);
				}
			}
		}
		else
			super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("2001") && npc._i_ai4 == 1 && !npc.isDead())
		{
			final L2Skill feedID1 = getNpcSkillAIParam(npc, "FeedID1");
			
			final int feedID1WarriorSilhouette1 = getNpcIntAIParam(npc, "FeedID1_warrior_silhouette1");
			final int feedID1WarriorSilhouette2 = getNpcIntAIParam(npc, "FeedID1_warrior_silhouette2");
			final int feedID1WizardSilhouette1 = getNpcIntAIParam(npc, "FeedID1_wizard_silhouette1");
			final int feedID1WizardSilhouette2 = getNpcIntAIParam(npc, "FeedID1_wizard_silhouette2");
			final int feedID2WarriorSilhouette1 = getNpcIntAIParam(npc, "FeedID2_warrior_silhouette1");
			final int feedID2WarriorSilhouette2 = getNpcIntAIParam(npc, "FeedID2_warrior_silhouette2");
			final int feedID2WizardSilhouette1 = getNpcIntAIParam(npc, "FeedID2_wizard_silhouette1");
			final int feedID2WizardSilhouette2 = getNpcIntAIParam(npc, "FeedID2_wizard_silhouette2");
			
			final int heading = npc.getHeading();
			if (npc._i_ai3 == feedID1.getId())
			{
				if (Rnd.get(100) < 50)
				{
					if (Rnd.get(100) < 50)
						createOnePrivateEx(npc, feedID1WarriorSilhouette1, npc.getX(), npc.getY(), npc.getZ(), heading, 0, true, npc._c_ai0.getObjectId(), npc._i_ai3, 0);
					else
						createOnePrivateEx(npc, feedID1WarriorSilhouette2, npc.getX(), npc.getY(), npc.getZ(), heading, 0, true, npc._c_ai0.getObjectId(), npc._i_ai3, 0);
				}
				else if (Rnd.get(100) < 50)
					createOnePrivateEx(npc, feedID1WizardSilhouette1, npc.getX(), npc.getY(), npc.getZ(), heading, 0, true, npc._c_ai0.getObjectId(), npc._i_ai3, 0);
				else
					createOnePrivateEx(npc, feedID1WizardSilhouette2, npc.getX(), npc.getY(), npc.getZ(), heading, 0, true, npc._c_ai0.getObjectId(), npc._i_ai3, 0);
			}
			else if (Rnd.get(100) < 50)
			{
				if (Rnd.get(100) < 50)
					createOnePrivateEx(npc, feedID2WarriorSilhouette1, npc.getX(), npc.getY(), npc.getZ(), heading, 0, true, npc._c_ai0.getObjectId(), npc._i_ai3, 0);
				else
					createOnePrivateEx(npc, feedID2WarriorSilhouette2, npc.getX(), npc.getY(), npc.getZ(), heading, 0, true, npc._c_ai0.getObjectId(), npc._i_ai3, 0);
			}
			else if (Rnd.get(100) < 50)
				createOnePrivateEx(npc, feedID2WizardSilhouette1, npc.getX(), npc.getY(), npc.getZ(), heading, 0, true, npc._c_ai0.getObjectId(), npc._i_ai3, 0);
			else
				createOnePrivateEx(npc, feedID2WizardSilhouette2, npc.getX(), npc.getY(), npc.getZ(), heading, 0, true, npc._c_ai0.getObjectId(), npc._i_ai3, 0);
			
			npc.deleteMe();
		}
		
		return null;
	}
}
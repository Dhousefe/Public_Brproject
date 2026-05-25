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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WizardBase.Wizard.WizardDDMagic2.WizardGrowth;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.actors.ClassId;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.scripting.script.ai.individual.Monster.WizardBase.Wizard.WizardDDMagic2.WizardDDMagic2Aggressive;
import ext.mods.gameserver.skills.L2Skill;

public class WizardGrowthStep3Aggressive extends WizardDDMagic2Aggressive
{
	public WizardGrowthStep3Aggressive()
	{
		super("ai/individual/Monster/WizardBase/Wizard/WizardDDMagic2/WizardGrowth");
	}
	
	public WizardGrowthStep3Aggressive(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		21461,
		21463,
		21465,
		21467,
		21480,
		21482,
		21484,
		21486,
		21499,
		21501,
		21503,
		21505
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai4 = 1;
		npc._c_ai0 = (Creature) World.getInstance().getObject(npc._param1);
		npc._i_ai3 = npc._param2;
		if (npc._c_ai0 != null)
			npc.getAI().getHateList().addHateInfo(npc._c_ai0, 100);
		
		npc._i_ai2 = 0;
		if (Rnd.get(100) < 33)
			npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.SELF_BUFF), 1000000);
		
		if (npc._c_ai0 != null)
		{
			final L2Skill wLongRangeDDMagic = getNpcSkillByType(npc, NpcSkillType.W_LONG_RANGE_DD_MAGIC);
			final L2Skill wShortRangeDDMagic = getNpcSkillByType(npc, NpcSkillType.W_SHORT_RANGE_DD_MAGIC);
			
			if (npc.distance2D(npc._c_ai0) > 100)
			{
				if (npc.getCast().meetsHpMpConditions(npc, wLongRangeDDMagic))
					npc.getAI().addCastDesire(npc._c_ai0, wLongRangeDDMagic, 1000000);
				else
				{
					npc._i_ai0 = 1;
					npc.getAI().addAttackDesire(npc._c_ai0, 1000);
				}
			}
			else if (npc.getCast().meetsHpMpConditions(npc, wShortRangeDDMagic))
				npc.getAI().addCastDesire(npc._c_ai0, wShortRangeDDMagic, 1000000);
			else
			{
				npc._i_ai0 = 1;
				npc.getAI().addAttackDesire(npc._c_ai0, 1000);
			}
		}
		
		super.onCreated(npc);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		int i0 = 0;
		int i1 = 0;
		final int takeSocial = getNpcIntAIParamOrDefault(npc, "TakeSocial", 5);
		
		if (skill != null && skill.getId() == npc._i_ai3)
		{
			if (takeSocial != 0)
			{
				final int moveAroundSocial = getNpcIntAIParam(npc, "MoveAroundSocial");
				npc.getAI().addSocialDesire(1, (moveAroundSocial * 1000) / 30, 200);
			}
			i0 = 1;
		}
		
		if (i0 == 1)
		{
			if (Rnd.get(100) < 5)
			{
				i0 = Rnd.get(5) + 2014;
				npc.broadcastNpcSay(NpcStringId.get(i0));
			}
			
			if (npc._c_ai0 != null)
			{
				if (npc._c_ai0 == attacker)
				{
					npc._i_ai2 = (npc._i_ai2 + 1);
					if (npc._i_ai4 == 1 && npc._c_ai0 == attacker)
					{
						if (Rnd.get(100) <= (npc._i_ai2 * 20))
						{
							startQuestTimer("2001", npc, null, (takeSocial * 1000) / 30);
							npc._i_ai4 = 3;
						}
					}
					else if (npc._i_ai4 != 3)
					{
						npc._i_ai4 = 2;
						i1 = 1;
					}
				}
			}
			
			if (i1 == 1 && attacker instanceof Playable)
			{
				double hateRatio = getHateRatio(npc, attacker);
				hateRatio = (((1.0 * damage) / (npc.getStatus().getLevel() + 7)) + ((hateRatio / 100) * ((1.0 * damage) / (npc.getStatus().getLevel() + 7))));
				npc.getAI().addAttackDesire(attacker, hateRatio * 100);
			}
		}
		else
		{
			super.onAttacked(npc, attacker, damage, skill);
			
			if (attacker instanceof Playable)
			{
				if (npc._i_ai0 == 0)
				{
					final Creature mostHated = npc.getAI().getHateList().getMostHatedCreature();
					if (mostHated != null && mostHated != attacker)
					{
						final L2Skill selfRangeDDMagic = getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_DD_MAGIC);
						
						if (npc.getCast().meetsHpMpConditions(npc, selfRangeDDMagic))
							npc.getAI().addCastDesire(attacker, selfRangeDDMagic, 1000000);
						else
						{
							npc._i_ai0 = 1;
							npc.getAI().addAttackDesire(attacker, 1000);
						}
					}
				}
			}
		}
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if ((name.equalsIgnoreCase("2001") && (npc._i_ai4 == 1 || npc._i_ai4 == 3)) && !npc.isDead())
		{
			final int heading = npc.getHeading();
			final int silhouette1 = getNpcIntAIParam(npc, "silhouette1");
			final int silhouette2 = getNpcIntAIParam(npc, "silhouette2");
			final int silhouette_ex = getNpcIntAIParam(npc, "silhouette_ex");
			final int silhouette_ex2 = getNpcIntAIParam(npc, "silhouette_ex2");
			
			if (Rnd.get(100) > 50)
			{
				if (Rnd.get(100) < 50)
					createOnePrivateEx(npc, silhouette1, npc.getX(), npc.getY(), npc.getZ(), heading, 0, true, npc._c_ai0.getObjectId(), npc._i_ai3, 0);
				else
					createOnePrivateEx(npc, silhouette2, npc.getX(), npc.getY(), npc.getZ(), heading, 0, true, npc._c_ai0.getObjectId(), npc._i_ai3, 0);
			}
			else if (ClassId.isSameOccupation((Player) npc._c_ai0, "@fighter"))
				createOnePrivateEx(npc, silhouette_ex, npc.getX(), npc.getY(), npc.getZ(), heading, 0, true, npc._c_ai0.getObjectId(), npc._i_ai3, 0);
			else
				createOnePrivateEx(npc, silhouette_ex2, npc.getX(), npc.getY(), npc.getZ(), heading, 0, true, npc._c_ai0.getObjectId(), npc._i_ai3, 0);
			
			npc.deleteMe();
		}
		
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onScriptEvent(Npc npc, int eventId, int arg1, int arg2)
	{
		if (eventId == 10018)
		{
			if (Rnd.get(100) < 20)
			{
				createOnePrivateEx(npc, getNpcIntAIParam(npc, "silhouette_ex2"), npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 0, true, npc._c_ai0.getObjectId(), npc._i_ai3, 0);
				npc.deleteMe();
			}
		}
		
		super.onScriptEvent(npc, eventId, arg1, arg2);
	}
}
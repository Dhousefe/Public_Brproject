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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior;

import ext.mods.commons.random.Rnd;
import ext.mods.commons.util.ArraysUtil;

import ext.mods.Config;
import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.WarriorBase;
import ext.mods.gameserver.skills.L2Skill;

public class Warrior extends WarriorBase
{
	public Warrior()
	{
		super("ai/individual/Monster/WarriorBase/Warrior");
	}
	
	public Warrior(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		20001,
		20009,
		20023,
		20028,
		20032,
		20065,
		20069,
		20073,
		20091,
		20095,
		20097,
		20107,
		20116,
		20119,
		20139,
		20142,
		20151,
		20152,
		20163,
		20175,
		20189,
		20255,
		20293,
		20297,
		20298,
		20299,
		20301,
		20303,
		20307,
		20309,
		20318,
		20321,
		20326,
		20328,
		20330,
		20334,
		20336,
		20337,
		20348,
		20358,
		20360,
		20381,
		20382,
		20391,
		20392,
		20393,
		20401,
		20405,
		20406,
		20407,
		20418,
		20440,
		20441,
		20443,
		20444,
		20450,
		20451,
		20465,
		20477,
		20488,
		20492,
		20510,
		20531,
		20533,
		20535,
		20653,
		20742,
		20744,
		21011,
		21094,
		21098,
		21119,
		21124,
		21126,
		21127,
		21132,
		21136,
		21265,
		27001,
		27002,
		27006,
		27007,
		27008,
		27009,
		27018,
		27019,
		27042,
		27046,
		27047,
		27048,
		27049,
		27050,
		27051,
		27052,
		27053,
		27055,
		27063,
		27066,
		27069,
		27078,
		27111,
		27115,
		27199,
		27200,
		18003,
		18231,
		18232,
		18233,
		18234,
		18235,
		18236,
		18237,
		18238,
		18239,
		18240,
		18241,
		18242,
		27154,
		27121,
		20074,
		20077,
		20079,
		27182,
		27183,
		20456,
		20388,
		20373,
		20370,
		27079,
		20317,
		20480,
		20400,
		20389,
		20390,
		20532,
		20775,
		20568,
		27038,
		20349,
		20236,
		20272,
		20316,
		20251,
		20252,
		20203,
		27153,
		27101,
		20013,
		20019,
		20281,
		20536,
		20538,
		20544,
		20539,
		20537,
		20442,
		27155,
		27316,
		21017,
		20008,
		20010,
		20014,
		20433,
		20594,
		20007,
		20651,
		20540,
		27098,
		20121,
		20250,
		20003,
		20322,
		20319,
		20528,
		20325,
		20327,
		20320,
		27179,
		20226,
		20525,
		20335,
		27214,
		27215,
		27216,
		20640,
		20147,
		27058,
		20308,
		20777,
		20286,
		20403,
		20274,
		20004,
		20005,
		27061,
		27067,
		27064,
		27059,
		20468,
		20473,
		20470,
		20206,
		21117,
		20476,
		20474,
		20475,
		20481,
		27021,
		20487,
		20030,
		20027,
		20357,
		27116,
		20577,
		27122,
		27123,
		20580,
		20919,
		20922,
		20363,
		20232,
		27022,
		20369,
		20365,
		27117,
		27016,
		20089,
		20575,
		27130,
		27031,
		20547,
		20031,
		20130,
		27054,
		20096,
		20131,
		20598,
		20106,
		27317,
		20778,
		20779,
		20311,
		20312,
		20359,
		20021,
		20534,
		27086,
		20551,
		20054,
		27171,
		20112,
		20114,
		20039,
		27090,
		20514,
		20168,
		27097,
		27096,
		27095,
		27100,
		20515,
		20104,
		27003,
		27004,
		27005,
		20509,
		20200,
		20040,
		20630,
		20573,
		20570,
		20583,
		20035,
		20560,
		20062,
		27070,
		18342,
		20192,
		27077,
		20446,
		20448,
		27020,
		20132,
		20553,
		20120,
		20529,
		20545,
		20530,
		18339,
		22001,
		22089,
		22090,
		22097,
		22098,
		27319,
		27320,
		27321,
		27322,
		27201,
		18004,
		18257,
		18258,
		18259,
		18260,
		18261,
		18262,
		18263,
		18264,
		27165,
		27166,
		27167,
		27173,
		27174,
		27175,
		27176,
		27177,
		27195,
		27202,
		27203,
		27204,
		27205,
		27206,
		27207,
		27208,
		27209,
		27210,
		27211,
		27212,
		27213,
		30936,
		30937,
		30938,
		30939,
		30940,
		30941,
		30942,
		30943,
		30955,
		30972,
		30982,
		30983,
		30984,
		30985,
		30986,
		30987,
		31003,
		31004,
		31005,
		31006,
		31007,
		31008,
		31009,
		31010,
		31011,
		31012,
		31013,
		31014,
		31015,
		31016,
		31017,
		31018,
		31019,
		31020,
		31021,
		31022,
		31023,
		31024,
		31025,
		31026
	};
	
	@Override
	public void onNoDesire(Npc npc)
	{
		npc.getAI().addWanderDesire(5, 5);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		final int shoutMsg1 = getNpcIntAIParam(npc, "ShoutMsg1");
		if (shoutMsg1 > 0)
			npc.broadcastNpcShout(NpcStringId.getNpcMessage(shoutMsg1));
		
		if (getNpcIntAIParam(npc, "MoveAroundSocial") > 0 || getNpcIntAIParam(npc, "ShoutMsg2") > 0 || getNpcIntAIParam(npc, "ShoutMsg3") > 0)
			startQuestTimerAtFixedRate("1001", npc, null, 10000, 10000);
		
		super.onCreated(npc);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("1001"))
		{
			switch (npc.getAI().getCurrentIntention().getType())
			{
				case WANDER, IDLE, MOVE_TO:
					if (!npc.isDead() && npc.getStatus().getHpRatio() > 0.4)
					{
						final int moveAroundSocial2 = getNpcIntAIParam(npc, "MoveAroundSocial2");
						final int moveAroundSocial1 = getNpcIntAIParam(npc, "MoveAroundSocial1");
						final int moveAroundSocial = getNpcIntAIParam(npc, "MoveAroundSocial");
						
						if (moveAroundSocial2 > 0 && Rnd.get(100) < Config.MONSTER_ANIMATION)
							npc.getAI().addSocialDesire(3, moveAroundSocial2 * 1000 / 30, 50);
						else if (moveAroundSocial1 > 0 && Rnd.get(100) < Config.MONSTER_ANIMATION)
							npc.getAI().addSocialDesire(2, moveAroundSocial1 * 1000 / 30, 50);
						else if (moveAroundSocial > 0 && Rnd.get(100) < Config.MONSTER_ANIMATION)
							npc.getAI().addSocialDesire(1, moveAroundSocial * 1000 / 30, 50);
						
						final int shoutMsg2 = getNpcIntAIParam(npc, "ShoutMsg2");
						if (shoutMsg2 > 0 && Rnd.get(1000) < 17)
							npc.broadcastNpcShout(NpcStringId.getNpcMessage(shoutMsg2));
					}
					break;
				
				case ATTACK:
					final int shoutMsg3 = getNpcIntAIParam(npc, "ShoutMsg3");
					if (shoutMsg3 > 0 && Rnd.get(100) < 10)
						npc.broadcastNpcShout(NpcStringId.getNpcMessage(shoutMsg3));
					break;
			}
		}
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		double initialHateRatio = getHateRatio(npc, attacker);
		double hateRatio = initialHateRatio;
		
		if (attacker instanceof Playable)
		{
			hateRatio = (((1.0 * damage) / (npc.getStatus().getLevel() + 7)) + ((hateRatio / 100) * ((1.0 * damage) / (npc.getStatus().getLevel() + 7))));
			npc.getAI().addAttackDesire(attacker, hateRatio * 100);
		}
		
		final Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
		if (mostHated != null)
		{
			if (npc.getMove().getGeoPathFailCount() >= 10 && attacker == mostHated && npc.getStatus().getHpRatio() < 1.)
				npc.teleportTo(attacker.getPosition(), 0);
			
			final int i0 = getAbnormalLevel(npc, 1201, 1);
			if (i0 >= 0 && npc.distance2D(attacker) > 40)
			{
				if (npc.getAttack().canAttack(mostHated))
				{
					if (attacker instanceof Playable)
					{
						hateRatio = initialHateRatio;
						hateRatio = (((1.0 * damage) / (npc.getStatus().getLevel() + 7)) + ((hateRatio / 100) * ((1.0 * damage) / (npc.getStatus().getLevel() + 7))));
						
						npc.getAI().addAttackDesire(attacker, hateRatio * 100);
					}
				}
				else
				{
					npc.getAI().getAggroList().stopHate(mostHated);
					
					if (attacker instanceof Playable)
					{
						hateRatio = initialHateRatio;
						hateRatio = (((1.0 * damage) / (npc.getStatus().getLevel() + 7)) + ((hateRatio / 100) * ((1.0 * damage) / (npc.getStatus().getLevel() + 7))));
						
						npc.getAI().addAttackDesire(attacker, hateRatio * 100);
					}
				}
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		if (called.getAI().getLifeTime() > 7)
		{
			final Creature mostHated = called.getAI().getAggroList().getMostHatedCreature();
			if (mostHated != null && attacker == mostHated && called.getMove().getGeoPathFailCount() >= 10 && called.getStatus().getHpRatio() < 1.)
				called.teleportTo(attacker.getPosition(), 0);
			
			if (attacker instanceof Playable)
			{
				double hateRatio = getHateRatio(called, attacker);
				hateRatio = (((1.0 * damage) / (called.getStatus().getLevel() + 7)) + ((hateRatio / 100) * ((1.0 * damage) / (called.getStatus().getLevel() + 7))));
				
				called.getAI().addAttackDesire(attacker, (int) (hateRatio * 100));
			}
		}
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		final Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
		
		if ((skill.getAggroPoints() > 0 || skill.getPower() > 0 || skill.isOffensive()) && npc.getAI().getCurrentIntention().getType() == IntentionType.ATTACK && ArraysUtil.contains(targets, mostHated))
		{
			double i0 = Math.max(Math.max(skill.getAggroPoints(), skill.getPower(targets[0])), 20);
			double hateRatio = getHateRatio(npc, caster);
			hateRatio = (((1.0 * i0) / (npc.getStatus().getLevel() + 7)) + ((hateRatio / 100) * ((1.0 * i0) / (npc.getStatus().getLevel() + 7))));
			
			npc.getAI().addAttackDesire(caster, hateRatio * 150);
		}
		
		if (caster == mostHated && npc.getMove().getGeoPathFailCount() >= 10 && npc.getStatus().getHpRatio() < 1.)
			npc.teleportTo(caster.getPosition(), 0);
	}
	
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		final int shoutMsg4 = getNpcIntAIParam(npc, "ShoutMsg4");
		if (shoutMsg4 > 0 && Rnd.get(100) < 30)
			npc.broadcastNpcShout(NpcStringId.getNpcMessage(shoutMsg4));
	}
}
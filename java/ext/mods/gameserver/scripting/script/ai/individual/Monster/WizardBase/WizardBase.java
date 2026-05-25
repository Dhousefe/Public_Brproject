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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WizardBase;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.scripting.script.ai.individual.Monster.MonsterAI;
import ext.mods.gameserver.skills.L2Skill;

public class WizardBase extends MonsterAI
{
	public WizardBase()
	{
		super("ai/individual/Monster/WizardBase");
	}
	
	public WizardBase(String descr)
	{
		super(descr);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("1"))
		{
			if (getNpcIntAIParam(npc, "AttackLowLevel") == 1)
				npc.lookNeighbor(300);
		}
		else if (name.equalsIgnoreCase("2"))
		{
			if (getNpcIntAIParam(npc, "IsVs") == 1)
				npc._c_ai0 = npc;
		}
		
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		if (npc._param1 == 1000)
		{
			final Creature c0 = (Creature) World.getInstance().getObject(npc._param2);
			if (c0 != null)
			{
				npc.getAI().addCastDesire(c0, 4663, 1, 1000000);
				npc.getAI().getHateList().addHateInfo(c0, 500);
			}
		}
		
		super.onCreated(npc);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (npc.getSpawn().getMemo().getInteger("CreviceOfDiminsion", 0) != 0 && !npc.getSpawn().isInMyTerritory(attacker))
		{
			npc.getAI().getAggroList().stopHate(attacker);
			return;
		}
		
		if (!npc.isStunned() && !npc.isParalyzed())
		{
			final int ssRate = getNpcIntAIParam(npc, "SoulShotRate");
			if (ssRate != 0 && Rnd.get(100) < ssRate)
				npc.rechargeShots(true, false);
			
			final int spsRate = getNpcIntAIParam(npc, "SpiritShotRate");
			if (spsRate != 0 && Rnd.get(100) < spsRate)
				npc.rechargeShots(false, true);
		}
		
		final IntentionType currentIntention = npc.getAI().getCurrentIntention().getType();
		final double hpRatio = npc.getStatus().getHpRatio();
		final double attackerHpRatio = attacker.getStatus().getHpRatio();
		
		if (getNpcIntAIParam(npc, "AttackLowLevel") == 1 && currentIntention != IntentionType.ATTACK && currentIntention != IntentionType.CAST)
			startQuestTimer("1", npc, null, 7000);
		
		if (getNpcIntAIParam(npc, "AttackLowHP") == 1 && attackerHpRatio <= 0.3 && Rnd.get(100) < 10)
		{
			int i0 = 0;
			if (npc.getAI().getHateList().getMostHatedCreature() != null)
				i0 = 1;
			
			if (i0 == 1)
			{
				if (npc.getAI().getHateList().getMostHatedCreature() != attacker)
				{
					if (attacker instanceof Playable)
					{
						npc.getAI().getHateList().clear();
						
						double f0 = getHateRatio(npc, attacker);
						f0 = (((1.0 * damage) / (npc.getStatus().getLevel() + 7)) + ((f0 / 100) * ((1.0 * damage) / (npc.getStatus().getLevel() + 7))));
						
						if (npc.getAI().getHateList().isEmpty())
							npc.getAI().getHateList().addHateInfo(attacker, (f0 * 100) + 300);
						else
							npc.getAI().getHateList().addHateInfo(attacker, (f0 * 100));
					}
				}
			}
		}
		
		if (attacker instanceof Player)
		{
			final int helpHeroSilhouetteId = getNpcIntAIParam(npc, "HelpHeroSilhouette");
			if (helpHeroSilhouetteId != 0 && attackerHpRatio <= 0.2 && Rnd.get(100) < 3)
				createOnePrivateEx(npc, helpHeroSilhouetteId, npc.getX() + 80, npc.getY() + 80, npc.getZ(), npc.getHeading(), 0, false, 0, 0, npc.getObjectId());
		}
		
		final L2Skill specialSkill = getNpcSkillByType(npc, NpcSkillType.SPECIAL_SKILL);
		if (specialSkill != null && hpRatio <= 0.3 && Rnd.get(100) < 10 && getAbnormalLevel(npc, specialSkill) <= 0)
		{
			switch (Rnd.get(4))
			{
				case 0:
					npc.broadcastNpcSay(NpcStringId.ID_1000290);
					break;
				
				case 1:
					npc.broadcastNpcSay(NpcStringId.ID_1000395);
					break;
				
				case 2:
					npc.broadcastNpcSay(NpcStringId.ID_1000396);
					break;
				
				case 3:
					npc.broadcastNpcSay(NpcStringId.ID_1000397);
					break;
			}
			
			npc.getAI().addCastDesire(npc, specialSkill, 1000000);
		}
		
		Creature mostHatedHI = npc.getAI().getHateList().getMostHatedCreature();
		int i0 = 0;
		if (mostHatedHI != null)
			i0 = 1;
		
		if (i0 == 1 && mostHatedHI == attacker && getNpcIntAIParam(npc, "ShoutTarget") != 0 && Rnd.get(100) < 5)
			broadcastScriptEvent(npc, 10016, attacker.getObjectId(), 300);
		
		final L2Skill selfExplosion = getNpcSkillByType(npc, NpcSkillType.SELF_EXPLOSION);
		if (selfExplosion != null && hpRatio < 0.5 && Rnd.get(100) < 5)
		{
			if ((10 - (int) (hpRatio * 10) > Rnd.get(100)))
				npc.getAI().addCastDesire(npc, selfExplosion, 3000000);
		}
		
		final int isTransform = getNpcIntAIParam(npc, "IsTransform");
		if (isTransform > 0)
		{
			switch (npc._param3)
			{
				case 0:
					if (npc._param3 < isTransform && hpRatio < 0.7 && hpRatio > 0.5 && Rnd.get(100) < 30)
					{
						switch (Rnd.get(3))
						{
							case 0:
								npc.broadcastNpcSay(NpcStringId.ID_1000406);
								break;
							
							case 1:
								npc.broadcastNpcSay(NpcStringId.ID_1000407);
								break;
							
							case 2:
								npc.broadcastNpcSay(NpcStringId.ID_1000408);
								break;
						}
						
						createOnePrivateEx(npc, getNpcIntAIParam(npc, "step1"), npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 0, false, 1000, attacker.getObjectId(), 1);
						npc.deleteMe();
					}
					break;
				
				case 1:
					if (npc._param3 < isTransform && hpRatio < 0.5 && hpRatio > 0.3 && Rnd.get(100) < 20)
					{
						switch (Rnd.get(3))
						{
							case 0:
								npc.broadcastNpcSay(NpcStringId.ID_1000409);
								break;
							
							case 1:
								npc.broadcastNpcSay(NpcStringId.ID_1000410);
								break;
							
							case 2:
								npc.broadcastNpcSay(NpcStringId.ID_1000411);
								break;
						}
						
						createOnePrivateEx(npc, getNpcIntAIParam(npc, "step2"), npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 0, false, 1000, attacker.getObjectId(), 2);
						npc.deleteMe();
					}
					break;
				
				case 2:
					if (npc._param3 < isTransform && hpRatio < 0.3 && hpRatio > 0.05 && Rnd.get(100) < 10)
					{
						switch (Rnd.get(3))
						{
							case 0:
								npc.broadcastNpcSay(NpcStringId.ID_1000412);
								break;
							
							case 1:
								npc.broadcastNpcSay(NpcStringId.ID_1000413);
								break;
							
							case 2:
								npc.broadcastNpcSay(NpcStringId.ID_1000414);
								break;
						}
						
						createOnePrivateEx(npc, getNpcIntAIParam(npc, "step3"), npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 0, false, 1000, attacker.getObjectId(), 3);
						npc.deleteMe();
					}
					break;
			}
		}
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		if (getNpcIntAIParam(called, "AttackLowHP") == 1 && attacker.getStatus().getHpRatio() < 0.3 && Rnd.get(100) < 3)
		{
			int i0 = 0;
			if (called.getAI().getHateList().getMostHatedCreature() != null)
				i0 = 1;
			
			if (i0 == 1)
			{
				if (called.getAI().getHateList().getMostHatedCreature() != attacker)
				{
					called.getAI().getHateList().clear();
					
					if (attacker instanceof Playable)
					{
						double f0 = getHateRatio(called, attacker);
						f0 = (((1.0 * damage) / (called.getStatus().getLevel() + 7)) + ((f0 / 100) * ((1.0 * damage) / (called.getStatus().getLevel() + 7))));
						
						if (called.getAI().getHateList().isEmpty())
							called.getAI().getHateList().addHateInfo(attacker, (f0 * 100) + 300);
						else
							called.getAI().getHateList().addHateInfo(attacker, (f0 * 100));
					}
				}
			}
		}
		
		final IntentionType currentIntention = called.getAI().getCurrentIntention().getType();
		if (getNpcIntAIParam(called, "AttackLowLevel") == 1 && currentIntention != IntentionType.ATTACK && currentIntention != IntentionType.CAST)
			startQuestTimer("1", called, null, 7000);
		
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (!(creature instanceof Playable))
			return;
		
		if (npc.isDead())
			return;
		
		if (getNpcIntAIParam(npc, "AttackLowLevel") == 1)
		{
			final IntentionType currentIntention = npc.getAI().getCurrentIntention().getType();
			if ((currentIntention == IntentionType.ATTACK || currentIntention == IntentionType.CAST) && npc.distance2D(creature) < 300)
			{
				if ((creature.getStatus().getLevel() + 15) < npc.getStatus().getLevel())
				{
					npc.getAI().getHateList().clear();
					npc.getAI().getHateList().addHateInfo(creature, (7 * 100));
				}
				
				startQuestTimer("1", npc, null, 7000);
			}
		}
		
		super.onSeeCreature(npc, creature);
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		if ((caster.getStatus().getLevel() + 15) < npc.getStatus().getLevel())
		{
			npc.getAI().getHateList().clear();
			
			if (skill.getAggroPoints() > 0 || skill.getPower(npc) > 0 || skill.isOffensive())
			{
				final Creature mostHated = npc.getAI().getAggroList().getMostHatedCreature();
				final IntentionType currentIntention = npc.getAI().getCurrentIntention().getType();
				final double skillPower = Math.max(Math.max(skill.getAggroPoints(), skill.getPower(npc)), 20);
				
				double f0 = getHateRatio(npc, caster);
				f0 = (((1.0 * skillPower) / (npc.getStatus().getLevel() + 7)) + ((f0 / 100) * ((1.0 * skillPower) / (npc.getStatus().getLevel() + 7))));
				
				if (mostHated != null && mostHated == caster && currentIntention == IntentionType.ATTACK)
					npc.getAI().getHateList().addHateInfo(caster, (f0 * 150));
				else
					npc.getAI().getHateList().addHateInfo(caster, (f0 * 75));
			}
		}
	}
	
	@Override
	public void onScriptEvent(Npc npc, int eventId, int arg1, int arg2)
	{
		if (eventId == 10016)
		{
			if (Rnd.get(100) < 50)
			{
				npc.getAI().getHateList().clear();
				
				final Creature c0 = (Creature) World.getInstance().getObject(arg1);
				if (c0 instanceof Playable)
					npc.getAI().getHateList().addHateInfo(c0, 200);
			}
		}
		else if (eventId == 10020)
		{
			final Creature c0 = (Creature) World.getInstance().getObject(arg1);
			if (c0 != null)
				npc.lookNeighbor(500);
		}
	}
	
	@Override
	public void onUseSkillFinished(Npc npc, Creature creature, L2Skill skill, boolean success)
	{
		final L2Skill selfExplosion = getNpcSkillByType(npc, NpcSkillType.SELF_EXPLOSION);
		if (selfExplosion != null && skill.getId() == selfExplosion.getId())
			npc.doDie(npc);
	}
}
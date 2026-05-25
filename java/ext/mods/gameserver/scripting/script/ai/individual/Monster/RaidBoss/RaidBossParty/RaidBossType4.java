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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.RaidBoss.RaidBossParty;

import ext.mods.commons.random.Rnd;

import ext.mods.Config;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.skills.L2Skill;

public class RaidBossType4 extends RaidBossParty
{
	public RaidBossType4()
	{
		super("ai/individual/Monster/RaidBoss/RaidBossAlone/RaidBossParty/RaidBossType4");
	}
	
	public RaidBossType4(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		25319,
		25325,
		25290,
		25306,
		25293,
		25349,
		25140,
		25054,
		25245,
		25453,
		25252,
		25309,
		25316,
		25013,
		25023,
		25038,
		25047,
		25064,
		25076,
		25092,
		25103,
		25122,
		25152,
		25176,
		25192,
		25205,
		25230,
		25266,
		25354,
		25369,
		25380,
		25385,
		25398,
		25415,
		25420,
		25423,
		25441,
		25450,
		25467,
		25478,
		25493,
		25498,
		29037,
		25509,
		29062
	};
	
	@Override
	public void onPartyAttacked(Npc caller, Npc called, Creature target, int damage)
	{
		if (target instanceof Playable)
		{
			final Creature topDesireTarget = called.getAI().getTopDesireTarget();
			if (topDesireTarget != null)
			{
				if (target != topDesireTarget && called.distance2D(target) < 150 && called.distance2D(topDesireTarget) < 150)
				{
					final L2Skill selfRangeDebuff_a = getNpcSkillByType(called, NpcSkillType.SELF_RANGE_DEBUFF_A);
					if (getAbnormalLevel(target, selfRangeDebuff_a) == -1 && getAbnormalLevel(topDesireTarget, selfRangeDebuff_a) == -1 && Rnd.get(2) < 1)
						called.getAI().addCastDesire(called, selfRangeDebuff_a, 1000000);
					
					final L2Skill selfRangeDebuffAnother_a = getNpcSkillByType(called, NpcSkillType.SELF_RANGE_DEBUFF_ANOTHER_A);
					if (getAbnormalLevel(target, selfRangeDebuffAnother_a) == -1 && getAbnormalLevel(topDesireTarget, selfRangeDebuffAnother_a) == -1 && Rnd.get(5) < 1)
						called.getAI().addCastDesire(called, selfRangeDebuffAnother_a, 1000000);
				}
				
				if (Rnd.get(75) < 1)
				{
					final L2Skill DDMagic_a = getNpcSkillByType(called, NpcSkillType.DD_MAGIC_A);
					called.getAI().addCastDesire(target, DDMagic_a, 1000000);
				}
			}
		}
		super.onPartyAttacked(caller, called, target, damage);
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		final Creature topDesireTarget = npc.getAI().getTopDesireTarget();
		if (topDesireTarget != null)
		{
			if (caster != topDesireTarget && npc.distance2D(caster) < 150 && npc.distance2D(topDesireTarget) < 150)
			{
				final L2Skill selfRangeDebuff_a = getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_DEBUFF_A);
				if (getAbnormalLevel(caster, selfRangeDebuff_a) == -1 && getAbnormalLevel(topDesireTarget, selfRangeDebuff_a) == -1 && Rnd.get(2) < 1)
					npc.getAI().addCastDesire(npc, selfRangeDebuff_a, 1000000);
				
				final L2Skill selfRangeDebuffAnother_a = getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_DEBUFF_ANOTHER_A);
				if (getAbnormalLevel(caster, selfRangeDebuffAnother_a) == -1 && getAbnormalLevel(topDesireTarget, selfRangeDebuffAnother_a) == -1 && Rnd.get(5) < 1)
					npc.getAI().addCastDesire(npc, selfRangeDebuffAnother_a, 1000000);
			}
			
			if (Rnd.get(75) < 1)
			{
				final L2Skill DDMagic_a = getNpcSkillByType(npc, NpcSkillType.DD_MAGIC_A);
				npc.getAI().addCastDesire(caster, DDMagic_a, 1000000);
			}
		}
		super.onSeeSpell(npc, caster, skill, targets, isPet);
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		if (npc.getNpcId() == 25325 && Config.BARAKIEL)
		{
			final Player player = killer.getActingPlayer();
			if (player != null)
			{
				final Party party = player.getParty();
				if (party != null)
				{
					for (Player members : player.getParty().getMembers())
					{
						if (!members.isIn3DRadius(npc, 2000))
						{
							members.sendMessage(player.getSysString(10_161));
							continue;
						}
						
						if (!members.isNoble())
						{
							members.setNoble(true, true);
							members.getInventory().addItem(7694, 1);
							members.sendMessage(player.getSysString(10_162));
						}
						
						members.sendMessage(player.getSysString(10_163));
						members.broadcastUserInfo();
					}
				}
				else
				{
					if (!player.isIn3DRadius(npc, 2000))
						player.sendMessage(player.getSysString(10_161));
					
					if (!player.isNoble())
					{
						player.setNoble(true, true);
						player.getInventory().addItem(7694, 1);
						player.sendMessage(player.getSysString(10_162));
					}
					
					player.sendMessage(player.getSysString(10_163));
					player.broadcastUserInfo();
				}
			}
		}
		super.onMyDying(npc, killer);
	}
}
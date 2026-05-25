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
package ext.mods.gameserver.enums.skills;

import java.lang.reflect.Constructor;

import ext.mods.commons.data.StatSet;

import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.skills.l2skills.L2SkillAppearance;
import ext.mods.gameserver.skills.l2skills.L2SkillChargeDmg;
import ext.mods.gameserver.skills.l2skills.L2SkillCreateItem;
import ext.mods.gameserver.skills.l2skills.L2SkillDefault;
import ext.mods.gameserver.skills.l2skills.L2SkillDrain;
import ext.mods.gameserver.skills.l2skills.L2SkillSeed;
import ext.mods.gameserver.skills.l2skills.L2SkillSiegeFlag;
import ext.mods.gameserver.skills.l2skills.L2SkillSignet;
import ext.mods.gameserver.skills.l2skills.L2SkillSignetCasttime;
import ext.mods.gameserver.skills.l2skills.L2SkillSpawn;
import ext.mods.gameserver.skills.l2skills.L2SkillSummon;
import ext.mods.gameserver.skills.l2skills.L2SkillTeleport;

public enum SkillType
{
	PDAM,
	FATAL,
	MDAM,
	CPDAMPERCENT,
	MANADAM,
	DOT,
	MDOT,
	DRAIN_SOUL,
	DRAIN(L2SkillDrain.class),
	DEATHLINK,
	BLOW,
	SIGNET(L2SkillSignet.class),
	SIGNET_CASTTIME(L2SkillSignetCasttime.class),
	SEED(L2SkillSeed.class),
	REAL_DAMAGE,
	
	BLEED,
	POISON,
	STUN,
	ROOT,
	CONFUSION,
	FEAR,
	SLEEP,
	MUTE,
	PARALYZE,
	WEAKNESS,
	
	HEAL,
	MANAHEAL,
	COMBATPOINTHEAL,
	HOT,
	MPHOT,
	BALANCE_LIFE,
	HEAL_STATIC,
	MANARECHARGE,
	HEAL_PERCENT,
	MANAHEAL_PERCENT,
	
	GIVE_SP,
	
	AGGDAMAGE,
	AGGREDUCE,
	AGGREMOVE,
	AGGREDUCE_CHAR,
	AGGDEBUFF,
	
	FISHING,
	PUMPING,
	REELING,
	
	UNLOCK,
	UNLOCK_SPECIAL,
	DELUXE_KEY_UNLOCK,
	ENCHANT_ARMOR,
	ENCHANT_WEAPON,
	SOULSHOT,
	SPIRITSHOT,
	SIEGE_FLAG(L2SkillSiegeFlag.class),
	TAKE_CASTLE,
	SOW,
	HARVEST,
	GET_PLAYER,
	DUMMY,
	INSTANT_JUMP,
	
	COMMON_CRAFT,
	DWARVEN_CRAFT,
	CREATE_ITEM(L2SkillCreateItem.class),
	EXTRACTABLE,
	EXTRACTABLE_FISH,
	
	SUMMON(L2SkillSummon.class),
	FEED_PET,
	STRIDER_SIEGE_ASSAULT,
	ERASE,
	BETRAY,
	SPAWN(L2SkillSpawn.class),
	
	CANCEL,
	MAGE_BANE,
	WARRIOR_BANE,
	
	NEGATE,
	CANCEL_DEBUFF,
	
	BUFF,
	SONIC,
	FORCE,
	DEBUFF,
	PASSIVE,
	CONT,
	
	RESURRECT,
	CHARGEDAM(L2SkillChargeDmg.class),
	LUCK,
	RECALL(L2SkillTeleport.class),
	TELEPORT(L2SkillTeleport.class),
	SUMMON_FRIEND,
	SUMMON_PARTY,
	SUMMON_CREATURE,
	REFLECT,
	SPOIL,
	SWEEP,
	FAKE_DEATH,
	BEAST_FEED,
	FUSION,
	
	CHANGE_APPEARANCE(L2SkillAppearance.class),
	
	COREDONE,
	
	NOTDONE;
	
	private final Class<? extends L2Skill> _class;
	
	public L2Skill makeSkill(StatSet set)
	{
		try
		{
			Constructor<? extends L2Skill> c = _class.getConstructor(StatSet.class);
			
			return c.newInstance(set);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private SkillType()
	{
		_class = L2SkillDefault.class;
	}
	
	private SkillType(Class<? extends L2Skill> classType)
	{
		_class = classType;
	}
}
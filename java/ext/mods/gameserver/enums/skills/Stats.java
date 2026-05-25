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

import java.util.NoSuchElementException;

public enum Stats
{
	MAX_HP("maxHp", true),
	MAX_MP("maxMp", true),
	MAX_CP("maxCp", true),
	REGENERATE_HP_RATE("regHp", false),
	REGENERATE_CP_RATE("regCp", false),
	REGENERATE_MP_RATE("regMp", false),
	RECHARGE_MP_RATE("gainMp", false),
	HEAL_EFFECTIVNESS("gainHp", false),
	HEAL_PROFICIENCY("giveHp", false),
	
	POWER_DEFENCE("pDef", true),
	MAGIC_DEFENCE("mDef", true),
	POWER_ATTACK("pAtk", true),
	MAGIC_ATTACK("mAtk", true),
	POWER_ATTACK_SPEED("pAtkSpd", true),
	MAGIC_ATTACK_SPEED("mAtkSpd", true),
	MAGIC_REUSE_RATE("mReuse", false),
	P_REUSE("pReuse", false),
	SHIELD_DEFENCE("sDef", true),
	SHIELD_DEFENCE_ANGLE("shieldDefAngle", false),
	SHIELD_RATE("rShld", false),
	
	CRITICAL_DAMAGE("cAtk", false),
	CRITICAL_DAMAGE_POS("cAtkPos", false),
	CRITICAL_DAMAGE_ADD("cAtkAdd", false),
	
	PVP_PHYSICAL_DMG("pvpPhysDmg", false),
	PVP_MAGICAL_DMG("pvpMagicalDmg", false),
	PVP_PHYS_SKILL_DMG("pvpPhysSkillsDmg", false),
	PVP_PHYS_SKILL_DEF("pvpPhysSkillsDef", false),
	
	EVASION_RATE("rEvas", false),
	P_SKILL_EVASION("pSkillEvas", false),
	CRITICAL_RATE("rCrit", false),
	BLOW_RATE("blowRate", false),
	LETHAL_RATE("lethalRate", false),
	MCRITICAL_RATE("mCritRate", false),
	ATTACK_CANCEL("cancel", false),
	
	ACCURACY_COMBAT("accCombat", false),
	POWER_ATTACK_RANGE("pAtkRange", false),
	POWER_ATTACK_ANGLE("pAtkAngle", false),
	ATTACK_COUNT_MAX("atkCountMax", false),
	
	RUN_SPEED("runSpd", false),
	
	STAT_STR("STR", true),
	STAT_CON("CON", true),
	STAT_DEX("DEX", true),
	STAT_INT("INT", true),
	STAT_WIT("WIT", true),
	STAT_MEN("MEN", true),
	
	BREATH("breath", false),
	FALL("fall", false),
	
	AGGRESSION("aggression", false),
	BLEED("bleed", false),
	POISON("poison", false),
	STUN("stun", false),
	ROOT("root", false),
	MOVEMENT("movement", false),
	CONFUSION("confusion", false),
	SLEEP("sleep", false),
	
	FIRE_RES("fireRes", false),
	WATER_RES("waterRes", false),
	WIND_RES("windRes", false),
	EARTH_RES("earthRes", false),
	HOLY_RES("holyRes", false),
	DARK_RES("darkRes", false),
	VALAKAS_RES("valakasRes", false),
	
	FIRE_POWER("firePower", false),
	WATER_POWER("waterPower", false),
	WIND_POWER("windPower", false),
	EARTH_POWER("earthPower", false),
	HOLY_POWER("holyPower", false),
	DARK_POWER("darkPower", false),
	VALAKAS_POWER("valakasPower", false),
	
	BLEED_VULN("bleedVuln", false),
	POISON_VULN("poisonVuln", false),
	STUN_VULN("stunVuln", false),
	PARALYZE_VULN("paralyzeVuln", false),
	ROOT_VULN("rootVuln", false),
	SLEEP_VULN("sleepVuln", false),
	DAMAGE_ZONE_VULN("damageZoneVuln", false),
	CRIT_VULN("critVuln", false),
	CANCEL_VULN("cancelVuln", false),
	DERANGEMENT_VULN("derangementVuln", false),
	DEBUFF_VULN("debuffVuln", false),
	
	SWORD_WPN_VULN("swordWpnVuln", false),
	BLUNT_WPN_VULN("bluntWpnVuln", false),
	DAGGER_WPN_VULN("daggerWpnVuln", false),
	BOW_WPN_VULN("bowWpnVuln", false),
	POLE_WPN_VULN("poleWpnVuln", false),
	DUAL_WPN_VULN("dualWpnVuln", false),
	DUALFIST_WPN_VULN("dualFistWpnVuln", false),
	BIGSWORD_WPN_VULN("bigSwordWpnVuln", false),
	BIGBLUNT_WPN_VULN("bigBluntWpnVuln", false),
	
	REFLECT_DAMAGE_PERCENT("reflectDam", false),
	REFLECT_SKILL_MAGIC("reflectSkillMagic", false),
	REFLECT_SKILL_PHYSIC("reflectSkillPhysic", false),
	COUNTER_SKILL_PHYSICAL("counterSkill", false),
	ABSORB_DAMAGE_PERCENT("absorbDam", false),
	TRANSFER_DAMAGE_PERCENT("transDam", false),
	
	PATK_PLANTS("pAtk-plants", false),
	PATK_INSECTS("pAtk-insects", false),
	PATK_ANIMALS("pAtk-animals", false),
	PATK_BEASTS("pAtk-beasts", false),
	PATK_DRAGONS("pAtk-dragons", false),
	PATK_GIANTS("pAtk-giants", false),
	PATK_MCREATURES("pAtk-magicCreature", false),
	
	PDEF_PLANTS("pDef-plants", false),
	PDEF_INSECTS("pDef-insects", false),
	PDEF_ANIMALS("pDef-animals", false),
	PDEF_BEASTS("pDef-beasts", false),
	PDEF_DRAGONS("pDef-dragons", false),
	PDEF_GIANTS("pDef-giants", false),
	PDEF_MCREATURES("pDef-magicCreature", false),
	
	WEIGHT_LIMIT("weightLimit", false),
	WEIGHT_PENALTY("weightPenalty", false),
	INV_LIM("inventoryLimit", false),
	WH_LIM("whLimit", false),
	FREIGHT_LIM("FreightLimit", false),
	P_SELL_LIM("PrivateSellLimit", false),
	P_BUY_LIM("PrivateBuyLimit", false),
	REC_D_LIM("DwarfRecipeLimit", false),
	REC_C_LIM("CommonRecipeLimit", false),
	
	PHYSICAL_MP_CONSUME_RATE("PhysicalMpConsumeRate", false),
	MAGICAL_MP_CONSUME_RATE("MagicalMpConsumeRate", false),
	DANCE_MP_CONSUME_RATE("DanceMpConsumeRate", false),
	
	XP_RATE("xpGain", false),
	SP_RATE("spGain", false),
	DROP_RATE("dropRate", false),
	SPOIL_RATE("spoilRate", false),
	CURRENCY_RATE("adenaRate", false),
	
	SKILL_MASTERY("skillMastery", false);
	
	public static final Stats[] VALUES = values();
	
	public static final int NUM_STATS = VALUES.length;
	
	private Stats(String name, boolean cantBeNegative)
	{
		_name = name;
		_cantBeNegative = cantBeNegative;
	}
	
	private String _name;
	
	private boolean _cantBeNegative;
	
	public String getName()
	{
		return _name;
	}
	
	public boolean cantBeNegative()
	{
		return _cantBeNegative;
	}
	
	public static Stats valueOfXml(String name)
	{
		name = name.intern();
		for (Stats stat : VALUES)
		{
			if (stat.getName().equals(name))
				return stat;
		}
		throw new NoSuchElementException("Unknown name '" + name + "' for enum Stats");
	}
}
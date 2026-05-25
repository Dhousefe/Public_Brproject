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

public enum EffectType
{
	BLOCK_BUFF,
	BLOCK_DEBUFF,
	
	BUFF,
	DEBUFF,
	
	CANCEL,
	
	CANCEL_DEBUFF,
	NEGATE,
	
	CLAN_GATE,
	CHANCE_SKILL_TRIGGER,
	INCREASE_CHARGES,
	
	DMG_OVER_TIME,
	HEAL,
	HEAL_OVER_TIME,
	
	MANA_DMG_OVER_TIME,
	MANA_HEAL,
	MANA_HEAL_OVER_TIME,
	
	ABORT_CAST,
	BLUFF,
	BETRAY,
	STUN,
	ROOT,
	SLEEP,
	MUTE,
	PHYSICAL_MUTE,
	SILENCE_MAGIC_PHYSICAL,
	FEAR,
	PARALYZE,
	PETRIFICATION,
	IMMOBILE_UNTIL_ATTACKED,
	STUN_SELF,
	CONFUSION,
	DISTRUST,
	RANDOMIZE_HATE,
	
	FAKE_DEATH,
	SILENT_MOVE,
	
	POLEARM_TARGET_SINGLE,
	
	SEED,
	SPOIL,
	
	REMOVE_TARGET,
	TARGET_ME,
	
	RELAXING,
	NOBLESSE_BLESSING,
	PROTECTION_BLESSING,
	FUSION,
	CHARM_OF_COURAGE,
	CHARM_OF_LUCK,
	INVINCIBLE,
	PHOENIX_BLESSING,
	
	THROW_UP,
	
	SIGNET_GROUND,
	SIGNET_EFFECT;
	
	public static boolean isntCancellable(EffectType type)
	{
		return type == CHARM_OF_COURAGE || type == CHARM_OF_LUCK || type == NOBLESSE_BLESSING || type == PROTECTION_BLESSING;
	}
}
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
package ext.mods.gameserver.enums.actors;

import ext.mods.gameserver.enums.skills.Stats;

public enum NpcRace
{
	DUMMY(null, null, -1),
	UNDEAD(null, null, 4290),
	MAGIC_CREATURE(Stats.PATK_MCREATURES, Stats.PDEF_MCREATURES, 4291),
	BEAST(Stats.PATK_BEASTS, Stats.PDEF_BEASTS, 4292),
	ANIMAL(Stats.PATK_ANIMALS, Stats.PDEF_ANIMALS, 4293),
	PLANT(Stats.PATK_PLANTS, Stats.PDEF_PLANTS, 4294),
	HUMANOID(null, null, 4295),
	SPIRIT(null, null, 4296),
	ANGEL(null, null, 4297),
	DEMON(null, null, 4298),
	DRAGON(Stats.PATK_DRAGONS, Stats.PDEF_DRAGONS, 4299),
	GIANT(Stats.PATK_GIANTS, Stats.PDEF_GIANTS, 4300),
	BUG(Stats.PATK_INSECTS, Stats.PDEF_INSECTS, 4301),
	FAIRIE(null, null, 4302),
	HUMAN(null, null, -1),
	ELVE(null, null, -1),
	DARKELVE(null, null, -1),
	ORC(null, null, -1),
	DWARVE(null, null, -1),
	OTHER(null, null, -1),
	NON_LIVING_BEING(null, null, -1),
	SIEGE_WEAPON(null, null, -1),
	DEFENDING_ARMY(null, null, -1),
	MERCENARIE(null, null, -1),
	UNKNOWN_CREATURE(null, null, -1);
	
	public static final NpcRace[] VALUES = values();
	
	private NpcRace(Stats atkStat, Stats resStat, int secondarySkillId)
	{
		_atkStat = atkStat;
		_resStat = resStat;
		_secondarySkillId = secondarySkillId;
	}
	
	private Stats _atkStat;
	private Stats _resStat;
	private int _secondarySkillId;
	
	public Stats getAtkStat()
	{
		return _atkStat;
	}
	
	public Stats getResStat()
	{
		return _resStat;
	}
	
	public int getSecondarySkillId()
	{
		return _secondarySkillId;
	}
	
	public static NpcRace retrieveBySecondarySkillId(int skillId)
	{
		for (NpcRace nr : VALUES)
		{
			if (nr.getSecondarySkillId() == skillId)
				return nr;
		}
		return DUMMY;
	}
}
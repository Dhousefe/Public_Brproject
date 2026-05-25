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
package ext.mods.gameserver.model.entity.autofarm;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import ext.mods.gameserver.enums.skills.SkillTargetType;
import ext.mods.gameserver.enums.skills.SkillType;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.network.serverpackets.ExServerPrimitive.Point;
import ext.mods.gameserver.skills.L2Skill;

public class Util
{
	public static String getRemainingTokens(StringTokenizer st)
	{
		final StringBuilder remainingTokens = new StringBuilder();
		while (st.hasMoreTokens())
			remainingTokens.append(st.nextToken() + " ");
		
		return remainingTokens.toString().trim();
	}
	
	public static boolean isSkillCompatible(L2Skill skill)
	{
		if (!skill.isActive() || skill.isSiegeSummonSkill())
			return false;
		
		if (skill.getTargetType().name().contains("CORPSE"))
			return false;
		
		if (skill.getSkillType() == SkillType.SLEEP && skill.getTargetType() == SkillTargetType.ONE)
			return false;
		
		switch (skill.getSkillType())
		{
			case SUMMON:
			case SUMMON_CREATURE:
			case SUMMON_FRIEND:
			case SUMMON_PARTY:
			case COMMON_CRAFT:
			case DWARVEN_CRAFT:
			case TELEPORT:
			case RECALL:
			case RESURRECT:
			case TAKE_CASTLE:
			case SIEGE_FLAG:
			case SEED:
			case SIGNET:
			case SIGNET_CASTTIME:
			case FUSION:
			case STRIDER_SIEGE_ASSAULT:
			case UNLOCK:
				return false;
		}
		
		switch (skill.getTargetType())
		{
			case SUMMON:
				return false;
		}

		switch (skill.getName())
		{
			case "Aura Symphony":
			case "Inferno":
			case "Blizzard":
			case "Demon Wind":
			case "Elemental Assault":
			case "Elemental Symphony":
			case "Elemental Storm":
			case "Harmony of Noblesse":
			case "Symphony of Noblesse":
				return false;
				
			case "Clan Gate":
				return false;
				
			case "Wyvern Aegis":
				return false;
		}
		
		return true;
	}
	
	public static Comparator<String> getMonsterComparator(Set<String> targets)
	{
		return (m1, m2) ->
		{
		    boolean m1Selected = targets.contains(m1);
		    boolean m2Selected = targets.contains(m2);

		    if (m1Selected && !m2Selected)
		        return -1;
		    else if (!m1Selected && m2Selected)
		        return 1;
		    else
		        return m1.compareTo(m2);
		};
	}
	
	public static Comparator<L2Skill> getSkillComparator(Collection<Integer> skills)
	{
		return (s1, s2) ->
		{
		    boolean m1Selected = skills.contains(s1.getId());
		    boolean m2Selected = skills.contains(s2.getId());

		    if (m1Selected && !m2Selected)
		        return -1;
		    else if (!m1Selected && m2Selected)
		        return 1;
		    else
		        return s1.getName().compareTo(s2.getName());
		};
	}
	
	public static boolean isNodeListEquals(Collection<Point> list1, List<Location> list2)
	{
		if (list1.size() != list2.size())
			return false;
		
		int indice = 0;
		for (Point point : list1)
		{
			final Location loc = list2.get(indice);
			if (point.getX() != loc.getX() || point.getY() != loc.getY() || point.getZ() != loc.getZ())
				return false;
			
			indice++;
		}
		
		return true;
	}
}
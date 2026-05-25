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
package ext.mods.gameserver.model.actor.template;

import java.util.List;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.data.xml.ItemData;
import ext.mods.gameserver.enums.actors.ClassId;
import ext.mods.gameserver.enums.actors.ClassRace;
import ext.mods.gameserver.enums.actors.Sex;
import ext.mods.gameserver.model.holder.skillnode.GeneralSkillNode;
import ext.mods.gameserver.model.item.kind.Weapon;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.records.NewbieItem;
import ext.mods.gameserver.model.records.custom.Macros;

/**
 * A datatype extending {@link CreatureTemplate}, used to retain Player template informations such as classId, specific collision values for female, hp/mp/cp tables, etc.<br>
 * <br>
 * Since each PlayerTemplate is associated to a {@link ClassId}, it is also used as a container for {@link GeneralSkillNode}s this class can use.<br>
 * <br>
 * Finally, it holds starter equipment (under an int array of itemId) and initial spawn {@link Location} for newbie templates.
 */
public class PlayerTemplate extends CreatureTemplate
{
	private final ClassId _classId;
	
	private final int[] _safeFallHeight;
	
	private final int _baseSwimSpd;
	
	private final double _collisionRadiusFemale;
	private final double _collisionHeightFemale;
	
	private final List<Location> _spawnLocations;
	
	private final int _classBaseLevel;
	
	private final double[] _hpTable;
	private final double[] _mpTable;
	private final double[] _cpTable;
	
	private final double[] _hpRegenTable;
	private final double[] _mpRegenTable;
	private final double[] _cpRegenTable;
	
	private final String _title;
	private final int _startLvl;
	
	private final List<Macros> _macros;
	private final List<NewbieItem> _items;
	private final List<GeneralSkillNode> _skills;
	
	private final String[] _buffs;
	
	private final Weapon _fists;
	
	public PlayerTemplate(StatSet set)
	{
		super(set);
		
		_classId = ClassId.VALUES[set.getInteger("id")];
		
		_safeFallHeight = set.getIntegerArray("safeFallHeight");
		
		_baseSwimSpd = set.getInteger("swimSpd", 1);
		
		_collisionRadiusFemale = set.getDouble("radiusFemale");
		_collisionHeightFemale = set.getDouble("heightFemale");
		
		_spawnLocations = set.getList("spawnLocations");
		
		_classBaseLevel = set.getInteger("baseLvl");
		
		_hpTable = set.getDoubleArray("hpTable");
		_mpTable = set.getDoubleArray("mpTable");
		_cpTable = set.getDoubleArray("cpTable");
		
		_hpRegenTable = set.getDoubleArray("hpRegenTable");
		_mpRegenTable = set.getDoubleArray("mpRegenTable");
		_cpRegenTable = set.getDoubleArray("cpRegenTable");
		
		_title = set.getString("title", "");
		_startLvl = set.getInteger("startLvl", 1);
		
		_macros = set.getList("macros");
		_items = set.getList("items");
		_skills = set.getList("skills");
		
		_buffs = set.getStringArray("buffs", new String[0]);
		
		_fists = (Weapon) ItemData.getInstance().getTemplate(set.getInteger("fists"));
	}
	
	public final ClassId getClassId()
	{
		return _classId;
	}
	
	public final ClassRace getRace()
	{
		return _classId.getRace();
	}
	
	public final String getClassName()
	{
		return _classId.toString();
	}
	
	public final int getSafeFallHeight(Sex sex)
	{
		return (sex == Sex.MALE) ? _safeFallHeight[1] : _safeFallHeight[0];
	}
	
	public final int getBaseSwimSpeed()
	{
		return _baseSwimSpd;
	}
	
	/**
	 * @param sex
	 * @return : height depends on sex.
	 */
	public double getCollisionRadiusBySex(Sex sex)
	{
		return (sex == Sex.MALE) ? _collisionRadius : _collisionRadiusFemale;
	}
	
	/**
	 * @param sex
	 * @return : height depends on sex.
	 */
	public double getCollisionHeightBySex(Sex sex)
	{
		return (sex == Sex.MALE) ? _collisionHeight : _collisionHeightFemale;
	}
	
	public final Location getRandomSpawn()
	{
		final Location loc = Rnd.get(_spawnLocations);
		return (loc == null) ? Location.DUMMY_LOC : loc;
	}
	
	public final int getClassBaseLevel()
	{
		return _classBaseLevel;
	}
	
	@Override
	public final double getBaseHpMax(int level)
	{
		return _hpTable[level - 1];
	}
	
	@Override
	public final double getBaseMpMax(int level)
	{
		return _mpTable[level - 1];
	}
	
	public final double getBaseCpMax(int level)
	{
		return _cpTable[level - 1];
	}
	
	@Override
	public final double getBaseHpRegen(int level)
	{
		return _hpRegenTable[level - 1];
	}
	
	@Override
	public final double getBaseMpRegen(int level)
	{
		return _mpRegenTable[level - 1];
	}
	
	public final double getBaseCpRegen(int level)
	{
		return _cpRegenTable[level - 1];
	}
	
	public final String getTitle()
	{
		return _title;
	}
	
	public final int getStartLevel()
	{
		return _startLvl;
	}
	
	public final List<Macros> getMacros()
	{
		return _macros;
	}
	
	/**
	 * @return the {@link List} of {@link NewbieItem}s holding the starter equipment informations for this {@link PlayerTemplate}.
	 */
	public final List<NewbieItem> getItems()
	{
		return _items;
	}
	
	/**
	 * @return the {@link List} of all available {@link GeneralSkillNode} for this {@link PlayerTemplate}.
	 */
	public final List<GeneralSkillNode> getSkills()
	{
		return _skills;
	}
	
	/**
	 * Find if the skill exists on skill tree.
	 * @param id : The skill id to check.
	 * @param level : The skill level to check.
	 * @return the associated {@link GeneralSkillNode} if a matching id/level is found on this {@link PlayerTemplate}, or null.
	 */
	public GeneralSkillNode findSkill(int id, int level)
	{
		return _skills.stream().filter(s -> s.getId() == id && s.getValue() == level).findFirst().orElse(null);
	}
	
	/**
	 * @return the {@link Weapon} used as fists for this {@link PlayerTemplate}.
	 */
	public final Weapon getFists()
	{
		return _fists;
	}
	
	public final String[] getBuffIds()
	{
		return _buffs;
	}
}
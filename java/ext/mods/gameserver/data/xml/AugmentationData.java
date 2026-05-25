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
package ext.mods.gameserver.data.xml;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;
import ext.mods.commons.random.Rnd;

import ext.mods.Config;
import ext.mods.gameserver.enums.skills.Stats;
import ext.mods.gameserver.model.Augmentation;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.network.clientpackets.AbstractRefinePacket;
import ext.mods.gameserver.skills.L2Skill;

import org.w3c.dom.Document;

/**
 * This class loads and stores :
 * <ul>
 * <li>{@link AugmentationStat} under 4 different tables of stats (pDef, etc)</li>
 * <li>Augmentation skills based on colors (blue, purple and red)</li>
 * </ul>
 * It is also used to generate new {@link Augmentation}, based on stored content.
 */
public class AugmentationData implements IXmlReader
{
	private static final int STAT_START = 1;
	private static final int STAT_END = 14560;
	private static final int STAT_BLOCKSIZE = 3640;
	private static final int STAT_SUBBLOCKSIZE = 91;
	private static final int STAT_NUM = 13;
	
	private static final byte[] STATS1_MAP = new byte[STAT_SUBBLOCKSIZE];
	private static final byte[] STATS2_MAP = new byte[STAT_SUBBLOCKSIZE];
	
	private static final int BLUE_START = 14561;
	private static final int SKILLS_BLOCKSIZE = 178;
	
	private static final int BASESTAT_STR = 16341;
	private static final int BASESTAT_CON = 16342;
	private static final int BASESTAT_INT = 16343;
	private static final int BASESTAT_MEN = 16344;
	
	private final List<List<AugmentationStat>> _augStats = new ArrayList<>(4);
	
	private final List<List<Integer>> _blueSkills = new ArrayList<>(10);
	private final List<List<Integer>> _purpleSkills = new ArrayList<>(10);
	private final List<List<Integer>> _redSkills = new ArrayList<>(10);
	
	private final Map<Integer, IntIntHolder> _allSkills = new HashMap<>();
	
	protected AugmentationData()
	{
		byte idx;
		
		for (idx = 0; idx < STAT_NUM; idx++)
		{
			STATS1_MAP[idx] = idx;
			STATS2_MAP[idx] = idx;
		}
		
		for (int i = 0; i < STAT_NUM; i++)
		{
			for (int j = i + 1; j < STAT_NUM; idx++, j++)
			{
				STATS1_MAP[idx] = (byte) i;
				STATS2_MAP[idx] = (byte) j;
			}
		}
		
		for (int i = 0; i < 4; i++)
			_augStats.add(new ArrayList<>());
		
		for (int i = 0; i < 10; i++)
		{
			_blueSkills.add(new ArrayList<>());
			_purpleSkills.add(new ArrayList<>());
			_redSkills.add(new ArrayList<>());
		}
		
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("xml/augmentation");
		LOGGER.info("Loaded {} sets of augmentation stats.", _augStats.size());
		
		final int blue = _blueSkills.stream().mapToInt(List::size).sum();
		final int purple = _purpleSkills.stream().mapToInt(List::size).sum();
		final int red = _redSkills.stream().mapToInt(List::size).sum();
		LOGGER.info("Loaded {} blue, {} purple and {} red Life-Stone skills.", blue, purple, red);
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode ->
		{
			forEach(listNode, "augmentation", augmentationNode ->
			{
				final StatSet set = parseAttributes(augmentationNode);
				final int augmentationId = set.getInteger("id");
				final int k = (augmentationId - BLUE_START) / SKILLS_BLOCKSIZE;
				
				switch (set.getString("type"))
				{
					case "blue" -> _blueSkills.get(k).add(augmentationId);
					case "purple" -> _purpleSkills.get(k).add(augmentationId);
					case "red" -> _redSkills.get(k).add(augmentationId);
				}
				_allSkills.put(augmentationId, new IntIntHolder(set.getInteger("skillId"), set.getInteger("skillLevel")));
			});
			
			forEach(listNode, "set", setNode ->
			{
				final int order = parseInteger(setNode.getAttributes(), "order");
				final List<AugmentationStat> statList = _augStats.get(order);
				forEach(setNode, "stat", statNode ->
				{
					final String statName = parseString(statNode.getAttributes(), "name");
					final List<Float> soloValues = new ArrayList<>();
					final List<Float> combinedValues = new ArrayList<>();
					forEach(statNode, "table", tableNode ->
					{
						final String tableName = parseString(tableNode.getAttributes(), "name");
						final StringTokenizer data = new StringTokenizer(tableNode.getFirstChild().getNodeValue());
						if ("#soloValues".equalsIgnoreCase(tableName))
							while (data.hasMoreTokens())
								soloValues.add(Float.parseFloat(data.nextToken()));
						else
							while (data.hasMoreTokens())
								combinedValues.add(Float.parseFloat(data.nextToken()));
					});
					
					final float[] soloValuesArr = new float[soloValues.size()];
					for (int i = 0; i < soloValuesArr.length; i++)
						soloValuesArr[i] = soloValues.get(i);
					
					final float[] combinedValuesArr = new float[combinedValues.size()];
					for (int i = 0; i < combinedValuesArr.length; i++)
						combinedValuesArr[i] = combinedValues.get(i);
					statList.add(new AugmentationStat(Stats.valueOfXml(statName), soloValuesArr, combinedValuesArr));
				});
			});
		});
	}
	
	public Augmentation generateRandomAugmentation(int lifeStoneLevel, int lifeStoneGrade)
	{
		
		int stat12 = 0;
		int stat34 = 0;
		boolean generateSkill = false;
		boolean generateGlow = false;
		
		lifeStoneLevel = Math.min(lifeStoneLevel, 9);
		
		switch (lifeStoneGrade)
		{
			case AbstractRefinePacket.GRADE_NONE -> {
				if (Rnd.get(1, 100) <= Config.AUGMENTATION_NG_SKILL_CHANCE)
					generateSkill = true;
				if (Rnd.get(1, 100) <= Config.AUGMENTATION_NG_GLOW_CHANCE)
					generateGlow = true;
			}
			
			case AbstractRefinePacket.GRADE_MID -> {
				if (Rnd.get(1, 100) <= Config.AUGMENTATION_MID_SKILL_CHANCE)
					generateSkill = true;
				if (Rnd.get(1, 100) <= Config.AUGMENTATION_MID_GLOW_CHANCE)
					generateGlow = true;
			}
			
			case AbstractRefinePacket.GRADE_HIGH -> {
				if (Rnd.get(1, 100) <= Config.AUGMENTATION_HIGH_SKILL_CHANCE)
					generateSkill = true;
				if (Rnd.get(1, 100) <= Config.AUGMENTATION_HIGH_GLOW_CHANCE)
					generateGlow = true;
			}
			
			case AbstractRefinePacket.GRADE_TOP -> {
				if (Rnd.get(1, 100) <= Config.AUGMENTATION_TOP_SKILL_CHANCE)
					generateSkill = true;
				if (Rnd.get(1, 100) <= Config.AUGMENTATION_TOP_GLOW_CHANCE)
					generateGlow = true;
			}
		}
		
		if (!generateSkill && Rnd.get(1, 100) <= Config.AUGMENTATION_BASESTAT_CHANCE)
			stat34 = Rnd.get(BASESTAT_STR, BASESTAT_MEN);
			
		int resultColor = Rnd.get(0, 100);
		if (stat34 == 0 && !generateSkill)
		{
			if (resultColor <= (15 * lifeStoneGrade) + 40)
				resultColor = 1;
			else
				resultColor = 0;
		}
		else
		{
			if (resultColor <= (10 * lifeStoneGrade) + 5 || stat34 != 0)
				resultColor = 3;
			else if (resultColor <= (10 * lifeStoneGrade) + 10)
				resultColor = 1;
			else
				resultColor = 2;
		}
		
		L2Skill skill = null;
		if (generateSkill)
		{
			stat34 = switch (resultColor)
			{
				case 1 -> _blueSkills.get(lifeStoneLevel).get(Rnd.get(0, _blueSkills.get(lifeStoneLevel).size() - 1));
				case 2 -> _purpleSkills.get(lifeStoneLevel).get(Rnd.get(0, _purpleSkills.get(lifeStoneLevel).size() - 1));
				case 3 -> _redSkills.get(lifeStoneLevel).get(Rnd.get(0, _redSkills.get(lifeStoneLevel).size() - 1));
				default -> stat34;
			};
			
			skill = _allSkills.get(stat34).getSkill();
		}
		
		
		int offset;
		if (stat34 == 0)
		{
			int temp = Rnd.get(2, 3);
			int colorOffset = resultColor * (10 * STAT_SUBBLOCKSIZE) + temp * STAT_BLOCKSIZE + 1;
			offset = (lifeStoneLevel * STAT_SUBBLOCKSIZE) + colorOffset;
			
			stat34 = Rnd.get(offset, offset + STAT_SUBBLOCKSIZE - 1);
			if (generateGlow && lifeStoneGrade >= 2)
				offset = (lifeStoneLevel * STAT_SUBBLOCKSIZE) + (temp - 2) * STAT_BLOCKSIZE + lifeStoneGrade * (10 * STAT_SUBBLOCKSIZE) + 1;
			else
				offset = (lifeStoneLevel * STAT_SUBBLOCKSIZE) + (temp - 2) * STAT_BLOCKSIZE + Rnd.get(0, 1) * (10 * STAT_SUBBLOCKSIZE) + 1;
		}
		else
		{
			if (!generateGlow)
				offset = (lifeStoneLevel * STAT_SUBBLOCKSIZE) + Rnd.get(0, 1) * STAT_BLOCKSIZE + 1;
			else
				offset = (lifeStoneLevel * STAT_SUBBLOCKSIZE) + Rnd.get(0, 1) * STAT_BLOCKSIZE + (lifeStoneGrade + resultColor) / 2 * (10 * STAT_SUBBLOCKSIZE) + 1;
		}
		stat12 = Rnd.get(offset, offset + STAT_SUBBLOCKSIZE - 1);
		
		return new Augmentation(((stat34 << 16) + stat12), skill);
	}
	
	/**
	 * Returns the stat and basestat boni for a given augmentation id
	 * @param augmentationId
	 * @return
	 */
	public List<AugStat> getAugStatsById(int augmentationId)
	{
		List<AugStat> temp = new ArrayList<>();
		int[] stats = new int[2];
		stats[0] = 0x0000FFFF & augmentationId;
		stats[1] = (augmentationId >> 16);
		
		for (int i = 0; i < 2; i++)
		{
			if (stats[i] >= STAT_START && stats[i] <= STAT_END)
			{
				int base = stats[i] - STAT_START;
				int color = base / STAT_BLOCKSIZE;
				int subblock = base % STAT_BLOCKSIZE;
				int level = subblock / STAT_SUBBLOCKSIZE;
				int stat = subblock % STAT_SUBBLOCKSIZE;
				
				byte stat1 = STATS1_MAP[stat];
				byte stat2 = STATS2_MAP[stat];
				
				if (stat1 == stat2)
				{
					AugmentationStat as = _augStats.get(color).get(stat1);
					temp.add(new AugStat(as.getStat(), as.getSingleStatValue(level)));
				}
				else
				{
					AugmentationStat as = _augStats.get(color).get(stat1);
					temp.add(new AugStat(as.getStat(), as.getCombinedStatValue(level)));
					
					as = _augStats.get(color).get(stat2);
					temp.add(new AugStat(as.getStat(), as.getCombinedStatValue(level)));
				}
			}
			else if (stats[i] >= BASESTAT_STR && stats[i] <= BASESTAT_MEN)
			{
				switch (stats[i])
				{
					case BASESTAT_STR -> temp.add(new AugStat(Stats.STAT_STR, 1.0f));
					case BASESTAT_CON -> temp.add(new AugStat(Stats.STAT_CON, 1.0f));
					case BASESTAT_INT -> temp.add(new AugStat(Stats.STAT_INT, 1.0f));
					case BASESTAT_MEN -> temp.add(new AugStat(Stats.STAT_MEN, 1.0f));
				}
			}
		}
		return temp;
	}
	
	public static class AugStat
	{
		private final Stats _stat;
		private final float _value;
		
		public AugStat(Stats stat, float value)
		{
			_stat = stat;
			_value = value;
		}
		
		public Stats getStat()
		{
			return _stat;
		}
		
		public float getValue()
		{
			return _value;
		}
	}
	
	public static class AugmentationStat
	{
		private final Stats _stat;
		private final int _singleSize;
		private final int _combinedSize;
		private final float[] _singleValues;
		private final float[] _combinedValues;
		
		public AugmentationStat(Stats stat, float[] singleValues, float[] combinedValues)
		{
			_stat = stat;
			_singleSize = singleValues.length;
			_singleValues = singleValues;
			_combinedSize = combinedValues.length;
			_combinedValues = combinedValues;
		}
		
		public int getSingleStatSize()
		{
			return _singleSize;
		}
		
		public int getCombinedStatSize()
		{
			return _combinedSize;
		}
		
		public float getSingleStatValue(int i)
		{
			if (i >= _singleSize || i < 0)
				return _singleValues[_singleSize - 1];
			
			return _singleValues[i];
		}
		
		public float getCombinedStatValue(int i)
		{
			if (i >= _combinedSize || i < 0)
				return _combinedValues[_combinedSize - 1];
			
			return _combinedValues[i];
		}
		
		public Stats getStat()
		{
			return _stat;
		}
	}
	
	public Map<Integer, IntIntHolder> getAllSkills()
	{
		return _allSkills;
	}
	
	public static final AugmentationData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final AugmentationData INSTANCE = new AugmentationData();
	}
}
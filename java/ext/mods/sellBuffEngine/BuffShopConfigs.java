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
package ext.mods.sellBuffEngine;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import ext.mods.gameserver.enums.actors.ClassId;
import ext.mods.gameserver.enums.skills.SkillTargetType;

public class BuffShopConfigs
{
	private static final String BUFF_SHOP_CONFIG_FILE = "./config/mods.properties";
	private static final Logger _log = Logger.getLogger(BuffShopConfigs.class.getName());
	
	public static List<Integer> BUFFSHOP_ALLOW_CLASS = new ArrayList<>();
	public static List<Integer> BUFFSHOP_FORBIDDEN_SKILL = new ArrayList<>();
	public static List<Integer> BUFFSHOP_ALLOWED_SELF_SKILLS = new ArrayList<>();
	public static List<Integer> BUFFSHOP_RESTRICTED_SUMMONS = new ArrayList<>();
	public static List<Integer> NON_REMOVABLE_BUFFS = new ArrayList<>();
	public static Set<ClassId> BUFFSHOP_SUMMON_BUYER_CLASSES = EnumSet.noneOf(ClassId.class);
	
	public static record SkillGrantRule(int requiredLevel, int skillId, int skillLevel)
	{
	}
	
	public static List<Integer> BUFFSHOP_REPLACEABLE_BUFFS = new ArrayList<>();
	
	public record Cost(int itemId, int count)
	{
	}
	
	public record SkillPath(int maxLevel, Map<Integer, List<Cost>> costsByLevel)
	{
	}
	
	public static final List<Integer> BUFFSHOP_ALLOW_CLASS_SKILLSHOP = new ArrayList<>();
	public static final List<Integer> SKILL_SHOP_ALLOWED_CLASSES = new ArrayList<>();
	public static final Map<ClassId, List<Integer>> SKILL_SHOP_AVAILABLE = new HashMap<>();
	public static final Map<Integer, SkillPath> SKILL_SHOP_PATHS = new HashMap<>();
	public static int SKILL_SHOP_REQUIRED_ITEM_ID;
	
	public static final Map<ClassId, List<SkillGrantRule>> BUFFSHOP_GRANT_SKILLS = new HashMap<>();
	
	public static final Map<String, List<Integer>> BUFFSHOP_CLASS_SPECIFIC_SKILLS = new HashMap<>();
	public static int BUFFSHOP_BUFFS_MAX_COUNT = 8;
	private final Properties DhousefeBuffProperties = new Properties();
	
	public static final List<ClassId> REFERENCE_SUMMONER_CLASSES = List.of(ClassId.WARLOCK, ClassId.ELEMENTAL_SUMMONER, ClassId.PHANTOM_SUMMONER);
	
	public void loadConfigs()
	{
		
		BUFFSHOP_ALLOW_CLASS_SKILLSHOP.clear();
		SKILL_SHOP_ALLOWED_CLASSES.clear();
		SKILL_SHOP_AVAILABLE.clear();
		SKILL_SHOP_PATHS.clear();
		
		SKILL_SHOP_REQUIRED_ITEM_ID = Integer.parseInt(DhousefeBuffProperties.getProperty("SkillShopRequiredItemId", "6622"));
		
		final File l2dsecsellbuff = new File(BUFF_SHOP_CONFIG_FILE);
		if (!l2dsecsellbuff.exists())
		{
			_log.log(Level.SEVERE, "BuffShop System: Configuration file not found: " + BUFF_SHOP_CONFIG_FILE + ". Using default values.");
			return;
		}
		try (InputStream is = new FileInputStream(l2dsecsellbuff))
		{
			DhousefeBuffProperties.load(is);
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "Error while loading BuffShop settings!", e);
			return;
		}
		
		try
		{
			BUFFSHOP_BUFFS_MAX_COUNT = Integer.parseInt(DhousefeBuffProperties.getProperty("MaxCount", "8"));
		}
		catch (NumberFormatException e)
		{
			_log.log(Level.SEVERE, "BuffShop System: Error parsing MaxCount. Using default value 8.");
			BUFFSHOP_BUFFS_MAX_COUNT = 8;
		}
		
		String[] classIds = DhousefeBuffProperties.getProperty("BuffShopAllowClassId", "").split(",");
		for (String id : classIds)
		{
			try
			{
				BUFFSHOP_ALLOW_CLASS.add(Integer.parseInt(id.trim()));
			}
			catch (NumberFormatException e)
			{
				_log.info("BuffShop System: Error parsing Class id '" + id + "' from property 'BuffShopAllowClassId'. Skipping.");
			}
		}
		
		String[] skillIds = DhousefeBuffProperties.getProperty("BuffShopForbiddenSkill", "").split(",");
		for (String id : skillIds)
		{
			try
			{
				BUFFSHOP_FORBIDDEN_SKILL.add(Integer.parseInt(id.trim()));
			}
			catch (NumberFormatException e)
			{
				_log.info("BuffShop System: Error parsing Skill id '" + id + "' from property 'BuffShopForbiddenSkill'. Skipping.");
			}
		}
		String[] allowedSelfSkillIds = DhousefeBuffProperties.getProperty("BuffShopAllowedSelfSkill", "").split(",");
		for (String id : allowedSelfSkillIds)
		{
			if (id.isEmpty())
			{
				continue;
			}
			try
			{
				BUFFSHOP_ALLOWED_SELF_SKILLS.add(Integer.parseInt(id.trim()));
			}
			catch (NumberFormatException e)
			{
				_log.log(Level.SEVERE, "BuffShop System: Error parsing Skill id '{}' from property 'BuffShopAllowedSelfSkill'. Skipping.", id);
			}
		}
		
		BUFFSHOP_GRANT_SKILLS.clear();
		
		final String grantPrefix = "GrantSkillsOnManage.";
		
		for (String key : DhousefeBuffProperties.stringPropertyNames())
		{
			if (key.startsWith(grantPrefix))
			{
				try
				{
					String className = key.substring(grantPrefix.length()).toUpperCase();
					ClassId classIdKey = ClassId.valueOf(className);
					
					String value = DhousefeBuffProperties.getProperty(key, "");
					if (value.isEmpty())
						continue;
					
					List<SkillGrantRule> rules = new ArrayList<>();
					
					for (String ruleString : value.split(","))
					{
						ruleString = ruleString.trim();
						if (ruleString.isEmpty())
							continue;
						
						String[] ruleParts = ruleString.split(":");
						if (ruleParts.length == 3)
						{
							int reqLevel = Integer.parseInt(ruleParts[0]);
							int skillId = Integer.parseInt(ruleParts[1]);
							int skillLevel = Integer.parseInt(ruleParts[2]);
							rules.add(new SkillGrantRule(reqLevel, skillId, skillLevel));
						}
					}
					
					if (!rules.isEmpty())
					{
						BUFFSHOP_GRANT_SKILLS.put(classIdKey, rules);
					}
				}
				catch (Exception e)
				{
					_log.log(Level.SEVERE, "BuffShop System: Error parsing property '{}'. Please check format (LvlReq:SkillID:SkillLvl,...).", e);
				}
			}
		}
		
		BUFFSHOP_CLASS_SPECIFIC_SKILLS.clear();
		
		for (String key : DhousefeBuffProperties.stringPropertyNames())
		{
			
			if (key.startsWith("ClassSpecificSkills."))
			{
				try
				{
					String className = key.substring("ClassSpecificSkills.".length());
					
					String value = DhousefeBuffProperties.getProperty(key, "");
					
					List<Integer> classSkillIds = new ArrayList<>();
					
					for (String idStr : value.split(","))
					{
						if (!idStr.trim().isEmpty())
						{
							classSkillIds.add(Integer.parseInt(idStr.trim()));
						}
					}
					
					if (!classSkillIds.isEmpty())
					{
						BUFFSHOP_CLASS_SPECIFIC_SKILLS.put(className, classSkillIds);
					}
				}
				catch (Exception e)
				{
					_log.log(Level.SEVERE, "BuffShop System: Error parsing property '{}'. Please check the format.", e);
				}
			}
		}
		
		String[] restrictedSummonIds = DhousefeBuffProperties.getProperty("BuffShopRestrictedSummons", "").split(",");
		for (String id : restrictedSummonIds)
		{
			if (!id.trim().isEmpty())
			{
				try
				{
					BUFFSHOP_RESTRICTED_SUMMONS.add(Integer.parseInt(id.trim()));
				}
				catch (NumberFormatException e)
				{
					_log.log(Level.SEVERE, "BuffShop System: Error parsing Skill id '{}' from property 'BuffShopRestrictedSummons'.", id);
				}
			}
		}
		
		BUFFSHOP_SUMMON_BUYER_CLASSES.clear();
		
		String[] summonBuyerClassNames = DhousefeBuffProperties.getProperty("BuffShopSummonBuyerClasses", "").split(",");
		for (String className : summonBuyerClassNames)
		{
			className = className.trim();
			if (className.isEmpty())
			{
				continue;
			}
			try
			{
				
				ClassId classId = ClassId.valueOf(className);
				BUFFSHOP_SUMMON_BUYER_CLASSES.add(classId);
			}
			catch (IllegalArgumentException e)
			{
				
				_log.log(Level.SEVERE, "BuffShop System: BUFFSHOP_SUMMON_BUYER_CLASSES=CLASSNAME '{}' from property 'net.sf.l2j.gameserver.enums.actors.ClassId' is invalid. Skipping.", className);
			}
		}
		
		String[] nonRemovableIds = DhousefeBuffProperties.getProperty("NonRemovableBuffs", "").split(",");
		for (String id : nonRemovableIds)
		{
			if (!id.trim().isEmpty())
			{
				try
				{
					NON_REMOVABLE_BUFFS.add(Integer.parseInt(id.trim()));
				}
				catch (NumberFormatException e)
				{
					_log.log(Level.SEVERE, "BuffShop System: Error parsing Skill id '{}' from property 'NonRemovableBuffs'.", id);
				}
			}
		}
		
		String[] replaceableIds = DhousefeBuffProperties.getProperty("BuffShopReplaceableBuffs", "").split(",");
		for (String id : replaceableIds)
		{
			if (!id.trim().isEmpty())
			{
				try
				{
					BUFFSHOP_REPLACEABLE_BUFFS.add(Integer.parseInt(id.trim()));
				}
				catch (NumberFormatException e)
				{
					_log.log(Level.SEVERE, "BuffShop System: Error parsing Skill id '{}' from property 'BuffShopReplaceableBuffs'.", id);
				}
			}
		}
		
		String[] allowedSkillShop = DhousefeBuffProperties.getProperty("SkillShopAllowedClasses", "").split(",");
		for (String id : allowedSkillShop)
		{
			if (!id.trim().isEmpty())
				BUFFSHOP_ALLOW_CLASS_SKILLSHOP.add(Integer.parseInt(id.trim()));
		}
		
		for (String key : DhousefeBuffProperties.stringPropertyNames())
		{
			if (key.startsWith("SkillShopAvailable."))
			{
				try
				{
					ClassId classId = ClassId.valueOf(key.substring("SkillShopAvailable.".length()));
					List<Integer> skills = new ArrayList<>();
					for (String skillId : DhousefeBuffProperties.getProperty(key, "").split(","))
					{
						if (!skillId.trim().isEmpty())
							skills.add(Integer.parseInt(skillId.trim()));
					}
					SKILL_SHOP_AVAILABLE.put(classId, skills);
				}
				catch (Exception e)
				{
					_log.log(Level.SEVERE, "BuffShop: Error parsing property '{}'.", e);
				}
			}
			else if (key.startsWith("SkillShopPath."))
			{
				try
				{
					int skillId = Integer.parseInt(key.substring("SkillShopPath.".length()));
					String[] parts = DhousefeBuffProperties.getProperty(key, "").split(";");
					int maxLevel = Integer.parseInt(parts[0].trim());
					Map<Integer, List<Cost>> costs = new HashMap<>();
					for (int i = 1; i < parts.length; i++)
					{
						List<Cost> levelCost = new ArrayList<>();
						for (String costStr : parts[i].split(","))
						{
							String[] costParts = costStr.split(":");
							levelCost.add(new Cost(Integer.parseInt(costParts[0].trim()), Integer.parseInt(costParts[1].trim())));
						}
						costs.put(i, levelCost);
					}
					SKILL_SHOP_PATHS.put(skillId, new SkillPath(maxLevel, costs));
				}
				catch (Exception e)
				{
					_log.log(Level.SEVERE, "BuffShop: Error parsing property '{}'.", e);
				}
			}
		}
		
	}
	
	public static BuffShopConfigs getInstance()
	{
		return SingletonHolder._instance;
	}
	
	public static void restoreOfflineTraders()
	{
		BuffShopManager.getInstance().restoreOfflineTraders();
	}
	
	private static class SingletonHolder
	{
		protected static final BuffShopConfigs _instance = new BuffShopConfigs();
	}
	
	public record BuffShopConfig(List<Integer> allowedClasses, List<Integer> forbiddenSkills, List<Integer> allowedSelfSkills, List<Integer> restrictedSummons, Set<ClassId> summonBuyerClasses, Map<ClassId, List<SkillGrantRule>> grantSkills, Map<String, List<Integer>> classSpecificSkills, int maxBuffsCount, Set<SkillTargetType> targetCheck)
	{
		public record SkillGrantRule(int requiredLevel, int skillId, int skillLevel)
		{
		}
		
		public static BuffShopConfig createDefault()
		{
			
			return new BuffShopConfig(List.of(), List.of(), List.of(), List.of(), EnumSet.noneOf(ClassId.class), Map.of(), Map.of(), 8, BUFFSHOP_TARGET_CHECK);
		}
	}
	
	public static final Set<SkillTargetType> BUFFSHOP_TARGET_CHECK = EnumSet.of(SkillTargetType.SELF, SkillTargetType.CORPSE_MOB, SkillTargetType.AURA, SkillTargetType.AREA, SkillTargetType.AREA_CORPSE_MOB, SkillTargetType.HOLY);
	
}

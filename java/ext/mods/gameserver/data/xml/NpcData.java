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
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;

import ext.mods.Config;
import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.enums.DropType;
import ext.mods.gameserver.enums.actors.NpcRace;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.actor.template.PetTemplate;
import ext.mods.gameserver.model.item.DropCategory;
import ext.mods.gameserver.model.item.DropData;
import ext.mods.gameserver.model.memo.NpcMemo;
import ext.mods.gameserver.model.records.PetDataEntry;
import ext.mods.gameserver.model.records.PrivateData;
import ext.mods.gameserver.skills.L2Skill;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

/**
 * Loads and stores {@link NpcTemplate}s.
 */
public class NpcData implements IXmlReader
{
	private final Map<Integer, NpcTemplate> _npcs = new HashMap<>();
	
	protected NpcData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("xml/npcs");
		LOGGER.info("Loaded {} NPC templates.", _npcs.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "npc", npcNode ->
		{
			final NamedNodeMap attrs = npcNode.getAttributes();
			final int npcId = parseInteger(attrs, "id");
			final int templateId = attrs.getNamedItem("idTemplate") == null ? npcId : parseInteger(attrs, "idTemplate");
			final StatSet set = new StatSet();
			set.set("id", npcId);
			set.set("idTemplate", templateId);
			set.set("name", parseString(attrs, "name"));
			set.set("title", parseString(attrs, "title"));
			set.set("alias", parseString(attrs, "alias"));
			
			forEach(npcNode, "set", setNode ->
			{
				final NamedNodeMap setAttrs = setNode.getAttributes();
				set.set(parseString(setAttrs, "name"), parseString(setAttrs, "val"));
			});
			forEach(npcNode, "ai", aiNode ->
			{
				final NpcMemo aiParams = new NpcMemo();
				forEach(aiNode, "set", setNode ->
				{
					final NamedNodeMap setAttrs = setNode.getAttributes();
					aiParams.set(parseString(setAttrs, "name"), parseString(setAttrs, "val"));
				});
				set.set("aiParams", aiParams);
			});
			forEach(npcNode, "drops", dropsNode ->
			{
				final List<DropCategory> drops = new ArrayList<>();
				forEach(dropsNode, "category", categoryNode ->
				{
					final NamedNodeMap categoryAttrs = categoryNode.getAttributes();
					final DropType dropType = parseEnum(categoryAttrs, DropType.class, "type");
					for (String skipType : Config.SKIP_CATEGORY)
					{
						if (skipType.trim().equalsIgnoreCase(dropType.name()))
							return;
					}
					
					final DropCategory category = new DropCategory(dropType, parseDouble(categoryAttrs, "chance", 100.0));
					forEach(categoryNode, "drop", dropNode ->
					{
						final NamedNodeMap dropAttrs = dropNode.getAttributes();
						final DropData data = new DropData(parseInteger(dropAttrs, "itemid"), parseInteger(dropAttrs, "min"), parseInteger(dropAttrs, "max"), parseDouble(dropAttrs, "chance"));
						
						if (ItemData.getInstance().getTemplate(data.getItemId()) == null)
						{
							LOGGER.warn("Droplist data for undefined itemId: {}.", data.getItemId());
							return;
						}
						
						if (SkipData.getInstance().isSkipped(data.getItemId()))
							return;
						
						category.addDropData(data);
					});
					drops.add(category);
				});
				set.set("drops", drops);
			});
			forEach(npcNode, "privates", privatesNode ->
			{
				final List<PrivateData> privateData = new ArrayList<>();
				forEach(privatesNode, "private", privateNode -> privateData.add(new PrivateData(parseAttributes(privateNode))));
				set.set("privates", privateData);
			});
			forEach(npcNode, "petdata", petdataNode ->
			{
				final NamedNodeMap petdataAttrs = petdataNode.getAttributes();
				set.set("mustUsePetTemplate", true);
				set.set("food1", parseInteger(petdataAttrs, "food1"));
				set.set("food2", parseInteger(petdataAttrs, "food2"));
				set.set("autoFeedLimit", parseDouble(petdataAttrs, "autoFeedLimit"));
				set.set("hungryLimit", parseDouble(petdataAttrs, "hungryLimit"));
				set.set("unsummonLimit", parseDouble(petdataAttrs, "unsummonLimit"));
				
				final Map<Integer, PetDataEntry> entries = new HashMap<>();
				forEach(petdataNode, "stat", statNode ->
				{
					final StatSet petSet = parseAttributes(statNode);
					
					final int mountBaseSpeed;
					final int mountWaterSpeed;
					final int mountFlySpeed;
					
					final String speed = petSet.getString("speedOnRide", null);
					if (speed != null)
					{
						final String[] speeds = speed.split(";");
						mountBaseSpeed = Integer.parseInt(speeds[0]);
						mountWaterSpeed = Integer.parseInt(speeds[2]);
						mountFlySpeed = Integer.parseInt(speeds[4]);
					}
					else
					{
						mountBaseSpeed = 0;
						mountWaterSpeed = 0;
						mountFlySpeed = 0;
					}
					
					petSet.set("mountBaseSpeed", mountBaseSpeed);
					petSet.set("mountWaterSpeed", mountWaterSpeed);
					petSet.set("mountFlySpeed", mountFlySpeed);
					
					entries.put(petSet.getInteger("level"), new PetDataEntry(petSet));
				});
				set.set("petData", entries);
			});
			forEach(npcNode, "skills", skillsNode ->
			{
				final List<L2Skill> passives = new ArrayList<>();
				final Map<NpcSkillType, L2Skill> skills = new EnumMap<>(NpcSkillType.class);
				
				forEach(skillsNode, "skill", skillNode ->
				{
					final NamedNodeMap skillAttrs = skillNode.getAttributes();
					final int skillId = parseInteger(skillAttrs, "id");
					final int level = parseInteger(skillAttrs, "level");
					
					final NpcRace nr = NpcRace.retrieveBySecondarySkillId(skillId);
					if (nr != null && nr != NpcRace.DUMMY)
					{
						set.set("race", nr);
						return;
					}
					
					if (skillId == L2Skill.SKILL_NPC_RACE && !set.containsKey("race"))
					{
						set.set("race", NpcRace.VALUES[level]);
						return;
					}
					
					for (String nstString : parseString(skillAttrs, "type").split(";"))
					{
						final L2Skill skill = SkillTable.getInstance().getInfo(skillId, level);
						if (skill == null)
						{
							LOGGER.warn("Invalid skill data {}-{} for : {}.", skillId, level, npcId);
							return;
						}
						
						final NpcSkillType nst = Enum.valueOf(NpcSkillType.class, nstString);
						if (nst == NpcSkillType.PASSIVE)
							passives.add(skill);
						else
							skills.put(nst, skill);
					}
				});
				set.set("passives", passives);
				set.set("skills", skills);
			});
			forEach(npcNode, "teachTo", teachToNode -> set.set("teachTo", parseString(teachToNode.getAttributes(), "classes")));
			
			_npcs.put(npcId, set.getBool("mustUsePetTemplate", false) ? new PetTemplate(set) : new NpcTemplate(set));
		}));
	}
	
	public void reload()
	{
		_npcs.clear();
		
		load();
	}
	
	public NpcTemplate getTemplate(int id)
	{
		return _npcs.get(id);
	}
	
	/**
	 * @param name : The name of the NPC to search.
	 * @return the {@link NpcTemplate} for a given name.
	 */
	public NpcTemplate getTemplateByName(String name)
	{
		return _npcs.values().stream().filter(t -> t.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	/**
	 * Gets all {@link NpcTemplate}s matching the filter.
	 * @param filter : The Predicate filter used as a filter.
	 * @return a NpcTemplate list matching the given filter.
	 */
	public List<NpcTemplate> getTemplates(Predicate<NpcTemplate> filter)
	{
		return _npcs.values().stream().filter(filter).toList();
	}
	
	public Collection<NpcTemplate> getTemplates()
	{
		return _npcs.values();
	}
	
	public static NpcData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final NpcData INSTANCE = new NpcData();
	}
}
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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ext.mods.commons.data.xml.IXmlReader;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.holder.skillnode.ClanSkillNode;
import ext.mods.gameserver.model.holder.skillnode.EnchantSkillNode;
import ext.mods.gameserver.model.holder.skillnode.FishingSkillNode;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.skills.L2Skill;

import org.w3c.dom.Document;

/**
 * This class loads and stores datatypes extending SkillNode, such as {@link FishingSkillNode}, {@link EnchantSkillNode} and {@link ClanSkillNode}.
 */
public class SkillTreeData implements IXmlReader
{
	private final List<FishingSkillNode> _fishingSkills = new LinkedList<>();
	private final List<ClanSkillNode> _clanSkills = new LinkedList<>();
	private final List<EnchantSkillNode> _enchantSkills = new LinkedList<>();
	
	protected SkillTreeData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("xml/skillstrees");
		LOGGER.info("Loaded {} fishing skills.", _fishingSkills.size());
		LOGGER.info("Loaded {} clan skills.", _clanSkills.size());
		LOGGER.info("Loaded {} enchant skills.", _enchantSkills.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode ->
		{
			forEach(listNode, "clanSkill", clanSkillNode -> _clanSkills.add(new ClanSkillNode(parseAttributes(clanSkillNode))));
			forEach(listNode, "fishingSkill", fishingSkillNode -> _fishingSkills.add(new FishingSkillNode(parseAttributes(fishingSkillNode))));
			forEach(listNode, "enchantSkill", enchantSkillNode -> _enchantSkills.add(new EnchantSkillNode(parseAttributes(enchantSkillNode))));
		});
	}
	
	/**
	 * @param player : The player to check.
	 * @return the {@link List} of available {@link FishingSkillNode} for the associated {@link Player}.
	 */
	public List<FishingSkillNode> getFishingSkillsFor(Player player)
	{
		final List<FishingSkillNode> result = new ArrayList<>();
		
		_fishingSkills.stream().filter(s -> s.getMinLvl() <= player.getStatus().getLevel() && (!s.isDwarven() || (player.hasDwarvenCraft() && s.isDwarven()))).forEach(s ->
		{
			if (player.getSkillLevel(s.getId()) == s.getValue() - 1)
				result.add(s);
		});
		
		return result;
	}
	
	/**
	 * @param player : The player to check.
	 * @param skillId : The skill id to check.
	 * @param skillLevel : The skill level to check.
	 * @return the {@link FishingSkillNode} for the associated {@link Player}.
	 */
	public FishingSkillNode getFishingSkillFor(Player player, int skillId, int skillLevel)
	{
		final FishingSkillNode fsn = _fishingSkills.stream().filter(s -> s.getId() == skillId && s.getValue() == skillLevel && (!s.isDwarven() || (player.hasDwarvenCraft() && s.isDwarven()))).findFirst().orElse(null);
		if (fsn == null)
			return null;
		
		if (fsn.getMinLvl() > player.getStatus().getLevel())
			return null;
		
		if (player.getSkillLevel(skillId) == fsn.getValue() - 1)
			return fsn;
		
		return null;
	}
	
	/**
	 * @param player : The player to check.
	 * @return the required level for next {@link FishingSkillNode} for the associated {@link Player}.
	 */
	public int getRequiredLevelForNextFishingSkill(Player player)
	{
		return _fishingSkills.stream().filter(s -> s.getMinLvl() > player.getStatus().getLevel() && (!s.isDwarven() || (player.hasDwarvenCraft() && s.isDwarven()))).min((s1, s2) -> Integer.compare(s1.getMinLvl(), s2.getMinLvl())).map(s -> s.getMinLvl()).orElse(0);
	}
	
	/**
	 * @param player : The player to check.
	 * @return the {@link List} of available {@link ClanSkillNode} for the associated {@link Player}.
	 */
	public List<ClanSkillNode> getClanSkillsFor(Player player)
	{
		final Clan clan = player.getClan();
		if (clan == null)
			return Collections.emptyList();
		
		final List<ClanSkillNode> result = new ArrayList<>();
		
		_clanSkills.stream().filter(s -> s.getMinLvl() <= clan.getLevel()).forEach(s ->
		{
			final L2Skill clanSkill = clan.getClanSkills().get(s.getId());
			if ((clanSkill == null && s.getValue() == 1) || (clanSkill != null && clanSkill.getLevel() == s.getValue() - 1))
				result.add(s);
		});
		
		return result;
	}
	
	/**
	 * @param player : The player to check.
	 * @param skillId : The skill id to check.
	 * @param skillLevel : The skill level to check.
	 * @return the {@link ClanSkillNode} for the associated {@link Player}.
	 */
	public ClanSkillNode getClanSkillFor(Player player, int skillId, int skillLevel)
	{
		final Clan clan = player.getClan();
		if (clan == null)
			return null;
		
		final ClanSkillNode csn = _clanSkills.stream().filter(s -> s.getId() == skillId && s.getValue() == skillLevel).findFirst().orElse(null);
		if (csn == null)
			return null;
		
		if (csn.getMinLvl() > clan.getLevel())
			return null;
		
		final L2Skill clanSkill = clan.getClanSkills().get(skillId);
		if ((clanSkill == null && csn.getValue() == 1) || (clanSkill != null && clanSkill.getLevel() == csn.getValue() - 1))
			return csn;
		
		return null;
	}
	
	/**
	 * @param player : The player to check.
	 * @return the {@link List} of available {@link EnchantSkillNode} for the associated {@link Player}.
	 */
	public List<EnchantSkillNode> getEnchantSkillsFor(Player player)
	{
		final List<EnchantSkillNode> result = new ArrayList<>();
		
		for (EnchantSkillNode esn : _enchantSkills)
		{
			final L2Skill skill = player.getSkill(esn.getId());
			if (skill != null && ((skill.getLevel() == SkillTable.getInstance().getMaxLevel(skill.getId()) && (esn.getValue() == 101 || esn.getValue() == 141)) || (skill.getLevel() == esn.getValue() - 1)))
				result.add(esn);
		}
		return result;
	}
	
	/**
	 * @param player : The player to check.
	 * @param skillId : The skill id to check.
	 * @param skillLevel : The skill level to check.
	 * @return the {@link EnchantSkillNode} for the associated {@link Player}.
	 */
	public EnchantSkillNode getEnchantSkillFor(Player player, int skillId, int skillLevel)
	{
		final EnchantSkillNode esn = _enchantSkills.stream().filter(s -> s.getId() == skillId && s.getValue() == skillLevel).findFirst().orElse(null);
		if (esn == null)
			return null;
		
		final int currentSkillLevel = player.getSkillLevel(skillId);
		if ((currentSkillLevel == SkillTable.getInstance().getMaxLevel(skillId) && (skillLevel == 101 || skillLevel == 141)) || (currentSkillLevel == skillLevel - 1))
			return esn;
		
		return null;
	}
	
	public static SkillTreeData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SkillTreeData INSTANCE = new SkillTreeData();
	}
}
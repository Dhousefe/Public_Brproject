package ext.mods.dolls;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;

import org.w3c.dom.Document;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.skills.L2Skill;

public class DollsData implements IXmlReader
{
	private final Map<Integer, Doll> _dolls = new HashMap<>();
	
	private DollsData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_dolls.clear();
		parseDataFile("custom/mods/Dolls.xml");
		LOGGER.info("Loaded {" + _dolls.size() + "} Dolls.");
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "Dolls", listNode -> forEach(listNode, "Doll", dollNode ->
		{
			StatSet attrs = parseAttributes(dollNode);
			
			int id = attrs.getInteger("Id");
			int skillId = attrs.getInteger("SkillId");
			int skillLvl = attrs.getInteger("SkillLvl");
			
			_dolls.put(id, new Doll(id, skillId, skillLvl));
		}));
	}
	
	public Map<Integer, Doll> getDolls()
	{
		return _dolls;
	}
	
	public Doll getDollById(int id)
	{
		return _dolls.get(id);
	}
	
	public boolean isDollById(int id)
	{
		return _dolls.containsKey(id);
	}
	
	public Doll getDoll(Player player)
	{
		List<ItemInstance> items = new ArrayList<>();
		for (ItemInstance item : player.getInventory().getItems())
		{
			if (item != null && isDollById(item.getItemId()))
				items.add(item);
		}
		
		int skillLv = 0;
		int itemId = 0;
		
		for (ItemInstance item : items)
		{
			int level = getDollById(item.getItemId()).getSkillLvl();
			if (level > skillLv)
			{
				skillLv = level;
				itemId = item.getItemId();
			}
		}
		
		if (itemId == 0)
			return null;
		
		return getDollById(itemId);
	}
	
	public void setSkillForDoll(Player player, int dollItemId)
	{
		Doll doll = getDollById(dollItemId);
		if (doll == null)
			return;
		
		int skillId = doll.getSkillId();
		int skillLvl = doll.getSkillLvl();
		
		L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLvl);
		if (skill == null)
			return;
		
		int currentSkillLvl = player.getSkillLevel(skillId);
		if (currentSkillLvl > 0)
			player.removeSkill(skillId, false);
		
		if (player.getInventory().getItemByItemId(dollItemId) == null)
		{
			refreshAllDollSkills(player);
		}
		else
		{
			player.addSkill(skill, false);
		}
		
		player.sendPacket(new ext.mods.gameserver.network.serverpackets.SkillList(player));
	}
	
	public void refreshAllDollSkills(Player player)
	{
		Map<Integer, Integer> highestSkillLevels = new HashMap<>();
		
		for (ItemInstance item : player.getInventory().getItems())
		{
			if (item == null || !isDollById(item.getItemId()))
				continue;
			
			Doll doll = getDollById(item.getItemId());
			int skillId = doll.getSkillId();
			int skillLvl = doll.getSkillLvl();
			
			Integer current = highestSkillLevels.get(skillId);
			if (current == null || skillLvl > current)
				highestSkillLevels.put(skillId, skillLvl);
		}
		
		for (Entry<Integer, Integer> entry : highestSkillLevels.entrySet())
		{
			L2Skill skill = SkillTable.getInstance().getInfo(entry.getKey(), entry.getValue());
			if (skill != null)
				player.addSkill(skill, false);
		}
		
		player.sendPacket(new ext.mods.gameserver.network.serverpackets.SkillList(player));
	}
	
	public void getSkillDoll(Player player, ItemInstance item)
	{
		if (item == null)
			return;
		
		if (isDollById(item.getItemId()))
		{
			setSkillForDoll(player, item.getItemId());
			refreshAllDollSkills(player);
		}
	}
	
	public static DollsData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final DollsData INSTANCE = new DollsData();
	}
}


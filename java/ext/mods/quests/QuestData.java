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
package ext.mods.quests;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;
import ext.mods.quests.holder.QuestHolder;
import ext.mods.quests.holder.QuestObjective;
import ext.mods.quests.holder.QuestReward;

public class QuestData implements IXmlReader
{
	private final Map<Integer, QuestHolder> _quests = new HashMap<>();
	
	protected QuestData()
	{
		load();
	}
	
	public void reload()
	{
		_quests.clear();
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("custom/mods/quests.xml");
		LOGGER.info("Loaded {" + _quests.size() + "} quests.");
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "quests", questsNode -> forEach(questsNode, "quest", questNode ->
		{
			final StatSet set = parseAttributes(questNode);
			final int questId = set.getInteger("id");
			final boolean repeatable = set.getBool("repeatable", false);
			
			QuestHolder quest = new QuestHolder(questId, repeatable);
			
			forEach(questNode, "info", infoNode ->
			{
				forEach(infoNode, "name", n -> quest.setName(n.getTextContent()));
				forEach(infoNode, "desc", d ->
				{
					StringBuilder desc = new StringBuilder();
					forEach(d, "line", l -> desc.append(l.getTextContent()).append("<br1>"));
					quest.setDesc(desc.toString());
				});
				
				forEach(infoNode, "icon", i -> quest.setIcon(i.getTextContent()));
				forEach(infoNode, "sound", s -> quest.setSound(s.getTextContent()));
			});
			
			forEach(questNode, "requirements", reqNode ->
			{
				forEach(reqNode, "classIds", classIdsNode ->
				{
					forEach(classIdsNode, "class", classNode ->
					{
						quest.addRequiredClass(parseAttributes(classNode).getInteger("id"));
					});
				});
			});
			
			
			forEach(questNode, "objectives", objNode ->
			{
				forEach(objNode, "objective", objectiveNode ->
				{
					int classId = parseAttributes(objectiveNode).getInteger("classId");
					
					forEach(objectiveNode, "kill", killNode ->
					{
						StatSet ks = parseAttributes(killNode);
						quest.addObjective(new QuestObjective(classId, ks.getInteger("npcId"), ks.getInteger("count")));
					});
				});
			});
			
			forEach(questNode, "rewards", rewardsNode ->
			{
				forEach(rewardsNode, "reward", rewardNode ->
				{
					StatSet rs = parseAttributes(rewardNode);
					quest.addReward(new QuestReward(rs.getInteger("classId"), rs.getInteger("itemId"), rs.getInteger("count")));
				});
			});
			
			_quests.put(questId, quest);
		}));
	}
	
	public QuestHolder getQuest(int id)
	{
		return _quests.get(id);
	}
	
	public Collection<QuestHolder> getQuests()
	{
		return _quests.values();
	}
	
	public static QuestData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final QuestData INSTANCE = new QuestData();
	}
}

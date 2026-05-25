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
package ext.mods.gameserver.custom.data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;

import ext.mods.Config;
import ext.mods.gameserver.enums.actors.MissionType;
import ext.mods.gameserver.model.Mission;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

public class MissionData implements IXmlReader
{
	private final Map<MissionType, List<Mission>> _missions = new LinkedHashMap<>();
	
	public MissionData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		if (!Config.ENABLE_MISSION)
			return;
		
		parseDataFile("custom/mods/missions.xml");
		LOGGER.info("Loaded {} of {} mission data.", _missions.size(), MissionType.values().length);
	}
	
	public void reload()
	{
		_missions.clear();
		load();
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "mission", missionNode ->
		{
			final NamedNodeMap missionAttrs = missionNode.getAttributes();
			final MissionType type = MissionType.valueOf(missionAttrs.getNamedItem("type").getNodeValue());
			
			final List<Mission> missions = new ArrayList<>();
			forEach(missionNode, "stage", stageNode ->
			{
				StatSet set = parseAttributes(stageNode);
				set.set("name", missionAttrs.getNamedItem("name").getNodeValue());
				set.set("desc", missionAttrs.getNamedItem("desc").getNodeValue());
				set.set("icon", missionAttrs.getNamedItem("icon").getNodeValue());
				missions.add(new Mission(set));
			});
			
			_missions.put(type, missions);
		}));
	}
	
	public List<Mission> getMission(MissionType type)
	{
		return _missions.get(type);
	}
	
	public Mission getMissionByLevel(MissionType type, int level)
	{
		return _missions.get(type).stream().filter(mission -> mission.getLevel() == Math.clamp(level, 1, _missions.get(type).size())).findFirst().orElse(null);
	}
	
	public Map<MissionType, List<Mission>> getMissions()
	{
		return _missions;
	}
	
	public static MissionData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final MissionData INSTANCE = new MissionData();
	}
}
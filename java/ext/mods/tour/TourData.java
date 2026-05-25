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
package ext.mods.tour;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import ext.mods.tour.holder.TourConfig;
import ext.mods.tour.holder.TourHolder;
import ext.mods.tour.holder.TourPrizeHolder;

public class TourData implements IXmlReader
{
	private final Map<String, List<TourHolder>> _tournament = new HashMap<>();
	private TourConfig _config;
	private final List<TourPrizeHolder> _prizes = new ArrayList<>();
	
	public TourData()
	{
		load();
		TournamentEvent.start();
	}
	
	public void reload()
	{
		TournamentEvent.reset();
		_tournament.clear();
		load();
	}
	
	@Override
	public void load()
	{	
		parseDataFile("custom/mods/tourBattle.xml");
		LOGGER.info("Loaded {" + _tournament.size() + "} Tournament battle.");
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "tournaments", eventsNode ->
		{
			forEach(eventsNode, "event", eventNode ->
			{
				StatSet set = parseAttributes(eventNode);
				String name = set.getString("name", "battle");
				
				boolean enabled = Boolean.parseBoolean(getChildText(eventNode, "enabled"));
				int duration = Integer.parseInt(getChildText(eventNode, "duration"));
				int preparation = Integer.parseInt(getChildText(eventNode, "preparation"));
				
				String[] dayTokens = getChildText(eventNode, "days").split(",");
				List<Integer> days = new ArrayList<>();
				for (String token : dayTokens)
					days.add(Integer.parseInt(token.trim()));
				
				List<String> times = new ArrayList<>();
				forEach(eventNode, "times", timesNode ->
				{
					forEach(timesNode, "time", timeNode -> times.add(timeNode.getTextContent()));
				});
				
				_config = new TourConfig(enabled, duration, preparation, days, times);
				
				forEach(eventNode, "battles", spawnsNode ->
				{
					forEach(spawnsNode, "battle", spawnNode ->
					{
						StatSet spawnSet = parseAttributes(spawnNode);
						TourHolder spawn = new TourHolder(spawnSet);
						_tournament.computeIfAbsent(name, k -> new ArrayList<>()).add(spawn);
					});
				});
				
				forEach(eventNode, "prizes", prizesNode ->
				{
					forEach(prizesNode, "prize", prizeNode ->
					{
						StatSet prizeSet = parseAttributes(prizeNode);
						TourPrizeHolder prize = new TourPrizeHolder(prizeSet);
						_prizes.add(prize);
					});
				});
				
			});
		});
	}
	
	public List<TourHolder> getBattleName(String name)
	{
		return _tournament.getOrDefault(name, new ArrayList<>());
	}
	
	public TourConfig getConfig()
	{
		return _config;
	}
	
	public List<TourPrizeHolder> getPrizes()
	{
		return _prizes;
	}
	
	private static String getChildText(Node node, String tag)
	{
		Node child = getChild(node, tag);
		return (child != null) ? child.getTextContent().trim() : "";
	}
	
	private static Node getChild(Node node, String tag)
	{
		for (int i = 0; i < node.getChildNodes().getLength(); i++)
		{
			Node child = node.getChildNodes().item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE && tag.equals(child.getNodeName()))
				return child;
		}
		return null;
	}
	
	public static TourData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final TourData _instance = new TourData();
	}
	
}

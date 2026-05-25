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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class BossHpAnnounceData implements IXmlReader
{
	
	private boolean _enabled = true;
	
	public static class HpThreshold
	{
		public final int percent;
		public final String message;
		
		public HpThreshold(int percent, String message)
		{
			this.percent = percent;
			this.message = message;
		}
	}
	
	private final Map<Integer, List<HpThreshold>> _thresholds = new ConcurrentHashMap<>();
	
	protected BossHpAnnounceData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_thresholds.clear();
		parseDataFile("custom/mods/bossHpAnnounce.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _thresholds.size() + " boss HP announce configs.");
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode ->
		{
			Node enabledNode = listNode.getAttributes().getNamedItem("enabled");
			if (enabledNode != null)
				_enabled = Boolean.parseBoolean(enabledNode.getNodeValue());
			
			forEach(listNode, "boss", bossNode ->
			{
				final StatSet set = new StatSet(parseAttributes(bossNode));
				final int npcId = set.getInteger("npcId");
				final List<HpThreshold> list = new ArrayList<>();
				
				forEach(bossNode, "hp", hpNode ->
				{
					final StatSet hpSet = new StatSet(parseAttributes(hpNode));
					final int percent = hpSet.getInteger("percent");
					final String message = hpSet.getString("message", "%boss% reached %hp%% of life!");
					
					if (percent > 0 && percent <= 100)
						list.add(new HpThreshold(percent, message));
				});
				
				list.sort((a, b) -> Integer.compare(b.percent, a.percent));
				_thresholds.put(npcId, list);
			});
		});
	}
	
	public boolean isEnabled()
	{
		return _enabled;
	}
	
	public boolean isAnnounceEnabledFor(int npcId)
	{
		return _enabled && _thresholds.containsKey(npcId);
	}
	
	public List<HpThreshold> getThresholds(int npcId)
	{
		return _thresholds.getOrDefault(npcId, Collections.emptyList());
	}
	
	public static BossHpAnnounceData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final BossHpAnnounceData INSTANCE = new BossHpAnnounceData();
	}
}

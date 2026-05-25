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
package ext.mods.global;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;
import ext.mods.commons.logging.CLogger;
import org.w3c.dom.Document;


import ext.mods.FarmEventRandom.holder.DropHolder;

public class GlobalDropData implements IXmlReader
{
	private static final CLogger LOGGER = new CLogger(GlobalDropData.class.getName());
	
	private boolean _isEnabled = false;
	private boolean _dropsOriginals = false;
	private int _minLevel = 1;
	private int _maxLevel = 0;
	private boolean _useServerRates = true;
	private double _chanceMultiplier = 1.0;
	private final List<DropHolder> _drops = new ArrayList<>();
	private final Set<Integer> _ignoreMonsters = new HashSet<>();
	
	public GlobalDropData()
	{
		load();
	}
	
	public void reload()
	{
		_drops.clear();
		_ignoreMonsters.clear();
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("custom/mods/global_drop.xml");
		LOGGER.info("Loaded Global Drop config. Enabled: " + _isEnabled + " | Drops: " + _drops.size() + " | Ignored Monsters: " + _ignoreMonsters.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> {
			forEach(listNode, "config", configNode -> {
				StatSet set = parseAttributes(configNode);
				
				_isEnabled = set.getBool("enable", false);
				_dropsOriginals = set.getBool("DropsOriginals", false);
				_minLevel = set.getInteger("minLevel", 1);
				_maxLevel = set.getInteger("maxLevel", 0);
				_useServerRates = set.getBool("UseServerRates", true);
				_chanceMultiplier = set.getDouble("ChanceMultiplier", 1.0);
				
				String dropsStr = set.getString("drops", "");
				if (!dropsStr.isEmpty()) {
					for (String part : dropsStr.split(";")) {
						String[] vals = part.split("-");
						if (vals.length >= 3) {
							try {
								int itemId = Integer.parseInt(vals[0]);
								int count = Integer.parseInt(vals[1]);
								int chance = Integer.parseInt(vals[2]);
								_drops.add(new DropHolder(itemId, count, chance));
							} catch (NumberFormatException e) {
								LOGGER.error("[GlobalDropData] Erro ao ler drop global: " + part, e);
							}
						}
					}
				}
				
				String ignoreStr = set.getString("IgnoreMonsters", "");
				if (!ignoreStr.isEmpty()) {
					_ignoreMonsters.addAll(Arrays.stream(ignoreStr.split(","))
						.map(String::trim)
						.map(Integer::parseInt)
						.collect(Collectors.toSet()));
				}
			});
		});
	}
	
	public boolean isEnabled() { return _isEnabled; }
	public boolean isDropsOriginals() { return _dropsOriginals; }
	public int getMinLevel() { return _minLevel; }
	public int getMaxLevel() { return _maxLevel; }
	public boolean isUseServerRates() { return _useServerRates; }
	public double getChanceMultiplier() { return _chanceMultiplier; }
	public List<DropHolder> getDrops() { return _drops; }
	public boolean isIgnored(int npcId) { return _ignoreMonsters.contains(npcId); }
	
	public static GlobalDropData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final GlobalDropData _instance = new GlobalDropData();
	}
}
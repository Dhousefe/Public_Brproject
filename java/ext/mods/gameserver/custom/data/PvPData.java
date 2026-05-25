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

import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;

import ext.mods.gameserver.model.holder.IntIntHolder;

import org.w3c.dom.Document;

public class PvPData implements IXmlReader
{
	private final List<ColorSystem> _color = new ArrayList<>();
	private final List<RewardSystem> _reward = new ArrayList<>();
	private boolean _enabled;
	
	public PvPData()
	{
		load();
	}
	
	public void reload()
	{
		_color.clear();
		_reward.clear();
		load();
	}
	
	@Override
	public void load()
	{
		if (_enabled)
		{
			parseDataFile("custom/mods/pvpSystem.xml");
			LOGGER.info("Loaded {} PvP Colors templates.", _color.size());
			LOGGER.info("Loaded {} PvP Rewards templates.", _reward.size());
		}
		else
			LOGGER.warn("PvP System is disabled.");
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode ->
		{
			final StatSet set = parseAttributes(listNode);
			_enabled = set.getBool("enabled", true);
			
			if (_enabled)
			{
				forEach(listNode, "color", colorNode ->
				{
					final StatSet colorSet = parseAttributes(colorNode);
					_color.add(new ColorSystem(colorSet));
				});
				
				forEach(listNode, "reward", rewardNode ->
				{
					final StatSet rewardSet = parseAttributes(rewardNode);
					_reward.add(new RewardSystem(rewardSet));
				});
			}
		});
	}
	
	public List<ColorSystem> getColor()
	{
		return _enabled ? _color : Collections.emptyList();
	}
	
	public List<RewardSystem> getReward()
	{
		return _enabled ? _reward : Collections.emptyList();
	}
	
	public boolean isEnabled()
	{
		return _enabled;
	}
	
	public record RewardSystem(List<IntIntHolder> reward)
	{
		public RewardSystem(StatSet set)
		{
			this(set.getIntIntHolderList("rewards"));
		}
	}
	
	public record ColorSystem(int pvpAmount, int nameColor, int titleColor)
	{
		public ColorSystem(StatSet set)
		{
			this(set.getInteger("pvpAmount"), Integer.decode("0x" + set.getString("nameColor", "FFFFFF")), Integer.decode("0x" + set.getString("titleColor", "FFFF77")));
		}
	}
	
	public static PvPData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final PvPData _instance = new PvPData();
	}
}
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
import java.util.HashMap;
import java.util.Map;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;

import ext.mods.gameserver.model.records.LevelingInfo;
import ext.mods.gameserver.model.records.SoulCrystal;

import org.w3c.dom.Document;

/**
 * This class loads and stores following Soul Crystal infos :
 * <ul>
 * <li>{@link SoulCrystal} infos related to items (such as level, initial / broken / succeeded itemId) ;</li>
 * <li>{@link LevelingInfo} infos related to NPCs (such as absorb type, chances of fail/success, if the item cast needs to be done and the list of allowed crystal levels).</li>
 * </ul>
 */
public class SoulCrystalData implements IXmlReader
{
	private final Map<Integer, SoulCrystal> _soulCrystals = new HashMap<>();
	private final Map<Integer, LevelingInfo> _levelingInfos = new HashMap<>();
	
	protected SoulCrystalData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("xml/soulCrystals.xml");
		LOGGER.info("Loaded {} Soul Crystals data and {} NPCs data.", _soulCrystals.size(), _levelingInfos.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode ->
		{
			forEach(listNode, "crystals", crystalsNode -> forEach(crystalsNode, "crystal", crystalNode ->
			{
				final StatSet set = parseAttributes(crystalNode);
				_soulCrystals.put(set.getInteger("initial"), new SoulCrystal(set));
			}));
			forEach(listNode, "npcs", npcsNode -> forEach(npcsNode, "npc", npcNode ->
			{
				final StatSet set = parseAttributes(npcNode);
				_levelingInfos.put(set.getInteger("id"), new LevelingInfo(set));
			}));
		});
	}
	
	public final Map<Integer, SoulCrystal> getSoulCrystals()
	{
		return _soulCrystals;
	}
	
	public final Map<Integer, LevelingInfo> getLevelingInfos()
	{
		return _levelingInfos;
	}
	
	public static SoulCrystalData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SoulCrystalData INSTANCE = new SoulCrystalData();
	}
}
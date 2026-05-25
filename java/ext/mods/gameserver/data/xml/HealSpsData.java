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
import java.util.Comparator;
import java.util.LinkedList;

import ext.mods.commons.data.xml.IXmlReader;

import ext.mods.gameserver.model.records.HealSps;
import ext.mods.gameserver.skills.L2Skill;

import org.w3c.dom.Document;

/**
 * This class loads and stores {@link HealSps}s infos. Those informations are used for Heal calculation.
 */
public class HealSpsData implements IXmlReader
{
	private final LinkedList<HealSps> _healSps = new LinkedList<>();
	
	protected HealSpsData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("xml/healSps.xml");
		LOGGER.info("Loaded {} healSps entries.", _healSps.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "healSps", healSpsNode -> _healSps.add(new HealSps(parseAttributes(healSpsNode)))));
	}
	
	/**
	 * @param skill : The {@link L2Skill} to check.
	 * @param mAtk : The mAtk to check.
	 * @return The heal SPS correction based on casted {@link L2Skill} and caster's mAtk.
	 */
	public double calculateHealSps(L2Skill skill, int mAtk)
	{
		HealSps healSps = _healSps.stream().filter(h -> h.skillId() == skill.getId() && h.skillLevel() == skill.getLevel()).findFirst().orElse(null);
		
		if (healSps == null && skill.getMagicLevel() > 0)
			healSps = _healSps.stream().filter(h -> h.magicLevel() <= skill.getMagicLevel()).max(Comparator.comparing(HealSps::magicLevel)).orElse(null);
		
		if (healSps == null)
			return 0.;
		
		double amount = healSps.correction();
		
		final int mAtkDiff = healSps.neededMatk() - mAtk;
		if (mAtkDiff <= 0)
			return amount;
		
		amount -= (mAtkDiff / 2d);
		
		return amount;
	}
	
	public static HealSpsData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final HealSpsData INSTANCE = new HealSpsData();
	}
}
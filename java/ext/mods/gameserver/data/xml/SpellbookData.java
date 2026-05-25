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

import ext.mods.commons.data.xml.IXmlReader;

import ext.mods.Config;
import ext.mods.gameserver.skills.L2Skill;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

/**
 * This class loads and stores spellbook / skillId relation.<br>
 * TODO Could be possibly moved back on skillTrees.
 */
public class SpellbookData implements IXmlReader
{
	private final Map<Integer, Integer> _books = new HashMap<>();
	
	protected SpellbookData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("xml/spellbooks.xml");
		LOGGER.info("Loaded {} spellbooks.", _books.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "book", bookNode ->
		{
			final NamedNodeMap attrs = bookNode.getAttributes();
			_books.put(parseInteger(attrs, "skillId"), parseInteger(attrs, "itemId"));
		}));
	}
	
	public int getBookForSkill(int skillId, int level)
	{
		if (skillId == L2Skill.SKILL_DIVINE_INSPIRATION)
		{
			if (!Config.DIVINE_SP_BOOK_NEEDED)
				return 0;
			
			switch (level)
			{
				case 1:
					return 8618;
				case 2:
					return 8619;
				case 3:
					return 8620;
				case 4:
					return 8621;
				default:
					return 0;
			}
		}
		
		if (level != 1)
			return 0;
		
		if (!Config.SP_BOOK_NEEDED)
			return 0;
		
		if (!_books.containsKey(skillId))
			return 0;
		
		return _books.get(skillId);
	}
	
	public static SpellbookData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SpellbookData INSTANCE = new SpellbookData();
	}
}
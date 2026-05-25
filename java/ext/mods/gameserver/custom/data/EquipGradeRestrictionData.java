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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.Map;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;

import ext.mods.gameserver.enums.items.CrystalType;

import org.w3c.dom.Document;

public class EquipGradeRestrictionData implements IXmlReader
{
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	private final Map<CrystalType, RestrictionInfo> restrictions = new EnumMap<>(CrystalType.class);
	
	protected EquipGradeRestrictionData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		restrictions.clear();
		parseDataFile("custom/mods/equip_grade_restrictions.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + restrictions.size() + " grade restrictions.");
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "restrictions", listNode ->
		{
			forEach(listNode, "grade", node ->
			{
				StatSet set = new StatSet(parseAttributes(node));
				
				try
				{
					CrystalType grade = CrystalType.valueOf(set.getString("name"));
					boolean enabled = set.getBool("enabled", false);
					String dateStr = set.getString("availableFrom", null);
					LocalDateTime date = (dateStr != null) ? LocalDateTime.parse(dateStr, FORMATTER) : null;
					String message = set.getString("message", "");
					
					restrictions.put(grade, new RestrictionInfo(enabled, date, message));
					
				}
				catch (IllegalArgumentException e)
				{
					LOGGER.warn(getClass().getSimpleName() + ": Invalid crystal grade '" + set.getString("name") + "' in " + path.getFileName());
				}
			});
		});
	}
	
	public boolean isEquipAllowed(CrystalType grade)
	{
		RestrictionInfo info = restrictions.get(grade);
		if (info == null || !info.enabled)
			return true;
		
		return info.availableFrom == null || LocalDateTime.now().isAfter(info.availableFrom);
	}
	
	public String getBlockMessage(CrystalType grade)
	{
		final RestrictionInfo info = restrictions.get(grade);
		if (info == null || !info.enabled || info.availableFrom == null)
			return "";
		
		final String raw = (info.message == null || info.message.isEmpty()) ? "You cannot yet equip items of the grade {grade}. Available at {date}." : info.message;
		
		return raw.replace("{grade}", grade.name()).replace("{date}", info.availableFrom.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
	}
	
	public static EquipGradeRestrictionData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final EquipGradeRestrictionData INSTANCE = new EquipGradeRestrictionData();
	}
	
	private static class RestrictionInfo
	{
		final boolean enabled;
		final LocalDateTime availableFrom;
		final String message;
		
		public RestrictionInfo(boolean enabled, LocalDateTime availableFrom, String message)
		{
			this.enabled = enabled;
			this.availableFrom = availableFrom;
			this.message = message;
		}
	}
	
}
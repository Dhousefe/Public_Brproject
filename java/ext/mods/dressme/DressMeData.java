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
package ext.mods.dressme;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;

import org.w3c.dom.Document;

import ext.mods.dressme.holder.DressMeEffectHolder;
import ext.mods.dressme.holder.DressMeHolder;

public class DressMeData implements IXmlReader
{
	private final List<DressMeHolder> _entries = new ArrayList<>();
	
	public DressMeData()
	{
		load();
	}
	
	public void reload()
	{
		_entries.clear();
		load();
	}
	
	@Override
	public void load()
	{
		
		parseDataFile("custom/mods/DressMeData.xml");
		LOGGER.info("Loaded {" + _entries.size() + "} DressMe entries.");
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "dressMeList", listNode -> forEach(listNode, "dress", dressNode ->
		{
			final StatSet attrs = parseAttributes(dressNode);
			
			final DressMeHolder holder = new DressMeHolder(attrs);
			
			forEach(dressNode, "visualSet", setNode -> holder.setVisualSet(parseAttributes(setNode)));
			forEach(dressNode, "visualWep", wepNode -> holder.setWeaponSet(parseAttributes(wepNode)));
			forEach(dressNode, "visualEffect", fxNode -> holder.setEffect(new DressMeEffectHolder(parseAttributes(fxNode))));
			
			_entries.add(holder);
		}));
	}
	
	public List<DressMeHolder> getEntries()
	{
		return _entries;
	}
	
	public DressMeHolder getBySkillId(int skillId)
	{
		return _entries.stream().filter(d -> d.getSkillId() == skillId).findFirst().orElse(null);
	}
	
	public static DressMeData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final DressMeData INSTANCE = new DressMeData();
	}
}
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ext.mods.Config;
import ext.mods.commons.logging.CLogger;
import ext.mods.gameserver.data.DocumentItem;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.Armor;
import ext.mods.gameserver.model.item.kind.EtcItem;
import ext.mods.gameserver.model.item.kind.Item;
import ext.mods.gameserver.model.item.kind.Weapon;

/**
 * This class loads and stores all {@link Item} templates.
 */
public class ItemData
{
	private static final CLogger LOGGER = new CLogger(ItemData.class.getName());
	
	private Item[] _templates;
	private static final Map<Integer, Armor> armors = new HashMap<>();
	private static final Map<Integer, EtcItem> etcItems = new HashMap<>();
	private static final Map<Integer, Weapon> weapons = new HashMap<>();
	
	protected ItemData()
	{
		load();
	}
	
	public void reload()
	{
		armors.clear();
		etcItems.clear();
		weapons.clear();
		
		load();
	}
	
	private void load()
	{
		final File dir = Config.DATA_PATH.resolve("xml").resolve("items").toFile();
		
		int highest = 0;
		for (File file : dir.listFiles())
		{
			DocumentItem document = new DocumentItem(file);
			document.parse();
			
			for (Item item : document.getItemList())
			{
				if (highest < item.getItemId())
					highest = item.getItemId();
				
				if (item instanceof EtcItem etcItem)
					etcItems.put(item.getItemId(), etcItem);
				else if (item instanceof Armor armor)
					armors.put(item.getItemId(), armor);
				else if (item instanceof Weapon weapon)
					weapons.put(item.getItemId(), weapon);
			}
		}
		
		_templates = new Item[highest + 1];
		
		for (Armor item : armors.values())
			_templates[item.getItemId()] = item;
		
		for (Weapon item : weapons.values())
			_templates[item.getItemId()] = item;
		
		for (EtcItem item : etcItems.values())
			_templates[item.getItemId()] = item;
		
		LOGGER.info("Loaded items.");
	}
	
	public ItemInstance createDummyItem(int itemId)
	{
		final Item item = getTemplate(itemId);
		if (item == null)
			return null;
		
		return new ItemInstance(0, item);
	}
	
	/**
	 * @param id : the item id to check.
	 * @return the {@link Item} corresponding to the item id.
	 */
	public Item getTemplate(int id)
	{
		return (id >= _templates.length) ? null : _templates[id];
	}
	
	public int getArraySize()
	{
		return _templates.length;
	}
	
	/**
	 * @return The array of all {@link Item} templates.
	 */
	public Item[] getTemplates()
	{
		return _templates;
	}
	
	public List<Item> searchItemsByName(String name)
	{
		List<Item> result = new ArrayList<>();
		String lowerName = name.toLowerCase();
		
		for (Armor armor : armors.values())
		{
			if (armor.getName().toLowerCase().contains(lowerName))
			{
				result.add(armor);
			}
		}
		
		for (EtcItem etcItem : etcItems.values())
		{
			if (etcItem.getName().toLowerCase().contains(lowerName))
			{
				result.add(etcItem);
			}
		}
		
		for (Weapon weapon : weapons.values())
		{
			if (weapon.getName().toLowerCase().contains(lowerName))
			{
				result.add(weapon);
			}
		}
		
		return result;
	}
	
	
	public int getItemIdByName(String itemName)
	{
		
		if (itemName == null || itemName.trim().isEmpty())
		{
			return -1;
		}
		
		for (Armor armor : armors.values())
		{
			if (armor.getName().equalsIgnoreCase(itemName))
			{
				return armor.getItemId();
			}
		}
		
		for (EtcItem etcItem : etcItems.values())
		{
			if (etcItem.getName().equalsIgnoreCase(itemName))
			{
				return etcItem.getItemId();
			}
		}
		
		for (Weapon weapon : weapons.values())
		{
			if (weapon.getName().equalsIgnoreCase(itemName))
			{
				return weapon.getItemId();
			}
		}
		
		return -1;
	}
	
	public static ItemData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ItemData INSTANCE = new ItemData();
	}
	
}
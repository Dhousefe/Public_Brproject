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
import java.util.HashMap;
import java.util.Map;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;

import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.serverpackets.ExShowScreenMessage;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

public class OlympiadEnchantData implements IXmlReader
{
	
	private boolean enabled = false;
	private int enchantValue = 6;
	private String enterMessage = "";
	private String exitMessage = "";
	
	private final Map<Integer, Map<Integer, Integer>> enchantBackup = new HashMap<>();
	
	protected OlympiadEnchantData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("custom/mods/olympiad_enchant_config.xml");
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "olympiadEnchant", node ->
		{
			StatSet set = new StatSet(parseAttributes(node));
			enabled = set.getBool("enabled", false);
			enchantValue = set.getInteger("value", 6);
			
			NamedNodeMap attrs = node.getAttributes();
			enterMessage = parseString(attrs, "enterMessage", "Their equipment has been refined to +{enchant} for the Olympiad.");
			exitMessage = parseString(attrs, "exitMessage", "Your equipment has returned to its original refinement.");
		});
	}
	
	public void applyOlympiadEnchant(Player player)
	{
		if (!enabled || player == null)
			return;
		
		Map<Integer, Integer> originalEnchants = new HashMap<>();
		for (ItemInstance item : player.getInventory().getPaperdollItems())
		{
			if (item != null && item.isEquipable())
			{
				originalEnchants.put(item.getObjectId(), item.getEnchantLevel());
				item.setEnchantLevel(enchantValue, player);
			}
		}
		enchantBackup.put(player.getObjectId(), originalEnchants);
		
		player.sendMessage(enterMessage.replace("{enchant}", String.valueOf(enchantValue)));
		player.sendPacket(new ExShowScreenMessage(enterMessage.replace("{enchant}", String.valueOf(enchantValue)), 4000));
		player.broadcastUserInfo();
	}
	
	public void restoreEnchant(Player player)
	{
		if (!enabled || player == null)
			return;
		
		Map<Integer, Integer> backup = enchantBackup.remove(player.getObjectId());
		if (backup == null)
			return;
		
		for (ItemInstance item : player.getInventory().getPaperdollItems())
		{
			if (item != null && item.isEquipable())
			{
				Integer original = backup.get(item.getObjectId());
				if (original != null)
					item.setEnchantLevel(original, player);
			}
		}
		
		player.sendMessage(exitMessage);
		player.sendPacket(new ExShowScreenMessage(exitMessage, 4000));
		player.broadcastUserInfo();
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
	
	public static OlympiadEnchantData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final OlympiadEnchantData INSTANCE = new OlympiadEnchantData();
	}
}

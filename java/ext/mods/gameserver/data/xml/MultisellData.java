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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ext.mods.commons.data.xml.IXmlReader;

import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.multisell.CommunityBoardListContainer;
import ext.mods.gameserver.model.multisell.Entry;
import ext.mods.gameserver.model.multisell.Ingredient;
import ext.mods.gameserver.model.multisell.ListContainer;
import ext.mods.gameserver.model.multisell.PreparedListContainer;
import ext.mods.gameserver.network.serverpackets.MultiSellList;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

/**
 * This class loads and stores multisell lists under {@link ListContainer}.<br>
 * Each ListContainer contains a List of {@link Entry}, and the list of allowed npcIds.<br>
 * <br>
 * File name is used as key, under its String hashCode.
 */
public class MultisellData implements IXmlReader
{
	public static final int PAGE_SIZE = 40;
	
	private final Map<Integer, ListContainer> _entries = new HashMap<>();
	
	public MultisellData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("xml/multisell");
		LOGGER.info("Loaded {} multisell.", _entries.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		final int id = path.toFile().getName().replaceAll(".xml", "").hashCode();
		final ListContainer list = new ListContainer(id);
		forEach(doc, "list", listNode ->
		{
			final NamedNodeMap attrs = listNode.getAttributes();
			
			list.setApplyTaxes(parseBoolean(attrs, "applyTaxes", false));
			list.setMaintainEnchantment(parseBoolean(attrs, "maintainEnchantment", false));
			
			forEach(listNode, "item", itemNode ->
			{
				final List<Ingredient> ingredients = new ArrayList<>();
				final List<Ingredient> products = new ArrayList<>();
				forEach(itemNode, "ingredient", ingredientNode -> ingredients.add(new Ingredient(parseAttributes(ingredientNode))));
				forEach(itemNode, "production", productionNode -> products.add(new Ingredient(parseAttributes(productionNode))));
				list.getEntries().add(new Entry(ingredients, products));
			});
			forEach(listNode, "npcs", npcsNode -> forEach(npcsNode, "npc", npcNode -> list.allowNpc(Integer.parseInt(npcNode.getTextContent()))));
			
			_entries.put(id, list);
		});
	}
	
	public void reload()
	{
		_entries.clear();
		
		load();
	}
	
	/**
	 * Send the correct multisell content to a {@link Player}.<br>
	 * <br>
	 * {@link ListContainer} template is first retrieved, based on its name, then {@link Npc} npcId check is done for security reason. Then the content is sent into {@link PreparedListContainer}, notably to check Player inventory. Finally a {@link MultiSellList} packet is sent to the Player. That
	 * new, prepared list is kept in memory on Player instance, mostly for memory reason.
	 * @param listName : The ListContainer list name.
	 * @param player : The Player to check.
	 * @param npc : The Npc to check (notably used for npcId check).
	 * @param inventoryOnly : if true we check inventory content.
	 */
	public void separateAndSend(String listName, Player player, Npc npc, boolean inventoryOnly)
	{
		final ListContainer template = _entries.get(listName.hashCode());
		if (template == null)
			return;
		
		if ((npc != null && !template.isNpcAllowed(npc.getNpcId())) || (npc == null && template.isNpcOnly()))
			return;
		
		final PreparedListContainer list = new PreparedListContainer(template, inventoryOnly, player, npc);
		
		int index = 0;
		do
		{
			player.sendPacket(new MultiSellList(list, index));
			index += PAGE_SIZE;
		}
		while (index < list.getEntries().size());
		
		player.setMultiSell(list);
	}
	
	/**
	 * Send the correct multisell content to a {@link Player}.<br>
	 * <br>
	 * {@link ListContainer} template is first retrieved, based on its name, no {@link Npc} checks are done. Then the content is sent into {@link PreparedListContainer}, notably to check Player inventory. Finally a {@link MultiSellList} packet is sent to the Player. That new, prepared list is kept
	 * in memory on Player instance, mostly for memory reason.
	 * @param listName : The ListContainer list name.
	 * @param player : The Player to check.
	 * @param inventoryOnly : if true we check inventory content.
	 */
	public void separateAndSendCb(String listName, Player player, boolean inventoryOnly)
	{
		final ListContainer template = _entries.get(listName.hashCode());
		if (template == null)
			return;
		
		final CommunityBoardListContainer list = new CommunityBoardListContainer(template, inventoryOnly, player, null);
		
		int index = 0;
		do
		{
			player.sendPacket(new MultiSellList(list, index));
			index += PAGE_SIZE;
		}
		while (index < list.getEntries().size());
		
		player.setMultiSell(list);
	}
	
	public ListContainer getList(String listName)
	{
		return _entries.get(listName.hashCode());
	}
	
	public static MultisellData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final MultisellData INSTANCE = new MultisellData();
	}
}
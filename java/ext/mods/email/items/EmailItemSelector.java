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
package ext.mods.email.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ext.mods.email.EmailProtectionItems;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;

public class EmailItemSelector
{
	private static final int ITEMS_PER_PAGE = 8;
	
	public static void showAvailableItems(Player player)
	{
		showAvailableItems(player, 1);
	}
	
	public static void showAvailableItems(Player player, int page)
	{
		Set<ItemInstance> allItems = player.getInventory().getItems();
		List<ItemInstance> filteredItems = new ArrayList<>();
		
		for (ItemInstance item : allItems)
		{
			if (item == null)
				continue;
			
			if (item.isEquipped() || item.isQuestItem() || EmailProtectionItems.PROIBIDOS.contains(item.getItemId()))
				continue;
			
			filteredItems.add(item);
		}
		
		int totalPages = (int) Math.ceil((double) filteredItems.size() / ITEMS_PER_PAGE);
		if (totalPages == 0)
			totalPages = 1;
		
		if (page < 1)
			page = 1;
		if (page > totalPages)
			page = totalPages;
		
		int startIndex = (page - 1) * ITEMS_PER_PAGE;
		int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, filteredItems.size());
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><title>Email Select Items</title><body><center>");
		sb.append("<table width=300><tr><td align=center><font color=\"LEVEL\">Selecionar Itens</font></td></tr></table>");
		
		if (filteredItems.isEmpty())
		{
			sb.append("<br><font color=LEVEL>Nenhum item disponível.</font><br>");
		}
		else
		{
			sb.append("<table width=280>");
			for (int i = startIndex; i < endIndex; i++)
			{
				ItemInstance item = filteredItems.get(i);
				sb.append("<tr>");
				sb.append("<td width=32><img src=\"" + item.getItem().getIcon() + "\" width=32 height=32></td>");
				
				String name = item.getName();
				
				String[] ocultarPalavras =
				{
					"Scroll:",
					"Top-Grade",
					"Sealed",
					"Ripe",
					"Recipe:",
					"Spellbook -",
					"Greater Mercenary"
				};
				
			
				for (String palavra : ocultarPalavras)
				{
					name = name.replace(palavra, "").trim();
				}
				
				if (name.length() > 29)
				{
					name = name.substring(0, 29);
				}
				
				if (item.getEnchantLevel() > 0)
				{
					String color = (item.getEnchantLevel() == 5) ? "FFFF00" : "LEVEL";
					name = "<font color=\"" + color + "\">+" + item.getEnchantLevel() + "</font> " + name;
				}
				
				sb.append("<td width=170>");
				if (item.isAugmented())
				{
					sb.append("<font color=00FF99>" + name + "</font> x<font color=LEVEL>" + item.getCount() + "</font><br1>");
					
				}
				else
				{
					sb.append("<font color=FFFFFF>" + name + "</font> x<font color=LEVEL>" + item.getCount() + "</font><br1>");
					
				}
				
				sb.append("</td>");
				
				boolean isSelected = player.getTempSelectedItems().containsKey(item.getObjectId());
				String buttonLabel = isSelected ? "Remover" : "Selecionar";
				
				sb.append("<td><button value=\"" + buttonLabel + "\" action=\"bypass -h voiced_email confirm_select_item " + item.getObjectId() + " " + page + "\" width=80 height=21 back=\"L2UI_CH3.Btn1_normalOn\" fore=\"L2UI_CH3.Btn1_normal\"></td>");
				
				sb.append("</tr>");
			}
			sb.append("</table><br>");
			
			sb.append("<table width=280><tr>");
			if (page > 1)
			{
				sb.append("<td align=left><button value=\"&$1037;\" action=\"bypass -h voiced_email select_items " + (page - 1) + "\" width=75 height=21 back=\"L2UI_CH3.Btn1_normalOn\" fore=\"L2UI_CH3.Btn1_normal\"></td>");
			}
			else
			{
				sb.append("<td width=70></td>");
			}
			
			sb.append("<td align=center>Page " + page + " / " + totalPages + "</td>");
			
			if (page < totalPages)
			{
				sb.append("<td align=right><button value=\"&$1038;\" action=\"bypass -h voiced_email select_items " + (page + 1) + "\" width=75 height=21 back=\"L2UI_CH3.Btn1_normalOn\" fore=\"L2UI_CH3.Btn1_normal\"></td>");
			}
			else
			{
				sb.append("<td width=70></td>");
			}
			sb.append("</tr></table><br>");
			
			sb.append("<center><button value=\"Voltar\" action=\"bypass -h voiced_email htm email_main.htm\" width=75 height=21 back=\"L2UI_CH3.Btn1_normalOn\" fore=\"L2UI_CH3.Btn1_normal\"></center>");
		}
		
		sb.append("</body></html>");
		
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setHtml(sb.toString());
		player.sendPacket(html);
	}
}

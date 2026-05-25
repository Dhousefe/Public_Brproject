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
package ext.mods.gameserver.handler.bypasshandlers;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import ext.mods.gameserver.data.DropCalc;
import ext.mods.gameserver.data.manager.DropSkipManager;
import ext.mods.gameserver.data.xml.ItemData;
import ext.mods.gameserver.data.xml.NpcData;
import ext.mods.gameserver.data.xml.SkipData;
import ext.mods.gameserver.enums.DropType;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.item.DropCategory;
import ext.mods.gameserver.model.item.DropData;
import ext.mods.gameserver.model.item.kind.Item;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;

public class DropListUI
{
	private static final DecimalFormat PERCENT = new DecimalFormat("##.##");
	private static final int ITEMS_PER_LIST = 7;
	
	private static final String checkon = "RECEIVE";
	private static final String checkoff = "SKIP";

	public static void sendNpcDrop(Player player, int npcId, int page)
	{
		final NpcTemplate template = NpcData.getInstance().getTemplate(npcId);
		if (template == null)
			return;
		
		if (template.getDropData().isEmpty())
		{
			player.sendMessage("This target has no drop information.");
			return;
		}
		
		final List<DropData> dropList = new ArrayList<>();
		for (DropCategory category : template.getDropData())
		{
			if (category.getDropType() == DropType.HERB)
				continue;

			for (DropData drop : category.getAllDrops())
			{
				
				if (SkipData.getInstance().isSkipped(drop.getItemId()))
					continue;
				
				
				drop.setCategoryType(category.getDropType());
				dropList.add(drop);
			}
		}
		
		dropList.sort((a, b) ->
		{
			if (a.getItemId() == 57)
				return -1;
			if (b.getItemId() == 57)
				return 1;
			return 0;
		});
		
		final StringBuilder sb = new StringBuilder();
		int pageIndex = 1, i = 0, shown = 0;
		boolean hasMore = false;
		
		for (DropData drop : dropList)
		{
			final int itemId = drop.getItemId();
			final Item item = ItemData.getInstance().getTemplate(itemId);
			if (item == null)
				continue;
			
			if (pageIndex != page)
			{
				i++;
				if (i == ITEMS_PER_LIST)
				{
					pageIndex++;
					i = 0;
				}
				continue;
			}
			
			if (shown == ITEMS_PER_LIST)
			{
				hasMore = true;
				break;
			}
			
			final boolean isSweep = drop.getCategoryType() == DropType.SPOIL;

			final NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(npcId);
			final boolean isRaid = npcTemplate.isType("RaidBoss");
			final boolean isGrand = npcTemplate.isType("GrandBoss");

			final Npc npc = (player.getTarget() instanceof Npc) ? (Npc) player.getTarget() : null;

			if (npc == null || npc.getNpcId() != npcId)
			{
			    player.sendMessage("You must target the correct NPC to view its drop list.");
			    return;
			}
			
			final double chance = DropCalc.getInstance().calcDropChance(player, npc , drop, drop.getCategoryType(), isRaid, isGrand);
			final double safeChance = (chance > 0 && chance < 0.01) ? 0.01 : chance;
			final double normChance = Math.min(99.999, safeChance);
			final String percent = PERCENT.format(normChance);

			
			String itemName = item.getName();
			if (itemName.startsWith("Recipe: "))
				itemName = "R: " + itemName.substring(8);
			if (itemName.length() >= 40)
				itemName = itemName.substring(0, 37) + "...";
			
			final boolean skipped = DropSkipManager.getInstance().isSkipped(player.getObjectId(), itemId);
			
			final String STATUS = skipped ? checkoff : checkon;
			
			final String icon = item.getIcon() != null ? item.getIcon() : "icon.noimage";
			
			sb.append("<table width=280 bgcolor=000000><tr>");
			sb.append("<td width=44 height=41 align=center>");
			sb.append("<table bgcolor=" + (isSweep ? "FF00FF" : "FFFFFF") + " cellpadding=6 cellspacing=\"-5\"><tr><td>");
			sb.append("<button width=32 height=32 back=" + icon + " fore=" + icon + ">");
			sb.append("</td></tr></table></td>");
			
			sb.append("<td width=240>");
			sb.append(isSweep ? "<font color=ff00ff>" + itemName + "</font>" : itemName);
			sb.append("<br1><font color=B09878>" + (isSweep ? "Spoil" : "Drop") + " Chance : " + percent + "%</font>");
			sb.append("</td>");
			
			sb.append("<td width=20>");
			sb.append("<button value=" + STATUS + " action=\"bypass -h droplist " + npcId + " " + page + " " + itemId + "\" width=65 height=19 back=L2UI_ch3.smallbutton2_over fore=L2UI_ch3.smallbutton2>");
			sb.append("</td></tr></table><img src=L2UI.SquareGray width=280 height=1>");
			shown++;
			
		}
		
		sb.append("<img height=").append(294 - (shown * 42)).append(">");
		sb.append("<img height=8><img src=L2UI.SquareGray width=280 height=1>");
		
		sb.append("<table width=280 bgcolor=000000><tr>");
		sb.append("<td align=center width=70>");
		if (page > 1)
			sb.append("<button value=\"< PREV\" action=\"bypass -h droplist " + npcId + " " + (page - 1) + "\" width=65 height=19 back=L2UI_ch3.smallbutton2_over fore=L2UI_ch3.smallbutton2>");
		sb.append("</td><td align=center width=140>Page ").append(page).append("</td><td align=center width=70>");
		if (hasMore)
			sb.append("<button value=\"NEXT >\" action=\"bypass -h droplist " + npcId + " " + (page + 1) + "\" width=65 height=19 back=L2UI_ch3.smallbutton2_over fore=L2UI_ch3.smallbutton2>");
		sb.append("</td></tr></table><img src=L2UI.SquareGray width=280 height=1>");
		
		final NpcHtmlMessage html = new NpcHtmlMessage(200);
		html.setHtml("<html><title>Droplist : " + template.getName() + "</title><body><img height=14><font color=B09878>* NOTE : Uncheck to ignore specific drop.</font><img src=L2UI.SquareGray width=280 height=1>" + sb + "</body></html>");
		player.sendPacket(html);
	}
}

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

import java.text.DateFormat;

import ext.mods.commons.lang.StringUtil;

import ext.mods.Config;
import ext.mods.gameserver.data.manager.LotteryManager;
import ext.mods.gameserver.enums.actors.MissionType;
import ext.mods.gameserver.handler.IBypassHandler;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public class Loto implements IBypassHandler {

	private static final String[] COMMANDS = { "Loto" };
	
	@Override
	public boolean useBypass(String command, Player player, Creature target)
	{
		if (target instanceof Npc npc)
		{
			int val = 0;
			try
			{
				val = Integer.parseInt(command.substring(5));
			}
			catch (IndexOutOfBoundsException | NumberFormatException e)
			{
			}
			
			if (val == 0)
			{
				for (int i = 0; i < 5; i++)
					player.setLoto(i, 0);
			}
			showLotoWindow(player, val, npc);
		}
		return true;
	}
	
	/**
	 * Open a Loto window for the {@link Player} set as parameter.
	 * <ul>
	 * <li>0 - first buy lottery ticket window</li>
	 * <li>1-20 - buttons</li>
	 * <li>21 - second buy lottery ticket window</li>
	 * <li>22 - selected ticket with 5 numbers</li>
	 * <li>23 - current lottery jackpot</li>
	 * <li>24 - Previous winning numbers/Prize claim</li>
	 * <li>>24 - check lottery ticket by item object id</li>
	 * </ul>
	 * @param player : The player that talk with this Npc.
	 * @param val : The number of the page to display.
	 * @param npc 
	 */
	public void showLotoWindow(Player player, int val, Npc npc)
	{
		final int npcId = npc.getTemplate().getNpcId();
		
		final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		
		if (val == 0)
			html.setFile(player.getLocale(), npc.getHtmlPath(player, npcId, 1));
		else if (val >= 1 && val <= 21)
		{
			if (!LotteryManager.getInstance().isStarted())
			{
				player.sendPacket(SystemMessageId.NO_LOTTERY_TICKETS_CURRENT_SOLD);
				return;
			}
			
			if (!LotteryManager.getInstance().isSellableTickets())
			{
				player.sendPacket(SystemMessageId.NO_LOTTERY_TICKETS_AVAILABLE);
				return;
			}
			
			html.setFile(player.getLocale(), npc.getHtmlPath(player, npcId, 5));
			
			int count = 0;
			int found = 0;
			for (int i = 0; i < 5; i++)
			{
				if (player.getLoto(i) == val)
				{
					player.setLoto(i, 0);
					found = 1;
				}
				else if (player.getLoto(i) > 0)
					count++;
			}
			
			if (count < 5 && found == 0 && val <= 20)
				for (int i = 0; i < 5; i++)
					if (player.getLoto(i) == 0)
					{
						player.setLoto(i, val);
						break;
					}
				
			count = 0;
			for (int i = 0; i < 5; i++)
				if (player.getLoto(i) > 0)
				{
					count++;
					String button = String.valueOf(player.getLoto(i));
					if (player.getLoto(i) < 10)
						button = "0" + button;
					
					final String search = "fore=\"L2UI.lottoNum" + button + "\" back=\"L2UI.lottoNum" + button + "a_check\"";
					final String replace = "fore=\"L2UI.lottoNum" + button + "a_check\" back=\"L2UI.lottoNum" + button + "\"";
					html.replace(search, replace);
				}
			
			if (count == 5)
			{
				final String search = "0\">" + player.getSysString(10_171);
				final String replace = "22\">" + player.getSysString(10_172);
				html.replace(search, replace);
			}
		}
		else if (val == 22)
		{
			if (!LotteryManager.getInstance().isStarted())
			{
				player.sendPacket(SystemMessageId.NO_LOTTERY_TICKETS_CURRENT_SOLD);
				return;
			}
			
			if (!LotteryManager.getInstance().isSellableTickets())
			{
				player.sendPacket(SystemMessageId.NO_LOTTERY_TICKETS_AVAILABLE);
				return;
			}
			
			final int lotonumber = LotteryManager.getInstance().getId();
			
			int enchant = 0;
			int type2 = 0;
			
			for (int i = 0; i < 5; i++)
			{
				if (player.getLoto(i) == 0)
					return;
				
				if (player.getLoto(i) < 17)
					enchant += Math.pow(2, player.getLoto(i) - 1);
				else
					type2 += Math.pow(2, player.getLoto(i) - 17);
			}
			
			if (!player.reduceAdena(Config.LOTTERY_TICKET_PRICE, true))
				return;
			
			LotteryManager.getInstance().increasePrize(Config.LOTTERY_TICKET_PRICE);
			
			final ItemInstance ticket = player.addItem(4442, 1, false);
			ticket.setCustomType1(lotonumber);
			ticket.setEnchantLevel(enchant, player);
			ticket.setCustomType2(type2);
			
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1).addItemName(4442));
			
			html.setFile(player.getLocale(), npc.getHtmlPath(player, npcId, 3));
		}
		else if (val == 23)
			html.setFile(player.getLocale(), npc.getHtmlPath(player, npcId, 3));
		else if (val == 24)
		{
			final int lotoNumber = LotteryManager.getInstance().getId();
			
			final StringBuilder sb = new StringBuilder();
			for (final ItemInstance item : player.getInventory().getItems())
			{
				if (item == null)
					continue;
				
				if (item.getItemId() == 4442 && item.getCustomType1() < lotoNumber)
				{
					StringUtil.append(sb, "<a action=\"bypass -h npc_%objectId%_Loto ", item.getObjectId(), "\">", item.getCustomType1(), " Event Number ");
					
					final int[] numbers = LotteryManager.decodeNumbers(item.getEnchantLevel(), item.getCustomType2());
					for (int i = 0; i < 5; i++)
						StringUtil.append(sb, numbers[i], " ");
					
					final int[] check = LotteryManager.checkTicket(item);
					if (check[0] > 0)
					{
						switch (check[0])
						{
							case 1:
								sb.append("- 1st Prize");
								break;
							case 2:
								sb.append("- 2nd Prize");
								break;
							case 3:
								sb.append("- 3th Prize");
								break;
							case 4:
								sb.append("- 4th Prize");
								break;
						}
						StringUtil.append(sb, " ", check[1], "a.");
					}
					sb.append("</a><br>");
				}
			}
			
			if (sb.length() == 0)
				sb.append("There is no winning lottery ticket...<br>");
			
			html.setFile(player.getLocale(), npc.getHtmlPath(player, npcId, 4));
			html.replace("%result%", sb.toString());
		}
		else if (val == 25)
		{
			html.setFile(player.getLocale(), npc.getHtmlPath(player, npcId, 2));
			html.replace("%prize5%", Config.LOTTERY_5_NUMBER_RATE * 100);
			html.replace("%prize4%", Config.LOTTERY_4_NUMBER_RATE * 100);
			html.replace("%prize3%", Config.LOTTERY_3_NUMBER_RATE * 100);
			html.replace("%prize2%", Config.LOTTERY_2_AND_1_NUMBER_PRIZE);
		}
		else if (val > 25)
		{
			final ItemInstance item = player.getInventory().getItemByObjectId(val);
			if (item == null || item.getItemId() != 4442 || item.getCustomType1() >= LotteryManager.getInstance().getId())
				return;
			
			if (player.destroyItem(item, true))
			{
				final int adena = LotteryManager.checkTicket(item)[1];
				if (adena > 0)
					player.addAdena(adena, true);
				
				player.getMissions().update(MissionType.LOTTERY_WIN);
			}
			return;
		}
		html.replace("%objectId%", npc.getObjectId());
		html.replace("%race%", LotteryManager.getInstance().getId());
		html.replace("%adena%", LotteryManager.getInstance().getPrize());
		html.replace("%ticket_price%", Config.LOTTERY_TICKET_PRICE);
		html.replace("%enddate%", DateFormat.getDateInstance().format(LotteryManager.getInstance().getEndDate()));
		
		player.sendPacket(html);
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}

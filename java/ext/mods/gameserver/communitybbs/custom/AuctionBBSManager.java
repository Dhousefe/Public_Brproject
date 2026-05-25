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
package ext.mods.gameserver.communitybbs.custom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import ext.mods.commons.data.Pagination;
import ext.mods.commons.lang.StringUtil;
import ext.mods.commons.pool.ConnectionPool;

import ext.mods.Config;
import ext.mods.gameserver.communitybbs.custom.model.Auction;
import ext.mods.gameserver.communitybbs.custom.model.Function;
import ext.mods.gameserver.communitybbs.manager.BaseBBSManager;
import ext.mods.gameserver.custom.data.AuctionCurrencies;
import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.data.xml.ItemData;
import ext.mods.gameserver.enums.items.EtcItemType;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.EtcItem;
import ext.mods.gameserver.model.item.kind.Item;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ItemList;

public class AuctionBBSManager extends BaseBBSManager
{
	private static final String SELECT_AUCTION = "SELECT * FROM bbs_auction";
	
	private final Map<Integer, Auction> _auctions = new ConcurrentHashMap<>();
	private final Map<Integer, Function> _functions = new ConcurrentHashMap<>();
	
	public void load()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_AUCTION);
			ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
				addAuction(new Auction(rs));
		}
		catch (Exception e)
		{
			LOGGER.warn("Couldn't load bbs_auction items.", e);
		}
		LOGGER.info("Loaded {} auction items.", _auctions.size());
	}
	
	@Override
	public void parseCmd(String command, Player player)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		
		String commandToken = st.hasMoreTokens() ? st.nextToken() : "";
		
		final Function function = getFunction(player);
		if (commandToken.equals("_friendlist_0_"))
		{
			final int page = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : function.getLastPage();
			String subCommand = st.hasMoreTokens() ? st.nextToken() : "";
			
			switch (subCommand)
			{
				case "src":
					final String search = st.hasMoreTokens() ? command.substring(19 + String.valueOf(page).length()) : "";
					if (search.isBlank() || command.length() > (17 + String.valueOf(page).length()))
						function.setSearch(search);
					else
						player.sendPacket(SystemMessageId.INCORRECT_SYNTAX);
					break;
				
				case "clear":
					function.setSearch("");
					break;
				
				case "type":
					function.setItemType(st.hasMoreTokens() ? st.nextToken() : "All");
					break;
				
				case "grade":
					function.setItemGrade(st.hasMoreTokens() ? st.nextToken() : "All");
					break;
				
				case "currency":
					function.setCurrency(st.hasMoreTokens() ? st.nextToken() : "All");
					break;
				
				case "select":
					function.setViewId(st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 0);
					break;
				
				case "purchase":
					final Auction auction = getAuction(function.getViewId());
					if (auction == null)
						player.sendMessage(player.getSysString(10_202));
					else if (player.getInventory().getItemCount(auction.getPriceId()) < auction.getPriceCount())
						player.sendMessage(player.getSysString(10_223, StringUtil.formatNumber(auction.getPriceCount()), auction.getPrice()));
					else if (auction.tryPurchase(player, st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 0))
					{
						function.setViewId(0);
						sendIndex(player, function.getLastPage(), function);
						return;
					}
					break;
				
				case "cancel":
					function.setViewId(0);
					break;
			}
			sendIndex(player, page, function);
		}
		else if (commandToken.equals("_friendlist_0_mine"))
		{
			final int page = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : function.getLastPage();
			final String param = st.hasMoreTokens() ? st.nextToken().toLowerCase() : "";
			
			switch (param)
			{
				case "toselect":
					function.setItemId(-1);
					player.sendPacket(new ItemList(player, true));
					break;
				
				case "unselect":
					function.setItemId(0);
					break;
				
				case "edit":
					function.setEditId(st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 0);
					break;
				
				case "update":
				case "remove":
					final Auction auction = getAuction(function.getEditId());
					if (auction == null)
						player.sendMessage(player.getSysString(10_202));
					else
					{
						if (param.equals("remove"))
						{
							auction.refund();
							function.setEditId(0);
							player.sendPacket(new ItemList(player, false));
						}
						else
							auction.updateDuration();
					}
					break;
				
				case "cancel":
					function.setEditId(0);
					break;
				
				case "sell":
					final ItemInstance item = player.getInventory().getItemByObjectId(function.getItemId());
					if (item == null)
						player.sendMessage(player.getSysString(10_203));
					else
					{
						String token1 = st.hasMoreTokens() ? st.nextToken() : "0";
						int value1 = 0;
						try
						{
							value1 = Integer.parseInt(token1);
						}
						catch (NumberFormatException e)
						{
						}
						
						String token2 = st.hasMoreTokens() ? st.nextToken() : "Adena";
						int value2 = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 0;
						
						sellItem(player, item.getObjectId(), value1, token2, value2);
					}
					
					function.setItemId(0);
					break;
			}
			
			sendIndexMine(player, page, function);
		}
	}
	
	public void sendIndex(Player player, int page, Function function)
	{
		String content = getContent(player, "index.htm");
		
		final Pagination<Auction> list = new Pagination<>(getAuctions().stream(), page, 8, a -> a.filter(function), Comparator.comparing(Auction::getItemName).thenComparing(Comparator.comparing(Auction::getPriceCount)));
		
		final Auction auctionView = getAuction(function.getViewId());
		StringBuilder str = new StringBuilder();
		
		if (auctionView == null)
		{
			function.setLastPage(page);
			
			list.generatePagesMedium("bypass _friendlist_0_ %page%");
			list.append("<img height=6>");
			
			for (Auction auction : list)
			{
				final Item item = auction.getItem();
				String auctionTemplate = getContent(player, "template-1.htm");
				
				auctionTemplate = auctionTemplate.replace("%icon%", item.getIcon());
				auctionTemplate = auctionTemplate.replace("%gradeIcon%", auction.getGradeIcon());
				auctionTemplate = auctionTemplate.replace("%itemName%", getName(auction));
				auctionTemplate = auctionTemplate.replace("%stackable%", item.isStackable() ? " " + player.getSysString(10_218) + "" : "");
				auctionTemplate = auctionTemplate.replace("%priceCount%", StringUtil.formatNumber(auction.getPriceCount()));
				auctionTemplate = auctionTemplate.replace("%currencyName%", AuctionCurrencies.getInstance().getCurrencyName(auction.getPriceId()));
				
				if (auction.getObjectId() == player.getObjectId())
					auctionTemplate = auctionTemplate.replace("%button%", "<font color=5A5A5A>" + player.getSysString(10_219) + "</font>");
				else
					auctionTemplate = auctionTemplate.replace("%button%", "<img height=6><button value=\"" + player.getSysString(10_220) + "\" action=\"bypass _friendlist_0_ " + page + " select " + auction.getId() + "\" width=65 height=19 back=L2UI_ch3.smallbutton2_down fore=L2UI_ch3.smallbutton2>");
				
				str.append(auctionTemplate);
			}
			content = content.replace("%template%", list.getContent() + str.toString());
		}
		else
		{
			final Item item = auctionView.getItem();
			String auctionTemplate = getContent(player, "template-2.htm");
			
			auctionTemplate = auctionTemplate.replace("%icon%", item.getIcon());
			auctionTemplate = auctionTemplate.replace("%gradeIcon%", auctionView.getGradeIcon());
			auctionTemplate = auctionTemplate.replace("%itemName%", getName(auctionView));
			auctionTemplate = auctionTemplate.replace("%stackable%", item.isStackable() ? " " + player.getSysString(10_218) + "" : "");
			auctionTemplate = auctionTemplate.replace("%priceCount%", StringUtil.formatNumber(auctionView.getPriceCount()));
			auctionTemplate = auctionTemplate.replace("%currencyName%", AuctionCurrencies.getInstance().getCurrencyName(auctionView.getPriceId()));
			auctionTemplate = auctionTemplate.replace("%playerName%", auctionView.getPlayerName());
			auctionTemplate = auctionTemplate.replace("%auctionEndDate%", new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(auctionView.getDuration()) + " GMT +2");
			
			str.append(auctionTemplate);
			
			if (item.isStackable())
			{
				String stackableTemplate = getContent(player, "template-stackable-3.htm");
				stackableTemplate = stackableTemplate.replace("%totalPrice%", StringUtil.formatNumber(auctionView.getItemCount() * auctionView.getPriceCount()));
				stackableTemplate = stackableTemplate.replace("%currencyName%", AuctionCurrencies.getInstance().getCurrencyName(auctionView.getPriceId()));
				stackableTemplate = stackableTemplate.replace("%button%", "<button value=" + player.getSysString(10_221) + " action=\"bypass _friendlist_0_ " + page + " purchase $quanity\" width=65 height=19 back=L2UI_ch3.smallbutton2_down fore=L2UI_ch3.smallbutton2>");
				stackableTemplate = stackableTemplate.replace("%button2%", "<button value=" + player.getSysString(10_222) + " action=\"bypass _friendlist_0_ " + page + " cancel\" width=65 height=19 back=L2UI_ch3.smallbutton2_down fore=L2UI_ch3.smallbutton2>");
				
				str.append(stackableTemplate);
			}
			else
			{
				String nonStackableTemplate = getContent(player, "template-nonstackable-3.htm");
				nonStackableTemplate = nonStackableTemplate.replace("%button%", "<button value=" + player.getSysString(10_221) + " action=\"bypass _friendlist_0_ " + page + " purchase 1\" width=65 height=19 back=L2UI_ch3.smallbutton2_down fore=L2UI_ch3.smallbutton2>");
				nonStackableTemplate = nonStackableTemplate.replace("%button2%", "<button value=" + player.getSysString(10_222) + " action=\"bypass _friendlist_0_ " + page + " cancel\" width=65 height=19 back=L2UI_ch3.smallbutton2_down fore=L2UI_ch3.smallbutton2>");
				
				str.append(nonStackableTemplate);
			}
			
			content = content.replace("%template%", list.getContent() + str.toString());
		}
		
		if (function.getViewId() != 0)
		{
			content = content.replace("%search%", " ");
			content = content.replace("%button%", " ");
		}
		else if (!function.getSearch().isBlank())
		{
			content = content.replace("%search%", function.getSearch());
			content = content.replace("%button%", "<button value=" + player.getSysString(10_224) + " action=\"bypass _friendlist_0_ 1 clear\" width=75 height=21 back=L2UI_ch3.bigbutton2 fore=L2UI_ch3.Btn1_normal>");
		}
		else
		{
			content = content.replace("%search%", "<edit var=param width=250 height=11 length=75>");
			content = content.replace("%button%", "<button value=" + player.getSysString(10_225) + " action=\"bypass _friendlist_0_ 1 src $param\" width=75 height=21 back=L2UI_ch3.Btn1_normalOn fore=L2UI_ch3.Btn1_normal>");
		}
		
		content = content.replace("%template%", list.getContent());
		content = content.replace("%filters%", getFilters(player, function));
		
		separateAndSend(content, player);
	}
	
	public void sendIndexMine(Player player, int page, Function function)
	{
		String content = getContent(player, "index-mine.htm");
		final Pagination<Auction> list = new Pagination<>(getAuctions().stream(), page, 9, a -> a.getObjectId() == player.getObjectId(), Comparator.comparing(Auction::getDuration).reversed());
		
		final Auction auctionEdit = getAuction(function.getEditId());
		StringBuilder listBuilder = new StringBuilder();
		
		if (auctionEdit == null)
		{
			function.setLastPage(page);
			listBuilder.append("<img height=6>" + String.format("<font color=A3A3A3>" + player.getSysString(10_231) + "</font> <font color=B09B79>(%s/9)</font>", list.size()));
			list.generatePagesMedium("bypass _friendlist_0_mine %page%");
			
			for (Auction auction : list)
			{
				final Item item = auction.getItem();
				String auctionTemplate = getContent(player, "auction-template.htm");
				auctionTemplate = auctionTemplate.replace("%bgColor%", auction.getDuration() < System.currentTimeMillis() ? "bgcolor=FF0000" : "");
				auctionTemplate = auctionTemplate.replace("%icon%", item.getIcon());
				auctionTemplate = auctionTemplate.replace("%gradeIcon%", auction.getGradeIcon());
				auctionTemplate = auctionTemplate.replace("%itemName%", getName(auction));
				auctionTemplate = auctionTemplate.replace("%isStackable%", item.isStackable() ? " " + player.getSysString(10_218) + "" : "");
				auctionTemplate = auctionTemplate.replace("%price%", StringUtil.formatNumber(auction.getPriceCount()));
				auctionTemplate = auctionTemplate.replace("%currencyName%", AuctionCurrencies.getInstance().getCurrencyName(auction.getPriceId()));
				auctionTemplate = auctionTemplate.replace("%page%", String.valueOf(page));
				auctionTemplate = auctionTemplate.replace("%auctionId%", String.valueOf(auction.getId()));
				listBuilder.append(auctionTemplate);
			}
			content = content.replace("%template%", list.getContent() + listBuilder.toString());
		}
		else
		{
			final String info = auctionEdit.getDuration() > System.currentTimeMillis() ? "<font color=00FF00>" + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(auctionEdit.getDuration()) + " GMT +2</font>" : "<font color=FF0000>" + player.getSysString(10_232) + "</font>";
			listBuilder.append("<img height=6>" + "<font color=A3A3A3>" + player.getSysString(10_226) + "</font> " + info);
			final Item item = auctionEdit.getItem();
			
			String auctionTemplate = getContent(player, "auction-details.htm");
			auctionTemplate = auctionTemplate.replace("%icon%", item.getIcon());
			auctionTemplate = auctionTemplate.replace("%gradeIcon%", auctionEdit.getGradeIcon());
			auctionTemplate = auctionTemplate.replace("%itemName%", getName(auctionEdit));
			auctionTemplate = auctionTemplate.replace("%isStackable%", item.isStackable() ? " " + player.getSysString(10_218) + "" : "");
			auctionTemplate = auctionTemplate.replace("%price%", StringUtil.formatNumber(auctionEdit.getPriceCount()));
			auctionTemplate = auctionTemplate.replace("%currencyName%", AuctionCurrencies.getInstance().getCurrencyName(auctionEdit.getPriceId()));
			auctionTemplate = auctionTemplate.replace("%page%", String.valueOf(page));
			auctionTemplate = auctionTemplate.replace("%auctionFee%", StringUtil.formatNumber(Config.AUCTION_FEE));
			auctionTemplate = auctionTemplate.replace("%auctionFeeName%", Config.AUCTION_ITEM_FEE_NAME);
			listBuilder.append(auctionTemplate);
			content = content.replace("%template%", list.getContent() + listBuilder.toString());
		}
		
		content = content.replace("%template%", list.getContent());
		content = content.replace("%inventory%", getInventory(player, page));
		
		separateAndSend(content, player);
	}
	
	public String getName(Auction auction)
	{
		String name = auction.getItem().getName();
		if (name.length() >= 44)
			name = name.substring(0, 42) + "..";
		
		if (auction.getItem().isEquipable() && name.contains(" - "))
			name = auction.getItem().getName().replace(" - ", "</font> - <font color=LEVEL>") + "</font>";
		
		if (auction.getItem().isStackable())
			name += " (" + StringUtil.formatNumber(auction.getItemCount()) + ")";
		
		if (auction.getItemEnchant() > 0)
			name += " <font color=B09B79>+" + auction.getItemEnchant() + "</font>";
		
		return name;
	}
	
	public String getFilters(Player player, Function function)
	{
		final StringBuilder sb = new StringBuilder();
		
		String content = getContent(player, "filter.htm");
		
		content = content.replace("%type%", getFilters("type", function.getItemType(), "All,Weapon,Armor,Jewel,Other,Soulshot/Spiritshot,Enchant"));
		content = content.replace("%grade%", getFilters("grade", function.getItemGrade(), "All,S,A,B,C,D,NONE"));
		
		StringBuilder currencyOptions = new StringBuilder("All");
		for (String currency : AuctionCurrencies.getInstance().getCurrencyNames())
			currencyOptions.append(",").append(currency);
		
		content = content.replace("%currency%", getFilters("currency", function.getCurrency(), currencyOptions.toString()));
		
		sb.append(content);
		return sb.toString();
	}
	
	
	public String getFilters(String bypass, String function, String type)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("<table width=180>");
		for (String split : type.split(","))
		{
			final String typeAll = function.toUpperCase().equals(split.replaceAll(" ", "").toUpperCase()) ? "width=12 height=12 back=L2UI.CheckBox_checked fore=L2UI.CheckBox_checked" : "action=\"bypass _friendlist_0_ 1 " + bypass + " " + split.replaceAll(" ", "") + "\" width=12 height=12 back=L2UI.CheckBox fore=L2UI.CheckBox";
			if (split.equals("NONE") || split.equals("D") || split.equals("C") || split.equals("B") || split.equals("A") || split.equals("S"))
				split = ((split.equals("NONE") ? "No-Grade" : split + "-Grade"));
			
			if (split.equals("Enchant"))
				split = "Enchant Scroll";
			
			StringUtil.append(sb, "<tr><td width=20 align=right><button ", typeAll, "></td><td width=160 align=left>", split, "</td></tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}
	
	public String getInventory(Player player, int page)
	{
		final int itemId = getFunction(player).getItemId();
		final StringBuilder sb = new StringBuilder();
		final ItemInstance item = player.getInventory().getItemByObjectId(itemId);
		
		if (item != null)
		{
			final Item items = ItemData.getInstance().getTemplate(item.getItemId());
			String name = item.getName();
			if (name.length() >= 44)
				name = name.substring(0, 42) + "..";
			if (item.isEquipable() && name.contains(" - "))
				name = item.getName().replace(" - ", "</font> - <font color=LEVEL>") + "</font>";
			
			String content = getContent(player, "inventory-item.htm");
			
			content = content.replace("%button%", "<button value=\"Remove\" action=\"bypass _friendlist_0_mine " + page + " unselect\" width=65 height=20 back=L2UI_CH3.smallbutton2_down fore=L2UI_CH3.smallbutton2>");
			content = content.replace("%etc%", (item.getItem() instanceof EtcItem etc && etc.getItemType() == EtcItemType.PET_COLLAR) ? "<font color=A3A3A3>Level:</font> <font color=B09B79>" + item.getEnchantLevel() + "</font></td>" : item.isStackable() ? "<font color=A3A3A3>Quantity:</font> " + StringUtil.formatNumber(item.getCount()) + "</font></td>" : "<font color=A3A3A3>Enchant Level:</font> <font color=B09B79>+" + item.getEnchantLevel() + "</font></td>");
			content = content.replace("%icon%", items.getIcon());
			content = content.replace("%name%", name);
			content = content.replace("%auction_fee%", StringUtil.formatNumber(Config.AUCTION_FEE));
			content = content.replace("%auctionFeeName%", Config.AUCTION_ITEM_FEE_NAME);
			content = content.replace("%format%", new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7)));
			content = content.replace("%stackable%", item.isStackable() ? "<edit var=quantity type=number width=180 height=12>" : "<font color=B09B79>1</font>");
			
			StringBuilder currencyList = new StringBuilder();
			for (String currency : AuctionCurrencies.getInstance().getCurrencyNames())
			{
				if (currencyList.length() > 0)
					currencyList.append(";");
				currencyList.append(currency);
			}
			
			content = content.replace("%currencyList%", currencyList);
			content = content.replace("%price%", item.isStackable() ? " " + player.getSysString(10_218) + "" : "");
			content = content.replace("%combobox%", "<combobox width=180 height=21 var=\"currency\" list=" + currencyList + ">");
			content = content.replace("%button2%", "<button value=" + player.getSysString(10_217) + " action=\"bypass _friendlist_0_mine " + page + " sell " + (item.isStackable() ? "$quantity" : "1") + " $currency $price\" width=65 height=19 back=L2UI_ch3.smallbutton2_down fore=L2UI_ch3.smallbutton2>");
			sb.append(content);
		}
		else
		{
			if (itemId == -1)
			{
				String auctionTemplate = getContent(player, "inventory-item1.htm");
				auctionTemplate = auctionTemplate.replace("%button%", "<button value=" + player.getSysString(10_227) + " action=\"bypass _friendlist_0_\" width=115 height=31 back=L2UI_ch3.bigbutton2 fore=L2UI_ch3.bigbutton2></td><td width=150 align=right valign=top><button value=" + player.getSysString(10_228) + " action=\"bypass _friendlist_0_mine " + page + " unselect\" width=65 height=20 back=L2UI_CH3.smallbutton2_down fore=L2UI_CH3.smallbutton2>");
				sb.append(auctionTemplate);
			}
			else
			{
				String auctionTemplate = getContent(player, "inventory-item2.htm");
				auctionTemplate = auctionTemplate.replace("%button%", "<button value=" + player.getSysString(10_227) + " action=\"bypass _friendlist_0_\" width=115 height=31 back=L2UI_ch3.bigbutton2 fore=L2UI_ch3.bigbutton2></td><td width=150 align=right valign=top><button value=" + player.getSysString(10_229) + " action=\"bypass _friendlist_0_mine " + page + " toselect\" width=65 height=20 back=L2UI_CH3.smallbutton2_down fore=L2UI_CH3.smallbutton2>");
				sb.append(auctionTemplate);
			}
		}
		
		return sb.toString();
	}
	
	public boolean selectItem(Player player, ItemInstance item)
	{
		final Function function = getFunction(player);
		if (function.getItemId() != -1)
			return false;
		
		if (_auctions.values().stream().filter(a -> a.getObjectId() == player.getObjectId()).count() >= Config.AUCTION_LIMIT_ITEM)
		{
			player.sendMessage(player.getSysString(10_204, Config.AUCTION_LIMIT_ITEM));
			return true;
		}
		
		if (item.getItemId() == 57 || item.isQuestItem() || item.isHeroItem())
		{
			player.sendMessage(player.getSysString(10_205, item.getName()));
			return true;
		}
		
		function.setItemId(item.getObjectId());
		sendIndexMine(player, 1, function);
		
		return true;
	}
	
	public void sellItem(Player player, int itemId, int quantity, String currency, int price)
	{
		final ItemInstance item = player.getInventory().getItemByObjectId(itemId);
		if (item == null)
		{
			player.sendMessage(player.getSysString(10_206));
			return;
		}
		
		if (quantity == 0 || item.getCount() < quantity)
		{
			player.sendMessage(player.getSysString(10_207));
			return;
		}
		
		if (price == 0 || (Integer.MAX_VALUE / item.getCount()) < price || (long) (price * item.getCount()) > Integer.MAX_VALUE)
		{
			player.sendMessage(player.getSysString(10_208));
			return;
		}
		
		final int costId = AuctionCurrencies.getInstance().getCurrencyId(currency);
		if (costId == -1)
		{
			player.sendMessage("Invalid currency: " + currency);
			return;
		}
		
		if (!player.destroyItemByItemId(Config.AUCTION_ITEM_FEE, Config.AUCTION_FEE, true))
		{
			player.sendMessage(player.getSysString(10_209, StringUtil.formatNumber(Config.AUCTION_FEE), Config.AUCTION_ITEM_FEE_NAME));
			return;
		}
		
		if (!player.destroyItem(itemId, quantity, true))
		{
			player.sendMessage(player.getSysString(10_206));
			return;
		}
		
		final Auction auction = new Auction(player.getObjectId(), item.getItemId(), quantity, item.getEnchantLevel(), costId, price);
		_auctions.put(auction.getId(), auction);
		auction.store();
		
		player.sendMessage(player.getSysString(10_210, item.getName()));
		player.sendPacket(new ItemList(player, false));
	}
	
	public Auction getAuction(int id)
	{
		return _auctions.get(id);
	}
	
	public Collection<Auction> getAuctions()
	{
		_auctions.values().removeIf(a -> a.getItemCount() == 0);
		return _auctions.values();
	}
	
	public void addAuction(Auction auction)
	{
		_auctions.put(auction.getId(), auction);
	}
	
	public int nextId()
	{
		return _auctions.keySet().stream().max(Integer::compareTo).orElse(0) + 1;
	}
	
	public Function getFunction(Player player)
	{
		_functions.putIfAbsent(player.getObjectId(), new Function());
		return _functions.get(player.getObjectId());
	}
	
	protected String getContent(Player player, String file)
	{
		if (Config.ENABLED_AUCTION)
			return HTMLData.getInstance().getHtm(player.getLocale(), CB_PATH + getFolder() + file);
		
		return HTMLData.getInstance().getHtm(player.getLocale(), CB_PATH + getFolder() + "disabled.htm");
	}
	
	@Override
	protected String getFolder()
	{
		return "custom/auction/";
	}
	
	public static AuctionBBSManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final AuctionBBSManager INSTANCE = new AuctionBBSManager();
	}
}
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
package ext.mods.gameserver.model.actor.instance;

import java.util.List;
import java.util.StringTokenizer;

import ext.mods.Config;
import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.data.manager.BuyListManager;
import ext.mods.gameserver.data.xml.MultisellData;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.buylist.NpcBuyList;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.serverpackets.BuyList;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.network.serverpackets.SellList;
import ext.mods.gameserver.network.serverpackets.ShopPreviewList;
import ext.mods.gameserver.skills.L2Skill;

/**
 * An instance type extending {@link Folk}, used for merchant (regular and multisell). It got buy/sell methods.<br>
 * <br>
 * It is used as mother class for few children, such as {@link Fisherman}.
 */
public class Merchant extends Folk
{
	public Merchant(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public String getHtmlPath(Player player, int npcId, int val)
	{
		String filename = "";
		
		if (val == 0)
			filename = "" + npcId;
		else
			filename = npcId + "-" + val;
		
		return "html/merchant/" + filename + ".htm";
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!Config.KARMA_PLAYER_CAN_SHOP && getNpcId() != 30420 && player.getKarma() > 0 && showPkDenyChatWindow(player, "merchant"))
			return;
		
		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken();
		
		if (actualCommand.equalsIgnoreCase("Buy"))
		{
			if (st.countTokens() < 1)
				return;
			
			showBuyWindow(player, Integer.parseInt(st.nextToken()));
		}
		else if (actualCommand.equalsIgnoreCase("Sell"))
		{
			final List<ItemInstance> items = player.getInventory().getSellableItems();
			if (items.isEmpty())
			{
				if (HTMLData.getInstance().exists(player, "" + ((this instanceof Fisherman) ? "fisherman" : "merchant") + "/" + getNpcId() + "-empty.htm"))
				{
					final String content = HTMLData.getInstance().getHtm(player, "" + ((this instanceof Fisherman) ? "fisherman" : "merchant") + "/" + getNpcId() + "-empty.htm");
					final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
					html.setHtml(content);
					html.replace("%objectId%", getObjectId());
					player.sendPacket(html);
					return;
				}
			}
			
			player.sendPacket(new SellList(player.getAdena(), items));
		}
		else if (actualCommand.equalsIgnoreCase("Wear") && Config.ALLOW_WEAR)
		{
			if (st.countTokens() < 1)
				return;
			
			showWearWindow(player, Integer.parseInt(st.nextToken()));
		}
		else if (actualCommand.equalsIgnoreCase("Multisell"))
		{
			if (st.countTokens() < 1)
				return;
			
			MultisellData.getInstance().separateAndSend(st.nextToken(), player, this, false);
		}
		else if (actualCommand.equalsIgnoreCase("Multisell_Shadow"))
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			
			if (!Config.ALLOW_SHADOW_WEAPONS)
			{
				showChatWindow(player, "html/script/feature/ShadowWeapon/message.htm");
				return;
			}
			
			if (player.getStatus().getLevel() < 40)
				html.setFile(player.getLocale(), "html/common/shadow_item-lowlevel.htm");
			else if (player.getStatus().getLevel() < 46)
				html.setFile(player.getLocale(), "html/common/shadow_item_mi_c.htm");
			else if (player.getStatus().getLevel() < 52)
				html.setFile(player.getLocale(), "html/common/shadow_item_hi_c.htm");
			else
				html.setFile(player.getLocale(), "html/common/shadow_item_b.htm");
			
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
		}
		else if (actualCommand.equalsIgnoreCase("Exc_Multisell"))
		{
			if (st.countTokens() < 1)
				return;
			
			MultisellData.getInstance().separateAndSend(st.nextToken(), player, this, true);
		}
		else if (actualCommand.equalsIgnoreCase("Newbie_Exc_Multisell"))
		{
			if (st.countTokens() < 1)
				return;
			
			if (player.isNewbie(true))
				MultisellData.getInstance().separateAndSend(st.nextToken(), player, this, true);
			else
				showChatWindow(player, "html/exchangelvlimit.htm");
		}
		else
			super.onBypassFeedback(player, command);
	}
	
	@Override
	public void showChatWindow(Player player, int val)
	{
		if (!Config.KARMA_PLAYER_CAN_SHOP && getNpcId() != 30420 && player.getKarma() > 0 && showPkDenyChatWindow(player, "merchant"))
			return;
		
		showChatWindow(player, getHtmlPath(player, getNpcId(), val));
	}
	
	private final void showWearWindow(Player player, int val)
	{
		final NpcBuyList buyList = BuyListManager.getInstance().getBuyList(val);
		if (buyList == null || !buyList.isNpcAllowed(getNpcId()))
			return;
		
		player.tempInventoryDisable();
		player.sendPacket(new ShopPreviewList(buyList, player.getAdena(), player.getSkillLevel(L2Skill.SKILL_EXPERTISE)));
	}
	
	protected final void showBuyWindow(Player player, int val)
	{
		final NpcBuyList buyList = BuyListManager.getInstance().getBuyList(val);
		if (buyList == null || !buyList.isNpcAllowed(getNpcId()))
			return;
		
		player.tempInventoryDisable();
		player.sendPacket(new BuyList(buyList, player.getAdena(), (getCastle() != null) ? getCastle().getTaxRate() : 0));
	}
}
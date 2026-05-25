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

import java.util.List;

import ext.mods.gameserver.communitybbs.manager.BaseBBSManager;
import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ExShowVariationCancelWindow;
import ext.mods.gameserver.network.serverpackets.ExShowVariationMakeWindow;
import ext.mods.gameserver.network.serverpackets.SellList;

public class ShopBBSManager extends BaseBBSManager
{
	public static final int BBS_SELL_LIST_ID = 9999;
	
	@Override
	public void parseCmd(String command, Player player)
	{
		showPage("index", player);
		
		if (command.equals("_maillist_0_1_0_"))
			showPage("index", player);
		else if (command.startsWith("_maillist_0_1_0_;page"))
		{
			showPage("index", player);
			
			String[] args = command.split(" ");
			if (args.length > 1)
				showPage(args[1], player);
		}
		else if (command.startsWith("_maillist_0_1_0_;crafter"))
		{
			showPage("crafter", player);
			
			String content = HTMLData.getInstance().getHtm(player.getLocale(), CB_PATH + getFolder() + "crafter.htm");
			content = content.replace("%name%", player.getName());
			separateAndSend(content, player);
		}
		else if (command.startsWith("_maillist_0_1_0_;sell"))
		{
			final List<ItemInstance> items = player.getInventory().getSellableItems();
			player.sendPacket(new SellList(BBS_SELL_LIST_ID, player.getAdena(), items));
		}
		else if (command.startsWith("_maillist_0_1_0_;augment"))
		{
			
			final int cmdChoice = Integer.parseInt(command.substring(25).trim());
			switch (cmdChoice)
			{
				case 1:
					player.sendPacket(SystemMessageId.SELECT_THE_ITEM_TO_BE_AUGMENTED);
					player.sendPacket(ExShowVariationMakeWindow.STATIC_PACKET);
					break;
				case 2:
					player.sendPacket(SystemMessageId.SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION);
					player.sendPacket(ExShowVariationCancelWindow.STATIC_PACKET);
					break;
			}
		}
	}
	
	private void showPage(String page, Player player)
	{
		String content = HTMLData.getInstance().getHtm(player.getLocale(), CB_PATH + getFolder() + page + ".htm");
		content = content.replace("%name%", player.getName());
		separateAndSend(content, player);
	}
	
	@Override
	protected String getFolder()
	{
		return "custom/shop/";
	}
	
	public static ShopBBSManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ShopBBSManager INSTANCE = new ShopBBSManager();
	}
}
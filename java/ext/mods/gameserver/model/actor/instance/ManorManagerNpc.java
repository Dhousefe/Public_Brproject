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

import java.util.StringTokenizer;

import ext.mods.Config;
import ext.mods.gameserver.data.manager.CastleManorManager;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.BuyListSeed;
import ext.mods.gameserver.network.serverpackets.ExShowCropInfo;
import ext.mods.gameserver.network.serverpackets.ExShowManorDefaultInfo;
import ext.mods.gameserver.network.serverpackets.ExShowProcureCropDetail;
import ext.mods.gameserver.network.serverpackets.ExShowSeedInfo;
import ext.mods.gameserver.network.serverpackets.ExShowSellCropList;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public class ManorManagerNpc extends Merchant
{
	public ManorManagerNpc(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (command.startsWith("manor_menu_select"))
		{
			if (CastleManorManager.getInstance().isUnderMaintenance())
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				player.sendPacket(SystemMessageId.THE_MANOR_SYSTEM_IS_CURRENTLY_UNDER_MAINTENANCE);
				return;
			}
			
			final StringTokenizer st = new StringTokenizer(command, "&");
			
			final int ask = Integer.parseInt(st.nextToken().split("=")[1]);
			final int state = Integer.parseInt(st.nextToken().split("=")[1]);
			final boolean time = st.nextToken().split("=")[1].equals("1");
			
			final int castleId = (state < 0) ? getCastle().getId() : state;
			
			switch (ask)
			{
				case 1:
					if (castleId != getCastle().getId())
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.HERE_YOU_CAN_BUY_ONLY_SEEDS_OF_S1_MANOR).addString(getCastle().getName()));
					else
						player.sendPacket(new BuyListSeed(player.getAdena(), castleId));
					break;
				
				case 2:
					player.sendPacket(new ExShowSellCropList(player.getInventory(), castleId));
					break;
				
				case 3:
					player.sendPacket(new ExShowSeedInfo(castleId, time, false));
					break;
				
				case 4:
					player.sendPacket(new ExShowCropInfo(castleId, time, false));
					break;
				
				case 5:
					player.sendPacket(new ExShowManorDefaultInfo(false));
					break;
				
				case 6:
					showBuyWindow(player, 300000 + getNpcId());
					break;
				
				case 9:
					player.sendPacket(new ExShowProcureCropDetail(state));
					break;
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
	
	@Override
	public String getHtmlPath(Player player, int npcId, int val)
	{
		return "html/manormanager/manager.htm";
	}
	
	@Override
	public void showChatWindow(Player player)
	{
		if (!Config.ALLOW_MANOR)
		{
			showChatWindow(player, "html/npcdefault.htm");
			return;
		}
		
		if (getCastle() != null && player.getClan() != null && getCastle().getOwnerId() == player.getClanId() && player.isClanLeader())
			showChatWindow(player, "html/manormanager/manager-lord.htm");
		else
			showChatWindow(player, "html/manormanager/manager.htm");
	}
}

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

import java.util.Locale;

import ext.mods.commons.lang.StringUtil;

import ext.mods.gameserver.data.manager.DerbyTrackManager;
import ext.mods.gameserver.data.manager.DerbyTrackManager.RaceState;
import ext.mods.gameserver.model.HistoryInfo;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.DeleteObject;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public class DerbyTrackManagerNpc extends Folk
{
	protected static final int[] TICKET_PRICES =
	{
		100,
		500,
		1000,
		5000,
		10000,
		20000,
		50000,
		100000
	};
	
	public DerbyTrackManagerNpc(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (command.startsWith("BuyTicket"))
		{
			if (DerbyTrackManager.getInstance().getCurrentRaceState() != RaceState.ACCEPTING_BETS)
			{
				player.sendPacket(SystemMessageId.MONSRACE_TICKETS_NOT_AVAILABLE);
				super.onBypassFeedback(player, "Chat 0");
				return;
			}
			
			int val = Integer.parseInt(command.substring(10));
			if (val == 0)
			{
				player.setRace(0, 0);
				player.setRace(1, 0);
			}
			
			if ((val == 10 && player.getRace(0) == 0) || (val == 20 && player.getRace(0) == 0 && player.getRace(1) == 0))
				val = 0;
			
			int npcId = getTemplate().getNpcId();
			
			String search;
			String replace;
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			
			if (val < 10)
			{
				html.setFile(player.getLocale(), getHtmlPath(player, npcId, 2));
				for (int i = 0; i < 8; i++)
				{
					int n = i + 1;
					search = "Mob" + n;
					html.replace(search, DerbyTrackManager.getInstance().getRunnerName(i));
				}
				search = "No1";
				if (val == 0)
					html.replace(search, "");
				else
				{
					html.replace(search, val);
					player.setRace(0, val);
				}
			}
			else if (val < 20)
			{
				if (player.getRace(0) == 0)
					return;
				
				html.setFile(player.getLocale(), getHtmlPath(player, npcId, 3));
				html.replace("0place", player.getRace(0));
				search = "Mob1";
				replace = DerbyTrackManager.getInstance().getRunnerName(player.getRace(0) - 1);
				html.replace(search, replace);
				search = "0adena";
				
				if (val == 10)
					html.replace(search, "");
				else
				{
					html.replace(search, TICKET_PRICES[val - 11]);
					player.setRace(1, val - 10);
				}
			}
			else if (val == 20)
			{
				if (player.getRace(0) == 0 || player.getRace(1) == 0)
					return;
				
				html.setFile(player.getLocale(), getHtmlPath(player, npcId, 4));
				html.replace("0place", player.getRace(0));
				search = "Mob1";
				replace = DerbyTrackManager.getInstance().getRunnerName(player.getRace(0) - 1);
				html.replace(search, replace);
				search = "0adena";
				int price = TICKET_PRICES[player.getRace(1) - 1];
				html.replace(search, price);
				search = "0tax";
				int tax = 0;
				html.replace(search, tax);
				search = "0total";
				int total = price + tax;
				html.replace(search, total);
			}
			else
			{
				if (player.getRace(0) == 0 || player.getRace(1) == 0)
					return;
				
				final int lane = player.getRace(0);
				final int priceId = player.getRace(1);
				
				if (!player.reduceAdena(TICKET_PRICES[priceId - 1], true))
					return;
				
				player.setRace(0, 0);
				player.setRace(1, 0);
				
				final ItemInstance ticket = player.addItem(4443, 1, false);
				ticket.setEnchantLevel(DerbyTrackManager.getInstance().getRaceNumber(), player);
				ticket.setCustomType1(lane);
				ticket.setCustomType2(TICKET_PRICES[priceId - 1] / 100);
				
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ACQUIRED_S1_S2).addNumber(DerbyTrackManager.getInstance().getRaceNumber()).addItemName(4443));
				
				DerbyTrackManager.getInstance().setBetOnLane(lane, TICKET_PRICES[priceId - 1], true);
				super.onBypassFeedback(player, "Chat 0");
				return;
			}
			html.replace("1race", DerbyTrackManager.getInstance().getRaceNumber());
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else if (command.equals("ShowOdds"))
		{
			if (DerbyTrackManager.getInstance().getCurrentRaceState() == RaceState.ACCEPTING_BETS)
			{
				player.sendPacket(SystemMessageId.MONSRACE_NO_PAYOUT_INFO);
				super.onBypassFeedback(player, "Chat 0");
				return;
			}
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player.getLocale(), getHtmlPath(player, getTemplate().getNpcId(), 5));
			for (int i = 0; i < 8; i++)
			{
				final int n = i + 1;
				
				html.replace("Mob" + n, DerbyTrackManager.getInstance().getRunnerName(i));
				
				final double odd = DerbyTrackManager.getInstance().getOdds().get(i);
				html.replace("Odd" + n, (odd > 0D) ? String.format(Locale.ENGLISH, "%.1f", odd) : "&$804;");
			}
			html.replace("1race", DerbyTrackManager.getInstance().getRaceNumber());
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else if (command.equals("ShowInfo"))
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player.getLocale(), getHtmlPath(player, getTemplate().getNpcId(), 6));
			
			for (int i = 0; i < 8; i++)
			{
				int n = i + 1;
				String search = "Mob" + n;
				html.replace(search, DerbyTrackManager.getInstance().getRunnerName(i));
			}
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else if (command.equals("ShowTickets"))
		{
			final StringBuilder sb = new StringBuilder();
			
			for (ItemInstance ticket : player.getInventory().getAllItemsByItemId(4443))
			{
				if (ticket.getEnchantLevel() == DerbyTrackManager.getInstance().getRaceNumber())
					continue;
				
				StringUtil.append(sb, "<tr><td><a action=\"bypass -h npc_%objectId%_ShowTicket ", ticket.getObjectId(), "\">", ticket.getEnchantLevel(), " Race Number</a></td><td align=right><font color=\"LEVEL\">", ticket.getCustomType1(), "</font> Number</td><td align=right><font color=\"LEVEL\">", ticket.getCustomType2() * 100, "</font> Adena</td></tr>");
			}
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player.getLocale(), getHtmlPath(player, getTemplate().getNpcId(), 7));
			html.replace("%tickets%", sb.toString());
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else if (command.startsWith("ShowTicket"))
		{
			final int val = Integer.parseInt(command.substring(11));
			if (val == 0)
			{
				super.onBypassFeedback(player, "Chat 0");
				return;
			}
			
			final ItemInstance ticket = player.getInventory().getItemByObjectId(val);
			if (ticket == null)
			{
				super.onBypassFeedback(player, "Chat 0");
				return;
			}
			
			final int raceId = ticket.getEnchantLevel();
			final int lane = ticket.getCustomType1();
			final int bet = ticket.getCustomType2() * 100;
			
			final HistoryInfo info = DerbyTrackManager.getInstance().getHistoryInfo(raceId);
			if (info == null)
			{
				super.onBypassFeedback(player, "Chat 0");
				return;
			}
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player.getLocale(), getHtmlPath(player, getTemplate().getNpcId(), 8));
			html.replace("%raceId%", raceId);
			html.replace("%lane%", lane);
			html.replace("%bet%", bet);
			html.replace("%firstLane%", info.getFirst() + 1);
			html.replace("%odd%", (lane == info.getFirst() + 1) ? String.format(Locale.ENGLISH, "%.2f", info.getOddRate()) : "0.01");
			html.replace("%objectId%", getObjectId());
			html.replace("%ticketObjectId%", val);
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else if (command.startsWith("CalculateWin"))
		{
			final int val = Integer.parseInt(command.substring(13));
			if (val == 0)
			{
				super.onBypassFeedback(player, "Chat 0");
				return;
			}
			
			final ItemInstance ticket = player.getInventory().getItemByObjectId(val);
			if (ticket == null)
			{
				super.onBypassFeedback(player, "Chat 0");
				return;
			}
			
			final int raceId = ticket.getEnchantLevel();
			final int lane = ticket.getCustomType1();
			final int bet = ticket.getCustomType2() * 100;
			
			final HistoryInfo info = DerbyTrackManager.getInstance().getHistoryInfo(raceId);
			if (info == null)
			{
				super.onBypassFeedback(player, "Chat 0");
				return;
			}
			
			if (player.destroyItem(ticket, true))
				player.addAdena((int) (bet * ((lane == info.getFirst() + 1) ? info.getOddRate() : 0.01)), true);
			
			super.onBypassFeedback(player, "Chat 0");
		}
		else if (command.equals("ViewHistory"))
		{
			final StringBuilder sb = new StringBuilder();
			
			final int raceNumber = DerbyTrackManager.getInstance().getRaceNumber();
			
			for (HistoryInfo info : DerbyTrackManager.getInstance().getLastHistoryEntries())
				StringUtil.append(sb, "<tr><td><font color=\"LEVEL\">", info.getRaceId(), "</font> th</td><td><font color=\"LEVEL\">", (raceNumber == info.getRaceId()) ? 0 : info.getFirst() + 1, "</font> Lane </td><td><font color=\"LEVEL\">", (raceNumber == info.getRaceId()) ? 0 : info.getSecond() + 1, "</font> Lane</td><td align=right><font color=00ffff>", String.format(Locale.ENGLISH, "%.2f", info.getOddRate()), "</font> Times</td></tr>");
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player.getLocale(), getHtmlPath(player, getTemplate().getNpcId(), 9));
			html.replace("%infos%", sb.toString());
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else
			super.onBypassFeedback(player, command);
	}
	
	@Override
	public void addKnownObject(WorldObject object)
	{
		if (object instanceof Player objectPlayer)
			objectPlayer.sendPacket(DerbyTrackManager.getInstance().getRacePacket());
	}
	
	@Override
	public void removeKnownObject(WorldObject object)
	{
		super.removeKnownObject(object);
		
		if (object instanceof Player objectPlayer)
		{
			for (Npc npc : DerbyTrackManager.getInstance().getRunners())
				objectPlayer.sendPacket(new DeleteObject(npc));
		}
	}
}
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
package ext.mods.gameserver.handler.voicedcommandhandlers;

import java.util.List;
import java.util.stream.Collectors;

import ext.mods.Crypta.BattleBossData;
import ext.mods.battlerboss.holder.EventHolder;
import ext.mods.battlerboss.register.BattleBossOpenRegister;
import ext.mods.gameserver.handler.IVoicedCommandHandler;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;

public class VoicedBossBattle implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"battleboss",
		"battlebossinfo",
		"battlebossregister",
		"battlebossunregister"
	};
	
	private static final int EVENTS_PER_PAGE = 3;
	
	@Override
	public boolean useVoicedCommand(String command, Player player, String target)
	{
		if (command.startsWith("battleboss"))
		{
			showEventList(player, 1);
		}
		if (command.startsWith("battleboss "))
		{
			int page = 1;
			String[] args = command.split(" ");
			if (args.length > 1)
			{
				try
				{
					page = Integer.parseInt(args[1]);
				}
				catch (NumberFormatException ignored)
				{
				}
			}
			showEventList(player, page);
		}
		else if (command.startsWith("battlebossinfo"))
		{
			String[] args = command.split(" ");
			if (args.length >= 2)
			{
				int eventId = Integer.parseInt(args[1]);
				int page = (args.length > 2) ? Integer.parseInt(args[2]) : 1;
				showEventInfo(player, eventId, page);
			}
		}
		else if (command.startsWith("battlebossregister"))
		{
			String[] args = command.split(" ");
			if (args.length >= 2)
			{
				try
				{
					int eventId = Integer.parseInt(args[1]);
					register(player, eventId);
				}
				catch (NumberFormatException e)
				{
					player.sendMessage("Invalid event ID.");
				}
			}
			else
			{
				player.sendMessage("Usage: .battlebossregister <eventId>");
			}
		}
		else if (command.startsWith("battlebossunregister"))
		{
			String[] args = command.split(" ");
			if (args.length >= 2)
			{
				try
				{
					int eventId = Integer.parseInt(args[1]);
					unregister(player, eventId);
				}
				catch (NumberFormatException e)
				{
					player.sendMessage("Invalid event ID.");
				}
			}
			else
			{
				player.sendMessage("Usage: .battlebossregister <eventId>");
			}
		}
		return true;
	}
	
	private void showEventList(Player player, int page)
	{
		Object battleBossDataInstance = BattleBossData.getInstance();
		if (battleBossDataInstance == null)
		{
			player.sendMessage("BattleBossData não está disponível.");
			return;
		}
		
		List<EventHolder> events = null;
		try
		{
			Object result = BattleBossData.getInstance().getEvents();
			if (result instanceof List)
			{
				@SuppressWarnings("unchecked")
				List<EventHolder> tempList = (List<EventHolder>) result;
				events = tempList;
			}
		}
		catch (Exception e)
		{
			player.sendMessage("Erro ao acessar dados do BattleBoss.");
			e.printStackTrace();
			return;
		}
		
		if (events == null)
		{
			player.sendMessage("Nenhum evento encontrado.");
			return;
		}
		
		events = events.stream().filter(e -> e.getConfig().isEnabled()).collect(Collectors.toList());
		
		int totalPages = Math.max(1, (int) Math.ceil(events.size() / (double) EVENTS_PER_PAGE));
		page = Math.max(1, Math.min(page, totalPages));
		
		int startIndex = (page - 1) * EVENTS_PER_PAGE;
		int endIndex = Math.min(startIndex + EVENTS_PER_PAGE, events.size());
		
		StringBuilder sb = new StringBuilder();
		sb.append("<html><title>Battle Boss - Eventos</title><body><center>");
		
		for (int i = startIndex; i < endIndex; i++)
		{
			EventHolder event = events.get(i);
			
			String firstDescLine = "";
			if (!event.getInfo().getDesc().isEmpty())
			{
				firstDescLine = event.getInfo().getDesc().get(0);
			}
			
			sb.append("<table width=300 bgcolor=000000 border=0 cellspacing=2 cellpadding=4>");
			sb.append("<tr>");
			sb.append("<td valign=top>");
			String status = BattleBossOpenRegister.getInstance().isRegistrationOpen(event) ? " <font color=\"00FF00\">(Ativo)</font>" : "";
			
			sb.append("<font color=\"LEVEL\"><b>" + event.getInfo().getName() + status + "</b></font><br>");
			
			sb.append("<font color=\"AAAAAA\">" + firstDescLine + "</font><br>");
			
			sb.append("<table border=0 cellspacing=2 cellpadding=0><tr>");
			sb.append("<td><button value=\"Info\" action=\"bypass -h voiced_battlebossinfo " + event.getId() + " " + page + "\" width=60 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
			sb.append("<td><button value=\"Registrar\" action=\"bypass -h voiced_battlebossregister " + event.getId() + "\" width=80 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
			sb.append("<td><button value=\"unRegistrar\" action=\"bypass -h voiced_battlebossunregister " + event.getId() + "\" width=80 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
			sb.append("</tr></table>");
			
			sb.append("</td>");
			sb.append("<td width=32 valign=top><img src=\"" + event.getInfo().getIcon() + "\" width=32 height=32></td>");
			sb.append("</tr>");
			sb.append("</table><br>");
			
		}
		
		sb.append("<br>");
		for (int i = 1; i <= totalPages; i++)
		{
			if (i == page)
				sb.append("<font color=LEVEL>[" + i + "]</font> ");
			else
				sb.append("<a action=\"bypass -h voiced_battleboss " + i + "\">" + i + "</a> ");
		}
		
		sb.append("</center></body></html>");
		
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setHtml(sb.toString());
		player.sendPacket(html);
	}
	
	private void showEventInfo(Player player, int eventId, int returnPage)
	{
		Object battleBossDataInstance = BattleBossData.getInstance();
		if (battleBossDataInstance == null)
		{
			player.sendMessage("BattleBossData não está disponível.");
			return;
		}
		
		EventHolder event = null;
		try
		{
			event = (EventHolder) BattleBossData.getInstance().getEvent(eventId);
		}
		catch (Exception e)
		{
			player.sendMessage("Erro ao acessar dados do evento.");
			e.printStackTrace();
			return;
		}
		
		if (event == null)
		{
			player.sendMessage("Evento não encontrado.");
			return;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("<html><title>Battle Boss - Info</title><body><center>");
		
		sb.append("<table width=300 bgcolor=000000>");
		sb.append("<tr><td width=32><img src=\"" + event.getInfo().getIcon() + "\" width=32 height=32></td>");
		String status = BattleBossOpenRegister.getInstance().isRegistrationOpen(event) ? " <font color=\"00FF00\">(Ativo)</font>" : "";
		sb.append("<td><font color=\"LEVEL\">" + event.getInfo().getName() + status + "</font></td></tr>");
		
		sb.append("</table><br>");
		
		sb.append("<font color=LEVEL>Descrição:</font><br>");
		for (String line : event.getInfo().getDesc())
		{
			sb.append(line + "<br>");
		}
		
		sb.append("<br><font color=LEVEL>Dias:</font> " + event.getConfig().getDays().stream().map(VoicedBossBattle::dayOfWeekToString).collect(Collectors.joining(", ")) + "<br>");
		sb.append("<font color=LEVEL>Horários:</font> " + String.join(", ", event.getConfig().getTimes()) + "<br><br>");
		
		sb.append("<table border=0 cellspacing=2 cellpadding=0><tr>");
		sb.append("<td><button value=\"Back\" action=\"bypass -h voiced_battleboss " + returnPage + "\" width=60 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		sb.append("<td><button value=\"Registrar\" action=\"bypass -h voiced_battlebossregister " + event.getId() + "\" width=80 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		sb.append("<td><button value=\"unRegistrar\" action=\"bypass -h voiced_battlebossunregister " + event.getId() + "\" width=80 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		sb.append("</tr></table>");
		
	
		sb.append("</center></body></html>");
		
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setHtml(sb.toString());
		player.sendPacket(html);
	}
	
	private void register(Player player, int eventId)
	{
		Object battleBossDataInstance = BattleBossData.getInstance();
		if (battleBossDataInstance == null)
		{
			player.sendMessage("BattleBossData não está disponível.");
			return;
		}
		
		EventHolder event = null;
		try
		{
			event = (EventHolder) BattleBossData.getInstance().getEvent(eventId);
		}
		catch (Exception e)
		{
			player.sendMessage("Erro ao acessar dados do evento.");
			e.printStackTrace();
			return;
		}
		
		if (event == null)
		{
			player.sendMessage("Evento inválido.");
			return;
		}
		
		if (BattleBossOpenRegister.getInstance().isRegistrationOpen(event))
		{
			BattleBossOpenRegister.getInstance().register(event, player);
		}
		else
		{
			String daysString = event.getConfig().getDays().stream().map(VoicedBossBattle::dayOfWeekToString).collect(Collectors.joining(", "));
			String timesString = String.join(", ", event.getConfig().getTimes());
			player.sendMessage("O evento " + event.getInfo().getName() + " não está ativo no momento. Dias: " + daysString + " às " + timesString + ".");
		}
	}
	
	private void unregister(Player player, int eventId)
	{
		Object battleBossDataInstance = BattleBossData.getInstance();
		if (battleBossDataInstance == null)
		{
			player.sendMessage("BattleBossData não está disponível.");
			return;
		}
		
		EventHolder event = null;
		try
		{
			event = (EventHolder) BattleBossData.getInstance().getEvent(eventId);
		}
		catch (Exception e)
		{
			player.sendMessage("Erro ao acessar dados do evento.");
			e.printStackTrace();
			return;
		}
		
		if (event == null)
		{
			player.sendMessage("Evento inválido.");
			return;
		}
		
		if (BattleBossOpenRegister.getInstance().isRegistrationOpen(event))
		{
			BattleBossOpenRegister.getInstance().unregister(event, player);
		}
		else
		{
			String daysString = event.getConfig().getDays().stream().map(VoicedBossBattle::dayOfWeekToString).collect(Collectors.joining(", "));
			String timesString = String.join(", ", event.getConfig().getTimes());
			player.sendMessage("O evento " + event.getInfo().getName() + " não está ativo no momento. Dias: " + daysString + " às " + timesString + ".");
		}
	}
	
	private static String dayOfWeekToString(int day)
	{
		final String[] days =
		{
			"Sunday",
			"Monday",
			"Tuesday",
			"Wednesday",
			"Thursday",
			"Friday",
			"Saturday"
		};
		if (day >= 0 && day <= 6)
			return days[day];
		return "Unknown(" + day + ")";
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}

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

import ext.mods.Config;
import ext.mods.gameserver.handler.IVoicedCommandHandler;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.entity.events.capturetheflag.CTFEvent;
import ext.mods.gameserver.model.entity.events.deathmatch.DMEvent;
import ext.mods.gameserver.model.entity.events.lastman.LMEvent;
import ext.mods.gameserver.model.entity.events.teamvsteam.TvTEvent;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;

public class EventCommand implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"ctfinfo",
		"ctfjoin",
		"ctfleave",
		"dminfo",
		"dmjoin",
		"dmleave",
		"lminfo",
		"lmjoin",
		"lmleave",
		"tvtinfo",
		"tvtjoin",
		"tvtleave"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player player, String target)
	{
		if (command.equals("ctfinfo") || command.equals("ctfjoin") || command.equals("ctfleave"))
		{
			if (Config.CTF_EVENT_ENABLED)
			{
				switch (command)
				{
					case "ctfinfo":
						if (CTFEvent.getInstance().isStarting() || CTFEvent.getInstance().isStarted())
						{
							showCTFStatusPage(player);
							return true;
						}
						player.sendMessage(player.getSysString(10048));
						break;
					
					case "ctfjoin":
						if (!CTFEvent.getInstance().isPlayerParticipant(player.getObjectId()))
							CTFEvent.getInstance().onBypass("ctf_event_participation", player);
						else
							player.sendMessage(player.getSysString(10049));
						break;
					
					case "ctfleave":
						if (CTFEvent.getInstance().isPlayerParticipant(player.getObjectId()))
							CTFEvent.getInstance().onBypass("ctf_event_remove_participation", player);
						else
							player.sendMessage(player.getSysString(10050));
						break;
				}
			}
			else
				player.sendMessage(player.getSysString(10200));
			
			return true;
		}
		
		if (command.equals("dminfo") || command.equals("dmjoin") || command.equals("dmleave"))
		{
			if (Config.DM_EVENT_ENABLED)
			{
				switch (command)
				{
					case "dminfo":
						if (DMEvent.getInstance().isStarting() || DMEvent.getInstance().isStarted())
						{
							showDMStatusPage(player);
							return true;
						}
						player.sendMessage(player.getSysString(10051));
						break;
					
					case "dmjoin":
						if (!DMEvent.getInstance().isPlayerParticipant(player))
							DMEvent.getInstance().onBypass("dm_event_participation", player);
						else
							player.sendMessage(player.getSysString(10052));
						break;
					
					case "dmleave":
						if (DMEvent.getInstance().isPlayerParticipant(player))
							DMEvent.getInstance().onBypass("dm_event_remove_participation", player);
						else
							player.sendMessage(player.getSysString(10053));
						break;
				}
			}
			else
				player.sendMessage(player.getSysString(10201));
			
			return true;
		}
		
		if (command.equals("lminfo") || command.equals("lmjoin") || command.equals("lmleave"))
		{
			if (Config.LM_EVENT_ENABLED)
			{
				switch (command)
				{
					case "lminfo":
						if (LMEvent.getInstance().isStarting() || LMEvent.getInstance().isStarted())
						{
							showLMStatusPage(player);
							return true;
						}
						player.sendMessage(player.getSysString(10054));
						break;
					
					case "lmjoin":
						if (!LMEvent.getInstance().isPlayerParticipant(player))
							LMEvent.getInstance().onBypass("lm_event_participation", player);
						else
							player.sendMessage(player.getSysString(10055));
						break;
					
					case "lmleave":
						if (LMEvent.getInstance().isPlayerParticipant(player))
							LMEvent.getInstance().onBypass("lm_event_remove_participation", player);
						else
							player.sendMessage(player.getSysString(10056));
						break;
				}
			}
			else
				player.sendMessage(player.getSysString(10200));
			
			return true;
		}
		
		if (command.equals("tvtinfo") || command.equals("tvtjoin") || command.equals("tvtleave"))
		{
			if (Config.TVT_EVENT_ENABLED)
			{
				switch (command)
				{
					case "tvtinfo":
						if (TvTEvent.getInstance().isStarting() || TvTEvent.getInstance().isStarted())
						{
							showTvTStatusPage(player);
							return true;
						}
						player.sendMessage(player.getSysString(10057));
						break;
					
					case "tvtjoin":
						if (!TvTEvent.getInstance().isPlayerParticipant(player.getObjectId()))
							TvTEvent.getInstance().onBypass("tvt_event_participation", player);
						else
							player.sendMessage(player.getSysString(10058));
						break;
					
					case "tvtleave":
						if (TvTEvent.getInstance().isPlayerParticipant(player.getObjectId()))
							TvTEvent.getInstance().onBypass("tvt_event_remove_participation", player);
						else
							player.sendMessage(player.getSysString(10059));
						break;
				}
			}
			else
				player.sendMessage(player.getSysString(10200));
			
			return true;
		}
		
		return false;
	}
	
	private void showCTFStatusPage(Player player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player.getObjectId());
		html.setFile(player.getLocale(), "html/mods/events/ctf/Status.htm");
		html.replace("%team1name%", Config.CTF_EVENT_TEAM_1_NAME);
		html.replace("%team1playercount%", String.valueOf(CTFEvent.getInstance().getTeamsPlayerCounts()[0]));
		html.replace("%team1points%", String.valueOf(CTFEvent.getInstance().getTeamsPoints()[0]));
		html.replace("%team2name%", Config.CTF_EVENT_TEAM_2_NAME);
		html.replace("%team2playercount%", String.valueOf(CTFEvent.getInstance().getTeamsPlayerCounts()[1]));
		html.replace("%team2points%", String.valueOf(CTFEvent.getInstance().getTeamsPoints()[1]));
		player.sendPacket(html);
	}
	
	private void showDMStatusPage(Player player)
	{
		String[] firstPositions = DMEvent.getInstance().getFirstPosition(Config.DM_REWARD_FIRST_PLAYERS);
		NpcHtmlMessage html = new NpcHtmlMessage(player.getObjectId());
		html.setFile(player.getLocale(), "html/mods/events/dm/Status.htm");
		
		String htmltext = "";
		if (firstPositions != null)
		{
			for (int i = 0; i < firstPositions.length; i++)
			{
				String[] row = firstPositions[i].split("\\,");
				htmltext += "<tr><td>" + row[0] + "</td><td width=\"100\" align=\"center\">" + row[1] + "</td></tr>";
			}
		}
		
		html.replace("%positions%", htmltext);
		player.sendPacket(html);
	}
	
	private void showLMStatusPage(Player player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player.getObjectId());
		html.setFile(player.getLocale(), "html/mods/events/lm/Status.htm");
		String htmltext = String.valueOf(LMEvent.getInstance().getPlayerCounts());
		html.replace("%countplayer%", htmltext);
		player.sendPacket(html);
	}
	
	private void showTvTStatusPage(Player player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player.getObjectId());
		html.setFile(player.getLocale(), "html/mods/events/tvt/Status.htm");
		html.replace("%team1name%", Config.TVT_EVENT_TEAM_1_NAME);
		html.replace("%team1playercount%", String.valueOf(TvTEvent.getInstance().getTeamsPlayerCounts()[0]));
		html.replace("%team1points%", String.valueOf(TvTEvent.getInstance().getTeamsPoints()[0]));
		html.replace("%team2name%", Config.TVT_EVENT_TEAM_2_NAME);
		html.replace("%team2playercount%", String.valueOf(TvTEvent.getInstance().getTeamsPlayerCounts()[1]));
		html.replace("%team2points%", String.valueOf(TvTEvent.getInstance().getTeamsPoints()[1]));
		player.sendPacket(html);
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
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

import ext.mods.Config;
import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.entity.events.capturetheflag.CTFEvent;
import ext.mods.gameserver.model.entity.events.deathmatch.DMEvent;
import ext.mods.gameserver.model.entity.events.lastman.LMEvent;
import ext.mods.gameserver.model.entity.events.teamvsteam.TvTEvent;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;

public class EventManager extends Npc
{
	private static final String ctfhtmlPath = "html/mods/events/ctf/";
	private static final String TvthtmlPath = "html/mods/events/tvt/";
	private static final String dmhtmlPath = "html/mods/events/dm/";
	private static final String lmhtmlPath = "html/mods/events/lm/";
	
	public EventManager(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		CTFEvent.getInstance().onBypass(command, player);
		TvTEvent.getInstance().onBypass(command, player);
		DMEvent.getInstance().onBypass(command, player);
		LMEvent.getInstance().onBypass(command, player);
	}
	
	@Override
	public void showChatWindow(Player player, int val)
	{
		if (player == null)
			return;
		
		if (TvTEvent.getInstance().isParticipating())
		{
			final boolean isParticipant = TvTEvent.getInstance().isPlayerParticipant(player.getObjectId());
			final String htmContent;
			
			if (!isParticipant)
				htmContent = HTMLData.getInstance().getHtm(player, TvthtmlPath + "Participation.htm");
			else
				htmContent = HTMLData.getInstance().getHtm(player, TvthtmlPath + "RemoveParticipation.htm");
			
			if (htmContent != null)
			{
				int[] teamsPlayerCounts = TvTEvent.getInstance().getTeamsPlayerCounts();
				NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
				
				npcHtmlMessage.setHtml(htmContent);
				npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));
				npcHtmlMessage.replace("%team1name%", Config.TVT_EVENT_TEAM_1_NAME);
				npcHtmlMessage.replace("%team1playercount%", String.valueOf(teamsPlayerCounts[0]));
				npcHtmlMessage.replace("%team2name%", Config.TVT_EVENT_TEAM_2_NAME);
				npcHtmlMessage.replace("%team2playercount%", String.valueOf(teamsPlayerCounts[1]));
				npcHtmlMessage.replace("%playercount%", String.valueOf(teamsPlayerCounts[0] + teamsPlayerCounts[1]));
				if (!isParticipant)
					npcHtmlMessage.replace("%fee%", TvTEvent.getInstance().getParticipationFee());
				
				player.sendPacket(npcHtmlMessage);
			}
		}
		else if (TvTEvent.getInstance().isStarting() || TvTEvent.getInstance().isStarted())
		{
			final String htmContent = HTMLData.getInstance().getHtm(player, TvthtmlPath + "Status.htm");
			
			if (htmContent != null)
			{
				int[] teamsPlayerCounts = TvTEvent.getInstance().getTeamsPlayerCounts();
				int[] teamsPointsCounts = TvTEvent.getInstance().getTeamsPoints();
				NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
				
				npcHtmlMessage.setHtml(htmContent);
				npcHtmlMessage.replace("%team1name%", Config.TVT_EVENT_TEAM_1_NAME);
				npcHtmlMessage.replace("%team1playercount%", String.valueOf(teamsPlayerCounts[0]));
				npcHtmlMessage.replace("%team1points%", String.valueOf(teamsPointsCounts[0]));
				npcHtmlMessage.replace("%team2name%", Config.TVT_EVENT_TEAM_2_NAME);
				npcHtmlMessage.replace("%team2playercount%", String.valueOf(teamsPlayerCounts[1]));
				npcHtmlMessage.replace("%team2points%", String.valueOf(teamsPointsCounts[1]));
				player.sendPacket(npcHtmlMessage);
			}
		}
		else if (DMEvent.getInstance().isParticipating())
		{
			final boolean isParticipant = DMEvent.getInstance().isPlayerParticipant(player.getObjectId());
			final String htmContent;
			
			if (!isParticipant)
				htmContent = HTMLData.getInstance().getHtm(player, dmhtmlPath + "Participation.htm");
			else
				htmContent = HTMLData.getInstance().getHtm(player, dmhtmlPath + "RemoveParticipation.htm");
			
			if (htmContent != null)
			{
				int PlayerCounts = DMEvent.getInstance().getPlayerCounts();
				NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
				
				npcHtmlMessage.setHtml(htmContent);
				npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));
				npcHtmlMessage.replace("%playercount%", String.valueOf(PlayerCounts));
				if (!isParticipant)
					npcHtmlMessage.replace("%fee%", DMEvent.getInstance().getParticipationFee());
				
				player.sendPacket(npcHtmlMessage);
			}
		}
		else if (DMEvent.getInstance().isStarting() || DMEvent.getInstance().isStarted())
		{
			final String htmContent = HTMLData.getInstance().getHtm(player, dmhtmlPath + "Status.htm");
			
			if (htmContent != null)
			{
				String[] firstPositions = DMEvent.getInstance().getFirstPosition(Config.DM_REWARD_FIRST_PLAYERS);
				NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
				
				String htmltext = "";
				if (firstPositions != null)
				{
					for (int i = 0; i < firstPositions.length; i++)
					{
						String[] row = firstPositions[i].split("\\,");
						htmltext += "<tr><td></td><td>" + row[0] + "</td><td align=\"center\">" + row[1] + "</td></tr>";
					}
				}
				
				npcHtmlMessage.setHtml(htmContent);
				npcHtmlMessage.replace("%positions%", htmltext);
				player.sendPacket(npcHtmlMessage);
			}
		}
		else if (LMEvent.getInstance().isParticipating())
		{
			final boolean isParticipant = LMEvent.getInstance().isPlayerParticipant(player.getObjectId());
			final String htmContent;
			
			if (!isParticipant)
				htmContent = HTMLData.getInstance().getHtm(player, lmhtmlPath + "Participation.htm");
			else
				htmContent = HTMLData.getInstance().getHtm(player, lmhtmlPath + "RemoveParticipation.htm");
			
			if (htmContent != null)
			{
				NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
				
				npcHtmlMessage.setHtml(htmContent);
				npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));
				npcHtmlMessage.replace("%playercount%", String.valueOf(LMEvent.getInstance().getPlayerCounts()));
				if (!isParticipant)
					npcHtmlMessage.replace("%fee%", LMEvent.getInstance().getParticipationFee());
				
				player.sendPacket(npcHtmlMessage);
			}
		}
		else if (LMEvent.getInstance().isStarting() || LMEvent.getInstance().isStarted())
		{
			final String htmContent = HTMLData.getInstance().getHtm(player, lmhtmlPath + "Status.htm");
			
			if (htmContent != null)
			{
				NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
				String htmltext = "";
				htmltext = String.valueOf(LMEvent.getInstance().getPlayerCounts());
				npcHtmlMessage.setHtml(htmContent);
				npcHtmlMessage.replace("%countplayer%", htmltext);
				player.sendPacket(npcHtmlMessage);
			}
		}
		else if (CTFEvent.getInstance().isParticipating())
		{
			final boolean isParticipant = CTFEvent.getInstance().isPlayerParticipant(player.getObjectId());
			final String htmContent;
			
			if (!isParticipant)
				htmContent = HTMLData.getInstance().getHtm(player, ctfhtmlPath + "Participation.htm");
			else
				htmContent = HTMLData.getInstance().getHtm(player, ctfhtmlPath + "RemoveParticipation.htm");
			
			if (htmContent != null)
			{
				int[] teamsPlayerCounts = CTFEvent.getInstance().getTeamsPlayerCounts();
				NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
				
				npcHtmlMessage.setHtml(htmContent);
				npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));
				npcHtmlMessage.replace("%team1name%", Config.CTF_EVENT_TEAM_1_NAME);
				npcHtmlMessage.replace("%team1playercount%", String.valueOf(teamsPlayerCounts[0]));
				npcHtmlMessage.replace("%team2name%", Config.CTF_EVENT_TEAM_2_NAME);
				npcHtmlMessage.replace("%team2playercount%", String.valueOf(teamsPlayerCounts[1]));
				npcHtmlMessage.replace("%playercount%", String.valueOf(teamsPlayerCounts[0] + teamsPlayerCounts[1]));
				if (!isParticipant)
					npcHtmlMessage.replace("%fee%", CTFEvent.getInstance().getParticipationFee());
				
				player.sendPacket(npcHtmlMessage);
			}
		}
		else if (CTFEvent.getInstance().isStarting() || CTFEvent.getInstance().isStarted())
		{
			final String htmContent = HTMLData.getInstance().getHtm(player, ctfhtmlPath + "Status.htm");
			
			if (htmContent != null)
			{
				int[] teamsPlayerCounts = CTFEvent.getInstance().getTeamsPlayerCounts();
				int[] teamsPointsCounts = CTFEvent.getInstance().getTeamsPoints();
				NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
				
				npcHtmlMessage.setHtml(htmContent);
				npcHtmlMessage.replace("%team1name%", Config.CTF_EVENT_TEAM_1_NAME);
				npcHtmlMessage.replace("%team1playercount%", String.valueOf(teamsPlayerCounts[0]));
				npcHtmlMessage.replace("%team1points%", String.valueOf(teamsPointsCounts[0]));
				npcHtmlMessage.replace("%team2name%", Config.CTF_EVENT_TEAM_2_NAME);
				npcHtmlMessage.replace("%team2playercount%", String.valueOf(teamsPlayerCounts[1]));
				npcHtmlMessage.replace("%team2points%", String.valueOf(teamsPointsCounts[1]));
				player.sendPacket(npcHtmlMessage);
			}
		}
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	@Override
	public boolean isInvul()
	{
		return true;
	}
}
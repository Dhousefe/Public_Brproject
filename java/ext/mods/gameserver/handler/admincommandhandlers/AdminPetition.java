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
package ext.mods.gameserver.handler.admincommandhandlers;

import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

import ext.mods.commons.data.Pagination;
import ext.mods.commons.lang.StringUtil;

import ext.mods.gameserver.data.manager.PetitionManager;
import ext.mods.gameserver.enums.petitions.PetitionState;
import ext.mods.gameserver.enums.petitions.PetitionType;
import ext.mods.gameserver.handler.IAdminCommandHandler;
import ext.mods.gameserver.model.Petition;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public class AdminPetition implements IAdminCommandHandler
{
	private static final String UNFOLLOW_BUTTON = "<td><button value=\"Unfollow\" action=\"bypass -h admin_petition unfollow\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></td>";
	private static final String BUTTONS = "<center><img src=\"L2UI.SquareGray\" width=280 height=1><br><table width=130><tr><td><button value=\"Join\" action=\"bypass -h admin_petition join %id%\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></td><td><button value=\"Reject\" action=\"bypass -h admin_petition reject %id%\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></td></tr></table></center>";
	private static final String FEEDBACK = "<center><img src=\"L2UI.SquareGray\" width=280 height=1><br><table width=280><tr><td>Rate: %rate%</td></tr><tr><td>Feedback: %feedback%</td></tr></table></center>";
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_petition",
		"admin_force_peti",
		"admin_add_peti_chat"
	};
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		st.nextToken();
		
		if (command.startsWith("admin_petition"))
		{
			int page = 1;
			
			if (!st.hasMoreTokens())
			{
				showPendingPetitions(player, page);
				return;
			}
			
			final String param = st.nextToken();
			if (StringUtil.isDigit(param))
				page = Integer.parseInt(param);
			else
			{
				try
				{
					switch (param)
					{
						case "join":
							int petitionId = Integer.parseInt(st.nextToken());
							
							if (!PetitionManager.getInstance().joinPetition(player, petitionId, false))
								player.sendPacket(SystemMessageId.NOT_UNDER_PETITION_CONSULTATION);
							break;
						
						case "reject":
							petitionId = Integer.parseInt(st.nextToken());
							if (!PetitionManager.getInstance().rejectPetition(player, petitionId))
								player.sendPacket(SystemMessageId.FAILED_CANCEL_PETITION_TRY_LATER);
							break;
						
						case "reset":
							if (PetitionManager.getInstance().isAnyPetitionInProcess())
							{
								player.sendPacket(SystemMessageId.PETITION_UNDER_PROCESS);
								return;
							}
							PetitionManager.getInstance().clearPetitions();
							break;
						
						case "show":
							petitionId = Integer.parseInt(st.nextToken());
							PetitionManager.getInstance().showCompleteLog(player, petitionId);
							break;
						
						case "unfollow":
							PetitionManager.getInstance().abortActivePetition(player);
							break;
						
						case "view":
							petitionId = Integer.parseInt(st.nextToken());
							showPetition(player, petitionId);
							return;
						
						default:
							player.sendMessage("Usage: //petition [join|reject|reset|show|unfollow|view]");
							break;
					}
				}
				catch (Exception e)
				{
					player.sendMessage("Usage: //petition [join|reject|reset|show|unfollow|view]");
				}
			}
			showPendingPetitions(player, page);
		}
		else if (command.startsWith("admin_add_peti_chat"))
		{
			final Player targetPlayer = getTargetPlayer(player, false);
			if (targetPlayer == null || !targetPlayer.isOnline())
			{
				player.sendPacket(SystemMessageId.CLIENT_NOT_LOGGED_ONTO_GAME_SERVER);
				return;
			}
			
			if (player == targetPlayer)
			{
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PETITION_ADDING_S1_FAILED_ERROR_NUMBER_S2).addCharName(targetPlayer).addNumber(1));
				return;
			}
			
			final Petition petition = PetitionManager.getInstance().getPetitionInProcess(player);
			if (petition == null)
			{
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PETITION_ADDING_S1_FAILED_ERROR_NUMBER_S2).addCharName(targetPlayer).addNumber(2));
				return;
			}
			
			if (petition.getPetitionerObjectId() == targetPlayer.getObjectId())
			{
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PETITION_ADDING_S1_FAILED_ERROR_NUMBER_S2).addCharName(targetPlayer).addNumber(3));
				return;
			}
			
			if (petition.getResponders().contains(targetPlayer.getObjectId()))
			{
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PETITION_ADDING_S1_FAILED_ERROR_NUMBER_S2).addCharName(targetPlayer).addNumber(4));
				return;
			}
			
			petition.addAdditionalResponder(player, targetPlayer);
		}
		else if (command.startsWith("admin_force_peti"))
		{
			final Player targetPlayer = getTargetPlayer(player, false);
			if (targetPlayer == null || !targetPlayer.isOnline())
			{
				player.sendPacket(SystemMessageId.CLIENT_NOT_LOGGED_ONTO_GAME_SERVER);
				return;
			}
			
			if (player == targetPlayer)
			{
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PETITION_FAILED_FOR_S1_ERROR_NUMBER_S2).addCharName(targetPlayer).addNumber(1));
				return;
			}
			
			if (PetitionManager.getInstance().isActivePetition(targetPlayer))
			{
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PETITION_FAILED_S1_ALREADY_SUBMITTED).addCharName(targetPlayer));
				return;
			}
			
			final int petitionId = PetitionManager.getInstance().submitPetition(PetitionType.OTHER, targetPlayer, "");
			
			if (!PetitionManager.getInstance().joinPetition(player, petitionId, true))
				player.sendPacket(SystemMessageId.NOT_UNDER_PETITION_CONSULTATION);
		}
	}
	
	public void showPendingPetitions(Player player, int page)
	{
		final Petition activePetition = PetitionManager.getInstance().getPetitionInProcess(player);
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(player.getLocale(), "html/admin/petitions.htm");
		html.replace("%unfollow%", (activePetition != null) ? UNFOLLOW_BUTTON : "");
		
		final Pagination<Petition> list = new Pagination<>(PetitionManager.getInstance().getPetitions().values().stream(), page, PAGE_LIMIT_7);
		for (Petition petition : list)
		{
			final String isReaded = (!petition.isUnread()) ? "party_styleicon1_2" : "QuestWndInfoIcon_5";
			final String playerName;
			final String petitionerStatus;
			
			final Player petitioner = World.getInstance().getPlayer(petition.getPetitionerObjectId());
			if (petitioner != null && petitioner.isOnline())
			{
				playerName = petitioner.getName();
				petitionerStatus = "1";
			}
			else
			{
				playerName = petition.getPetitionerName();
				petitionerStatus = "4";
			}
			
			list.append(((list.indexOf(petition) % 2) == 0 ? "<table width=280 height=40 bgcolor=000000>" : "<table width=280 height=40>"));
			
			list.append("<tr><td width=20 align=center><img src=\"L2UI_CH3.msnicon", petitionerStatus, "\" width=12 height=16><img src=\"L2UI_CH3.", isReaded, "\" width=11 height=16></td>");
			
			if (activePetition != null && activePetition.getId() == petition.getId())
				list.append("<td width=260>#", petition.getId(), " by ", playerName, "<br1><font color=B09878>Type:</font> ", petition.getType(), " <font color=B09878>State:</font> ", petition.getState(), "</td>");
			else
				list.append("<td width=260><a action=\"bypass -h admin_petition view ", petition.getId(), "\">#", petition.getId(), " by ", playerName, "</a><br1><font color=B09878>Type:</font> ", petition.getType(), " <font color=B09878>State:</font> ", petition.getState(), "</td>");
			
			list.append("</tr></table><img src=\"L2UI.SquareGray\" width=280 height=1>");
		}
		list.generateSpace(41);
		list.generatePages("bypass admin_petition %page%");
		
		html.replace("%content%", list.getContent());
		player.sendPacket(html);
	}
	
	public void showPetition(Player player, int id)
	{
		if (!player.isGM())
			return;
		
		final Petition petition = PetitionManager.getInstance().getPetitions().get(id);
		if (petition == null)
			return;
		
		final Player petitioner = World.getInstance().getPlayer(petition.getPetitionerObjectId());
		final String petitionerStatus = (petitioner != null && petitioner.isOnline()) ? "online" : "offline";
		
		petition.setAsRead();
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(player.getLocale(), "html/admin/petition.htm");
		html.replace("%submitDate%", new SimpleDateFormat("dd-MM-yyyy HH:mm").format(petition.getSubmitDate()));
		html.replace("%petitionerName%", petition.getPetitionerName());
		html.replace("%petitionerStatus%", petitionerStatus);
		html.replace("%type%", petition.getType().toString());
		html.replace("%state%", petition.getState().toString());
		html.replace("%responders%", petition.getFormattedResponders());
		html.replace("%content%", petition.getContent());
		
		if (petition.getState() == PetitionState.PENDING || petition.getState() == PetitionState.ACCEPTED)
			html.replace("%buttonsOrFeedback%", BUTTONS);
		else if (petition.getState() == PetitionState.CLOSED)
		{
			html.replace("%buttonsOrFeedback%", FEEDBACK);
			html.replace("%rate%", petition.getRate().getDesc());
			html.replace("%feedback%", petition.getFeedback());
		}
		else
			html.replace("%buttonsOrFeedback%", "");
		
		html.replace("%id%", petition.getId());
		player.sendPacket(html);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
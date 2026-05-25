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

import java.util.Map;
import java.util.StringTokenizer;

import ext.mods.commons.lang.StringUtil;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.manager.CastleManager;
import ext.mods.gameserver.handler.IAdminCommandHandler;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.SpawnLocation;
import ext.mods.gameserver.model.location.TowerSpawnLocation;
import ext.mods.gameserver.model.residence.castle.Castle;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.network.serverpackets.SiegeInfo;

public class AdminSiege implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_castle",
		"admin_siege"
	};
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		command = st.nextToken();
		
		String param = null;
		Castle castle = null;
		
		final int paramCount = st.countTokens();
		if (paramCount == 1)
			castle = CastleManager.getInstance().getCastleByAlias(st.nextToken());
		else if (paramCount == 2)
		{
			param = st.nextToken();
			castle = CastleManager.getInstance().getCastleByAlias(st.nextToken());
		}
		
		if (castle == null)
		{
			showCastleSelectPage(player);
			return;
		}
		
		if (param == null)
		{
			showCastleSelectPage(player, castle);
			return;
		}
		
		if (command.startsWith("admin_castle"))
		{
			switch (param)
			{
				case "set":
					final Player targetPlayer = getTargetPlayer(player, false);
					if (targetPlayer == null || targetPlayer.getClan() == null)
						player.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
					else if (targetPlayer.getClan().hasCastle())
						player.sendMessage(targetPlayer.getName() + "'s clan already owns a castle.");
					else
					{
						castle.setOwner(targetPlayer.getClan());
						
						final Map<Integer, Integer> skill = player.isClanLeader() ? castle.getSkillsLeader() : castle.getSkillsMember();
						if (castle.getId() == targetPlayer.getClan().getCastleId())
						{
							skill.forEach((skillId, skillLvl) ->
							{
								player.addSkill(SkillTable.getInstance().getInfo(skillId, skillLvl), true);
							});
						}
					}
					break;
				
				case "remove":
					if (castle.getOwnerId() > 0)
						castle.removeOwner();
					else
						player.sendMessage("This castle does not have an owner.");
					break;
				
				case "certificates":
					castle.setLeftCertificates(300, true);
					player.sendMessage(castle.getName() + "'s castle certificates are reset.");
					break;
				
				case "tax":
					castle.updateTaxes();
					player.sendMessage(castle.getName() + "'s taxes have been updated.");
					break;
				
				default:
					player.sendMessage("Usage: //castle [set|remove|certificates|tax castleName].");
					break;
			}
		}
		else if (command.startsWith("admin_siege"))
		{
			switch (param)
			{
				case "attack":
					Player targetPlayer = getTargetPlayer(player, false);
					if (targetPlayer == null)
						player.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
					else
						castle.getSiege().registerAttacker(targetPlayer);
					break;
				
				case "clear":
					castle.getSiege().clearAllClans();
					break;
				
				case "defend":
					targetPlayer = getTargetPlayer(player, false);
					if (targetPlayer == null)
						player.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
					else
						castle.getSiege().registerDefender(targetPlayer);
					break;
				
				case "end":
					castle.getSiege().endSiege();
					break;
				
				case "list":
					player.sendPacket(new SiegeInfo(castle));
					return;
				
				case "start":
					castle.getSiege().startSiege();
					break;
				
				default:
					player.sendMessage("Usage: //siege [attack|clear|defend|end|list|start castleName].");
					break;
			}
		}
		showCastleSelectPage(player, castle);
	}
	
	/**
	 * Show detailed informations of a {@link Castle} to a {@link Player}.
	 * @param player : The {@link Player} who requested the action.
	 * @param castle : The {@link Castle} to show informations.
	 */
	private static void showCastleSelectPage(Player player, Castle castle)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(player.getLocale(), "html/admin/castle.htm");
		html.replace("%castleName%", castle.getName());
		html.replace("%castleAlias%", castle.getAlias());
		html.replace("%circletId%", castle.getCircletId());
		html.replace("%artifactId%", castle.getArtifacts().toString());
		html.replace("%ticketsNumber%", castle.getTickets().size());
		html.replace("%droppedTicketsNumber%", castle.getDroppedTickets().size());
		html.replace("%npcsNumber%", castle.getNpcs().size());
		html.replace("%certificates%", castle.getLeftCertificates());
		html.replace("%parent%", castle.getParentId());
		html.replace("%aliveLifeTowers%", castle.getAliveLifeTowerCount());
		
		html.replace("%defaultTax%", castle.getDefaultTaxRate());
		html.replace("%currentTax%", castle.getCurrentTaxPercent());
		html.replace("%nextTax%", castle.getNextTaxPercent());
		html.replace("%taxSysgetRate%", castle.getTaxSysgetRate());
		html.replace("%taxRevenue%", StringUtil.formatNumber(castle.getTaxRevenue()));
		html.replace("%tributeRate%", StringUtil.formatNumber(castle.getTributeRate()));
		html.replace("%seedIncome%", StringUtil.formatNumber(castle.getSeedIncome()));
		html.replace("%treasury%", StringUtil.formatNumber(castle.getTreasury()));
		
		final StringBuilder sb = new StringBuilder();
		int index = 1;
		
		for (SpawnLocation spawn : castle.getArtifacts().values())
		{
			StringUtil.append(sb, "<a action=\"bypass -h admin_teleport ", spawn.toString().replace(",", ""), "\">[", index, "]</a>&nbsp;&nbsp;");
			index++;
		}
		
		html.replace("%artifacts%", sb.toString());
		
		sb.setLength(0);
		index = 1;
		
		for (TowerSpawnLocation spawn : castle.getControlTowers())
		{
			StringUtil.append(sb, "<a action=\"bypass -h admin_teleport ", spawn.toString().replace(",", ""), "\">[", index, "]</a>&nbsp;&nbsp;");
			index++;
		}
		
		html.replace("%ct%", sb.toString());
		player.sendPacket(html);
	}
	
	/**
	 * Show the complete list of {@link Castle}s.
	 * @param player : The {@link Player} who requested the action.
	 */
	private static void showCastleSelectPage(Player player)
	{
		int row = 0;
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(player.getLocale(), "html/admin/castles.htm");
		
		final StringBuilder sb = new StringBuilder();
		for (Castle castle : CastleManager.getInstance().getCastles())
		{
			sb.append(((row % 2) == 0 ? "<table width=300 bgcolor=000000><tr>" : "<table width=300><tr>"));
			
			StringUtil.append(sb, "<td width=90><a action=\"bypass -h admin_siege ", castle.getAlias(), "\">", castle.getName(), "</a></td><td width=130>", castle.getSiege().getStatusTranslation(player, castle.getSiege().getStatus()), "</td><td width=80 align=right><a action=\"bypass admin_siege list ", castle.getAlias(), "\">" + player.getSysString(10_170) + "</a></td>");
			
			sb.append("</tr></table><img src=\"L2UI.SquareGray\" width=270 height=1>");
			row++;
		}
		html.replace("%castles%", sb.toString());
		player.sendPacket(html);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
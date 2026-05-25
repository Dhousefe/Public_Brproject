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
package ext.mods.Crypta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;

import ext.mods.commons.pool.ConnectionPool;
import ext.mods.gameserver.data.sql.PlayerInfoTable;
import ext.mods.gameserver.data.xml.ItemData;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;

import ext.mods.util.Tokenizer;

public class PlayerEmailManager
{
	private static Logger LOGGER = Logger.getLogger(PlayerEmailManager.class.getName());
	
	public void handlerHtm(Player player, Tokenizer tokenizer)
	{
		String filename = tokenizer.getToken(2);
		navi(player, filename);
	}
	
	public void navi(Player player, String filename)
	{
		NpcHtmlMessage htm = new NpcHtmlMessage(0);
		htm.setFile(player.getLocale(), "html/mods/menu/" + filename);
		
		StringBuilder sb = new StringBuilder();
		String targetName = player.getSelectedEmailTarget();
		
		sb.append("<table width=300 cellpadding=2 cellspacing=2 align=center bgcolor=000000>");
		
		sb.append("<tr>");
		sb.append("<td width=60 align=right><font color=LEVEL>Jogador:</font></td>");
		
		if (targetName != null)
		{
			sb.append("<td width=120 align=left><font color=00FF00>" + targetName + "</font></td>");
			sb.append("<td width=80 align=center>");
			sb.append("<button value=\"Limpar\" action=\"bypass -h voiced_email clean\" width=55 height=15 back=sek.cbui94 fore=sek.cbui92>");
			sb.append("</td>");
			
		}
		else
		{
			sb.append("<td width=120 align=left><edit var=\"targetName\" width=150></td>");
			sb.append("<td width=80 align=center>");
			sb.append("<button value=\"Buscar\" action=\"bypass -h voiced_email search $targetName\" width=55 height=15 back=sek.cbui94 fore=sek.cbui92><br>");
			sb.append("</td>");
		}
		sb.append("</tr>");
		sb.append("</table><br>");
		
		htm.replace("%playerinfo%", sb.toString());
		htm.replace("%targetName%", targetName != null ? targetName : "invalido");
		player.sendPacket(htm);
	}
	
	public void searchPlayer(Player player, String targetName)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><title>Email Search</title><body><center>");
		sb.append("<font color=LEVEL>Resultado da Busca</font>");
		
		sb.append("<table width=300 border=1 cellspacing=0 cellpadding=3 bgcolor=000000>");
		sb.append("<tr>");
		sb.append("<td width=60 align=center><font color=LEVEL>Status</font></td>");
		sb.append("<td width=110 align=center><font color=LEVEL>Nome</font></td>");
		sb.append("<td width=50 align=center><font color=LEVEL>Ação</font></td>");
		sb.append("</tr>");
		sb.append("</table>");
		

		sb.append("<table width=300 cellspacing=0 cellpadding=3 bgcolor=000000>");
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT char_name, online FROM characters " + "WHERE LOWER(char_name) LIKE ? AND char_name != ? AND accesslevel = 0 " + "ORDER BY online DESC, char_name LIMIT 15"))
		{
			ps.setString(1, "%" + targetName.toLowerCase() + "%");
			ps.setString(2, player.getName());
			
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					String name = rs.getString("char_name");
					
					String displayName = name.length() > 12 ? name.substring(0, 10) + "..." : name;
					boolean isOnline = rs.getInt("online") == 1;
					
					sb.append("<tr>");
					sb.append("<td width=60 align=center><font color=").append(isOnline ? "00FF00" : "FF0000").append(">").append(isOnline ? "Online" : "Offline").append("</font></td>");
					sb.append("<td width=110 align=center>").append(displayName).append("</td>");
					sb.append("<td width=50 align=center>");
					sb.append("<button value=\"OK\" action=\"bypass -h voiced_email target ").append(name).append("\" width=40 height=21 back=sek.cbui94 fore=sek.cbui92>");
					sb.append("</td></tr>");
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.warning("searchPlayer DB error: " + e.getMessage());
		}
		
		sb.append("</table><br>");
		sb.append("<button value=\"Voltar\" action=\"bypass -h voiced_email htm email_main.htm\" width=80 height=21 back=\"L2UI_CH3.Btn1_normalOn\" fore=\"L2UI_CH3.Btn1_normal\">");
		sb.append("</center></body></html>");
		
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setHtml(sb.toString());
		player.sendPacket(html);
	}
	
	public static PlayerEmailManager getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder
	{
		protected static final PlayerEmailManager instance = new PlayerEmailManager();
	}
	
	public void showInbox(Player player) {
	    StringBuilder sb = new StringBuilder();
	    sb.append("<html><body>");
	    sb.append("<table width=300><tr><td align=center><font color=\"LEVEL\">Sua Inbox de Emails</font></td></tr></table><br>");

	    try (Connection con = ConnectionPool.getConnection();
	         PreparedStatement ps = con.prepareStatement(
	             "SELECT DISTINCT email_id, sender_id, expiration_time, is_paid, payment_item_id, payment_item_count FROM player_emails " +
	             "WHERE target_id=? AND status='PENDING' AND expiration_time > ? ORDER BY created_time DESC")) {
	        ps.setInt(1, player.getObjectId());
	        ps.setLong(2, System.currentTimeMillis());
	        try (ResultSet rs = ps.executeQuery()) {
	            if (!rs.next()) {
	                sb.append("Nenhum email pendente.<br>");
	            } else {
	                do {
	                    int emailId = rs.getInt("email_id");
	                    int senderId = rs.getInt("sender_id");
	                    long exp = rs.getLong("expiration_time");
	                    boolean isPaid = rs.getBoolean("is_paid");
	                    int payItem = rs.getInt("payment_item_id");
	                    int payCount = rs.getInt("payment_item_count");

	                    String senderName = PlayerInfoTable.getInstance().getPlayerName(senderId);
	                    long remaining = (exp - System.currentTimeMillis()) / 60000;

	                    sb.append("<table width=300 border=0>");
	                    sb.append("<tr><td>Email ID: ").append(emailId).append(" de ").append(senderName).append("</td></tr>");
	                    sb.append("<tr><td>Expira em: ").append(remaining).append(" minutos</td></tr>");

	                    try (PreparedStatement psItems = con.prepareStatement(
	                            "SELECT item_id, count, enchant_level, is_augmented FROM player_emails WHERE email_id=?")) {
	                        psItems.setInt(1, emailId);
	                        try (ResultSet rsItems = psItems.executeQuery()) {
	                            sb.append("<tr><td>Itens:</td></tr>");
	                            while (rsItems.next()) {
	                                int itemId = rsItems.getInt("item_id");
	                                int count = rsItems.getInt("count");
	                                int enchant = rsItems.getInt("enchant_level");
	                                boolean isAug = rsItems.getBoolean("is_augmented");
	                                String itemName = ItemData.getInstance().getTemplate(itemId).getName();
	                                String displayName = (enchant > 0 ? "+" + enchant + " " : "") + itemName + (isAug ? " (Aug)" : "");
	                                sb.append("<tr><td>- ").append(displayName).append(" x").append(count).append("</td></tr>");
	                            }
	                        }
	                    }

	                    if (isPaid && payItem > 0) {
	                        String payName = ItemData.getInstance().getTemplate(payItem).getName();
	                        sb.append("<tr><td>Pagamento requerido: ").append(payName).append(" x").append(payCount).append("</td></tr>");
	                    }

	                    sb.append("<tr><td><button value=\"Reclamar\" action=\"bypass -h voiced_email claim ").append(emailId).append("\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></td></tr>");
	                    sb.append("</table><br>");
	                } while (rs.next());
	            }
	        }
	    } catch (Exception e) {
	        LOGGER.severe("Erro ao mostrar inbox: " + e.getMessage());
	        e.printStackTrace();
	    }

	    sb.append("<table width=300><tr><td align=center>");
	    sb.append("<button value=\"Voltar\" action=\"bypass -h voiced_email htm email_main.htm\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\">");
	    sb.append("</td></tr></table>");
	    sb.append("</body></html>");

	    NpcHtmlMessage html = new NpcHtmlMessage(0);
	    html.setHtml(sb.toString());
	    player.sendPacket(html);
	}
}
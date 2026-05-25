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
package ext.mods.gameserver.communitybbs.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.commons.lang.StringUtil;
import ext.mods.commons.pool.ConnectionPool;

import ext.mods.gameserver.communitybbs.model.Mail;
import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.data.manager.RelationManager;
import ext.mods.gameserver.data.sql.PlayerInfoTable;
import ext.mods.gameserver.enums.MailType;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ExMailArrived;
import ext.mods.gameserver.network.serverpackets.PlaySound;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public class MailBBSManager extends BaseBBSManager
{
	private static final String SELECT_MAILS = "SELECT * FROM bbs_mail ORDER BY id ASC";
	private static final String INSERT_MAIL = "INSERT INTO bbs_mail (id,receiver_id,sender_id,location,recipients,subject,message,sent_date,is_unread) VALUES (?,?,?,?,?,?,?,?,?)";
	private static final String DELETE_MAIL = "DELETE FROM bbs_mail WHERE id=?";
	private static final String UPDATE_MAIL_AS_READ = "UPDATE bbs_mail SET is_unread=0 WHERE id=?";
	private static final String UPDATE_MAIL_LOCATION = "UPDATE bbs_mail SET location=? WHERE id=?";
	
	private final Map<Integer, Set<Mail>> _mails = new ConcurrentHashMap<>();
	
	private int _lastMailId = 0;
	
	protected MailBBSManager()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_MAILS);
			ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
			{
				final Set<Mail> mails = _mails.computeIfAbsent(rs.getInt("receiver_id"), m -> ConcurrentHashMap.newKeySet());
				mails.add(new Mail(rs));
				
				final int mailId = rs.getInt("id");
				if (mailId > _lastMailId)
					_lastMailId = mailId;
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't load mails.", e);
		}
	}
	
	@Override
	public void parseCmd(String command, Player player)
	{
		if (command.equals("_bbsmail") || command.equals("_maillist_0_1_0_"))
			showMailList(player, 1, MailType.INBOX);
		else if (command.startsWith("_bbsmail"))
		{
			final StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			
			final String action = st.nextToken();
			
			if (action.equals("inbox") || action.equals("sentbox") || action.equals("archive") || action.equals("temparchive"))
			{
				final int page = (st.hasMoreTokens()) ? Integer.parseInt(st.nextToken()) : 1;
				final String sType = (st.hasMoreTokens()) ? st.nextToken() : "";
				final String search = (st.hasMoreTokens()) ? st.nextToken() : "";
				
				showMailList(player, page, Enum.valueOf(MailType.class, action.toUpperCase()), sType, search);
			}
			else if (action.equals("crea"))
				showWriteView(player);
			else
			{
				final Mail mail = getMail(player, (st.hasMoreTokens()) ? Integer.parseInt(st.nextToken()) : -1);
				if (mail == null)
				{
					showLastForum(player);
					return;
				}
				
				if (action.equals("view"))
				{
					showMailView(player, mail);
					if (mail.isUnread())
						setMailAsRead(player, mail.getId());
				}
				else if (action.equals("reply"))
					showWriteView(player, mail);
				else if (action.equals("del"))
				{
					deleteMail(player, mail.getId());
					showLastForum(player);
				}
				else if (action.equals("store"))
				{
					setMailLocation(player, mail.getId(), MailType.ARCHIVE);
					showMailList(player, 1, MailType.ARCHIVE);
				}
			}
		}
		else
			super.parseCmd(command, player);
	}
	
	@Override
	public void parseWrite(String ar1, String ar2, String ar3, String ar4, String ar5, Player player)
	{
		if (ar1.equals("Send"))
		{
			sendMail(ar3, ar4, ar5, player);
			showMailList(player, 1, MailType.SENTBOX);
		}
		else if (ar1.startsWith("Search"))
		{
			final StringTokenizer st = new StringTokenizer(ar1, ";");
			st.nextToken();
			
			showMailList(player, 1, Enum.valueOf(MailType.class, st.nextToken().toUpperCase()), ar4, ar5);
		}
		else
			super.parseWrite(ar1, ar2, ar3, ar4, ar5, player);
	}
	
	private synchronized int getNewMailId()
	{
		return ++_lastMailId;
	}
	
	private Set<Mail> getMails(int objectId)
	{
		return _mails.computeIfAbsent(objectId, m -> ConcurrentHashMap.newKeySet());
	}
	
	private Mail getMail(Player player, int mailId)
	{
		return getMails(player.getObjectId()).stream().filter(l -> l.getId() == mailId).findFirst().orElse(null);
	}
	
	public boolean checkIfUnreadMail(Player player)
	{
		return getMails(player.getObjectId()).stream().anyMatch(Mail::isUnread);
	}
	
	private void showMailList(Player player, int page, MailType type)
	{
		showMailList(player, page, type, "", "");
	}
	
	private void showMailList(Player player, int page, MailType type, String sType, String search)
	{
		Set<Mail> mails;
		if (!sType.equals("") && !search.equals(""))
		{
			mails = ConcurrentHashMap.newKeySet();
			
			boolean byTitle = sType.equalsIgnoreCase("title");
			
			for (Mail mail : getMails(player.getObjectId()))
			{
				if (byTitle && mail.getSubject().toLowerCase().contains(search.toLowerCase()))
					mails.add(mail);
				else if (!byTitle)
				{
					String writer = getPlayerName(mail.getSenderId());
					if (writer.toLowerCase().contains(search.toLowerCase()))
						mails.add(mail);
				}
			}
		}
		else
			mails = getMails(player.getObjectId());
		
		final int countMails = getMailCount(player.getObjectId(), type, sType, search);
		final int maxpage = getPagesCount(countMails);
		
		if (page > maxpage)
			page = maxpage;
		if (page < 1)
			page = 1;
		
		player.setMailPosition(page);
		
		int index = 0;
		int minIndex = 0;
		int maxIndex = 0;
		
		maxIndex = (page == 1 ? page * 9 : (page * 10) - 1);
		minIndex = maxIndex - 9;
		
		String content = HTMLData.getInstance().getHtm(player, CB_PATH + "mail/mail.htm");
		content = content.replace("%inbox%", Integer.toString(getMailCount(player.getObjectId(), MailType.INBOX, "", "")));
		content = content.replace("%sentbox%", Integer.toString(getMailCount(player.getObjectId(), MailType.SENTBOX, "", "")));
		content = content.replace("%archive%", Integer.toString(getMailCount(player.getObjectId(), MailType.ARCHIVE, "", "")));
		content = content.replace("%temparchive%", Integer.toString(getMailCount(player.getObjectId(), MailType.TEMPARCHIVE, "", "")));
		content = content.replace("%type%", type.getDescription());
		content = content.replace("%htype%", type.toString().toLowerCase());
		
		final StringBuilder sb = new StringBuilder();
		for (Mail mail : mails)
		{
			if (mail.getMailType().equals(type))
			{
				if (index < minIndex)
				{
					index++;
					continue;
				}
				
				if (index > maxIndex)
					break;
				
				StringUtil.append(sb, "<table width=610><tr><td width=5></td><td width=150>", getPlayerName(mail.getSenderId()), "</td><td width=300><a action=\"bypass _bbsmail;view;", mail.getId(), "\">");
				
				if (mail.isUnread())
					sb.append("<font color=\"LEVEL\">");
				
				sb.append(StringUtil.trimAndDress(mail.getSubject(), 30));
				
				if (mail.isUnread())
					sb.append("</font>");
				
				StringUtil.append(sb, "</a></td><td width=150>", mail.getFormattedSentDate(), "</td><td width=5></td></tr></table><img src=\"L2UI.Squaregray\" width=610 height=1>");
				index++;
			}
		}
		content = content.replace("%maillist%", sb.toString());
		
		sb.setLength(0);
		
		final String fullSearch = (!sType.equals("") && !search.equals("")) ? ";" + sType + ";" + search : "";
		
		StringUtil.append(sb, "<td><table><tr><td></td></tr><tr><td><button action=\"bypass _bbsmail;", type, ";", (page == 1 ? page : page - 1), fullSearch, "\" back=\"l2ui_ch3.prev1_down\" fore=\"l2ui_ch3.prev1\" width=16 height=16></td></tr></table></td>");
		
		int i = 0;
		if (maxpage > 21)
		{
			if (page <= 11)
			{
				for (i = 1; i <= (10 + page); i++)
				{
					if (i == page)
						StringUtil.append(sb, "<td> ", i, " </td>");
					else
						StringUtil.append(sb, "<td><a action=\"bypass _bbsmail;", type, ";", i, fullSearch, "\"> ", i, " </a></td>");
				}
			}
			else if (page > 11 && (maxpage - page) > 10)
			{
				for (i = (page - 10); i <= (page - 1); i++)
				{
					if (i == page)
						continue;
					
					StringUtil.append(sb, "<td><a action=\"bypass _bbsmail;", type, ";", i, fullSearch, "\"> ", i, " </a></td>");
				}
				for (i = page; i <= (page + 10); i++)
				{
					if (i == page)
						StringUtil.append(sb, "<td> ", i, " </td>");
					else
						StringUtil.append(sb, "<td><a action=\"bypass _bbsmail;", type, ";", i, fullSearch, "\"> ", i, " </a></td>");
				}
			}
			else if ((maxpage - page) <= 10)
			{
				for (i = (page - 10); i <= maxpage; i++)
				{
					if (i == page)
						StringUtil.append(sb, "<td> ", i, " </td>");
					else
						StringUtil.append(sb, "<td><a action=\"bypass _bbsmail;", type, ";", i, fullSearch, "\"> ", i, " </a></td>");
				}
			}
		}
		else
		{
			for (i = 1; i <= maxpage; i++)
			{
				if (i == page)
					StringUtil.append(sb, "<td> ", i, " </td>");
				else
					StringUtil.append(sb, "<td><a action=\"bypass _bbsmail;", type, ";", i, fullSearch, "\"> ", i, " </a></td>");
			}
		}
		StringUtil.append(sb, "<td><table><tr><td></td></tr><tr><td><button action=\"bypass _bbsmail;", type, ";", (page == maxpage ? page : page + 1), fullSearch, "\" back=\"l2ui_ch3.next1_down\" fore=\"l2ui_ch3.next1\" width=16 height=16 ></td></tr></table></td>");
		
		content = content.replace("%maillistlength%", sb.toString());
		
		separateAndSend(content, player);
	}
	
	private void showMailView(Player player, Mail mail)
	{
		if (mail == null)
		{
			showMailList(player, 1, MailType.INBOX);
			return;
		}
		
		String content = HTMLData.getInstance().getHtm(player, CB_PATH + "mail/mail-show.htm");
		
		String link = mail.getMailType().getBypass() + "&nbsp;&gt;&nbsp;" + mail.getSubject();
		content = content.replace("%maillink%", link);
		
		content = content.replace("%writer%", getPlayerName(mail.getSenderId()));
		content = content.replace("%sentDate%", mail.getFormattedSentDate());
		content = content.replace("%receiver%", mail.getRecipients());
		content = content.replace("%delDate%", "Unknown");
		content = content.replace("%title%", mail.getSubject().replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;"));
		content = content.replace("%mes%", mail.getMessage().replace("\r\n", "<br>").replace("<br1>", "|||BR1|||").replace("<br>", "|||BR|||").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("|||BR|||", "<br>").replace("|||BR1|||", "<br1>"));
		content = content.replace("%mailId%", mail.getId() + "");
		
		separateAndSend(content, player);
	}
	
	private static void showWriteView(Player player)
	{
		String content = HTMLData.getInstance().getHtm(player, CB_PATH + "mail/mail-write.htm");
		separateAndSend(content, player);
	}
	
	private static void showWriteView(Player player, Mail mail)
	{
		String content = HTMLData.getInstance().getHtm(player, CB_PATH + "mail/mail-reply.htm");
		
		String link = mail.getMailType().getBypass() + "&nbsp;&gt;&nbsp;<a action=\"bypass _bbsmail;view;" + mail.getId() + "\">" + mail.getSubject() + "</a>&nbsp;&gt;&nbsp;";
		content = content.replace("%maillink%", link);
		
		content = content.replace("%recipients%", mail.getSenderId() == player.getObjectId() ? mail.getRecipients() : getPlayerName(mail.getSenderId()));
		content = content.replace("%mailId%", mail.getId() + "");
		send1001(content, player);
		send1002(player, " ", "Re: " + mail.getSubject(), "0");
	}
	
	public void sendMail(String recipients, String subject, String message, Player player)
	{
		final long currentDate = Calendar.getInstance().getTimeInMillis();
		
		final Timestamp ts = new Timestamp(currentDate - 86400000L);
		
		if (getMails(player.getObjectId()).stream().filter(l -> l.getSentDate().after(ts) && l.getMailType() == MailType.SENTBOX).count() >= 10)
		{
			player.sendPacket(SystemMessageId.NO_MORE_MESSAGES_TODAY);
			return;
		}
		
		final String[] recipientNames = recipients.trim().split(";");
		if (recipientNames.length > 5 && !player.isGM())
		{
			player.sendPacket(SystemMessageId.ONLY_FIVE_RECIPIENTS);
			return;
		}
		
		subject = StringUtil.trim(subject, 128, "(no subject)");
		
		message = message.replaceAll("\n", "<br1>");
		
		final Timestamp time = new Timestamp(currentDate);
		final String formattedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(time);
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(INSERT_MAIL))
		{
			ps.setInt(3, player.getObjectId());
			ps.setString(4, "inbox");
			ps.setString(5, recipients);
			ps.setString(6, subject);
			ps.setString(7, message);
			ps.setTimestamp(8, time);
			ps.setInt(9, 1);
			
			for (String recipientName : recipientNames)
			{
				final int recipientId = PlayerInfoTable.getInstance().getPlayerObjectId(recipientName);
				if (recipientId <= 0 || recipientId == player.getObjectId())
				{
					player.sendPacket(SystemMessageId.INVALID_TARGET);
					continue;
				}
				
				final Player recipientPlayer = World.getInstance().getPlayer(recipientId);
				
				if (!player.isGM())
				{
					if (PlayerInfoTable.getInstance().getPlayerAccessLevel(recipientId) > 0)
					{
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANNOT_MAIL_GM_S1).addString(recipientName));
						continue;
					}
					
					if (recipientPlayer != null)
					{
						if (recipientPlayer.isBlockingAll())
						{
							player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_BLOCKED_EVERYTHING).addString(recipientName));
							continue;
						}
						
						if (RelationManager.getInstance().isInBlockList(recipientPlayer, player))
						{
							player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_BLOCKED_YOU_CANNOT_MAIL).addString(recipientName));
							continue;
						}
					}
					
					if (isInboxFull(recipientId))
					{
						player.sendPacket(SystemMessageId.MESSAGE_NOT_SENT);
						if (recipientPlayer != null)
							recipientPlayer.sendPacket(SystemMessageId.MAILBOX_FULL);
						
						continue;
					}
				}
				
				final int id = getNewMailId();
				
				ps.setInt(1, id);
				ps.setInt(2, recipientId);
				ps.addBatch();
				
				getMails(recipientId).add(new Mail(id, recipientId, player.getObjectId(), MailType.INBOX, recipients, subject, message, time, formattedTime, true));
				
				if (recipientPlayer != null)
				{
					recipientPlayer.sendPacket(SystemMessageId.NEW_MAIL);
					recipientPlayer.sendPacket(new PlaySound("systemmsg_e.1233"));
					recipientPlayer.sendPacket(ExMailArrived.STATIC_PACKET);
				}
			}
			
			final int[] result = ps.executeBatch();
			if (result.length > 0)
			{
				final int id = getNewMailId();
				
				ps.setInt(1, id);
				ps.setInt(2, player.getObjectId());
				ps.setInt(3, player.getObjectId());
				ps.setString(4, "sentbox");
				ps.setString(5, recipients);
				ps.setString(6, subject);
				ps.setString(7, message);
				ps.setTimestamp(8, time);
				ps.setInt(9, 0);
				ps.execute();
				
				getMails(player.getObjectId()).add(new Mail(id, player.getObjectId(), player.getObjectId(), MailType.SENTBOX, recipients, subject, message, time, formattedTime, false));
				
				player.sendPacket(SystemMessageId.SENT_MAIL);
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't send mail for {}.", e, player.getName());
		}
	}
	
	private int getMailCount(int objectId, MailType location, String type, String search)
	{
		int count = 0;
		if (!type.equals("") && !search.equals(""))
		{
			boolean byTitle = type.equalsIgnoreCase("title");
			for (Mail mail : getMails(objectId))
			{
				if (!mail.getMailType().equals(location))
					continue;
				
				if (byTitle && mail.getSubject().toLowerCase().contains(search.toLowerCase()))
					count++;
				else if (!byTitle)
				{
					String writer = getPlayerName(mail.getSenderId());
					if (writer.toLowerCase().contains(search.toLowerCase()))
						count++;
				}
			}
		}
		else
		{
			for (Mail mail : getMails(objectId))
			{
				if (mail.getMailType().equals(location))
					count++;
			}
		}
		return count;
	}
	
	private void deleteMail(Player player, int mailId)
	{
		getMails(player.getObjectId()).removeIf(m -> m.getId() == mailId);
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_MAIL))
		{
			ps.setInt(1, mailId);
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't delete mail #{}.", e, mailId);
		}
	}
	
	private void setMailAsRead(Player player, int mailId)
	{
		final Mail mail = getMail(player, mailId);
		if (mail != null)
			mail.setAsRead();
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_MAIL_AS_READ))
		{
			ps.setInt(1, mailId);
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't set read status for mail #{}.", e, mailId);
		}
	}
	
	private void setMailLocation(Player player, int mailId, MailType location)
	{
		final Mail mail = getMail(player, mailId);
		if (mail != null)
			mail.setMailType(location);
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_MAIL_LOCATION))
		{
			ps.setString(1, location.toString().toLowerCase());
			ps.setInt(2, mailId);
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't set mail #{} location.", e, mailId);
		}
	}
	
	private boolean isInboxFull(int objectId)
	{
		return getMailCount(objectId, MailType.INBOX, "", "") >= 100;
	}
	
	private void showLastForum(Player player)
	{
		final int page = player.getMailPosition() % 1000;
		final int type = player.getMailPosition() / 1000;
		
		showMailList(player, page, MailType.VALUES[type]);
	}
	
	private static String getPlayerName(int objectId)
	{
		final String name = PlayerInfoTable.getInstance().getPlayerName(objectId);
		return (name == null) ? "Unknown" : name;
	}
	
	private static int getPagesCount(int mailCount)
	{
		if (mailCount < 1)
			return 1;
		
		if (mailCount % 10 == 0)
			return mailCount / 10;
		
		return (mailCount / 10) + 1;
	}
	
	public static MailBBSManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final MailBBSManager INSTANCE = new MailBBSManager();
	}
}
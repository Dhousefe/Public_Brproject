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
package ext.mods.gameserver.communitybbs.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import ext.mods.gameserver.enums.MailType;

public class Mail
{
	private final int _id;
	private final int _receiverId;
	private final int _senderId;
	
	private final String _recipients;
	private final String _subject;
	private final String _message;
	
	private final Timestamp _sentDate;
	
	private final String _formattedSentDate;
	
	private MailType _mailType;
	
	private boolean _isUnread;
	
	public Mail(ResultSet rs) throws SQLException
	{
		_id = rs.getInt("id");
		_receiverId = rs.getInt("receiver_id");
		_senderId = rs.getInt("sender_id");
		_mailType = Enum.valueOf(MailType.class, rs.getString("location").toUpperCase());
		_recipients = rs.getString("recipients");
		_subject = rs.getString("subject");
		_message = rs.getString("message");
		_sentDate = rs.getTimestamp("sent_date");
		_formattedSentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(_sentDate);
		_isUnread = rs.getInt("is_unread") != 0;
	}
	
	public Mail(int id, int receiverId, int senderId, MailType location, String recipients, String subject, String message, Timestamp sentDate, String formattedSentDate, boolean isUnread)
	{
		_id = id;
		_receiverId = receiverId;
		_senderId = senderId;
		_mailType = location;
		_recipients = recipients;
		_subject = subject;
		_message = message;
		_sentDate = sentDate;
		_formattedSentDate = formattedSentDate;
		_isUnread = isUnread;
	}
	
	public int getId()
	{
		return _id;
	}
	
	public int getReceiverId()
	{
		return _receiverId;
	}
	
	public int getSenderId()
	{
		return _senderId;
	}
	
	public MailType getMailType()
	{
		return _mailType;
	}
	
	public void setMailType(MailType mailType)
	{
		_mailType = mailType;
	}
	
	public String getRecipients()
	{
		return _recipients;
	}
	
	public String getSubject()
	{
		return _subject;
	}
	
	public String getMessage()
	{
		return _message;
	}
	
	public Timestamp getSentDate()
	{
		return _sentDate;
	}
	
	public String getFormattedSentDate()
	{
		return _formattedSentDate;
	}
	
	public boolean isUnread()
	{
		return _isUnread;
	}
	
	public void setAsRead()
	{
		_isUnread = false;
	}
}
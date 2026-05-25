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
package ext.mods.email.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import ext.mods.email.sql.EmailDAO;
import ext.mods.commons.pool.ConnectionPool;
import ext.mods.commons.pool.ThreadPool;
import ext.mods.gameserver.model.actor.Player;

public class EmailDeliveryTask
{
	private final Map<Integer, ScheduledFuture<?>> activeTasks = new ConcurrentHashMap<>();
	
	public void loadAllPending()
	{
		try (Connection con = ConnectionPool.getConnection();
		     PreparedStatement ps = con.prepareStatement("SELECT email_id, expiration_time FROM player_emails WHERE status='PENDING'"))
		{
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					int emailId = rs.getInt("email_id");
					long expTime = rs.getLong("expiration_time");
					scheduleExpiration(null, emailId, expTime);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	
	public void scheduleExpiration(Player sender, int emailId, long expirationTime)
	{
		long currentTime = System.currentTimeMillis();
		long delay = expirationTime - currentTime;
		
		if (delay <= 0)
		{
			expireEmail(emailId);
			return;
		}
		
		ScheduledFuture<?> task = ThreadPool.schedule(() ->
		{
			expireEmail(emailId);
			activeTasks.remove(emailId);
		}, delay);
		
		long minutes = delay / 60000;
		long seconds = (delay % 60000) / 1000;
		
		if(sender != null) {
			if (minutes > 0) {
				sender.sendMessage("Tarefa agendada: o e-mail será expirado em " + minutes + " minuto(s) e " + seconds + " segundo(s).");
			} else {
				sender.sendMessage("Tarefa agendada: o e-mail será expirado em " + seconds + " segundo(s).");
			}
		}
		
		activeTasks.put(emailId, task);
	}
	
	public void cancel(int emailId)
	{
		ScheduledFuture<?> task = activeTasks.remove(emailId);
		if (task != null)
			task.cancel(false);
	}
	
	private void expireEmail(int emailId) {
	    if (!EmailDAO.isPending(emailId)) return;
	    EmailDAO.expireAndReturnToSender(emailId);
	}
	
	public static EmailDeliveryTask getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		private static final EmailDeliveryTask _instance = new EmailDeliveryTask();
	}
	
}

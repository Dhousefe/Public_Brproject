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
package ext.mods.gameserver.model.entity.events.capturetheflag;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ScheduledFuture;

import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ThreadPool;

import ext.mods.Config;
import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.enums.actors.ClassId;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.olympiad.OlympiadManager;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ConfirmDlg;
import ext.mods.gameserver.network.serverpackets.TutorialShowQuestionMark;

public class CTFManager
{
	private static final CLogger LOGGER = new CLogger(CTFManager.class.getName());
	
	private CTFStartTask _task;
	private java.util.concurrent.ScheduledFuture<?> _questionMarkRefresher;
	
	public static final int JOIN_CTF_REQ_ID = 100_001;
	/** ID do ícone de tutorial (quest mark) para o evento CTF. */
	public static final int TUTORIAL_QUESTION_MARK_ID = 2001;
	
	public Calendar _nextEvent;
	
	protected CTFManager()
	{
		if (Config.CTF_EVENT_ENABLED)
		{
			if (Config.CTF_EVENT_TEAM_1_NAME != Config.CTF_EVENT_TEAM_2_NAME)
			{
				CTFEvent.getInstance().init();
				scheduleEventStart();
				LOGGER.info("Capture The Flag Engine: is Started.");
			}
			else
				LOGGER.info("Capture The Flag Engine: is uninitiated. Cannot start if both teams have same name!");
		}
		else
			LOGGER.info("Capture The Flag Engine: is disabled.");
	}
	
	public void scheduleEventStart()
	{
		try
		{
			Calendar currentTime = Calendar.getInstance();
			Calendar nextStartTime = null;
			Calendar testStartTime = null;
			
			for (String timeOfDay : Config.CTF_EVENT_INTERVAL)
			{
				testStartTime = Calendar.getInstance();
				testStartTime.setLenient(true);
				
				String[] splitTimeOfDay = timeOfDay.split(":");
				testStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTimeOfDay[0]));
				testStartTime.set(Calendar.MINUTE, Integer.parseInt(splitTimeOfDay[1]));
				testStartTime.set(Calendar.SECOND, Integer.parseInt("00"));
				
				if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis())
					testStartTime.add(Calendar.DAY_OF_MONTH, 1);
				
				if ((nextStartTime == null) || (testStartTime.getTimeInMillis() < nextStartTime.getTimeInMillis()))
					nextStartTime = testStartTime;
			}
			
			_nextEvent = nextStartTime;
			
			if (nextStartTime != null)
			{
				_task = new CTFStartTask(nextStartTime.getTimeInMillis());
				ThreadPool.execute(_task);
			}
		}
		catch (Exception e)
		{
			LOGGER.warn("CTFManager.scheduleEventStart(): Error figuring out a start time. Check CTFEventInterval in config file.");
		}
	}
	
	public void startReg()
	{
		if (!CTFEvent.getInstance().startParticipation())
		{
			World.announceToOnlinePlayers("CTF Event: Event was cancelled.");
			scheduleEventStart();
		}
		else
		{
			World.announceToOnlinePlayers("CTF Event: Joinable in " + Config.CTF_NPC_LOC_NAME + "!");
			
			if (Config.EVENT_COMMANDS)
				World.announceToOnlinePlayers("CTF Event: Command: .ctfjoin / .ctfleave / .ctfinfo");
			
			if (Config.ALLOW_CTF_DLG)
			{
				for (Player players : World.getInstance().getPlayers())
				{
					if (OlympiadManager.getInstance().isRegistered(players) || players.isAlikeDead() || players.isTeleporting() || players.isDead() || players.isInObserverMode() || players.isInStoreMode() || CTFEvent.getInstance().isStarted() && CTFEvent.getInstance().isPlayerParticipant(players.getObjectId()) || players.isInsideZone(ZoneId.CASTLE) || players.isInsideZone(ZoneId.SIEGE) || players.getClassId() == ClassId.BISHOP || players.getClassId() == ClassId.CARDINAL || players.getClassId() == ClassId.SHILLIEN_ELDER || players.getClassId() == ClassId.SHILLIEN_SAINT || players.getClassId() == ClassId.ELVEN_ELDER || players.getClassId() == ClassId.EVAS_SAINT || players.getClassId() == ClassId.PROPHET || players.getClassId() == ClassId.HIEROPHANT)
						continue;
					
					ConfirmDlg confirm = new ConfirmDlg(SystemMessageId.S1);
					confirm.addString("Do you wish to Join CTF Event?");
					confirm.addTime(45000);
					confirm.addRequesterId(JOIN_CTF_REQ_ID);
					players.sendPacket(confirm);
					players = null;
				}
			}
			sendQuestionMarkToAllOnline();
			_questionMarkRefresher = ThreadPool.scheduleAtFixedRate(CTFManager::sendQuestionMarkToAllOnline, 60_000, 60_000);
			_task.setStartTime(System.currentTimeMillis() + (60000L * Config.CTF_EVENT_PARTICIPATION_TIME));
			ThreadPool.execute(_task);
		}
	}
	
	public void startEvent()
	{
		if (!CTFEvent.getInstance().startFight())
		{
			World.announceToOnlinePlayers("CTF Event: Event cancelled due to lack of Participation.");
			scheduleEventStart();
		}
		else
		{
			CTFEvent.getInstance().sysMsgToAllParticipants("Teleporting in " + Config.CTF_EVENT_START_LEAVE_TELEPORT_DELAY + " second(s).");
			_task.setStartTime(System.currentTimeMillis() + (60000L * Config.CTF_EVENT_RUNNING_TIME));
			ThreadPool.execute(_task);
		}
	}
	
	public void endEvent()
	{
		if (_questionMarkRefresher != null)
		{
			_questionMarkRefresher.cancel(false);
			_questionMarkRefresher = null;
		}
		World.announceToOnlinePlayers(CTFEvent.getInstance().calculateRewards());
		CTFEvent.getInstance().sysMsgToAllParticipants("Teleporting back town in " + Config.CTF_EVENT_START_LEAVE_TELEPORT_DELAY + " second(s).");
		CTFEvent.getInstance().stopFight();
		
		scheduleEventStart();
	}
	
	public void skipDelay()
	{
		if (_task == null)
			return;
		
		if (_task.nextRun.cancel(false))
		{
			_task.setStartTime(System.currentTimeMillis());
			ThreadPool.execute(_task);
		}
	}
	
	class CTFStartTask implements Runnable
	{
		private long _startTime;
		public ScheduledFuture<?> nextRun;
		
		public CTFStartTask(long startTime)
		{
			_startTime = startTime;
		}
		
		public void setStartTime(long startTime)
		{
			_startTime = startTime;
		}
		
		@Override
		public void run()
		{
			int delay = (int) Math.round((_startTime - System.currentTimeMillis()) / 1000.0);
			
			if (delay > 0)
				announce(delay);
			
			int nextMsg = 0;
			if (delay > 3600)
				nextMsg = delay - 3600;
			else if (delay > 1800)
				nextMsg = delay - 1800;
			else if (delay > 900)
				nextMsg = delay - 900;
			else if (delay > 600)
				nextMsg = delay - 600;
			else if (delay > 300)
				nextMsg = delay - 300;
			else if (delay > 60)
				nextMsg = delay - 60;
			else if (delay > 5)
				nextMsg = delay - 5;
			else if (delay > 0)
				nextMsg = delay;
			else
			{
				if (CTFEvent.getInstance().isInactive())
					startReg();
				else if (CTFEvent.getInstance().isParticipating())
					startEvent();
				else
					endEvent();
			}
			
			if (delay > 0)
				nextRun = ThreadPool.schedule(this, nextMsg * 1000);
		}
		
		private void announce(long time)
		{
			if ((time >= 3600) && ((time % 3600) == 0))
			{
				if (CTFEvent.getInstance().isParticipating())
					World.announceToOnlinePlayers("CTF Event: " + (time / 60 / 60) + " hour(s) until registration is closed!");
				else if (CTFEvent.getInstance().isStarted())
					CTFEvent.getInstance().sysMsgToAllParticipants((time / 60 / 60) + " hour(s) until event is finished!");
			}
			else if (time >= 60)
			{
				if (CTFEvent.getInstance().isParticipating())
					World.announceToOnlinePlayers("CTF Event: " + (time / 60) + " minute(s) until registration is closed!");
				else if (CTFEvent.getInstance().isStarted())
					CTFEvent.getInstance().sysMsgToAllParticipants((time / 60) + " minute(s) until the event is finished!");
			}
			else
			{
				if (CTFEvent.getInstance().isParticipating())
					World.announceToOnlinePlayers("CTF Event: " + time + " second(s) until registration is closed!");
				else if (CTFEvent.getInstance().isStarted())
					CTFEvent.getInstance().sysMsgToAllParticipants(time + " second(s) until the event is finished!");
			}
		}
	}
	
	public String getNextTime()
	{
		Calendar currentTime = Calendar.getInstance();
		for (String timeOfDay : Config.CTF_EVENT_INTERVAL)
		{
			String[] splitTimeOfDay = timeOfDay.split(":");
			int eventHour = Integer.parseInt(splitTimeOfDay[0]);
			int eventMinute = Integer.parseInt(splitTimeOfDay[1]);
			
			Calendar eventTime = Calendar.getInstance();
			eventTime.setLenient(true);
			eventTime.set(Calendar.HOUR_OF_DAY, eventHour);
			eventTime.set(Calendar.MINUTE, eventMinute);
			eventTime.set(Calendar.SECOND, 0);
			
			if (eventTime.getTimeInMillis() > currentTime.getTimeInMillis())
			{
				SimpleDateFormat format = new SimpleDateFormat("HH:mm");
				return format.format(eventTime.getTime());
			}
		}
		
		return "00:00";
	}
	
	private static void sendQuestionMarkToAllOnline()
	{
		if (!CTFEvent.getInstance().isParticipating() && !CTFEvent.getInstance().isStarted())
			return;
		for (Player online : World.getInstance().getPlayers())
		{
			if (online != null && online.isOnline())
				online.sendPacket(new TutorialShowQuestionMark(TUTORIAL_QUESTION_MARK_ID));
		}
	}
	
	public String getTutorialAlertHtml(Player player)
	{
		if (player == null || (!CTFEvent.getInstance().isParticipating() && !CTFEvent.getInstance().isStarted()))
			return null;
		String html = HTMLData.getInstance().getHtm(player.getLocale(), "html/mods/tournament/tutorial_alert_ctf.htm");
		if (html == null || html.isEmpty())
			return null;
		String message = CTFEvent.getInstance().isParticipating()
			? ("Registration is open! Join in " + Config.CTF_NPC_LOC_NAME + ". Commands: .ctfjoin / .ctfleave / .ctfinfo")
			: "CTF event is in progress. Use .ctfinfo for status.";
		String buttonLabel = CTFEvent.getInstance().isParticipating() ? "Join CTF" : "Info";
		String buttonBypass = CTFEvent.getInstance().isParticipating() ? "ctfjoin" : "ctfinfo";
		return html.replace("%message%", message).replace("%button_label%", buttonLabel).replace("%button_bypass%", buttonBypass);
	}
	
	public static final CTFManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final CTFManager INSTANCE = new CTFManager();
	}
}
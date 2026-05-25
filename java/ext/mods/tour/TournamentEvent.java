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
package ext.mods.tour;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.TutorialShowQuestionMark;

import ext.mods.tour.battle.TournamentManager;
import ext.mods.tour.holder.TourConfig;
import ext.mods.tour.holder.TourHolder;
import ext.mods.tour.ranking.TournamentRankingManager;

public class TournamentEvent
{
	public static Logger LOGGER = Logger.getLogger(TournamentEvent.class.getName());
	private static ScheduledFuture<?> eventChecker;
	private static ScheduledFuture<?> fightRepeater;
	
	private static ScheduledFuture<?> questionMarkRefresher;
	private static boolean isRunning;
	private static boolean isRunningRegister;
	private static String lastEventTime;
	
	public static void start()
	{
		if (eventChecker == null || eventChecker.isCancelled())
			eventChecker = ThreadPool.scheduleAtFixedRate(() -> checkAndStartEvent(), 500, 1000);
	}
	
	private static void checkAndStartEvent()
	{
		LocalDateTime now = LocalDateTime.now();
		int currentDay = now.getDayOfWeek().getValue() % 7;
		
		TourConfig config = TourData.getInstance().getConfig();
		
		if (!config.isEnabled() || !config.getDays().contains(currentDay))
			return;
		
		String nowStr = new SimpleDateFormat("HH:mm").format(new Date());
		
		for (String time : config.getTimes())
		{
			if (nowStr.equals(time) && !isRunning && !nowStr.equals(lastEventTime))
			{
				isRunning = true;
				lastEventTime = nowStr;
				World.announceToOnlinePlayers("[TOURNAMENT] Registration is now OPEN! The battles will start in " + config.getPreparation() + " minutes.", true);
				sendQuestionMarkToAllOnline();
				questionMarkRefresher = ThreadPool.scheduleAtFixedRate(TournamentEvent::sendQuestionMarkToAllOnline, 60_000, 60_000);
				ThreadPool.schedule(() -> startFight(), 1000 * 60 * config.getPreparation());
				
				break;
			}
		}
	}
	
	private static void startFight()
	{
		TourConfig config = TourData.getInstance().getConfig();
		Math();
		isRunningRegister = true;
		World.announceToOnlinePlayers("[TOURNAMENT] The battles have started! The event will end in " + config.getDuration() + " minutes.", true);
		ThreadPool.schedule(() -> endEvent(), 1000 * 60 * config.getDuration());
	}
	
	private static void Math()
	{
		if (fightRepeater == null || fightRepeater.isCancelled())
			fightRepeater = ThreadPool.scheduleAtFixedRate(() -> checkFights(), 500, 1000);
		
	}

	/** Envia o ícone de tutorial (quest mark) do Tournament a todos os jogadores online. Só envia se o evento estiver ativo. */
	private static void sendQuestionMarkToAllOnline()
	{
		if (!isRunning)
			return;
		for (Player online : World.getInstance().getPlayers())
		{
			if (online != null && online.isOnline())
				online.sendPacket(new TutorialShowQuestionMark(TUTORIAL_QUESTION_MARK_ID));
		}
	}
	
	private static void checkFights()
	{
		List<TourHolder> battles = TourData.getInstance().getBattleName("battle");
		
		for (TourHolder holder : battles)
		{
			int playersCount = holder.getCount();
			List<Player> selectedPlayers = TournamentManager.getInstance().selectPlayers(playersCount);
			
			if (selectedPlayers.size() < playersCount)
			{
				continue;
			}
			TournamentManager.getInstance().startBattle(selectedPlayers, holder);
		}
	}
	
	private static void endEvent()
	{
		isRunningRegister = false;
		if (questionMarkRefresher != null)
		{
			questionMarkRefresher.cancel(false);
			questionMarkRefresher = null;
		}
		if (fightRepeater != null)
		{
			fightRepeater.cancel(false);
			fightRepeater = null;
		}
		TournamentManager.getInstance().endAllBattles();
		
		TourConfig config = TourData.getInstance().getConfig();

		World.announceToOnlinePlayers("[TOURNAMENT] The event has ended! The next tournament will begin at " + String.join(", ", config.getTimes()) + ".", true);
		
		isRunning = false;
		lastEventTime = "";
		TournamentRankingManager.getInstance().rewardTopPlayers();
		TournamentRankingManager.getInstance().clearRankings();
	}
	
	
	public static boolean isRunning()
	{
		return isRunning;
	}
	
	public static boolean isRunningRegister()
	{
		return isRunningRegister;
	}
	
	public static String lastEvent()
	{
		return lastEventTime;
	}

	/** ID do ícone de tutorial (question mark) para registro no Tournament. */
	public static final int TUTORIAL_QUESTION_MARK_ID = 2000;

	/**
	 * Retorna o HTML do alerta de tutorial do Tournament preenchido para o jogador, ou null se o evento não estiver ativo ou o HTML não existir.
	 */
	public static String getTutorialAlertHtml(Player player)
	{
		if (!isRunning() || player == null)
			return null;
		String html = HTMLData.getInstance().getHtm(player.getLocale(), "html/mods/tournament/tutorial_alert.htm");
		if (html == null || html.isEmpty())
			return null;
		String lastStart = lastEvent();
		LocalTime startTime = lastStart != null && !lastStart.isEmpty() ? LocalTime.parse(lastStart, DateTimeFormatter.ofPattern("HH:mm")) : null;
		LocalTime now = LocalTime.now();
		int durationMinutes = TourData.getInstance().getConfig().getDuration();
		LocalTime endTime = startTime != null ? startTime.plusMinutes(durationMinutes) : null;
		Duration remaining = (startTime != null && endTime != null) ? Duration.between(now, endTime) : null;
		long minutes = (remaining != null && !remaining.isNegative()) ? remaining.toMinutes() : 0;
		boolean battlesStarted = isRunningRegister();
		String msg = battlesStarted
			? ("Tournament battles are in progress! Remaining time: " + minutes + " minute(s). Check the ranking.")
			: ("Registration is OPEN! The battles will start in " + TourData.getInstance().getConfig().getPreparation() + " minutes. Register now.");
		String btnLabel = battlesStarted ? "See Ranking" : "Register for Tournament";
		String btnBypass = battlesStarted ? "voiced_tournamentrank" : "voiced_tour register";
		return html.replace("%message%", msg).replace("%button_label%", btnLabel).replace("%button_bypass%", btnBypass);
	}
	
	public static void reset()
	{
		if (questionMarkRefresher != null)
		{
			questionMarkRefresher.cancel(false);
			questionMarkRefresher = null;
		}
		if (eventChecker != null)
		{
			eventChecker.cancel(false);
			eventChecker = null;
		}
	}
	
}
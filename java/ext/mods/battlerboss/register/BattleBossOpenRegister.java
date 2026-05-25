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
package ext.mods.battlerboss.register;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

import ext.mods.battlerboss.holder.EventHolder;
import ext.mods.battlerboss.matches.BattleBossRumble;
import ext.mods.commons.pool.ThreadPool;
import ext.mods.gameserver.idfactory.IdFactory;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.olympiad.OlympiadManager;
import ext.mods.gameserver.network.serverpackets.TutorialShowQuestionMark;

public class BattleBossOpenRegister
{
	/** ID do quest mark para Battle Boss (deve ser igual a BattleBossData.TUTORIAL_QUESTION_MARK_ID). */
	private static final int TUTORIAL_QUESTION_MARK_ID = 2006;
	private final Map<EventHolder, EventRegistration> eventRegistrations = new ConcurrentHashMap<>();
	private final Map<Integer, BattleBossRumble> _activeRumbles = new ConcurrentHashMap<>();
	private static volatile ScheduledFuture<?> questionMarkRefresher;
	
	private static class EventRegistration
	{
		final List<Player> registeredPlayers = new CopyOnWriteArrayList<>();
		volatile boolean registrationOpen = false;
		ScheduledFuture<?> matchChecker;
	}
	
	public boolean isRegistrationOpen(EventHolder event)
	{
		EventRegistration reg = eventRegistrations.get(event);
		return reg != null && reg.registrationOpen;
	}
	
	public boolean hasOpenRegistration()
	{
		for (EventRegistration reg : eventRegistrations.values())
		{
			if (reg != null && reg.registrationOpen)
				return true;
		}
		return false;
	}
	
	public void open(EventHolder event)
	{
		EventRegistration reg = new EventRegistration();
		reg.registrationOpen = true;
		eventRegistrations.put(event, reg);
		
		reg.matchChecker = ThreadPool.scheduleAtFixedRate(() -> checkMatches(event), 1000L * event.getRegistration().getWaitingTimeMatches(), 1000L * event.getRegistration().getWaitingTimeMatches());
		
		World.announceToOnlinePlayers("[BattleBoss] Evento Aberto: " + event.getInfo().getName() + " - use .battleboss para participar!", true);
		sendQuestionMarkToAllOnline();
		if (questionMarkRefresher == null || questionMarkRefresher.isCancelled())
			questionMarkRefresher = ThreadPool.scheduleAtFixedRate(BattleBossOpenRegister::sendQuestionMarkToAllOnline, 60_000, 60_000);
		for (String line : event.getInfo().getAnnounce())
		{
			if (line != null && !line.trim().isEmpty())
				World.announceToOnlinePlayers(line, true);
		}
		if (event.getConfig().getDuration() != 0)
		{
			long durationMs = 1000L * 60 * event.getConfig().getDuration() + 1;
			ThreadPool.schedule(() -> close(event), durationMs);
			
			scheduleReminders(event, durationMs);
		}
	}
	
	public void close(EventHolder event)
	{
		EventRegistration reg = eventRegistrations.get(event);
		if (reg != null)
		{
			reg.registrationOpen = false;
			if (reg.matchChecker != null)
			{
				reg.matchChecker.cancel(false);
			}
		}
		if (!hasOpenRegistration() && questionMarkRefresher != null)
		{
			questionMarkRefresher.cancel(false);
			questionMarkRefresher = null;
		}
	}
	
	private static void sendQuestionMarkToAllOnline()
	{
		BattleBossOpenRegister reg = getInstance();
		if (reg == null || !reg.hasOpenRegistration()) return;
		for (Player online : World.getInstance().getPlayers())
		{
			if (online != null && online.isOnline())
				online.sendPacket(new TutorialShowQuestionMark(TUTORIAL_QUESTION_MARK_ID));
		}
	}
	
	public boolean register(EventHolder event, Player player)
	{
		EventRegistration reg = eventRegistrations.get(event);
		if (reg == null || !reg.registrationOpen)
		{
			player.sendMessage("Registro fechado para o evento " + event.getName());
			return false;
		}
		
		if (player.isInBattleBossRumble())
		{
			player.sendMessage("Você já está em um match deste ou de outro evento.");
			return false;
		}
		
		if (player.getClassId().getLevel() != 3)
		{
			player.sendMessage("Somente personagens com terceira profissão podem se registrar neste evento.");
			return false;
		}
		
		if (OlympiadManager.getInstance().isRegistered(player))
		{
			player.sendMessage("Você não pode se registrar enquanto estiver na Olympiad.");
			return false;
		}
		
		if (player.isCursedWeaponEquipped())
		{
			player.sendMessage("Você não pode se registrar com uma Cursed Weapon equipada.");
			return false;
		}
		
		if (player.getKarma() > 0)
		{
			player.sendMessage("Jogadores com Karma positivo não podem se registrar em eventos.");
			return false;
		}
		
		if (reg.registeredPlayers.size() >= event.getRegistration().getMaxPlayers())
		{
			player.sendMessage("Registro cheio. Você não pode se registrar.");
			return false;
		}
		
		if (!reg.registeredPlayers.contains(player))
		{
			reg.registeredPlayers.add(player);
			player.sendMessage("Você foi registrado no evento " + event.getName() + ". Aguarde a formação das equipes.");
			return true;
		}
		else
		{
			player.sendMessage("Você já está registrado neste evento.");
			return false;
		}
	}
	
	public void unregister(EventHolder event, Player player)
	{
		EventRegistration reg = eventRegistrations.get(event);
		if (reg != null)
		{
			reg.registeredPlayers.remove(player);
			player.sendMessage("Você foi removido do registro do evento " + event.getName());
		}
	}
	
	public List<Player> getRegisteredPlayers(EventHolder event)
	{
		EventRegistration reg = eventRegistrations.get(event);
		if (reg != null)
			return Collections.unmodifiableList(reg.registeredPlayers);
		return Collections.emptyList();
	}
	
	private void checkMatches(EventHolder event)
	{
		EventRegistration reg = eventRegistrations.get(event);
		if (reg == null || !reg.registrationOpen)
			return;
		
		int groupSize = event.getRegistration().getGroupSize();
		int requiredForMatch = groupSize * 2;
		
		while (reg.registeredPlayers.size() >= requiredForMatch)
		{
			int rumbleId = IdFactory.getInstance().getNextId();
			
			List<Player> playersInMatch = new ArrayList<>();
			for (int i = 0; i < requiredForMatch; i++)
			{
				Player p = reg.registeredPlayers.remove(0);
				
				Player online = World.getInstance().getPlayer(p.getObjectId());
				if (online != null && online.isOnline())
				{
					playersInMatch.add(online);
				}
			}
			
			if (playersInMatch.size() == requiredForMatch)
			{
				BattleBossRumble match = new BattleBossRumble(rumbleId, event, playersInMatch);
				_activeRumbles.put(rumbleId, match);
				ThreadPool.execute(match);
			}
			else
			{
				reg.registeredPlayers.addAll(playersInMatch);
				break;
			}
		}
	}
	
	public void onPlayerDeath(Player player)
	{
		BattleBossRumble rumble = getRumbleForPlayer(player);
		if (rumble != null)
		{
			rumble.onPlayerDeath(player);
		}
	}
	
	public void onPlayerDeathMonster(Player player, Creature monster)
	{
		BattleBossRumble rumble = getRumbleForPlayer(player);
		if (rumble != null)
		{
			rumble.onPlayerDeathMonster(player, monster);
		}
	}
	
	public BattleBossRumble getRumbleForPlayer(Player player)
	{
		if (player == null || player.getBattleBossRumbleId() <= 0)
			return null;
		
		return _activeRumbles.get(player.getBattleBossRumbleId());
	}
	
	public void removeRumble(int rumbleId)
	{
		_activeRumbles.remove(rumbleId);
	}
	
	private void scheduleReminders(EventHolder event, long durationMs)
	{
		long now = System.currentTimeMillis();
		long endTime = now + durationMs;
		
		for (long t = 5 * 60 * 1000; t < durationMs; t += 5 * 60 * 1000)
		{
			long delay = t;
			ThreadPool.schedule(() ->
			{
				long remaining = (endTime - System.currentTimeMillis()) / 1000;
				long minutes = remaining / 60;
				World.announceToOnlinePlayers("[BattleBoss] O evento " + event.getName() + " fecha em " + minutes + " minutos. Registre-se!", true);
			}, delay);
		}
		
		if (durationMs > 30 * 1000)
		{
			ThreadPool.schedule(() ->
			{
				World.announceToOnlinePlayers("[BattleBoss] O evento " + event.getName() + " fechará em 30 segundos!", true);
			}, durationMs - 30 * 1000);
		}
		
		ThreadPool.schedule(() ->
		{
			World.announceToOnlinePlayers("[BattleBoss] O evento " + event.getName() + " foi encerrado!", true);
		}, durationMs);
	}
	
	public static BattleBossOpenRegister getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final BattleBossOpenRegister INSTANCE = new BattleBossOpenRegister();
	}
}

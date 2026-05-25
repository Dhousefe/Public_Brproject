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
package ext.mods.levelupmaker;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

import ext.mods.commons.config.ExProperties;
import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ThreadPool;
import ext.mods.commons.random.Rnd;
import ext.mods.Config;
import ext.mods.extensions.listener.command.OnBypassCommandListener;
import ext.mods.extensions.listener.actor.player.OnLevelUpListener;
import ext.mods.extensions.listener.manager.BypassCommandManager;
import ext.mods.extensions.listener.manager.PlayerListenerManager;
import ext.mods.gameserver.enums.GaugeColor;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.model.actor.instance.Monster;
import ext.mods.gameserver.model.entity.events.capturetheflag.CTFEvent;
import ext.mods.gameserver.model.entity.events.deathmatch.DMEvent;
import ext.mods.gameserver.model.entity.events.lastman.LMEvent;
import ext.mods.gameserver.model.entity.events.teamvsteam.TvTEvent;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.actor.move.MovementIntegration;
import ext.mods.gameserver.network.serverpackets.MagicSkillUse;
import ext.mods.gameserver.network.serverpackets.SetupGauge;
import ext.mods.gameserver.network.serverpackets.TutorialShowQuestionMark;
import ext.mods.tour.TournamentEvent;
import ext.mods.Crypta.RandomManager;

/**
 * @author Dhousefe <dhousefe@gmail.com>
 * data: 16/02/2026
 * LevelUpMaker - Sistema de up dinâmico e perpétuo.
 * Quando nenhum evento estiver ativo, exibe quest mark para teleporte a área de monstros.
 */
public final class LevelUpMakerManager implements OnBypassCommandListener, OnLevelUpListener
{
	private static final CLogger LOGGER = new CLogger(LevelUpMakerManager.class.getName());
	private static final String BYPASS_TELEPORT = "levelupmaker_teleport";

	private static final LevelUpMakerManager INSTANCE = new LevelUpMakerManager();

	private boolean _enabled;
	private int _questionMarkId;
	private int _levelOffset;
	private int _levelTolerance;
	private int _teleportMinRange;
	private int _teleportMaxRange;
	private int _castTimeMs;
	private int _scrollSkillId;
	private int _scrollSkillLevel;
	private int _refreshIntervalSec;
	private int _levelUpDelaySec;
	private List<String> _messages;
	private String _htmlTitle;
	private String _buttonLabel;
	private String _noMonsterMessage;
	private String _cannotTeleportMessage;

	private volatile ScheduledFuture<?> _refreshTask;
	private final AtomicInteger _messageIndex = new AtomicInteger(0);

	public static LevelUpMakerManager getInstance()
	{
		return INSTANCE;
	}

	private LevelUpMakerManager()
	{
	}

	public void init()
	{
		loadConfig();
		if (!_enabled)
		{
			LOGGER.info("LevelUpMaker is disabled.");
			return;
		}
		BypassCommandManager.getInstance().registerBypassListener(this);
		PlayerListenerManager.getInstance().registerLevelUpListener(this);
		startRefreshTask();
		LOGGER.info("LevelUpMaker initialized. QuestionMarkId={}, LevelOffset={}, LevelUpDelaySec={}", _questionMarkId, _levelOffset, _levelUpDelaySec);
	}

	public void shutdown()
	{
		if (_refreshTask != null)
		{
			_refreshTask.cancel(false);
			_refreshTask = null;
		}
		BypassCommandManager.getInstance().unregisterBypassListener(this);
		PlayerListenerManager.getInstance().unregisterLevelUpListener(this);
	}

	private void loadConfig()
	{
		final Path path = Config.CONFIG_PATH.resolve("levelupmaker.properties");
		final ExProperties props = new ExProperties();
		try (Reader reader = new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8))
		{
			props.load(reader);
		}
		catch (IOException e)
		{
			LOGGER.error("Failed to load levelupmaker.properties (UTF-8). Using defaults.", e);
		}

		_enabled = props.getProperty("LevelUpMakerEnabled", true);
		_questionMarkId = props.getProperty("LevelUpMakerQuestionMarkId", 2010);
		_levelOffset = props.getProperty("LevelUpMakerLevelOffset", 5);
		_levelTolerance = props.getProperty("LevelUpMakerLevelTolerance", 2);
		_teleportMinRange = Math.max(0, props.getProperty("LevelUpMakerTeleportMinRange", 200));
		_teleportMaxRange = Math.max(_teleportMinRange, props.getProperty("LevelUpMakerTeleportMaxRange", 400));
		_castTimeMs = Math.max(1000, props.getProperty("LevelUpMakerCastTimeMs", 10000));
		_scrollSkillId = props.getProperty("LevelUpMakerScrollSkillId", 2040);
		_scrollSkillLevel = props.getProperty("LevelUpMakerScrollSkillLevel", 1);
		_refreshIntervalSec = Math.max(30, props.getProperty("LevelUpMakerRefreshIntervalSec", 60));
		_levelUpDelaySec = Math.max(0, props.getProperty("LevelUpMakerLevelUpDelaySec", 30));
		_htmlTitle = props.getProperty("LevelUpMakerHtmlTitle", "Level Up Maker");
		_buttonLabel = props.getProperty("LevelUpMakerButtonLabel", "Teleportar para Area de Up");
		_noMonsterMessage = props.getProperty("LevelUpMakerNoMonsterMessage", "Nenhum monstro adequado encontrado no momento. Tente novamente mais tarde.");
		_cannotTeleportMessage = props.getProperty("LevelUpMakerCannotTeleportMessage", "Você não pode usar o teleporte no momento. Verifique se está em combate ou em área restrita.");

		final String msgList = props.getProperty("LevelUpMakerMessages", "Clique para ser teleportado para monstros nível %level%!");
		_messages = new ArrayList<>(List.of(msgList.split("\\|")));
		if (_messages.isEmpty())
			_messages.add("Teleporte para área de up (nível %level%).");
	}

	public boolean isEnabled()
	{
		return _enabled;
	}

	public int getQuestionMarkId()
	{
		return _questionMarkId;
	}

	/**
	 * Retorna true se nenhum evento estiver ativo (Tournament, CTF, DM, LM, TvT, Crypta).
	 */
	public boolean isNoEventActive()
	{
		if (TournamentEvent.isRunning())
			return false;
		if (Config.CTF_EVENT_ENABLED && (CTFEvent.getInstance().isParticipating() || CTFEvent.getInstance().isStarted()))
			return false;
		if (Config.DM_EVENT_ENABLED && (DMEvent.getInstance().isParticipating() || DMEvent.getInstance().isStarted()))
			return false;
		if (Config.LM_EVENT_ENABLED && (LMEvent.getInstance().isParticipating() || LMEvent.getInstance().isStarted()))
			return false;
		if (Config.TVT_EVENT_ENABLED && (TvTEvent.getInstance().isParticipating() || TvTEvent.getInstance().isStarted()))
			return false;
		for (ext.mods.gameserver.model.zone.type.RandomZone zone : RandomManager.getInstance().getActiveZones())
		{
			ext.mods.FarmEventRandom.holder.RandomZoneData zoneData = (ext.mods.FarmEventRandom.holder.RandomZoneData) RandomManager.getInstance().getZoneDataForZone(zone);
			if (zoneData != null && zoneData.isActive())
				return false;
		}
		return true;
	}

	/**
	 * Gera HTML com mensagem dinâmica alternada e botão de teleporte.
	 * Usa a mesma estrutura dos eventos (tutorial_alert).
	 */
	public String getTutorialAlertHtml(Player player)
	{
		if (player == null || !_enabled)
			return null;

		final String html = HTMLData.getInstance().getHtm(player.getLocale(), "html/mods/levelupmaker/tutorial_alert.htm");
		if (html == null || html.isEmpty())
			return null;

		final int playerLevel = player.getStatus().getLevel();
		final int targetLevel = playerLevel + _levelOffset;
		final int idx = _messageIndex.getAndUpdate(i -> (i + 1) % Math.max(1, _messages.size()));
		final String msgTemplate = _messages.get(idx);
		final String message = msgTemplate
			.replace("%level%", String.valueOf(targetLevel))
			.replace("%player_level%", String.valueOf(playerLevel));

		return html
			.replace("%title%", _htmlTitle)
			.replace("%message%", message)
			.replace("%button_label%", _buttonLabel)
			.replace("%button_bypass%", BYPASS_TELEPORT);
	}

	@Override
	public void onLevelUp(Player player)
	{
		if (!_enabled || _levelUpDelaySec <= 0 || player == null)
			return;

		ThreadPool.schedule(() ->
		{
			if (player == null || !player.isOnline() || player.isDead())
				return;
			if (!isNoEventActive())
				return;

			player.sendPacket(new TutorialShowQuestionMark(_questionMarkId));
		}, _levelUpDelaySec * 1000L);
	}

	@Override
	public boolean onBypass(Player player, String command)
	{
		if (command == null || (!command.equals(BYPASS_TELEPORT) && !command.endsWith(BYPASS_TELEPORT)))
			return false;

		handleTeleport(player);
		return true;
	}

	private void handleTeleport(Player player)
	{
		if (!_enabled || player == null)
			return;

		if (!canTeleport(player))
		{
			player.sendMessage(_cannotTeleportMessage);
			return;
		}

		ThreadPool.execute(() ->
		{
			final Monster monster = findMonsterForPlayer(player);
			if (monster == null)
			{
				player.sendMessage(_noMonsterMessage);
				return;
			}

			final Location targetLoc = calculateTeleportLocation(monster);
			if (targetLoc == null)
			{
				player.sendMessage(_noMonsterMessage);
				return;
			}

			executeTeleport(player, targetLoc);
		});
	}

	private boolean canTeleport(Player player)
	{
		if (player.isDead())
			return false;
		if (player.getKarma() > 0)
			return false;
		if (player.isInCombat())
			return false;
		if (player.getCast().isCastingNow() || player.isTeleporting())
			return false;
		if (player.getDungeon() != null)
			return false;
		if (player.isInTournament())
			return false;
		if (player.isInOlympiadMode() || player.isInObserverMode() || player.isFestivalParticipant())
			return false;
		if (player.isInJail())
			return false;
		if (player.isInsideZone(ext.mods.gameserver.enums.ZoneId.BOSS))
			return false;
		if (CTFEvent.getInstance().isStarted() && CTFEvent.getInstance().onEscapeUse(player.getObjectId()))
			return false;
		if (DMEvent.getInstance().isStarted() && DMEvent.getInstance().onEscapeUse(player.getObjectId()))
			return false;
		if (LMEvent.getInstance().isStarted() && LMEvent.getInstance().onEscapeUse(player.getObjectId()))
			return false;
		if (TvTEvent.getInstance().isStarted() && TvTEvent.getInstance().onEscapeUse(player.getObjectId()))
			return false;
		return true;
	}

	private Monster findMonsterForPlayer(Player player)
	{
		final int playerLevel = player.getStatus().getLevel();
		final int targetLevel = playerLevel + _levelOffset;
		final int minLevel = targetLevel - _levelTolerance;
		final int maxLevel = targetLevel + _levelTolerance;

		return World.getInstance().getObjects().stream()
			.filter(o -> o instanceof Monster m && !m.isDead() && !m.isRaidRelated())
			.map(Monster.class::cast)
			.filter(m ->
			{
				final int lvl = m.getStatus().getLevel();
				return lvl >= minLevel && lvl <= maxLevel;
			})
			.min((a, b) ->
			{
				final int diffA = Math.abs(a.getStatus().getLevel() - targetLevel);
				final int diffB = Math.abs(b.getStatus().getLevel() - targetLevel);
				return Integer.compare(diffA, diffB);
			})
			.orElse(null);
	}

	private Location calculateTeleportLocation(Monster monster)
	{
		final int mx = monster.getX();
		final int my = monster.getY();
		final int mz = monster.getZ();
		final int radiusStep = 50;
		final int angleStep = 30;

		for (int radius = _teleportMinRange; radius <= _teleportMaxRange; radius += radiusStep)
		{
			for (int angleDeg = 0; angleDeg < 360; angleDeg += angleStep)
			{
				final double angle = Math.toRadians(angleDeg);
				final int x = mx + (int) Math.round(radius * Math.cos(angle));
				final int y = my + (int) Math.round(radius * Math.sin(angle));

				final Location valid = MovementIntegration.getValidLocation(mx, my, mz, x, y, mz);
				if (valid != null)
					return valid;
			}
		}

		for (int i = 0; i < 10; i++)
		{
			final int radius = Rnd.get(_teleportMinRange, _teleportMaxRange);
			final double angle = Math.toRadians(Rnd.get(0, 359));
			final int x = mx + (int) Math.round(radius * Math.cos(angle));
			final int y = my + (int) Math.round(radius * Math.sin(angle));

			final Location valid = MovementIntegration.getValidLocation(mx, my, mz, x, y, mz);
			if (valid != null)
				return valid;
		}

		return null;
	}

	private void executeTeleport(Player player, Location targetLoc)
	{
		player.broadcastPacket(new MagicSkillUse(player, player, _scrollSkillId, _scrollSkillLevel, _castTimeMs, 0));
		player.sendPacket(new SetupGauge(GaugeColor.BLUE, _castTimeMs));

		ThreadPool.schedule(() ->
		{
			if (player.isDead() || player.isInCombat() || player.isTeleporting())
				return;

			player.teleToLocation(targetLoc);
		}, _castTimeMs);
	}

	private void startRefreshTask()
	{
		if (_refreshTask != null)
			_refreshTask.cancel(false);

		_refreshTask = ThreadPool.scheduleAtFixedRate(() ->
		{
			if (!_enabled || !isNoEventActive())
				return;

			World.getInstance().getPlayers().stream()
				.filter(p -> p != null && p.isOnline() && !p.isDead())
				.forEach(p -> p.sendPacket(new TutorialShowQuestionMark(_questionMarkId)));
		}, _refreshIntervalSec * 1000L, _refreshIntervalSec * 1000L);
	}

	public void sendQuestionMark(Player player)
	{
		if (!_enabled || !isNoEventActive() || player == null)
			return;

		player.sendPacket(new TutorialShowQuestionMark(_questionMarkId));
	}
}

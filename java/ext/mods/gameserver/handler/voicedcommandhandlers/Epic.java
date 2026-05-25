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
package ext.mods.gameserver.handler.voicedcommandhandlers;

import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.IntStream;

import ext.mods.commons.data.Pagination;
import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ThreadPool;
import ext.mods.commons.random.Rnd;

import ext.mods.Config;
import ext.mods.gameserver.data.manager.SpawnManager;
import ext.mods.gameserver.data.xml.NpcData;
import ext.mods.gameserver.enums.GaugeColor;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.handler.IVoicedCommandHandler;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.move.MovementIntegration;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.entity.events.capturetheflag.CTFEvent;
import ext.mods.gameserver.model.entity.events.deathmatch.DMEvent;
import ext.mods.gameserver.model.entity.events.lastman.LMEvent;
import ext.mods.gameserver.model.entity.events.teamvsteam.TvTEvent;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.spawn.ASpawn;
import ext.mods.gameserver.model.spawn.MultiSpawn;
import ext.mods.gameserver.model.spawn.Spawn;
import ext.mods.gameserver.model.spawn.SpawnData;
import ext.mods.gameserver.model.olympiad.OlympiadManager;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.network.serverpackets.MagicSkillUse;
import ext.mods.gameserver.network.serverpackets.SetupGauge;

/**
 * Epic Command Handler
 * 
 * Autor: Dhousefe - L2jBr
 * Data: 29/01/2026
 * Versão: 1.0.0
 * Descrição: Gerencia o comando de epic e aplica teleporte aos jogadores.
 * 
*/

public class Epic implements IVoicedCommandHandler
{
	protected static final CLogger LOGGER = new CLogger(Epic.class.getName());
	
	public static final int PAGE_LIMIT_15 = 15;
	private static final int SOE_VISUAL_SKILL_ID = 2040;
	private static final int SOE_VISUAL_SKILL_LEVEL = 1;
	private static final int CAST_TIME_MS = 17000;
	
	public static List<NpcTemplate> bosses = List.of();
	
	public static void load()
	{
		bosses = IntStream.of(Config.EPIC_BOSS_LIST).boxed().map(id -> NpcData.getInstance().getTemplate(id)).filter(o -> o != null).sorted(Comparator.comparing(NpcTemplate::getLevel)).toList();
	}
	
	private static final String[] VOICED_COMMANDS =
	{
		"epic"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player player, String target)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		st.nextToken();
		
		try
		{
			int page = 1;
			if (st.hasMoreTokens())
			{
				final String token = st.nextToken();
				if ("tele".equalsIgnoreCase(token))
				{
					final int npcId = Integer.parseInt(st.nextToken());
					page = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
					handleTeleport(player, npcId);
				}
				else
				{
					page = Integer.parseInt(token);
				}
			}
			showEpicHtm(player, page);
		}
		catch (Exception e)
		{
			LOGGER.error(e);
			showEpicHtm(player, 1);
		}
		
		return true;
	}
	
	private void showEpicHtm(Player player, int page)
	{
		if (!Config.SHOW_EPIC_HTM)
		{
			player.sendMessage(player.getSysString(10_200));
			return;
		}
		
		int row = 0;
		final Pagination<NpcTemplate> list = new Pagination<>(bosses.stream(), page, PAGE_LIMIT_15);
		list.append("<html><title>Epic Boss Spawn Info</title><body>");
		list.append("<center><br>");
		boolean antarasDeadMessageDisplayed = false;
		boolean antarasLiveMessageDisplayed = false;
		for (NpcTemplate npc : list)
		{
			String npcName = npc.getName().length() > 23 ? npc.getName().substring(0, 23) + "..." : npc.getName();
			final String teleLink = Config.EPIC_BOSS_TELEPORT_ENABLED
				? "<a action=\"bypass voiced_epic tele " + npc.getNpcId() + " " + page + "\">Tele</a>"
				: "";
			
			final var spawn = SpawnManager.getInstance().getSpawn(npc.getNpcId());
			if (spawn != null)
			{
				final SpawnData spawnData = spawn.getSpawnData();
				
				int npcId = spawn.getNpcId();
				if (npcId >= 29066 && npcId <= 29068)
				{
					if (!antarasDeadMessageDisplayed && spawnData != null && spawnData.getRespawnTime() > System.currentTimeMillis())
					{
						list.append((row % 2 == 0) ? "<table width=280 height=22 bgcolor=000000><tr>" : "<table width=280><tr>");
						list.append("<td width=\"140\" align=\"left\">", npcName, "[" + npc.getLevel() + "]", "</td>");
						list.append("<td width=\"90\" align=\"right\"><font color=\"FB5858\">" + formatRemaining(spawnData.getRespawnTime()) + "</font></td>");
						list.append("<td width=\"50\" align=\"center\">", teleLink, "</td>");
						list.append("</tr></table><img src=\"L2UI.SquareGray\" width=280 height=1>");
						antarasDeadMessageDisplayed = true;
					}
					else if (!antarasDeadMessageDisplayed && !antarasLiveMessageDisplayed)
					{
						list.append((row % 2 == 0) ? "<table width=280 height=22 bgcolor=000000><tr>" : "<table width=280><tr>");
						list.append("<td width=\"140\" align=\"left\">", npcName, "[" + npc.getLevel() + "]", "</td>");
						list.append("<td width=\"90\" align=\"right\"><font color=\"9CC300\">" + player.getSysString(10_074) + "</font></td>");
						list.append("<td width=\"50\" align=\"center\">", teleLink, "</td>");
						list.append("</tr></table><img src=\"L2UI.SquareGray\" width=280 height=1>");
						antarasLiveMessageDisplayed = true;
					}
					
					row++;
					continue;
				}
				
				if (spawnData != null && spawnData.getRespawnTime() > System.currentTimeMillis())
				{
					list.append((row % 2) == 0 ? "<table width=280 height=22 bgcolor=000000><tr>" : "<table width=280><tr>");
					list.append("<td width=\"140\" align=\"left\">", npcName, "[" + npc.getLevel() + "]", "</td>");
					list.append("<td width=\"90\" align=\"right\"><font color=\"FB5858\">" + formatRemaining(spawnData.getRespawnTime()) + "</font></td>");
					list.append("<td width=\"50\" align=\"center\">", teleLink, "</td>");
					list.append("</tr></table><img src=\"L2UI.SquareGray\" width=280 height=1>");
				}
				else
				{
					list.append((row % 2) == 0 ? "<table width=280 height=22 bgcolor=000000><tr>" : "<table width=280><tr>");
					list.append("<td width=\"140\" align=\"left\">", npcName, "[" + npc.getLevel() + "]", "</td>");
					list.append("<td width=\"90\" align=\"right\"><font color=\"9CC300\">" + player.getSysString(10_074) + "</font></td>");
					list.append("<td width=\"50\" align=\"center\">", teleLink, "</td>");
					list.append("</tr></table><img src=\"L2UI.SquareGray\" width=280 height=1>");
				}
			}
			else
			{
				list.append((row % 2) == 0 ? "<table width=280 height=22 bgcolor=000000><tr>" : "<table width=280><tr>");
				list.append("<td width=\"140\" align=\"left\">", npcName, "[" + npc.getLevel() + "]", "</td>");
				list.append("<td width=\"90\" align=\"right\"><font color=\"9CC300\">" + player.getSysString(10_074) + "</font></td>");
				list.append("<td width=\"50\" align=\"center\">", teleLink, "</td>");
				list.append("</tr></table><img src=\"L2UI.SquareGray\" width=280 height=1>");
			}
			
			row++;
		}
		list.generateSpace(22);
		list.generatePages("bypass voiced_epic %page%");
		list.append("</body></html>");
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setHtml(list.getContent());
		player.sendPacket(html);
	}
	
	private String formatRemaining(long respawnTime)
	{
		long remainingMs = respawnTime - System.currentTimeMillis();
		if (remainingMs <= 0)
			return "00D-00H-00M";
		
		long totalMinutes = remainingMs / 60000L;
		long days = totalMinutes / (24 * 60);
		long hours = (totalMinutes % (24 * 60)) / 60;
		long minutes = totalMinutes % 60;
		return String.format("%02dD-%02dH-%02dM", days, hours, minutes);
	}

	private void handleTeleport(Player player, int npcId)
	{
		if (!Config.EPIC_BOSS_TELEPORT_ENABLED)
			return;
		
		if (!canTeleport(player))
			return;
		
		final ASpawn spawn = SpawnManager.getInstance().getSpawn(npcId);
		if (spawn == null)
		{
			player.sendMessage("Não foi possível localizar o boss selecionado.");
			return;
		}

		if (!chargeTeleportFee(player))
			return;
		
		final Location bossLoc = resolveBossLocation(player, spawn);
		if (bossLoc == null)
		{
			player.sendMessage("Não foi possível determinar a localização do boss selecionado.");
			return;
		}
		
		final Location targetLoc = calculateTeleportLocation(player, bossLoc);
		if (targetLoc == null)
		{
			player.sendMessage("Não foi possível encontrar um ponto seguro para o teleporte.");
			return;
		}
		
		player.broadcastPacket(new MagicSkillUse(player, player, SOE_VISUAL_SKILL_ID, SOE_VISUAL_SKILL_LEVEL, CAST_TIME_MS, 0));
		player.sendPacket(new SetupGauge(GaugeColor.BLUE, CAST_TIME_MS));
		
		ThreadPool.schedule(() ->
		{
			if (player.isDead() || player.isInCombat() || player.isTeleporting())
				return;
			
			player.teleToLocation(targetLoc);
		}, CAST_TIME_MS);
	}
	
	private boolean canTeleport(Player player)
	{
		if (player.isDead())
		{
			player.sendMessage("Você não pode usar este teleporte enquanto está morto.");
			return false;
		}
		
		if (player.getKarma() > 0)
		{
			player.sendMessage("Você não pode usar este teleporte enquanto está PK.");
			return false;
		}
		
		if (player.isInCombat())
		{
			player.sendMessage("Você não pode usar este teleporte em combate.");
			return false;
		}
		
		if (player.getCast().isCastingNow() || player.isTeleporting())
		{
			player.sendMessage("Aguarde sua ação atual terminar.");
			return false;
		}
		
		if (player.getDungeon() != null)
		{
			player.sendMessage("Você não pode usar este teleporte dentro de dungeons.");
			return false;
		}
		
		if (player.isInTournament())
		{
			player.sendMessage("Você não pode usar este teleporte durante torneios.");
			return false;
		}
		
		if (CTFEvent.getInstance().isStarted() && CTFEvent.getInstance().onEscapeUse(player.getObjectId())
			|| DMEvent.getInstance().isStarted() && DMEvent.getInstance().onEscapeUse(player.getObjectId())
			|| LMEvent.getInstance().isStarted() && LMEvent.getInstance().onEscapeUse(player.getObjectId())
			|| TvTEvent.getInstance().isStarted() && TvTEvent.getInstance().onEscapeUse(player.getObjectId())
			|| player.isInOlympiadMode() || player.isInObserverMode() || player.isFestivalParticipant()
			|| player.isInJail() || player.isInsideZone(ZoneId.BOSS))
		{
			player.sendMessage("Seu estado atual não permite usar este teleporte.");
			return false;
		}
		
		if (OlympiadManager.getInstance().isRegistered(player))
		{
			player.sendMessage("Você não pode usar este teleporte enquanto está registrado na Olimpíada.");
			return false;
		}
		
		return true;
	}
	
	private Location calculateTeleportLocation(Player player, Location bossLoc)
	{
		final int minRange = Math.max(0, Config.BOSS_TELEPORT_MIN_RANGE);
		final int maxRange = Math.max(minRange, Config.BOSS_TELEPORT_MAX_RANGE);
		final int radiusStep = 50;
		final int angleStep = 30;
		
		for (int radius = minRange; radius <= maxRange; radius += radiusStep)
		{
			for (int angleDeg = 0; angleDeg < 360; angleDeg += angleStep)
			{
				final double angle = Math.toRadians(angleDeg);
				final int x = bossLoc.getX() + (int)Math.round(radius * Math.cos(angle));
				final int y = bossLoc.getY() + (int)Math.round(radius * Math.sin(angle));
				final int z = bossLoc.getZ();
				
				final Location valid = MovementIntegration.getValidLocation(
					bossLoc.getX(), bossLoc.getY(), bossLoc.getZ(),
					x, y, z
				);
				if (valid != null)
					return valid;
			}
		}
		
		for (int i = 0; i < 10; i++)
		{
			final int radius = Rnd.get(minRange, maxRange);
			final double angle = Math.toRadians(Rnd.get(0, 359));
			final int x = bossLoc.getX() + (int)Math.round(radius * Math.cos(angle));
			final int y = bossLoc.getY() + (int)Math.round(radius * Math.sin(angle));
			final int z = bossLoc.getZ();
			
			final Location valid = MovementIntegration.getValidLocation(
				bossLoc.getX(), bossLoc.getY(), bossLoc.getZ(),
				x, y, z
			);
			if (valid != null)
				return valid;
		}
		
		return null;
	}

	private Location resolveBossLocation(Player player, ASpawn spawn)
	{
		if (spawn.getNpc() != null)
			return new Location(spawn.getNpc().getX(), spawn.getNpc().getY(), spawn.getNpc().getZ());
		
		final SpawnData spawnData = spawn.getSpawnData();
		if (spawnData != null && (spawnData.getX() != 0 || spawnData.getY() != 0 || spawnData.getZ() != 0))
			return new Location(spawnData.getX(), spawnData.getY(), spawnData.getZ());
		
		if (spawn instanceof Spawn single)
			return new Location(single.getLocX(), single.getLocY(), single.getLocZ());
		
		if (spawn instanceof MultiSpawn multi)
		{
			final int[][] coords = multi.getCoords();
			if (coords != null && coords.length > 0)
				return getClosestSpawnLocation(player, coords);
		}
		
		final var spawnLoc = spawn.getSpawnLocation();
		if (spawnLoc != null)
			return new Location(spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ());
		
		return null;
	}

	private Location getClosestSpawnLocation(Player player, int[][] coords)
	{
		int bestIdx = 0;
		long bestDist = Long.MAX_VALUE;
		for (int i = 0; i < coords.length; i++)
		{
			final int dx = coords[i][0] - player.getX();
			final int dy = coords[i][1] - player.getY();
			final long dist = (long) dx * dx + (long) dy * dy;
			if (dist < bestDist)
			{
				bestDist = dist;
				bestIdx = i;
			}
		}
		
		return new Location(coords[bestIdx][0], coords[bestIdx][1], coords[bestIdx][2]);
	}

	private boolean chargeTeleportFee(Player player)
	{
		if (Config.BOSS_TELEPORT_ITEM_ID <= 0 || Config.BOSS_TELEPORT_ITEM_COUNT <= 0)
			return true;
		
		final long count = Config.BOSS_TELEPORT_ITEM_COUNT;
		if (count > Integer.MAX_VALUE)
		{
			player.sendMessage("Quantidade configurada inválida para o teleporte.");
			return false;
		}
		
		if (!player.destroyItemByItemId(Config.BOSS_TELEPORT_ITEM_ID, (int) count, true))
			return false;
		
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
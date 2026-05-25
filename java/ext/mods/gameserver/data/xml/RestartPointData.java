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
package ext.mods.gameserver.data.xml;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;

import ext.mods.gameserver.data.manager.CastleManager;
import ext.mods.gameserver.data.manager.ClanHallManager;
import ext.mods.gameserver.data.manager.SevenSignsManager;
import ext.mods.gameserver.data.manager.ZoneManager;
import ext.mods.gameserver.enums.CabalType;
import ext.mods.gameserver.enums.RestartType;
import ext.mods.gameserver.enums.SealType;
import ext.mods.gameserver.enums.SiegeSide;
import ext.mods.gameserver.enums.SpawnType;
import ext.mods.gameserver.enums.actors.ClassRace;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.location.Point2D;
import ext.mods.gameserver.model.residence.castle.Castle;
import ext.mods.gameserver.model.residence.castle.Siege;
import ext.mods.gameserver.model.residence.clanhall.ClanHall;
import ext.mods.gameserver.model.residence.clanhall.ClanHallSiege;
import ext.mods.gameserver.model.restart.RestartArea;
import ext.mods.gameserver.model.restart.RestartPoint;
import ext.mods.gameserver.model.zone.type.ArenaZone;
import ext.mods.gameserver.model.zone.type.RandomZone;
import ext.mods.gameserver.scripting.script.siegablehall.FlagWar;
import ext.mods.Crypta.RandomManager;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

/**
 * This class loads and handles following types of zones used when under the process of revive, or scrolling out :
 * <ul>
 * <li>{@link RestartArea}s retain delimited areas bound to {@link Player}'s {@link ClassRace}. It priors and overrides initial behavior.</li>
 * <li>{@link RestartPoint}s define region-scale restart points.</li>
 * </ul>
 */
public final class RestartPointData implements IXmlReader
{
	private final List<RestartArea> _restartAreas = new ArrayList<>();
	private final List<RestartPoint> _restartPoints = new ArrayList<>();
	
	protected RestartPointData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("xml/restartPointAreas.xml");
		LOGGER.info("Loaded {} restart areas and {} restart points.", _restartAreas.size(), _restartPoints.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		final List<Point2D> coords = new ArrayList<>();
		
		forEach(doc, "list", listNode ->
		{
			forEach(listNode, "area", areaNode ->
			{
				final StatSet set = parseAttributes(areaNode);
				
				forEach(areaNode, "node", nodeNode -> coords.add(parsePoint2D(nodeNode)));
				set.set("coords", coords);
				
				
				final EnumMap<ClassRace, String> classRestrictions = new EnumMap<>(ClassRace.class);
				forEach(areaNode, "restart", restartNode ->
				{
					final NamedNodeMap nodeAttrs = restartNode.getAttributes();
					classRestrictions.put(parseEnum(nodeAttrs, ClassRace.class, "race"), parseString(nodeAttrs, "zone"));
				});
				set.set("classRestrictions", classRestrictions);
				
				_restartAreas.add(new RestartArea(set));
				
				coords.clear();
			});
			
			forEach(listNode, "point", pointNode ->
			{
				final StatSet set = new StatSet();
				final List<Location> points = new ArrayList<>();
				final List<Location> chaoPoints = new ArrayList<>();
				final List<IntIntHolder> mapRegions = new ArrayList<>();
				
				forEach(pointNode, "set", setNode ->
				{
					final NamedNodeMap setAttrs = setNode.getAttributes();
					final String name = parseString(setAttrs, "name");
					
					switch (name)
					{
						case "point":
							points.add(parseLocation(setAttrs, "val"));
							break;
						
						case "chaoPoint":
							chaoPoints.add(parseLocation(setAttrs, "val"));
							break;
						
						case "map":
							mapRegions.add(parseIntIntHolder(setAttrs, "val"));
							break;
						
						default:
							set.set(name, parseString(setAttrs, "val"));
							break;
					}
				});
				
				set.set("points", (points.isEmpty()) ? Collections.emptyList() : points);
				set.set("chaoPoints", (chaoPoints.isEmpty()) ? Collections.emptyList() : chaoPoints);
				set.set("mapRegions", (mapRegions.isEmpty()) ? Collections.emptyList() : mapRegions);
				
				_restartPoints.add(new RestartPoint(set));
			});
		});
	}
	
	public void reload()
	{
		_restartAreas.clear();
		_restartPoints.clear();
		
		load();
	}
	
	public List<RestartArea> getRestartAreas()
	{
		return _restartAreas;
	}
	
	public List<RestartPoint> getRestartPoints()
	{
		return _restartPoints;
	}
	
	/**
	 * @param player : The {@link Player} to test.
	 * @return The {@link RestartArea} associated to the X/Y/Z position of the {@link Player} set as parameter, or null if none is found.
	 */
	public RestartArea getRestartArea(Player player)
	{
		return _restartAreas.stream().filter(ra -> ra.isInside(player)).findFirst().orElse(null);
	}
	
	/**
	 * @param creature : The {@link Creature} to test.
	 * @return The {@link RestartPoint} associated to the geomap position of the {@link Player} set as parameter, or null if none is found.
	 */
	public RestartPoint getRestartPoint(Creature creature)
	{
		return getRestartPoint(creature.getPosition());
	}
	
	/**
	 * @param loc : The {@link Location} to test.
	 * @return The {@link RestartPoint} associated to the geomap position of the {@link Location} set as parameter, or null if none is found.
	 */
	public RestartPoint getRestartPoint(Location loc)
	{
		final int rx = (loc.getX() - World.WORLD_X_MIN) / World.TILE_SIZE + World.TILE_X_MIN;
		final int ry = (loc.getY() - World.WORLD_Y_MIN) / World.TILE_SIZE + World.TILE_Y_MIN;
		
		for (RestartPoint ra : _restartPoints)
		{
			for (IntIntHolder iih : ra.getMapRegions())
				if (iih.equals(rx, ry))
					return ra;
		}
		return null;
	}
	
	/**
	 * @param name : The {@link String} to test.
	 * @return The {@link RestartPoint} associated to a name.
	 */
	public RestartPoint getRestartPointByName(String name)
	{
		return _restartPoints.stream().filter(rp -> rp.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	/**
	 * Search over both {@link RestartArea}s and {@link RestartPoint}s to define best teleport {@link RestartPoint}, based on {@link Player}'s {@link ClassRace} and karma.
	 * @param player : The {@link Player} to test.
	 * @return The nearest {@link RestartPoint} took over either {@link RestartArea}s or {@link RestartPoint}s.
	 */
	public RestartPoint getCalculatedRestartPoint(Player player)
	{
		final RestartArea area = getRestartArea(player);
		if (area != null)
			return getRestartPointByName(area.getClassRestriction(player));
		
		RestartPoint restartPoint = getRestartPoint(player);
		if (restartPoint == null)
			return null;
		
		if (restartPoint.getBannedRace() == player.getRace())
			restartPoint = getRestartPointByName(restartPoint.getBannedPoint());
		
		return restartPoint;
	}
	
	/**
	 * Search over both {@link RestartArea}s and {@link RestartPoint}s to define best teleport {@link Location}, based on {@link Player}'s {@link ClassRace} and karma.
	 * @param player : The {@link Player} to test.
	 * @return A {@link Location} took randomly over either {@link RestartArea}s or {@link RestartPoint}s.
	 */
	public Location getNearestRestartLocation(Player player)
	{
		RestartPoint restartPoint;
		
		final RestartArea area = getRestartArea(player);
		if (area != null)
		{
			restartPoint = getRestartPointByName(area.getClassRestriction(player));
			if (restartPoint == null)
				return null;
			
			return (player.getKarma() > 0) ? restartPoint.getRandomChaoPoint() : restartPoint.getRandomPoint();
		}
		
		restartPoint = getRestartPoint(player);
		if (restartPoint == null)
			return null;
		
		if (restartPoint.getBannedRace() == player.getRace())
			restartPoint = getRestartPointByName(restartPoint.getBannedPoint());
		
		if (restartPoint == null)
			return null;
		
		return (player.getKarma() > 0) ? restartPoint.getRandomChaoPoint() : restartPoint.getRandomPoint();
	}
	
	/**
	 * @param player : The {@link Player} to check.
	 * @param teleportType : The {@link RestartType} to check.
	 * @return a {@link Location} based on {@link Player} own position.
	 */
	public Location getLocationToTeleport(Player player, RestartType teleportType)
	{
		if (teleportType != RestartType.TOWN && player.getClan() != null)
		{
			if (teleportType == RestartType.CLAN_HALL)
			{
				final ClanHall ch = ClanHallManager.getInstance().getClanHallByOwner(player.getClan());
				if (ch != null)
					return ch.getRndSpawn(SpawnType.OWNER);
			}
			else if (teleportType == RestartType.CASTLE)
			{
				final Castle castle = CastleManager.getInstance().getCastleByOwner(player.getClan());
				if (castle != null)
					return castle.getRndSpawn(SpawnType.OWNER);
				
				final Siege siege = CastleManager.getInstance().getActiveSiege(player);
				if (siege != null && siege.checkSides(player.getClan(), SiegeSide.DEFENDER, SiegeSide.OWNER))
					return siege.getCastle().getRndSpawn(SpawnType.OWNER);
			}
			else if (teleportType == RestartType.SIEGE_FLAG)
			{
				final Siege siege = CastleManager.getInstance().getActiveSiege(player);
				if (siege != null)
				{
					final Npc flag = siege.getFlag(player.getClan());
					if (flag != null)
						return flag.getPosition();
				}
				
				final ClanHallSiege chs = ClanHallManager.getInstance().getActiveSiege(player);
				if (chs != null)
				{
					final Npc flag = chs.getFlag(player.getClan());
					if (flag != null)
						return flag.getPosition();
				}
			}
		}
		
		final ArenaZone arena = ZoneManager.getInstance().getZone(player, ArenaZone.class);
		if (arena != null)
			return arena.getRndSpawn((player.getKarma() > 0) ? SpawnType.CHAOTIC : SpawnType.NORMAL);
			
		final RandomZone random = ZoneManager.getInstance().getZone(player, RandomZone.class);
		if (random != null)
		{
			try
			{
				if (random.isActive())
				{
					Object randomManager = RandomManager.getInstance();
					if (randomManager != null)
					{
						Boolean isRunning = (Boolean) RandomManager.getInstance().isEventRunning();
						if (isRunning != null && isRunning)
						{
						}
						else
						{
							Location zoneSpawn = random.getRndSpawn((player.getKarma() > 0) ? SpawnType.CHAOTIC : SpawnType.NORMAL);
							if (zoneSpawn != null)
								return zoneSpawn;
						}
					}
				}
				else
				{
					Location zoneSpawn = random.getRndSpawn((player.getKarma() > 0) ? SpawnType.CHAOTIC : SpawnType.NORMAL);
					if (zoneSpawn != null)
						return zoneSpawn;
				}
			}
			catch (Exception e)
			{
				try
				{
					Location zoneSpawn = random.getRndSpawn((player.getKarma() > 0) ? SpawnType.CHAOTIC : SpawnType.NORMAL);
					if (zoneSpawn != null)
						return zoneSpawn;
				}
				catch (Exception ex)
				{
				}
			}
		}
			
		
		final Castle castle = CastleManager.getInstance().getCastle(player);
		if (castle != null && castle.getSiege().isInProgress() && SevenSignsManager.getInstance().isSealValidationPeriod() && SevenSignsManager.getInstance().getSealOwner(SealType.STRIFE) == CabalType.DAWN)
			return castle.getRndSpawn((player.getKarma() > 0) ? SpawnType.CHAOTIC : SpawnType.OTHER);
		
		final ClanHallSiege chs = ClanHallManager.getInstance().getActiveSiege(player);
		if (chs instanceof FlagWar fw)
		{
			final Location fwRestartPoint = fw.getClanRestartPoint(player.getClan());
			if (fwRestartPoint != null)
				return fwRestartPoint;
		}
		
		return getNearestRestartLocation(player);
	}
	
	/**
	 * @param player : The {@link Player} to check.
	 * @return the {@link String} used for Auctioneer HTM.
	 */
	public final String getAgitMap(Player player)
	{
		final RestartPoint restartPoint = getRestartPoint(player);
		if (restartPoint == null)
			return "aden";
		
		switch (restartPoint.getLocName())
		{
			case 912:
				return "gludio";
			
			case 911:
				return "gludin";
			
			case 916:
				return "dion";
			
			case 918:
				return "giran";
			
			case 1537:
				return "rune";
			
			case 1538:
				return "godard";
			
			case 1714:
				return "schuttgart";
			
			default:
				return "aden";
		}
	}
	
	public static RestartPointData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final RestartPointData INSTANCE = new RestartPointData();
	}
}
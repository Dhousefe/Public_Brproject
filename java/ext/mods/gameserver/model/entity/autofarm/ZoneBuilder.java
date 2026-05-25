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
package ext.mods.gameserver.model.entity.autofarm;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ThreadPool;

import ext.mods.Config;
import ext.mods.gameserver.model.entity.autofarm.zone.AutoFarmArea;
import ext.mods.gameserver.model.entity.autofarm.zone.form.ZoneNPolyZ;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.network.serverpackets.ExServerPrimitive;
import ext.mods.gameserver.network.serverpackets.ExServerPrimitive.Point;

public class ZoneBuilder
{
	private static final CLogger LOGGER = new CLogger(ZoneBuilder.class.getName());
	
	public ExServerPrimitive getDebugPacket(Player player)
	{
		return player.getDebugPacket("ZoneBuilder");
	}
	
	private static ExServerPrimitive getDebugPacket2(Player player)
	{
		return player.getDebugPacket("ZBCylinder");
	}
	
	public Map<Integer, Point> getPoints(Player player)
	{
		return getDebugPacket(player).getPoints();
	}
	
	public List<Location> getPointsLoc(Player player)
	{
		return new ArrayList<>(getPoints(player).values());
	}
	
	public void clearAllPreview(Player player)
	{
		player.clearDebugPackets();
	}
	
	public void clearCylinderPreview(Player player)
	{
		getDebugPacket2(player).reset();
		getDebugPacket2(player).sendTo(player);
	}
	
	public void restoreDebugPoints(Player player, List<Location> location)
	{
		final ExServerPrimitive debug = getDebugPacket(player);
		for (Location loc : location)
		{
			addPoint(debug, loc);
		}
	}
	
	public void removePoint(Player player, int nodeId)
	{
		final ExServerPrimitive debug = getDebugPacket(player);
		
		debug.getPoints().remove(nodeId);
		
		if (debug.getPoints().isEmpty())
		{
			ThreadPool.execute(() -> clearAllPreview(player));
			return;
		}
		
		final List<Location> backup = getPointsLoc(player);
		debug.reset();
		
		backup.forEach(l -> addPoint(debug, l));
		
		debug.sendTo(player);
	}
	
	public void addPoint(Player player, Location loc)
	{
		final ExServerPrimitive debug = getDebugPacket(player);
		addPoint(debug, loc);
		debug.sendTo(player);
		AutoFarmManager.getInstance().showZoneWindow(player);
	}
	
	private static void addPoint(ExServerPrimitive debug, Location loc)
	{
		debug.addPoint(String.valueOf(debug.getPoints().size() + 1), Color.RED, true, loc.getX(), loc.getY(), loc.getZ());
		
		if (debug.getPoints().size() > 1)
		{
			final Point previousPoint = debug.getPoints().get(debug.getPoints().size() - 1);
			debug.addLine(Color.GREEN, previousPoint.getX(), previousPoint.getY(), previousPoint.getZ(), loc.getX(), loc.getY(), loc.getZ());
		}
	}
	
	public void preview(Player player)
	{
		if (getPoints(player).isEmpty())
			return;
		
		getDebugPacket(player).sendTo(player);
	}
	
	/*
	 * Método Padrão: Desenha ao redor do player (Modo Open)
	 */
	public void previewCylinder(Player player, int radius)
	{
		previewCylinder(player, radius, Color.YELLOW, player.getPosition());
	}

	public void previewCylinder(Player player, int radius, Color color)
	{
		previewCylinder(player, radius, color, player.getPosition());
	}
	
	/**
	 * MÉTODO CRÍTICO PARA MODO FECHADO:
	 * Desenha o cilindro ao redor de um ponto específico (Anchor), independente de onde o player esteja.
	 */
	public void previewCylinder(Player player, int radius, Color color, Location center)
	{
		final ExServerPrimitive packet = getDebugPacket2(player);
		packet.reset();
		
		previewCylinderCalc(player, packet, radius, color, center);
		
		packet.sendTo(player);
	}
	
	private static void previewCylinderCalc(Player player, ExServerPrimitive debug, int radius, Color color, Location center)
	{
		final int centerX = center.getX();
		final int centerY = center.getY();
		
		final int centerZ = center.getZ(); 
		
		final int count = (int) (2 * Math.PI * radius / (radius / 5)); 
		final double angle = 2 * Math.PI / count;
		
		int prevX = (int) (Math.cos(0) * radius) + centerX;
		int prevY = (int) (Math.sin(0) * radius) + centerY;
		int prevZ = GeoEngine.getInstance().getHeight(prevX, prevY, centerZ);
		
		for (int i = 1; i <= count; i++)
		{
			final int x = (int) (Math.cos(angle * i) * radius) + centerX;
			final int y = (int) (Math.sin(angle * i) * radius) + centerY;
			final int z = GeoEngine.getInstance().getHeight(x, y, centerZ);
			
			debug.addLine("", color, true, prevX, prevY, prevZ, x, y, z);
			
			prevX = x;
			prevY = y;
			prevZ = z;
		}
	}
	
	public void previewFinalArea(AutoFarmProfile autoFarmProfile, int areaId)
	{
		final ExServerPrimitive packet = getDebugPacket(autoFarmProfile.getPlayer());
		packet.reset();
		autoFarmProfile.getAreaById(areaId).visualizeZone(packet);
		packet.sendTo(autoFarmProfile.getPlayer());
	}
	
	public void setAutoFarmAreaZone(Player player, AutoFarmArea area)
	{
		final List<Location> nodes = area.getNodes();
		if (nodes.isEmpty()) 
			nodes.addAll(getPoints(player).values());
		
		final int minZ = nodes.stream().mapToInt(Location::getZ).min().orElse(0) - 100;
		final int maxZ = nodes.stream().mapToInt(Location::getZ).max().orElse(0) + 100;
		
		int[] aX = new int[nodes.size()];
		int[] aY = new int[nodes.size()];
		int[] aZ = new int[nodes.size()];
		
		if (Config.DEVELOPER)
		{
			final StringBuilder sb = new StringBuilder();
			sb.append(String.format("<zone shape=\"%s\" minZ=\"%d\" maxZ=\"%d\">\n", "NPoly", minZ, maxZ));
			sb.append(String.format("\t<stat name=\"name\" val=\"%s\"/>\n", area.getName()));
			
			for (Location loc : nodes)
				sb.append(String.format("\t<node x=\"%d\" y=\"%d\" z=\"%d\"/>\n", loc.getX(), loc.getY(), loc.getZ()));
			
			sb.append("</zone>");
			LOGGER.info(sb.toString());
		}
		
		for (int i = 0; i < nodes.size(); i++)
		{
			aX[i] = nodes.get(i).getX();
			aY[i] = nodes.get(i).getY();
			aZ[i] = nodes.get(i).getZ();
		}
		
		area.setZone(new ZoneNPolyZ(aX, aY, aZ, minZ, maxZ));
	}
	
	public double calculateArea(Player player)
	{
		final List<Location> nodes = getPointsLoc(player);
		final int n = nodes.size();
		double area = 0.0;
		
		for (int i = 0; i < n; i++)
		{
			int j = (i + 1) % n;
			area += nodes.get(i).getX() * nodes.get(j).getY() - nodes.get(i).getY() * nodes.get(j).getX();
		}
		
		area = Math.abs(area) / 2.0;
		return area;
	}
	
	public double calculatePerimeter(Player player)
	{
		final List<Location> nodes = getPointsLoc(player);
		final int n = nodes.size();
		double perimeter = 0.0;
		
		for (int i = 0; i < n; i++)
		{
			int j = (i + 1) % n;
			double dx = nodes.get(i).getX() - nodes.get(j).getX();
			double dy = nodes.get(i).getY() - nodes.get(j).getY();
			perimeter += Math.sqrt(dx * dx + dy * dy);
		}
		
		return perimeter;
	}
	
	public static ZoneBuilder getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ZoneBuilder INSTANCE = new ZoneBuilder();
	}
}
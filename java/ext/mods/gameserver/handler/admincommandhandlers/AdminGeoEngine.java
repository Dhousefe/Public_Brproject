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
package ext.mods.gameserver.handler.admincommandhandlers;

import java.awt.Color;
import java.util.List;
import java.util.StringTokenizer;

import ext.mods.Config;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.geoengine.geodata.ABlock;
import ext.mods.gameserver.geoengine.geodata.GeoStructure;
import ext.mods.gameserver.geoengine.geodata.IGeoObject;
import ext.mods.gameserver.handler.IAdminCommandHandler;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.location.SpawnLocation;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ExServerPrimitive;

public class AdminGeoEngine implements IAdminCommandHandler
{
	private static final String Y = "x ";
	private static final String N = "   ";
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_geo",
		"admin_path"
	};
	
	private Creature getCreatureTarget(Player player, boolean sendMessage)
	{
		final WorldObject target = player.getTarget();
		if (target == null || !(target instanceof Creature))
		{
			if (sendMessage)
				player.sendPacket(SystemMessageId.INVALID_TARGET);
			return null;
		}
		return (Creature) target;
	}
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		st.nextToken();
		
		if (command.startsWith("admin_geo"))
		{
			try
			{
				switch (st.nextToken())
				{
					case "bug":
						int geoX = GeoEngine.getGeoX(player.getX());
						int geoY = GeoEngine.getGeoY(player.getY());
						
						if (GeoEngine.getInstance().hasGeoPos(geoX, geoY))
						{
							try
							{
								String comment = command.substring(14);
								if (GeoEngine.getInstance().addGeoBug(player.getPosition(), player.getName() + ": " + comment))
									player.sendMessage("GeoData bug saved.");
							}
							catch (Exception e)
							{
								player.sendMessage("Usage: //geo bug comments");
							}
						}
						else
							player.sendMessage("There is no geodata at this position.");
						break;
					
					case "pos":
						int ox = player.getX();
						int oy = player.getY();
						int oz = player.getZ();
						
						geoX = GeoEngine.getGeoX(ox);
						geoY = GeoEngine.getGeoY(oy);
						
						int rx = (ox - World.WORLD_X_MIN) / World.TILE_SIZE + World.TILE_X_MIN;
						int ry = (oy - World.WORLD_Y_MIN) / World.TILE_SIZE + World.TILE_Y_MIN;
						ABlock block = GeoEngine.getInstance().getBlock(geoX, geoY);
						
						player.sendMessage("Region: " + rx + "_" + ry + "; Block: " + block.getClass().getSimpleName());
						
						if (block.hasGeoPos())
						{
							int geoZ = block.getHeightNearest(geoX, geoY, player.getZ(), null);
							byte nswe = block.getNsweNearest(geoX, geoY, geoZ, null);
							
							player.sendMessage("    " + ((nswe & GeoStructure.CELL_FLAG_N) != 0 && (nswe & GeoStructure.CELL_FLAG_W) != 0 ? Y : N) + ((nswe & GeoStructure.CELL_FLAG_N) != 0 ? Y : N) + ((nswe & GeoStructure.CELL_FLAG_N) != 0 && (nswe & GeoStructure.CELL_FLAG_E) != 0 ? Y : N) + "         GeoX=" + geoX);
							player.sendMessage("    " + ((nswe & GeoStructure.CELL_FLAG_W) != 0 ? Y : N) + "o " + ((nswe & GeoStructure.CELL_FLAG_E) != 0 ? Y : N) + "         GeoY=" + geoY);
							player.sendMessage("    " + ((nswe & GeoStructure.CELL_FLAG_S) != 0 && (nswe & GeoStructure.CELL_FLAG_W) != 0 ? Y : N) + ((nswe & GeoStructure.CELL_FLAG_S) != 0 ? Y : N) + ((nswe & GeoStructure.CELL_FLAG_S) != 0 && (nswe & GeoStructure.CELL_FLAG_E) != 0 ? Y : N) + "         GeoZ=" + geoZ);
							
							ExServerPrimitive debug = player.getDebugPacket("POS");
							debug.reset();
							
							debug.addSquare(Color.GREEN, ox & 0xFFFFFFF0, oy & 0xFFFFFFF0, oz, 15);
							debug.addPoint("POS", Color.RED, true, ox, oy, oz);
							
							debug.sendTo(player);
						}
						else
							player.sendMessage("There is no geodata at this position.");
						break;
					
					case "see":
						Creature targetCreature = getCreatureTarget(player, true);
						
						ExServerPrimitive debug = player.getDebugPacket("CAN_SEE");
						debug.reset();
						
						ox = player.getX();
						oy = player.getY();
						oz = player.getZ();
						
						int oh = (int) (2 * player.getCollisionHeight());
						debug.addLine("origin", Color.BLUE, true, ox, oy, oz, ox, oy, oz + oh);
						oh = (oh * Config.PART_OF_CHARACTER_HEIGHT) / 100;
						
						int tx = targetCreature.getX();
						int ty = targetCreature.getY();
						int tz = targetCreature.getZ();
						
						int th = (int) (2 * targetCreature.getCollisionHeight());
						debug.addLine("target", Color.BLUE, true, tx, ty, tz, tx, ty, tz + th);
						th = (th * Config.PART_OF_CHARACTER_HEIGHT) / 100;
						
						IGeoObject ignore = (targetCreature instanceof IGeoObject igo) ? igo : null;
						
						boolean canSee = GeoEngine.getInstance().canSee(tx, ty, tz, th, ox, oy, oz, oh, ignore, debug);
						canSee &= GeoEngine.getInstance().canSee(ox, oy, oz, oh, tx, ty, tz, th, ignore, debug);
						
						oh += oz;
						th += tz;
						
						debug.addLine("Line-of-Sight", canSee ? Color.GREEN : Color.RED, true, ox, oy, oh, tx, ty, th);
						debug.addLine("Geodata limit", Color.MAGENTA, true, ox, oy, oh + Config.MAX_OBSTACLE_HEIGHT, tx, ty, th + Config.MAX_OBSTACLE_HEIGHT);
						debug.sendTo(player);
						break;
					
					case "move":
						final WorldObject targetWorldObject = player.getTarget();
						if (targetWorldObject == null)
						{
							player.sendPacket(SystemMessageId.INVALID_TARGET);
							return;
						}
						
						SpawnLocation aLoc = player.getPosition();
						SpawnLocation tLoc = targetWorldObject.getPosition();
						
						debug = player.getDebugPacket("CAN_MOVE");
						debug.reset();
						
						Location loc = GeoEngine.getInstance().getValidLocation(aLoc.getX(), aLoc.getY(), aLoc.getZ(), tLoc.getX(), tLoc.getY(), tLoc.getZ(), debug);
						debug.addLine("Can move", Color.GREEN, true, aLoc, loc);
						if (loc.equals(tLoc))
						{
							player.sendMessage("Can move beeline.");
						}
						else
						{
							debug.addLine(Color.WHITE, aLoc, tLoc);
							debug.addLine("Inaccessible", Color.RED, true, loc, tLoc);
							debug.addPoint("Limit", Color.RED, true, loc);
							player.sendMessage("Can not move beeline!");
						}
						break;
					
					case "fly":
						targetCreature = getCreatureTarget(player, true);
						
						debug = player.getDebugPacket("CAN_FLY");
						debug.reset();
						
						ox = player.getX();
						oy = player.getY();
						oz = player.getZ();
						oh = (int) (2 * player.getCollisionHeight());
						debug.addLine("origin", Color.BLUE, true, ox, oy, oz - 32, ox, oy, oz + oh - 32);
						
						tx = targetCreature.getX();
						ty = targetCreature.getY();
						tz = targetCreature.getZ();
						
						loc = GeoEngine.getInstance().getValidFlyLocation(ox, oy, oz, oh, tx, ty, tz, debug);
						int x = loc.getX();
						int y = loc.getY();
						int z = loc.getZ();
						
						boolean canFly = x == tx && y == ty && z == tz;
						
						debug.addLine("Can fly", Color.GREEN, true, ox, oy, oz - 32, x, y, z - 32);
						
						if (canFly)
							player.sendMessage("Can fly beeline.");
						else
						{
							player.sendMessage("Can not fly beeline!");
							
							debug.addLine(Color.WHITE, ox, oy, oz - 32, tx, ty, tz - 32);
							debug.addLine("Inaccessible", Color.RED, true, x, y, z - 32, tx, ty, tz - 32);
							debug.addLine("Last position", Color.RED, true, x, y, z - 32, x, y, z + oh - 32);
						}
						
						debug.addLine("Line-of-Flight MIN", canFly ? Color.GREEN : Color.RED, true, ox, oy, oz - 32, tx, ty, tz - 32);
						debug.addLine("Line-of-Fligth MAX", canFly ? Color.GREEN : Color.RED, true, ox, oy, oz + oh - 32, tx, ty, tz + oh - 32);
						debug.sendTo(player);
						break;
					
					case "grid":
						try
						{
							int radius = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 5;
							radius = Math.min(Math.max(radius, 2), 20);
							
							displayGeoGrid(player, radius);
						}
						catch (NumberFormatException e)
						{
							player.sendMessage("Usage: //geo grid [radius] - radius deve ser entre 2 e 20");
						}
						break;
				}
			}
			catch (Exception e)
			{
				player.sendMessage("Usage: //geo bug|pos|see|move|fly|grid");
			}
		}
		else if (command.startsWith("admin_path"))
		{
			try
			{
				switch (st.nextToken())
				{
					case "find":
						final WorldObject targetWorldObject = player.getTarget();
						if (targetWorldObject == null)
						{
							player.sendPacket(SystemMessageId.INVALID_TARGET);
							return;
						}
						
						final ExServerPrimitive debug = player.getDebugPacket("PATH");
						debug.reset();
						
						final List<Location> path = GeoEngine.getInstance().findPath(player.getX(), player.getY(), player.getZ(), targetWorldObject.getX(), targetWorldObject.getY(), targetWorldObject.getZ(), true, debug);
						if (path.isEmpty())
						{
							player.sendMessage("No route found or pathfinding is disabled.");
							return;
						}
						
						for (Location loc : path)
							player.sendMessage("x:" + loc.getX() + " y:" + loc.getY() + " z:" + loc.getZ());
						
						debug.sendTo(player);
						break;
				}
			}
			catch (Exception e)
			{
				player.sendMessage("Usage: //path find|info");
			}
		}
	}
	
	/**
	 * Exibe um grid visual 3D da GeoEngine ao redor do player.
	 * Cores representam diferentes alturas do terreno.
	 * @param player O player que receberá a visualização
	 * @param radius O raio do grid em células (cada célula = 16 unidades)
	 */
	private void displayGeoGrid(Player player, int radius)
	{
		final ExServerPrimitive debug = player.getDebugPacket("GEO_GRID");
		debug.reset();
		
		final int playerX = player.getX();
		final int playerY = player.getY();
		final int playerZ = player.getZ();
		
		final int centerGeoX = GeoEngine.getGeoX(playerX);
		final int centerGeoY = GeoEngine.getGeoY(playerY);
		
		int minHeight = Integer.MAX_VALUE;
		int maxHeight = Integer.MIN_VALUE;
		
		for (int dx = -radius; dx <= radius; dx++)
		{
			for (int dy = -radius; dy <= radius; dy++)
			{
				final int geoX = centerGeoX + dx;
				final int geoY = centerGeoY + dy;
				
				if (!GeoEngine.getInstance().hasGeoPos(geoX, geoY))
					continue;
				
				final short height = GeoEngine.getInstance().getHeightNearest(geoX, geoY, playerZ);
				minHeight = Math.min(minHeight, height);
				maxHeight = Math.max(maxHeight, height);
			}
		}
		
		if (minHeight == Integer.MAX_VALUE)
		{
			player.sendMessage("Sem geodata disponível nesta área!");
			return;
		}
		
		final int heightRange = maxHeight - minHeight;
		
		int cellCount = 0;
		int blockedCount = 0;
		
		for (int dx = -radius; dx <= radius; dx++)
		{
			for (int dy = -radius; dy <= radius; dy++)
			{
				final int geoX = centerGeoX + dx;
				final int geoY = centerGeoY + dy;
				
				if (!GeoEngine.getInstance().hasGeoPos(geoX, geoY))
					continue;
				
				final short height = GeoEngine.getInstance().getHeightNearest(geoX, geoY, playerZ);
				final byte nswe = GeoEngine.getInstance().getNsweNearest(geoX, geoY, height);
				
				final int worldX = GeoEngine.getWorldX(geoX);
				final int worldY = GeoEngine.getWorldY(geoY);
				
				final Color cellColor = getHeightColor(height, minHeight, maxHeight);
				
				final int cellSize = 15;
				
				debug.addSquare(cellColor, worldX, worldY, height, cellSize);
				
				if (nswe == 0)
				{
					debug.addLine(Color.RED, worldX, worldY, height + 10, worldX + cellSize, worldY + cellSize, height + 10);
					debug.addLine(Color.RED, worldX + cellSize, worldY, height + 10, worldX, worldY + cellSize, height + 10);
					blockedCount++;
				}
				else if (nswe != GeoStructure.CELL_FLAG_ALL)
				{
					drawNsweIndicators(debug, worldX, worldY, height, cellSize, nswe);
				}
				
				if (dx == 0 && dy == 0)
				{
					debug.addSquare("VOCÊ", Color.CYAN, true, worldX, worldY, height + 20, cellSize);
					debug.addPoint("Player", Color.CYAN, true, playerX, playerY, playerZ);
				}
				
				cellCount++;
			}
		}
		
		drawGridLines(debug, centerGeoX, centerGeoY, radius, playerZ);
		
		player.sendMessage("=== GeoEngine Grid Debug ===");
		player.sendMessage("Células analisadas: " + cellCount);
		player.sendMessage("Células bloqueadas: " + blockedCount + " (" + (cellCount > 0 ? (blockedCount * 100 / cellCount) : 0) + "%)");
		player.sendMessage("Altura mínima: " + minHeight);
		player.sendMessage("Altura máxima: " + maxHeight);
		player.sendMessage("Variação de altura: " + heightRange);
		player.sendMessage("Posição GeoData: [" + centerGeoX + ", " + centerGeoY + "]");
		player.sendMessage("===========================");
		
		debug.sendTo(player);
	}
	
	/**
	 * Calcula a cor baseada na altura usando gradiente.
	 * Azul (baixo) -> Ciano -> Verde -> Amarelo -> Vermelho (alto)
	 * @param height A altura atual
	 * @param minHeight A altura mínima da área
	 * @param maxHeight A altura máxima da área
	 * @return A cor correspondente ao gradiente de altura
	 */
	private Color getHeightColor(int height, int minHeight, int maxHeight)
	{
		if (maxHeight == minHeight)
			return Color.GREEN;
		
		final float normalized = (float)(height - minHeight) / (maxHeight - minHeight);
		
		if (normalized < 0.25f)
		{
			final float t = normalized / 0.25f;
			return new Color(0, (int)(255 * t), 255);
		}
		else if (normalized < 0.5f)
		{
			final float t = (normalized - 0.25f) / 0.25f;
			return new Color(0, 255, (int)(255 * (1 - t)));
		}
		else if (normalized < 0.75f)
		{
			final float t = (normalized - 0.5f) / 0.25f;
			return new Color((int)(255 * t), 255, 0);
		}
		else
		{
			final float t = (normalized - 0.75f) / 0.25f;
			return new Color(255, (int)(255 * (1 - t)), 0);
		}
	}
	
	/**
	 * Desenha indicadores visuais para direções bloqueadas (NSWE).
	 * @param debug O pacote de primitivas para desenhar
	 * @param x Coordenada X world
	 * @param y Coordenada Y world
	 * @param z Coordenada Z world
	 * @param size Tamanho da célula
	 * @param nswe Flags de direção NSWE
	 */
	private void drawNsweIndicators(ExServerPrimitive debug, int x, int y, int z, int size, byte nswe)
	{
		final int offset = 5;
		
		if ((nswe & GeoStructure.CELL_FLAG_N) == 0)
			debug.addLine(Color.ORANGE, x, y, z + offset, x + size, y, z + offset);
		
		if ((nswe & GeoStructure.CELL_FLAG_S) == 0)
			debug.addLine(Color.ORANGE, x, y + size, z + offset, x + size, y + size, z + offset);
		
		if ((nswe & GeoStructure.CELL_FLAG_W) == 0)
			debug.addLine(Color.ORANGE, x, y, z + offset, x, y + size, z + offset);
		
		if ((nswe & GeoStructure.CELL_FLAG_E) == 0)
			debug.addLine(Color.ORANGE, x + size, y, z + offset, x + size, y + size, z + offset);
	}
	
	/**
	 * Desenha linhas de grade para melhor visualização do terreno.
	 * @param debug O pacote de primitivas para desenhar
	 * @param centerGeoX Coordenada GeoX central
	 * @param centerGeoY Coordenada GeoY central
	 * @param radius Raio do grid
	 * @param baseZ Altura base de referência
	 */
	private void drawGridLines(ExServerPrimitive debug, int centerGeoX, int centerGeoY, int radius, int baseZ)
	{
		for (int dx = -radius; dx <= radius; dx++)
		{
			final int geoXStart = centerGeoX + dx;
			final int geoYStart = centerGeoY - radius;
			final int geoYEnd = centerGeoY + radius;
			
			if (!GeoEngine.getInstance().hasGeoPos(geoXStart, geoYStart) || 
				!GeoEngine.getInstance().hasGeoPos(geoXStart, geoYEnd))
				continue;
			
			final int worldX = GeoEngine.getWorldX(geoXStart);
			final int worldYStart = GeoEngine.getWorldY(geoYStart);
			final int worldYEnd = GeoEngine.getWorldY(geoYEnd);
			
			final short zStart = GeoEngine.getInstance().getHeightNearest(geoXStart, geoYStart, baseZ);
			final short zEnd = GeoEngine.getInstance().getHeightNearest(geoXStart, geoYEnd, baseZ);
			
			if (dx % 2 == 0)
				debug.addLine(new Color(128, 128, 128, 100), worldX, worldYStart, zStart, worldX, worldYEnd, zEnd);
		}
		
		for (int dy = -radius; dy <= radius; dy++)
		{
			final int geoXStart = centerGeoX - radius;
			final int geoXEnd = centerGeoX + radius;
			final int geoY = centerGeoY + dy;
			
			if (!GeoEngine.getInstance().hasGeoPos(geoXStart, geoY) || 
				!GeoEngine.getInstance().hasGeoPos(geoXEnd, geoY))
				continue;
			
			final int worldXStart = GeoEngine.getWorldX(geoXStart);
			final int worldXEnd = GeoEngine.getWorldX(geoXEnd);
			final int worldY = GeoEngine.getWorldY(geoY);
			
			final short zStart = GeoEngine.getInstance().getHeightNearest(geoXStart, geoY, baseZ);
			final short zEnd = GeoEngine.getInstance().getHeightNearest(geoXEnd, geoY, baseZ);
			
			if (dy % 2 == 0)
				debug.addLine(new Color(128, 128, 128, 100), worldXStart, worldY, zStart, worldXEnd, worldY, zEnd);
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
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
package ext.mods.gameserver.data.manager;

import java.util.ArrayList;
import java.util.List;

import ext.mods.commons.logging.CLogger;

import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.instance.Fence;

/**
 * Fence table to manage {@link Fence} spawn/despawn.
 */
public class FenceManager
{
	private static final CLogger LOGGER = new CLogger(FenceManager.class.getName());
	
	/**
	 * Description of each fence dimension parameters.
	 * <ul>
	 * <li>_offset parameter says, what is the fence position compared to geodata grid.</li>
	 * <li>_geoDataSize parameter says, what is the raw fence size in geodata coordinates (cells).</li>
	 * </ul>
	 */
	private enum FenceSize
	{
		
		SIZE_100(8, 11),
		SIZE_200(0, 18),
		SIZE_300(0, 24),
		SIZE_400(0, 30),
		SIZE_500(0, 36),
		SIZE_600(0, 42),
		SIZE_700(8, 49),
		SIZE_800(8, 55),
		SIZE_900(8, 61),
		SIZE_1000(0, 68);
		
		final int _offset;
		final int _geoDataSize;
		
		private FenceSize(int offset, int geoDataSize)
		{
			_offset = offset;
			_geoDataSize = geoDataSize;
		}
	}
	
	private final List<Fence> _fences = new ArrayList<>();
	
	protected FenceManager()
	{
	}
	
	/**
	 * Returns list of all fences spawned in the world.
	 * @return List<Fence> : List of all fences.
	 */
	public final List<Fence> getFences()
	{
		return _fences;
	}
	
	/**
	 * Adds {@link Fence} to the world.
	 * @param x : Spawn X world coordinate.
	 * @param y : Spawn Y world coordinate.
	 * @param z : Spawn Z world coordinate.
	 * @param type : Type of the fence. 1..corner stones only, 2..fence + corner stones
	 * @param sizeX : Size of the {@link Fence} in X direction.
	 * @param sizeY : Size of the {@link Fence} in Y direction.
	 * @param height : The height of {@link Fence}.
	 * @return The newly created Fence object.
	 */
	public final Fence addFence(int x, int y, int z, int type, int sizeX, int sizeY, int height)
	{
		if (World.isOutOfWorld(x, x + sizeX, y, y + sizeY))
		{
			LOGGER.error("Fence coords are outside of world.");
			return null;
		}
		
		final FenceSize fsx = getFenceSize(sizeX);
		final FenceSize fsy = getFenceSize(sizeY);
		
		if (fsx == null || fsy == null)
		{
			LOGGER.warn("Unknown dimensions for fence, x={} y={}.", sizeX, sizeY);
			return null;
		}
		
		x = x & 0xFFFFFFF0 + fsx._offset;
		y = y & 0xFFFFFFF0 + fsy._offset;
		
		final int sx = fsx._geoDataSize;
		final int sy = fsy._geoDataSize;
		
		int geoX = GeoEngine.getGeoX(x) - sx / 2;
		int geoY = GeoEngine.getGeoY(y) - sy / 2;
		int geoZ = GeoEngine.getInstance().getHeight(x, y, z);
		
		final boolean[][] inside = new boolean[sx][sy];
		for (int ix = 1; ix < sx - 1; ix++)
			for (int iy = 1; iy < sy - 1; iy++)
				if (type == 2)
					inside[ix][iy] = (ix < 3 || ix >= sx - 3) || (iy < 3 || iy >= sy - 3);
				else
					inside[ix][iy] = (ix < 3 || ix >= sx - 3) && (iy < 3 || iy >= sy - 3);
		final byte[][] geoData = GeoEngine.calculateGeoObject(inside);
		
		Fence fence = new Fence(type, sizeX, sizeY, height, geoX, geoY, geoZ, geoData);
		
		fence.spawnMe(x, y, z);
		
		GeoEngine.getInstance().addGeoObject(fence);
		_fences.add(fence);
		
		return fence;
	}
	
	/**
	 * Remove given {@link Fence} from the world.
	 * @param fence : {@link Fence} to be removed.
	 */
	public final void removeFence(Fence fence)
	{
		fence.decayMe();
		
		GeoEngine.getInstance().removeGeoObject(fence);
		_fences.remove(fence);
	}
	
	/**
	 * Returns the size template of {@link Fence} based on given size value.
	 * @param size : Requested size.
	 * @return {@link FenceSize} : Size of {@link Fence} in particular dimension.
	 */
	private static final FenceSize getFenceSize(int size)
	{
		if (size < 199)
			return FenceSize.SIZE_100;
		
		if (size < 299)
			return FenceSize.SIZE_200;
		
		if (size < 399)
			return FenceSize.SIZE_300;
		
		if (size < 499)
			return FenceSize.SIZE_400;
		
		if (size < 599)
			return FenceSize.SIZE_500;
		
		if (size < 699)
			return FenceSize.SIZE_600;
		
		if (size < 799)
			return FenceSize.SIZE_700;
		
		if (size < 899)
			return FenceSize.SIZE_800;
		
		if (size < 999)
			return FenceSize.SIZE_900;
		
		if (size < 1099)
			return FenceSize.SIZE_1000;
		
		return null;
	}
	
	public static FenceManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final FenceManager INSTANCE = new FenceManager();
	}
}
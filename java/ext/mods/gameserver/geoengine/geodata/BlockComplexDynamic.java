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
package ext.mods.gameserver.geoengine.geodata;

import java.util.LinkedList;
import java.util.List;

public final class BlockComplexDynamic extends BlockComplex implements IBlockDynamic
{
	private final int _bx;
	private final int _by;
	private final byte[] _original;
	private final List<IGeoObject> _objects;
	
	/**
	 * Creates {@link BlockComplexDynamic}.
	 * @param bx : Block X coordinate.
	 * @param by : Block Y coordinate.
	 * @param block : The original FlatBlock to create a dynamic version from.
	 */
	public BlockComplexDynamic(int bx, int by, BlockFlat block)
	{
		final byte nswe = block._nswe;
		final byte heightLow = (byte) (block._height & 0x00FF);
		final byte heightHigh = (byte) (block._height >> 8);
		
		_buffer = new byte[GeoStructure.BLOCK_CELLS * 3];
		
		for (int i = 0; i < GeoStructure.BLOCK_CELLS; i++)
		{
			_buffer[i * 3] = nswe;
			
			_buffer[i * 3 + 1] = heightLow;
			_buffer[i * 3 + 2] = heightHigh;
		}
		
		_bx = bx;
		_by = by;
		
		_original = new byte[GeoStructure.BLOCK_CELLS * 3];
		System.arraycopy(_buffer, 0, _original, 0, GeoStructure.BLOCK_CELLS * 3);
		
		_objects = new LinkedList<>();
	}
	
	/**
	 * Creates {@link BlockComplexDynamic}.
	 * @param bx : Block X coordinate.
	 * @param by : Block Y coordinate.
	 * @param block : The original ComplexBlock to create a dynamic version from.
	 */
	public BlockComplexDynamic(int bx, int by, BlockComplex block)
	{
		_buffer = block._buffer;
		block._buffer = null;
		
		_bx = bx;
		_by = by;
		
		_original = new byte[GeoStructure.BLOCK_CELLS * 3];
		System.arraycopy(_buffer, 0, _original, 0, GeoStructure.BLOCK_CELLS * 3);
		
		_objects = new LinkedList<>();
	}
	
	@Override
	public final short getHeightNearest(int geoX, int geoY, int worldZ, IGeoObject ignore)
	{
		byte[] buffer = _objects.contains(ignore) ? _original : _buffer;
		
		final int index = ((geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y + (geoY % GeoStructure.BLOCK_CELLS_Y)) * 3;
		
		return (short) (buffer[index + 1] & 0x00FF | buffer[index + 2] << 8);
	}
	
	@Override
	public final byte getNsweNearest(int geoX, int geoY, int worldZ, IGeoObject ignore)
	{
		byte[] buffer = _objects.contains(ignore) ? _original : _buffer;
		
		final int index = ((geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y + (geoY % GeoStructure.BLOCK_CELLS_Y)) * 3;
		
		return buffer[index];
	}
	
	@Override
	public final int getIndexAbove(int geoX, int geoY, int worldZ, IGeoObject ignore)
	{
		byte[] buffer = _objects.contains(ignore) ? _original : _buffer;
		
		final int index = ((geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y + (geoY % GeoStructure.BLOCK_CELLS_Y)) * 3;
		
		final int height = buffer[index + 1] & 0x00FF | buffer[index + 2] << 8;
		
		return height > worldZ ? index : -1;
	}
	
	@Override
	public final int getIndexBelow(int geoX, int geoY, int worldZ, IGeoObject ignore)
	{
		byte[] buffer = _objects.contains(ignore) ? _original : _buffer;
		
		final int index = ((geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y + (geoY % GeoStructure.BLOCK_CELLS_Y)) * 3;
		
		final int height = buffer[index + 1] & 0x00FF | buffer[index + 2] << 8;
		
		return height < worldZ ? index : -1;
	}
	
	@Override
	public final short getHeight(int index, IGeoObject ignore)
	{
		byte[] buffer = _objects.contains(ignore) ? _original : _buffer;
		
		return (short) (buffer[index + 1] & 0x00FF | buffer[index + 2] << 8);
	}
	
	@Override
	public final byte getNswe(int index, IGeoObject ignore)
	{
		byte[] buffer = _objects.contains(ignore) ? _original : _buffer;
		
		return buffer[index];
	}
	
	@Override
	public final synchronized void addGeoObject(IGeoObject object)
	{
		if (_objects.add(object))
			update();
	}
	
	@Override
	public final synchronized void removeGeoObject(IGeoObject object)
	{
		if (_objects.remove(object))
			update();
	}
	
	/**
	 * Resets current geodata to original state and than apply all {@link IGeoObject}'s modifications.
	 */
	private final void update()
	{
		System.arraycopy(_original, 0, _buffer, 0, GeoStructure.BLOCK_CELLS * 3);
		
		final int minBX = _bx * GeoStructure.BLOCK_CELLS_X;
		final int minBY = _by * GeoStructure.BLOCK_CELLS_Y;
		final int maxBX = minBX + GeoStructure.BLOCK_CELLS_X;
		final int maxBY = minBY + GeoStructure.BLOCK_CELLS_Y;
		
		for (IGeoObject object : _objects)
		{
			final int minOX = object.getGeoX();
			final int minOY = object.getGeoY();
			final int minOZ = object.getGeoZ();
			final int maxOZ = minOZ + object.getHeight();
			final byte[][] geoData = object.getObjectGeoData();
			
			final int minGX = Math.max(minBX, minOX);
			final int minGY = Math.max(minBY, minOY);
			final int maxGX = Math.min(maxBX, minOX + geoData.length);
			final int maxGY = Math.min(maxBY, minOY + geoData[0].length);
			
			for (int gx = minGX; gx < maxGX; gx++)
			{
				for (int gy = minGY; gy < maxGY; gy++)
				{
					final byte objNswe = geoData[gx - minOX][gy - minOY];
					
					if (objNswe == GeoStructure.CELL_FLAG_ALL)
						continue;
					
					final int ib = ((gx - minBX) * GeoStructure.BLOCK_CELLS_Y + (gy - minBY)) * 3;
					
					if (_buffer[ib + 1] != _original[ib + 1] || _buffer[ib + 2] != _original[ib + 2])
						continue;
					
					if (objNswe == GeoStructure.CELL_FLAG_NONE)
					{
						
						_buffer[ib] = GeoStructure.CELL_FLAG_NONE;
						
						_buffer[ib + 1] = (byte) (maxOZ & 0x00FF);
						_buffer[ib + 2] = (byte) (maxOZ >> 8);
					}
					else
					{
						
						short z = getHeight(ib, null);
						if (Math.abs(z - minOZ) > GeoStructure.CELL_IGNORE_HEIGHT)
							continue;
						
						_buffer[ib] &= objNswe;
					}
				}
			}
		}
	}
}
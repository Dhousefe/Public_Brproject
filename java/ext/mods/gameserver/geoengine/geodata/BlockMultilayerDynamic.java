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

public final class BlockMultilayerDynamic extends BlockMultilayer implements IBlockDynamic
{
	private final int _bx;
	private final int _by;
	private final byte[] _original;
	private final List<IGeoObject> _objects;
	
	/**
	 * Creates {@link BlockMultilayerDynamic}.
	 * @param bx : Block X coordinate.
	 * @param by : Block Y coordinate.
	 * @param block : The original MultilayerBlock to create a dynamic version from.
	 */
	public BlockMultilayerDynamic(int bx, int by, BlockMultilayer block)
	{
		_buffer = block._buffer;
		block._buffer = null;
		
		_bx = bx;
		_by = by;
		
		_original = new byte[_buffer.length];
		System.arraycopy(_buffer, 0, _original, 0, _buffer.length);
		
		_objects = new LinkedList<>();
	}
	
	@Override
	public short getHeightNearest(int geoX, int geoY, int worldZ, IGeoObject ignore)
	{
		byte[] buffer = _objects.contains(ignore) ? _original : _buffer;
		
		final int index = getIndexNearest(geoX, geoY, worldZ, ignore);
		
		return (short) (buffer[index + 1] & 0x00FF | buffer[index + 2] << 8);
	}
	
	@Override
	public byte getNsweNearest(int geoX, int geoY, int worldZ, IGeoObject ignore)
	{
		byte[] buffer = _objects.contains(ignore) ? _original : _buffer;
		
		final int index = getIndexNearest(geoX, geoY, worldZ, ignore);
		
		return buffer[index];
	}
	
	@Override
	public final int getIndexNearest(int geoX, int geoY, int worldZ, IGeoObject ignore)
	{
		byte[] buffer = _objects.contains(ignore) ? _original : _buffer;
		
		int index = 0;
		for (int i = 0; i < (geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y + (geoY % GeoStructure.BLOCK_CELLS_Y); i++)
		{
			index += buffer[index] * 3 + 1;
		}
		
		byte layers = buffer[index++];
		
		int limit = Integer.MAX_VALUE;
		while (layers-- > 0)
		{
			final int height = buffer[index + 1] & 0x00FF | buffer[index + 2] << 8;
			
			final int distance = Math.abs(height - worldZ);
			if (distance > limit)
				break;
			
			limit = distance;
			index += 3;
		}
		
		return index - 3;
	}
	
	@Override
	public final int getIndexAbove(int geoX, int geoY, int worldZ, IGeoObject ignore)
	{
		byte[] buffer = _objects.contains(ignore) ? _original : _buffer;
		
		int index = 0;
		for (int i = 0; i < (geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y + (geoY % GeoStructure.BLOCK_CELLS_Y); i++)
		{
			index += buffer[index] * 3 + 1;
		}
		
		byte layers = buffer[index++];
		index += (layers - 1) * 3;
		
		while (layers-- > 0)
		{
			final int height = buffer[index + 1] & 0x00FF | buffer[index + 2] << 8;
			
			if (height > worldZ)
				return index;
			
			index -= 3;
		}
		
		return -1;
	}
	
	@Override
	public final int getIndexBelow(int geoX, int geoY, int worldZ, IGeoObject ignore)
	{
		byte[] buffer = _objects.contains(ignore) ? _original : _buffer;
		
		int index = 0;
		for (int i = 0; i < (geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y + (geoY % GeoStructure.BLOCK_CELLS_Y); i++)
		{
			index += buffer[index] * 3 + 1;
		}
		
		byte layers = buffer[index++];
		
		while (layers-- > 0)
		{
			final int height = buffer[index + 1] & 0x00FF | buffer[index + 2] << 8;
			
			if (height < worldZ)
				return index;
			
			index += 3;
		}
		
		return -1;
	}
	
	@Override
	public short getHeight(int index, IGeoObject ignore)
	{
		byte[] buffer = _objects.contains(ignore) ? _original : _buffer;
		
		return (short) (buffer[index + 1] & 0x00FF | buffer[index + 2] << 8);
	}
	
	@Override
	public byte getNswe(int index, IGeoObject ignore)
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
		System.arraycopy(_original, 0, _buffer, 0, _original.length);
		
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
					
					int ib = getIndexNearest(gx, gy, minOZ, null);
					
					if (_buffer[ib + 1] != _original[ib + 1] || _buffer[ib + 2] != _original[ib + 2])
						continue;
					
					if (objNswe == GeoStructure.CELL_FLAG_NONE)
					{
						
						_buffer[ib] = GeoStructure.CELL_FLAG_NONE;
						
						int z = maxOZ;
						int i = getIndexAbove(gx, gy, minOZ, null);
						if (i != -1)
						{
							int az = getHeight(i, null);
							if (az <= maxOZ)
								z = az - GeoStructure.CELL_IGNORE_HEIGHT;
						}
						
						_buffer[ib + 1] = (byte) (z & 0x00FF);
						_buffer[ib + 2] = (byte) (z >> 8);
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
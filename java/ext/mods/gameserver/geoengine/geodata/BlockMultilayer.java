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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import ext.mods.gameserver.enums.GeoType;

public class BlockMultilayer extends ABlock
{
	private static final int MAX_LAYERS = Byte.MAX_VALUE;
	
	private static ByteBuffer _temp;
	
	/**
	 * Initializes the temporary buffer.
	 */
	public static final void initialize()
	{
		_temp = ByteBuffer.allocate(GeoStructure.BLOCK_CELLS * MAX_LAYERS * 3);
		_temp.order(ByteOrder.LITTLE_ENDIAN);
	}
	
	/**
	 * Releases temporary buffer.
	 */
	public static final void release()
	{
		_temp = null;
	}
	
	protected byte[] _buffer;
	
	/**
	 * Implicit constructor for children class.
	 */
	protected BlockMultilayer()
	{
		_buffer = null;
	}
	
	/**
	 * Creates MultilayerBlock.
	 * @param bb : Input byte buffer.
	 * @param type : The type of loaded geodata.
	 */
	public BlockMultilayer(ByteBuffer bb, GeoType type)
	{
		for (int cell = 0; cell < GeoStructure.BLOCK_CELLS; cell++)
		{
			final byte layers = type != GeoType.L2OFF ? bb.get() : (byte) bb.getShort();
			
			if (layers <= 0 || layers > MAX_LAYERS)
				throw new RuntimeException("Invalid layer count for MultilayerBlock");
			
			_temp.put(layers);
			
			for (byte layer = 0; layer < layers; layer++)
			{
				short data = bb.getShort();
				
				_temp.put((byte) (data & 0x000F));
				_temp.putShort((short) ((short) (data & 0xFFF0) >> 1));
			}
		}
		
		_buffer = Arrays.copyOf(_temp.array(), _temp.position());
		
		_temp.clear();
	}
	
	@Override
	public final boolean hasGeoPos()
	{
		return true;
	}
	
	@Override
	public short getHeightNearest(int geoX, int geoY, int worldZ, IGeoObject ignore)
	{
		final int index = getIndexNearest(geoX, geoY, worldZ, ignore);
		
		return (short) (_buffer[index + 1] & 0x00FF | _buffer[index + 2] << 8);
	}
	
	@Override
	public byte getNsweNearest(int geoX, int geoY, int worldZ, IGeoObject ignore)
	{
		final int index = getIndexNearest(geoX, geoY, worldZ, ignore);
		
		return _buffer[index];
	}
	
	@Override
	public int getIndexNearest(int geoX, int geoY, int worldZ, IGeoObject ignore)
	{
		int index = 0;
		for (int i = 0; i < (geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y + (geoY % GeoStructure.BLOCK_CELLS_Y); i++)
		{
			index += _buffer[index] * 3 + 1;
		}
		
		byte layers = _buffer[index++];
		
		int limit = Integer.MAX_VALUE;
		while (layers-- > 0)
		{
			final int height = _buffer[index + 1] & 0x00FF | _buffer[index + 2] << 8;
			
			final int distance = Math.abs(height - worldZ);
			if (distance > limit)
				break;
			
			limit = distance;
			index += 3;
		}
		
		return index - 3;
	}
	
	@Override
	public int getIndexAbove(int geoX, int geoY, int worldZ, IGeoObject ignore)
	{
		int index = 0;
		for (int i = 0; i < (geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y + (geoY % GeoStructure.BLOCK_CELLS_Y); i++)
		{
			index += _buffer[index] * 3 + 1;
		}
		
		byte layers = _buffer[index++];
		index += (layers - 1) * 3;
		
		while (layers-- > 0)
		{
			final int height = _buffer[index + 1] & 0x00FF | _buffer[index + 2] << 8;
			
			if (height > worldZ)
				return index;
			
			index -= 3;
		}
		
		return -1;
	}
	
	@Override
	public int getIndexBelow(int geoX, int geoY, int worldZ, IGeoObject ignore)
	{
		int index = 0;
		for (int i = 0; i < (geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y + (geoY % GeoStructure.BLOCK_CELLS_Y); i++)
		{
			index += _buffer[index] * 3 + 1;
		}
		
		byte layers = _buffer[index++];
		
		while (layers-- > 0)
		{
			final int height = _buffer[index + 1] & 0x00FF | _buffer[index + 2] << 8;
			
			if (height < worldZ)
				return index;
			
			index += 3;
		}
		
		return -1;
	}
	
	@Override
	public short getHeight(int index, IGeoObject ignore)
	{
		return (short) (_buffer[index + 1] & 0x00FF | _buffer[index + 2] << 8);
	}
	
	@Override
	public byte getNswe(int index, IGeoObject ignore)
	{
		return _buffer[index];
	}
}
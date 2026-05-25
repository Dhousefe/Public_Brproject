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

public class BlockComplex extends ABlock
{
	protected byte[] _buffer;
	
	/**
	 * Implicit constructor for children class.
	 */
	protected BlockComplex()
	{
		_buffer = null;
	}
	
	/**
	 * Creates ComplexBlock.
	 * @param bb : Input byte buffer.
	 */
	public BlockComplex(ByteBuffer bb)
	{
		_buffer = new byte[GeoStructure.BLOCK_CELLS * 3];
		
		for (int i = 0; i < GeoStructure.BLOCK_CELLS; i++)
		{
			short data = bb.getShort();
			
			_buffer[i * 3] = (byte) (data & 0x000F);
			
			data = (short) ((short) (data & 0xFFF0) >> 1);
			_buffer[i * 3 + 1] = (byte) (data & 0x00FF);
			_buffer[i * 3 + 2] = (byte) (data >> 8);
		}
	}
	
	@Override
	public final boolean hasGeoPos()
	{
		return true;
	}
	
	@Override
	public short getHeightNearest(int geoX, int geoY, int worldZ, IGeoObject ignore)
	{
		final int index = ((geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y + (geoY % GeoStructure.BLOCK_CELLS_Y)) * 3;
		
		return (short) (_buffer[index + 1] & 0x00FF | _buffer[index + 2] << 8);
	}
	
	@Override
	public byte getNsweNearest(int geoX, int geoY, int worldZ, IGeoObject ignore)
	{
		final int index = ((geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y + (geoY % GeoStructure.BLOCK_CELLS_Y)) * 3;
		
		return _buffer[index];
	}
	
	@Override
	public final int getIndexNearest(int geoX, int geoY, int worldZ, IGeoObject ignore)
	{
		return ((geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y + (geoY % GeoStructure.BLOCK_CELLS_Y)) * 3;
	}
	
	@Override
	public int getIndexAbove(int geoX, int geoY, int worldZ, IGeoObject ignore)
	{
		final int index = ((geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y + (geoY % GeoStructure.BLOCK_CELLS_Y)) * 3;
		
		final int height = _buffer[index + 1] & 0x00FF | _buffer[index + 2] << 8;
		
		return height > worldZ ? index : -1;
	}
	
	@Override
	public int getIndexBelow(int geoX, int geoY, int worldZ, IGeoObject ignore)
	{
		final int index = ((geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y + (geoY % GeoStructure.BLOCK_CELLS_Y)) * 3;
		
		final int height = _buffer[index + 1] & 0x00FF | _buffer[index + 2] << 8;
		
		return height < worldZ ? index : -1;
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
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
package ext.mods.gameserver.model.actor.instance;

import ext.mods.gameserver.geoengine.geodata.IGeoObject;
import ext.mods.gameserver.idfactory.IdFactory;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.ExColosseumFenceInfo;

public class Fence extends WorldObject implements IGeoObject
{
	private static final int FENCE_HEIGHT = 24;
	
	private final int _type;
	private final int _sizeX;
	private final int _sizeY;
	private final int _height;
	
	private final L2DummyFence _object2;
	private final L2DummyFence _object3;
	
	private final int _geoX;
	private final int _geoY;
	private final int _geoZ;
	private final byte[][] _geoData;
	
	public Fence(int type, int sizeZ, int sizeY, int height, int geoX, int geoY, int geoZ, byte[][] geoData)
	{
		super(IdFactory.getInstance().getNextId());
		
		_type = type;
		_sizeX = sizeZ;
		_sizeY = sizeY;
		_height = height * FENCE_HEIGHT;
		
		_object2 = height > 1 ? new L2DummyFence(this) : null;
		_object3 = height > 2 ? new L2DummyFence(this) : null;
		
		_geoX = geoX;
		_geoY = geoY;
		_geoZ = geoZ;
		_geoData = geoData;
	}
	
	public int getType()
	{
		return _type;
	}
	
	public int getSizeX()
	{
		return _sizeX;
	}
	
	public int getSizeY()
	{
		return _sizeY;
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		if (_object2 != null)
			_object2.spawnMe(getPosition());
		if (_object3 != null)
			_object3.spawnMe(getPosition());
	}
	
	@Override
	public void decayMe()
	{
		if (_object2 != null)
			_object2.decayMe();
		if (_object3 != null)
			_object3.decayMe();
		
		super.decayMe();
	}
	
	@Override
	public void sendInfo(Player player)
	{
		player.sendPacket(new ExColosseumFenceInfo(getObjectId(), this));
	}
	
	@Override
	public int getGeoX()
	{
		return _geoX;
	}
	
	@Override
	public int getGeoY()
	{
		return _geoY;
	}
	
	@Override
	public int getGeoZ()
	{
		return _geoZ;
	}
	
	@Override
	public int getHeight()
	{
		return _height;
	}
	
	@Override
	public byte[][] getObjectGeoData()
	{
		return _geoData;
	}
	
	/**
	 * Dummy fence class in order to spawn/delete multi-layer fences correctly.
	 */
	protected class L2DummyFence extends WorldObject
	{
		private final Fence _fence;
		
		public L2DummyFence(Fence fence)
		{
			super(IdFactory.getInstance().getNextId());
			
			_fence = fence;
		}
		
		@Override
		public void sendInfo(Player player)
		{
			player.sendPacket(new ExColosseumFenceInfo(getObjectId(), _fence));
		}
	}
}
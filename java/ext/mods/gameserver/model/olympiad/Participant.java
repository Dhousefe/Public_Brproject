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
package ext.mods.gameserver.model.olympiad;

import ext.mods.commons.data.StatSet;

import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;

/**
 * A model containing all informations related to a single {@link Olympiad} Participant.
 */
public final class Participant
{
	private final int _objectId;
	private final String _name;
	private final int _side;
	private final int _baseClass;
	private final StatSet _set;
	
	private boolean _isDisconnected = false;
	private boolean _isDefecting = false;
	private Player _player;
	
	public Participant(Player player, int side)
	{
		_objectId = player.getObjectId();
		_player = player;
		_name = player.getName();
		_side = side;
		_baseClass = player.getBaseClass();
		_set = Olympiad.getInstance().getNobleStats(_objectId);
	}
	
	public Participant(int objectId, int side)
	{
		_objectId = objectId;
		_player = null;
		_name = "-";
		_side = side;
		_baseClass = 0;
		_set = null;
	}
	
	public int getObjectId()
	{
		return _objectId;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public int getSide()
	{
		return _side;
	}
	
	public int getBaseClass()
	{
		return _baseClass;
	}
	
	public StatSet getStatSet()
	{
		return _set;
	}
	
	public boolean isDisconnected()
	{
		return _isDisconnected;
	}
	
	public void setDisconnection(boolean isDisconnected)
	{
		_isDisconnected = isDisconnected;
	}
	
	public boolean isDefecting()
	{
		return _isDefecting;
	}
	
	public void setDefection(boolean isDefecting)
	{
		_isDefecting = isDefecting;
	}
	
	public Player getPlayer()
	{
		return _player;
	}
	
	public void setPlayer(Player player)
	{
		_player = player;
	}
	
	public final void updatePlayer()
	{
		if (_player == null || !_player.isOnline())
			_player = World.getInstance().getPlayer(_objectId);
	}
	
	public final void updateStat(String statName, int increment)
	{
		_set.set(statName, Math.max(_set.getInteger(statName) + increment, 0));
	}
}
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
package ext.mods.gameserver.model.entity;

import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.Location;

/**
 * This class hold important player informations, which will be restored on duel end.
 */
public class PlayerCondition
{
	private Player _player;
	
	private double _hp;
	private double _mp;
	private double _cp;
	
	private Location _loc;
	
	public PlayerCondition(Player player, boolean partyDuel)
	{
		if (player == null)
			return;
		
		_player = player;
		_hp = _player.getStatus().getHp();
		_mp = _player.getStatus().getMp();
		_cp = _player.getStatus().getCp();
		
		if (partyDuel)
			_loc = _player.getPosition().clone();
		
		_player.storeEffect(true);
	}
	
	public void restoreCondition(boolean abnormalEnd)
	{
		if (_loc != null)
			_player.teleportTo(_loc, 0);
		
		if (abnormalEnd)
			return;
		
		_player.getStatus().setCpHpMp(_cp, _hp, _mp);
		
		_player.stopAllEffects();
		_player.restoreEffects();
	}
	
	public Player getPlayer()
	{
		return _player;
	}
}
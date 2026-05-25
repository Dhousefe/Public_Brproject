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
package ext.mods.gameserver.model.entity.events.capturetheflag;

import ext.mods.commons.pool.ThreadPool;
import ext.mods.commons.random.Rnd;

import ext.mods.Config;
import ext.mods.gameserver.enums.TeamType;
import ext.mods.gameserver.enums.duels.DuelState;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.Summon;

public class CTFEventTeleporter implements Runnable
{
	private Player _player = null;
	
	private int[] _coordinates = new int[3];
	
	private boolean _adminRemove = false;
	
	public CTFEventTeleporter(Player player, int[] coordinates, boolean fastSchedule, boolean adminRemove)
	{
		_player = player;
		_coordinates = coordinates;
		_adminRemove = adminRemove;
		
		ThreadPool.schedule(this, fastSchedule ? 0 : (CTFEvent.getInstance().isStarted() ? Config.CTF_EVENT_RESPAWN_TELEPORT_DELAY : Config.CTF_EVENT_START_LEAVE_TELEPORT_DELAY) * 1000);
	}
	
	@Override
	public void run()
	{
		if (_player == null)
			return;
		
		Summon summon = _player.getSummon();
		
		if (summon != null)
			summon.unSummon(_player);
		
		if ((Config.CTF_EVENT_EFFECTS_REMOVAL == 0) || ((Config.CTF_EVENT_EFFECTS_REMOVAL == 1) && ((_player.getTeam() == TeamType.NONE) || (_player.isInDuel() && (_player.getDuelState() != DuelState.INTERRUPTED)))))
			_player.stopAllEffectsExceptThoseThatLastThroughDeath();
		
		if (_player.isInDuel())
			_player.setDuelState(DuelState.INTERRUPTED);
		
		_player.doRevive();
		
		_player.teleportTo((_coordinates[0] + Rnd.get(101)) - 50, (_coordinates[1] + Rnd.get(101)) - 50, _coordinates[2], 0);
		
		if (CTFEvent.getInstance().playerIsCarrier(_player))
		{
			CTFEvent.getInstance().removeFlagCarrier(_player);
			CTFEvent.getInstance().sysMsgToAllParticipants("The " + CTFEvent.getInstance().getParticipantEnemyTeam(_player.getObjectId()).getName() + " flag has been returned!");
		}
		
		if (CTFEvent.getInstance().isStarted() && !_adminRemove)
		{
			int teamId = CTFEvent.getInstance().getParticipantTeamId(_player.getObjectId()) + 1;
			switch (teamId)
			{
				case 0:
					_player.setTeam(TeamType.NONE);
					break;
				case 1:
					_player.setTeam(TeamType.BLUE);
					break;
				case 2:
					_player.setTeam(TeamType.RED);
					break;
			}
		}
		else
			_player.setTeam(TeamType.NONE);
		
		_player.getStatus().setCp(_player.getStatus().getMaxCp());
		_player.getStatus().setHp(_player.getStatus().getMaxHp());
		_player.getStatus().setMp(_player.getStatus().getMaxMp());
		
		_player.getStatus().broadcastStatusUpdate();
		_player.broadcastUserInfo();
	}
}
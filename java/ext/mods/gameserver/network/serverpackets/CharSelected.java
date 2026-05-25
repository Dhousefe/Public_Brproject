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
package ext.mods.gameserver.network.serverpackets;

import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.taskmanager.GameTimeTaskManager;

public class CharSelected extends L2GameServerPacket
{
	private final Player _player;
	private final int _sessionId;
	
	public CharSelected(Player player, int sessionId)
	{
		_player = player;
		_sessionId = sessionId;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x15);
		
		writeS(_player.getName());
		writeD(_player.getObjectId());
		writeS(_player.getTitle());
		writeD(_sessionId);
		writeD(_player.getClanId());
		
		writeD(0x00);
		
		writeD(_player.getAppearance().getSex().ordinal());
		writeD(_player.getRace().ordinal());
		writeD(_player.getClassId().getId());
		
		writeD(0x01);
		
		writeD(_player.getX());
		writeD(_player.getY());
		writeD(_player.getZ());
		writeF(_player.getStatus().getHp());
		writeF(_player.getStatus().getMp());
		writeD(_player.getStatus().getSp());
		writeQ(_player.getStatus().getExp());
		writeD(_player.getStatus().getLevel());
		writeD(_player.getKarma());
		writeD(_player.getPkKills());
		writeD(_player.getStatus().getINT());
		writeD(_player.getStatus().getSTR());
		writeD(_player.getStatus().getCON());
		writeD(_player.getStatus().getMEN());
		writeD(_player.getStatus().getDEX());
		writeD(_player.getStatus().getWIT());
		
		for (int i = 0; i < 30; i++)
		{
			writeD(0x00);
		}
		
		writeD(0x00);
		writeD(0x00);
		
		writeD(GameTimeTaskManager.getInstance().getGameTime());
		
		writeD(0x00);
		
		writeD(_player.getClassId().getId());
		
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);
	}
}
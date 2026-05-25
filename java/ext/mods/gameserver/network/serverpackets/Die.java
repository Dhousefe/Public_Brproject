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

import ext.mods.gameserver.data.manager.CastleManager;
import ext.mods.gameserver.data.manager.ClanHallManager;
import ext.mods.gameserver.enums.SiegeSide;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Monster;
import ext.mods.gameserver.model.entity.events.capturetheflag.CTFEvent;
import ext.mods.gameserver.model.entity.events.deathmatch.DMEvent;
import ext.mods.gameserver.model.entity.events.lastman.LMEvent;
import ext.mods.gameserver.model.entity.events.teamvsteam.TvTEvent;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.model.residence.castle.Siege;
import ext.mods.gameserver.model.residence.clanhall.ClanHallSiege;

public class Die extends L2GameServerPacket
{
	private final Creature _creature;
	private final boolean _canTeleport;
	private final int _objectId;
	private final boolean _fake;
	
	private boolean _sweepable;
	private boolean _allowFixedRes;
	private Clan _clan;
	
	public Die(Creature creature)
	{
		_creature = creature;
		_objectId = creature.getObjectId();
		_canTeleport = !(((creature instanceof Player)) && CTFEvent.getInstance().isStarted() && CTFEvent.getInstance().isPlayerParticipant(_objectId) || TvTEvent.getInstance().isStarted() && TvTEvent.getInstance().isPlayerParticipant(_objectId) || LMEvent.getInstance().isStarted() && LMEvent.getInstance().isPlayerParticipant(_objectId) || DMEvent.getInstance().isStarted() && DMEvent.getInstance().isPlayerParticipant(_objectId));
		_fake = !creature.isDead();
		
		if (creature instanceof Player player)
		{
			_allowFixedRes = player.getAccessLevel().allowFixedRes() || player.isInsideZone(ZoneId.RANDOM);
			_clan = player.getClan();
			
		}
		else if (creature instanceof Monster monster)
			_sweepable = monster.getSpoilState().isSweepable();
	}
	
	@Override
	protected final void writeImpl()
	{
		if (_fake)
			return;
		
		writeC(0x06);
		writeD(_objectId);
		writeD(_canTeleport ? 0x01 : 0);
		
		if (_canTeleport && _clan != null)
		{
			final Siege siege = CastleManager.getInstance().getActiveSiege(_creature);
			final ClanHallSiege chs = ClanHallManager.getInstance().getActiveSiege(_creature);
			
			if (siege != null)
			{
				final SiegeSide side = siege.getSide(_clan);
				
				writeD((_clan.hasClanHall()) ? 0x01 : 0x00);
				writeD((_clan.hasCastle() || side == SiegeSide.OWNER || side == SiegeSide.DEFENDER) ? 0x01 : 0x00);
				writeD((side == SiegeSide.ATTACKER && _clan.getFlag() != null) ? 0x01 : 0x00);
			}
			else if (chs != null)
			{
				writeD((_clan.hasClanHall()) ? 0x01 : 0x00);
				writeD((_clan.hasCastle()) ? 0x01 : 0x00);
				writeD((chs.checkSide(_clan, SiegeSide.ATTACKER) && _clan.getFlag() != null) ? 0x01 : 0x00);
			}
			else
			{
				writeD((_clan.hasClanHall()) ? 0x01 : 0x00);
				writeD((_clan.hasCastle()) ? 0x01 : 0x00);
				writeD(0x00);
			}
		}
		else
		{
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
		}
		
		writeD((_sweepable) ? 0x01 : 0x00);
		writeD((_allowFixedRes) ? 0x01 : 0x00);
	}
}
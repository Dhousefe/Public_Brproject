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
package ext.mods.gameserver.network.clientpackets;

import ext.mods.gameserver.data.manager.CastleManager;
import ext.mods.gameserver.data.manager.ClanHallManager;
import ext.mods.gameserver.data.xml.RestartPointData;
import ext.mods.gameserver.enums.RestartType;
import ext.mods.gameserver.enums.SiegeSide;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.entity.events.capturetheflag.CTFEvent;
import ext.mods.gameserver.model.entity.events.deathmatch.DMEvent;
import ext.mods.gameserver.model.entity.events.lastman.LMEvent;
import ext.mods.gameserver.model.entity.events.teamvsteam.TvTEvent;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.model.residence.castle.Castle;
import ext.mods.gameserver.model.residence.castle.Castle.CastleFunction;
import ext.mods.gameserver.model.residence.castle.Siege;
import ext.mods.gameserver.model.residence.clanhall.ClanHall;
import ext.mods.gameserver.model.residence.clanhall.ClanHallFunction;

public final class RequestRestartPoint extends L2GameClientPacket
{
	protected static final Location JAIL_LOCATION = new Location(-114356, -249645, -2984);
	
	protected int _requestType;
	
	@Override
	protected void readImpl()
	{
		_requestType = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (player.isFakeDeath())
		{
			player.stopFakeDeath(true);
			return;
		}
		
		if (CTFEvent.getInstance().isStarted() && CTFEvent.getInstance().isPlayerParticipant(player.getObjectId()) || DMEvent.getInstance().isStarted() && DMEvent.getInstance().isPlayerParticipant(player.getObjectId()) || LMEvent.getInstance().isStarted() && LMEvent.getInstance().isPlayerParticipant(player.getObjectId()) || TvTEvent.getInstance().isStarted() && TvTEvent.getInstance().isPlayerParticipant(player.getObjectId()))
			return;
		
		if (!player.isDead())
			return;
		
		portPlayer(player);
	}
	
	/**
	 * Teleport the {@link Player} to the associated {@link Location}, based on _requestType.
	 * @param player : The player set as parameter.
	 */
	private void portPlayer(Player player)
	{
		final Clan clan = player.getClan();
		
		Location loc = null;
		
		if (player.isInJail())
			_requestType = 27;
		else if (player.isFestivalParticipant())
			_requestType = 4;
		
		if (_requestType == 1)
		{
			if (clan == null || !clan.hasClanHall())
				return;
			
			loc = RestartPointData.getInstance().getLocationToTeleport(player, RestartType.CLAN_HALL);
			
			final ClanHall ch = ClanHallManager.getInstance().getClanHallByOwner(clan);
			if (ch != null)
			{
				final ClanHallFunction function = ch.getFunction(ClanHall.FUNC_RESTORE_EXP);
				if (function != null)
					player.restoreExp(function.getLvl());
			}
		}
		else if (_requestType == 2)
		{
			final Siege siege = CastleManager.getInstance().getActiveSiege(player);
			if (siege != null)
			{
				final SiegeSide side = siege.getSide(clan);
				if (side == SiegeSide.DEFENDER || side == SiegeSide.OWNER)
					loc = RestartPointData.getInstance().getLocationToTeleport(player, RestartType.CASTLE);
				else if (side == SiegeSide.ATTACKER)
					loc = RestartPointData.getInstance().getLocationToTeleport(player, RestartType.TOWN);
				else
					return;
			}
			else
			{
				if (clan == null || !clan.hasCastle())
					return;
				
				final CastleFunction chfExp = CastleManager.getInstance().getCastleByOwner(clan).getFunction(Castle.FUNC_RESTORE_EXP);
				if (chfExp != null)
					player.restoreExp(chfExp.getLvl());
				
				loc = RestartPointData.getInstance().getLocationToTeleport(player, RestartType.CASTLE);
			}
		}
		else if (_requestType == 3)
			loc = RestartPointData.getInstance().getLocationToTeleport(player, RestartType.SIEGE_FLAG);
		else if (_requestType == 4)
		{
			if (!player.isFestivalParticipant() && !player.isGM())
				return;
			else
				loc = player.getPosition();
		}
		else if (_requestType == 27)
		{
			if (!player.isInJail())
				return;
			
			loc = JAIL_LOCATION;
		}
		else
			loc = RestartPointData.getInstance().getLocationToTeleport(player, RestartType.TOWN);
		
		player.setIsIn7sDungeon(false);
		
		if (player.isDead())
			player.doRevive();
		
		player.teleportTo(loc, 20);
	}
}
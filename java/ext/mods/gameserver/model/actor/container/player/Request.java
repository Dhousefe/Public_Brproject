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
package ext.mods.gameserver.model.actor.container.player;

import java.util.concurrent.ScheduledFuture;

import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.clientpackets.L2GameClientPacket;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

/**
 * A request between two {@link Player}s. It is associated to a 15 seconds timer, where both partner and packet references are destroyed.<br>
 * <br>
 * On request response, the task is canceled.
 */
public class Request
{
	private static final int REQUEST_TIMEOUT = 15000;
	
	private Player _player;
	private Player _partner;
	
	private L2GameClientPacket _requestPacket;
	
	private ScheduledFuture<?> _requestTimer;
	
	public Request(Player player)
	{
		_player = player;
	}
	
	/**
	 * @return the {@link Player} partner of a request.
	 */
	public synchronized Player getPartner()
	{
		return _partner;
	}
	
	/**
	 * Set the {@link Player} partner of a request.
	 * @param partner : The player to set as partner.
	 */
	private synchronized void setPartner(Player partner)
	{
		_partner = partner;
	}
	
	/**
	 * @return the {@link L2GameClientPacket} originally sent by the requestor.
	 */
	public synchronized L2GameClientPacket getRequestPacket()
	{
		return _requestPacket;
	}
	
	/**
	 * Set the {@link L2GameClientPacket} originally sent by the requestor.
	 * @param packet : The packet to set.
	 */
	private synchronized void setRequestPacket(L2GameClientPacket packet)
	{
		_requestPacket = packet;
	}
	
	private void clear()
	{
		_partner = null;
		_requestPacket = null;
	}
	
	/**
	 * Check if a request can be made ; if successful, put {@link Player}s on request state.
	 * @param partner : The player partner to check.
	 * @param packet : The packet to register.
	 * @return true if the request has succeeded.
	 */
	public synchronized boolean setRequest(Player partner, L2GameClientPacket packet)
	{
		if (partner == null)
		{
			_player.sendPacket(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET);
			return false;
		}
		
		if (partner.getRequest().isProcessingRequest())
		{
			_player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_IS_BUSY_TRY_LATER).addCharName(partner));
			return false;
		}
		
		if (isProcessingRequest())
		{
			_player.sendPacket(SystemMessageId.WAITING_FOR_ANOTHER_REPLY);
			return false;
		}
		
		_partner = partner;
		_requestPacket = packet;
		clearRequestOnTimeout();
		
		_partner.getRequest().setPartner(_player);
		_partner.getRequest().setRequestPacket(packet);
		_partner.getRequest().clearRequestOnTimeout();
		return true;
	}
	
	private void clearRequestOnTimeout()
	{
		_requestTimer = ThreadPool.schedule(this::clear, REQUEST_TIMEOUT);
	}
	
	/**
	 * Clear {@link Player} request state. Should be called after answer packet receive.
	 */
	public void onRequestResponse()
	{
		if (_requestTimer != null)
		{
			_requestTimer.cancel(true);
			_requestTimer = null;
		}
		
		clear();
		
		if (_partner != null)
			_partner.getRequest().clear();
	}
	
	/**
	 * @return true if a request is in progress.
	 */
	public boolean isProcessingRequest()
	{
		return _partner != null;
	}
}
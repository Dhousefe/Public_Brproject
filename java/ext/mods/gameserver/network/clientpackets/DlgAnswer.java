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

import ext.mods.gameserver.handler.voicedcommandhandlers.OfflineFarm;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.entity.events.capturetheflag.CTFEvent;
import ext.mods.gameserver.model.entity.events.capturetheflag.CTFManager;
import ext.mods.gameserver.model.entity.events.deathmatch.DMEvent;
import ext.mods.gameserver.model.entity.events.deathmatch.DMManager;
import ext.mods.gameserver.model.entity.events.lastman.LMEvent;
import ext.mods.gameserver.model.entity.events.lastman.LMManager;
import ext.mods.gameserver.model.entity.events.teamvsteam.TvTEvent;
import ext.mods.gameserver.model.entity.events.teamvsteam.TvTManager;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.Crypta.RandomManager;

public final class DlgAnswer extends L2GameClientPacket
{
	private int _messageId;
	private int _answer;
	private int _requesterId;
	
	@Override
	protected void readImpl()
	{
		_messageId = readD();
		_answer = readD();
		_requesterId = readD();
	}
	
	@Override
	public void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (_requesterId == CTFManager.JOIN_CTF_REQ_ID && _answer == 1)
			CTFEvent.getInstance().onBypass("ctf_event_participation", player);
		else if (_requesterId == DMManager.JOIN_DM_REQ_ID && _answer == 1)
			DMEvent.getInstance().onBypass("dm_event_participation", player);
		else if (_requesterId == LMManager.JOIN_LM_REQ_ID && _answer == 1)
			LMEvent.getInstance().onBypass("lm_event_participation", player);
		else if (_requesterId == TvTManager.JOIN_TVT_REQ_ID && _answer == 1)
			TvTEvent.getInstance().onBypass("tvt_event_participation", player);
		else if (_messageId == SystemMessageId.RESSURECTION_REQUEST_BY_S1.getId() || _messageId == SystemMessageId.DO_YOU_WANT_TO_BE_RESTORED.getId())
			player.reviveAnswer(_answer);
		else if (_messageId == SystemMessageId.S1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT.getId())
			player.teleportAnswer(_answer, _requesterId);
		else if (_messageId == 1983)
			player.engageAnswer(_answer);
		else if (_messageId == SystemMessageId.WOULD_YOU_LIKE_TO_OPEN_THE_GATE.getId())
			player.activateGate(_answer, 1);
		else if (_messageId == SystemMessageId.WOULD_YOU_LIKE_TO_CLOSE_THE_GATE.getId())
			player.activateGate(_answer, 0);
        else if (_messageId == SystemMessageId.S1.getId() && "away".equals(player.getLastCommand()))
        {
            final OfflineFarm offlineFarmHandler = new OfflineFarm();
            offlineFarmHandler.handleConfirmation(player, _answer == 1);
        }
        else if (_messageId == SystemMessageId.S1.getId() && "farm_teleport".equals(player.getLastCommand()))
        {
            final ext.mods.gameserver.handler.voicedcommandhandlers.FarmZoneTeleport farmHandler = new ext.mods.gameserver.handler.voicedcommandhandlers.FarmZoneTeleport();
            farmHandler.handleConfirmation(player, _answer == 1);
        }
        else if (_messageId == SystemMessageId.S1.getId() && "farm_event_teleport".equals(player.getLastCommand()))
        {
            final Object randomManager = RandomManager.getInstance();
            if (RandomManager.getInstance() != null)
            {
                RandomManager.getInstance().handleEventTeleportConfirmation(player, _answer == 1);
            }
        }
    }
}
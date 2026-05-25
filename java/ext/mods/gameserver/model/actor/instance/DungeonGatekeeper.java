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

import ext.mods.gameserver.data.manager.SevenSignsManager;
import ext.mods.gameserver.enums.CabalType;
import ext.mods.gameserver.enums.SealType;
import ext.mods.gameserver.handler.bypasshandlers.InstantTeleport;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;

public class DungeonGatekeeper extends Folk
{
	public DungeonGatekeeper(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		
		final CabalType playerCabal = SevenSignsManager.getInstance().getPlayerCabal(player.getObjectId());
		boolean allowCatacombsInAnyPeriod = SevenSignsManager.getInstance().allowCatacombsInAnyPeriod();
		
		InstantTeleport teleport = new InstantTeleport();
		
		if (command.startsWith("necro"))
		{
			boolean canPort = true;
			if (!allowCatacombsInAnyPeriod)
			{
				if (SevenSignsManager.getInstance().isSealValidationPeriod())
				{
					final CabalType winningCabal = SevenSignsManager.getInstance().getWinningCabal();
					final CabalType sealAvariceOwner = SevenSignsManager.getInstance().getSealOwner(SealType.AVARICE);
					
					if (winningCabal == CabalType.DAWN && (playerCabal != CabalType.DAWN || sealAvariceOwner != CabalType.DAWN))
					{
						player.sendPacket(SystemMessageId.CAN_BE_USED_BY_DAWN);
						canPort = false;
					}
					else if (winningCabal == CabalType.DUSK && (playerCabal != CabalType.DUSK || sealAvariceOwner != CabalType.DUSK))
					{
						player.sendPacket(SystemMessageId.CAN_BE_USED_BY_DUSK);
						canPort = false;
					}
					else if (winningCabal == CabalType.NORMAL && playerCabal != CabalType.NORMAL)
						canPort = true;
					else if (playerCabal == CabalType.NORMAL)
						canPort = false;
				}
				else
				{
					if (playerCabal == CabalType.NORMAL)
						canPort = false;
				}
			}
			
			if (!canPort)
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(player.getLocale(), SevenSignsManager.SEVEN_SIGNS_HTML_PATH + "necro_no.htm");
				player.sendPacket(html);
			}
			else
			{
				teleport.instantTeleport(player, this, 0);
				player.setIsIn7sDungeon(true);
			}
		}
		else if (command.startsWith("cata"))
		{
			boolean canPort = true;
			if (!allowCatacombsInAnyPeriod)
			{
				if (SevenSignsManager.getInstance().isSealValidationPeriod())
				{
					final CabalType winningCabal = SevenSignsManager.getInstance().getWinningCabal();
					final CabalType sealGnosisOwner = SevenSignsManager.getInstance().getSealOwner(SealType.GNOSIS);
					
					if (winningCabal == CabalType.DAWN && (playerCabal != CabalType.DAWN || sealGnosisOwner != CabalType.DAWN))
					{
						player.sendPacket(SystemMessageId.CAN_BE_USED_BY_DAWN);
						canPort = false;
					}
					else if (winningCabal == CabalType.DUSK && (playerCabal != CabalType.DUSK || sealGnosisOwner != CabalType.DUSK))
					{
						player.sendPacket(SystemMessageId.CAN_BE_USED_BY_DUSK);
						canPort = false;
					}
					else if (winningCabal == CabalType.NORMAL && playerCabal != CabalType.NORMAL)
						canPort = true;
					else if (playerCabal == CabalType.NORMAL)
						canPort = false;
				}
				else
				{
					if (playerCabal == CabalType.NORMAL)
						canPort = false;
				}
			}
			
			if (!canPort)
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(player.getLocale(), SevenSignsManager.SEVEN_SIGNS_HTML_PATH + "cata_no.htm");
				player.sendPacket(html);
			}
			else
			{
				teleport.instantTeleport(player, this, 0);
				player.setIsIn7sDungeon(true);
			}
		}
		else if (command.startsWith("exit"))
		{
			teleport.instantTeleport(player, this, 0);
			player.setIsIn7sDungeon(false);
		}
		else
			super.onBypassFeedback(player, command);
	}
	
	@Override
	public String getHtmlPath(Player player, int npcId, int val)
	{
		String filename = "";
		if (val == 0)
			filename = "" + npcId;
		else
			filename = npcId + "-" + val;
		
		return "html/gatekeeper/" + filename + ".htm";
	}
}
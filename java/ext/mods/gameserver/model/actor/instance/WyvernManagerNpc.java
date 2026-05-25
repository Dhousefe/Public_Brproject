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

import ext.mods.Config;
import ext.mods.gameserver.data.manager.SevenSignsManager;
import ext.mods.gameserver.enums.CabalType;
import ext.mods.gameserver.enums.SealType;
import ext.mods.gameserver.enums.actors.NpcTalkCond;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * This instance leads the behavior of Wyvern Managers.<br>
 * Those NPCs allow Castle Lords to mount a wyvern in return for B Crystals.<br>
 * Three configs exist so far :<br>
 * <ul>
 * <li>WYVERN_ALLOW_UPGRADER : spawn instances of Wyvern Manager through the world, or no;</li>
 * <li>WYVERN_REQUIRED_LEVEL : the strider's required level;</li>
 * <li>WYVERN_REQUIRED_CRYSTALS : the B-crystals' required amount;</li>
 * </ul>
 */
public class WyvernManagerNpc extends CastleChamberlain
{
	public WyvernManagerNpc(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (player.getCurrentFolk() == null || player.getCurrentFolk().getObjectId() != getObjectId())
			return;
		
		if (command.startsWith("RideWyvern"))
		{
			if (!isLordOwner(player))
			{
				sendHtm(player, "2");
				return;
			}
			
			if (SevenSignsManager.getInstance().getSealOwner(SealType.STRIFE) == CabalType.DUSK)
			{
				sendHtm(player, "3");
				return;
			}
			
			if (!player.isMounted() || (player.getMountNpcId() != 12526 && player.getMountNpcId() != 12527 && player.getMountNpcId() != 12528))
			{
				player.sendPacket(SystemMessageId.YOU_MAY_ONLY_RIDE_WYVERN_WHILE_RIDING_STRIDER);
				sendHtm(player, "1");
				return;
			}
			
			if (player.getMountLevel() < Config.WYVERN_REQUIRED_LEVEL)
			{
				sendHtm(player, "6");
				return;
			}
			
			if (!player.destroyItemByItemId(1460, Config.WYVERN_REQUIRED_CRYSTALS, true))
			{
				sendHtm(player, "5");
				return;
			}
			
			player.dismount();
			
			if (player.mount(12621, 0))
				sendHtm(player, "4");
		}
		else if (command.startsWith("Chat"))
		{
			String val = "1";
			try
			{
				val = command.substring(5);
			}
			catch (IndexOutOfBoundsException ioobe)
			{
			}
			
			sendHtm(player, val);
		}
		else
			super.onBypassFeedback(player, command);
	}
	
	@Override
	public void showChatWindow(Player player)
	{
		final NpcTalkCond condition = getNpcTalkCond(player);
		if (condition == NpcTalkCond.OWNER)
			sendHtm(player, (player.isFlying()) ? "4" : "0");
		else if (condition == NpcTalkCond.CLAN_MEMBER)
			sendHtm(player, "2");
		else
			sendHtm(player, "0a");
	}
	
	@Override
	protected NpcTalkCond getNpcTalkCond(Player player)
	{
		if (player.getClan() != null && ((getCastle() != null && getCastle().getOwnerId() == player.getClanId()) || (getSiegableHall() != null && getSiegableHall().getOwnerId() == player.getClanId())))
			return (player.isClanLeader()) ? NpcTalkCond.OWNER : NpcTalkCond.CLAN_MEMBER;
		
		return NpcTalkCond.NONE;
	}
	
	private void sendHtm(Player player, String val)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(player.getLocale(), "html/wyvernmanager/wyvernmanager-" + val + ".htm");
		html.replace("%objectId%", getObjectId());
		html.replace("%npcname%", getName());
		html.replace("%wyvern_level%", Config.WYVERN_REQUIRED_LEVEL);
		html.replace("%needed_crystals%", Config.WYVERN_REQUIRED_CRYSTALS);
		player.sendPacket(html);
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
}
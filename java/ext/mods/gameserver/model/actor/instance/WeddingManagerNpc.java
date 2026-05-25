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

import java.util.StringTokenizer;

import ext.mods.commons.lang.StringUtil;

import ext.mods.Config;
import ext.mods.gameserver.data.manager.CastleManager;
import ext.mods.gameserver.data.manager.CoupleManager;
import ext.mods.gameserver.data.manager.RelationManager;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.enums.actors.MissionType;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.network.serverpackets.ConfirmDlg;
import ext.mods.gameserver.network.serverpackets.MagicSkillUse;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;

public class WeddingManagerNpc extends Folk
{
	public WeddingManagerNpc(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onInteract(Player player)
	{
		if (player.getCoupleId() > 0)
			sendHtmlMessage(player, "html/mods/wedding/start2.htm");
		else if (player.isUnderMarryRequest())
			sendHtmlMessage(player, "html/mods/wedding/waitforpartner.htm");
		else
			sendHtmlMessage(player, "html/mods/wedding/start.htm");
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (command.startsWith("AskWedding"))
		{
			final StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			
			if (st.hasMoreTokens())
			{
				final Player partner = World.getInstance().getPlayer(st.nextToken());
				if (partner == null)
				{
					sendHtmlMessage(player, "html/mods/wedding/notfound.htm");
					return;
				}
				
				if (!weddingConditions(player, partner))
					return;
				
				player.setUnderMarryRequest(true);
				partner.setUnderMarryRequest(true);
				
				partner.setRequesterId(player.getObjectId());
				partner.sendPacket(new ConfirmDlg(1983).addString(player.getName() + " asked you to marry. Do you want to start a new relationship ?"));
			}
			else
				sendHtmlMessage(player, "html/mods/wedding/notfound.htm");
		}
		else if (command.startsWith("Divorce"))
			CoupleManager.getInstance().deleteCouple(player.getCoupleId());
		else if (command.startsWith("GoToLove"))
		{
			final int partnerId = CoupleManager.getInstance().getPartnerId(player.getCoupleId(), player.getObjectId());
			if (partnerId == 0)
			{
				player.sendMessage(player.getSysString(10_061));
				return;
			}
			
			final Player partner = World.getInstance().getPlayer(partnerId);
			if (partner == null)
			{
				player.sendMessage(player.getSysString(10_062));
				return;
			}
			
			if (partner.isInsideZone(ZoneId.NO_SUMMON_FRIEND) || partner.isInJail() || partner.isInOlympiadMode() || partner.isInDuel() || partner.isFestivalParticipant() || partner.isInObserverMode())
			{
				player.sendMessage(player.getSysString(10_063));
				return;
			}
			
			if (partner.getClan() != null && CastleManager.getInstance().getCastleByOwner(partner.getClan()) != null && CastleManager.getInstance().getCastleByOwner(partner.getClan()).getSiege().isInProgress())
			{
				player.sendMessage(player.getSysString(10_064));
				return;
			}
			
			player.teleportTo(partner.getX(), partner.getY(), partner.getZ(), 20);
		}
	}
	
	private boolean weddingConditions(Player requester, Player partner)
	{
		if (partner.getObjectId() == requester.getObjectId())
		{
			sendHtmlMessage(requester, "html/mods/wedding/error_wrongtarget.htm");
			return false;
		}
		
		if (!Config.WEDDING_SAMESEX && partner.getAppearance().getSex() == requester.getAppearance().getSex())
		{
			sendHtmlMessage(requester, "html/mods/wedding/error_sex.htm");
			return false;
		}
		
		if (!RelationManager.getInstance().areFriends(requester.getObjectId(), partner.getObjectId()))
		{
			sendHtmlMessage(requester, "html/mods/wedding/error_friendlist.htm");
			return false;
		}
		
		if (partner.getCoupleId() > 0)
		{
			sendHtmlMessage(requester, "html/mods/wedding/error_alreadymarried.htm");
			return false;
		}
		
		if (Config.WEDDING_FORMALWEAR && (!requester.isWearingFormalWear() || !partner.isWearingFormalWear()))
		{
			sendHtmlMessage(requester, "html/mods/wedding/error_noformal.htm");
			return false;
		}
		
		if (requester.getAdena() < Config.WEDDING_PRICE || partner.getAdena() < Config.WEDDING_PRICE)
		{
			sendHtmlMessage(requester, "html/mods/wedding/error_adena.htm");
			return false;
		}
		
		return true;
	}
	
	public static void justMarried(Player requester, Player partner)
	{
		requester.setUnderMarryRequest(false);
		partner.setUnderMarryRequest(false);
		
		requester.reduceAdena(Config.WEDDING_PRICE, true);
		partner.reduceAdena(Config.WEDDING_PRICE, true);
		
		requester.sendMessage(requester.getSysString(10_065, partner.getName()));
		partner.sendMessage(partner.getSysString(10_066, requester.getName()));
		
		requester.broadcastPacket(new MagicSkillUse(requester, requester, 2230, 1, 1, 0));
		partner.broadcastPacket(new MagicSkillUse(partner, partner, 2230, 1, 1, 0));
		
		requester.broadcastPacket(new MagicSkillUse(requester, requester, 2025, 1, 1, 0));
		partner.broadcastPacket(new MagicSkillUse(partner, partner, 2025, 1, 1, 0));
		
		requester.getMissions().update(MissionType.MARRIED);
		partner.getMissions().update(MissionType.MARRIED);
		
		World.announceToOnlinePlayers(requester.getSysString(10_067, requester.getName(), partner.getName()));
	}
	
	private void sendHtmlMessage(Player player, String file)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(player.getLocale(), file);
		html.replace("%objectId%", getObjectId());
		html.replace("%adenaCost%", StringUtil.formatNumber(Config.WEDDING_PRICE));
		html.replace("%needOrNot%", Config.WEDDING_FORMALWEAR ? player.getSysString(10_068) : player.getSysString(10_069));
		player.sendPacket(html);
	}
}
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

import ext.mods.gameserver.custom.data.DonateData;
import ext.mods.gameserver.custom.data.DonateData.Donate;
import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.data.xml.MultisellData;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;

public final class Service extends Merchant
{
	public Service(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public String getHtmlPath(Player player, int npcId, int val)
	{
		String htmlName = val == 0 ? "" + npcId : "" + npcId + "-" + val;
		return String.format("html/mods/donate/%s.htm", htmlName);
	}
	
	@Override
	protected boolean showPkDenyChatWindow(Player player, String type)
	{
		final String content = HTMLData.getInstance().getHtm(player, "html/mods/donate/" + getNpcId() + "-pk.htm");
		if (content != null)
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setHtml(content);
			
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return true;
		}
		return false;
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken();
		switch (actualCommand)
		{
			case "nobles" -> {
				final var service = scanService(st);
				if (!checkService(service, player, command))
					return;
				
				DonateData.setNobless(player, service);
			}
			
			case "hero" -> {
				final var service = scanService(st);
				if (!checkService(service, player, command))
					return;
				
				DonateData.setHero(player, service);
			}
			
			case "setnamecolor" -> {
				final var service = scanService(st);
				if (!checkService(service, player, command))
					return;
				
				if (!st.hasMoreTokens())
					return;
				
				DonateData.setNameColor(player, service, st.nextToken());
			}
			
			case "settitlecolor" -> {
				final var service = scanService(st);
				if (!checkService(service, player, command))
					return;
				
				if (!st.hasMoreTokens())
					return;
				
				DonateData.setTitleColor(player, service, st.nextToken());
			}
			
			case "setname" -> {
				final var service = scanService(st);
				if (!checkService(service, player, command))
					return;
				
				if (!st.hasMoreTokens())
					return;
				
				DonateData.setName(player, service, st.nextToken());
			}
			
			case "premium" -> {
				final var service = scanService(st);
				if (!checkService(service, player, command))
					return;
				
				DonateData.setPremium(player, service);
			}
			
			case "gender" -> {
				final var service = scanService(st);
				if (!checkService(service, player, command))
				{
					return;
				}
				DonateData.setGender(player, service);
			}
			
			case "nullpk" -> {
				final var service = scanService(st);
				if (!checkService(service, player, command))
					return;
				
				DonateData.clearPK(player, service);
			}
			
			case "clanlvl" -> {
				final var service = scanService(st);
				if (!checkService(service, player, command))
					return;
				
				DonateData.setClanLevel(player, service);
			}
			
			case "clanskill" -> {
				final var service = scanService(st);
				if (!checkService(service, player, command))
					return;
				
				DonateData.addClanSkill(player, service);
			}
			
			case "clanrep" -> {
				final var service = scanService(st);
				if (!checkService(service, player, command))
					return;
				
				DonateData.addClanRep(player, service);
			}
			
			case "multisell" -> {
				MultisellData.getInstance().separateAndSend(st.nextToken(), player, this, false);
			}
		}
		
		super.onBypassFeedback(player, command);
	}
	
	public static Donate scanService(StringTokenizer st)
	{
		return DonateData.getInstance().getDonate(Integer.parseInt(st.nextToken()));
	}
	
	public static boolean checkService(Donate donate, Player pc, String command)
	{
		if (donate != null)
			return true;
		
		LOGGER.info("pc[{}] use missing service[{}]", pc.getName(), command);
		return false;
	}
}
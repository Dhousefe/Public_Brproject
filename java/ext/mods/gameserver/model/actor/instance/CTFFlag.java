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
import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.enums.Paperdoll;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.entity.events.capturetheflag.CTFEvent;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.MagicSkillUse;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;

public class CTFFlag extends Folk
{
	private static final String flagsPath = "html/mods/events/ctf/flags/";
	
	public CTFFlag(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void showChatWindow(Player player, int val)
	{
		if (player == null)
			return;
		
		if (CTFEvent.getInstance().isStarting() || CTFEvent.getInstance().isStarted())
		{
			final String team = CTFEvent.getInstance().getParticipantTeam(player.getObjectId()).getName();
			final String enemyteam = CTFEvent.getInstance().getParticipantEnemyTeam(player.getObjectId()).getName();
			
			if (getTitle() == team)
			{
				if (CTFEvent.getInstance().getEnemyCarrier(player) != null)
				{
					final String htmContent = HTMLData.getInstance().getHtm(player, flagsPath + "flag_friendly_missing.htm");
					NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
					
					npcHtmlMessage.setHtml(htmContent);
					npcHtmlMessage.replace("%enemyteam%", enemyteam);
					npcHtmlMessage.replace("%team%", team);
					npcHtmlMessage.replace("%player%", player.getName());
					player.sendPacket(npcHtmlMessage);
				}
				else if (player == CTFEvent.getInstance().getTeamCarrier(player))
				{
					if (Config.CTF_EVENT_CAPTURE_SKILL > 0)
						player.broadcastPacket(new MagicSkillUse(player, Config.CTF_EVENT_CAPTURE_SKILL, 1, 1, 1));
					
					CTFEvent.getInstance().removeFlagCarrier(player);
					CTFEvent.getInstance().getParticipantTeam(player.getObjectId()).increasePoints();
					CTFEvent.getInstance().broadcastScreenMessage(player.getName() + " scored for the " + team + " team!", 7);
				}
				else
				{
					final String htmContent = HTMLData.getInstance().getHtm(player, flagsPath + "flag_friendly.htm");
					NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
					
					npcHtmlMessage.setHtml(htmContent);
					npcHtmlMessage.replace("%enemyteam%", enemyteam);
					npcHtmlMessage.replace("%team%", team);
					npcHtmlMessage.replace("%player%", player.getName());
					player.sendPacket(npcHtmlMessage);
				}
			}
			else
			{
				if (CTFEvent.getInstance().playerIsCarrier(player))
				{
					final String htmContent = HTMLData.getInstance().getHtm(player, flagsPath + "flag_enemy.htm");
					NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
					
					npcHtmlMessage.setHtml(htmContent);
					npcHtmlMessage.replace("%enemyteam%", enemyteam);
					npcHtmlMessage.replace("%team%", team);
					npcHtmlMessage.replace("%player%", player.getName());
					player.sendPacket(npcHtmlMessage);
				}
				else if (CTFEvent.getInstance().getTeamCarrier(player) != null)
				{
					final String htmContent = HTMLData.getInstance().getHtm(player, flagsPath + "flag_enemy_missing.htm");
					NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
					
					npcHtmlMessage.setHtml(htmContent);
					npcHtmlMessage.replace("%enemyteam%", enemyteam);
					npcHtmlMessage.replace("%player%", CTFEvent.getInstance().getTeamCarrier(player).getName());
					player.sendPacket(npcHtmlMessage);
				}
				else
				{
					if (Config.CTF_EVENT_CAPTURE_SKILL > 0)
						player.broadcastPacket(new MagicSkillUse(player, Config.CTF_EVENT_CAPTURE_SKILL, 1, 1, 1));
					
					CTFEvent.getInstance().setCarrierUnequippedWeapons(player, player.getInventory().getItemFrom(Paperdoll.RHAND), player.getInventory().getItemFrom(Paperdoll.LHAND));
					player.getInventory().equipItem(ItemInstance.create(CTFEvent.getInstance().getEnemyTeamFlagId(player), 1));
					player.getInventory().blockAllItems();
					player.broadcastUserInfo();
					CTFEvent.getInstance().setTeamCarrier(player);
					CTFEvent.getInstance().broadcastScreenMessage(player.getName() + " has taken the " + enemyteam + " flag team!", 5);
				}
			}
		}
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	@Override
	public boolean isInvul()
	{
		return true;
	}
}
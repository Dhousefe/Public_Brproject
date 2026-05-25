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

import java.util.List;

import ext.mods.Config;
import ext.mods.gameserver.data.manager.FishingChampionshipManager;
import ext.mods.gameserver.data.xml.SkillTreeData;
import ext.mods.gameserver.enums.skills.AcquireSkillType;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.holder.skillnode.FishingSkillNode;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.AcquireSkillDone;
import ext.mods.gameserver.network.serverpackets.AcquireSkillList;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

/**
 * An instance type extending {@link Merchant}, used for fishing event.
 */
public class Fisherman extends Merchant
{
	public Fisherman(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public String getHtmlPath(Player player, int npcId, int val)
	{
		String filename = "";
		
		if (val == 0)
			filename = "" + npcId;
		else
			filename = npcId + "-" + val;
		
		return "html/fisherman/" + filename + ".htm";
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!Config.KARMA_PLAYER_CAN_SHOP && player.getKarma() > 0 && showPkDenyChatWindow(player, "fisherman"))
			return;
		
		if (command.startsWith("FishSkillList"))
			showFishSkillList(player);
		else if (command.startsWith("FishingChampionship"))
		{
			if (!Config.ALLOW_FISH_CHAMPIONSHIP)
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(player.getLocale(), "html/fisherman/championship/no_fish_event001.htm");
				player.sendPacket(html);
				return;
			}
			FishingChampionshipManager.getInstance().showChampScreen(player, getObjectId());
		}
		else if (command.startsWith("FishingReward"))
		{
			if (!Config.ALLOW_FISH_CHAMPIONSHIP)
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(player.getLocale(), "html/fisherman/championship/no_fish_event001.htm");
				player.sendPacket(html);
				return;
			}
			
			if (!FishingChampionshipManager.getInstance().isWinner(player.getName()))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(player.getLocale(), "html/fisherman/championship/no_fish_event_reward001.htm");
				player.sendPacket(html);
				return;
			}
			FishingChampionshipManager.getInstance().getReward(player);
		}
		else
			super.onBypassFeedback(player, command);
	}
	
	@Override
	public void showChatWindow(Player player, int val)
	{
		if (!Config.KARMA_PLAYER_CAN_SHOP && player.getKarma() > 0 && showPkDenyChatWindow(player, "fisherman"))
			return;
		
		showChatWindow(player, getHtmlPath(player, getNpcId(), val));
	}
	
	public static void showFishSkillList(Player player)
	{
		final List<FishingSkillNode> skills = SkillTreeData.getInstance().getFishingSkillsFor(player);
		if (skills.isEmpty())
		{
			final int minlevel = SkillTreeData.getInstance().getRequiredLevelForNextFishingSkill(player);
			if (minlevel > 0)
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.DO_NOT_HAVE_FURTHER_SKILLS_TO_LEARN_S1).addNumber(minlevel));
			else
				player.sendPacket(SystemMessageId.NO_MORE_SKILLS_TO_LEARN);
			
			player.sendPacket(AcquireSkillDone.STATIC_PACKET);
		}
		else
			player.sendPacket(new AcquireSkillList(AcquireSkillType.FISHING, skills));
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
}
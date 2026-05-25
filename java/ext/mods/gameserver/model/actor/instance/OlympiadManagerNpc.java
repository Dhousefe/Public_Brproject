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
import java.util.concurrent.CopyOnWriteArrayList;

import ext.mods.commons.lang.StringUtil;

import ext.mods.gameserver.data.manager.HeroManager;
import ext.mods.gameserver.data.xml.MultisellData;
import ext.mods.gameserver.enums.OlympiadType;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.olympiad.Olympiad;
import ext.mods.gameserver.model.olympiad.OlympiadGameManager;
import ext.mods.gameserver.model.olympiad.OlympiadGameTask;
import ext.mods.gameserver.model.olympiad.OlympiadManager;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.ExHeroList;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;

public class OlympiadManagerNpc extends Folk
{
	private static final List<OlympiadManagerNpc> _managers = new CopyOnWriteArrayList<>();
	
	private static final int GATE_PASS = 6651;
	
	public OlympiadManagerNpc(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	public static List<OlympiadManagerNpc> getInstances()
	{
		return _managers;
	}
	
	@Override
	public String getHtmlPath(Player player, int npcId, int val)
	{
		String filename = "noble";
		
		if (val > 0)
			filename = "noble_" + val;
		
		return filename + ".htm";
	}
	
	@Override
	public void showChatWindow(Player player, int val)
	{
		int npcId = getTemplate().getNpcId();
		String filename = getHtmlPath(player, npcId, val);
		
		switch (npcId)
		{
			case 31688:
				if (player.isNoble() && val == 0)
					filename = "noble_main.htm";
				break;
			
			case 31690, 31769, 31770, 31771, 31772:
				if (player.isHero() || HeroManager.getInstance().isInactiveHero(player.getObjectId()))
					filename = "hero_main.htm";
				else
					filename = "hero_main2.htm";
				break;
		}
		
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(player.getLocale(), "html/olympiad/" + filename);
		
		if (filename.equals("hero_main.htm"))
		{
			String hiddenText = "";
			if (HeroManager.getInstance().isInactiveHero(player.getObjectId()))
				hiddenText = "<a action=\"bypass -h npc_%objectId%_Olympiad 5\">\"I want to be a Hero.\"</a><br>";
			
			html.replace("%hero%", hiddenText);
		}
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (command.startsWith("OlympiadNoble"))
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			if (player.isCursedWeaponEquipped())
			{
				html.setFile(player.getLocale(), Olympiad.OLYMPIAD_HTML_PATH + "noble_cant_cw.htm");
				player.sendPacket(html);
				return;
			}
			
			if (player.getClassIndex() != 0)
			{
				html.setFile(player.getLocale(), Olympiad.OLYMPIAD_HTML_PATH + "noble_cant_sub.htm");
				html.replace("%objectId%", getObjectId());
				player.sendPacket(html);
				return;
			}
			
			if (!player.isNoble() || (player.getClassId().getLevel() < 3))
			{
				html.setFile(player.getLocale(), Olympiad.OLYMPIAD_HTML_PATH + "noble_cant_thirdclass.htm");
				html.replace("%objectId%", getObjectId());
				player.sendPacket(html);
				return;
			}
			
			int val = Integer.parseInt(command.substring(14));
			switch (val)
			{
				case 1:
					OlympiadManager.getInstance().unRegisterNoble(player);
					break;
				
				case 2:
					final int nonClassed = OlympiadManager.getInstance().getNonClassBasedParticipants().size();
					final int classed = OlympiadManager.getInstance().getClassBasedParticipants().size();
					
					html.setFile(player.getLocale(), Olympiad.OLYMPIAD_HTML_PATH + "noble_registered.htm");
					html.replace("%listClassed%", classed);
					html.replace("%listNonClassed%", nonClassed);
					html.replace("%objectId%", getObjectId());
					player.sendPacket(html);
					break;
				
				case 3:
					int points = Olympiad.getInstance().getNoblePoints(player.getObjectId());
					html.setFile(player.getLocale(), Olympiad.OLYMPIAD_HTML_PATH + "noble_points1.htm");
					html.replace("%points%", points);
					html.replace("%objectId%", getObjectId());
					player.sendPacket(html);
					break;
				
				case 4:
					OlympiadManager.getInstance().registerNoble(this, player, OlympiadType.NON_CLASSED);
					break;
				
				case 5:
					OlympiadManager.getInstance().registerNoble(this, player, OlympiadType.CLASSED);
					break;
				
				case 6:
					html.setFile(player.getLocale(), Olympiad.OLYMPIAD_HTML_PATH + ((Olympiad.getInstance().getNoblessePasses(player, false) > 0) ? "noble_settle.htm" : "noble_nopoints2.htm"));
					
					html.replace("%objectId%", getObjectId());
					player.sendPacket(html);
					break;
				
				case 7:
					MultisellData.getInstance().separateAndSend("102", player, this, false);
					break;
				
				case 10:
					player.addItem(GATE_PASS, Olympiad.getInstance().getNoblessePasses(player, true), true);
					break;
				
				default:
					break;
			}
		}
		else if (command.startsWith("Olympiad"))
		{
			int val = Integer.parseInt(command.substring(9, 10));
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			switch (val)
			{
				case 2:
					int classId = Integer.parseInt(command.substring(11));
					if (classId >= 88 && classId <= 118)
					{
						List<String> names = Olympiad.getInstance().getClassLeaderBoard(classId);
						html.setFile(player.getLocale(), Olympiad.OLYMPIAD_HTML_PATH + "noble_ranking.htm");
						
						int index = 1;
						for (String name : names)
						{
							html.replace("%place" + index + "%", index);
							html.replace("%rank" + index + "%", name);
							
							index++;
							if (index > 10)
								break;
						}
						
						for (; index <= 10; index++)
						{
							html.replace("%place" + index + "%", "");
							html.replace("%rank" + index + "%", "");
						}
						
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					break;
				
				case 3:
					html.setFile(player.getLocale(), Olympiad.OLYMPIAD_HTML_PATH + "olympiad_observe_list.htm");
					
					int i = 0;
					
					final StringBuilder sb = new StringBuilder(2000);
					for (OlympiadGameTask task : OlympiadGameManager.getInstance().getOlympiadTasks())
					{
						StringUtil.append(sb, "<a action=\"bypass arenachange ", i, "\">Arena ", ++i, "&nbsp;");
						
						if (task.isGameStarted())
						{
							if (task.isInTimerTime())
								StringUtil.append(sb, "(&$907;)");
							else if (task.isBattleStarted())
								StringUtil.append(sb, "(&$829;)");
							else
								StringUtil.append(sb, "(&$908;)");
								
							StringUtil.append(sb, "&nbsp;", task.getGame().getPlayerNames()[0], "&nbsp; : &nbsp;", task.getGame().getPlayerNames()[1]);
						}
						else
							StringUtil.append(sb, "(&$906;)</td><td>&nbsp;");
							
						StringUtil.append(sb, "</a><br>");
					}
					html.replace("%list%", sb.toString());
					html.replace("%objectId%", getObjectId());
					player.sendPacket(html);
					break;
				
				case 4:
					player.sendPacket(new ExHeroList());
					break;
				
				case 5:
					if (HeroManager.getInstance().isInactiveHero(player.getObjectId()))
					{
						html.setFile(player.getLocale(), Olympiad.OLYMPIAD_HTML_PATH + "hero_confirm.htm");
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					break;
				
				case 6:
					if (HeroManager.getInstance().isInactiveHero(player.getObjectId()))
					{
						if (player.isSubClassActive() || player.getStatus().getLevel() < 76)
						{
							player.sendMessage("You may only become an hero on a main class whose level is 75 or more.");
							return;
						}
						
						HeroManager.getInstance().activateHero(player);
					}
					break;
				
				case 7:
					html.setFile(player.getLocale(), Olympiad.OLYMPIAD_HTML_PATH + "hero_main.htm");
					
					String hiddenText = "";
					if (HeroManager.getInstance().isInactiveHero(player.getObjectId()))
						hiddenText = "<a action=\"bypass -h npc_%objectId%_Olympiad 5\">\"I want to be a Hero.\"</a><br>";
					
					html.replace("%hero%", hiddenText);
					html.replace("%objectId%", getObjectId());
					player.sendPacket(html);
					break;
				
				default:
					break;
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		if (getNpcId() == 31688)
			_managers.add(this);
	}
	
	@Override
	public void onDecay()
	{
		_managers.remove(this);
		super.onDecay();
	}
}
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

import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import ext.mods.Config;
import ext.mods.commons.util.QueryParser;
import ext.mods.dungeon.DungeonManager;
import ext.mods.extensions.listener.manager.BypassCommandManager;
import ext.mods.gameserver.communitybbs.CommunityBoard;
import ext.mods.gameserver.communitybbs.CustomCommunityBoard;
import ext.mods.gameserver.data.manager.BotsPreventionManager;
import ext.mods.gameserver.data.manager.DropSkipManager;
import ext.mods.gameserver.data.manager.HeroManager;
import ext.mods.gameserver.data.manager.SpawnManager;
import ext.mods.gameserver.data.xml.AdminData;
import ext.mods.gameserver.enums.EventHandler;
import ext.mods.gameserver.enums.FloodProtector;
import ext.mods.gameserver.handler.AdminCommandHandler;
import ext.mods.gameserver.handler.BypassHandler;
import ext.mods.gameserver.handler.IAdminCommandHandler;
import ext.mods.gameserver.handler.IBypassHandler;
import ext.mods.gameserver.handler.IVoicedCommandHandler;
import ext.mods.gameserver.handler.VoicedCommandHandler;
import ext.mods.gameserver.handler.bypasshandlers.DropListUI;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.OlympiadManagerNpc;
import ext.mods.gameserver.model.entity.autofarm.AutoFarmManager;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.olympiad.OlympiadManager;
import ext.mods.gameserver.model.spawn.ASpawn;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.scripting.QuestState;
import ext.mods.quests.QuestData;
import ext.mods.quests.QuestManager;
import ext.mods.quests.holder.QuestHolder;
import ext.mods.quests.holder.QuestObjective;
import ext.mods.sellBuffEngine.BuffShopBypassHandler;
import ext.mods.util.Tokenizer;
import ext.mods.Crypta.DeeplTranslator;

public final class RequestBypassToServer extends L2GameClientPacket
{
	private static final Logger GMAUDIT_LOG = Logger.getLogger("gmaudit");
	
	private String _command;
	
	@Override
	protected void readImpl()
	{
		try
		{
			_command = readS();
		}
		catch (Exception e)
		{
			LOGGER.warn("RequestBypassToServer readImpl failed (packet format/encoding?).", e);
			throw e;
		}
	}
	
	@Override
	protected void runImpl()
	{
		try
		{
			runImplInternal();
		}
		catch (Exception e)
		{
			LOGGER.warn("RequestBypassToServer runImpl failed: _command=[{}]", _command != null ? _command : "(null)", e);
			throw e;
		}
	}
	
	private void runImplInternal()
	{
		if (_command == null || _command.isEmpty())
			return;
		
		if (!getClient().performAction(FloodProtector.SERVER_BYPASS))
			return;
		
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (BypassCommandManager.getInstance().notify(player, _command))
			return;
		
		if (_command.startsWith("admin_"))
		{
			String command = _command.split(" ")[0];
			
			final IAdminCommandHandler ach = AdminCommandHandler.getInstance().getHandler(command);
			if (ach == null)
			{
				if (player.isGM())
					player.sendMessage("The command " + command.substring(6) + " doesn't exist.");
				
				LOGGER.warn("No handler registered for admin command '{}'.", command);
				return;
			}
			
			if (!AdminData.getInstance().hasAccess(command, player.getAccessLevel()))
			{
				player.sendMessage("You don't have the access rights to use this command.");
				LOGGER.warn("{} tried to use admin command '{}' without proper Access Level.", player.getName(), command);
				return;
			}
			
			if (Config.GMAUDIT)
				GMAUDIT_LOG.info(player.getName() + " [" + player.getObjectId() + "] used '" + _command + "' command on: " + ((player.getTarget() != null) ? player.getTarget().getName() : "none"));
			
			ach.useAdminCommand(_command, player);
		}
		else if (_command.startsWith("questnav"))
		{
			StringTokenizer st = new StringTokenizer(_command, " ");
			st.nextToken();
			
			int questId = Integer.parseInt(st.nextToken());
			int page = Integer.parseInt(st.nextToken());
			
			QuestHolder quest = QuestData.getInstance().getQuest(questId);
			if (quest != null)
			{
				QuestManager.getInstance().showMenuQuest(player, page);
			}
		}
		else if (_command.startsWith("questnav2"))
		{
			StringTokenizer st = new StringTokenizer(_command, " ");
			st.nextToken();
			
			int questId = Integer.parseInt(st.nextToken());
			int page = Integer.parseInt(st.nextToken());
			
			QuestHolder quest = QuestData.getInstance().getQuest(questId);
			if (quest != null)
			{
				QuestManager.getInstance().showCompleteQuest(player, quest, page);
			}
		}
		else if (_command.startsWith("questnotify"))
		{
			StringTokenizer st = new StringTokenizer(_command, " ");
			st.nextToken();
			
			if (!st.hasMoreTokens())
				return;
			
			String type = st.nextToken();
			String status = st.hasMoreTokens() ? st.nextToken() : "false";
			
			boolean enable = status.equalsIgnoreCase("on") || status.equalsIgnoreCase("true");
			
			if (type.equalsIgnoreCase("html"))
			{
				player.setQuestNotifyHtml(enable);
				player.sendMessage("Notificação via HTML " + (enable ? "ativada" : "desativada") + ".");
			}
			else if (type.equalsIgnoreCase("chat"))
			{
				player.setQuestNotifyChat(enable);
				player.sendMessage("Notificação via chat " + (enable ? "ativada" : "desativada") + ".");
			}
			QuestManager.getInstance().showMenuQuest(player, 1);
		}
		
		else if (_command.startsWith("setquest"))
		{
			StringTokenizer st = new StringTokenizer(_command, " ");
			st.nextToken();
			
			int setQuestId = Integer.parseInt(st.nextToken());
			
			QuestHolder quest = QuestData.getInstance().getQuest(setQuestId);
			if (quest == null)
			{
				if (player.isGM())
				{
					player.sendMessage("Quest id invalid.");
				}
				return;
			}
			
			boolean isClassValid = quest.getRequiredClasses().isEmpty() || quest.getRequiredClasses().contains(player.getClassId().getId());
			if (!isClassValid)
			{
				player.sendMessage("You cannot access this quest because your class does not match.");
				return;
			}
			
			for (QuestObjective obj : quest.getObjectives())
			{
				player.resetQuestKillCount(quest.getId(), obj.getNpcId());
			}
			
			player.setQuestCompleted(quest.getId(), false);
			
			QuestManager.getInstance().showMenuQuest(player, 1);
			
		}
		else if (_command.startsWith("dungeon"))
		{
			final Tokenizer tokenizer = new Tokenizer(_command);
			final String param = tokenizer.getToken(1);
			if (param == null)
			{
				player.sendMessage("Invalid command parameter.");
				return;
			}
			switch (param.toLowerCase())
			{
				case "enter":
					DungeonManager.getInstance().handleEnterDungeonId(player, tokenizer);
					break;
				
				default:
					player.sendMessage("Unknown command.");
					break;
			}
		}
		
		else if (_command.startsWith("droplist"))
		{
			StringTokenizer st = new StringTokenizer(_command, " ");
			st.nextToken();
			
			int npcId = Integer.parseInt(st.nextToken());
			int page = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
			
			if (st.hasMoreTokens())
			{
				int itemId = Integer.parseInt(st.nextToken());
				DropSkipManager.getInstance().toggleSkip(player.getObjectId(), itemId);
				
				DropSkipManager.getInstance().loadPlayer(player.getObjectId());
			}
			
			DropListUI.sendNpcDrop(player, npcId, page);
		}
		
		else if (_command.startsWith("player_help "))
		{
			final String path = _command.substring(12);
			if (path.indexOf("..") != -1)
				return;
			
			final StringTokenizer st = new StringTokenizer(path);
			final String[] cmd = st.nextToken().split("#");
			
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile(player.getLocale(), "html/help/" + cmd[0]);
			if (cmd.length > 1)
			{
				final int itemId = Integer.parseInt(cmd[1]);
				html.setItemId(itemId);
				
				if (itemId == 7064 && cmd[0].equalsIgnoreCase("lidias_diary/7064-16.htm"))
				{
					final QuestState qs = player.getQuestList().getQuestState("Q023_LidiasHeart");
					if (qs != null && qs.getCond() == 5 && qs.getInteger("diary") == 0)
						qs.set("diary", "1");
				}
			}
			html.disableValidation();
			player.sendPacket(html);
		}
		else if (_command.startsWith("npc_"))
		{
			if (!player.validateBypass(_command))
				return;
			
			int endOfId = _command.indexOf('_', 5);
			String id;
			if (endOfId > 0)
				id = _command.substring(4, endOfId);
			else
				id = _command.substring(4);
			
			try
			{
				final WorldObject object = World.getInstance().getObject(Integer.parseInt(id));
				if (!(object instanceof Npc npc) || endOfId <= 0)
				{
					player.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				final String bypassRest = _command.substring(endOfId + 1);
				if (npc.getNpcId() == 31540 && bypassRest.startsWith("Quest GrandBossTeleporter"))
				{
					player.getQuestList().setLastQuestNpcObjectId(npc.getObjectId());
					try
					{
						player.getQuestList().processQuestEvent("GrandBossTeleporter", "31540");
					}
					catch (Exception e)
					{
						LOGGER.warn("Klein (31540) quest event failed: bypass=[{}]", _command, e);
					}
					player.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				if (player.getAI() != null && player.getAI().canDoInteract(npc))
					npc.onBypassFeedback(player, bypassRest);
				player.sendPacket(ActionFailed.STATIC_PACKET);
			}
			catch (NumberFormatException nfe)
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
			}
		}
		else if (_command.startsWith("_trans") || _command.startsWith("user_translationmenu"))
		{
			
			if (DeeplTranslator.getInstance() != null) {
				if (_command.startsWith("user_translationmenu")) {
					
					DeeplTranslator.getInstance().showLanguageMenu(player);
				} else if (_command.startsWith("_trans set ")) {
					
					String langCode = _command.substring("_trans set ".length());
					DeeplTranslator.getInstance().setPlayerLanguage(player, langCode);
				} else if (_command.equals("_trans toggle")) {
					
					player.switchTranslatePreference();
					player.sendMessage("Tradução HTML " + (player.isHtmlTranslationEnabled() ? "ativada" : "desativada") + ".");
				} else if (_command.equals("_trans status")) {
					
					Object currentLang = player.getDeeplLanguage();
					String langName = "Inglês";
					if (currentLang != null) {
						try {
							
							java.lang.reflect.Method getDisplayName = currentLang.getClass().getMethod("getDisplayName");
							langName = (String) getDisplayName.invoke(currentLang);
						} catch (Exception e) {
							langName = currentLang.toString();
						}
					}
					player.sendMessage("Idioma atual: " + langName + " | Tradução HTML: " + (player.isHtmlTranslationEnabled() ? "Ativa" : "Inativa"));
				} else {
					final IBypassHandler handler = BypassHandler.getInstance().getHandler(_command);
					if (handler != null)
						handler.useBypass(_command, player, null);
				}
			} else {
				
				final IBypassHandler handler = BypassHandler.getInstance().getHandler(_command);
				if (handler != null)
					handler.useBypass(_command, player, null);
			}
		}

		else if (_command.startsWith("manor_menu_select?"))
		{
			WorldObject object = player.getTarget();
			if (object instanceof Npc targetNpc)
				targetNpc.onBypassFeedback(player, _command);
		}
		else if (_command.startsWith("bbs_") || _command.startsWith("_bbs") || _command.startsWith("_friend") || _command.startsWith("_mail") || _command.startsWith("_block") || _command.startsWith("_cbauction") || _command.startsWith("_cbmission"))
		{
			if (Config.ENABLE_CUSTOM_BBS)
				CustomCommunityBoard.getInstance().handleCommands(getClient(), _command);
			
			if (Config.ENABLE_COMMUNITY_BOARD)
				CommunityBoard.getInstance().handleCommands(getClient(), _command);
		}
		else if (_command.startsWith("Quest "))
		{
			if (!player.validateBypass(_command))
				return;
			
			String[] str = _command.substring(6).trim().split(" ", 2);
			try
			{
				if (str.length == 1)
					player.getQuestList().processQuestEvent(str[0], "");
				else
					player.getQuestList().processQuestEvent(str[0], str[1]);
			}
			catch (Exception e)
			{
				LOGGER.warn("Quest bypass failed: command=[{}] quest=[{}] event=[{}]", _command, str.length > 0 ? str[0] : "", str.length > 1 ? str[1] : "", e);
				player.sendPacket(ActionFailed.STATIC_PACKET);
			}
			return;
		}
		else if (_command.startsWith("_match"))
		{
			String params = _command.substring(_command.indexOf("?") + 1);
			StringTokenizer st = new StringTokenizer(params, "&");
			int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
			int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
			int heroid = HeroManager.getInstance().getHeroByClass(heroclass);
			if (heroid > 0)
				HeroManager.getInstance().showHeroFights(player, heroclass, heroid, heropage);
		}
		else if (_command.startsWith("_diary"))
		{
			String params = _command.substring(_command.indexOf("?") + 1);
			StringTokenizer st = new StringTokenizer(params, "&");
			int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
			int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
			int heroid = HeroManager.getInstance().getHeroByClass(heroclass);
			if (heroid > 0)
				HeroManager.getInstance().showHeroDiary(player, heroclass, heroid, heropage);
		}
		if (_command.startsWith("arenachange"))
		{
			final boolean isManager = player.getCurrentFolk() instanceof OlympiadManagerNpc;
			if (!isManager)
			{
				if (!player.isInObserverMode() || player.isInOlympiadMode() || player.getOlympiadGameId() < 0)
					return;
			}
			
			if (OlympiadManager.getInstance().isRegisteredInComp(player))
			{
				player.sendPacket(SystemMessageId.WHILE_YOU_ARE_ON_THE_WAITING_LIST_YOU_ARE_NOT_ALLOWED_TO_WATCH_THE_GAME);
				return;
			}
			
			final int arenaId = Integer.parseInt(_command.substring(12).trim());
			player.enterOlympiadObserverMode(arenaId);
		}
		if (_command.startsWith("report"))
			BotsPreventionManager.getInstance().analyseBypass(_command, player);
		if (_command.startsWith("QuestGatekeeper"))
		{
			String[] args = _command.substring(16).split(" ");
			
			int loc = Integer.parseInt(args[0]);
			int loc1 = Integer.parseInt(args[1]);
			int loc2 = Integer.parseInt(args[2]);
			int itemid = Integer.parseInt(args[3]);
			int count = Integer.parseInt(args[4]);
			
			if (player.getInventory().getItemByItemId(itemid) == null || player.getInventory().getItemByItemId(itemid).getCount() < count)
			{
				player.sendMessage("Incorrect item count. You need " + count + " " + itemid);
				return;
			}
			
			player.destroyItemByItemId(itemid, count, true);
			player.teleportTo(loc, loc1, loc2, 20);
		}
		if (_command.startsWith("npcfind_byid"))
		{
			String[] args = _command.substring(13).split(" ");
			final int raidId = Integer.parseInt(args[0]);
			
			final ASpawn spawn = SpawnManager.getInstance().getSpawn(raidId);
			if (spawn != null)
				player.getRadarList().addMarker(spawn.getSpawnLocation());
			else
			{
				final Npc raid = World.getInstance().getNpc(raidId);
				if (raid != null)
					player.getRadarList().addMarker(raid.getPosition());
			}
		}
		
		if (isBuffShopManagerCommand(_command))
		{
			BuffShopBypassHandler.getInstance().handleBypass(player, _command);
		}
		
		else if (_command.startsWith("voiced_"))
		{
			String command = _command.split(" ")[0];
			
			IVoicedCommandHandler ach = VoicedCommandHandler.getInstance().getHandler(_command.substring(7));
			
			if (ach == null)
			{
				player.sendMessage("The command " + command.substring(7) + " does not exist!");
				LOGGER.warn("No handler registered for command '" + _command + "'");
				return;
			}
			
			ach.useVoicedCommand(_command.substring(7), player, null);
		}
		if (_command.startsWith("autofarm"))
			AutoFarmManager.getInstance().handleBypass(player, _command.substring(9));
		if (_command.startsWith("menu_select"))
		{
			final WorldObject target = player.getTarget();
			if (target instanceof Npc npc)
			{
				final var list = npc.getTemplate().getEventQuests(EventHandler.AI_MENU_SELECTED);
				if (!list.isEmpty())
				{
					final Map<String, String> props = QueryParser.parse(_command);
					final int ask = Integer.parseInt(props.getOrDefault("ask", "0"));
					final int reply = Integer.parseInt(props.getOrDefault("reply", "0"));
					list.get(0).notifyMENU_SELECTED(npc, player, ask, reply);
				}
				else
					LOGGER.warn("pc[{}] target[{}] unhandle {}", player.getName(), target.getName(), _command);
			}
			else
				LOGGER.warn("pc[{}] target[{}] menu_select on incorrect target", player.getName(), target);
		}
		else if (_command.startsWith("radar"))
		{
			final StringTokenizer st = new StringTokenizer(_command, " ");
			st.nextToken();
			
			if (st.countTokens() == 3)
			{
				try
				{
					final int getLocX = Integer.parseInt(st.nextToken());
					final int getLocY = Integer.parseInt(st.nextToken());
					final int getLocZ = Integer.parseInt(st.nextToken());
					player.getRadarList().removeAllMarkers();
					
					player.getRadarList().addMarker(new Location(getLocX, getLocY, getLocZ));
					player.getRadarList().loadMarkers();
					player.sendMessage("Marker added to radar.");
				}
				catch (NumberFormatException e)
				{
					player.sendMessage("Invalid coordinates.");
				}
			}
			else
			{
				if (player.isGM())
					player.sendMessage("Invalid command format. Use: radar x y z");
			}
		}
		
		useBypassHandler(player);
	}
	
	private boolean isBuffShopManagerCommand(String fullCommand)
	{
		if (fullCommand == null || fullCommand.isEmpty())
		{
			return false;
		}
		
		String commandPart = fullCommand;
		int spaceIdx = fullCommand.indexOf(" ");
		if (spaceIdx != -1)
		{
			commandPart = fullCommand.substring(0, spaceIdx);
		}
		
		switch (commandPart)
		{
			case "index":
			case "setprice":
			case "settitle":
			case "setbuffs":
			case "close":
			case "setshop":
			case "list":
			case "add":
			case "del":
			case "startshop":
			case "stopshop":
			case "showShop":
			case "cast":
			case "remove_buff":
			case "remove_buffatt":
			case "manage_my_buffs":
			case "cast_confirm":
			case "shopskill":
			case "buy_skill":
			case "show_skill_shop":
				return true;
			default:
				return false;
		}
	}
	
	private void useBypassHandler(Player player)
	{
		_command = _command.replace("?", " ");
		final IBypassHandler handler = BypassHandler.getInstance().getHandler(_command);
		if (handler != null)
			handler.useBypass(_command, player, null);
	}
}
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
package ext.mods.gameserver.data.manager;

import java.io.File;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import ext.mods.Config;
import ext.mods.commons.lang.StringUtil;
import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ConnectionPool;
import ext.mods.commons.pool.ThreadPool;
import ext.mods.commons.random.Rnd;
import ext.mods.gameserver.enums.PunishmentType;
import ext.mods.gameserver.enums.RestartType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Monster;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.network.serverpackets.PledgeCrest;

public class BotsPreventionManager
{
	private static final CLogger LOGGER = new CLogger(BotsPreventionManager.class.getName());
	private static final String ACCESS_LEVEL = "UPDATE characters SET accesslevel=? WHERE obj_id=?";
	private static final String UPDATE_JAIL = "UPDATE characters SET x=-114356, y=-249645, z=-2984, punish_level=?, punish_timer=? WHERE obj_id=?";
	
	protected static Map<Integer, Integer> _monstersCounter = new HashMap<>();
	protected static Map<Integer, Future<?>> _beginValidation = new HashMap<>();
	protected static Map<Integer, PlayerData> _validation = new HashMap<>();
	protected static Map<Integer, byte[]> _images = new HashMap<>();
	
	protected int WINDOW_DELAY = 3;
	protected int VALIDATION_TIME = Config.VALIDATION_TIME * 1000;
	
	BotsPreventionManager()
	{
		getImages();
	}
	
	public void updateCounter(Creature player, Creature creature)
	{
		if (player instanceof Player killer && creature instanceof Monster)
		{
			if (_validation.get(killer.getObjectId()) != null)
				return;
			
			int count = 1;
			if (_monstersCounter.get(killer.getObjectId()) != null)
				count = _monstersCounter.get(killer.getObjectId()) + 1;
			
			int next = Rnd.nextInt(Config.KILLS_COUNTER_RANDOMIZATION);
			if (Config.KILLS_COUNTER + next < count)
			{
				validationTasks(killer);
				_monstersCounter.remove(killer.getObjectId());
			}
			else
				_monstersCounter.put(killer.getObjectId(), count);
		}
	}
	
	private static void getImages()
	{
		final File directory = new File(Config.DATA_PATH.resolve("./prevention").toString());
		directory.mkdirs();
		
		int i = 0;
		for (File file : directory.listFiles())
		{
			if (!file.getName().endsWith(".dds"))
				continue;
			
			byte[] data;
			
			try (RandomAccessFile f = new RandomAccessFile(file, "r"))
			{
				data = new byte[(int) f.length()];
				f.readFully(data);
			}
			catch (Exception e)
			{
				continue;
			}
			
			_images.put(i, data);
			i++;
		}
	}
	
	public void preValidationWindow(Player player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(1);
		StringBuilder tb = new StringBuilder();
		StringUtil.append(tb, "<html>");
		StringUtil.append(tb, "<title>Bots prevention</title>");
		StringUtil.append(tb, "<body><center><br><br><img src=\"L2UI_CH3.herotower_deco\" width=\"256\" height=\"32\">");
		StringUtil.append(tb, "<br><br><font color=\"a2a0a2\">if such window appears it means server suspect,<br1>that you may using cheating software.</font>");
		StringUtil.append(tb, "<br><br><font color=\"b09979\">if given answer results are incorrect or no action is made<br1>server is going to punish character instantly.</font>");
		StringUtil.append(tb, "<br><br><button value=\"CONTINUE\" action=\"bypass report_continue\" width=\"75\" height=\"21\" back=\"L2UI_CH3.Btn1_normal\" fore=\"L2UI_CH3.Btn1_normal\">");
		StringUtil.append(tb, "</center></body>");
		StringUtil.append(tb, "</html>");
		html.setHtml(tb.toString());
		player.sendPacket(html);
	}
	
	private static void validationWindow(Player player)
	{
		PlayerData container = _validation.get(player.getObjectId());
		NpcHtmlMessage html = new NpcHtmlMessage(1);
		
		StringBuilder tb = new StringBuilder();
		StringUtil.append(tb, "<html>");
		StringUtil.append(tb, "<title>Bots prevention</title>");
		StringUtil.append(tb, "<body><center><br><br><img src=\"L2UI_CH3.herotower_deco\" width=\"256\" height=\"32\">");
		StringUtil.append(tb, "<br><br><font color=\"a2a0a2\">in order to prove you are a human being<br1>you've to</font> <font color=\"b09979\">match colours within generated pattern:</font>");
		
		StringUtil.append(tb, "<br><br><img src=\"Crest.crest_" + Config.SERVER_ID + "_" + (_validation.get(player.getObjectId()).patternId) + "\" width=\"32\" height=\"32\"></td></tr>");
		StringUtil.append(tb, "<br><br><font color=b09979>click-on pattern of your choice beneath:</font>");
		
		StringUtil.append(tb, "<table><tr>");
		for (int i = 0; i < container.options.size(); i++)
			StringUtil.append(tb, "<td><button action=\"bypass -h report_" + i + "\" width=32 height=32 back=\"Crest.crest_" + Config.SERVER_ID + "_" + (container.options.get(i) + 1500) + "\" fore=\"Crest.crest_" + Config.SERVER_ID + "_" + (container.options.get(i) + 1500) + "\"></td>");
		
		StringUtil.append(tb, "</tr></table>");
		StringUtil.append(tb, "</center></body>");
		StringUtil.append(tb, "</html>");
		
		html.setHtml(tb.toString());
		player.sendPacket(html);
	}
	
	public void punishmentnWindow(Player player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(1);
		StringBuilder tb = new StringBuilder();
		StringUtil.append(tb, "<html>");
		StringUtil.append(tb, "<title>Bots prevention</title>");
		StringUtil.append(tb, "<body><center><br><br><img src=\"L2UI_CH3.herotower_deco\" width=\"256\" height=\"32\">");
		StringUtil.append(tb, "<br><br><font color=\"a2a0a2\">if such window appears, it means character haven't<br1>passed through prevention system.");
		StringUtil.append(tb, "<br><br><font color=\"b09979\">in such case character get moved to nearest town.</font>");
		StringUtil.append(tb, "</center></body>");
		StringUtil.append(tb, "</html>");
		html.setHtml(tb.toString());
		player.sendPacket(html);
	}
	
	public void validationTasks(Player player)
	{
		PlayerData container = new PlayerData();
		randomizeImages(container, player);
		
		for (int i = 0; i < container.options.size(); i++)
		{
			PledgeCrest packet = new PledgeCrest((container.options.get(i) + 1500), _images.get(container.options.get(i)));
			player.sendPacket(packet);
		}
		
		PledgeCrest packet = new PledgeCrest(container.patternId, _images.get(container.options.get(container.mainPattern)));
		player.sendPacket(packet);
		
		_validation.put(player.getObjectId(), container);
		
		Future<?> newTask = ThreadPool.schedule(new reportCheckTask(player), VALIDATION_TIME);
		ThreadPool.schedule(new countdown(player, VALIDATION_TIME / 1000), 0);
		_beginValidation.put(player.getObjectId(), newTask);
	}
	
	protected void randomizeImages(PlayerData container, Player player)
	{
		int buttonscount = 4;
		int imagescount = _images.size();
		
		for (int i = 0; i < buttonscount; i++)
		{
			int next = Rnd.nextInt(imagescount);
			while (container.options.indexOf(next) > -1)
			{
				next = Rnd.nextInt(imagescount);
			}
			container.options.add(next);
		}
		
		int mainIndex = Rnd.nextInt(buttonscount);
		container.mainPattern = mainIndex;
		
		Calendar token = Calendar.getInstance();
		String uniquetoken = Integer.toString(token.get(Calendar.DAY_OF_MONTH)) + Integer.toString(token.get(Calendar.HOUR_OF_DAY)) + Integer.toString(token.get(Calendar.MINUTE)) + Integer.toString(token.get(Calendar.SECOND)) + Integer.toString(token.get(Calendar.MILLISECOND) / 100);
		container.patternId = Integer.parseInt(uniquetoken);
	}
	
	protected void banPunishment(Player player)
	{
		_validation.remove(player.getObjectId());
		_beginValidation.get(player.getObjectId()).cancel(true);
		_beginValidation.remove(player.getObjectId());
		
		switch (Config.PUNISHMENT)
		{
			case 0:
				player.abortAll(true);
				player.teleportTo(RestartType.TOWN);
				punishmentnWindow(player);
				break;
			case 1:
				if (player.isOnline())
					player.logout(true);
				break;
			case 2:
				jailPunishment(player, Config.PUNISHMENT_TIME * 60);
				break;
			case 3:
				changeAccessLevel(player, -100);
				break;
		}
		
		player.sendMessage("Unfortunately, colours doesn't match.");
	}
	
	private static void jailPunishment(Player player, int delay)
	{
		if (player.isOnline())
			player.getPunishment().setType(PunishmentType.JAIL, Config.PUNISHMENT_TIME);
		else
		{
			try (Connection con = ConnectionPool.getConnection();
				PreparedStatement ps = con.prepareStatement(UPDATE_JAIL))
			{
				ps.setInt(1, PunishmentType.JAIL.ordinal());
				ps.setLong(2, (delay > 0 ? delay * Config.PUNISHMENT_TIME * 100 : 0));
				ps.setInt(3, player.getObjectId());
				ps.execute();
			}
			catch (SQLException se)
			{
				player.sendMessage("SQLException while jailing player");
			}
		}
	}
	
	private static void changeAccessLevel(Player targetPlayer, int lvl)
	{
		if (targetPlayer.isOnline())
		{
			targetPlayer.setAccessLevel(lvl);
			targetPlayer.logout(false);
		}
		else
		{
			try (Connection con = ConnectionPool.getConnection();
				PreparedStatement statement = con.prepareStatement(ACCESS_LEVEL))
			{
				statement.setInt(1, lvl);
				statement.setInt(2, targetPlayer.getObjectId());
				statement.execute();
				statement.close();
			}
			catch (SQLException se)
			{
				se.printStackTrace();
			}
		}
	}
	
	public void analyseBypass(String command, Player player)
	{
		if (!_validation.containsKey(player.getObjectId()))
			return;
		
		String params = command.substring(command.indexOf("_") + 1);
		
		if (params.startsWith("continue"))
		{
			validationWindow(player);
			_validation.get(player.getObjectId()).firstWindow = false;
			return;
		}
		
		int choosenOption = -1;
		if (tryParseInt(params))
			choosenOption = Integer.parseInt(params);
		
		if (choosenOption > -1)
		{
			PlayerData playerData = _validation.get(player.getObjectId());
			if (choosenOption != playerData.mainPattern)
			{
				banPunishment(player);
				if (Config.BOTS_LOGS)
					LOGGER.info("Detected possible bot " + player);
			}
			else
			{
				player.sendMessage(player.getSysString(10_010));
				_validation.remove(player.getObjectId());
				_beginValidation.get(player.getObjectId()).cancel(true);
				_beginValidation.remove(player.getObjectId());
			}
		}
	}
	
	protected class countdown implements Runnable
	{
		private final Player _player;
		private int _time;
		
		public countdown(Player player, int time)
		{
			_time = time;
			_player = player;
		}
		
		@Override
		public void run()
		{
			if (_player.isOnline())
			{
				if (_validation.containsKey(_player.getObjectId()) && _validation.get(_player.getObjectId()).firstWindow)
				{
					if (_time % WINDOW_DELAY == 0)
						preValidationWindow(_player);
				}
				
				switch (_time)
				{
					case 300:
					case 240:
					case 180:
					case 120:
					case 60:
						_player.sendMessage(_time / 60 + " minute(s) to match colors.");
						break;
					case 30:
					case 10:
					case 5:
					case 4:
					case 3:
					case 2:
					case 1:
						_player.sendMessage(_time + " second(s) to match colors!");
						break;
				}
				
				if (_time > 1 && _validation.containsKey(_player.getObjectId()))
					ThreadPool.schedule(new countdown(_player, _time - 1), 1000);
			}
		}
	}
	
	protected boolean tryParseInt(String value)
	{
		try
		{
			Integer.parseInt(value);
			return true;
		}
		
		catch (NumberFormatException e)
		{
			return false;
		}
	}
	
	public void captchaSuccessfull(Player player)
	{
		if (_validation.get(player.getObjectId()) != null)
			_validation.remove(player.getObjectId());
	}
	
	public Boolean isAlredyInReportMode(Player player)
	{
		if (_validation.get(player.getObjectId()) != null)
			return true;
		
		return false;
	}
	
	private class reportCheckTask implements Runnable
	{
		private final Player _player;
		
		public reportCheckTask(Player player)
		{
			_player = player;
		}
		
		@Override
		public void run()
		{
			if (_validation.get(_player.getObjectId()) != null)
				banPunishment(_player);
		}
	}
	
	public class PlayerData
	{
		public PlayerData()
		{
			firstWindow = true;
		}
		
		public int mainPattern;
		public List<Integer> options = new ArrayList<>();
		public boolean firstWindow;
		public int patternId;
	}
	
	public static final BotsPreventionManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final BotsPreventionManager INSTANCE = new BotsPreventionManager();
	}
}
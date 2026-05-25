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
package ext.mods.gameserver.custom.data;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;
import ext.mods.commons.pool.ConnectionPool;
import ext.mods.commons.pool.ThreadPool;

import ext.mods.Config;
import ext.mods.gameserver.data.sql.PlayerInfoTable;
import ext.mods.gameserver.enums.actors.Sex;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ItemList;
import ext.mods.gameserver.network.serverpackets.PledgeSkillList;
import ext.mods.gameserver.taskmanager.PremiumTaskManager;

import org.w3c.dom.Document;

public class DonateData implements IXmlReader
{
	private final List<Donate> _services = new ArrayList<>();
	private static final String UPDATE_PREMIUMSERVICE = "REPLACE INTO account_premium (premium_service,enddate,account_name) values(?,?,?)";
	
	public DonateData()
	{
		load();
	}
	
	public void reload()
	{
		_services.clear();
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("custom/mods/donate.xml");
		LOGGER.info("Loaded {} Donate Service data.", _services.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "donate", node ->
		{
			final StatSet set = parseAttributes(node);
			_services.add(new Donate(set));
		}));
	}
	
	public List<Donate> getDonate()
	{
		return _services;
	}
	
	public Donate getDonate(int id)
	{
		for (var donate : _services)
		{
			if (donate.service() == id)
				return donate;
		}
		return null;
	}
	
	public record Donate(int service, int duration, IntIntHolder price)
	{
		public Donate(StatSet set)
		{
			this(set.getInteger("id"), set.getInteger("value"), set.getIntIntHolder("price"));
		}
	}
	
	public static boolean isValidNick(String name)
	{
		boolean result = true;
		Pattern pattern;
		try
		{
			pattern = Pattern.compile(Config.DONATE_CNAME_TEMPLATE);
		}
		catch (PatternSyntaxException e)
		{
			pattern = Pattern.compile(".*");
		}
		
		Matcher regexp = pattern.matcher(name);
		if (!regexp.matches())
			result = false;
		
		return result;
	}
	
	public static void updateDatabasePremium(long time, String AccName)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement statement = con.prepareStatement(UPDATE_PREMIUMSERVICE))
		{
			statement.setInt(1, 1);
			statement.setLong(2, time);
			statement.setString(3, AccName);
			statement.execute();
		}
		catch (Exception e)
		{
			LOGGER.warn("updateDatabasePremium: Could not update data:" + e);
		}
	}
	
	public static void setNobless(Player player, Donate service)
	{
		
		if (player.getClassId().getLevel() < 3)
		{
			player.sendMessage(player.getSysString(10_020));
			return;
		}
		
		if (player.isNoble())
		{
			player.sendMessage(player.getSysString(10_021));
			return;
		}
		
		if (!player.destroyItemByItemId(service.price().getId(), service.price().getValue(), true))
			return;
		player.setNoble(true, true);
		player.broadcastUserInfo();
	}
	
	public static void setHero(Player player, Donate service)
	{
		final var price = service.price();
		
		if (player.isHero())
		{
			player.sendMessage(player.getSysString(10_022));
			return;
		}
		
		if (player.getInventory().getItemByItemId(price.getId()) == null || player.getInventory().getItemByItemId(price.getId()).getCount() < price.getValue())
		{
			player.sendMessage(player.getSysString(10_023, price.getValue()));
			return;
		}
		
		player.destroyItemByItemId(price.getId(), price.getValue(), true);
		player.sendPacket(new ItemList(player, false));
		
		player.setHero(true);
		player.setHeroUntil(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(service.duration()));
		player.store();
		player.sendMessage(player.getSysString(10_024, service.duration()));
		player.broadcastUserInfo();
		ThreadPool.schedule(new Runnable()
		{
			@Override
			public void run()
			{
				if (player.isOnline() && player.isHero())
				{
					player.setHero(false);
					player.setHeroUntil(0);
					player.store();
					player.broadcastUserInfo();
					player.sendMessage(player.getSysString(10_025));
				}
			}
		}, player.getHeroUntil() - System.currentTimeMillis());
	}
	
	public static void setNameColor(Player player, Donate service, String color)
	{
		final var price = service.price();
		int colorName = Integer.decode("0x" + color);
		if (!player.destroyItemByItemId(price.getId(), price.getValue(), true))
			return;
		player.getAppearance().setNameColor(colorName);
		player.setNameColor(colorName);
		player.broadcastUserInfo();
		player.store();
		player.sendMessage(player.getSysString(10_026));
	}
	
	public static void setTitleColor(Player player, Donate service, String color)
	{
		final var price = service.price();
		int colorTitle = Integer.decode("0x" + color);
		if (!player.destroyItemByItemId(price.getId(), price.getValue(), true))
			return;
		player.getAppearance().setTitleColor(colorTitle);
		player.setTitleColor(colorTitle);
		player.broadcastUserInfo();
		player.store();
		player.sendMessage(player.getSysString(10_027));
	}
	
	public static void setName(Player player, Donate service, String nick)
	{
		final var price = service.price();
		if (nick.length() < 1 || nick.length() > 16 || !isValidNick(nick))
		{
			player.sendMessage(player.getSysString(10_028));
			return;
		}
		
		if (Config.LIST_RESTRICTED_CHAR_NAMES.contains(nick.toLowerCase()))
		{
			player.sendMessage(player.getSysString(10_028));
			return;
		}
		
		if (PlayerInfoTable.getInstance().getPlayerObjectId(nick) > 0)
		{
			player.sendMessage(player.getSysString(10_029));
			return;
		}
		
		if (!player.destroyItemByItemId(price.getId(), price.getValue(), true))
			return;
		
		player.setName(nick);
		PlayerInfoTable.getInstance().updatePlayerData(player, false);
		
		player.store();
		player.broadcastUserInfo();
		
		if (player.getClan() != null)
			player.getClan().broadcastClanStatus();
		
		player.sendMessage(player.getSysString(10_030));
	}
	
	public static void setPremium(Player player, Donate service)
	{
		final var price = service.price();
		long premiumTime = 0L;
		
		if (!Config.USE_PREMIUM_SERVICE)
		{
			player.sendMessage(player.getSysString(10_031));
			return;
		}
		
		if (player.getPremServiceData() > Calendar.getInstance().getTimeInMillis())
		{
			player.sendMessage(player.getSysString(10_032));
			return;
		}
		
		try
		{
			Calendar now = Calendar.getInstance();
			now.add(Calendar.DATE, service.duration());
			premiumTime = now.getTimeInMillis();
		}
		catch (NumberFormatException nfe)
		{
			return;
		}
		
		if (!player.destroyItemByItemId(price.getId(), price.getValue(), true))
			return;
		
		player.setPremiumService(1);
		PremiumTaskManager.getInstance().add(player);
		updateDatabasePremium(premiumTime, player.getAccountName());
		player.sendMessage(player.getSysString(10_033, service.duration()));
		player.broadcastUserInfo();
	}
	
	public static void setGender(Player player, Donate service)
	{
		final var price = service.price();
		if (!player.destroyItemByItemId(price.getId(), price.getValue(), true))
			return;
		
		switch (player.getAppearance().getSex())
		{
			case MALE:
				player.getAppearance().setSex(Sex.FEMALE);
				break;
			
			case FEMALE:
				player.getAppearance().setSex(Sex.MALE);
				break;
		}
		
		player.store();
		player.broadcastUserInfo();
		player.sendMessage(player.getSysString(10_034));
		player.decayMe();
		player.spawnMe();
		player.logout(false);
	}
	
	public static void clearPK(Player player, Donate service)
	{
		final var price = service.price();
		
		if (player.getPkKills() == 0 && player.getKarma() == 0)
		{
			player.sendMessage(player.getSysString(10_035));
			return;
		}
		
		if (player.getInventory().getItemByItemId(price.getId()) == null)
		{
			player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
			return;
		}
		
		if (player.getInventory().getItemByItemId(price.getId()).getCount() < price.getValue())
		{
			player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
			return;
		}
		
		player.destroyItemByItemId(price.getId(), price.getValue(), true);
		player.setPkKills(0);
		player.setKarma(0);
		player.broadcastUserInfo();
		player.sendMessage(player.getSysString(10_036));
		
	}
	
	public static void setClanLevel(Player player, Donate service)
	{
		final var price = service.price();
		final var clanItemId = price.getId();
		
		if (player.getClan() == null)
			return;
		
		if (player.getClan().getLevel() == 8)
		{
			player.sendMessage(player.getSysString(10_037));
			return;
		}
		
		if (!player.isClanLeader())
		{
			player.sendMessage(player.getSysString(10_038));
			return;
		}
		
		if (player.getInventory().getItemByItemId(clanItemId) == null)
		{
			player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
			return;
		}
		
		if (player.getInventory().getItemByItemId(clanItemId).getCount() < price.getValue())
		{
			player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
			return;
		}
		
		if (!(service.duration() <= player.getClan().getLevel()))
		{
			player.destroyItemByItemId(clanItemId, price.getValue(), true);
			player.getClan().changeLevel(service.duration());
			player.sendMessage(player.getSysString(10_039));
		}
	}
	
	public static void addClanSkill(Player player, Donate service)
	{
		final var price = service.price();
		
		if (!player.isClanLeader())
		{
			player.sendMessage(player.getSysString(10_040));
			return;
		}
		
		if (player.getClan() == null || player.getClan().getLevel() < 5)
		{
			player.sendMessage(player.getSysString(10_041));
			return;
		}
		
		if (player.getInventory().getItemByItemId(price.getId()) == null)
		{
			player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
			return;
		}
		
		if (player.getInventory().getItemByItemId(price.getId()).getCount() < price.getValue())
		{
			player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
			return;
		}
		
		if (!player.getClan().addAllClanSkills())
		{
			player.sendMessage(player.getSysString(10_042));
			return;
		}
		
		player.destroyItemByItemId(price.getId(), price.getValue(), true);
		player.getClan().addAllClanSkills();
		player.getClan().broadcastToMembers(new PledgeSkillList(player.getClan()));
		player.sendMessage(player.getSysString(10_043));
	}
	
	public static void addClanRep(Player player, Donate service)
	{
		final var price = service.price();
		
		if (!player.isClanLeader())
		{
			player.sendMessage(player.getSysString(10_038));
			return;
		}
		
		if (player.getClan() == null || player.getClan().getLevel() < 5)
		{
			player.sendMessage(player.getSysString(10_041));
			return;
		}
		
		if (player.getInventory().getItemByItemId(price.getId()) == null)
		{
			player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
			return;
		}
		
		if (player.getInventory().getItemByItemId(price.getId()).getCount() < price.getValue())
		{
			player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
			return;
		}
		
		player.destroyItemByItemId(price.getId(), price.getValue(), true);
		player.getClan().addReputationScore(service.duration());
		player.sendMessage(player.getSysString(10_044, player.getClan().getReputationScore()));
	}
	
	public static DonateData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final DonateData INSTANCE = new DonateData();
	}
}
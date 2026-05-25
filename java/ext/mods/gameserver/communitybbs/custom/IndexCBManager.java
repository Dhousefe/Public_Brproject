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
package ext.mods.gameserver.communitybbs.custom;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import ext.mods.Config;
import ext.mods.gameserver.communitybbs.manager.BaseBBSManager;
import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.data.sql.ClanTable;
import ext.mods.gameserver.data.sql.OfflineTradersTable;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.taskmanager.GameTimeTaskManager;

public class IndexCBManager extends BaseBBSManager
{
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	
	protected IndexCBManager()
	{
	}
	
	@Override
	public void parseCmd(String command, Player player)
	{
		if (command.startsWith("_bbshome"))
		{
			String content = HTMLData.getInstance().getHtm(player.getLocale(), CB_PATH + getFolder() + "index.htm");
			String noexpirespremium = "<font color=\"FF99FF\">--------</font>";
			content = content.replace("%name%", String.valueOf(player.getName()));
			content = content.replace("%accountName%", player.getAccountName());
			content = content.replace("%class%", player.getTemplate().getClassName());
			content = content.replace("%lvl%", String.valueOf(player.getStatus().getLevel()));
			content = content.replace("%players%", String.valueOf(World.getInstance().getPlayers().size()));
			content = content.replace("%pvpkills%", String.valueOf(player.getPvpKills()));
			content = content.replace("%pkkills%", String.valueOf(player.getPkKills()));
			content = content.replace("%hpRegen%", String.valueOf(getRateNumber(player.getStatus().getRegenHp())));
			content = content.replace("%mpRegen%", String.valueOf(getRateNumber(player.getStatus().getRegenMp())));
			content = content.replace("%cpRegen%", String.valueOf(getRateNumber(player.getStatus().getRegenCp())));
			content = content.replace("%rec%", String.valueOf(player.getRecomHave()));
			
			final Clan clan = ClanTable.getInstance().getClan(player.getClanId());
			
			if (clan != null)
				content = content.replaceAll("%clan%", clan.getName());
			else
				content = content.replaceAll("%clan%", "<font color=FF0000>No</font>");
			
			if (player.getPremServiceData() == 1)
			{
				content = content.replace("%rate_xp%", String.valueOf(getRateNumber(Config.PREMIUM_RATE_XP)));
				content = content.replace("%rate_sp%", String.valueOf(getRateNumber(Config.PREMIUM_RATE_SP)));
				content = content.replace("%rate_adena%", String.valueOf(getRateNumber(Config.PREMIUM_RATE_DROP_CURRENCY)));
				content = content.replace("%rate_seal%", String.valueOf(getRateNumber(Config.PREMIUM_RATE_DROP_SEAL_STONE)));
				content = content.replace("%rate_items%", String.valueOf(getRateNumber(Config.PREMIUM_RATE_DROP_ITEMS)));
				content = content.replace("%rate_spoil%", String.valueOf(getRateNumber(Config.PREMIUM_RATE_DROP_SPOIL)));
				content = content.replace("%rate_quest%", String.valueOf(getRateNumber(Config.PREMIUM_RATE_QUEST_DROP)));
				content = content.replace("%rate_raid%", String.valueOf(getRateNumber(Config.PREMIUM_RATE_DROP_ITEMS_BY_RAID)));
			}
			else
			{
				content = content.replace("%rate_xp%", String.valueOf(getRateNumber(Config.RATE_XP)));
				content = content.replace("%rate_sp%", String.valueOf(getRateNumber(Config.RATE_SP)));
				content = content.replace("%rate_adena%", String.valueOf(getRateNumber(Config.RATE_DROP_CURRENCY)));
				content = content.replace("%rate_seal%", String.valueOf(getRateNumber(Config.RATE_DROP_SEAL_STONE)));
				content = content.replace("%rate_items%", String.valueOf(getRateNumber(Config.RATE_DROP_ITEMS)));
				content = content.replace("%rate_spoil%", String.valueOf(getRateNumber(Config.RATE_DROP_SPOIL)));
				content = content.replace("%rate_quest%", String.valueOf(getRateNumber(Config.RATE_QUEST_DROP)));
				content = content.replace("%rate_raid%", String.valueOf(getRateNumber(Config.RATE_DROP_ITEMS_BY_RAID)));
			}
			
			content = content.replace("%premium%", player.getPremiumService() == 1 ? "<font color=00FF00>ON</font>" : "<font color=FF0000>OFF</font>");
			content = content.replace("%premiumEnd%", player.getPremiumService() == 1 ? DATE_FORMAT.format(player.getPremServiceData()) : "");
			
			if (player.getPremiumService() == 0)
			{
				content = content.replace("%nameaccount%", player.getAccountName());
				content = content.replace("%ip%", player.getClient().getConnection().getInetAddress().getHostAddress());
				content = content.replace("%charId%", String.valueOf(player.getObjectId()));
				content = content.replace("%playername%", player.getName());
				content = content.replace("%expirespremium%", noexpirespremium);
				content = content.replace("%statuspremium%", "<font color=\"FF99FF\">Normal</font>");
				
				if (player.getStatus().getLevel() >= 1 && player.getStatus().getLevel() <= 39)
				{
					content = content.replace("%server_rate_xp%", String.valueOf(20));
				}
				if (player.getStatus().getLevel() >= 40 && player.getStatus().getLevel() <= 51)
				{
					content = content.replace("%server_rate_xp%", String.valueOf(20));
				}
				else if (player.getStatus().getLevel() >= 52 && player.getStatus().getLevel() <= 61)
				{
					content = content.replace("%server_rate_xp%", String.valueOf(10));
				}
				if (player.getStatus().getLevel() >= 62 && player.getStatus().getLevel() <= 75)
				{
					content = content.replace("%server_rate_xp%", String.valueOf(5));
				}
				else if (player.getStatus().getLevel() >= 76)
				{
					content = content.replace("%server_rate_xp%", String.valueOf(2));
				}
				
				content = content.replace("%server_rate_sp%", String.valueOf(getRateNumber(Config.RATE_SP)));
				content = content.replace("%server_rate_adena%", String.valueOf(getRateNumber(Config.RATE_DROP_CURRENCY)));
				content = content.replace("%server_rate_items%", String.valueOf(getRateNumber(Config.RATE_DROP_ITEMS)));
				content = content.replace("%server_rate_spoil%", String.valueOf(getRateNumber(Config.RATE_DROP_SPOIL)));
				content = content.replace("%server_rate_quest%", String.valueOf(getRateNumber(Config.RATE_QUEST_DROP)));
				content = content.replace("%server_quest_reward%", String.valueOf(getRateNumber(Config.RATE_QUEST_REWARD)));
				content = content.replace("%server_r_drop%", String.valueOf(getRateNumber(Config.RATE_DROP_ITEMS_BY_RAID)));
				content = content.replace("%server_g_drop%", String.valueOf(getRateNumber(Config.RATE_DROP_ITEMS_BY_GRAND)));
				content = content.replace("%server_karma_drop%", String.valueOf(getRateNumber(Config.KARMA_RATE_DROP)));
			}
			else
			{
				content = content.replace("%nameaccount%", player.getAccountName());
				content = content.replace("%ip%", player.getClient().getConnection().getInetAddress().getHostAddress());
				content = content.replace("%charId%", String.valueOf(player.getObjectId()));
				content = content.replace("%playername%", player.getName());
				content = content.replace("%expirespremium%", String.valueOf("<font color=\"00FF7F\">" + DATE_FORMAT.format(player.getPremServiceData()) + "</font>"));
				content = content.replace("%statuspremium%", "<font color=\"00FF7F\">Premium</font>");
				
				if (player.getStatus().getLevel() >= 1 && player.getStatus().getLevel() <= 39)
				{
					content = content.replace("%server_rate_xp%", String.valueOf(26));
				}
				if (player.getStatus().getLevel() >= 40 && player.getStatus().getLevel() <= 51)
				{
					content = content.replace("%server_rate_xp%", String.valueOf(26));
				}
				else if (player.getStatus().getLevel() >= 52 && player.getStatus().getLevel() <= 61)
				{
					content = content.replace("%server_rate_xp%", String.valueOf(13));
				}
				if (player.getStatus().getLevel() >= 62 && player.getStatus().getLevel() <= 75)
				{
					content = content.replace("%server_rate_xp%", String.valueOf(6.5));
				}
				else if (player.getStatus().getLevel() >= 76)
				{
					content = content.replace("%server_rate_xp%", String.valueOf(2.6));
				}
				
				content = content.replace("%server_rate_sp%", String.valueOf(getRateNumber(Config.PREMIUM_RATE_SP)));
				content = content.replace("%server_rate_adena%", String.valueOf(getRateNumber(Config.PREMIUM_RATE_DROP_CURRENCY)));
				content = content.replace("%server_rate_items%", String.valueOf(getRateNumber(Config.PREMIUM_RATE_DROP_ITEMS)));
				content = content.replace("%server_rate_spoil%", String.valueOf(getRateNumber(Config.PREMIUM_RATE_DROP_SPOIL)));
				content = content.replace("%server_rate_quest%", String.valueOf(getRateNumber(Config.PREMIUM_RATE_QUEST_DROP)));
				content = content.replace("%server_quest_reward%", String.valueOf(getRateNumber(Config.PREMIUM_RATE_QUEST_REWARD)));
				content = content.replace("%server_r_drop%", String.valueOf(getRateNumber(Config.RATE_DROP_ITEMS_BY_RAID)));
				content = content.replace("%server_g_drop%", String.valueOf(getRateNumber(Config.RATE_DROP_ITEMS_BY_GRAND)));
				content = content.replace("%server_karma_drop%", String.valueOf(getRateNumber(Config.KARMA_RATE_DROP)));
			}
			
			content = content.replace("%rate_party_xp%", String.valueOf(getRateNumber(Config.RATE_PARTY_XP)));
			content = content.replace("%rate_party_sp%", String.valueOf(getRateNumber(Config.RATE_PARTY_SP)));
			content = content.replace("%rate_drop_manor%", String.valueOf(getRateNumber(Config.RATE_DROP_MANOR)));
			content = content.replace("%pet_rate_xp%", String.valueOf(getRateNumber(Config.PET_XP_RATE)));
			content = content.replace("%sineater_rate_xp%", String.valueOf(getRateNumber(Config.SINEATER_XP_RATE)));
			content = content.replace("%pet_food_rate%", String.valueOf(getRateNumber(Config.PET_FOOD_RATE)));
			content = content.replace("%server_stone_top%", String.valueOf(getRateNumber(Config.AUGMENTATION_TOP_SKILL_CHANCE)));
			content = content.replace("%server_stone_high%", String.valueOf(getRateNumber(Config.AUGMENTATION_HIGH_SKILL_CHANCE)));
			content = content.replace("%server_stone_mid%", String.valueOf(getRateNumber(Config.AUGMENTATION_MID_SKILL_CHANCE)));
			content = content.replace("%server_stone_low%", String.valueOf(getRateNumber(Config.AUGMENTATION_NG_SKILL_CHANCE)));
			content = content.replace("%deathpenality%", String.valueOf(getRateNumber(Config.DEATH_PENALTY_CHANCE)));
			content = content.replace("%level%", String.valueOf(player.getStatus().getLevel()));
			content = content.replace("%currentdate%", String.valueOf(DATE_FORMAT.format(System.currentTimeMillis())));
			
			Clan playerClan = ClanTable.getInstance().getClan(player.getClanId());
			if (playerClan != null)
			{
				content = content.replace("%clan%", playerClan.getName());
			}
			else
			{
				content = content.replace("%clan%", "No");
			}
			
			if (player.isSubClassActive())
			{
				content = content.replace("%subclass%", String.valueOf("SubClass"));
			}
			else
			{
				content = content.replace("%subclass%", String.valueOf("Main"));
			}
			
			content = content.replace("%xp%", String.valueOf(getLongNumber(player.getStatus().getExp())));
			content = content.replace("%sp%", String.valueOf(getIntNumber(player.getStatus().getSp())));
			content = content.replace("%class%", player.getTemplate().getClassName());
			content = content.replace("%ordinal%", String.valueOf(player.getClassId().ordinal()));
			content = content.replace("%classid%", String.valueOf(player.getClassId()));
			content = content.replace("%x%", String.valueOf(player.getX()));
			content = content.replace("%y%", String.valueOf(player.getY()));
			content = content.replace("%z%", String.valueOf(player.getZ()));
			content = content.replace("%currenthp%", String.valueOf(getDoubleNumber(player.getStatus().getHp())));
			content = content.replace("%maxhp%", String.valueOf(getDoubleNumber(player.getStatus().getMaxHp())));
			content = content.replace("%currentmp%", String.valueOf(getDoubleNumber(player.getStatus().getMp())));
			content = content.replace("%maxmp%", String.valueOf(getDoubleNumber(player.getStatus().getMaxMp())));
			content = content.replace("%currentcp%", String.valueOf(getDoubleNumber(player.getStatus().getCp())));
			content = content.replace("%maxcp%", String.valueOf(getDoubleNumber(player.getStatus().getMaxCp())));
			content = content.replace("%karma%", String.valueOf(getIntNumber(player.getKarma())));
			content = content.replace("%pvpflag%", String.valueOf(player.getPvpFlag()));
			content = content.replace("%pvpkills%", String.valueOf(getIntNumber(player.getPvpKills())));
			content = content.replace("%pkkills%", String.valueOf(getIntNumber(player.getPkKills())));
			content = content.replace("%patk%", String.valueOf(getIntNumber(player.getStatus().getPAtk(null))));
			content = content.replace("%matk%", String.valueOf(getIntNumber(player.getStatus().getMAtk(null, null))));
			content = content.replace("%pdef%", String.valueOf(getIntNumber(player.getStatus().getPDef(null))));
			content = content.replace("%mdef%", String.valueOf(getIntNumber(player.getStatus().getMDef(null, null))));
			content = content.replace("%accuracy%", String.valueOf(getIntNumber(player.getStatus().getAccuracy())));
			content = content.replace("%evasion%", String.valueOf(getIntNumber(player.getStatus().getEvasionRate(null))));
			content = content.replace("%critical%", String.valueOf(getIntNumber(player.getStatus().getCriticalHit(null, null))));
			content = content.replace("%mcritical%", String.valueOf(getIntNumber(player.getStatus().getMCriticalHit(null, null))));
			content = content.replace("%runspeed%", String.valueOf(getFloatNumber(player.getStatus().getMoveSpeed())));
			content = content.replace("%patkspd%", String.valueOf(getIntNumber(player.getStatus().getPAtkSpd())));
			content = content.replace("%matkspd%", String.valueOf(getIntNumber(player.getStatus().getMAtkSpd())));
			content = content.replace("%access%", String.valueOf(player.getAccessLevel().getLevel()));
			content = content.replace("%account%", player.getAccountName());
			content = content.replace("%max_players%", String.valueOf(World.getInstance().getPlayers().size()));
			content = content.replace("%trade%", player.getTradeRefusal() ? "<font color=00FF00>ON</font>" : "<font color=FF0000>OFF</font>");
			content = content.replace("%messagerefusal%", player.isBlockingAll() ? "<font color=00FF00>ON</font>" : "<font color=FF0000>OFF</font>");
			content = content.replace("%gainxp%", player.getStopExp() ? "<font color=00FF00>ON</font>" : "<font color=FF0000>OFF</font>");
			content = content.replace("%playeron%", String.valueOf(getRealOnline()));
			content = content.replace("%offmode%", String.valueOf(getRealOffline()));
			content = content.replace("%heroeon%", String.valueOf(getHeroOnline()));
			content = content.replace("%leaderon%", String.valueOf(getClanLeaderOnline()));
			content = content.replace("%noblesseon%", String.valueOf(getNobleOnline()));
			content = content.replace("%jailon%", String.valueOf(getJailOnline()));
			content = content.replace("%gmon%", String.valueOf(getGMOnline()));
			
			if (player.isNoble())
			{
				content = content.replace("%noble%", "Yes");
			}
			else
			{
				content = content.replace("%noble%", "No");
			}
			
			content = content.replace("%time%", GameTimeTaskManager.getInstance().getGameTimeFormated());
			
			separateAndSend(content, player);
		}
		else
			super.parseCmd(command, player);
	}
	
	public String getRealOnline()
	{
		int counter = 0;
		Iterator<Player> var2 = World.getInstance().getPlayers().iterator();
		
		while (var2.hasNext())
		{
			Player onlinePlayer = var2.next();
			if (onlinePlayer.getClient() != null && !OfflineTradersTable.offlineMode(onlinePlayer) && !onlinePlayer.isGM())
			{
				++counter;
			}
		}
		
		String realOnline = "" + counter + "";
		return realOnline;
	}
	
	public String getRealOffline()
	{
		int counter = 0;
		Iterator<Player> var2 = World.getInstance().getPlayers().iterator();
		
		while (var2.hasNext())
		{
			Player onlinePlayer = var2.next();
			if (onlinePlayer.isInStoreMode() && OfflineTradersTable.offlineMode(onlinePlayer) && !onlinePlayer.isGM())
			{
				++counter;
			}
		}
		
		String realOffline = "" + counter + "";
		return realOffline;
	}
	
	public String getHeroOnline()
	{
		int counter = 0;
		Iterator<Player> var2 = World.getInstance().getPlayers().iterator();
		
		while (var2.hasNext())
		{
			Player onlinePlayer = var2.next();
			if (onlinePlayer.getClient() != null && onlinePlayer.isHero() && !onlinePlayer.isGM())
			{
				++counter;
			}
		}
		
		String heroOnline = "" + counter + "";
		return heroOnline;
	}
	
	public String getClanLeaderOnline()
	{
		int counter = 0;
		Iterator<Player> var2 = World.getInstance().getPlayers().iterator();
		
		while (var2.hasNext())
		{
			Player onlinePlayer = var2.next();
			if (onlinePlayer.getClient() != null && onlinePlayer.isClanLeader() && !onlinePlayer.isGM())
			{
				++counter;
			}
		}
		
		String clanLeaderOnline = "" + counter + "";
		return clanLeaderOnline;
	}
	
	public String getNobleOnline()
	{
		int counter = 0;
		Iterator<Player> var2 = World.getInstance().getPlayers().iterator();
		
		while (var2.hasNext())
		{
			Player onlinePlayer = var2.next();
			if (onlinePlayer.getClient() != null && onlinePlayer.isNoble() && !onlinePlayer.isGM())
			{
				++counter;
			}
		}
		
		String nobleOnline = "" + counter + "";
		return nobleOnline;
	}
	
	public String getJailOnline()
	{
		int counter = 0;
		Iterator<Player> var2 = World.getInstance().getPlayers().iterator();
		
		while (var2.hasNext())
		{
			Player onlinePlayer = var2.next();
			if (onlinePlayer.getClient() != null && onlinePlayer.isInJail() && !onlinePlayer.isGM())
			{
				++counter;
			}
		}
		
		String jailOnline = "" + counter + "";
		return jailOnline;
	}
	
	public String getGMOnline()
	{
		int counter = 0;
		Iterator<Player> var2 = World.getInstance().getPlayers().iterator();
		
		while (var2.hasNext())
		{
			Player onlinePlayer = var2.next();
			if (onlinePlayer.getClient() != null && onlinePlayer.isGM())
			{
				++counter;
			}
		}
		
		String gmOnline = "" + counter + "";
		return gmOnline;
	}
	
	public static String getDoubleNumber(double number)
	{
		DecimalFormat df = new DecimalFormat("##,###");
		String fivenumbers = df.format(number);
		return fivenumbers;
	}
	
	public static String getRateNumber(double number)
	{
		DecimalFormat df = new DecimalFormat("##.##");
		String ratenumbers = df.format(number);
		return ratenumbers;
	}
	
	public static String getIntNumber(int number)
	{
		DecimalFormat df = new DecimalFormat("#,###,###,###");
		String billionnumbers = df.format(number);
		return billionnumbers;
	}
	
	public static String getLongNumber(long number)
	{
		DecimalFormat df = new DecimalFormat("#,###,###,###");
		String billionnumbers = df.format(number);
		return billionnumbers;
	}
	
	public static String getFloatNumber(float number)
	{
		DecimalFormat df = new DecimalFormat("#,###");
		String fournumbers = df.format(number);
		return fournumbers;
	}
	
	@Override
	protected String getFolder()
	{
		return "custom/";
	}
	
	public static IndexCBManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final IndexCBManager INSTANCE = new IndexCBManager();
	}
}
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
package ext.mods;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Stream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ext.mods.commons.config.ExProperties;
import ext.mods.commons.logging.CLogger;
import ext.mods.gameserver.enums.GeoType;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.model.olympiad.enums.OlympiadPeriod;
import ext.mods.protection.hwid.crypt.FirstKey;

/**
 * This class contains global server configuration.<br>
 * It has static final fields initialized from configuration files.
 */
public final class Config
{
	private Config()
	{
		throw new IllegalStateException("Utility class");
	}
	
	private static final CLogger LOGGER = new CLogger(Config.class.getName());
	
	public static final boolean DEV_MODE = System.getProperty("ext.mods.Config.devMode", "false").equalsIgnoreCase("true");
	
	public static final Path BASE_PATH = Path.of(System.getProperty("ext.mods.Config.basePath", "."));

	public static final Path DATA_PATH = Path.of(System.getProperty("ext.mods.Config.dataPath", "data"));
	
	public static final Path CONFIG_PATH = Path.of(System.getProperty("ext.mods.Config.configPath", "config"));
	
	private static final String CHAT_FILTER_FILE = CONFIG_PATH.resolve("chatfilter.txt").toString();
	
	private static final String BOSS_HEAL_FILE = CONFIG_PATH.resolve("bossHeal.properties").toString();
	private static final String CLANS_FILE = CONFIG_PATH.resolve("clans.properties").toString();
	private static final String EVENTS_FILE = CONFIG_PATH.resolve("events.properties").toString();
	public static final String GEOENGINE_FILE = CONFIG_PATH.resolve("geoengine.properties").toString();
	private static final String HEXID_FILE = CONFIG_PATH.resolve("hexid.txt").toString();
	private static final String LANGUAGE_FILE = CONFIG_PATH.resolve("language.properties").toString();
	private static final String LOGINSERVER_FILE = CONFIG_PATH.resolve("loginserver.properties").toString();
	private static final String NPCS_FILE = CONFIG_PATH.resolve("npcs.properties").toString();
	private static final String OFFLINE_FILE = CONFIG_PATH.resolve("offlineshop.properties").toString();
	private static final String PLAYERS_FILE = CONFIG_PATH.resolve("players.properties").toString();
	private static final String RATES_FILE = CONFIG_PATH.resolve("rates.properties").toString();
	private static final String BR_FILE = CONFIG_PATH.resolve("project.properties").toString();
	private static final String SERVER_FILE = CONFIG_PATH.resolve("server.properties").toString();
	private static final String SAFE_DISCONNECT_FILE = CONFIG_PATH.resolve("safedisconnect.properties").toString();
	private static final String BOSS_ZERG_FILE = CONFIG_PATH.resolve("bosszerg.properties").toString();
	private static final String SIEGE_FILE = CONFIG_PATH.resolve("siege.properties").toString();
	private static final String PROTECTION_FILE = CONFIG_PATH.resolve("protection.properties").toString();
	private static final String TRANSLATOR_FILE = CONFIG_PATH.resolve("translator.properties").toString();
	private static final String ITEMS_FILE = CONFIG_PATH.resolve("items.properties").toString();
	

	/** hwid settings */
	public static byte[] GUARD_CLIENT_CRYPT_KEY;
	public static byte[] GUARD_CLIENT_CRYPT;
	public static byte[] GUARD_SERVER_CRYPT_KEY;
	public static byte[] GUARD_SERVER_CRYPT;
	public static boolean ALLOW_GUARD_SYSTEM;
	public static int PROTECT_WINDOWS_COUNT;
	public static int GET_CLIENT_HWID;
	public static boolean ENABLE_CONSOLE_LOG;
	public static boolean PROTECT_KICK_WITH_EMPTY_HWID;
	public static boolean PROTECT_KICK_WITH_LASTERROR_HWID;
	public static byte FST_KEY = 110;
	public static byte SCN_KEY = 36;
	public static byte ANP_KEY = -5;
	public static byte ULT_KEY = 12;
	public static int NPROTECT_KEY = -1;
	
	public static boolean BLOCK_HEAL_ON_RAIDBOSS;
	public static boolean BLOCK_HEAL_ON_GRANDBOSS;
	public static Set<Integer> BLOCKED_HEAL_SKILL_IDS = new HashSet<>();
	public static String HEAL_BLOCK_MESSAGE;

	private static final String BOSS_JEWEL_UPGRADES_FILE = CONFIG_PATH.resolve("BossJewelUpgrades.properties").toString();
    public static boolean UPGRADE_BOSS_JEWELS_ENCHANT;
    public static List<JewelUpgrade> UPGRADE_BOSS_JEWELS = new ArrayList<>();
    public static Set<Integer> UPGRADEABLE_BOSS_JEWELS = new HashSet<>();
    
    public static class JewelUpgrade {
        public final int itemId;
        public final int enchantLevel;
        public final int newItemId;

        public JewelUpgrade(int itemId, int enchantLevel, int newItemId) {
            this.itemId = itemId;
            this.enchantLevel = enchantLevel;
            this.newItemId = newItemId;
        }
    }
	
	
	/** Clans */
	public static int CLAN_JOIN_DAYS;
	public static int CLAN_CREATE_DAYS;
	public static int CLAN_DISSOLVE_DAYS;
	public static int ALLY_JOIN_DAYS_WHEN_LEAVED;
	public static int ALLY_JOIN_DAYS_WHEN_DISMISSED;
	public static int ACCEPT_CLAN_DAYS_WHEN_DISMISSED;
	public static int CREATE_ALLY_DAYS_WHEN_DISSOLVED;
	public static int MAX_NUM_OF_CLANS_IN_ALLY;
	public static int CLAN_MEMBERS_FOR_WAR;
	public static int CLAN_WAR_PENALTY_WHEN_ENDED;
	public static boolean MEMBERS_CAN_WITHDRAW_FROM_CLANWH;
	
	/** Manor */
	public static int MANOR_REFRESH_TIME;
	public static int MANOR_REFRESH_MIN;
	public static int MANOR_APPROVE_TIME;
	public static int MANOR_APPROVE_MIN;
	public static int MANOR_MAINTENANCE_MIN;
	public static int MANOR_SAVE_PERIOD_RATE;
	
	public static long CS_TELE_FEE_RATIO;
	public static int CS_TELE1_FEE;
	public static int CS_TELE2_FEE;
	public static long CS_MPREG_FEE_RATIO;
	public static int CS_MPREG1_FEE;
	public static int CS_MPREG2_FEE;
	public static int CS_MPREG3_FEE;
	public static int CS_MPREG4_FEE;
	public static long CS_HPREG_FEE_RATIO;
	public static int CS_HPREG1_FEE;
	public static int CS_HPREG2_FEE;
	public static int CS_HPREG3_FEE;
	public static int CS_HPREG4_FEE;
	public static int CS_HPREG5_FEE;
	public static long CS_EXPREG_FEE_RATIO;
	public static int CS_EXPREG1_FEE;
	public static int CS_EXPREG2_FEE;
	public static int CS_EXPREG3_FEE;
	public static int CS_EXPREG4_FEE;
	public static long CS_SUPPORT_FEE_RATIO;
	public static int CS_SUPPORT1_FEE;
	public static int CS_SUPPORT2_FEE;
	public static int CS_SUPPORT3_FEE;
	public static int CS_SUPPORT4_FEE;
	
	
	/** Olympiad */
	public static boolean OLY_ENABLED;
	public static int OLY_START_TIME;
	public static int OLY_MIN;
	public static long OLY_CPERIOD;
	public static long OLY_BATTLE;
	public static long OLY_WPERIOD;
	public static long OLY_VPERIOD;
	public static int OLY_WAIT_TIME;
	public static int OLY_WAIT_BATTLE;
	public static int OLY_WAIT_END;
	public static int OLY_START_POINTS;
	public static int OLY_WEEKLY_POINTS;
	public static int OLY_MIN_MATCHES;
	public static int OLY_CLASSED;
	public static int OLY_NONCLASSED;
	/** Impede duelo Olympiad entre players da mesma maquina (mesmo HWID) para evitar farm de pontos. */
	public static boolean OLY_BLOCK_SAME_HWID;
	public static IntIntHolder[] OLY_CLASSED_REWARD;
	public static IntIntHolder[] OLY_NONCLASSED_REWARD;
	public static int OLY_GP_PER_POINT;
	public static int OLY_HERO_POINTS;
	public static int OLY_RANK1_POINTS;
	public static int OLY_RANK2_POINTS;
	public static int OLY_RANK3_POINTS;
	public static int OLY_RANK4_POINTS;
	public static int OLY_RANK5_POINTS;
	public static int OLY_MAX_POINTS;
	public static int OLY_DIVIDER_CLASSED;
	public static int OLY_DIVIDER_NON_CLASSED;
	public static boolean OLY_ANNOUNCE_GAMES;
	public static int OLY_ENCHANT_LIMIT;
	public static boolean OLY_SHOW_MONTHLY_WINNERS;
	
	/** SevenSigns Festival */
	public static boolean SEVEN_SIGNS_BYPASS_PREREQUISITES;
	public static int FESTIVAL_MIN_PLAYER;
	public static int MAXIMUM_PLAYER_CONTRIB;

	/** Subclass Village Master*/
	public static boolean SUBCLASS_REQUIRE_MIMIR;
	public static boolean SUBCLASS_REQUIRE_FATE;
	
	/** Four Sepulchers */
	public static int FS_PARTY_MEMBER_COUNT;
	
	/** dimensional rift */
	public static int RIFT_MIN_PARTY_SIZE;
	public static int RIFT_AUTO_JUMPS_TIME_MIN;
	public static int RIFT_AUTO_JUMPS_TIME_RND;
	public static int RIFT_ENTER_COST_RECRUIT;
	public static int RIFT_ENTER_COST_SOLDIER;
	public static int RIFT_ENTER_COST_OFFICER;
	public static int RIFT_ENTER_COST_CAPTAIN;
	public static int RIFT_ENTER_COST_COMMANDER;
	public static int RIFT_ENTER_COST_HERO;
	public static int RIFT_ANAKAZEL_PORT_CHANCE;
	
	/** Lottery */
	public static int LOTTERY_PRIZE;
	public static int LOTTERY_TICKET_PRICE;
	public static double LOTTERY_5_NUMBER_RATE;
	public static double LOTTERY_4_NUMBER_RATE;
	public static double LOTTERY_3_NUMBER_RATE;
	public static int LOTTERY_2_AND_1_NUMBER_PRIZE;
	
	/** Fishing tournament */
	public static boolean ALLOW_FISH_CHAMPIONSHIP;
	public static int FISH_CHAMPIONSHIP_REWARD_ITEM;
	public static int FISH_CHAMPIONSHIP_REWARD_1;
	public static int FISH_CHAMPIONSHIP_REWARD_2;
	public static int FISH_CHAMPIONSHIP_REWARD_3;
	public static int FISH_CHAMPIONSHIP_REWARD_4;
	public static int FISH_CHAMPIONSHIP_REWARD_5;
	
	public static int COFFER_PRICE_ID;
	public static int COFFER_PRICE_AMOUNT;
	
	public static boolean EVENT_COMMANDS;
	
	public static boolean CTF_EVENT_ENABLED;
	public static String[] CTF_EVENT_INTERVAL;
	public static int CTF_EVENT_PARTICIPATION_TIME;
	public static int CTF_EVENT_RUNNING_TIME;
	public static String CTF_NPC_LOC_NAME;
	public static int CTF_EVENT_PARTICIPATION_NPC_ID;
	public static int CTF_EVENT_TEAM_1_HEADQUARTERS_ID;
	public static int CTF_EVENT_TEAM_2_HEADQUARTERS_ID;
	public static int CTF_EVENT_TEAM_1_FLAG;
	public static int CTF_EVENT_TEAM_2_FLAG;
	public static int CTF_EVENT_CAPTURE_SKILL;
	public static int[] CTF_EVENT_PARTICIPATION_FEE = new int[2];
	public static int[] CTF_EVENT_PARTICIPATION_NPC_COORDINATES = new int[4];
	public static int CTF_EVENT_MIN_PLAYERS_IN_TEAMS;
	public static int CTF_EVENT_MAX_PLAYERS_IN_TEAMS;
	public static byte CTF_EVENT_MIN_LVL;
	public static byte CTF_EVENT_MAX_LVL;
	public static int CTF_EVENT_RESPAWN_TELEPORT_DELAY;
	public static int CTF_EVENT_START_LEAVE_TELEPORT_DELAY;
	public static String CTF_EVENT_TEAM_1_NAME;
	public static int[] CTF_EVENT_TEAM_1_COORDINATES = new int[3];
	public static int[] CTF_EVENT_TEAM_1_FLAG_COORDINATES = new int[4];
	public static String CTF_EVENT_TEAM_2_NAME;
	public static int[] CTF_EVENT_TEAM_2_COORDINATES = new int[3];
	public static int[] CTF_EVENT_TEAM_2_FLAG_COORDINATES = new int[4];
	public static IntIntHolder[] CTF_EVENT_REWARDS;
	public static boolean CTF_EVENT_TARGET_TEAM_MEMBERS_ALLOWED;
	public static boolean CTF_EVENT_SCROLL_ALLOWED;
	public static boolean CTF_EVENT_POTIONS_ALLOWED;
	public static boolean CTF_EVENT_SUMMON_BY_ITEM_ALLOWED;
	public static List<Integer> CTF_DOORS_IDS_TO_OPEN;
	public static List<Integer> CTF_DOORS_IDS_TO_CLOSE;
	public static boolean CTF_REWARD_TEAM_TIE;
	public static int CTF_EVENT_EFFECTS_REMOVAL;
	public static Map<Integer, Integer> CTF_EVENT_FIGHTER_BUFFS;
	public static Map<Integer, Integer> CTF_EVENT_MAGE_BUFFS;
	public static boolean ALLOW_CTF_DLG;
	public static int CTF_EVENT_MAX_PARTICIPANTS_PER_IP;
	
	public static boolean DM_EVENT_ENABLED;
	public static String[] DM_EVENT_INTERVAL;
	public static int DM_EVENT_PARTICIPATION_TIME;
	public static int DM_EVENT_RUNNING_TIME;
	public static String DM_NPC_LOC_NAME;
	public static int DM_EVENT_PARTICIPATION_NPC_ID;
	public static int[] DM_EVENT_PARTICIPATION_FEE = new int[2];
	public static int[] DM_EVENT_PARTICIPATION_NPC_COORDINATES = new int[4];
	public static int DM_EVENT_MIN_PLAYERS;
	public static int DM_EVENT_MAX_PLAYERS;
	public static byte DM_EVENT_MIN_LVL;
	public static byte DM_EVENT_MAX_LVL;
	public static List<int[]> DM_EVENT_PLAYER_COORDINATES;
	public static int DM_EVENT_RESPAWN_TELEPORT_DELAY;
	public static int DM_EVENT_START_LEAVE_TELEPORT_DELAY;
	public static boolean DM_SHOW_TOP_RANK;
	public static int DM_TOP_RANK;
	public static int DM_REWARD_FIRST_PLAYERS;
	public static Map<Integer, List<int[]>> DM_EVENT_REWARDS;
	public static boolean DM_REWARD_PLAYERS_TIE;
	public static boolean DM_EVENT_SCROLL_ALLOWED;
	public static boolean DM_EVENT_POTIONS_ALLOWED;
	public static boolean DM_EVENT_SUMMON_BY_ITEM_ALLOWED;
	public static List<Integer> DM_DOORS_IDS_TO_OPEN;
	public static List<Integer> DM_DOORS_IDS_TO_CLOSE;
	public static int DM_EVENT_EFFECTS_REMOVAL;
	public static Map<Integer, Integer> DM_EVENT_FIGHTER_BUFFS;
	public static Map<Integer, Integer> DM_EVENT_MAGE_BUFFS;
	public static String DISABLE_ID_CLASSES_STRING_DM;
	public static List<Integer> DISABLE_ID_CLASSES_DM;
	public static boolean ALLOW_DM_DLG;
	public static int DM_EVENT_MAX_PARTICIPANTS_PER_IP;
	
	public static boolean LM_EVENT_ENABLED;
	public static String[] LM_EVENT_INTERVAL;
	public static int LM_EVENT_PARTICIPATION_TIME;
	public static boolean LM_EVENT_HERO;
	public static int LV_EVENT_HERO_DAYS;
	public static int LM_EVENT_RUNNING_TIME;
	public static String LM_NPC_LOC_NAME;
	public static int LM_EVENT_PARTICIPATION_NPC_ID;
	public static short LM_EVENT_PLAYER_CREDITS;
	public static int[] LM_EVENT_PARTICIPATION_NPC_COORDINATES = new int[4];
	public static int[] LM_EVENT_PARTICIPATION_FEE = new int[2];
	public static int LM_EVENT_MIN_PLAYERS;
	public static int LM_EVENT_MAX_PLAYERS;
	public static int LM_EVENT_RESPAWN_TELEPORT_DELAY;
	public static int LM_EVENT_START_LEAVE_TELEPORT_DELAY;
	public static List<int[]> LM_EVENT_PLAYER_COORDINATES;
	public static IntIntHolder[] LM_EVENT_REWARDS;
	public static boolean LM_EVENT_SCROLL_ALLOWED;
	public static boolean LM_EVENT_POTIONS_ALLOWED;
	public static boolean LM_EVENT_SUMMON_BY_ITEM_ALLOWED;
	public static List<Integer> LM_DOORS_IDS_TO_OPEN;
	public static List<Integer> LM_DOORS_IDS_TO_CLOSE;
	public static boolean LM_REWARD_PLAYERS_TIE;
	public static byte LM_EVENT_MIN_LVL;
	public static byte LM_EVENT_MAX_LVL;
	public static int LM_EVENT_EFFECTS_REMOVAL;
	public static Map<Integer, Integer> LM_EVENT_FIGHTER_BUFFS;
	public static Map<Integer, Integer> LM_EVENT_MAGE_BUFFS;
	public static String DISABLE_ID_CLASSES_STRING_LM;
	public static List<Integer> DISABLE_ID_CLASSES_LM;
	public static boolean ALLOW_LM_DLG;
	public static int LM_EVENT_MAX_PARTICIPANTS_PER_IP;
	
	public static boolean TVT_EVENT_ENABLED;
	public static String[] TVT_EVENT_INTERVAL;
	public static int TVT_EVENT_PARTICIPATION_TIME;
	public static int TVT_EVENT_RUNNING_TIME;
	public static String TVT_NPC_LOC_NAME;
	public static int TVT_EVENT_PARTICIPATION_NPC_ID;
	public static int[] TVT_EVENT_PARTICIPATION_NPC_COORDINATES = new int[4];
	public static int[] TVT_EVENT_PARTICIPATION_FEE = new int[2];
	public static int TVT_EVENT_MIN_PLAYERS_IN_TEAMS;
	public static int TVT_EVENT_MAX_PLAYERS_IN_TEAMS;
	public static int TVT_EVENT_RESPAWN_TELEPORT_DELAY;
	public static int TVT_EVENT_START_LEAVE_TELEPORT_DELAY;
	public static String TVT_EVENT_TEAM_1_NAME;
	public static int[] TVT_EVENT_TEAM_1_COORDINATES = new int[3];
	public static String TVT_EVENT_TEAM_2_NAME;
	public static int[] TVT_EVENT_TEAM_2_COORDINATES = new int[3];
	public static IntIntHolder[] TVT_EVENT_REWARDS;
	public static boolean TVT_EVENT_TARGET_TEAM_MEMBERS_ALLOWED;
	public static boolean TVT_EVENT_SCROLL_ALLOWED;
	public static boolean TVT_EVENT_POTIONS_ALLOWED;
	public static boolean TVT_EVENT_SUMMON_BY_ITEM_ALLOWED;
	public static List<Integer> TVT_DOORS_IDS_TO_OPEN;
	public static List<Integer> TVT_DOORS_IDS_TO_CLOSE;
	public static boolean TVT_REWARD_TEAM_TIE;
	public static byte TVT_EVENT_MIN_LVL;
	public static byte TVT_EVENT_MAX_LVL;
	public static int TVT_EVENT_EFFECTS_REMOVAL;
	public static Map<Integer, Integer> TVT_EVENT_FIGHTER_BUFFS;
	public static Map<Integer, Integer> TVT_EVENT_MAGE_BUFFS;
	public static boolean TVT_REWARD_PLAYER;
	public static String TVT_EVENT_ON_KILL;
	public static String DISABLE_ID_CLASSES_STRING_TVT;
	public static List<Integer> DISABLE_ID_CLASSES_TVT;
	public static boolean ALLOW_TVT_DLG;
	public static int TVT_EVENT_MAX_PARTICIPANTS_PER_IP;
	
	
	/** Geodata */
	public static String GEODATA_PATH;
	public static GeoType GEODATA_TYPE;
	
	/** Movement */
	public static int MAX_GEOPATH_FAIL_COUNT;
	
	/** Path checking */
	public static int PART_OF_CHARACTER_HEIGHT;
	public static int MAX_OBSTACLE_HEIGHT;
	/** NPC spawn Z bias (downwards). Positive values bias towards ground. */
	public static int NPC_Z_INDEX;
	
	/** Path finding */
	public static int MOVE_WEIGHT;
	public static int MOVE_WEIGHT_DIAG;
	public static int OBSTACLE_WEIGHT;
	public static int OBSTACLE_WEIGHT_DIAG;
	public static boolean ENABLE_BOUNDARY_CELL_PENALTY;
	public static int BOUNDARY_CELL_PENALTY;
	public static int BOUNDARY_BUFFER;
	public static int HEURISTIC_WEIGHT;
	public static int MAX_ITERATIONS;
	
	
	public static int SERVER_ID;
	public static byte[] HEX_ID;
	
	
	public static Locale DEFAULT_LOCALE = Locale.forLanguageTag("en-US");
	public static Set<Locale> LOCALES = Set.of(DEFAULT_LOCALE);
	public static Charset CHARSET = Charset.forName("utf-8");
	
	
	public static String LOGINSERVER_HOSTNAME;
	public static int LOGINSERVER_PORT;
	
	public static int LOGIN_TRY_BEFORE_BAN;
	public static int LOGIN_BLOCK_AFTER_BAN;
	public static boolean ACCEPT_NEW_GAMESERVER;
	
	public static boolean SHOW_LICENCE;
	
	public static boolean AUTO_CREATE_ACCOUNTS;
	
	public static boolean FLOOD_PROTECTION;
	public static int FAST_CONNECTION_LIMIT;
	public static int NORMAL_CONNECTION_TIME;
	public static int FAST_CONNECTION_TIME;
	public static int MAX_CONNECTION_PER_IP;
	
	public static boolean SHOW_CONNECT;
	
	
	/** Spawn */
	public static double SPAWN_MULTIPLIER;
	public static String[] SPAWN_EVENTS;
	
	/** Champion Mod */
	public static int CHAMPION_FREQUENCY;
	public static int CHAMP_MIN_LVL;
	public static int CHAMP_MAX_LVL;
	public static int CHAMPION_HP;
	public static double CHAMPION_HP_REGEN;
	public static double CHAMPION_RATE_XP;
	public static double CHAMPION_RATE_SP;
	public static double PREMIUM_CHAMPION_RATE_XP;
	public static double PREMIUM_CHAMPION_RATE_SP;
	public static int CHAMPION_REWARDS;
	public static int PREMIUM_CHAMPION_REWARDS;
	public static int CHAMPION_ADENAS_REWARDS;
	public static int CHAMPION_SEALSTONE_REWARDS;
	public static int PREMIUM_CHAMPION_ADENAS_REWARDS;
	public static int PREMIUM_CHAMPION_SEALSTONE_REWARDS;
	public static int CHAMPION_SPOIL_REWARDS;
	public static int PREMIUM_CHAMPION_SPOIL_REWARDS;
	public static double CHAMPION_ATK;
	public static double CHAMPION_MATK;
	public static double CHAMPION_SPD_ATK;
	public static double CHAMPION_SPD_MATK;
	public static int CHAMPION_REWARD;
	public static int CHAMPION_REWARD_ID;
	public static int CHAMPION_REWARD_QTY;
	public static int CHAMPION_AURA;
	
	/** Class Master */
	public static boolean ALLOW_ENTIRE_TREE;
	public static ClassMasterSettings CLASS_MASTER_SETTINGS;
	public static boolean ALTERNATE_CLASS_MASTER;
	
	public static int NOBLE_ITEM_ID;
	public static int NOBLE_ITEM_COUNT;
	
	/** Wedding Manager */
	public static int WEDDING_PRICE;
	public static boolean WEDDING_SAMESEX;
	public static boolean WEDDING_FORMALWEAR;
	
	/** Scheme Buffer */
	public static int BUFFER_MAX_SCHEMES;
	public static int BUFFER_STATIC_BUFF_COST;
	
	/** Misc */
	public static boolean FREE_TELEPORT;
	public static int LVL_FREE_TELEPORT;
	public static boolean ANNOUNCE_MAMMON_SPAWN;
	public static boolean MOB_AGGRO_IN_PEACEZONE;
	public static boolean SHOW_NPC_LVL;
	public static String SHOW_NPC_TITLE_FORMAT;
	public static String SHOW_NPC_TITLE_COLOR;
	public static boolean SHOW_NPC_CREST;
	public static boolean SHOW_SUMMON_CREST;
	
	/** Wyvern Manager */
	public static int WYVERN_REQUIRED_LEVEL;
	public static int WYVERN_REQUIRED_CRYSTALS;
	
	public static boolean RAID_DISABLE_CURSE;
	
	/** Grand Boss */
	public static int WAIT_TIME_ANTHARAS;
	public static boolean NEED_ITEM_ANTHARAS;
	
	public static int WAIT_TIME_VALAKAS;
	public static boolean NEED_ITEM_VALAKAS;
	
	public static int WAIT_TIME_FRINTEZZA;
	public static int FRINTEZZA_MINIMUM_ALLOWED_PLAYERS;
	public static int FRINTEZZA_MAXIMUM_ALLOWED_PLAYERS;
	public static int FRINTEZZA_MINIMUM_PARTIES;
	public static int FRINTEZZA_MAXIMUM_PARTIES;
	public static boolean NEED_ITEM_FRINTEZZA;
	
	public static boolean NEED_ITEM_BAIUM;
	
	public static boolean NEED_ITEM_SHILEN;
	
	/** AI */
	public static boolean GUARD_ATTACK_AGGRO_MOB;
	public static int RANDOM_WALK_RATE;
	public static int MAX_DRIFT_RANGE;
	public static boolean DEBUG_MELEE_ATTACK;
	
	/** Guard Chat System */
	public static boolean ENABLE_GUARD_CHAT;
	public static int GUARD_CHAT_RANGE;
	public static int GUARD_CHATTY_CHANCE;
	public static int GUARD_SAY_NORMAL_CHANCE;
	public static long GUARD_SAY_NORMAL_PERIOD;
	public static long GUARD_SAY_AGGRO_PERIOD;
	
	public static int NPC_ANIMATION;
	public static int MONSTER_ANIMATION;
	
	public static int DEFAULT_SEE_RANGE;
	public static int SUMMON_DRIFT_RANGE;
	
	public static int[] RAID_BOSS_LIST;
	public static int[] EPIC_BOSS_LIST;
	
	
	public static boolean OFFLINE_TRADE_ENABLE;
	public static boolean OFFLINE_CRAFT_ENABLE;
	public static boolean OFFLINE_MODE_IN_PEACE_ZONE;
	public static boolean OFFLINE_MODE_NO_DAMAGE;
	public static boolean RESTORE_OFFLINERS;
	public static int OFFLINE_MAX_DAYS;
	public static boolean OFFLINE_DISCONNECT_FINISHED;
	public static boolean OFFLINE_SLEEP_EFFECT;
	public static boolean RESTORE_STORE_ITEMS;
	
	
	/** Misc */
	public static boolean EFFECT_CANCELING;
	public static double HP_REGEN_MULTIPLIER;
	public static double MP_REGEN_MULTIPLIER;
	public static double CP_REGEN_MULTIPLIER;
	public static int PLAYER_SPAWN_PROTECTION;
	public static int PLAYER_FAKEDEATH_UP_PROTECTION;
	public static double RESPAWN_RESTORE_HP;
	public static int MAX_PVTSTOREBUY_SLOTS_DWARF;
	public static int MAX_PVTSTOREBUY_SLOTS_OTHER;
	public static int MAX_PVTSTORESELL_SLOTS_DWARF;
	public static int MAX_PVTSTORESELL_SLOTS_OTHER;
	public static boolean DEEPBLUE_DROP_RULES;
	public static boolean ALLOW_DELEVEL;
	public static int DEATH_PENALTY_CHANCE;
	
	/** Inventory & WH */
	public static int INVENTORY_MAXIMUM_NO_DWARF;
	public static int INVENTORY_MAXIMUM_DWARF;
	public static int INVENTORY_MAXIMUM_PET;
	public static int MAX_ITEM_IN_PACKET;
	public static double WEIGHT_LIMIT;
	public static int WAREHOUSE_SLOTS_NO_DWARF;
	public static int WAREHOUSE_SLOTS_DWARF;
	public static int WAREHOUSE_SLOTS_CLAN;
	public static int FREIGHT_SLOTS;
	public static boolean REGION_BASED_FREIGHT;
	public static int FREIGHT_PRICE;
	
	/** Augmentations */
	public static int AUGMENTATION_NG_SKILL_CHANCE;
	public static int AUGMENTATION_NG_GLOW_CHANCE;
	public static int AUGMENTATION_MID_SKILL_CHANCE;
	public static int AUGMENTATION_MID_GLOW_CHANCE;
	public static int AUGMENTATION_HIGH_SKILL_CHANCE;
	public static int AUGMENTATION_HIGH_GLOW_CHANCE;
	public static int AUGMENTATION_TOP_SKILL_CHANCE;
	public static int AUGMENTATION_TOP_GLOW_CHANCE;
	public static int AUGMENTATION_BASESTAT_CHANCE;
	
	/** Karma & PvP */
	public static boolean KARMA_PLAYER_CAN_SHOP;
	public static boolean KARMA_PLAYER_CAN_USE_GK;
	public static boolean KARMA_PLAYER_CAN_TELEPORT;
	public static boolean KARMA_PLAYER_CAN_TRADE;
	public static boolean KARMA_PLAYER_CAN_USE_WH;
	
	public static boolean KARMA_DROP_GM;
	public static boolean KARMA_AWARD_PK_KILL;
	public static int KARMA_PK_LIMIT;
	
	public static int[] KARMA_NONDROPPABLE_PET_ITEMS;
	public static int[] KARMA_NONDROPPABLE_ITEMS;
	
	public static int PVP_NORMAL_TIME;
	public static int PVP_PVP_TIME;
	
	/** Party */
	public static String PARTY_XP_CUTOFF_METHOD;
	public static int PARTY_XP_CUTOFF_LEVEL;
	public static double PARTY_XP_CUTOFF_PERCENT;
	public static int PARTY_RANGE;
	
	/** GMs & Admin Stuff */
	public static int DEFAULT_ACCESS_LEVEL;
	public static boolean GM_HERO_AURA;
	public static boolean GM_STARTUP_INVULNERABLE;
	public static boolean GM_STARTUP_INVISIBLE;
	public static boolean GM_STARTUP_BLOCK_ALL;
	public static boolean GM_STARTUP_AUTO_LIST;
	
	/** petitions */
	public static boolean PETITIONING_ALLOWED;
	public static int MAX_PETITIONS_PER_PLAYER;
	public static int MAX_PETITIONS_PENDING;
	
	/** Crafting **/
	public static boolean IS_CRAFTING_ENABLED;
	public static int DWARF_RECIPE_LIMIT;
	public static int COMMON_RECIPE_LIMIT;
	
	/** Skills & Classes **/
	public static boolean AUTO_LEARN_SKILLS;
	public static int LVL_AUTO_LEARN_SKILLS;
	public static boolean MAGIC_FAILURES;
	public static int PERFECT_SHIELD_BLOCK_RATE;
	public static boolean LIFE_CRYSTAL_NEEDED;
	public static boolean SP_BOOK_NEEDED;
	public static boolean ES_SP_BOOK_NEEDED;
	public static boolean DIVINE_SP_BOOK_NEEDED;
	public static boolean SUBCLASS_WITHOUT_QUESTS;
	
	/** Buffs */
	public static boolean STORE_SKILL_COOLTIME;
	public static boolean EXPERTISE_PENALTY;
	public static int MAX_BUFFS_AMOUNT;
	
	
	public static int SIEGE_LENGTH;
	public static int MINIMUM_CLAN_LEVEL;
	public static int MAX_ATTACKERS_NUMBER;
	public static int MAX_DEFENDERS_NUMBER;
	
	public static int CH_MINIMUM_CLAN_LEVEL;
	public static int CH_MAX_ATTACKERS_NUMBER;
	
	public static boolean SIEGE_INFO;
	
	
	public static String HOSTNAME;
	public static String GAMESERVER_HOSTNAME;
	public static int GAMESERVER_PORT;
	public static String GAMESERVER_LOGIN_HOSTNAME;
	public static int GAMESERVER_LOGIN_PORT;
	public static int REQUEST_ID;
	public static boolean ACCEPT_ALTERNATE_ID;
	public static boolean USE_BLOWFISH_CIPHER;
	
	/** Access to database */
	public static String DATABASE_URL;
	public static String DATABASE_LOGIN;
	public static String DATABASE_PASSWORD;
	
	public static String CNAME_TEMPLATE;
	public static String DONATE_CNAME_TEMPLATE;
	public static String TITLE_TEMPLATE;
	public static String PET_NAME_TEMPLATE;
	public static String CLAN_ALLY_NAME_TEMPLATE;
	
	/** serverList & Test */
	public static boolean SERVER_LIST_BRACKET;
	public static boolean SERVER_LIST_CLOCK;
	public static int SERVER_LIST_AGE;
	public static boolean SERVER_LIST_TESTSERVER;
	public static boolean SERVER_LIST_PVPSERVER;
	public static boolean SERVER_GMONLY;
	
	/** clients related */
	public static int DELETE_DAYS;
	public static int MAXIMUM_ONLINE_USERS;
	
	/** Auto-loot */
	public static boolean AUTO_LOOT;
	public static boolean AUTO_LOOT_HERBS;
	public static boolean AUTO_LOOT_RAID;
	
	/** Items Management */
	public static boolean ALLOW_DISCARDITEM;
	public static boolean MULTIPLE_ITEM_DROP;
	public static int HERB_AUTO_DESTROY_TIME;
	public static int ITEM_AUTO_DESTROY_TIME;
	public static int EQUIPABLE_ITEM_AUTO_DESTROY_TIME;
	public static Map<Integer, Integer> SPECIAL_ITEM_DESTROY_TIME;
	public static int PLAYER_DROPPED_ITEM_MULTIPLIER;
	
	/** GC cleanup - itens no chao: tempo maximo em ms, 0 = desativado */
	public static boolean ITEMS_GC_CLEANUP_ENABLED;
	public static int ITEMS_GC_CLEANUP_TIME_MS;
	
	/** Rate control */
	public static double RATE_XP;
	public static double RATE_SP;
	public static double RATE_PARTY_XP;
	public static double RATE_PARTY_SP;
	public static double RATE_DROP_CURRENCY;
	public static double RATE_DROP_SEAL_STONE;
	public static double RATE_DROP_ITEMS;
	public static double RATE_DROP_ITEMS_BY_RAID;
	public static double RATE_DROP_ITEMS_BY_GRAND;
	public static double RATE_DROP_SPOIL;
	
	public static double PREMIUM_RATE_XP;
	public static double PREMIUM_RATE_SP;
	public static double PREMIUM_RATE_DROP_CURRENCY;
	public static double PREMIUM_RATE_DROP_SEAL_STONE;
	public static double PREMIUM_RATE_DROP_SPOIL;
	public static double PREMIUM_RATE_DROP_ITEMS;
	public static double PREMIUM_RATE_DROP_ITEMS_BY_RAID;
	public static double PREMIUM_RATE_DROP_ITEMS_BY_GRAND;
	
	public static double PREMIUM_RATE_QUEST_DROP;
	public static double PREMIUM_RATE_QUEST_REWARD;
	public static double PREMIUM_RATE_QUEST_REWARD_XP;
	public static double PREMIUM_RATE_QUEST_REWARD_SP;
	public static double PREMIUM_RATE_QUEST_REWARD_ADENA;
	
	public static boolean DYNAMIC_XP;
	public static Map<Integer, Double> DYNAMIC_XP_RATES;
	
	public static double RATE_DROP_HERBS;
	public static int RATE_DROP_MANOR;
	
	public static double RATE_QUEST_DROP;
	public static double RATE_QUEST_REWARD;
	public static double RATE_QUEST_REWARD_XP;
	public static double RATE_QUEST_REWARD_SP;
	public static double RATE_QUEST_REWARD_ADENA;
	
	public static double RATE_KARMA_EXP_LOST;
	public static double RATE_SIEGE_GUARDS_PRICE;
	
	public static int PLAYER_DROP_LIMIT;
	public static int PLAYER_RATE_DROP;
	public static int PLAYER_RATE_DROP_ITEM;
	public static int PLAYER_RATE_DROP_EQUIP;
	public static int PLAYER_RATE_DROP_EQUIP_WEAPON;
	
	public static int KARMA_DROP_LIMIT;
	public static int KARMA_RATE_DROP;
	public static int KARMA_RATE_DROP_ITEM;
	public static int KARMA_RATE_DROP_EQUIP;
	public static int KARMA_RATE_DROP_EQUIP_WEAPON;
	
	public static double PET_XP_RATE;
	public static int PET_FOOD_RATE;
	public static double SINEATER_XP_RATE;
	
	public static double GRANDBOSS_RATE_XP;
	public static double GRANDBOSS_RATE_SP;
	
	public static double RAIDBOSS_RATE_XP;
	public static double RAIDBOSS_RATE_SP;
	
	/** Allow types */
	public static boolean ALLOW_FREIGHT;
	public static boolean ALLOW_WAREHOUSE;
	public static boolean ALLOW_WEAR;
	public static int WEAR_DELAY;
	public static int WEAR_PRICE;
	public static boolean ALLOW_LOTTERY;
	public static boolean ALLOW_WATER;
	public static boolean ALLOW_BOAT;
	public static boolean ALLOW_CURSED_WEAPONS;
	public static boolean ALLOW_SHADOW_WEAPONS;
	public static boolean ALLOW_MANOR;
	public static boolean ENABLE_FALLING_DAMAGE;
	
	/** Debug & Dev */
	public static boolean NO_SPAWNS;
	public static boolean DEVELOPER;
	public static boolean PACKET_HANDLER_DEBUG;
	
	public static List<String> CLIENT_PACKETS;
	public static List<String> SERVER_PACKETS;
	
	/** Logs */
	public static boolean LOG_CHAT;
	public static boolean LOG_ITEMS;
	public static boolean DROP_ITEMS;
	public static boolean GMAUDIT;
	
	/** Community Board */
	public static boolean ENABLE_CUSTOM_BBS;
	public static boolean ENABLE_COMMUNITY_BOARD;
	public static String BBS_DEFAULT;
	
	/** Flood Protectors */
	public static int ROLL_DICE_TIME;
	public static int HERO_VOICE_TIME;
	public static int SUBCLASS_TIME;
	public static int DROP_ITEM_TIME;
	public static int SERVER_BYPASS_TIME;
	public static int MULTISELL_TIME;
	public static int MANUFACTURE_TIME;
	public static int MANOR_TIME;
	public static int SENDMAIL_TIME;
	public static int CHARACTER_SELECT_TIME;
	public static int GLOBAL_CHAT_TIME;
	public static int TRADE_CHAT_TIME;
	public static int SOCIAL_TIME;
	public static int ITEM_TIME;
	public static int ACTION_TIME;
	
	/** ThreadPool */
	public static int SCHEDULED_THREAD_POOL_COUNT;
	public static int INSTANT_THREAD_POOL_COUNT;
	
	/** Misc */
	public static boolean L2WALKER_PROTECTION;
	public static boolean SERVER_NEWS;
	public static int ZONE_TOWN;

	/** Translator */
	public static boolean TRANSLATOR_ENABLE;
	public static String DEEPL_AUTH_KEY;
	public static String DEEP_CONTEXT_STRING;
	public static List<String> DO_NOT_TRANSLATE;
	
	
	/** Reserve Host on LoginServerThread */
	public static boolean RESERVE_HOST_ON_LOGIN = false;
	
	/** MMO settings */
	public static int MMO_SELECTOR_SLEEP_TIME = 20;
	public static int MMO_MAX_SEND_PER_PASS = 80;
	public static int MMO_MAX_READ_PER_PASS = 80;
	public static int MMO_HELPER_BUFFER_COUNT = 20;
	
	/** Client Packets Queue settings */
	public static int CLIENT_PACKET_QUEUE_SIZE = MMO_MAX_READ_PER_PASS + 2;
	public static int CLIENT_PACKET_QUEUE_MAX_BURST_SIZE = MMO_MAX_READ_PER_PASS + 1;
	public static int CLIENT_PACKET_QUEUE_MAX_PACKETS_PER_SECOND = 320;
	public static int CLIENT_PACKET_QUEUE_MEASURE_INTERVAL = 5;
	public static int CLIENT_PACKET_QUEUE_MAX_AVERAGE_PACKETS_PER_SECOND = 160;
	public static int CLIENT_PACKET_QUEUE_MAX_FLOODS_PER_MIN = 2;
	public static int CLIENT_PACKET_QUEUE_MAX_OVERFLOWS_PER_MIN = 1;
	public static int CLIENT_PACKET_QUEUE_MAX_UNDERFLOWS_PER_MIN = 1;
	public static int CLIENT_PACKET_QUEUE_MAX_UNKNOWN_PER_MIN = 5;
	
	
	/** Infinity SS and Arrows */
	public static boolean INFINITY_SS;
	public static boolean INFINITY_ARROWS;
	/** Restringe Infinity SS/Arrows apenas a personagens Premium */
	public static boolean INFINITY_SS_PREMIUM_ONLY;
	public static boolean INFINITY_ARROWS_PREMIUM_ONLY;
	
	/** Olympiad Period */
	public static boolean OLY_USE_CUSTOM_PERIOD_SETTINGS;
	public static OlympiadPeriod OLY_PERIOD;
	public static int OLY_PERIOD_MULTIPLIER;
	
	public static boolean ENABLE_MODIFY_SKILL_DURATION;
	public static HashMap<Integer, Integer> SKILL_DURATION_LIST;
	
	public static String GLOBAL_CHAT;
	public static String TRADE_CHAT;
	public static int CHAT_ALL_LEVEL;
	public static int CHAT_TELL_LEVEL;
	public static int CHAT_SHOUT_LEVEL;
	public static int CHAT_TRADE_LEVEL;
	
	public static boolean ENABLE_MENU;
	public static boolean PROP_STOP_EXP;
	public static boolean PROP_TRADE_REFUSAL;
	public static boolean PROP_AUTO_LOOT;
	public static boolean PROP_BUFF_PROTECTED;
	public static boolean ENABLE_ONLINE_COMMAND;
	public static int MULTIPLIER_ONLINE_COMMAND;
	
	public static boolean BOTS_PREVENTION;
	public static boolean BOTS_LOGS;
	public static int KILLS_COUNTER;
	public static int KILLS_COUNTER_RANDOMIZATION;
	public static int VALIDATION_TIME;
	public static int PUNISHMENT;
	public static int PUNISHMENT_TIME;
	
	public static boolean USE_PREMIUM_SERVICE;
	public static boolean ALTERNATE_DROP_LIST;
	
	public static boolean ATTACK_PTS;
	public static boolean SUBCLASS_SKILLS;
	public static boolean GAME_SUBCLASS_EVERYWHERE;
	
	public static boolean SHOW_NPC_INFO;
	public static boolean ALLOW_GRAND_BOSSES_TELEPORT;
	
	public static List<String> FILTER_LIST;
	
	public static boolean USE_SAY_FILTER;
	public static String CHAT_FILTER_CHARS;
	
	public static boolean CABAL_BUFFER;
	public static boolean SUPER_HASTE;
	
	public static String RESTRICTED_CHAR_NAMES;
	public static List<String> LIST_RESTRICTED_CHAR_NAMES = new ArrayList<>();
	
	public static int FAKE_ONLINE_AMOUNT;
	
	public static String BUFFS_CATEGORY;
	public static List<String> PREMIUM_BUFFS_CATEGORY = new ArrayList<>();
	
	public static boolean ANTIFEED_ENABLE;
	public static boolean ANTIFEED_DUALBOX;
	public static boolean ANTIFEED_DISCONNECTED_AS_DUALBOX;
	public static int ANTIFEED_INTERVAL;
	
	public static int DUALBOX_CHECK_MAX_PLAYERS_PER_IP;
	public static int DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP;
	
	public static List<Integer> AUTO_LOOT_ITEM_IDS;
	
	public static boolean NPC_STAT_MULTIPLIERS;
	public static double MONSTER_HP_MULTIPLIER;
	public static double MONSTER_MP_MULTIPLIER;
	public static double MONSTER_PATK_MULTIPLIER;
	public static double MONSTER_MATK_MULTIPLIER;
	public static double MONSTER_PDEF_MULTIPLIER;
	public static double MONSTER_MDEF_MULTIPLIER;
	
	public static double RAIDBOSS_HP_MULTIPLIER;
	public static double RAIDBOSS_MP_MULTIPLIER;
	public static double RAIDBOSS_PATK_MULTIPLIER;
	public static double RAIDBOSS_MATK_MULTIPLIER;
	public static double RAIDBOSS_PDEF_MULTIPLIER;
	public static double RAIDBOSS_MDEF_MULTIPLIER;
	
	public static double GRANDBOSS_HP_MULTIPLIER;
	public static double GRANDBOSS_MP_MULTIPLIER;
	public static double GRANDBOSS_PATK_MULTIPLIER;
	public static double GRANDBOSS_MATK_MULTIPLIER;
	public static double GRANDBOSS_PDEF_MULTIPLIER;
	public static double GRANDBOSS_MDEF_MULTIPLIER;
	
	public static boolean HIT_TIME;
	
	public static boolean SHOW_RAID_HTM;
	public static boolean SHOW_EPIC_HTM;
	public static boolean RAID_BOSS_TELEPORT_ENABLED;
	public static boolean EPIC_BOSS_TELEPORT_ENABLED;
	public static int BOSS_TELEPORT_MIN_RANGE;
	public static int BOSS_TELEPORT_MAX_RANGE;
	public static int BOSS_TELEPORT_ITEM_ID;
	public static long BOSS_TELEPORT_ITEM_COUNT;
	public static boolean USE_CONFIG_RAID_GRAND_BOSS_RESPAWN;
	public static long RAID_BOSS_RESPAWN_BASE_MS;
	public static long RAID_BOSS_RESPAWN_RANDOM_MS;
	public static long GRAND_BOSS_RESPAWN_BASE_MS;
	public static long GRAND_BOSS_RESPAWN_RANDOM_MS;
	public static Map<Integer, long[]> RAID_BOSS_RESPAWN_OVERRIDES;
	public static Map<Integer, long[]> GRAND_BOSS_RESPAWN_OVERRIDES;
	
	public static String TIME_ZONE;
	public static String DATE_FORMAT;
	
	public static boolean CUSTOM_BUFFER_MANAGER_NPC;
	public static String[] SKIP_CATEGORY;
	
	public static boolean BARAKIEL;
	public static boolean CREATURE_SEE;
	
	public static boolean NEW_REGEN;
	public static boolean CATACOMBS_IN_ANY_PERIOD;
	public static boolean STRICT_SEVENSIGNS;
	public static boolean CLASS_OVERLORD;
	public static boolean RACE_ELF;
	public static boolean RESTRICTED_CLASSES;
	
	public static int BANKING_SYSTEM_GOLDBARS;
	public static int BANKING_SYSTEM_ADENA;
	public static boolean ENABLE_COMMAND_GOLDBAR;
	
	public static boolean AUTO_POTIONS_ENABLED;
	public static boolean AUTO_POTIONS_IN_OLYMPIAD;
	public static int AUTO_POTION_MIN_LEVEL;
	public static int ACP_PERIOD;
	public static boolean AUTO_CP_ENABLED;
	public static boolean AUTO_HP_ENABLED;
	public static boolean AUTO_MP_ENABLED;
	public static Set<Integer> AUTO_CP_ITEM_IDS;
	public static Set<Integer> AUTO_HP_ITEM_IDS;
	public static Set<Integer> AUTO_MP_ITEM_IDS;
	public static int MULTISELL_MAX_AMOUNT;
	
	public static boolean ENABLED_AUCTION;
	public static int AUCTION_LIMIT_ITEM;
	public static int AUCTION_FEE;
	
	public static int AUCTION_ITEM_FEE;
	public static String AUCTION_ITEM_FEE_NAME;
	
	public static boolean AUTOFARM_ENABLED;
	public static boolean AUTOFARM_ALLOW_DUALBOX;
	public static boolean AUTOFARM_SEND_LOG_MESSAGES;
	public static boolean AUTOFARM_CHANGE_PLAYER_TITLE;
	public static boolean AUTOFARM_CHANGE_PLAYER_NAME_COLOR;
	public static boolean AUTOFARM_DISABLE_TOWN;
	public static double AUTOFARM_HP_HEAL_RATE;
	public static double AUTOFARM_MP_HEAL_RATE;
	public static int AUTOFARM_MAX_ZONE_AREA;
	public static int AUTOFARM_MAX_ROUTE_PERIMITER;
	public static int AUTOFARM_MAX_ZONES;
	public static int AUTOFARM_MAX_ROUTES;
	public static int AUTOFARM_MAX_ZONE_NODES;
	public static int AUTOFARM_MAX_ROUTE_NODES;
	public static int AUTOFARM_DEBUFF_CHANCE;
	public static int AUTOFARM_MAX_TIMER;
	public static int AUTOFARM_MAX_OPEN_RADIUS;
	public static int[] AUTOFARM_HP_POTIONS;
	public static int[] AUTOFARM_MP_POTIONS;
	public static String AUTOFARM_PLAYER_NAME_COLOR;
	public static boolean AUTOFARM_DEBUG_RETURN;
	
	public static boolean ENABLE_OFFLINE_FARM_COMMAND;
	public static boolean OFFLINE_FARM_PREMIUM;
	public static boolean OFFLINE_FARM_LOGOUT_ON_DEATH;
	public static int DUALBOX_CHECK_MAX_OFFLINEPLAY_PER_IP;
	public static int DUALBOX_CHECK_MAX_OFFLINEPLAY_PREMIUM_PER_IP;
	public static boolean DUALBOX_COUNT_OFFLINE_TRADERS;
	public static boolean AWAY_PLAY_SET_NAME_COLOR;
	public static String AWAY_PLAY_NAME_COLOR;
	public static int HP_POTION_ITEM_ID;
	public static int MP_POTION_ITEM_ID;
	public static int AUTOFARM_MAX_LEVEL_DIFFERENCE;
	public static boolean IGNORE_RAID_BOSSES;
	public static boolean IGNORE_RAID_MINIONS;
	public static boolean IGNORE_AGATHIONS;
	public static boolean IGNORE_CHESTS;
	
	public static boolean SELLBUFF_ENABLED;
	public static int SELLBUFF_MP_MULTIPLER;
	public static int SELLBUFF_PAYMENT_ID;
	public static long SELLBUFF_MIN_PRICE;
	public static long SELLBUFF_MAX_PRICE;
	public static int SELLBUFF_MAX_BUFFS;
	public static boolean CUSTOM_TIME_BUFF;
	
	public static boolean ENTER_ANAKAZEL;
	
	public static int MAX_RUN_SPEED;
	public static int MAX_PATK;
	public static int MAX_MATK;
	public static int MAX_PCRIT_RATE;
	public static int MAX_MCRIT_RATE;
	public static int MAX_PATK_SPEED;
	public static int MAX_MATK_SPEED;
	public static int MAX_EVASION;
	
	public static boolean NEW_FOLLOW;
	public static boolean ENABLE_MISSION;
	public static boolean RANDOM_PVP_ZONE;
	
	public static boolean PTS_EMULATION_SPAWN;
	public static int PTS_EMULATION_SPAWN_DURATION;
	
	public static boolean STOP_TOGGLE;
	
	public static boolean ANNOUNCE_DIE_RAIDBOSS;
	public static boolean ANNOUNCE_SPAWN_RAIDBOSS;
	
	public static boolean ANNOUNCE_DIE_GRANDBOSS;
	public static boolean ANNOUNCE_SPAWN_GRANDBOSS;
	
	public static boolean PROXY;
	
	public static boolean NPC_SOULSHOT;
	public static boolean NPC_SPIRITSHOT;
	
	public static boolean RETURN_HOME_MONSTER;
	public static int RETURN_HOME_MONSTER_RADIUS;
	
	public static boolean RETURN_HOME_RAIDBOSS;
	public static int RETURN_HOME_RAIDBOSS_RADIUS;

	public static boolean CANCEL_RETURN_ENABLED;
	public static String CANCEL_RETURN_MODE;
	public static boolean CANCEL_RETURN_MASS_ONLY;
	public static int CANCEL_RETURN_MASS_MIN_COUNT;
	public static int CANCEL_RETURN_TIME_MS;
	public static boolean CANCEL_RETURN_SKIP_OLYMPIAD;
	public static boolean CANCEL_RETURN_NOTIFY;
	public static String CANCEL_RETURN_MESSAGE;
	
	public static boolean SISTEMA_PATHFINDING;
	public static boolean USE_OPTIMIZED_MOVEMENT;
	public static boolean ENABLE_CLIENT_SIDE_PREDICTION;
	public static int MOVEMENT_VALIDATION_INTERVAL;
	public static double MOVEMENT_RECONCILIATION_THRESHOLD;
	public static int PATHFINDING_MAX_NODES;
	public static int PATHFINDING_MAX_ITERATIONS;
	public static boolean ENABLE_PATH_SMOOTHING;
	public static int GEOENGINE_CACHE_SIZE;
	
	public static boolean ENABLE_PATHFINDING_CACHE;
	public static int PATHFINDING_CACHE_SIZE;
	public static int PATHFINDING_CACHE_EXPIRATION;
	public static int PATHFINDING_THREADS;
	public static boolean USE_PATHFINDING_POOL;
	public static boolean USE_L2BR_PATHFINDING;
	
	public static boolean ENABLE_SMOOTH_OBSTACLE_AVOIDANCE;
	public static int OBSTACLE_SMOOTHING_DISTANCE;
	public static int OBSTACLE_DETECTION_DISTANCE;
	public static int PATHFINDING_SMOOTHING_LEVEL;
	public static boolean ENABLE_REAL_TIME_OBSTACLE_AVOIDANCE;
	
	public static boolean ATTACK_USE_PATHFINDER;
	
	public static int GEO_HEIGHT_TOLERANCE;
	public static int SHORT_DISTANCE_THRESHOLD;
	public static int MAX_SHORT_DISTANCE_HEIGHT_DIFF;
	public static int MAX_CONSECUTIVE_BLOCKS;
	
	public static boolean ENABLE_NPC_MOVEMENT_OPTIMIZATION;
	public static int NPC_MOVEMENT_PLAYER_RANGE;
	
	
	public static boolean ENABLE_CREATURE_COLLISION;
	public static int CREATURE_COLLISION_CHECK_RADIUS;
	public static int CREATURE_COLLISION_AVOIDANCE_RADIUS;
	public static int MAX_ALTERNATIVE_POSITIONS;
	public static double CREATURE_COLLISION_PUSH_FORCE;
	
	public static boolean SAFEDISCONNECT_ENABLED;
	public static long SAFEDISCONNECT_TIMEOUT_MS;
	public static boolean SAFEDISCONNECT_ALLOW_OLYMPIAD;
	public static boolean SAFEDISCONNECT_ALLOW_BOSS;
	public static boolean SAFEDISCONNECT_ALLOW_QUEST;
	public static boolean SAFEDISCONNECT_ALLOW_FARM;
	public static boolean SAFEDISCONNECT_INTEGRATION_OLYMPIAD;
	public static boolean SAFEDISCONNECT_INTEGRATION_DUNGEON;
	public static boolean SAFEDISCONNECT_INTEGRATION_TOURNAMENT;
	public static boolean SAFEDISCONNECT_INTEGRATION_TVT;
	public static boolean SAFEDISCONNECT_INTEGRATION_CTF;
	public static boolean SAFEDISCONNECT_INTEGRATION_DM;
	public static boolean SAFEDISCONNECT_INTEGRATION_LM;
	public static boolean SAFEDISCONNECT_INVULNERABLE;
	public static boolean SAFEDISCONNECT_IMMOBILIZE;
	public static boolean SAFEDISCONNECT_CHANGE_NAME_COLOR;
	public static int SAFEDISCONNECT_NAME_COLOR;
	public static long SAFEDISCONNECT_AUTOFARM_RESTORE_DELAY_MS;
	public static String SAFEDISCONNECT_TITLE;
	
	public static boolean BOSS_ZERG_ENABLED;
	public static boolean BOSS_ZERG_FLAG_ENABLED;
	public static int BOSS_ZERG_FLAG_RANGE;
	public static long BOSS_ZERG_FLAG_RENEW_MS;
	public static int BOSS_ZERG_FLAG_MIN_PLAYERS;
	public static int BOSS_ZERG_FLAG_WARNING_RANGE;
	public static int BOSS_ZERG_FLAG_AREA_RADIUS;
	public static int BOSS_ZERG_FLAG_AREA_COLOR;
	public static int BOSS_ZERG_RANGE;
	public static int BOSS_ZERG_MIN_PARTY_SIZE;
	public static int BOSS_ZERG_MAX_ALLY_MEMBERS;
	public static boolean BOSS_ZERG_SHOW_ALLY_COUNT;
	public static boolean BOSS_ZERG_ANNOUNCE;
	public static long BOSS_ZERG_ANNOUNCE_COOLDOWN_MS;
	public static boolean BOSS_ZERG_SHOW_AREA;
	public static int BOSS_ZERG_AREA_RADIUS;
	public static long BOSS_ZERG_CHECK_INTERVAL_MS;
	public static boolean BOSS_ZERG_FLAG_AREA_ENABLED;
	public static boolean BOSS_ZERG_HEAL_PENALTY_ENABLED;
	public static double BOSS_ZERG_HEAL_PENALTY_MULTIPLIER;
	public static double BOSS_ZERG_PRAYER_REVERSE_MULTIPLIER;
	public static int[] BOSS_ZERG_PRAYER_SKILL_IDS = new int[0];
	public static Set<Integer> BOSS_ZERG_IGNORED_BOSS_IDS = new HashSet<>();
	
	/**
	 * Initialize {@link ExProperties} from specified configuration file.
	 * @param filename : File name to be loaded.
	 * @return ExProperties : Initialized {@link ExProperties}.
	 */
	public static final ExProperties initProperties(String filename)
	{
		final ExProperties result = new ExProperties();
		
		try
		{
			result.load(new File(filename));
		}
		catch (Exception e)
		{
			LOGGER.error("An error occured loading '{}' config.", e, filename);
		}
		
		return result;
	}
	
	/**
	 * Loads offline shop settings
	 */
	private static final void loadOfflineShop()
	{
		final ExProperties offline = initProperties(OFFLINE_FILE);
		OFFLINE_TRADE_ENABLE = offline.getProperty("OfflineTradeEnable", false);
		OFFLINE_CRAFT_ENABLE = offline.getProperty("OfflineCraftEnable", false);
		OFFLINE_MODE_IN_PEACE_ZONE = offline.getProperty("OfflineModeInPeaceZone", false);
		OFFLINE_MODE_NO_DAMAGE = offline.getProperty("OfflineModeNoDamage", false);
		RESTORE_OFFLINERS = offline.getProperty("RestoreOffliners", false);
		OFFLINE_MAX_DAYS = offline.getProperty("OfflineMaxDays", 10);
		OFFLINE_DISCONNECT_FINISHED = offline.getProperty("OfflineDisconnectFinished", true);
		OFFLINE_SLEEP_EFFECT = offline.getProperty("OfflineSleepEffect", true);
		RESTORE_STORE_ITEMS = offline.getProperty("RestoreStoreItems", false);
	}
	
	private static final void loadSafeDisconnect()
	{
		final ExProperties safeDisconnect = initProperties(SAFE_DISCONNECT_FILE);
		SAFEDISCONNECT_ENABLED = safeDisconnect.getProperty("SafeDisconnectEnabled", true);
		SAFEDISCONNECT_TIMEOUT_MS = safeDisconnect.getProperty("SafeDisconnectTimeoutMs", 300000L);
		SAFEDISCONNECT_ALLOW_OLYMPIAD = safeDisconnect.getProperty("SafeDisconnectAllowOlympiad", false);
		SAFEDISCONNECT_ALLOW_BOSS = safeDisconnect.getProperty("SafeDisconnectAllowBoss", false);
		SAFEDISCONNECT_ALLOW_QUEST = safeDisconnect.getProperty("SafeDisconnectAllowQuest", false);
		SAFEDISCONNECT_ALLOW_FARM = safeDisconnect.getProperty("SafeDisconnectAllowFarm", true);
		SAFEDISCONNECT_INTEGRATION_OLYMPIAD = safeDisconnect.getProperty("SafeDisconnectIntegrationOlympiad", true);
		SAFEDISCONNECT_INTEGRATION_DUNGEON = safeDisconnect.getProperty("SafeDisconnectIntegrationDungeon", true);
		SAFEDISCONNECT_INTEGRATION_TOURNAMENT = safeDisconnect.getProperty("SafeDisconnectIntegrationTournament", true);
		SAFEDISCONNECT_INTEGRATION_TVT = safeDisconnect.getProperty("SafeDisconnectIntegrationTvT", true);
		SAFEDISCONNECT_INTEGRATION_CTF = safeDisconnect.getProperty("SafeDisconnectIntegrationCTF", true);
		SAFEDISCONNECT_INTEGRATION_DM = safeDisconnect.getProperty("SafeDisconnectIntegrationDM", true);
		SAFEDISCONNECT_INTEGRATION_LM = safeDisconnect.getProperty("SafeDisconnectIntegrationLM", true);
		SAFEDISCONNECT_INVULNERABLE = safeDisconnect.getProperty("SafeDisconnectInvulnerable", true);
		SAFEDISCONNECT_IMMOBILIZE = safeDisconnect.getProperty("SafeDisconnectImmobilize", true);
		SAFEDISCONNECT_CHANGE_NAME_COLOR = safeDisconnect.getProperty("SafeDisconnectChangeNameColor", false);
		SAFEDISCONNECT_AUTOFARM_RESTORE_DELAY_MS = safeDisconnect.getProperty("SafeDisconnectAutoFarmRestoreDelayMs", 1500L);
		SAFEDISCONNECT_TITLE = safeDisconnect.getProperty("SafeDisconnectTitle", "Disconnect...");
		
		String nameColor = safeDisconnect.getProperty("SafeDisconnectNameColor", "00FFFF");
		if (nameColor.startsWith("0x") || nameColor.startsWith("0X"))
			nameColor = nameColor.substring(2);
		
		try
		{
			SAFEDISCONNECT_NAME_COLOR = Integer.decode("0x" + nameColor);
		}
		catch (NumberFormatException e)
		{
			SAFEDISCONNECT_NAME_COLOR = Integer.decode("0x00FFFF");
		}
	}
	
	private static final void loadBossZerg()
	{
		final ExProperties bossZerg = initProperties(BOSS_ZERG_FILE);
		BOSS_ZERG_ENABLED = bossZerg.getProperty("BossZergEnabled", true);
		BOSS_ZERG_FLAG_ENABLED = bossZerg.getProperty("BossZergFlagEnabled", true);
		BOSS_ZERG_FLAG_RANGE = bossZerg.getProperty("BossZergFlagRange", 1200);
		BOSS_ZERG_FLAG_RENEW_MS = bossZerg.getProperty("BossZergFlagRenewMs", 10000L);
		BOSS_ZERG_FLAG_MIN_PLAYERS = bossZerg.getProperty("BossZergFlagMinPlayers", 1);
		BOSS_ZERG_FLAG_WARNING_RANGE = bossZerg.getProperty("BossZergFlagWarningRange", 1200);
		BOSS_ZERG_FLAG_AREA_RADIUS = bossZerg.getProperty("BossZergFlagAreaRadius", 600);
		
		String flagColor = bossZerg.getProperty("BossZergFlagAreaColor", "FFA500");
		if (flagColor.startsWith("0x") || flagColor.startsWith("0X"))
			flagColor = flagColor.substring(2);
		
		try
		{
			BOSS_ZERG_FLAG_AREA_COLOR = Integer.decode("0x" + flagColor);
		}
		catch (NumberFormatException e)
		{
			BOSS_ZERG_FLAG_AREA_COLOR = Integer.decode("0xFFA500");
		}
		BOSS_ZERG_RANGE = bossZerg.getProperty("BossZergRange", 1200);
		BOSS_ZERG_MIN_PARTY_SIZE = bossZerg.getProperty("BossZergMinPartySize", 3);
		BOSS_ZERG_MAX_ALLY_MEMBERS = bossZerg.getProperty("BossZergMaxAllyMembers", 18);
		BOSS_ZERG_SHOW_ALLY_COUNT = bossZerg.getProperty("BossZergShowAllyCount", true);
		BOSS_ZERG_ANNOUNCE = bossZerg.getProperty("BossZergAnnounce", true);
		BOSS_ZERG_ANNOUNCE_COOLDOWN_MS = bossZerg.getProperty("BossZergAnnounceCooldownMs", 60000L);
		BOSS_ZERG_SHOW_AREA = bossZerg.getProperty("BossZergShowArea", true);
		BOSS_ZERG_AREA_RADIUS = bossZerg.getProperty("BossZergAreaRadius", 1200);
		BOSS_ZERG_CHECK_INTERVAL_MS = bossZerg.getProperty("BossZergCheckIntervalMs", 3000L);
		BOSS_ZERG_FLAG_AREA_ENABLED = bossZerg.getProperty("BossZergFlagAreaEnabled", true);
		BOSS_ZERG_HEAL_PENALTY_ENABLED = bossZerg.getProperty("BossZergHealPenaltyEnabled", true);
		BOSS_ZERG_HEAL_PENALTY_MULTIPLIER = bossZerg.getProperty("BossZergHealPenaltyMultiplier", 0.75);
		BOSS_ZERG_PRAYER_REVERSE_MULTIPLIER = bossZerg.getProperty("BossZergPrayerReverseMultiplier", 0.85);
		
		final String prayerIds = bossZerg.getProperty("BossZergPrayerSkillIds", "");
		if (prayerIds == null || prayerIds.trim().isEmpty())
		{
			BOSS_ZERG_PRAYER_SKILL_IDS = new int[0];
		}
		else
		{
			final String[] parts = prayerIds.split(",");
			final List<Integer> ids = new ArrayList<>(parts.length);
			for (String part : parts)
			{
				final String trimmed = part.trim();
				if (trimmed.isEmpty())
					continue;
				
				try
				{
					ids.add(Integer.parseInt(trimmed));
				}
				catch (NumberFormatException e)
				{
					LOGGER.warn("BossZerg: Invalid prayer skill id '{}'.", trimmed);
				}
			}
			
			BOSS_ZERG_PRAYER_SKILL_IDS = ids.stream().mapToInt(Integer::intValue).toArray();
		}
		
		final String ignoredBossIds = bossZerg.getProperty("BossZergIgnoredBossIds", "");
		final Set<Integer> ignoredIds = new HashSet<>();
		if (ignoredBossIds != null && !ignoredBossIds.trim().isEmpty())
		{
			final String[] parts = ignoredBossIds.split(",");
			for (String part : parts)
			{
				final String trimmed = part.trim();
				if (trimmed.isEmpty())
					continue;
				
				try
				{
					ignoredIds.add(Integer.parseInt(trimmed));
				}
				catch (NumberFormatException e)
				{
					LOGGER.warn("BossZerg: Invalid ignored boss id '{}'.", trimmed);
				}
			}
		}
		BOSS_ZERG_IGNORED_BOSS_IDS = ignoredIds;
	}
	
	/**
	 * Loads clan and clan hall settings.
	 */
	private static final void loadClans()
	{
		final ExProperties clans = initProperties(CLANS_FILE);
		
		CLAN_JOIN_DAYS = clans.getProperty("DaysBeforeJoinAClan", 5);
		CLAN_CREATE_DAYS = clans.getProperty("DaysBeforeCreateAClan", 10);
		MAX_NUM_OF_CLANS_IN_ALLY = clans.getProperty("MaxNumOfClansInAlly", 3);
		CLAN_MEMBERS_FOR_WAR = clans.getProperty("ClanMembersForWar", 15);
		CLAN_WAR_PENALTY_WHEN_ENDED = clans.getProperty("ClanWarPenaltyWhenEnded", 5);
		CLAN_DISSOLVE_DAYS = clans.getProperty("DaysToPassToDissolveAClan", 7);
		ALLY_JOIN_DAYS_WHEN_LEAVED = clans.getProperty("DaysBeforeJoinAllyWhenLeaved", 1);
		ALLY_JOIN_DAYS_WHEN_DISMISSED = clans.getProperty("DaysBeforeJoinAllyWhenDismissed", 1);
		ACCEPT_CLAN_DAYS_WHEN_DISMISSED = clans.getProperty("DaysBeforeAcceptNewClanWhenDismissed", 1);
		CREATE_ALLY_DAYS_WHEN_DISSOLVED = clans.getProperty("DaysBeforeCreateNewAllyWhenDissolved", 10);
		MEMBERS_CAN_WITHDRAW_FROM_CLANWH = clans.getProperty("MembersCanWithdrawFromClanWH", false);
		
		MANOR_REFRESH_TIME = clans.getProperty("ManorRefreshTime", 20);
		MANOR_REFRESH_MIN = clans.getProperty("ManorRefreshMin", 0);
		MANOR_APPROVE_TIME = clans.getProperty("ManorApproveTime", 6);
		MANOR_APPROVE_MIN = clans.getProperty("ManorApproveMin", 0);
		MANOR_MAINTENANCE_MIN = clans.getProperty("ManorMaintenanceMin", 6);
		MANOR_SAVE_PERIOD_RATE = clans.getProperty("ManorSavePeriodRate", 2) * 3600000;
		
		CS_TELE_FEE_RATIO = clans.getProperty("CastleTeleportFunctionFeeRatio", 604800000L);
		CS_TELE1_FEE = clans.getProperty("CastleTeleportFunctionFeeLvl1", 7000);
		CS_TELE2_FEE = clans.getProperty("CastleTeleportFunctionFeeLvl2", 14000);
		CS_SUPPORT_FEE_RATIO = clans.getProperty("CastleSupportFunctionFeeRatio", 86400000L);
		CS_SUPPORT1_FEE = clans.getProperty("CastleSupportFeeLvl1", 7000);
		CS_SUPPORT2_FEE = clans.getProperty("CastleSupportFeeLvl2", 21000);
		CS_SUPPORT3_FEE = clans.getProperty("CastleSupportFeeLvl3", 37000);
		CS_SUPPORT4_FEE = clans.getProperty("CastleSupportFeeLvl4", 52000);
		CS_MPREG_FEE_RATIO = clans.getProperty("CastleMpRegenerationFunctionFeeRatio", 86400000L);
		CS_MPREG1_FEE = clans.getProperty("CastleMpRegenerationFeeLvl1", 2000);
		CS_MPREG2_FEE = clans.getProperty("CastleMpRegenerationFeeLvl2", 6500);
		CS_MPREG3_FEE = clans.getProperty("CastleMpRegenerationFeeLvl3", 13750);
		CS_MPREG4_FEE = clans.getProperty("CastleMpRegenerationFeeLvl4", 20000);
		CS_HPREG_FEE_RATIO = clans.getProperty("CastleHpRegenerationFunctionFeeRatio", 86400000L);
		CS_HPREG1_FEE = clans.getProperty("CastleHpRegenerationFeeLvl1", 1000);
		CS_HPREG2_FEE = clans.getProperty("CastleHpRegenerationFeeLvl2", 1500);
		CS_HPREG3_FEE = clans.getProperty("CastleHpRegenerationFeeLvl3", 2250);
		CS_HPREG4_FEE = clans.getProperty("CastleHpRegenerationFeeLvl4", 3270);
		CS_HPREG5_FEE = clans.getProperty("CastleHpRegenerationFeeLvl5", 5166);
		CS_EXPREG_FEE_RATIO = clans.getProperty("CastleExpRegenerationFunctionFeeRatio", 86400000L);
		CS_EXPREG1_FEE = clans.getProperty("CastleExpRegenerationFeeLvl1", 9000);
		CS_EXPREG2_FEE = clans.getProperty("CastleExpRegenerationFeeLvl2", 15000);
		CS_EXPREG3_FEE = clans.getProperty("CastleExpRegenerationFeeLvl3", 21000);
		CS_EXPREG4_FEE = clans.getProperty("CastleExpRegenerationFeeLvl4", 30000);
	}
	
	private static final void loadBossHealConfigs()
	{
	    final ExProperties bossHeal = initProperties(BOSS_HEAL_FILE);
	    
	    BLOCK_HEAL_ON_RAIDBOSS = bossHeal.getProperty("BlockHealOnRaidBoss", true);
	    BLOCK_HEAL_ON_GRANDBOSS = bossHeal.getProperty("BlockHealOnGrandBoss", true);
	    
	    String blockedIds = bossHeal.getProperty("BlockedHealSkillIds", "");
	    if (!blockedIds.isEmpty()) {
	        for (String id : blockedIds.split(",")) {
	            try {
	                BLOCKED_HEAL_SKILL_IDS.add(Integer.parseInt(id.trim()));
	            } catch (NumberFormatException e) {
	                LOGGER.warn("Invalid skill ID in BlockedHealSkillIds: {}", id);
	            }
	        }
	    }
	    
	    HEAL_BLOCK_MESSAGE = bossHeal.getProperty("HealBlockMessage", "");
	}
	
	/**
	 * Loads event settings.<br>
	 * Such as olympiad, seven signs festival, four sepulchures, dimensional rift, weddings, lottery, fishing championship.
	 */
	private static final void loadEvents()
	{
		final ExProperties events = initProperties(EVENTS_FILE);
		
		OLY_ENABLED = events.getProperty("OlympiadEnabled", true);
		OLY_START_TIME = events.getProperty("OlyStartTime", 18);
		OLY_MIN = events.getProperty("OlyMin", 0);
		OLY_CPERIOD = events.getProperty("OlyCPeriod", 21600000L);
		OLY_BATTLE = events.getProperty("OlyBattle", 180000L);
		OLY_WPERIOD = events.getProperty("OlyWPeriod", 604800000L);
		OLY_VPERIOD = events.getProperty("OlyVPeriod", 86400000L);
		OLY_WAIT_TIME = events.getProperty("OlyWaitTime", 30);
		OLY_WAIT_BATTLE = events.getProperty("OlyWaitBattle", 60);
		OLY_WAIT_END = events.getProperty("OlyWaitEnd", 40);
		OLY_START_POINTS = events.getProperty("OlyStartPoints", 18);
		OLY_WEEKLY_POINTS = events.getProperty("OlyWeeklyPoints", 3);
		OLY_MIN_MATCHES = events.getProperty("OlyMinMatchesToBeClassed", 5);
		OLY_CLASSED = events.getProperty("OlyClassedParticipants", 5);
		OLY_NONCLASSED = events.getProperty("OlyNonClassedParticipants", 9);
		OLY_CLASSED_REWARD = events.parseIntIntList("OlyClassedReward", "6651-50");
		OLY_NONCLASSED_REWARD = events.parseIntIntList("OlyNonClassedReward", "6651-30");
		OLY_GP_PER_POINT = events.getProperty("OlyGPPerPoint", 1000);
		OLY_HERO_POINTS = events.getProperty("OlyHeroPoints", 300);
		OLY_RANK1_POINTS = events.getProperty("OlyRank1Points", 100);
		OLY_RANK2_POINTS = events.getProperty("OlyRank2Points", 75);
		OLY_RANK3_POINTS = events.getProperty("OlyRank3Points", 55);
		OLY_RANK4_POINTS = events.getProperty("OlyRank4Points", 40);
		OLY_RANK5_POINTS = events.getProperty("OlyRank5Points", 30);
		OLY_MAX_POINTS = events.getProperty("OlyMaxPoints", 10);
		OLY_DIVIDER_CLASSED = events.getProperty("OlyDividerClassed", 3);
		OLY_DIVIDER_NON_CLASSED = events.getProperty("OlyDividerNonClassed", 5);
		OLY_ANNOUNCE_GAMES = events.getProperty("OlyAnnounceGames", true);
		OLY_ENCHANT_LIMIT = events.getProperty("OlyMaxEnchant", -1);
		OLY_SHOW_MONTHLY_WINNERS = events.getProperty("OlyShowMonthlyWinners", false);
		
		
		
		SEVEN_SIGNS_BYPASS_PREREQUISITES = events.getProperty("SevenSignsBypassPrerequisites", false);
		FESTIVAL_MIN_PLAYER = Math.clamp(events.getProperty("FestivalMinPlayer", 5), 2, 9);
		MAXIMUM_PLAYER_CONTRIB = events.getProperty("MaxPlayerContrib", 1000000);
		

		
		FS_PARTY_MEMBER_COUNT = Math.clamp(events.getProperty("NeededPartyMembers", 4), 2, 9);
		
		RIFT_MIN_PARTY_SIZE = events.getProperty("RiftMinPartySize", 2);
		RIFT_AUTO_JUMPS_TIME_MIN = events.getProperty("AutoJumpsDelayMin", 8);
		RIFT_AUTO_JUMPS_TIME_RND = events.getProperty("AutoJumpsDelayRnd", 5);
		RIFT_ENTER_COST_RECRUIT = events.getProperty("RecruitCost", 21);
		RIFT_ENTER_COST_SOLDIER = events.getProperty("SoldierCost", 24);
		RIFT_ENTER_COST_OFFICER = events.getProperty("OfficerCost", 27);
		RIFT_ENTER_COST_CAPTAIN = events.getProperty("CaptainCost", 30);
		RIFT_ENTER_COST_COMMANDER = events.getProperty("CommanderCost", 33);
		RIFT_ENTER_COST_HERO = events.getProperty("HeroCost", 36);
		RIFT_ANAKAZEL_PORT_CHANCE = events.getProperty("AnakazelPortChance", 15);
		
		LOTTERY_PRIZE = events.getProperty("LotteryPrize", 50000);
		LOTTERY_TICKET_PRICE = events.getProperty("LotteryTicketPrice", 2000);
		LOTTERY_5_NUMBER_RATE = events.getProperty("Lottery5NumberRate", 0.6);
		LOTTERY_4_NUMBER_RATE = events.getProperty("Lottery4NumberRate", 0.2);
		LOTTERY_3_NUMBER_RATE = events.getProperty("Lottery3NumberRate", 0.2);
		LOTTERY_2_AND_1_NUMBER_PRIZE = events.getProperty("Lottery2and1NumberPrize", 200);
		
		ALLOW_FISH_CHAMPIONSHIP = events.getProperty("AllowFishChampionship", true);
		FISH_CHAMPIONSHIP_REWARD_ITEM = events.getProperty("FishChampionshipRewardItemId", 57);
		FISH_CHAMPIONSHIP_REWARD_1 = events.getProperty("FishChampionshipReward1", 800000);
		FISH_CHAMPIONSHIP_REWARD_2 = events.getProperty("FishChampionshipReward2", 500000);
		FISH_CHAMPIONSHIP_REWARD_3 = events.getProperty("FishChampionshipReward3", 300000);
		FISH_CHAMPIONSHIP_REWARD_4 = events.getProperty("FishChampionshipReward4", 200000);
		FISH_CHAMPIONSHIP_REWARD_5 = events.getProperty("FishChampionshipReward5", 100000);
		
		COFFER_PRICE_ID = events.getProperty("CofferPriceId", 57);
		COFFER_PRICE_AMOUNT = events.getProperty("CofferPriceCount", 50000);
		
		EVENT_COMMANDS = events.getProperty("AllowEventCommands", false);
		
		CTF_EVENT_ENABLED = events.getProperty("CTFEventEnabled", false);
		CTF_EVENT_INTERVAL = events.getProperty("CTFEventInterval", "00:00,04:00,08:00,12:00,16:00,20:00").split(",");
		CTF_EVENT_PARTICIPATION_TIME = events.getProperty("CTFEventParticipationTime", 3600);
		CTF_EVENT_RUNNING_TIME = events.getProperty("CTFEventRunningTime", 1800);
		CTF_NPC_LOC_NAME = events.getProperty("CTFNpcLocName", "Giran Town");
		CTF_EVENT_PARTICIPATION_NPC_ID = events.getProperty("CTFEventParticipationNpcId", 0);
		CTF_EVENT_TEAM_1_HEADQUARTERS_ID = events.getProperty("CTFEventFirstTeamHeadquartersId", 0);
		CTF_EVENT_TEAM_2_HEADQUARTERS_ID = events.getProperty("CTFEventSecondTeamHeadquartersId", 0);
		CTF_EVENT_TEAM_1_FLAG = events.getProperty("CTFEventFirstTeamFlag", 0);
		CTF_EVENT_TEAM_2_FLAG = events.getProperty("CTFEventSecondTeamFlag", 0);
		CTF_EVENT_CAPTURE_SKILL = events.getProperty("CTFEventCaptureSkillId", 0);
		CTF_EVENT_PARTICIPATION_FEE = events.getProperty("CTFEventParticipationFee", new int[]
		{
			4037,
			50
		});
		CTF_EVENT_PARTICIPATION_NPC_COORDINATES = events.getProperty("CTFEventParticipationNpcCoordinates", new int[]
		{
			83425,
			148585,
			-3406,
			0
		});
		CTF_EVENT_MIN_PLAYERS_IN_TEAMS = events.getProperty("CTFEventMinPlayersInTeams", 1);
		CTF_EVENT_MAX_PLAYERS_IN_TEAMS = events.getProperty("CTFEventMaxPlayersInTeams", 20);
		CTF_EVENT_MIN_LVL = Byte.parseByte(events.getProperty("CTFEventMinPlayerLevel", "1"));
		CTF_EVENT_MAX_LVL = Byte.parseByte(events.getProperty("CTFEventMaxPlayerLevel", "80"));
		CTF_EVENT_RESPAWN_TELEPORT_DELAY = events.getProperty("CTFEventRespawnTeleportDelay", 20);
		CTF_EVENT_START_LEAVE_TELEPORT_DELAY = events.getProperty("CTFEventStartLeaveTeleportDelay", 20);
		CTF_EVENT_TEAM_1_NAME = events.getProperty("CTFEventTeam1Name", "Team1");
		CTF_EVENT_TEAM_1_COORDINATES = events.getProperty("CTFEventTeam1Coordinates", new int[]
		{
			148607,
			46719,
			-3414
		});
		CTF_EVENT_TEAM_1_FLAG_COORDINATES = events.getProperty("CTFEventTeam1FlagCoordinates", new int[]
		{
			148314,
			46715,
			-3412,
			0
		});
		CTF_EVENT_TEAM_2_NAME = events.getProperty("CTFEventTeam2Name", "Team2");
		CTF_EVENT_TEAM_2_COORDINATES = events.getProperty("CTFEventTeam2Coordinates", new int[]
		{
			150439,
			46731,
			-3414
		});
		CTF_EVENT_TEAM_2_FLAG_COORDINATES = events.getProperty("CTFEventTeam2FlagCoordinates", new int[]
		{
			150686,
			46713,
			-3414,
			0
		});
		
		CTF_EVENT_REWARDS = events.parseIntIntList("CTFEventReward", "57-100000;4037-20");
		CTF_EVENT_TARGET_TEAM_MEMBERS_ALLOWED = events.getProperty("CTFEventTargetTeamMembersAllowed", true);
		CTF_EVENT_SCROLL_ALLOWED = events.getProperty("CTFEventScrollsAllowed", false);
		CTF_EVENT_POTIONS_ALLOWED = events.getProperty("CTFEventPotionsAllowed", false);
		CTF_EVENT_SUMMON_BY_ITEM_ALLOWED = events.getProperty("CTFEventSummonByItemAllowed", false);
		
		String[] CTFDoorsToOpen = events.getProperty("CTFDoorsToOpen", "24190001;24190002;24190003;24190004").split(";");
		CTF_DOORS_IDS_TO_OPEN = new ArrayList<>(CTFDoorsToOpen.length);
		for (String item : CTFDoorsToOpen)
		{
			Integer itm = 0;
			try
			{
				itm = Integer.parseInt(item);
			}
			catch (NumberFormatException nfe)
			{
				LOGGER.warn("CTFDoors: Wrong doorId passed: " + item);
			}
			
			if (itm != 0)
				CTF_DOORS_IDS_TO_OPEN.add(itm);
		}
		
		String[] CTFDoorsToClose = events.getProperty("CTFDoorsToClose", "24190001;24190002;24190003;24190004").split(";");
		CTF_DOORS_IDS_TO_CLOSE = new ArrayList<>(CTFDoorsToClose.length);
		for (String item : CTFDoorsToClose)
		{
			Integer itm = 0;
			try
			{
				itm = Integer.parseInt(item);
			}
			catch (NumberFormatException nfe)
			{
				LOGGER.warn("CTFDoors: Wrong doorId passed: " + item);
			}
			
			if (itm != 0)
				CTF_DOORS_IDS_TO_CLOSE.add(itm);
		}
		
		CTF_REWARD_TEAM_TIE = events.getProperty("CTFRewardTeamTie", false);
		CTF_EVENT_EFFECTS_REMOVAL = events.getProperty("CTFEventEffectsRemoval", 0);
		
		CTF_EVENT_FIGHTER_BUFFS = new HashMap<>();
		String[] CtfFighterBuffs = events.getProperty("CTFEventFighterBuffs", (String[]) null, ";");
		if (CtfFighterBuffs != null)
		{
			for (String itemData : CtfFighterBuffs)
			{
				if (!itemData.isEmpty())
				{
					String[] item = itemData.split(",");
					CTF_EVENT_FIGHTER_BUFFS.put(Integer.parseInt(item[0]), Integer.parseInt(item[1]));
				}
			}
		}
		
		CTF_EVENT_MAGE_BUFFS = new HashMap<>();
		String[] CtfMageBuffs = events.getProperty("CTFEventMageBuffs", (String[]) null, ";");
		if (CtfMageBuffs != null)
		{
			for (String itemData : CtfMageBuffs)
			{
				if (!itemData.isEmpty())
				{
					String[] item = itemData.split(",");
					CTF_EVENT_MAGE_BUFFS.put(Integer.parseInt(item[0]), Integer.parseInt(item[1]));
				}
			}
		}
		
		ALLOW_CTF_DLG = events.getProperty("AllowDlgCTFInvite", false);
		CTF_EVENT_MAX_PARTICIPANTS_PER_IP = events.getProperty("CTFEventMaxParticipantsPerIP", 0);
		
		DM_EVENT_ENABLED = events.getProperty("DMEventEnabled", false);
		DM_EVENT_INTERVAL = events.getProperty("DMEventInterval", "01:00,05:00,09:00,13:00,17:00,21:00").split(",");
		DM_EVENT_PARTICIPATION_TIME = events.getProperty("DMEventParticipationTime", 3600);
		DM_EVENT_RUNNING_TIME = events.getProperty("DMEventRunningTime", 1800);
		DM_NPC_LOC_NAME = events.getProperty("DMNpcLocName", "Giran Town");
		DM_EVENT_PARTICIPATION_NPC_ID = events.getProperty("DMEventParticipationNpcId", 0);
		DM_EVENT_PARTICIPATION_FEE = events.getProperty("DMEventParticipationFee", new int[]
		{
			4037,
			50
		});
		
		DM_EVENT_PARTICIPATION_NPC_COORDINATES = events.getProperty("DMEventParticipationNpcCoordinates", new int[]
		{
			83425,
			148585,
			-3406,
			0
		});
		
		DM_EVENT_MIN_PLAYERS = events.getProperty("DMEventMinPlayers", 1);
		DM_EVENT_MAX_PLAYERS = events.getProperty("DMEventMaxPlayers", 20);
		DM_EVENT_MIN_LVL = (byte) events.getProperty("DMEventMinPlayerLevel", 1);
		DM_EVENT_MAX_LVL = (byte) events.getProperty("DMEventMaxPlayerLevel", 80);
		
		DM_EVENT_PLAYER_COORDINATES = new ArrayList<>();
		String[] propertySplit = events.getProperty("DMEventPlayerCoordinates", "0,0,0").split(";");
		for (String coordPlayer : propertySplit)
		{
			String[] coordSplit = coordPlayer.split(",");
			if (coordSplit.length != 3)
				LOGGER.warn("DMEventPlayerCoordinates \"" + coordPlayer + "\"");
			else
			{
				try
				{
					DM_EVENT_PLAYER_COORDINATES.add(new int[]
					{
						Integer.parseInt(coordSplit[0]),
						Integer.parseInt(coordSplit[1]),
						Integer.parseInt(coordSplit[2])
					});
				}
				catch (NumberFormatException nfe)
				{
					if (!coordPlayer.isEmpty())
						LOGGER.warn("DMEventPlayerCoordinates \"" + coordPlayer + "\"");
				}
			}
		}
		
		DM_EVENT_RESPAWN_TELEPORT_DELAY = events.getProperty("DMEventRespawnTeleportDelay", 20);
		DM_EVENT_START_LEAVE_TELEPORT_DELAY = events.getProperty("DMEventStartLeaveTeleportDelay", 20);
		DM_SHOW_TOP_RANK = events.getProperty("DMShowTopRank", false);
		DM_TOP_RANK = events.getProperty("DMTopRank", 10);
		
		DM_EVENT_REWARDS = new HashMap<>();
		DM_REWARD_FIRST_PLAYERS = events.getProperty("DMRewardFirstPlayers", 3);
		propertySplit = events.getProperty("DMEventReward", "57,100000;5575,5000|57,50000|57,25000").split("\\|");
		int i = 1;
		if (DM_REWARD_FIRST_PLAYERS < propertySplit.length)
			LOGGER.warn("DMRewardFirstPlayers < DMEventReward");
		else
		{
			for (String pos : propertySplit)
			{
				List<int[]> value = new ArrayList<>();
				String[] rewardSplit = pos.split("\\;");
				for (String rewards : rewardSplit)
				{
					String[] reward = rewards.split("\\-");
					if (reward.length != 2)
						LOGGER.warn("DMEventReward \"" + pos + "\"");
					else
					{
						try
						{
							value.add(new int[]
							{
								Integer.parseInt(reward[0]),
								Integer.parseInt(reward[1])
							});
						}
						catch (NumberFormatException nfe)
						{
							LOGGER.warn("DMEventReward \"" + pos + "\"");
						}
					}
					
					try
					{
						if (value.isEmpty())
							DM_EVENT_REWARDS.put(i, DM_EVENT_REWARDS.get(i - 1));
						else
							DM_EVENT_REWARDS.put(i, value);
					}
					catch (Exception e)
					{
						LOGGER.warn("DMEventReward array index out of bounds (1)");
						e.printStackTrace();
					}
					i++;
				}
			}
			
			int countPosRewards = DM_EVENT_REWARDS.size();
			if (countPosRewards < DM_REWARD_FIRST_PLAYERS)
			{
				for (i = countPosRewards + 1; i <= DM_REWARD_FIRST_PLAYERS; i++)
				{
					try
					{
						DM_EVENT_REWARDS.put(i, DM_EVENT_REWARDS.get(i - 1));
					}
					catch (Exception e)
					{
						LOGGER.warn("DMEventReward array index out of bounds (2)");
						e.printStackTrace();
					}
				}
			}
		}
		
		DM_REWARD_PLAYERS_TIE = events.getProperty("DMRewardPlayersTie", false);
		
		DM_EVENT_SCROLL_ALLOWED = events.getProperty("DMEventScrollsAllowed", false);
		DM_EVENT_POTIONS_ALLOWED = events.getProperty("DMEventPotionsAllowed", false);
		DM_EVENT_SUMMON_BY_ITEM_ALLOWED = events.getProperty("DMEventSummonByItemAllowed", false);
		
		String[] DMDoorsToOpen = events.getProperty("DMDoorsToOpen", "24190001;24190002;24190003;24190004").split(";");
		DM_DOORS_IDS_TO_OPEN = new ArrayList<>(DMDoorsToOpen.length);
		for (String item : DMDoorsToOpen)
		{
			Integer itm = 0;
			try
			{
				itm = Integer.parseInt(item);
			}
			catch (NumberFormatException nfe)
			{
				LOGGER.warn("DMDoors: Wrong doorId passed: " + item);
			}
			
			if (itm != 0)
				DM_DOORS_IDS_TO_OPEN.add(itm);
		}
		
		String[] DMDoorsToClose = events.getProperty("DMDoorsToClose", "24190001;24190002;24190003;24190004").split(";");
		DM_DOORS_IDS_TO_CLOSE = new ArrayList<>(DMDoorsToClose.length);
		for (String item : DMDoorsToClose)
		{
			Integer itm = 0;
			try
			{
				itm = Integer.parseInt(item);
			}
			catch (NumberFormatException nfe)
			{
				LOGGER.warn("DMDoors: Wrong doorId passed: " + item);
			}
			
			if (itm != 0)
				DM_DOORS_IDS_TO_CLOSE.add(itm);
		}
		
		DM_EVENT_EFFECTS_REMOVAL = events.getProperty("DMEventEffectsRemoval", 0);
		
		DM_EVENT_FIGHTER_BUFFS = new HashMap<>();
		String[] DmFighterBuffs = events.getProperty("DMEventFighterBuffs", (String[]) null, ";");
		if (DmFighterBuffs != null)
		{
			for (String itemData : DmFighterBuffs)
			{
				if (!itemData.isEmpty())
				{
					String[] item = itemData.split(",");
					DM_EVENT_FIGHTER_BUFFS.put(Integer.parseInt(item[0]), Integer.parseInt(item[1]));
				}
			}
		}
		
		DM_EVENT_MAGE_BUFFS = new HashMap<>();
		String[] DmMageBuffs = events.getProperty("DMEventMageBuffs", (String[]) null, ";");
		if (DmMageBuffs != null)
		{
			for (String itemData : DmMageBuffs)
			{
				if (!itemData.isEmpty())
				{
					String[] item = itemData.split(",");
					DM_EVENT_MAGE_BUFFS.put(Integer.parseInt(item[0]), Integer.parseInt(item[1]));
				}
			}
		}
		
		DISABLE_ID_CLASSES_STRING_DM = events.getProperty("DMDisabledForClasses");
		DISABLE_ID_CLASSES_DM = new ArrayList<>();
		for (String classId : DISABLE_ID_CLASSES_STRING_DM.split(","))
			DISABLE_ID_CLASSES_DM.add(Integer.parseInt(classId));
		
		ALLOW_DM_DLG = events.getProperty("AllowDlgDMInvite", false);
		DM_EVENT_MAX_PARTICIPANTS_PER_IP = events.getProperty("DMEventMaxParticipantsPerIP", 0);
		
		LM_EVENT_ENABLED = events.getProperty("LMEventEnabled", false);
		LM_EVENT_INTERVAL = events.getProperty("LMEventInterval", "02:00,06:00,10:00,14:00,18:00,22:00").split(",");
		LM_EVENT_PARTICIPATION_TIME = events.getProperty("LMEventParticipationTime", 3600);
		LM_EVENT_HERO = events.getProperty("LMEventHero", false);
		LV_EVENT_HERO_DAYS = events.getProperty("LMEventHeroDays", 1);
		LM_EVENT_RUNNING_TIME = events.getProperty("LMEventRunningTime", 1800);
		LM_EVENT_PLAYER_CREDITS = Short.parseShort(events.getProperty("LMEventPlayerCredits", "1"));
		LM_NPC_LOC_NAME = events.getProperty("LMNpcLocName", "Giran Town");
		LM_EVENT_PARTICIPATION_NPC_ID = events.getProperty("LMEventParticipationNpcId", 0);
		LM_EVENT_PARTICIPATION_FEE = events.getProperty("LMEventParticipationFee", new int[]
		{
			4037,
			50
		});
		LM_EVENT_PARTICIPATION_NPC_COORDINATES = events.getProperty("LMEventParticipationNpcCoordinates", new int[]
		{
			83425,
			148585,
			-3406,
			0
		});
		LM_EVENT_MIN_PLAYERS = events.getProperty("LMEventMinPlayers", 1);
		LM_EVENT_MAX_PLAYERS = events.getProperty("LMEventMaxPlayers", 20);
		LM_EVENT_MIN_LVL = (byte) events.getProperty("LMEventMinPlayerLevel", 1);
		LM_EVENT_MAX_LVL = (byte) events.getProperty("LMEventMaxPlayerLevel", 80);
		
		LM_EVENT_PLAYER_COORDINATES = new ArrayList<>();
		String[] propertySplitLM = events.getProperty("LMEventPlayerCoordinates", "0,0,0").split(";");
		for (String coordPlayer : propertySplitLM)
		{
			String[] coordSplit = coordPlayer.split(",");
			if (coordSplit.length != 3)
				LOGGER.warn("LMEventPlayerCoordinates \"" + coordPlayer + "\"");
			else
			{
				try
				{
					LM_EVENT_PLAYER_COORDINATES.add(new int[]
					{
						Integer.parseInt(coordSplit[0]),
						Integer.parseInt(coordSplit[1]),
						Integer.parseInt(coordSplit[2])
					});
				}
				catch (NumberFormatException nfe)
				{
					if (!coordPlayer.isEmpty())
						LOGGER.warn("LMEventPlayerCoordinates \"" + coordPlayer + "\"");
				}
			}
		}
		
		LM_EVENT_RESPAWN_TELEPORT_DELAY = events.getProperty("LMEventRespawnTeleportDelay", 20);
		LM_EVENT_START_LEAVE_TELEPORT_DELAY = events.getProperty("LMEventStartLeaveTeleportDelay", 20);
		LM_EVENT_REWARDS = events.parseIntIntList("LMEventReward", "4037-50;57-100000");
		LM_REWARD_PLAYERS_TIE = events.getProperty("LMRewardPlayersTie", false);
		LM_EVENT_SCROLL_ALLOWED = events.getProperty("LMEventScrollsAllowed", false);
		LM_EVENT_POTIONS_ALLOWED = events.getProperty("LMEventPotionsAllowed", false);
		LM_EVENT_SUMMON_BY_ITEM_ALLOWED = events.getProperty("LMEventSummonByItemAllowed", false);
		
		String[] LMDoorsToOpen = events.getProperty("LMDoorsToOpen", "24190001;24190002;24190003;24190004").split(";");
		LM_DOORS_IDS_TO_OPEN = new ArrayList<>(LMDoorsToOpen.length);
		for (String item : LMDoorsToOpen)
		{
			Integer itm = 0;
			try
			{
				itm = Integer.parseInt(item);
			}
			catch (NumberFormatException nfe)
			{
				LOGGER.warn("LMDoors: Wrong doorId passed: " + item);
			}
			
			if (itm != 0)
				LM_DOORS_IDS_TO_OPEN.add(itm);
		}
		
		String[] LMDoorsToClose = events.getProperty("LMDoorsToClose", "24190001;24190002;24190003;24190004").split(";");
		LM_DOORS_IDS_TO_CLOSE = new ArrayList<>(LMDoorsToClose.length);
		for (String item : LMDoorsToClose)
		{
			Integer itm = 0;
			try
			{
				itm = Integer.parseInt(item);
			}
			catch (NumberFormatException nfe)
			{
				LOGGER.warn("LMDoors: Wrong doorId passed: " + item);
			}
			
			if (itm != 0)
				LM_DOORS_IDS_TO_CLOSE.add(itm);
		}
		
		LM_EVENT_EFFECTS_REMOVAL = events.getProperty("LMEventEffectsRemoval", 0);
		
		LM_EVENT_FIGHTER_BUFFS = new HashMap<>();
		String[] LmFighterBuffs = events.getProperty("LMEventFighterBuffs", (String[]) null, ";");
		if (LmFighterBuffs != null)
		{
			for (String itemData : LmFighterBuffs)
			{
				if (!itemData.isEmpty())
				{
					String[] item = itemData.split(",");
					LM_EVENT_FIGHTER_BUFFS.put(Integer.parseInt(item[0]), Integer.parseInt(item[1]));
				}
			}
		}
		
		LM_EVENT_MAGE_BUFFS = new HashMap<>();
		String[] LmMageBuffs = events.getProperty("LMEventMageBuffs", (String[]) null, ";");
		if (LmMageBuffs != null)
		{
			for (String itemData : LmMageBuffs)
			{
				if (!itemData.isEmpty())
				{
					String[] item = itemData.split(",");
					LM_EVENT_MAGE_BUFFS.put(Integer.parseInt(item[0]), Integer.parseInt(item[1]));
				}
			}
		}
		
		DISABLE_ID_CLASSES_STRING_LM = events.getProperty("LMDisabledForClasses");
		DISABLE_ID_CLASSES_LM = new ArrayList<>();
		for (String classId : DISABLE_ID_CLASSES_STRING_LM.split(","))
			DISABLE_ID_CLASSES_LM.add(Integer.parseInt(classId));
		
		ALLOW_LM_DLG = events.getProperty("AllowDlgLMInvite", false);
		LM_EVENT_MAX_PARTICIPANTS_PER_IP = events.getProperty("LMEventMaxParticipantsPerIP", 0);
		
		TVT_EVENT_ENABLED = events.getProperty("TvTEventEnabled", false);
		TVT_EVENT_INTERVAL = events.getProperty("TvTEventInterval", "03:00,07:00,11:00,15:00,19:00,23:00").split(",");
		TVT_EVENT_PARTICIPATION_TIME = events.getProperty("TvTEventParticipationTime", 3600);
		TVT_EVENT_RUNNING_TIME = events.getProperty("TvTEventRunningTime", 1800);
		TVT_NPC_LOC_NAME = events.getProperty("TvTNpcLocName", "Giran Town");
		TVT_EVENT_PARTICIPATION_NPC_ID = events.getProperty("TvTEventParticipationNpcId", 0);
		
		TVT_EVENT_PARTICIPATION_NPC_COORDINATES = events.getProperty("TvTEventParticipationNpcCoordinates", new int[]
		{
			83425,
			148585,
			-3406,
			0
		});
		
		TVT_EVENT_PARTICIPATION_FEE = events.getProperty("TvTEventParticipationFee", new int[]
		{
			4037,
			50
		});
		
		TVT_EVENT_REWARDS = events.parseIntIntList("TvTEventReward", "57-100000;4037-20");
		TVT_EVENT_MIN_PLAYERS_IN_TEAMS = events.getProperty("TvTEventMinPlayersInTeams", 1);
		TVT_EVENT_MAX_PLAYERS_IN_TEAMS = events.getProperty("TvTEventMaxPlayersInTeams", 20);
		TVT_EVENT_MIN_LVL = Byte.parseByte(events.getProperty("TvTEventMinPlayerLevel", "1"));
		TVT_EVENT_MAX_LVL = Byte.parseByte(events.getProperty("TvTEventMaxPlayerLevel", "80"));
		TVT_EVENT_RESPAWN_TELEPORT_DELAY = events.getProperty("TvTEventRespawnTeleportDelay", 20);
		TVT_EVENT_START_LEAVE_TELEPORT_DELAY = events.getProperty("TvTEventStartLeaveTeleportDelay", 20);
		TVT_EVENT_EFFECTS_REMOVAL = events.getProperty("TvTEventEffectsRemoval", 0);
		TVT_EVENT_TEAM_1_NAME = events.getProperty("TvTEventTeam1Name", "Team1");
		TVT_EVENT_TEAM_1_COORDINATES = events.getProperty("TvTEventTeam1Coordinates", new int[]
		{
			148476,
			46061,
			-3411
		});
		TVT_EVENT_TEAM_2_NAME = events.getProperty("TvTEventTeam2Name", "Team2");
		TVT_EVENT_TEAM_2_COORDINATES = events.getProperty("TvTEventTeam2Coordinates", new int[]
		{
			150480,
			47444,
			-3411
		});
		TVT_EVENT_TARGET_TEAM_MEMBERS_ALLOWED = events.getProperty("TvTEventTargetTeamMembersAllowed", true);
		TVT_EVENT_SCROLL_ALLOWED = events.getProperty("TvTEventScrollsAllowed", false);
		TVT_EVENT_POTIONS_ALLOWED = events.getProperty("TvTEventPotionsAllowed", false);
		TVT_EVENT_SUMMON_BY_ITEM_ALLOWED = events.getProperty("TvTEventSummonByItemAllowed", false);
		
		String[] tvTDoorsToOpen = events.getProperty("TvTDoorsToOpen", "24190001;24190002;24190003;24190004").split(";");
		TVT_DOORS_IDS_TO_OPEN = new ArrayList<>(tvTDoorsToOpen.length);
		for (String item : tvTDoorsToOpen)
		{
			Integer itm = 0;
			try
			{
				itm = Integer.parseInt(item);
			}
			catch (NumberFormatException nfe)
			{
				LOGGER.warn("TVTDoors: Wrong doorId passed: " + item);
			}
			
			if (itm != 0)
				TVT_DOORS_IDS_TO_OPEN.add(itm);
		}
		
		String[] tvTDoorsToClose = events.getProperty("TvTDoorsToClose", "24190001;24190002;24190003;24190004").split(";");
		TVT_DOORS_IDS_TO_CLOSE = new ArrayList<>(tvTDoorsToClose.length);
		for (String item : tvTDoorsToClose)
		{
			Integer itm = 0;
			try
			{
				itm = Integer.parseInt(item);
			}
			catch (NumberFormatException nfe)
			{
				LOGGER.warn("TVTDoors: Wrong doorId passed: " + item);
			}
			
			if (itm != 0)
				TVT_DOORS_IDS_TO_CLOSE.add(itm);
		}
		
		TVT_REWARD_TEAM_TIE = events.getProperty("TvTRewardTeamTie", false);
		TVT_EVENT_EFFECTS_REMOVAL = events.getProperty("TvTEventEffectsRemoval", 0);
		TVT_EVENT_FIGHTER_BUFFS = new HashMap<>();
		String[] TvtFighterBuffs = events.getProperty("TvTEventFighterBuffs", (String[]) null, ";");
		if (TvtFighterBuffs != null)
		{
			for (String itemData : TvtFighterBuffs)
			{
				if (!itemData.isEmpty())
				{
					String[] item = itemData.split(",");
					TVT_EVENT_FIGHTER_BUFFS.put(Integer.parseInt(item[0]), Integer.parseInt(item[1]));
				}
			}
		}
		
		TVT_EVENT_MAGE_BUFFS = new HashMap<>();
		String[] TvtMageBuffs = events.getProperty("TvTEventMageBuffs", (String[]) null, ";");
		if (TvtMageBuffs != null)
		{
			for (String itemData : TvtMageBuffs)
			{
				if (!itemData.isEmpty())
				{
					String[] item = itemData.split(",");
					TVT_EVENT_MAGE_BUFFS.put(Integer.parseInt(item[0]), Integer.parseInt(item[1]));
				}
			}
		}
		
		TVT_REWARD_PLAYER = events.getProperty("TvTRewardOnlyKillers", false);
		TVT_EVENT_ON_KILL = events.getProperty("TvTEventOnKill", "pmteam");
		DISABLE_ID_CLASSES_STRING_TVT = events.getProperty("TvTDisabledForClasses");
		DISABLE_ID_CLASSES_TVT = new ArrayList<>();
		
		for (String classId : DISABLE_ID_CLASSES_STRING_TVT.split(","))
			DISABLE_ID_CLASSES_TVT.add(Integer.parseInt(classId));
		
		ALLOW_TVT_DLG = events.getProperty("AllowDlgTvTInvite", false);
		TVT_EVENT_MAX_PARTICIPANTS_PER_IP = events.getProperty("TvTEventMaxParticipantsPerIP", 0);
	}
	
	/**
	 * Loads geoengine settings.
	 */
	private static final void loadGeoengine()
	{
		final ExProperties geoengine = initProperties(GEOENGINE_FILE);
		
		GEODATA_PATH = getString(geoengine, "GeoDataPath", "./data/geodata/");
		GEODATA_TYPE = Enum.valueOf(GeoType.class, geoengine.getProperty("GeoDataType", "L2OFF"));
		
		MAX_GEOPATH_FAIL_COUNT = Math.max(15, geoengine.getProperty("MaxGeopathFailCount", 30));
		
		PART_OF_CHARACTER_HEIGHT = geoengine.getProperty("PartOfCharacterHeight", 75);
		MAX_OBSTACLE_HEIGHT = geoengine.getProperty("MaxObstacleHeight", 32);
		NPC_Z_INDEX = geoengine.getProperty("NpcZindex", 0);
		
		MOVE_WEIGHT = geoengine.getProperty("MoveWeight", 10);
		MOVE_WEIGHT_DIAG = geoengine.getProperty("MoveWeightDiag", 14);
		OBSTACLE_WEIGHT = geoengine.getProperty("ObstacleWeight", 30);
		OBSTACLE_WEIGHT_DIAG = (int) (OBSTACLE_WEIGHT * Math.sqrt(2));
		ENABLE_BOUNDARY_CELL_PENALTY = geoengine.getProperty("EnableBoundaryCellPenalty", true);
		BOUNDARY_CELL_PENALTY = geoengine.getProperty("BoundaryCellPenalty", 25);
		BOUNDARY_BUFFER = geoengine.getProperty("BoundaryBuffer", 20);
		HEURISTIC_WEIGHT = geoengine.getProperty("HeuristicWeight", 12);
		MAX_ITERATIONS = geoengine.getProperty("MaxIterations", 10000);
		
		SISTEMA_PATHFINDING = geoengine.getProperty("SistemaPathfinding", true);
		USE_OPTIMIZED_MOVEMENT = geoengine.getProperty("UseOptimizedMovement", true);
		ENABLE_CLIENT_SIDE_PREDICTION = geoengine.getProperty("EnableClientSidePrediction", true);
		MOVEMENT_VALIDATION_INTERVAL = geoengine.getProperty("MovementValidationInterval", 500);
		MOVEMENT_RECONCILIATION_THRESHOLD = geoengine.getProperty("MovementReconciliationThreshold", 32.0);
		PATHFINDING_MAX_NODES = geoengine.getProperty("PathfindingMaxNodes", 1500);
		PATHFINDING_MAX_ITERATIONS = geoengine.getProperty("PathfindingMaxIterations", 2000);
		ENABLE_PATH_SMOOTHING = geoengine.getProperty("EnablePathSmoothing", true);
		GEOENGINE_CACHE_SIZE = geoengine.getProperty("GeoEngineCacheSize", 50000);
		
		ENABLE_PATHFINDING_CACHE = geoengine.getProperty("EnablePathfindingCache", true);
		PATHFINDING_CACHE_SIZE = geoengine.getProperty("PathfindingCacheSize", 5000);
		PATHFINDING_CACHE_EXPIRATION = geoengine.getProperty("PathfindingCacheExpiration", 30) * 1000;
		PATHFINDING_THREADS = geoengine.getProperty("PathfindingThreads", -1);
		USE_PATHFINDING_POOL = geoengine.getProperty("UsePathfindingPool", true);
		USE_L2BR_PATHFINDING = geoengine.getProperty("UseL2BRPathfinding", true);
		
		if (PATHFINDING_THREADS == -1)
		{
			PATHFINDING_THREADS = Math.max(2, Runtime.getRuntime().availableProcessors() / 2);
		}
		
		ENABLE_SMOOTH_OBSTACLE_AVOIDANCE = geoengine.getProperty("EnableSmoothObstacleAvoidance", true);
		OBSTACLE_SMOOTHING_DISTANCE = geoengine.getProperty("ObstacleSmoothingDistance", 50);
		OBSTACLE_DETECTION_DISTANCE = geoengine.getProperty("ObstacleDetectionDistance", 32);
		PATHFINDING_SMOOTHING_LEVEL = geoengine.getProperty("PathfindingSmoothingLevel", 7);
		ENABLE_REAL_TIME_OBSTACLE_AVOIDANCE = geoengine.getProperty("EnableRealTimeObstacleAvoidance", true);
		
		ATTACK_USE_PATHFINDER = geoengine.getProperty("AttackUsePathfinder", false);
		
		GEO_HEIGHT_TOLERANCE = geoengine.getProperty("GeoHeightTolerance", 512);
		SHORT_DISTANCE_THRESHOLD = geoengine.getProperty("ShortDistanceThreshold", 1000);
		MAX_SHORT_DISTANCE_HEIGHT_DIFF = geoengine.getProperty("MaxShortDistanceHeightDiff", 200);
		MAX_CONSECUTIVE_BLOCKS = geoengine.getProperty("MaxConsecutiveBlocks", 3);
		
		ENABLE_NPC_MOVEMENT_OPTIMIZATION = geoengine.getProperty("EnableNpcMovementOptimization", true);
		NPC_MOVEMENT_PLAYER_RANGE = geoengine.getProperty("NpcMovementPlayerRange", 2000);
		
		
		ENABLE_CREATURE_COLLISION = geoengine.getProperty("EnableCreatureCollision", true);
		CREATURE_COLLISION_CHECK_RADIUS = geoengine.getProperty("CreatureCollisionCheckRadius", 150);
		CREATURE_COLLISION_AVOIDANCE_RADIUS = geoengine.getProperty("CreatureCollisionAvoidanceRadius", 80);
		MAX_ALTERNATIVE_POSITIONS = geoengine.getProperty("MaxAlternativePositions", 8);
		CREATURE_COLLISION_PUSH_FORCE = geoengine.getProperty("CreatureCollisionPushForce", 0.3);
		
		/*if (USE_MINIMAL_GEO_ONLY)
		{
			SISTEMA_PATHFINDING = false;
			USE_OPTIMIZED_MOVEMENT = false;
			LOGGER.info("UseMinimalGeoOnly ativado: Pathfinding completamente desabilitado para economia máxima de recursos.");
		}
		*/
	}
	
	/**
	 * Loads hex ID settings.
	 */
	private static final void loadHexID()
	{
		final String serverId = System.getProperty("ext.mods.Config.ServerID");
		final String id = System.getProperty("ext.mods.Config.HexID");
		
		if (serverId == null || id == null)
		{
			final ExProperties hexid = initProperties(HEXID_FILE);
			SERVER_ID = Integer.parseInt(hexid.getProperty("ServerID"));
			HEX_ID = new BigInteger(hexid.getProperty("HexID"), 16).toByteArray();
		}
		else
		{
			SERVER_ID = Integer.parseInt(serverId);
			HEX_ID = new BigInteger(id, 16).toByteArray();
		}
	}
	
	/**
	 * Loads language settings.
	 */
	private static final void loadLanguage()
	{
		final ExProperties language = initProperties(LANGUAGE_FILE);
		
		DEFAULT_LOCALE = Locale.forLanguageTag(language.getProperty("defaultLocale", "en-US"));
		LOCALES = Set.copyOf(Stream.of(language.getProperty("locales", "en-US").split(",")).map(Locale::forLanguageTag).toList());
		CHARSET = Charset.forName(language.getProperty("charset", "utf-8"));
	}
	
	/**
	 * Saves hex ID file.
	 * @param serverId : The ID of server.
	 * @param hexId : The hex ID of server.
	 */
	public static final void saveHexid(int serverId, String hexId)
	{
		saveHexid(serverId, hexId, HEXID_FILE);
	}
	
	/**
	 * Saves hexID file.
	 * @param serverId : The ID of server.
	 * @param hexId : The hexID of server.
	 * @param filename : The file name.
	 */
	public static final void saveHexid(int serverId, String hexId, String filename)
	{
		try
		{
			final File file = new File(filename);
			file.createNewFile();
			
			final Properties hexSetting = new Properties();
			hexSetting.setProperty("ServerID", String.valueOf(serverId));
			hexSetting.setProperty("HexID", hexId);
			
			try (OutputStream out = new FileOutputStream(file))
			{
				hexSetting.store(out, "the hexID to auth into login");
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to save hex ID to '{}' file.", e, filename);
		}
	}
	
	/**
	 * Loads NPC settings.<br>
	 * Such as champion monsters, NPC buffer, class master, wyvern, raid bosses and grand bosses, AI.
	 */
	private static final void loadNpcs()
	{
		final ExProperties npcs = initProperties(NPCS_FILE);
		
		SPAWN_MULTIPLIER = npcs.getProperty("SpawnMultiplier", 1.);
		SPAWN_EVENTS = npcs.getProperty("SpawnEvents", new String[]
		{
			"extra_mob",
			"18age",
			"start_weapon",
		});
		
		CHAMPION_FREQUENCY = npcs.getProperty("ChampionFrequency", 0);
		CHAMP_MIN_LVL = npcs.getProperty("ChampionMinLevel", 20);
		CHAMP_MAX_LVL = npcs.getProperty("ChampionMaxLevel", 70);
		CHAMPION_HP = npcs.getProperty("ChampionHp", 8);
		CHAMPION_HP_REGEN = npcs.getProperty("ChampionHpRegen", 1.);
		CHAMPION_RATE_XP = npcs.getProperty("ChampionRateXp", 1.);
		CHAMPION_RATE_SP = npcs.getProperty("ChampionRateSp", 1.);
		PREMIUM_CHAMPION_RATE_XP = npcs.getProperty("PremiumChampionRateXp", 1.);
		PREMIUM_CHAMPION_RATE_SP = npcs.getProperty("PremiumChampionRateSp", 1.);
		CHAMPION_REWARDS = npcs.getProperty("ChampionRewards", 1);
		PREMIUM_CHAMPION_REWARDS = npcs.getProperty("PremiumChampionRewards", 1);
		CHAMPION_ADENAS_REWARDS = npcs.getProperty("ChampionAdenasRewards", 1);
		CHAMPION_SEALSTONE_REWARDS = npcs.getProperty("ChampionSealStoneRewards", 1);
		PREMIUM_CHAMPION_ADENAS_REWARDS = npcs.getProperty("PremiumChampionAdenasRewards", 1);
		PREMIUM_CHAMPION_SEALSTONE_REWARDS = npcs.getProperty("PremiumChampionSealStoneRewards", 1);
		CHAMPION_SPOIL_REWARDS = npcs.getProperty("ChampionSpoilRewards", 1);
		PREMIUM_CHAMPION_SPOIL_REWARDS = npcs.getProperty("PremiumChampionSpoilRewards", 1);
		CHAMPION_ATK = npcs.getProperty("ChampionAtk", 1.);
		CHAMPION_MATK = npcs.getProperty("ChampionMAtk", 1.);
		CHAMPION_SPD_ATK = npcs.getProperty("ChampionSpdAtk", 1.);
		CHAMPION_SPD_MATK = npcs.getProperty("ChampionSpdMAtk", 1.);
		CHAMPION_REWARD = npcs.getProperty("ChampionRewardItem", 0);
		CHAMPION_REWARD_ID = npcs.getProperty("ChampionRewardItemID", 6393);
		CHAMPION_REWARD_QTY = npcs.getProperty("ChampionRewardItemQty", 1);
		CHAMPION_AURA = npcs.getProperty("ChampionAura", 0);
		
		ALLOW_ENTIRE_TREE = npcs.getProperty("AllowEntireTree", false);
		CLASS_MASTER_SETTINGS = new ClassMasterSettings(npcs.getProperty("ConfigClassMaster"));
		ALTERNATE_CLASS_MASTER = npcs.getProperty("AlternateClassMaster", true);
		
		NOBLE_ITEM_ID = npcs.getProperty("NobleItemId", 4037);
		NOBLE_ITEM_COUNT = npcs.getProperty("NobleItemCount", 50);
		
		WEDDING_PRICE = npcs.getProperty("WeddingPrice", 1000000);
		WEDDING_SAMESEX = npcs.getProperty("WeddingAllowSameSex", false);
		WEDDING_FORMALWEAR = npcs.getProperty("WeddingFormalWear", true);
		
		BUFFER_MAX_SCHEMES = npcs.getProperty("BufferMaxSchemesPerChar", 4);
		BUFFER_STATIC_BUFF_COST = npcs.getProperty("BufferStaticCostPerBuff", -1);
		
		FREE_TELEPORT = npcs.getProperty("FreeTeleport", false);
		LVL_FREE_TELEPORT = npcs.getProperty("LvlFreeTeleport", 40);
		ANNOUNCE_MAMMON_SPAWN = npcs.getProperty("AnnounceMammonSpawn", false);
		MOB_AGGRO_IN_PEACEZONE = npcs.getProperty("MobAggroInPeaceZone", true);
		SHOW_NPC_LVL = npcs.getProperty("ShowNpcLevel", false);
		SHOW_NPC_TITLE_FORMAT = npcs.getProperty("ShowNpcTitleFormat", "Lvl %level% %aggro% %title%");
		SHOW_NPC_TITLE_COLOR = npcs.getProperty("ShowNpcTitleColor", "");
		SHOW_NPC_CREST = npcs.getProperty("ShowNpcCrest", false);
		SHOW_SUMMON_CREST = npcs.getProperty("ShowSummonCrest", false);
		
		WYVERN_REQUIRED_LEVEL = npcs.getProperty("RequiredStriderLevel", 55);
		WYVERN_REQUIRED_CRYSTALS = npcs.getProperty("RequiredCrystalsNumber", 10);
		
		NPC_STAT_MULTIPLIERS = npcs.getProperty("NpcStatMultipliers", false);
		MONSTER_HP_MULTIPLIER = npcs.getProperty("MonsterHP", 10.0);
		MONSTER_MP_MULTIPLIER = npcs.getProperty("MonsterMP", 10.0);
		MONSTER_PATK_MULTIPLIER = npcs.getProperty("MonsterPAtk", 10.0);
		MONSTER_MATK_MULTIPLIER = npcs.getProperty("MonsterMAtk", 10.0);
		MONSTER_PDEF_MULTIPLIER = npcs.getProperty("MonsterPDef", 10.0);
		MONSTER_MDEF_MULTIPLIER = npcs.getProperty("MonsterMDef", 10.0);
		
		RAIDBOSS_HP_MULTIPLIER = npcs.getProperty("RaidbossHP", 1.0);
		RAIDBOSS_MP_MULTIPLIER = npcs.getProperty("RaidbossMP", 1.0);
		RAIDBOSS_PATK_MULTIPLIER = npcs.getProperty("RaidbossPAtk", 1.0);
		RAIDBOSS_MATK_MULTIPLIER = npcs.getProperty("RaidbossMAtk", 1.0);
		RAIDBOSS_PDEF_MULTIPLIER = npcs.getProperty("RaidbossPDef", 1.0);
		RAIDBOSS_MDEF_MULTIPLIER = npcs.getProperty("RaidbossMDef", 1.0);
		
		GRANDBOSS_HP_MULTIPLIER = npcs.getProperty("GrandbossHP", 1.0);
		GRANDBOSS_MP_MULTIPLIER = npcs.getProperty("GrandbossMP", 1.0);
		GRANDBOSS_PATK_MULTIPLIER = npcs.getProperty("GrandbossPAtk", 1.0);
		GRANDBOSS_MATK_MULTIPLIER = npcs.getProperty("GrandbossMAtk", 1.0);
		GRANDBOSS_PDEF_MULTIPLIER = npcs.getProperty("GrandbossPDef", 1.0);
		GRANDBOSS_MDEF_MULTIPLIER = npcs.getProperty("GrandbossMDef", 1.0);
		
		RAID_DISABLE_CURSE = npcs.getProperty("DisableRaidCurse", false);
		
		WAIT_TIME_ANTHARAS = npcs.getProperty("AntharasWaitTime", 30) * 60000;
		NEED_ITEM_ANTHARAS = npcs.getProperty("AntharasNeedItem", true);
		
		WAIT_TIME_VALAKAS = npcs.getProperty("ValakasWaitTime", 20) * 60000;
		NEED_ITEM_VALAKAS = npcs.getProperty("ValakasNeedItem", true);
		
		WAIT_TIME_FRINTEZZA = npcs.getProperty("FrintezzaWaitTime", 10) * 60000;
		FRINTEZZA_MINIMUM_ALLOWED_PLAYERS = npcs.getProperty("FrintezzaMinimumAllowedPlayers", 1);
		FRINTEZZA_MAXIMUM_ALLOWED_PLAYERS = npcs.getProperty("FrintezzaMaximumAllowedPlayers", 99);
		FRINTEZZA_MINIMUM_PARTIES = npcs.getProperty("FrintezzaMinimumParties", 4);
		FRINTEZZA_MAXIMUM_PARTIES = npcs.getProperty("FrintezzaMaximumParties", 5);
		NEED_ITEM_FRINTEZZA = npcs.getProperty("FrintezzaNeedItem", true);
		
		NEED_ITEM_BAIUM = npcs.getProperty("BaiumNeedItem", true);
		
		NEED_ITEM_SHILEN = npcs.getProperty("ShilenNeedItem", false);
		
		GUARD_ATTACK_AGGRO_MOB = npcs.getProperty("GuardAttackAggroMob", false);
		RANDOM_WALK_RATE = npcs.getProperty("RandomWalkRate", 30);
		MAX_DRIFT_RANGE = npcs.getProperty("MaxDriftRange", 200);
		DEBUG_MELEE_ATTACK = npcs.getProperty("DebugMeleeAttack", false);
		
		ENABLE_GUARD_CHAT = npcs.getProperty("EnableGuardChat", true);
		GUARD_CHAT_RANGE = npcs.getProperty("GuardChatRange", 300);
		GUARD_CHATTY_CHANCE = npcs.getProperty("GuardChattyChance", 50);
		GUARD_SAY_NORMAL_CHANCE = npcs.getProperty("GuardSayNormalChance", 5);
		GUARD_SAY_NORMAL_PERIOD = npcs.getProperty("GuardSayNormalPeriod", 120) * 1000;
		GUARD_SAY_AGGRO_PERIOD = npcs.getProperty("GuardSayAggroPeriod", 10) * 1000;
		
		NPC_ANIMATION = npcs.getProperty("NpcAnimation", 40);
		MONSTER_ANIMATION = npcs.getProperty("MonsterAnimation", 20);
		
		DEFAULT_SEE_RANGE = npcs.getProperty("DefaultSeeRange", 450);
		SUMMON_DRIFT_RANGE = npcs.getProperty("SummonDriftRange", 70);
		
		RAID_BOSS_LIST = npcs.getProperty("RaidBossList", new int[]
		{
			0
		});
		EPIC_BOSS_LIST = npcs.getProperty("EpicBossList", new int[]
		{
			0
		});
	}
	
	/**
	 * Loads player settings.<br>
	 * Such as stats, inventory/warehouse, enchant, augmentation, karma, party, admin, petition, skill learn.
	 */
	private static final void loadPlayers()
	{
		final ExProperties players = initProperties(PLAYERS_FILE);
		
		EFFECT_CANCELING = players.getProperty("CancelLesserEffect", true);
		HP_REGEN_MULTIPLIER = players.getProperty("HpRegenMultiplier", 1.);
		MP_REGEN_MULTIPLIER = players.getProperty("MpRegenMultiplier", 1.);
		CP_REGEN_MULTIPLIER = players.getProperty("CpRegenMultiplier", 1.);
		PLAYER_SPAWN_PROTECTION = players.getProperty("PlayerSpawnProtection", 0);
		PLAYER_FAKEDEATH_UP_PROTECTION = players.getProperty("PlayerFakeDeathUpProtection", 5);
		RESPAWN_RESTORE_HP = players.getProperty("RespawnRestoreHP", 0.7);
		MAX_PVTSTOREBUY_SLOTS_DWARF = players.getProperty("MaxPvtStoreBuySlotsDwarf", 5);
		MAX_PVTSTOREBUY_SLOTS_OTHER = players.getProperty("MaxPvtStoreBuySlotsOther", 4);
		MAX_PVTSTORESELL_SLOTS_DWARF = players.getProperty("MaxPvtStoreSellSlotsDwarf", 4);
		MAX_PVTSTORESELL_SLOTS_OTHER = players.getProperty("MaxPvtStoreSellSlotsOther", 3);
		DEEPBLUE_DROP_RULES = players.getProperty("UseDeepBlueDropRules", true);
		ALLOW_DELEVEL = players.getProperty("AllowDelevel", true);
		DEATH_PENALTY_CHANCE = players.getProperty("DeathPenaltyChance", 20);
		DEBUG_MELEE_ATTACK = players.getProperty("DebugMeleeAttack", DEBUG_MELEE_ATTACK);
		
		INVENTORY_MAXIMUM_NO_DWARF = players.getProperty("MaximumSlotsForNoDwarf", 80);
		INVENTORY_MAXIMUM_DWARF = players.getProperty("MaximumSlotsForDwarf", 100);
		INVENTORY_MAXIMUM_PET = players.getProperty("MaximumSlotsForPet", 12);
		MAX_ITEM_IN_PACKET = Math.max(INVENTORY_MAXIMUM_NO_DWARF, INVENTORY_MAXIMUM_DWARF);
		WEIGHT_LIMIT = players.getProperty("WeightLimit", 1.);
		WAREHOUSE_SLOTS_NO_DWARF = players.getProperty("MaximumWarehouseSlotsForNoDwarf", 100);
		WAREHOUSE_SLOTS_DWARF = players.getProperty("MaximumWarehouseSlotsForDwarf", 120);
		WAREHOUSE_SLOTS_CLAN = players.getProperty("MaximumWarehouseSlotsForClan", 150);
		FREIGHT_SLOTS = players.getProperty("MaximumFreightSlots", 20);
		REGION_BASED_FREIGHT = players.getProperty("RegionBasedFreight", true);
		FREIGHT_PRICE = players.getProperty("FreightPrice", 1000);

		SUBCLASS_REQUIRE_MIMIR = players.getProperty("SubclassRequireMimir", true);
		SUBCLASS_REQUIRE_FATE = players.getProperty("SubclassRequireFate", true);
		AUGMENTATION_NG_SKILL_CHANCE = players.getProperty("AugmentationNGSkillChance", 15);
		AUGMENTATION_NG_GLOW_CHANCE = players.getProperty("AugmentationNGGlowChance", 0);
		AUGMENTATION_MID_SKILL_CHANCE = players.getProperty("AugmentationMidSkillChance", 30);
		AUGMENTATION_MID_GLOW_CHANCE = players.getProperty("AugmentationMidGlowChance", 40);
		AUGMENTATION_HIGH_SKILL_CHANCE = players.getProperty("AugmentationHighSkillChance", 45);
		AUGMENTATION_HIGH_GLOW_CHANCE = players.getProperty("AugmentationHighGlowChance", 70);
		AUGMENTATION_TOP_SKILL_CHANCE = players.getProperty("AugmentationTopSkillChance", 60);
		AUGMENTATION_TOP_GLOW_CHANCE = players.getProperty("AugmentationTopGlowChance", 100);
		AUGMENTATION_BASESTAT_CHANCE = players.getProperty("AugmentationBaseStatChance", 1);
		
		KARMA_PLAYER_CAN_SHOP = players.getProperty("KarmaPlayerCanShop", false);
		KARMA_PLAYER_CAN_USE_GK = players.getProperty("KarmaPlayerCanUseGK", false);
		KARMA_PLAYER_CAN_TELEPORT = players.getProperty("KarmaPlayerCanTeleport", true);
		KARMA_PLAYER_CAN_TRADE = players.getProperty("KarmaPlayerCanTrade", true);
		KARMA_PLAYER_CAN_USE_WH = players.getProperty("KarmaPlayerCanUseWareHouse", true);
		KARMA_DROP_GM = players.getProperty("CanGMDropEquipment", false);
		KARMA_AWARD_PK_KILL = players.getProperty("AwardPKKillPVPPoint", true);
		KARMA_PK_LIMIT = players.getProperty("MinimumPKRequiredToDrop", 5);
		KARMA_NONDROPPABLE_PET_ITEMS = players.getProperty("ListOfPetItems", new int[]
		{
			2375,
			3500,
			3501,
			3502,
			4422,
			4423,
			4424,
			4425,
			6648,
			6649,
			6650
		});
		KARMA_NONDROPPABLE_ITEMS = players.getProperty("ListOfNonDroppableItemsForPK", new int[]
		{
			1147,
			425,
			1146,
			461,
			10,
			2368,
			7,
			6,
			2370,
			2369
		});
		
		PVP_NORMAL_TIME = players.getProperty("PvPVsNormalTime", 40000);
		PVP_PVP_TIME = players.getProperty("PvPVsPvPTime", 20000);
		
		PARTY_XP_CUTOFF_METHOD = players.getProperty("PartyXpCutoffMethod", "level");
		PARTY_XP_CUTOFF_PERCENT = players.getProperty("PartyXpCutoffPercent", 3.);
		PARTY_XP_CUTOFF_LEVEL = players.getProperty("PartyXpCutoffLevel", 20);
		PARTY_RANGE = players.getProperty("PartyRange", 1500);
		
		DEFAULT_ACCESS_LEVEL = players.getProperty("DefaultAccessLevel", 0);
		GM_HERO_AURA = players.getProperty("GMHeroAura", false);
		GM_STARTUP_INVULNERABLE = players.getProperty("GMStartupInvulnerable", false);
		GM_STARTUP_INVISIBLE = players.getProperty("GMStartupInvisible", false);
		GM_STARTUP_BLOCK_ALL = players.getProperty("GMStartupBlockAll", false);
		GM_STARTUP_AUTO_LIST = players.getProperty("GMStartupAutoList", true);
		
		PETITIONING_ALLOWED = players.getProperty("PetitioningAllowed", true);
		MAX_PETITIONS_PER_PLAYER = players.getProperty("MaxPetitionsPerPlayer", 5);
		MAX_PETITIONS_PENDING = players.getProperty("MaxPetitionsPending", 25);
		
		IS_CRAFTING_ENABLED = players.getProperty("CraftingEnabled", true);
		DWARF_RECIPE_LIMIT = players.getProperty("DwarfRecipeLimit", 50);
		COMMON_RECIPE_LIMIT = players.getProperty("CommonRecipeLimit", 50);
		
		AUTO_LEARN_SKILLS = players.getProperty("AutoLearnSkills", false);
		LVL_AUTO_LEARN_SKILLS = players.getProperty("LvlAutoLearnSkills", 40);
		MAGIC_FAILURES = players.getProperty("MagicFailures", true);
		PERFECT_SHIELD_BLOCK_RATE = players.getProperty("PerfectShieldBlockRate", 5);
		LIFE_CRYSTAL_NEEDED = players.getProperty("LifeCrystalNeeded", true);
		SP_BOOK_NEEDED = players.getProperty("SpBookNeeded", true);
		ES_SP_BOOK_NEEDED = players.getProperty("EnchantSkillSpBookNeeded", true);
		DIVINE_SP_BOOK_NEEDED = players.getProperty("DivineInspirationSpBookNeeded", true);
		SUBCLASS_WITHOUT_QUESTS = players.getProperty("SubClassWithoutQuests", false);
		
		MAX_BUFFS_AMOUNT = players.getProperty("MaxBuffsAmount", 20);
		STORE_SKILL_COOLTIME = players.getProperty("StoreSkillCooltime", true);
		EXPERTISE_PENALTY = players.getProperty("ExpertisePenalty", true);
	}
	
	/**
	 * Loads siege settings.
	 */
	private static final void loadSieges()
	{
		final ExProperties sieges = initProperties(Config.SIEGE_FILE);
		
		SIEGE_LENGTH = sieges.getProperty("SiegeLength", 120);
		MINIMUM_CLAN_LEVEL = sieges.getProperty("SiegeClanMinLevel", 4);
		MAX_ATTACKERS_NUMBER = sieges.getProperty("AttackerMaxClans", 10);
		MAX_DEFENDERS_NUMBER = sieges.getProperty("DefenderMaxClans", 10);
		
		CH_MINIMUM_CLAN_LEVEL = sieges.getProperty("ChSiegeClanMinLevel", 4);
		CH_MAX_ATTACKERS_NUMBER = sieges.getProperty("ChAttackerMaxClans", 10);
		
		SIEGE_INFO = sieges.getProperty("SiegeInfo", false);
	}
	
	private static final void loadProtection()
	{
		final ExProperties Protect = initProperties(Config.PROTECTION_FILE);
		
		ALLOW_GUARD_SYSTEM = Protect.getProperty("AllowGuardSystem", true);
		PROTECT_WINDOWS_COUNT = Protect.getProperty("AllowedWindowsCount", 1);

		GET_CLIENT_HWID = Protect.getProperty("UseClientHWID", 2);
		ENABLE_CONSOLE_LOG = Protect.getProperty("EnableConsoleLog", false);
		PROTECT_KICK_WITH_EMPTY_HWID = Protect.getProperty("KickWithEmptyHWID", false);
		PROTECT_KICK_WITH_LASTERROR_HWID = Protect.getProperty("KickWithLastErrorHWID", false);

		String key_client = "GOGX2_RB(]Slnjt15~EgyqTv%[$YR]!1E~ayK?$9[R%%m4{zoMF$D?f:zvS2q&>~";
		String key_server = "b*qR43<9J1pD>Q4Uns6FsKao~VbU0H]y`A0ytTveiWn)SuSYsM?m*eblL!pwza!t";
		byte[] keyS = key_server.getBytes();
		byte[] tmpS = new byte[32];

		byte[] keyC = key_client.getBytes();
		byte[] tmpC = new byte[32];

		System.arraycopy(keyC, 0, tmpC, 0, 32);
		GUARD_CLIENT_CRYPT_KEY = FirstKey.expandKey(tmpC, 32);
		System.arraycopy(keyC, 32, tmpC, 0, 32);
		GUARD_CLIENT_CRYPT = FirstKey.expandKey(tmpC, 32);

		System.arraycopy(keyS, 0, tmpS, 0, 32);
		GUARD_SERVER_CRYPT_KEY = FirstKey.expandKey(tmpS, 32);
		System.arraycopy(keyS, 32, tmpS, 0, 32);
		GUARD_SERVER_CRYPT = FirstKey.expandKey(tmpS, 32);

	}
	
	/**
	 * Loads gameserver settings.<br>
	 * IP addresses, database, feature enabled/disabled, misc.
	 */
	private static final void loadServer()
	{
		final ExProperties server = initProperties(SERVER_FILE);
		

		HOSTNAME = server.getProperty("Hostname", "*");
		GAMESERVER_HOSTNAME = server.getProperty("GameserverHostname");
		GAMESERVER_PORT = server.getProperty("GameserverPort", 7777);
		GAMESERVER_LOGIN_HOSTNAME = server.getProperty("LoginHost", "127.0.0.1");
		GAMESERVER_LOGIN_PORT = server.getProperty("LoginPort", 9014);
		REQUEST_ID = server.getProperty("RequestServerID", 0);
		ACCEPT_ALTERNATE_ID = server.getProperty("AcceptAlternateID", true);
		USE_BLOWFISH_CIPHER = server.getProperty("UseBlowfishCipher", true);
		
		loadDatabaseProperties(server);
		
		CNAME_TEMPLATE = server.getProperty("CnameTemplate", ".*");
		DONATE_CNAME_TEMPLATE = server.getProperty("DonateCnameTemplate", ".*");
		TITLE_TEMPLATE = server.getProperty("TitleTemplate", ".*");
		PET_NAME_TEMPLATE = server.getProperty("PetNameTemplate", ".*");
		CLAN_ALLY_NAME_TEMPLATE = server.getProperty("ClanAllyNameTemplate", ".*");
		
		SERVER_LIST_BRACKET = server.getProperty("ServerListBrackets", false);
		SERVER_LIST_CLOCK = server.getProperty("ServerListClock", false);
		SERVER_GMONLY = server.getProperty("ServerGMOnly", false);
		SERVER_LIST_AGE = server.getProperty("ServerListAgeLimit", 0);
		SERVER_LIST_TESTSERVER = server.getProperty("TestServer", false);
		SERVER_LIST_PVPSERVER = server.getProperty("PvpServer", true);
		
		DELETE_DAYS = server.getProperty("DeleteCharAfterDays", 7);
		MAXIMUM_ONLINE_USERS = server.getProperty("MaximumOnlineUsers", 100);
		
		AUTO_LOOT = server.getProperty("AutoLoot", false);
		AUTO_LOOT_HERBS = server.getProperty("AutoLootHerbs", false);
		AUTO_LOOT_RAID = server.getProperty("AutoLootRaid", false);
		
		ALLOW_DISCARDITEM = server.getProperty("AllowDiscardItem", true);
		MULTIPLE_ITEM_DROP = server.getProperty("MultipleItemDrop", true);
		HERB_AUTO_DESTROY_TIME = server.getProperty("AutoDestroyHerbTime", 15) * 1000;
		ITEM_AUTO_DESTROY_TIME = server.getProperty("AutoDestroyItemTime", 600) * 1000;
		EQUIPABLE_ITEM_AUTO_DESTROY_TIME = server.getProperty("AutoDestroyEquipableItemTime", 0) * 1000;
		SPECIAL_ITEM_DESTROY_TIME = new HashMap<>();
		String[] data = server.getProperty("AutoDestroySpecialItemTime", (String[]) null, ",");
		if (data != null)
		{
			for (String itemData : data)
			{
				String[] item = itemData.split("-");
				SPECIAL_ITEM_DESTROY_TIME.put(Integer.parseInt(item[0]), Integer.parseInt(item[1]) * 1000);
			}
		}
		PLAYER_DROPPED_ITEM_MULTIPLIER = server.getProperty("PlayerDroppedItemMultiplier", 1);
		
		final ExProperties items = initProperties(ITEMS_FILE);
		ITEMS_GC_CLEANUP_ENABLED = items.getProperty("ItemsGcCleanupEnabled", true);
		final int itemsGcSeconds = items.getProperty("ItemsGcCleanupTime", 120);
		ITEMS_GC_CLEANUP_TIME_MS = ITEMS_GC_CLEANUP_ENABLED ? itemsGcSeconds * 1000 : 0;
		
		ALLOW_FREIGHT = server.getProperty("AllowFreight", true);
		ALLOW_WAREHOUSE = server.getProperty("AllowWarehouse", true);
		ALLOW_WEAR = server.getProperty("AllowWear", true);
		WEAR_DELAY = server.getProperty("WearDelay", 5);
		WEAR_PRICE = server.getProperty("WearPrice", 10);
		ALLOW_LOTTERY = server.getProperty("AllowLottery", true);
		ALLOW_WATER = server.getProperty("AllowWater", true);
		ALLOW_MANOR = server.getProperty("AllowManor", true);
		ALLOW_BOAT = server.getProperty("AllowBoat", true);
		ALLOW_CURSED_WEAPONS = server.getProperty("AllowCursedWeapons", true);
		ALLOW_SHADOW_WEAPONS = server.getProperty("AllowShadowWeapon", true);
		
		ENABLE_FALLING_DAMAGE = server.getProperty("EnableFallingDamage", true);
		
		NO_SPAWNS = server.getProperty("NoSpawns", false);
		DEVELOPER = server.getProperty("Developer", false);
		PACKET_HANDLER_DEBUG = server.getProperty("PacketHandlerDebug", false);
		
		CLIENT_PACKETS = Arrays.asList(server.getProperty("ClientPacket", "ValidatePosition").split(","));
		SERVER_PACKETS = Arrays.asList(server.getProperty("ServerPacket", "AbnormalStatusUpdate,AcquireSkillList,Attack,AutoAttackStart,AutoAttackStop,DeleteObject,ExAutoSoulShot,ExStorageMaxCount,MoveToLocation,NpcInfo,NpcSay,SkillCoolTime,SocialAction,StatusUpdate,UserInfo").split(","));
		
		LOG_CHAT = server.getProperty("LogChat", false);
		LOG_ITEMS = server.getProperty("LogItems", false);
		DROP_ITEMS = server.getProperty("DropItems", false);
		GMAUDIT = server.getProperty("GMAudit", false);
		
		ENABLE_CUSTOM_BBS = server.getProperty("EnableCustomBbs", false);
		ENABLE_COMMUNITY_BOARD = server.getProperty("EnableCommunityBoard", false);
		BBS_DEFAULT = server.getProperty("BBSDefault", "_bbshome");
		
		ROLL_DICE_TIME = server.getProperty("RollDiceTime", 4200);
		HERO_VOICE_TIME = server.getProperty("HeroVoiceTime", 10000);
		SUBCLASS_TIME = server.getProperty("SubclassTime", 2000);
		DROP_ITEM_TIME = server.getProperty("DropItemTime", 1000);
		SERVER_BYPASS_TIME = server.getProperty("ServerBypassTime", 100);
		MULTISELL_TIME = server.getProperty("MultisellTime", 100);
		MANUFACTURE_TIME = server.getProperty("ManufactureTime", 300);
		MANOR_TIME = server.getProperty("ManorTime", 3000);
		SENDMAIL_TIME = server.getProperty("SendMailTime", 10000);
		CHARACTER_SELECT_TIME = server.getProperty("CharacterSelectTime", 3000);
		GLOBAL_CHAT_TIME = server.getProperty("GlobalChatTime", 0);
		TRADE_CHAT_TIME = server.getProperty("TradeChatTime", 0);
		SOCIAL_TIME = server.getProperty("SocialTime", 2000);
		ITEM_TIME = server.getProperty("ItemTime", 100);
		ACTION_TIME = server.getProperty("ActionTime", 2000);
		
		SCHEDULED_THREAD_POOL_COUNT = server.getProperty("ScheduledThreadPoolCount", -1);
		if (SCHEDULED_THREAD_POOL_COUNT == -1)
			SCHEDULED_THREAD_POOL_COUNT = Runtime.getRuntime().availableProcessors() * 4;
		
		INSTANT_THREAD_POOL_COUNT = server.getProperty("InstantThreadPoolCount", -1);
		if (INSTANT_THREAD_POOL_COUNT == -1)
			INSTANT_THREAD_POOL_COUNT = Runtime.getRuntime().availableProcessors() * 2;
		
		L2WALKER_PROTECTION = server.getProperty("L2WalkerProtection", false);
		ZONE_TOWN = server.getProperty("ZoneTown", 0);
		SERVER_NEWS = server.getProperty("ShowServerNews", false);
	}
	
	private static final void loadRates()
	{
		final ExProperties rates = initProperties(RATES_FILE);
		RATE_XP = rates.getProperty("RateXp", 1.);
		RATE_SP = rates.getProperty("RateSp", 1.);
		RATE_PARTY_XP = rates.getProperty("RatePartyXp", 1.);
		RATE_PARTY_SP = rates.getProperty("RatePartySp", 1.);
		RATE_DROP_CURRENCY = rates.getProperty("RateDropCurrency", 1.);
		RATE_DROP_SEAL_STONE = rates.getProperty("RateDropSealStone", 1.);
		RATE_DROP_ITEMS = rates.getProperty("RateDropItems", 1.);
		RATE_DROP_ITEMS_BY_RAID = rates.getProperty("RateRaidDropItems", 1.);
		RATE_DROP_ITEMS_BY_GRAND = rates.getProperty("RateGrandDropItems", 1.);
		RATE_DROP_SPOIL = rates.getProperty("RateDropSpoil", 1.);
		
		PREMIUM_RATE_XP = rates.getProperty("PremiumRateXp", 2.);
		PREMIUM_RATE_SP = rates.getProperty("PremiumRateSp", 2.);
		PREMIUM_RATE_DROP_CURRENCY = rates.getProperty("PremiumRateDropCurrency", 2.);
		PREMIUM_RATE_DROP_SEAL_STONE = rates.getProperty("PremiumRateDropSealStone", 2.);
		PREMIUM_RATE_DROP_SPOIL = rates.getProperty("PremiumRateDropSpoil", 2.);
		PREMIUM_RATE_DROP_ITEMS = rates.getProperty("PremiumRateDropItems", 2.);
		PREMIUM_RATE_DROP_ITEMS_BY_RAID = rates.getProperty("PremiumRateRaidDropItems", 2.);
		PREMIUM_RATE_DROP_ITEMS_BY_GRAND = rates.getProperty("PremiumRateGrandDropItems", 2.);
		
		PREMIUM_RATE_QUEST_DROP = rates.getProperty("PremiumRateQuestDrop", 2.);
		PREMIUM_RATE_QUEST_REWARD = rates.getProperty("PremiumRateQuestReward", 2.);
		PREMIUM_RATE_QUEST_REWARD_XP = rates.getProperty("PremiumRateQuestRewardXP", 2.);
		PREMIUM_RATE_QUEST_REWARD_SP = rates.getProperty("PremiumRateQuestRewardSP", 2.);
		PREMIUM_RATE_QUEST_REWARD_ADENA = rates.getProperty("PremiumRateQuestRewardAdena", 2.);
		
		DYNAMIC_XP = rates.getProperty("DynamicXp", false);
		if (DYNAMIC_XP)
		{
			DYNAMIC_XP_RATES = new HashMap<>();
			String[] propertySplit = rates.getProperty("DynamicXpRates", "").split(";");
			
			for (String rate : propertySplit)
			{
				String[] rateSplit = rate.split(":");
				if (rateSplit.length != 2)
					LOGGER.warn("[DynamicXpRates]: invalid config property -> DynamicXpRates \"" + rate + "\"");
				else
				{
					try
					{
						DYNAMIC_XP_RATES.put(Integer.parseInt(rateSplit[0]), Double.parseDouble(rateSplit[1]));
					}
					catch (NumberFormatException nfe)
					{
						nfe.printStackTrace();
						
						if (!rate.equals(""))
							LOGGER.warn("[DynamicXpRates]: invalid config property -> DynamicXpRates \"" + rateSplit[0] + "\"" + rateSplit[1]);
					}
				}
			}
		}
		
		RATE_DROP_HERBS = rates.getProperty("RateDropHerbs", 1.);
		RATE_DROP_MANOR = rates.getProperty("RateDropManor", 1);
		RATE_QUEST_DROP = rates.getProperty("RateQuestDrop", 1.);
		RATE_QUEST_REWARD = rates.getProperty("RateQuestReward", 1.);
		RATE_QUEST_REWARD_XP = rates.getProperty("RateQuestRewardXP", 1.);
		RATE_QUEST_REWARD_SP = rates.getProperty("RateQuestRewardSP", 1.);
		RATE_QUEST_REWARD_ADENA = rates.getProperty("RateQuestRewardAdena", 1.);
		RATE_KARMA_EXP_LOST = rates.getProperty("RateKarmaExpLost", 1.);
		RATE_SIEGE_GUARDS_PRICE = rates.getProperty("RateSiegeGuardsPrice", 1.);
		PLAYER_DROP_LIMIT = rates.getProperty("PlayerDropLimit", 3);
		PLAYER_RATE_DROP = rates.getProperty("PlayerRateDrop", 5);
		PLAYER_RATE_DROP_ITEM = rates.getProperty("PlayerRateDropItem", 70);
		PLAYER_RATE_DROP_EQUIP = rates.getProperty("PlayerRateDropEquip", 25);
		PLAYER_RATE_DROP_EQUIP_WEAPON = rates.getProperty("PlayerRateDropEquipWeapon", 5);
		PET_XP_RATE = rates.getProperty("PetXpRate", 1.);
		PET_FOOD_RATE = rates.getProperty("PetFoodRate", 1);
		SINEATER_XP_RATE = rates.getProperty("SinEaterXpRate", 1.);
		KARMA_DROP_LIMIT = rates.getProperty("KarmaDropLimit", 10);
		KARMA_RATE_DROP = rates.getProperty("KarmaRateDrop", 70);
		KARMA_RATE_DROP_ITEM = rates.getProperty("KarmaRateDropItem", 50);
		KARMA_RATE_DROP_EQUIP = rates.getProperty("KarmaRateDropEquip", 40);
		KARMA_RATE_DROP_EQUIP_WEAPON = rates.getProperty("KarmaRateDropEquipWeapon", 10);
		
		GRANDBOSS_RATE_XP = rates.getProperty("GrandBossRateXp", 1.);
		GRANDBOSS_RATE_SP = rates.getProperty("GrandBossRateSp", 1.);
		
		RAIDBOSS_RATE_XP = rates.getProperty("RaidBossRateXp", 1.);
		RAIDBOSS_RATE_SP = rates.getProperty("RaidBossRateSp", 1.);
	}
	
	private static final void loadRusAcis()
	{
		final ExProperties rusacis = initProperties(BR_FILE);
		

		INFINITY_SS = rusacis.getProperty("InfinitySS", false);
		INFINITY_ARROWS = rusacis.getProperty("InfinityArrows", false);
		INFINITY_SS_PREMIUM_ONLY = rusacis.getProperty("InfinitySSPremiumOnly", false);
		INFINITY_ARROWS_PREMIUM_ONLY = rusacis.getProperty("InfinityArrowsPremiumOnly", false);
		
		OLY_USE_CUSTOM_PERIOD_SETTINGS = rusacis.getProperty("OlyUseCustomPeriodSettings", false);
		OLY_CLASSED = rusacis.getProperty("OlyClassedParticipants", OLY_CLASSED);
		OLY_NONCLASSED = rusacis.getProperty("OlyNonClassedParticipants", OLY_NONCLASSED);
		OLY_BLOCK_SAME_HWID = rusacis.getProperty("OlyBlockSameHwid", true);
		OLY_PERIOD = OlympiadPeriod.valueOf(rusacis.getProperty("OlyPeriod", "MONTH"));
		OLY_PERIOD_MULTIPLIER = rusacis.getProperty("OlyPeriodMultiplier", 1);
		
		ENABLE_MODIFY_SKILL_DURATION = rusacis.getProperty("EnableModifySkillDuration", true);
		
		SKILL_DURATION_LIST = new HashMap<>();
		String[] propertySplit = rusacis.getProperty("SkillDurationList", "").split(";");
		
		for (String skill : propertySplit)
		{
			String[] skillSplit = skill.split(",");
			if (skillSplit.length != 2)
				LOGGER.warn("[SkillDurationList]: invalid config property -> SkillDurationList \"" + skill + "\"");
			else
			{
				try
				{
					SKILL_DURATION_LIST.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
				}
				catch (NumberFormatException nfe)
				{
					nfe.printStackTrace();
					
					if (!skill.equals(""))
						LOGGER.warn("[SkillDurationList]: invalid config property -> SkillList \"" + skillSplit[0] + "\"" + skillSplit[1]);
				}
			}
		}
		
		GLOBAL_CHAT = rusacis.getProperty("GlobalChat", "ON");
		TRADE_CHAT = rusacis.getProperty("TradeChat", "ON");
		CHAT_ALL_LEVEL = rusacis.getProperty("AllChatLevel", 1);
		CHAT_TELL_LEVEL = rusacis.getProperty("TellChatLevel", 1);
		CHAT_SHOUT_LEVEL = rusacis.getProperty("ShoutChatLevel", 1);
		CHAT_TRADE_LEVEL = rusacis.getProperty("TradeChatLevel", 1);
		
		ENABLE_MENU = rusacis.getProperty("EnableMenu", false);
		PROP_STOP_EXP = rusacis.getProperty("PropStopExp", true);
		PROP_TRADE_REFUSAL = rusacis.getProperty("PropTradeRefusal", true);
		PROP_AUTO_LOOT = rusacis.getProperty("PropAutoLoot", false);
		PROP_BUFF_PROTECTED = rusacis.getProperty("PropBuffProtected", false);
		ENABLE_ONLINE_COMMAND = rusacis.getProperty("EnabledOnlineCommand", false);
		MULTIPLIER_ONLINE_COMMAND = rusacis.getProperty("MultiplierOnlineCommand", 1);
		
		BOTS_PREVENTION = rusacis.getProperty("EnableBotsPrevention", false);
		BOTS_LOGS = rusacis.getProperty("BotsLogs", false);
		KILLS_COUNTER = rusacis.getProperty("KillsCounter", 60);
		KILLS_COUNTER_RANDOMIZATION = rusacis.getProperty("KillsCounterRandomization", 50);
		VALIDATION_TIME = rusacis.getProperty("ValidationTime", 60);
		PUNISHMENT = rusacis.getProperty("Punishment", 0);
		PUNISHMENT_TIME = rusacis.getProperty("PunishmentTime", 60);
		
		USE_PREMIUM_SERVICE = rusacis.getProperty("UsePremiumServices", false);
		ALTERNATE_DROP_LIST = rusacis.getProperty("AlternateDropList", false);
		
		ATTACK_PTS = rusacis.getProperty("AttackPTS", true);
		SUBCLASS_SKILLS = rusacis.getProperty("SubClassSkills", false);
		GAME_SUBCLASS_EVERYWHERE = rusacis.getProperty("SubclassEverywhere", false);
		
		SHOW_NPC_INFO = rusacis.getProperty("ShowNpcInfo", false);
		ALLOW_GRAND_BOSSES_TELEPORT = rusacis.getProperty("AllowGrandBossesTeleport", false);
		
		USE_SAY_FILTER = rusacis.getProperty("UseChatFilter", false);
		CHAT_FILTER_CHARS = rusacis.getProperty("ChatFilterChars", "^_^");
		
		try (Stream<String> lines = Files.lines(Paths.get(CHAT_FILTER_FILE), StandardCharsets.UTF_8))
		{
			FILTER_LIST = lines.map(String::trim).filter(line -> (!line.isEmpty() && (line.charAt(0) != '#'))).toList();
			LOGGER.info("Loaded " + FILTER_LIST.size() + " Filter Words.");
		}
		catch (IOException e)
		{
			LOGGER.warn("Error while loading chat filter words!", e);
		}
		
		CABAL_BUFFER = rusacis.getProperty("CabalBuffer", false);
		SUPER_HASTE = rusacis.getProperty("SuperHaste", false);
		
		RESTRICTED_CHAR_NAMES = rusacis.getProperty("ListOfRestrictedCharNames", "");
		LIST_RESTRICTED_CHAR_NAMES = new ArrayList<>();
		for (String name : RESTRICTED_CHAR_NAMES.split(","))
			LIST_RESTRICTED_CHAR_NAMES.add(name.toLowerCase());
		
		FAKE_ONLINE_AMOUNT = rusacis.getProperty("FakeOnlineAmount", 1);
		
		BUFFS_CATEGORY = rusacis.getProperty("PremiumBuffsCategory", "");
		PREMIUM_BUFFS_CATEGORY = new ArrayList<>();
		for (String buffs : BUFFS_CATEGORY.split(","))
			PREMIUM_BUFFS_CATEGORY.add(buffs);
		
		ANTIFEED_ENABLE = rusacis.getProperty("AntiFeedEnable", false);
		ANTIFEED_DUALBOX = rusacis.getProperty("AntiFeedDualbox", true);
		ANTIFEED_DISCONNECTED_AS_DUALBOX = rusacis.getProperty("AntiFeedDisconnectedAsDualbox", true);
		ANTIFEED_INTERVAL = rusacis.getProperty("AntiFeedInterval", 120) * 1000;
		
		DUALBOX_CHECK_MAX_PLAYERS_PER_IP = rusacis.getProperty("DualboxCheckMaxPlayersPerIP", 0);
		DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP = rusacis.getProperty("DualboxCheckMaxOlympiadParticipantsPerIP", 0);
		
		String[] autoLootItemIds = rusacis.getProperty("AutoLootItemIds", "0").split(",");
		AUTO_LOOT_ITEM_IDS = new ArrayList<>(autoLootItemIds.length);
		for (String item : autoLootItemIds)
		{
			Integer itm = 0;
			try
			{
				itm = Integer.parseInt(item);
			}
			catch (NumberFormatException nfe)
			{
				LOGGER.warn("Auto loot item ids: Wrong ItemId passed: " + item);
			}
			
			if (itm != 0)
				AUTO_LOOT_ITEM_IDS.add(itm);
		}
		
		HIT_TIME = rusacis.getProperty("HitTime", false);
		
		SHOW_RAID_HTM = rusacis.getProperty("ShowRaidHtm", false);
		SHOW_EPIC_HTM = rusacis.getProperty("ShowEpicHtm", false);
		USE_CONFIG_RAID_GRAND_BOSS_RESPAWN = rusacis.getProperty("UseConfigRaidGrandBossRespawn", true);
		final long[] raidRespawn = parseBossRespawn(rusacis.getProperty("RaidBossRespawn", ""));
		RAID_BOSS_RESPAWN_BASE_MS = raidRespawn[0];
		RAID_BOSS_RESPAWN_RANDOM_MS = raidRespawn[1];
		final long[] grandRespawn = parseBossRespawn(rusacis.getProperty("GrandBossRespawn", ""));
		GRAND_BOSS_RESPAWN_BASE_MS = grandRespawn[0];
		GRAND_BOSS_RESPAWN_RANDOM_MS = grandRespawn[1];
		RAID_BOSS_RESPAWN_OVERRIDES = parseBossRespawnOverrides(rusacis.getProperty("RaidBossRespawnById", ""));
		GRAND_BOSS_RESPAWN_OVERRIDES = parseBossRespawnOverrides(rusacis.getProperty("GrandBossRespawnById", ""));
		RAID_BOSS_TELEPORT_ENABLED = rusacis.getProperty("RaidBossTeleportEnabled", true);
		EPIC_BOSS_TELEPORT_ENABLED = rusacis.getProperty("EpicBossTeleportEnabled", true);
		BOSS_TELEPORT_MIN_RANGE = rusacis.getProperty("BossTeleportMinRange", 600);
		BOSS_TELEPORT_MAX_RANGE = rusacis.getProperty("BossTeleportMaxRange", 1200);
		BOSS_TELEPORT_ITEM_ID = rusacis.getProperty("BossTeleportItemId", 0);
		BOSS_TELEPORT_ITEM_COUNT = rusacis.getProperty("BossTeleportItemCount", 0L);
		
		TIME_ZONE = rusacis.getProperty("TimeZone", "GMT+2");
		DATE_FORMAT = rusacis.getProperty("DateFormat", "E MMM dd HH:mm yyyy 'GMT+2'");
		
		CUSTOM_BUFFER_MANAGER_NPC = rusacis.getProperty("CustomBufferManagerNpc", false);
		SKIP_CATEGORY = rusacis.getProperty("SkipCategory", "").split(",");
		
		BARAKIEL = rusacis.getProperty("Barakiel", false);
		CREATURE_SEE = rusacis.getProperty("CreatureSee", true);
		
		NEW_REGEN = rusacis.getProperty("NewRegen", false);
		CATACOMBS_IN_ANY_PERIOD = rusacis.getProperty("CatacombsInAnyPeriod", false);
		STRICT_SEVENSIGNS = rusacis.getProperty("StrictSevenSigns", true);
		CLASS_OVERLORD = rusacis.getProperty("ClassOverlord", false);
		RACE_ELF = rusacis.getProperty("RaceElf", false);
		RESTRICTED_CLASSES = rusacis.getProperty("RestrictedClasses", false);
		
		ENABLE_COMMAND_GOLDBAR = rusacis.getProperty("BankingEnabled", false);
		BANKING_SYSTEM_GOLDBARS = rusacis.getProperty("BankingGoldbarCount", 1);
		BANKING_SYSTEM_ADENA = rusacis.getProperty("BankingAdenaCount", 5000);
		
		AUTO_POTIONS_ENABLED = rusacis.getProperty("AutoPotionsEnabled", false);
		AUTO_POTIONS_IN_OLYMPIAD = rusacis.getProperty("AutoPotionsInOlympiad", false);
		AUTO_POTION_MIN_LEVEL = rusacis.getProperty("AutoPotionMinimumLevel", 1);
		ACP_PERIOD = rusacis.getProperty("AcpPeriod", 500);
		AUTO_CP_ENABLED = rusacis.getProperty("AutoCpEnabled", true);
		AUTO_HP_ENABLED = rusacis.getProperty("AutoHpEnabled", true);
		AUTO_MP_ENABLED = rusacis.getProperty("AutoMpEnabled", true);
		AUTO_CP_ITEM_IDS = new HashSet<>();
		for (String s : rusacis.getProperty("AutoCpItemIds", "0").split(","))
			AUTO_CP_ITEM_IDS.add(Integer.parseInt(s));
		
		AUTO_HP_ITEM_IDS = new HashSet<>();
		for (String s : rusacis.getProperty("AutoHpItemIds", "0").split(","))
			AUTO_HP_ITEM_IDS.add(Integer.parseInt(s));
		
		AUTO_MP_ITEM_IDS = new HashSet<>();
		for (String s : rusacis.getProperty("AutoMpItemIds", "0").split(","))
			AUTO_MP_ITEM_IDS.add(Integer.parseInt(s));
		
		MULTISELL_MAX_AMOUNT = rusacis.getProperty("MultisellMaxAmount", 9999);
		
		ENABLED_AUCTION = rusacis.getProperty("EnabledAuction", false);
		AUCTION_LIMIT_ITEM = rusacis.getProperty("AuctionLimitItem", 20);
		AUCTION_FEE = rusacis.getProperty("AuctionFee", 15000);
		
		AUCTION_ITEM_FEE = rusacis.getProperty("AuctionItemFee", 57);
		AUCTION_ITEM_FEE_NAME = rusacis.getProperty("AuctionItemFeeName", "Adena");
		
		AUTOFARM_ENABLED = rusacis.getProperty("AutoFarmEnabled", false);
		AUTOFARM_MAX_ZONE_AREA = rusacis.getProperty("MaxZoneArea", 7000000);
		AUTOFARM_MAX_ROUTE_PERIMITER = rusacis.getProperty("MaxRoutePerimeter", 7000000);
		AUTOFARM_MAX_OPEN_RADIUS = rusacis.getProperty("MaxOpenRadius", 0);
		AUTOFARM_MAX_ZONES = rusacis.getProperty("MaxZones", 5);
		AUTOFARM_MAX_ROUTES = rusacis.getProperty("MaxRoutes", 5);
		AUTOFARM_MAX_ZONE_NODES = rusacis.getProperty("MaxZoneNodes", 15);
		AUTOFARM_MAX_ROUTE_NODES = rusacis.getProperty("MaxRouteNodes", 30);
		AUTOFARM_MAX_TIMER = rusacis.getProperty("MaxTimer", 0);
		AUTOFARM_HP_HEAL_RATE = rusacis.getProperty("HpHealRate", 80) / 100.;
		AUTOFARM_MP_HEAL_RATE = rusacis.getProperty("MpHealRate", 80) / 100.;
		AUTOFARM_DEBUFF_CHANCE = rusacis.getProperty("DebuffChance", 30);
		AUTOFARM_HP_POTIONS = rusacis.getProperty("HpPotions", new int[0]);
		AUTOFARM_MP_POTIONS = rusacis.getProperty("MpPotions", new int[0]);
		AUTOFARM_ALLOW_DUALBOX = rusacis.getProperty("AllowDualbox", true);
		AUTOFARM_DISABLE_TOWN = rusacis.getProperty("DisableTown", true);
		AUTOFARM_SEND_LOG_MESSAGES = rusacis.getProperty("SendLogMessages", false);
		AUTOFARM_CHANGE_PLAYER_TITLE = rusacis.getProperty("ChangePlayerTitle", false);
		AUTOFARM_CHANGE_PLAYER_NAME_COLOR = rusacis.getProperty("ChangePlayerNameColor", false);
		AUTOFARM_PLAYER_NAME_COLOR = rusacis.getProperty("PlayerNameColor", "000000");
		AUTOFARM_DEBUG_RETURN = rusacis.getProperty("DebugAutoFarmReturn", false);
		
		ENABLE_OFFLINE_FARM_COMMAND = rusacis.getProperty("EnableOfflineFarmCommand", true);
		OFFLINE_FARM_PREMIUM = rusacis.getProperty("OfflineFarmPremium", false);
		OFFLINE_FARM_LOGOUT_ON_DEATH = rusacis.getProperty("OfflineFarmLogoutOnDeath", true);
		DUALBOX_CHECK_MAX_OFFLINEPLAY_PER_IP = rusacis.getProperty("DualboxCheckMaxOfflinePlayPerIP", 3);
		DUALBOX_CHECK_MAX_OFFLINEPLAY_PREMIUM_PER_IP = rusacis.getProperty("DualboxCheckMaxOfflinePlayPremiumPerIP", 5);
		DUALBOX_COUNT_OFFLINE_TRADERS = rusacis.getProperty("DualboxCountOfflineTraders", true);
		AWAY_PLAY_SET_NAME_COLOR = rusacis.getProperty("OfflineFarmSetNameColor", true);
		AWAY_PLAY_NAME_COLOR = rusacis.getProperty("OfflineFarmNameColor", "00FF00");
		HP_POTION_ITEM_ID = rusacis.getProperty("HpPotionItemId", 1539);
		MP_POTION_ITEM_ID = rusacis.getProperty("MpPotionItemId", 1540);
		AUTOFARM_MAX_LEVEL_DIFFERENCE = rusacis.getProperty("AutofarmMaxLevelDifference", 10);
		IGNORE_RAID_BOSSES = rusacis.getProperty("IgnoreRaidBosses", true);
		IGNORE_RAID_MINIONS = rusacis.getProperty("IgnoreRaidMinions", true);
		IGNORE_AGATHIONS = rusacis.getProperty("IgnoreAgathions", true);
		IGNORE_CHESTS = rusacis.getProperty("IgnoreChests", true);
		
		SELLBUFF_ENABLED = rusacis.getProperty("SellBuffEnable", true);
		SELLBUFF_MP_MULTIPLER = rusacis.getProperty("MpCostMultipler", 1);
		SELLBUFF_PAYMENT_ID = rusacis.getProperty("PaymentID", 57);
		SELLBUFF_MIN_PRICE = rusacis.getProperty("MinimumPrice", 1);
		SELLBUFF_MAX_PRICE = rusacis.getProperty("MaximumPrice", 100000000);
		SELLBUFF_MAX_BUFFS = rusacis.getProperty("MaxBuffs", 15);
		CUSTOM_TIME_BUFF = rusacis.getProperty("CustomTimeBuff", false);
		
		ENTER_ANAKAZEL = rusacis.getProperty("EnterAnakazel", false);
		
		MAX_RUN_SPEED = rusacis.getProperty("MaxRunSpeed", 250);
		MAX_PATK = rusacis.getProperty("MaxPAtk", 999999);
		MAX_MATK = rusacis.getProperty("MaxMAtk", 999999);
		MAX_PCRIT_RATE = rusacis.getProperty("MaxPCritRate", 500);
		MAX_MCRIT_RATE = rusacis.getProperty("MaxMCritRate", 200);
		MAX_PATK_SPEED = rusacis.getProperty("MaxPAtkSpeed", 1500);
		MAX_MATK_SPEED = rusacis.getProperty("MaxMAtkSpeed", 1999);
		MAX_EVASION = rusacis.getProperty("MaxEvasion", 250);
		
		NEW_FOLLOW = rusacis.getProperty("NewFollow", false);
		ENABLE_MISSION = rusacis.getProperty("EnableMission", false);
		RANDOM_PVP_ZONE = rusacis.getProperty("RandomPvpZone", false);
		
		PTS_EMULATION_SPAWN = rusacis.getProperty("PTSEmulationSpawn", true);
		PTS_EMULATION_SPAWN_DURATION = rusacis.getProperty("PTSEmulationSpawnDuraion", 60);
		
		STOP_TOGGLE = rusacis.getProperty("StopToggle", true);
		
		ANNOUNCE_DIE_RAIDBOSS = rusacis.getProperty("AnnounceDieRaidBoss", false);
		ANNOUNCE_SPAWN_RAIDBOSS = rusacis.getProperty("AnnounceSpawnRaidBoss", false);
		
		ANNOUNCE_DIE_GRANDBOSS = rusacis.getProperty("AnnounceDieGrandBoss", false);
		ANNOUNCE_SPAWN_GRANDBOSS = rusacis.getProperty("AnnounceSpawnGrandBoss", false);
		
		NPC_SOULSHOT = rusacis.getProperty("NpcSoulshot", true);
		NPC_SPIRITSHOT = rusacis.getProperty("NpcSpiritshot", true);
		
		RETURN_HOME_MONSTER = rusacis.getProperty("ReturnHomeMonster", true);
		RETURN_HOME_MONSTER_RADIUS = rusacis.getProperty("ReturnHomeMonsterRadius", 2500);
		
		RETURN_HOME_RAIDBOSS = rusacis.getProperty("ReturnHomeRaidBoss", true);
		RETURN_HOME_RAIDBOSS_RADIUS = rusacis.getProperty("ReturnHomeRaidBossRadius", 2500);

		CANCEL_RETURN_ENABLED = rusacis.getProperty("CancelReturnEnabled", false);
		CANCEL_RETURN_MODE = rusacis.getProperty("CancelReturnMode", "CANCEL_ONLY");
		CANCEL_RETURN_MASS_ONLY = rusacis.getProperty("CancelReturnMassOnly", false);
		CANCEL_RETURN_MASS_MIN_COUNT = rusacis.getProperty("CancelReturnMassMinCount", 3);
		CANCEL_RETURN_TIME_MS = rusacis.getProperty("CancelReturnTimeMs", 15000);
		CANCEL_RETURN_SKIP_OLYMPIAD = rusacis.getProperty("CancelReturnSkipOlympiad", true);
		CANCEL_RETURN_NOTIFY = rusacis.getProperty("CancelReturnNotify", true);
		CANCEL_RETURN_MESSAGE = rusacis.getProperty("CancelReturnMessage", "Seus buffs foram restaurados.");
		
	}
	
	private static final void loadDatabaseProperties(ExProperties properties)
	{
		DATABASE_URL = getString(properties, "sql.url", "jdbc:mariadb://localhost/rusacis?useUnicode=true&characterEncoding=UTF-8");
		DATABASE_LOGIN = getString(properties, "sql.login", "root");
		DATABASE_PASSWORD = getString(properties, "sql.password", "");
	}
	
	/**
	 * Loads loginserver settings.<br>
	 * IP addresses, database, account, misc.
	 */
	private static final void loadLogin()
	{
		final ExProperties server = initProperties(LOGINSERVER_FILE);

		HOSTNAME = server.getProperty("Hostname", "localhost");
		LOGINSERVER_HOSTNAME = server.getProperty("LoginserverHostname", "*");
		LOGINSERVER_PORT = server.getProperty("LoginserverPort", 2106);
		GAMESERVER_LOGIN_HOSTNAME = server.getProperty("LoginHostname", "*");
		GAMESERVER_LOGIN_PORT = server.getProperty("LoginPort", 9014);
		LOGIN_TRY_BEFORE_BAN = server.getProperty("LoginTryBeforeBan", 3);
		LOGIN_BLOCK_AFTER_BAN = server.getProperty("LoginBlockAfterBan", 600);
		ACCEPT_NEW_GAMESERVER = server.getProperty("AcceptNewGameServer", false);
		SHOW_LICENCE = server.getProperty("ShowLicence", true);
		
		loadDatabaseProperties(server);
		
		AUTO_CREATE_ACCOUNTS = server.getProperty("AutoCreateAccounts", true);
		
		FLOOD_PROTECTION = server.getProperty("EnableFloodProtection", true);
		FAST_CONNECTION_LIMIT = server.getProperty("FastConnectionLimit", 15);
		NORMAL_CONNECTION_TIME = server.getProperty("NormalConnectionTime", 700);
		FAST_CONNECTION_TIME = server.getProperty("FastConnectionTime", 350);
		MAX_CONNECTION_PER_IP = server.getProperty("MaxConnectionPerIP", 50);
		SHOW_CONNECT = server.getProperty("ShowConnect", false);
		PROXY = server.getProperty("Proxy", false);
	}

	private static final void loadTranslator()
	{
		final ExProperties translator = initProperties(TRANSLATOR_FILE);
		
		DEEPL_AUTH_KEY = translator.getProperty("DeeplAuthKey", "");
		DEEP_CONTEXT_STRING = translator.getProperty("DeeplContext", "");
		DO_NOT_TRANSLATE = Arrays.asList(translator.getProperty("DoNotTranslate", new String[] {}));
	}
	
	/**
	 * Loads boss jewel upgrade settings from rusacis.properties.
	 */
	/**
	 * Loads boss jewel upgrade settings from BossJewelUpgrades.properties.
	 */
	private static final void loadBossJewelUpgrades()
	{
	    final ExProperties bossJewelUpgrades = initProperties(BOSS_JEWEL_UPGRADES_FILE);
	    UPGRADE_BOSS_JEWELS_ENCHANT = bossJewelUpgrades.getProperty("UpgradeBossJewelsEnchant", false);
	    LOGGER.info("UpgradeBossJewelsEnchant: {}", UPGRADE_BOSS_JEWELS_ENCHANT);

	    String[] jewelUpgrades = bossJewelUpgrades.getProperty("UpgradeBossJewels", "").split(",");
	    for (String upgrade : jewelUpgrades) {
	        if (upgrade.trim().isEmpty()) continue;
	        String[] parts = upgrade.split(":");
	        if (parts.length == 3) {
	            try {
	                int itemId = Integer.parseInt(parts[0].trim());
	                int enchantLevel = Integer.parseInt(parts[1].trim());
	                int newItemId = Integer.parseInt(parts[2].trim());
	                UPGRADE_BOSS_JEWELS.add(new JewelUpgrade(itemId, enchantLevel, newItemId));
	                UPGRADEABLE_BOSS_JEWELS.add(itemId);
	                LOGGER.info("Loaded JewelUpgrade: itemId={}, enchantLevel={}, newItemId={}", itemId, enchantLevel, newItemId);
	            } catch (NumberFormatException e) {
	                LOGGER.error("Failed to parse JewelUpgrade: {}", upgrade, e);
	            }
	        } else {
	            LOGGER.warn("Invalid JewelUpgrade format: {}", upgrade);
	        }
	    }
	    LOGGER.info("Loaded {} boss jewel upgrades.", UPGRADE_BOSS_JEWELS.size());
	    LOGGER.info("Loaded {} upgradeable boss jewels.", UPGRADEABLE_BOSS_JEWELS.size());
	}
	
	public static final void loadGameServer()
	{
		LOGGER.info("Loading gameserver configuration files.");
		
		loadOfflineShop();
		
		loadSafeDisconnect();
		
		loadBossZerg();
		
		loadClans();
		
		loadEvents();
		
		loadGeoengine();
		
		loadHexID();
		
		loadLanguage();
		
		loadProtection();
		
		loadNpcs();
		
		loadPlayers();
		
		loadSieges();
		
		loadServer();
		
		loadRates();
		
		loadRusAcis();

		loadTranslator();
		
	    loadBossJewelUpgrades();
	    
	    loadBossHealConfigs();
	}	
	
	public static final void loadLoginServer()
	{
		LOGGER.info("Loading loginserver configuration files.");
		
		loadLogin();
	}
	
	public static final void loadAccountManager()
	{
		LOGGER.info("Loading account manager configuration files.");
		
		loadLogin();
	}
	
	public static final void loadGameServerRegistration()
	{
		LOGGER.info("Loading gameserver registration configuration files.");
		
		loadLogin();
	}
	
	/**
	 * Load property value with {@code name}. First we check {@link java.lang.System system} properties, then config {@code properties}. If value is not found in both, return {@code defaultValue}.
	 * @param properties config properties.
	 * @param name property name.
	 * @param defaultValue default value.
	 * @return property value.
	 */
	private static String getString(ExProperties properties, String name, String defaultValue)
	{
		String systemValue = System.getProperty(name);
		if (systemValue != null)
			return systemValue;
		
		String value = properties.getProperty(name, defaultValue);
		if (value == null)
			return defaultValue;
		
		return value;
	}
	
	private static final Pattern BOSS_RESPAWN_PATTERN = Pattern.compile("^(\\d+)d\\+(\\d+)h(?:\\+(\\d+)h)?$", Pattern.CASE_INSENSITIVE);
	
	private static long[] parseBossRespawn(String value)
	{
		if (value == null || value.isBlank())
			return new long[] { 0L, 0L };
		
		final Matcher matcher = BOSS_RESPAWN_PATTERN.matcher(value.replace(" ", ""));
		if (!matcher.matches())
			return new long[] { 0L, 0L };
		
		final long days = Long.parseLong(matcher.group(1));
		final long hours = Long.parseLong(matcher.group(2));
		final String randomGroup = matcher.group(3);
		final long randomHours = (randomGroup != null) ? Long.parseLong(randomGroup) : 0L;
		
		final long baseMs = (days * 24L + hours) * 3_600_000L;
		final long randomMs = randomHours * 3_600_000L;
		return new long[] { baseMs, randomMs };
	}
	
	private static Map<Integer, long[]> parseBossRespawnOverrides(String value)
	{
		if (value == null || value.isBlank())
			return Map.of();
		
		final Map<Integer, long[]> overrides = new HashMap<>();
		final String[] entries = value.split(";");
		for (String entry : entries)
		{
			if (entry == null || entry.isBlank())
				continue;
			
			final String[] parts = entry.split("-", 2);
			if (parts.length != 2)
				continue;
			
			try
			{
				final int npcId = Integer.parseInt(parts[0].trim());
				final long[] respawn = parseBossRespawn(parts[1].trim());
				if (respawn[0] > 0L)
					overrides.put(npcId, respawn);
			}
			catch (NumberFormatException e)
			{
				continue;
			}
		}
		
		return overrides;
	}
	
	public static final class ClassMasterSettings
	{
		private final Map<Integer, Boolean> _allowedClassChange;
		private final Map<Integer, List<IntIntHolder>> _claimItems;
		private final Map<Integer, List<IntIntHolder>> _rewardItems;
		
		private ClassMasterSettings(String configLine)
		{
			_allowedClassChange = HashMap.newHashMap(3);
			_claimItems = HashMap.newHashMap(3);
			_rewardItems = HashMap.newHashMap(3);
			
			if (configLine != null)
				parseConfigLine(configLine.trim());
		}
		
		private void parseConfigLine(String configLine)
		{
			StringTokenizer st = new StringTokenizer(configLine, ";");
			while (st.hasMoreTokens())
			{
				int job = Integer.parseInt(st.nextToken());
				
				_allowedClassChange.put(job, true);
				
				List<IntIntHolder> items = new ArrayList<>();
				
				if (st.hasMoreTokens())
				{
					StringTokenizer st2 = new StringTokenizer(st.nextToken(), "[],");
					while (st2.hasMoreTokens())
					{
						StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "()");
						items.add(new IntIntHolder(Integer.parseInt(st3.nextToken()), Integer.parseInt(st3.nextToken())));
					}
				}
				
				_claimItems.put(job, items);
				items = new ArrayList<>();
				
				if (st.hasMoreTokens())
				{
					StringTokenizer st2 = new StringTokenizer(st.nextToken(), "[],");
					while (st2.hasMoreTokens())
					{
						StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "()");
						items.add(new IntIntHolder(Integer.parseInt(st3.nextToken()), Integer.parseInt(st3.nextToken())));
					}
				}
				
				_rewardItems.put(job, items);
			}
		}
		
		public boolean isAllowed(int job)
		{
			if (_allowedClassChange == null)
				return false;
			
			if (_allowedClassChange.containsKey(job))
				return _allowedClassChange.get(job);
			
			return false;
		}
		
		public List<IntIntHolder> getRewardItems(int job)
		{
			return _rewardItems.get(job);
		}
		
		public List<IntIntHolder> getRequiredItems(int job)
		{
			return _claimItems.get(job);
		}
	}
}
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import ext.mods.Config;
import ext.mods.Crypta.GlobalDropManager;
import ext.mods.commons.pool.ThreadPool;
import ext.mods.commons.random.Rnd;
import ext.mods.dungeon.Dungeon;
import ext.mods.gameserver.custom.data.EventsData;
import ext.mods.gameserver.custom.data.RaidDropAnnounceData;
import ext.mods.gameserver.custom.data.RatesData;
import ext.mods.gameserver.data.manager.CursedWeaponManager;
import ext.mods.gameserver.data.manager.DropSkipManager;
import ext.mods.gameserver.data.manager.EventsDropManager;
import ext.mods.gameserver.data.xml.ItemData;
import ext.mods.gameserver.enums.BossInfoType;
import ext.mods.gameserver.enums.DropType;
import ext.mods.gameserver.enums.skills.Stats;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Attackable;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.Summon;
import ext.mods.gameserver.model.actor.container.monster.OverhitState;
import ext.mods.gameserver.model.actor.container.monster.SeedState;
import ext.mods.gameserver.model.actor.container.monster.SpoilState;
import ext.mods.gameserver.model.actor.container.npc.AbsorbInfo;
import ext.mods.gameserver.model.actor.container.npc.AggroInfo;
import ext.mods.gameserver.model.actor.container.npc.RewardInfo;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.group.CommandChannel;
import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.model.item.DropCategory;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.Item;
import ext.mods.gameserver.model.records.custom.EventsInfo;
import ext.mods.gameserver.model.records.custom.RatesHolder;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.skills.L2Skill;

/**
 * A monster extends {@link Attackable} class.<br>
 * <br>
 * It is an attackable {@link Creature}, with the capability to hold minions/master.
 */
public class Monster extends Attackable
{
	private static final Logger DROP_LOG = Logger.getLogger("drop");
	
	private final Map<Integer, AbsorbInfo> _absorbersList = new ConcurrentHashMap<>();
	
	private final OverhitState _overhitState = new OverhitState(this);
	private final SpoilState _spoilState = new SpoilState();
	private final SeedState _seedState = new SeedState(this);
	
	private ScheduledFuture<?> _ccTask;
	
	private CommandChannel _firstCcAttacker;
	
	private long _lastCcAttack;
	
	private boolean _isRaidRelated;
	
	private boolean _isChampion;
	
	public Monster(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	public boolean isAgathion() {
      return getTemplate() != null && getTemplate().isAgathion();
	}
	
	@Override
	protected void calculateRewards(Creature creature)
	{
		if (getAI().getAggroList().isEmpty())
			return;
		
		final Map<Creature, RewardInfo> rewards = new ConcurrentHashMap<>();
		
		Player maxDealer = null;
		double maxDamage = 0.;
		double totalDamage = 0.;
		
		for (AggroInfo info : getAI().getAggroList().values())
		{
			if (!(info.getAttacker() instanceof Playable attacker))
				continue;
			
			final double damage = info.getDamage();
			if (damage <= 1)
				continue;
			
			if (!isInStrictRadius(attacker, Config.PARTY_RANGE))
				continue;
			
			final Player attackerPlayer = attacker.getActingPlayer();
			
			totalDamage += damage;
			
			RewardInfo reward = rewards.get(attacker);
			if (reward == null)
			{
				reward = new RewardInfo(attacker);
				rewards.put(attacker, reward);
			}
			reward.addDamage(damage);
			
			if (attacker instanceof Summon)
			{
				reward = rewards.get(attackerPlayer);
				if (reward == null)
				{
					reward = new RewardInfo(attackerPlayer);
					rewards.put(attackerPlayer, reward);
				}
				reward.addDamage(damage);
			}
			
			if (reward.getDamage() > maxDamage)
			{
				maxDealer = attackerPlayer;
				maxDamage = reward.getDamage();
			}
		}
		
		if (_firstCcAttacker != null)
			maxDealer = _firstCcAttacker.getLeader();
		
		doItemDrop((maxDealer != null && maxDealer.isOnline()) ? maxDealer : creature);
		
		for (RewardInfo reward : rewards.values())
		{
			if (reward.getAttacker() instanceof Summon)
				continue;
			
			final Player attacker = reward.getAttacker().getActingPlayer();
			
			final double damage = reward.getDamage();
			
			final Party attackerParty = attacker.getParty();
			if (attackerParty == null)
			{
				if (!attacker.isDead() && attacker.knows(this))
				{
					final int levelDiff = attacker.getStatus().getLevel() - getStatus().getLevel();
					final float penalty = (attacker.hasServitor()) ? ((Servitor) attacker.getSummon()).getExpPenalty() : 0;
					final int[] expSp = calculateExpAndSp(attacker, levelDiff, damage, totalDamage, attacker.getPremiumService());
					
					long exp = expSp[0];
					int sp = expSp[1];
					
					exp *= 1 - penalty;
					
					if (_overhitState.isValidOverhit(attacker))
					{
						attacker.sendPacket(SystemMessageId.OVER_HIT);
						exp += _overhitState.calculateOverhitExp(exp);
					}
					
					attacker.updateKarmaLoss(exp);
					
					attacker.addExpAndSp(exp, sp, rewards);
				}
			}
			else
			{
				double partyDmg = 0.;
				double partyMul = 1.;
				
				int partyLvl = 0;
				
				final List<Player> rewardedMembers = new ArrayList<>();
				final Map<Creature, RewardInfo> playersWithPets = new HashMap<>();
				
				for (Player partyPlayer : (attackerParty.isInCommandChannel()) ? attackerParty.getCommandChannel().getMembers() : attackerParty.getMembers())
				{
					if (partyPlayer == null || partyPlayer.isDead())
						continue;
					
					final boolean isInRange = isInStrictRadius(partyPlayer, Config.PARTY_RANGE);
					if (isInRange)
					{
						rewardedMembers.add(partyPlayer);
						
						if (partyPlayer.getStatus().getLevel() > partyLvl)
							partyLvl = (attackerParty.isInCommandChannel()) ? attackerParty.getCommandChannel().getLevel() : partyPlayer.getStatus().getLevel();
					}
					
					final RewardInfo reward2 = rewards.get(partyPlayer);
					if (reward2 != null)
					{
						if (isInRange)
							partyDmg += reward2.getDamage();
						
						rewards.remove(partyPlayer);
						
						playersWithPets.put(partyPlayer, reward2);
						if (partyPlayer.hasPet() && rewards.containsKey(partyPlayer.getSummon()))
							playersWithPets.put(partyPlayer.getSummon(), rewards.get(partyPlayer.getSummon()));
					}
				}
				
				if (partyDmg < totalDamage)
					partyMul = partyDmg / totalDamage;
				
				final int levelDiff = partyLvl - getStatus().getLevel();
				
				final int[] expSp1 = calculateExpAndSp(attacker, levelDiff, partyDmg, totalDamage, 1);
				long exp_premium = (long) (expSp1[0] * partyMul);
				int sp_premium = (int) (expSp1[1] * partyMul);
				
				final int[] expSp = calculateExpAndSp(attacker, levelDiff, partyDmg, totalDamage, 0);
				long exp = (long) (expSp[0] * partyMul);
				int sp = (int) (expSp[1] * partyMul);
				
				final int[] dynamExp = calculateExpAndSp(attacker, levelDiff, partyDmg, totalDamage, 0);
				long exp_dynam = (long) (dynamExp[0] * partyMul);
				
				if (_overhitState.isValidOverhit(attacker))
				{
					attacker.sendPacket(SystemMessageId.OVER_HIT);
					exp += _overhitState.calculateOverhitExp(exp);
					exp_premium += _overhitState.calculateOverhitExp(exp_premium);
					exp_dynam += _overhitState.calculateOverhitExp(exp_dynam);
				}
				
				if (partyDmg > 0)
					attackerParty.distributeXpAndSp(exp_dynam, exp_premium, sp_premium, exp, sp, rewardedMembers, partyLvl, playersWithPets);
			}
		}
	}
	
	@Override
	public boolean isAggressive()
	{
		return getTemplate().getAggroRange() > 0;
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		_overhitState.clear();
		
		_spoilState.clear();
		
		_seedState.clear();
		
		_absorbersList.clear();
		
	}
	
	@Override
	public void reduceCurrentHp(double damage, Creature attacker, boolean awake, boolean isDOT, L2Skill skill)
	{
		if (attacker != null && isRaidBoss())
		{
			final Party party = attacker.getParty();
			if (party != null)
			{
				final CommandChannel cc = party.getCommandChannel();
				if (BossInfoType.isCcMeetCondition(cc, getNpcId()))
				{
					if (_ccTask == null)
					{
						_ccTask = ThreadPool.scheduleAtFixedRate(this::checkCcLastAttack, 1000, 1000);
						_lastCcAttack = System.currentTimeMillis();
						_firstCcAttacker = cc;
						
						broadcastOnScreen(10000, BossInfoType.getBossInfo(getNpcId()).getCcRightsMsg(), cc.getLeader().getName());
					}
					else if (_firstCcAttacker.equals(cc))
						_lastCcAttack = System.currentTimeMillis();
				}
			}
		}
		super.reduceCurrentHp(damage, attacker, awake, isDOT, skill);
	}
	
	@Override
	public boolean isAttackableBy(Creature attacker)
	{
		if ((attacker instanceof Playable playableAttacker) && playableAttacker.getClanId() > 0 && playableAttacker.getClanId() == getClanId())
			return false;
		
		return super.isAttackableBy(attacker);
	}
	
	@Override
	public boolean isAttackableWithoutForceBy(Playable attacker)
	{
		return isAttackableBy(attacker);
	}
	
	@Override
	public boolean isRaidRelated()
	{
		return _isRaidRelated;
	}
	
	/**
	 * Set this object as part of raid (it can be either a boss or a minion).<br>
	 * <br>
	 * This state affects behaviors such as auto loot configs, Command Channel acquisition, or even Config related to raid bosses.<br>
	 * <br>
	 * A raid boss can't be lethal-ed, and a raid curse occurs if the level difference is too high.
	 */
	public void setRaidRelated()
	{
		_isRaidRelated = true;
	}
	
	public OverhitState getOverhitState()
	{
		return _overhitState;
	}
	
	public SpoilState getSpoilState()
	{
		return _spoilState;
	}
	
	public SeedState getSeedState()
	{
		return _seedState;
	}
	
	/**
	 * Add a {@link Player} that successfully absorbed the soul of this {@link Monster} into the _absorbersList.
	 * @param player : The {@link Player} to test.
	 * @param crystal : The {@link ItemInstance} which was used to register.
	 */
	public void addAbsorber(Player player, ItemInstance crystal)
	{
		AbsorbInfo ai = _absorbersList.get(player.getObjectId());
		if (ai == null)
		{
			_absorbersList.put(player.getObjectId(), new AbsorbInfo(crystal.getObjectId()));
		}
		else
		{
			if (!ai.isRegistered())
				ai.setItemId(crystal.getObjectId());
		}
	}
	
	/**
	 * Register a {@link Player} into this instance _absorbersList, setting the HP ratio. The {@link AbsorbInfo} must already exist.
	 * @param player : The {@link Player} to test.
	 */
	public void registerAbsorber(Player player)
	{
		AbsorbInfo ai = _absorbersList.get(player.getObjectId());
		if (ai == null)
			return;
		
		if (player.getInventory().getItemByObjectId(ai.getItemId()) == null)
			return;
		
		if (!ai.isRegistered())
		{
			ai.setAbsorbedHpPercent((int) getStatus().getHpRatio() * 100);
			ai.setRegistered(true);
		}
	}
	
	public AbsorbInfo getAbsorbInfo(int npcObjectId)
	{
		return _absorbersList.get(npcObjectId);
	}
	
	/**
	 * Calculate the XP and SP to distribute to the attacker of the {@link Monster}.
	 * @param player
	 * @param diff : The difference of level between the attacker and the {@link Monster}.
	 * @param damage : The damages done by the attacker.
	 * @param totalDamage : The total damage done.
	 * @param isPremium
	 * @return an array consisting of xp and sp values.
	 */
	private int[] calculateExpAndSp(Player player, int diff, double damage, double totalDamage, int isPremium)
	{
		
		if (dungeon != null)
			dungeon.onMobKill(this);
		double xp = getExpReward(isPremium) * damage / totalDamage;
		double sp = getSpReward(isPremium) * damage / totalDamage;
		
		if (diff > 5)
		{
			double pow = Math.pow((double) 5 / 6, diff - 5);
			xp = xp * pow;
			sp = sp * pow;
		}
		
		if (this instanceof GrandBoss)
		{
			xp *= Config.GRANDBOSS_RATE_XP;
			sp *= Config.GRANDBOSS_RATE_SP;
		}
		
		if (isRaidBoss())
		{
			xp *= Config.RAIDBOSS_RATE_XP;
			sp *= Config.RAIDBOSS_RATE_SP;
		}
		
		if (isChampion())
		{
			xp *= Config.CHAMPION_RATE_XP;
			sp *= Config.CHAMPION_RATE_SP;
		}
		
		if (isChampion() && player.getPremiumService() == 1)
		{
			xp *= Config.PREMIUM_CHAMPION_RATE_XP;
			sp *= Config.PREMIUM_CHAMPION_RATE_SP;
		}
		
		if (Config.DYNAMIC_XP)
		{
			int level = player.getStatus().getLevel();
			if (player.getParty() != null)
			{
				for (Player partyPlayer : player.getParty().getMembers())
				{
					var partyLevel = partyPlayer.getStatus().getLevel();
					if (Config.DYNAMIC_XP_RATES.containsKey(partyLevel))
					{
						double dynamicRate = Config.DYNAMIC_XP_RATES.get(partyLevel);
						xp *= dynamicRate;
					}
				}
			}
			else
			{
				if (Config.DYNAMIC_XP_RATES.containsKey(level))
				{
					double dynamicRate = Config.DYNAMIC_XP_RATES.get(level);
					xp *= dynamicRate;
				}
			}
		}
		
		if (player.getParty() != null)
		{
			double totalXpRate = 0;
			double totalSpRate = 0;
			int count = 0;
			
			for (Player partyPlayer : player.getParty().getMembers())
			{
				RatesHolder rates = RatesData.getInstance().getRates(partyPlayer.getStatus().getLevel());
				if (rates != null)
				{
					totalXpRate += rates.getXpRate();
					totalSpRate += rates.getSpRate();
					count++;
				}
			}
			
			if (count > 0)
			{
				xp *= totalXpRate / count;
				sp *= totalSpRate / count;
			}
		}
		else
		{
			RatesHolder rates = RatesData.getInstance().getRates(player.getStatus().getLevel());
			if (rates != null)
			{
				xp *= rates.getXpRate();
				sp *= rates.getSpRate();
			}
		}
		
		xp = xp * player.getStatus().calcStat(Stats.XP_RATE, 100, null, null) / 100;
		sp = sp * player.getStatus().calcStat(Stats.SP_RATE, 100, null, null) / 100;
		
		if (xp <= 0)
		{
			xp = 0;
			sp = 0;
		}
		else if (sp <= 0)
			sp = 0;
		
		return new int[]
		{
			(int) xp,
			(int) sp
		};
	}
	
	@Override
	public final boolean isChampion()
	{
		return _isChampion;
	}
	
	public final void setChampion(boolean value)
	{
		_isChampion = value;
	}
	
	/**
	 * @param player : The {@link Player} to test.
	 * @return The multiplier for drop purpose, based on this instance and the {@link Player} set as parameter.
	 */
	private double calculateLevelMultiplier(Player player)
	{
		if (!Config.DEEPBLUE_DROP_RULES)
			return 1.;
		
		final int levelDiff = player.getStatus().getLevel() - getStatus().getLevel() - ((isRaidBoss()) ? 3 : 5);
		
		return (levelDiff <= 0) ? 1. : Math.max(0.1, 1 - 0.18 * levelDiff);
	}
	
	/**
	 * Manage drops of this {@link Monster} using an associated {@link NpcTemplate}.<br>
	 * <br>
	 * This method is called by {@link #calculateRewards}.
	 * @param creature : The {@link Creature} that made the most damage.
	 */
	public void doItemDrop(Creature creature)
	{
		if (creature == null)
			return;
		
		final Player player = creature.getActingPlayer();
		if (player == null)
			return;
		
		CursedWeaponManager.getInstance().checkDrop(this, player);
		
		final double levelMultiplier = calculateLevelMultiplier(player);
		
		if (isChampion() && Config.CHAMPION_REWARD > 0 && player.getStatus().getLevel() <= getStatus().getLevel() + 9)
		{
			int dropChance = Config.CHAMPION_REWARD;
			
			if (Rnd.get(100) < dropChance)
			{
				final IntIntHolder item = new IntIntHolder(Config.CHAMPION_REWARD_ID, Math.max(1, Rnd.get(1, Config.CHAMPION_REWARD_QTY)));
				
				if (Config.AUTO_LOOT)
					player.addItem(item.getId(), item.getValue(), true);
				else
					dropItem(item, player);
			}
		}
		
		handleEventDrop("Christmas", creature, player);
		handleEventDrop("HeavyMedal", creature, player);
		handleEventDrop("L2Day", creature, player);
		handleEventDrop("Squash", creature, player);
		
		final Object globalDropManager = GlobalDropManager.getInstance();
		if (globalDropManager != null)
		{
			final Object shouldCancel = GlobalDropManager.getInstance().shouldCancelOriginalDrop(this);
			if (shouldCancel instanceof Boolean && (Boolean) shouldCancel)
			{
				GlobalDropManager.getInstance().onKill(player, this);
				return;
			}
			
			GlobalDropManager.getInstance().onKill(player, this);
		}
		
		RatesHolder rates = RatesData.getInstance().getRates(player.getStatus().getLevel());
		double dropRateMult = (rates != null) ? rates.getDropRate() : 1.0;
		double spoilRateMult = (rates != null) ? rates.getSpoilRate() : 1.0;
		
		final boolean isSpoiled = getSpoilState().isSpoiled();
		final boolean isBlockingDrops = getSeedState().isSeeded() && !getSeedState().getSeed().isAlternative();
		final boolean isRaid = isRaidBoss();
		
		for (DropCategory category : getTemplate().getDropData())
		{
			final DropType type = category.getDropType();
			
			if (type == DropType.SPOIL && !isSpoiled)
				continue;
			
			if (type == DropType.DROP && isBlockingDrops)
				continue;
			
			double effectiveMultiplier = levelMultiplier * ((type == DropType.SPOIL) ? spoilRateMult : dropRateMult);
			
			for (IntIntHolder drop : Config.ALTERNATE_DROP_LIST ? category.calcDropList(player, this, new ArrayList<>(), isRaid) : category.calculateDrop(player, this, effectiveMultiplier, isRaid))
			{
				if (Config.DROP_ITEMS && type != DropType.HERB)
				{
					final LogRecord logRecord = new LogRecord(Level.INFO, type.name());
					logRecord.setLoggerName("drop");
					logRecord.setParameters(new Object[]
					{
						creature,
						this,
						ItemData.getInstance().getTemplate(drop.getId()),
						"Quantity (" + drop.getValue() + ")",
					});
					DROP_LOG.log(logRecord);
				}
				
				if (type == DropType.SPOIL)
					getSpoilState().add(drop);
				else if (type == DropType.HERB)
					dropOrAutoLootHerb(player, drop);
				else
					dropOrAutoLootItem(player, drop);
				
				if (isRaidBoss() && RaidDropAnnounceData.getInstance().isEnabled())
				{
					final Item item = ItemData.getInstance().getTemplate(drop.getId());
					if (RaidDropAnnounceData.getInstance().shouldAnnounce(item))
					{
						String template = RaidDropAnnounceData.getInstance().getMessageTemplate(item);
						if (template == null || template.isEmpty())
							template = "[RAID DROP] {player} killed {boss} and got: {amount}x {item}.";
						
						String msg = template.replace("{player}", player.getName()).replace("{boss}", getName()).replace("{item}", item.getName()).replace("{amount}", String.valueOf(drop.getValue()));
						
						World.announceToOnlinePlayers(msg, true);
					}
				}
				
			}
		}
		
	}
	
	/**
	 * Drop on ground or auto loot a reward item, depending about activated {@link Config}s.
	 * @param player : The {@link Player} who made the highest damage contribution.
	 * @param holder : The {@link IntIntHolder} used for reward (item id / amount).
	 */
	public void dropOrAutoLootItem(Player player, IntIntHolder holder)
	{
		final int itemId = holder.getId();
		
		if (DropSkipManager.getInstance().isSkipped(player.getObjectId(), itemId))
			return;
		
		final Item item = ItemData.getInstance().getTemplate(holder.getId());
		
		RatesHolder rates = RatesData.getInstance().getRates(player.getStatus().getLevel());
		double adenaRate = (rates != null) ? rates.getAdenaRate() : 1.0;
		
		if (Config.ENABLE_MENU)
		{
			if (isRaidBoss() && Config.AUTO_LOOT_RAID)
				player.addItem(holder.getId(), holder.getValue(), true);
			else if (isRaidBoss())
				dropItem(holder, player);
			
			if (player.getAutoLoot() && !isRaidBoss())
			{
				if (player.getInventory().validateCapacityByItemId(holder))
				{
					if (player.isInParty())
						player.getParty().distributeItem(player, holder, false, this);
					else if (holder.getId() == 57)
					{
						int adenaAmount = (int) Math.max(1, holder.getValue() * adenaRate);
						if ((Integer.MAX_VALUE - player.getInventory().getAdena() - adenaAmount) < 0)
							dropItem(holder, player);
						else
							player.addAdena(adenaAmount, true);
					}
					else
						player.addItem(holder.getId(), holder.getValue(), true);
				}
				else
					dropItem(holder, player);
			}
			else if (!isRaidBoss())
				dropItem(holder, player);
		}
		else if ((Config.AUTO_LOOT_ITEM_IDS.contains(item.getItemId())) || ((isRaidBoss() && Config.AUTO_LOOT_RAID) || (!isRaidBoss() && Config.AUTO_LOOT)) && player.getInventory().validateCapacityByItemId(holder))
		{
			if (player.isInParty())
				player.getParty().distributeItem(player, holder, false, this);
			else if (holder.getId() == 57)
			{
				int adenaAmount = (int) Math.max(1, holder.getValue() * adenaRate);
				if ((Integer.MAX_VALUE - player.getInventory().getAdena() - adenaAmount) < 0)
					dropItem(holder, player);
				else
					player.addAdena(adenaAmount, true);
			}
			else
				player.addItem(holder.getId(), holder.getValue(), true);
		}
		else
			dropItem(holder, player);
		
		if (isRaidBoss())
			broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DIED_DROPPED_S3_S2).addCharName(this).addItemName(holder.getId()).addNumber(holder.getValue()));
	}
	
	/**
	 * Drop on ground or auto loot a reward item, depending about activated {@link Config}s.
	 * @param player : The {@link Player} who made the highest damage contribution.
	 * @param holder : The {@link IntIntHolder} used for reward (item id / amount).
	 */
	private void dropOrAutoLootHerb(Player player, IntIntHolder holder)
	{
		if (Config.AUTO_LOOT_HERBS)
			player.addItem(holder.getId(), 1, true);
		else
		{
			final int count = holder.getValue();
			if (count > 1)
			{
				holder.setValue(1);
				for (int i = 0; i < count; i++)
					dropItem(holder, player);
			}
			else
				dropItem(holder, player);
		}
	}
	
	/**
	 * Drop a reward on the ground, to this {@link Monster} feet.
	 * @param holder : The {@link IntIntHolder} used for reward (item id / amount).
	 */
	public void dropItem(IntIntHolder holder)
	{
		dropItem(holder, null);
	}
	
	/**
	 * Drop a reward on the ground, to this {@link Monster} feet. It is item protected to the {@link Player} set as parameter.
	 * @param player : The {@link Player} used as item protection.
	 * @param holder : The {@link IntIntHolder} used for reward (item id / amount).
	 */
	public void dropItem(IntIntHolder holder, Player player)
	{
		for (int i = 0; i < holder.getValue(); i++)
		{
			final ItemInstance item = ItemInstance.create(holder.getId(), holder.getValue());
			if (player != null)
				item.setDropProtection(player.getObjectId(), isRaidBoss());
			
			if (getInstanceMap() != null)
			{
				item.setInstanceMap(getInstanceMap(), true);
			}
			else
			{
				item.setInstanceMap(getInstanceMap(), false);
			}
			
			item.dropMe(this);
			
			if (item.isStackable() || !Config.MULTIPLE_ITEM_DROP)
				break;
		}
	}
	
	/**
	 * Check CommandChannel loot priority every second. After 5min, the loot priority dissapears.
	 */
	private void checkCcLastAttack()
	{
		if (System.currentTimeMillis() - _lastCcAttack <= 300000)
			return;
		
		_firstCcAttacker = null;
		_lastCcAttack = 0;
		
		if (_ccTask != null)
		{
			_ccTask.cancel(false);
			_ccTask = null;
		}
		
		broadcastOnScreen(10000, BossInfoType.getBossInfo(getNpcId()).getCcNoRightsMsg());
	}
	
	private void handleEventDrop(String eventName, Creature creature, Player player)
	{
		List<Integer> rewardItem = null;
		
		switch (eventName)
		{
			case "Christmas":
				if (EventsDropManager.getInstance().haveActiveChristmasEvent())
					rewardItem = EventsDropManager.getInstance().calculateChristmasRewardItem(this.getTemplate(), creature);
				break;
			case "HeavyMedal":
				if (EventsDropManager.getInstance().haveActiveMedalsEvent())
					rewardItem = EventsDropManager.getInstance().calculateMedalsRewardItem(this.getTemplate(), creature);
				break;
			case "L2Day":
				if (EventsDropManager.getInstance().haveActiveL2DayEvent())
					rewardItem = EventsDropManager.getInstance().calculateL2DayRewardItem(this.getTemplate(), creature);
				break;
			case "Squash":
				if (EventsDropManager.getInstance().haveActiveSquashEvent())
					rewardItem = EventsDropManager.getInstance().calculateSquashRewardItem(this.getTemplate(), creature);
				break;
		}
		
		if (rewardItem != null && rewardItem.get(0) > 0 && rewardItem.get(1) > 0)
		{
			final EventsInfo event = EventsData.getInstance().getEventsData(eventName);
			if (event != null && event.items() != null && !event.items().isEmpty())
			{
				int eventMinLevel = event.items().get(0).minLvl();
				if (getStatus().getLevel() >= eventMinLevel)
				{
					final IntIntHolder item = new IntIntHolder(rewardItem.get(0), rewardItem.get(1));
					if (player.getAutoLoot())
						player.addItem(item.getId(), item.getValue(), true);
					else
						dropItem(player, item.getId(), item.getValue());
				}
			}
		}
	}
	
	private Dungeon dungeon = null;
	
	public void setDungeon(Dungeon dungeon)
	{
		this.dungeon = dungeon;
	}
	
	@Override
	public Monster getMonster()
	{
		return this;
	}
}
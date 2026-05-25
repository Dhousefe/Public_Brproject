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
package ext.mods.gameserver.model.residence.clanhall;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ConnectionPool;
import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.data.sql.ClanTable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.model.residence.Residence;
import ext.mods.gameserver.model.zone.type.ClanHallZone;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

/**
 * In Lineage 2, there are special building for clans: clan halls.<br>
 * <br>
 * Clan halls give the owning clan some useful benefits. There are 2 types of clan halls: auctionable and contestable. A clan can own only 1 hall at the same time.
 * <ul>
 * <li>Auctionable clan halls can be found in any big township, excluding starting villages, Oren and Heine. Any clan can purchase a hall via auction if they can afford it.</li>
 * <li>Some clan halls come into players possession only once they're conquered. Just like clan halls available via purchase, they are used for making items, teleportation, casting auras etc.</li>
 * </ul>
 */
public class ClanHall extends Residence
{
	protected static final CLogger LOGGER = new CLogger(ClanHall.class.getName());
	
	private static final String DELETE_FUNCTIONS = "DELETE FROM clanhall_functions WHERE hall_id=?";
	private static final String UPDATE_CH = "UPDATE clanhall SET ownerId=?, paidUntil=?, paid=?, sellerBid=?, sellerName=?, sellerClanName=?, endDate=? WHERE id=?";
	
	private static final int ONE_DAY = 86400000;
	private static final int ONE_WEEK = 604800000;
	
	public static final int FUNC_RESTORE_HP = 1;
	public static final int FUNC_RESTORE_MP = 2;
	public static final int FUNC_RESTORE_EXP = 4;
	public static final int FUNC_TELEPORT = 5;
	public static final int FUNC_DECO_CURTAINS = 7;
	public static final int FUNC_SUPPORT = 9;
	public static final int FUNC_DECO_FRONTPLATEFORM = 11;
	public static final int FUNC_ITEM_CREATE = 12;
	
	private static final  Map<Integer, Integer> GRADE_LIST = Map.ofEntries(
        Map.entry(21, 2),
        Map.entry(22, 2),
        Map.entry(23, 2),
        Map.entry(24, 2),
        Map.entry(25, 2),
        Map.entry(26, 2),
        Map.entry(27, 2),
        Map.entry(28, 2),
        Map.entry(29, 2),
        Map.entry(30, 2),
        Map.entry(31, 1),
        Map.entry(32, 1),
        Map.entry(33, 1),
        Map.entry(34, 3),
        Map.entry(35, 3),
        Map.entry(36, 3),
        Map.entry(37, 3),
        Map.entry(38, 3),
        Map.entry(39, 3),
        Map.entry(40, 3),
        Map.entry(41, 3),
        Map.entry(42, 3),
        Map.entry(43, 3),
        Map.entry(44, 3),
        Map.entry(45, 3),
        Map.entry(46, 3),
        Map.entry(47, 3),
        Map.entry(48, 3),
        Map.entry(49, 3),
        Map.entry(50, 3),
        Map.entry(51, 3),
        Map.entry(52, 3),
        Map.entry(53, 3),
        Map.entry(54, 3),
        Map.entry(55, 3),
        Map.entry(56, 3),
        Map.entry(57, 3),
        Map.entry(58, 2),
        Map.entry(59, 2),
        Map.entry(60, 2),
        Map.entry(61, 2),
        Map.entry(62, 3),
        Map.entry(63, 3),
        Map.entry(64, 3)
    );
	
	private final Map<Integer, ClanHallFunction> _functions = new ConcurrentHashMap<>();
	
	private final String _desc;
	
	private final int _auctionMin;
	private final int _deposit;
	private final int _lease;
	private final int _size;
	
	private ScheduledFuture<?> _feeTask;
	private Auction _auction;
	private ClanHallZone _zone;
	private long _paidUntil;
	private boolean _isPaid;
	
	private List<Buff> _buffs = new ArrayList<>();
	
	public ClanHall(StatSet set)
	{
		super(set);
		
		_desc = set.getString("desc");
		_townName = set.getString("loc");
		_auctionMin = set.getInteger("auctionMin", 0);
		_deposit = set.getInteger("deposit", 0);
		_lease = set.getInteger("lease", 0);
		_size = set.getInteger("size", 0);
	}
	
	public final String getDesc()
	{
		return _desc;
	}
	
	public final int getAuctionMin()
	{
		return _auctionMin;
	}
	
	public final int getDeposit()
	{
		return _deposit;
	}
	
	public final int getLease()
	{
		return _lease;
	}
	
	public final int getSize()
	{
		return _size;
	}
	
	public final int getGrade()
	{
		return GRADE_LIST.get(_id);
	}
	
	public final Auction getAuction()
	{
		return _auction;
	}
	
	public final void setAuction(Auction auction)
	{
		_auction = auction;
	}
	
	public final long getPaidUntil()
	{
		return _paidUntil;
	}
	
	public void setPaidUntil(long paidUntil)
	{
		_paidUntil = paidUntil;
	}
	
	public final boolean getPaid()
	{
		return _isPaid;
	}
	
	public void setPaid(boolean isPaid)
	{
		_isPaid = isPaid;
	}
	
	public ClanHallZone getZone()
	{
		return _zone;
	}
	
	public void setZone(ClanHallZone zone)
	{
		_zone = zone;
	}
	
	/**
	 * @return the {@link List} of all {@link ClanHallFunction}s this {@link ClanHall} owns.
	 */
	public final Map<Integer, ClanHallFunction> getFunctions()
	{
		return _functions;
	}
	
	/**
	 * @param type : The type of {@link ClanHallFunction} we search.
	 * @return the {@link ClanHallFunction} associated to the type.
	 */
	public ClanHallFunction getFunction(int type)
	{
		return _functions.get(type);
	}
	
	/**
	 * Free this {@link ClanHall}.
	 * <ul>
	 * <li>Remove the {@link ClanHall} from the {@link Clan}.</li>
	 * <li>Reset all variables to default.</li>
	 * <li>Delete {@link ClanHallFunction}s, and update the database.</li>
	 * </ul>
	 */
	public void free()
	{
		if (_feeTask != null)
		{
			_feeTask.cancel(false);
			_feeTask = null;
		}
		
		final Clan clan = ClanTable.getInstance().getClan(_ownerId);
		if (clan != null)
		{
			clan.setClanHallId(0);
			
			clan.broadcastToMembers(new PledgeShowInfoUpdate(clan));
		}
		
		_ownerId = 0;
		_paidUntil = 0;
		_isPaid = false;
		
		removeAllFunctions();
		
		closeDoors();
		
		if (_auction != null)
		{
			_auction.removeBids(null);
			
			_auction.reset(true);
			
			_auction.startAutoTask();
		}
		
		updateDb();
	}
	
	/**
	 * Set {@link ClanHall} {@link Clan} owner. If previous owner was existing, do some actions on it.
	 * @param clan : The new {@link ClanHall} owner.
	 */
	public void setOwner(Clan clan)
	{
		if (_auction != null)
		{
			_auction.removeBids(clan);
			
			_auction.reset(false);
		}
		
		if (clan == null)
		{
			if (_auction != null)
				_auction.startAutoTask();
			
			return;
		}
		
		final Clan owner = ClanTable.getInstance().getClan(_ownerId);
		if (owner != null)
		{
			owner.setClanHallId(0);
			
			owner.broadcastToMembers(new PledgeShowInfoUpdate(owner));
		}
		
		removeAllFunctions();
		
		closeDoors();
		
		clan.setClanHallId(_id);
		
		_ownerId = clan.getClanId();
		_paidUntil = System.currentTimeMillis() + ONE_WEEK;
		_isPaid = true;
		
		initializeFeeTask();
		
		clan.broadcastToMembers(new PledgeShowInfoUpdate(clan));
		
		banishForeigners();
		
		updateDb();
	}
	
	/**
	 * Banish all {@link Player}s from that {@link ClanHall} zone.
	 */
	public void banishForeigners()
	{
		if (_zone != null)
			_zone.banishForeigners(getOwnerId());
	}
	
	/**
	 * Remove all {@link ClanHallFunction}s linked to this {@link ClanHall}.
	 */
	public void removeAllFunctions()
	{
		_functions.clear();
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_FUNCTIONS))
		{
			ps.setInt(1, getId());
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't delete all clan hall functions.", e);
		}
	}
	
	/**
	 * Update a {@link ClanHallFunction} linked to this {@link ClanHall}. If it doesn't exist, generate it and save it on database.
	 * @param player : The {@link Player} who requested the change.
	 * @param type : The type of {@link ClanHallFunction} to update.
	 * @param lvl : The new level to set.
	 * @param lease : The associated lease taken from {@link Player} inventory.
	 * @param rate : The new rate to set.
	 * @return True if the {@link ClanHallFunction} has been successfully updated.
	 */
	public boolean updateFunction(Player player, int type, int lvl, int lease, long rate)
	{
		if (player == null)
			return false;
		
		if (lease > 0 && !player.destroyItemByItemId(57, lease, true))
			return false;
		
		ClanHallFunction chf = _functions.get(type);
		if (chf == null)
		{
			chf = new ClanHallFunction(this, type, lvl, lease, rate, System.currentTimeMillis() + rate);
			chf.dbSave();
			
			_functions.put(type, chf);
			
			return true;
		}
		
		if (lvl == 0 && lease == 0)
			chf.removeFunction();
		else
			chf.refreshFunction(lease, lvl);
		
		return true;
	}
	
	/**
	 * Save all related informations of this {@link ClanHall} into database.
	 */
	public void updateDb()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_CH))
		{
			ps.setInt(1, _ownerId);
			ps.setLong(2, _paidUntil);
			ps.setInt(3, (_isPaid) ? 1 : 0);
			
			if (_auction != null)
			{
				if (_auction.getSeller() != null)
				{
					ps.setInt(4, _auction.getSeller().getBid());
					ps.setString(5, _auction.getSeller().getName());
					ps.setString(6, _auction.getSeller().getClanName());
				}
				else
				{
					ps.setInt(4, 0);
					ps.setString(5, "");
					ps.setString(6, "");
				}
				ps.setLong(7, _auction.getEndDate());
			}
			else
			{
				ps.setInt(4, 0);
				ps.setString(5, "");
				ps.setString(6, "");
				ps.setLong(7, 0);
			}
			ps.setInt(8, _id);
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't update clan hall.", e);
		}
	}
	
	/**
	 * Initialize Fee Task.
	 */
	public void initializeFeeTask()
	{
		if (_feeTask != null)
			_feeTask.cancel(false);
		
		long time = System.currentTimeMillis();
		
		time = (_paidUntil > time) ? _paidUntil - time : 0;
		
		_feeTask = ThreadPool.schedule(this::payFee, time);
	}
	
	private void payFee()
	{
		if (isFree())
			return;
		
		final Clan clan = ClanTable.getInstance().getClan(getOwnerId());
		if (clan == null)
		{
			free();
			return;
		}
		
		if (clan.getWarehouse().getAdena() >= getLease())
		{
			clan.getWarehouse().destroyItemByItemId(57, getLease());
			
			_feeTask = ThreadPool.schedule(this::payFee, ONE_WEEK);
			
			_paidUntil += ONE_WEEK;
			_isPaid = true;
			
			updateDb();
		}
		else if (_isPaid)
		{
			_feeTask = ThreadPool.schedule(this::payFee, ONE_DAY);
			
			_paidUntil += ONE_DAY;
			_isPaid = false;
			
			updateDb();
			
			clan.broadcastToMembers(SystemMessage.getSystemMessage(SystemMessageId.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW).addNumber(getLease()));
		}
		else
		{
			free();
			
			clan.broadcastToMembers(SystemMessage.getSystemMessage(SystemMessageId.THE_CLAN_HALL_FEE_IS_ONE_WEEK_OVERDUE_THEREFORE_THE_CLAN_HALL_OWNERSHIP_HAS_BEEN_REVOKED));
		}
	}
	
	public void addBuff(int buffId, int buffLvl, String buffDesc, int buffPrice)
	{
		Buff buff = new Buff(buffId, buffLvl, buffDesc, buffPrice);
		_buffs.add(buff);
	}
	
	public List<Buff> getBuffs()
	{
		return _buffs;
	}
	
	public record Buff(int id, int lvl, String desc, int price)
	{
	}
}
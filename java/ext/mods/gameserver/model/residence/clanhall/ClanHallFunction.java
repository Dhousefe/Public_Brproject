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
import java.util.concurrent.ScheduledFuture;

import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ConnectionPool;
import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.data.sql.ClanTable;
import ext.mods.gameserver.model.pledge.Clan;

/**
 * Players can add a variety of decorations and functions to their {@link ClanHall}.
 * <ul>
 * <li>1) Recovery Facilities (Higher levels can be chosen for higher-grade houses)
 * <ul>
 * <li>Fireplace: Same as existing HP recovery ability.</li>
 * <li>Carpet: Same as existing MP recovery ability.</li>
 * <li>Chandelier: When a character dies and returns to the clan house, a fixed % of experience is recovered.</li>
 * </ul>
 * </li>
 * <li>2) Miscellaneous Equipment
 * <ul>
 * <li>Mirror: Allows teleportation to nearby regions and hunting areas.
 * <li>Interior curtains: Offer a variety of buffs through Clan Hall manager NPC. Higher levels available for higher-grade houses.
 * <li>Magic Machine: For B-grade and higher houses, item production is available by grade.</li>
 * </ul>
 * </li>
 * <li>3) Decorative Additions
 * <ul>
 * <li>Curtain, Foyer: These are decorations to make the house look more beautiful.</li>
 * </ul>
 * </li>
 * </ul>
 */
public class ClanHallFunction
{
	private static final CLogger LOGGER = new CLogger(ClanHallFunction.class.getName());
	
	private static final String UPDATE_FUNCTION = "REPLACE INTO clanhall_functions (hall_id, type, lvl, lease, rate, endTime) VALUES (?,?,?,?,?,?)";
	private static final String DELETE_FUNCTION = "DELETE FROM clanhall_functions WHERE hall_id=? AND type=?";
	
	private final ClanHall _ch;
	private final int _type;
	private final long _rate;
	
	private ScheduledFuture<?> _feeTask;
	private int _lvl;
	private int _fee;
	private long _endDate;
	
	public ClanHallFunction(ClanHall ch, int type, int lvl, int fee, long rate, long endDate)
    {
        _ch = ch;
        _type = type;
        _lvl = lvl;
        _fee = fee;
        _rate = rate;
        _endDate = endDate;
        
        if (_ch.isFree())
            return;
            
        // PROTEÇÃO
        if (_rate <= 0)
        {
            LOGGER.warn("ClanHall: Hall ID " + _ch.getId() + " has an invalid rate (" + _rate + "ms) for function type " + _type + ". Task cancelled to prevent DB spam.");
            return;
        }
        
        final long currentTime = System.currentTimeMillis();
        if (_endDate > currentTime)
            _feeTask = ThreadPool.scheduleIO(this::payFunctionFee, _endDate - currentTime);
        else
            ThreadPool.executeIO(this::payFunctionFee);
    }
	
	public int getType()
	{
		return _type;
	}
	
	public int getLvl()
	{
		return _lvl;
	}
	
	public int getFuncLvl()
	{
		int funcLevel = 0;
		
		switch (_type)
		{
			case ClanHall.FUNC_RESTORE_HP:
				funcLevel = _lvl / 20;
				break;
			case ClanHall.FUNC_RESTORE_MP, ClanHall.FUNC_RESTORE_EXP:
				funcLevel = _lvl / 5;
				break;
			case ClanHall.FUNC_TELEPORT, ClanHall.FUNC_DECO_CURTAINS, ClanHall.FUNC_SUPPORT, ClanHall.FUNC_DECO_FRONTPLATEFORM, ClanHall.FUNC_ITEM_CREATE:
				funcLevel = _lvl;
				break;
			default:
				return 0;
		}
		
		if (_ch instanceof SiegableHall && funcLevel > 0)
			funcLevel += 10;
		
		return funcLevel;
	}
	
	public int getLease()
	{
		return _fee;
	}
	
	public long getRate()
	{
		return _rate;
	}
	
	public long getEndTime()
	{
		return _endDate;
	}
	
	public void refreshEndTime()
	{
		_endDate = System.currentTimeMillis() + getRate();
	}
	
	/**
	 * Stop the current fee task.
	 */
	public void stopFeeTask()
	{
		if (_feeTask != null)
		{
			_feeTask.cancel(false);
			_feeTask = null;
		}
	}
	
	/**
	 * Save all {@link ClanHallFunction} variables into the database.
	 */
	public void dbSave()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_FUNCTION))
		{
			ps.setInt(1, _ch.getId());
			ps.setInt(2, getType());
			ps.setInt(3, getLvl());
			ps.setInt(4, getLease());
			ps.setLong(5, getRate());
			ps.setLong(6, getEndTime());
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't save clan hall function.", e);
		}
	}
	
	/**
	 * Remove this {@link ClanHallFunction} from its linked {@link ClanHall}.
	 */
	public void removeFunction()
	{
		stopFeeTask();
		
		_ch.getFunctions().remove(getType());
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_FUNCTION))
		{
			ps.setInt(1, _ch.getId());
			ps.setInt(2, getType());
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't remove clan hall function.", e);
		}
	}
	
	/**
	 * Refresh this {@link ClanHallFunction} fee and level. Also refresh the end date, and cancel old fee task to renew it.
	 * @param fee : The new fee which will be taken during fee task.
	 * @param lvl : The new level of the ClanHallFunction.
	 */
	public void refreshFunction(int fee, int lvl)
    {
        stopFeeTask();
        
        _fee = fee;
        _lvl = lvl;
        refreshEndTime();
        
        dbSave();
        
        // PROTEÇÃO
        if (_rate > 0)
        {
            _feeTask = ThreadPool.scheduleIO(this::payFunctionFee, getRate());
        }
    }
	
	/**
	 * Checks clan warehouse content, then remove this {@link ClanHallFunction} if the fee can't be paid.<br>
	 * <br>
	 * If paid, the fee end date is refreshed, and another task is scheduled.
	 */
	private void payFunctionFee()
	{
		if (_ch.isFree())
			return;
		
		if (getRate() < 60000)
			{
				LOGGER.warn("ClanHall: Hall ID " + _ch.getId() + " function type " + _type + " has extremely low rate (" + getRate() + "ms). Removing function to stop DB spam.");
				removeFunction();
				return;
			}

		final Clan clan = ClanTable.getInstance().getClan(_ch.getOwnerId());
		if (clan != null && clan.getWarehouse().getAdena() >= _fee)
		{
			clan.getWarehouse().destroyItemByItemId(57, _fee);
			
			refreshEndTime();
			
			dbSave();
			
			_feeTask = ThreadPool.scheduleIO(this::payFunctionFee, getRate());
		}
		else
			removeFunction();
	}
}
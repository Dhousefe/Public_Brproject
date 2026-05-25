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
package ext.mods.gameserver.communitybbs.custom.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import ext.mods.commons.lang.StringUtil;
import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ConnectionPool;

import ext.mods.Config;
import ext.mods.gameserver.communitybbs.custom.AuctionBBSManager;
import ext.mods.gameserver.custom.data.AuctionCurrencies;
import ext.mods.gameserver.data.sql.PlayerInfoTable;
import ext.mods.gameserver.data.xml.ItemData;
import ext.mods.gameserver.enums.items.CrystalType;
import ext.mods.gameserver.idfactory.IdFactory;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.Armor;
import ext.mods.gameserver.model.item.kind.EtcItem;
import ext.mods.gameserver.model.item.kind.Item;
import ext.mods.gameserver.model.item.kind.Weapon;
import ext.mods.gameserver.network.SystemMessageId;

public class Auction
{
	protected static final CLogger LOGGER = new CLogger(Auction.class.getName());
	
	private static final String INSERT_AUCTION = "INSERT INTO bbs_auction (id,obj_Id,item_id,item_count,item_enchant,price_id,price_count,duration) VALUES (?,?,?,?,?,?,?,?)";
	private static final String DELETE_AUCTION = "DELETE FROM bbs_auction WHERE id=?";
	private static final String UPDATE_AUCTION_ITEMCOUNT = "UPDATE bbs_auction SET item_count=? WHERE id=?";
	private static final String UPDATE_AUCTION_DURATION = "UPDATE bbs_auction SET duration=? WHERE id=?";
	
	private static final String SELECT_INVENTORY = "SELECT count FROM items WHERE owner_id=? AND item_id=?";
	private static final String UPDATE_INVENTORY = "UPDATE items SET count=? WHERE owner_id=? AND item_id=?";
	private static final String INSERT_INVENTORY = "INSERT INTO items VALUES (?,?,?,?,?,?,?,?,?,?,?)";
	
	private final int _id;
	private final int _objId;
	private final int _itemId;
	private final int _itemEnchant;
	
	private final int _priceId;
	private final int _priceCount;
	
	private int _itemCount;
	private long _duration;
	
	public Auction(ResultSet rs) throws SQLException
	{
		_id = rs.getInt("id");
		_objId = rs.getInt("obj_Id");
		_itemId = rs.getInt("item_id");
		_itemCount = rs.getInt("item_count");
		_itemEnchant = rs.getInt("item_enchant");
		
		_priceId = rs.getInt("price_id");
		_priceCount = rs.getInt("price_count");
		_duration = rs.getLong("duration");
	}
	
	public Auction(int objId, int itemId, int itemCount, int itemEnchant, int priceId, int priceCount)
	{
		_id = AuctionBBSManager.getInstance().nextId();
		_objId = objId;
		_itemId = itemId;
		_itemCount = itemCount;
		_itemEnchant = itemEnchant;
		
		_priceId = priceId;
		_priceCount = priceCount;
		_duration = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7);
	}
	
	public final int getId()
	{
		return _id;
	}
	
	public final int getObjectId()
	{
		return _objId;
	}
	
	public final String getPlayerName()
	{
		return PlayerInfoTable.getInstance().getPlayerName(_objId);
	}
	
	public final int getItemId()
	{
		return _itemId;
	}
	
	public final String getItemName()
	{
		return ItemData.getInstance().getTemplate(_itemId).getName();
	}
	
	public final Item getItem()
	{
		return ItemData.getInstance().getTemplate(_itemId);
	}
	
	public final int getItemCount()
	{
		return _itemCount;
	}
	
	public final int getItemEnchant()
	{
		return _itemEnchant;
	}
	
	public final int getPriceId()
	{
		return _priceId;
	}
	
	public final Item getPrice()
	{
		return ItemData.getInstance().getTemplate(_priceId);
	}
	
	public final int getPriceCount()
	{
	    return _priceCount;
	}

	public final long getDuration()
	{
		return _duration;
	}
	
	public final boolean hasExpire()
	{
		return _duration - System.currentTimeMillis() <= 0;
	}
	
	public final String getGradeIcon()
	{
		switch (getItem().getCrystalType())
		{
			case S:
				return "<img src=symbol.grade_s width=14 height=14>";
			case A:
				return "<img src=symbol.grade_a width=14 height=14>";
			case B:
				return "<img src=symbol.grade_b width=14 height=14>";
			case C:
				return "<img src=symbol.grade_c width=14 height=14>";
			case D:
				return "<img src=symbol.grade_d width=14 height=14>";
		}
		return "<img height=14>";
	}
	
	public final boolean filter(Function function)
	{
		if (_duration < System.currentTimeMillis())
			return false;
		
		final Item item = getItem();
		if (function.isItemType("Weapon") && !(item instanceof Weapon))
			return false;
		
		if (function.isItemType("Armor") && (!(item instanceof Armor) || item.isJewel()))
			return false;
		
		if (function.isItemType("Other") && (!(item instanceof EtcItem) || item.isShot() || item.isEnchantScroll()))
			return false;
		
		if (function.isItemType("Jewel") && !item.isJewel())
			return false;
		
		if (function.isItemType("Soulshot/Spiritshot") && !item.isShot())
			return false;
		
		if (function.isItemType("Enchant") && !item.isEnchantScroll())
			return false;
		
		String itemGrade = function.getItemGrade().toUpperCase();

		if (itemGrade.equals("S-GRADE"))
		    itemGrade = "S";
		else if (itemGrade.equals("A-GRADE"))
		    itemGrade = "A";
		else if (itemGrade.equals("B-GRADE"))
		    itemGrade = "B";
		else if (itemGrade.equals("C-GRADE"))
		    itemGrade = "C";
		else if (itemGrade.equals("D-GRADE"))
		    itemGrade = "D";
		else if (itemGrade.equals("NO-GRADE"))
		    itemGrade = "NONE";
		
	    if (!function.isItemGrade("All") && CrystalType.valueOf(itemGrade) != item.getCrystalType())
	        return false;
		
		if (!function.getSearch().isBlank() && !StringUtil.matches(getItemName(), function.getSearch()))
			return false;
		
		if (!isCurrency(function.getCurrency()))
			return false;
		
		return true;
	}
	
	public final boolean isCurrency(String currency)
	{
	    if (currency.equalsIgnoreCase("All"))
	        return true;
	    
		final int currencyId = AuctionCurrencies.getInstance().getCurrencyId(currency.replace(" ", ""));
	    
	    if (currencyId == -1)
	        return false;
	    
	    return _priceId == currencyId;
	}
	
	public void updateDuration()
	{
		final Player player = World.getInstance().getPlayer(_objId);
		if (player == null)
			return;
		
		if ((_duration - System.currentTimeMillis()) > TimeUnit.DAYS.toDays(1))
		{
			player.sendMessage(player.getSysString(10_211));
			return;
		}
		
		if (!player.destroyItemByItemId(Config.AUCTION_ITEM_FEE, Config.AUCTION_FEE, true))
			return;
		
		_duration = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7);
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_AUCTION_DURATION))
		{
			ps.setLong(1, _duration);
			ps.setInt(2, _id);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.warn("Couldn't update auction house duration '{}'.", e, _id);
		}
		
		player.sendMessage(player.getSysString(10_212, new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(_duration)));
	}
	
	public boolean tryPurchase(Player player, int count)
	{
		if (count == 0)
		{
			player.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
			return false;
		}
		
		if (count > _itemCount)
		{
			player.sendMessage(player.getSysString(10_213, getPlayerName()));
			return false;
		}
		
		final int totalCost = (count * _priceCount);
		if (!player.destroyItemByItemId(_priceId, totalCost, true))
			return false;
		
		_itemCount -= count;
		
		player.sendMessage(player.getSysString(10_214));
		final ItemInstance item = player.addItem(_itemId, count, true);
		item.setEnchantLevel(_itemEnchant, null);
		
		try (Connection con = ConnectionPool.getConnection())
		{
			final Player owner = World.getInstance().getPlayer(_objId);
			if (owner == null)
			{
				try (PreparedStatement ps = con.prepareStatement(SELECT_INVENTORY))
				{
					ps.setInt(1, _objId);
					ps.setInt(2, _priceId);
					try (ResultSet rs = ps.executeQuery())
					{
						if (rs.next() && getItem().isStackable())
						{
							try (PreparedStatement update = con.prepareStatement(UPDATE_INVENTORY))
							{
								update.setInt(1, rs.getInt("count") + totalCost);
								update.setInt(2, _objId);
								update.setInt(3, _priceId);
								update.execute();
							}
						}
						else
						{
							try (PreparedStatement insert = con.prepareStatement(INSERT_INVENTORY))
							{
								insert.setInt(1, _objId);
								insert.setInt(2, IdFactory.getInstance().getNextId());
								insert.setInt(3, _priceId);
								insert.setInt(4, totalCost);
								insert.setInt(5, 0);
								insert.setString(6, "INVENTORY");
								insert.setInt(7, 0);
								insert.setInt(8, 0);
								insert.setInt(9, 0);
								insert.setInt(10, -60);
								insert.setLong(11, System.currentTimeMillis());
								insert.execute();
								
							}
						}
					}
				}
			}
			else
			{
				owner.sendMessage(player.getSysString(10_215));
				owner.addItem(_priceId, totalCost, true);
			}
			
			if (_itemCount > 0)
			{
				try (PreparedStatement ps = con.prepareStatement(UPDATE_AUCTION_ITEMCOUNT))
				{
					ps.setInt(1, _itemCount);
					ps.setInt(2, _id);
					ps.execute();
				}
			}
			else
			{
				try (PreparedStatement ps = con.prepareStatement(DELETE_AUCTION))
				{
					ps.setInt(1, _id);
					ps.execute();
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.warn("{} couldn't purchase auction house item '{}' (count: {}).", e, player.getName(), _id, count);
		}
		return true;
	}
	
	public void refund()
	{
		final Player player = World.getInstance().getPlayer(_objId);
		if (player == null)
			return;
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_AUCTION))
		{
			ps.setInt(1, _id);
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.warn("Couldn't refound auction house item '{}'.", e, _id);
			return;
		}
		
		player.sendMessage(player.getSysString(10_216));
		final ItemInstance item = player.addItem(_itemId, _itemCount, true);
		item.setEnchantLevel(_itemEnchant, null);
		
		_itemCount = 0;
	}
	
	public void store()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(INSERT_AUCTION))
		{
			ps.setInt(1, _id);
			ps.setInt(2, _objId);
			ps.setInt(3, _itemId);
			ps.setInt(4, _itemCount);
			ps.setInt(5, _itemEnchant);
			ps.setInt(6, _priceId);
			ps.setInt(7, _priceCount);
			ps.setLong(8, _duration);
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.warn("Couldn't store auction house item '{}'.", e, _id);
		}
	}
}
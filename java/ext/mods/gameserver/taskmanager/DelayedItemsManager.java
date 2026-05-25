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
package ext.mods.gameserver.taskmanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ConnectionPool;
import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.data.xml.ItemData;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.Item;

public class DelayedItemsManager implements Runnable
{
	private static final CLogger LOGGER = new CLogger(DelayedItemsManager.class.getName());
	
	private static final String SELECT = "SELECT * FROM items_delayed WHERE payment_status = 0";
	
	private DelayedItemsManager()
	{
		ThreadPool.scheduleAtFixedRate(this, 60000L, 60000L);
	}
	
	@Override
	public void run()
	{
		try (Connection con = ConnectionPool.getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement(SELECT);
				ResultSet rset = ps.executeQuery())
			{
				while (rset.next())
				{
					final Player player = World.getInstance().getPlayer(rset.getInt("owner_id"));
					if (player != null && player.isOnline())
					{
						final int itemId = rset.getInt("item_id");
						final int count = rset.getInt("count");
						final int enchant = rset.getInt("enchant_level");
						final Item giveItem = ItemData.getInstance().getTemplate(itemId);
						if (giveItem != null)
						{
							final ItemInstance item = player.addItem(itemId, count, true);
							if (item != null && enchant > 0)
								item.setEnchantLevel(enchant, player);
							
							updateDonation(player.getObjectId(), itemId, count, enchant);
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.warn("DelayedItemsManager: " + e);
		}
	}
	
	private static void updateDonation(int objId, int id, long count, int enchant)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement statement = con.prepareStatement("DELETE FROM items_delayed WHERE owner_id=? AND item_id=? AND count=? AND enchant_level=?;"))
		{
			statement.setInt(1, objId);
			statement.setInt(2, id);
			statement.setLong(3, count);
			statement.setInt(4, enchant);
			statement.execute();
		}
		catch (SQLException e)
		{
			LOGGER.warn("Failed to remove unitpay_payments from database id: " + id);
		}
	}
	
	public static final DelayedItemsManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static final class SingletonHolder
	{
		protected static final DelayedItemsManager INSTANCE = new DelayedItemsManager();
	}
}
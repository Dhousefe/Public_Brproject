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
package ext.mods.email.items;

import java.sql.Connection;
import java.sql.PreparedStatement;

import ext.mods.commons.pool.ConnectionPool;

import ext.mods.gameserver.model.item.instance.ItemInstance;

public class EmailStorage
{
	
	public static void saveEmail(int emailId, int senderId, int targetId, ItemInstance item, boolean isPaid, int paymentItemId, int paymentItemCount, long expirationTime)
	{
		try (Connection con = ConnectionPool.getConnection())
		{
			PreparedStatement ps = con.prepareStatement("INSERT INTO player_emails (email_id, sender_id, target_id, item_object_id, item_id, count, enchant_level, is_augmented, augment_id, is_paid, payment_item_id, payment_item_count, expiration_time, created_time) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			
			ps.setInt(1, emailId);
			ps.setInt(2, senderId);
			ps.setInt(3, targetId);
			ps.setInt(4, item.getObjectId());
			ps.setInt(5, item.getItemId());
			ps.setInt(6, item.getCount());
			ps.setInt(7, item.getEnchantLevel());
			ps.setBoolean(8, item.isAugmented());
			ps.setInt(9, item.isAugmented() ? item.getAugmentation().getId() : 0);
			ps.setBoolean(10, isPaid);
			ps.setObject(11, isPaid ? paymentItemId : null);
			ps.setObject(12, isPaid ? paymentItemCount : null);
			ps.setLong(13, expirationTime);
			ps.setLong(14, System.currentTimeMillis());
			
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
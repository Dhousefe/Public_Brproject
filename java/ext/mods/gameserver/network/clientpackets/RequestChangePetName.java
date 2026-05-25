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
package ext.mods.gameserver.network.clientpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import ext.mods.commons.lang.StringUtil;
import ext.mods.commons.pool.ConnectionPool;

import ext.mods.Config;
import ext.mods.gameserver.data.xml.NpcData;
import ext.mods.gameserver.enums.items.ItemState;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Pet;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.SystemMessageId;

public final class RequestChangePetName extends L2GameClientPacket
{
	private static final String SEARCH_NAME = "SELECT name FROM pets WHERE name=?";
	
	private String _name;
	
	@Override
	protected void readImpl()
	{
		_name = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (!player.hasPet())
			return;
		
		if (_name.length() < 1 || _name.length() > 16)
		{
			player.sendPacket(SystemMessageId.NAMING_CHARNAME_UP_TO_16CHARS);
			return;
		}
		
		final Pet pet = (Pet) player.getSummon();
		if (pet.getName() != null)
		{
			player.sendPacket(SystemMessageId.NAMING_YOU_CANNOT_SET_NAME_OF_THE_PET);
			return;
		}
		
		if (!StringUtil.isValidString(_name, Config.PET_NAME_TEMPLATE))
		{
			player.sendPacket(SystemMessageId.NAMING_PETNAME_CONTAINS_INVALID_CHARS);
			return;
		}
		
		if (NpcData.getInstance().getTemplateByName(_name) != null)
			return;
		
		if (doesPetNameExist(_name))
		{
			player.sendPacket(SystemMessageId.NAMING_ALREADY_IN_USE_BY_ANOTHER_PET);
			return;
		}
		
		pet.setName(_name);
		
		final ItemInstance controlItem = pet.getControlItem();
		if (controlItem != null)
		{
			controlItem.setCustomType2(1);
			controlItem.updateState(player, ItemState.MODIFIED);
		}
		
		pet.sendPetInfosToOwner();
	}
	
	/**
	 * @param name : The name to search.
	 * @return true if such name already exists on database, false otherwise.
	 */
	private static boolean doesPetNameExist(String name)
	{
		boolean result = true;
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(SEARCH_NAME))
		{
			ps.setString(1, name);
			
			try (ResultSet rs = ps.executeQuery())
			{
				result = rs.next();
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't check existing petname.", e);
		}
		return result;
	}
}
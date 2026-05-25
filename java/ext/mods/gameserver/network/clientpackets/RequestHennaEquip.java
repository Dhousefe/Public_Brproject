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

import ext.mods.gameserver.data.xml.HennaData;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.records.Henna;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.HennaInfo;
import ext.mods.gameserver.network.serverpackets.UserInfo;

public final class RequestHennaEquip extends L2GameClientPacket
{
	private int _symbolId;
	
	@Override
	protected void readImpl()
	{
		_symbolId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final Henna henna = HennaData.getInstance().getHenna(_symbolId);
		if (henna == null)
			return;
		
		if (!henna.canBeUsedBy(player))
		{
			player.sendPacket(SystemMessageId.CANT_DRAW_SYMBOL);
			return;
		}
		
		if (player.getHennaList().isFull())
		{
			player.sendPacket(SystemMessageId.SYMBOLS_FULL);
			return;
		}
		
		final ItemInstance ownedDyes = player.getInventory().getItemByItemId(henna.dyeId());
		final int count = (ownedDyes == null) ? 0 : ownedDyes.getCount();
		
		if (count < Henna.DRAW_AMOUNT)
		{
			player.sendPacket(SystemMessageId.CANT_DRAW_SYMBOL);
			return;
		}
		
		if (!player.reduceAdena(henna.drawPrice(), true))
			return;
		
		if (!player.destroyItemByItemId(henna.dyeId(), Henna.DRAW_AMOUNT, true))
			return;
		
		final boolean success = player.getHennaList().add(henna);
		if (success)
		{
			player.sendPacket(new HennaInfo(player));
			player.sendPacket(new UserInfo(player));
			player.sendPacket(SystemMessageId.SYMBOL_ADDED);
		}
	}
}
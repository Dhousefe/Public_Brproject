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

import java.util.ArrayList;
import java.util.List;

import ext.mods.Config;
import ext.mods.gameserver.data.manager.CastleManorManager;
import ext.mods.gameserver.enums.PrivilegeType;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.manor.CropProcure;
import ext.mods.gameserver.model.manor.Seed;
import ext.mods.gameserver.network.serverpackets.ActionFailed;

public class RequestSetCrop extends L2GameClientPacket
{
	private static final int BATCH_LENGTH = 13;
	
	private int _manorId;
	private List<CropProcure> _items;
	
	@Override
	protected void readImpl()
	{
		_manorId = readD();
		final int count = readD();
		if (count <= 0 || count > Config.MAX_ITEM_IN_PACKET || (count * BATCH_LENGTH) != _buf.remaining())
			return;
		
		_items = new ArrayList<>(count);
		for (int i = 0; i < count; i++)
		{
			final int itemId = readD();
			final int sales = readD();
			final int price = readD();
			final int type = readC();
			
			if (itemId < 1 || sales < 0 || price < 0)
			{
				_items.clear();
				return;
			}
			
			if (sales > 0)
				_items.add(new CropProcure(itemId, sales, type, sales, price));
		}
	}
	
	@Override
	protected void runImpl()
	{
		if (_items.isEmpty())
			return;
		
		final CastleManorManager manor = CastleManorManager.getInstance();
		if (!manor.isModifiablePeriod())
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final Player player = getClient().getPlayer();
		if (player == null || player.getClan() == null || player.getClan().getCastleId() != _manorId || !player.hasClanPrivileges(PrivilegeType.CP_MANOR_ADMINISTRATION) || !player.getAI().canDoInteract(player.getCurrentFolk()))
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final List<CropProcure> list = new ArrayList<>(_items.size());
		for (CropProcure cp : _items)
		{
			final Seed s = manor.getSeedByCrop(cp.getId(), _manorId);
			if (s != null && cp.getStartAmount() <= s.getCropsLimit() && cp.getPrice() >= s.getCropMinPrice() && cp.getPrice() <= s.getCropMaxPrice())
				list.add(cp);
		}
		
		manor.setNextCropProcure(list, _manorId);
	}
}
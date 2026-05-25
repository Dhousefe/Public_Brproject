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

import ext.mods.extensions.listener.manager.PlayerListenerManager;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ExAutoSoulShot;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public final class RequestAutoSoulShot extends L2GameClientPacket
{
	private int _itemId;
	private int _type;
	
	@Override
	protected void readImpl()
	{
		_itemId = readD();
		_type = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (!player.isOperating() && player.getActiveRequester() == null && !player.isDead())
		{
			final ItemInstance item = player.getInventory().getItemByItemId(_itemId);
			if (item == null)
				return;
			
			if (_type == 1)
			{
				if (_itemId < 6535 || _itemId > 6540)
				{
					if (_itemId == 6645 || _itemId == 6646 || _itemId == 6647)
					{
						if (player.getSummon() != null)
						{
							if (_itemId == 6647 && player.isInOlympiadMode())
							{
								player.sendPacket(SystemMessageId.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
								return;
							}
							
							if (_itemId == 6645)
							{
								if (player.getSummon().getSoulShotsPerHit() > item.getCount())
								{
									player.sendPacket(SystemMessageId.NOT_ENOUGH_SOULSHOTS_FOR_PET);
									return;
								}
							}
							else
							{
								if (player.getSummon().getSpiritShotsPerHit() > item.getCount())
								{
									player.sendPacket(SystemMessageId.NOT_ENOUGH_SPIRITSHOTS_FOR_PET);
									return;
								}
							}
							
							player.addAutoSoulShot(_itemId);
							player.sendPacket(new ExAutoSoulShot(_itemId, _type));
							player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.USE_OF_S1_WILL_BE_AUTO).addItemName(_itemId));
							player.rechargeShots(true, true);
							player.getSummon().rechargeShots(true, true);
						}
						else
							player.sendPacket(SystemMessageId.NO_SERVITOR_CANNOT_AUTOMATE_USE);
					}
					else
					{
						if (_itemId >= 3947 && _itemId <= 3952 && player.isInOlympiadMode())
						{
							player.sendPacket(SystemMessageId.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
							return;
						}
						
						player.addAutoSoulShot(_itemId);
						player.sendPacket(new ExAutoSoulShot(_itemId, _type));
						
						if (player.getActiveWeaponInstance() != null && item.getItem().getCrystalType() == player.getActiveWeaponItem().getCrystalType())
							player.rechargeShots(true, true);
						else
						{
							if ((_itemId >= 2509 && _itemId <= 2514) || (_itemId >= 3947 && _itemId <= 3952) || _itemId == 5790)
								player.sendPacket(SystemMessageId.SPIRITSHOTS_GRADE_MISMATCH);
							else
								player.sendPacket(SystemMessageId.SOULSHOTS_GRADE_MISMATCH);
						}
						
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.USE_OF_S1_WILL_BE_AUTO).addItemName(_itemId));
					}
				}
				
				PlayerListenerManager.getInstance().notifyAutoSoulShot(player, _itemId, true);
			}
			else if (_type == 0)
			{
				player.removeAutoSoulShot(_itemId);
				player.sendPacket(new ExAutoSoulShot(_itemId, _type));
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.AUTO_USE_OF_S1_CANCELLED).addItemName(_itemId));

			}
		}
	}
}
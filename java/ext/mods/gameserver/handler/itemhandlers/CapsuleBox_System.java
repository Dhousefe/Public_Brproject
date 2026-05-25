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
package ext.mods.gameserver.handler.itemhandlers;

/**
 * @author Eli
 *
 */
import ext.mods.gameserver.handler.IItemHandler;
import ext.mods.gameserver.idfactory.IdFactory;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.serverpackets.MagicSkillUse;

import ext.mods.commons.random.Rnd;

import ext.mods.CapsuleBox.CapsuleBoxData;
import ext.mods.CapsuleBox.CapsuleBoxItem;
import ext.mods.CapsuleBox.CapsuleBoxItem.Item;

public class CapsuleBox_System implements IItemHandler {

    @Override
    public void useItem(Playable playable, ItemInstance item, boolean forceUse) {
        if (!(playable instanceof Player))
            return;

        final Player activeChar = (Player) playable;
        final int itemId = item.getItemId();

        CapsuleBoxItem capsuleBoxItem = CapsuleBoxData.getInstance().getCapsuleBoxItemById(itemId);
        if (capsuleBoxItem != null) {
            if (activeChar.getLevel() < capsuleBoxItem.getPlayerLevel()) {
                activeChar.sendMessage("Para Usar Esta Capsule Box Necesitas El LvL." + capsuleBoxItem.getPlayerLevel());
                return;
            }

            ItemInstance toGive = null;
            for (Item boxItem : capsuleBoxItem.getItems()) {
                toGive = new ItemInstance(IdFactory.getInstance().getNextId(), boxItem.getItemId());
                int random = Rnd.get(100);
                if (random < boxItem.getChance()) {
                    if (!toGive.isStackable()) {
                        toGive.setEnchantLevel(boxItem.getEnchantLevel(), playable);
                        activeChar.addItem(toGive, true);
                    } else {
                        activeChar.addItem(boxItem.getItemId(), boxItem.getAmount(), true);
                    }
                } else {
                    
                }
                MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
                activeChar.broadcastPacket(MSU);
               
            }
           
        } else {
            activeChar.sendMessage("This Capsule box expired or is invalid!");
        }

        playable.destroyItem(item.getObjectId(), 1, false);
    }
}
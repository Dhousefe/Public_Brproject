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
 */
import ext.mods.gameserver.handler.IItemHandler;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.serverpackets.ExShowScreenMessage;
import ext.mods.gameserver.network.serverpackets.MagicSkillUse;

public class ItemNobles implements IItemHandler
{
    public ItemNobles()
    {
        super();
    }

    @Override
    public void useItem(Playable playable, ItemInstance item, boolean forceUse)
    {
        if (!(playable instanceof Player))
            return;

        Player activeChar = (Player) playable;

        if (activeChar.isInOlympiadMode())
        {
            activeChar.sendMessage("This item cannot be used on Olympiad Games.");
            return;
        }

        if (activeChar.isNoble())
        {
            activeChar.sendMessage("You are already a noblesse.");
            return;
        }

        activeChar.broadcastPacket(new MagicSkillUse(activeChar, 5103, 1, 1000, 0));
        activeChar.sendPacket(new ExShowScreenMessage("Congratulations! You are a Noble!", 8000));
        activeChar.setNoble(true, true);
        activeChar.getInventory().addItem(7694, 1);
        activeChar.sendMessage("You are now a Noble, Check your Skills!");
        activeChar.getInventory().destroyItem(item.getObjectId(), 1);
        activeChar.broadcastUserInfo();

        activeChar = null;
    }
}
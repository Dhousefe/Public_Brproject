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
package ext.mods.fakeplayer.ai.addon;

import ext.mods.gameserver.handler.IItemHandler;
import ext.mods.gameserver.handler.ItemHandler;
import ext.mods.gameserver.model.item.instance.ItemInstance;

import ext.mods.fakeplayer.FakePlayer;

public interface IPotionUser
{
	
	default void handlePotions(FakePlayer fakePlayer, int cpPotionId, int hpPotionId, int mpPotionId)
	{
		if (fakePlayer == null || fakePlayer.isDead() || fakePlayer.isAllSkillsDisabled())
			return;
		
		final double maxCp = fakePlayer.getStatus().getMaxCp();
		final double currentCp = fakePlayer.getStatus().getCp();
		
		final double maxHp = fakePlayer.getStatus().getMaxHp();
		final double currentHp = fakePlayer.getStatus().getHp();
		
		final double maxMp = fakePlayer.getStatus().getMaxMp();
		final double currentMp = fakePlayer.getStatus().getMp();
		
		if ((currentCp / maxCp) < 0.90)
			usePotion(fakePlayer, cpPotionId);
		
		if ((currentHp / maxHp) < 0.50)
			usePotion(fakePlayer, hpPotionId);
		
		if ((currentMp / maxMp) < 0.30)
			usePotion(fakePlayer, mpPotionId);
	}
	
	default void usePotion(FakePlayer fakePlayer, int itemId)
	{
		ItemInstance potion = fakePlayer.getInventory().getItemByItemId(itemId);
		if (potion != null)
		{
		    IItemHandler handler = ItemHandler.getInstance().getHandler(potion.getEtcItem());
		    if (handler != null)
		    {
		        handler.useItem(fakePlayer, potion, false);
		    }
		}

	}
}
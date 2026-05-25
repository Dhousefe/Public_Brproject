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
package ext.mods.gameserver.model.multisell;

import java.util.ArrayList;
import java.util.LinkedList;

import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.Armor;
import ext.mods.gameserver.model.item.kind.Weapon;

/**
 * A dynamic layer of {@link ListContainer}, which holds the current {@link Npc} objectId for security reasons.<br>
 * <br>
 * It can also allow to check inventory content.
 */
public class CommunityBoardListContainer extends PreparedListContainer
{
	private int _npcObjectId = 0;
	
	public CommunityBoardListContainer(ListContainer template, boolean inventoryOnly, Player player, Npc npc)
	{
		super(template.getId());
		
		setMaintainEnchantment(template.getMaintainEnchantment());
		setApplyTaxes(false);
		
		double taxRate = 0;
		int npcId = 0;
		
		if (npc != null)
		{
			_npcObjectId = npc.getObjectId();
			npcId = npc.getNpcId();
			
			if (template.getApplyTaxes() && npc.getCastle() != null && npc.getCastle().getOwnerId() > 0)
			{
				setApplyTaxes(true);
				taxRate = npc.getCastle().getTaxRate();
			}
		}
		
		if (inventoryOnly)
		{
			if (player == null)
				return;
			
			_entries = new LinkedList<>();
			
			for (ItemInstance item : player.getInventory().getUniqueItems(getMaintainEnchantment(), false, false, false, npcId == 31760))
			{
				if (!item.isEquipped() && (item.getItem() instanceof Armor || item.getItem() instanceof Weapon))
				{
					for (Entry ent : template.getEntries())
					{
						for (Ingredient ing : ent.getIngredients())
						{
							if (item.getItemId() == ing.getItemId())
							{
								_entries.add(new PreparedEntry(ent, item, getApplyTaxes(), getMaintainEnchantment(), taxRate));
								break;
							}
						}
					}
				}
			}
		}
		else
		{
			_entries = new ArrayList<>(template.getEntries().size());
			
			for (Entry ent : template.getEntries())
				_entries.add(new PreparedEntry(ent, null, getApplyTaxes(), false, taxRate));
		}
	}
	
	@Override
	public final boolean checkNpcObjectId(int npcObjectId)
	{
		return _npcObjectId == 0 || _npcObjectId == npcObjectId;
	}
}
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
package ext.mods.gameserver.model.item.kind;

import ext.mods.commons.data.StatSet;

import ext.mods.gameserver.enums.items.EtcItemType;
import ext.mods.gameserver.model.itemcontainer.PcInventory;

/**
 * This class is dedicated to the management of EtcItem.
 */
public final class EtcItem extends Item
{
	private final String _handler;
	private final int _sharedReuseGroup;
	private EtcItemType _type;
	private final int _reuseDelay;
	
	public EtcItem(StatSet set)
	{
		super(set);
		
		_type = set.getEnum("etcitem_type", EtcItemType.class, EtcItemType.NONE);
		
		switch (getDefaultAction())
		{
			case soulshot:
			case summon_soulshot:
			case summon_spiritshot:
			case spiritshot:
				_type = EtcItemType.SHOT;
				break;
		}
		
		_type1 = Item.TYPE1_ITEM_QUESTITEM_ADENA;
		_type2 = Item.TYPE2_OTHER;
		
		if (isQuestItem())
			_type2 = Item.TYPE2_QUEST;
		else if (getItemId() == PcInventory.ADENA_ID || getItemId() == PcInventory.ANCIENT_ADENA_ID)
			_type2 = Item.TYPE2_MONEY;
		
		_handler = set.getString("handler", null);
		_sharedReuseGroup = set.getInteger("shared_reuse_group", -1);
		_reuseDelay = set.getInteger("reuse_delay", 0);
	}
	
	@Override
	public EtcItemType getItemType()
	{
		return _type;
	}
	
	@Override
	public final boolean isConsumable()
	{
		return ((getItemType() == EtcItemType.SHOT) || (getItemType() == EtcItemType.POTION));
	}
	
	@Override
	public int getItemMask()
	{
		return getItemType().mask();
	}
	
	/**
	 * @return the handler name, or null otherwise.
	 */
	public String getHandlerName()
	{
		return _handler;
	}
	
	public int getSharedReuseGroup()
	{
		return _sharedReuseGroup;
	}
	
	public int getReuseDelay()
	{
		return _reuseDelay;
	}
}
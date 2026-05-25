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
package ext.mods.gameserver.skills.basefuncs;

import ext.mods.Config;
import ext.mods.gameserver.enums.items.WeaponType;
import ext.mods.gameserver.enums.skills.Stats;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.skills.conditions.Condition;

/**
 * @see Func
 */
public class FuncEnchant extends Func
{
	public FuncEnchant(Object owner, Stats stat, double value, Condition cond)
	{
		super(owner, stat, 3, value, cond);
	}
	
	@Override
	public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value)
	{
		if (getCond() != null && !getCond().test(effector, effected, skill))
			return value;
		
		final ItemInstance item = (ItemInstance) getFuncOwner();
		
		int enchant = item.getEnchantLevel();
		if (enchant <= 0)
			return value;
		
		int overenchant = 0;
		if (enchant > 3)
		{
			overenchant = enchant - 3;
			enchant = 3;
		}
		
		if (effector != null && effector instanceof Player player)
		{
			if (player.isInOlympiadMode() && Config.OLY_ENCHANT_LIMIT >= 0 && (enchant + overenchant) > Config.OLY_ENCHANT_LIMIT)
			{
				if (Config.OLY_ENCHANT_LIMIT > 3)
					overenchant = Config.OLY_ENCHANT_LIMIT - 3;
				else
				{
					overenchant = 0;
					enchant = Config.OLY_ENCHANT_LIMIT;
				}
			}
		}
		
		if (getStat() == Stats.MAGIC_DEFENCE || getStat() == Stats.POWER_DEFENCE)
			return value + enchant + (3 * overenchant);
		
		if (getStat() == Stats.MAGIC_ATTACK)
		{
			switch (item.getItem().getCrystalType())
			{
				case S:
					value += (4 * enchant + 8 * overenchant);
					break;
				
				case A, B, C:
					value += (3 * enchant + 6 * overenchant);
					break;
				
				case D:
					value += (2 * enchant + 4 * overenchant);
					break;
			}
			return value;
		}
		
		if (item.isWeapon())
		{
			final WeaponType type = (WeaponType) item.getItemType();
			
			switch (item.getItem().getCrystalType())
			{
				case S:
					switch (type)
					{
						case BOW:
							value += (10 * enchant + 20 * overenchant);
							break;
						
						case BIGBLUNT, BIGSWORD, DUALFIST, DUAL:
							value += (6 * enchant + 12 * overenchant);
							break;
						
						default:
							value += (5 * enchant + 10 * overenchant);
							break;
					}
					break;
				
				case A:
					switch (type)
					{
						case BOW:
							value += (8 * enchant + 16 * overenchant);
							break;
						
						case BIGBLUNT, BIGSWORD, DUALFIST, DUAL:
							value += (5 * enchant + 10 * overenchant);
							break;
						
						default:
							value += (4 * enchant + 8 * overenchant);
							break;
					}
					break;
				
				case B:
				case C:
					switch (type)
					{
						case BOW:
							value += (6 * enchant + 12 * overenchant);
							break;
						
						case BIGBLUNT, BIGSWORD, DUALFIST, DUAL:
							value += (4 * enchant + 8 * overenchant);
							break;
						
						default:
							value += (3 * enchant + 6 * overenchant);
							break;
					}
					break;
				
				case D:
					switch (type)
					{
						case BOW:
							value += (4 * enchant + 8 * overenchant);
							break;
						
						default:
							value += (2 * enchant + 4 * overenchant);
							break;
					}
					break;
			}
		}
		return value;
	}
}
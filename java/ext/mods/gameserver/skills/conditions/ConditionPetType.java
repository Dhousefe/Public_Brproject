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
package ext.mods.gameserver.skills.conditions;

import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.instance.Pet;
import ext.mods.gameserver.model.item.kind.Item;
import ext.mods.gameserver.skills.L2Skill;


public class ConditionPetType extends Condition
{
	private final int petType;
	
	public ConditionPetType(int petType)
	{
		this.petType = petType;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item)
	{
		if (!(effector instanceof Pet pet))
			return false;
		
		int npcid = pet.getNpcId();
		
		if (isStrider(npcid) && (petType == Item.STRIDER))
			return true;
		else if (isHatchlingGroup(npcid) && (petType == Item.HATCHLING_GROUP))
			return true;
		else if (isAllWolfGroup(npcid) && (petType == Item.ALL_WOLF_GROUP))
			return true;
		else if (isBabyPetGroup(npcid) && (petType == Item.BABY_PET_GROUP))
			return true;
		else if (isItemEquipPetGroup(npcid) && (petType == Item.ITEM_EQUIP_PET_GROUP))
			return true;
		
		return false;
	}
	
	public static boolean isStrider(int npcId)
	{
		return (npcId >= 12526) && (npcId <= 12528);
	}
	
	public static boolean isHatchlingGroup(int npcId)
	{
		return (npcId >= 12311) && (npcId <= 12313);
	}
	
	public static boolean isAllWolfGroup(int npcId)
	{
		return (npcId == 12077);
	}
	
	public static boolean isBabyPetGroup(int npcId)
	{
		return (npcId >= 12780) && (npcId <= 12782);
	}
	
	public static boolean isItemEquipPetGroup(int npcId)
	{
		return (npcId == 12077) || ((npcId >= 12311) && (npcId <= 12313)) || ((npcId >= 12526) && (npcId <= 12528)) || ((npcId >= 12780) && (npcId <= 12782));
	}
}
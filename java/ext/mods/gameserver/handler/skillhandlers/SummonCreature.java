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
package ext.mods.gameserver.handler.skillhandlers;

import java.util.List;

import ext.mods.Config;
import ext.mods.Crypta.AgathionData;
import ext.mods.aghation.holder.AgathionHolder;
import ext.mods.gameserver.data.xml.NpcData;
import ext.mods.gameserver.data.xml.SummonItemData;
import ext.mods.gameserver.enums.items.ItemLocation;
import ext.mods.gameserver.enums.skills.SkillType;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.handler.ISkillHandler;
import ext.mods.gameserver.idfactory.IdFactory;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Agathion;
import ext.mods.gameserver.model.actor.instance.Pet;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.location.SpawnLocation;
import ext.mods.gameserver.skills.L2Skill;

public class SummonCreature implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.SUMMON_CREATURE
	};
	
	@Override
	public void useSkill(Creature creature, L2Skill skill, WorldObject[] targets, ItemInstance item)
	{
		if (!(creature instanceof Player player))
			return;
		
		final ItemInstance checkedItem = player.getInventory().getItemByObjectId(player.getAI().getCurrentIntention().getItemObjectId());
		if (checkedItem == null)
			return;
		
		if (checkedItem.getOwnerId() != player.getObjectId() || checkedItem.getLocation() != ItemLocation.INVENTORY)
			return;
		
		int npcId = 0;
		boolean isAgathion = false;
		
		Object agathionDataInstance = AgathionData.getInstance();
		if (agathionDataInstance != null)
		{
			try
			{
				@SuppressWarnings("unchecked")
				List<AgathionHolder> agathionList = (List<AgathionHolder>) AgathionData.getInstance().getAgathionsByItemId(checkedItem.getItemId());
				if (agathionList != null && !agathionList.isEmpty())
				{
					npcId = agathionList.get(0).getNpcId();
					isAgathion = true;
				}
			}
			catch (Exception e)
			{
			}
		}
		
		if (npcId == 0)
		{
			final IntIntHolder summonItem = SummonItemData.getInstance().getSummonItem(checkedItem.getItemId());
			if (summonItem != null)
			{
				npcId = summonItem.getId();
			}
		}
		
		if (npcId == 0)
			return;
		
		final NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(npcId);
		if (npcTemplate == null)
			return;
		
		final SpawnLocation spawnLoc = creature.getPosition().clone();
		spawnLoc.addStrictOffset(Config.SUMMON_DRIFT_RANGE);
		spawnLoc.setHeadingTo(creature.getPosition());
		spawnLoc.set(GeoEngine.getInstance().getValidLocation(creature, spawnLoc));
		
		if (isAgathion)
		{
			if (player.getCurrentAgation() != null)
				return;
			
			final Agathion agathion = new Agathion(IdFactory.getInstance().getNextId(), npcTemplate, player, checkedItem.getItemId());
			
			player.setCurrentAgation(agathion);
			player.getMemos().set("agation", checkedItem.getItemId());
			
			agathion.spawnMe(spawnLoc);
			agathion.setInstanceMap(player.getInstanceMap(), false);
			
			agathion.getAI().addFollowDesire(player, 1000);
			agathion.forceRunStance();
		}
		else
		{
			if (player.getSummon() != null || World.getInstance().getPet(player.getObjectId()) != null)
				return;
			
			final Pet pet = Pet.restore(checkedItem, npcTemplate, player);
			if (pet == null)
				return;
			
			World.getInstance().addPet(player.getObjectId(), pet);
			
			player.setSummon(pet);
			
			pet.forceRunStance();
			pet.setTitle(player.getName());
			pet.startFeed();
			pet.spawnMe(spawnLoc);
			pet.getAI().setFollowStatus(true);
		}
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}

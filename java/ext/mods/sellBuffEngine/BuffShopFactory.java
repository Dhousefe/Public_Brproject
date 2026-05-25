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
package ext.mods.sellBuffEngine;

import java.util.List;
import java.util.stream.Collectors;

import ext.mods.gameserver.data.sql.PlayerInfoTable;
import ext.mods.gameserver.data.xml.AdminData;
import ext.mods.gameserver.data.xml.PlayerData;
import ext.mods.gameserver.data.xml.PlayerLevelData;
import ext.mods.gameserver.idfactory.IdFactory;
import ext.mods.gameserver.model.AccessLevel;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.PlayerTemplate;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.records.PlayerLevel;
import ext.mods.gameserver.taskmanager.ItemInstanceTaskManager;

public final class BuffShopFactory
{
	private static class SingletonHolder
	{
		private static final BuffShopFactory _instance = new BuffShopFactory();
	}
	
	public static BuffShopFactory getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private BuffShopFactory()
	{
	}
	
	public Player createShopNpc(final Player creator, final ShopObject shopConfig)
	{
		populateShopConfigFromCreator(shopConfig, creator);
		final PlayerTemplate template = creator.getTemplate();
		final Player dummyNpc = new Player(IdFactory.getInstance().getNextId(), template, "BuffShop_" + creator.getObjectId(), creator.getAppearance());
		configureDummyBase(dummyNpc, creator.getName(), shopConfig.getTitle());
		configureNpcStats(dummyNpc, creator.getStatus().getMaxHp(), creator.getStatus().getMaxCp());
		equipGhostItems(dummyNpc, shopConfig.getEquippedItems());
		return dummyNpc;
	}
	
	public Player createShopNpc(final ShopObject shopConfig)
	{
		final PlayerTemplate template = PlayerData.getInstance().getTemplate(shopConfig.getClassId());
		if (template == null)
			return null;
		final Player dummyNpc = new Player(IdFactory.getInstance().getNextId(), template, "BuffShopOffline_" + shopConfig.getOwnerId(), shopConfig.getAppearance());
		String ownerName = PlayerInfoTable.getInstance().getPlayerName(shopConfig.getOwnerId());
		if (ownerName == null || ownerName.isEmpty())
			ownerName = "Buff Seller";
		configureDummyBase(dummyNpc, ownerName, shopConfig.getTitle());
		configureNpcStats(dummyNpc, template.getBaseHpMax(80), template.getBaseCpMax(80));
		equipGhostItems(dummyNpc, shopConfig.getEquippedItems());
		return dummyNpc;
	}
	
	private void configureDummyBase(Player npc, String name, String title)
	{
		npc.setDummy(true);
		npc.setName(name);
		npc.setTitle(title);
		AccessLevel defaultAccessLevel = AdminData.getInstance().getAccessLevel(0);
		if (defaultAccessLevel != null)
		{
			npc.setAccessLevel(defaultAccessLevel.getLevel());
		}
	}
	
	private void populateShopConfigFromCreator(ShopObject config, Player creator)
	{
		config.setClassId(creator.getClassId().getId());
		config.setAppearance(creator.getAppearance());
		config.setEquippedItems(creator.getInventory().getPaperdollItems().stream().map(ItemInstance::getItemId).collect(Collectors.toList()));
		config.setStoreMessage("Buffs " + creator.getClassId().toString() + " Lv" + creator.getStatus().getLevel());
	}
	
	private void configureNpcStats(Player npc, double maxHp, double maxCp)
	{
		final int baseLevel = 80;
		PlayerLevel pl = PlayerLevelData.getInstance().getPlayerLevel(baseLevel);
		if (pl != null)
			npc.getStatus().addExpAndSp(pl.requiredExpToLevelUp() - npc.getStatus().getExp(), 0);
		npc.getStatus().setHp(maxHp);
		npc.getStatus().setCp(maxCp);
		npc.getStatus().setMp(4000);
	}
	
	private void equipGhostItems(Player npc, List<Integer> itemIds)
	{
		if (itemIds == null)
			return;
		for (final int itemId : itemIds)
		{
			final ItemInstance dummyItem = ItemInstance.create(itemId, 1);
			if (dummyItem == null)
				continue;
			ItemInstanceTaskManager.getInstance().add(dummyItem);
			npc.getInventory().addItem(dummyItem);
			npc.getInventory().equipItem(dummyItem);
		}
	}
}

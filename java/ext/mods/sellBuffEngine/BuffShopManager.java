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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import ext.mods.commons.pool.ThreadPool;
import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.enums.PrivateStoreType;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.RecipeShopMsg;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.sellBuffEngine.BuffShopConfigs.Cost;
import ext.mods.sellBuffEngine.BuffShopConfigs.SkillPath;
import ext.mods.sellBuffEngine.ShopObject.PrivateBuff;

public final class BuffShopManager
{
	private static final Logger _log = Logger.getLogger(BuffShopManager.class.getName());
	private static final int OFFLINE_SHOPS_PER_TICK = 2;
	private static final int OFFLINE_SHOPS_TICK_DELAY = 2000;
	
	private final BuffShopDAO dao;
	private final BuffShopFactory factory;
	private final Map<Integer, ShopObject> shops = new ConcurrentHashMap<>();
	private final Map<Integer, Integer> sellers = new ConcurrentHashMap<>();
	private final Map<Integer, ShopObject> playerProfiles = new ConcurrentHashMap<>();
	
	private static class SingletonHolder
	{
		private static final BuffShopManager _instance = new BuffShopManager();
	}
	
	public static BuffShopManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private BuffShopManager()
	{
		this.dao = new BuffShopDAO();
		this.factory = BuffShopFactory.getInstance();
		_log.info("BuffShopManager: Sistema de Lojas de Buffs inicializado.");
	}
	
	public void startShopSetup(final Player player)
	{
		if (!BuffShopConfigs.BUFFSHOP_ALLOW_CLASS.contains(player.getClassId().getId()))
		{
			player.sendMessage("Sua classe n�o tem permiss�o para criar uma loja de buffs.");
			return;
		}
		BuffShopUIManager.getInstance().showManagementWindow(player, 1);
	}
	
	public void addBuffToProfile(final Player player, final int skillId, final int skillLevel, final int price)
	{
		final ShopObject shopProfile = getProfile(player);
		if (shopProfile.getBuffList().size() >= BuffShopConfigs.BUFFSHOP_BUFFS_MAX_COUNT)
		{
			player.sendMessage("Voc� atingiu o limite m�ximo de buffs na loja.");
			return;
		}
		shopProfile.addBuff(skillId, skillLevel, price);
	}
	
	public void removeBuffFromProfile(final Player player, final int skillId)
	{
		getProfile(player).removeBuff(skillId);
	}
	
	public void startShop(final Player player)
	{
		final ShopObject shopConfig = getProfile(player);
		if (!player.canOpenPrivateStore(true))
		{
			return;
		}
		
		if (shopConfig.getBuffList().isEmpty())
		{
			player.sendMessage("Voc� precisa adicionar pelo menos um buff para vender.");
			return;
		}
		if (shops.containsKey(player.getObjectId()))
		{
			stopShop(player);
		}
		
		final Player sellerNpc = factory.createShopNpc(player, shopConfig);
		if (sellerNpc == null)
		{
			player.sendMessage("Ocorreu um erro ao criar sua loja.");
			return;
		}
		
		shopConfig.setSellerNpcObjectId(sellerNpc.getObjectId());
		shopConfig.setXYZ(player.getX(), player.getY(), player.getZ(), player.getHeading());
		this.shops.put(player.getObjectId(), shopConfig);
		this.sellers.put(sellerNpc.getObjectId(), player.getObjectId());
		
		sellerNpc.getManufactureList().setStoreName(shopConfig.getStoreMessage());
		sellerNpc.setPrivateStoreType(PrivateStoreType.MANUFACTURE);
		sellerNpc.spawnMe(player.getX(), player.getY(), player.getZ());
		World.getInstance().addPlayer(sellerNpc);
		sellerNpc.setOnlineStatus(true, false);
		sellerNpc.sitDown();
		sellerNpc.broadcastUserInfo();
		sellerNpc.broadcastPacket(new RecipeShopMsg(sellerNpc));
		
		ThreadPool.execute(() -> dao.saveShop(shopConfig));
		player.sendMessage("Sua loja de buffs foi aberta com sucesso.");
	}
	
	public void stopShop(final Player playerOwner)
	{
		final ShopObject activeShop = shops.remove(playerOwner.getObjectId());
		if (activeShop != null)
		{
			final int npcId = activeShop.getSellerNpcObjectId();
			if (npcId > 0)
			{
				sellers.remove(npcId);
				final Player sellerNpc = World.getInstance().getPlayer(npcId);
				if (sellerNpc != null)
				{
					sellerNpc.deleteMe();
				}
			}
			ThreadPool.execute(() -> dao.removeShop(playerOwner.getObjectId()));
		}
	}
	
	public void sellBuff(final Player sellerNpc, final Player buyer, final int buffId, final int buffLevel, final String targetType, final int page)
	{
		final Integer ownerId = sellers.get(sellerNpc.getObjectId());
		if (ownerId == null)
			return;
		final ShopObject shop = shops.get(ownerId);
		if (shop == null)
			return;
		
		final BuffShopTransaction transaction = new BuffShopTransaction(buyer, sellerNpc, shop, buffId, buffLevel, targetType);
		
		if (transaction.execute())
		{
			final PrivateBuff soldBuff = shop.getBuff(buffId);
			if (soldBuff != null)
			{
				rewardSeller(shop, soldBuff.price());
			}
		}
		
		int currentTab = "pet".equalsIgnoreCase(targetType) ? 2 : 1;
		BuffShopUIManager.getInstance().showPublicShopWindow(buyer, sellerNpc, shop, currentTab, page);
	}
	
	public void restoreOfflineTraders()
	{
		_log.info("BuffShopManager: Agendando restauração de lojas de buffs offline...");
		
		final List<ShopObject> offlineShops = dao.loadShops();
		
		if (offlineShops.isEmpty())
		{
			_log.info("BuffShopManager: Nenhuma loja de buffs offline para restaurar.");
			return;
		}
		ThreadPool.schedule(new RestoreTask(offlineShops), 5000L);
	}

	private class RestoreTask implements Runnable
	{
		private final List<ShopObject> _shopsToRestore;
		private int _restoredCount;
		
		public RestoreTask(List<ShopObject> shops)
		{
			_shopsToRestore = shops;
			setrestoredCount(0);
		}
		
		@Override
		public void run()
		{
			int processed = 0;
			while (processed < OFFLINE_SHOPS_PER_TICK && !_shopsToRestore.isEmpty())
			{
		
				final ShopObject shopConfig = _shopsToRestore.remove(0);
				
	
				if (World.getInstance().getPlayer(shopConfig.getOwnerId()) == null)
				{
					final Player sellerNpc = factory.createShopNpc(shopConfig);
					if (sellerNpc != null)
					{
						shopConfig.setSellerNpcObjectId(sellerNpc.getObjectId());
						shops.put(shopConfig.getOwnerId(), shopConfig);
						sellers.put(sellerNpc.getObjectId(), shopConfig.getOwnerId());
						
						sellerNpc.getManufactureList().setStoreName(shopConfig.getStoreMessage());
						sellerNpc.setPrivateStoreType(PrivateStoreType.MANUFACTURE);
						sellerNpc.spawnMe(shopConfig.getX(), shopConfig.getY(), shopConfig.getZ());
						World.getInstance().addPlayer(sellerNpc);
						sellerNpc.setOnlineStatus(true, false);
						sellerNpc.sitDown();
						sellerNpc.broadcastUserInfo();
						sellerNpc.broadcastPacket(new RecipeShopMsg(sellerNpc));
						
						setrestoredCount(getrestoredCount() + 1);
					}
				}
				processed++;
			}

			if (!_shopsToRestore.isEmpty())
			{
				ThreadPool.schedule(this, OFFLINE_SHOPS_TICK_DELAY);
			}
			
		}

		public int getrestoredCount()
		{
			return _restoredCount;
		}

		public void setrestoredCount(int _restoredCount)
		{
			this._restoredCount = _restoredCount;
		}
	}
	
	private void rewardSeller(final ShopObject shop, final int price)
	{
		if (price <= 0)
		{
			return;
		}
		
		final int ownerId = shop.getOwnerId();
		final Player owner = World.getInstance().getPlayer(ownerId);
		
	
		if (owner != null && owner.isOnline())
		{
		
			owner.addAdena(price, true);
			owner.sendMessage("Seu buff foi vendido por " + price + " adena!");
		}
	
		else
		{

			ThreadPool.execute(() -> dao.addAdenaToOfflinePlayer(ownerId, price));
		}
	}
	
	public void buyPermanentSkill(Player player, int skillId, int levelToLearn)
	{

		if (!BuffShopConfigs.BUFFSHOP_ALLOW_CLASS_SKILLSHOP.contains(player.getClassId().getId()))
		{
			BuffShopUIManager.getInstance().showSkillShopMessage(player, "A sua classe n�o pode usar esta loja.");
			return;
		}
		
		SkillPath skillPath = BuffShopConfigs.SKILL_SHOP_PATHS.get(skillId);
		if (skillPath == null || levelToLearn > skillPath.maxLevel())
		{
			BuffShopUIManager.getInstance().showSkillShopMessage(player, "Esta skill n�o est� dispon�vel<br1> para compra neste n�vel.");
			return;
		}
		
		if (player.getSkillLevel(skillId) >= levelToLearn)
		{
			BuffShopUIManager.getInstance().showSkillShopMessage(player, "Voc� j� aprendeu este n�vel da skill.");
			return;
		}
	
		List<Cost> costs = skillPath.costsByLevel().get(levelToLearn);
		if (costs == null || costs.isEmpty())
		{
			BuffShopUIManager.getInstance().showSkillShopMessage(player, "O custo n�o foi definido para<br1> este n�vel de skill.");
			return;
		}

		for (Cost cost : costs)
		{
			if (player.getInventory().getItemByItemId(cost.itemId()) == null || player.getInventory().getItemByItemId(cost.itemId()).getCount() < cost.count())
			{
				BuffShopUIManager.getInstance().showSkillShopMessage(player, "Voc� n�o possui os itens necess�rios<br1> para aprender esta skill.");
				return;
			}
		}

		for (Cost cost : costs)
		{
			player.destroyItemByItemId(cost.itemId(), cost.count(), true);
		}
		
		final L2Skill skill = SkillTable.getInstance().getInfo(skillId, levelToLearn);
		player.addSkill(skill, true);
		
		BuffShopUIManager.getInstance().showSkillShopMessage(player, "Você aprendeu<br><font color=LEVEL>" + skill.getName() + " Nível " + levelToLearn + "</font>!");
		
		BuffShopUIManager.getInstance().showSkillShopWindow(player, 1); 
	}
	
	public Map<Integer, ShopObject> getShops()
	{
		return shops;
	}
	
	public Map<Integer, Integer> getSellers()
	{
		return sellers;
	}
	
	public ShopObject getProfile(Player player)
	{
		return playerProfiles.computeIfAbsent(player.getObjectId(), ShopObject::new);
	}
}

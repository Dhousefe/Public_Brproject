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

import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.skills.AbstractEffect;
import ext.mods.gameserver.skills.L2Skill;

public final class BuffShopBypassHandler
{
	private static final Logger _log = Logger.getLogger(BuffShopBypassHandler.class.getName());
	
	private final BuffShopManager manager = BuffShopManager.getInstance();
	private final BuffShopUIManager uiManager = BuffShopUIManager.getInstance();
	
	private static class SingletonHolder
	{
		private static final BuffShopBypassHandler _instance = new BuffShopBypassHandler();
	}
	
	public static BuffShopBypassHandler getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private BuffShopBypassHandler()
	{
	}
	
	public void handleBypass(Player player, String bypass)
	{
		if (!isPlayerStateValid(player))
			return;
		try
		{
			final StringTokenizer st = new StringTokenizer(bypass, " ");
			if (!st.hasMoreTokens())
				return;
			final String command = st.nextToken();
			
		
			switch (command)
			{
				case "index":
					uiManager.showIndexWindow(player, null);
					break;
				case "setshop":
					manager.startShopSetup(player);
					break;
				case "list":
					handleList(player, st);
					break;
				case "add":
					handleAddBuff(player, st);
					break;
				case "del":
					handleRemoveBuff(player, st);
					break;
				case "setprice":
					handleSetPrice(player, st);
					break;
				case "settitle":
					handleSetTitle(player, st);
					break;
				case "startshop":
					manager.startShop(player);
					break;
				case "stopshop":
					handleStopShop(player);
					break;
				case "showShop":
					handleShowShop(player, st);
					break;
				case "cast":
					handleCastBuff(player, st, false);
					break;
				case "manage_my_buffs":
					handleManageMyBuffs(player, st);
					break;
				case "remove_buff":
					handleRemoveActiveBuff(player, st);
					break;
				case "cast_confirm":
					handleConfirmedCast(player, st);
					break;
				case "shopskill":
					
					uiManager.showSkillShopWindow(player, 1);
					break;
				case "show_skill_shop":
					handleShowSkillShop(player, st);
					break;
				case "buy_skill":
					handleBuySkill(player, st);
					break;
				default:
					_log.warning("[BuffShop] Comando não reconhecido: " + command);
					break;
			}
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Erro ao processar bypass de BuffShop: " + bypass, e);
		}
	}
	
	/**
	 * NOVO M�TODO: Lida com a navega��o de p�ginas da loja de skills.
	 * @param player O jogador.
	 * @param st O StringTokenizer do bypass.
	 */
	private void handleShowSkillShop(Player player, StringTokenizer st)
	{
		try
		{
			int page = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
			uiManager.showSkillShopWindow(player, page);
		}
		catch (NumberFormatException e)
		{
			uiManager.showSkillShopWindow(player, 1);
		}
	}
	
	private void handleShowShop(Player buyer, StringTokenizer st)
	{
		
		if (!st.hasMoreTokens())
		{
			return;
		}
		
		final int sellerNpcId = Integer.parseInt(st.nextToken());
		
		final Player sellerNpc = World.getInstance().getPlayer(sellerNpcId);
		if (sellerNpc == null)
		{
			return;
		}
		
		final Integer ownerId = manager.getSellers().get(sellerNpcId);
		
		if (ownerId == null)
		{
			buyer.sendMessage("Erro: O dono desta loja n�o foi encontrado.");
			return;
		}
		
		final ShopObject shop = manager.getShops().get(ownerId);
		
		if (shop == null)
		{
			buyer.sendMessage("Erro: A configura��o da loja n�o foi encontrada.");
			return;
		}
		
		int tabId = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
		int page = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
		
		uiManager.showPublicShopWindow(buyer, sellerNpc, shop, tabId, page);
		
	}
	
	private void handleStopShop(Player player)
	{
		manager.stopShop(player);
		uiManager.showIndexWindow(player, "Sua loja foi fechada com sucesso.");
	}
	
	private void handleList(Player player, StringTokenizer st)
	{
		int page = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
		uiManager.showManagementWindow(player, page);
	}
	
	private void handleAddBuff(Player player, StringTokenizer st)
	{
		if (st.countTokens() < 4)
			return;
		final int skillId = Integer.parseInt(st.nextToken());
		final int skillLevel = Integer.parseInt(st.nextToken());
		final int price = Integer.parseInt(st.nextToken());
		final int page = Integer.parseInt(st.nextToken());
		manager.addBuffToProfile(player, skillId, skillLevel, price);
		uiManager.showManagementWindow(player, page);
	}
	
	private void handleRemoveBuff(Player player, StringTokenizer st)
	{
		if (st.countTokens() < 2)
			return;
		final int skillId = Integer.parseInt(st.nextToken());
		final int page = Integer.parseInt(st.nextToken());
		manager.removeBuffFromProfile(player, skillId);
		uiManager.showManagementWindow(player, page);
	}
	
	private void handleSetPrice(Player player, StringTokenizer st)
	{
		if (!st.hasMoreTokens())
			return;
		final String price = st.nextToken();
		final int page = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
		if (isDigit(price))
		{
			manager.getProfile(player).setTempPrice(price);
		}
		uiManager.showManagementWindow(player, page);
	}
	
	private void handleSetTitle(Player player, StringTokenizer st)
	{
		final String title = st.hasMoreTokens() ? st.nextToken("").trim() : "";
		if (!title.isEmpty() && title.length() < 30)
		{
			manager.getProfile(player).setTitle(title);
		}
		uiManager.showManagementWindow(player, 1);
	}
	
	private void handleCastBuff(Player buyer, StringTokenizer st, boolean isConfirmed)
	{
		
		if (st.countTokens() < 4)
			return;
		final int sellerNpcId = Integer.parseInt(st.nextToken());
		final int buffId = Integer.parseInt(st.nextToken());
		final int buffLevel = Integer.parseInt(st.nextToken());
		final int activeTab = Integer.parseInt(st.nextToken());
		final String target = st.hasMoreTokens() ? st.nextToken() : "player";
		final int page = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
		
		final Player sellerNpc = World.getInstance().getPlayer(sellerNpcId);
		final L2Skill skill = SkillTable.getInstance().getInfo(buffId, buffLevel);
		
		final L2Skill newSkill = SkillTable.getInstance().getInfo(buffId, buffLevel);
		
		final Integer ownerId = manager.getSellers().get(sellerNpcId);
		if (ownerId == null)
			return;
		final ShopObject shop = manager.getShops().get(ownerId);
		
		if (sellerNpc == null || manager.getSellers().get(sellerNpcId) == null)
		{
			buyer.sendMessage("O vendedor n�o est� mais dispon�vel.");
			return;
		}
		if (manager.getSellers().get(sellerNpcId).equals(buyer.getObjectId()))
		{
			buyer.sendMessage("Voc� n�o pode comprar de sua pr�pria loja.");
			return;
		}
		if (!checkIfInRange(150, sellerNpc, buyer, true))
		{
			buyer.sendMessage("Voc� est� muito longe da loja.");
			final Location destination = GeoEngine.getInstance().getValidLocation(buyer, sellerNpc.getX(), sellerNpc.getY(), sellerNpc.getZ());
			buyer.getAI().doMoveToIntention(destination, null);
			handleShowShop(buyer, st);
			buyer.sendPacket(ActionFailed.STATIC_PACKET);
			
			return;
		}
		
		boolean isSummon = BuffShopConfigs.BUFFSHOP_RESTRICTED_SUMMONS.contains(buffId);
		boolean isCubic = BuffShopConfigs.BUFFSHOP_ALLOWED_SELF_SKILLS.contains(buffId);
		if (isSummon || isCubic)
		{
			if (isSummon && buyer.getSummon() != null)
			{
				
				buyer.sendPacket(SystemMessageId.SUMMON_ONLY_ONE);
				return;
			}
			
			if (buyer.getStatus().getMp() < skill.getMpConsume())
			{
				
				buyer.sendPacket(SystemMessageId.NOT_ENOUGH_MP);
				return;
			}
			if (skill.getItemConsumeId() > 0 && buyer.getInventory().getItemByItemId(skill.getItemConsumeId()) == null)
			{
				
				buyer.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
				return;
			}
			
		}
		
		if (BuffShopConfigs.BUFFSHOP_REPLACEABLE_BUFFS.contains(buffId))
		{
			for (int replaceableId : BuffShopConfigs.BUFFSHOP_REPLACEABLE_BUFFS)
			{
				
				
				AbstractEffect oldEffect = buyer.getFirstEffect(replaceableId);
				if (oldEffect != null)
				{
					L2Skill oldSkill = oldEffect.getSkill();
					
					final ShopObject.PrivateBuff buffInfo = shop.getBuff(buffId);
					if (buffInfo == null)
					{
						return;
					}
					final long adenaCost = buffInfo.price();
					
					final String confirmBypass = String.format("bypass -h cast_confirm %d %d %d %s %d", sellerNpcId, buffId, buffLevel, target, page);
					
					uiManager.showBuffReplaceConfirmation(buyer, sellerNpc, oldSkill, newSkill, adenaCost, confirmBypass, activeTab, page);
					return;
				}
			}
		}
		
		manager.sellBuff(sellerNpc, buyer, buffId, buffLevel, target, page);
	}
	
	private void handleBuySkill(Player player, StringTokenizer st)
	{
		if (st.countTokens() < 2)
			return;
		int skillId = Integer.parseInt(st.nextToken());
		int skillLevel = Integer.parseInt(st.nextToken());
		manager.buyPermanentSkill(player, skillId, skillLevel);
	}
	

	private void handleConfirmedCast(Player buyer, StringTokenizer st)
	{
		if (st.countTokens() < 5)
			return;
		
		final int sellerNpcId = Integer.parseInt(st.nextToken());
		final int buffId = Integer.parseInt(st.nextToken());
		final int buffLevel = Integer.parseInt(st.nextToken());
		final String target = st.nextToken();
		final int page = Integer.parseInt(st.nextToken());
		
		final Player sellerNpc = World.getInstance().getPlayer(sellerNpcId);
		if (sellerNpc == null)
			return;
		
		for (int replaceableId : BuffShopConfigs.BUFFSHOP_REPLACEABLE_BUFFS)
		{
			buyer.stopSkillEffects(replaceableId);
		}
		buyer.stopSkillEffects(buffId);
		
		manager.sellBuff(sellerNpc, buyer, buffId, buffLevel, target, page);
	}
	
	public void sendConfirmationDialog(Player player, Player sellerNpc, String question, int sellerNpcId, int buffId, int buffLevel, String target, int page)
	{
		final String confirmBypass = String.format("bypass -h cast_confirm %d %d %d %s %d", sellerNpcId, buffId, buffLevel, target, page);
		final String cancelBypass = "bypass -h Dialog 1";
		
		final NpcHtmlMessage html = new NpcHtmlMessage(sellerNpc.getObjectId());
		html.setHtml("<html><body>" + question + "<br><center>" + "<button value=\"Sim\" action=\"" + confirmBypass + "\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\">" + "&nbsp;<button value=\"N�o\" action=\"" + cancelBypass + "\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\">" + "</center></body></html>");
		player.sendPacket(html);
	}
	
	private void handleManageMyBuffs(Player player, StringTokenizer st)
	{
		String targetType = st.hasMoreTokens() ? st.nextToken() : "player";
		uiManager.showBuffRemovalWindow(player, targetType);
	}
	
	private void handleRemoveActiveBuff(Player player, StringTokenizer st)
	{
		if (st.countTokens() < 3)
			return;
		final int skillId = Integer.parseInt(st.nextToken());
		final int skillLevel = Integer.parseInt(st.nextToken());
		final String targetType = st.nextToken();
		uiManager.removePlayerBuff(player, skillId, skillLevel, targetType);
	}
	
	private boolean isPlayerStateValid(Player player)
	{
		if (player.isInStoreMode() || player.isDead() || player.isInCombat())
		{
			player.sendMessage("Voc� n�o pode fazer isso agora.");
			return false;
		}
		return true;
	}
	
	private boolean checkIfInRange(int range, WorldObject obj1, WorldObject obj2, boolean includeZ)
	{
		if (obj1 == null || obj2 == null || range == -1)
			return true;
		long dx = obj1.getX() - obj2.getX();
		long dy = obj1.getY() - obj2.getY();
		long dz = includeZ ? obj1.getZ() - obj2.getZ() : 0;
		return dx * dx + dy * dy + dz * dz <= (long) range * range;
	}
	
	private boolean isDigit(String text)
	{
		return text != null && !text.isEmpty() && text.matches("\\d+");
	}
}

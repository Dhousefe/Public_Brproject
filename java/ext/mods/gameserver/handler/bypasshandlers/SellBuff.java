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
package ext.mods.gameserver.handler.bypasshandlers;

import java.util.StringTokenizer;

import ext.mods.commons.lang.StringUtil;

import ext.mods.Config;
import ext.mods.gameserver.data.manager.SellBuffsManager;
import ext.mods.gameserver.data.xml.ItemData;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.handler.IBypassHandler;
import ext.mods.gameserver.model.SellBuffHolder;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.records.SellBuffData;
import ext.mods.gameserver.network.serverpackets.MagicSkillUse;
import ext.mods.gameserver.skills.L2Skill;

public class SellBuff implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"sellbuffadd",
		"sellbuffaddskill",
		"sellbuffedit",
		"sellbuffchangeprice",
		"sellbuffremove",
		"sellbuffbuymenu",
		"sellbuffbuyskill",
		"sellbuffstart",
		"sellbuffstop",
	};
	
	@Override
	public boolean useBypass(String command, Player player, Creature target)
	{
		String cmd = "";
		final StringBuilder params = new StringBuilder();
		final StringTokenizer st = new StringTokenizer(command, " ");
		
		if (st.hasMoreTokens())
			cmd = st.nextToken();
		
		while (st.hasMoreTokens())
		{
			params.append(st.nextToken());
			if (st.hasMoreTokens())
				params.append(" ");
		}
		
		if (cmd.isEmpty())
			return false;
		
		return useBypass(cmd, player, params.toString().trim());
	}
	
	public boolean useBypass(String command, Player player, String params)
	{
		if (!Config.SELLBUFF_ENABLED)
			return false;
		
		switch (command)
		{
			case "sellbuffstart":
				if (player.isSellingBuffs() || params == null || params.isEmpty())
					return false;
				
				if (!SellBuffsManager.getInstance().canStartSellBuffs(player))
					return false;
				
				if (player.getSellingBuffs().isEmpty())
				{
					player.sendMessage("Your list of buffs is empty, please add some buffs first!");
					return false;
				}
				else
				{
					final StringBuilder title = new StringBuilder();
					title.append("BUFF SELL: ");
					final StringTokenizer st = new StringTokenizer(params, " ");
					while (st.hasMoreTokens())
					{
						title.append(st.nextToken() + " ");
					}
					
					if (title.length() > 40)
					{
						player.sendMessage("Your title cannot exceed 29 characters in length. Please try again.");
						return false;
					}
					
					SellBuffsManager.getInstance().startSellBuffs(player, title.toString());
				}
				break;
			
			case "sellbuffstop":
				if (player.isSellingBuffs())
					SellBuffsManager.getInstance().stopSellBuffs(player);
				
				break;
			
			case "sellbuffadd":
				if (!player.isSellingBuffs())
				{
					int index = 1;
					if (params != null && !params.isEmpty() && StringUtil.isDigit(params))
						index = Integer.parseInt(params);
					
					SellBuffsManager.getInstance().sendBuffChoiceMenu(player, index);
				}
				break;
			
			case "sellbuffedit":
				if (!player.isSellingBuffs())
				{
					int index = 1;
					if (params != null && !params.isEmpty() && StringUtil.isDigit(params))
						index = Integer.parseInt(params);
					
					SellBuffsManager.getInstance().sendBuffEditMenu(player, index);
				}
				break;
			
			case "sellbuffchangeprice":
				if (!player.isSellingBuffs() && (params != null) && !params.isEmpty())
				{
					final StringTokenizer st = new StringTokenizer(params, " ");
					
					int skillId = -1;
					int price = -1;
					
					if (st.hasMoreTokens())
						skillId = Integer.parseInt(st.nextToken());
					
					if (st.hasMoreTokens())
					{
						try
						{
							price = Integer.parseInt(st.nextToken());
						}
						catch (NumberFormatException e)
						{
							player.sendMessage("Too big price! Maximum price is " + Config.SELLBUFF_MAX_PRICE);
							SellBuffsManager.getInstance().sendBuffEditMenu(player, 1);
						}
					}
					
					if (skillId == -1 || price == -1)
						return false;
					
					final L2Skill skillToChange = switch (skillId)
					{
						case 4699 -> SellBuffsManager.getInstance().getBuffSkill(player, 1331, NpcSkillType.BUFF1);
						case 4700 -> SellBuffsManager.getInstance().getBuffSkill(player, 1331, NpcSkillType.BUFF2);
						case 4702 -> SellBuffsManager.getInstance().getBuffSkill(player, 1332, NpcSkillType.BUFF1);
						case 4703 -> SellBuffsManager.getInstance().getBuffSkill(player, 1332, NpcSkillType.BUFF2);
						default -> player.getSkill(skillId);
					};
					
					if (skillToChange == null)
						return false;
					
					final SellBuffHolder holder = player.getSellingBuffs().stream().filter(h -> (h.getSkillId() == skillToChange.getId())).findFirst().orElse(null);
					if (holder != null)
					{
						player.sendMessage("Price of " + skillToChange.getName() + " has been changed to " + price + "!");
						holder.setPrice(price);
						player.saveSellingBuffs();
						SellBuffsManager.getInstance().sendBuffEditMenu(player, 1);
					}
				}
				break;
			
			case "sellbuffremove":
				if (!player.isSellingBuffs() && (params != null) && !params.isEmpty())
				{
					final StringTokenizer st = new StringTokenizer(params, " ");
					
					int skillId = -1;
					
					if (st.hasMoreTokens())
						skillId = Integer.parseInt(st.nextToken());
					
					if (skillId == -1)
						return false;
					
					final L2Skill skillToRemove = switch (skillId)
					{
						case 4699 -> SellBuffsManager.getInstance().getBuffSkill(player, 1331, NpcSkillType.BUFF1);
						case 4700 -> SellBuffsManager.getInstance().getBuffSkill(player, 1331, NpcSkillType.BUFF2);
						case 4702 -> SellBuffsManager.getInstance().getBuffSkill(player, 1332, NpcSkillType.BUFF1);
						case 4703 -> SellBuffsManager.getInstance().getBuffSkill(player, 1332, NpcSkillType.BUFF2);
						default -> player.getSkill(skillId);
					};
					
					if (skillToRemove == null)
						return false;
					
					final SellBuffHolder holder = player.getSellingBuffs().stream().filter(h -> (h.getSkillId() == skillToRemove.getId())).findFirst().orElse(null);
					if (holder != null && player.getSellingBuffs().remove(holder))
					{
						player.sendMessage("Skill " + skillToRemove.getName() + " has been removed!");
						player.saveSellingBuffs();
						SellBuffsManager.getInstance().sendBuffEditMenu(player, 1);
					}
				}
				break;
			
			case "sellbuffaddskill":
				if (!player.isSellingBuffs() && params != null && !params.isEmpty())
				{
					final String[] parts = params.split("\\s+");
					if (parts.length != 3)
					{
						LOGGER.warn("illegal bypass");
						SellBuffsManager.getInstance().sendBuffChoiceMenu(player, 1);
						return false;
					}
					
					final int skillId;
					final int price;
					final int index;
					try
					{
						skillId = Integer.parseInt(parts[0]);
						price = Integer.parseInt(parts[1]);
						index = Integer.parseInt(parts[2]);
					}
					catch (NumberFormatException e)
					{
						LOGGER.warn("illegal bypass input");
						try
						{
							int index2 = Integer.parseInt(parts[3]);
							SellBuffsManager.getInstance().sendBuffChoiceMenu(player, index2 <= 0 ? 1 : index2);
						}
						catch (NumberFormatException e1)
						{
							SellBuffsManager.getInstance().sendBuffChoiceMenu(player, 1);
						}
						return false;
					}
					
					if (skillId <= 0 || index <= 0)
					{
						LOGGER.warn("illegal bypass input");
						SellBuffsManager.getInstance().sendBuffChoiceMenu(player, index <= 0 ? 1 : index);
						return false;
					}
					
					if (price > Config.SELLBUFF_MAX_PRICE)
					{
						player.sendMessage("Too big price! Maximum price is " + Config.SELLBUFF_MAX_PRICE);
						SellBuffsManager.getInstance().sendBuffChoiceMenu(player, index);
						return false;
					}
					
					if (price < Config.SELLBUFF_MIN_PRICE)
					{
						player.sendMessage("Too small price! Minimum price is " + Config.SELLBUFF_MIN_PRICE);
						SellBuffsManager.getInstance().sendBuffChoiceMenu(player, index);
						return false;
					}
					
					if (player.getSellingBuffs().size() >= Config.SELLBUFF_MAX_BUFFS)
					{
						player.sendMessage("You already reached max count of buffs! Max buffs is: " + Config.SELLBUFF_MAX_BUFFS);
						SellBuffsManager.getInstance().sendBuffChoiceMenu(player, index);
						return false;
					}
					
					final L2Skill skillToAdd = switch (skillId)
					{
						case 4699 -> SellBuffsManager.getInstance().getBuffSkill(player, 1331, NpcSkillType.BUFF1);
						case 4700 -> SellBuffsManager.getInstance().getBuffSkill(player, 1331, NpcSkillType.BUFF2);
						case 4702 -> SellBuffsManager.getInstance().getBuffSkill(player, 1332, NpcSkillType.BUFF1);
						case 4703 -> SellBuffsManager.getInstance().getBuffSkill(player, 1332, NpcSkillType.BUFF2);
						default -> player.getSkill(skillId);
					};
					
					if (skillToAdd == null)
					{
						LOGGER.warn("illegal state bypass");
						SellBuffsManager.getInstance().sendBuffChoiceMenu(player, index);
						return false;
					}
					
					final SellBuffHolder skillHolder = new SellBuffHolder(skillId, skillToAdd.getLevel(), price);
					
					if (!SellBuffsManager.getInstance().isInSellList(player, skillToAdd))
					{
					    player.getSellingBuffs().add(skillHolder);
					    player.sendMessage(skillToAdd.getName() + " has been added!");
					    player.saveSellingBuffs();
					    SellBuffsManager.getInstance().sendBuffChoiceMenu(player, index);
					}
					else
					{
					    LOGGER.warn("Attempt to add a skill that is already on sale.");
					    player.sendMessage("This skill is already on sale.");
					    SellBuffsManager.getInstance().sendBuffChoiceMenu(player, index);
					}
				}
				break;
			
			case "sellbuffbuymenu":
				if (params != null && !params.isEmpty())
				{
					final StringTokenizer st = new StringTokenizer(params, " ");
					
					int objId = -1;
					int index = 0;
					if (st.hasMoreTokens())
						objId = Integer.parseInt(st.nextToken());
					
					if (st.hasMoreTokens())
						index = Integer.parseInt(st.nextToken());
					
					final Player seller = World.getInstance().getPlayer(objId);
					if (seller != null)
					{
						if (!seller.isSellingBuffs() || !player.isIn3DRadius(seller, Npc.INTERACTION_DISTANCE))
							return false;
						
						SellBuffsManager.getInstance().sendBuffMenu(player, seller, index);
					}
				}
				break;
			
			case "sellbuffbuyskill":
				if (params != null && !params.isEmpty())
				{
					final String[] parts = params.split("\\s+");
					
					if (parts.length != 5)
					{
						LOGGER.warn("illegal bypass");
						SellBuffsManager.getInstance().sendBuffMenu(player, player, 1);
						return false;
					}
					
					final int objId;
					final int skillId;
					final int skillLvl;
					final int skillPrice;
					final int index;
					try
					{
						objId = Integer.parseInt(parts[0]);
						skillId = Integer.parseInt(parts[1]);
						skillLvl = Integer.parseInt(parts[2]);
						skillPrice = Integer.parseInt(parts[3]);
						index = Integer.parseInt(parts[4]);
					}
					catch (NumberFormatException e)
					{
						LOGGER.warn("illegal bypass input");
						try
						{
							int index2 = Integer.parseInt(parts[3]);
							SellBuffsManager.getInstance().sendBuffMenu(player, player, index2 <= 0 ? 1 : index2);
						}
						catch (NumberFormatException e1)
						{
							SellBuffsManager.getInstance().sendBuffMenu(player, player, 1);
						}
						return false;
					}
					
					if (skillId == -1 || skillLvl <= 0 || objId == -1)
						return false;
					
					final Player seller = World.getInstance().getPlayer(objId);
					if (seller == null)
						return false;
					
					final SellBuffHolder holder = new SellBuffHolder(skillId, skillLvl, skillPrice);
					
					final L2Skill skillToBuy = holder.getSkillFrom(seller);
					
					if (skillToBuy == null)
					{
						LOGGER.warn("Seller[{}] try to sell missing skill[{}]", seller.getName(), holder.getSkill());
						return false;
					}
					
					if (!seller.isSellingBuffs() || !player.isInStrictRadius(player, Npc.INTERACTION_DISTANCE))
						return false;
					
					if (seller.getStatus().getMp() < (Math.max(skillToBuy.getMpConsume(), 50) * Config.SELLBUFF_MP_MULTIPLER))
					{
						player.sendMessage(seller.getName() + " has no enough mana for " + skillToBuy.getName() + "!");
						SellBuffsManager.getInstance().sendBuffMenu(player, seller, index);
						return false;
					}
					
					if (player.getInventory().getItemCount(Config.SELLBUFF_PAYMENT_ID) >= holder.getPrice())
					{
						if ((Integer.MAX_VALUE - seller.getInventory().getAdena() - holder.getPrice()) < 0)
						{
							player.sendMessage("Seller have limit adena.");
							SellBuffsManager.getInstance().sendBuffMenu(player, seller, index);
							return false;
						}
						
						player.destroyItemByItemId(Config.SELLBUFF_PAYMENT_ID, holder.getPrice(), true);
						seller.addItem(Config.SELLBUFF_PAYMENT_ID, holder.getPrice(), true);
						
						seller.getStatus().reduceMp(Math.max(skillToBuy.getMpConsume(), 50) * Config.SELLBUFF_MP_MULTIPLER);
						player.sendPacket(new MagicSkillUse(seller, player, holder.getSkillUse(), 1, 900, 0));
						
						SellBuffData buffSkill = SellBuffsManager.getInstance().getBuff(skillToBuy);
						if (Config.CUSTOM_TIME_BUFF)
						{
							if (player.getSummon() != null && buffSkill.applyOnPets())
								skillToBuy.getEffectsSellBuff(seller, player.getSummon());
							
							skillToBuy.getEffectsSellBuff(seller, player);
						}
						else
						{
							if (player.getSummon() != null && buffSkill.applyOnPets())
								skillToBuy.getEffects(seller, player.getSummon());
							
							skillToBuy.getEffects(seller, player);
						}
					}
					else
					{
						final var item = ItemData.getInstance().getTemplate(Config.SELLBUFF_PAYMENT_ID);
						if (item != null)
							player.sendMessage("Not enough " + item.getName() + "!");
						else
							player.sendMessage("Not enough items!");
					}
					
					SellBuffsManager.getInstance().sendBuffMenu(player, seller, index);
				}
				break;
		}
		
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}
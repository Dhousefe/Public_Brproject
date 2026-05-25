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

import java.util.Objects;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.enums.skills.SkillTargetType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.Summon;
import ext.mods.gameserver.model.olympiad.OlympiadManager;
import ext.mods.gameserver.network.serverpackets.MagicSkillUse;
import ext.mods.gameserver.network.serverpackets.SkillList;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.sellBuffEngine.ShopObject.PrivateBuff;

public final class BuffShopTransaction
{
	private final Player buyer;
	private final Player sellerNpc;
	private final PrivateBuff buffToSell;
	private final L2Skill buffSkill;
	private final String targetTypeName;
	private Creature actualTarget;
	
	public BuffShopTransaction(Player buyer, Player sellerNpc, ShopObject shop, int buffId, int buffLevel, String targetTypeName)
	{
		this.buyer = Objects.requireNonNull(buyer, "Buyer n�o pode ser nulo.");
		this.sellerNpc = Objects.requireNonNull(sellerNpc, "SellerNpc n�o pode ser nulo.");
		this.targetTypeName = targetTypeName;
		this.buffToSell = shop.getBuff(buffId);
		this.buffSkill = (this.buffToSell != null) ? SkillTable.getInstance().getInfo(buffId, buffLevel) : null;
	}
	
	public boolean execute()
	{
		if (!validateInitialState())
			return false;
		if (!applyEffectAndConsumeResources())
			return false;
		processShopPayment();
		return true;
	}
	
	private boolean validateInitialState()
	{
		if (buffToSell == null || buffSkill == null)
		{
			buyer.sendMessage("Este buff n�o est� mais dispon�vel.");
			return false;
		}
		
		if (OlympiadManager.getInstance().isRegistered(buyer) || buyer.isDead() || buyer.isInCombat())
		{
			buyer.sendMessage("Voc� n�o pode comprar buffs neste estado.");
			return false;
		}
		
		if (buyer.getAdena() < buffToSell.price())
		{
			buyer.sendMessage("Voc� n�o tem adena suficiente.");
			return false;
		}
		
		return validateAndSetTarget() && validatePurchaseRestrictions();
	}
	
	private boolean validateAndSetTarget()
	{
		if ("pet".equalsIgnoreCase(targetTypeName))
		{
			final Summon pet = buyer.getSummon();
			if (pet == null || pet.isDead())
			{
				buyer.sendMessage("Voc� n�o possui um pet/summon ativo para receber este buff.");
				return false;
			}
			this.actualTarget = pet;
		}
		else
		{
			this.actualTarget = buyer;
		}
		
		if (BuffShopConfigs.BUFFSHOP_ALLOWED_SELF_SKILLS.contains(buffSkill.getId()) && actualTarget != buyer)
		{
			buyer.sendMessage("Esta skill s� pode ser usada em voc�.");
			return false;
		}
		if (buffSkill.getTargetType() == SkillTargetType.SUMMON && actualTarget == buyer)
		{
			buyer.sendMessage("Este buff s� pode ser usado em um pet/summon.");
			return false;
		}
		return true;
	}
	
	private boolean validatePurchaseRestrictions()
	{
		if (BuffShopConfigs.BUFFSHOP_RESTRICTED_SUMMONS.contains(buffSkill.getId()) || BuffShopConfigs.BUFFSHOP_ALLOWED_SELF_SKILLS.contains(buffSkill.getId()))
		{
			if (buyer.getSkillLevel(buffSkill.getId()) > 0)
			{
				buyer.sendMessage("Voc� j� possui esta skill em sua classe original.");
				return false;
			}
		}
		if (BuffShopConfigs.BUFFSHOP_RESTRICTED_SUMMONS.contains(buffSkill.getId()))
		{
			if (!BuffShopConfigs.BUFFSHOP_SUMMON_BUYER_CLASSES.contains(buyer.getClassId()))
			{
				buyer.sendMessage("Sua classe n�o tem permiss�o para comprar esta invoca��o.");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Cobra o pre�o da loja (Adena) AP�S o sucesso da conjura��o.
	 */
	private void processShopPayment()
	{
		if (buffToSell != null)
		{
			buyer.reduceAdena(buffToSell.price(), true);
		}
	}
	
	private boolean applyEffectAndConsumeResources()
	{
		boolean isSpecialSkill = BuffShopConfigs.BUFFSHOP_RESTRICTED_SUMMONS.contains(buffSkill.getId()) || BuffShopConfigs.BUFFSHOP_ALLOWED_SELF_SKILLS.contains(buffSkill.getId());
		
		if (isSpecialSkill)
		{
			
			buyer.addSkill(buffSkill, false);
			try
			{
				
				final int requiredItemId = buffSkill.getItemConsumeId();
				if (requiredItemId > 0)
				{
					if (!buyer.destroyItemByItemId(requiredItemId, buffSkill.getItemConsume(), true))
					{
						buyer.sendMessage("Voc� n�o possui os itens necess�rios.");
						return false;
					}
					
					buffSkill.useSkill(buyer, new Creature[]
					{
						buyer
					});
				}
			}
			finally
			{
				buyer.removeSkill(buffSkill.getId(), false);
				buyer.sendPacket(new SkillList(buyer));
			}
			return true;
		}
		else
		{
			if (sellerNpc.getStatus().getMp() < buffSkill.getMpConsume())
			{
				buyer.sendMessage("O vendedor est� sem mana no momento.");
				return false;
			}
			
			final int requiredItemId = buffSkill.getItemConsumeId();
			if (requiredItemId > 0)
			{
				if (!buyer.destroyItemByItemId(requiredItemId, buffSkill.getItemConsume(), true))
				{
					buyer.sendMessage("Voc� n�o possui os itens necess�rios.");
					return false;
				}
			}
			
			sellerNpc.getStatus().reduceMp(buffSkill.getMpConsume());
			buyer.sendPacket(new MagicSkillUse(sellerNpc, actualTarget, buffSkill.getId(), buffSkill.getLevel(), 1500, 1500));
			buffSkill.getEffects(sellerNpc, actualTarget);
			return true;
		}
	}
}

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
import ext.mods.commons.random.Rnd;
import ext.mods.commons.util.ArraysUtil;

import ext.mods.gameserver.enums.EventHandler;
import ext.mods.gameserver.enums.items.WeaponType;
import ext.mods.gameserver.enums.skills.ShieldDefense;
import ext.mods.gameserver.handler.ISkillHandler;
import ext.mods.gameserver.handler.SkillHandler;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.skills.AbstractEffect;
import ext.mods.gameserver.skills.Formulas;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.skills.conditions.Condition;
import ext.mods.gameserver.skills.conditions.ConditionGameChance;

/**
 * This class is dedicated to the management of weapons.
 */
public final class Weapon extends Item
{
	private final WeaponType _type;
	private final int _rndDam;
	private final int _soulShotCount;
	private final int _spiritShotCount;
	private final int _mpConsume;
	private final int _mpConsumeReduceRate;
	private final int _mpConsumeReduceValue;
	private final boolean _isMagical;
	
	private IntIntHolder _enchant4Skill;
	
	private IntIntHolder _skillOnMagic;
	private Condition _skillOnMagicCondition;
	
	private IntIntHolder _skillOnCrit;
	private Condition _skillOnCritCondition;
	
	private final int _reuseDelay;
	
	private final int _reducedSoulshot;
	private final int _reducedSoulshotChance;
	
	public Weapon(StatSet set)
	{
		super(set);
		
		_type = set.getEnum("weapon_type", WeaponType.class, WeaponType.NONE);
		_type1 = Item.TYPE1_WEAPON_RING_EARRING_NECKLACE;
		_type2 = Item.TYPE2_WEAPON;
		
		_soulShotCount = set.getInteger("soulshots", 0);
		_spiritShotCount = set.getInteger("spiritshots", 0);
		_rndDam = set.getInteger("random_damage", 0);
		_mpConsume = set.getInteger("mp_consume", 0);
		
		String[] reduce = set.getString("mp_consume_reduce", "0,0").split(",");
		_mpConsumeReduceRate = Integer.parseInt(reduce[0]);
		_mpConsumeReduceValue = Integer.parseInt(reduce[1]);
		
		_reuseDelay = set.getInteger("reuse_delay", 0);
		_isMagical = set.getBool("is_magical", false);
		
		String[] reducedSoulshot = set.getString("reduced_soulshot", "").split(",");
		_reducedSoulshotChance = (reducedSoulshot.length == 2) ? Integer.parseInt(reducedSoulshot[0]) : 0;
		_reducedSoulshot = (reducedSoulshot.length == 2) ? Integer.parseInt(reducedSoulshot[1]) : 0;
		
		if (set.containsKey("enchant4_skill"))
			_enchant4Skill = set.getIntIntHolder("enchant4_skill");
		
		if (set.containsKey("oncast_skill"))
		{
			_skillOnMagic = set.getIntIntHolder("oncast_skill");
			
			if (set.containsKey("oncast_chance"))
				_skillOnMagicCondition = new ConditionGameChance(set.getInteger("oncast_chance"));
		}
		
		if (set.containsKey("oncrit_skill"))
		{
			_skillOnCrit = set.getIntIntHolder("oncrit_skill");
			
			if (set.containsKey("oncrit_chance"))
				_skillOnCritCondition = new ConditionGameChance(set.getInteger("oncrit_chance"));
		}
	}
	
	@Override
	public WeaponType getItemType()
	{
		return _type;
	}
	
	@Override
	public int getItemMask()
	{
		return getItemType().mask();
	}
	
	/**
	 * @return the quantity of used SoulShot.
	 */
	public int getSoulShotCount()
	{
		return _soulShotCount;
	}
	
	/**
	 * @return the quantity of used SpiritShot.
	 */
	public int getSpiritShotCount()
	{
		return _spiritShotCount;
	}
	
	/**
	 * @return the reduced quantity of SoulShot used.
	 */
	public int getReducedSoulShot()
	{
		return _reducedSoulshot;
	}
	
	/**
	 * @return the chance to use reduced SoulShot.
	 */
	public int getReducedSoulShotChance()
	{
		return _reducedSoulshotChance;
	}
	
	/**
	 * @return the random damage inflicted by the {@link Weapon}.
	 */
	public int getRandomDamage()
	{
		return _rndDam;
	}
	
	/**
	 * @return the reuse delay of the {@link Weapon}.
	 */
	public int getReuseDelay()
	{
		return _reuseDelay;
	}
	
	/**
	 * @return true if the {@link Weapon} is considered as a mage weapon, false otherwise.
	 */
	public final boolean isMagical()
	{
		return _isMagical;
	}
	
	/**
	 * @return true if the {@link Weapon} is an Apprentice's weapon, false otherwise.
	 */
	public final boolean isApprenticeWeapon()
	{
		return getItemId() >= 7816 && getItemId() <= 7821;
	}
	
	/**
	 * @return true if the {@link Weapon} is a Traveler's weapon, false otherwise.
	 */
	public final boolean isTravelerWeapon()
	{
		return getItemId() >= 7822 && getItemId() <= 7831;
	}
	
	/**
	 * @return the MP consumption of the {@link Weapon}.
	 */
	public int getMpConsume()
	{
		if (_mpConsumeReduceRate > 0 && Rnd.get(100) < _mpConsumeReduceRate)
			return _mpConsumeReduceValue;
		
		return _mpConsume;
	}
	
	/**
	 * @return the passive {@link L2Skill} when a {@link Weapon} owner equips a weapon +4 (used for duals SA).
	 */
	public L2Skill getEnchant4Skill()
	{
		return (_enchant4Skill == null) ? null : _enchant4Skill.getSkill();
	}
	
	/**
	 * Cast a {@link L2Skill} upon critical hit.
	 * @param caster : The Creature caster.
	 * @param target : The Creature target.
	 */
	public void castSkillOnCrit(Creature caster, Creature target)
	{
		if (_skillOnCrit == null)
			return;
		
		final L2Skill skillOnCrit = _skillOnCrit.getSkill();
		if (skillOnCrit == null)
			return;
		
		if (_skillOnCritCondition != null && !_skillOnCritCondition.test(caster, target, skillOnCrit))
			return;
		
		final ShieldDefense sDef = Formulas.calcShldUse(caster, target, skillOnCrit, false);
		if (!Formulas.calcSkillSuccess(caster, target, skillOnCrit, sDef, false))
			return;
		
		final AbstractEffect effect = target.getFirstEffect(skillOnCrit.getId());
		if (effect != null)
			effect.exit();
		
		skillOnCrit.getEffects(caster, target, sDef, false);
	}
	
	/**
	 * Cast a {@link L2Skill} upon magic use.
	 * @param caster : The Creature caster.
	 * @param target : The Creature target.
	 * @param trigger : The L2Skill triggering this action.
	 */
	public void castSkillOnMagic(Creature caster, Creature target, L2Skill trigger)
	{
		if (_skillOnMagic == null)
			return;
		
		final L2Skill skillOnMagic = _skillOnMagic.getSkill();
		if (skillOnMagic == null)
			return;
		
		if (isHeroItem() && trigger.getId() == 2165 && skillOnMagic.isHealSkill())
			return;
		
		if (trigger.isOffensive() != skillOnMagic.isOffensive())
			return;
		
		if (trigger.isToggle() || trigger.isPotion())
			return;
		
		if (_skillOnMagicCondition != null && !_skillOnMagicCondition.test(caster, target, skillOnMagic))
			return;
		
		final ShieldDefense sDef = Formulas.calcShldUse(caster, target, skillOnMagic, false);
		if (skillOnMagic.isOffensive() && !Formulas.calcSkillSuccess(caster, target, skillOnMagic, sDef, false))
			return;
		
		if (caster instanceof Player player)
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_ACTIVATED).addSkillName(skillOnMagic));
		
		final Creature[] targets = new Creature[]
		{
			target
		};
		
		final ISkillHandler handler = SkillHandler.getInstance().getHandler(skillOnMagic.getSkillType());
		if (handler != null)
			handler.useSkill(caster, skillOnMagic, targets, null);
		else
			skillOnMagic.useSkill(caster, targets);
		
		if (caster instanceof Player player)
		{
			caster.forEachKnownTypeInRadius(Npc.class, 1000, npc ->
			{
				if (targets.length == 1 && ((player.getSummon() != null && ArraysUtil.contains(targets, player.getSummon())) || (!skillOnMagic.isOffensive() && !skillOnMagic.isDebuff() && ArraysUtil.contains(targets, npc))))
					return;
				
				for (Quest quest : npc.getTemplate().getEventQuests(EventHandler.SEE_SPELL))
					quest.onSeeSpell(npc, (Player) caster, skillOnMagic, targets, false);
			});
			
			if (!skillOnMagic.isOffensive() && target instanceof Npc targetNpc)
			{
				for (Quest quest : targetNpc.getTemplate().getEventQuests(EventHandler.SPELLED))
					quest.onSpelled(targetNpc, player, skillOnMagic);
			}
		}
	}
}
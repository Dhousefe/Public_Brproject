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
package ext.mods.gameserver.network.clientpackets;

import ext.mods.commons.random.Rnd;
import ext.mods.commons.util.ArraysUtil;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.enums.SayType;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.Summon;
import ext.mods.gameserver.model.actor.instance.Door;
import ext.mods.gameserver.model.actor.instance.Pet;
import ext.mods.gameserver.model.actor.instance.Servitor;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.NpcSay;
import ext.mods.gameserver.skills.L2Skill;

public final class RequestActionUse extends L2GameClientPacket
{
	private static final int[] PASSIVE_SUMMONS =
	{
		12564,
		12621,
		14702,
		14703,
		14704,
		14705,
		14706,
		14707,
		14708,
		14709,
		14710,
		14711,
		14712,
		14713,
		14714,
		14715,
		14716,
		14717,
		14718,
		14719,
		14720,
		14721,
		14722,
		14723,
		14724,
		14725,
		14726,
		14727,
		14728,
		14729,
		14730,
		14731,
		14732,
		14733,
		14734,
		14735,
		14736
	};
	
	private static final int SIN_EATER_ID = 12564;
	private static final String[] SIN_EATER_ACTIONS_STRINGS =
	{
		"special skill? Abuses in this kind of place, can turn blood Knots...!",
		"Hey! Brother! What do you anticipate to me?",
		"shouts ha! Flap! Flap! Response?",
		", has not hit...!"
	};
	
	private int _actionId;
	private boolean _isCtrlPressed;
	private boolean _isShiftPressed;
	
	@Override
	protected void readImpl()
	{
		_actionId = readD();
		_isCtrlPressed = (readD() == 1);
		_isShiftPressed = (readC() == 1);
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
			
		if ((player.isFakeDeath() && _actionId != 0) || player.isDead() || player.isOutOfControl())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.isInObserverMode())
		{
			player.sendPacket(SystemMessageId.OBSERVERS_CANNOT_PARTICIPATE);
			return;
		}
		
		final Summon summon = player.getSummon();
		final WorldObject target = player.getTarget();
		
		switch (_actionId)
		{
			case 0:
				
				if (player.isFakeDeath())
				{
					player.stopFakeDeath(true);
					break;
				}
				
				if (player.isSitting() || player.isSittingNow())
					player.getAI().tryToStand();
				else
					player.getAI().tryToSit(target);
				break;
			
			case 1:
				if (player.isMounted())
					return;
				
				if (player.isRunning())
					player.forceWalkStance();
				else
					player.forceRunStance();
				break;
			
			case 10:
				player.tryOpenPrivateSellStore(false);
				break;
			
			case 28:
				player.tryOpenPrivateBuyStore();
				break;
			
			case 15, 21:
				if (summon == null)
					return;
				
				if (summon.getAI().getFollowStatus() && !player.isIn3DRadius(summon, 2000))
					return;
				
				if (summon.isOutOfControl())
				{
					player.sendPacket(SystemMessageId.PET_REFUSING_ORDER);
					return;
				}
				
				summon.getAI().switchFollowStatus();
				break;
			
			case 16, 22:
				if (target == null || summon == null || summon == target || player == target)
					return;
				
				if (target instanceof Creature targetCreature && targetCreature.isDead())
					return;
				
				if (ArraysUtil.contains(PASSIVE_SUMMONS, summon.getNpcId()))
					return;
				
				if (summon.isOutOfControl())
				{
					player.sendPacket(SystemMessageId.PET_REFUSING_ORDER);
					return;
				}
				
				if (summon instanceof Pet && (summon.getStatus().getLevel() - player.getStatus().getLevel() > 20))
				{
					player.sendPacket(SystemMessageId.PET_TOO_HIGH_TO_CONTROL);
					return;
				}
				
				summon.setTarget(target);
				
				if (target instanceof Creature targetCreature)
				{
					if (targetCreature.isAttackableWithoutForceBy(player) || (_isCtrlPressed && targetCreature.isAttackableBy(player)))
						summon.getAI().tryToAttack(targetCreature, _isCtrlPressed, _isShiftPressed);
					else
						summon.getAI().tryToFollow(targetCreature, _isShiftPressed);
				}
				else
					summon.getAI().tryToInteract(target, _isCtrlPressed, _isShiftPressed);
				break;
			
			case 17, 23:
				if (summon == null)
					return;
				
				if (summon.isOutOfControl())
				{
					player.sendPacket(SystemMessageId.PET_REFUSING_ORDER);
					return;
				}
				
				if (summon.getAttack().isAttackingNow())
					summon.getAttack().stop();
				
				summon.getAI().tryToIdle();
				break;
			
			case 19:
				if (!(summon instanceof Pet pet))
					return;
				
				if (pet.isDead())
					player.sendPacket(SystemMessageId.DEAD_PET_CANNOT_BE_RETURNED);
				else if (pet.isOutOfControl())
					player.sendPacket(SystemMessageId.PET_REFUSING_ORDER);
				else if (pet.getAttack().isAttackingNow() || pet.isInCombat())
					player.sendPacket(SystemMessageId.PET_CANNOT_SENT_BACK_DURING_BATTLE);
				else if (pet.checkUnsummonState())
					player.sendPacket(SystemMessageId.YOU_CANNOT_RESTORE_HUNGRY_PETS);
				else
					pet.unSummon(player);
				break;
			
			case 38:
				player.mountPlayer(summon);
				break;
			
			case 32:
				break;
			
			case 36:
				useSkill(4259, target);
				break;
			
			case 37:
				player.tryOpenWorkshop(true);
				break;
			
			case 39:
				useSkill(4138, target);
				break;
			
			case 41:
				if (!(target instanceof Door))
				{
					player.sendPacket(SystemMessageId.INVALID_TARGET);
					return;
				}
				
				useSkill(4230, target);
				break;
			
			case 42:
				useSkill(4378, player);
				break;
			
			case 43:
				useSkill(4137, target);
				break;
			
			case 44:
				useSkill(4139, target);
				break;
			
			case 45:
				useSkill(4025, player);
				break;
			
			case 46:
				useSkill(4261, target);
				break;
			
			case 47:
				useSkill(4260, target);
				break;
			
			case 48:
				useSkill(4068, target);
				break;
			
			case 51:
				player.tryOpenWorkshop(false);
				break;
			
			case 52:
				if (!(summon instanceof Servitor servitor))
					return;
				
				if (servitor.isDead())
					player.sendPacket(SystemMessageId.DEAD_PET_CANNOT_BE_RETURNED);
				else if (servitor.isOutOfControl())
					player.sendPacket(SystemMessageId.PET_REFUSING_ORDER);
				else if (servitor.getAttack().isAttackingNow() || servitor.isInCombat())
					player.sendPacket(SystemMessageId.PET_CANNOT_SENT_BACK_DURING_BATTLE);
				else
					servitor.unSummon(player);
				break;
			
			case 53, 54:
				if (target == null || summon == null || summon == target)
					return;
				
				if (summon.isOutOfControl())
				{
					player.sendPacket(SystemMessageId.PET_REFUSING_ORDER);
					return;
				}
				
				summon.getAI().setFollowStatus(false);
				
				if (target instanceof Creature targetCreature)
					summon.getAI().tryToFollow(targetCreature, _isShiftPressed);
				else
					summon.getAI().tryToInteract(target, _isCtrlPressed, _isShiftPressed);
				break;
			
			case 61:
				player.tryOpenPrivateSellStore(true);
				break;
			
			case 1000:
				if (!(target instanceof Door))
				{
					player.sendPacket(SystemMessageId.INVALID_TARGET);
					return;
				}
				
				useSkill(4079, target);
				break;
			
			case 1001:
				if (useSkill(4139, summon) && summon.getNpcId() == SIN_EATER_ID && Rnd.get(100) < 10)
					summon.broadcastPacket(new NpcSay(summon, SayType.ALL, Rnd.get(SIN_EATER_ACTIONS_STRINGS)));
				break;
			
			case 1003:
				useSkill(4710, target);
				break;
			
			case 1004:
				useSkill(4711, player);
				break;
			
			case 1005:
				useSkill(4712, target);
				break;
			
			case 1006:
				useSkill(4713, player);
				break;
			
			case 1007:
				useSkill(4699, player);
				break;
			
			case 1008:
				useSkill(4700, player);
				break;
			
			case 1009:
				useSkill(4701, target);
				break;
			
			case 1010:
				useSkill(4702, player);
				break;
			
			case 1011:
				useSkill(4703, player);
				break;
			
			case 1012:
				useSkill(4704, target);
				break;
			
			case 1013:
				useSkill(4705, target);
				break;
			
			case 1014:
				useSkill(4706, player);
				break;
			
			case 1015:
				useSkill(4707, target);
				break;
			
			case 1016:
				useSkill(4709, target);
				break;
			
			case 1017:
				useSkill(4708, target);
				break;
			
			case 1031:
				useSkill(5135, target);
				break;
			
			case 1032:
				useSkill(5136, target);
				break;
			
			case 1033:
				useSkill(5137, target);
				break;
			
			case 1034:
				useSkill(5138, target);
				break;
			
			case 1035:
				useSkill(5139, target);
				break;
			
			case 1036:
				useSkill(5142, target);
				break;
			
			case 1037:
				useSkill(5141, target);
				break;
			
			case 1038:
				useSkill(5140, target);
				break;
			
			case 1039:
				if (target instanceof Door)
				{
					player.sendPacket(SystemMessageId.INVALID_TARGET);
					return;
				}
				
				useSkill(5110, target);
				break;
			
			case 1040:
				if (target instanceof Door)
				{
					player.sendPacket(SystemMessageId.INVALID_TARGET);
					return;
				}
				
				useSkill(5111, target);
				break;
			
			default:
				LOGGER.warn("Unhandled action type {} detected for {}.", _actionId, player.getName());
		}
	}
	
	/**
	 * Cast a skill for active pet/servitor.
	 * @param skillId The id of the skill to launch.
	 * @param target The target is specified as a parameter but can be overwrited or ignored depending on skill type.
	 * @return true if you can use the skill, false otherwise.
	 */
	private boolean useSkill(int skillId, WorldObject target)
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return false;
		
		final Summon summon = player.getSummon();
		if (summon == null)
			return false;
		
		final int skillLevel = summon.getSkillLevel(skillId);
		if (skillLevel == 0)
			return false;
		
		final L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLevel);
		if (skill == null)
			return false;
		
		if (summon instanceof Pet pet && pet.getStatus().getLevel() - player.getStatus().getLevel() > 20)
			return false;
		
		summon.getAI().tryToCast((target instanceof Creature targetCreature) ? targetCreature : null, skill, _isCtrlPressed, _isShiftPressed, 0);
		return true;
	}
}
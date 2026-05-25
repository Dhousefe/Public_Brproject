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
package ext.mods.gameserver.network.serverpackets;

import ext.mods.Config;
import ext.mods.gameserver.data.manager.CursedWeaponManager;
import ext.mods.gameserver.enums.Paperdoll;
import ext.mods.gameserver.enums.TeamType;
import ext.mods.gameserver.enums.skills.AbnormalEffect;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.Summon;
import ext.mods.gameserver.model.actor.instance.Cubic;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.Weapon;

import ext.mods.dressme.holder.DressMeHolder;

public class UserInfo extends L2GameServerPacket
{
	private final Player _player;
	private int _relation;
	
	public UserInfo(Player player)
	{
		_player = player;
		
		_relation = _player.isClanLeader() ? 0x40 : 0;
		
		if (_player.getSiegeState() == 1)
			_relation |= 0x180;
		if (_player.getSiegeState() == 2)
			_relation |= 0x80;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x04);
		writeLoc(_player.getPosition());
		writeD(_player.getHeading());
		writeD(_player.getObjectId());
		writeS((_player.getPolymorphTemplate() != null) ? _player.getPolymorphTemplate().getName() : _player.getName());
		writeD(_player.getRace().ordinal());
		writeD(_player.getAppearance().getSex().ordinal());
		writeD((_player.getClassIndex() == 0) ? _player.getClassId().getId() : _player.getBaseClass());
		writeD(_player.getStatus().getLevel());
		writeQ(_player.getStatus().getExp());
		writeD(_player.getStatus().getSTR());
		writeD(_player.getStatus().getDEX());
		writeD(_player.getStatus().getCON());
		writeD(_player.getStatus().getINT());
		writeD(_player.getStatus().getWIT());
		writeD(_player.getStatus().getMEN());
		writeD(_player.getStatus().getMaxHp());
		writeD((int) _player.getStatus().getHp());
		writeD(_player.getStatus().getMaxMp());
		writeD((int) _player.getStatus().getMp());
		writeD(_player.getStatus().getSp());
		writeD(_player.getCurrentWeight());
		writeD(_player.getWeightLimit());
		writeD(_player.getActiveWeaponItem() != null ? 40 : 20);
		
		DressMeHolder armorSkin = _player.getArmorSkin();
		DressMeHolder weaponSkin = _player.getWeaponSkin();
		
		int hairallObj = _player.getInventory().getItemObjectIdFrom(Paperdoll.HAIRALL);
		writeD((armorSkin != null && hairallObj > 0 && armorSkin.getHelmetId() > 0) ? armorSkin.getHelmetId() : hairallObj);
		
		writeD(_player.getInventory().getItemObjectIdFrom(Paperdoll.REAR));
		writeD(_player.getInventory().getItemObjectIdFrom(Paperdoll.LEAR));
		writeD(_player.getInventory().getItemObjectIdFrom(Paperdoll.NECK));
		writeD(_player.getInventory().getItemObjectIdFrom(Paperdoll.RFINGER));
		writeD(_player.getInventory().getItemObjectIdFrom(Paperdoll.LFINGER));
		writeD(_player.getInventory().getItemObjectIdFrom(Paperdoll.HEAD));
		
		int rhandObj = _player.getInventory().getItemObjectIdFrom(Paperdoll.RHAND);
		int lhandObj = _player.getInventory().getItemObjectIdFrom(Paperdoll.LHAND);
		
		if (_player.isDressMe() && weaponSkin != null)
		{
			
			ItemInstance rhandInstance = _player.getInventory().getItemFrom(Paperdoll.RHAND);
			ItemInstance lhandInstance = _player.getInventory().getItemFrom(Paperdoll.LHAND);
			
			String equippedWeaponType = "";
			
			if (rhandInstance != null && rhandInstance.getItem() instanceof Weapon)
			{
				Weapon weapon = (Weapon) rhandInstance.getItem();
				equippedWeaponType = weapon.getItemType().toString().toLowerCase();
			}
			
			if (equippedWeaponType.equalsIgnoreCase(weaponSkin.getWeaponTypeVisual()))
			{
				if (weaponSkin.getTwoHandId() > 0)
				{
					rhandObj = weaponSkin.getTwoHandId();
					lhandObj = 0;
				}
				else
				{
					if (weaponSkin.getRightHandId() > 0 && rhandInstance != null)
						rhandObj = weaponSkin.getRightHandId();
					
					if (weaponSkin.getLeftHandId() > 0 && lhandInstance != null)
						lhandObj = weaponSkin.getLeftHandId();
				}
			}
			
		}
		
		writeD(rhandObj);
		writeD(lhandObj);
		
		int glovesOId = _player.getInventory().getItemObjectIdFrom(Paperdoll.GLOVES);
		int chestOId = _player.getInventory().getItemObjectIdFrom(Paperdoll.CHEST);
		int legsOId = _player.getInventory().getItemObjectIdFrom(Paperdoll.LEGS);
		int feetOId = _player.getInventory().getItemObjectIdFrom(Paperdoll.FEET);
		
		writeD((armorSkin != null && glovesOId > 0 && armorSkin.getGlovesId() > 0) ? armorSkin.getGlovesId() : glovesOId);
		writeD((armorSkin != null && chestOId > 0 && armorSkin.getChestId() > 0) ? armorSkin.getChestId() : chestOId);
		writeD((armorSkin != null && legsOId > 0 && armorSkin.getLegsId() > 0) ? armorSkin.getLegsId() : legsOId);
		writeD((armorSkin != null && feetOId > 0 && armorSkin.getFeetId() > 0) ? armorSkin.getFeetId() : feetOId);
		
		writeD(_player.getInventory().getItemObjectIdFrom(Paperdoll.CLOAK));
		writeD(rhandObj);
		
		int hairObj = _player.getInventory().getItemObjectIdFrom(Paperdoll.HAIR);
		writeD((armorSkin != null && hairObj > 0 && armorSkin.getHelmetId() > 0) ? armorSkin.getHelmetId() : hairObj);
		
		int faceObj = _player.getInventory().getItemObjectIdFrom(Paperdoll.FACE);
		writeD((armorSkin != null && faceObj > 0 && armorSkin.getHelmetId() > 0) ? armorSkin.getHelmetId() : faceObj);
		
		int hairall = _player.getInventory().getItemIdFrom(Paperdoll.HAIRALL);
		writeD((armorSkin != null && hairall > 0 && armorSkin.getHelmetId() > 0) ? armorSkin.getHelmetId() : hairall);
		
		writeD(_player.getInventory().getItemIdFrom(Paperdoll.REAR));
		writeD(_player.getInventory().getItemIdFrom(Paperdoll.LEAR));
		writeD(_player.getInventory().getItemIdFrom(Paperdoll.NECK));
		writeD(_player.getInventory().getItemIdFrom(Paperdoll.RFINGER));
		writeD(_player.getInventory().getItemIdFrom(Paperdoll.LFINGER));
		writeD(_player.getInventory().getItemIdFrom(Paperdoll.HEAD));
		
		int rhand = _player.getInventory().getItemIdFrom(Paperdoll.RHAND);
		int lhand = _player.getInventory().getItemIdFrom(Paperdoll.LHAND);
		
		if (_player.isDressMe() && weaponSkin != null)
		{
			
			ItemInstance rhandInstance = _player.getInventory().getItemFrom(Paperdoll.RHAND);
			ItemInstance lhandInstance = _player.getInventory().getItemFrom(Paperdoll.LHAND);
			
			String equippedWeaponType = "";
			
			if (rhandInstance != null && rhandInstance.getItem() instanceof Weapon)
			{
				Weapon weapon = (Weapon) rhandInstance.getItem();
				equippedWeaponType = weapon.getItemType().toString().toLowerCase();
			}
			
			if (equippedWeaponType.equalsIgnoreCase(weaponSkin.getWeaponTypeVisual()))
			{
				if (weaponSkin.getTwoHandId() > 0)
				{
					rhand = weaponSkin.getTwoHandId();
					lhand = 0;
				}
				else
				{
					if (weaponSkin.getRightHandId() > 0 && rhandInstance != null)
						rhand = weaponSkin.getRightHandId();
					
					if (weaponSkin.getLeftHandId() > 0 && lhandInstance != null)
						lhand = weaponSkin.getLeftHandId();
				}
			}
			
		}
		
		writeD(rhand);
		writeD(lhand);
		
		int glovesId = _player.getInventory().getItemIdFrom(Paperdoll.GLOVES);
		int chestId = _player.getInventory().getItemIdFrom(Paperdoll.CHEST);
		int legsId = _player.getInventory().getItemIdFrom(Paperdoll.LEGS);
		int feetId = _player.getInventory().getItemIdFrom(Paperdoll.FEET);
		
		writeD((armorSkin != null && glovesId > 0 && armorSkin.getGlovesId() > 0) ? armorSkin.getGlovesId() : glovesId);
		writeD((armorSkin != null && chestId > 0 && armorSkin.getChestId() > 0) ? armorSkin.getChestId() : chestId);
		writeD((armorSkin != null && legsId > 0 && armorSkin.getLegsId() > 0) ? armorSkin.getLegsId() : legsId);
		writeD((armorSkin != null && feetId > 0 && armorSkin.getFeetId() > 0) ? armorSkin.getFeetId() : feetId);
		
		writeD(_player.getInventory().getItemIdFrom(Paperdoll.CLOAK));
		writeD(rhand);
		
		int hair = _player.getInventory().getItemIdFrom(Paperdoll.HAIR);
		writeD((armorSkin != null && hair > 0 && armorSkin.getHelmetId() > 0) ? armorSkin.getHelmetId() : hair);
		int face = _player.getInventory().getItemIdFrom(Paperdoll.FACE);
		writeD((armorSkin != null && face > 0 && armorSkin.getHelmetId() > 0) ? armorSkin.getHelmetId() : face);
		
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeD(_player.getInventory().getAugmentationIdFrom(Paperdoll.RHAND));
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeD(_player.getInventory().getAugmentationIdFrom(Paperdoll.LHAND));
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		
		writeD(_player.getStatus().getPAtk(null));
		writeD(_player.getStatus().getPAtkSpd());
		writeD(_player.getStatus().getPDef(null));
		writeD(_player.getStatus().getEvasionRate(null));
		writeD(_player.getStatus().getAccuracy());
		writeD(_player.getStatus().getCriticalHit(null, null));
		writeD(_player.getStatus().getMAtk(null, null));
		writeD(_player.getStatus().getMAtkSpd());
		writeD(_player.getStatus().getPAtkSpd());
		writeD(_player.getStatus().getMDef(null, null));
		writeD(_player.getPvpFlag());
		writeD(_player.getKarma());
		
		final int runSpd = _player.getStatus().getBaseRunSpeed();
		final int walkSpd = _player.getStatus().getBaseWalkSpeed();
		final int swimSpd = _player.getStatus().getBaseSwimSpeed();
		
		writeD(runSpd);
		writeD(walkSpd);
		writeD(swimSpd);
		writeD(swimSpd);
		writeD(0);
		writeD(0);
		writeD((_player.isFlying()) ? runSpd : 0);
		writeD((_player.isFlying()) ? walkSpd : 0);
		
		writeF(_player.getStatus().getMovementSpeedMultiplier());
		writeF(_player.getStatus().getAttackSpeedMultiplier());
		
		final Summon summon = _player.getSummon();
		if (_player.isMounted() && summon != null)
		{
			writeF(summon.getCollisionRadius());
			writeF(summon.getCollisionHeight());
		}
		else
		{
			writeF(_player.getCollisionRadius());
			writeF(_player.getCollisionHeight());
		}
		
		writeD(_player.getAppearance().getHairStyle());
		writeD(_player.getAppearance().getHairColor());
		writeD(_player.getAppearance().getFace());
		writeD((_player.isGM()) ? 1 : 0);
		
		writeS((_player.getPolymorphTemplate() != null) ? "Morphed" : _player.getTitle());
		
		writeD(_player.getClanId());
		writeD(_player.getClanCrestId());
		writeD(_player.getAllyId());
		writeD(_player.getAllyCrestId());
		writeD(_relation);
		writeC(_player.getMountType());
		writeC(_player.getOperateType().getId());
		writeC((_player.hasCrystallize()) ? 1 : 0);
		writeD(_player.getPkKills());
		writeD(_player.getPvpKills());
		
		writeH(_player.getCubicList().size());
		for (final Cubic cubic : _player.getCubicList())
			writeH(cubic.getId());
		
		writeC((_player.isInPartyMatchRoom()) ? 1 : 0);
		writeD((!_player.getAppearance().isVisible() && _player.isGM()) ? (_player.getAbnormalEffect() | AbnormalEffect.STEALTH.getMask()) : _player.getAbnormalEffect());
		writeC(0x00);
		writeD(_player.getClanPrivileges());
		writeH(_player.getRecomLeft());
		writeH(_player.getRecomHave());
		writeD((_player.getMountNpcId() > 0) ? _player.getMountNpcId() + 1000000 : 0);
		writeH(_player.getStatus().getInventoryLimit());
		writeD(_player.getClassId().getId());
		writeD(0x00);
		writeD(_player.getStatus().getMaxCp());
		writeD((int) _player.getStatus().getCp());
		writeC((_player.isMounted()) ? 0 : _player.getEnchantEffect());
		writeC((Config.PLAYER_SPAWN_PROTECTION > 0 && _player.isSpawnProtected()) ? TeamType.BLUE.getId() : _player.getTeam().getId());
		writeD(_player.getClanCrestLargeId());
		writeC((_player.isNoble()) ? 1 : 0);
		writeC((_player.isHero() || _player.getHeroAura() || (_player.isGM() && Config.GM_HERO_AURA)) ? 1 : 0);
		writeC((_player.isFishing()) ? 1 : 0);
		writeLoc(_player.getFishingStance().getLoc());
		writeD(_player.getAppearance().getNameColor());
		writeC((_player.isRunning()) ? 0x01 : 0x00);
		writeD(_player.getPledgeClass());
		writeD(_player.getPledgeType());
		writeD(_player.getAppearance().getTitleColor());
		writeD(CursedWeaponManager.getInstance().getCurrentStage(_player.getCursedWeaponEquippedId()));
	}
}
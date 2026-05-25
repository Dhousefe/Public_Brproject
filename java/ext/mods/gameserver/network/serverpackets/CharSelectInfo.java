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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ext.mods.commons.pool.ConnectionPool;

import ext.mods.gameserver.data.sql.ClanTable;
import ext.mods.gameserver.enums.Paperdoll;
import ext.mods.gameserver.model.CharSelectSlot;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.network.GameClient;

public class CharSelectInfo extends L2GameServerPacket
{
	private static final String SELECT_INFOS = "SELECT obj_Id, char_name, level, maxHp, curHp, maxMp, curMp, face, hairStyle, hairColor, sex, heading, x, y, z, exp, sp, karma, pvpkills, pkkills, clanid, race, classid, deletetime, title, accesslevel, lastAccess, base_class FROM characters WHERE account_name=?";
	private static final String SELECT_CURRENT_SUBCLASS = "SELECT exp, sp, level FROM character_subclasses WHERE char_obj_id=? && class_id=? ORDER BY char_obj_id";
	private static final String SELECT_AUGMENTS = "SELECT attributes FROM augmentations WHERE item_oid = ?";
	
	private final CharSelectSlot[] _slots;
	private final String _loginName;
	private final int _sessionId;
	
	private int _activeId;
	
	public CharSelectInfo(String loginName, int sessionId)
	{
		_slots = loadCharSelectSlots(loginName);
		_sessionId = sessionId;
		_loginName = loginName;
		
		_activeId = -1;
	}
	
	public CharSelectInfo(String loginName, int sessionId, int activeId)
	{
		_slots = loadCharSelectSlots(loginName);
		_sessionId = sessionId;
		_loginName = loginName;
		
		_activeId = activeId;
	}
	
	@Override
	protected final void writeImpl()
	{
		final int size = _slots.length;
		
		writeC(0x13);
		writeD(size);
		
		long lastAccess = 0L;
		
		if (_activeId == -1)
		{
			for (int i = 0; i < size; i++)
				if (lastAccess < _slots[i].getLastAccess())
				{
					lastAccess = _slots[i].getLastAccess();
					_activeId = i;
				}
		}
		
		for (int i = 0; i < size; i++)
		{
			CharSelectSlot slot = _slots[i];
			
			writeS(slot.getName());
			writeD(slot.getCharId());
			writeS(_loginName);
			writeD(_sessionId);
			writeD(slot.getClanId());
			writeD(0x00);
			
			writeD(slot.getSex());
			writeD(slot.getRace());
			writeD(slot.getBaseClassId());
			
			writeD(0x01);
			
			writeD(slot.getX());
			writeD(slot.getY());
			writeD(slot.getZ());
			
			writeF(slot.getCurrentHp());
			writeF(slot.getCurrentMp());
			
			writeD(slot.getSp());
			writeQ(slot.getExp());
			writeD(slot.getLevel());
			
			writeD(slot.getKarma());
			writeD(slot.getPkKills());
			writeD(slot.getPvpKills());
			
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			
			boolean isDressMe = slot.getArmorSkin() != null;
			boolean isWeaponSkin = slot.getWeaponSkin() != null;
			writeD(isDressMe ? slot.getArmorSkin().getHelmetId() : slot.getPaperdollObjectId(Paperdoll.HAIRALL));
			writeD(slot.getPaperdollObjectId(Paperdoll.REAR));
			writeD(slot.getPaperdollObjectId(Paperdoll.LEAR));
			writeD(slot.getPaperdollObjectId(Paperdoll.NECK));
			writeD(slot.getPaperdollObjectId(Paperdoll.RFINGER));
			writeD(slot.getPaperdollObjectId(Paperdoll.LFINGER));
			writeD(slot.getPaperdollObjectId(Paperdoll.HEAD));
			
			writeD(isWeaponSkin ? slot.getWeaponSkin().getRightHandId() : slot.getPaperdollObjectId(Paperdoll.RHAND));
			writeD(isWeaponSkin ? slot.getWeaponSkin().getLeftHandId() : slot.getPaperdollObjectId(Paperdoll.LHAND));
			
			writeD(isDressMe ? slot.getArmorSkin().getGlovesId() : slot.getPaperdollObjectId(Paperdoll.GLOVES));
			writeD(isDressMe ? slot.getArmorSkin().getChestId() : slot.getPaperdollObjectId(Paperdoll.CHEST));
			writeD(isDressMe ? slot.getArmorSkin().getLegsId() : slot.getPaperdollObjectId(Paperdoll.LEGS));
			writeD(isDressMe ? slot.getArmorSkin().getFeetId() : slot.getPaperdollObjectId(Paperdoll.FEET));
			
			writeD(slot.getPaperdollObjectId(Paperdoll.CLOAK));
			writeD(slot.getPaperdollObjectId(Paperdoll.RHAND));
			writeD(isDressMe ? slot.getArmorSkin().getHelmetId() : slot.getPaperdollObjectId(Paperdoll.HAIR));
			writeD(slot.getPaperdollObjectId(Paperdoll.FACE));
			
			writeD(isDressMe ? slot.getArmorSkin().getHelmetId() : slot.getPaperdollItemId(Paperdoll.HAIRALL));
			writeD(slot.getPaperdollItemId(Paperdoll.REAR));
			writeD(slot.getPaperdollItemId(Paperdoll.LEAR));
			writeD(slot.getPaperdollItemId(Paperdoll.NECK));
			writeD(slot.getPaperdollItemId(Paperdoll.RFINGER));
			writeD(slot.getPaperdollItemId(Paperdoll.LFINGER));
			writeD(slot.getPaperdollItemId(Paperdoll.HEAD));
			
			writeD(isWeaponSkin ? slot.getWeaponSkin().getRightHandId() : slot.getPaperdollItemId(Paperdoll.RHAND));
			writeD(isWeaponSkin ? slot.getWeaponSkin().getLeftHandId() : slot.getPaperdollItemId(Paperdoll.LHAND));
			
			writeD(isDressMe ? slot.getArmorSkin().getGlovesId() : slot.getPaperdollItemId(Paperdoll.GLOVES));
			writeD(isDressMe ? slot.getArmorSkin().getChestId() : slot.getPaperdollItemId(Paperdoll.CHEST));
			writeD(isDressMe ? slot.getArmorSkin().getLegsId() : slot.getPaperdollItemId(Paperdoll.LEGS));
			writeD(isDressMe ? slot.getArmorSkin().getFeetId() : slot.getPaperdollItemId(Paperdoll.FEET));
			writeD(slot.getPaperdollItemId(Paperdoll.CLOAK));
			writeD(isWeaponSkin ? slot.getWeaponSkin().getRightHandId() : slot.getPaperdollItemId(Paperdoll.RHAND));
			writeD(isDressMe ? slot.getArmorSkin().getHelmetId() : slot.getPaperdollItemId(Paperdoll.HAIR));
			writeD(isDressMe ? slot.getArmorSkin().getHelmetId() : slot.getPaperdollItemId(Paperdoll.FACE));
			
			writeD(slot.getHairStyle());
			writeD(slot.getHairColor());
			writeD(slot.getFace());
			
			writeF(slot.getMaxHp());
			writeF(slot.getMaxMp());
			
			writeD((slot.getAccessLevel() > -1) ? ((slot.getDeleteTimer() > 0) ? (int) ((slot.getDeleteTimer() - System.currentTimeMillis()) / 1000) : 0) : -1);
			writeD(slot.getClassId());
			writeD((i == _activeId) ? 0x01 : 0x00);
			writeC(Math.min(127, slot.getEnchantEffect()));
			writeD(slot.getAugmentationId());
		}
		getClient().setCharSelectSlot(_slots);
	}
	
	public CharSelectSlot[] getCharacterSlots()
	{
		return _slots;
	}
	
	private static CharSelectSlot[] loadCharSelectSlots(String loginName)
	{
		final List<CharSelectSlot> list = new ArrayList<>();
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_INFOS))
		{
			ps.setString(1, loginName);
			
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					final int objectId = rs.getInt("obj_id");
					final String name = rs.getString("char_name");
					
					final long deleteTime = rs.getLong("deletetime");
					if (deleteTime > 0 && System.currentTimeMillis() > deleteTime)
					{
						final Clan clan = ClanTable.getInstance().getClan(rs.getInt("clanid"));
						if (clan != null)
							clan.removeClanMember(objectId, 0);
						
						GameClient.deleteCharByObjId(objectId);
						continue;
					}
					
					final CharSelectSlot slot = new CharSelectSlot(objectId, name);
					slot.setAccessLevel(rs.getInt("accesslevel"));
					slot.setLevel(rs.getInt("level"));
					slot.setMaxHp(rs.getInt("maxhp"));
					slot.setCurrentHp(rs.getDouble("curhp"));
					slot.setMaxMp(rs.getInt("maxmp"));
					slot.setCurrentMp(rs.getDouble("curmp"));
					slot.setKarma(rs.getInt("karma"));
					slot.setPkKills(rs.getInt("pkkills"));
					slot.setPvpKills(rs.getInt("pvpkills"));
					slot.setFace(rs.getInt("face"));
					slot.setHairStyle(rs.getInt("hairstyle"));
					slot.setHairColor(rs.getInt("haircolor"));
					slot.setSex(rs.getInt("sex"));
					slot.setExp(rs.getLong("exp"));
					slot.setSp(rs.getInt("sp"));
					slot.setClanId(rs.getInt("clanid"));
					slot.setRace(rs.getInt("race"));
					slot.setX(rs.getInt("x"));
					slot.setY(rs.getInt("y"));
					slot.setZ(rs.getInt("z"));
					
					final int baseClassId = rs.getInt("base_class");
					final int activeClassId = rs.getInt("classid");
					
					if (baseClassId != activeClassId)
					{
						try (PreparedStatement ps2 = con.prepareStatement(SELECT_CURRENT_SUBCLASS))
						{
							ps2.setInt(1, objectId);
							ps2.setInt(2, activeClassId);
							
							try (ResultSet rs2 = ps2.executeQuery())
							{
								if (rs2.next())
								{
									slot.setExp(rs2.getLong("exp"));
									slot.setSp(rs2.getInt("sp"));
									slot.setLevel(rs2.getInt("level"));
								}
							}
						}
					}
					
					slot.setClassId(activeClassId);
					
					final int weaponObjId = slot.getPaperdollObjectId(Paperdoll.RHAND);
					if (weaponObjId > 0)
					{
						try (PreparedStatement ps3 = con.prepareStatement(SELECT_AUGMENTS))
						{
							ps3.setInt(1, weaponObjId);
							
							try (ResultSet rs3 = ps3.executeQuery())
							{
								if (rs3.next())
								{
									final int augment = rs3.getInt("attributes");
									slot.setAugmentationId((augment == -1) ? 0 : augment);
								}
							}
						}
					}
					
					slot.setBaseClassId((baseClassId == 0 && activeClassId > 0) ? activeClassId : baseClassId);
					slot.setDeleteTimer(deleteTime);
					slot.setLastAccess(rs.getLong("lastAccess"));
					
					list.add(slot);
				}
			}
			
			return list.toArray(new CharSelectSlot[list.size()]);
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't restore player slots for account {}.", e, loginName);
		}
		
		return new CharSelectSlot[0];
	}
}
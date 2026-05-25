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

import ext.mods.gameserver.enums.Paperdoll;
import ext.mods.gameserver.model.actor.Player;

public class GMViewCharacterInfo extends L2GameServerPacket
{
	private final Player _player;
	
	public GMViewCharacterInfo(Player player)
	{
		_player = player;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x8f);
		
		writeD(_player.getX());
		writeD(_player.getY());
		writeD(_player.getZ());
		writeD(_player.getHeading());
		writeD(_player.getObjectId());
		writeS(_player.getName());
		writeD(_player.getRace().ordinal());
		writeD(_player.getAppearance().getSex().ordinal());
		writeD(_player.getClassId().getId());
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
		writeD(0x28);
		
		writeD(_player.getInventory().getItemObjectIdFrom(Paperdoll.HAIRALL));
		writeD(_player.getInventory().getItemObjectIdFrom(Paperdoll.REAR));
		writeD(_player.getInventory().getItemObjectIdFrom(Paperdoll.LEAR));
		writeD(_player.getInventory().getItemObjectIdFrom(Paperdoll.NECK));
		writeD(_player.getInventory().getItemObjectIdFrom(Paperdoll.RFINGER));
		writeD(_player.getInventory().getItemObjectIdFrom(Paperdoll.LFINGER));
		writeD(_player.getInventory().getItemObjectIdFrom(Paperdoll.HEAD));
		writeD(_player.getInventory().getItemObjectIdFrom(Paperdoll.RHAND));
		writeD(_player.getInventory().getItemObjectIdFrom(Paperdoll.LHAND));
		writeD(_player.getInventory().getItemObjectIdFrom(Paperdoll.GLOVES));
		writeD(_player.getInventory().getItemObjectIdFrom(Paperdoll.CHEST));
		writeD(_player.getInventory().getItemObjectIdFrom(Paperdoll.LEGS));
		writeD(_player.getInventory().getItemObjectIdFrom(Paperdoll.FEET));
		writeD(_player.getInventory().getItemObjectIdFrom(Paperdoll.CLOAK));
		writeD(_player.getInventory().getItemObjectIdFrom(Paperdoll.RHAND));
		writeD(_player.getInventory().getItemObjectIdFrom(Paperdoll.HAIR));
		writeD(_player.getInventory().getItemObjectIdFrom(Paperdoll.FACE));
		
		writeD(_player.getInventory().getItemIdFrom(Paperdoll.HAIRALL));
		writeD(_player.getInventory().getItemIdFrom(Paperdoll.REAR));
		writeD(_player.getInventory().getItemIdFrom(Paperdoll.LEAR));
		writeD(_player.getInventory().getItemIdFrom(Paperdoll.NECK));
		writeD(_player.getInventory().getItemIdFrom(Paperdoll.RFINGER));
		writeD(_player.getInventory().getItemIdFrom(Paperdoll.LFINGER));
		writeD(_player.getInventory().getItemIdFrom(Paperdoll.HEAD));
		writeD(_player.getInventory().getItemIdFrom(Paperdoll.RHAND));
		writeD(_player.getInventory().getItemIdFrom(Paperdoll.LHAND));
		writeD(_player.getInventory().getItemIdFrom(Paperdoll.GLOVES));
		writeD(_player.getInventory().getItemIdFrom(Paperdoll.CHEST));
		writeD(_player.getInventory().getItemIdFrom(Paperdoll.LEGS));
		writeD(_player.getInventory().getItemIdFrom(Paperdoll.FEET));
		writeD(_player.getInventory().getItemIdFrom(Paperdoll.CLOAK));
		writeD(_player.getInventory().getItemIdFrom(Paperdoll.RHAND));
		writeD(_player.getInventory().getItemIdFrom(Paperdoll.HAIR));
		writeD(_player.getInventory().getItemIdFrom(Paperdoll.FACE));
		
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
		writeH(0x00);
		writeH(0x00);
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
		
		int _runSpd = _player.getStatus().getBaseRunSpeed();
		int _walkSpd = _player.getStatus().getBaseWalkSpeed();
		int _swimSpd = _player.getStatus().getBaseSwimSpeed();
		writeD(_runSpd);
		writeD(_walkSpd);
		writeD(_swimSpd);
		writeD(_swimSpd);
		writeD(0);
		writeD(0);
		writeD(_player.isFlying() ? _runSpd : 0);
		writeD(_player.isFlying() ? _walkSpd : 0);
		writeF(_player.getStatus().getMovementSpeedMultiplier());
		writeF(_player.getStatus().getAttackSpeedMultiplier());
		
		writeF(_player.getCollisionRadius());
		writeF(_player.getCollisionHeight());
		writeD(_player.getAppearance().getHairStyle());
		writeD(_player.getAppearance().getHairColor());
		writeD(_player.getAppearance().getFace());
		writeD(_player.isGM() ? 0x01 : 0x00);
		
		writeS(_player.getTitle());
		writeD(_player.getClanId());
		writeD(_player.getClanCrestId());
		writeD(_player.getAllyId());
		writeC(_player.getMountType());
		writeC(_player.getOperateType().getId());
		writeC(_player.hasCrystallize() ? 1 : 0);
		writeD(_player.getPkKills());
		writeD(_player.getPvpKills());
		
		writeH(_player.getRecomLeft());
		writeH(_player.getRecomHave());
		writeD(_player.getClassId().getId());
		writeD(0x00);
		writeD(_player.getStatus().getMaxCp());
		writeD((int) _player.getStatus().getCp());
		
		writeC(_player.isRunning() ? 0x01 : 0x00);
		
		writeC(321);
		
		writeD(_player.getPledgeClass());
		
		writeC(_player.isNoble() ? 0x01 : 0x00);
		writeC(_player.isHero() ? 0x01 : 0x00);
		
		writeD(_player.getAppearance().getNameColor());
		writeD(_player.getAppearance().getTitleColor());
	}
}
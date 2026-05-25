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

import java.text.DecimalFormat;

import ext.mods.Config;
import ext.mods.gameserver.custom.data.PolymorphData.Polymorph;
import ext.mods.gameserver.data.sql.ClanTable;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.Summon;
import ext.mods.gameserver.model.actor.instance.Monster;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.pledge.Clan;

public abstract class AbstractNpcInfo extends L2GameServerPacket
{
	protected int _x;
	protected int _y;
	protected int _z;
	protected int _heading;
	
	protected int _idTemplate;
	
	protected boolean _isAttackable;
	protected boolean _isSummoned;
	
	protected int _mAtkSpd;
	protected int _pAtkSpd;
	protected int _runSpd;
	protected int _walkSpd;
	
	protected int _rhand;
	protected int _lhand;
	protected int _chest;
	protected int _enchantEffect;
	
	protected double _collisionHeight;
	protected double _collisionRadius;
	
	protected int _clanCrest;
	protected int _allyCrest;
	protected int _allyId;
	protected int _clanId;
	
	protected String _name = "";
	protected String _title = "";
	
	protected AbstractNpcInfo(Creature creature)
	{
		_x = creature.getX();
		_y = creature.getY();
		_z = creature.getZ();
		_heading = creature.getHeading();
		
		_isSummoned = creature.isShowSummonAnimation();
		
		_mAtkSpd = creature.getStatus().getMAtkSpd();
		_pAtkSpd = creature.getStatus().getPAtkSpd();
		_runSpd = creature.getStatus().getBaseRunSpeed();
		_walkSpd = creature.getStatus().getBaseWalkSpeed();
	}

	private static String buildNpcTitle(Monster monster, String baseTitle, String npcName)
	{
		String format = Config.SHOW_NPC_TITLE_FORMAT;
		if (format == null || format.isEmpty())
			format = "Lvl %level% %aggro% %title%";

		String aggro = monster.getTemplate().getAggro() ? "*" : "";
		String title = (baseTitle == null) ? "" : baseTitle;
		String name = (npcName == null) ? "" : npcName;

		String result = format
			.replace("%level%", String.valueOf(monster.getStatus().getLevel()))
			.replace("%aggro%", aggro)
			.replace("%title%", title)
			.replace("%name%", name)
			.replace("%id%", String.valueOf(monster.getTemplate().getNpcId()));

		result = normalizeNpcTitle(result);

		return result;
	}

	private static String normalizeNpcTitle(String title)
	{
		String trimmed = (title == null) ? "" : title.trim();
		return trimmed.replaceAll("\\s{2,}", " ");
	}

	private static String applyNpcTitleColor(String title)
	{
		String color = Config.SHOW_NPC_TITLE_COLOR;
		if (color == null || color.isEmpty() || !color.matches("[0-9A-Fa-f]{6}"))
			return title;

		if (title == null || title.isEmpty())
			return title;

		if (title.startsWith("a,"))
			return title.substring(2).trim();

		return title;
	}
	
	/**
	 * Packet for Npcs
	 */
	public static class NpcInfo extends AbstractNpcInfo
	{
		private final Npc _npc;
		
		public NpcInfo(Npc npc, Player attacker)
		{
			super(npc);
			
			_npc = npc;
			
			_enchantEffect = _npc.getEnchantEffect();
			_isAttackable = _npc.isAttackableWithoutForceBy(attacker);
			
			if (_npc.getPolymorphTemplate() != null)
			{
				_idTemplate = _npc.getPolymorphTemplate().getIdTemplate();
				
				_rhand = _npc.getPolymorphTemplate().getRightHand();
				_lhand = _npc.getPolymorphTemplate().getLeftHand();
				
				_collisionHeight = _npc.getPolymorphTemplate().getCollisionHeight();
				_collisionRadius = _npc.getPolymorphTemplate().getCollisionRadius();
			}
			else
			{
				_idTemplate = _npc.getTemplate().getIdTemplate();
				
				_rhand = _npc.getRightHandItemId();
				_lhand = _npc.getLeftHandItemId();
				
				_collisionHeight = _npc.getCollisionHeight();
				_collisionRadius = _npc.getCollisionRadius();
			}
			
			if (_npc.getTemplate().isUsingServerSideName())
				_name = _npc.getName();
			
			String npcTitle = _npc.getTitle();
			String templateTitle = _npc.getTemplate().getTitle();
			boolean hasCustomTitle = npcTitle != null && !npcTitle.isEmpty() && !npcTitle.equals(templateTitle);
			
			if (hasCustomTitle)
			{
				_title = npcTitle;
			}
			else if (_npc.getInstanceMap() != null && _npc.getInstanceMap().getId() > 0)
			{
				_title = npcTitle;
			}
			else
			{
				if (_npc.isChampion())
					_title = "Champion";
				else if (_npc.getTemplate().isUsingServerSideTitle())
					_title = npcTitle;
			}
			
			if (Config.SHOW_NPC_LVL && _npc instanceof Monster monster)
				_title = buildNpcTitle(monster, _title, _name);
			
			if (_npc instanceof Monster)
				_title = applyNpcTitleColor(_title);
			
			if (Config.SHOW_NPC_CREST)
			{
				if (_npc.getCastle() != null && _npc.getCastle().getOwnerId() != 0)
				{
					Clan clan = ClanTable.getInstance().getClan(_npc.getCastle().getOwnerId());
					_clanCrest = clan.getCrestId();
					_clanId = clan.getClanId();
					_allyCrest = clan.getAllyCrestId();
					_allyId = clan.getAllyId();
				}
				else if (_npc.getClanId() != 0)
				{
					Clan clan = ClanTable.getInstance().getClan(_npc.getClanId());
					_clanCrest = clan.getCrestId();
					_clanId = clan.getClanId();
					_allyCrest = clan.getAllyCrestId();
					_allyId = clan.getAllyId();
				}
			}
		}
		
		@Override
		protected void writeImpl()
		{
			Polymorph fpc = _npc.getFakePc();
			
			if (fpc != null)
			{
				writeC(0x03);
				writeD(_x);
				writeD(_y);
				writeD(_z);
				writeD(_heading);
				writeD(_npc.getObjectId());
				writeS(fpc.name());
				writeD(fpc.race());
				writeD(fpc.sex());
				writeD(fpc.classId());
				writeD(0x00);
				writeD(0x00);
				writeD(fpc.rightHand());
				writeD(fpc.leftHand());
				writeD(fpc.gloves());
				writeD(fpc.chest());
				writeD(fpc.legs());
				writeD(fpc.feet());
				writeD(fpc.hero());
				writeD(fpc.rightHand());
				writeD(fpc.hair());
				writeD(fpc.hair2());
				
				for (int i = 0; i < 24; i++)
					writeH(0);
				
				writeD(0x00);
				writeD(0x00);
				writeD(_mAtkSpd);
				writeD(_pAtkSpd);
				writeD(0x00);
				writeD(0x00);
				writeD(_runSpd);
				writeD(_walkSpd);
				writeD(_runSpd);
				writeD(_walkSpd);
				writeD(_runSpd);
				writeD(_walkSpd);
				writeD(_runSpd);
				writeD(_walkSpd);
				writeF(_npc.getStatus().getMovementSpeedMultiplier());
				writeF(_npc.getStatus().getAttackSpeedMultiplier());
				
				writeF(fpc.radius());
				writeF(fpc.height());
				writeD(fpc.hairStyle());
				writeD(fpc.hairColor());
				writeD(fpc.face());
				
				if (_npc instanceof Monster)
					writeS(fpc.title() + " - HP " + new DecimalFormat("#.##").format(100.0 * _npc.getStatus().getHp() / _npc.getStatus().getMaxHp()) + "%");
				else
					writeS(fpc.title());
				
				writeD(fpc.clanId());
				writeD(fpc.clanCrest());
				writeD(fpc.allyId());
				writeD(fpc.allyCrest());
				writeD(0x00);
				writeC(0x01);
				writeC(_npc.isRunning() ? 1 : 0);
				writeC(_npc.isInCombat() ? 1 : 0);
				writeC(_npc.isAlikeDead() ? 1 : 0);
				
				for (int i = 0; i < 3; i++)
					writeC(0);
				
				writeH(0x00);
				writeC(0x00);
				writeD(0x00);
				writeC(0x00);
				writeH(0x00);
				writeD(fpc.classId());
				writeD(0x00);
				writeD(0x00);
				writeC(fpc.enchant());
				writeC(0x00);
				writeD(0x00);
				writeC(0x00);
				writeC(fpc.hero());
				writeC(0x00);
				
				for (int i = 0; i < 3; i++)
					writeD(0);
				
				writeD(fpc.nameColor());
				writeD(_heading);
				writeD(0x00);
				writeD(0x00);
				writeD(fpc.titleColor());
				writeD(0x00);
			}
			else
			{
				writeC(0x16);
				
				writeD(_npc.getObjectId());
				writeD(_idTemplate + 1000000);
				writeD(_isAttackable ? 1 : 0);
				
				writeD(_x);
				writeD(_y);
				writeD(_z);
				writeD(_heading);
				
				writeD(0x00);
				
				writeD(_mAtkSpd);
				writeD(_pAtkSpd);
				writeD(_runSpd);
				writeD(_walkSpd);
				writeD(_runSpd);
				writeD(_walkSpd);
				writeD(_runSpd);
				writeD(_walkSpd);
				writeD(_runSpd);
				writeD(_walkSpd);
				
				writeF(_npc.getStatus().getMovementSpeedMultiplier());
				writeF(_npc.getStatus().getAttackSpeedMultiplier());
				
				writeF(_collisionRadius);
				writeF(_collisionHeight);
				
				writeD(_rhand);
				writeD(_chest);
				writeD(_lhand);
				
				writeC(1);
				writeC(_npc.isRunning() ? 1 : 0);
				writeC(_npc.isInCombat() ? 1 : 0);
				writeC(_npc.isAlikeDead() ? 1 : 0);
				writeC(_isSummoned ? 2 : 2);
				
				writeS(_name);
				writeS(_title);
				
				writeD(0x00);
				writeD(0x00);
				writeD(0x00);
				
				writeD(_npc.getAbnormalEffect());
				
				writeD(_clanId);
				writeD(_clanCrest);
				writeD(_allyId);
				writeD(_allyCrest);
				
				writeC(_npc.getMove().getMoveType().getId());
				
				if (Config.CHAMPION_FREQUENCY > 0)
					writeC(_npc.isChampion() ? Config.CHAMPION_AURA : 0);
				else
					writeC(0x00);
					
				writeF(_collisionRadius);
				writeF(_collisionHeight);
				
				writeD(_enchantEffect);
				writeD(_npc.isFlying() ? 1 : 0);
			}
		}
	}
	
	/**
	 * Packet for summons
	 */
	public static class SummonInfo extends AbstractNpcInfo
	{
		private final Summon _summon;
		private final Player _owner;
		private final int _summonAnimation;
		
		public SummonInfo(Summon summon, Player attacker, int val)
		{
			super(summon);
			
			_summon = summon;
			_owner = _summon.getOwner();
			
			_summonAnimation = (_summon.isShowSummonAnimation()) ? 2 : val;
			
			_isAttackable = _summon.isAttackableWithoutForceBy(attacker);
			_rhand = _summon.getWeapon();
			_chest = _summon.getArmor();
			_title = (_owner == null || !_owner.isOnline()) ? "" : _owner.getName();
			_idTemplate = _summon.getTemplate().getIdTemplate();
			
			_collisionHeight = _summon.getCollisionHeight();
			_collisionRadius = _summon.getCollisionRadius();
			
			if (Config.SHOW_SUMMON_CREST && _owner != null && _owner.getClan() != null)
			{
				Clan clan = ClanTable.getInstance().getClan(_owner.getClanId());
				_clanCrest = clan.getCrestId();
				_clanId = clan.getClanId();
				_allyCrest = clan.getAllyCrestId();
				_allyId = clan.getAllyId();
			}
		}
		
		@Override
		protected void writeImpl()
		{
			if (_owner != null && !_owner.getAppearance().isVisible() && _owner != getClient().getPlayer())
				return;
			
			writeC(0x16);
			
			writeD(_summon.getObjectId());
			writeD(_idTemplate + 1000000);
			writeD(_isAttackable ? 1 : 0);
			
			writeD(_x);
			writeD(_y);
			writeD(_z);
			writeD(_heading);
			
			writeD(0x00);
			
			writeD(_mAtkSpd);
			writeD(_pAtkSpd);
			writeD(_runSpd);
			writeD(_walkSpd);
			writeD(_runSpd);
			writeD(_walkSpd);
			writeD(_runSpd);
			writeD(_walkSpd);
			writeD(_runSpd);
			writeD(_walkSpd);
			
			writeF(_summon.getStatus().getMovementSpeedMultiplier());
			writeF(_summon.getStatus().getAttackSpeedMultiplier());
			
			writeF(_collisionRadius);
			writeF(_collisionHeight);
			
			writeD(_rhand);
			writeD(_chest);
			writeD(_lhand);
			
			writeC(1);
			writeC(_summon.isRunning() ? 1 : 0);
			writeC(_summon.isInCombat() ? 1 : 0);
			writeC(_summon.isAlikeDead() ? 1 : 0);
			writeC(_summonAnimation);
			
			writeS(_name);
			writeS(_title);
			
			writeD(0x01);
			writeD(_summon.getPvpFlag());
			writeD(_summon.getKarma());
			
			writeD(_summon.getAbnormalEffect());
			
			writeD(_clanId);
			writeD(_clanCrest);
			writeD(_allyId);
			writeD(_allyCrest);
			
			writeC(_summon.getMove().getMoveType().getId());
			writeC(_summon.getTeam().getId());
			
			writeF(_collisionRadius);
			writeF(_collisionHeight);
			
			writeD(_enchantEffect);
			writeD(0x00);
		}
	}
	
	/**
	 * Packet for morphed PCs
	 */
	public static class PcMorphInfo extends AbstractNpcInfo
	{
		private final Player _pc;
		private final NpcTemplate _template;
		private final int _swimSpd;
		
		public PcMorphInfo(Player player, NpcTemplate template)
		{
			super(player);
			
			_pc = player;
			_template = template;
			
			_swimSpd = player.getStatus().getBaseSwimSpeed();
			
			_rhand = _template.getRightHand();
			_lhand = _template.getLeftHand();
			
			_collisionHeight = _template.getCollisionHeight();
			_collisionRadius = _template.getCollisionRadius();
		}
		
		@Override
		protected void writeImpl()
		{
			writeC(0x16);
			
			writeD(_pc.getObjectId());
			writeD(_template.getNpcId() + 1000000);
			writeD(1);
			
			writeD(_x);
			writeD(_y);
			writeD(_z);
			writeD(_heading);
			
			writeD(0x00);
			
			writeD(_mAtkSpd);
			writeD(_pAtkSpd);
			writeD(_runSpd);
			writeD(_walkSpd);
			writeD(_swimSpd);
			writeD(_swimSpd);
			writeD(_runSpd);
			writeD(_walkSpd);
			writeD(_runSpd);
			writeD(_walkSpd);
			
			writeF(_pc.getStatus().getMovementSpeedMultiplier());
			writeF(_pc.getStatus().getAttackSpeedMultiplier());
			
			writeF(_collisionRadius);
			writeF(_collisionHeight);
			
			writeD(_rhand);
			writeD(0);
			writeD(_lhand);
			
			writeC(1);
			writeC(_pc.isRunning() ? 1 : 0);
			writeC(_pc.isInCombat() ? 1 : 0);
			writeC(_pc.isAlikeDead() ? 1 : 0);
			writeC(0);
			
			writeS(_name);
			writeS(_title);
			
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			
			writeD(_pc.getAbnormalEffect());
			
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			
			writeC(_pc.getMove().getMoveType().getId());
			writeC(0x00);
			
			writeF(_collisionRadius);
			writeF(_collisionHeight);
			
			writeD(_enchantEffect);
			writeD(0x00);
		}
	}
}
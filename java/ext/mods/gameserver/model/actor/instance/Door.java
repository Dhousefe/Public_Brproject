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
package ext.mods.gameserver.model.actor.instance;

import java.util.ArrayList;
import java.util.List;

import ext.mods.commons.pool.ThreadPool;
import ext.mods.commons.random.Rnd;
import ext.mods.commons.util.ArraysUtil;
import ext.mods.extensions.listener.manager.DoorListenerManager;
import ext.mods.gameserver.data.xml.DoorData;
import ext.mods.gameserver.enums.DoorType;
import ext.mods.gameserver.enums.EventHandler;
import ext.mods.gameserver.enums.OpenType;
import ext.mods.gameserver.enums.PrivilegeType;
import ext.mods.gameserver.enums.SiegeSide;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.geoengine.geodata.IGeoObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.ai.type.DoorAI;
import ext.mods.gameserver.model.actor.status.DoorStatus;
import ext.mods.gameserver.model.actor.template.DoorTemplate;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.Weapon;
import ext.mods.gameserver.model.residence.Residence;
import ext.mods.gameserver.model.residence.castle.Castle;
import ext.mods.gameserver.model.residence.clanhall.ClanHall;
import ext.mods.gameserver.model.residence.clanhall.SiegableHall;
import ext.mods.gameserver.model.spawn.NpcMaker;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ConfirmDlg;
import ext.mods.gameserver.network.serverpackets.DoorInfo;
import ext.mods.gameserver.network.serverpackets.DoorStatusUpdate;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.skills.L2Skill;

public class Door extends Creature implements IGeoObject
{
	private static final String[] DOOR_CLAN = new String[]
	{
		"door_clan"
	};
	
	private Residence _residence;
	
	private boolean _open;
	
	private List<Quest> _quests;
	private List<NpcMaker> _npcMakers;
	
	public Door(int objectId, DoorTemplate template)
	{
		super(objectId, template);
		
		_open = !getTemplate().isOpened();
		
		setName(template.getName());
	}
	
	@Override
	public DoorAI getAI()
	{
		return (DoorAI) _ai;
	}
	
	@Override
	public void setAI()
	{
		_ai = new DoorAI(this);
	}
	
	@Override
	public final DoorStatus getStatus()
	{
		return (DoorStatus) _status;
	}
	
	@Override
	public void setStatus()
	{
		_status = new DoorStatus(this);
	}
	
	@Override
	public final DoorTemplate getTemplate()
	{
		return (DoorTemplate) super.getTemplate();
	}
	
	@Override
	public void addFuncsToNewCharacter()
	{
	}
	
	@Override
	public void updateAbnormalEffect()
	{
	}
	
	@Override
	public ItemInstance getActiveWeaponInstance()
	{
		return null;
	}
	
	@Override
	public Weapon getActiveWeaponItem()
	{
		return null;
	}
	
	@Override
	public ItemInstance getSecondaryWeaponInstance()
	{
		return null;
	}
	
	@Override
	public Weapon getSecondaryWeaponItem()
	{
		return null;
	}
	
	@Override
	public boolean isAttackableBy(Creature attacker)
	{
		if (!super.isAttackableBy(attacker))
			return false;
		
		if (!(attacker instanceof Playable))
			return false;
		
		if (_residence instanceof Castle castle && castle.getSiege().isInProgress())
		{
			if (!castle.getSiege().checkSides(attacker.getActingPlayer().getClan(), SiegeSide.ATTACKER))
				return false;
			
			if (isWall())
				return attacker instanceof SiegeSummon siegeSummon && siegeSummon.getNpcId() != SiegeSummon.SWOOP_CANNON_ID;
			
			return true;
		}
		
		if (_residence instanceof SiegableHall sh)
			return sh.isInSiege() && sh.getSiege().doorIsAutoAttackable() && sh.getSiege().checkSides(attacker.getActingPlayer().getClan(), SiegeSide.ATTACKER);
		
		return false;
	}
	
	@Override
	public boolean isAttackableWithoutForceBy(Playable attacker)
	{
		return isAttackableBy(attacker);
	}
	
	@Override
	public void onInteract(Player player)
	{
		if (canBeManuallyOpenedBy(player))
		{
			player.setRequestedGate(this);
			player.sendPacket(new ConfirmDlg((!isOpened()) ? 1140 : 1141));
		}
	}
	
	@Override
	public void reduceCurrentHp(double damage, Creature attacker, boolean awake, boolean isDOT, L2Skill skill)
	{
		if (_residence instanceof Castle castle && castle.getSiege().isInProgress())
		{
			if (attacker instanceof SiegeSummon siegeSummon && siegeSummon.getNpcId() == SiegeSummon.SWOOP_CANNON_ID)
				return;
			
			super.reduceCurrentHp(damage, attacker, awake, isDOT, skill);
		}
		else if (_residence instanceof SiegableHall sh && sh.getSiegeZone().isActive())
			super.reduceCurrentHp(damage, attacker, awake, isDOT, skill);
		
		forEachKnownTypeInRadius(Npc.class, 600, called ->
		{
			if (called.isDead())
				return;
			
			if (!ArraysUtil.contains(DOOR_CLAN, called.getTemplate().getClans()))
				return;
			
			for (Quest quest : called.getTemplate().getEventQuests(EventHandler.STATIC_OBJECT_CLAN_ATTACKED))
				quest.onStaticObjectClanAttacked(this, called, attacker, (int) damage, skill);
		});
	}
	
	@Override
	public void reduceCurrentHpByDOT(double i, Creature attacker, L2Skill skill)
	{
	}
	
	@Override
	public void onSpawn()
	{
		changeState(getTemplate().isOpened(), false);
		
		super.onSpawn();
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		if (!super.doDie(killer))
			return false;
		
		if (!_open)
			GeoEngine.getInstance().removeGeoObject(this);
		
		if (_residence instanceof Castle castle && castle.getSiege().isInProgress())
			castle.getSiege().announce((isWall()) ? SystemMessageId.CASTLE_WALL_DAMAGED : SystemMessageId.CASTLE_GATE_BROKEN_DOWN, SiegeSide.DEFENDER);
		
		return true;
	}
	
	@Override
	public void doRevive()
	{
		_open = getTemplate().isOpened();
		
		if (!_open)
			GeoEngine.getInstance().addGeoObject(this);
		
		super.doRevive();
	}
	
	@Override
	public void sendInfo(Player player)
	{
		player.sendPacket(new DoorInfo(player, this));
		player.sendPacket(new DoorStatusUpdate(this));
	}
	
	@Override
	public int getGeoX()
	{
		return getTemplate().getGeoX();
	}
	
	@Override
	public int getGeoY()
	{
		return getTemplate().getGeoY();
	}
	
	@Override
	public int getGeoZ()
	{
		return getTemplate().getGeoZ();
	}
	
	@Override
	public int getHeight()
	{
		return (int) getTemplate().getCollisionHeight();
	}
	
	@Override
	public byte[][] getObjectGeoData()
	{
		return getTemplate().getGeoData();
	}
	
	@Override
	public double getCollisionHeight()
	{
		return getTemplate().getCollisionHeight() / 2;
	}
	
	@Override
	public boolean canBeHealed()
	{
		return false;
	}
	
	@Override
	public boolean isLethalable()
	{
		return false;
	}
	
	/**
	 * @return The {@link Door} id.
	 */
	public final int getDoorId()
	{
		return getTemplate().getId();
	}
	
	/**
	 * @return True if this {@link Door} is opened, false otherwise.
	 */
	public final boolean isOpened()
	{
		return _open;
	}
	
	/**
	 * @return True if this {@link Door} can be unlocked.
	 */
	public final boolean isUnlockable()
	{
		return getTemplate().getOpenType() == OpenType.SKILL;
	}
	
	/**
	 * @return True if this {@link Door} is a wall.
	 */
	public final boolean isWall()
	{
		return getTemplate().getType() == DoorType.WALL;
	}
	
	/**
	 * @return The actual damage of this {@link Door}.
	 */
	public final int getDamage()
	{
		return Math.max(0, Math.min(6, 6 - (int) Math.ceil(getStatus().getHpRatio() * 6)));
	}
	
	/**
	 * Open the {@link Door}.
	 */
	public final void openMe()
	{
		changeState(true, false);
	}
	
	/**
	 * Close the {@link Door}.
	 */
	public final void closeMe()
	{
		changeState(false, false);
	}
	
	/**
	 * Open/close the {@link Door}, triggers other {@link Door}s and schedule automatic open/close task.
	 * @param open : Requested status change.
	 * @param triggered : If true, it means the status change was triggered by another {@link Door}.
	 */
	public final void changeState(boolean open, boolean triggered)
	{
		if (isDead() || _open == open)
			return;
		
		_open = open;
		if (open)
			GeoEngine.getInstance().removeGeoObject(this);
		else
			GeoEngine.getInstance().addGeoObject(this);
		
		getStatus().broadcastStatusUpdate();
		
		if (open)
			DoorListenerManager.getInstance().notifyDoorOpen(this);
		else
			DoorListenerManager.getInstance().notifyDoorClose(this);
		
		if (_quests != null)
		{
			for (Quest quest : _quests)
				quest.onDoorChange(this);
		}
		
		if (_npcMakers != null)
		{
			for (NpcMaker npcMaker : _npcMakers)
				npcMaker.getMaker().onDoorEvent(this, npcMaker);
		}
		

		int triggerId = getTemplate().getTriggerId();
		if (triggerId > 0)
		{
			Door door = DoorData.getInstance().getDoor(triggerId);
			if (door != null)
				door.changeState(open, true);
		}
		

		if (!triggered)
		{
			int time = open ? getTemplate().getCloseTime() : getTemplate().getOpenTime();
			if (getTemplate().getRandomTime() > 0)
				time += Rnd.get(getTemplate().getRandomTime());
			
			if (time > 0)
				ThreadPool.schedule(() -> changeState(!open, false), time * 1000);
		}
	}
	
	public final Residence getResidence()
	{
		return _residence;
	}
	
	public final void setResidence(Residence residence)
	{
		_residence = residence;
	}
	
	/**
	 * Registers {@link Quest}.<br>
	 * Generate {@link List} if not existing (lazy initialization).<br>
	 * If already existing, we remove and add it back.
	 * @param quest : The {@link Quest}.
	 */
	public void addQuestEvent(Quest quest)
	{
		if (_quests == null)
			_quests = new ArrayList<>();
		
		_quests.remove(quest);
		_quests.add(quest);
	}
	
	/**
	 * Register {@link NpcMaker}.<br>
	 * Generate {@link List} if not existing (lazy initialization).<br>
	 * If already existing, we remove and add it back.
	 * @param npcMaker : The {@link NpcMaker}.
	 */
	public void addMakerEvent(NpcMaker npcMaker)
	{
		if (_npcMakers == null)
			_npcMakers = new ArrayList<>();
		
		_npcMakers.remove(npcMaker);
		_npcMakers.add(npcMaker);
	}
	
	/**
	 * @param player : The {@link Player} to test.
	 * @return True if this {@link Door} can be manually opened, or false otherwise. Only used by {@link Player} upon {@link ClanHall} doors.
	 */
	public boolean canBeManuallyOpenedBy(Player player)
	{
		return player.getClan() != null && _residence instanceof ClanHall ch && player.getClanId() == ch.getOwnerId() && player.hasClanPrivileges(PrivilegeType.CHP_ENTRY_EXIT_RIGHTS);
	}
}
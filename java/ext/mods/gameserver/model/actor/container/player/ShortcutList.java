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
package ext.mods.gameserver.model.actor.container.player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Predicate;

import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ConnectionPool;

import ext.mods.gameserver.enums.ShortcutType;
import ext.mods.gameserver.enums.items.EtcItemType;
import ext.mods.gameserver.model.Macro;
import ext.mods.gameserver.model.Shortcut;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.serverpackets.ExAutoSoulShot;
import ext.mods.gameserver.network.serverpackets.ShortCutDelete;
import ext.mods.gameserver.network.serverpackets.ShortCutRegister;
import ext.mods.gameserver.skills.L2Skill;

public class ShortcutList extends ConcurrentSkipListMap<Integer, Shortcut>
{
	private static final long serialVersionUID = 1L;
	
	private static final CLogger LOGGER = new CLogger(ShortcutList.class.getName());
	
	private static final String INSERT_SHORTCUT = "REPLACE INTO character_shortcuts (char_obj_id,slot,page,type,id,level,class_index) values(?,?,?,?,?,?,?)";
	private static final String DELETE_SHORTCUT = "DELETE FROM character_shortcuts WHERE char_obj_id=? AND slot=? AND page=? AND class_index=?";
	private static final String LOAD_SHORTCUTS = "SELECT char_obj_id, slot, page, type, id, level FROM character_shortcuts WHERE char_obj_id=? AND class_index=?";
	
	private static final int MAX_SHORTCUTS_PER_BAR = 12;
	
	private final Player _owner;
	
	public ShortcutList(Player owner)
	{
		_owner = owner;
	}
	
	public Shortcut[] getShortcuts()
	{
		return values().toArray(new Shortcut[values().size()]);
	}
	
	/**
	 * @param shortcut : The {@link Shortcut} object to add.
	 * @see #addShortcut(Shortcut, boolean, boolean)
	 */
	public void addShortcut(Shortcut shortcut)
	{
		addShortcut(shortcut, true, true);
	}
	
	/**
	 * Add a {@link Shortcut} to this {@link ShortcutList}.
	 * @param shortcut : The {@link Shortcut} object to add.
	 * @param checkIntegrity : If set to false, no integrity checks are done.
	 * @param store : if set true store in db
	 */
	public void addShortcut(Shortcut shortcut, boolean checkIntegrity, boolean store)
	{
		if (checkIntegrity)
		{
			switch (shortcut.getType())
			{
				case ITEM:
					final ItemInstance item = _owner.getInventory().getItemByObjectId(shortcut.getId());
					if (item == null)
						return;
					
					if (item.isEtcItem())
						shortcut.setSharedReuseGroup(item.getEtcItem().getSharedReuseGroup());
					break;
				
				case SKILL:
					final L2Skill skill = _owner.getSkill(shortcut.getId());
					if (skill == null)
						return;
					
					if (skill.getLevel() != shortcut.getLevel())
						shortcut.setLevel(skill.getLevel());
					break;
				
				case MACRO:
					final Macro macro = _owner.getMacroList().get(shortcut.getId());
					if (macro == null)
						return;
					break;
				
				case RECIPE:
					if (!_owner.getRecipeBook().hasRecipe(shortcut.getId()))
						return;
					break;
			}
		}
		
		final Shortcut oldShortcut = put(shortcut.getSlot() + (shortcut.getPage() * MAX_SHORTCUTS_PER_BAR), shortcut);
		
		if (store)
		{
			if (oldShortcut != null)
				deleteShortCutFromDb(oldShortcut);
			
			try (Connection con = ConnectionPool.getConnection();
				PreparedStatement ps = con.prepareStatement(INSERT_SHORTCUT))
			{
				ps.setInt(1, _owner.getObjectId());
				ps.setInt(2, shortcut.getSlot());
				ps.setInt(3, shortcut.getPage());
				ps.setString(4, shortcut.getType().toString());
				ps.setInt(5, shortcut.getId());
				ps.setInt(6, shortcut.getLevel());
				ps.setInt(7, _owner.getClassIndex());
				ps.execute();
			}
			catch (Exception e)
			{
				LOGGER.error("Couldn't store shortcut.", e);
			}
		}
	}
	
	/**
	 * Delete the {@link Shortcut} corresponding to this {@link ShortcutList}.
	 * @param slot : The slot of the {@link Shortcut}.
	 * @param page : The page of the {@link Shortcut}.
	 */
	public void deleteShortcut(int slot, int page)
	{
		slot += page * 12;
		
		final Shortcut oldShortcut = remove(slot);
		if (oldShortcut == null || _owner == null)
			return;
		
		deleteShortCutFromDb(oldShortcut);
		if (oldShortcut.getType() == ShortcutType.ITEM)
		{
			final ItemInstance item = _owner.getInventory().getItemByObjectId(oldShortcut.getId());
			
			if (item != null && item.getItemType() == EtcItemType.SHOT && _owner.removeAutoSoulShot(item.getItemId()))
				_owner.sendPacket(new ExAutoSoulShot(item.getItemId(), 0));
		}
		
		_owner.sendPacket(new ShortCutDelete(slot));
		
		for (int shotId : _owner.getAutoSoulShot())
			_owner.sendPacket(new ExAutoSoulShot(shotId, 1));
	}
	
	private void deleteShortCutFromDb(Shortcut shortcut)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_SHORTCUT))
		{
			ps.setInt(1, _owner.getObjectId());
			ps.setInt(2, shortcut.getSlot());
			ps.setInt(3, shortcut.getPage());
			ps.setInt(4, _owner.getClassIndex());
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't delete shortcut.", e);
		}
	}
	
	/**
	 * Restore {@link Shortcut}s associated to the {@link Player} owner.
	 */
	public void restore()
	{
		clear();
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(LOAD_SHORTCUTS))
		{
			ps.setInt(1, _owner.getObjectId());
			ps.setInt(2, _owner.getClassIndex());
			
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					final int slot = rs.getInt("slot");
					final int page = rs.getInt("page");
					final Shortcut shortcut = new Shortcut(slot, page, Enum.valueOf(ShortcutType.class, rs.getString("type")), rs.getInt("id"), rs.getInt("level"), 1);
					
					if (shortcut.getType() == ShortcutType.ITEM)
					{
						final ItemInstance item = _owner.getInventory().getItemByObjectId(shortcut.getId());
						if (item == null)
							continue;
						
						if (item.isEtcItem())
							shortcut.setSharedReuseGroup(item.getEtcItem().getSharedReuseGroup());
					}
					
					put(slot + (page * MAX_SHORTCUTS_PER_BAR), shortcut);
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't restore shortcuts.", e);
		}
	}
	
	/**
	 * Refresh all occurences of {@link Shortcut}s based on a {@link Predicate}.
	 * @param predicate : The {@link Predicate} to use as filter.
	 * @param level : The new level to set.
	 */
	public void refreshShortcuts(Predicate<Shortcut> predicate, int level)
	{
		final List<Shortcut> shortcuts = values().stream().filter(predicate).toList();
		if (shortcuts.isEmpty())
			return;
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(INSERT_SHORTCUT))
		{
			for (Shortcut s : shortcuts)
			{
				if (level > 0)
					s.setLevel(level);
				
				_owner.sendPacket(new ShortCutRegister(_owner, s));
				
				ps.setInt(1, _owner.getObjectId());
				ps.setInt(2, s.getSlot());
				ps.setInt(3, s.getPage());
				ps.setString(4, s.getType().toString());
				ps.setInt(5, s.getId());
				ps.setInt(6, s.getLevel());
				ps.setInt(7, _owner.getClassIndex());
				ps.addBatch();
			}
			ps.executeBatch();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't store shortcuts.", e);
		}
	}
	
	/**
	 * Refresh all occurences of {@link Shortcut}s based on a {@link Predicate}.
	 * @param predicate : The {@link Predicate} to use as filter.
	 */
	public void refreshShortcuts(Predicate<Shortcut> predicate)
	{
		values().stream().filter(predicate).forEach(s -> _owner.sendPacket(new ShortCutRegister(_owner, s)));
	}
	
	/**
	 * Delete all existing occurences of a given {@link Shortcut} based on its {@link ShortcutType} and related id.
	 * @param id : The id related to the {@link Shortcut} to delete.
	 * @param type : The {@link ShortcutType} to affect.
	 */
	public void deleteShortcuts(int id, ShortcutType type)
	{
		for (Shortcut shortcut : values())
		{
			if (shortcut.getId() == id && shortcut.getType() == type)
				deleteShortcut(shortcut.getSlot(), shortcut.getPage());
		}
	}
}
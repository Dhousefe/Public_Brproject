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
package ext.mods.gameserver.data.manager;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;

import ext.mods.Config;
import ext.mods.gameserver.model.actor.Attackable;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.FeedableBeast;
import ext.mods.gameserver.model.actor.instance.GrandBoss;
import ext.mods.gameserver.model.actor.instance.SiegeGuard;
import ext.mods.gameserver.model.entity.CursedWeapon;
import ext.mods.gameserver.model.item.instance.ItemInstance;

import org.w3c.dom.Document;

/**
 * Load and store {@link CursedWeapon}s. A cursed weapon is a feature involving the drop of a powerful weapon, which stages on player kills and give powerful stats.
 * <ul>
 * <li><u>dropRate :</u> the drop rate used by the monster to drop the item. Default : 1/1000000</li>
 * <li><u>duration :</u> the overall lifetime duration in hours. Default : 72 hours (3 days)</li>
 * <li><u>durationLost :</u> the task time duration, launched when someone pickups a cursed weapon. Renewed when the owner kills a player. Default : 24 hours.</li>
 * <li><u>disapearChance :</u> chance to dissapear when the owner dies. Default : 50%</li>
 * <li><u>stageKills :</u> the number used to calculate random number of needed kills to rank up the cursed weapon. That number is used as a base, it takes a random number between 50% and 150% of that value. Default : 10</li>
 * </ul>
 */
public class CursedWeaponManager implements IXmlReader
{
	private final Map<Integer, CursedWeapon> _cursedWeapons = new HashMap<>();
	
	public CursedWeaponManager()
	{
		if (!Config.ALLOW_CURSED_WEAPONS)
		{
			LOGGER.info("Cursed weapons loading is skipped.");
			return;
		}
		
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("xml/cursedWeapons.xml");
		LOGGER.info("Loaded {} cursed weapons.", _cursedWeapons.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "item", itemNode ->
		{
			final StatSet set = parseAttributes(itemNode);
			_cursedWeapons.put(set.getInteger("id"), new CursedWeapon(set));
		}));
	}
	
	/**
	 * End the life of existing {@link CursedWeapon}s, clear the map, and reload content.
	 */
	public void reload()
	{
		for (CursedWeapon cw : _cursedWeapons.values())
			cw.endOfLife();
		
		_cursedWeapons.clear();
		
		load();
	}
	
	public boolean isCursed(int itemId)
	{
		return _cursedWeapons.containsKey(itemId);
	}
	
	public Collection<CursedWeapon> getCursedWeapons()
	{
		return _cursedWeapons.values();
	}
	
	public Set<Integer> getCursedWeaponsIds()
	{
		return _cursedWeapons.keySet();
	}
	
	public CursedWeapon getCursedWeapon(int itemId)
	{
		return _cursedWeapons.get(itemId);
	}
	
	/**
	 * Check if a {@link CursedWeapon} can drop, verifying if it is already active and if the killed {@link Attackable} is a valid candidate.
	 * @param attackable : The {@link Attackable} to test.
	 * @param player : The {@link Player} who killed the {@link Attackable}.
	 */
	public synchronized void checkDrop(Attackable attackable, Player player)
	{
		if (attackable instanceof SiegeGuard || attackable instanceof GrandBoss || attackable instanceof FeedableBeast)
			return;
		
		for (CursedWeapon cw : _cursedWeapons.values())
		{
			if (cw.isActive())
				continue;
			
			if (cw.checkDrop(attackable, player))
				break;
		}
	}
	
	/**
	 * Assimilate a {@link CursedWeapon} if the {@link Player} set as parameter already possesses one (which ranks up possessed weapon), or activate it otherwise.
	 * @param player : The {@link Player} to test.
	 * @param item : The picked up {@link ItemInstance}.
	 */
	public void activate(Player player, ItemInstance item)
	{
		final CursedWeapon cw = _cursedWeapons.get(item.getItemId());
		if (cw == null)
			return;
		
		if (player.isCursedWeaponEquipped())
		{
			_cursedWeapons.get(player.getCursedWeaponEquippedId()).rankUp();
			
			cw.setPlayer(player);
			
			cw.endOfLife();
		}
		else
			cw.activate(player, item);
	}
	
	/**
	 * Retrieve the {@link CursedWeapon} based on its itemId and handle the drop process.
	 * @param itemId : The cursed weapon itemId.
	 * @param creature : The {@link Creature} who killed the {@link CursedWeapon} holder.
	 */
	public void drop(int itemId, Creature creature)
	{
		final CursedWeapon cw = _cursedWeapons.get(itemId);
		if (cw == null)
			return;
		
		cw.dropIt(creature);
	}
	
	/**
	 * Retrieve the {@link CursedWeapon} based on its itemId and increase its kills.
	 * @param itemId : The cursed weapon itemId.
	 */
	public void increaseKills(int itemId)
	{
		final CursedWeapon cw = _cursedWeapons.get(itemId);
		if (cw == null)
			return;
		
		cw.increaseKills();
	}
	
	public int getCurrentStage(int itemId)
	{
		final CursedWeapon cw = _cursedWeapons.get(itemId);
		return (cw == null) ? 0 : cw.getCurrentStage();
	}
	
	/**
	 * Check if the {@link Player} is equipped with a {@link CursedWeapon} on EnterWorld.<br>
	 * <br>
	 * If so, we set the {@link Player} and item references on the {@link CursedWeapon}, then we reward associated skills to that {@link Player}.
	 * @param player : The {@link Player} to test.
	 */
	public void checkPlayer(Player player)
	{
		if (player == null)
			return;
		
		for (CursedWeapon cw : _cursedWeapons.values())
		{
			if (cw.isActivated() && player.getObjectId() == cw.getPlayerId())
			{
				cw.setPlayer(player);
				cw.setItem(player.getInventory().getItemByItemId(cw.getItemId()));
				cw.giveDemonicSkills();
				
				player.setCursedWeaponEquippedId(cw.getItemId());
				break;
			}
		}
	}
	
	public static final CursedWeaponManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final CursedWeaponManager INSTANCE = new CursedWeaponManager();
	}
}
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
package ext.mods.gameserver.model.location;

import ext.mods.commons.data.StatSet;

import ext.mods.gameserver.enums.actors.TowerType;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.instance.FlameTower;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.residence.castle.Castle;
import ext.mods.gameserver.model.spawn.Spawn;

/**
 * A datatype extending {@link SpawnLocation}, which handles a single Control Tower spawn point and its parameters (such as guards npcId List), npcId to spawn and upgrade level.
 */
public class TowerSpawnLocation extends SpawnLocation
{
	public static final int LT_DISPLAY_NPC_WORKING = 13002;
	public static final int LT_DISPLAY_NPC_NON_WORKING = 13003;
	public static final int FT_DISPLAY_NPC_WORKING = 13004;
	public static final int FT_DISPLAY_NPC_NON_WORKING = 13005;
	
	private final TowerType _type;
	private final String _alias;
	private final Castle _castle;
	
	private double _hp;
	private double _pDef;
	private double _mDef;
	
	private String[] _zones;
	private int _upgradeLevel;
	
	private Npc _npc;
	
	public TowerSpawnLocation(TowerType type, String alias, Castle castle)
	{
		super(SpawnLocation.DUMMY_SPAWNLOC);
		
		_type = type;
		_alias = alias;
		_castle = castle;
	}
	
	public TowerType getType()
	{
		return _type;
	}
	
	public String getAlias()
	{
		return _alias;
	}
	
	public double getHp()
	{
		return _hp;
	}
	
	public double getPdef()
	{
		return _pDef;
	}
	
	public double getMdef()
	{
		return _mDef;
	}
	
	public void setStats(double hp, double pDef, double mDef)
	{
		_hp = hp;
		_pDef = pDef;
		_mDef = mDef;
	}
	
	public String[] getZones()
	{
		return _zones;
	}
	
	public void setZones(String[] zones)
	{
		_zones = zones;
	}
	
	public int getUpgradeLevel()
	{
		return _upgradeLevel;
	}
	
	public void setUpgradeLevel(int level)
	{
		_upgradeLevel = level;
	}
	
	public Npc getNpc()
	{
		return _npc;
	}
	
	public void spawnMe()
	{
		try
		{
			final StatSet npcDat = new StatSet();
			
			npcDat.set("id", (_type == TowerType.LIFE_CONTROL) ? LT_DISPLAY_NPC_NON_WORKING : FT_DISPLAY_NPC_NON_WORKING);
			npcDat.set("type", (_type == TowerType.LIFE_CONTROL) ? "LifeTower" : "FlameTower");
			
			npcDat.set("name", (_type == TowerType.LIFE_CONTROL) ? "Life Control Tower" : "Flame Control Tower");
			
			npcDat.set("hp", _hp);
			npcDat.set("mp", 0);
			
			npcDat.set("pAtk", 0);
			npcDat.set("mAtk", 0);
			npcDat.set("pDef", _pDef);
			npcDat.set("mDef", _mDef);
			
			npcDat.set("runSpd", 0);
			
			npcDat.set("radius", 7);
			npcDat.set("height", 35);
			
			npcDat.set("undying", true);
			npcDat.set("baseDamageRange", "0;0;80;120");
			
			final Spawn spawn = new Spawn(new NpcTemplate(npcDat));
			spawn.setLoc(this);
			
			_npc = spawn.doSpawn(false);
			_npc.setResidence(_castle);
		}
		catch (Exception e)
		{
		}
	}
	
	/**
	 * Morph the {@link Npc} of this {@link TowerSpawnLocation}.
	 */
	public void polymorph()
	{
		if (_npc == null)
			return;
		
		_npc.polymorph((_type == TowerType.LIFE_CONTROL) ? LT_DISPLAY_NPC_WORKING : FT_DISPLAY_NPC_WORKING);
	}
	
	/**
	 * Reinitialize the {@link Npc} of this {@link TowerSpawnLocation}.
	 */
	public void unpolymorph()
	{
		if (_npc == null)
			return;
		
		_npc.getStatus().setMaxHp();
		
		_npc.unpolymorph();
	}
	
	/**
	 * Apply Mid Victory effects ; Flame Tower is disabled, Life Tower is enabled.
	 */
	public void midVictory()
	{
		if (_npc == null)
			return;
		
		if (_type == TowerType.TRAP_CONTROL && _npc.getPolymorphTemplate() != null)
		{
			_npc.unpolymorph();
			
			((FlameTower) _npc).enableZones(false);
		}
		else if (_type == TowerType.LIFE_CONTROL && _npc.getPolymorphTemplate() == null)
		{
			_npc.getStatus().setMaxHp();
			
			polymorph();
		}
	}
}
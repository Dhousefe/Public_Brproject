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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;
import ext.mods.commons.pool.ConnectionPool;

import ext.mods.gameserver.data.sql.ClanTable;
import ext.mods.gameserver.enums.CabalType;
import ext.mods.gameserver.enums.SpawnType;
import ext.mods.gameserver.enums.actors.TowerType;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.location.SpawnLocation;
import ext.mods.gameserver.model.location.TowerSpawnLocation;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.model.pledge.ItemInfo;
import ext.mods.gameserver.model.records.custom.SiegeInfo;
import ext.mods.gameserver.model.residence.castle.Castle;
import ext.mods.gameserver.model.residence.castle.Siege;
import ext.mods.gameserver.model.spawn.Spawn;
import ext.mods.gameserver.model.zone.type.SiegeZone;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

/**
 * Loads and stores {@link Castle}s informations, using database and XML informations.
 */
public final class CastleManager implements IXmlReader
{
	private static final String LOAD_CASTLES = "SELECT * FROM castle ORDER BY id";
	private static final String LOAD_OWNER = "SELECT clan_id FROM clan_data WHERE hasCastle=?";
	private static final String LOAD_TRAPS = "SELECT * FROM castle_trapupgrade WHERE castleId=?";
	private static final String LOAD_DOORS = "SELECT * FROM castle_doorupgrade WHERE castleId=?";
	
	private static final String RESET_CERTIFICATES = "UPDATE castle SET certificates=300";
	
	private final Map<Integer, Castle> _castles = new HashMap<>();
	
	protected CastleManager()
	{
		load();
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(LOAD_CASTLES);
			ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
			{
				final Castle castle = _castles.get(rs.getInt("id"));
				if (castle == null)
					continue;
				
				castle.setSiegeDate(Calendar.getInstance());
				castle.getSiegeDate().setTimeInMillis(rs.getLong("siegeDate"));
				castle.setTimeRegistrationOver(rs.getBoolean("regTimeOver"));
				castle.setCurrentTaxPercent(rs.getInt("currentTaxPercent"), false);
				castle.setNextTaxPercent(rs.getInt("nextTaxPercent"), false);
				castle.setTreasury(rs.getLong("treasury"));
				castle.setTaxRevenue(rs.getLong("taxRevenue"));
				castle.setSeedIncome(rs.getLong("seedIncome"));
				castle.setLeftCertificates(rs.getInt("certificates"), false);
				
				try (PreparedStatement ps1 = con.prepareStatement(LOAD_OWNER);
					PreparedStatement ps2 = con.prepareStatement(LOAD_TRAPS);
					PreparedStatement ps3 = con.prepareStatement(LOAD_DOORS))
				{
					ps1.setInt(1, castle.getId());
					
					try (ResultSet rs1 = ps1.executeQuery())
					{
						while (rs1.next())
						{
							final int ownerId = rs1.getInt("clan_id");
							if (ownerId > 0)
							{
								final Clan clan = ClanTable.getInstance().getClan(ownerId);
								if (clan != null)
									castle.setOwnerId(ownerId);
							}
						}
					}
					
					ps2.setInt(1, castle.getId());
					
					try (ResultSet rs2 = ps2.executeQuery())
					{
						while (rs2.next())
							castle.getControlTowers().get(rs2.getInt("towerIndex")).setUpgradeLevel(rs2.getInt("level"));
					}
					
					castle.launchSiege();
					
					ps3.setInt(1, castle.getId());
					
					try (ResultSet rs3 = ps3.executeQuery())
					{
						while (rs3.next())
							castle.upgradeDoor(rs3.getInt("doorId"), rs3.getInt("hp"), false);
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to load castles.", e);
		}
	}
	
	@Override
	public void load()
	{
		parseDataFile("xml/castles.xml");
		LOGGER.info("Loaded {} castles.", _castles.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "castle", castleNode ->
		{
			final StatSet set = parseAttributes(castleNode);
			forEach(castleNode, "tax", taxNode -> addAttributes(set, taxNode.getAttributes()));
			
			final Castle castle = new Castle(set);
			
			forEach(castleNode, "sieges", siegesNode ->
			{
				forEach(siegesNode, "siege", siegeNode ->
				{
					final NamedNodeMap siegeAttrs = siegeNode.getAttributes();
					final int week = parseInteger(siegeAttrs, "week", 0);
					final int day = parseInteger(siegeAttrs, "day", 0);
					final int hour = parseInteger(siegeAttrs, "hour", 0);
					final int minute = parseInteger(siegeAttrs, "minute", 0);
					
					final SiegeInfo siegeInfo = new SiegeInfo(day, hour, minute);
					castle.getSieges().put(week, siegeInfo);
				});
			});
			forEach(castleNode, "skills", skillsNode ->
			{
				forEach(skillsNode, "member", memberNode ->
				{
					final NamedNodeMap skillAttrs = memberNode.getAttributes();
					final int skillId = parseInteger(skillAttrs, "id");
					final int skillLvl = parseInteger(skillAttrs, "level");
					castle.getSkillsMember().put(skillId, skillLvl);
				});
				
				forEach(skillsNode, "leader", leaderNode ->
				{
					final NamedNodeMap skillAttrs = leaderNode.getAttributes();
					final int skillId = parseInteger(skillAttrs, "id");
					final int skillLvl = parseInteger(skillAttrs, "level");
					castle.getSkillsLeader().put(skillId, skillLvl);
				});
			});
			forEach(castleNode, "rewards", rewardsNode ->
			{
				forEach(rewardsNode, "member", memberNode ->
				{
					final NamedNodeMap rewardAttrs = memberNode.getAttributes();
					final int itemId = parseInteger(rewardAttrs, "id");
					final int itemCount = parseInteger(rewardAttrs, "count");
					final int enchant = parseInteger(rewardAttrs, "enchant", 0);
					
					final ItemInfo itemInfo = new ItemInfo(itemCount, enchant);
					castle.getItemsMember().put(itemId, itemInfo);
				});
				
				forEach(rewardsNode, "leader", leaderNode ->
				{
					final NamedNodeMap rewardAttrs = leaderNode.getAttributes();
					final int itemId = parseInteger(rewardAttrs, "id");
					final int itemCount = parseInteger(rewardAttrs, "count");
					final int enchant = parseInteger(rewardAttrs, "enchant", 0);
					
					final ItemInfo itemInfo = new ItemInfo(itemCount, enchant);
					castle.getItemsLeader().put(itemId, itemInfo);
				});
			});
			forEach(castleNode, "artifacts", artifactsNode -> forEach(artifactsNode, "artifact", artifactNode ->
			{
				final NamedNodeMap artifactAttrs = artifactNode.getAttributes();
				final int npcId = parseInteger(artifactAttrs, "id");
				final SpawnLocation pos = parseSpawnLocation(artifactAttrs, "pos");
				
				castle.getArtifacts().put(npcId, pos);
			}));
			forEach(castleNode, "controlTowers", controlTowersNode -> forEach(controlTowersNode, "controlTower", towerNode ->
			{
				final NamedNodeMap towerAttrs = towerNode.getAttributes();
				final String alias = parseString(towerAttrs, "alias");
				final TowerType type = parseEnum(towerAttrs, TowerType.class, "type");
				
				final TowerSpawnLocation tsl = new TowerSpawnLocation(type, alias, castle);
				
				forEach(towerNode, "position", positionNode ->
				{
					final NamedNodeMap attrs = positionNode.getAttributes();
					tsl.set(parseInteger(attrs, "x"), parseInteger(attrs, "y"), parseInteger(attrs, "z"));
				});
				forEach(towerNode, "stats", statNode ->
				{
					final NamedNodeMap attrs = statNode.getAttributes();
					tsl.setStats(parseDouble(attrs, "hp"), parseDouble(attrs, "pDef"), parseDouble(attrs, "mDef"));
				});
				forEach(towerNode, "zones", zoneNode -> tsl.setZones(parseString(zoneNode.getAttributes(), "val").split(";")));
				
				castle.getControlTowers().add(tsl);
			}));
			forEach(castleNode, "gates", gatesNode -> castle.setDoors(parseString(gatesNode.getAttributes(), "val")));
			forEach(castleNode, "npcs", npcsNode -> castle.setNpcs(parseString(npcsNode.getAttributes(), "val")));
			forEach(castleNode, "spawns", spawnsNode -> forEach(spawnsNode, "spawn", spawnNode -> castle.addSpawn(parseEnum(spawnNode.getAttributes(), SpawnType.class, "type"), parseLocation(spawnNode))));
			forEach(castleNode, "tickets", ticketsNode -> forEach(ticketsNode, "ticket", ticketNode -> castle.addTicket(parseAttributes(ticketNode))));
			
			_castles.put(castle.getId(), castle);
		}));
	}
	
	public Castle getCastleById(int castleId)
	{
		return _castles.get(castleId);
	}
	
	public Castle getCastleByOwner(Clan clan)
	{
		return _castles.values().stream().filter(c -> c.getOwnerId() == clan.getClanId()).findFirst().orElse(null);
	}
	
	public Castle getCastleByAlias(String alias)
	{
		return _castles.values().stream().filter(c -> c.getAlias().equalsIgnoreCase(alias)).findFirst().orElse(null);
	}
	
	public Castle getCastle(int x, int y, int z)
	{
		return _castles.values().stream().filter(c -> c.getSiegeZone().isInsideZone(x, y, z)).findFirst().orElse(null);
	}
	
	public Castle getCastle(WorldObject object)
	{
		return getCastle(object.getX(), object.getY(), object.getZ());
	}
	
	public Collection<Castle> getCastles()
	{
		return _castles.values();
	}
	
	public void validateTaxes(CabalType sealStrifeOwner)
	{
		int maxTax;
		switch (sealStrifeOwner)
		{
			case DAWN:
				maxTax = 25;
				break;
			
			case DUSK:
				maxTax = 5;
				break;
			
			default:
				maxTax = 15;
				break;
		}
		
		_castles.values().stream().filter(c -> c.getCurrentTaxPercent() > maxTax).forEach(c -> c.setCurrentTaxPercent(maxTax, true));
	}
	
	/**
	 * @param object : The {@link WorldObject} to check.
	 * @return True if the {@link WorldObject} set as parameter is inside an ACTIVE {@link SiegeZone}, or false otherwise.
	 */
	public Siege getActiveSiege(WorldObject object)
	{
		return getActiveSiege(object.getX(), object.getY(), object.getZ());
	}
	
	/**
	 * @param x : The X coord to test.
	 * @param y : The Y coord to test.
	 * @param z : The Z coord to test.
	 * @return True if coords are set inside an ACTIVE {@link SiegeZone}, or false otherwise.
	 */
	public Siege getActiveSiege(int x, int y, int z)
	{
		for (Castle castle : _castles.values())
		{
			final Siege siege = castle.getSiege();
			if (siege.isInProgress() && castle.getSiegeZone().isInsideZone(x, y, z))
				return siege;
		}
		return null;
	}
	
	/**
	 * Reset all castles certificates. Reset the memory value, and run a unique query.
	 */
	public void resetCertificates()
	{
		for (Castle castle : _castles.values())
			castle.setLeftCertificates(300, false);
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(RESET_CERTIFICATES))
		{
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to reset certificates.", e);
		}
	}
	
	public void spawnEntities()
	{
		_castles.values().forEach(castle ->
		{
			castle.getControlTowers().forEach(TowerSpawnLocation::spawnMe);
			
			castle.getArtifacts().forEach((npcId, spawnLoc) ->
			{
				try
				{
					final Spawn spawn = new Spawn(npcId);
					spawn.setLoc(spawnLoc);
					spawn.doSpawn(false);
				}
				catch (Exception e)
				{
					LOGGER.error("Couldn't spawn artifact for {} castle.", castle.getName());
				}
			});
		});
	}
	
	/**
	 * Update taxes for all {@link Castle}s.<br>
	 * <br>
	 * For none owned castle :
	 * <ul>
	 * <li>Reset all vars as default.</li>
	 * <li>Use default tax rate for both current and next vars.</li>
	 * </ul>
	 * For owned castle :
	 * <ul>
	 * <li>Increase treasury based on tax revenue and seed income.</li>
	 * <li>Reset tax revenue and seed income vars.</li>
	 * <li>Set current tax using next tax rate.</li>
	 * </ul>
	 */
	public void updateTaxes()
	{
		_castles.values().forEach(Castle::updateTaxes);
	}
	
	public static final CastleManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static final class SingletonHolder
	{
		protected static final CastleManager INSTANCE = new CastleManager();
	}
}
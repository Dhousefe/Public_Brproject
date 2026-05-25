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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;
import ext.mods.commons.pool.ConnectionPool;

import ext.mods.gameserver.data.sql.ClanTable;
import ext.mods.gameserver.enums.SpawnType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.model.records.ClanHallDeco;
import ext.mods.gameserver.model.residence.clanhall.Auction;
import ext.mods.gameserver.model.residence.clanhall.ClanHall;
import ext.mods.gameserver.model.residence.clanhall.ClanHallFunction;
import ext.mods.gameserver.model.residence.clanhall.ClanHallSiege;
import ext.mods.gameserver.model.residence.clanhall.SiegableHall;
import ext.mods.gameserver.model.zone.type.ClanHallZone;

import org.w3c.dom.Document;

/**
 * Loads and store {@link ClanHall}s informations, along their associated {@link Auction}s (if existing), using database and XML informations.
 */
public class ClanHallManager implements IXmlReader
{
	private static final String LOAD_CLANHALLS = "SELECT * FROM clanhall";
	private static final String LOAD_FUNCTIONS = "SELECT * FROM clanhall_functions WHERE hall_id = ?";
	
	private final Map<Integer, ClanHall> _clanHalls = new HashMap<>();
	private final List<ClanHallDeco> _decos = new ArrayList<>();
	
	protected ClanHallManager()
	{
		load();
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(LOAD_CLANHALLS);
			PreparedStatement ps2 = con.prepareStatement(LOAD_FUNCTIONS);
			ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
			{
				final int id = rs.getInt("id");
				
				final ClanHall ch = _clanHalls.get(id);
				if (ch == null)
					continue;
				
				final ClanHallZone zone = ZoneManager.getInstance().getAllZones(ClanHallZone.class).stream().filter(z -> z.getResidenceId() == id).findFirst().orElse(null);
				if (zone == null)
					LOGGER.warn("No existing ClanHallZone for ClanHall {}.", id);
				
				if (ch.getAuctionMin() > 0)
					ch.setAuction(new Auction(ch, rs.getInt("sellerBid"), rs.getString("sellerName"), rs.getString("sellerClanName"), rs.getLong("endDate")));
				else
				{
					long nextSiege = rs.getLong("endDate");
					if (nextSiege - System.currentTimeMillis() < 0)
						((SiegableHall) ch).updateNextSiege();
					else
					{
						final Calendar cal = Calendar.getInstance();
						cal.setTimeInMillis(nextSiege);
						
						((SiegableHall) ch).setNextSiegeDate(cal);
					}
				}
				
				ch.setZone(zone);
				
				final int ownerId = rs.getInt("ownerId");
				if (ownerId > 0)
				{
					final Clan clan = ClanTable.getInstance().getClan(ownerId);
					if (clan == null)
					{
						ch.free();
						continue;
					}
					
					clan.setClanHallId(id);
					
					ch.setOwnerId(ownerId);
					ch.setPaidUntil(rs.getLong("paidUntil"));
					ch.setPaid(rs.getBoolean("paid"));
					
					ch.initializeFeeTask();
					
					ps2.setInt(1, id);
					
					try (ResultSet rs2 = ps2.executeQuery())
					{
						while (rs2.next())
							ch.getFunctions().put(rs2.getInt("type"), new ClanHallFunction(ch, rs2.getInt("type"), rs2.getInt("lvl"), rs2.getInt("lease"), rs2.getLong("rate"), rs2.getLong("endTime")));
					}
					ps2.clearParameters();
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't load clan hall data.", e);
		}
	}
	
	@Override
	public void load()
	{
		parseDataFile("xml/clanHalls.xml");
		parseDataFile("xml/clanHallDeco.xml");
		LOGGER.info("Loaded {} clan halls and {} siegable clan halls.", _clanHalls.size(), getSiegableHalls().size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		if (path.endsWith("clanHalls.xml"))
		{
			forEach(doc, "list", listNode -> forEach(listNode, "clanHall", chNode ->
			{
				final StatSet set = parseAttributes(chNode);
				forEach(chNode, "agit", agitNode -> addAttributes(set, agitNode.getAttributes()));
				forEach(chNode, "tax", taxNode -> addAttributes(set, taxNode.getAttributes()));
				
				final ClanHall ch = (set.containsKey("siegeLength")) ? new SiegableHall(set) : new ClanHall(set);
				
				forEach(chNode, "gates", gatesNode -> ch.setDoors(parseString(gatesNode.getAttributes(), "val")));
				forEach(chNode, "npcs", npcsNode -> ch.setNpcs(parseString(npcsNode.getAttributes(), "val")));
				forEach(chNode, "spawns", spawnsNode -> forEach(spawnsNode, "spawn", spawnNode -> ch.addSpawn(parseEnum(spawnNode.getAttributes(), SpawnType.class, "type"), parseLocation(spawnNode))));
				
				_clanHalls.put(set.getInteger("id"), ch);
			}));
		}
		else
		{
			forEach(doc, "list", listNode -> forEach(listNode, "deco", chdNode ->
			{
				_decos.add(new ClanHallDeco(parseAttributes(chdNode)));
			}));
		}
	}
	
	/**
	 * @param id : The ClanHall id to retrieve.
	 * @return a {@link ClanHall} by its id.
	 */
	public final ClanHall getClanHall(int id)
	{
		return _clanHalls.get(id);
	}
	
	/**
	 * @param id : The ClanHall id to retrieve.
	 * @return a {@link SiegableHall} by its id.
	 */
	public SiegableHall getSiegableHall(int id)
	{
		final ClanHall ch = _clanHalls.get(id);
		return (ch instanceof SiegableHall sh) ? sh : null;
	}
	
	/**
	 * @return a {@link Map} with all {@link ClanHall}s.
	 */
	public final Map<Integer, ClanHall> getClanHalls()
	{
		return _clanHalls;
	}
	
	/**
	 * @return a {@link List} with all {@link SiegableHall}s.
	 */
	public List<SiegableHall> getSiegableHalls()
	{
		return _clanHalls.values().stream().filter(SiegableHall.class::isInstance).map(SiegableHall.class::cast).toList();
	}
	
	/**
	 * @return a {@link List} with all auctionable {@link ClanHall}s.
	 */
	public final List<ClanHall> getAuctionableClanHalls()
	{
		final List<ClanHall> list = new ArrayList<>();
		for (ClanHall ch : _clanHalls.values())
		{
			final Auction auction = ch.getAuction();
			if (auction == null)
				continue;
			
			if (ch.getOwnerId() > 0 && auction.getSeller() == null)
				continue;
			
			list.add(ch);
		}
		return list;
	}
	
	/**
	 * @param location : The location name used as parameter.
	 * @return a {@link List} with all {@link ClanHall}s which are in a given location.
	 */
	public final List<ClanHall> getClanHallsByLocation(String location)
	{
		return _clanHalls.values().stream().filter(ch -> ch.getTownName().equalsIgnoreCase(location)).toList();
	}
	
	/**
	 * @param clan : The {@link Clan} to check.
	 * @return the {@link ClanHall} owned by the Clan, or null otherwise.
	 */
	public final ClanHall getClanHallByOwner(Clan clan)
	{
		return _clanHalls.values().stream().filter(ch -> ch.getOwnerId() == clan.getClanId()).findFirst().orElse(null);
	}
	
	/**
	 * @param id : The ClanHall id used as reference.
	 * @return the {@link Auction} associated to a {@link ClanHall}, or null if not existing.
	 */
	public final Auction getAuction(int id)
	{
		final ClanHall ch = _clanHalls.get(id);
		return (ch == null) ? null : ch.getAuction();
	}
	
	public final ClanHallSiege getActiveSiege(Creature creature)
	{
		for (ClanHall ch : _clanHalls.values())
		{
			if (!(ch instanceof SiegableHall sh))
				continue;
			
			if (sh.getSiegeZone().isActive() && sh.getSiegeZone().isInsideZone(creature))
				return sh.getSiege();
		}
		return null;
	}
	
	public final boolean isClanParticipating(Clan clan)
	{
		for (SiegableHall hall : getSiegableHalls())
		{
			if (hall.getSiege() != null && hall.getSiege().getAttackerClans().contains(clan))
				return true;
		}
		return false;
	}
	
	public final void save()
	{
		for (SiegableHall hall : getSiegableHalls())
		{
			if (hall.getId() == 62 || hall.getSiege() == null)
				continue;
			
			hall.getSiege().saveAttackers();
		}
	}
	
	public final int getDecoFee(int type, int level)
	{
		final ClanHallDeco deco = _decos.stream().filter(d -> d.type() == type && d.level() == level).findFirst().orElse(null);
		if (deco != null)
			return deco.price();
		
		return 0;
	}
	
	public final int getDecoDays(int type, int level)
	{
		final ClanHallDeco deco = _decos.stream().filter(d -> d.type() == type && d.level() == level).findFirst().orElse(null);
		if (deco != null)
			return deco.days();
		
		return 0;
	}
	
	public static ClanHallManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ClanHallManager INSTANCE = new ClanHallManager();
	}
}
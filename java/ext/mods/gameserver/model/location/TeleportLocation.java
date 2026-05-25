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

import java.util.Calendar;

import ext.mods.commons.data.StatSet;

import ext.mods.gameserver.data.manager.SevenSignsManager;
import ext.mods.gameserver.enums.SealType;
import ext.mods.gameserver.enums.TeleportType;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.itemcontainer.PcInventory;

/**
 * A datatype extending {@link Location}, used to retain a single Gatekeeper teleport location.
 */
public class TeleportLocation extends Location
{
	private final String _descEn;
	private final String _descRu;
	private final TeleportType _type;
	private final int _priceId;
	private final int _priceCount;
	private final int _castleId;
	
	public TeleportLocation(StatSet set)
	{
		super(set.getInteger("x"), set.getInteger("y"), set.getInteger("z"));
		
		_descEn = set.getString("descEn");
		_descRu = set.getString("descRu");
		_type = set.getEnum("type", TeleportType.class, TeleportType.STANDARD);
		_priceId = set.getInteger("priceId");
		_priceCount = set.getInteger("priceCount");
		_castleId = set.getInteger("castleId", 0);
	}
	
	@Override
	public String toString()
	{
		return "TeleportLocation [_descEn=" + _descEn + ", _descRu=" + _descRu + ", _type=" + _type + ", _priceId=" + _priceId + ", _priceCount=" + _priceCount + ", _castleId=" + _castleId + "]";
	}
	
	public String getDescEn(Player player)
	{
		return _descEn;
	}
	
	public String getDescRu(Player player)
	{
		return _descRu;
	}
	
	public TeleportType getType()
	{
		return _type;
	}
	
	public int getPriceId()
	{
		return _priceId;
	}
	
	public int getPriceCount()
	{
		return _priceCount;
	}
	
	public int getCastleId()
	{
		return _castleId;
	}
	
	/**
	 * In L2OFF half price teleport feature is set in 'event.ini' and is named 'CoreTime'.<br>
	 * However some custom/extended L2OFF packs are likely to use type "PRIMEHOURS", but it is nothing more than static data duplication.<br>
	 * Also core-time shall effect only standard teleport.
	 * @return True if the time is core time or not.
	 */
	private static boolean isCoreTime()
	{
		final Calendar now = Calendar.getInstance();
		switch (now.get(Calendar.DAY_OF_WEEK))
		{
			case Calendar.SATURDAY, Calendar.SUNDAY:
				final int currentHour = now.get(Calendar.HOUR_OF_DAY);
				return currentHour >= 20 && currentHour <= 23;
		}
		
		return false;
	}
	
	/**
	 * @param player : The {@link Player} to test.
	 * @return The teleport price, modified by multiple sources (Seven Signs, half price time).
	 */
	public int getCalculatedPriceCount(Player player)
	{
		if (_priceId == PcInventory.ANCIENT_ADENA_ID)
		{
			final SevenSignsManager ss = SevenSignsManager.getInstance();
			final boolean check = ss.isSealValidationPeriod() && ss.getPlayerCabal(player.getObjectId()) == ss.getSealOwner(SealType.GNOSIS) && ss.getPlayerSeal(player.getObjectId()) == SealType.GNOSIS;
			
			return (check) ? _priceCount : (int) (_priceCount * 1.6);
		}
		
		if (_type == TeleportType.STANDARD && isCoreTime())
			return Math.max(_priceCount >> 1, 1);
		
		return _priceCount;
	}
}
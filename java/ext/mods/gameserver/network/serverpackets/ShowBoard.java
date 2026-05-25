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

import java.util.List;

import ext.mods.commons.lang.StringUtil;

public class ShowBoard extends L2GameServerPacket
{
	public static final ShowBoard STATIC_SHOWBOARD_102 = new ShowBoard(null, "102");
	public static final ShowBoard STATIC_SHOWBOARD_103 = new ShowBoard(null, "103");
	
	public static final ShowBoard STATIC_CLOSE = new ShowBoard();
	
	private static final String TOP = "bypass _bbshome";
	private static final String FAV = "bypass _bbsgetfav";
	private static final String REGION = "bypass _bbsloc";
	private static final String CLAN = "bypass _bbsclan";
	private static final String MEMO = "bypass _bbsmemo";
	private static final String MAIL = "bypass _maillist_0_1_0_";
	private static final String FRIENDS = "bypass _friendlist_0_";
	private static final String ADDFAV = "bypass _bbsgetfav_add";
	
	private final StringBuilder _htmlCode = new StringBuilder();
	
	private boolean _canShow;
	
	public ShowBoard(String htmlCode, String id)
	{
		_canShow = true;
		
		StringUtil.append(_htmlCode, id, "\u0008", htmlCode);
	}
	
	public ShowBoard(List<String> arg)
	{
		_canShow = true;
		
		_htmlCode.append("1002\u0008");
		for (String str : arg)
			StringUtil.append(_htmlCode, str, " \u0008");
	}
	
	public ShowBoard()
	{
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x6e);
		writeC(_canShow ? 0x01 : 0x00);
		
		if (_canShow)
		{
			writeS(TOP);
			writeS(FAV);
			writeS(REGION);
			writeS(CLAN);
			writeS(MEMO);
			writeS(MAIL);
			writeS(FRIENDS);
			writeS(ADDFAV);
			writeS(_htmlCode.toString());
		}
	}
}
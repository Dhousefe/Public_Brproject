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

import ext.mods.gameserver.model.residence.clanhall.ClanHall;
import ext.mods.gameserver.model.residence.clanhall.ClanHallFunction;

public class ClanHallDecoration extends L2GameServerPacket
{
	private final ClanHall _ch;
	
	public ClanHallDecoration(ClanHall ch)
	{
		_ch = ch;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xf7);
		writeD(_ch.getId());
		
		ClanHallFunction chf = _ch.getFunction(ClanHall.FUNC_RESTORE_HP);
		if (chf == null || chf.getLvl() == 0)
			writeC(0);
		else if ((_ch.getGrade() == 0 && chf.getLvl() < 220) || (_ch.getGrade() == 1 && chf.getLvl() < 160) || (_ch.getGrade() == 2 && chf.getLvl() < 260) || (_ch.getGrade() == 3 && chf.getLvl() < 300))
			writeC(1);
		else
			writeC(2);
		
		chf = _ch.getFunction(ClanHall.FUNC_RESTORE_MP);
		if (chf == null || chf.getLvl() == 0)
		{
			writeC(0);
			writeC(0);
		}
		else if (((_ch.getGrade() == 0 || _ch.getGrade() == 1) && chf.getLvl() < 25) || (_ch.getGrade() == 2 && chf.getLvl() < 30) || (_ch.getGrade() == 3 && chf.getLvl() < 40))
		{
			writeC(1);
			writeC(1);
		}
		else
		{
			writeC(2);
			writeC(2);
		}
		
		chf = _ch.getFunction(ClanHall.FUNC_RESTORE_EXP);
		if (chf == null || chf.getLvl() == 0)
			writeC(0);
		else if ((_ch.getGrade() == 0 && chf.getLvl() < 25) || (_ch.getGrade() == 1 && chf.getLvl() < 30) || (_ch.getGrade() == 2 && chf.getLvl() < 40) || (_ch.getGrade() == 3 && chf.getLvl() < 50))
			writeC(1);
		else
			writeC(2);
		
		chf = _ch.getFunction(ClanHall.FUNC_TELEPORT);
		if (chf == null || chf.getLvl() == 0)
			writeC(0);
		else if (chf.getLvl() < 2)
			writeC(1);
		else
			writeC(2);
		
		writeC(0);
		
		chf = _ch.getFunction(ClanHall.FUNC_DECO_CURTAINS);
		if (chf == null || chf.getLvl() == 0)
			writeC(0);
		else if (chf.getLvl() <= 1)
			writeC(1);
		else
			writeC(2);
		
		chf = _ch.getFunction(ClanHall.FUNC_ITEM_CREATE);
		if (chf == null || chf.getLvl() == 0)
			writeC(0);
		else if ((_ch.getGrade() == 0 && chf.getLvl() < 2) || chf.getLvl() < 3)
			writeC(1);
		else
			writeC(2);
		
		chf = _ch.getFunction(ClanHall.FUNC_SUPPORT);
		if (chf == null || chf.getLvl() == 0)
		{
			writeC(0);
			writeC(0);
		}
		else if ((_ch.getGrade() == 0 && chf.getLvl() < 2) || (_ch.getGrade() == 1 && chf.getLvl() < 4) || (_ch.getGrade() == 2 && chf.getLvl() < 5) || (_ch.getGrade() == 3 && chf.getLvl() < 8))
		{
			writeC(1);
			writeC(1);
		}
		else
		{
			writeC(2);
			writeC(2);
		}
		
		chf = _ch.getFunction(ClanHall.FUNC_DECO_FRONTPLATEFORM);
		if (chf == null || chf.getLvl() == 0)
			writeC(0);
		else if (chf.getLvl() <= 1)
			writeC(1);
		else
			writeC(2);
		
		chf = _ch.getFunction(ClanHall.FUNC_ITEM_CREATE);
		if (chf == null || chf.getLvl() == 0)
			writeC(0);
		else if ((_ch.getGrade() == 0 && chf.getLvl() < 2) || chf.getLvl() < 3)
			writeC(1);
		else
			writeC(2);
		
		writeD(0);
		writeD(0);
	}
}
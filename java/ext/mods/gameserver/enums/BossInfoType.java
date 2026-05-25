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
package ext.mods.gameserver.enums;

import ext.mods.gameserver.model.group.CommandChannel;
import ext.mods.gameserver.network.NpcStringId;

/**
 * Related informations regarding boss.
 */
public enum BossInfoType
{
	AQ(29001, NpcStringId.ID_1800001, NpcStringId.ID_1800005, 36),
	CORE(29006, NpcStringId.ID_1800002, NpcStringId.ID_1800006, 36),
	ORFEN(29014, NpcStringId.ID_1800003, NpcStringId.ID_1800007, 36),
	ZAKEN(29022, NpcStringId.ID_1800004, NpcStringId.ID_1800008, 36),
	REGULAR(0, NpcStringId.ID_1800009, NpcStringId.ID_1800010, 18),
	BAIUM(29020, NpcStringId.ID_1800009, NpcStringId.ID_1800010, 36),
	ANTHARAS(29019, NpcStringId.ID_1800009, NpcStringId.ID_1800010, 225),
	VALAKAS(29028, NpcStringId.ID_1800009, NpcStringId.ID_1800010, 36);
	
	public static final BossInfoType[] VALUES = values();
	
	private final int _npcId;
	private final NpcStringId _ccRightsMsg;
	private final NpcStringId _ccNoRightsMsg;
	private final int _requiredMembersAmount;
	
	private BossInfoType(int npcId, NpcStringId ccRightsMsg, NpcStringId ccNoRightsMsg, int requiredMembersAmount)
	{
		_npcId = npcId;
		_ccRightsMsg = ccRightsMsg;
		_ccNoRightsMsg = ccNoRightsMsg;
		_requiredMembersAmount = requiredMembersAmount;
	}
	
	public int getNpcId()
	{
		return _npcId;
	}
	
	public NpcStringId getCcRightsMsg()
	{
		return _ccRightsMsg;
	}
	
	public NpcStringId getCcNoRightsMsg()
	{
		return _ccNoRightsMsg;
	}
	
	public int getRequiredMembersAmount()
	{
		return _requiredMembersAmount;
	}
	
	public static BossInfoType getBossInfo(int npcId)
	{
		for (BossInfoType bit : VALUES)
			if (bit.getNpcId() == npcId)
				return bit;
			
		return REGULAR;
	}
	
	public static boolean isCcMeetCondition(CommandChannel cc, int npcId)
	{
		return cc != null && cc.getMembersCount() > getBossInfo(npcId).getRequiredMembersAmount();
	}
}
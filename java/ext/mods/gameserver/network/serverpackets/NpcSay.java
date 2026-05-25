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

import ext.mods.gameserver.enums.SayType;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Summon;

public final class NpcSay extends L2GameServerPacket
{
	private final int _objectId;
	private final SayType _type;
	private final int _npcId;
	private final String _text;
	
	/**
	 * For non-existing {@link Npc} instances, while we want to use {@link Npc} name.<br>
	 * Display a {@link Npc}'s name (from client id).
	 * @param npcId : The template ID of {@link Npc}.
	 * @param type : Type of message.
	 * @param text : The message.
	 */
	public NpcSay(int npcId, SayType type, String text)
	{
		_objectId = 0;
		_type = type;
		_npcId = 1000000 + npcId;
		_text = text;
	}
	
	/**
	 * The {@link Npc} saying a message.<br>
	 * Display a {@link Npc}'s name (from client id). Show message above {@link Npc} instance's head.
	 * @param npc : The {@link Npc}.
	 * @param type : Type of message.
	 * @param text : The message.
	 */
	public NpcSay(Npc npc, SayType type, String text)
	{
		_objectId = npc.getObjectId();
		_type = type;
		_npcId = 1000000 + npc.getNpcId();
		_text = text;
	}
	
	/**
	 * The {@link Summon} saying a message.<br>
	 * Display a {@link Npc}'s name (from client id). Show message above {@link Npc} instance's head.
	 * @param summon : The {@link Summon}.
	 * @param type : Type of message.
	 * @param text : The message.
	 */
	public NpcSay(Summon summon, SayType type, String text)
	{
		_objectId = summon.getObjectId();
		_type = type;
		_npcId = 1000000 + summon.getNpcId();
		_text = text;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x02);
		writeD(_objectId);
		writeD(_type.ordinal());
		writeD(_npcId);
		writeS(_text);
	}
}
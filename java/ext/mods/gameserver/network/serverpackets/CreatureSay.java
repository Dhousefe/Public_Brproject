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

import java.sql.ResultSet;
import java.sql.SQLException;

import ext.mods.gameserver.data.LocalizedString;
import ext.mods.gameserver.enums.SayType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.network.SystemMessageId;

public class CreatureSay extends L2GameServerPacket
{
	private final int _objectId;
	private final SayType _sayType;
	
	private String _name;
	private String _content;
	
	private int _sysStringId;
	private int _sysMsgId;
	
	private Object[] _args;
	
	private LocalizedString _localeContent;
	
	/**
	 * The {@link Creature} says a message.<br>
	 * <br>
	 * Display a {@link Creature}'s name. Show message above the {@link Creature} instance's head.
	 * @param creature : The {@link Creature} who speaks.
	 * @param sayType : The {@link SayType} chat channel to send.
	 * @param content : The {@link String} content to send.
	 */
	public CreatureSay(Creature creature, SayType sayType, String content)
	{
		this(creature.getObjectId(), sayType, creature.getName(), content);
	}
	
	/**
	 * Load and generate a {@link CreatureSay} from the database.
	 * @see #CreatureSay(Creature, SayType, String)
	 * @param rs : The {@link ResultSet} needed to feed variables.
	 * @throws SQLException : If the columnLabel is not valid; if a database access error occurs or this method is called on a closed {@link ResultSet}.
	 */
	public CreatureSay(ResultSet rs) throws SQLException
	{
		this(rs.getInt("player_oid"), Enum.valueOf(SayType.class, rs.getString("type")), rs.getString("player_name"), rs.getString("content"));
	}
	
	/**
	 * Announcement of a message.<br>
	 * <br>
	 * Display a defined character name.
	 * @param type : The {@link SayType} chat channel to send.
	 * @param name : The {@link String} name to be displayed in front of message.
	 * @param content : The {@link String} content to send.
	 */
	public CreatureSay(SayType type, String name, String content)
	{
		this(0, type, name, content);
	}
	
	/**
	 * A character says a message.<br>
	 * <br>
	 * Display a defined character name. Show message above the {@link Creature} instance's head.
	 * @param objectId : The objectId used to show the chat bubble over the head.
	 * @param sayType : The {@link SayType} chat channel to send.
	 * @param name : The {@link String} name to be displayed in front of message.
	 * @param content : The {@link String} content to send.
	 */
	public CreatureSay(int objectId, SayType sayType, String name, String content)
	{
		_objectId = objectId;
		_sayType = sayType;
		_name = name;
		_content = content;
	}
	
	public CreatureSay(int objectId, SayType sayType, String name, LocalizedString localeContent, Object... args)
	{
		_objectId = objectId;
		_sayType = sayType;
		_name = name;
		_localeContent = localeContent;
		_args = args;
	}
	
	/**
	 * Announce a boat message.
	 * @param sayType : The {@link SayType} chat channel to send.
	 * @param sysStringId : The client's sysString ID (see sysstring-e.dat).
	 * @param sysMsgId : The {@link SystemMessageId} to be shown.
	 */
	public CreatureSay(SayType sayType, int sysStringId, SystemMessageId sysMsgId)
	{
		_objectId = 0;
		_sayType = sayType;
		_sysStringId = sysStringId;
		_sysMsgId = sysMsgId.getId();
	}
	
	public int getObjectId()
	{
		return _objectId;
	}
	
	public SayType getSayType()
	{
		return _sayType;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public String getContent()
	{
		return _content;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x4a);
		writeD(_objectId);
		writeD(_sayType.ordinal());
		if (_content != null)
		{
			writeS(_name);
			writeS(_content);
		}
		else
		{
			if (_localeContent != null)
			{
				writeS(_name);
				writeS(_localeContent.get(getClient().getPlayer().getLocale()).formatted(_args));
			}
			else
			{
				writeD(_sysStringId);
				writeD(_sysMsgId);
			}
		}
	}
}
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

import java.util.ArrayList;
import java.util.List;

import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.Item;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.records.CnfDlgData;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.skills.AbstractEffect;
import ext.mods.gameserver.skills.L2Skill;

public class ConfirmDlg extends L2GameServerPacket
{
	private static final int TYPE_ZONE_NAME = 7;
	private static final int TYPE_SKILL_NAME = 4;
	private static final int TYPE_ITEM_NAME = 3;
	private static final int TYPE_NPC_NAME = 2;
	private static final int TYPE_NUMBER = 1;
	private static final int TYPE_TEXT = 0;
	
	private final int _messageId;
	private final List<CnfDlgData> _info = new ArrayList<>();
	
	private int _time = 0;
	private int _requesterId = 0;
	
	public ConfirmDlg(int messageId)
	{
		_messageId = messageId;
	}
	
	public ConfirmDlg(SystemMessageId messageId)
	{
		_messageId = messageId.getId();
	}
	
	public ConfirmDlg addString(String text)
	{
		_info.add(new CnfDlgData(TYPE_TEXT, text));
		return this;
	}
	
	public ConfirmDlg addNumber(int number)
	{
		_info.add(new CnfDlgData(TYPE_NUMBER, number));
		return this;
	}
	
	public ConfirmDlg addCharName(Creature cha)
	{
		return addString(cha.getName());
	}
	
	public ConfirmDlg addItemName(ItemInstance item)
	{
		return addItemName(item.getItem().getItemId());
	}
	
	public ConfirmDlg addItemName(Item item)
	{
		return addItemName(item.getItemId());
	}
	
	public ConfirmDlg addItemName(int id)
	{
		_info.add(new CnfDlgData(TYPE_ITEM_NAME, id));
		return this;
	}
	
	public ConfirmDlg addZoneName(Location loc)
	{
		_info.add(new CnfDlgData(TYPE_ZONE_NAME, loc));
		return this;
	}
	
	public ConfirmDlg addSkillName(AbstractEffect effect)
	{
		return addSkillName(effect.getSkill());
	}
	
	public ConfirmDlg addSkillName(L2Skill skill)
	{
		return addSkillName(skill.getId(), skill.getLevel());
	}
	
	public ConfirmDlg addSkillName(int id)
	{
		return addSkillName(id, 1);
	}
	
	public ConfirmDlg addSkillName(int id, int lvl)
	{
		_info.add(new CnfDlgData(TYPE_SKILL_NAME, new IntIntHolder(id, lvl)));
		return this;
	}
	
	public ConfirmDlg addTime(int time)
	{
		_time = time;
		return this;
	}
	
	public ConfirmDlg addRequesterId(int id)
	{
		_requesterId = id;
		return this;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xed);
		writeD(_messageId);
		
		if (_info.isEmpty())
		{
			writeD(0x00);
			writeD(_time);
			writeD(_requesterId);
		}
		else
		{
			writeD(_info.size());
			
			for (CnfDlgData data : _info)
			{
				writeD(data.type());
				
				switch (data.type())
				{
					case TYPE_TEXT:
						writeS((String) data.object());
						break;
					
					case TYPE_NUMBER, TYPE_NPC_NAME, TYPE_ITEM_NAME:
						writeD((Integer) data.object());
						break;
					
					case TYPE_SKILL_NAME:
						final IntIntHolder info = (IntIntHolder) data.object();
						writeD(info.getId());
						writeD(info.getValue());
						break;
					
					case TYPE_ZONE_NAME:
						writeLoc((Location) data.object());
						break;
				}
			}
			if (_time != 0)
				writeD(_time);
			if (_requesterId != 0)
				writeD(_requesterId);
		}
	}
}
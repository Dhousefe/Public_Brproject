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

import ext.mods.gameserver.data.xml.PlayerData;
import ext.mods.gameserver.enums.actors.ClassId;
import ext.mods.gameserver.model.actor.template.PlayerTemplate;

public class NewCharacterSuccess extends L2GameServerPacket
{
	private final List<PlayerTemplate> _templates = new ArrayList<>();
	
	public static final NewCharacterSuccess STATIC_PACKET = new NewCharacterSuccess();
	
	private NewCharacterSuccess()
	{
		_templates.add(PlayerData.getInstance().getTemplate(0));
		_templates.add(PlayerData.getInstance().getTemplate(ClassId.HUMAN_FIGHTER));
		_templates.add(PlayerData.getInstance().getTemplate(ClassId.HUMAN_MYSTIC));
		_templates.add(PlayerData.getInstance().getTemplate(ClassId.ELVEN_FIGHTER));
		_templates.add(PlayerData.getInstance().getTemplate(ClassId.ELVEN_MYSTIC));
		_templates.add(PlayerData.getInstance().getTemplate(ClassId.DARK_FIGHTER));
		_templates.add(PlayerData.getInstance().getTemplate(ClassId.DARK_MYSTIC));
		_templates.add(PlayerData.getInstance().getTemplate(ClassId.ORC_FIGHTER));
		_templates.add(PlayerData.getInstance().getTemplate(ClassId.ORC_MYSTIC));
		_templates.add(PlayerData.getInstance().getTemplate(ClassId.DWARVEN_FIGHTER));
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x17);
		writeD(_templates.size());
		
		for (PlayerTemplate template : _templates)
		{
			writeD(template.getRace().ordinal());
			writeD(template.getClassId().getId());
			writeD(0x46);
			writeD(template.getBaseSTR());
			writeD(0x0a);
			writeD(0x46);
			writeD(template.getBaseDEX());
			writeD(0x0a);
			writeD(0x46);
			writeD(template.getBaseCON());
			writeD(0x0a);
			writeD(0x46);
			writeD(template.getBaseINT());
			writeD(0x0a);
			writeD(0x46);
			writeD(template.getBaseWIT());
			writeD(0x0a);
			writeD(0x46);
			writeD(template.getBaseMEN());
			writeD(0x0a);
		}
	}
}
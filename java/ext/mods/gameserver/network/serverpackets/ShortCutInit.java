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

import ext.mods.gameserver.model.Shortcut;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.records.Timestamp;
import ext.mods.gameserver.skills.L2Skill;

public class ShortCutInit extends L2GameServerPacket
{
	private final Player _player;
	private final Shortcut[] _shortcuts;
	
	public ShortCutInit(Player player)
	{
		_player = player;
		_shortcuts = player.getShortcutList().getShortcuts();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x45);
		writeD(_shortcuts.length);
		
		for (Shortcut shortcut : _shortcuts)
		{
			writeD(shortcut.getType().ordinal());
			writeD(shortcut.getSlot() + shortcut.getPage() * 12);
			
			switch (shortcut.getType())
			{
				case ITEM:
					writeD(shortcut.getId());
					writeD(shortcut.getCharacterType());
					writeD(shortcut.getSharedReuseGroup());
					
					final ItemInstance item = _player.getInventory().getItemByObjectId(shortcut.getId());
					if (item == null)
					{
						writeD(0x00);
						writeD(0x00);
						writeD(0x00);
					}
					else if (!item.isEtcItem())
					{
						writeD(0x00);
						writeD(0x00);
						writeD((item.isAugmented()) ? item.getAugmentation().getId() : 0x00);
					}
					else
					{
						final IntIntHolder[] skills = item.getEtcItem().getSkills();
						if (skills == null)
						{
							writeD(0x00);
							writeD(0x00);
						}
						else
						{
							final L2Skill itemSkill = skills[0].getSkill();
							
							final Timestamp timestamp = _player.getReuseTimeStamp().get(itemSkill.getReuseHashCode());
							if (timestamp == null)
							{
								writeD(0x00);
								writeD(0x00);
							}
							else
							{
								writeD((int) (timestamp.getRemaining() / 1000L));
								writeD((int) (itemSkill.getReuseDelay() / 1000L));
							}
						}
						writeD(0x00);
					}
					break;
				
				case SKILL:
					writeD(shortcut.getId());
					writeD(shortcut.getLevel());
					writeC(0x00);
					writeD(shortcut.getCharacterType());
					break;
				
				default:
					writeD(shortcut.getId());
					writeD(shortcut.getCharacterType());
			}
		}
	}
}
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
package ext.mods.gameserver.scripting.script.feature;

import ext.mods.commons.lang.StringUtil;

import ext.mods.gameserver.enums.EventHandler;
import ext.mods.gameserver.enums.TeleportType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.itemcontainer.PcInventory;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.WarehouseWithdrawList;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;
import ext.mods.gameserver.skills.L2Skill;

/**
 * This script supports :
 * <ul>
 * <li>Ketra Orc Village functions</li>
 * <li>Quests failures && alliance downgrade if you kill an allied mob.</li>
 * <li>Petrification effect in case an allied player helps a neutral or enemy.</li>
 * </ul>
 */
public class KetraOrcSupport extends Quest
{
	private static final int KADUN = 31370;
	private static final int WAHKAN = 31371;
	private static final int ASEFA = 31372;
	private static final int ATAN = 31373;
	private static final int JAFF = 31374;
	private static final int JUMARA = 31375;
	private static final int KURFA = 31376;
	
	private static final int HORN = 7186;
	
	private static final int[] KETRAS =
	{
		21324,
		21325,
		21327,
		21328,
		21329,
		21331,
		21332,
		21334,
		21335,
		21336,
		21338,
		21339,
		21340,
		21342,
		21343,
		21344,
		21345,
		21346,
		21347,
		21348,
		21349
	};
	
	private static final IntIntHolder[] BUFFS =
	{
		new IntIntHolder(4359, 2),
		new IntIntHolder(4360, 2),
		new IntIntHolder(4345, 3),
		new IntIntHolder(4355, 3),
		new IntIntHolder(4352, 3),
		new IntIntHolder(4354, 3),
		new IntIntHolder(4356, 6),
		new IntIntHolder(4357, 6)
	};
	
	/**
	 * Names of missions which will be automatically dropped if the alliance is broken.
	 */
	private static final String[] KETRA_QUESTS =
	{
		"Q605_AllianceWithKetraOrcs",
		"Q606_WarWithVarkaSilenos",
		"Q607_ProveYourCourage",
		"Q608_SlayTheEnemyCommander",
		"Q609_MagicalPowerOfWater_Part1",
		"Q610_MagicalPowerOfWater_Part2"
	};
	
	public KetraOrcSupport()
	{
		super(-1, "feature");
		
		addFirstTalkId(KADUN, WAHKAN, ASEFA, ATAN, JAFF, JUMARA, KURFA);
		addTalkId(ASEFA, JAFF, KURFA);
		
		addEventIds(KETRAS, EventHandler.ATTACKED, EventHandler.CLAN_ATTACKED, EventHandler.MY_DYING, EventHandler.SEE_SPELL);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg();
		
		if (StringUtil.isDigit(event))
		{
			final IntIntHolder buff = BUFFS[Integer.parseInt(event)];
			if (player.getInventory().getItemCount(HORN) >= buff.getValue())
			{
				htmltext = "31372-4.htm";
				takeItems(player, HORN, buff.getValue());
				npc.getAI().addCastDesire(player, buff.getId(), 1, 1000000);
			}
		}
		else if (event.equals("Withdraw"))
		{
			if (player.getWarehouse().getSize() == 0)
				htmltext = "31374-0.htm";
			else
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				player.setActiveWarehouse(player.getWarehouse());
				player.sendPacket(new WarehouseWithdrawList(player, 1));
			}
		}
		else if (event.equals("Teleport"))
		{
			switch (player.getAllianceWithVarkaKetra())
			{
				case 4:
					npc.showTeleportWindow(player, TeleportType.STANDARD);
					return null;
				
				case 5:
					npc.showTeleportWindow(player, TeleportType.ALLY);
					return null;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg();
		
		final int allianceLevel = player.getAllianceWithVarkaKetra();
		
		switch (npc.getNpcId())
		{
			case KADUN:
				if (allianceLevel > 0)
					htmltext = "31370-friend.htm";
				else
					htmltext = "31370-no.htm";
				break;
			
			case WAHKAN:
				if (allianceLevel > 0)
					htmltext = "31371-friend.htm";
				else
					htmltext = "31371-no.htm";
				break;
			
			case ASEFA:
				if (allianceLevel < 1)
					htmltext = "31372-3.htm";
				else if (allianceLevel < 3 && allianceLevel > 0)
					htmltext = "31372-1.htm";
				else if (allianceLevel > 2)
				{
					if (player.getInventory().hasItems(HORN))
						htmltext = "31372-4.htm";
					else
						htmltext = "31372-2.htm";
				}
				break;
			
			case ATAN:
				if (player.getKarma() >= 1)
					htmltext = "31373-pk.htm";
				else if (allianceLevel <= 0)
					htmltext = "31373-no.htm";
				else if (allianceLevel == 1 || allianceLevel == 2)
					htmltext = "31373-1.htm";
				else
					htmltext = "31373-2.htm";
				break;
			
			case JAFF:
				switch (allianceLevel)
				{
					case 1:
						htmltext = "31374-1.htm";
						break;
					case 2, 3:
						htmltext = "31374-2.htm";
						break;
					default:
						if (allianceLevel <= 0)
							htmltext = "31374-no.htm";
						else if (player.getWarehouse().getSize() == 0)
							htmltext = "31374-3.htm";
						else
							htmltext = "31374-4.htm";
						break;
				}
				break;
			
			case JUMARA:
				switch (allianceLevel)
				{
					case 2:
						htmltext = "31375-1.htm";
						break;
					case 3, 4:
						htmltext = "31375-2.htm";
						break;
					case 5:
						htmltext = "31375-3.htm";
						break;
					default:
						htmltext = "31375-no.htm";
						break;
				}
				break;
			
			case KURFA:
				if (allianceLevel <= 0)
					htmltext = "31376-no.htm";
				else if (allianceLevel > 0 && allianceLevel < 4)
					htmltext = "31376-1.htm";
				else if (allianceLevel == 4)
					htmltext = "31376-2.htm";
				else
					htmltext = "31376-3.htm";
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		final Player player = attacker.getActingPlayer();
		if (player != null && player.isAlliedWithKetra())
		{
			npc.getAI().addCastDesire(attacker, 4515, 1, 1000000);
			npc.getAI().getAggroList().remove(attacker);
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		final Player player = attacker.getActingPlayer();
		if (player != null && player.isAlliedWithKetra())
		{
			called.getAI().addCastDesire(attacker, 4515, 1, 1000000);
			called.getAI().getAggroList().remove(attacker);
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		final Player player = killer.getActingPlayer();
		if (player != null)
		{
			final Party party = player.getParty();
			if (party != null)
			{
				for (Player member : party.getMembers())
					testKetraDemote(member);
			}
			else
				testKetraDemote(player);
		}
		super.onMyDying(npc, killer);
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		super.onSeeSpell(npc, caster, skill, targets, isPet);
	}
	
	/**
	 * That method drops current alliance and retrograde badge.<BR>
	 * If any Varka quest is in progress, it stops the quest (and drop all related qItems).
	 * @param player : The {@link Player} to check.
	 */
	private static void testKetraDemote(Player player)
	{
		if (player.isAlliedWithKetra())
		{
			player.setAllianceWithVarkaKetra(0);
			
			final PcInventory inventory = player.getInventory();
			
			for (int i = 7215; i >= 7211; i--)
			{
				ItemInstance item = inventory.getItemByItemId(i);
				if (item != null)
				{
					player.destroyItemByItemId(i, item.getCount(), true);
					
					if (i != 7211)
						player.addItem(i - 1, 1, true);
					
					break;
				}
			}
			
			for (String mission : KETRA_QUESTS)
			{
				QuestState pst = player.getQuestList().getQuestState(mission);
				if (pst != null)
					pst.exitQuest(true);
			}
		}
	}
}
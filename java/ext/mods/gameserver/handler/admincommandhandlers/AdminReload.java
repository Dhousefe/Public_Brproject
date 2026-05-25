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
package ext.mods.gameserver.handler.admincommandhandlers;

import java.util.StringTokenizer;

import ext.mods.Config;
import ext.mods.dressme.DressMeData;
import ext.mods.gameserver.custom.data.AuctionCurrencies;
import ext.mods.gameserver.custom.data.DonateData;
import ext.mods.gameserver.custom.data.EnchantData;
import ext.mods.gameserver.custom.data.MissionData;
import ext.mods.gameserver.custom.data.PcCafeData;
import ext.mods.gameserver.custom.data.PolymorphData;
import ext.mods.gameserver.custom.data.PvPData;
import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.cache.CrestCache;
import ext.mods.gameserver.data.manager.BuyListManager;
import ext.mods.gameserver.data.manager.CursedWeaponManager;
import ext.mods.gameserver.data.manager.SpawnManager;
import ext.mods.gameserver.data.manager.ZoneManager;
import ext.mods.gameserver.data.xml.AdminData;
import ext.mods.gameserver.data.xml.AnnouncementData;
import ext.mods.gameserver.data.xml.BoatData;
import ext.mods.gameserver.data.xml.DoorData;
import ext.mods.gameserver.data.xml.InstantTeleportData;
import ext.mods.gameserver.data.xml.ItemData;
import ext.mods.gameserver.data.xml.MultisellData;
import ext.mods.gameserver.data.xml.NpcData;
import ext.mods.gameserver.data.xml.ObserverGroupData;
import ext.mods.gameserver.data.xml.RestartPointData;
import ext.mods.gameserver.data.xml.ScriptData;
import ext.mods.gameserver.data.xml.SysString;
import ext.mods.gameserver.data.xml.TeleportData;
import ext.mods.gameserver.data.xml.WalkerRouteData;
import ext.mods.gameserver.handler.AdminCommandHandler; 
import ext.mods.gameserver.handler.IAdminCommandHandler;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.quests.QuestData;
import ext.mods.Crypta.RandomManager;
import ext.mods.Crypta.GlobalDropManager;
import ext.mods.CapsuleBox.CapsuleBoxData;
import ext.mods.commons.logging.CLogger;



public class AdminReload implements IAdminCommandHandler
{
	public static final CLogger LOGGER = new CLogger(AdminReload.class.getName());
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_reload"
	};
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		final StringTokenizer st = new StringTokenizer(command);
		st.nextToken();
		
		try
		{
			do
			{
				String type = st.nextToken();
				
				
				if (type.startsWith("admin"))
				{
					AdminData.getInstance().reload();
					player.sendMessage("Admin data has been reloaded.");
				}
				else if (type.startsWith("announcement"))
				{
					AnnouncementData.getInstance().reload();
					player.sendMessage("The content of announcements.xml has been reloaded.");
				}
				else if (type.startsWith("auction"))
				{
					AuctionCurrencies.getInstance().reload();
					player.sendMessage("Auction have been reloaded.");
				}
				else if (type.startsWith("boat"))
				{
					BoatData.getInstance().reload();
					player.sendMessage("Boat have been reloaded.");
				}
				else if (type.startsWith("buylist"))
				{
					BuyListManager.getInstance().reload();
					player.sendMessage("Buylists have been reloaded.");
				}
				else if (type.startsWith("capsule"))
				{
					CapsuleBoxData.getInstance().reload();
					player.sendMessage("Capsule Box have been reloaded.");
				}
				else if (type.startsWith("config"))
				{
					Config.loadGameServer();
					player.sendMessage("Configs files have been reloaded.");
				}
				else if (type.startsWith("crest"))
				{
					CrestCache.getInstance().reload();
					player.sendMessage("Crests have been reloaded.");
				}
				else if (type.startsWith("cw"))
				{
					CursedWeaponManager.getInstance().reload();
					player.sendMessage("Cursed weapons have been reloaded.");
				}
				else if (type.startsWith("donate"))
				{
					DonateData.getInstance().reload();
					player.sendMessage("Donate have been reloaded.");
				}
				else if (type.startsWith("door"))
				{
					DoorData.getInstance().reload();
					player.sendMessage("Doors instance has been reloaded.");
				}
				else if (type.startsWith("enchant"))
				{
					EnchantData.getInstance().reload();
					player.sendMessage("Enchant have been reloaded.");
				}
				else if (type.startsWith("farm_event"))
				{

					Object RandomManagerIntance = RandomManager.getInstance();
					if (RandomManagerIntance == null)
					{
						player.sendMessage("AdminReload: RandomManager is not available.");
						return;
					}

					try
					{
						RandomManager.getInstance().reload();
						player.sendMessage("Random Farm Mod has been reloaded.");
					}
					catch (Exception e)
					{
						player.sendMessage("AdminReload: Error accessing RandomManager.");
						e.printStackTrace();
						return;
					}
					
				}
				else if (type.startsWith("global_drop"))
				{
					
					Object blobaldrops = GlobalDropManager.getInstance();
					if (blobaldrops == null)
					{
						player.sendMessage("AdminReload: GlobalDropManager is not available.");
						return;
					}

					try
					{
						GlobalDropManager.getInstance().reload();
						player.sendMessage("Global Drop Mod has been reloaded.");
					}
					catch (Exception e)
					{
						player.sendMessage("AdminReload: Error accessing GlobalDropManager.");
						e.printStackTrace();
						return;
					}


					
				}
				else if (type.startsWith("htm"))
				{
					HTMLData.getInstance().reload();
					player.sendMessage("The HTM cache has been reloaded.");
				}
				else if (type.startsWith("item"))
				{
					ItemData.getInstance().reload();
					player.sendMessage("Items' templates have been reloaded.");
				}
				else if (type.startsWith("mission"))
				{
					MissionData.getInstance().reload();
					player.sendMessage("Mission have been reloaded.");
				}
				else if (type.equals("multisell"))
				{
					MultisellData.getInstance().reload();
					player.sendMessage("The multisell instance has been reloaded.");
				}
				else if (type.equals("npc"))
				{
					NpcData.getInstance().reload();
					ScriptData.getInstance().reload();
					player.sendMessage("NPCs templates and Scripts have been reloaded.");
				}
				else if (type.startsWith("npcwalker"))
				{
					WalkerRouteData.getInstance().reload();
					player.sendMessage("Walking routes have been reloaded.");
				}
				else if (type.startsWith("observer"))
				{
					ObserverGroupData.getInstance().reload();
					player.sendMessage("ObserverGroupData have been reloaded.");
				}
				else if (type.startsWith("pccafe"))
				{
					PcCafeData.getInstance().reload();
					player.sendMessage("PcCafe have been reloaded.");
				}
				else if (type.startsWith("poly"))
				{
					PolymorphData.getInstance().reload();
					player.sendMessage("Polymorph templates have been reloaded.");
				}
				else if (type.startsWith("pvpdata"))
				{
					PvPData.getInstance().reload();
					player.sendMessage("PvPData have been reloaded.");
				}
				else if (type.startsWith("quests"))
				{
					QuestData.getInstance().reload();
					player.sendMessage("QuestCustomData have been reloaded.");
				}
				else if (type.startsWith("restart"))
				{
					RestartPointData.getInstance().reload();
					player.sendMessage("RestartPointData have been reloaded.");
				}
				else if (type.equals("script"))
				{
					ScriptData.getInstance().reload();
					player.sendMessage("Scripts have been reloaded.");
				}
				else if (type.startsWith("skins"))
				{
					DressMeData.getInstance().reload();
					player.sendMessage("Skins have been reloaded.");
				}
				else if (type.startsWith("skill"))
				{
					SkillTable.getInstance().reload();
					player.sendMessage("Skills' XMLs have been reloaded.");
				}
				else if (type.startsWith("spawnlist"))
				{
					SpawnManager.getInstance().despawn();
					World.getInstance().deleteVisibleNpcSpawns();
					NpcData.getInstance().reload();
					ScriptData.getInstance().reload();
					SpawnManager.getInstance().reload();
				}
				else if (type.startsWith("sysstring"))
				{
					SysString.getInstance().reload();
					player.sendMessage("SysString have been reloaded.");
				}
				else if (type.startsWith("teleport"))
				{
					InstantTeleportData.getInstance().reload();
					TeleportData.getInstance().reload();
					player.sendMessage("Teleport locations have been reloaded.");
				}
				else if (type.startsWith("zone"))
				{
					ZoneManager.getInstance().reload();
					player.sendMessage("Zones have been reloaded.");
				}
				else
					sendUsage(player);
			}
			while (st.hasMoreTokens());
		}
		catch (Exception e)
		{
			sendUsage(player);
		}
	}
	
	public void sendUsage(Player player)
	{
		player.sendMessage("Usage : //reload <admin|announcement|auction|boat|buylist>");
		player.sendMessage("Usage : //reload <capsule|config|crest|cw|donate|door>");
		player.sendMessage("Usage : //reload <enchant|farm_event|global_drop|htm|item|mission>");
		player.sendMessage("Usage : //reload <multisell|npc|npcwalker|observer|pccafe|poly>");
		player.sendMessage("Usage : //reload <pvpdata|quests|restart|script|skins|skill>");
		player.sendMessage("Usage : //reload <spawnlist|sysstring|teleport|zone>");
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import ext.mods.commons.data.Pagination;
import ext.mods.commons.lang.StringUtil;
import ext.mods.Config;
import ext.mods.gameserver.data.manager.FenceManager;
import ext.mods.gameserver.data.manager.SpawnManager;
import ext.mods.gameserver.data.xml.AdminData;
import ext.mods.gameserver.data.xml.NpcData;
import ext.mods.gameserver.handler.IAdminCommandHandler;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Fence;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.spawn.ASpawn;
import ext.mods.gameserver.model.spawn.MultiSpawn;
import ext.mods.gameserver.model.spawn.Spawn;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public class AdminSpawn implements IAdminCommandHandler
{
	private static final String OTHER_XML_FOLDER = Config.DATA_PATH.resolve("xml/spawnlist/custom").toString();
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_list_spawns",
		"admin_show_spawns",
		"admin_spawn",
		"admin_delete",
		"admin_spawn_index",
		"admin_unspawnall",
		"admin_respawnall",
		"admin_npc_index",
		"admin_spawn_once",
		"admin_show_npcs",
		"admin_spawnfence",
		"admin_deletefence",
		"admin_listfence"
	};
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		if (command.startsWith("admin_list_spawns"))
		{
			final StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			
			int npcId = 0;
			
			final String entry = (st.hasMoreTokens()) ? st.nextToken() : null;
			final int page = (st.hasMoreTokens()) ? Integer.parseInt(st.nextToken()) : 1;
			
			if (entry == null)
			{
				final Npc npc = getTarget(Npc.class, player, false);
				if (npc == null)
				{
					player.sendPacket(SystemMessageId.INVALID_TARGET);
					return;
				}
				
				npcId = npc.getNpcId();
			}
			else if (StringUtil.isDigit(entry))
				npcId = Integer.parseInt(entry);
			else
			{
				final NpcTemplate template = NpcData.getInstance().getTemplateByName(entry);
				if (template != null)
					npcId = template.getNpcId();
			}
			
			if (npcId == 0)
			{
				player.sendPacket(SystemMessageId.INVALID_TARGET);
				return;
			}
			
			int row = 0 + (8 * (page - 1));
			
			final Pagination<Npc> list = new Pagination<>(World.getInstance().getNpcs(npcId).stream(), page, PAGE_LIMIT_8);
			list.append("<html><body>");
			
			for (Npc npc : list)
			{
				list.append((row % 2) == 0 ? "<table width=280 height=41 bgcolor=000000><tr>" : "<table width=280 height=41><tr>");
				list.append("<td><a action=\"bypass -h admin_teleport ", npc.getX(), " ", npc.getY(), " ", npc.getZ(), "\">", row);
				
				final ASpawn spawn = npc.getSpawn();
				if (spawn == null)
					list.append(" - (", npc.getPosition(), ")", "</a>");
				else
					list.append(" - ", spawn, "</a><br1>", spawn.getDescription());
				
				list.append("</td></tr></table><img src=\"L2UI.SquareGray\" width=280 height=1>");
				
				row++;
			}
			
			list.generateSpace(42);
			list.generatePages("bypass admin_list_spawns " + npcId + " %page%");
			list.append("</body></html>");
			
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setHtml(list.getContent());
			player.sendPacket(html);
		}
		else if (command.equals("admin_show_spawns"))
			sendFile(player, "spawns.htm");
		else if (command.startsWith("admin_spawn_index"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			try
			{
				st.nextToken();
				int level = Integer.parseInt(st.nextToken());
				int from = 0;
				try
				{
					from = Integer.parseInt(st.nextToken());
				}
				catch (NoSuchElementException nsee)
				{
				}
				showMonsters(player, level, from);
			}
			catch (Exception e)
			{
				sendFile(player, "spawns.htm");
			}
		}
		else if (command.equals("admin_show_npcs"))
			sendFile(player, "npcs.htm");
		else if (command.startsWith("admin_npc_index"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			try
			{
				st.nextToken();
				String letter = st.nextToken();
				int from = 0;
				try
				{
					from = Integer.parseInt(st.nextToken());
				}
				catch (NoSuchElementException nsee)
				{
				}
				showNpcs(player, letter, from);
			}
			catch (Exception e)
			{
				sendFile(player, "npcs.htm");
			}
		}
		else if (command.startsWith("admin_unspawnall"))
		{
			World.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.NPC_SERVER_NOT_OPERATING));
			SpawnManager.getInstance().despawn();
			World.getInstance().deleteVisibleNpcSpawns();
			AdminData.getInstance().broadcastMessageToGMs("NPCs' unspawn is now complete.");
		}
		else if (command.startsWith("admin_respawnall"))
		{
			SpawnManager.getInstance().despawn();
			World.getInstance().deleteVisibleNpcSpawns();
			
			NpcData.getInstance().reload();
			SpawnManager.getInstance().reload();
			AdminData.getInstance().broadcastMessageToGMs("NPCs' respawn is now complete.");
		}
		else if (command.startsWith("admin_spawnfence"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			try
			{
				st.nextToken();
				int type = Integer.parseInt(st.nextToken());
				int sizeX = (Integer.parseInt(st.nextToken()) / 100) * 100;
				int sizeY = (Integer.parseInt(st.nextToken()) / 100) * 100;
				int height = 1;
				if (st.hasMoreTokens())
					height = Math.min(Integer.parseInt(st.nextToken()), 3);
				
				FenceManager.getInstance().addFence(player.getX(), player.getY(), player.getZ(), type, sizeX, sizeY, height);
				
				listFences(player);
			}
			catch (Exception e)
			{
				player.sendMessage("Usage: //spawnfence <type> <width> <length> [height]");
			}
		}
		else if (command.startsWith("admin_deletefence"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			try
			{
				final WorldObject worldObject = World.getInstance().getObject(Integer.parseInt(st.nextToken()));
				if (worldObject instanceof Fence fence)
				{
					FenceManager.getInstance().removeFence(fence);
					
					if (st.hasMoreTokens())
						listFences(player);
				}
				else
					player.sendPacket(SystemMessageId.INVALID_TARGET);
			}
			catch (Exception e)
			{
				player.sendMessage("Usage: //deletefence <objectId>");
			}
		}
		else if (command.startsWith("admin_listfence"))
			listFences(player);
		else if (command.startsWith("admin_spawn"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			try
			{
				final String cmd = st.nextToken();
				final String idOrName = st.nextToken();
				final int respawnTime = (st.hasMoreTokens()) ? Integer.parseInt(st.nextToken()) : 60;
				
				final WorldObject targetWorldObject = getTarget(WorldObject.class, player, true);
				
				NpcTemplate template;
				
				if (idOrName.matches("[0-9]*"))
					template = NpcData.getInstance().getTemplate(Integer.parseInt(idOrName));
				else
					template = NpcData.getInstance().getTemplateByName(idOrName.replace('_', ' '));
				
				try
				{
					final Spawn spawn = new Spawn(template);
					spawn.setLoc(targetWorldObject.getPosition());
					spawn.setRespawnDelay(respawnTime);
					spawn.doSpawn(false);
					SpawnManager.getInstance().addSpawn(spawn, true);
					
					player.sendMessage("You spawned " + template.getName() + ". - Cmd: " + cmd);
				}
				catch (Exception e)
				{
					player.sendPacket(SystemMessageId.APPLICANT_INFORMATION_INCORRECT);
				}
			}
			catch (Exception e)
			{
				sendFile(player, "spawns.htm");
			}
		}
		else if (command.startsWith("admin_delete"))
		{
			final WorldObject targetWorldObject = player.getTarget();
			if (!(targetWorldObject instanceof Npc targetNpc))
			{
				player.sendPacket(SystemMessageId.INVALID_TARGET);
				return;
			}
			
			final ASpawn spawn = targetNpc.getSpawn();
			if (spawn instanceof MultiSpawn multi)
			{
				final String name = multi.getNpc().getName().replaceAll("(\\s|')+", "").toLowerCase() + "_" + multi.getSpawnLocation().getX() + multi.getSpawnLocation().getY();
				final String npcMakerName = multi.getNpc().getName().replaceAll("(\\s|')+", "").toLowerCase() + "_" + multi.getSpawnLocation().getX() + multi.getSpawnLocation().getY() + "1";
				final String fileName = multi.getNpc().getName().replaceAll("(\\s|')+", "").toLowerCase();
				
				final int x = ((spawn.getNpc().getX() - World.WORLD_X_MIN) >> 15) + World.TILE_X_MIN;
				final int y = ((spawn.getNpc().getY() - World.WORLD_Y_MIN) >> 15) + World.TILE_Y_MIN;
				
				final File spawnFile = new File(OTHER_XML_FOLDER + "/" + fileName + "_" + x + "_" + y + ".xml");
				
				if (!spawnFile.exists())
				{
					player.sendPacket(SystemMessageId.INVALID_TARGET);
					return;
				}
				
				multi.doDelete();
				
				final File tempFile = new File(OTHER_XML_FOLDER + "/" + fileName + "_" + x + "_" + y + ".tmp");
				
				try (BufferedReader reader = new BufferedReader(new FileReader(spawnFile));
					BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile)))
				{
					String currentLine;
					boolean inTerritory = false;
					boolean inNpcMaker = false;
					boolean needToDelete = false;
					
					while ((currentLine = reader.readLine()) != null)
					{
						if (currentLine.contains("<territory name=\"" + name))
							inTerritory = true;
						
						if (currentLine.contains("<npcmaker name=\"" + npcMakerName))
							inNpcMaker = true;
						
						if (inTerritory || inNpcMaker)
						{
							if (currentLine.contains("</territory>") || currentLine.contains("</npcmaker>"))
							{
								inTerritory = false;
								inNpcMaker = false;
								if (!needToDelete)
									writer.write(currentLine + "\n");
							}
							needToDelete = true;
						}
						else
							writer.write(currentLine + "\n");
					}
					
					writer.close();
					reader.close();
					
					if (needToDelete)
					{
						spawnFile.delete();
						tempFile.renameTo(spawnFile);
					}
					else
						tempFile.delete();
				}
				catch (Exception e)
				{
					LOGGER.warn("Could not remove spawn from the spawn XML files: " + e);
				}
				
				if (SpawnManager.isEmptyXml(spawnFile))
					spawnFile.delete();
			}
			else
			{
				if (!(spawn instanceof Spawn))
				{
					player.sendPacket(SystemMessageId.INVALID_TARGET);
					return;
				}
				final Spawn spawnObj = (Spawn) spawn;
				spawnObj.setRespawnDelay(0);
				targetNpc.cancelRespawn();
				
				targetNpc.setReadyForRespawn(false);
				
				targetNpc.deleteMe();
				
				SpawnManager.getInstance().deleteSpawn((Spawn) spawn, false);

				SpawnManager.getInstance().deleteSpawn(spawnObj);

				spawnObj.doDelete();
				
				player.sendMessage("You deleted " + targetNpc.getName() + " (respawn prevented).");

				
			}
		}
	}
	
	private static void listFences(Player player)
	{
		final List<Fence> fences = FenceManager.getInstance().getFences();
		final StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body>Total Fences: " + fences.size() + "<br><br>");
		for (Fence fence : fences)
			sb.append("<a action=\"bypass -h admin_deletefence " + fence.getObjectId() + " 1\">Fence: " + fence.getObjectId() + " [" + fence.getX() + " " + fence.getY() + " " + fence.getZ() + "]</a><br>");
		sb.append("</body></html>");
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setHtml(sb.toString());
		player.sendPacket(html);
	}
	
	private static void showMonsters(Player player, int level, int from)
	{
	    final List<NpcTemplate> mobs = NpcData.getInstance().getTemplates(t -> t.isType("Monster") && t.getLevel() == level);
	    final StringBuilder sb = new StringBuilder(200 + mobs.size() * 100);
	    
	    StringUtil.append(sb, "<html><title>Spawn Monster</title><body><table width=570><tr><td>Level: ", level, "</td><td>Total Npc's: ", mobs.size(), "</td></tr></table><br>");
	    
	    int i = from;
	    for (int j = 0; i < mobs.size() && j < 50; i++, j++)
	        StringUtil.append(sb, "<a action=\"bypass -h admin_spawn ", mobs.get(i).getNpcId(), "\">", mobs.get(i).getName(), "</a><br>");
	    
	    if (i == mobs.size())
	        sb.append("<br><center><button value=\"Back\" action=\"bypass -h admin_show_spawns\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></center></body></html>");
	    else
	        StringUtil.append(sb, "<br><center><table width=\"520\"><tr><td><button value=\"Back\" action=\"bypass -h admin_show_spawns\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td><button value=\"Next\" action=\"bypass -h admin_spawn_index ", level, " ", i, "\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr></table></center></body></html>");
	    
	    final NpcHtmlMessage html = new NpcHtmlMessage(0);
	    html.setHtml(sb.toString());
	    player.sendPacket(html);
	}
	
	private static void showNpcs(Player player, String starting, int from)
	{
		final List<NpcTemplate> npcs = NpcData.getInstance().getTemplates(t -> t.isType("Folk") && t.getName().startsWith(starting));
		final StringBuilder sb = new StringBuilder(200 + npcs.size() * 100);
		
		StringUtil.append(sb, "<html><title>Spawn Monster</title><body><p> There are ", npcs.size(), " Npcs whose name starts with ", starting, ":<br>");
		
		int i = from;
		for (int j = 0; i < npcs.size() && j < 50; i++, j++)
			StringUtil.append(sb, "<a action=\"bypass -h admin_spawn ", npcs.get(i).getNpcId(), "\">", npcs.get(i).getName(), "</a><br1>");
		
		if (i == npcs.size())
			sb.append("<br><center><button value=\"Back\" action=\"bypass -h admin_show_npcs\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></center></body></html>");
		else
	        StringUtil.append(sb, "<br><center><table width=\"520\"><tr><td><button value=\"Back\" action=\"bypass -h admin_show_npcs\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td><button value=\"Next\" action=\"bypass -h admin_npc_index ", starting, " ", i, "\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr></table></center></body></html>");
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setHtml(sb.toString());
		player.sendPacket(html);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
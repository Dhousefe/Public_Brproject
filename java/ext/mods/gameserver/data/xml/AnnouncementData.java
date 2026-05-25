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
package ext.mods.gameserver.data.xml;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.commons.data.xml.IXmlReader;
import ext.mods.commons.lang.StringUtil;

import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.enums.SayType;
import ext.mods.gameserver.model.Announcement;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.CreatureSay;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

/**
 * This class loads and stores {@link Announcement}s, the key being dynamically generated on loading.<br>
 * As the storage is a XML, the whole XML needs to be regenerated on Announcement addition/deletion.
 */
public class AnnouncementData implements IXmlReader
{
	private static final String HEADER = "<?xml version='1.0' encoding='utf-8'?> \n<!-- \n@param String message - the message to be announced \n@param Boolean critical - type of announcement (true = critical,false = normal) \n@param Boolean auto - when the announcement will be displayed (true = auto,false = on player login) \n@param Integer initial_delay - time delay for the first announce (used only if auto=true;value in seconds) \n@param Integer delay - time delay for the announces following the first announce (used only if auto=true;value in seconds) \n@param Integer limit - limit of announces (used only if auto=true, 0 = unlimited) \n--> \n";
	
	private final Map<Integer, Announcement> _announcements = new ConcurrentHashMap<>();
	
	protected AnnouncementData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("xml/announcements.xml");
		LOGGER.info("Loaded {} announcements.", _announcements.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "announcement", announcementNode ->
		{
			final NamedNodeMap attrs = announcementNode.getAttributes();
			final String message = parseString(attrs, "message");
			if (message == null || message.isEmpty())
			{
				LOGGER.warn("The message is empty on an announcement. Ignoring it.");
				return;
			}
			
			final boolean critical = parseBoolean(attrs, "critical", false);
			final boolean auto = parseBoolean(attrs, "auto", false);
			if (auto)
			{
				final int initialDelay = parseInteger(attrs, "initial_delay");
				final int delay = parseInteger(attrs, "delay");
				final int limit = Math.max(parseInteger(attrs, "limit"), 0);
				_announcements.put(_announcements.size(), new Announcement(message, critical, auto, initialDelay, delay, limit));
			}
			else
				_announcements.put(_announcements.size(), new Announcement(message, critical));
		}));
	}
	
	public void reload()
	{
		for (Announcement announce : _announcements.values())
			announce.stopTask();
		
		_announcements.clear();
		
		load();
	}
	
	/**
	 * Send stored {@link Announcement}s from _announcements Map to a specific {@link Player}.
	 * @param player : The Player to send infos.
	 * @param autoOrNot : If true, sends only automatic announcements, otherwise send classic ones.
	 */
	public void showAnnouncements(Player player, boolean autoOrNot)
	{
		for (Announcement announce : _announcements.values())
		{
			if (autoOrNot)
				announce.reloadTask();
			else
			{
				if (announce.isAuto())
					continue;
				
				player.sendPacket(new CreatureSay(announce.isCritical() ? SayType.CRITICAL_ANNOUNCE : SayType.ANNOUNCEMENT, player.getName(), announce.getMessage()));
			}
		}
	}
	
	/**
	 * Use {@link World#announceToOnlinePlayers(String, boolean)} in order to send announcement, wrapped into a ioobe try/catch.
	 * @param command : The command to handle.
	 * @param lengthToTrim : The length to trim, in order to send only the message without the command.
	 * @param critical : Is the message critical or not.
	 */
	public void handleAnnounce(String command, int lengthToTrim, boolean critical)
	{
		try
		{
			World.announceToOnlinePlayers(command.substring(lengthToTrim), critical);
		}
		catch (StringIndexOutOfBoundsException e)
		{
		}
	}
	
	/**
	 * Send a static HTM with dynamic announcements content took from _announcements Map to a {@link Player}.
	 * @param player : The Player to send the {@link NpcHtmlMessage} packet.
	 */
	public void listAnnouncements(Player player)
	{
		final StringBuilder sb = new StringBuilder("<br>");
		if (_announcements.isEmpty())
			sb.append("<tr><td>The XML file doesn't contain any content.</td></tr>");
		else
		{
			for (Map.Entry<Integer, Announcement> entry : _announcements.entrySet())
			{
				final int index = entry.getKey();
				final Announcement announce = entry.getValue();
				
				StringUtil.append(sb, "<table width=260><tr><td width=240>#", index, " - ", announce.getMessage(), "</td><td></td></tr></table><table width=260><tr><td>Critical: ", announce.isCritical(), " | Auto: ", announce.isAuto(), "</td><td><button value=\"Delete\" action=\"bypass -h admin_announce del ", index, "\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></td></tr></table>");
			}
		}
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setHtml(HTMLData.getInstance().getHtm(player.getLocale(), "html/admin/announce_list.htm"));
		html.replace("%announces%", sb.toString());
		player.sendPacket(html);
	}
	
	/**
	 * Add an {@link Announcement} but only if the message isn't empty or null. Regenerate the XML.
	 * @param message : The String to announce.
	 * @param critical : Is it a critical announcement or not.
	 * @param auto : Is it using a specific task or not.
	 * @param initialDelay : Initial delay of the task, used only if auto is set to True.
	 * @param delay : Delay of the task, used only if auto is set to True.
	 * @param limit : Maximum amount of loops the task will do before ending.
	 * @return true if the announcement has been successfully added, false otherwise.
	 */
	public boolean addAnnouncement(String message, boolean critical, boolean auto, int initialDelay, int delay, int limit)
	{
		if (message == null || message.isEmpty())
			return false;
		
		if (auto)
			_announcements.put(_announcements.size(), new Announcement(message, critical, auto, initialDelay, delay, limit));
		else
			_announcements.put(_announcements.size(), new Announcement(message, critical));
		
		regenerateXML();
		return true;
	}
	
	/**
	 * End the task linked to an {@link Announcement} and delete it.
	 * @param index : The Map index to remove.
	 */
	public void delAnnouncement(int index)
	{
		_announcements.remove(index).stopTask();
		
		regenerateXML();
	}
	
	/**
	 * This method allows to refresh the XML with infos took from _announcements Map.
	 */
	private void regenerateXML()
	{
		final StringBuilder sb = new StringBuilder(HEADER);
		
		sb.append("<list> \n");
		
		for (Announcement announce : _announcements.values())
			StringUtil.append(sb, "<announcement message=\"", announce.getMessage(), "\" critical=\"", announce.isCritical(), "\" auto=\"", announce.isAuto(), "\" initial_delay=\"", announce.getInitialDelay(), "\" delay=\"", announce.getDelay(), "\" limit=\"", announce.getLimit(), "\" /> \n");
		
		sb.append("</list>");
		
		try (FileWriter fw = new FileWriter(new File("./data/xml/announcements.xml")))
		{
			fw.write(sb.toString());
		}
		catch (Exception e)
		{
			LOGGER.error("Error regenerating XML.", e);
		}
	}
	
	public static AnnouncementData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final AnnouncementData INSTANCE = new AnnouncementData();
	}
}
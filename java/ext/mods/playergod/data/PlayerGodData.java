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
package ext.mods.playergod.data;

import java.nio.file.Path;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import ext.mods.playergod.holder.PlayerGodHolder;

public class PlayerGodData implements IXmlReader
{
	private PlayerGodHolder holder;
	
	public PlayerGodData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("custom/mods/PlayerGOD.xml");
		LOGGER.info("Loaded PlayerGOD configuration.");
	}
		
	@Override
	public void parseDocument(Document doc, Path path)
	{
		final StatSet set = new StatSet();
		
		final Node root = doc.getFirstChild();
		final NamedNodeMap rootAttr = root.getAttributes();
		
		set.set("enabled", Boolean.parseBoolean(rootAttr.getNamedItem("enabled").getNodeValue()));
		
		forEach(root, node ->
		{
			switch (node.getNodeName())
			{
				case "Conditions":
					forEach(node, cond ->
					{
						switch (cond.getNodeName())
						{
							case "KillsRequired":
								set.set("killsRequired", Integer.parseInt(cond.getTextContent()));
								break;
							case "TimeWindow":
								set.set("timeWindow", Integer.parseInt(cond.getTextContent()));
								break;
							case "KillAnnouncement":
				                set.set("killAnnouncement", cond.getTextContent());
				                break;
						}
					});
					break;
				
				case "HeroAura":
					forEach(node, aura ->
					{
						switch (aura.getNodeName())
						{
							case "Duration":
								set.set("heroAuraDuration", Integer.parseInt(aura.getTextContent()));
								break;
							case "AuraOnly":
								set.set("auraOnly", Boolean.parseBoolean(aura.getTextContent()));
								break;
						}
					});
					break;
				
				case "LoginAnnouncement":
					NamedNodeMap annAttr = node.getAttributes();
					set.set("loginAnnouncementEnabled", Boolean.parseBoolean(annAttr.getNamedItem("enabled").getNodeValue()));
					forEach(node, msg ->
					{
						if ("Message".equals(msg.getNodeName()))
							set.set("loginMessage", msg.getTextContent());
					});
					break;
			}
		});
		
		holder = new PlayerGodHolder(set);
	}
	
	public PlayerGodHolder getHolder()
	{
		return holder;
	}
	
	public int size()
	{
		return holder != null ? 1 : 0;
	}
	
	public static PlayerGodData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		private static final PlayerGodData _instance = new PlayerGodData();
	}
}
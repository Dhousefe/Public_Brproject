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
package ext.mods.loginserver.data.xml;

import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.HashMap;

import ext.mods.commons.data.xml.IXmlReader;
import ext.mods.commons.logging.CLogger;

import ext.mods.loginserver.data.manager.ProxyManager;
import ext.mods.loginserver.model.L2Proxy;
import ext.mods.loginserver.model.L2ProxyInfo;

import org.w3c.dom.Document;

public class ProxyDataLoader implements IXmlReader
{
	private static final CLogger LOGGER = new CLogger(ProxyDataLoader.class.getName());
	
	@Override
	public void load()
	{
		parseDataFile("custom/mods/proxy.xml");
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		var proxyInfoList = new HashMap<Integer, L2ProxyInfo>();
		var proxies = new HashMap<Integer, L2Proxy>();
		
		forEach(doc, "list", listNode ->
		{
			forEach(listNode, "gameserver", gameserverNode ->
			{
				final var gameserverSet = parseAttributes(gameserverNode);
				var serverId = gameserverSet.getInteger("serverId");
				var hidesGameserver = gameserverSet.getBool("hide");
				var fallbackToGameserver = gameserverSet.getBool("fallbackToGameserver");
				var proxyInfo = new L2ProxyInfo(serverId, hidesGameserver, fallbackToGameserver);
				proxyInfoList.put(serverId, proxyInfo);
				
				forEach(gameserverNode, "proxy", proxyNode ->
				{
					final var proxySet = parseAttributes(proxyNode);
					try
					{
						final var proxy = new L2Proxy(serverId, proxySet.getInteger("proxyServerId"), proxySet.getString("proxyHost"), proxySet.getInteger("proxyPort"), proxySet.getString("apiKey"), proxySet.getInteger("apiPort"), proxySet.getBool("validateHealth"));
						proxies.put(proxySet.getInteger("proxyServerId"), proxy);
					}
					catch (UnknownHostException ex)
					{
						LOGGER.warn("Failed to process proxy due to badly formatted proxy host", ex);
					}
				});
			});
		});
		
		ProxyManager.getInstance().initialise(proxyInfoList, proxies);
	}
	
	public static ProxyDataLoader getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ProxyDataLoader INSTANCE = new ProxyDataLoader();
	}
}
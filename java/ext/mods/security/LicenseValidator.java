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
package ext.mods.security;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import ext.mods.commons.logging.CLogger;

public class LicenseValidator
{
	protected static CLogger LOGGER = new CLogger(LicenseValidator.class.getName());
	
	public static String getPublicIPAddress()
	{
		try
		{
			HttpClient client = HttpClient.newHttpClient();
			
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://api.ipify.org")).GET().build();
			
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			
			return response.body();
		}
		catch (Exception e)
		{
			LOGGER.info("⚠ Não foi possível detectar o IP público. Usando 127.0.0.1.");
			return "127.0.0.1";
		}
	}
	
	public static String checkLicenseAndGetExpiry(String ip, String key, String email)
	{
		if ("brprojeto@l2jbrasil.com".equalsIgnoreCase(email))
		{
			return "Nunca";
		}
		return null;
	}
	
}

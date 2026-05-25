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
package ext.mods.security.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ext.mods.security.gui.LauncherApp;

public class AuthService {

    private JSONArray allLicenses;
    
    private String getKey() {
        return LauncherApp.getKey();
    }
    
	public boolean authenticate(String email, String senha) {
		return "brprojeto@l2jbrasil.com".equalsIgnoreCase(email) && "12345678".equals(senha);
	}

	public String generateRandomKey() {
		return UUID.randomUUID().toString().replace("-", "");
	}
    
	public String getPublicIP() {
		try {
			URI uri = new URI("https://api.ipify.org");
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			return response.body().trim();
		} catch (Exception e) {
            System.err.println("Falha ao obter IP público: " + e.getMessage());
			return "127.0.0.1";
		}
	}
    
    @SuppressWarnings("unchecked")
	public void loadLicenses(String email) {
		if ("brprojeto@l2jbrasil.com".equalsIgnoreCase(email)) {
			allLicenses = new JSONArray();
			JSONObject license = new JSONObject();
			license.put("license_key", getKey());
			license.put("expires_at", "Nunca");
			license.put("server_ip", getPublicIP());
			license.put("active", true);
			allLicenses.add(license);
		} else {
			allLicenses = new JSONArray();
		}
	}
    
    public JSONArray getAllLicenses() {
        return allLicenses;
    }
}
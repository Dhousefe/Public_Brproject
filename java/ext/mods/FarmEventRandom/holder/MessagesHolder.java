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
package ext.mods.FarmEventRandom.holder;

import java.util.ArrayList;
import java.util.List;

public class MessagesHolder
{
	private final List<String> _onPrepare = new ArrayList<>();
	private final List<String> _onStart = new ArrayList<>();
	private final List<String> _onZone = new ArrayList<>();
	private final List<String> _onEnd = new ArrayList<>();
	private final List<String> _onAuto = new ArrayList<>();

	public MessagesHolder() {}

	public void addOnPrepare(String msg) {
		if (msg != null && !msg.isEmpty())
			_onPrepare.add(msg);
	}

	public void addOnStart(String msg) {
		if (msg != null && !msg.isEmpty())
			_onStart.add(msg);
	}

	public void addOnZone(String msg) {
	    if (msg != null && !msg.isEmpty())
	        _onZone.add(msg);
	}

	public void addOnEnd(String msg) {
		if (msg != null && !msg.isEmpty())
			_onEnd.add(msg);
	}

	public void addOnAuto(String msg) {
		if (msg != null && !msg.isEmpty())
			_onAuto.add(msg);
	}

	public List<String> getOnPrepare() { return _onPrepare; }
	public List<String> getOnStart() { return _onStart; }
	public List<String> getOnZone() { return _onZone; }
	public List<String> getOnEnd() { return _onEnd; }
	public List<String> getOnAuto() { return _onAuto; }
}
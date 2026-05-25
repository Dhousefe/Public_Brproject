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
package ext.mods.fakeplayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ext.mods.gameserver.data.sql.PlayerInfoTable;
import ext.mods.gameserver.enums.actors.Sex;

public class FakePlayerNames
{
	private static final List<String> MALE_NAMES = new ArrayList<>();
	private static final List<String> FEMALE_NAMES = new ArrayList<>();
	private static final List<String> USED_NAMES = new ArrayList<>();
	
	static
	{
		Collections.addAll(MALE_NAMES, "Arthur", "Lucian", "Draven", "Garen", "Valen", "Kael", "Darius", "Talon", "Alaric", "Thorne", "Cedric", "Theron", "Leoric", "Balthazar", "Rowan", "Aedric", "Corwin", "Sylas", "Orin", "Thalion", "Ronan", "Evander", "Fenris", "Kaelen", "Auron", "Gideon", "Malric", "Hadrian", "Tyrion", "Cassian", "Eryk", "Ashur", "Soren", "Lucius", "Kieran", "Elias", "Zephyr", "Varek", "Calen", "Renar", "Magnus", "Daelen", "Beren", "Torin", "Xander", "Aric", "Ryder", "Drake", "Jareth", "Thane", "Varric", "Iskandar", "Lorien", "Kellen", "Alistair", "Braeden", "Mael", "Jarek", "Ravyn", "Zarek", "Caelum", "Thorne", "Nikolai", "Damon", "Kaelen", "Lucan", "Aleron", "Zane", "Eron", "Kalen", "Rurik", "Talon", "Galen", "Caspian", "Jax", "Orion", "Eamon", "Veyron", "Malik", "Korrin", "Sirius", "Kaine", "Oberyn", "Therin", "Tristan", "Roland", "Eldric", "Zeth", "Luther", "Darion", "Tiberius", "Falkor", "Leif", "Aeron", "Gavriel", "Myron", "Quint", "Torren", "Rhett", "Hawke", "Kelric", "Dain", "Caius", "Ignis", "Drael", "Arden", "Merek", "Sylvain", "Jorik", "Brann", "Corvus", "Thalos", "Riven", "Darrek", "Keldor", "Judas", "Blaine", "Kaemon", "Andros", "Xavian", "Zoren", "Laziel", "Garrick", "Darian", "Tavon", "Aziel", "Korran", "Cael", "Zephyrus", "Emric", "Lazrus", "Hadrik", "Fenrik", "Cyrus", "Malthus", "Drystan", "Orren", "Rhogar", "Thalion", "Taran", "Brynden", "Quorin", "Eryndor", "Cassiel", "Marek", "Roderic", "Davor", "Veylin", "Zadric", "Khalen", "Alarion", "Morthos", "Ithran", "Vrael", "Tirion", "Gareth", "Durion", "Kaedin", "Thyr", "Astar", "Rurik", "Aelric", "Zale", "Korvin", "Vandor", "Eryx", "Belric", "Fendrel", "Kalem", "Nyron", "Sevrin", "Orrick", "Eirik", "Torvyn", "Halrik", "Stryke", "Vorn", "Kadrin", "Aethor", "Dax", "Griffin", "Drelan", "Kaesor", "Miran", "Jorvan", "Balen", "Rowrick", "Quintis", "Sareth", "Drelik");
		Collections.addAll(FEMALE_NAMES, "Luna", "Selene", "Aurora", "Freya", "Lilith", "Seraphina", "Isolde", "Elira", "Nyx", "Kairi", "Aeliana", "Thalia", "Lyra", "Ravena", "Sylva", "Celeste", "Aria", "Eira", "Zafira", "Maelis", "Ysolde", "Vespera", "Melisande", "Kaelia", "Evelyne", "Saphira", "Mirelle", "Calista", "Aeris", "Naeris", "Tahlia", "Fiora", "Liora", "Mirabel", "Zelena", "Vanya", "Elowen", "Seren", "Taliah", "Ilyana", "Nerissa", "Anara", "Coraline", "Brielle", "Isara", "Amaris", "Zephyra", "Orla", "Lunara", "Thessia", "Aelith", "Selindra", "Kyria", "Velena", "Nimue", "Saren", "Eirlys", "Vaela", "Maris", "Yrene", "Alira", "Sanya", "Liora", "Caelia", "Elanil", "Myra", "Astrid", "Dahlia", "Kassia", "Nyssa", "Vaeria", "Althea", "Elaena", "Saphyra", "Meliora", "Ishara", "Nysa", "Zariel", "Alina", "Aris", "Miria", "Velina", "Tiriel", "Esme", "Naela", "Arwen", "Aeris", "Leira", "Shayra", "Virelle", "Azura", "Liora", "Narella", "Evelis", "Rowena", "Zyana", "Orielle", "Maeryn", "Selira", "Ylva", "Cassira", "Thessara", "Iria", "Mireya", "Arlen", "Tahlira", "Kariel", "Isilme", "Evalyn", "Sarina", "Zeyla", "Adalyn", "Luneth", "Arianne", "Cerys", "Kalira", "Thalira", "Morwen", "Velaria", "Ysera", "Eowyn", "Shyria", "Ellaria", "Maelis", "Nerina", "Faelina", "Selira", "Kalyra", "Aliona", "Delya", "Vaelora", "Illyra", "Serilda", "Aerisyl", "Myrren", "Lunaria", "Elaya", "Anwyn", "Caerwyn", "Aurelia", "Elaria", "Nymeria", "Mirellia", "Thalara", "Saelis", "Velwyn", "Zyra", "Kalista", "Virelia", "Nelyra", "Aurelle", "Celina", "Isolyn", "Kalyndra", "Liorael", "Saphyrah", "Maeriel", "Thenyra", "Ysara", "Aralya", "Oriana", "Azshara", "Lyrelle", "Eireen", "Kirelle", "Yllari", "Elariel", "Seliora", "Mirelith", "Thenyssa", "Ziriel", "Isylle", "Amelith", "Vaenara", "Felyra", "Talindra", "Naerwyn", "Oralyn", "Mireva", "Lyanna", "Sylria", "Kalyssa", "Velissa", "Celestra", "Mylenne", "Nysalia", "Vaelith", "Serilda", "Aeriselle", "Ysolde");
	}
	
	public static String getUniqueName(Sex sex)
	{
		List<String> sourceList = (sex == Sex.MALE) ? MALE_NAMES : FEMALE_NAMES;
		
		if (sourceList.isEmpty())
			return null;
		
		List<String> copy = new ArrayList<>(sourceList);
		
		for (String name : copy)
		{
			
			if (PlayerInfoTable.getInstance().getPlayerObjectId(name) > 0)
				continue;
			
			sourceList.remove(name);
			USED_NAMES.add(name);
			return name;
		}
		
		return null;
	}
	
	public static void releaseName(String name, Sex sex)
	{
		if (name == null || name.isEmpty())
			return;
		
		if (USED_NAMES.remove(name))
		{
			if (sex == Sex.MALE)
			{
				MALE_NAMES.add(name);
				Collections.sort(MALE_NAMES);
			}
			else
			{
				FEMALE_NAMES.add(name);
				Collections.sort(FEMALE_NAMES);
			}
			
		}
	}
	
}
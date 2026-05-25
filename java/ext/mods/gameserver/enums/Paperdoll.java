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
package ext.mods.gameserver.enums;

public enum Paperdoll
{
    NULL(-1),
    UNDER(0),
    LEAR(1),
    REAR(2),
    NECK(3),
    LFINGER(4),
    RFINGER(5),
    HEAD(6),
    RHAND(7),
    LHAND(8),
    LRHAND(9),
    GLOVES(10),
    CHEST(11),
    LEGS(12),
    FEET(13),
    CLOAK(14),
    FACE(15),
    HAIR(16),
    HAIRALL(17);

    public static final Paperdoll[] VALUES = values();
    public static final int TOTAL_SLOTS = 18;

    private final int _id;

    private Paperdoll(int id)
    {
        _id = id;
    }

    public int getId()
    {
        return _id;
    }

    public static Paperdoll getEnumByName(String name)
    {
        for (Paperdoll paperdoll : VALUES)
        {
            if (paperdoll.toString().equalsIgnoreCase(name))
                return paperdoll;
        }
        return NULL;
    }

    public static Paperdoll getEnumById(int id)
    {
        for (Paperdoll paperdoll : VALUES)
        {
            if (paperdoll.getId() == id)
                return paperdoll;
        }
        return NULL;
    }
}
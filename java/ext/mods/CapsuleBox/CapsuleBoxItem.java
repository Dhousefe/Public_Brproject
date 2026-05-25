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
package ext.mods.CapsuleBox;

/**
 * @author Eli
 *
 */
import java.util.ArrayList;
import java.util.List;

public class CapsuleBoxItem {
    private int id;
    private int playerLevel;
    private List<Item> items;

    public CapsuleBoxItem(int id, int playerLevel) {
        this.id = id;
        this.playerLevel = playerLevel;
        items = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public int getPlayerLevel() {
        return playerLevel;
    }

    public List<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public static class Item {
        private int itemId;
        private int amount;
        private int enchantLevel;
        private int chance;

        public Item(int itemId, int amount, int enchantLevel, int chance) {
            this.itemId = itemId;
            this.amount = amount;
            this.enchantLevel = enchantLevel;
            this.chance = chance;
        }

        public int getItemId() {
            return itemId;
        }

        public int getAmount() {
            return amount;
        }

        public int getEnchantLevel() {
            return enchantLevel;
        }

        public int getChance() {
            return chance;
        }

 
    }
}
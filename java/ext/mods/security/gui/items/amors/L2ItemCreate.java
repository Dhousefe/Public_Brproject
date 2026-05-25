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
package ext.mods.security.gui.items.amors;

public class L2ItemCreate
{
	private int id;
	private String type;
	private String name;
	private String icon;
	private String defaultAction;
	private String armorType;
	private String bodyPart;
	private String crystalType;
	private int crystalCount;
	private String material;
	private int weight;
	private int price;
	private int basePDef;
	private int enchantPDef;
	
	public L2ItemCreate(int id, String type, String name, String icon, String defaultAction, String armorType, String bodyPart, String crystalType, int crystalCount, String material, int weight, int price, int basePDef, int enchantPDef)
	{
		this.id = id;
		this.type = type;
		this.name = name;
		this.icon = icon;
		this.defaultAction = defaultAction;
		this.armorType = armorType;
		this.bodyPart = bodyPart;
		this.crystalType = crystalType;
		this.crystalCount = crystalCount;
		this.material = material;
		this.weight = weight;
		this.price = price;
		this.basePDef = basePDef;
		this.enchantPDef = enchantPDef;
	}
	
	public int getId()
	{
		return id;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public String getType()
	{
		return type;
	}
	
	public void setType(String type)
	{
		this.type = type;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getIcon()
	{
		return icon;
	}
	
	public void setIcon(String icon)
	{
		this.icon = icon;
	}
	
	public String getDefaultAction()
	{
		return defaultAction;
	}
	
	public void setDefaultAction(String defaultAction)
	{
		this.defaultAction = defaultAction;
	}
	
	public String getArmorType()
	{
		return armorType;
	}
	
	public void setArmorType(String armorType)
	{
		this.armorType = armorType;
	}
	
	public String getBodyPart()
	{
		return bodyPart;
	}
	
	public void setBodyPart(String bodyPart)
	{
		this.bodyPart = bodyPart;
	}
	
	public String getCrystalType()
	{
		return crystalType;
	}
	
	public void setCrystalType(String crystalType)
	{
		this.crystalType = crystalType;
	}
	
	public int getCrystalCount()
	{
		return crystalCount;
	}
	
	public void setCrystalCount(int crystalCount)
	{
		this.crystalCount = crystalCount;
	}
	
	public String getMaterial()
	{
		return material;
	}
	
	public void setMaterial(String material)
	{
		this.material = material;
	}
	
	public int getWeight()
	{
		return weight;
	}
	
	public void setWeight(int weight)
	{
		this.weight = weight;
	}
	
	public int getPrice()
	{
		return price;
	}
	
	public void setPrice(int price)
	{
		this.price = price;
	}
	
	public int getBasePDef()
	{
		return basePDef;
	}
	
	public void setBasePDef(int basePDef)
	{
		this.basePDef = basePDef;
	}
	
	public int getEnchantPDef()
	{
		return enchantPDef;
	}
	
	public void setEnchantPDef(int enchantPDef)
	{
		this.enchantPDef = enchantPDef;
	}
	
	public String toXML()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\t<item id=\"").append(id).append("\" type=\"").append(type).append("\" name=\"").append(name).append("\">\n");
		sb.append("\t\t<set name=\"icon\" val=\"").append(icon).append("\" />\n");
		sb.append("\t\t<set name=\"default_action\" val=\"").append(defaultAction).append("\" />\n");
		
		if (armorType != null && !armorType.isEmpty())
		{
			sb.append("\t\t<set name=\"armor_type\" val=\"").append(armorType).append("\" />\n");
		}
		
		sb.append("\t\t<set name=\"bodypart\" val=\"").append(bodyPart).append("\" />\n");
		sb.append("\t\t<set name=\"crystal_type\" val=\"").append(crystalType).append("\" />\n");
		sb.append("\t\t<set name=\"crystal_count\" val=\"").append(crystalCount).append("\" />\n");
		sb.append("\t\t<set name=\"material\" val=\"").append(material).append("\" />\n");
		sb.append("\t\t<set name=\"weight\" val=\"").append(weight).append("\" />\n");
		sb.append("\t\t<set name=\"price\" val=\"").append(price).append("\" />\n");
		sb.append("\t\t<for>\n");
		sb.append("\t\t\t<baseadd stat=\"pDef\" val=\"").append(basePDef).append("\" />\n");
		sb.append("\t\t\t<enchant stat=\"pDef\" val=\"").append(enchantPDef).append("\" />\n");
		sb.append("\t\t</for>\n");
		sb.append("\t</item>");
		return sb.toString();
	}
}